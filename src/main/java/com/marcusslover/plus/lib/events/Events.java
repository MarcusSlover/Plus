package com.marcusslover.plus.lib.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

// not sure if this is a good idea
public class Events {

    /**
     * Listens to an event.
     *
     * @param type the event type
     * @param <T>  the type of the event
     * @return the event reference
     */
    public static <T extends Event> @NotNull EventReference<T> listen(@NotNull Class<T> type) {
        //noinspection unchecked
        return listen(type, new Class[0]);
    }

    /**
     * Listens to multiple events.
     *
     * @param type    the event type
     * @param classes the classes
     * @param <T>     the type of the event
     * @return the event reference
     */
    @Deprecated // messy
    public static <T extends Event> @NotNull EventReference<T> listen(@NotNull Class<T> type, @NotNull Class<? extends T>... classes) {
        return EventReference.of(type, classes);
    }
}
