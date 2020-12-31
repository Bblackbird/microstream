package com.bblackbird.spi;

public interface ProcessorConfigurator<T, C>  extends ProcessorProviderAndSupplier<T, C> {

    Processor<T, C> configure() throws Exception;

    Processor<T, C> getProcessor();

    Processor<T, C> replace(String processorId, Processor<T, C> replacementProcessor);

}
