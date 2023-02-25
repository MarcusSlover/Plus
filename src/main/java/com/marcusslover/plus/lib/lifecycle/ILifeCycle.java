package com.marcusslover.plus.lib.lifecycle;

/**
 * Represents an object that has its own life cycle.
 * A life usually has a start and an end.
 *
 * @param <T> the type of the life cycle
 */
public interface ILifeCycle<T extends ILifeCycle<T>> {

    /**
     * Wraps an object into a life cycle.
     *
     * @return the life cycle
     */
    default LifeCycle<T> lifeCycle() {
        //noinspection unchecked
        return new LifeCycle<>(() -> (T) this);
    }

    /**
     * Unregisters the life cycle.
     */
    void unregister();
}
