package me.marcusslover.plus.lib.item;

import me.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;

public class Menu {
    public static Set<Menu> menus = new HashSet<>(); // This will change later to more efficient cache or weak storage.

    private static Plugin pluginListener = null;
    private static ClickListener clickListener = null;

    protected Inventory inventory;
    protected LinkedList<ClickAdapter> clickAdapters;

    public Menu() {
        this(3 * 9);
    }

    public Menu(int size) {
        this(size, (String) null);
    }

    public Menu(int size, @Nullable String name) {
        if (name == null) this.inventory = Bukkit.createInventory(null, size);
        else this.inventory = Bukkit.createInventory(null, size, name);
        this.clickAdapters = new LinkedList<>();
        menus.add(this);
    }

    public Menu(int size, @Nullable Text text) {
        if (text == null) this.inventory = Bukkit.createInventory(null, size);
        else this.inventory = Bukkit.createInventory(null, size, text.comp());
        this.clickAdapters = new LinkedList<>();
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

    public Inventory inventory() {
        return this.inventory;
    }

    public int size() {
        return this.inventory.getSize();
    }

    public Menu set(int slot, Item item) {
        this.inventory.setItem(slot, item.getItemStack());
        return this;
    }

    public Menu onClick(int slot, Consumer<InventoryClickEvent> event) {
        if (pluginListener == null) throw new RuntimeException("The Menu API hasn't been initialized!");
        clickAdapters.add(new ClickAdapter(slot, event));
        return this;
    }

    public Menu open(Player player) {
        player.openInventory(inventory);
        return this;
    }

    private static record ClickAdapter(int slot, Consumer<InventoryClickEvent> event) {
    }

    private static class ClickListener implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            menus.stream()
                    .filter(menu -> menu.inventory.equals(event.getInventory()))
                    .forEach(menu -> menu.clickAdapters.stream()
                            .filter(click -> click.slot == event.getRawSlot())
                            .forEach(click -> click.event.accept(event)));
        }
    }
}
