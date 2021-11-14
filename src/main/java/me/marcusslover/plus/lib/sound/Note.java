package me.marcusslover.plus.lib.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class Note {
    protected Sound sound;
    protected float volume;
    protected float pitch;
    protected SoundCategory category;

    public Note(Sound sound) {
        this(sound, 1.0f, 1.0f, SoundCategory.MASTER);
    }

    public Note(Sound sound, float volume, float pitch) {
        this(sound, volume, pitch, SoundCategory.MASTER);
    }

    public Note(Sound sound, float volume, float pitch, SoundCategory category) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.category = category;
    }

    public Sound getSound() {
        return sound;
    }

    public Note setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public float getVolume() {
        return volume;
    }

    public Note setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public Note setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public SoundCategory getCategory() {
        return category;
    }

    public Note setCategory(SoundCategory category) {
        this.category = category;
        return this;
    }

    public Note play(Player player) {
        return play(player, player.getLocation());
    }

    public Note play(Player player, Location location) {
        player.playSound(location, sound, category, volume, pitch);
        return this;
    }
}
