package com.marcusslover.plus.lib.container.extra;

/**
 * Indicates that this object can be written to files without having to call
 * its handler or manager class aka the container.
 *
 * @param <T> Type of the object.
 */
public interface ICanSave<T> {

    /**
     * Saves the object.
     */
    void save();
}

