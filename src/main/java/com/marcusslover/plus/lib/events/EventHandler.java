package com.marcusslover.plus.lib.events;

import com.destroystokyo.paper.event.executor.MethodHandleEventExecutor;
import com.destroystokyo.paper.util.SneakyThrow;
import com.marcusslover.plus.lib.events.annotations.Event;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventHandler implements Listener {
    private static final String PREFIX = "EventHandler";
    private static final Map<Plugin, EventHandler> handlers = new ConcurrentHashMap<>();

    private final Map<Class<? extends org.bukkit.event.Event>, EventList> subscribers = new ConcurrentHashMap<>();
    private final Map<EventListener, Set<EventList>> referenceMap = new ConcurrentHashMap<>();

    private final Set<Class<? extends org.bukkit.event.Event>> injectedEvents = new HashSet<>();
    private final Plugin plugin;
    private final Logger logger;
    private Method notifyMethod;

    public EventHandler(Plugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger(plugin.getName().concat("@" + PREFIX));
        handlers.put(plugin, this);
    }

    /**
     * Register the event.
     *
     * @param observer the event to register
     * @return the event
     */
    public EventListener subscribe(@NotNull EventListener observer) {
        if (this.plugin == null) {
            throw new IllegalStateException("Make sure to register the EventHandler before subscribing to events!");
        }

        Set<Method> methods;
        try {
            Method[] publicMethods = observer.getClass().getMethods();
            Method[] privateMethods = observer.getClass().getDeclaredMethods();

            methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);

            methods.addAll(Arrays.asList(publicMethods));
            methods.addAll(Arrays.asList(privateMethods));
        } catch (NoClassDefFoundError e) {
            this.logger.severe(PREFIX + "Failed to register events for " + observer.getClass() + " because " + e.getMessage() + " does not exist.");
            return observer;
        }

        for (final Method method : methods) {
            final Event eh = method.getAnnotation(Event.class);
            if (eh == null) {
                continue;
            }
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !org.bukkit.event.Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                this.logger.severe(PREFIX + "attempted to register an invalid CustomEvents method signature \"" + method.toGenericString() + "\" in " + observer.getClass());
                continue;
            }
            final Class<? extends org.bukkit.event.Event> eventClass = checkClass.asSubclass(org.bukkit.event.Event.class);
            method.setAccessible(true);
            for (Class<?> clazz = eventClass; org.bukkit.event.Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    Warning warning = clazz.getAnnotation(Warning.class);
                    Warning.WarningState warningState = Bukkit.getServer().getWarningState();
                    if (!warningState.printFor(warning)) {
                        break;
                    }
                    this.logger.log(
                            Level.WARNING,
                            String.format(
                                    "%s\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated. \"%s\"; please notify the authors %s.",
                                    PREFIX,
                                    this.plugin.getDescription().getFullName(),
                                    clazz.getName(),
                                    method.toGenericString(),
                                    (warning != null && warning.reason().length() != 0) ? warning.reason() : "Server performance will be affected",
                                    Arrays.toString(this.plugin.getDescription().getAuthors().toArray())),
                            warningState == Warning.WarningState.ON ? new AuthorNagException(null) : null);
                    break;
                }
            }

            try {
                var list = this.getSubscribers(eventClass, eh.async());

                list.add(WrappedListener.of(observer, MethodHandles.lookup().unreflect(method), eh.priority(), eh.ignoreCancelled()));
                /* Easy link for the event listener to access the EventList it is part of */
                this.referenceMap.computeIfAbsent(observer, k -> new HashSet<>()).add(list);
            } catch (Exception e) {
                SneakyThrow.sneaky(e);
            }

            /* Check for event injection */
            if (eh.inject() && !this.injectedEvents.contains(eventClass)) {
                /* Inject event into this event bus allowing this class to notify observers */
                try {
                    if (this.notifyMethod == null) {
                        this.notifyMethod = this.getClass().getMethod("notify", org.bukkit.event.Event.class);
                    }

                    Bukkit.getPluginManager().registerEvent(
                            eventClass,
                            this,
                            eh.injectionPriority(),
                            new MethodHandleEventExecutor(eventClass, MethodHandles.lookup().unreflect(this.notifyMethod)),
                            this.plugin,
                            eh.async());

                    this.injectedEvents.add(eventClass);

                    this.logger.warning(PREFIX + "Injecting Event: " + eventClass.getName());
                } catch (Exception e) {
                    SneakyThrow.sneaky(e);
                }
            }
        }

        return observer;
    }

    public <T extends org.bukkit.event.Event> EventListener subscribe(EventReference<T> reference) {
        EventListener registeredEvent = null;

        switch (reference.priority()) {
            case LOWEST -> registeredEvent = this.subscribe(new EventListener() {
                @Event(injectionPriority = EventPriority.LOWEST)
                public void onEvent(T event) {
                    if (reference.handler() != null) {
                        reference.handler().accept(event);
                    }
                }
            });
            case LOW -> registeredEvent = this.subscribe(new EventListener() {
                @Event(injectionPriority = EventPriority.LOW)
                public void onEvent(T event) {
                    if (reference.handler() != null) {
                        reference.handler().accept(event);
                    }
                }
            });
            case NORMAL -> registeredEvent = this.subscribe(new EventListener() {
                @Event(injectionPriority = EventPriority.NORMAL)
                public void onEvent(T event) {
                    if (reference.handler() != null) {
                        reference.handler().accept(event);
                    }
                }
            });
            case HIGH -> registeredEvent = this.subscribe(new EventListener() {
                @Event(injectionPriority = EventPriority.HIGH)
                public void onEvent(T event) {
                    if (reference.handler() != null) {
                        reference.handler().accept(event);
                    }
                }
            });
            case HIGHEST -> registeredEvent = this.subscribe(new EventListener() {
                @Event(injectionPriority = EventPriority.HIGHEST)
                public void onEvent(T event) {
                    if (reference.handler() != null) {
                        reference.handler().accept(event);
                    }
                }
            });
            case MONITOR -> registeredEvent = this.subscribe(new EventListener() {
                @Event(injectionPriority = EventPriority.MONITOR)
                public void onEvent(T event) {
                    if (reference.handler() != null) {
                        reference.handler().accept(event);
                    }
                }
            });
        }

        return registeredEvent;
    }

    /**
     * This will notify all observers of the event as well as create an observer mapping if one is not already cached.
     *
     * @param event The event to notify observers of.
     * @param <T>   The type of event.
     */
    public <T extends org.bukkit.event.Event> void notify(T event) {
        this.getSubscribers(event)
                .sort()
                .forEach(wrapped -> {
                    try {
                        var handler = wrapped.getMethodHandle();
                        var listener = wrapped.getListener();
                        var ignoreCancelled = wrapped.isIgnoreCancelled();

                        if (event instanceof Cancellable cancellable) {
                            if (cancellable.isCancelled() && !ignoreCancelled) {
                                return;
                            }
                        }

                        handler.invoke(listener, event);
                    } catch (Throwable t) {
                        SneakyThrow.sneaky(t);
                    }
                });
    }

    /**
     * Unsubscribe all observing classes from the event.
     *
     * @param event The event to unsubscribe from.
     */
    public <T extends org.bukkit.event.Event> void unsubscribe(Class<T> event) {
        this.subscribers.remove(event);

        this.logger.warning(PREFIX + "Unsubscribing Event: " + event.getCanonicalName());
    }

    /**
     * Unsubscribe the given listener from all events.
     *
     * @param registeredListener The listener to unsubscribe.
     */
    public void unsubscribe(EventListener registeredListener) {
        if (this.referenceMap.containsKey(registeredListener)) {
            this.referenceMap.get(registeredListener).forEach(list -> list.remove(registeredListener));
        }
    }

    /**
     * Returns a collection of all registered event classes.
     *
     * @return The registered event classes.
     */
    public Collection<Class<? extends org.bukkit.event.Event>> getSubscribedEvents() {
        return this.subscribers.keySet();
    }

    /**
     * Obtain all observers for the given event.
     *
     * @param event The event to obtain observers for.
     * @return The observer list.
     */
    public <T extends org.bukkit.event.Event> EventList getSubscribers(T event) {
        return this.getSubscribers(event.getClass(), event.isAsynchronous());
    }

    <T extends org.bukkit.event.Event> EventList getSubscribers(Class<T> eventClass, boolean async) {
        return this.subscribers.computeIfAbsent(eventClass, k -> new EventList(async));
    }

    /**
     * Register this event manager as a listener and allocate a plugin as its manager.
     *
     * @param plugin The plugin to register this event manager with.
     * @return The event manager.
     */
    public static EventHandler get(Plugin plugin) {
        handlers.computeIfAbsent(plugin, EventHandler::new);

        return handlers.get(plugin);
    }
}
