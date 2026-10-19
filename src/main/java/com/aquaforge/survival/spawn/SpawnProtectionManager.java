package com.aquaforge.survival.spawn;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

/**
 * Enterprise Spawn Protection & Sanctuary Manager.
 * Eliminates hunger loss, fall damage, PvP and griefing within the spawn radius.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class SpawnProtectionManager implements Listener {

    private final Plugin plugin;
    private boolean enabled;
    private double radiusSquared;
    private Location spawnLocation;
    
    // Flags
    private boolean preventHunger;
    private boolean preventFallDamage;
    private boolean preventPvP;
    private boolean preventDamage;
    private boolean preventBlockBreak;
    private boolean preventBlockPlace;
    private boolean preventMobSpawning;

    public SpawnProtectionManager(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.enabled = config.getBoolean("spawn-protection.enabled", true);
        double radius = config.getDouble("spawn-protection.radius-blocks", 64.0);
        this.radiusSquared = radius * radius;

        String worldName = config.getString("spawn-protection.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null && !Bukkit.getWorlds().isEmpty()) {
            world = Bukkit.getWorlds().get(0);
        }

        if (world != null) {
            double x = config.getDouble("spawn-protection.x", world.getSpawnLocation().getX());
            double y = config.getDouble("spawn-protection.y", world.getSpawnLocation().getY());
            double z = config.getDouble("spawn-protection.z", world.getSpawnLocation().getZ());
            this.spawnLocation = new Location(world, x, y, z);
        }

        this.preventHunger = config.getBoolean("spawn-protection.flags.prevent-hunger", true);
        this.preventFallDamage = config.getBoolean("spawn-protection.flags.prevent-fall-damage", true);
        this.preventPvP = config.getBoolean("spawn-protection.flags.prevent-pvp", true);
        this.preventDamage = config.getBoolean("spawn-protection.flags.prevent-damage", true);
        this.preventBlockBreak = config.getBoolean("spawn-protection.flags.prevent-block-break", true);
        this.preventBlockPlace = config.getBoolean("spawn-protection.flags.prevent-block-place", true);
        this.preventMobSpawning = config.getBoolean("spawn-protection.flags.prevent-mob-spawning", true);
    }

    public boolean isInSpawn(Location loc) {
        if (!enabled || spawnLocation == null || loc == null || loc.getWorld() == null) {
            return false;
        }
        if (!loc.getWorld().equals(spawnLocation.getWorld())) {
            return false;
        }
        return loc.distanceSquared(spawnLocation) <= radiusSquared;
    }

    // 1. Spawnda Acıkmama / Yemek Yememe İhtiyacını Sıfırlama
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!preventHunger) return;
        if (event.getEntity() instanceof Player player) {
            if (isInSpawn(player.getLocation())) {
                event.setCancelled(true);
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
            }
        }
    }

    // 2. Düşme Hasarı ve Genel Hasar Engelleme
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (isInSpawn(player.getLocation())) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL && preventFallDamage) {
                    event.setCancelled(true);
                    return;
                }
                if (preventDamage) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // 3. Spawnda PvP ve Saldırı Engelleme
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!preventPvP) return;
        if (event.getEntity() instanceof Player victim) {
            if (isInSpawn(victim.getLocation()) || (event.getDamager() instanceof Player && isInSpawn(event.getDamager().getLocation()))) {
                event.setCancelled(true);
                if (event.getDamager() instanceof Player attacker) {
                    attacker.sendActionBar(MiniMessage.miniMessage().deserialize(
                            "<red>🛡️ PvP is disabled within the Spawn Sanctuary!</red>"
                    ));
                }
            }
        }
    }

    // 4. Spawnda Blok Kırma Engelleme
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!preventBlockBreak) return;
        Player player = event.getPlayer();
        if (player.hasPermission("aquasurvival.admin")) return;

        if (isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendActionBar(MiniMessage.miniMessage().deserialize(
                    "<red>🛡️ You cannot break blocks inside the Spawn Sanctuary!</red>"
            ));
        }
    }

    // 5. Spawnda Blok Koyma Engelleme
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!preventBlockPlace) return;
        Player player = event.getPlayer();
        if (player.hasPermission("aquasurvival.admin")) return;

        if (isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
            player.sendActionBar(MiniMessage.miniMessage().deserialize(
                    "<red>🛡️ You cannot place blocks inside the Spawn Sanctuary!</red>"
            ));
        }
    }

    // 6. Spawnda Zararlı Yaratık Doğmasını Engelleme
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!preventMobSpawning) return;
        if (event.getEntity() instanceof Monster) {
            if (isInSpawn(event.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    // 7. Giriş/Çıkış Bildirimleri & Açlık Reset
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        boolean fromIn = isInSpawn(event.getFrom());
        boolean toIn = isInSpawn(event.getTo());

        if (!fromIn && toIn) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize(
                    "<gradient:#00c853:#00e5ff><bold>🛡️ Entered Spawn Sanctuary</bold></gradient> <gray>(Hunger & PvP Disabled)</gray>"
            ));
            if (preventHunger) {
                player.setFoodLevel(20);
                player.setSaturation(20.0f);
            }
        } else if (fromIn && !toIn) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize(
                    "<gradient:#ffd700:#ff9100><bold>⚠️ Left Spawn Sanctuary</bold></gradient> <gray>(Wilderness Danger Active)</gray>"
            ));
        }
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
        plugin.getConfig().set("spawn-protection.world", location.getWorld().getName());
        plugin.getConfig().set("spawn-protection.x", location.getX());
        plugin.getConfig().set("spawn-protection.y", location.getY());
        plugin.getConfig().set("spawn-protection.z", location.getZ());
        plugin.saveConfig();
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }
}
