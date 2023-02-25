package com.marcusslover.plus.lib.sound;

import com.marcusslover.plus.lib.common.ISendable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Data
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Note implements ISendable<Note> {
    protected final Sound sound;

    public static Note of(@NotNull Sound sound) {
        return new Note(sound);
    }

    public static Note of(@NotNull String sound) {
        return new Note(Sound.sound(Key.key(sound), Sound.Source.MASTER, 1F, 1F));
    }

    public static Note of(@NotNull String sound, float volume, float pitch) {
        return new Note(Sound.sound(Key.key(sound), Sound.Source.MASTER, volume, pitch));
    }

    /* Static Constructors */

    public static Note of(@NotNull String sound, float volume, float pitch, Sound.Source source) {
        return new Note(Sound.sound(Key.key(sound), source, volume, pitch));
    }

    public @NotNull Note send(@NotNull Location location) {
        location.getWorld().playSound(this.sound, location.getX(), location.getY(), location.getZ());

        return this;
    }

    @Override
    public @NotNull <T extends CommandSender> Note send(@NotNull T target) {
        target.playSound(this.sound);

        return this;
    }

    @Override
    public @NotNull Note send(Audience audience) {
        audience.playSound(this.sound);

        return this;
    }

    /* Static Constructors */

    public static Note of(@NotNull Sound sound) {
        return new Note(sound);
    }

    public static Note of(@NotNull String sound) {
        return new Note(Sound.sound(Key.key(sound), Sound.Source.MASTER, 1F, 1F));
    }

    public static Note of(@NotNull String sound, float volume, float pitch) {
        return new Note(Sound.sound(Key.key(sound), Sound.Source.MASTER, volume, pitch));
    }

    public static Note of(@NotNull String sound, float volume, float pitch, Sound.Source source) {
        return new Note(Sound.sound(Key.key(sound), source, volume, pitch));
    }

    public static Note of(@NotNull Key key) {
        return new Note(Sound.sound(key, Sound.Source.MASTER, 1F, 1F));
    }

    public static Note of(@NotNull Key key, float volume, float pitch) {
        return new Note(Sound.sound(key, Sound.Source.MASTER, volume, pitch));
    }

    public static Note of(@NotNull Key key, float volume, float pitch, Sound.Source source) {
        return new Note(Sound.sound(key, source, volume, pitch));
    }
}
