package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.text.Text;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A canvas is a representation of a menu.
 * It is used to customize the menu layout.
 */
@Data
@Accessors(fluent = true, chain = true)
public class Canvas {
    private final int rows; // 1-6
    private final @NotNull List<Button> buttons = new ArrayList<>();
    private @Nullable Component title;
    private @Nullable Inventory assosiatedInventory = null;
    private @Nullable GenericClick genericClick = null;
    private @Nullable SelfInventory selfInventory = null;

    /**
     * Set the title of the canvas.
     *
     * @param title the title
     * @return the canvas
     */
    public @NotNull Canvas title(@Nullable Component title) {
        this.title = title;
        return this;
    }

    /**
     * Set the title of the canvas.
     *
     * @param title the title
     * @return the canvas
     */
    public @NotNull Canvas title(@Nullable Text title) {
        if (title != null) {
            this.title = title.comp();
        } else {
            this.title = null;
        }
        return this;
    }

    /**
     * Set the title of the canvas.
     *
     * @param title the title
     * @return the canvas
     */
    public @NotNull Canvas title(@Nullable String title) {
        if (title != null) {
            this.title = Text.of(title).comp();
        } else {
            this.title = null;
        }
        return this;
    }

    /**
     * Craft the inventory.
     *
     * @return the inventory
     */
    public @NotNull Inventory craftInventory() {
        if (this.title == null) {
            return Bukkit.createInventory(null, this.rows * 9);
        }
        return Bukkit.createInventory(null, this.rows * 9, this.title);
    }

    /**
     * Add a button to the canvas.
     *
     * @param button      the button
     * @param buttonClick the button click
     * @return the button
     */
    public @NotNull Button button(@NotNull Button button, @NotNull Canvas.ButtonClick buttonClick) {
        this.buttons.add(button);
        button.buttonClick(buttonClick);
        return button;
    }

    /**
     * A button click. This is called when a player clicks on the button.
     */
    @FunctionalInterface
    public interface ButtonClick {
        /**
         * Called when a player clicks on the button.
         *
         * @param player the player
         * @param event  the event
         * @param canvas the canvas
         * @param button the button
         */
        void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull Canvas canvas, @NotNull Button button);
    }

    /**
     * A generic click. This is called when a player clicks on the inventory.
     * This is called before the button click.
     */
    @FunctionalInterface
    public interface GenericClick {
        /**
         * Called when a player clicks on the inventory.
         *
         * @param player the player
         * @param event  the event
         * @param canvas the canvas
         */
        void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull Canvas canvas);
    }

    /**
     * A self inventory click. This is called when a player clicks on their inventory.
     */
    @FunctionalInterface
    public interface SelfInventory {
        /**
         * Called when a player clicks on their inventory.
         *
         * @param player the player
         * @param event  the event
         * @param canvas the canvas
         */
        void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull Canvas canvas);
    }
}
