package com.aquaforge.survival.sleep;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 50-100+ Player Enterprise SMP Sleep Engine.
 * Supports One-Player Sleep (1 person sleeping skips night) with dynamic BossBars.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class SleepVoteManager implements Listener {

    private final Plugin plugin;
    private final boolean onePlayerMode;
    private final int neededPercent;
    private final int countdownSeconds;
    private final Set<UUID> sleepingPlayers = new HashSet<>();
    private BossBar sleepBossBar;
    private BukkitTask countdownTask;
    private int currentCountdown;

    public SleepVoteManager(Plugin plugin, boolean onePlayerMode, int neededPercent, int countdownSeconds) {
        this.plugin = plugin;
        this.onePlayerMode = onePlayerMode;
        this.neededPercent = neededPercent;
        this.countdownSeconds = countdownSeconds;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player player = event.getPlayer();
        sleepingPlayers.add(player.getUniqueId());

        if (onePlayerMode) {
            // In 50-100 player servers, 1 person sleeping is immediately sufficient!
            if (countdownTask == null) {
                startOnePlayerCountdown(player);
            }
        } else {
            checkPercentageSleep(player.getWorld());
        }
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        sleepingPlayers.remove(event.getPlayer().getUniqueId());
        if (sleepingPlayers.isEmpty() && countdownTask != null) {
            cancelCountdown(event.getPlayer().getWorld());
        }
    }

    private void startOnePlayerCountdown(Player sleeper) {
        World world = sleeper.getWorld();
        currentCountdown = countdownSeconds;

        sleepBossBar = BossBar.bossBar(
                MiniMessage.miniMessage().deserialize("<gradient:#ffd700:#ff9100><bold>🌙 " + sleeper.getName() + " is sleeping!</bold></gradient> <gray>Skipping night in " + currentCountdown + "s...</gray>"),
                1.0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );

        for (Player p : world.getPlayers()) {
            p.showBossBar(sleepBossBar);
            p.showTitle(Title.title(
                    MiniMessage.miniMessage().deserialize("<gradient:#ffd700:#ff9100><bold>🌙 Sweet Dreams</bold></gradient>"),
                    MiniMessage.miniMessage().deserialize("<gray><white>" + sleeper.getName() + "</white> is sleeping. Skipping night...</gray>"),
                    Title.Times.times(Duration.ofMillis(200), Duration.ofSeconds(2), Duration.ofMillis(500))
            ));
        }

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            currentCountdown--;
            if (currentCountdown <= 0) {
                skipNight(world);
            } else {
                float progress = (float) currentCountdown / (float) countdownSeconds;
                sleepBossBar.progress(Math.max(0.0f, progress));
                sleepBossBar.name(MiniMessage.miniMessage().deserialize(
                        "<gradient:#ffd700:#ff9100><bold>🌙 " + sleeper.getName() + " is sleeping!</bold></gradient> <gray>Skipping night in " + currentCountdown + "s...</gray>"
                ));
            }
        }, 20L, 20L);
    }

    private void checkPercentageSleep(World world) {
        int total = world.getPlayers().size();
        if (total == 0) return;

        int sleeping = (int) world.getPlayers().stream()
                .filter(p -> sleepingPlayers.contains(p.getUniqueId()) || p.isSleeping())
                .count();

        int percent = (sleeping * 100) / total;
        if (percent >= neededPercent && countdownTask == null) {
            currentCountdown = countdownSeconds;
            sleepBossBar = BossBar.bossBar(
                    MiniMessage.miniMessage().deserialize("<gradient:#ffd700:#ff9100><bold>SLEEP VOTE</bold></gradient> <gray>— " + sleeping + "/" + total + " in bed... Skipping night in " + currentCountdown + "s</gray>"),
                    1.0f,
                    BossBar.Color.YELLOW,
                    BossBar.Overlay.PROGRESS
            );

            for (Player p : world.getPlayers()) {
                p.showBossBar(sleepBossBar);
            }

            countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                currentCountdown--;
                if (currentCountdown <= 0) {
                    skipNight(world);
                } else {
                    float progress = (float) currentCountdown / (float) countdownSeconds;
                    sleepBossBar.progress(Math.max(0.0f, progress));
                }
            }, 20L, 20L);
        }
    }

    private void skipNight(World world) {
        world.setTime(1000L);
        world.setStorm(false);
        world.setThundering(false);

        for (Player p : world.getPlayers()) {
            p.hideBossBar(sleepBossBar);
            p.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<gradient:#00c853:#00e5ff><bold>AquaForge</bold></gradient> <dark_gray>▏</dark_gray> <yellow>☀️ Good morning! Night has been smoothly skipped.</yellow>"
            ));
        }

        cancelCountdown(world);
    }

    private void cancelCountdown(World world) {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        if (sleepBossBar != null) {
            for (Player p : world.getPlayers()) {
                p.hideBossBar(sleepBossBar);
            }
            sleepBossBar = null;
        }
    }
}
