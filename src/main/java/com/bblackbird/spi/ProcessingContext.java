package com.bblackbird.spi;

import java.util.Map;

public interface ProcessingContext<T, C> extends ProcessingContextResults<T>, ProcessingContextDictionary, ProcessingChangeAudit, DomainContextSupplier<C>, ProcessorMetricSupplier {

    void setData(T data);

    T getData();

    ProcessorProvider<T, C> getProcessorProvider();

    CommandContext getCommandContext();

    ProcessingResult<T> process(final T value, String processorId, String callerProcessorId);

    ProcessingResult<T> process(final T value, ProcessingResult<T> processingResult, String processorId, String callerProcessorId);

    ProcessingResult<T> process(T value, Map<String, Object> args, ProcessingResult<T> processingResult, String processorId, String callerProcessorId);

    <S> S getComputedData(String processorId, String callerProcessorId);


}
