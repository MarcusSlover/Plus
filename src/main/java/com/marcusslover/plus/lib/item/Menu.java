package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.common.RequiresManager;
import com.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiresManager
public class Menu implements ISendable<Menu> {
    protected @NotNull Inventory inventory;
    protected @NotNull LinkedList<@NotNull ClickAdapter> clickAdapters;
    protected @Nullable ClickAdapter mainClickAdapter;
    protected boolean cancelled = true;

    long lastActivity = -1L;

    Menu() {
        this(3 * 9);
    }

    Menu(int size) {
        this(size, (String) null);
    }

    Menu(int size, @Nullable String name) {
        this(size, (name == null) ? null : Text.of(name));
    }

    Menu(int size, @Nullable Text text) {
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
            this.inventory.setItem(slot, item.get());
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

    public @NotNull Menu open(@NotNull Player player) {
        return this.send(player);
    }

    public @NotNull Menu open(@NotNull Player... players) {
        return this.send(players);
    }

    public @NotNull Menu open(@NotNull Collection<@NotNull Player> players) {
        return this.send(players);
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

    @Override
    public @NotNull <T extends CommandSender> Menu send(@NotNull T target) {
        if (!(target instanceof Player player)) {
            return this;
        }

        this.lastActivity = System.currentTimeMillis();
        player.openInventory(this.inventory);

        return this;
    }

    @Override
    public @NotNull Menu send(Audience audience) {
        audience.forEachAudience(member -> {
            if (member instanceof Player player) {
                this.send(player);
            }
        });

        return this;
    }

    public record ClickAdapter(int slot, @NotNull Consumer<@NotNull InventoryClickEvent> event) {
    }
}
