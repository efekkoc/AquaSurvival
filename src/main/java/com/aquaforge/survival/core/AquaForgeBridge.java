package com.aquaforge.survival.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * AquaForge Corporate Ecosystem Bridge for Survival Division.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class AquaForgeBridge {

    private final Logger logger;
    private boolean aquaMapHooked = false;
    private boolean aquaEconomyHooked = false;
    private boolean aquaChatHooked = false;
    private boolean aquaScoreboardHooked = false;

    public AquaForgeBridge(Logger logger) {
        this.logger = logger;
    }

    public void discoverAndHook() {
        logger.info("============================================================");
        logger.info("   AquaForge Ecosystem — Survival Division Handshake");
        logger.info("   Parent Brand: AquaForge (https://github.com/aquaforge-mc)");
        logger.info("   Architect: Turgut Efekan Koç (@efekkoc)");
        logger.info("============================================================");

        Plugin aquaMap = Bukkit.getPluginManager().getPlugin("AquaMap");
        if (aquaMap != null && aquaMap.isEnabled()) {
            this.aquaMapHooked = true;
            logger.info("» [AquaForge] Hooked into AquaMap! Dynamic death waypoint radar markers enabled.");
        }

        Plugin aquaEconomy = Bukkit.getPluginManager().getPlugin("AquaEconomy");
        if (aquaEconomy != null && aquaEconomy.isEnabled()) {
            this.aquaEconomyHooked = true;
            logger.info("» [AquaForge] Hooked into AquaEconomy! Survival transactions integrated.");
        }

        Plugin aquaChat = Bukkit.getPluginManager().getPlugin("AquaChat");
        if (aquaChat != null && aquaChat.isEnabled()) {
            this.aquaChatHooked = true;
            logger.info("» [AquaForge] Hooked into AquaChat! Death announcements & sleep broadcasts formatted.");
        }

        Plugin aquaScoreboard = Bukkit.getPluginManager().getPlugin("AquaScoreboard");
        if (aquaScoreboard != null && aquaScoreboard.isEnabled()) {
            this.aquaScoreboardHooked = true;
            logger.info("» [AquaForge] Hooked into AquaScoreboard! Survival stats & death counters active.");
        }
    }

    public boolean isAquaMapHooked() {
        return aquaMapHooked;
    }

    public boolean isAquaEconomyHooked() {
        return aquaEconomyHooked;
    }

    public boolean isAquaChatHooked() {
        return aquaChatHooked;
    }

    public boolean isAquaScoreboardHooked() {
        return aquaScoreboardHooked;
    }
}
