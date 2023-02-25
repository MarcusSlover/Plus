package com.marcusslover.plus.lib.sound;

import com.marcusslover.plus.lib.common.ISendable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/* Todo this class needs to be switched to Adventure API */
public class Note implements ISendable<Note> {
    protected @Nullable Sound sound;
    protected @Nullable String rawSound;
    protected float volume;
    protected float pitch;
    protected @NotNull SoundCategory category;

    public Note(@NotNull String sound) {
        this(sound, 1.0f, 1.0f, SoundCategory.MASTER);
    }

    public Note(@NotNull String sound, float volume, float pitch) {
        this(sound, volume, pitch, SoundCategory.MASTER);
    }

    public Note(@NotNull String sound, float volume, float pitch, @NotNull SoundCategory category) {
        this.rawSound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.category = category;
    }

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

    public static @NotNull Note of(@NotNull String sound) {
        return new Note(sound);
    }

    public static @NotNull Note of(@NotNull String sound, float volume, float pitch) {
        return new Note(sound, volume, pitch);
    }

    public static @NotNull Note of(@NotNull String sound, float volume, float pitch, @NotNull SoundCategory category) {
        return new Note(sound, volume, pitch, category);
    }

    public @Nullable Sound getSound() {
        return this.sound;
    }

    public @NotNull Note setSound(@NotNull Sound sound) {
        this.sound = sound;
        return this;
    }

    public @NotNull Note setSound(@NotNull String sound) {
        this.rawSound = sound;
        return this;
    }

    public @Nullable String getRawSound() {
        return this.rawSound;
    }

    public float getVolume() {
        return this.volume;
    }

    public @NotNull Note setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public float getPitch() {
        return this.pitch;
    }

    public @NotNull Note setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public @NotNull SoundCategory getCategory() {
        return this.category;
    }

    public @NotNull Note setCategory(@NotNull SoundCategory category) {
        this.category = category;
        return this;
    }

    public @NotNull Note play(@NotNull Player player) {
        return this.send(player);
    }

    public @NotNull Note play(@NotNull Player player, @NotNull Location location) {
        return this.send(player, location);
    }

    public @NotNull Note play(@NotNull Player... players) {
        return this.send(players);
    }

    public @NotNull Note play(@NotNull Collection<Player> players) {
        return this.send(players);
    }

    public @NotNull Note play(@NotNull Location location) {
        World world = location.getWorld();
        if (this.sound != null) {
            world.playSound(location, this.sound, this.category, this.volume, this.pitch);
        } else if (this.rawSound != null) {
            world.playSound(location, this.rawSound, this.category, this.volume, this.pitch);
        }
        return this;
    }

    public @NotNull Note send(@NotNull Player player, @NotNull Location location) {
        if (this.sound != null) {
            player.playSound(location, this.sound, this.category, this.volume, this.pitch);
        } else if (this.rawSound != null) {
            player.playSound(location, this.rawSound, this.category, this.volume, this.pitch);
        }
        return this;
    }

    public @NotNull Note send(@NotNull Location location) {
        World world = location.getWorld();
        if (this.sound != null) {
            world.playSound(location, this.sound, this.category, this.volume, this.pitch);
        } else if (this.rawSound != null) {
            world.playSound(location, this.rawSound, this.category, this.volume, this.pitch);
        }
        return this;
    }

    @Override
    public @NotNull <T extends CommandSender> Note send(@NotNull T target) {
        var key = this.sound != null ? Key.key(this.sound.getKey().getNamespace(), this.sound.getKey().getKey()) : Key.key(this.rawSound);

        target.playSound(net.kyori.adventure.sound.Sound.sound(
                key,
                Source.MASTER,
                this.volume,
                this.pitch
        ));

        return this;
    }
}
