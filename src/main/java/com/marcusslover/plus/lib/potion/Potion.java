package com.marcusslover.plus.lib.potion;

import com.marcusslover.plus.lib.common.IApplicable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Data()
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true, chain = true)
public class Potion implements IApplicable<LivingEntity, Potion> {
    protected @NotNull PotionEffectType type;
    protected int duration;
    protected int amplifier;
    protected boolean particles;
    protected boolean transparent;
    protected boolean icon;
    protected boolean force;

    public static @NotNull Potion of(@NotNull PotionEffectType type) {
        return new Potion(type, Ticks.TICKS_PER_SECOND * 30, 0, true, false, true, false);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier) {
        return new Potion(type, durationTicks, amplifier, true, false, true, false);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles) {
        return new Potion(type, durationTicks, amplifier, particles, false, true, false);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent) {
        return new Potion(type, durationTicks, amplifier, particles, transparent, true, false);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent, boolean icon) {
        return new Potion(type, durationTicks, amplifier, particles, transparent, icon, false);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent, boolean icon, boolean force) {
        return new Potion(type, durationTicks, amplifier, particles, transparent, icon, force);
    }

    @Override
    public @NotNull Potion apply(@NotNull LivingEntity entity) {
        PotionEffect potionEffect = new PotionEffect(this.type, this.duration, this.amplifier)
                .withParticles(this.particles)
                .withAmbient(this.transparent)
                .withIcon(this.icon);

        //noinspection deprecation
        entity.addPotionEffect(potionEffect, this.force);

        return this;
    }
}
