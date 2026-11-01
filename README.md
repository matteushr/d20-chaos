# 🎲 D20 Chaos — Minecraft Mod

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)
![Modloader](https://img.shields.io/badge/NeoForge-21.1.256-orange.svg)
![Java Version](https://img.shields.io/badge/Java-21-blue.svg)
![License](https://img.shields.io/badge/License-MIT-purple.svg)

> **Transform Minecraft survival into an unpredictable casino of fate.**  
> Every routine action — walking, mining, placing blocks, fighting, or crafting — is at the mercy of the D20 die.

---

## 📖 Overview

**D20 Chaos** is a high-octane gameplay mod built for **Minecraft 1.21.1 (NeoForge)** that introduces a two-tier risk-reward system governed by D20 dice rolls:

1. **Macro Layer (General World Decision — 20-Minute Cycle):**
   - Every 20 minutes (24,000 ticks), the master die rolls on screen with an ominous sound, dictating the **Global World Tier** and atmosphere.
   - Fully configurable in real time via `/d20 timer <seconds>` and `/d20 cycle <seconds>`.

2. **Micro Layer (Action Rolls — 4% Trigger Rate):**
   - Whenever a player performs an essential action (walking 1 block, breaking a block, placing a block, striking a mob, or crafting an item), the D20 die gently rolls in the HUD.
   - There is a **4% chance (0.04f)** of triggering a **deterministic event** matching the archetype currently displayed on the active action card.
   - When the 4% trigger activates, the die locks the rolled number for 3 seconds with a visual scale pulse, celestial sound, radial particle burst, and a floating badge `★ TRIGGER: [EVENT]`.

---

## 🎨 Clean Pixel Art HUD & Visual Identity

The HUD in D20 Chaos is tailored for high readability and complete non-intrusiveness:

- **Top-Left Action Cards:**
  - Status banner displaying the active Tier (`DIVINE MODE`, `CURSE OF CALAMITY`, etc.), countdown timer (`02:45`), Chaos Level, and Trigger Rate.
  - 5 bounded cards with clear left-right alignment: `Mining`, `Movement`, `Building`, `Combat`, and `Crafting`.
  - Dedicated trigger card tracking the last activated outcome.

- **Pixel-Perfect D20 Die (1:1 Native 32x32 Scale):**
  - Floats cleanly above the hearts and hunger bar (`screenHeight - 78`).
  - Native 1:1 sampling: **zero subpixel interpolation, zero blurry fringes or edge artifacts**.
  - **Dedicated Micro Pixel Art Font (3x5):** Rolled numbers (1 through 20) are rendered in the exact center of the inverted triangular face with relief drop-shadow, using the authentic lilac-white palette (`#BEAAC3`) and deep shadow (`#110419`).
  - **Living Celestial Particle Aura:** 5-pixel crosses (`+`), 2x2 stardust blocks, and twinkling glint motes orbit and breathe smoothly around the die, exploding outward in an 8-way radial nova during action triggers.

---

## ⚡ The 5 World Tiers & Event Matrix

| Tier | D20 Range | Tier Name | Color | World Humor & Active Modules |
| :---: | :---: | :--- | :---: | :--- |
| **Tier 1** | **1 to 3** | **Mortal Calamity** | 🔴 Crimson | **Extreme Hazard:**<br>• `InescapableAnvilTrap` (Movement): Snaps to block center, triple jump-lock, and a heavily damaged anvil crashes and shatters on impact.<br>• `AbyssalNestTrap` (Mining): Fractures rock to spawn a Vex and a hostile Cave Spider attacking the miner.<br>• `UnstableGravityTrap` (Building): Placed block loses physical support and collapses into falling sand physics.<br>• `ReverseThornsTrap` (Combat): Reflects 50% of dealt melee damage straight back to the player.<br>• `ScrapCraftTrap` (Crafting): Scraps workstation materials in toxic smoke without producing the crafted item. |
| **Tier 2** | **4 to 9** | **Sabotage & Misfortune** | 🟠 Rust Orange | **Penalties & Hazards:**<br>• `StumbleDebuff` (Movement): Involuntary forward/lateral stumble with Slowness I.<br>• `ToxicGasDebuff` (Mining): Subterranean gas pocket ruptures, inflicting Nausea I and Poison I.<br>• `BrittleBlockDebuff` (Building): Fragile placement causes the placed block to instantly crumble with glass shattering sound.<br>• `LoweredGuardDebuff` (Combat): Weapon bounces awkwardly, taking extra durability damage + Weakness I.<br>• `ToolWearDebuff` (Crafting): Workstation friction chips equipped tools with durability loss. |
| **Tier 3** | **10 to 11** | **Neutral Equilibrium** | ⚪ Silver Slate | **Vanilla Breathing Window:**<br>• `SereneBreezeNeutral`: Cosmic starlight breeze with End Rod particles and gentle amethyst chime. Zero penalties and zero buffs. |
| **Tier 4** | **12 a 17** | **Favorable Winds** | 🟢 Emerald | **Blessings & Abundance:**<br>• `MomentumBuff` (Movement): Gust of wind granting Speed II and Jump Boost II for 15s.<br>• `RichVeinBuff` (Mining): Reveals mineral abundance with Haste II for 20s and duplicate ore drops.<br>• `ArchitectBuff` (Building): Instantly refunds the placed block back into the inventory + Resistance I.<br>• `HunterBuff` (Combat): Vampiric critical strike healing 2 full hearts and granting Strength II.<br>• `ReinforcedCraftBuff` (Crafting): Immediately repairs 40 durability on mainhand tool + grants 40 XP. |
| **Tier 5** | **18 to 20** | **God Mode (Divine)** | 🟡 Gold / Purple | **Cosmic Supremacy:**<br>• `HermesStepsBuff` (Movement): Speed III, Jump Boost III, golden speed trails, and surface freezing.<br>• `MidasTransmutationBuff` (Mining): Transmutes surrounding stone and deepslate into solid Gold and Diamond blocks.<br>• `FreeBlockBuff` (Building): Unlimited architecture with a chance to duplicate blocks in inventory.<br>• `ThorExecutionBuff` (Combat): Calls down targeted lightning executing hostile mobs and dropping monster heads.<br>• `MasterpieceCraftBuff` (Crafting): Infuses crafted item with bonus enchantments and showers 100 XP. |

---

## 🛡️ Kinetic Forcefield System (`ForcefieldManager`)

To protect bases and player builds from destructive chaotic events and explosions, D20 Chaos features a pure **Kinetic Energy Forcefield**:
- Dynamically seals the perimeter around the player without altering or replacing world terrain blocks.
- Uses elastic repulsion physics and particle barriers to contain hazard radius.
- **Anti-Griefing Explosion Boundary (`clearToBlow`):** Cancels block destruction damage from internal blasts, keeping chests, redstone, and structures completely safe.

---

## 💻 In-Game Commands

All commands can be executed in chat (by operators or in singleplayer):

| Command | Description |
| :--- | :--- |
| `/d20 timer <seconds>` | Sets remaining time for the current macro cycle (e.g. `/d20 timer 30`). |
| `/d20 cycle <seconds>` | Sets default macro cycle duration (default: 1200s = 20 min). |
| `/d20 nextCycle` | Instantly rolls the D20 master die and shifts to the next global tier. |
| `/d20 setTier <1-20>` | Forces the macro decision to a specific roll (1 through 20). |
| `/d20 chance <0.0-1.0>` | Adjusts the micro action trigger chance (e.g. `/d20 chance 1.0` for 100% test rolls). |
| `/d20 trigger <event_id>` | Manually triggers any registered module by ID. |
| `/d20 list` | Lists all registered action modules and their categories. |
| `/d20 status` | Displays full player status (Tier, Chaos, Remaining Time, Trigger Chance). |

---

## 🛠️ Building From Source

### Prerequisites
- **Java 21** JDK installed.
- **Python 3.10+** (used by the standalone compilation pipeline).
- Minecraft 1.21.1 client with NeoForge 21.1.256.

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/matteushr/d20-chaos.git
cd d20-chaos

# Compile and package
python build.py
```
The build script automatically compiles all sources with UTF-8 encoding, merges all texture/sound assets, and generates the mod `.jar` in `build/d20chaos-1.0.0.jar`.

---

## 🤝 Contributing & Issue Reporting

We welcome bug reports, gameplay suggestions, and pull requests!

### 1. Reporting Bugs 🐛
If you experience an issue or crash:
1. Navigate to the **[Issues](https://github.com/matteushr/d20-chaos/issues)** tab on GitHub.
2. Click **New Issue** and select the **Bug Report** template.
3. Fill in the reproduction steps, current Tier, and attach relevant lines from your `latest.log` containing `[D20]`.

### 2. Proposing Events & Calamities 🎲
Have an idea for a creative trap, monster encounter, or divine blessing?
1. Open an issue using the **New Event Proposal** template.
2. Specify the target **Tier** (1 to 5) and action **Category** (`MINING`, `MOVEMENT`, `BLOCK_PLACE`, `COMBAT_EXPLORE`, `CRAFTING`).
3. Describe the intended mechanic, visual cues, and gameplay outcome!

### 3. Pull Requests 🚀
1. Fork the repository and create a feature branch: `git checkout -b feature/my-cool-event`.
2. Follow the modular `ID20ActionEvent` contract.
3. Verify compilation via `python build.py`.
4. Submit a clear, well-documented Pull Request.

---

## 📜 License

Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.
