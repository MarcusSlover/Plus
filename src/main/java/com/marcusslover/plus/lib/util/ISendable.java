package com.marcusslover.plus.lib.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ISendable<T extends CommandSender, V> {
    @NotNull V send(@NotNull T target);

    @NotNull V send(@NotNull T... targets);

    @NotNull V send(@NotNull Collection<T> targets);

    default @NotNull V sendAll() {
        return this.sendAll("");
    }

    default @NotNull V sendAll(String permission) {
        permission = permission == null ? "" : permission;

        for (CommandSender sender : Bukkit.getOnlinePlayers()) {
            if (permission.isBlank() || sender.hasPermission(permission)) {
                this.send((T) sender);
            }
        }

        return (V) this;
    }
}
