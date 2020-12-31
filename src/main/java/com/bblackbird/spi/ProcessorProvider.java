package com.bblackbird.spi;

public interface ProcessorProvider<T, C> {

    Processor<T, C> register(String id, Processor<T, C> processor);

    Processor<T, C> deregister(String id);

    Processor<T, C> get(String id);

}
