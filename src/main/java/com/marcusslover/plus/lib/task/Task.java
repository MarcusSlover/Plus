package com.marcusslover.plus.lib.task;

import com.marcusslover.plus.lib.util.RequiresManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@RequiresManager
public class Task {
    protected final @NotNull Plugin plugin;
    protected final @NotNull Runnable runnable;

    public Task(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        this.plugin = plugin;
        this.runnable = runnable;
    }

    public @NotNull Runnable getRunnable() {
        return runnable;
    }

    public void executeSync() {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public void executeAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }
}
