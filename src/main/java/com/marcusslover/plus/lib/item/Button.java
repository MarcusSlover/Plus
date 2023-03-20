package com.marcusslover.plus.lib.item;

import com.marcusslover.plus.lib.region.IRegion;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a button in a menu.
 */
@Data
@Accessors(fluent = true, chain = true)
public class Button {
    private @NotNull Button.DetectableArea detectableArea; // The area where the button is detectable
    private @Nullable Item item = null; // The item that represents the button
    private Canvas.ClickContext clickContext; // The click event of the button

    /**
     * Creates a button with the given coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The button
     */
    public static @NotNull Button create(int x, int y) {
        return create(x, y, false);
    }

    /**
     * Creates a button with the given slot.
     *
     * @param slot The slot
     * @return The button
     */
    public static @NotNull Button create(int slot) {
        return create(transformX(slot), transformY(slot), true);
    }

    /**
     * Creates a button with the given slots.
     *
     * @param min_or_x The minimum slot or minimum x coordinate
     * @param max_or_y The maximum slot or maximum x coordinate
     * @param rawSlot  True if the slots are raw slots (0-53) or false if they are x and y coordinates.
     * @return The button
     */
    public static @NotNull Button create(int min_or_x, int max_or_y, boolean rawSlot) {
        if (rawSlot) {
            int x1 = transformX(min_or_x);
            int y1 = transformY(min_or_x);
            int x2 = transformX(max_or_y);
            int y2 = transformY(max_or_y);
            return Button.create(x1, y1, x2, y2);
        } else {
            return new Button(DetectableArea.of(min_or_x, max_or_y));
        }
    }

    /**
     * Creates a button with the given coordinates.
     * Works like {@link #create(int, int)} but with a region.
     *
     * @param x1 The minimum x coordinate
     * @param y1 The minimum y coordinate
     * @param x2 The maximum x coordinate
     * @param y2 The maximum y coordinate
     * @return The button
     */
    public static @NotNull Button create(int x1, int y1, int x2, int y2) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        return new Button(DetectableArea.of(new Vector(minX, 0, minY), new Vector(maxX, 0, maxY)));
    }

    /**
     * Transforms a slot to a y coordinate.
     *
     * @param slot The slot
     * @return The y coordinate
     */
    public static int transformY(int slot) {
        return (int) Math.floor(slot / 9d);
    }

    /**
     * Transforms a slot to a x coordinate.
     *
     * @param slot The slot
     * @return The x coordinate
     */
    public static int transformX(int slot) {
        return slot - (transformY(slot) * 9);
    }

    /**
     * Transforms a y and x coordinate to a slot.
     *
     * @param y The y coordinate
     * @param x The x coordinate
     * @return The slot
     */
    public static int transformSlot(int y, int x) {
        return (y * 9) + x;
    }

    public @NotNull Button slot(int slot) {
        int x = transformX(slot);
        int y = transformY(slot);
        return this.slot(x, y);
    }

    public @NotNull Button slot(int x, int y) {
        return this.slot(x, y, false);
    }

    public @NotNull Button slot(int min_or_x, int max_or_y, boolean rawSlot) {
        if (rawSlot) {
            int x1 = transformX(min_or_x);
            int y1 = transformY(min_or_x);
            int x2 = transformX(max_or_y);
            int y2 = transformY(max_or_y);
            return this.slot(x1, y1, x2, y2);
        } else {
            this.detectableArea = DetectableArea.of(min_or_x, max_or_y);
            return this;
        }
    }

    public @NotNull Button slot(int x1, int y1, int x2, int y2) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        this.detectableArea = DetectableArea.of(new Vector(minX, 0, minY), new Vector(maxX, 0, maxY));
        return this;
    }

    /**
     * Checks if the given slot is within the button.
     *
     * @param slot The slot
     * @return True if the slot is within the button
     */
    public boolean within(int slot) {
        int y = transformY(slot);
        int x = transformX(slot);
        return this.detectableArea.within(new Vector(x, 0, y));
    }

    /**
     * Represents a detectable area of a button.
     * This is used to detect if a player clicked on a button.
     */
    @Data
    @Accessors(fluent = true, chain = true)
    public static class DetectableArea implements IRegion {
        private final @NotNull String id = UUID.randomUUID().toString();
        private final @NotNull Vector min; // two-dimensional array
        private final @NotNull Vector max; // two-dimensional array
        private int size = 0; // for matrix scaling

        /**
         * Creates a new detectable area with the given vectors.
         *
         * @param min The minimum vector
         * @param max The maximum vector
         */
        public DetectableArea(@NotNull Vector min, @NotNull Vector max) {
            this.min = min;
            this.max = max;
        }

        /**
         * Creates a new detectable area with the given coordinates.
         *
         * @param x The x coordinate
         * @param y The y coordinate
         */
        public DetectableArea(int x, int y) {
            this(new Vector(x, 0, y), new Vector(x, 0, y));
        }

        public static @NotNull DetectableArea of(@NotNull Vector min, @NotNull Vector max) {
            return new DetectableArea(min, max);
        }

        public static @NotNull DetectableArea of(int x, int y) {
            return new DetectableArea(x, y);
        }

        /**
         * Gets the minimum vector.
         *
         * @return The minimum vector
         */
        @Override
        public @NotNull Vector min() {
            return Vector.getMinimum(this.min, this.max);
        }

        /**
         * Gets the maximum vector.
         *
         * @return The maximum vector
         */
        @Override
        public @NotNull Vector max() {
            return Vector.getMaximum(this.min, this.max);
        }

        /**
         * Checks if the given vector is within the region.
         *
         * @param vector   The vector
         * @param strategy The strategy
         * @return True if the vector is within the region
         */
        @Override
        public boolean within(@NotNull Vector vector, @NotNull VectorStrategy strategy) {
            if (this.singular()) {
                return IRegion.super.within(vector, this.min(), this.max().clone().add(new Vector(this.size, 0, this.size)), strategy);
            }
            return IRegion.super.within(vector, strategy);
        }

        /**
         * Check if the matrix is a single point.
         *
         * @return true if the matrix is a single point.
         */
        public boolean singular() {
            return this.min() == this.max();
        }

        /**
         * Gets the slots of the button.
         *
         * @return The slots
         */
        public @NotNull Set<Integer> slots() {
            Set<Integer> slots = new HashSet<>();

            for (int y = (int) this.min().getZ(); y <= this.max().getZ(); y++) { // must be z
                for (int x = (int) this.min().getX(); x <= this.max().getX(); x++) {
                    slots.add(transformSlot(y, x));
                }
            }

            return slots;
        }
    }
}
