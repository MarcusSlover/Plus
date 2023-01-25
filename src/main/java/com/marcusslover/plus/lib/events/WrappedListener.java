package com.marcusslover.plus.lib.events;

import lombok.Data;

import java.lang.invoke.MethodHandle;

@Data(staticConstructor = "of")
public class WrappedListener {
    private final EventListener listener;
    private final MethodHandle methodHandle;
    private final int priority;
    private final boolean ignoreCancelled;
}
