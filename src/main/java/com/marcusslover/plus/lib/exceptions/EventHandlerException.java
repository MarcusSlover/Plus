package com.marcusslover.plus.lib.exceptions;

public class EventHandlerException extends RuntimeException {
    public EventHandlerException(Throwable cause, Object event) {
        super(event.getClass().getName(), cause);
    }
}