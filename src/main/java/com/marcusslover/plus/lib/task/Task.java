package com.marcusslover.plus.lib.task;

import org.jetbrains.annotations.NotNull;

public class Task {
    protected final Runnable runnable;

    public Task(@NotNull Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
