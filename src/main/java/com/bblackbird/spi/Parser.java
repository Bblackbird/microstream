package com.bblackbird.spi;

public interface Parser<T, C> {

    ProcessingResult<T>parses(String message);

    ProcessingResult<T>parses(MessagePayload messagePayload);

}
