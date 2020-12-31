package com.bblackbird.spi;

public interface ProcessorConfiguration<T, C> {

    Processor<T, C> configure() throws Exception;

    Processor<T, C> getProcessor();

    default ProcessorConfigurationType getType() {
        return ProcessorConfigurationType.Code;
    }

}
