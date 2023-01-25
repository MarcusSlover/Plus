package com.marcusslover.plus.lib.events.annotations;

import com.marcusslover.plus.lib.events.EventHandler;
import org.bukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark methods as being event handler methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
    /**
     * Whether the event is called async by bukkit.
     *
     * @return true if the event is called async
     */
    boolean async() default false;

    /**
     * The priority of the event handler method. The lower the value the sooner your method will be called.
     * Default value is <code>1000</code>
     *
     * @return The priority of the event handler method.
     */
    int priority() default 1000;

    /**
     * Whether the event handler method should ignore cancelled events.
     * @return true if the event handler method should ignore cancelled events.
     */
    boolean ignoreCancelled() default true;

    /**
     * If this event should be injected into the event bus.
     * Setting this to true will register a new event listener in bukkit and allow the {@link EventHandler}
     * to listen to the event.
     *
     * @return true if the event should be injected into the event bus.
     */
    boolean inject() default true;

    /**
     * The priority of the event listener.
     * @return The priority of the event listener.
     */
    EventPriority injectionPriority() default EventPriority.NORMAL;
}
