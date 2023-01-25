package com.marcusslover.plus.lib.events;

import com.destroystokyo.paper.event.executor.MethodHandleEventExecutor;
import com.destroystokyo.paper.util.SneakyThrow;
import com.marcusslover.plus.lib.events.annotations.Event;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.java.JavaPlugin;
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
    private static final String PREFIX = "[EventHandler] ";
    private static EventHandler instance;

    private final Map<Class<? extends org.bukkit.event.Event>, EventList> subscribers = new ConcurrentHashMap<>();

    private final Set<Class<? extends org.bukkit.event.Event>> injectedEvents = new HashSet<>();
    private JavaPlugin plugin;
    private Logger logger;

    public EventHandler() {
        instance = this;
    }

    public void subscribe(@NotNull EventListener observer) {
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
            return;
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
                this.getSubscribers(eventClass, eh.async())
                        .add(WrappedListener.of(observer, MethodHandles.lookup().unreflect(method), eh.priority(), eh.ignoreCancelled()));
            } catch (Throwable t) {
                SneakyThrow.sneaky(t);
            }

            /* Check for event injection */
            if (eh.inject() && !this.injectedEvents.contains(eventClass)) {
                /* Inject event into this event bus allowing this class to notify observers */
                try {
                    Method m = EventHandler.class.getMethod("notify", org.bukkit.event.Event.class);

                    Bukkit.getPluginManager().registerEvent(
                            eventClass,
                            this,
                            eh.injectionPriority(),
                            new MethodHandleEventExecutor(eventClass, MethodHandles.lookup().unreflect(m)),
                            this.plugin,
                            eh.async());

                    this.injectedEvents.add(eventClass);

                    this.logger.warning(PREFIX + "Injecting Event: " + eventClass.getName());
                } catch (Throwable t) {
                    SneakyThrow.sneaky(t);
                }
            }
        }
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
                    } catch (Throwable e) {
                        SneakyThrow.sneaky(e);
                    }
                });
    }

    /**
     * Unsubscribe all observing classes from the event.
     *
     * @param event The event to unsubscribe from.
     */
    public <T extends org.bukkit.event.Event> void unsubscribe(T event) {
        this.subscribers.remove(event.getClass());

        this.logger.warning(PREFIX + "Unsubscribing Event: " + event.getClass().getSuperclass().getName());
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

    private <T extends org.bukkit.event.Event> EventList getSubscribers(Class<T> eventClass, boolean async) {
        return this.subscribers.computeIfAbsent(eventClass, k -> new EventList(async));
    }

    /**
     * Register this event manager as a listener and allocate a java plugin as its manager.
     *
     * @param plugin The plugin to register this event manager with.
     */
    public EventHandler register(JavaPlugin plugin) {
        if (this.plugin != null) {
            this.logger.warning(PREFIX + "Event manager is already registered with " + this.plugin.getName() + "!");
            return this;
        }

        this.plugin = plugin;
        this.logger = plugin.getLogger();

        Bukkit.getPluginManager().registerEvents(this, plugin);

        return this;
    }

    public static EventHandler get() {
        return instance == null ? instance = new EventHandler() : instance;
    }
}
