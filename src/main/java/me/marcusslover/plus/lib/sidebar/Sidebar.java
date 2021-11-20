package me.marcusslover.plus.lib.sidebar;

import me.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Sidebar {
    private static final ChatColor[] CHAT_COLORS = ChatColor.values();
    private static final Map<UUID, Sidebar> SIDEBAR_MAP = new HashMap<>();
    private static int customID = 0;
    public final Scoreboard scoreboard;
    public final Objective objective;

    private final LinkedList<SidebarField> fields;

    public Sidebar(Text title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective(customID + "DS", "dummy", title.comp());
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.fields = new LinkedList<>();

        // Add to the id value
        customID += 1;
    }

    public static Optional<Sidebar> get(Player player) {
        return get(player.getUniqueId());
    }

    public static Optional<Sidebar> get(UUID uuid) {
        if (SIDEBAR_MAP.containsKey(uuid)) {
            return Optional.of(SIDEBAR_MAP.get(uuid));
        }
        return Optional.empty();
    }

    private String getEntry(int currentSize) {
        return CHAT_COLORS[currentSize].toString() + CHAT_COLORS[currentSize / 4].toString() + CHAT_COLORS[CHAT_COLORS.length - 1];
    }

    public Sidebar addField(Text prefix, Text suffix) {
        int currentSize = fields.size();
        String entry = getEntry(currentSize);

        Team team = this.scoreboard.registerNewTeam(currentSize + "T");
        team.addEntry(entry);

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Add to the list
        int fieldsSize = fields.size();
        fields.add(new SidebarField(team, entry, fieldsSize));

        this.objective.getScore(entry).setScore(16 - currentSize);
        return this;
    }

    public Sidebar insertField(int index, Text prefix, Text suffix) {
        int currentSize = fields.size();
        String entry = getEntry(currentSize);

        Team team = this.scoreboard.registerNewTeam(currentSize + "T");
        team.addEntry(entry);

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Add to the list
        int fieldsSize = fields.size();
        fields.add(index, new SidebarField(team, entry, fieldsSize));

        // Clear old ones
        Set<String> entries = this.scoreboard.getEntries();
        for (String s : entries) {
            this.scoreboard.resetScores(s);
        }

        // Update the whole sidebar
        for (SidebarField field : fields) {
            this.objective.getScore(field.entry()).setScore(16 - currentSize);
        }
        return this;
    }

    public Sidebar updateField(int index, Text prefix, Text suffix) {
        Team team = this.scoreboard.getTeam(index + "T");
        if (team == null) {
            return this;
        }
        team.prefix(prefix.comp());
        team.suffix(suffix.comp());
        return this;
    }

    public SidebarField getField(int index) {
        return this.fields.get(index);
    }

    public SidebarField findField(String... filters) {
        for (String filter : filters) {
            for (SidebarField field : this.fields) {
                // match the entry
                if (field.match(filter)) {
                    return field;
                }
            }
        }

        return null;
    }

    public Sidebar removeField(int index) {
        // Add to the list
        fields.remove(index);

        // Clear old ones
        Set<String> entries = this.scoreboard.getEntries();
        for (String s : entries) {
            this.scoreboard.resetScores(s);
        }

        // Update the whole sidebar
        int fieldsSize = fields.size();
        for (SidebarField field : fields) {
            this.objective.getScore(field.entry()).setScore(16 - fieldsSize);
        }
        return this;
    }

    public Sidebar clearFields() {
        for (Team team : this.scoreboard.getTeams()) {
            team.unregister();
        }
        this.fields.clear();
        return this;
    }

    public Sidebar send(Player player) {
        player.setScoreboard(scoreboard);
        return this;
    }
}
