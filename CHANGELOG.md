=============================================
## Changelog 1.10
=============================================

##CONFIG FILE NAME CHANGE `Mob Lootbags` -> `mlb`

## ✨ Changes & Improvements

- 🔁 **Switched config format**: All configuration files are now `.json` instead of `.toml`.
- ⚙️ **Dependency update**: Now using the powerful **Jauml** library. _(This is a required dependency!)_
- 🔄 **Live config updates**: Configs are now **dynamic** and can be changed in **real-time** without restarting.
- 🚀 **Performance boost**: Optimized the mod's performance and cleaned up around **30%** of unnecessary code.
- 🖼️ **Visual refresh**: changed the mod icon to a new design.


=============================================
## Changelog 1.9.0
=============================================

### 🔧 Configuration Changes
- ✅ **Whitelist and Blacklist Support**
  - Added support for **entity whitelisting/blacklisting** using the `NameRegistry` format (e.g. `"minecraft:cow"`).
  - Configure which entities can drop lootbags with precision.

- 📝 **Config File Updates**
  - Renamed the main configuration file to: `main.toml`
  - Renamed section:  
    `Bag Recycler Settings` ➡️ **`Bag Recycler XP Settings`**

=============================================
## Changelog 1.8.0
=============================================

## 🔒 NEW Locked Lootbags
- Consume **Diamonds Keys** to open
- Contains Netherite Armor/Tools Always

## 🧪 NEW Summoning Lootbags
- Grant a **random spawn egg** upon opening

## 🧿 NEW Cursed Lootbags
- Added **config option** to disable cursed lootbag drops
- New **funny sounds** added when opening
- Added config option to **disable cursed bag sounds**
- Now **replaces Treasure Chests** as the cursed alternative

## 🧳 Wandering Trader
- Wandering Trader now **trades Lootbags**

## 🛠 Fixes & Improvements
- Improved code logic for **better performance**
- Fixed issue where **lootbags could drop** even if drop rate was set to 0

## 🧾 Crafting
- **Downgrade recipes** added for converting higher-tier lootbags into lower ones

## ⏳ NEW Time-Warped Bags
- Opens depending on **in-game time**
- Chance to receive **Legendary** or **Epic Lootbags** after a random amount of time (5-10 minutes)
- Drop rates scale with that of **rare lootbags**

## ⚙ Config Updates
- Added options to **customize XP values** from the Lootbag Recycler
- All new lootbags have **configurable loot table names**

## 🧱 Block & UI Changes
- Updated **lootbag textures** for better visual appeal
- **Mod icon updated** for a fresh new look
