package com.marcusslover.plus.lib.item;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a menu.
 */
public interface IMenu {
    /**
     * Opens the menu to the player.
     *
     * @param canvas the canvas
     * @param player the player
     */
    void open(@NotNull Canvas canvas, @NotNull Player player);
}
