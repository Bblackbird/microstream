package com.bblackbird.spi;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

public class AbstractProcessorConfigurationGenerator<T, C> {

    public static void createDirectory(String propertyFileName, File outputDir, String path) {
        path = path.replaceAll("\\.", "/");
        File pkgdir = new File(outputDir, propertyFileName);
        if(!pkgdir.exists()) {
            // create the pkg directory
            boolean ret = pkgdir.mkdirs();
            if(!ret) {
                System.out.println("Cannot create directory: " + path);
                System.exit(1);
            }
        } else if(!pkgdir.isDirectory()) {
            // not directory
            System.out.println(path + " is not directory");
            System.exit(1);
        }
    }

    private static char keyValueSeparator = ':';
    private static char commaSeparator = ',';
    private static char pipeSeparator = '|';
    private static String TRUE = "true";
    private static String FALSE = "false";

    public static String getBuildDate() {
        return LocalDate.now().toString();
    }

    private void generateProcessors(FileWriter w, Processor<T, C> p, int count) throws IOException {
        w.write("processor-" + count);
        generateCommonProcessors(w, p, count);
    }

    private void generateCommonProcessors(FileWriter w, Processor<T,C> p, int count) throws IOException {

        w.write(keyValueSeparator  + p.getClass().getName() + commaSeparator + TRUE);
        String[] dep = p.dependencies();
        if(dep.length > 0) {
            String deps = Joiner.on(pipeSeparator).skipNulls().join(dep);
            w.write(commaSeparator + deps);
        }

        if(p.getClass().getSimpleName().equals(p.getId())) {
            w.write("\n");
        } else {
            if(dep.length == 0) {
                w.write(commaSeparator);
            }
            w.write(commaSeparator + p.getId() + "\n");
        }
    }

    private void generateContainerProcessors(FileWriter w, Processor<T, C> p, int containerCount, int count) throws IOException {
        w.write("processor-" + containerCount + "-" + count);
        generateCommonProcessors(w, p, count);
    }

