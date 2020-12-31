package com.bblackbird.spi;

public interface ProcessingContextFactory<T, C> {

    ProcessingContext<T, C> createProcessingContext();

    ProcessingContext<T, C> createProcessingContext(Object commandContextId);

    ProcessingContext<T, C> createProcessingContext(CommandContext commandContext);

}
