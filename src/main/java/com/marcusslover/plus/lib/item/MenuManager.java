package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.events.EventReference;
import com.marcusslover.plus.lib.events.Events;
import com.marcusslover.plus.lib.item.Button.DetectableArea;
import com.marcusslover.plus.lib.item.Canvas.ItemDecorator;
import com.marcusslover.plus.lib.item.event.PlayerMenuOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class MenuManager {
    private final @NotNull Plugin plugin;
    private final @NotNull List<@NotNull Menu> menus = new LinkedList<>();

    private final @NotNull EventReference<InventoryCloseEvent> closeEvent;
    private final @NotNull EventReference<InventoryClickEvent> clickEvent;

    // Global click context
    private @Nullable Canvas.GenericClick genericClick = null;

    public MenuManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.closeEvent = Events.listen(InventoryCloseEvent.class).handler(event -> {
            InventoryView view = event.getView();
            Inventory inventory = view.getTopInventory();
            Player player = (Player) event.getPlayer();

            if (!(inventory.getHolder() instanceof Canvas canvas)) {
                return;
            }

            // empty or invalid inventory shouldn't be handled
            if (canvas.assosiatedInventory() == null) return;

            // handle the close event action
            Canvas.ClickContext closeInventory = canvas.closeInventory();
            if (closeInventory != null) {
                Canvas.CloseInventory function = closeInventory.closeInventory();
                if (function != null) {
                    try {
                        function.onClose(player, event, canvas);
                    } catch (Throwable e) {
                        if (closeInventory.throwableConsumer() != null) {
                            closeInventory.throwableConsumer().accept(e);
                        } else {
                            Bukkit.getLogger().warning(e.getMessage());
                        }
                    }
                }
            }

            Menu menu = canvas.assosiatedMenu();
            Canvas remove = menu.canvasMap().remove(player.getUniqueId());
            if (remove != null) {
                remove.clear(); // help the gc
            }
            menu.close(canvas, player); // call the close method so developers can handle it

        }).asRegistered(plugin);

        this.clickEvent = Events.listen(InventoryClickEvent.class).handler(event -> {
            InventoryView view = event.getView();
            Inventory inventory = view.getTopInventory();

            if (!(inventory.getHolder() instanceof Canvas canvas)) {
                return;
            }

            event.setCancelled(true);
            int slot = event.getRawSlot();
            int size = inventory.getSize();

            Item item = Item.of(event.getCurrentItem());

            // handle the generic click context (global)
            if (this.genericClick != null) {
                try {
                    this.genericClick.onClick((Player) event.getWhoClicked(), item, event, canvas);
                } catch (Throwable e) {
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }

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
                        if (context == null) {
                            return;
                        }
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
     * Registers a menu to the manager.
     * @param menus the menus
     * @return the manager
     */
    public @NotNull MenuManager register(@NotNull Menu... menus) {
        for (Menu menu : menus) this.addMenu(menu);
        return this;
    }

    /**
     * Unregisters a menu from the manager.
     * @param menus the menus
     * @return the manager
     */
    public @NotNull MenuManager unregister(@NotNull Menu... menus) {
        for (Menu menu : menus) this.removeMenu(menu);
        return this;
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
        this.internallyOpen(player, menu, false, null);
    }

    /**
     * Opens a menu to the player.
     *
     * @param player the player
     * @param menu   the menu
     * @param force  if the menu should be forced
     * @param ctx    the context of the update
     */
    public void internallyOpen(@NotNull Player player, @NotNull Menu menu, boolean force, @Nullable Menu.UpdateContext ctx) {
        if (!new PlayerMenuOpenEvent(player, menu, force, ctx).callEvent()) return; // Call the event

        InventoryView openInventory = player.getOpenInventory();
        Canvas canvas = menu.canvasMap().get(player.getUniqueId());

        if (canvas == null || force) {
            // just for performance
            if (canvas != null) {
                canvas.clear(); // help the gc

                // remove canvas from the tracked map
                menu.canvasMap().remove(player.getUniqueId());
            }
            Canvas newCanvas = new Canvas(6, menu);
            newCanvas.menuUpdateContext(ctx); // set the context of the update
            menu.canvasMap().put(player.getUniqueId(), newCanvas);
            newCanvas.assosiatedMenu(menu);
            try {
                menu.open(newCanvas, player); // fill the canvas
            } catch (Exception e) {
                e.printStackTrace();
            }
            // craft inventory
            Inventory inventory = newCanvas.craftInventory();
            newCanvas.assosiatedInventory(inventory);
            this.updateInventory(player, inventory, newCanvas);
            player.openInventory(inventory);
        } else {
            // update the canvas
            canvas.menuUpdateContext(ctx); // set the context of the update

            // update inventory
            Inventory topInventory = openInventory.getTopInventory();

            // remove all populated buttons
            canvas.buttons().removeIf(Button::populated);

            canvas.populatorContext().forEach(populatorContext -> {
                if (populatorContext != null) {
                    Canvas.PopulatorContext.Populator<?> populator = populatorContext.populator();
                    if (populator != null) {
                        try {
                            if (populatorContext.pageBackwards() != null) {
                                canvas.buttons().remove(populatorContext.pageBackwards());
                            }
                            if (populatorContext.pageForwards() != null) {
                                canvas.buttons().remove(populatorContext.pageForwards());
                            }
                            // repopulate
                            populatorContext.updateContent(player, populator);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            try {
                menu.update(canvas, player); // update event
            } catch (Exception e) {
                e.printStackTrace();
            }
            // update inventory
            this.updateInventory(player, topInventory, canvas);
        }
    }

    /**
     * Updates the inventory.
     *
     * @param player    the player
     * @param inventory the inventory
     * @param canvas    the canvas
     */
    private void updateInventory(@NotNull Player player, @NotNull Inventory inventory, @NotNull Canvas canvas) {
        inventory.clear(); // clear inventory

        // free items first aka (decorations)
        for (ItemDecorator decorator : canvas.decorators()) {
            decorator.handle(canvas, inventory); // handle the decoration
        }

        // then buttons
        for (Button button : canvas.buttons()) {
            Button.ItemFactory itemFactory = button.itemFactory();
            if (itemFactory == null) {
                continue;
            }
            Item item = itemFactory.create(player);
            if (item == null) {
                continue;
            }
            DetectableArea matrix = button.detectableArea();
            Set<Integer> slots = matrix.slots();
            slots.forEach(slot -> inventory.setItem(slot, item.get()));
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
     * Sets the generic click context, this will affect all menus.
     * @param context the context
     * @return the manager
     */
    public @NotNull MenuManager genericClick(@Nullable Canvas.GenericClick context) {
        this.genericClick = context;
        return this;
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
