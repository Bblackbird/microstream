package com.bblackbird.spi;

public class MessagePayload {

    public final String message;
    public final String previousMessage;
    public final long receiveUtcTime;
    public final long externalId;

    public MessagePayload(String message, String previousMessage, long receiveUtcTime, long externalId) {
        this.message = message;
        this.previousMessage = previousMessage;
        this.receiveUtcTime = receiveUtcTime;
        this.externalId = externalId;
    }
}
