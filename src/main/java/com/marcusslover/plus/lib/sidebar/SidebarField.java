package com.marcusslover.plus.lib.sidebar;

import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public record SidebarField(@NotNull Team team, @NotNull String entry, int index) {
    boolean match(@NotNull String entry) {
        return entry().toLowerCase().contains(entry.toLowerCase());
    }
}
