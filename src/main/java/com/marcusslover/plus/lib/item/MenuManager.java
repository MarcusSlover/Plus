package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.events.EventReference;
import com.marcusslover.plus.lib.events.Events;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class MenuManager {
    private final @NotNull Plugin plugin;
    private final @NotNull List<@NotNull Menu> menus = new LinkedList<>();

    private final @NotNull EventReference<InventoryCloseEvent> closeEvent;
    private final @NotNull EventReference<InventoryClickEvent> clickEvent;

    public MenuManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.closeEvent = Events.listen(InventoryCloseEvent.class).handler(event -> {
            InventoryView view = event.getView();
            for (Menu gameMenu : MenuManager.this.menus) {
                List<UUID> toRemove = new ArrayList<>();
                for (Map.Entry<UUID, Canvas> entry : gameMenu.canvasMap().entrySet()) {
                    UUID uuid = entry.getKey();
                    Canvas canvas = entry.getValue();
                    Inventory inventory = canvas.assosiatedInventory();
                    if (inventory == null) {
                        continue;
                    }
                    if (!inventory.equals(view.getTopInventory())) {
                        continue;
                    }
                    canvas.assosiatedInventory(null);
                    toRemove.add(uuid);
                }
                toRemove.forEach(gameMenu.canvasMap()::remove);
            }
        }).asRegistered(plugin);

        this.clickEvent = Events.listen(InventoryClickEvent.class).handler(event -> {
            InventoryView view = event.getView();
            for (Menu menu : MenuManager.this.menus) {
                Map<UUID, Canvas> map = menu.canvasMap();
                // copy map to avoid concurrent modification
                Map<UUID, Canvas> copy = new HashMap<>(map);
                copy.forEach((uuid, canvas) -> {
                    Inventory inventory = canvas.assosiatedInventory();
                    if (inventory == null) {
                        return;
                    }
                    if (!inventory.equals(view.getTopInventory())) {
                        return;
                    }
                    event.setCancelled(true);
                    int slot = event.getRawSlot();
                    int size = inventory.getSize();

                    Item item = Item.of(event.getCurrentItem());

                    Canvas.ClickContext genericClick = canvas.genericClick();
                    if (genericClick != null) {
                        Canvas.ButtonClick buttonClick = genericClick.click();
                        if (buttonClick != null) {
                            try {
                                buttonClick.onClick((Player) event.getWhoClicked(), item, event, canvas);
                            } catch (Throwable e) {
                                if (genericClick.throwableConsumer() != null) {
                                    genericClick.throwableConsumer().accept(e);
                                } else {
                                    Bukkit.getLogger().warning(e.getMessage());
                                }
                            }
                        }
                    }

                    if (slot > size) {
                        Canvas.ClickContext selfInventory = canvas.selfInventory();
                        if (selfInventory != null) {
                            Canvas.ButtonClick buttonClick = selfInventory.click();
                            if (buttonClick != null) {
                                try {
                                    buttonClick.onClick((Player) event.getWhoClicked(), item, event, canvas);
                                } catch (Throwable e) {
                                    if (selfInventory.throwableConsumer() != null) {
                                        selfInventory.throwableConsumer().accept(e);
                                    } else {
                                        Bukkit.getLogger().warning(e.getMessage());
                                    }
                                }
                            }
                        }
                        return;
                    }

                    canvas.buttons().stream()
                            .filter(button -> button.within(slot))
                            .findFirst()
                            .ifPresent(button -> {
                                Player player = (Player) event.getWhoClicked();
                                Canvas.ClickContext context = button.clickContext();
                                Canvas.ButtonClick click = context.click();
                                if (click == null) {
                                    return;
                                }
                                try {
                                    click.onClick(player, item, event, canvas);
                                } catch (Throwable e) {
                                    if (context.throwableConsumer() != null) {
                                        context.throwableConsumer().accept(e);
                                    } else {
                                        Bukkit.getLogger().warning(e.getMessage());
                                    }
                                }
                            });
                });
            }
        }).asRegistered(plugin);
    }

    @Deprecated
    public static @NotNull MenuManager get(@NotNull Plugin plugin) {
        return new MenuManager(plugin);
    }

    @Deprecated
    public static @NotNull MenuManager createManager(@NotNull Plugin plugin) {
        return new MenuManager(plugin);
    }

    /**
     * Gets the plugin associated with the manager.
     *
     * @return the plugin
     */
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * Adds a menu to the manager.
     *
     * @param menu the menu
     * @return the manager
     */
    public @NotNull MenuManager addMenu(@NotNull Menu menu) {
        menu.hookManager(this);
        this.menus.add(menu);
        return this;
    }

    /**
     * Removes a menu from the manager.
     *
     * @param menu the menu
     * @return the manager
     */
    public @NotNull MenuManager removeMenu(@NotNull Menu menu) {
        menu.hookManager(null);
        this.menus.remove(menu);
        return this;
    }

    /**
     * Opens a menu to the player.
     *
     * @param player the player
     * @param clazz  the class
     */
    public <T extends Menu> void open(@NotNull Player player, @NotNull Class<T> clazz) {
        this.menus.stream()
                .filter(menu -> menu.getClass().equals(clazz))
                .findFirst()
                .ifPresent(menu -> this.internallyOpen(player, menu));
    }

    /**
     * Opens a menu to the player.
     *
     * @param player the player
     * @param menu   the menu
     */
    public void internallyOpen(@NotNull Player player, @NotNull Menu menu) {
        InventoryView openInventory = player.getOpenInventory();
        @SuppressWarnings("deprecation") String currentTitle = openInventory.getTitle();
        Canvas canvas = menu.canvasMap().get(player.getUniqueId());

        if (canvas == null) {
            canvas = new Canvas(6, menu);
            menu.canvasMap().put(player.getUniqueId(), canvas);
            try {
                menu.open(canvas, player);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // craft inventory
            Inventory inventory = canvas.craftInventory();
            canvas.assosiatedInventory(inventory);
            this.updateInventory(inventory, canvas);
            player.openInventory(inventory);
        } else {
            Inventory topInventory = openInventory.getTopInventory();
            this.updateInventory(topInventory, canvas);
        }
    }

    /**
     * Updates the inventory.
     *
     * @param inventory the inventory
     * @param canvas    the canvas
     */
    private void updateInventory(@NotNull Inventory inventory, @NotNull Canvas canvas) {
        inventory.clear(); // clear inventory

        for (Button button : canvas.buttons()) {
            Item item = button.item();
            if (item == null) {
                continue;
            }
            Button.DetectableArea matrix = button.detectableArea();
            Set<Integer> slots = matrix.slots();

            for (Integer slot : slots) {
                inventory.setItem(slot, item.get());
            }
        }
    }

    /**
     * Gets the menus.
     *
     * @return the menus
     */
    public @NotNull List<@NotNull Menu> getMenus() {
        return this.menus;
    }

    /**
     * Clears the menus.
     * Shutdown the manager.
     */
    public void clearMenus() {
        this.menus.clear();
        this.clickEvent.unregister();
        this.closeEvent.unregister();
    }
}
