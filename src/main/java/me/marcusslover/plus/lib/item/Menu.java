package me.marcusslover.plus.lib.item;

import me.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class Menu {
    protected Inventory inventory;

    public Menu() {
        this(3 * 9);
    }

    public Menu(int size) {
        this(size, (String) null);
    }

    public Menu(int size, @Nullable String name) {
        if (name == null) this.inventory = Bukkit.createInventory(null, size);
        else this.inventory = Bukkit.createInventory(null, size, name);
    }

    public Menu(int size, @Nullable Text text) {
        if (text == null) this.inventory = Bukkit.createInventory(null, size);
        else this.inventory = Bukkit.createInventory(null, size, text.comp());
    }

    public Menu set(int slot, Item item) {
        this.inventory.setItem(slot, item.getItemStack());
        return this;
    }

    public Menu open(Player player) {
        player.openInventory(inventory);
        return this;
    }
}
