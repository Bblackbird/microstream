package com.bblackbird.spi;

import java.util.List;

public interface ProcessingContextResults<T> {

    ProcessingResult<T> createProcessingResult(String processorId);

    ProcessingResult<T> getProcessingResult(String processorId);

    void add(ProcessingResult<T> result);

    void clear();

    List<ProcessingResult<T>> getProcessingResults();

    boolean isProcessed(String processorId);

    boolean hasFailed(String processorId);

    boolean hasViolations();

}
