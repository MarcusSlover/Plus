package com.marcusslover.plus.lib;

import com.marcusslover.plus.PlusPlugin;
import com.marcusslover.plus.lib.sound.Note;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class Plus {
    private static Plus instance;

    private Plus() {
        instance = this;

        var x = Note.of(Sound.sound(Key.key("minecraft", "block.note_block.bell"), Sound.Source.BLOCK, 1.0F, 1.0F));
        x = Note.of("minecraft:block.note_block.bell", 1.0F, 1.0F, Sound.Source.BLOCK);
        x = Note.of("minecraft:block.note_block.bell", 1.0F, 1.0F);

    }

    @NotNull
    public static Plus get() {
        return instance == null ? new Plus() : instance;
    }

    @NotNull
    public static File file(@NotNull String path) {
        Plus plus = get();
        Plugin plugin = plus.plugin();
        return new File(plugin.getDataFolder(), path);
    }

    @NotNull
    private Plugin plugin() {
        return PlusPlugin.getPlugin(PlusPlugin.class);
    }
}