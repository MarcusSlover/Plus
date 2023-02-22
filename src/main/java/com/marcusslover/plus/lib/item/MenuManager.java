package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.item.Menu.ClickAdapter;
import com.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public final class MenuManager implements Listener {
    private final @NotNull Plugin plugin;
    private final @NotNull Set<@NotNull Menu> menus = new HashSet<>();

    public MenuManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            Iterator<@NotNull Menu> iterator = this.menus.iterator();
            while (iterator.hasNext()) {
                Menu menu = iterator.next();
                if (!menu.inventory.getViewers().isEmpty()) {
                    continue;
                }

                if (menu.lastActivity < 0) {
                    iterator.remove();
                } else {
                    long time = now - menu.lastActivity;
                    if (time > 1000 * 60 * 5) { // 5 minutes
                        iterator.remove();
                    }
                }
            }
        }, 0L, 20 * 60 * 5L);
    }

    @Deprecated
    public static @NotNull MenuManager get(@NotNull Plugin plugin) {
        return new MenuManager(plugin);
    }

    @Deprecated
    public static @NotNull MenuManager createManager(@NotNull Plugin plugin) {
        return new MenuManager(plugin);
    }

    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

    public @NotNull Menu createMenu(int size) {
        return this.createMenu(size, (String) null);
    }

    public @NotNull Menu createMenu(int size, @Nullable String name) {
        return this.createMenu(size, (name == null) ? null : new Text(name));
    }

    public @NotNull Menu createMenu(int size, @Nullable Text text) {
        Menu menu = new Menu(size, text);
        this.menus.add(menu);
        return menu;
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Optional<@NotNull Menu> optionalMenu = this.menus.stream().filter(menu -> menu.inventory.equals(event.getInventory())).findFirst();
        if (optionalMenu.isEmpty()) {
            return;
        }
        Menu menu = optionalMenu.get();
        menu.lastActivity = System.currentTimeMillis();

        event.setCancelled(menu.cancelled);
        ClickAdapter mainClickAdapter = menu.mainClickAdapter;
        if (mainClickAdapter != null) {
            mainClickAdapter.event().accept(event);
        }

        menu.clickAdapters.stream()
                .filter(click -> click.slot() == event.getRawSlot() || click.slot() == -1)
                .forEach(click -> click.event().accept(event));
    }

    public @NotNull Set<@NotNull Menu> getMenus() {
        return this.menus;
    }
}
