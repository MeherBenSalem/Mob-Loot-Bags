# Mob Loot Bags

Minecraft mod that adds tiered loot bags dropped by mobs, plus opener and recycler utility blocks. Bags range from Common through Legendary, with special cursed, timed, locked, and summoning variants.

## Which jar do I download?

Jar names are `mob_loot_bags-{loader}-{minecraft}-{modVersion}.jar`. Match **both** the Minecraft version and the loader.

| Minecraft | Java | Fabric jar | NeoForge / Forge jar |
|-----------|------|------------|----------------------|
| 1.20.1 | 17+ | `…-fabric-1.20.1-….jar` | `…-neoforge-1.20.1-….jar` (Forge-compatible on 1.20.1) |
| 1.21.1 | 21+ | `…-fabric-1.21.1-….jar` | `…-neoforge-1.21.1-….jar` |
| 26.1.2 | 25+ | `…-fabric-26.1.2-….jar` | `…-neoforge-26.1.2-….jar` |
| 26.2 | 25+ | `…-fabric-26.2-….jar` | `…-neoforge-26.2-….jar` |
| 26.3 | 25+ | `…-fabric-26.3-….jar` | `…-neoforge-26.3-….jar` |

**Do not** install a `26.1.2`, `26.2`, or `26.3` jar on Minecraft 1.21.1 (or the reverse). Those are different game versions; a 26.x jar declares `minecraft: 26.x` and needs Java 25+.

If a download page lists several files under “1.21.1”, open the file name and confirm it contains `-1.21.1-`, not `-26.1.2-`. Prefer the May 29 **1.11.0** Fabric 1.21.1 build or any later jar whose **filename** includes `fabric-1.21.1` (or `neoforge-1.21.1`).

> **Publisher note (1.11.1 / 1.11.2):** some `*-26.1.2-*.jar` files were mistakenly tagged as game version 1.21.1 on Modrinth/CurseForge by a publish-script bug. Those files still require Minecraft **26.1.2**. Hide or retag them on the storefronts; this repo no longer defaults unknown jars to 1.21.1.

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
| 26.3 | 25+ | Fabric, NeoForge |

## Installation

1. Install Fabric or NeoForge for your Minecraft version.
2. Download the matching jar from [Modrinth](https://modrinth.com/mod/mob-loot-bags/versions) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mob-loot-bags) using the table above.
3. Place the jar in your `mods` folder.

## Building

Each Minecraft version is a separate MultiLoader Gradle workspace:

```bash
cd 1.21.1
./gradlew build
```

Jars are written under each loader's `build/libs/` directory. Prebuilt release jars may also be placed in `releases/` for the publish workflow.

Publish metadata is derived from the jar **filename** (see `.github/scripts/detect-jar-meta.js`). Run `node .github/scripts/detect-jar-meta.test.js` to verify detection.

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
