package com.marcusslover.plus.lib.task;

import com.marcusslover.plus.lib.server.ServerUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.InvalidParameterException;
import java.util.function.Consumer;

/**
 * Simple utility for Bukkit scheduler tasks, essentially just shorthand
 *
 * @author Redempt
 */
public class Task {

    private final int task;
    /**
     * -- GETTER --
     *
     * @return The type of this Task
     */
    @Getter
    private final TaskType type;
    /**
     * -- GETTER --
     *
     * @return The Plugin which scheduled this task
     */
    @Getter
    private final Plugin plugin;

    private Task(int task, TaskType type, Plugin plugin) {
        this.task = task;
        this.type = type;
        this.plugin = plugin;
    }

    /**
     * Schedules a sync delayed task to run as soon as possible
     *
     * @param run The task to run
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task syncDelayed(Runnable run) {
        return syncDelayed(ServerUtils.getCallingPlugin(), run);
    }

    /**
     * Schedules a sync delayed task to run as soon as possible
     *
     * @param plugin The plugin scheduling the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task syncDelayed(Plugin plugin, Runnable run) {
        return syncDelayed(plugin, 0, run);
    }

    /**
     * Schedules a sync delayed task to run as soon as possible
     *
     * @param run The task to run
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task syncDelayed(Consumer<Task> run) {
        return syncDelayed(ServerUtils.getCallingPlugin(), run);
    }

    /**
     * Schedules a sync delayed task to run as soon as possible
     *
     * @param plugin The plugin scheduling the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task syncDelayed(Plugin plugin, Consumer<Task> run) {
        return syncDelayed(plugin, 0, run);
    }

    /**
     * Schedules a sync delayed task to run after a delay
     *
     * @param delay The delay in ticks to wait before running the task
     * @param run   The task to run
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task syncDelayed(long delay, Runnable run) {
        return syncDelayed(ServerUtils.getCallingPlugin(), delay, run);
    }

    /**
     * Schedules a sync delayed task to run after a delay
     *
     * @param plugin The plugin scheduling the task
     * @param delay  The delay in ticks to wait before running the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task syncDelayed(Plugin plugin, long delay, Runnable run) {
        checkRunnable(run);

        return syncDelayed(plugin, delay, t -> run.run());
    }

    /**
     * Schedules a sync delayed task to run after a delay
     *
     * @param delay The delay in ticks to wait before running the task
     * @param run   The task to run
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task syncDelayed(long delay, Consumer<Task> run) {
        return syncDelayed(ServerUtils.getCallingPlugin(), delay, run);
    }

    /**
     * Schedules a sync delayed task to run after a delay
     *
     * @param plugin The plugin scheduling the task
     * @param delay  The delay in ticks to wait before running the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task syncDelayed(Plugin plugin, long delay, Consumer<Task> run) {
        Task[] task = {null};
        task[0] = new Task(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> run.accept(task[0]), delay), TaskType.SYNC_DELAYED, plugin);
        return task[0];
    }

    /**
     * Schedules a sync repeating task to run later
     *
     * @param delay  The delay in ticks to wait before running the task
     * @param period The number of ticks between executions of the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task syncRepeating(long delay, long period, Runnable run) {
        return syncRepeating(ServerUtils.getCallingPlugin(), delay, period, run);
    }

    /**
     * Schedules a sync repeating task to run later
     *
     * @param plugin The plugin scheduling the task
     * @param delay  The delay in ticks to wait before running the task
     * @param period The number of ticks between executions of the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task syncRepeating(Plugin plugin, long delay, long period, Runnable run) {
        checkRunnable(run);

        return syncRepeating(plugin, delay, period, t -> run.run());
    }

    /**
     * Schedules a sync repeating task to run later
     *
     * @param delay  The delay in ticks to wait before running the task
     * @param period The number of ticks between executions of the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task syncRepeating(long delay, long period, Consumer<Task> run) {
        return syncRepeating(ServerUtils.getCallingPlugin(), delay, period, run);
    }

    /**
     * Schedules a sync repeating task to run later
     *
     * @param plugin The plugin scheduling the task
     * @param delay  The delay in ticks to wait before running the task
     * @param period The number of ticks between executions of the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task syncRepeating(Plugin plugin, long delay, long period, Consumer<Task> run) {
        Task[] task = {null};
        task[0] = new Task(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> run.accept(task[0]), delay, period), TaskType.SYNC_REPEATING, plugin);
        return task[0];
    }

    /**
     * Schedules an async delayed task to run as soon as possible
     *
     * @param run The task to run
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task asyncDelayed(Runnable run) {
        return asyncDelayed(ServerUtils.getCallingPlugin(), run);
    }

    /**
     * Schedules an async delayed task to run as soon as possible
     *
     * @param plugin The plugin scheduling the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task asyncDelayed(Plugin plugin, Runnable run) {
        checkRunnable(run);

        return asyncDelayed(plugin, 0, t -> run.run());
    }

    /**
     * Schedules an async delayed task to run as soon as possible
     *
     * @param run The task to run
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task asyncDelayed(Consumer<Task> run) {
        return asyncDelayed(ServerUtils.getCallingPlugin(), run);
    }

    /**
     * Schedules an async delayed task to run as soon as possible
     *
     * @param plugin The plugin scheduling the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task asyncDelayed(Plugin plugin, Consumer<Task> run) {
        return asyncDelayed(plugin, 0, run);
    }

    /**
     * Schedules an async delayed task to run after a delay
     *
     * @param delay The delay in ticks to wait before running the task
     * @param run   The task to run
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task asyncDelayed(long delay, Runnable run) {
        return asyncDelayed(ServerUtils.getCallingPlugin(), delay, run);
    }

    /**
     * Schedules an async delayed task to run after a delay
     *
     * @param plugin The plugin scheduling the task
     * @param delay  The delay in ticks to wait before running the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    public static Task asyncDelayed(Plugin plugin, long delay, Runnable run) {
        checkRunnable(run);

        return asyncDelayed(plugin, delay, t -> run.run());
    }

    /**
     * Schedules an async delayed task to run after a delay
     *
     * @param delay The delay in ticks to wait before running the task
     * @param run   The task to run
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task asyncDelayed(long delay, Consumer<Task> run) {
        return asyncDelayed(ServerUtils.getCallingPlugin(), delay, run);
    }

    /**
     * Schedules an async delayed task to run after a delay
     *
     * @param plugin The plugin scheduling the task
     * @param delay  The delay in ticks to wait before running the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task asyncDelayed(Plugin plugin, long delay, Consumer<Task> run) {
        Task[] task = {null};
        task[0] = new Task(Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> run.accept(task[0]), delay), TaskType.ASYNC_DELAYED, plugin);
        return task[0];
    }

    /**
     * Schedules an async repeating task to run later
     *
     * @param delay  The delay in ticks to wait before running the task
     * @param period The number of ticks between executions of the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task asyncRepeating(long delay, long period, Consumer<Task> run) {
        return asyncRepeating(ServerUtils.getCallingPlugin(), delay, period, run);
    }

    /**
     * Schedules an async repeating task to run later
     *
     * @param plugin The plugin scheduling the task
     * @param delay  The delay in ticks to wait before running the task
     * @param period The number of ticks between executions of the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    @Deprecated(forRemoval = true)
    public static Task asyncRepeating(Plugin plugin, long delay, long period, Consumer<Task> run) {
        Task[] task = {null};
        task[0] = new Task(Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> run.accept(task[0]), delay, period), TaskType.ASYNC_REPEATING, plugin);
        return task[0];
    }

    /**
     * Schedules an async repeating task to run later
     *
     * @param delay  The delay in ticks to wait before running the task
     * @param period The number of ticks between executions of the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @return The Task that has been scheduled
     */
    @Deprecated
    public static Task asyncRepeating(long delay, long period, Runnable run) {
        return asyncRepeating(ServerUtils.getCallingPlugin(), delay, period, run);
    }

