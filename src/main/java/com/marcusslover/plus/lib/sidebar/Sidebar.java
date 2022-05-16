package com.marcusslover.plus.lib.sidebar;

import com.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Sidebar {
    private static final ChatColor[] COLOR = ChatColor.values();
    private static final Map<UUID, Sidebar> SIDEBAR_MAP = new HashMap<>();

    private static int customID = 0;

    @NotNull
    public final Scoreboard scoreboard;
    @NotNull
    public final Objective objective;

    @NotNull
    private final LinkedList<SidebarField> fields;

    public Sidebar(@NotNull String title) {
        this(new Text(title));
    }

    public Sidebar(@NotNull Text title) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective(customID + "DS", "dummy", title.comp());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        fields = new LinkedList<>();

        // Add to the id value
        customID += 1;
    }

    @NotNull
    public static Sidebar of(@NotNull String title) {
        return new Sidebar(title);
    }

    @NotNull
    public static Sidebar of(@NotNull Text title) {
        return new Sidebar(title);
    }

    @NotNull
    public static Optional<Sidebar> get(@NotNull Player player) {
        return get(player.getUniqueId());
    }

    @NotNull
    public static Optional<Sidebar> get(@NotNull UUID uuid) {
        if (SIDEBAR_MAP.containsKey(uuid)) {
            return Optional.of(SIDEBAR_MAP.get(uuid));
        }
        return Optional.empty();
    }

    public static void destroy(@NotNull Player player) {
        destroy(player.getUniqueId());
    }

    public static void destroy(@NotNull UUID uuid) {
        get(uuid).ifPresent(sidebar -> SIDEBAR_MAP.remove(uuid));
    }

    public static int getCustomID() {
        return customID;
    }

    @NotNull
    public static Map<UUID, Sidebar> getSidebarMap() {
        return SIDEBAR_MAP;
    }

    @NotNull
    private String getEntry(int currentSize) {
        return COLOR[currentSize].toString() + COLOR[currentSize / 4].toString() + COLOR[COLOR.length - 1];
    }

    @NotNull
    public Sidebar addField(@NotNull String prefix, @NotNull String suffix) {
        return addField(new Text(prefix), new Text(suffix));
    }

    @NotNull
    public Sidebar addField(@NotNull Text prefix, @NotNull Text suffix) {
        int currentSize = getSize();
        String entry = getEntry(currentSize);

        Team team = scoreboard.registerNewTeam(currentSize + "T");
        team.addEntry(entry);

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Add to the list
        int fieldsSize = fields.size();
        fields.add(new SidebarField(team, entry, fieldsSize));

        objective.getScore(entry).setScore(16 - currentSize);
        return this;
    }

    @NotNull
    public Sidebar insertField(int index, @NotNull String prefix, @NotNull String suffix) {
        return insertField(index, new Text(prefix), new Text(suffix));
    }

    @NotNull
    public Sidebar insertField(int index, @NotNull Text prefix, @NotNull Text suffix) {
        int currentSize = getSize();
        String entry = getEntry(currentSize);

        Team team = scoreboard.registerNewTeam(currentSize + "T");
        team.addEntry(entry);

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Add to the list
        int fieldsSize = fields.size();
        fields.add(index, new SidebarField(team, entry, fieldsSize));

        // Clear old ones
        Set<String> entries = scoreboard.getEntries();
        entries.forEach(scoreboard::resetScores);

        // Update the whole sidebar
        fields.forEach(field -> objective.getScore(field.entry()).setScore(16 - currentSize));
        return this;
    }

    @NotNull
    public Sidebar updateField(int index, @NotNull String prefix, @NotNull String suffix) {
        return updateField(index, new Text(prefix), new Text(suffix));
    }

    @NotNull
    public Sidebar updateField(int index, @NotNull Text prefix, @NotNull Text suffix) {
        Team team = this.scoreboard.getTeam(index + "T");
        if (team == null) return this;
        team.prefix(prefix.comp());
        team.suffix(suffix.comp());
        return this;
    }

    @Nullable
    public SidebarField getField(int index) {
        return fields.get(index);
    }

    @Nullable
    public SidebarField findField(@NotNull String... filters) {
        for (String filter : filters) {
            for (SidebarField field : fields) {
                // match the entry
                if (field.match(filter)) return field;
            }
        }
        return null;
    }

    @NotNull
    public Sidebar removeField(int index) {
        // Add to the list
        fields.remove(index);

        // Clear old ones
        Set<String> entries = scoreboard.getEntries();
        entries.forEach(scoreboard::resetScores);

        // Update the whole sidebar
        int fieldsSize = fields.size();
        fields.forEach(field -> objective.getScore(field.entry()).setScore(16 - fieldsSize));
        return this;
    }

    @NotNull
    public Sidebar clearFields() {
        scoreboard.getTeams().forEach(Team::unregister);
        fields.clear();
        return this;
    }

    @NotNull
    public Objective getObjective() {
        return objective;
    }

    @NotNull
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public int getSize() {
        return fields.size();
    }

    @NotNull
    public LinkedList<SidebarField> getFields() {
        return fields;
    }

    @NotNull
    public Sidebar send(@NotNull Player player) {
        player.setScoreboard(scoreboard);
        return this;
    }
}
