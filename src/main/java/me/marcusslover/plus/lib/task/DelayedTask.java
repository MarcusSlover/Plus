package me.marcusslover.plus.lib.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class DelayedTask extends Task {
    public DelayedTask(long ticks, Plugin plugin, Runnable runnable) {
        super(runnable);
        Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
    }
}