    /**
     * Schedules an async repeating task to run later
     *
     * @param plugin The plugin scheduling the task
     * @param run    The task to run - Do not use bukkit runnable. (Cancelling the bukkit runnable will not cancel the created task)
     * @param delay  The delay in ticks to wait before running the task
     * @param period The number of ticks between executions of the task
     * @return The Task that has been scheduled
     */
    public static Task asyncRepeating(Plugin plugin, long delay, long period, Runnable run) {
        return asyncRepeating(plugin, delay, period, t -> run.run());
    }

    /**
     * Throws an exception if the runnable is of type {@link BukkitRunnable}
     */
    private static void checkRunnable(Runnable run) {
        if (run instanceof BukkitRunnable) {
            throw new InvalidParameterException("Cannot schedule a BukkitRunnable as a Task please use \"() -> {}\" or \"new Runnable() {}\"... or crazy idea just use the consumer method.");
        }
    }

    /**
     * @return Whether this Task is queued, same as {@link org.bukkit.scheduler.BukkitScheduler#isQueued(int)}
     */
    public boolean isQueued() {
        return Bukkit.getScheduler().isQueued(this.task);
    }

    /**
     * @return Whether this Task is currently running, same as {@link org.bukkit.scheduler.BukkitScheduler#isCurrentlyRunning(int)}
     */
    public boolean isCurrentlyRunning() {
        return Bukkit.getScheduler().isCurrentlyRunning(this.task);
    }

    /**
     * Cancels this task, same as {@link org.bukkit.scheduler.BukkitScheduler#cancelTask(int)}
     */
    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.task);
    }

    /**
     * Represents a type of task
     */
    public enum TaskType {

        SYNC_DELAYED, ASYNC_DELAYED, SYNC_REPEATING, ASYNC_REPEATING

    }
}