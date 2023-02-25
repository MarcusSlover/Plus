package com.marcusslover.plus.lib.region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface IRegion {
    @NotNull Vector min();

    @NotNull Vector max();

    default boolean within(@NotNull Entity entity) {
        return this.within(entity.getLocation());
    }

    default boolean within(@NotNull Entity entity, boolean excludeY) {
        return this.within(entity.getLocation(), excludeY);
    }

    default boolean within(@NotNull Vector vector) {
        return this.within(vector, VectorStrategy.FULL);
    }

    default boolean within(@NotNull Vector vector, boolean excludeY) {
        return this.within(vector, excludeY ? VectorStrategy.EXCLUDE_Y : VectorStrategy.FULL);
    }

    default boolean within(@NotNull Location location) {
        return this.within(location.toVector());
    }

    default boolean within(@NotNull Location location, boolean excludeY) {
        return this.within(location.toVector(), excludeY);
    }

    default boolean within(@NotNull Vector vector, @NotNull VectorStrategy strategy) {
        Vector min = this.min();
        Vector max = this.max();
        return this.within(vector, min, max, strategy);
    }

    default boolean within(@NotNull Vector vector, @NotNull Vector min, @NotNull Vector max, @NotNull VectorStrategy strategy) {
        if (strategy == VectorStrategy.FULL) {
            return vector.isInAABB(min, max);
        } else {
            return vector.getX() >= min.getX() && vector.getX() <= max.getX() && vector.getZ() >= min.getZ() && vector.getZ() <= max.getZ();
        }
    }

    enum VectorStrategy {
        FULL,
        EXCLUDE_Y
    }
}
