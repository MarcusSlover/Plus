package com.marcusslover.plus.lib.sound;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.server.ServerUtils;
import com.marcusslover.plus.lib.task.Task;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
    public static Map<Audience, Music> playerMusic = new HashMap<>();
    public long ticks = 0;
    public int loops = 0;
    protected Note intro;
    protected long introLength;
    protected Note loop;
    protected long loopLength;
    protected Note tail;
    protected Task loopTask;
    protected Task tickTask;

    private Music(Note intro, long introLength, Note loop, long loopLength, Note tail) {
        this.intro = intro;
        this.introLength = introLength;
        this.loop = loop;
        this.loopLength = loopLength;
        this.tail = tail;
        this.loopTask = null;
        this.tickTask = Task.asyncRepeating(ServerUtils.getCallingPlugin(), () -> {
            ticks++;
        }, 1, 1);
    }

    /**
     * Creates a new music object.
     *
     * @param note       The note to play on loop.
     * @param noteLength The length of the note in ticks.
     * @return The music object.
     */
    public static @NotNull Music of(@NotNull Note note, long noteLength) {
        return new Music(null, 0, note, noteLength, null);
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
     * Stop music from playing to all audiences.
     */
    public static void stopAll() {
        for (Audience audience : Music.playerMusic.keySet()) {
            Music.playerMusic.get(audience).forceStop(audience);
        }
    }

    /**
     * Stop the music for an audience and play the tail if it exists.
     *
     * @param audience The audience.
     */
    public void stop(Audience audience) {
        if (!Music.playerMusic.containsKey(audience)) {
            return;
        }
        stopTasks();
        if (this.loops > 0) {
            Task.syncDelayed(ServerUtils.getCallingPlugin(), () -> {
                audience.stopSound(this.loop.sound);
                if (this.tail != null) {
                    this.tail.send(audience);
                }
            }, this.loopLength - (this.ticks % this.loopLength));
        } else {
            audience.stopSound(this.loop.sound);
            if (this.tail != null) {
                this.tail.send(audience);
            }
        }
        Music.playerMusic.remove(audience);
    }

    /**
     * Stop the music from the audience without playing the tail.
     *
     * @param audience The audience.
     */
    public void forceStop(Audience audience) {
        if (!Music.playerMusic.containsKey(audience)) {
            return;
        }
        stopTasks();
        if (this.intro != null) {
            audience.stopSound(this.intro().sound);
        }
        if (this.loop != null) {
            audience.stopSound(this.loop().sound);
        }
        if (this.tail != null) {
            audience.stopSound(this.tail().sound);
        }
        Music.playerMusic.remove(audience);
    }

    /**
     * Stops the music from looping again, but it will finish the current note.
     */
    public void stopTasks() {
        this.tickTask.cancel();
        this.loopTask.cancel();
    }

    @Override
    public @NotNull <T extends CommandSender> Music send(@NotNull T target) throws IllegalStateException {
        if (Music.playerMusic.containsKey(target)) {
            throw new IllegalStateException("Music is already playing for this target.");
        } else {
            Music.playerMusic.put(target, this);
        }
        if (this.introLength != 0 && this.intro != null) {
            this.intro.send(target);
            loopTask = Task.syncRepeating(ServerUtils.getCallingPlugin(), () -> {
                this.loop.send(target);
                this.loops++;
            }, this.introLength + 20, loopLength);
        } else {
            loopTask = Task.syncRepeating(ServerUtils.getCallingPlugin(), () -> {
                this.loop.send(target);
                this.loops++;
            }, 1, loopLength);
        }
        return this;
    }

    @Override
    public @NotNull Music send(Audience audience) throws IllegalStateException {
        if (Music.playerMusic.containsKey(audience)) {
            throw new IllegalStateException("Music is already playing for this audience.");
        } else {
            Music.playerMusic.put(audience, this);
        }
        if (this.introLength != 0 && this.intro != null) {
            this.intro.send(audience);
            loopTask = Task.syncRepeating(ServerUtils.getCallingPlugin(), () -> {
                this.loop.send(audience);
                this.loops++;
            }, this.introLength + 20, loopLength);
        } else {
            loopTask = Task.syncRepeating(ServerUtils.getCallingPlugin(), () -> {
                this.loop.send(audience);
                this.loops++;
            }, 1, loopLength);
        }
        return this;
    }

    /**
     * Set the intro note and length.
     *
     * @param intro       Note to play before the loop.
     * @param introLength Length of the intro in ticks.
     * @return The music object.
     */
    public @NotNull Music intro(@NotNull Note intro, long introLength) {
        this.intro = intro;
        this.introLength = introLength;
        return this;
    }

    /**
     * Set the loop note and length.
     *
     * @param loop       Note to play on loop.
     * @param loopLength Length of the loop in ticks.
     * @return The music object.
     */
    public @NotNull Music loop(@NotNull Note loop, long loopLength) {
        this.loop = loop;
        this.loopLength = loopLength;
        return this;
    }

    /**
     * Set the tail note and length.
     *
     * @param tail Note to play when stopped.
     * @return The music object.
     */
    public @NotNull Music tail(@NotNull Note tail) {
        this.tail = tail;
        return this;
    }
}
