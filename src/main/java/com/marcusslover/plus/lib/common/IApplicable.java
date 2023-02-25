package com.marcusslover.plus.lib.common;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface IApplicable<T extends LivingEntity, V> {
    @NotNull V apply(@NotNull T target);

    default @NotNull V apply(@NotNull T... targets) {
        for (var target : targets) {
            this.apply(target);
        }

        return (V) this;
    }

    default @NotNull V apply(@NotNull Collection<T> targets) {
        for (var target : targets) {
            this.apply(target);
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
                this.apply((T) sender);
            }
        }

        return (V) this;
    }
}
