package me.marcusslover.plus.lib.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DelayedTask extends Task {
    public DelayedTask(long ticks, @NotNull Plugin plugin, @NotNull Runnable runnable) {
        super(runnable);
        Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
    }
}
