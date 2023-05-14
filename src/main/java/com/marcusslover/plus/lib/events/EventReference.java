package com.marcusslover.plus.lib.events;

import com.marcusslover.plus.lib.lifecycle.ILifeCycle;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Data(staticConstructor = "of")
@Accessors(fluent = true, chain = true)
@Getter(AccessLevel.PACKAGE)
public class EventReference<T extends Event> implements ILifeCycle<EventReference<T>> {
    private final @NotNull Class<T> base;

    private @NotNull EventPriority eventPriority = EventPriority.NORMAL;
    @Setter(AccessLevel.PACKAGE)
    private @NotNull Class<? extends T>[] merged;
    private @Nullable Consumer<T> handler = null;

    @Setter(AccessLevel.NONE)
    private Listener[] listeners = new Listener[0];

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
    public @NotNull EventReference<T> asRegistered(@NotNull Plugin plugin) {
        //this.registerListener(this.base, plugin); // register the base event

        // register the merged events
        for (Class<? extends T> type : this.merged) {
            this.registerListener(type, plugin);
        }
        return this;
    }

    /**
     * Register a listener.
     *
     * @param type   the event type
     * @param plugin the plugin
     */
    private void registerListener(@NotNull Class<? extends T> type, @NotNull Plugin plugin) {
        Listener listener = new Listener() {
        };
        Bukkit.getPluginManager().registerEvent(type, listener, this.eventPriority, (__, event) -> {
            if (this.handler != null) {
                this.handler.accept(this.base.cast(event));
            }
        }, plugin);

        // add listener to the listeners array
        Listener[] listeners = new Listener[this.listeners.length + 1];
        System.arraycopy(this.listeners, 0, listeners, 0, this.listeners.length);
        listeners[this.listeners.length] = listener;
        this.listeners = listeners;
    }

    @Override
    public void unregister() {
        for (Listener listener : this.listeners) {
            HandlerList.unregisterAll(listener);
        }
    }
}
