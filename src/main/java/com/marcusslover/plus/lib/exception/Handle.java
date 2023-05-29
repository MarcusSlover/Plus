package com.marcusslover.plus.lib.exception;

import com.marcusslover.plus.lib.server.Delegates;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Simple class to handle lines of code that require try-catch statements in a cleaner fashion.
 */
public class Handle {
    private Handle() {
    }

    public static <T extends Exception, V> Optional<V> throwable(ThrowableSupplier<T, V> supplier) {
        Objects.requireNonNull(supplier, "supplier");

        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception t) {
            t.printStackTrace();
        }

        return Optional.empty();
    }

    public static <T extends Exception, V> Optional<V> throwable(ThrowableSupplier<T, V> supplier, Consumer<Exception> error) {
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(error, "error");

        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception t) {
            error.accept(t);
        }

        return Optional.empty();
    }

    public static <T extends Exception, V> Optional<V> throwable(ThrowableSupplier<T, V> supplier, Consumer<Exception> error, Runnable finallyHandle) {
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(finallyHandle, "finally-handle");
        Objects.requireNonNull(error, "error");

        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception t) {
            error.accept(t);
        } finally {
            finallyHandle.run();
        }

        return Optional.empty();
    }

    public static <T extends Exception> void throwable(ThrowableRunnable<T> handler) {
        Handle.throwable(Delegates.runnableToThrowableSupplier(handler));
    }

    public static <T extends Exception> void throwable(ThrowableRunnable<T> handler, Consumer<Exception> error) {
        Handle.throwable(Delegates.runnableToThrowableSupplier(handler), error);
    }

    public static <T extends Exception> void throwable(ThrowableRunnable<T> handler, Consumer<Exception> error, Runnable finallyHandler) {
        Handle.throwable(Delegates.runnableToThrowableSupplier(handler), error, finallyHandler);
    }

    public static void sanitizeStackTrace(Throwable throwable, int linesToRemove) {
        StackTraceElement[] originalStackTrace = throwable.getStackTrace();

        if (linesToRemove >= originalStackTrace.length) {
            return;
        }

        StackTraceElement[] newStackTrace = new StackTraceElement[originalStackTrace.length - linesToRemove];

        System.arraycopy(originalStackTrace, linesToRemove, newStackTrace, 0, newStackTrace.length);
        throwable.setStackTrace(newStackTrace);
    }
}
