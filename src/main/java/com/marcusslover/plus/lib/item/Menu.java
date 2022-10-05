package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.text.Text;
import com.marcusslover.plus.lib.util.Alternative;
import com.marcusslover.plus.lib.util.ISendable;
import com.marcusslover.plus.lib.util.RequiresManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiresManager
public class Menu implements ISendable<Player, Menu> {
    protected @NotNull Inventory inventory;
    protected @NotNull LinkedList<@NotNull ClickAdapter> clickAdapters;
    protected @Nullable ClickAdapter mainClickAdapter;
    protected boolean cancelled = true;

    long lastActivity = -1L;

    public Menu() {
        this(3 * 9);
    }

    public Menu(int size) {
        this(size, (String) null);
    }

    public Menu(int size, @Nullable String name) {
        this(size, (name == null) ? null : new Text(name));
    }

    public Menu(int size, @Nullable Text text) {
        if (text == null) {
            this.inventory = Bukkit.createInventory(null, size);
        } else {
            this.inventory = Bukkit.createInventory(null, size, text.comp());
        }
        this.clickAdapters = new LinkedList<>();
        this.mainClickAdapter = null;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public @NotNull Menu setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        return this;
    }

    public @NotNull Inventory inventory() {
        return this.inventory;
    }

    public int size() {
        return this.inventory.getSize();
    }

    public @NotNull Menu set(int slot, @Nullable Item item) {
        return this.setItem(slot, item, null);
    }

    public @NotNull Menu setItem(int slot, @Nullable Item item) {
        return this.setItem(slot, item, null);
    }

    public @NotNull Menu set(int slot, @Nullable Item item, @Nullable Consumer<@NotNull InventoryClickEvent> event) {
        return this.setItem(slot, item, event);
    }

    public @NotNull Menu setItem(int slot, @Nullable Item item, @Nullable Consumer<@NotNull InventoryClickEvent> event) {
        if (item != null) {
            this.inventory.setItem(slot, item.getItemStack());
        }
        if (event != null) {
            return this.onClick(slot, event);
        }
        return this;
    }

    public @NotNull Menu onClick(@NotNull Consumer<@NotNull InventoryClickEvent> event) {
        return this.onClick(-1, event);
    }

    public @NotNull Menu onClick(int slot, @NotNull Consumer<@NotNull InventoryClickEvent> event) {
        this.clickAdapters.add(new ClickAdapter(slot, event));
        return this;
    }

    public @NotNull Menu onMainClick(@NotNull Consumer<@NotNull InventoryClickEvent> event) {
        this.mainClickAdapter = new Menu.ClickAdapter(-1, event);
        return this;
    }

    @Alternative
    public @NotNull Menu open(@NotNull Player player) {
        return this.send(player);
    }

    @Override
    public @NotNull Menu send(@NotNull Player target) {
        this.lastActivity = System.currentTimeMillis();
        target.openInventory(this.inventory);
        return this;
    }

    public @NotNull List<@NotNull Player> getViewers() {
        return this.inventory.getViewers().stream().map(entity -> (Player) entity).collect(Collectors.toList());
    }

    public boolean isViewing(@NotNull Player player) {
        return this.getViewers().contains(player);
    }

    public @Nullable ClickAdapter getMainClickAdapter() {
        return this.mainClickAdapter;
    }

    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public @NotNull LinkedList<@NotNull ClickAdapter> getClickAdapters() {
        return this.clickAdapters;
    }

    public record ClickAdapter(int slot, @NotNull Consumer<@NotNull InventoryClickEvent> event) {
    }
}
