package com.marcusslover.plus.lib.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.marcusslover.plus.lib.common.Taggable;
import com.marcusslover.plus.lib.text.ColorUtil;
import com.marcusslover.plus.lib.text.Text;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Item extends Taggable<Item, ItemMeta> {

    protected @NotNull ItemStack itemStack;

    private Item(@NotNull Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    private Item(@NotNull Material material, int amount, @Nullable Text name) {
        this(material, amount);
        this.name(name);
    }

    private Item(@NotNull Material material, int amount, @Nullable Text name, @Nullable List<@NotNull Text> lore) {
        this(material, amount);
        this.name(name);

        if (lore != null) {
            this.lore(lore.stream().map(Text::raw).toList());
        }
    }

    private Item(@NotNull Material material, int amount, @Nullable String name) {
        this(material, amount);
        this.name(name);
    }

    private Item(@NotNull Material material, int amount, @Nullable String name, @Nullable List<@NotNull String> lore) {
        this(material, amount);
        this.name(name);
        this.lore(lore);
    }

    public static @NotNull Item of() {
        return new Item(Material.AIR, 1);
    }

    public static @NotNull Item of(@NotNull Material material) {
        return new Item(material, 1);
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
        if (itemStack == null) {
            return Item.of();
        } else {
            return new Item(itemStack);
        }
    }

    public boolean isValid() {
        return !this.type().isAir();
    }

    public boolean isEmpty() {
        return !this.isValid();
    }

    public boolean isPresent() {
        return this.isValid();
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
    public @NotNull Material material() {
        return this.type();
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
    public @NotNull Item material(@NotNull Material material) {
        return this.type(material);
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
     * Change the material type used in this item.
     * IMPORTANT: This method creates a new item with the specified material.
     *
     * @param material the material type of this item
     * @return brand new item
     */
    public @NotNull Item withType(@NotNull Material material) {
        return Item.of(this.itemStack.withType(material));
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
     * Increment the amount of items in the stack by 1.
     * @return the item
     */
    public @NotNull Item increment() {
        return this.increase();
    }

    /**
     * Increment the amount of items in the stack by the specified amount.
     * @param amount the amount to increment by
     * @return the item
     */
    public @NotNull Item increment(int amount) {
        return this.increase(amount);
    }

    /**
     * Increment the amount of items in the stack by 1.
     * @return the item
     */
    public @NotNull Item increase() {
        return this.increase(1);
    }

    /**
     * Increment the amount of items in the stack by the specified amount.
     * @param amount the amount to increment by
     * @return the item
     */
    public @NotNull Item increase(int amount) {
        return this.amount(this.amount() + amount);
    }

    /**
     * Decrement the amount of items in the stack by 1.
     * @return the item
     */
    public @NotNull Item decrement() {
        return this.decrease();
    }

    /**
     * Decrement the amount of items in the stack by the specified amount.
     * @param amount the amount to decrement by
     * @return the item
     */
    public @NotNull Item decrement(int amount) {
        return this.decrease(amount);
    }

    /**
     * Decrement the amount of items in the stack by 1.
     * @return the item
     */
    public @NotNull Item decrease() {
        return this.decrease(1);
    }

    /**
     * Decrement the amount of items in the stack by the specified amount.
     * @param amount the amount to decrement by
     * @return the item
     */
    public @NotNull Item decrease(int amount) {
        return this.amount(this.amount() - amount);
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
                double damage = max * (1 - finalPercent);
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

    public @NotNull Item glow() {
        return this.glow(true);
    }

    public @NotNull Item glow(boolean glow) {
        if (glow) {
            if (this.itemStack.getType().equals(Material.BOW)) {
                enchant(Enchantment.DIG_SPEED, 1);
            } else {
                enchant(Enchantment.ARROW_INFINITE, 1);
            }
            return addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            clearEnchants();
            return removeItemFlag(ItemFlag.HIDE_ENCHANTS);
        }
    }

    /**
     * Checks if the item can be colored.
     *
     * @return True if the item can be colored, false otherwise.
     */
    public boolean isColorable() {
        if (!this.isValid()) return false;
        ItemMeta meta = this.meta();
        if (meta == null) return false;
        // Colorable items
        return meta instanceof LeatherArmorMeta || meta instanceof PotionMeta || meta instanceof MapMeta;
    }

    /**
     * Change the color of this item.
     *
     * @param hex The hex color code e.g. #FF0000.
     * @return This item.
     * @see #color(Color)
     */
    public @NotNull Item color(@Nullable String hex) {
        return this.color(hex != null ? com.marcusslover.plus.lib.color.Color.of(hex) : null);
    }

    /**
     * Change the color of this item.
     *
     * @param plusColor The color.
     * @return This item.
     * @see #color(Color)
     */
    public @NotNull Item color(@Nullable com.marcusslover.plus.lib.color.Color plusColor) {
        return this.color(plusColor != null ? Color.fromRGB(plusColor.rgb()) : null);
    }

    /**
     * Change the color of this item.
     * Works with {@link Material#POTION}, {@link Material#SPLASH_POTION}, {@link Material#LINGERING_POTION},
     * {@link Material#TIPPED_ARROW}, {@link Material#LEATHER_HELMET}, {@link Material#LEATHER_CHESTPLATE},
     * {@link Material#LEATHER_LEGGINGS}, {@link Material#LEATHER_BOOTS}, {@link Material#LEATHER_HORSE_ARMOR},
     * and colored maps.
     *
     * @param color The color.
     * @return This item.
     */
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
        return Objects.requireNonNull(this.meta()).getPersistentDataContainer();
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

    public @NotNull Item customModelData(@Nullable Integer customModelData) {
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
            return Text.of(component);
        }
        return null;
    }

    public @NotNull Item name(@Nullable String name) {
        return this.meta(itemMeta -> {
            if (name != null) {
                if (name.isEmpty()) {
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
        //noinspection ConstantConditions,deprecation
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

    /**
     * Set the skull texture of this item. Example:
     * eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNjMjVhN2ExODgxOTZiMTg3MTcyNjRmZmU4MzdjYTM0OGNmNzE5ZTgyNzE3OWVkYzRiNzhjYmNiOGM3ZGQ4In19fQ==
     *
     * @param skullValue the skull texture
     * @return this item
     */
    public @NotNull Item skullValue(@NotNull String skullValue) {
        return this.meta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                Base64.Encoder encoder = Base64.getEncoder();
                String texture = new String(encoder.encode(skullValue.getBytes()));
                this.skull(skullMeta, texture);
            }
        });
    }

    /**
     * Set the skull texture of this item. Example:
     * 83c25a7a188196b18717264ffe837ca348cf719e827179edc4b78cbcb8c7dd8
     * @param minecraftUrl the minecraft url
     * @return this item
     */
    public @NotNull Item skullMinecraftUrl(@NotNull String minecraftUrl) {
        return this.meta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                String suffix = "http://textures.minecraft.net/texture/";
                String texture;
                if (minecraftUrl.startsWith(suffix)) {
                    Base64.Encoder encoder = Base64.getEncoder();
                    texture = new String(encoder.encode(minecraftUrl.getBytes()));
                } else {
                    texture = new String(this.getEncodedTexture(suffix + minecraftUrl));
                }
                if (texture.isEmpty()) {
                    return;
                }
                this.skull(skullMeta, texture);
            }
        });
    }

    private void skull(@NotNull SkullMeta skullMeta, @NotNull String texture) {
        int len = texture.length();
        UUID uuid = new UUID(texture.substring(len - 20).hashCode(), texture.substring(len - 10).hashCode());

        PlayerProfile profile = Bukkit.createProfile(uuid, null);
        ProfileProperty property = new ProfileProperty("textures", texture);
        profile.setProperty(property);

        skullMeta.setPlayerProfile(profile);
    }

    @Deprecated(forRemoval = true)
    public @NotNull Item skull(@NotNull String url) {
        return this.meta(itemMeta -> {
            if (itemMeta instanceof SkullMeta skullMeta) {
                String texture = new String(this.getEncodedTexture(url));
                skull(skullMeta, texture);
            }
        });
    }

    private byte[] getEncodedTexture(@NotNull String url) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    }

    @Override
    public @NotNull Item meta(@NotNull Consumer<@NotNull ItemMeta> meta) {
        this.itemStack.editMeta(meta);
        return this;
    }

    @Override
    public @Nullable ItemMeta meta() {
        return this.itemStack.getItemMeta();
    }

    @Override
    public @NotNull Item meta(@NotNull ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public @NotNull ItemStack get() {
        return this.itemStack;
    }

    public @NotNull Item itemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
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

    @Override
    protected @NotNull Item holder() {
        return this;
    }
}
