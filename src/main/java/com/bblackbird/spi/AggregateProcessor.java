package com.bblackbird.spi;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract  class AggregateProcessor<T, C> extends AbstractProcessor<T, C> {

    private Map<String, Processor<T, C>> processorMap = Collections.emptyMap();

    protected AggregateProcessor(String processorId, String[] dependencies, int minDepSize, List<String> processorIds, ProcessorConfigurator<T, C> configurator) {
        super(processorId, dependencies);
        init(processorIds, configurator);
    }


    protected AggregateProcessor(String processorId, List<String> processorIds, ProcessorConfigurator<T, C> configurator) {
        super(processorId);
        init(processorIds, configurator);
    }

    protected AggregateProcessor(String processorId, String[] dependencies, int minDepSize, List<Processor<T, C>> processors) {
        super(processorId, dependencies);
        this.processorMap = processors.stream().collect(Collectors.toMap(Processor::getId, Function.identity()));
        this.processorInfo = String.format("[ %s %s %s ]", getId(), getType().name(), processorMap.size());
    }

    protected AggregateProcessor(String processorId, Processor<T, C>... processors) {
        this(processorId, Arrays.asList(processors));
    }

    protected AggregateProcessor(String processorId, List<Processor<T, C>> processors) {
        super(processorId);
        this.processorMap = processors.stream().collect(Collectors.toMap(Processor::getId, Function.identity()));
        this.processorInfo = String.format("[ %s %s %s ]", getId(), getType().name(), processorMap.size());
    }

    protected void init(List<String> processorIds, ProcessorConfigurator<T,C> configurator) {
        processorMap = new HashMap<>(processorIds.size());
        for(String id : processorIds) {
            Processor<T, C> processor = configurator.get(id);
            if(processor == null)
                throw new IllegalStateException();
            processorMap.put(id, processor);
        }
        this.processorInfo = String.format("[ %s | %s | %s ]", getId(), getType().name(), processorMap.size());
    }

    ///////////////

    @Override
    public ProcessorType getType() {
        return ProcessorType.AggregateProcessor;
    }

    public Collection<Processor<T, C>> getProcessors() {
        return processorMap.values();
    }

    protected ProcessingResult<T> invokeProcessor(T data, ProcessingContext<T, C> context, ProcessingResult<T> result, String processorId) {
        if(!processorMap.isEmpty() && processorMap.containsKey(processorId)) {
            context.setData(data);
            return getProcessor(processorId).process(context, result, getId());
        }
        return context.process(data, processorId, getId());
    }

    protected ProcessingResult<T> invokeProcessor(T data, Map<String, Object> args, ProcessingContext<T, C> context, ProcessingResult<T> result, String processorId) {
            context.setData(data);
            return getProcessor(processorId).process(args, context, result, getId());
    }

    public Processor<T,C> getProcessor(String processorId) {
        return processorMap.get(processorId);
    }

    protected void logProcessingDetails() {
        logInfo(this);
    }

    protected void logProcessingResult(ProcessingResult<T> processingResult) {
        logInfo(processingResult);
    }

    @Override
    public String info() {
        return processorMap.toString();
    }

}
