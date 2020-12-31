package com.bblackbird.violation;


import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ErrorCodeTypeGenerator {


    public static void main(String[] args) throws Exception {

        // 1. Configure FreeMarker
        //
        // You should do this ONLY ONCE, when your application starts,
        // then reuse the same Configuration object elsewhere.

        Configuration cfg = configureFreemarker();

        Template template = cfg.getTemplate("ErrorCodeType.ftl");

        CSVBeanUtil<ErrorCodeTypeDescription> csvBeanUtil = new CSVBeanUtil<>(ErrorCodeTypeDescription.class);

        List<ErrorCodeTypeDescription> errorDescriptions = csvBeanUtil.parseCSVResource("ErrorCodeTypes.csv");

        if(!validateErorCodeType(errorDescriptions)){
            System.out.println("Failed.... fast");
            return;
        }

        List<String> errors = errorDescriptions.stream()
                .filter(e -> e.signalIfEnabled())
                .map(e -> e.generateLine())
                .collect(Collectors.toList());

        Map<String, Object> dataMap = ImmutableMap.of("errors", errors);

        try(Writer file = new FileWriter(new File("./src/main/java/com/bblackbird/violation/ErrorCodeType.java"))) {
            template.process(dataMap, file);
            file.flush();
            System.out.println("Success");
        } catch (Exception e){
            System.err.println(e);
        }


/*        // Where do we load the templates from:
        cfg.setClassForTemplateLoading(MainTest.class, "templates");

        // Some other recommended settings:
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // 2. Proccess template(s)
        //
        // You will do this for several times in typical applications.

        // 2.1. Prepare the template input:

        Map<String, Object> input = new HashMap<String, Object>();

        input.put("title", "Vogella example");

        input.put("exampleObject", new ValueExampleObject("Java object", "me"));

        List<ValueExampleObject> systems = new ArrayList<ValueExampleObject>();
        systems.add(new ValueExampleObject("Android", "Google"));
        systems.add(new ValueExampleObject("iOS States", "Apple"));
        systems.add(new ValueExampleObject("Ubuntu", "Canonical"));
        systems.add(new ValueExampleObject("Windows7", "Microsoft"));
        input.put("systems", systems);

        // 2.2. Get the template

        Template template = cfg.getTemplate("helloworld.ftl");

        // 2.3. Generate the output

        // Write output to the console
        Writer consoleWriter = new OutputStreamWriter(System.out);
        template.process(input, consoleWriter);

        // For the sake of example, also write output into a file:
        Writer fileWriter = new FileWriter(new File("output.html"));
        try {
            template.process(input, fileWriter);
        } finally {
            fileWriter.close();
        }*/

    }

    private static boolean validateErorCodeType(List<ErrorCodeTypeDescription> errorDescriptions) {

        if(errorDescriptions == null || errorDescriptions.isEmpty())
            return true;

        boolean result = true;

        errorDescriptions.stream().filter(e -> e.Name != null && e.Name.length() > 40).forEach(e -> {
            System.err.println(e);
        });

        return result;
    }

    private static Configuration configureFreemarker() throws IOException {
        // Create your Configuration instance, and specify if up to what FreeMarker
// version (here 2.3.27) do you want to apply the fixes that are not 100%
// backward-compatible. See the Configuration JavaDoc for details.
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);

// Specify the source where the template files come from. Here I set a
// plain directory for it, but non-file-system sources are possible too:
        cfg.setClassForTemplateLoading(ErrorCodeTypeGenerator.class, "/");

        //cfg.setDirectoryForTemplateLoading(new File("/where/you/store/templates"));

// Set the preferred charset template files are stored in. UTF-8 is
// a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8");

        //cfg.setIncompatibleImprovements(new Version(2, 3, 20));

        cfg.setLocale(Locale.US);

// Sets how errors will appear.
// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.setLogTemplateExceptions(false);

// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
        cfg.setWrapUncheckedExceptions(true);

        return cfg;
    }


}
