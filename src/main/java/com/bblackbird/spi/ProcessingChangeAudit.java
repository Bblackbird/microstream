package com.bblackbird.spi;

public interface ProcessingChangeAudit {

    <T> String trackChange(String processorId, T value);

    <T> String trackAndLogChange(String processorId, T value);

    String changeLog();

}
