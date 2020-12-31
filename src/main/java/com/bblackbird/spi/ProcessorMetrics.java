package com.bblackbird.spi;

import java.util.Map;

public interface ProcessorMetrics {

    void update(String counter, long executionDuration, boolean success);

    void reset();

    void clear();

    String getStats();

    String getSimpleStats();

    String getRawStats();

    Map<String, long[]> getSerializedStats();

}
