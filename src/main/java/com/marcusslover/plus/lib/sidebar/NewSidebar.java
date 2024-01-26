package com.marcusslover.plus.lib.sidebar;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NewSidebar implements ISendable<NewSidebar> {
    private static final @NotNull Map<UUID, NewSidebar> SIDEBAR_MAP = new HashMap<>();

    private final @NotNull Scoreboard scoreboard;
    private final @NotNull Objective objective;

    private NewSidebar(@NotNull Text title, @NotNull Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.objective = this.scoreboard.registerNewObjective("", Criteria.DUMMY, title.comp());
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setRenderType(RenderType.INTEGER);
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
