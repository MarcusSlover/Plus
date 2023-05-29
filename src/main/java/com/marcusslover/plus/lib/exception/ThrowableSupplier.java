package com.marcusslover.plus.lib.exception;

@FunctionalInterface
public interface ThrowableSupplier<T extends Throwable, V> {
    V get() throws T;
}
