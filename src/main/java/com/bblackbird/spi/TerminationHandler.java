package com.bblackbird.spi;

public interface TerminationHandler<T, C> {

    void handleTermination(T value, ProcessingResult<T> result, ProcessingContext<T, C> processingContext);

}
