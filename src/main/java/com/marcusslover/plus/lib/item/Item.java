package com.marcusslover.plus.lib.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Item {
    protected @NotNull ItemStack itemStack;

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

    public Item(@NotNull Material material, int amount, @Nullable Text name, @Nullable List<@NotNull Text> lore) {
        this(material, amount);
        name(name);
        lore(lore);
    }

    public Item(@NotNull Material material, int amount, @Nullable String name) {
        this(material, amount);
        setName(name);
    }

    public Item(@NotNull Material material, int amount, @Nullable String name, @Nullable List<@NotNull String> lore) {
        this(material, amount);
        setName(name);
        setLore(lore);
    }

    public Item(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack == null ? new ItemStack(Material.AIR) : itemStack;
    }

    public static @NotNull Item of() {
        return new Item();
    }

    public static @NotNull Item of(@NotNull Material material) {
        return new Item(material);
    }

    public static @NotNull Item of(@NotNull Material material, int amount) {
        return new Item(material, amount);
    }

    public static @NotNull Item of(@NotNull Material material, int amount, @Nullable Text name) {
        return new Item(material, amount, name);
    }

    public static @NotNull Item of(@NotNull Material material, int amount, @Nullable Text name, @Nullable List<@NotNull Text> lore) {
        return new Item(material, amount, name, lore);
    }

    public static @NotNull Item of(@NotNull Material material, int amount, @Nullable String name) {
        return new Item(material, amount, name);
    }

    public static @NotNull Item of(@NotNull Material material, int amount, @Nullable String name, @Nullable List<@NotNull String> lore) {
        return new Item(material, amount, name, lore);
    }

    public static @NotNull Item of(@Nullable ItemStack itemStack) {
        return new Item(itemStack);
    }

    public boolean isValid() {
        //noinspection ConstantConditions
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    public int getMaxStack() {
        return itemStack.getType().getMaxStackSize();
    }

    public @NotNull Material getType() {
        return itemStack.getType();
    }

    public @NotNull Item setType(@NotNull Material material) {
        itemStack.setType(material);
        return this;
    }

    public int getAmount() {
        return itemStack.getAmount();
    }

    public @NotNull Item setAmount(int amount) {
        itemStack.setAmount(Math.min(64, Math.max(0, amount)));
        return this;
    }

    public @NotNull Item setDamagePercent(double percent) {
        return editMeta(itemMeta -> {
            if (itemMeta instanceof Damageable damageable) {
                double max = itemStack.getType().getMaxDurability();
                double damage = max * percent / 100;
                damageable.setDamage((int) Math.max(0, damage));
            }
        });
    }

    public @Nullable Integer getDamage() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof Damageable damageable) {
            return damageable.getDamage();
        }
        return 0;
    }

    public @NotNull Item setDamage(int damage) {
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

    public @NotNull Item setEnchant(@NotNull Enchantment enchantment, int level) {
        if (level <= 0) itemStack.removeEnchantment(enchantment);
        else itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public @NotNull Item setColor(@Nullable com.marcusslover.plus.lib.color.Color plusColor) {
        return setColor(plusColor != null ? Color.fromRGB(plusColor.rgb()) : null);
    }

    public @NotNull Item setColor(@Nullable Color color) {
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

    public @NotNull Item addAttribute(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        return editMeta(meta -> meta.addAttributeModifier(attribute, modifier));
    }

    public @NotNull Item removeAttribute(@NotNull Attribute attribute) {
        return editMeta(meta -> meta.removeAttributeModifier(attribute));
    }

    public @NotNull Item removeAttribute(@NotNull EquipmentSlot equipmentSlot) {
        return editMeta(meta -> meta.removeAttributeModifier(equipmentSlot));
    }

    public @NotNull Item addItemFlag(@NotNull ItemFlag itemFlag) {
        return editMeta(itemMeta -> itemMeta.addItemFlags(itemFlag));
    }

    public @NotNull Item removeItemFlag(@NotNull ItemFlag itemFlag) {
        return editMeta(itemMeta -> itemMeta.removeItemFlags(itemFlag));
    }

    public boolean hasItemFlag(@NotNull ItemFlag itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return itemMeta.hasItemFlag(itemFlag);
    }


    public @NotNull Item setHideflags(boolean hideflags) {
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

    public @NotNull Item setUnbreakable(boolean unbreakable) {
        return editMeta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return getMeta().getPersistentDataContainer();
    }

    public boolean hasTag(@NotNull String key) {
        PersistentDataContainer p = getMeta().getPersistentDataContainer();
        NamespacedKey n = new NamespacedKey("plus", key);
        return p.has(n);
    }

    public @NotNull Item setTag(@NotNull String key, @NotNull String value) {
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.STRING, value);
        });
        return this;
    }

    public @NotNull String getTag(@NotNull String key, @NotNull String defaultValue) {
        AtomicReference<String> v = new AtomicReference<>(defaultValue);
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) v.set(p.get(n, PersistentDataType.STRING));
        });
        return v.get();
    }

    public @NotNull Item setTag(@NotNull String key, @NotNull Integer value) {
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.INTEGER, value);
        });
        return this;
    }

    public @NotNull Item setTag(@NotNull String key, @NotNull Long value) {
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.LONG, value);
        });
        return this;
    }


    public @NotNull Integer getTag(@NotNull String key, @NotNull Integer defaultValue) {
        AtomicReference<Integer> v = new AtomicReference<>(defaultValue);
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) v.set(p.get(n, PersistentDataType.INTEGER));
        });
        return v.get();
    }

    public @NotNull Item setTag(@NotNull String key, @NotNull Double value) {
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.DOUBLE, value);
        });
        return this;
    }

    public @NotNull Double getTag(@NotNull String key, @NotNull Double defaultValue) {
        AtomicReference<Double> v = new AtomicReference<>(defaultValue);
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) v.set(p.get(n, PersistentDataType.DOUBLE));
        });
        return v.get();
    }

    public @NotNull Long getTag(@NotNull String key, @NotNull Long defaultValue) {
        AtomicReference<Long> v = new AtomicReference<>(defaultValue);
        editMeta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) v.set(p.get(n, PersistentDataType.LONG));
        });
        return v.get();
    }

    public boolean hasCustomModelData() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return itemMeta.hasCustomModelData();
    }

    public @Nullable Integer getCustomModelData() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        return itemMeta.getCustomModelData();
    }

    public @NotNull Item setCustomModelData(int customModelData) {
        return editMeta(itemMeta -> itemMeta.setCustomModelData(customModelData));
    }

    public boolean hasName() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        return itemMeta.hasDisplayName();
    }

    public @NotNull Item name(@Nullable Text name) {
        return editMeta(itemMeta -> {
            if (name != null) itemMeta.displayName(name.comp());
            else itemMeta.displayName(null);
        });
    }

    public @Nullable Text name() {
        ItemMeta itemMeta = getMeta();
        if (itemMeta == null) return null;
        if (itemMeta.hasDisplayName()) {
            Component component = itemMeta.displayName();
            if (component == null) return null;
            return new Text(component);
        }
        return null;
    }

    public @NotNull Item setName(@Nullable String name) {
        return editMeta(itemMeta -> {
            if (name != null) {
                if (name.isEmpty() || name.isBlank()) itemMeta.displayName(null);
                else itemMeta.displayName(Text.of(name).comp());
            } else itemMeta.displayName(null);
        });
    }

    public boolean hasLore() {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore();
    }

    public @NotNull List<@NotNull Text> lore() {
        //noinspection ConstantConditions
        return hasLore() ? Text.list(getMeta().lore()) : new ArrayList<>();
    }

    public @NotNull Item lore(@Nullable List<@NotNull Text> lore) {
        return editMeta(itemMeta -> {
            if (lore != null) itemMeta.lore(lore.stream().map(Text::comp).collect(Collectors.toList()));
            else itemMeta.lore(null);
        });
    }

    public @NotNull List<@NotNull String> getLore() {
        //noinspection ConstantConditions
        return hasLore() ? getMeta().getLore() : new ArrayList<>();
    }

    public @NotNull Item setLore(@Nullable List<@NotNull String> lore) {
        return editMeta(itemMeta -> {
            if (lore != null)
                if (lore.isEmpty()) itemMeta.lore(null);
                else itemMeta.lore(lore.stream().map(line -> Text.of(line).comp()).toList());
            else itemMeta.lore(null);
        });
    }

    public @NotNull Item skull(@Nullable PlayerProfile playerProfile) {
        return setSkull(playerProfile);
    }

    public @NotNull Item setSkull(@Nullable OfflinePlayer player) {
        return editMeta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(player);
            }
        });
    }

    public @NotNull Item setSkull(@Nullable PlayerProfile playerProfile) {
        return editMeta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                skullMeta.setPlayerProfile(playerProfile);
            }
        });
    }

    public @NotNull Item skull(@NotNull String url) {
        return setSkull(url);
    }

    public @NotNull Item setSkull(@NotNull String url) {
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

    public @NotNull Item editMeta(@NotNull Consumer<@NotNull ItemMeta> meta) {
        itemStack.editMeta(meta);
        return this;
    }

    public ItemMeta getMeta() {
        return itemStack.getItemMeta();
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    public @NotNull Item setItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public @NotNull Item clone() {
        return new Item(itemStack.clone());
    }
}
