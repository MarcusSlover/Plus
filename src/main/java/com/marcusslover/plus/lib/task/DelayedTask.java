package com.marcusslover.plus.lib.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DelayedTask extends Task {
    private final long ticks;

    public DelayedTask(long ticks, @NotNull Plugin plugin, @NotNull Runnable runnable) {
        super(plugin, runnable);
        this.ticks = ticks;
    }

    @Override
    public void executeSync() {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
    }

    @Override
    public void executeAsync() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks);
    }


}
