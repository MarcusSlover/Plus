package com.marcusslover.plus.lib.sidebar;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.text.Text;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
@Accessors(fluent = true, chain = true)
public class Sidebar implements ISendable<Sidebar> {
    @SuppressWarnings("deprecation")
    private static final @NotNull ChatColor[] COLOR = ChatColor.values();
    private static final @NotNull Map<UUID, Sidebar> SIDEBAR_MAP = new HashMap<>();
    private static long customID = 0;

    public final @NotNull Scoreboard scoreboard;
    public final @NotNull Objective objective;
    protected final @NotNull LinkedList<@NotNull SidebarField> fields;

    private Sidebar(@NotNull String title) {
        this(Text.of(title));
    }

    private Sidebar(@NotNull String title, @NotNull Scoreboard scoreboard) {
        this(Text.of(title), scoreboard);
    }

    private Sidebar(@NotNull Text title) {
        this(title, Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private Sidebar(@NotNull Text title, @NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.objective = this.scoreboard.registerNewObjective(Sidebar.customID + "DS", Criteria.DUMMY, title.comp());
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setAutoUpdateDisplay(true);

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

    public static @NotNull Sidebar of(@NotNull Text title, @NotNull Scoreboard scoreboard) {
        return new Sidebar(title, scoreboard);
    }

    public static @NotNull Sidebar of(@NotNull String title, @NotNull Scoreboard scoreboard) {
        return new Sidebar(Text.of(title), scoreboard);
    }

    public static @NotNull Sidebar of(@NotNull Component title, @NotNull Scoreboard scoreboard) {
        return new Sidebar(Text.of(title), scoreboard);
    }

    public static @NotNull Optional<Sidebar> get(@NotNull Player player) {
        return Sidebar.get(player.getUniqueId());
    }

    public static Optional<Sidebar> get(@NotNull UUID uuid) {
        return Optional.ofNullable(Sidebar.SIDEBAR_MAP.get(uuid));
    }

    public static void destroy(@NotNull Player player) {
        Sidebar.destroy(player.getUniqueId());
    }

    public static void destroy(@NotNull UUID uuid) {
        Sidebar.get(uuid).ifPresent(sidebar -> Sidebar.SIDEBAR_MAP.remove(uuid));
    }

    public static long customId() {
        return Sidebar.customID;
    }

    public static @NotNull Map<UUID, Sidebar> map() {
        return Sidebar.SIDEBAR_MAP;
    }

    private @NotNull String entry(int currentSize) {
        return Sidebar.COLOR[currentSize].toString() + Sidebar.COLOR[currentSize / 4] + Sidebar.COLOR[COLOR.length - 1];
    }

    public @NotNull Sidebar numberFormat(@Nullable NumberFormat numberFormat) {
        this.objective.numberFormat(numberFormat);
        return this;
    }

    public @NotNull Sidebar addField(@NotNull String prefix, @NotNull String suffix) {
        return this.addField(Text.of(prefix), Text.of(suffix), null);
    }

    public @NotNull Sidebar addField(@NotNull String prefix, @NotNull String suffix, @Nullable NumberFormat numberFormat) {
        return this.addField(Text.of(prefix), Text.of(suffix), numberFormat);
    }

    public @NotNull Sidebar addField(@NotNull Text prefix, @NotNull Text suffix) {
        return this.addField(prefix, suffix, null);
    }

    public @NotNull Sidebar addField(@NotNull Text prefix, @NotNull Text suffix, @Nullable NumberFormat numberFormat) {
        int currentSize = this.size();
        String entry = this.entry(currentSize);

        Team team = this.scoreboard.registerNewTeam(currentSize + "TPLUS");
        team.addEntry(entry);

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Add to the list
        int fieldsSize = this.fields.size();
        this.fields.add(new SidebarField(team, entry, fieldsSize));

        Score score = this.objective.getScore(entry);
        score.setScore(16 - currentSize);
        score.numberFormat(numberFormat);
        return this;
    }

    public @NotNull Sidebar addField() {
        this.addField("", "");
        return this;
    }

    public @NotNull Sidebar insertField(int index, @NotNull String prefix, @NotNull String suffix) {
        return this.insertField(index, Text.of(prefix), Text.of(suffix));
    }

    public @NotNull Sidebar insertField(int index, @NotNull String prefix, @NotNull String suffix, @Nullable NumberFormat numberFormat) {
        return this.insertField(index, Text.of(prefix), Text.of(suffix), numberFormat);
    }

    public @NotNull Sidebar insertField(int index, @NotNull Text prefix, @NotNull Text suffix) {
        return this.insertField(index, prefix, suffix, null);
    }

    public @NotNull Sidebar insertField(int index, @NotNull Text prefix, @NotNull Text suffix, @Nullable NumberFormat numberFormat) {
        int currentSize = this.size();
        String entry = this.entry(currentSize);

        Team team = this.scoreboard.registerNewTeam(currentSize + "TPLUS");
        team.addEntry(entry);

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Add to the list
        int fieldsSize = this.fields.size();
        SidebarField newField = new SidebarField(team, entry, fieldsSize);
        this.fields.add(index, newField);

        // Clear old ones
        Set<String> entries = this.scoreboard.getEntries();
        entries.forEach(this.scoreboard::resetScores);

        // Update the whole sidebar
        for (SidebarField field : this.fields) {
            Score score = this.objective.getScore(field.entry());
            score.setScore(16 - currentSize);

            // Update the number format
            if (newField == field) {
                score.numberFormat(numberFormat);
            }
        }
        return this;
    }

    public @NotNull Sidebar updateField(int index, @NotNull String prefix, @NotNull String suffix) {
        return this.updateField(index, Text.of(prefix), Text.of(suffix));
    }

    public @NotNull Sidebar updateField(int index, @NotNull String prefix, @NotNull String suffix, @Nullable NumberFormat numberFormat) {
        return this.updateField(index, Text.of(prefix), Text.of(suffix), numberFormat);
    }

    public @NotNull Sidebar updateField(int index, @NotNull Text prefix, @NotNull Text suffix) {
        return this.updateField(index, prefix, suffix, null);
    }

    public @NotNull Sidebar updateField(int index, @NotNull Text prefix, @NotNull Text suffix, @Nullable NumberFormat numberFormat) {
        Team team = this.scoreboard.getTeam(index + "TPLUS");
        if (team == null) {
            return this;
        }
        // Check if isn't anything to update
        if (team.prefix().equals(prefix.comp()) && team.suffix().equals(suffix.comp())) {
            return this;
        }

        team.prefix(prefix.comp());
        team.suffix(suffix.comp());

        // Update the number format
        SidebarField sidebarField = this.fields.get(index);
        Score score = this.objective.getScore(sidebarField.entry());
        score.numberFormat(numberFormat);

        return this;
    }

    public @Nullable SidebarField field(int index) {
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

    public int size() {
        return this.fields.size();
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
