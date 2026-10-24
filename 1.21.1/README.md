# Mob Loot Bags — Minecraft 1.21.1

Fabric and NeoForge workspace for Minecraft 1.21.1. Shared product docs live in the [repository root README](../README.md).

## Features (this workspace)

- Mob Loot Bags 2.0: six tiers (Common to Mythic) with Bag Luck, bad-luck protection, boss drops and bag carriers
- Special bags: Cursed, Hourglass (migrates 1.x Timed bags), Locked (Diamond Key) and Summoning; Fortune Talisman
- Bag Opener and Loot Recycler machines, reveal toasts, loot previews and loot beams
- `/lootbags` (alias `/mlb`): give, simulate, odds, stats, reload
- Commented JSON configs under `config/mob_loot_bags/` (1.x `config/mlb/` settings are migrated once)

## Requirements

- Java 21+
- Open this `1.21.1/` folder as the Gradle project root

## Build

```bash
./gradlew build
./gradlew :common:test
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Configuration

Generated on first launch in `config/mob_loot_bags/`:

- `drops.json`: who drops bags, how often and how good
- `bags.json`: opening bags, loot table overrides and the special bags
- `machines.json`: the Bag Opener and the Loot Recycler
- `client.json`: client-side visuals

## License

Apache License 2.0 — see [../LICENSE](../LICENSE) and [../NOTICE](../NOTICE).
