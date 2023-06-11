package com.marcusslover.plus.lib.item;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Menu implements IMenu {
    @EqualsAndHashCode.Include
    private final @NotNull Map<UUID, Canvas> canvasMap = new HashMap<>(); // player -> canvas
    @EqualsAndHashCode.Include
    private @Nullable String id; // menu id, not required

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.NONE)
    private @Nullable MenuManager manager;

    void hookManager(@Nullable MenuManager manager) {
        this.manager = manager;
    }

    /**
     * Debugs the message if the menu is in development mode.
     *
     * @param message The message to debug.
     */
    void debug(String message) {
        DevelopmentMenu[] annotationsByType = this.getClass().getAnnotationsByType(DevelopmentMenu.class);
        for (DevelopmentMenu developmentMenu : annotationsByType) {
            if (!developmentMenu.value()) {
                continue;
            }
            if (this.manager != null) {
                this.manager.getPlugin().getLogger().info(message);
            }
        }
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
