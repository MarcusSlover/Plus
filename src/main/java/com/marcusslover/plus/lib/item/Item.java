package com.marcusslover.plus.lib.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.marcusslover.plus.lib.Plus;
import com.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Item {
    @NotNull
    protected ItemStack itemStack;

    public Item() {
        this(Material.STONE);
    }

    public Item(@NotNull Material material) {
        this(material, 1);
    }

    public Item(@NotNull Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public Item(@NotNull Material material, int amount, @Nullable Text name) {
        this(material, amount);
        name(name);
    }

    public Item(@NotNull Material material, int amount, @Nullable Text name, @Nullable List<Text> lore) {
        this(material, amount);
        name(name);
        lore(lore);
    }

    public Item(@NotNull Material material, int amount, @Nullable String name) {
        this(material, amount);
        setName(name);
    }

    public Item(@NotNull Material material, int amount, @Nullable String name, @Nullable List<String> lore) {
        this(material, amount);
        setName(name);
        setLore(lore);
    }

    public Item(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack == null ? new ItemStack(Material.AIR) : itemStack;
    }

    public boolean isValid() {
        //noinspection ConstantConditions
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    @NotNull
    public Material getType() {
        return itemStack.getType();
    }

    @NotNull
    public Item setType(Material material) {
        itemStack.setType(material);
        return this;
    }

    public int getAmount() {
        return itemStack.getAmount();
    }

    @NotNull
    public Item setAmount(int amount) {
        itemStack.setAmount(Math.min(64, Math.max(0, amount)));
        return this;
    }

    @NotNull
    public Item setDamagePercent(double percent) {
        return editMeta(itemMeta -> {
            if (itemMeta instanceof Damageable damageable) {
                double max = itemStack.getType().getMaxDurability();
                double damage = max * percent / 100;
                damageable.setDamage((int) Math.max(0, damage));
            }
        });
    }

    @Nullable
    public Integer getDamage() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof Damageable damageable) {
            return damageable.getDamage();
        }
        return 0;
    }

    @NotNull
    public Item setDamage(int damage) {
        return editMeta(itemMeta -> {
            if (itemMeta instanceof Damageable damageable) {
                damageable.setDamage(damage);
            }
        });
    }

    public int getEnchant(@NotNull Enchantment enchantment) {
        return itemStack.getEnchantmentLevel(enchantment);
    }

    public boolean hasEnchant(@NotNull Enchantment enchantment) {
        return getEnchant(enchantment) > 0;
    }

    @NotNull
    public Item setEnchant(@NotNull Enchantment enchantment, int level) {
        if (level <= 0) itemStack.removeEnchantment(enchantment);
        else itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    @NotNull
    public Item setColor(@Nullable Color color) {
        return editMeta(itemMeta -> {
            if (itemMeta instanceof PotionMeta meta) {
                meta.setColor(color);
                return;
            }
            if (itemMeta instanceof MapMeta meta) {
                meta.setColor(color);
                return;
            }
            if (itemMeta instanceof LeatherArmorMeta meta) {
                meta.setColor(color);
            }
        });
    }

    @NotNull
    public Item addAttribute(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        return editMeta(meta -> meta.addAttributeModifier(attribute, modifier));
    }

    @NotNull
    public Item removeAttribute(@NotNull Attribute attribute) {
        return editMeta(meta -> meta.removeAttributeModifier(attribute));
    }

    @NotNull
    public Item removeAttribute(@NotNull EquipmentSlot equipmentSlot) {
        return editMeta(meta -> meta.removeAttributeModifier(equipmentSlot));
    }

    @NotNull
    public Item addItemFlag(@NotNull ItemFlag itemFlag) {
        return editMeta(itemMeta -> itemMeta.addItemFlags(itemFlag));
    }

    @NotNull
    public Item removeItemFlag(@NotNull ItemFlag itemFlag) {
        return editMeta(itemMeta -> itemMeta.removeItemFlags(itemFlag));
    }

    public boolean hasItemFlag(@NotNull ItemFlag itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return itemMeta.hasItemFlag(itemFlag);
    }

    @NotNull
    public Item setHideflags(boolean hideflags) {
        return editMeta(itemMeta -> {
            if (!hideflags) itemMeta.removeItemFlags(ItemFlag.values());
            else itemMeta.addItemFlags(ItemFlag.values());
        });
    }

    public boolean isUnbreakable() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return itemMeta.isUnbreakable();
    }

    @NotNull
    public Item setUnbreakable(boolean unbreakable) {
        return editMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    @NotNull
    public Item setTag(@NotNull String key, @NotNull String value) {
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.STRING, value);
        });
        return this;
    }

    @NotNull
    public String getTag(@NotNull String key, @NotNull String defaultValue) {
        AtomicReference<String> v = new AtomicReference<>(defaultValue);
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) v.set(p.get(n, PersistentDataType.STRING));
        });
        return v.get();
    }

    @NotNull
    public Item setTag(@NotNull String key, @NotNull Integer value) {
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.INTEGER, value);
        });
        return this;
    }

    @NotNull
    public Integer getTag(@NotNull String key, @NotNull Integer defaultValue) {
        AtomicReference<Integer> v = new AtomicReference<>(defaultValue);
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) v.set(p.get(n, PersistentDataType.INTEGER));
        });
        return v.get();
    }

    @NotNull
    public Item setTag(@NotNull String key, @NotNull Double value) {
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.DOUBLE, value);
        });
        return this;
    }

    @NotNull
    public Double getTag(@NotNull String key, @NotNull Double defaultValue) {
        AtomicReference<Double> v = new AtomicReference<>(defaultValue);
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) v.set(p.get(n, PersistentDataType.DOUBLE));
        });
        return v.get();
    }

    public boolean hasCustomModelData() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return itemMeta.hasCustomModelData();
    }

    @Nullable
    public Integer getCustomModelData() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        return itemMeta.getCustomModelData();
    }

    @NotNull
    public Item setCustomModelData(int customModelData) {
        return editMeta(itemMeta -> itemMeta.setCustomModelData(customModelData));
    }

    public boolean hasName() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return itemMeta.hasDisplayName();
    }

    @NotNull
    public Item name(@Nullable Text name) {
        return editMeta(itemMeta -> {
            if (name != null) itemMeta.displayName(name.comp());
            else itemMeta.displayName(null);
        });
    }

    @Nullable
    public Text name() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta.hasDisplayName()) {
            Component component = itemMeta.displayName();
            if (component == null) return null;
            return new Text(component);
        }
        return null;
    }

    @NotNull
    public Item setName(@Nullable String name) {
        return editMeta(itemMeta -> {
            if (name != null) {
                Text text = new Text(name);
                itemMeta.displayName(text.comp());
            } else itemMeta.displayName(null);
        });
    }

    public boolean hasLore() {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore();
    }

    @NotNull
    public Item setLore(@Nullable List<String> lore) {
        return editMeta(itemMeta -> {
            if (lore != null)
                itemMeta.lore(lore.stream().map(line -> new Text(line).comp()).collect(Collectors.toList()));
            else itemMeta.lore(null);
        });
    }

    @Nullable
    public List<Text> lore() {
        //noinspection ConstantConditions
        return hasLore() ? Text.list(itemStack.getItemMeta().lore()) : null;
    }

    @NotNull
    public Item lore(@Nullable List<Text> lore) {
        return editMeta(itemMeta -> {
            if (lore != null) itemMeta.lore(lore.stream().map(Text::comp).collect(Collectors.toList()));
            else itemMeta.lore(null);
        });
    }

    @NotNull
    public Item skull(@Nullable PlayerProfile playerProfile) {
        return setSkull(playerProfile);
    }

    @NotNull
    public Item setSkull(@Nullable PlayerProfile playerProfile) {
        return editMeta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                skullMeta.setPlayerProfile(playerProfile);
            }
        });
    }

    @NotNull
    public Item skull(@NotNull String url) {
        return setSkull(url);
    }

    public Item setSkull(String url) {
        return editMeta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                String texture = new String(getEncodedTexture(url));
                int len = texture.length();
                UUID uuid = new UUID(texture.substring(len - 20).hashCode(), texture.substring(len - 10).hashCode());

                PlayerProfile profile = Bukkit.createProfile(uuid, null);
                ProfileProperty property = new ProfileProperty("textures", texture);
                profile.setProperty(property);
                skullMeta.setPlayerProfile(profile);
            }
        });
    }

    private byte[] getEncodedTexture(@NotNull String url) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    }

    @NotNull
    public Item editMeta(@NotNull Consumer<ItemMeta> meta) {
        itemStack.editMeta(meta);
        return this;
    }

    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @NotNull
    public Item clone() {
        return new Item(itemStack.clone());
    }
}
