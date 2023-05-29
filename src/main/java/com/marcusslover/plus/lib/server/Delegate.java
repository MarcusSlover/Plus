package com.marcusslover.plus.lib.server;

/**
 * Represents a class which delegates calls to a different object.
 *
 * @param <T> the delegate type
 */
public interface Delegate<T> {

    static Object resolve(Object obj) {
        while (obj instanceof Delegate<?>) {
            Delegate<?> delegate = (Delegate<?>) obj;
            obj = delegate.getDelegate();
        }
        return obj;
    }

    /**
     * Gets the delegate object
     *
     * @return the delegate object
     */
    T getDelegate();

}