package com.marcusslover.plus.lib.region;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Data
@Accessors(fluent = true, chain = true)
public class Region implements IRegion {
    private final @NotNull Vector a;
    private final @NotNull Vector b;
    private int priority;

    @Override
    public @NotNull Vector min() {
        return Vector.getMinimum(this.a, this.b);
    }

    @Override
    public @NotNull Vector max() {
        return Vector.getMaximum(this.a, this.b);
    }
}
