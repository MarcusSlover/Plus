package me.marcusslover.plus.lib.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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
        name(name);
    }

    public Item(Material material, int amount, Text name, List<Text> lore) {
        this(material, amount);
        name(name);
        lore(lore);
    }

    public Item(Material material, int amount, String name) {
        this(material, amount);
        setName(name);
    }

    public Item(Material material, int amount, String name, List<String> lore) {
        this(material, amount);
        setName(name);
        setLore(lore);
    }
    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
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

    public Item setDamage(int damage) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(damage);
            itemStack.setItemMeta(damageable);
        }
        return this;
    }

    public Item setDamagePercent(double percent) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            double max = itemStack.getType().getMaxDurability();
            double damage = max * percent / 100;
            damageable.setDamage((int) Math.max(0, damage));
            itemStack.setItemMeta(damageable);
        }
        return this;
    }

    public int getDamage() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            return damageable.getDamage();
        }
        return 0;
    }

    public int getEnchant(Enchantment enchantment) {
        return itemStack.getEnchantmentLevel(enchantment);
    }

    public boolean hasEnchant(Enchantment enchantment) {
        return getEnchant(enchantment) > 0;
    }

    public Item setEnchant(Enchantment enchantment, int level) {
        if (level <= 0) itemStack.removeEnchantment(enchantment);
        else itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public Item setColor(@Nullable Color color) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof PotionMeta meta) {
            meta.setColor(color);
            itemStack.setItemMeta(meta);
            return this;
        }
        if (itemMeta instanceof MapMeta meta) {
            meta.setColor(color);
            itemStack.setItemMeta(meta);
            return this;
        }
        if (itemMeta instanceof LeatherArmorMeta meta) {
            meta.setColor(color);
            itemStack.setItemMeta(meta);
            return this;
        }
        return this;
    }

    public Item addItemFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Item removeItemFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.removeItemFlags(itemFlag);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public boolean hasItemFlag(ItemFlag itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta.hasItemFlag(itemFlag);
    }

    public Item setHideflags(boolean hideflags) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!hideflags) itemMeta.removeItemFlags(ItemFlag.values());
        else itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public boolean isUnbreakable() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta.isUnbreakable();
    }

    public Item setUnbreakable(boolean unbreakable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public boolean hasCustomModelData() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta.hasCustomModelData();
    }

    public Item setCustomModelData(int customModelData) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public int getCustomModelData() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta.getCustomModelData();
    }

    public boolean hasName() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta.hasDisplayName();
    }

    public Item name(@Nullable Text name) {
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

    public boolean hasLore() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta.hasLore();
    }

    public Item setLore(@Nullable List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (lore != null) {
            List<Component> components = new ArrayList<>();
            for (String line : lore) components.add(new Text(line).comp());
            itemMeta.lore(components);
        } else itemMeta.lore(null);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Item lore(@Nullable List<Text> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (lore != null) {
            List<Component> components = new ArrayList<>();
            for (Text line : lore) components.add(line.comp());
            itemMeta.lore(components);
        } else itemMeta.lore(null);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Item skull(PlayerProfile playerProfile) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof SkullMeta skullMeta) {
            skullMeta.setPlayerProfile(playerProfile);
            itemStack.setItemMeta(skullMeta);
        }
        return this;
    }

    public Item skull(String url) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof SkullMeta skullMeta) {
            String texture = new String(getEncodedTexture(url));
            int len = texture.length();
            UUID uuid = new UUID(texture.substring(len - 20).hashCode(), texture.substring(len - 10).hashCode());

            PlayerProfile profile = Bukkit.createProfile(uuid, null);
            ProfileProperty property = new ProfileProperty("textures", texture);
            profile.setProperty(property);
            skullMeta.setPlayerProfile(profile);
            itemStack.setItemMeta(skullMeta);
        }
        return this;
    }

    private byte[] getEncodedTexture(String url) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    }

    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
