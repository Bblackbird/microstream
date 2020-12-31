package com.bblackbird.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.*;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractProcessor<T, C> implements Processor<T, C>, Parser<T, C> {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);
    
    
    protected static final String[] NO_DEPENDENCIES = {};
    
    protected String processorId;
    protected String processorInfo;
    protected String[] dependencies = NO_DEPENDENCIES;
    protected ProcessingPredicate<T,C> processingPredicate = (v,c) -> true;
    
    abstract protected boolean doProcess(T value, ProcessingContext<T, C> context, ProcessingResult<T> result);
    
    protected boolean doProcess(T value, Map<String, Object> args,ProcessingContext<T, C> context, ProcessingResult<T> result){
        return doProcess(value, context, result);
    }
    

    protected AbstractProcessor() {
        defaultInit();
    }

    protected AbstractProcessor(ProcessingPredicate<T, C> processingPredicate) {
        checkNotNull(processingPredicate);
        this.processingPredicate = processingPredicate;
        defaultInit();
    }

    protected AbstractProcessor(String processorId) {
        checkNotNull(processorId);
        this.processorId = processorId;
        initProcessorInfo();
    }

    protected AbstractProcessor(String[] dependencies, int minDepSize) {
        this();
        checkNotNull(dependencies);
        checkArgument(dependencies.length >= minDepSize);
        this.dependencies = dependencies;
        initProcessorInfo();
    }

    public AbstractProcessor(String processorId, ProcessingPredicate<T, C> processingPredicate) {
        this(processorId, NO_DEPENDENCIES, processingPredicate);
    }

    public AbstractProcessor(String processorId, String[] dependencies) {
        checkNotNull(processorId);
        checkNotNull(dependencies);
        this.processorId = processorId;
        this.dependencies = dependencies;
        initProcessorInfo();
    }

    public AbstractProcessor(String processorId, String[] dependencies, ProcessingPredicate<T, C> processingPredicate) {
        checkNotNull(processorId);
        checkNotNull(dependencies);
        checkNotNull(processingPredicate);
        this.processorId = processorId;
        this.dependencies = dependencies;
        this.processingPredicate = processingPredicate;
        initProcessorInfo();
    }

    private void defaultInit() {
        initDefaultProcessorId();
        initProcessorInfo();
    }

    private void initProcessorInfo() {
        this.processorInfo = String.format("[ %s | %s ]", getId(), getType().name());
    }

    private void initDefaultProcessorId() {
        this.processorId = getProcessorId(getClass());
    }

    public static String geDefaultProcessorId(Class<?> klazz) {
        return klazz.getSimpleName();
    }

    private String getProcessorId(Class<? extends AbstractProcessor> klazz) {
        return geDefaultProcessorId(klazz);
    }


    @Override
    public String getId() {
        return processorId;
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.Processor;
    }

    @Override
    public String[] dependencies() {
        return dependencies;
    }

    @Override
    public ProcessingPredicate<T, C> predicate() {
        return processingPredicate;
    }

    @Override
    public ProcessingResult<T> createProcessingResult(ProcessingContext<T, C> context) {
        return context.createProcessingResult(getId());
    }

    @Override
    public ProcessingResult<T> process(ProcessingContext<T, C> context) {
        logProcessorDetails();
        if(context.isProcessed(getId())) {
            ProcessingResult<T> processingResult = context.getProcessingResult(getId());
            logProcessedResult(processingResult);
            return processingResult;
        }
        ProcessingResult<T> processingResult = createProcessingResult(context);
        return processInternal(context, processingResult);
    }

    @Override
    public ProcessingResult<T> process(T value, ProcessingContext<T, C> context) {
        context.setData(value);
        return process(context);
    }

    @Override
    public ProcessingResult<T> process(T value, ProcessingContext<T, C> context, ProcessingResult<T> processingResult) {
        context.setData(value);
        return process(context, processingResult);
    }

    // TODO???
    @Override
    public ProcessingResult<T> process(T value, ProcessingContext<T, C> context, String callerProcessorId) {

        context.setData(value);
        logProcessorDetails();
        if(context.isProcessed(getId())) {
            ProcessingResult<T> processingResult = context.getProcessingResult(getId());
            //logProcessedResult(processingResult);
            return processingResult;
        }
        ProcessingResult<T> processingResult = createProcessingResult(context);
        return processInternal(context, processingResult);
    }

    @Override
    public ProcessingResult<T> process(T value, ProcessingContext<T, C> context, ProcessingResult<T> processingResult, String callerProcessorId) {
        context.setData(value);
        return process(context, processingResult, callerProcessorId);
    }

    // Incoming ProcessingResult is not reused because of "applyChange" logic, but only the violations are passed back to invoking processor.
    @Override
    public ProcessingResult<T> process(ProcessingContext<T, C> context, ProcessingResult<T> processingResult) {
        logProcessorDetails();
        ProcessingResult<T> result = createProcessingResult(context);
        ProcessingResult<T> r = processInternal(context, result);
        if(r.hasViolations()) {
            for(Violation v : r.getViolations()) {
                processingResult.addViolation(v);
            }
        }
        return r;
    }

    @Override
    public ProcessingResult<T> process(ProcessingContext<T, C> context, ProcessingResult<T> processingResult, String callerProcessorId) {
        logProcessorDetails(callerProcessorId);
        // process(context, processingResult);
        ProcessingResult<T> result = createProcessingResult(context);
        ProcessingResult<T> r = processInternal(context, result);
        if(r.hasViolations()) {
            for(Violation v : r.getViolations()) {
                processingResult.addViolation(v);
            }
        }
        return r;
    }

    @Override
    public ProcessingResult<T> process(Map<String, Object> args, ProcessingContext<T, C> context, ProcessingResult<T> processingResult, String callerProcessorId) {
        logProcessorDetails(callerProcessorId);
        ProcessingResult<T> result = createProcessingResult(context);
        ProcessingResult<T> r = processInternal(context, args, result);
        if(r.hasViolations()) {
            for(Violation v : r.getViolations()) {
                processingResult.addViolation(v);
            }
        }
        return r;
    }

    private ProcessingResult<T> processInternal(ProcessingContext<T,C> context, ProcessingResult<T> processingResult) {
        return processInternal(context, Collections.emptyMap(), processingResult);
    }

    private ProcessingResult<T> processInternal(ProcessingContext<T,C> context, Map<String, Object> args, ProcessingResult<T> processingResult) {
        try{
            processingResult.setProcessingState(ProcessingState.Processing);
            if(!predicate().test(context.getData(), context)) {
                processingResult.setProcessingState(ProcessingState.Skipped);
                context.add(processingResult);
                return processingResult;
            }
            
            boolean success = false;
            if(args.isEmpty()) {
                success = doProcess(context.getData(), context, processingResult);
            } else {
                success = doProcess(context.getData(), args, context, processingResult);
            }
            setProcessingState(processingResult, success);
            
            processingResult.applyChanges(context.getData());
            context.add(processingResult);
            
            String change = context.trackChange(processorId, context.getData());
            logDebug(change);
        } catch(Exception e) {
            if(processingResult != null){
                processingResult.addUnexpectedViolation(e, this.getClass());
                processingResult.setProcessingState(ProcessingState.Failed);
            }
        } finally {
            logProcessedResult(processingResult);
        }
        return processingResult;
    }


    private void setProcessingState(ProcessingResult<T> processingResult, boolean isSuccess) {
        ProcessingState currentState = processingResult.getProcessingState();
        if(ProcessingState.Completed == currentState || ProcessingState.Skipped == currentState) {
            return;
        }
        if(isSuccess) {
            processingResult.setProcessingState(ProcessingState.Succeded);
        } else {
            processingResult.setProcessingState(ProcessingState.Failed);
        }
    }


    @Override
    public ProcessingContext<T, C> createProcessingContext(T data, C context) {
        ProcessingContext<T, C> processingContext = new ProcessingContextImpl<>(data, context);
        this.getProcessors().forEach(p -> processingContext.getProcessorProvider().register(p.getId(), p));
        return processingContext;
    }

    @Override
    public ProcessingContext<T, C> createProcessingContext(T data, C context, Function<String, ProcessingResult<T>> processingResultCreator) {
        ProcessingContext<T, C> processingContext = new ProcessingContextImpl<>(data, context, processingResultCreator);
        this.getProcessors().forEach(p -> processingContext.getProcessorProvider().register(p.getId(), p));
        return processingContext;
    }

    @Override
    public ProcessingContext<T, C> createProcessingContext(ProcessorProviderAndSupplier<T, C> processorProviderAndSupplier) {
        ProcessingContext<T, C> processingContext = new ProcessingContextImpl<>(processorProviderAndSupplier);
        this.getProcessors().forEach(p -> processingContext.getProcessorProvider().register(p.getId(), p));
        return processingContext;
    }


    @Override
    public String info() {
        return processorInfo;
    }

    @Override
    public Collection<Processor<T, C>> getProcessors() {
        return null;
    }

    @Override
    public void insertBefore(String processorId, Processor<T, C> processor) {

    }

    @Override
    public Processor<T, C> remove(String processorId) {
        return null;
    }

    @Override
    public Processor<T, C> replace(String processorId, Processor<T, C> replacementProcessor) {
        return null;
    }

    // Parser methods


    @Override
    public ProcessingResult<T> parses(String message) {
        return null;
    }

    @Override
    public ProcessingResult<T> parses(MessagePayload messagePayload) {
        return null;
    }

    // Helper methods

    protected void logProcessorDetails() {
        logDebug(this);
    }

    protected void logProcessorDetails(String callerProcessorId) {
        StringBuilder s = new StringBuilder(callerProcessorId);
        s.append(" => ").append(toString());
        logDebug(s);
    }


    protected void logProcessingResult(ProcessingResult<T> processingResult) {
        logDebug(processingResult);
    }

    protected void logProcessedResult(String callerProcessorId) {
        logDebug(callerProcessorId + " => " + getId());
    }

    protected void logProcessedResult(ProcessingResult<T> processingResult) {
        logDebug("Processed => " + processingResult.toString());
    }

    protected void logDebug(Logger logger, Object obj) {
        if(logger.isDebugEnabled())
            logger.debug(obj.toString());
    }

    protected void logDInfo(Logger logger, Object obj) {
        if(logger.isInfoEnabled())
            logger.debug(obj.toString());
    }

/*    protected void log(Logger logger, Level level, Object obj) {
        if(logger.isEnabledFor(level))
            logger.log(obj);
    }*/

    protected void logDebug(Object obj) {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug(obj.toString());
    }

    protected void logInfo(Object obj) {
        if(LOGGER.isDebugEnabled())
            LOGGER.debug(obj.toString());
    }


    protected void logDebug(String change) {
    }

    @Override
    public String toString() {
        return "AbstractProcessor{" +
                "processorInfo='" + processorInfo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractProcessor<?, ?> that = (AbstractProcessor<?, ?>) o;
        return Objects.equals(processorId, that.processorId) &&
                Objects.equals(processorInfo, that.processorInfo) &&
                Arrays.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(processorId, processorInfo);
        result = 31 * result + Arrays.hashCode(dependencies);
        return result;
    }
}
