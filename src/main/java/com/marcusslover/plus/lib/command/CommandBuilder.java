package com.marcusslover.plus.lib.command;

/**
 * Allows you to create a command.
 */
public class CommandBuilder {
    private boolean empty = true;

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isEmpty() {
        return this.empty;
    }
}
