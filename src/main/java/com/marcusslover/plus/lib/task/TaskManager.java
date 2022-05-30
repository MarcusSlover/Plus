package com.marcusslover.plus.lib.task;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class TaskManager {

    private final @NotNull Plugin plugin;

    private TaskManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public static @NotNull TaskManager createManager(@NotNull Plugin plugin) {
        return new TaskManager(plugin);
    }

    public Task createTask(@NotNull Runnable runnable) {
        return new Task(plugin, runnable);
    }

    public Task createDelayedTask(long ticks, @NotNull Runnable runnable) {
        return new DelayedTask(ticks, plugin, runnable);
    }

    public @NotNull Plugin getPlugin() {
        return plugin;
    }
}
