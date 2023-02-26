package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.common.ISendable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a menu.
 */
public interface IMenu extends ISendable<IMenu> {
    /**
     * Opens the menu to the player.
     *
     * @param menuCanvas the canvas
     * @param player the player
     */
    void open(@NotNull Canvas menuCanvas, @NotNull Player player);
}
