package com.aquaforge.survival.graves;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 1.21 Display Entity Animated Tombstone Grave.
 * Preserves items and XP securely with 3D models and floating timer HUD.
 * 
 * @author Turgut Efekan Koç (@efekkoc) - Founder & Chief Architect
 */
public class Grave {

    private final UUID id;
    private final UUID ownerUuid;
    private final String ownerName;
    private final Location location;
    private final List<ItemStack> items;
    private final int experience;
    private final long expireAtMillis;
    
    private BlockDisplay blockDisplay;
    private TextDisplay textDisplay;
    private boolean claimed = false;

    public Grave(UUID ownerUuid, String ownerName, Location location, List<ItemStack> items, int experience, int expireMinutes) {
        this.id = UUID.randomUUID();
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.location = location;
        this.items = new ArrayList<>(items);
        this.experience = experience;
        this.expireAtMillis = System.currentTimeMillis() + (expireMinutes * 60L * 1000L);
    }

    public void spawn() {
        if (location == null || location.getWorld() == null) return;

        Location spawnLoc = location.clone().add(0.5, 0.1, 0.5);

        // 1.21 BlockDisplay Tombstone
        blockDisplay = location.getWorld().spawn(spawnLoc, BlockDisplay.class, display -> {
            display.setBlock(Material.POLISHED_BLACKSTONE.createBlockData());
            display.setTransformation(new Transformation(
                    new Vector3f(-0.35f, 0.0f, -0.35f),
                    new AxisAngle4f(0, 0, 1, 0),
                    new Vector3f(0.7f, 1.1f, 0.7f),
                    new AxisAngle4f(0, 0, 1, 0)
            ));
        });

        // 1.21 TextDisplay Floating Hologram
        Location textLoc = location.clone().add(0.5, 1.4, 0.5);
        textDisplay = location.getWorld().spawn(textLoc, TextDisplay.class, text -> {
            text.text(MiniMessage.miniMessage().deserialize(
                    "<gradient:#ff1744:#ff9100><bold>" + ownerName + "'s Grave</bold></gradient>\n<gray>⏱ " + getRemainingMinutes() + "m remaining <dark_gray>▏</dark_gray> " + items.size() + " items</gray>"
            ));
            text.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
            text.setDefaultBackground(false);
        });

        location.getWorld().spawnParticle(Particle.SOUL, location.clone().add(0.5, 0.5, 0.5), 15, 0.2, 0.4, 0.2, 0.02);
    }

    public void tick() {
        if (claimed || textDisplay == null || !textDisplay.isValid()) return;

        if (isExpired()) {
            despawn();
            return;
        }

        // Particle aura
        Location pLoc = location.clone().add(0.5, 0.6, 0.5);
        pLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, pLoc, 1, 0.2, 0.2, 0.2, 0.01);

        // Update timer text
        textDisplay.text(MiniMessage.miniMessage().deserialize(
                "<gradient:#ff1744:#ff9100><bold>" + ownerName + "'s Grave</bold></gradient>\n<gray>⏱ " + getRemainingMinutes() + "m remaining <dark_gray>▏</dark_gray> " + items.size() + " items</gray>"
        ));
    }

    public boolean claim(Player player) {
        if (claimed) return false;

        for (ItemStack item : items) {
            if (item != null) {
                var leftover = player.getInventory().addItem(item);
                for (ItemStack drop : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                }
            }
        }

        if (experience > 0) {
            player.giveExp(experience);
        }

        claimed = true;
        despawn();

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, location.clone().add(0.5, 1.0, 0.5), 25, 0.3, 0.5, 0.3, 0.1);

        return true;
    }

    public void despawn() {
        if (blockDisplay != null && blockDisplay.isValid()) {
            blockDisplay.remove();
            blockDisplay = null;
        }
        if (textDisplay != null && textDisplay.isValid()) {
            textDisplay.remove();
            textDisplay = null;
        }
    }

    public long getRemainingMinutes() {
        return Math.max(0, (expireAtMillis - System.currentTimeMillis()) / (60 * 1000));
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expireAtMillis;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Location getLocation() {
        return location;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public boolean isClaimed() {
        return claimed;
    }
}
