package com.bblackbird.spi;

public interface ProcessorProviderAndSupplier<T, C> extends ProcessingContextFactory<T, C>, ProcessorProvider<T, C>, DomainContextSupplier<C>, ProcessorMetricSupplier {
}
