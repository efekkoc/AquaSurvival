package com.aquaforge.survival;

import com.aquaforge.survival.commands.SurvivalCommand;
import com.aquaforge.survival.core.AquaForgeBridge;
import com.aquaforge.survival.graves.GraveManager;
import com.aquaforge.survival.mechanics.ElevatorListener;
import com.aquaforge.survival.mechanics.FastLeafDecayListener;
import com.aquaforge.survival.mechanics.SilkSpawnerListener;
import com.aquaforge.survival.sleep.SleepVoteManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * AquaSurvival — Enterprise Survival & SMP Mechanics Engine.
 * Proudly engineered as a core division of the AquaForge Ecosystem.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class AquaSurvivalPlugin extends JavaPlugin {

    private static AquaSurvivalPlugin instance;
    private AquaForgeBridge bridge;
    private GraveManager graveManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // 1. Holding Ecosystem Handshake
        this.bridge = new AquaForgeBridge(getLogger());
        this.bridge.discoverAndHook();

        // 2. Graves Manager
        int expireMinutes = getConfig().getInt("graves.expire-time-minutes", 30);
        this.graveManager = new GraveManager(expireMinutes);
        getServer().getPluginManager().registerEvents(graveManager, this);

        // 3. Sleep Vote Manager
        int sleepPercent = getConfig().getInt("sleep.needed-percent", 25);
        int sleepCountdown = getConfig().getInt("sleep.bossbar-countdown-seconds", 5);
        getServer().getPluginManager().registerEvents(new SleepVoteManager(this, sleepPercent, sleepCountdown), this);

        // 4. Survival Mechanics Listeners
        if (getConfig().getBoolean("mechanics.silk-touch-spawners", true)) {
            getServer().getPluginManager().registerEvents(new SilkSpawnerListener(this), this);
        }
        if (getConfig().getBoolean("mechanics.fast-leaf-decay", true)) {
            getServer().getPluginManager().registerEvents(new FastLeafDecayListener(this), this);
        }
        if (getConfig().getBoolean("mechanics.elevators.enabled", true)) {
            String matName = getConfig().getString("mechanics.elevators.block-type", "WHITE_WOOL");
            Material mat = Material.matchMaterial(matName);
            if (mat == null) mat = Material.WHITE_WOOL;
            int dist = getConfig().getInt("mechanics.elevators.max-floor-distance", 24);
            getServer().getPluginManager().registerEvents(new ElevatorListener(mat, dist), this);
        }

        // 5. Commands
        SurvivalCommand cmd = new SurvivalCommand(this);
        if (getCommand("survival") != null) getCommand("survival").setExecutor(cmd);
        if (getCommand("graves") != null) getCommand("graves").setExecutor(cmd);
        if (getCommand("back") != null) getCommand("back").setExecutor(cmd);

        // 6. Graves Tick Task
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            graveManager.tickAll();
        }, 20L, 20L);

        getLogger().info("============================================================");
        getLogger().info("   AquaSurvival — Survival Division v1.0.0 Online");
        getLogger().info("   Parent Brand: AquaForge (https://github.com/aquaforge-mc)");
        getLogger().info("   Founder & Chief Architect: Turgut Efekan Koç (@efekkoc)");
        getLogger().info("   Optimized for Paper 1.20.x – 1.21.11 Mounts of Mayhem");
        getLogger().info("============================================================");
    }

    @Override
    public void onDisable() {
        if (graveManager != null) {
            graveManager.cleanUpAll();
        }
        getLogger().info("» [AquaForge] AquaSurvival safely shut down.");
    }

    public static AquaSurvivalPlugin getInstance() {
        return instance;
    }

    public AquaForgeBridge getBridge() {
        return bridge;
    }

    public GraveManager getGraveManager() {
        return graveManager;
    }
}
