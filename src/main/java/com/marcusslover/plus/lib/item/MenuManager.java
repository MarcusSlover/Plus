package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class MenuManager implements Listener {
    private final @NotNull Plugin plugin;
    private final @NotNull Set<@NotNull Menu> menus = new HashSet<>();

    private MenuManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Deprecated
    public static @NotNull MenuManager get(@NotNull Plugin plugin) {
        return new MenuManager(plugin);
    }

    public static @NotNull MenuManager createManager(@NotNull Plugin plugin) {
        return new MenuManager(plugin);
    }

    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    public @NotNull Menu createMenu(int size) {
        return createMenu(size, (String) null);
    }

    public @NotNull Menu createMenu(int size, @Nullable String name) {
        return createMenu(size, (name == null) ? null : new Text(name));
    }

    public @NotNull Menu createMenu(int size, @Nullable Text text) {
        Menu menu = new Menu(size, text);
        menus.add(menu);
        return menu;
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Iterator<Menu> iterator = menus.iterator();
        while (iterator.hasNext()) {
            Menu menu = iterator.next();
            if (menu.inventory.equals(event.getInventory())) {
                event.setCancelled(menu.isCancelled);

                Menu.ClickAdapter mainClickAdapter = menu.mainClickAdapter;
                if (mainClickAdapter != null) mainClickAdapter.event().accept(event);

                menu.clickAdapters.stream()
                        .filter(click -> click.slot() == event.getRawSlot() || click.slot() == -1)
                        .forEach(click -> click.event().accept(event));
            }
        }
    }

    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        menus.removeIf(menu -> menu.inventory.equals(event.getInventory()));
    }

    public @NotNull Set<@NotNull Menu> getMenus() {
        return menus;
    }
}
