package me.marcusslover.plus.lib.sidebar;

import org.bukkit.scoreboard.Team;

public record SidebarField(Team team, String entry, int index) {
    boolean match(String entry) {
        return this.entry.toLowerCase().contains(entry.toLowerCase());
    }
}
