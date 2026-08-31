# Mob Loot Bags

Minecraft mod that adds tiered loot bags dropped by mobs, plus opener and recycler utility blocks. Bags range from Common through Legendary, with special cursed, timed, locked, and summoning variants.

## Features

- Configurable mob drop rates and loot tables
- Tiered bags: Common, Uncommon, Rare, Epic, Legendary
- Special bags: Cursed, Timed, Locked, Summoning
- Loot Bag Opener block (redstone-driven auto-open)
- Loot Bag Recycler block (bags to experience)
- JSON configs under `config/mlb/` (drop rates, loot tables, whitelist/blacklist, recycler)

## Requirements

| Minecraft | Java | Loaders |
|-----------|------|---------|
| 1.20.1 | 17+ | Fabric, NeoForge |
| 1.21.1 | 21+ | Fabric, NeoForge |
| 26.1.2 | 25+ | Fabric, NeoForge |
| 26.2 | 25+ | Fabric, NeoForge |

## Installation

1. Install Fabric or NeoForge for your Minecraft version.
2. Download the matching jar from [Modrinth](https://modrinth.com/mod/mob-loot-bags/versions) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mob-loot-bags).
3. Place the jar in your `mods` folder.

## Building

Each Minecraft version is a separate MultiLoader Gradle workspace:

```bash
cd 1.21.1
./gradlew build
```

Jars are written under each loader's `build/libs/` directory. Prebuilt release jars may also be placed in `releases/` for the publish workflow.

## Configuration

On first launch the mod creates files under `config/mlb/`, including:

- `drop_rates.json`
- `loot_tables.json`
- `special_bags.json`
- `whitelist.json` / `blacklist.json`
- `bag_recycler.json`

A starter loot-table zip is available in `templates/`.

## Links

- [Discord](https://discord.gg/SAmd9fhNhr)
- [Modrinth](https://modrinth.com/mod/mob-loot-bags/versions)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mob-loot-bags)
- [Docs](https://meherbensalem.github.io/pages/mods.html)
- [Ko-fi](https://ko-fi.com/nightbeamstudio)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Please follow the [Code of Conduct](CODE_OF_CONDUCT.md).

## Security

See [.github/SECURITY.md](.github/SECURITY.md).

## License

Licensed under the [Apache License, Version 2.0](LICENSE).
See [NOTICE](NOTICE) for copyright attribution.
