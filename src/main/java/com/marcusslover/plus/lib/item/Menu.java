package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Menu {
    public static Set<Menu> menus = new HashSet<>(); // This will change later to more efficient cache or weak storage.

    @Nullable
    private static Plugin pluginListener = null;
    @Nullable
    private static ClickListener clickListener = null;

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
        menus.add(this);
    }

    public static void initialize(@Nullable Plugin plugin) {
        if (plugin == null) {
            if (pluginListener != null && clickListener != null) {
                HandlerList.unregisterAll(clickListener);
            }
            return;
        }
        clickListener = new ClickListener();
        pluginListener = plugin;
        Bukkit.getPluginManager().registerEvents(clickListener, pluginListener);

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
        if (pluginListener == null) throw new RuntimeException("The Menu API hasn't been initialized!");
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

    private record ClickAdapter(int slot, @NotNull Consumer<InventoryClickEvent> event) {
    }

    private static class ClickListener implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            menus.stream()
                    .filter(menu -> menu.inventory.equals(event.getInventory()))
                    .forEach(menu -> {
                        event.setCancelled(menu.isCancelled);

                        ClickAdapter mainClickAdapter = menu.mainClickAdapter;
                        if (mainClickAdapter != null) mainClickAdapter.event.accept(event);

                        menu.clickAdapters.stream()
                                .filter(click -> click.slot == event.getRawSlot() || click.slot == -1)
                                .forEach(click -> click.event.accept(event));
                    });
        }
    }
}
