package com.marcusslover.plus.lib.exception;

@FunctionalInterface
public interface ThrowableRunnable<T extends Throwable> {
    void run() throws T;
}
