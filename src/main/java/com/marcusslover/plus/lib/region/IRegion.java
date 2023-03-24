package com.marcusslover.plus.lib.region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a 2d region with two points.
 */
public interface IRegion {
    /**
     * Gets the minimum point of the region.
     *
     * @return The minimum point of the region.
     */
    @NotNull Vector min();

    /**
     * Gets the maximum point of the region.
     *
     * @return The maximum point of the region.
     */
    @NotNull Vector max();

    /**
     * Gets the minimum x value of the region.
     *
     * @return The minimum x value of the region.
     */
    default double getMinX() {
        return this.min().getX();
    }

    /**
     * Gets the minimum y value of the region.
     *
     * @return The minimum y value of the region.
     */
    default double getMinY() {
        return this.min().getY();
    }

    /**
     * Gets the minimum z value of the region.
     *
     * @return The minimum z value of the region.
     */
    default double getMinZ() {
        return this.min().getZ();
    }

    /**
     * Gets the maximum x value of the region.
     *
     * @return The maximum x value of the region.
     */
    default double getMaxX() {
        return this.max().getX();
    }

    /**
     * Gets the maximum y value of the region.
     *
     * @return The maximum y value of the region.
     */
    default double getMaxY() {
        return this.max().getY();
    }

    /**
     * Gets the maximum z value of the region.
     *
     * @return The maximum z value of the region.
     */
    default double getMaxZ() {
        return this.max().getZ();
    }

    /**
     * Check if the given entity is within the region.
     *
     * @param entity The entity to check.
     * @return True if the entity is within the region.
     */
    default boolean within(@NotNull Entity entity) {
        return this.within(entity.getLocation());
    }

    /**
     * Check if the given entity is within the region.
     * Add the excludeY parameter to exclude the y-axis check.
     *
     * @param entity   The entity to check.
     * @param excludeY Exclude the y-axis check.
     * @return True if the entity is within the region.
     */
    default boolean within(@NotNull Entity entity, boolean excludeY) {
        return this.within(entity.getLocation(), excludeY);
    }

    /**
     * Check if the given vector is within the region.
     *
     * @param vector The vector to check.
     * @return True if the vector is within the region.
     */
    default boolean within(@NotNull Vector vector) {
        return this.within(vector, DefaultVectorStrategy.FULL);
    }

    /**
     * Check if the given vector is within the region.
     * Add the excludeY parameter to exclude the y-axis check.
     *
     * @param vector   The vector to check.
     * @param excludeY Exclude the y-axis check.
     * @return True if the vector is within the region.
     */
    default boolean within(@NotNull Vector vector, boolean excludeY) {
        return this.within(vector, excludeY ? DefaultVectorStrategy.EXCLUDE_Y : DefaultVectorStrategy.FULL);
    }

    /**
     * Check if the given location is within the region.
     *
     * @param location The location to check.
     * @return True if the location is within the region.
     */
    default boolean within(@NotNull Location location) {
        return this.within(location.toVector());
    }

    /**
     * Check if the given location is within the region.
     * Add the excludeY parameter to exclude the y-axis check.
     *
     * @param location The location to check.
     * @param excludeY Exclude the y-axis check.
     * @return True if the location is within the region.
     */
    default boolean within(@NotNull Location location, boolean excludeY) {
        return this.within(location.toVector(), excludeY);
    }

    /**
     * Check if the given vector is within the region.
     * Add the strategy parameter to customize the check more briefly.
     *
     * @param vector   The vector to check.
     * @param strategy The strategy to use.
     * @return True if the vector is within the region.
     */
    default boolean within(@NotNull Vector vector, @NotNull IRegion.DefaultVectorStrategy strategy) {
        Vector min = this.min();
        Vector max = this.max();
        return this.within(vector, min, max, strategy);
    }

    /**
     * Check if the given vector is within the region.
     * Add the strategy parameter to customize the check more briefly.
     *
     * @param vector   The vector to check.
     * @param min      The minimum vector.
     * @param max      The maximum vector.
     * @param strategy The strategy to use.
     * @return True if the vector is within the region.
     */
    default boolean within(@NotNull Vector vector, @NotNull Vector min, @NotNull Vector max, @NotNull IRegion.DefaultVectorStrategy strategy) {
        if (strategy == DefaultVectorStrategy.FULL) {
            return vector.isInAABB(min, max);
        } else {
            return vector.getX() >= min.getX() && vector.getX() <= max.getX() && vector.getZ() >= min.getZ() && vector.getZ() <= max.getZ();
        }
    }

    /**
     * All default strategies for the vector check.
     */
    enum DefaultVectorStrategy implements IVectorStrategy {
        /**
         * Checking if all the axis are within the region.
         */
        FULL(Vector::isInAABB), // Minecraft's method.
        /**
         * Checking if the x and z axis are within the region.
         */
        EXCLUDE_Y((vector, min, max) -> vector.getX() >= min.getX() && vector.getX() <= max.getX() && vector.getZ() >= min.getZ() && vector.getZ() <= max.getZ()),
        /**
         * Checking if the x and y axis are within the region.
         */
        EXCLUDE_Z((vector, min, max) -> vector.getX() >= min.getX() && vector.getX() <= max.getX() && vector.getY() >= min.getY() && vector.getY() <= max.getY()),
        /**
         * Checking if the y and z axis are within the region.
         */
        EXCLUDE_X((vector, min, max) -> vector.getY() >= min.getY() && vector.getY() <= max.getY() && vector.getZ() >= min.getZ() && vector.getZ() <= max.getZ()),
        /**
         * Checking if only the x axis is within the region.
         */
        ONLY_X((vector, min, max) -> vector.getX() >= min.getX() && vector.getX() <= max.getX()),
        /**
         * Checking if only the y axis is within the region.
         */
        ONLY_Y((vector, min, max) -> vector.getY() >= min.getY() && vector.getY() <= max.getY()),
        /**
         * Checking if only the z axis is within the region.
         */
        ONLY_Z((vector, min, max) -> vector.getZ() >= min.getZ() && vector.getZ() <= max.getZ());

        private final IVectorStrategy strategy;

        DefaultVectorStrategy(IVectorStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public boolean within(Vector vector, Vector min, Vector max) {
            return this.strategy.within(vector, min, max); // call the strategy
        }
    }

    /**
     * Represents a vector strategy.
     * This is used to check if the vector is within the region.
     * Implement this interface to create your own strategy.
     */
    interface IVectorStrategy {
        /**
         * Check if the given vector is within the region.
         *
         * @param vector The vector to check.
         * @param min    The minimum vector.
         * @param max    The maximum vector.
         * @return True if the vector is within the region.
         */
        boolean within(Vector vector, Vector min, Vector max);
    }
}
