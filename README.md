# Mob Loot Bags

![Mob Loot Bags 2.0](marketing/images/banner.png)

Mobs drop loot bags in six tiers, from Common to Mythic. Hold to open them, preview what's inside, automate them
with the Bag Opener, grind mob-farm junk into new bags with the Loot Recycler, and gamble on Cursed, Hourglass and
Locked bags. Every drop rate, loot table and machine value is data-driven.

**2.0 "Spoils of the Hunt"** is a full rework: see the [2.0.0 notes](patchnotes/2.0.0.md).

## Which jar do I download?

Mob Loot Bags supports exactly these Minecraft versions and loaders (8 jars per release). Jar names are
`mob_loot_bags-{loader}-{minecraft}-{modVersion}.jar`. Match **both** the Minecraft version and the loader.

| Minecraft | Loaders | Java | Fabric jar (needs Fabric API) | Forge / NeoForge jar |
|-----------|---------|------|-------------------------------|----------------------|
| 1.20.1 | Fabric, Forge | 17+ | `…-fabric-1.20.1-….jar` | `…-forge-1.20.1-….jar` (Forge 47.2.30 or newer) |
| 1.21.1 | Fabric, NeoForge | 21+ | `…-fabric-1.21.1-….jar` | `…-neoforge-1.21.1-….jar` |
| 26.2 | Fabric, NeoForge | 25+ | `…-fabric-26.2-….jar` | `…-neoforge-26.2-….jar` |
| 26.3 | Fabric, NeoForge | 25+ | `…-fabric-26.3-….jar` | `…-neoforge-26.3-….jar` |

There is no NeoForge jar for 1.20.1: on 1.20.1 use the Forge jar (releases before 2.0.0 named it
`…-neoforge-1.20.1-…`). `26.2` and `26.3` are separate game versions from 1.21.1 and need Java 25+.

The mod is needed on both the server and the client.

## Features

- **Six tiers**: Common, Uncommon, Rare, Epic, Legendary and the new Mythic. Craft four bags into one of the next
  tier, or split one back into four.
- **Smarter drops**: an 18% base chance that scales with the mob's health, Bag Luck from Looting, Luck, mob health,
  the Nether, the End and Hard difficulty, bad-luck protection, boss drops (three Epic+ bags), bag-carrying mobs,
  a spawner penalty for farms, and themed Nether/End loot.
- **Opening**: hold Use to open (sneak for the whole stack), loot straight to the inventory, a reveal toast, titles
  and fanfares for Legendary and Mythic bags, Shift loot previews from the real loot tables, and loot beams on
  dropped bags.
- **Special bags**: Cursed (ambush, jackpot, hex or chaos), Hourglass (ripens into better bags), Locked (needs a
  Diamond Key) and Summoning (spawn eggs). The Fortune Talisman makes bags likelier and better.
- **Machines**: the Bag Opener (9 in / 18 out, four redstone modes, hopper I/O, comparator) and the Loot Recycler
  (junk to bags, bags to stored experience).
- **Progression**: 13 advancements, bag statistics and recipe unlocks.
- **For modpacks**: commented configs, datapack-overridable tables, `#mob_loot_bags:bonus/<tier>` item tags,
  per-mob multipliers, whitelist/blacklist, and `/lootbags` commands to test it all.

## Installation

1. Install Fabric (with Fabric API) for any supported version, Forge for 1.20.1, or NeoForge for 1.21.1, 26.2 or 26.3.
2. Download the matching jar from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mob-loot-bags)
   using the table above.
3. Put it in your `mods` folder (on the server and on every client).

## Configuration

On first launch the mod writes four commented files to `config/mob_loot_bags/`. Each value is explained in the file;
missing keys are restored with their defaults, and `/lootbags reload` applies changes without a restart.

| File | What it controls |
|------|------------------|
| `drops.json` | Drop chances, health scaling, tier weights, Bag Luck, special bag odds, themes, bad-luck protection, bosses, bag carriers, announcements, per-mob multipliers, mob filter |
| `bags.json` | Opening (hold time, stacks, toasts, titles), loot table overrides per bag, Cursed outcomes, Hourglass timing, Locked bag key, previews |
| `machines.json` | Bag Opener speed, Loot Recycler speed, points per bag, junk values and bag experience |
| `client.json` | Loot beams, reveal toast and preview tooltips (client only) |

### Loot tables and datapacks

- Each bag rolls `mob_loot_bags:bags/<bag>`; a datapack can replace any of them.
- Bags dropped in a dimension roll `mob_loot_bags:bags/<tier>/<dimension path>` when that table exists
  (the mod ships `the_nether` and `the_end`; add your own for modded dimensions).
