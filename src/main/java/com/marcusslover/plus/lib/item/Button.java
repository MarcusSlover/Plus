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
    private final @NotNull Button.DetectableArea detectableArea; // The area where the button is detectable
    private @Nullable Item item = null; // The item that represents the button
    private @Nullable Canvas.ButtonClick buttonClick = null; // The click event of the button

    /**
     * Creates a button with the given coordinates.
     *
     * @param y The y coordinate
     * @param x The x coordinate
     * @return The button
     */
    public static @NotNull Button pos(int y, int x) {
        return new Button(new DetectableArea(new Vector(x, 0, y), new Vector(x, 0, y)));
    }

    /**
     * Creates a button with the given coordinates.
     * Works like {@link #pos(int, int)} but with a range.
     *
     * @param minY The minimum y coordinate
     * @param minX The minimum x coordinate
     * @param maxY The maximum y coordinate
     * @param maxX The maximum x coordinate
     * @return The button
     */
    public static @NotNull Button pos(int minY, int minX, int maxY, int maxX) {
        return new Button(new DetectableArea(new Vector(minX, 0, minY), new Vector(maxX, 0, maxY)));
    }

    /**
     * Creates a button with the given slot.
     *
     * @param slot The slot
     * @return The button
     */
    public static @NotNull Button slot(int slot) {
        int y = transformY(slot);
        int x = transformX(slot);
        return new Button(new DetectableArea(new Vector(x, 0, y), new Vector(x, 0, y)));
    }

    /**
     * Creates a button with the given slots.
     *
     * @param min The minimum slot
     * @param max The maximum slot
     * @return The button
     */
    public static @NotNull Button field(int min, int max) {
        int minY = transformY(min);
        int minX = transformX(min);
        int maxY = transformY(max);
        int maxX = transformX(max);
        return new Button(new DetectableArea(new Vector(minX, 0, minY), new Vector(maxX, 0, maxY)));
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
