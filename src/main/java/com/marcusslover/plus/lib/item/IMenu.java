package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.common.ISendable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a menu.
 */
public interface IMenu extends ISendable<IMenu> {
    /**
     * Called when the menu is opened.
     *
     * @param ctx    the canvas
     * @param player the player
     */
    void open(@NotNull Canvas ctx, @NotNull Player player);

    /**
     * Called right before the canvas data is erased. You may use the menuUpdateContext here.
     *
     * @param ctx    the canvas
     * @param player the player
     */
    default void preClose(@NotNull Canvas ctx, @NotNull Player player) {
        // nothing here
    }

    /**
     * Called when the menu is closed. At this point using menuUpdateContext is not possible.
     *
     * @param ctx    the canvas
     * @param player the player
     */
    default void close(@NotNull Canvas ctx, @NotNull Player player) {
        // nothing here
    }

    /**
     * Called when the menu is updated.
     *
     * @param ctx    the canvas
     * @param player the player
     */
    default void update(@NotNull Canvas ctx, @NotNull Player player) {
        // nothing here
    }
}
