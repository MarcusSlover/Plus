package com.marcusslover.plus.lib.events;

import com.marcusslover.plus.lib.events.annotations.Event;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
public class WrappedListener {
    private final EventListener listener;
    private final MethodHandle methodHandle;
    private final int priority;
    private final boolean ignoreCancelled;

    public static WrappedListener of(EventListener listener, MethodHandle methodHandle, int priority, boolean ignoreCancelled) {
        return new WrappedListener(listener, methodHandle, priority, ignoreCancelled);
    }

    public static <T extends org.bukkit.event.Event> WrappedListener of(EventReference<T> reference) {
        EventListener listener = new EventListener() {
            @Event
            public void onEvent(T event) {
                reference.handler().accept(event);
            }
        };

        MethodHandle methodHandle = null;

        try {
            methodHandle = getEventHandles(listener).stream().findFirst().get();
        } catch (Exception ignored) {
            // ignored
        }

        if (methodHandle == null) {
            throw new NullPointerException("Failed to get method handle for event listener.");
        }

        return new WrappedListener(listener, methodHandle, 1000 + reference.priority().getSlot(), reference.ignoreCancelled());
    }

    static @NotNull Set<MethodHandle> getEventHandles(EventListener listener) throws IllegalAccessException {
        Set<MethodHandle> handles = new HashSet<>();
        Set<Method> methods;

        Method[] publicMethods = listener.getClass().getMethods();
        Method[] privateMethods = listener.getClass().getDeclaredMethods();

        methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);

        methods.addAll(Arrays.asList(publicMethods));
        methods.addAll(Arrays.asList(privateMethods));

        for (final Method method : methods) {
            final Event eh = method.getAnnotation(Event.class);

            if (eh == null) {
                continue;
            }

            handles.add(getMethodHandle(method));
        }

        return handles;
    }

    static @Nullable MethodHandle getMethodHandle(Method method) throws IllegalAccessException {
        if (method.isBridge() || method.isSynthetic()) {
            return null;
        }

        if (method.getParameterTypes().length != 1 || !org.bukkit.event.Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
            return null;
        }

        method.setAccessible(true);

        return MethodHandles.lookup().unreflect(method);
    }
}
