package com.bblackbird.spi;

import java.util.List;

public interface ProcessorFactory<T, C> {

    String prefix();

    Processor<T, C> createProcessor(String processorClassName) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, String processorId) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, ClassLoader classLoader) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, String[] deps) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, String[] deps, ClassLoader classLoader) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, String processorId, String[] deps) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, String processorId, String[] deps, ClassLoader classLoader) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, List<Processor<T, C>> containerList) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, String processorId, List<Processor<T, C>> containerList) throws Exception;

    Processor<T, C> createProcessor(String processorClassName, String[] deps, List<Processor<T, C>> containerList) throws Exception;

}
