package com.marcusslover.plus.lib.events;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The event reference.
 *
 * @param <T> the type of the event
 */
public class EventReference<T extends Event> implements Filter<T, EventReference<T>> {
    private final @NotNull Class<T> eventClass;

    private @NotNull EventPriority eventPriority = EventPriority.NORMAL;

    private @Nullable Consumer<T> handler = null;

    private final List<Predicate<T>> filters = new ArrayList<>();
    private final List<Predicate<T>> expireList = new ArrayList<>();

    private final AtomicLong callCount = new AtomicLong(0);
    private final AtomicLong maxCalls = new AtomicLong(0);

    public final long[] established = new long[0];
    public final long[] tillClose = new long[0];

    private @NotNull Class<? extends T>[] merged;
    private Listener[] listeners = new Listener[0];


    private final AtomicBoolean active = new AtomicBoolean(true);

    private @Nullable Plugin plugin;

    public EventReference(@NotNull Class<T> eventClass) {
        this(eventClass, new Class[]{eventClass});
    }

    public EventReference(@NotNull Class<T> eventClass, Class<? extends T>[] classes) {
        this.eventClass = eventClass;
        this.merged = classes;
    }

    /**
     * Allows the event to be merged with another event,
     * as long as the other event is a subclass of the base event.
     *
     * @param otherEvent the other event
     * @return the event reference
     */
    public @NotNull EventReference<T> merge(@NotNull Class<? extends T> otherEvent) {
        // add the otherEvent to the merged array
        @SuppressWarnings("unchecked") Class<? extends T>[] merged = new Class[this.merged.length + 1];
        System.arraycopy(this.merged, 0, merged, 0, this.merged.length);
        merged[this.merged.length] = otherEvent;
        this.merged = merged;

        return this;
    }

    /**
     * Register the event.
     */
    public @NotNull EventReference<T> bind(@NotNull Plugin plugin) {
        Preconditions.checkNotNull(this.handler, "No handler was set for the event");

        this.plugin = plugin;

        this.listeners = new Listener[this.merged.length];

        for (int i = 0; i < this.merged.length; i++) {
            final Class<? extends T> type = this.merged[i];
            Bukkit.getPluginManager().registerEvent(type, this.listeners[i] = new Listener() {
            }, this.eventPriority, (listener, event) -> {
                T eventInstance = this.eventClass.cast(event);

                try {
                    /* check filters */
                    for (Predicate<T> filter : this.filters) {
                        if (!filter.test(eventInstance)) {
                            return;
                        }
                    }

                    /* check expiry tests */
                    if (this.test(eventInstance)) {
                        this.unregister();
                        return;
                    }

                    if (this.handler != null) {
                        this.handler.accept(eventInstance);

                        this.callCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    DEFAULT_EXCEPTION_CONSUMER.accept(this, e);
                }
            }, plugin);
        }

        this.established[0] = System.currentTimeMillis();

        return this;
    }

    private boolean test(T eventInstance) {
        if (!this.active.get()) {
            return true;
        }

        long calls = this.callCount.get();
        if (calls > 0 && calls >= this.maxCalls.get()) {
            return true;
        }

        if (this.tillClose[0] > 0 && System.currentTimeMillis() - this.established[0] >= this.tillClose[0]) {
            return true;
        }

        for (Predicate<T> predicate : this.expireList) {
            if (predicate.test(eventInstance)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void unregister() {
        for (Listener listener : this.listeners) {
            HandlerList.unregisterAll(listener);
        }
    }

    @NotNull
    @Override
    public EventReference<T> expireIf(@NotNull Predicate<T> predicate) {
        this.expireList.add(predicate);

        return this;
    }

    @NotNull
    @Override
    public EventReference<T> expireAfter(long duration, @NotNull TimeUnit unit) {
        Preconditions.checkArgument(this.maxCalls.get() > 0, "duration must be greater than or equal to 1");

        this.tillClose[0] = TimeUnit.MILLISECONDS.convert(duration, unit);

        return this;
    }

    @NotNull
    @Override
    public EventReference<T> expireAfter(long maxCalls) {
        Preconditions.checkArgument(this.maxCalls.get() > 0, "maxCalls must be greater than or equal to 1");

        this.maxCalls.set(maxCalls);

        return this;
    }

    @NotNull
    @Override
    public EventReference<T> filter(@NotNull Predicate<T> predicate) {
        this.filters.add(predicate);

        return this;
    }

    /* Getters & Setters */

    public @NotNull Class<T> getEventClass() {
        return this.eventClass;
    }

    public boolean isActive() {
        return this.active.get();
    }

    public boolean isClosed() {
        return !this.active.get();
    }

    public long getCallsCounted() {
        return this.callCount.get();
    }

    public EventPriority getPriority() {
        return this.eventPriority;
    }

    public @Nullable Plugin getPlugin() {
        return this.plugin;
    }

    public EventReference<T> handler(@NotNull Consumer<T> handler) {
        this.handler = handler;
        return this;
    }

    public EventReference<T> handler(@NotNull BiConsumer<EventReference<T>, T> handler) {
        this.handler = t -> handler.accept(EventReference.this, t);
        return this;
    }

    public EventReference<T> priority(@NotNull EventPriority eventPriority) {
        this.eventPriority = eventPriority;
        return this;
    }

    /* Static Constructors */

    public static <T extends Event> EventReference<T> of(@NotNull Class<T> eventClass) {
        return new EventReference<>(eventClass);
    }

    public static <T extends Event> EventReference<T> of(Class<T> eventClass, Class<? extends T>[] classes) {
        return new EventReference<>(eventClass, classes);
    }
}
