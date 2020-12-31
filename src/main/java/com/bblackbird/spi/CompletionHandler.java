package com.bblackbird.spi;

public interface CompletionHandler<T, C> {

    void complete(T value, ProcessingResult<T> result, ProcessingContext<T, C> processingContext);

}
