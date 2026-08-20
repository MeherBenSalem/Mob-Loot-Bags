# Mob Loot Bags — Minecraft 1.21.1

Fabric and NeoForge workspace for Minecraft 1.21.1. Shared product docs live in the [repository root README](../README.md).

## Features (this workspace)

- Custom loot bags with configurable drop rates
- Tiered bags: Common, Uncommon, Rare, Epic, Legendary
- Special bags: Cursed, Timed, Locked, Summoning
- Loot Bag Opener and Recycler blocks
- JSON configs under `config/mlb/`

## Requirements

- Java 21+
- Open this `1.21.1/` folder as the Gradle project root

## Build

```bash
./gradlew build
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Configuration

Generated on first launch in `config/mlb/`:

- `drop_rates.json`
- `loot_tables.json`
- `special_bags.json`
- `whitelist.json` / `blacklist.json`
- `bag_recycler.json`

## License

Apache License 2.0 — see [../LICENSE](../LICENSE) and [../NOTICE](../NOTICE).
