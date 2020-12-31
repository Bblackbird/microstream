package com.bblackbird.spi;

public interface ProcessingPredicate<T, C> {

    boolean test(T value, ProcessingContext<T, C> context);
}
