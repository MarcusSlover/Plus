package com.marcusslover.plus.lib.sound;

import com.marcusslover.plus.lib.common.ISendable;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Data
@Accessors(fluent = true)
public class Note implements ISendable<Note> {
    protected final Sound sound;

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
}
