package com.bblackbird.violation;

import com.google.common.base.Strings;
import com.opencsv.CSVReader;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DebugCsvToBean<T> extends CsvToBean<T> {
    

    public List<T> parse(HeaderColumnNameMappingStrategy<T> beanStrategy, CSVReader csvReader) throws IOException, CsvRequiredFieldEmptyException, IllegalAccessException, CsvDataTypeMismatchException, InstantiationException {
        
        beanStrategy.captureHeader(csvReader);
        
        String[] line;
        List<T> list = new ArrayList<>();
        while(null != (line = csvReader.readNext())){
            T obj = processLine(beanStrategy, line);
            if(obj == null){
                continue;
            }
            list.add(obj);
        }
        
        return list;
    }

    private T processLine(HeaderColumnNameMappingStrategy<T> beanStrategy, String[] line) throws IllegalAccessException, InstantiationException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

        if(line == null || line.length == 0){
            return null;
        }

        T bean = beanStrategy.createBean();

        for(int col = 0; col < line.length; col++) {

            String value = line[col];

            BeanField<T> beanField = beanStrategy.findField(col);

            if(Strings.isNullOrEmpty(value)){
                continue;
            }

            beanField.write(bean, value);

        }

        return bean;

    }
}
