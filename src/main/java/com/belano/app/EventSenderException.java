package com.belano.app;

public class EventSenderException extends RuntimeException {
    public EventSenderException(Exception e) {
        super(e);
    }
}
