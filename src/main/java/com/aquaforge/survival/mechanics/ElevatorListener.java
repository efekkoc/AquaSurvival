package com.aquaforge.survival.mechanics;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Modern Vertical Elevator System.
 * Jump to ascend, sneak to descend between identical elevator blocks.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class ElevatorListener implements Listener {

    private final Material elevatorMaterial;
    private final int maxDistance;

    public ElevatorListener(Material elevatorMaterial, int maxDistance) {
        this.elevatorMaterial = elevatorMaterial;
        this.maxDistance = maxDistance;
    }

    @EventHandler
    public void onJump(PlayerMoveEvent event) {
        if (event.getTo().getY() <= event.getFrom().getY()) return;

        Player player = event.getPlayer();
        Block standingOn = player.getLocation().getBlock().getRelative(0, -1, 0);

        if (standingOn.getType() == elevatorMaterial && player.getVelocity().getY() > 0.08) {
            // Player jumped on elevator, search upward
            for (int y = 2; y <= maxDistance; y++) {
                Block target = standingOn.getRelative(0, y, 0);
                if (target.getType() == elevatorMaterial) {
                    Block head = target.getRelative(0, 1, 0);
                    Block above = target.getRelative(0, 2, 0);
                    if (head.isPassable() && above.isPassable()) {
                        Location dest = target.getLocation().add(0.5, 1.0, 0.5);
                        dest.setYaw(player.getLocation().getYaw());
                        dest.setPitch(player.getLocation().getPitch());
                        player.teleport(dest);
                        playEffects(player, dest);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        Block standingOn = player.getLocation().getBlock().getRelative(0, -1, 0);

        if (standingOn.getType() == elevatorMaterial) {
            // Player sneaked on elevator, search downward
            for (int y = -2; y >= -maxDistance; y--) {
                Block target = standingOn.getRelative(0, y, 0);
                if (target.getType() == elevatorMaterial) {
                    Block head = target.getRelative(0, 1, 0);
                    Block above = target.getRelative(0, 2, 0);
                    if (head.isPassable() && above.isPassable()) {
                        Location dest = target.getLocation().add(0.5, 1.0, 0.5);
                        dest.setYaw(player.getLocation().getYaw());
                        dest.setPitch(player.getLocation().getPitch());
                        player.teleport(dest);
                        playEffects(player, dest);
                        break;
                    }
                }
            }
        }
    }

    private void playEffects(Player player, Location loc) {
        player.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
        loc.getWorld().spawnParticle(Particle.PORTAL, loc, 20, 0.3, 0.5, 0.3, 0.1);
        player.sendActionBar(MiniMessage.miniMessage().deserialize(
                "<gradient:#00c853:#00e5ff><bold>AquaForge</bold></gradient> <dark_gray>▏</dark_gray> <aqua>Elevator Teleport</aqua>"
        ));
    }
}
