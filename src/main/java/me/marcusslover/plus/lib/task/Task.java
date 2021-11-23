package me.marcusslover.plus.lib.task;

public class Task {
    protected final Runnable runnable;

    public Task(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
