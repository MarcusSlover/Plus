package com.marcusslover.plus.lib.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class Events {

    /**
     * Listens to an event.
     *
     * @param type the event type
     * @param <T>  the type of the event
     * @return the event reference
     */
    public static <T extends Event> @NotNull EventReference<T> listen(@NotNull Class<T> type) {
        return EventReference.of(type);
    }
}
