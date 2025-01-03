package com.marcusslover.plus.lib.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

// not sure if this is a good idea
@Deprecated
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
     * Listens to an event.
     *
     * @param type    the event type
     * @param handler the event handler
     * @param <T>     the type of the event
     * @return the event reference
     * @since 4.1.0
     */
    public static <T extends Event> @NotNull EventReference<T> listen(@NotNull Class<T> type, @Nullable Consumer<T> handler) {
        return listen(type).handler(handler);
    }

    /**
     * Registers a listener.
     *
     * @param listener the listener
     * @param plugin   the plugin
     * @since 4.1.0
     */
    public static void register(@NotNull Listener listener, @NotNull Plugin plugin) {
        // register the listener
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Unregisters a listener.
     *
     * @param listener the listener
     * @since 4.1.0
     */
    public static void unregister(@NotNull Listener listener) {
        // unregister the listener
        HandlerList.unregisterAll(listener);
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
