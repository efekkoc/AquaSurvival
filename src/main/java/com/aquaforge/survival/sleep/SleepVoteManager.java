package com.aquaforge.survival.sleep;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Multiplayer Dynamic Sleep Vote with Smooth BossBar Countdown.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class SleepVoteManager implements Listener {

    private final Plugin plugin;
    private final int neededPercent;
    private final int countdownSeconds;
    private final Set<UUID> sleepingPlayers = new HashSet<>();
    private BossBar sleepBossBar;
    private BukkitTask countdownTask;
    private int currentCountdown;

    public SleepVoteManager(Plugin plugin, int neededPercent, int countdownSeconds) {
        this.plugin = plugin;
        this.neededPercent = neededPercent;
        this.countdownSeconds = countdownSeconds;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player player = event.getPlayer();
        sleepingPlayers.add(player.getUniqueId());

        checkSleep(player.getWorld());
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        sleepingPlayers.remove(event.getPlayer().getUniqueId());
        if (sleepingPlayers.isEmpty() && countdownTask != null) {
            cancelCountdown(event.getPlayer().getWorld());
        }
    }

    private void checkSleep(World world) {
        int total = world.getPlayers().size();
        if (total == 0) return;

        int sleeping = (int) world.getPlayers().stream()
                .filter(p -> sleepingPlayers.contains(p.getUniqueId()) || p.isSleeping())
                .count();

        int percent = (sleeping * 100) / total;
        if (percent >= neededPercent && countdownTask == null) {
            startCountdown(world, sleeping, total);
        }
    }

    private void startCountdown(World world, int sleeping, int total) {
        currentCountdown = countdownSeconds;

        sleepBossBar = BossBar.bossBar(
                MiniMessage.miniMessage().deserialize("<gradient:#ffd700:#ff9100><bold>SLEEPING</bold></gradient> <gray>— " + sleeping + "/" + total + " in bed... Skipping night in " + currentCountdown + "s</gray>"),
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
                // Night skipped!
                world.setTime(1000L);
                world.setStorm(false);
                world.setThundering(false);

                for (Player p : world.getPlayers()) {
                    p.hideBossBar(sleepBossBar);
                    p.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<gradient:#00c853:#00e5ff><bold>AquaForge</bold></gradient> <dark_gray>▏</dark_gray> <yellow>☀️ Good morning! The night has been skipped.</yellow>"
                    ));
                }

                cancelCountdown(world);
            } else {
                float progress = (float) currentCountdown / (float) countdownSeconds;
                sleepBossBar.progress(Math.max(0.0f, progress));
                sleepBossBar.name(MiniMessage.miniMessage().deserialize(
                        "<gradient:#ffd700:#ff9100><bold>SLEEPING</bold></gradient> <gray>— Skipping night in " + currentCountdown + "s...</gray>"
                ));
            }
        }, 20L, 20L);
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
