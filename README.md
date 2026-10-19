# 🪦 AquaSurvival — Enterprise Survival & SMP Mechanics Engine

<p align="left">
  <a href="https://github.com/aquaforge-mc"><img src="https://img.shields.io/badge/Holding_Brand-AquaForge%20Ecosystem-00c853?style=for-the-badge&logo=github&logoColor=white" alt="AquaForge Ecosystem" /></a>
  <a href="https://github.com/efekkoc"><img src="https://img.shields.io/badge/Chief%20Architect-%40efekkoc-38bdf8?style=for-the-badge" alt="Chief Architect" /></a>
  <a href="https://github.com/aquaforge-mc"><img src="https://img.shields.io/badge/PaperMC-1.20.x%20--%201.21.11-E53935?style=for-the-badge&logo=curseforge&logoColor=white" alt="PaperMC 1.20.x - 1.21.11" /></a>
  <a href="https://github.com/efekkoc/AquaSurvival"><img src="https://img.shields.io/badge/Version-1.0.0%20Release-blueviolet?style=for-the-badge" alt="Version 1.0.0" /></a>
</p>

**AquaSurvival** is an advanced, high-performance survival mechanics and SMP essentials suite engineered as the dedicated **Survival Division** of the **[AquaForge Ecosystem](https://github.com/aquaforge-mc)**. Universal compatibility across **Paper 1.20.x through 1.21.11 (Mounts of Mayhem Ready)** with zero external dependencies.

---

### ⚡ Architectural Features

* 🪦 **1.21 Display Entity Gravestones:**
  * When a player dies, items and experience are safely sealed into a 3D animated `BlockDisplay` tombstone with floating `TextDisplay` timer holograms.
  * Respawning players receive a dynamic **Soul Recovery Compass** pointing straight to their death coordinates.
  * Walking over the tombstone instantly restores items and experience with particle bursts.
* 🌙 **Dynamic Multiplayer Sleep Vote:**
  * Smooth, configurable percentage-based sleeping with a real-time **MiniMessage BossBar** countdown.
  * Transitions world time smoothly to morning and clears weather when criteria are met.
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
| `/graves` | `aquasurvival.user` | Inspect your active death gravestones and coordinates |
| `/back` | `aquasurvival.back` | Teleport back to your recent death coordinates |

---

### 🛠️ Configuration (`config.yml`)

```yaml
graves:
  enabled: true
  expire-time-minutes: 30
  give-compass-on-respawn: true

sleep:
  needed-percent: 25
  bossbar-countdown-seconds: 5

mechanics:
  silk-touch-spawners: true
  fast-leaf-decay: true
  elevators:
    enabled: true
    block-type: "WHITE_WOOL"
```

---

### 👨‍💻 Author & Attribution

* **Holding Brand:** [AquaForge Ecosystem](https://github.com/aquaforge-mc)
* **Founder & Chief Architect:** [Turgut Efekan Koç (@efekkoc)](https://github.com/efekkoc)
* **Licensing:** MIT Open Source
