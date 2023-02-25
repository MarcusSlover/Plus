package com.marcusslover.plus.lib.common;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ISendable<V extends ISendable<V>> {
    @NotNull <T extends CommandSender> V send(@NotNull T target);

    default @NotNull <T extends CommandSender> V send(@NotNull T... targets) {
        for (var target : targets) {
            this.send(target);
        }

        return (V) this;
    }

    default @NotNull <T extends CommandSender> V send(@NotNull Collection<T> targets) {
        for (var target : targets) {
            this.send(target);
        }

        return (V) this;
    }

    default @NotNull V sendAll() {
        return this.sendAll("");
    }

    default @NotNull V sendAll(String permission) {
        permission = permission == null ? "" : permission;

        for (var sender : Bukkit.getOnlinePlayers()) {
            if (permission.isBlank() || sender.hasPermission(permission)) {
                this.send(sender);
            }
        }

        return (V) this;
    }
}
