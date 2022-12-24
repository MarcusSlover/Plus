package com.marcusslover.plus.lib.container.extra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate your custom container class with this annotation to toggle
 * whether you want your container to load all the data upon plugin enabling process.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InitialLoading {

    /**
     * Toggle whether this container's data should be loaded on start.
     *
     * @return True if it should autoload, false if it shouldn't load.
     */
    boolean value() default true;
}

