package com.marcusslover.plus.lib.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.marcusslover.plus.lib.text.ColorUtil;
import com.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
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
        this.name(name);
    }

    public Item(@NotNull Material material, int amount, @Nullable Text name, @Nullable List<@NotNull Text> lore) {
        this(material, amount);
        this.name(name);

        if (lore != null) {
            this.lore(lore.stream().map(Text::raw).toList());
        }
    }

    public Item(@NotNull Material material, int amount, @Nullable String name) {
        this(material, amount);
        this.name(name);
    }

    public Item(@NotNull Material material, int amount, @Nullable String name, @Nullable List<@NotNull String> lore) {
        this(material, amount);
        this.name(name);
        this.lore(lore);
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
        return !this.type().isAir();
    }

    /**
     * @return the max item stack of the material used in this item
     */
    public int maxStack() {
        return this.itemStack.getType().getMaxStackSize();
    }

    /**
     * @return the material type of this item
     */
    public @NotNull Material type() {
        return this.itemStack.getType();
    }

    /**
     * Change the material type used in this item.
     *
     * @param material the material type of this item
     * @return this item
     */
    public @NotNull Item type(@NotNull Material material) {
        this.itemStack.setType(material);
        return this;
    }

    /**
     * @return the amount of items in the stack
     */
    public int amount() {
        return this.itemStack.getAmount();
    }

    /**
     * Set the amount of the item.
     *
     * @param amount the amount items in the stack
     * @return the item
     */
    public @NotNull Item amount(int amount) {
        this.itemStack.setAmount(Math.min(64, Math.max(0, amount)));
        return this;
    }

    /**
     * Changes the amount of damage this item has as a percentage.
     *
     * @param percent 0.0 - 1.0
     * @return this item
     */
    public @NotNull Item damage(double percent) {
        double finalPercent = Math.min(1, Math.max(0, percent));

        return this.meta(itemMeta -> {
            if (itemMeta instanceof Damageable damageable) {
                double max = this.itemStack.getType().getMaxDurability();
                double damage = max * finalPercent / 100;
                damageable.setDamage((int) Math.max(0, damage));
            }
        });
    }

    /**
     * @return the amount of damage this item has as a value.
     */
    public @Nullable Integer damage() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        if (itemMeta instanceof Damageable damageable) {
            return damageable.getDamage();
        }
        return 0;
    }

    /**
     * Changes the amount of damage this item has as a value.
     *
     * @param damage item damage
     * @return this item
     */
    public @NotNull Item damage(int damage) {
        return this.meta(itemMeta -> {
            if (itemMeta instanceof Damageable damageable) {
                damageable.setDamage(damage);
            }
        });
    }

    /**
     * Get the level of the applied enchantment.
     *
     * @param enchantment enchantment
     * @return enchantment level
     */
    public int enchant(@NotNull Enchantment enchantment) {
        return this.itemStack.getEnchantmentLevel(enchantment);
    }

    /**
     * Add an enchantment to this item.
     *
     * @param enchantment enchantment
     * @param level       the level of the enchantment. If the level is 0, the enchantment will be removed.
     * @return this item
     */
    public @NotNull Item enchant(@NotNull Enchantment enchantment, int level) {
        if (level <= 0) {
            this.itemStack.removeEnchantment(enchantment);
        } else {
            this.itemStack.addUnsafeEnchantment(enchantment, level);
        }
        return this;
    }

    /**
     * Clears all enchantments from this item
     *
     * @return this item
     */
    public @NotNull Item clearEnchants() {
        this.itemStack.getEnchantments().keySet().forEach(this.itemStack::removeEnchantment);
        return this;
    }

    /**
     * @return a map of all the enchantments on this item
     */
    public @NotNull Map<Enchantment, Integer> enchants() {
        return this.itemStack.getEnchantments();
    }

    public boolean hasEnchant(@NotNull Enchantment enchantment) {
        return this.enchant(enchantment) > 0;
    }

    public @NotNull Item color(@Nullable com.marcusslover.plus.lib.color.Color plusColor) {
        return this.color(plusColor != null ? Color.fromRGB(plusColor.rgb()) : null);
    }

    public @NotNull Item color(@Nullable Color color) {
        return this.meta(itemMeta -> {
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
        return this.meta(meta -> meta.addAttributeModifier(attribute, modifier));
    }

    public @NotNull Item removeAttribute(@NotNull Attribute attribute) {
        return this.meta(meta -> meta.removeAttributeModifier(attribute));
    }

    public @NotNull Item removeAttribute(@NotNull EquipmentSlot equipmentSlot) {
        return this.meta(meta -> meta.removeAttributeModifier(equipmentSlot));
    }

    public @NotNull Item addItemFlag(@NotNull ItemFlag itemFlag) {
        return this.meta(itemMeta -> itemMeta.addItemFlags(itemFlag));
    }

    public @NotNull Item removeItemFlag(@NotNull ItemFlag itemFlag) {
        return this.meta(itemMeta -> itemMeta.removeItemFlags(itemFlag));
    }

    public boolean hasItemFlag(@NotNull ItemFlag itemFlag) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        return itemMeta.hasItemFlag(itemFlag);
    }

    public @NotNull Item hideItemFlags() {
        return this.hideItemFlags(true);
    }

    public @NotNull Item hideItemFlags(boolean hideflags) {
        return this.meta(itemMeta -> {
            if (!hideflags) {
                itemMeta.removeItemFlags(ItemFlag.values());
            } else {
                itemMeta.addItemFlags(ItemFlag.values());
            }
        });
    }

    /**
     * @return whether the item is breakable or not.
     */
    public boolean unbreakable() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        return itemMeta.isUnbreakable();
    }

    /**
     * @param unbreakable whether the item is breakable or not.
     * @return this item.
     */
    public @NotNull Item unbreakable(boolean unbreakable) {
        return this.meta(itemMeta -> itemMeta.setUnbreakable(unbreakable));
    }

    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return this.meta().getPersistentDataContainer();
    }

    public boolean hasTag(@NotNull String key) {
        PersistentDataContainer p = this.meta().getPersistentDataContainer();
        NamespacedKey n = new NamespacedKey("plus", key);
        return p.has(n);
    }

    public @NotNull Item setTag(@NotNull String key, @NotNull String value) {
        this.meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.STRING, value);
        });
        return this;
    }

    public @NotNull String getTag(@NotNull String key, @NotNull String defaultValue) {
        AtomicReference<String> v = new AtomicReference<>(defaultValue);
        this.meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) {
                v.set(p.get(n, PersistentDataType.STRING));
            }
        });
        return v.get();
    }

    public @NotNull Item setTag(@NotNull String key, @NotNull Integer value) {
        this.meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.INTEGER, value);
        });
        return this;
    }

    public @NotNull Item setTag(@NotNull String key, @NotNull Long value) {
        this.meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.LONG, value);
        });
        return this;
    }


    public @NotNull Integer getTag(@NotNull String key, @NotNull Integer defaultValue) {
        AtomicReference<Integer> v = new AtomicReference<>(defaultValue);
        this.meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) {
                v.set(p.get(n, PersistentDataType.INTEGER));
            }
        });
        return v.get();
    }

    public @NotNull Item setTag(@NotNull String key, @NotNull Double value) {
        this.meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, PersistentDataType.DOUBLE, value);
        });
        return this;
    }

    public @NotNull Double getTag(@NotNull String key, @NotNull Double defaultValue) {
        AtomicReference<Double> v = new AtomicReference<>(defaultValue);
        this.meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) {
                v.set(p.get(n, PersistentDataType.DOUBLE));
            }
        });
        return v.get();
    }

    public @NotNull Long getTag(@NotNull String key, @NotNull Long defaultValue) {
        AtomicReference<Long> v = new AtomicReference<>(defaultValue);
        this.meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            if (p.has(n)) {
                v.set(p.get(n, PersistentDataType.LONG));
            }
        });
        return v.get();
    }

    public boolean hasCustomModelData() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        return itemMeta.hasCustomModelData();
    }

    public @Nullable Integer customModelData() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        return itemMeta.getCustomModelData();
    }

    public @NotNull Item customModelData(int customModelData) {
        return this.meta(itemMeta -> itemMeta.setCustomModelData(customModelData));
    }

    public boolean hasName() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        return itemMeta.hasDisplayName();
    }

    public @NotNull Item name(@Nullable Text name) {
        return this.meta(itemMeta -> {
            if (name != null) {
                itemMeta.displayName(name.comp());
            } else {
                itemMeta.displayName(null);
            }
        });
    }

    public @Nullable Text name() {
        ItemMeta itemMeta = this.meta();
        if (itemMeta == null) {
            return null;
        }
        if (itemMeta.hasDisplayName()) {
            Component component = itemMeta.displayName();
            if (component == null) {
                return null;
            }
            return new Text(component);
        }
        return null;
    }

    public @NotNull Item name(@Nullable String name) {
        return this.meta(itemMeta -> {
            if (name != null) {
                if (name.isEmpty() || name.isBlank()) {
                    itemMeta.displayName(null);
                } else {
                    itemMeta.displayName(Text.of(name).comp());
                }
            } else {
                itemMeta.displayName(null);
            }
        });
    }

    public boolean hasLore() {
        return this.itemStack.hasItemMeta() && this.itemStack.getItemMeta().hasLore();
    }

    public @NotNull List<@NotNull String> lore() {
        //noinspection ConstantConditions
        return this.hasLore() ? ColorUtil.translateList(this.meta().getLore()) : new ArrayList<>();
    }

    public @NotNull Item lore(@Nullable Collection<@NotNull String> lore) {
        return this.meta(itemMeta -> {
            if (lore != null) {
                if (lore.isEmpty()) {
                    itemMeta.lore(null);
                } else {
                    itemMeta.lore(lore.stream().map(line -> Text.of(line).comp()).toList());
                }
            } else {
                itemMeta.lore(null);
            }
        });
    }

    public @NotNull Item skull(@Nullable PlayerProfile playerProfile) {
        return this.meta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                skullMeta.setPlayerProfile(playerProfile);
            }
        });
    }

    public @NotNull Item skull(@Nullable OfflinePlayer player) {
        return this.meta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(player);
            }
        });
    }

    public @NotNull Item skull(@NotNull String url) {
        return this.meta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                String texture = new String(this.getEncodedTexture(url));
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

    public @NotNull Item meta(@NotNull Consumer<@NotNull ItemMeta> meta) {
        this.itemStack.editMeta(meta);
        return this;
    }

    public @NotNull Item meta(@NotNull ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemMeta meta() {
        return this.itemStack.getItemMeta();
    }

    public @NotNull ItemStack get() {
        return this.itemStack;
    }

    public @NotNull Item itemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public @NotNull Item clone() {
        return new Item(this.itemStack.clone());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Item item) {
            return this.itemStack.equals(item.itemStack);
        }

        return super.equals(obj);
    }
}
