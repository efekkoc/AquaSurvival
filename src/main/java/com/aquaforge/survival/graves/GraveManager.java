package com.aquaforge.survival.graves;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GraveManager implements Listener {

    private final Map<UUID, List<Grave>> playerGraves = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastDeathLocations = new ConcurrentHashMap<>();
    private final int expireMinutes;

    public GraveManager(int expireMinutes) {
        this.expireMinutes = expireMinutes;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        if (drops.isEmpty()) return;

        event.getDrops().clear();
        int xp = Math.min(100, event.getDroppedExp());
        event.setDroppedExp(0);

        Location loc = player.getLocation().getBlock().getLocation();
        lastDeathLocations.put(player.getUniqueId(), loc);

        Grave grave = new Grave(player.getUniqueId(), player.getName(), loc, drops, xp, expireMinutes);
        grave.spawn();

        playerGraves.computeIfAbsent(player.getUniqueId(), k -> Collections.synchronizedList(new ArrayList<>())).add(grave);

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gradient:#00c853:#00e5ff><bold>AquaForge</bold></gradient> <dark_gray>▏</dark_gray> <green>Your items have been safely sealed in a 3D Grave at</green> <yellow>[" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]</yellow>!"
        ));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location deathLoc = lastDeathLocations.get(player.getUniqueId());
        if (deathLoc != null) {
            ItemStack compass = new ItemStack(Material.COMPASS);
            CompassMeta meta = (CompassMeta) compass.getItemMeta();
            if (meta != null) {
                meta.setLodestoneTracked(false);
                meta.setLodestone(deathLoc);
                meta.displayName(MiniMessage.miniMessage().deserialize("<gradient:#ff1744:#ff9100><bold>Soul Recovery Compass</bold></gradient>"));
                List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
                lore.add(MiniMessage.miniMessage().deserialize("<gray>Points towards your recent death grave.</gray>"));
                lore.add(MiniMessage.miniMessage().deserialize("<yellow>Coords: " + deathLoc.getBlockX() + ", " + deathLoc.getBlockY() + ", " + deathLoc.getBlockZ() + "</yellow>"));
                meta.lore(lore);
                compass.setItemMeta(meta);
            }
            player.getInventory().addItem(compass);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        List<Grave> graves = playerGraves.get(player.getUniqueId());
        if (graves == null || graves.isEmpty()) return;

        Iterator<Grave> it = graves.iterator();
        while (it.hasNext()) {
            Grave grave = it.next();
            if (grave.isClaimed()) {
                it.remove();
                continue;
            }

            if (grave.getLocation().getWorld().equals(player.getWorld()) &&
                grave.getLocation().distanceSquared(player.getLocation()) <= 2.25) {
                if (grave.claim(player)) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<gradient:#00c853:#00e5ff><bold>AquaForge</bold></gradient> <dark_gray>▏</dark_gray> <green>All items and experience from your grave restored!</green>"
                    ));
                    it.remove();
                }
            }
        }
    }

    public void tickAll() {
        for (List<Grave> list : playerGraves.values()) {
            list.removeIf(grave -> {
                grave.tick();
                return grave.isExpired() || grave.isClaimed();
            });
        }
    }

    public void cleanUpAll() {
        for (List<Grave> list : playerGraves.values()) {
            for (Grave grave : list) {
                grave.despawn();
            }
        }
        playerGraves.clear();
    }

    public Location getLastDeathLocation(UUID playerUuid) {
        return lastDeathLocations.get(playerUuid);
    }

    public List<Grave> getGraves(UUID playerUuid) {
        return playerGraves.getOrDefault(playerUuid, Collections.emptyList());
    }
}