    private void generateContainer(FileWriter w, CompositeProcessor<T, C> container, int count) throws IOException {
        w.write("processor-" + count + keyValueSeparator + container.getClass().getName() + commaSeparator + TRUE + commaSeparator +
                container.getId() + commaSeparator + container.getProcessors().size() + "\n");

        int localCount = 0;
        for(Processor<T, C> p : container.getProcessors()) {
            localCount++;
            generateAggregateProcessors(w, p, count, localCount);
        }

/*        int localCount = 0;
        IntStream.range(0, container.geProcessors().size()).forEach(idx -> {
            try {
                generateContainerProcessors(w, container.geProcessors().get(idx), count, idx);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/
    }

    private int getTotalProcessorCount(List<Processor<T, C>> processors) {
        return processors.size();
    }

    private int getTotalProcessorCountWithContainers(List<Processor<T, C>> processors) {
        return processors.stream().filter(this::isProcessorContainer).mapToInt(p -> ((CompositeProcessor<T, C>)p).getProcessors().size()).sum();
    }

    private boolean isProcessorContainer(Processor<T, C> p) {
        return p.getClass().getSimpleName().contains("Container");
    }

    private boolean isAggregateProcessor(Processor<T, C> p) {
        return p.getClass().getSimpleName().contains("AggregateProcessor");
    }

    private void generateAggregateProcessors(FileWriter w, Processor<T, C> p, int containerCount, int count) throws IOException {
        String[] dep = p.dependencies();
        if(dep.length > 0) {
            String deps = Joiner.on(pipeSeparator).skipNulls().join(dep);
            w.write("processor-" + containerCount + "-" + count + "-" + keyValueSeparator + p.getClass().getName() + commaSeparator + TRUE + commaSeparator + deps + "\n");
        } else {
            w.write("processor-" + containerCount + "-" + count + "-" + keyValueSeparator + p.getClass().getName() + commaSeparator + TRUE + "\n");
        }
    }

    private void generateAggregateProcessor(FileWriter w, Processor<T, C> aggProcessor, int count) throws IOException {
        String[] dep = aggProcessor.dependencies();
        if(dep.length > 0) {
            String deps = Joiner.on(pipeSeparator).skipNulls().join(dep);
            w.write("processor-" + count + keyValueSeparator + aggProcessor.getClass().getName() + commaSeparator + TRUE + commaSeparator + deps + commaSeparator +
                    aggProcessor.getProcessors().size() + "\n");
        } else {
            w.write("processor-" + count + keyValueSeparator + aggProcessor.getClass().getName() + commaSeparator + TRUE + commaSeparator +
                    aggProcessor.getProcessors().size() + "\n");
        }

        int localCount = 0;
        for(Processor<T, C> p : aggProcessor.getProcessors()) {
            localCount++;
            generateAggregateProcessors(w, p, count, localCount);
        }
    }

    public void generaePropertyFile(String propertyFileName, File outputDir, List<Processor<T, C>> processors, String buildDate) {

        int processorCount = getTotalProcessorCount(processors);

        File file = new File(outputDir, propertyFileName + ".properties");

        try(FileWriter w = new FileWriter(file)) {
            w.write("# Do not edit!\n#File generated by " + this.getClass().getName() + " on " + buildDate + ".\n");
            w.write("processorCount" + keyValueSeparator + processorCount + "\n");

            int count = 1;
            for(Processor<T, C> p : processors) {
                if(isAggregateProcessor(p)) {
                    generateAggregateProcessor(w, p, count);
                } else if(isProcessorContainer(p)) {
                    CompositeProcessor<T, C> container = (CompositeProcessor<T, C>) p;
                    generateContainer(w, container, count);
                } else {
                    generateProcessors(w, p, count);
                }
                count++;

            }

            w.write("reloadRequest" + keyValueSeparator + FALSE + "\n");


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static Collection<Object[]> getClassses(Class<?>... klazzez) throws IOException {
        ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        ArrayList<Object[]> params = Lists.newArrayList();
        for(Class<?> klazz : klazzez) {
            String packageName = klazz.getPackage().getName();
            Collection<Object[]> p = getClassses(classPath, packageName);
            params.addAll(p);
        }
        return params;
    }

    public static Collection<Object[]> getClassses(Class<?> klazz) throws IOException {
        ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        String packageName = klazz.getPackage().getName();
        return getClassses(classPath, packageName);
    }

    public static Collection<Object[]> getClassses(Class<?> exclusionList[], Class<?>... klazzez) throws IOException {
        ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        List<Class<?>> exclList = Arrays.asList(exclusionList);
        ArrayList<Object[]> params = Lists.newArrayList();
        for(Class<?> klazz : klazzez) {
            String packageName = klazz.getPackage().getName();
            Collection<Object[]> p = getClassses(classPath, packageName, exclList);
            params.addAll(p);
        }
        return params;
    }

    public static Collection<Object[]> getClassses(Class<?> klazz, Class<?> exclusionList[]) throws IOException {
        ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        String packageName = klazz.getPackage().getName();
        List<Class<?>> exclList = Arrays.asList(exclusionList);
        return getClassses(classPath, packageName, exclList);
    }

    private static Collection<Object[]> getClassses(ClassPath classPath, String packageName, List<Class<?>> exclusionList) {

        ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClasses(packageName);
        Collection<ClassPath.ClassInfo> portableObjects = Collections2.filter(classes, new Predicate<ClassPath.ClassInfo>() {
            @Override
            public boolean apply(ClassPath.@Nullable ClassInfo input) {
                Class<?> klazz = input.load();
                if(exclusionList.contains(klazz))
                    return false;
                Deprecated d = klazz.getAnnotation(Deprecated.class);
                Portable p = klazz.getAnnotation(Portable.class);
                return (PortableObject.class.isAssignableFrom(klazz) || p != null) && d == null && !Comparator.class.isAssignableFrom(klazz);
            }
        });

        Collection<Object[]> klazzez = Collections2.transform(portableObjects, new Function<ClassPath.ClassInfo, Object[]>() {

            @Override
            public Object @Nullable [] apply(ClassPath.@Nullable ClassInfo classInfo) {
                return new Object[] {classInfo.load()};
            }
        });

        return klazzez;

    }

    public static Collection<Object[]> getClassses(ClassPath classPath, String packageName) {
        ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClasses(packageName);
        Collection<ClassPath.ClassInfo> portableObjects = Collections2.filter(classes, new Predicate<ClassPath.ClassInfo>() {
            @Override
            public boolean apply(ClassPath.@Nullable ClassInfo input) {
                Class<?> klazz = input.load();
                Deprecated d = klazz.getAnnotation(Deprecated.class);
                Portable p = klazz.getAnnotation(Portable.class);
                return (PortableObject.class.isAssignableFrom(klazz) || p != null) && d == null && !Comparator.class.isAssignableFrom(klazz);
            }
        });

        Collection<Object[]> klazzez = Collections2.transform(portableObjects, new Function<ClassPath.ClassInfo, Object[]>() {

            @Override
            public Object @Nullable [] apply(ClassPath.@Nullable ClassInfo classInfo) {
                return new Object[] {classInfo.load()};
            }
        });

        return klazzez;

    }


}
