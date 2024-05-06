package com.marcusslover.plus.lib.item;

import lombok.*;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
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

    /**
     * Allows you to perform an update on the menu.
     * All current menu viewers will have their menu updated.
     */
    public void performUpdate() {
        this.canvasMap.keySet().stream().toList().forEach(uuid -> performUpdate(uuid, false, null)); // update all viewers
    }

    /**
     * Allows you to perform an update on the menu.
     * All current menu viewers will have their menu updated.
     *
     * @param force If the update should be forced.
     *              If true, the menu will be literally re-opened,
     *              if false, the menu will be updated.
     *              Setting it to false eliminates a lot of performance issues, however,
     *              if you are using a lot of dynamic elements outside item factories, you should set it to true.
     * @param ctx   The context of the update.
     */
    public void performUpdate(boolean force, @Nullable Menu.UpdateContext ctx) {
        this.canvasMap.keySet().stream().toList().forEach(uuid -> performUpdate(uuid, force, ctx)); // update all viewers
    }

    /**
     * Allows you to perform an update on the menu.
     *
     * @param uuid  The player to update the menu for.
     * @param force If the update should be forced, see {@link #performUpdate(boolean, UpdateContext)}.
     * @param ctx   The context of the update.
     */
    public void performUpdate(UUID uuid, boolean force, @Nullable Menu.UpdateContext ctx) {
        if (!this.canvasMap.containsKey(uuid)) {
            return; // player is not viewing the menu
        }
        Player viewer = Bukkit.getPlayer(uuid);
        if (viewer == null) {
            return; // player is not online
        }
        this.send(viewer, force, ctx); // update the menu
    }

    @Override
    public @NotNull <T extends CommandSender> IMenu send(@NotNull T target) {
        return this.send(target, false, null);
    }

    @Override
    public @NotNull IMenu send(Audience audience) {
        return this.send(audience, false, null);
    }

    /**
     * Sends the menu to the target.
     *
     * @param target The target to send the menu to.
     * @param force  If the menu should be forced.
     * @param ctx    The context of the update.
     * @param <T>    The type of the target.
     * @return The menu.
     */
    public @NotNull <T extends CommandSender> IMenu send(@NotNull T target, boolean force, @Nullable Menu.UpdateContext ctx) {
        if (target instanceof Player player) {
            if (this.manager == null) return this;
            this.manager.internallyOpen(player, this, force, ctx);
        }
        return this;
    }

    /**
     * Sends the menu to the audience.
     *
     * @param audience The audience to send the menu to.
     * @param force    If the menu should be forced.
     * @param ctx      The context of the update.
     * @return The menu.
     */
    public @NotNull IMenu send(Audience audience, boolean force, @Nullable Menu.UpdateContext ctx) {
        if (audience instanceof Player player) {
            if (this.manager == null) return this;
            this.manager.internallyOpen(player, this, force, ctx);
        }
        return this;
    }

    /**
     * Context about the menu update.
     */
    public abstract static class UpdateContext {

    }
}
