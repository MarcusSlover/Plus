package com.marcusslover.plus.lib.sidebar;

import com.marcusslover.plus.lib.common.ISendable;
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
        this(title, Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public Sidebar(@NotNull Text title, @NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.objective = this.scoreboard.registerNewObjective(customID + "DS", "dummy", title.comp());
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.fields = new LinkedList<>();

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
        return this.addField(new Text(prefix), new Text(suffix));
    }

    public @NotNull Sidebar addField(@NotNull Text prefix, @NotNull Text suffix) {
        int currentSize = this.getSize();
        String entry = this.getEntry(currentSize);

        Team team = this.scoreboard.registerNewTeam(currentSize + "TPLUS");
        team.addEntry(entry);

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Add to the list
        int fieldsSize = this.fields.size();
        this.fields.add(new SidebarField(team, entry, fieldsSize));

        this.objective.getScore(entry).setScore(16 - currentSize);
        return this;
    }

    public @NotNull Sidebar insertField(int index, @NotNull String prefix, @NotNull String suffix) {
        return this.insertField(index, new Text(prefix), new Text(suffix));
    }

    public @NotNull Sidebar insertField(int index, @NotNull Text prefix, @NotNull Text suffix) {
        int currentSize = this.getSize();
        String entry = this.getEntry(currentSize);

        Team team = this.scoreboard.registerNewTeam(currentSize + "TPLUS");
        team.addEntry(entry);

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Add to the list
        int fieldsSize = this.fields.size();
        this.fields.add(index, new SidebarField(team, entry, fieldsSize));

        // Clear old ones
        Set<String> entries = this.scoreboard.getEntries();
        entries.forEach(this.scoreboard::resetScores);

        // Update the whole sidebar
        this.fields.forEach(field -> this.objective.getScore(field.entry()).setScore(16 - currentSize));
        return this;
    }

    public @NotNull Sidebar updateField(int index, @NotNull String prefix, @NotNull String suffix) {
        return this.updateField(index, new Text(prefix), new Text(suffix));
    }

    public @NotNull Sidebar updateField(int index, @NotNull Text prefix, @NotNull Text suffix) {
        Team team = this.scoreboard.getTeam(index + "TPLUS");
        if (team == null) {
            return this;
        }
        team.prefix(prefix.comp());
        team.suffix(suffix.comp());
        return this;
    }

    public @Nullable SidebarField getField(int index) {
        return this.fields.get(index);
    }

    public @Nullable SidebarField findField(@NotNull String... filters) {
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

    public @NotNull Sidebar removeField(int index) {
        // Add to the list
        this.fields.remove(index);

        // Clear old ones
        Set<String> entries = this.scoreboard.getEntries();
        entries.forEach(this.scoreboard::resetScores);

        // Update the whole sidebar
        int fieldsSize = this.fields.size();
        this.fields.forEach(field -> this.objective.getScore(field.entry()).setScore(16 - fieldsSize));
        return this;
    }

    public @NotNull Sidebar clearFields() {
        for (Team team : this.scoreboard.getTeams()) {

            if (team.getName().endsWith("TPLUS")) {
                team.unregister();
            }
        }
        this.fields.clear();
        return this;
    }

    public @NotNull Objective getObjective() {
        return this.objective;
    }

    public @NotNull Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public int getSize() {
        return this.fields.size();
    }

    public @NotNull LinkedList<@NotNull SidebarField> getFields() {
        return this.fields;
    }

    @Override
    public @NotNull Sidebar send(@NotNull Player player) {
        player.setScoreboard(this.scoreboard);
        SIDEBAR_MAP.put(player.getUniqueId(), this);
        return this;
    }

    @Override
    public @NotNull Sidebar send(@NotNull Player... targets) {
        for (Player target : targets) {
            this.send(target);
        }

        return this;
    }

    @Override
    public @NotNull Sidebar send(@NotNull Collection<Player> targets) {
        for (Player target : targets) {
            this.send(target);
        }

        return this;
    }
}
