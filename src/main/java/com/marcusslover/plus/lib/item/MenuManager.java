package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class MenuManager implements Listener {
    @NotNull
    private final Plugin plugin;
    @NotNull
    private final Set<Menu> menus = new HashSet<>();

    private MenuManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @NotNull
    public static MenuManager get(@NotNull Plugin plugin) {
        return new MenuManager(plugin);
    }

    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    @NotNull
    public Menu createMenu(int size) {
        return createMenu(size, (String) null);
    }

    @NotNull
    public Menu createMenu(int size, @Nullable String name) {
        return createMenu(size, (name == null) ? null : new Text(name));
    }

    @NotNull
    public Menu createMenu(int size, @Nullable Text text) {
        Menu menu = new Menu(size, text);
        menus.add(menu);
        return menu;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        menus.stream()
                .filter(menu -> menu.inventory.equals(event.getInventory()))
                .forEach(menu -> {
                    event.setCancelled(menu.isCancelled);

                    Menu.ClickAdapter mainClickAdapter = menu.mainClickAdapter;
                    if (mainClickAdapter != null) mainClickAdapter.event().accept(event);

                    menu.clickAdapters.stream()
                            .filter(click -> click.slot() == event.getRawSlot() || click.slot() == -1)
                            .forEach(click -> click.event().accept(event));
                });
    }

    @NotNull
    public Set<Menu> getMenus() {
        return menus;
    }
}