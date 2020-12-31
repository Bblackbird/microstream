package com.bblackbird.spi;

public class ConfiguratorFunctons<T, C> {

    public final CompositeProcessingPredicate<T, C> processingPredicate;

    public final TerminationHandler<T, C> terminationHandler;

    public final CompletionHandler<T, C> completionHandler;

    public ConfiguratorFunctons() {
        this(new DefaultCompositePredicate<>(), new DefaultTerminationHandler<>(), new DefaultCompletionHandler<>());
    }

    public ConfiguratorFunctons(CompositeProcessingPredicate<T, C> processingPredicate) {
        this(processingPredicate, new DefaultTerminationHandler<>(), new DefaultCompletionHandler<>());
    }

    public ConfiguratorFunctons(CompositeProcessingPredicate<T, C> processingPredicate, TerminationHandler<T, C> terminationHandler) {
        this(processingPredicate, terminationHandler, new DefaultCompletionHandler<>());
    }

    public ConfiguratorFunctons(CompositeProcessingPredicate<T, C> processingPredicate, TerminationHandler<T, C> terminationHandler, CompletionHandler<T, C> completionHandler) {
        this.processingPredicate = processingPredicate;
        this.terminationHandler = terminationHandler;
        this.completionHandler = completionHandler;
    }
}
