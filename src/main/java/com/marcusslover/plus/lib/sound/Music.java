package com.marcusslover.plus.lib.sound;

import com.marcusslover.plus.lib.common.ISendable;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Music can be used to loop notes for an audience or command sender.
 *
 * <p>Example:
 * <pre>
 *     {@code
 *     Music.of(Note.of(Key.key("music.loop"), 0.2f, 1f), 2400).send(audience);
 *     }
 * </pre>
 * This will play the note "music.loop" for 2400 ticks (2 minutes) for the audience.
 * <p>
 */
@Data
@Accessors(fluent = true)
public class Music implements ISendable<Music> {
    protected final Map<UUID, Session> sessions = new HashMap<>();
    /**
     * The intro note to play before the loop.
     */
    protected @Nullable Note intro;
    /**
     * The length of the intro in ticks.
     */
    protected long introLength;
    /**
     * The note to play on loop.
     */
    protected @NotNull Note loop;
    /**
     * The length of the loop in ticks.
     */
    protected long loopLength;
    /**
     * The note to play when stopped.
     */
    protected @Nullable Note tail;

    private Music(@Nullable Note intro, long introLength, @NotNull Note loop, long loopLength, @Nullable Note tail) {
        this.intro = intro;
        this.introLength = introLength;
        this.loop = loop;
        this.loopLength = loopLength;
        this.tail = tail;
    }

    /**
     * Creates a new music object.
     *
     * @param loop       The note to play on loop.
     * @param noteLength The length of the note in ticks.
     * @return The music object.
     */
    public static @NotNull Music of(@NotNull Note loop, long noteLength) {
        return new Music(null, 0, loop, noteLength, null);
    }

    /**
     * Create a new music object with an intro and tail note.
     *
     * @param intro       Note to play before the loop.
     * @param introLength Length of the intro in ticks.
     * @param loop        Note to play on loop.
     * @param loopLength  Length of the loop in ticks.
     * @param tail        Note to play when stopped.
     * @return The music object.
     */
    public static @NotNull Music of(@NotNull Note intro, long introLength, @NotNull Note loop, long loopLength, @NotNull Note tail) {
        return new Music(intro, introLength, loop, loopLength, tail);
    }

    /**
     * Checks if the given player is playing the music.
     *
     * @param uuid The player to check.
     * @return True if the player is playing the music.
     */
    public boolean isPlaying(UUID uuid) {
        return this.sessions.containsKey(uuid);
    }

    @Override
    public @NotNull <T extends CommandSender> Music send(@NotNull T target) {
        if (target instanceof Player) this.send((Audience) target);
        return this;
    }

    @Override
    public @NotNull Music send(Audience audience) {
        audience.forEachAudience(p -> {
            if (p instanceof Player player) {
                this.sessions.put(player.getUniqueId(), new Session());
            }
        });
        return this;
    }

    /**
     * Set the intro note and length.
     *
     * @param note        Note to play before the loop.
     * @param introLength Length of the intro in ticks.
     * @return The music object.
     */
    public @NotNull Music intro(@NotNull Note note, long introLength) {
        this.intro = note;
        this.introLength = introLength;
        return this;
    }

    /**
     * Set the loop note and length.
     *
     * @param note       Note to play on loop.
     * @param loopLength Length of the loop in ticks.
     * @return The music object.
     */
    public @NotNull Music loop(@NotNull Note note, long loopLength) {
        this.loop = note;
        this.loopLength = loopLength;
        return this;
    }

    /**
     * Set the tail note and length.
     *
     * @param note Note to play when stopped.
     * @return The music object.
     */
    public @NotNull Music tail(@NotNull Note note) {
        this.tail = note;
        return this;
    }


    @Getter
    public static class Session {

    }
}
