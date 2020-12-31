package com.bblackbird.spi;

public class DefaultCompositePredicate<T, C> implements CompositeProcessingPredicate<T, C> {
    @Override
    public boolean test(Processor<T, C> processor, ProcessingContext<T, C> context) {
        ProcessingResult<T> processingResult  = context.getProcessingResult(processor.getId());
        return test(processingResult, context);
    }

    @Override
    public boolean test(ProcessingResult<T> processingResult, ProcessingContext<T, C> context) {
        if(processingResult == null)
            return true;
        return !processingResult.hasFatalErrors();
    }
}
