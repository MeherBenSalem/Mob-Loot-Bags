# Mob Loot Bags

Mob loot bag system with configurable drop rates, loot tables, and utility blocks.

**Minecraft Version:** 1.21.1
**Supported Loaders:** Fabric, NeoForge
**Forge:** Removed (migrated to NeoForge)

## Features

- Custom loot bags drop from mobs with configurable drop rates
- Tiered loot bags: Common, Uncommon, Rare, Epic, Legendary
- Special bags: Cursed, Timed, Locked, Summoning
- Loot Bag Opener block — uses redstone signal to auto-open bags
- Loot Bag Recycler block — converts bags into experience
- Fully configurable via JSON config files (config/mlb/)
- Whitelist/Blacklist system for mob drops

## Project Structure

```
├── common/         # Shared code (vanilla Minecraft only)
├── fabric/         # Fabric-specific code & entry point
├── neoforge/       # NeoForge-specific code & entry point
├── buildSrc/       # Gradle build logic
└── build.gradle    # Root build config
```

## Setup

### Prerequisites
- Java 21 JDK
- Git

### IntelliJ IDEA
1. Clone this repository.
2. Open the `1.21.1/` folder as a project in IDEA.
3. Ensure Project SDK is set to Java 21 (File > Project Structure > Project SDK).
4. Run the Gradle sync (refresh Gradle project).
5. Run the `genSources` task if you need decompiled source access.

## Building

```bash
# Build all loaders
./gradlew build

# Build only Fabric
./gradlew :fabric:build

# Build only NeoForge
./gradlew :neoforge:build
```

## Running

### Fabric
```bash
./gradlew :fabric:runClient
```

### NeoForge
```bash
./gradlew :neoforge:runClient
```

## Configuration

Config files are generated in `config/mlb/` on first launch:

- `drop_rates.json` — Drop rate percentages per tier
- `loot_tables.json` — Which loot tables each bag uses
- `special_bags.json` — Cursed bag events & settings
- `whitelist.json` / `blacklist.json` — Mob filtering
- `bag_recycler.json` — XP values per bag type

## License

CC0-1.0
