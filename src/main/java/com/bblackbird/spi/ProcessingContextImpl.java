package com.bblackbird.spi;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ProcessingContextImpl<T, C> implements ProcessingContext<T, C> {
    public <T> ProcessingContextImpl(T data, Object p1) {
    }

    public <T, C> ProcessingContextImpl(T data, C context, Function<String, ProcessingResult<T>> processingResultCreator) {
    }

    public <T> ProcessingContextImpl(ProcessorProviderAndSupplier<T, C> processorProviderAndSupplier) {
    }

    @Override
    public void setData(T data) {
        
    }

    @Override
    public T getData() {
        return null;
    }

    @Override
    public ProcessorProvider<T, C> getProcessorProvider() {
        return null;
    }

    @Override
    public CommandContext getCommandContext() {
        return null;
    }

    @Override
    public ProcessingResult<T> process(T value, String processorId, String callerProcessorId) {
        return null;
    }

    @Override
    public ProcessingResult<T> process(T value, ProcessingResult<T> processingResult, String processorId, String callerProcessorId) {
        return null;
    }

    @Override
    public ProcessingResult<T> process(T value, Map<String, Object> args, ProcessingResult<T> processingResult, String processorId, String callerProcessorId) {
        return null;
    }

    @Override
    public <S> S getComputedData(String processorId, String callerProcessorId) {
        return null;
    }

    @Override
    public C getDomainContext() {
        return null;
    }

    @Override
    public <T> String trackChange(String processorId, T value) {
        return null;
    }

    @Override
    public <T> String trackAndLogChange(String processorId, T value) {
        return null;
    }

    @Override
    public String changeLog() {
        return null;
    }

    @Override
    public Object setValue(String key, Object value) {
        return null;
    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public Object removeValue(String key) {
        return null;
    }

    @Override
    public ProcessingResult<T> createProcessingResult(String processorId) {
        return null;
    }

    @Override
    public ProcessingResult<T> getProcessingResult(String processorId) {
        return null;
    }

    @Override
    public void add(ProcessingResult<T> result) {

    }

    @Override
    public void clear() {

    }

    @Override
    public List<ProcessingResult<T>> getProcessingResults() {
        return null;
    }

    @Override
    public boolean isProcessed(String processorId) {
        return false;
    }

    @Override
    public boolean hasFailed(String processorId) {
        return false;
    }

    @Override
    public boolean hasViolations() {
        return false;
    }

    @Override
    public ProcessorMetrics getProcessorMetrics() {
        return null;
    }
}
