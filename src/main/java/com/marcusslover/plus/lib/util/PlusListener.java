package com.marcusslover.plus.lib.util;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@Deprecated
public interface PlusListener extends Listener {
     default void register() {
        Bukkit.getPluginManager().registerEvents(this, ServerUtils.getCallingPlugin());
    }

    default void unregister() {
        HandlerList.unregisterAll(this);
    }
}
