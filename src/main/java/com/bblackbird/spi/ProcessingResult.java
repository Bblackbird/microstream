package com.bblackbird.spi;

import java.util.function.Function;

public interface ProcessingResult<T> {

   T getData();

   void setData(T data);

   String getProcessorId();

   ProcessingState getProcessingState();

   boolean isProcessed();

   boolean hasFailed();

   void setProcessingState(ProcessingState processingState);

   boolean isModifie();

   void setModified(boolean isModified);

   long getProcessingTime();

   void addApplyChanges(final Function<T, Boolean> apply);

   boolean applyChanges(T value);

   void addViolation(Violation violation);

   void addViolations(Violations violations);

   void addUnexpectedViolation(Exception e, Class<?> exceptionCausingClass);

    void addUnexpectedViolation(String errorMsg, Exception e, Class<?> exceptionCausingClass);

    Violations getViolations();

    boolean hasViolations();

    boolean hasFatalErrors();

    boolean hasErrors();

    boolean hasWarnings();

    boolean hasWarningsOnly();

    <S> void setComputedData(S data);

    <S> S getComputedData();

}
