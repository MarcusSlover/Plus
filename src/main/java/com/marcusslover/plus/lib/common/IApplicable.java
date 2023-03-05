package com.marcusslover.plus.lib.common;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
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

    default @NotNull V sendAll(@NotNull Predicate<T> predicate) {
        for (var sender : Bukkit.getOnlinePlayers()) {
            if (sender == null) {
                continue;
            }

            if (predicate.test((T) sender)) {
                this.apply((T) sender);
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
