package me.marcusslover.plus.lib.item;

import me.marcusslover.plus.lib.text.Text;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Item {
    @NotNull
    protected ItemStack itemStack;

    public Item() {
        this(Material.STONE);
    }

    public Item(Material material) {
        this(material, 1);
    }

    public Item(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public Item(Material material, int amount, Text name) {
        this(material, amount);
        setName(name);
    }

    public Item setType(Material material) {
        itemStack.setType(material);
        return this;
    }

    public Material getType() {
        return itemStack.getType();
    }

    public Item setAmount(int amount) {
        itemStack.setAmount(Math.min(64, Math.max(0, amount)));
        return this;
    }

    public int getAmount() {
        return itemStack.getAmount();
    }

    public Item setName(@Nullable Text name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (name != null) itemMeta.displayName(name.comp());
        else itemMeta.displayName(null);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Item setName(@Nullable String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (name != null) {
            Text text = new Text(name);
            itemMeta.displayName(text.comp());
        } else itemMeta.displayName(null);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    @Nullable
    public Text getName() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.hasDisplayName()) return new Text(itemMeta.displayName());
        return null;
    }


    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
