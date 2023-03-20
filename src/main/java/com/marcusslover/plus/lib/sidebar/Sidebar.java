package com.marcusslover.plus.lib.sidebar;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Sidebar implements ISendable<Sidebar> {
    private static final @NotNull ChatColor[] COLOR = ChatColor.values();
    private static final @NotNull Map<UUID, Sidebar> SIDEBAR_MAP = new HashMap<>();

    private static int customID = 0;

    public final @NotNull Scoreboard scoreboard;
    public final @NotNull Objective objective;

    protected final @NotNull LinkedList<@NotNull SidebarField> fields;

    private Sidebar(@NotNull String title) {
        this(Text.of(title));
    }

    private Sidebar(@NotNull Text title) {
        this(title, Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private Sidebar(@NotNull Text title, @NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.objective = this.scoreboard.registerNewObjective(Sidebar.customID + "DS", "dummy", title.comp());
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.fields = new LinkedList<>();

        // Add to the id value
        Sidebar.customID += 1;
    }

    public static @NotNull Sidebar of(@NotNull String title) {
        return new Sidebar(title);
    }

    public static @NotNull Sidebar of(@NotNull Component title) {
        return new Sidebar(Text.of(title));
    }

    public static @NotNull Sidebar of(@NotNull Text title) {
        return new Sidebar(title);
    }

    public static @NotNull Optional<Sidebar> get(@NotNull Player player) {
        return get(player.getUniqueId());
    }

    public static @NotNull Optional<Sidebar> get(@NotNull UUID uuid) {
        if (Sidebar.SIDEBAR_MAP.containsKey(uuid)) {
            return Optional.of(Sidebar.SIDEBAR_MAP.get(uuid));
        }
        return Optional.empty();
    }

    public static void destroy(@NotNull Player player) {
        destroy(player.getUniqueId());
    }

    public static void destroy(@NotNull UUID uuid) {
        get(uuid).ifPresent(sidebar -> Sidebar.SIDEBAR_MAP.remove(uuid));
    }

    public static int getCustomID() {
        return Sidebar.customID;
    }

    public static @NotNull Map<UUID, Sidebar> getSidebarMap() {
        return Sidebar.SIDEBAR_MAP;
    }

    private @NotNull String getEntry(int currentSize) {
        return Sidebar.COLOR[currentSize].toString() + Sidebar.COLOR[currentSize / 4].toString() + Sidebar.COLOR[COLOR.length - 1];
    }

    public @NotNull Sidebar addField(@NotNull String prefix, @NotNull String suffix) {
        return this.addField(Text.of(prefix), Text.of(suffix));
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
        return this.insertField(index, Text.of(prefix), Text.of(suffix));
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
        return this.updateField(index, Text.of(prefix), Text.of(suffix));
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
    public @NotNull <T extends CommandSender> Sidebar send(@NotNull T target) {
        if (!(target instanceof Player player)) {
            return this;
        }

        player.setScoreboard(this.scoreboard);
        Sidebar.SIDEBAR_MAP.put(player.getUniqueId(), this);
        return this;
    }

    @Override
    public @NotNull Sidebar send(Audience audience) {
        audience.forEachAudience(member -> {
            if (member instanceof Player player) {
                this.send(player);
            }
        });

        return this;
    }
}
