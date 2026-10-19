package com.aquaforge.survival.mechanics;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Fast Leaf Decay Mechanics.
 * Naturally decays floating foliage when trunk logs are harvested.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class FastLeafDecayListener implements Listener {

    private final Plugin plugin;

    public FastLeafDecayListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogBreak(BlockBreakEvent event) {
        Block broken = event.getBlock();
        if (!Tag.LOGS.isTagged(broken.getType())) return;

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            decayLeavesNear(broken);
        }, 3L);
    }

    private void decayLeavesNear(Block root) {
        Queue<Block> queue = new ArrayDeque<>();
        Set<Block> visited = new HashSet<>();

        // Add 6 direct neighbors
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    Block b = root.getRelative(x, y, z);
                    if (Tag.LEAVES.isTagged(b.getType())) {
                        queue.add(b);
                        visited.add(b);
                    }
                }
            }
        }

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            int processed = 0;
            while (!queue.isEmpty() && processed < 4) {
                Block leaf = queue.poll();
                if (leaf != null && Tag.LEAVES.isTagged(leaf.getType())) {
                    leaf.breakNaturally();
                    processed++;
                }
            }
            if (queue.isEmpty()) {
                task.cancel();
            }
        }, 1L, 2L);
    }
}
