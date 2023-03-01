package com.marcusslover.plus.lib.events;

import com.marcusslover.plus.lib.exceptions.EventHandlerException;
import com.marcusslover.plus.lib.lifecycle.ILifeCycle;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;

public interface Filter<T, V> extends ILifeCycle<Filter<T, V>> {
    BiConsumer<Object, Throwable> DEFAULT_EXCEPTION_CONSUMER = (o, throwable) -> {
        var e = new EventHandlerException(throwable, o);
        Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
    };

    /**
     * Add a expiry predicate.
     *
     * @param predicate the expiry test
     */
    @Nonnull
    V expireIf(@Nonnull Predicate<T> predicate);

    /**
     * Sets the expiry time on the handler
     *
     * @param duration the duration until expiry
     * @param unit     the unit for the duration
     * @throws IllegalArgumentException if duration is not greater than or equal to 1
     */
    @Nonnull
    V expireAfter(long duration, @Nonnull TimeUnit unit);

    /**
     * Sets the number of calls until the handler will automatically be unregistered
     *
     * <p>The call counter is only incremented if the event call passes all filters and if the handler completes
     * without throwing an exception.
     *
     * @param maxCalls the number of times the handler will be called until being unregistered.
     * @throws IllegalArgumentException if maxCalls is not greater than or equal to 1
     */
    @Nonnull
    V expireAfter(long maxCalls);

    /**
     * Adds a filter to the handler.
     *
     * <p>An event will only be handled if it passes all filters. Filters are evaluated in the order they are
     * registered.
     *
     * @param predicate the filter
     */
    @Nonnull
    V filter(@Nonnull Predicate<T> predicate);
}
