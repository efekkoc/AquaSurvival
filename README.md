# 🪦 AquaSurvival — Enterprise Survival, Spawn Sanctuary & SMP Engine

<p align="left">
  <a href="https://github.com/aquaforge-mc"><img src="https://img.shields.io/badge/Holding_Brand-AquaForge%20Ecosystem-00c853?style=for-the-badge&logo=github&logoColor=white" alt="AquaForge Ecosystem" /></a>
  <a href="https://github.com/efekkoc"><img src="https://img.shields.io/badge/Chief%20Architect-%40efekkoc-38bdf8?style=for-the-badge" alt="Chief Architect" /></a>
  <a href="https://github.com/aquaforge-mc"><img src="https://img.shields.io/badge/PaperMC-1.20.x%20--%201.21.11-E53935?style=for-the-badge&logo=curseforge&logoColor=white" alt="PaperMC 1.20.x - 1.21.11" /></a>
  <a href="https://github.com/efekkoc/AquaSurvival"><img src="https://img.shields.io/badge/Version-1.0.0%20Release-blueviolet?style=for-the-badge" alt="Version 1.0.0" /></a>
</p>

**AquaSurvival** is a high-performance survival mechanics, spawn sanctuary, and SMP essentials suite engineered as the dedicated **Survival Division** of the **[AquaForge Ecosystem](https://github.com/aquaforge-mc)**. Universal compatibility across **Paper 1.20.x through 1.21.11 (Mounts of Mayhem Ready)** with zero external dependencies.

---

### ⚡ Architectural Features

* 🛡️ **Spawn Sanctuary (No Hunger & No Damage):**
  * **No Hunger Loss:** Players in the spawn sanctuary never lose hunger, eliminating the need to consume food at spawn (automatic 20 food level & infinite saturation).
  * **No Fall Damage:** Completely negates vertical drop damage inside the spawn hub.
  * **No PvP / Griefing:** Blocks player-vs-player combat, griefing, and hostile monster spawning in spawn.
* 🌙 **50–100+ Player One-Player Sleep:**
  * Tailored for high-population multiplayer servers where 50–100 people cannot all sleep simultaneously.
  * **A single player entering a bed** triggers a sleek MiniMessage **BossBar** countdown (`🌙 %player% is sleeping! Skipping night in 4s...`), smoothly transitioning the entire server to morning and clearing storms.
* 🪦 **1.21 Display Entity Gravestones:**
  * When a player dies, items and experience are safely sealed into a 3D animated `BlockDisplay` tombstone with floating `TextDisplay` timer holograms.
  * Respawning players receive a dynamic **Soul Recovery Compass** pointing straight to their death coordinates.
  * Walking over the tombstone instantly restores items and experience with particle bursts.
* ⛏️ **Silk Touch Spawner Mining:**
  * Harvest spawners with Silk Touch pickaxes, preserving the exact entity type (`CreatureSpawner`) via modern PersistentDataContainer metadata.
* 🍃 **Fast Leaf Decay:**
  * Naturally and rapidly decays floating tree foliage when trunk logs are broken.
* 🪜 **Wool Elevators:**
  * Jump to ascend, sneak to descend between identical elevator blocks with teleportation sound and portal particle effects.

---

### 📜 Commands & Permissions

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/survival` | `aquasurvival.user` | View active survival suite mechanics |
| `/survival setspawn` | `aquasurvival.admin` | Set Spawn Sanctuary coordinates to your location |
| `/graves` | `aquasurvival.user` | Inspect your active death gravestones and coordinates |
| `/back` | `aquasurvival.back` | Teleport back to your recent death coordinates |

---

### 🛠️ Configuration (`config.yml`)

```yaml
spawn-protection:
  enabled: true
  radius-blocks: 64.0
  flags:
    prevent-hunger: true          # Spawnda acıkmama ve sürekli tok kalma
    prevent-fall-damage: true     # Düşme hasarını engelleme
    prevent-pvp: true             # PvP engelleme
    prevent-damage: true          # Her türlü hasarı engelleme
    prevent-block-break: true     # Blok kırmayı engelleme
    prevent-block-place: true     # Blok koymayı engelleme
    prevent-mob-spawning: true    # Canavar doğmasını engelleme

sleep:
  enabled: true
  mode: "ONE_PLAYER"              # 50-100 kişilik sunucularda 1 kişi yettiğinde geceyi atlar
  min-players-needed: 1
  bossbar-countdown-seconds: 4

graves:
  enabled: true
  expire-time-minutes: 30
  give-compass-on-respawn: true
```

---

### 👨‍💻 Author & Attribution

* **Holding Brand:** [AquaForge Ecosystem](https://github.com/aquaforge-mc)
* **Founder & Chief Architect:** [Turgut Efekan Koç (@efekkoc)](https://github.com/efekkoc)
* **Licensing:** MIT Open Source