- `bags.json` → `loot_tables` can point a bag at your own tables (several = one picked at random).
- Add items to `#mob_loot_bags:bonus/<tier>` to mix them into a tier without writing a table.
- [`templates/loot_tables_template.zip`](templates/loot_tables_template.zip) holds a starter datapack for every
  supported version.

### Commands

`/lootbags` (alias `/mlb`):

| Command | Who | What it does |
|---------|-----|--------------|
| `give <players> <bag> [count] [theme]` | ops | Gives bags (`common` … `mythic`, `cursed`, `hourglass`, `locked`, `summoning`) |
| `simulate <bag> [bags] [theme]` | ops | Opens a bag's table up to 10,000 times and lists each item's chance |
| `odds [mob]` | everyone | Shows a mob's drop chance and tier odds with your Bag Luck |
| `stats [player]` | everyone (others: ops) | Bags found and opened per tier, and bad-luck counters |
| `reload` | ops | Reloads the config files |

## Upgrading from 1.x

Item and block IDs are unchanged, so existing bags and machines stay in your world. Timed bags become Hourglass
bags, old machines are rearranged into the new slot layout, and customised settings in `config/mlb/` (drop rates,
whitelist/blacklist, cursed events, custom loot tables, recycler values) are copied into the new files once.

## Building

Each Minecraft version folder (`1.20.1/`, `1.21.1/`, `26.2/`, `26.3/`) is a separate MultiLoader Gradle workspace
with `common/` and its two loaders: `fabric/` + `forge/` on 1.20.1, `fabric/` + `neoforge/` elsewhere. Every
workspace's `gradle.properties` carries the same `version=`. The root Gradle build drives all four:

```bash
./gradlew buildAll        # build every workspace (unit tests included) and copy the 8 jars into releases/
./gradlew build263        # one workspace (keys: 1201, 1211, 262, 263)
./gradlew checkVersions   # fail unless every workspace has the same version=
./gradlew syncVersion -PmodVersion=x.y.z
```

Gradle for 1.20.1 and 1.21.1 runs on Java 21 (1.20.1 compiles to Java 17 bytecode with a JDK 17 toolchain); 26.x
needs Gradle itself on Java 25. Pass `-Pjava21Home=<jdk>` / `-Pjava25Home=<jdk>` or set `JAVA_HOME_21_X64` /
`JAVA_HOME_25_X64`. A single workspace still builds on its own with `./gradlew build` from its folder.

CI (`.github/workflows/ci.yml`) builds all 8 jars on every push to `main`, runs the unit and publisher-script tests
and checks the targets with `python3 scripts/check_targets.py workspaces`. Publishing
(`.github/workflows/publish.yml`) runs only by hand or on a `v*.*.*` tag, refuses a second release of the mod on the
same day (`scripts/same_day_guard.mjs`), and uploads exactly the 8 jars to CurseForge, each tagged with its own
Minecraft version and loader. It needs the secrets `CURSEFORGE_TOKEN` (upload token) and `CURSEFORGE_API_KEY` (Core API
key) and the variable `CURSEFORGE_ID`. NightBeam publishes to CurseForge only; the Modrinth page stays up but gets
no new versions. Release notes for a version live in `patchnotes/<version>.md`.

## Tools

- [`tools/assets/`](tools/assets): generates every texture, GUI, sound, model, blockstate, language file, loot
  table, recipe, tag and advancement for each Minecraft version from one source
  (`python3 tools/assets/build.py <version|all>`), plus the datapack template (`python3 tools/assets/template.py`).
- [`tools/qa/`](tools/qa): runs dev servers and a real game client under Xvfb. `server_smoke.py` checks drops,
  loot tables, machines, commands and reloads over RCON; `client_checks.py` checks opening, special bags,
  migration, machine screens and spawner tagging in game.
- [`marketing/`](marketing): the CurseForge description, listing images and how to regenerate them.

## Links

- [Discord](https://discord.gg/SAmd9fhNhr)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mob-loot-bags)
- [Docs](https://meherbensalem.github.io/pages/mods.html)
- [Support the project](https://nightbeam.dev/support)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Please follow the [Code of Conduct](CODE_OF_CONDUCT.md).

## Security

See [.github/SECURITY.md](.github/SECURITY.md).

## License

Licensed under the [Apache License, Version 2.0](LICENSE).
See [NOTICE](NOTICE) for copyright attribution.
