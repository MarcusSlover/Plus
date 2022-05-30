package com.marcusslover.plus.lib.sidebar;

import com.marcusslover.plus.lib.text.Text;
import com.marcusslover.plus.lib.util.ISendable;
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

public class Sidebar implements ISendable<Player, Sidebar> {
    private static final @NotNull ChatColor[] COLOR = ChatColor.values();
    private static final @NotNull Map<UUID, Sidebar> SIDEBAR_MAP = new HashMap<>();

    private static int customID = 0;

    public final @NotNull Scoreboard scoreboard;
    public final @NotNull Objective objective;

    protected final @NotNull LinkedList<@NotNull SidebarField> fields;

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

    public static @NotNull Sidebar of(@NotNull String title) {
        return new Sidebar(title);
    }

    public static @NotNull Sidebar of(@NotNull Text title) {
        return new Sidebar(title);
    }

    public static @NotNull Optional<Sidebar> get(@NotNull Player player) {
        return get(player.getUniqueId());
    }

    public static @NotNull Optional<Sidebar> get(@NotNull UUID uuid) {
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

    public static @NotNull Map<UUID, Sidebar> getSidebarMap() {
        return SIDEBAR_MAP;
    }

    private @NotNull String getEntry(int currentSize) {
        return COLOR[currentSize].toString() + COLOR[currentSize / 4].toString() + COLOR[COLOR.length - 1];
    }

    public @NotNull Sidebar addField(@NotNull String prefix, @NotNull String suffix) {
        return addField(new Text(prefix), new Text(suffix));
    }

    public @NotNull Sidebar addField(@NotNull Text prefix, @NotNull Text suffix) {
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

    public @NotNull Sidebar insertField(int index, @NotNull String prefix, @NotNull String suffix) {
        return insertField(index, new Text(prefix), new Text(suffix));
    }

    public @NotNull Sidebar insertField(int index, @NotNull Text prefix, @NotNull Text suffix) {
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

    public @NotNull Sidebar updateField(int index, @NotNull String prefix, @NotNull String suffix) {
        return updateField(index, new Text(prefix), new Text(suffix));
    }

    public @NotNull Sidebar updateField(int index, @NotNull Text prefix, @NotNull Text suffix) {
        Team team = this.scoreboard.getTeam(index + "T");
        if (team == null) return this;
        team.prefix(prefix.comp());
        team.suffix(suffix.comp());
        return this;
    }

    public @Nullable SidebarField getField(int index) {
        return fields.get(index);
    }

    public @Nullable SidebarField findField(@NotNull String... filters) {
        for (String filter : filters) {
            for (SidebarField field : fields) {
                // match the entry
                if (field.match(filter)) return field;
            }
        }
        return null;
    }

    public @NotNull Sidebar removeField(int index) {
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

    public @NotNull Sidebar clearFields() {
        scoreboard.getTeams().forEach(Team::unregister);
        fields.clear();
        return this;
    }

    public @NotNull Objective getObjective() {
        return objective;
    }

    public @NotNull Scoreboard getScoreboard() {
        return scoreboard;
    }

    public int getSize() {
        return fields.size();
    }

    public @NotNull LinkedList<@NotNull SidebarField> getFields() {
        return fields;
    }

    @Override
    public @NotNull Sidebar send(@NotNull Player player) {
        player.setScoreboard(scoreboard);
        SIDEBAR_MAP.put(player.getUniqueId(), this);
        return this;
    }
}
