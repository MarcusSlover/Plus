package com.marcusslover.plus.lib.common.interfaces;

@FunctionalInterface
public interface ThrowableSupplier<T extends Throwable, V> {
    V get() throws T;
}
