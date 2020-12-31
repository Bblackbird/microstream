package com.bblackbird.spi;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface Processor<T, C> {

    String getId();

    ProcessorType getType();

    String[] dependencies();

    ProcessingResult<T> createProcessingResult(ProcessingContext<T, C> context);

    ProcessingPredicate<T, C> predicate();

    ProcessingResult<T> process(ProcessingContext<T, C> context);

    // Supplying ProcessingResult implies that processor will be always executed i.e. even if previously invoked in different context
    ProcessingResult<T> process(ProcessingContext<T, C> context, ProcessingResult<T> processingResult);

    ProcessingResult<T> process(ProcessingContext<T, C> context, ProcessingResult<T> processingResult, String callerProcessorId);

    ProcessingResult<T> process(Map<String, Object> args, ProcessingContext<T, C> context, ProcessingResult<T> processingResult, String callerProcessorId);

    ProcessingResult<T> process(T value, ProcessingContext<T, C> context);

    ProcessingResult<T> process(T value, ProcessingContext<T, C> context, String callerProcessorId);

    ProcessingResult<T> process(final T value, ProcessingContext<T, C> context, ProcessingResult<T> processingResult);

    ProcessingResult<T> process(final T value, ProcessingContext<T, C> context, ProcessingResult<T> processingResult, String callerProcessorId);

    ProcessingContext<T, C> createProcessingContext(T data, C Context);

    ProcessingContext<T, C> createProcessingContext(T data, C Context, Function<String, ProcessingResult<T>> processingResultCreator);

    ProcessingContext<T, C> createProcessingContext(ProcessorProviderAndSupplier<T, C> processorProviderAndSupplier);

    String info();

    Collection<Processor<T, C>> getProcessors();

    // For testing purpose

    void insertBefore(String processorId, Processor<T, C> processor);

    Processor<T, C> remove(String processorId);

    Processor<T, C> replace(String processorId, Processor<T, C> replacementProcessor);


}
