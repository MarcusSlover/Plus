package com.marcusslover.plus.lib.sidebar;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Deprecated(forRemoval = true)
public class NewSidebar implements ISendable<NewSidebar> {
    private static final @NotNull ChatColor[] COLOR = ChatColor.values();
    private static final @NotNull Map<UUID, NewSidebar> SIDEBAR_MAP = new HashMap<>();

    private final @NotNull Scoreboard scoreboard;
    private final @NotNull Objective objective;

    protected final @NotNull LinkedList<@NotNull SidebarField> fields;

    private NewSidebar(@NotNull Text title, @NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.objective = this.scoreboard.registerNewObjective("", Criteria.DUMMY, title.comp());
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setRenderType(RenderType.INTEGER);
        this.fields = new LinkedList<>();
    }

    private NewSidebar(@NotNull String title) {
        this(Text.of(title));
    }

    private NewSidebar(@NotNull Text title) {
        this(title, Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public static @NotNull NewSidebar of(@NotNull String title) {
        return new NewSidebar(title);
    }

    public static @NotNull NewSidebar of(@NotNull Component title) {
        return new NewSidebar(Text.of(title));
    }

    public static @NotNull NewSidebar of(@NotNull Text title) {
        return new NewSidebar(title);
    }

    public static @NotNull NewSidebar of(@NotNull Text title, @NotNull Scoreboard scoreboard) {
        return new NewSidebar(title, scoreboard);
    }

    public static @NotNull NewSidebar of(@NotNull String title, @NotNull Scoreboard scoreboard) {
        return new NewSidebar(Text.of(title), scoreboard);
    }

    public static @NotNull NewSidebar of(@NotNull Component title, @NotNull Scoreboard scoreboard) {
        return new NewSidebar(Text.of(title), scoreboard);
    }

    private @NotNull String getEntry(int currentSize) {
        return NewSidebar.COLOR[currentSize].toString() + NewSidebar.COLOR[currentSize / 4] + NewSidebar.COLOR[COLOR.length - 1];
    }

    public @NotNull NewSidebar addField(@NotNull String prefix, @NotNull String suffix) {
        return this.addField(Text.of(prefix), Text.of(suffix));
    }

    public @NotNull NewSidebar addField(@NotNull Text prefix, @NotNull Text suffix) {
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

    public @NotNull NewSidebar addField() {
        addField("", "");
        return this;
    }

    public @NotNull NewSidebar insertField(int index, @NotNull String prefix, @NotNull String suffix) {
        return this.insertField(index, Text.of(prefix), Text.of(suffix));
    }

    public @NotNull NewSidebar insertField(int index, @NotNull Text prefix, @NotNull Text suffix) {
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

    public @NotNull NewSidebar updateField(int index, @NotNull String prefix, @NotNull String suffix) {
        return this.updateField(index, Text.of(prefix), Text.of(suffix));
    }

    public @NotNull NewSidebar updateField(int index, @NotNull Text prefix, @NotNull Text suffix) {
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

    public @NotNull NewSidebar removeField(int index) {
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

    public @NotNull NewSidebar clearFields() {
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


    public static @NotNull Optional<NewSidebar> get(@NotNull Player player) {
        return NewSidebar.get(player.getUniqueId());
    }

    public static @NotNull Optional<NewSidebar> get(@NotNull UUID uuid) {
        if (NewSidebar.SIDEBAR_MAP.containsKey(uuid)) {
            return Optional.of(NewSidebar.SIDEBAR_MAP.get(uuid));
        }
        return Optional.empty();
    }

    public static void destroy(@NotNull Player player) {
        NewSidebar.destroy(player.getUniqueId());
    }

    public static void destroy(@NotNull UUID uuid) {
        NewSidebar.get(uuid).ifPresent(sidebar -> NewSidebar.SIDEBAR_MAP.remove(uuid));
    }

    public static @NotNull Map<UUID, NewSidebar> getSidebarMap() {
        return NewSidebar.SIDEBAR_MAP;
    }

    @Override
    public @NotNull <T extends CommandSender> NewSidebar send(@NotNull T target) {
        if (!(target instanceof Player player)) {
            return this;
        }

        player.setScoreboard(this.scoreboard);
        NewSidebar.SIDEBAR_MAP.put(player.getUniqueId(), this);
        return this;
    }

    @Override
    public @NotNull NewSidebar send(Audience audience) {
        audience.forEachAudience(member -> {
            if (member instanceof Player player) {
                this.send(player);
            }
        });

        return this;
    }
}
