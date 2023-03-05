package com.marcusslover.plus.lib.common;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public interface ISendable<V extends ISendable<V>> {
    @NotNull <T extends CommandSender> V send(@NotNull T target);

    @NotNull V send(Audience audience);

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

    default @NotNull <T extends CommandSender> V sendAll(@NotNull Predicate<T> predicate) {
        for (var sender : Bukkit.getOnlinePlayers()) {
            if (predicate.test((T) sender)) {
                this.send(sender);
            }
        }

        return (V) this;
    }

    default @NotNull V sendAll(@Nullable String permission) {
        final String p = permission == null ? "" : permission;

        return this.sendAll(sender -> {
            if (p.isBlank()) {
                return true;
            }

            return sender.hasPermission(p);
        });
    }

    default @NotNull V sendAll() {
        return this.sendAll("");
    }
}
