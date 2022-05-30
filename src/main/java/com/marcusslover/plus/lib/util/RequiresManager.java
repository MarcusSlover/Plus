package com.marcusslover.plus.lib.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Requires a manager to be registered by some plugin to use it.
 */
@Target(ElementType.TYPE)
public @interface RequiresManager {
}
