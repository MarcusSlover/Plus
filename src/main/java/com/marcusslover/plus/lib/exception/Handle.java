package com.marcusslover.plus.lib.exception;

import com.marcusslover.plus.lib.server.Delegates;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

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

    public static <T extends Exception> void throwable(ThrowableRunnable<T> handler, Consumer<EscapedException> error) {
        Objects.requireNonNull(handler, "supplier");
        Objects.requireNonNull(error, "error");

        try {
            ThrowableSupplier<Exception, Void> supplier = Delegates.runnableToThrowableSupplier(handler);

            supplier.get();
        } catch (Exception t) {
            if (t instanceof EscapedException) {
                error.accept((EscapedException) t);
            } else {
                error.accept(new EscapedException(t));
            }
        }
    }

    public static <T extends Exception> void throwable(ThrowableRunnable<T> handler, Consumer<EscapedException> error, ThrowableRunnable<T> finallyHandler) {
        Objects.requireNonNull(handler, "supplier");
        Objects.requireNonNull(error, "error");
        Objects.requireNonNull(finallyHandler, "finally-handler");

        try {
            ThrowableSupplier<Exception, Void> supplier = Delegates.runnableToThrowableSupplier(handler);

            supplier.get();
        } catch (Exception t) {
            if (t instanceof EscapedException) {
                error.accept((EscapedException) t);
            } else {
                error.accept(new EscapedException(t));
            }
        } finally {
            Handle.throwable(finallyHandler);
        }
    }

    public static <T extends Exception, A extends AutoCloseable> void withResource(ThrowableSupplier<T, A> supplier, ThrowableConsumer<T, A> consumer) {
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(consumer, "consumer");

        try (A resource = supplier.get()) {
            consumer.accept(resource);
        } catch (Exception t) {
            t.printStackTrace();
        }
    }

    public static <T extends Exception, A extends AutoCloseable> void withResource(ThrowableSupplier<T, A> supplier, ThrowableConsumer<T, A> consumer, Consumer<Exception> error) {
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(consumer, "consumer");
        Objects.requireNonNull(error, "error");

        try (A resource = supplier.get()) {
            consumer.accept(resource);
        } catch (Exception t) {
            error.accept(t);
        }
    }

    public static <T extends Exception, A extends AutoCloseable> void withResource(ThrowableSupplier<T, A> supplier, ThrowableConsumer<T, A> consumer, Consumer<Exception> error, ThrowableRunnable<T> finallyHandle) {
        Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(consumer, "consumer");
        Objects.requireNonNull(error, "error");
        Objects.requireNonNull(finallyHandle, "finally-handle");

        try (A resource = supplier.get()) {
            consumer.accept(resource);
        } catch (Exception t) {
            error.accept(t);
        } finally {
            Handle.throwable(finallyHandle);
        }
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
