package com.marcusslover.plus.lib.sound;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.server.ServerUtils;
import com.marcusslover.plus.lib.task.Task;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
    /** Pointer which checks if the player has music playing. */
    @Getter public static final Pointer<Boolean> MUSIC_PLAYING = Pointer.pointer(Boolean.class, Key.key("music_playing"));
    protected final Map<Audience, Session> sessions = new HashMap<>();
    /** The intro note to play before the loop. */
    protected Note intro;
    /** The length of the intro in ticks. */
    protected long introLength;
    /** The note to play on loop. */
    protected Note loop;
    /** The length of the loop in ticks. */
    protected long loopLength;
    /** The note to play when stopped. */
    protected Note tail;


    private Music(Note intro, long introLength, Note loop, long loopLength, Note tail) {
        this.intro = intro;
        this.introLength = introLength;
        this.loop = loop;
        this.loopLength = loopLength;
        this.tail = tail;
    }

    /**
     * Creates a new music object.
     *
     * @param note       The note to play on loop.
     * @param noteLength The length of the note in ticks.
     * @return The music object.
     */
    public static @NotNull Music of(@NotNull Note note, long noteLength, @NotNull Audience audience) {
        Music music = new Music(null, 0, note, noteLength, null);;
        music.sessions.put(audience, music.createSession(audience));
        return music;
    }

    private Session createSession(Audience audience) {
        return new Session(System.currentTimeMillis(), (audience1) -> (session) -> {
                session.audience(audience1);
                if (session.stopped) {
                    return null;
                }
                if (this.introLength != 0 && this.intro != null) {
                    this.intro.send(audience1);
                    return Task.syncRepeating(ServerUtils.getCallingPlugin(), () -> {
                            this.loop.send(audience1);
                            audience1.getOrDefault(MUSIC_PLAYING, true);
                            session.incrementLoops();
                    }, this.introLength, loopLength);
                } else {
                    return Task.syncRepeating(ServerUtils.getCallingPlugin(), () -> {
                        this.loop.send(audience1);
                        audience1.getOrDefault(MUSIC_PLAYING, true);
                        session.incrementLoops();
                    }, 1, loopLength);
                }
        }, audience);
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
    public static @NotNull Music of(@NotNull Note intro, long introLength, @NotNull Note loop, long loopLength, @NotNull Note tail, @NotNull Audience audience) {
        Music music = new Music(intro, introLength, loop, loopLength, tail);
        music.sessions.put(audience, music.createSession(audience));
        return music;
    }

    /**
     * Stop the music for an audience and play the tail if it exists.
     */
    public void stop(Session session) {
        if (session.loops() > 0) {
            Task.syncDelayed(ServerUtils.getCallingPlugin(), () -> {
                session.stopSound(this.loop);
                session.audience().getOrDefault(MUSIC_PLAYING, false);
                if (this.tail != null) {
                    this.tail.send(session.audience);
                }
            }, this.loopLength - (session.ticks() % this.loopLength));
        } else {
            session.stopSound(this.loop);
            if (this.tail != null) {
                this.tail.send(session.audience);
            }
        }
    }

    /**
     * Stop the music from the audience without playing the tail.
     *
     * @param audience The audience.
     */
    public void forceStop(Audience audience) {
        this.sessions().get(audience).stop();
        if (this.intro() != null) {
            audience.stopSound(this.intro().sound);
        }
        if (this.loop() != null) {
            audience.stopSound(this.loop().sound);
        }
        if (this.tail() != null) {
            audience.stopSound(this.tail().sound);
        }
    }

    @Override
    public @NotNull <T extends CommandSender> Music send(@NotNull T target) {
        this.sessions().get(target).start();
        return this;
    }

    @Override
    public @NotNull Music send(Audience audience) {
        this.sessions().get(audience).start();
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

    public Music playAllSessions() {
        this.sessions().forEach((audience, session) -> session.start());
        return this;
    }


    private static class Session {
        @Getter private final long startTime;
        @Getter @Setter private long ticks = 0;
        @Getter private final Task tickTask;
        @Getter private final Task loopTask;
        @Getter @Setter private int loops = 0;
        @Getter @Setter private boolean stopped = true;
        @Getter @Setter private Audience audience;

        protected Session(long startTime, BiFunction<Audience, Session, Task> loopTask, Audience audience) {
            this.startTime = startTime;
            this.tickTask = Task.syncRepeating(ServerUtils.getCallingPlugin(), this::tick, 1, 1);
            this.loopTask = loopTask.apply(audience).apply(this);
            this.audience = audience;
        }

        protected void tick() {
            if (!this.stopped) {
                this.ticks++;
            }
        }

        protected void incrementLoops() {
            if (!this.stopped) {
                this.loops++;
            }
        }

        protected void stop() {
            this.stopped(true);
            this.tickTask.cancel();
            if (this.loopTask != null) {
                this.loopTask.cancel();
            }
        }

        protected void stopSound(Note note) {
            this.audience.stopSound(note.sound);
        }

        protected void start() {
            this.stopped(false);
        }
    }
}
