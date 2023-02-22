package com.marcusslover.plus.lib.events;

import com.marcusslover.plus.lib.common.ReadWriteLock;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@AllArgsConstructor
public class EventList {
    final List<WrappedListener> observers = new CopyOnWriteArrayList<>();
    private final ReadWriteLock lock = new ReadWriteLock();
    private boolean isAsync;

    public void add(WrappedListener listener) {
        this.observers.add(listener);
    }

    public void remove(WrappedListener listener) {
        this.observers.remove(listener);
    }

    void forEach(Consumer<WrappedListener> action) {
        Objects.requireNonNull(action);
        for (var t : this.observers) {
            action.accept(t);
        }
    }

    public EventList sort() {
        if (this.isAsync) {
            /* Read Operations */
            this.lock.readLock();
            var sort = new java.util.ArrayList<>(this.observers);
            this.lock.readUnlock();

            sort.sort(Comparator.comparingInt(WrappedListener::getPriority));

            /* Write Operations */
            this.lock.writeLock();
            this.observers.clear();
            this.observers.addAll(sort);
            this.lock.writeUnlock();
        } else {
            this.observers.sort(Comparator.comparingInt(WrappedListener::getPriority));
        }

        return this;
    }
}
