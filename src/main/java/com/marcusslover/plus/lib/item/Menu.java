package com.marcusslover.plus.lib.item;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a menu registry that holds all the menus.
 * Each canvas is different and belongs to a player.
 */
@Data
@Accessors(fluent = true, chain = true)
public abstract class Menu implements IMenu {
    private final @NotNull Map<UUID, Canvas> canvasMap = new HashMap<>(); // player -> canvas
    private @Nullable String id; // menu id, not required
}
