package com.marcusslover.plus.lib.common.interfaces;

@FunctionalInterface
public interface ThrowableConsumer<T extends Throwable, V> {
    void accept(V object) throws T;
}
