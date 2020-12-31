package com.bblackbird.violation;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CSVBeanUtil<T> {

    private final Class<T> klazz;

    public CSVBeanUtil(Class<T> klazz) {
        this.klazz = klazz;
    }

    public List<T> parseCSV(File file) throws IOException {
        return parseCSVToBeanList(file, klazz);
    }

    public List<T> parseCSV(String dir, String fileName) throws IOException {
        return parseCSVToBeanList(dir, fileName, klazz);
    }

    public List<T> parseCSVResource(String resourcePath) throws IOException {
        return parseCSVResourceToBeanList(resourcePath, klazz);
    }

    public void writeCSV(Collection<T> records, String dir, String fileName, boolean append) throws IOException {
        writeBeanListToCSV(records, dir, fileName, append, klazz);
    }

    public void writeCSV(Collection<T> records, String fileName, boolean append) throws IOException {
        writeBeanListToCSV(records, null, fileName, append, klazz);
    }

    public void writeCSV(Collection<T> records, String dir, String fileName) throws IOException {
        writeBeanListToCSV(records, dir, fileName, false, klazz);
    }

    public void writeBeanListToCSV(Collection<T> records, String fileName) throws IOException {
        writeBeanListToCSV(records, fileName, false, klazz);
    }


    //////////////////////

    private static File getFile(String dir, String fileName) {
        File file = null;
        if(Strings.isNullOrEmpty(dir)){
            file = new File(fileName);
        } else {
            file = new File(dir, fileName);
        }
        return file;
    }

    public static Reader fromResource(String resourcePath) {
        try{
            return fromURL(getFromResource(resourcePath));
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }

    private static Reader fromURL(URL url) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        return in;
    }

    public static URL getFromResource(String resourcePath) {
        final URL url = Resources.getResource(resourcePath);
        return url;
    }

    public static <T> List<T> parseCSVResourceToBeanList(String resourcePath, Class<T> klazz) throws IOException {
        final URL url = getFromResource(resourcePath);
        Preconditions.checkNotNull(url);
        List<T> result = Collections.emptyList();
        try ( Reader in = fromResource(resourcePath)) {
            result = parseCSVToBeanList(in, klazz);
        }
        return result;
    }

    public static <T> List<T> parseCSVToBeanList(String dir, String fileName, Class<T> klazz) throws IOException {
        File file = getFile(dir, fileName);
        return parseCSVToBeanList(file, klazz);
    }

    public static <T> List<T> parseCSVToBeanList(File file, Class<T> klazz) throws IOException {
        return parseCSVToBeanList(new FileReader(file), klazz);
    }

    public static <T> List<T> parseCSVToBeanList(Reader reader, Class<T> klazz) throws IOException {
        Preconditions.checkNotNull(reader);
        HeaderColumnNameMappingStrategy<T> beanStrategy = new HeaderColumnNameMappingStrategy<>();
        beanStrategy.setType(klazz);

        //DebugCsvToBean<T> csvToBean = new DebugCsvToBean<>();
        CsvToBean<T> csvToBean = new CsvToBean<>();
        try(CSVReader csvReader = new CSVReader(reader)) {
            List<T> records = csvToBean.parse(beanStrategy, csvReader);
            return records;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void writeBeanListToCSV(Collection<T> records, String dir, String fileName, boolean append, Class<T> klazz) throws IOException {
        File file = getFile(dir, fileName);
        boolean empty = (append == false || !file.exists() || file.length()==0);
        try(CSVWriter csvWriter  = new CSVWriter(new FileWriter(file, append))) {
            HeaderColumnNameMappingStrategy<T> beanStrategy = new HeaderColumnNameMappingStrategy<>();
            beanStrategy.setType(klazz);
            List<String[]> data = toStringArray(records, empty, klazz);
            csvWriter.writeAll(data);
        }

    }

    public static <T> void writeBeanListToCSV(Collection<T> records, String fileName, boolean append, Class<T> klazz) throws IOException {
        writeBeanListToCSV(records, null, fileName, append, klazz);
    }

    public static <T> void writeBeanListToCSV(Collection<T> records, String dir, String fileName, Class<T> klazz) throws IOException {
        writeBeanListToCSV(records, dir, fileName, false, klazz);
    }

    public static <T> void writeBeanListToCSV(Collection<T> records, String fileName, Class<T> klazz) throws IOException {
        writeBeanListToCSV(records, fileName, false, klazz);
    }

    public static <T> List<String[]> toStringArray(Collection<T> records, boolean includeHeader, Class<T> klazz) {
        List<String[]> columns = new ArrayList<>();
        if(includeHeader) {
            columns.add(getFieldNamesAsArray(klazz));
        }
        
        records.forEach(item -> {
            columns.add(getFieldValuesAsArray(item, klazz));
        });
        
        return columns;
    }

    public static <T> String[] getFieldValuesAsArray(T item, Class<T> klazz) {
        List<String> values = getFieldValues(item, klazz);
        return values.toArray(new String[values.size()]);
    }

    public static <T> List<String> getFieldValues(T item, Class<T> klazz) {
        return ReflectUtils.getAllDeclaredFields(klazz, ReflectUtils.isStatic).stream().map(field -> {

            Object value = ReflectUtils.getField(field, item);
            Class<?> type = field.getType();
            if(type.isAssignableFrom(String.class)){
                return (String) value;
            } else {
                return String.valueOf(value);
            }
        }).collect(Collectors.toList());
    }

    public static <T> String[] getFieldNamesAsArray(Class<T> klazz) {
        List<String> columnList = getFieldNames(klazz);
        return columnList.toArray(new String[columnList.size()]);

    }

    public static <T> List<String> getFieldNames(Class<T> klazz) {
        return ReflectUtils.getAllDeclaredFields(klazz, ReflectUtils.isStatic).stream().map(field -> field.getName()).collect(Collectors.toList());
    }


}
