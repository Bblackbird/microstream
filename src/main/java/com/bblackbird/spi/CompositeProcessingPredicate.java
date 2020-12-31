package com.bblackbird.spi;

public interface CompositeProcessingPredicate<T, C> {

    boolean test(Processor<T, C> processor, ProcessingContext<T, C> context);

    boolean test(ProcessingResult<T> processingResult, ProcessingContext<T, C> context);


}
