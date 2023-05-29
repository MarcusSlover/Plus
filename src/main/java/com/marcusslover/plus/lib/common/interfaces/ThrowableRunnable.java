package com.marcusslover.plus.lib.common.interfaces;

@FunctionalInterface
public interface ThrowableRunnable<T extends Throwable> {
    void run() throws T;
}
