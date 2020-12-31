package com.bblackbird.spi;

public class BaseProcessorConfiguration<T, C> implements ProcessorConfiguration<T, C> {
    @Override
    public Processor<T, C> configure() throws Exception {
        return null;
    }

    @Override
    public Processor<T, C> getProcessor() {
        return null;
    }
}
