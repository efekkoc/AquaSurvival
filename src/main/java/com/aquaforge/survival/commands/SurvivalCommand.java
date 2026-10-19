package com.aquaforge.survival.commands;

import com.aquaforge.survival.AquaSurvivalPlugin;
import com.aquaforge.survival.graves.Grave;
import com.aquaforge.survival.graves.GraveManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Master Survival, Spawn Protection & Graves Command Controller.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class SurvivalCommand implements CommandExecutor {

    private final AquaSurvivalPlugin plugin;
    private final GraveManager graveManager;

    public SurvivalCommand(AquaSurvivalPlugin plugin) {
        this.plugin = plugin;
        this.graveManager = plugin.getGraveManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is for in-game players only.");
            return true;
        }

        String prefix = plugin.getConfig().getString("branding.prefix", "<green>AquaSurvival</green> » ");

        if (label.equalsIgnoreCase("back")) {
            handleBack(player, prefix);
            return true;
        }

        if (label.equalsIgnoreCase("graves") || label.equalsIgnoreCase("grave")) {
            handleGraves(player, prefix);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("setspawn")) {
            handleSetSpawn(player, prefix);
            return true;
        }

        // Default /survival info
        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</dark_gray>"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("   <gradient:#00c853:#00e5ff><bold>AquaForge Survival Suite v1.0.0</bold></gradient>"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>• 🛡️ Spawn Sanctuary: <green>Active</green> (No Hunger, No Fall, No PvP)</gray>"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>• 🌙 One-Player Sleep: <green>Active</green> (1 player skips night)</gray>"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>• 🪦 1.21 Display Graves: <green>Active</green> (Items safe for 30m)</gray>"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>• ⛏️ Silk Touch Spawners: <green>Active</green></gray>"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>• 🍃 Fast Leaf Decay: <green>Active</green></gray>"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>• 🪜 Wool Elevators: <green>Active</green> (Jump / Sneak)</gray>"));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</dark_gray>"));

        return true;
    }

    private void handleSetSpawn(Player player, String prefix) {
        if (!player.hasPermission("aquasurvival.admin")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "<red>Only administrators can set the spawn sanctuary!</red>"));
            return;
        }

        plugin.getSpawnProtectionManager().setSpawnLocation(player.getLocation());
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                prefix + "<green>Spawn Sanctuary location successfully updated to your current coordinates!</green>"
        ));
    }

    private void handleBack(Player player, String prefix) {
        if (!player.hasPermission("aquasurvival.back")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "<red>You lack permission for /back!</red>"));
            return;
        }

        Location lastDeath = graveManager.getLastDeathLocation(player.getUniqueId());
        if (lastDeath == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "<red>No recent death location found!</red>"));
            return;
        }

        player.teleport(lastDeath);
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                prefix + "<green>Teleported to your last death location!</green>"
        ));
    }

    private void handleGraves(Player player, String prefix) {
        List<Grave> graves = graveManager.getGraves(player.getUniqueId());
        if (graves.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "<gray>You currently have no active graves.</gray>"));
            return;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "<gradient:#ff1744:#ff9100><bold>Your Active Graves</bold></gradient>"));
        for (Grave g : graves) {
            Location l = g.getLocation();
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<gray>• <yellow>[" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + "]</yellow> <dark_gray>▏</dark_gray> " +
                    g.getItems().size() + " items <dark_gray>▏</dark_gray> ⏱ " + g.getRemainingMinutes() + "m left"
            ));
        }
    }
}
