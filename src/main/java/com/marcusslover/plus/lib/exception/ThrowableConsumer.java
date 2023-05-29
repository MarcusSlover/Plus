package com.marcusslover.plus.lib.exception;

@FunctionalInterface
public interface ThrowableConsumer<T extends Throwable, V> {
    void accept(V object) throws T;
}
