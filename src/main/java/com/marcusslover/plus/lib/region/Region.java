package com.marcusslover.plus.lib.region;

import com.marcusslover.plus.lib.world.WorldPoint;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a 2d region with two points.
 * The minimum point is the point with the lowest x, y and z values.
 * The maximum point is the point with the highest x, y and z values.
 */
@Data
@Accessors(fluent = true, chain = true)
public class Region implements IRegion {
    private final @NotNull Vector a;
    private final @NotNull Vector b;
    private int priority; // Priority of the region.

    public static @NotNull Region of(@NotNull Vector a, @NotNull Vector b) {
        return new Region(a, b);
    }

    public static @NotNull Region of(@NotNull Vector a, @NotNull Vector b, int priority) {
        return new Region(a, b).priority(priority);
    }

    public static @NotNull Region of(@NotNull Location a, @NotNull Location b) {
        return new Region(a.toVector(), b.toVector());
    }

    public static @NotNull Region of(@NotNull Location a, @NotNull Location b, int priority) {
        return new Region(a.toVector(), b.toVector()).priority(priority);
    }

    public static @NotNull Region of(@NotNull Entity a, @NotNull Entity b) {
        return new Region(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static @NotNull Region of(@NotNull Entity a, @NotNull Entity b, int priority) {
        return new Region(a.getLocation().toVector(), b.getLocation().toVector()).priority(priority);
    }

    public static @NotNull Region of(@NotNull WorldPoint a, @NotNull WorldPoint b) {
        return new Region(a.toVector(), b.toVector());
    }

    public static @NotNull Region of(@NotNull WorldPoint a, @NotNull WorldPoint b, int priority) {
        return new Region(a.toVector(), b.toVector()).priority(priority);
    }

    /**
     * Gets the minimum point of the region.
     *
     * @return The minimum point of the region.
     */
    @Override
    public @NotNull Vector min() {
        return Vector.getMinimum(this.a, this.b);
    }

    /**
     * Gets the maximum point of the region.
     *
     * @return The maximum point of the region.
     */
    @Override
    public @NotNull Vector max() {
        return Vector.getMaximum(this.a, this.b);
    }
}
