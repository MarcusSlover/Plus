package com.marcusslover.plus.lib.sound;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.server.ServerUtils;
import com.marcusslover.plus.lib.task.Task;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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
public class Music implements ISendable<Music> {
	protected final Note intro;
	protected final long introLength;
	protected final Note loop;
	protected final long loopLength;
	protected final Note tail;

	protected Task loopTask = null;
	public long ticks = 0;
	protected Task tickTask = null;
	public int loops = 0;

 private Music(Note intro, long introLength, Note length, long loopLength, Note tail) {
  this.intro = intro;
  this.introLength = introLength;
  this.loop = loop;
  this.loopLength = loopLength;
  this.tail = tail;
  this.tickTask = Task.asyncRepeating(ServerUtils.getCallingPlugin(), () -> {
		  ticks++;
	 }, 1, 1);
 }


	/**
	 * Stop the music for an audience and play the tail if it exists.
	 *
	 * @param audience The audience.
	 */
	public void stop(Audience audience) {
		this.tickTask.cancel();
		this.loopTask.cancel();
		// If we are playing the loop sound, then wait until its finished before then playing the tail.
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
	}

	/**
	 * Stop the music for a command sender and play the tail if it exists.
	 *
	 * @param target The command sender.
	 */
	public void stop(CommandSender target) {
		this.tickTask.cancel();
		this.loopTask.cancel();
		if (this.loops > 0) {
			Task.syncDelayed(ServerUtils.getCallingPlugin(), () -> {
				target.stopSound(this.loop.sound);
				if (this.tail != null) {
					this.tail.send(target);
				}
			}, this.loopLength - (this.ticks % this.loopLength) - 1);
		} else {
			target.stopSound(this.loop.sound);
			if (this.tail != null) {
				this.tail.send(target);
			}
		}
	}

	/**
	 * Stop the music from the audience without playing the tail.
	 *
	 * @param audience The audience.
	 */
	public void stopAll(Audience audience) {
		this.tickTask.cancel();
		this.loopTask.cancel();
		if (this.intro != null) {
			audience.stopSound(this.intro().sound);}
		if (this.loop != null){
			audience.stopSound(this.loop().sound);
		}
		if (this.tail != null){
			audience.stopSound(this.tail().sound);
		}
	}

	/**
	 * Stop the music from the command sender without playing the tail.
	 *
	 * @param target The command sender.
	 */
	public void stopAll(CommandSender target) {
		this.tickTask.cancel();
		this.loopTask.cancel();
		if (this.intro != null)
			target.stopSound(this.intro().sound);
		if (this.loop != null)
			target.stopSound(this.loop().sound);
		if (this.tail != null)
			target.stopSound(this.tail().sound);
	}

	@Override
	public @NotNull <T extends CommandSender> Music send(@NotNull T target) {
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
	public @NotNull Music send(Audience audience) {
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
	 * Creates a new music object.
	 *
	 * @param note The note to play on loop.
	 * @param noteLength The length of the note in ticks.
	 * @return The music object.
	 */
	public static @NotNull Music of(@NotNull Note note, long noteLength) {
		return new Music(null, 0, note, noteLength, null);
	}

	/**
	 * Create a new music object with an intro and tail note.
	 *
	 * @param intro Note to play before the loop.
	 * @param introLength Length of the intro in ticks.
	 * @param loop Note to play on loop.
	 * @param loopLength Length of the loop in ticks.
	 * @param tail Note to play when stopped.
	 * @return The music object.
	 */
	public static @NotNull Music of (@NotNull Note intro, long introLength, @NotNull Note loop, long loopLength, @NotNull Note tail) {
		return new Music(intro, introLength, loop, loopLength, tail);
	}

	/**
	 * Set the intro note and length.
	 *
	 * @param intro Note to play before the loop.
	 * @param introLength Length of the intro in ticks.
	 * @return The music object.
	 */
	public @NotNull Music intro(@NotNull Note intro, long introLength) {
		return new Music(intro, introLength, this.loop, this.loopLength, this.tail);
	}

	/**
	 * Set the loop note and length.
	 *
	 * @param loop Note to play on loop.
	 * @param loopLength Length of the loop in ticks.
	 * @return The music object.
	 */
	public @NotNull Music loop(@NotNull Note loop, long loopLength) {
		return new Music(this.intro, this.introLength, loop, loopLength, this.tail);
	}

	/**
	 * Set the tail note and length.
	 *
	 * @param tail Note to play when stopped.
	 * @return The music object.
	 */
	public @NotNull Music tail(@NotNull Note tail) {
		return new Music(this.intro, this.introLength, this.loop, this.loopLength, tail);
	}
}
