package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.text.Text;
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

public class Menu {
    @NotNull
    protected Inventory inventory;
    @NotNull
    protected LinkedList<ClickAdapter> clickAdapters;
    @Nullable
    protected ClickAdapter mainClickAdapter;
    protected boolean isCancelled = true;

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
        if (text == null) inventory = Bukkit.createInventory(null, size);
        else inventory = Bukkit.createInventory(null, size, text.comp());
        clickAdapters = new LinkedList<>();
        mainClickAdapter = null;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    @NotNull
    public Menu setCancelled(boolean cancelled) {
        isCancelled = cancelled;
        return this;
    }

    @NotNull
    public Inventory inventory() {
        return inventory;
    }

    public int size() {
        return inventory.getSize();
    }

    @NotNull
    public Menu set(int slot, @Nullable Item item) {
        return setItem(slot, item, null);
    }

    @NotNull
    public Menu setItem(int slot, @Nullable Item item) {
        return setItem(slot, item, null);
    }

    @NotNull
    public Menu set(int slot, @Nullable Item item, @Nullable Consumer<InventoryClickEvent> event) {
        return setItem(slot, item, event);
    }

    @NotNull
    public Menu setItem(int slot, @Nullable Item item, @Nullable Consumer<InventoryClickEvent> event) {
        if (item != null) inventory.setItem(slot, item.getItemStack());
        if (event != null) return onClick(slot, event);
        return this;
    }

    @NotNull
    public Menu onClick(@NotNull Consumer<InventoryClickEvent> event) {
        return onClick(-1, event);
    }

    @NotNull
    public Menu onClick(int slot, @NotNull Consumer<InventoryClickEvent> event) {
        clickAdapters.add(new ClickAdapter(slot, event));
        return this;
    }

    @NotNull
    public Menu onMainClick(@NotNull Consumer<InventoryClickEvent> event) {
        mainClickAdapter = new Menu.ClickAdapter(-1, event);
        return this;
    }

    @NotNull
    public Menu open(@NotNull Player player) {
        player.openInventory(inventory);
        return this;
    }

    @NotNull
    public List<Player> getViewers() {
        return inventory.getViewers().stream().map(entity -> (Player) entity).collect(Collectors.toList());
    }

    public boolean isViewing(@NotNull Player player) {
        return getViewers().contains(player);
    }

    public @Nullable ClickAdapter getMainClickAdapter() {
        return mainClickAdapter;
    }

    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public @NotNull LinkedList<ClickAdapter> getClickAdapters() {
        return clickAdapters;
    }

    public record ClickAdapter(int slot, @NotNull Consumer<InventoryClickEvent> event) {
    }
}
