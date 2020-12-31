package com.bblackbird.spi;

public interface ProcessingContextDictionary {

    Object setValue(String key, Object value);

    Object getValue(String key);

    Object removeValue(String key);

}
