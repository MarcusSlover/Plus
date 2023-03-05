package com.marcusslover.plus.lib.item;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a menu registry that holds all the menus.
 * Each canvas is different and belongs to a player.
 */
@Data
@Accessors(fluent = true, chain = true)
public abstract class Menu implements IMenu {
    private final @NotNull Map<UUID, Canvas> canvasMap = new HashMap<>(); // player -> canvas
    private @Nullable String id; // menu id, not required

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.NONE)
    private @Nullable MenuManager manager;

    void hookManager(@Nullable MenuManager manager) {
        this.manager = manager;
    }

    @Override
    public @NotNull <T extends CommandSender> IMenu send(@NotNull T target) {
        if (target instanceof Player player) {
            if (this.manager == null) {
                return this;
            }
            this.manager.internallyOpen(player, this);
        }
        return this;
    }

    @Override
    public @NotNull IMenu send(Audience audience) {
        if (audience instanceof Player player) {
            if (this.manager == null) {
                return this;
            }
            this.manager.internallyOpen(player, this);
        }
        return this;
    }
}
