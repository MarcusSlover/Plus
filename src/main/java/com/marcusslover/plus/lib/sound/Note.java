package com.marcusslover.plus.lib.sound;

import com.marcusslover.plus.lib.util.Alternative;
import com.marcusslover.plus.lib.util.ISendable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Note implements ISendable<Player, Note> {
    protected @NotNull Sound sound;
    protected float volume;
    protected float pitch;
    protected @NotNull SoundCategory category;

    public Note(@NotNull Sound sound) {
        this(sound, 1.0f, 1.0f, SoundCategory.MASTER);
    }

    public Note(@NotNull Sound sound, float volume, float pitch) {
        this(sound, volume, pitch, SoundCategory.MASTER);
    }

    public Note(@NotNull Sound sound, float volume, float pitch, @NotNull SoundCategory category) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.category = category;
    }

    public static @NotNull Note of(@NotNull Sound sound) {
        return new Note(sound);
    }

    public static @NotNull Note of(@NotNull Sound sound, float volume, float pitch) {
        return new Note(sound, volume, pitch);
    }

    public static @NotNull Note of(@NotNull Sound sound, float volume, float pitch, @NotNull SoundCategory category) {
        return new Note(sound, volume, pitch, category);
    }

    public @NotNull Sound getSound() {
        return sound;
    }

    public @NotNull Note setSound(@NotNull Sound sound) {
        this.sound = sound;
        return this;
    }

    public float getVolume() {
        return volume;
    }

    public @NotNull Note setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public @NotNull Note setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public @NotNull SoundCategory getCategory() {
        return category;
    }

    public @NotNull Note setCategory(@NotNull SoundCategory category) {
        this.category = category;
        return this;
    }

    @Alternative
    public @NotNull Note play(@NotNull Player player) {
        return this.send(player);
    }

    @Alternative
    public @NotNull Note play(@NotNull Player player, @NotNull Location location) {
        return this.send(player, location);
    }

    @Override
    public @NotNull Note send(@NotNull Player player) {
        return this.send(player, player.getLocation());
    }

    public @NotNull Note send(@NotNull Player player, @NotNull Location location) {
        player.playSound(location, sound, category, volume, pitch);
        return this;
    }
}
