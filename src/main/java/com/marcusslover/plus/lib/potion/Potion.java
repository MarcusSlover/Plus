package com.marcusslover.plus.lib.potion;

import com.marcusslover.plus.lib.common.IApplicable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Potion implements IApplicable<LivingEntity, Potion> {
    protected @NotNull PotionEffectType type;
    protected int duration;
    protected int amplifier;
    protected boolean particles;
    protected boolean transparent;
    protected boolean icon;
    protected boolean force;

    public Potion(@NotNull PotionEffectType type) {
        this(type, 20 * 30, 0);
    }

    public Potion(@NotNull PotionEffectType type, int durationTicks, int amplifier) {


        this(type, durationTicks, amplifier, true);
    }

    public Potion(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles) {
        this(type, durationTicks, amplifier, particles, false);
    }

    public Potion(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent) {
        this(type, durationTicks, amplifier, particles, transparent, true);
    }

    public Potion(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent, boolean icon) {
        this(type, durationTicks, amplifier, particles, transparent, icon, false);
    }

    public Potion(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent, boolean icon, boolean force) {
        this.type = type;
        this.duration = durationTicks;
        this.amplifier = amplifier;
        this.particles = particles;
        this.transparent = transparent;
        this.icon = icon;
        this.force = force;
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type) {
        return new Potion(type);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier) {
        return new Potion(type, durationTicks, amplifier);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles) {
        return new Potion(type, durationTicks, amplifier, particles);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent) {
        return new Potion(type, durationTicks, amplifier, particles, transparent);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent, boolean icon) {
        return new Potion(type, durationTicks, amplifier, particles, transparent, icon);
    }

    public static @NotNull Potion of(@NotNull PotionEffectType type, int durationTicks, int amplifier, boolean particles, boolean transparent, boolean icon, boolean force) {
        return new Potion(type, durationTicks, amplifier, particles, transparent, icon, force);
    }

    public boolean isForce() {
        return this.force;
    }

    public @NotNull Potion setForce(boolean force) {
        this.force = force;
        return this;
    }

    public boolean isIcon() {
        return this.icon;
    }

    public @NotNull Potion setIcon(boolean icon) {
        this.icon = icon;
        return this;
    }

    public boolean isParticles() {
        return this.particles;
    }

    public @NotNull Potion setParticles(boolean particles) {
        this.particles = particles;
        return this;
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public @NotNull Potion setTransparent(boolean transparent) {
        this.transparent = transparent;
        return this;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public @NotNull Potion setAmplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public long getDurationTicks() {
        return this.duration;
    }

    public @NotNull Potion setDurationTicks(int durationTicks) {
        this.duration = durationTicks;
        return this;
    }

    public @NotNull PotionEffectType getType() {
        return this.type;
    }

    public @NotNull Potion setType(@NotNull PotionEffectType type) {
        this.type = type;
        return this;
    }

    @Override
    public @NotNull Potion apply(@NotNull LivingEntity entity) {
        PotionEffect potionEffect = new PotionEffect(this.type, this.duration, this.amplifier)
                .withParticles(this.particles)
                .withAmbient(this.transparent)
                .withIcon(this.icon);

        entity.addPotionEffect(potionEffect, this.force);

        return this;
    }
}
