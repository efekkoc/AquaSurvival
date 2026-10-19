package com.aquaforge.survival.mechanics;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Modern Silk Touch Spawner Harvesting & Placement Engine.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class SilkSpawnerListener implements Listener {

    private final NamespacedKey spawnerKey;

    public SilkSpawnerListener(Plugin plugin) {
        this.spawnerKey = new NamespacedKey(plugin, "spawner_mob_type");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            event.setExpToDrop(0);
            event.setDropItems(false);

            if (block.getState() instanceof CreatureSpawner spawner) {
                EntityType type = spawner.getSpawnedType();
                String mobName = type != null ? type.name() : "PIG";

                ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
                ItemMeta meta = spawnerItem.getItemMeta();
                if (meta != null) {
                    meta.displayName(MiniMessage.miniMessage().deserialize(
                            "<gradient:#ffd700:#ff9100><bold>" + formatName(mobName) + " Spawner</bold></gradient>"
                    ));
                    meta.lore(List.of(
                            MiniMessage.miniMessage().deserialize("<gray>Mined with Silk Touch.</gray>"),
                            MiniMessage.miniMessage().deserialize("<yellow>Entity Type: <white>" + mobName + "</white></yellow>")
                    ));
                    meta.getPersistentDataContainer().set(spawnerKey, PersistentDataType.STRING, mobName);
                    spawnerItem.setItemMeta(meta);
                }

                block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);
                player.sendActionBar(MiniMessage.miniMessage().deserialize(
                        "<gradient:#00c853:#00e5ff><bold>AquaForge</bold></gradient> <dark_gray>▏</dark_gray> <green>Harvested " + formatName(mobName) + " Spawner!</green>"
                ));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.SPAWNER) return;

        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String mobType = meta.getPersistentDataContainer().get(spawnerKey, PersistentDataType.STRING);
        if (mobType != null && block.getState() instanceof CreatureSpawner spawner) {
            try {
                EntityType type = EntityType.valueOf(mobType);
                spawner.setSpawnedType(type);
                spawner.update();
            } catch (Exception ignored) {}
        }
    }

    private String formatName(String raw) {
        String[] parts = raw.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
