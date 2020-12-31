package com.bblackbird.spi;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class CompositeProcessor<T, C> extends AbstractProcessor<T, C> {


    protected final List<Processor<T, C>> processors;
    protected final CompositeProcessingPredicate<T, C> compositePredicate;
    protected final TerminationHandler<T, C> terminationHandler;
    protected final CompletionHandler<T, C> completionHandler;


    public CompositeProcessor(String processorId, List<String> processorIds, ProcessorConfigurator<T, C> configurator) {
        this(processorId, processorIds, configurator, new DefaultCompositePredicate<>(), new DefaultTerminationHandler<>(), new DefaultCompletionHandler<>());
    }

    public CompositeProcessor(String processorId, List<String> processorIds, ProcessorConfigurator<T, C> configurator, ConfiguratorFunctons<T, C> configuratorFunctons) {
        this(processorId, processorIds, configurator, configuratorFunctons.processingPredicate, configuratorFunctons.terminationHandler, configuratorFunctons.completionHandler);
    }

    public CompositeProcessor(String processorId, List<String> processorIds, ProcessorConfigurator<T, C> configurator, CompositeProcessingPredicate<T, C> compositePredicate,
                              TerminationHandler<T, C> terminationHandler, CompletionHandler<T, C> completionHandler) {

        super(processorId);
        processors = processorIds.stream().map(id -> configurator.get(id)).collect(Collectors.toList());
        this.compositePredicate = compositePredicate;
        this.terminationHandler = terminationHandler;
        this.completionHandler = completionHandler;

    }

    /////////

    @SafeVarargs
    public CompositeProcessor(Processor<T, C>... processor) {
        this(CompositeProcessor.class.getSimpleName(), Arrays.asList(processor));
    }

    public CompositeProcessor(List<Processor<T, C>> processors) {
        this(CompositeProcessor.class.getSimpleName(), processors, new DefaultCompositePredicate<>());
    }

    @SafeVarargs
    public CompositeProcessor(String processorId, Processor<T, C>... processor) {
        this(processorId, Arrays.asList(processor));
    }

    public CompositeProcessor(String processorId, List<Processor<T, C>> processors) {
        this(processorId, processors, new DefaultCompositePredicate<>());
    }

    public CompositeProcessor(String processorId, List<Processor<T, C>> processors, CompositeProcessingPredicate<T, C> compositePredicate) {
        this(processorId, processors, compositePredicate, new DefaultTerminationHandler<>(), new DefaultCompletionHandler<>());
    }

    public CompositeProcessor(String processorId, List<Processor<T, C>> processors, ConfiguratorFunctons<T, C> configuratorFunctons) {
        this(processorId, processors, configuratorFunctons.processingPredicate, configuratorFunctons.terminationHandler, configuratorFunctons.completionHandler);
    }

    public CompositeProcessor(String processorId, List<Processor<T, C>> processors, CompositeProcessingPredicate<T, C> compositePredicate,
                              TerminationHandler<T, C> terminationHandler, CompletionHandler<T, C> completionHandler) {
        super(processorId);
        this.processors = processors;
        this.compositePredicate = compositePredicate;
        this.terminationHandler = terminationHandler;
        this.completionHandler = completionHandler;
        this.processorInfo = String.format("[ %s | %s | %s ]", getId(), getType().name(), processors.size());
    }

    ///////////////

    @Override
    public ProcessingPredicate<T, C> predicate() {
        return (v, c) -> true;
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.CompositeProcessor;
    }

    @Override
    public String info() {
        return processors.toString();
    }

    public List<Processor<T, C>> geProcessors() {
        return processors;
    }

    @Override
    protected boolean doProcess(T value, ProcessingContext<T, C> context, ProcessingResult<T> result) {

        boolean sucess = true;
        for (Processor<T, C> processor : processors) {

            ProcessingResult<T> r = processor.process(value, context);
            if (sucess)
                sucess = !r.hasViolations();

            if (!compositePredicate.test(r, context)) {
                r.setProcessingState(ProcessingState.Terminated);
                terminationHandler.handleTermination(value, r, context);
                return false;
            }

            if (r.getProcessingState() == ProcessingState.Completed) {
                completionHandler.complete(value, r, context);
                return sucess;
            }

        }

        return sucess;
    }


    protected void logProcessorDetails() {
        logInfo(this);
    }

    protected void logProcessingResult(ProcessingResult<T> processingResult) {
        logInfo(processingResult);
    }

    @Override
    public void insertBefore(String processorId, Processor<T, C> processor) {
        checkNotNull(processorId);
        checkNotNull(processor);
        for (int i = 0; i < processors.size(); i++) {
            if (processors.get(i).getId().equals(processorId)) {
                processors.add(i - 1, processor);
                break;
            }
        }

    }

    @Override
    public Processor<T, C> replace(String processorId, Processor<T, C> processor) {
        checkNotNull(processorId);
        checkNotNull(processor);
        Processor<T, C> oldProcessor = null;
        for (int i = 0; i < processors.size(); i++) {
            oldProcessor = processors.get(i);
            if (oldProcessor.getId().equals(processorId)) {
                processors.set(i, processor);
                break;
            }
        }
        return oldProcessor;

    }

    @Override
    public Processor<T, C> remove(String processorId) {
        checkNotNull(processorId);
        Iterator<Processor<T, C>> iter = processors.iterator();
        while (iter.hasNext()) {
            Processor<T, C> p = iter.next();
            if (p.getId().equals(processorId)) {
                iter.remove();
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CompositeProcessor<?, ?> that = (CompositeProcessor<?, ?>) o;
        return Objects.equals(processors, that.processors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), processors);
    }
}
