package com.marcusslover.plus.lib.events;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class EventManager {
    private final @NotNull List<Listener> listeners = new ArrayList<>();
    private final @NotNull Plugin plugin;

    public EventManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(@NotNull Listener... listeners) {
        for (Listener listener : listeners) {
            try {
                this.register(listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void register(@NotNull Listener listener) {
        this.testPlugin();
        if (this.listeners.contains(listener)) {
            return;
        }
        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
        this.listeners.add(listener);
    }

    public void unregister(@NotNull Listener listener) {
        this.testPlugin();
        if (!this.listeners.contains(listener)) {
            return;
        }
        HandlerList.unregisterAll(listener);
        this.listeners.remove(listener);
    }

    public void clearEvents() {
        this.unregisterAll();
    }

    public void unregisterAll() {
        this.testPlugin();
        this.listeners.forEach(HandlerList::unregisterAll);
        this.listeners.clear();
    }

    private void testPlugin() {
        if (!this.plugin.isEnabled()) {
            throw new IllegalStateException("Could not perform operation because Plugin is not enabled.");
        }
    }

    public @NotNull List<Listener> listeners() {
        return this.listeners;
    }

    public @NotNull Plugin plugin() {
        return this.plugin;
    }
}
