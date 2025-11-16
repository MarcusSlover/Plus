package com.marcusslover.plus.lib.color;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Represents a class that can be used to create a color gradient.
 */
@Data(staticConstructor = "of")
public class ColorGradient {
    private final @NotNull Color start;
    private final @NotNull Color end;

    /**
     * Gets the color at the specified percentage.
     *
     * @param percentage the percentage, between 0 and 1
     * @return the color
     */
    public @NotNull Color at(float percentage) {
        if (percentage < 0) {
            percentage = 0;
        } else if (percentage > 1) {
            percentage = 1;
        }

        int red = (int) (this.start.red() + (this.end.red() - this.start.red()) * percentage);
        int green = (int) (this.start.green() + (this.end.green() - this.start.green()) * percentage);
        int blue = (int) (this.start.blue() + (this.end.blue() - this.start.blue()) * percentage);
        return Color.of(red, green, blue);
    }

    /**
     * Iterates over the gradient.
     *
     * @param amount   the number of times to iterate
     * @param consumer the consumer
     * @return the gradient
     */
    public @NotNull ColorGradient forEach(int amount, @NotNull Consumer<Color> consumer) {
        for (int i = 0; i < amount; i++) {
            float percentage = (float) i / (amount - 1);
            consumer.accept(this.at(percentage));
        }
        return this;
    }

    /**
     * Reverses the gradient.
     *
     * @return the reversed gradient
     */
    public @NotNull ColorGradient reverse() {
        return ColorGradient.of(this.end, this.start);
    }
}
