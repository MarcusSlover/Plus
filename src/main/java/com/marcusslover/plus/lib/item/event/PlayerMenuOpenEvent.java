package com.marcusslover.plus.lib.item.event;

import com.marcusslover.plus.lib.item.Menu;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player opens a menu.
 */
public class PlayerMenuOpenEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Menu menu;
    @Getter
    private final boolean force;
    @Getter
    private final @Nullable Menu.UpdateContext ctx;
    private boolean cancel = false;

    public PlayerMenuOpenEvent(@NotNull Player who, @NotNull Menu menu, boolean force, @Nullable Menu.UpdateContext ctx) {
        super(who);
        this.menu = menu;
        this.force = force;
        this.ctx = ctx;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
