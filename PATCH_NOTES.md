# Mob Loot Bags — Patch Notes

## 1.11.3

### Fixes
- **CurseForge / Modrinth game-version metadata** — republish with the fixed publish detector (PR #11): `*-26.1.2-*` jars upload with Minecraft **26.1.2** game-version tags instead of **1.21.1**.

### No gameplay changes
- Mod behavior is unchanged from **1.11.2**. This release only corrects storefront metadata so each loader jar appears under the right Minecraft version filter.

## 1.11.2 (metadata maintenance)

### Fixes
- **Publish game-version tagging** — calendar Minecraft versions such as `26.1.2` are no longer mis-detected as `1.21.1` when uploading to Modrinth/CurseForge. The old detector only matched `26.X` (not `26.X.Y`) and fell back to `1.21.1`, so `mob_loot_bags-*-26.1.2-*.jar` files appeared under the 1.21.1 filter while still requiring Minecraft 26.1.2 / Java 25+.
- **NeoForge Minecraft ranges** — `1.20.1` workspace now declares `[1.20.1, 1.21)`; `1.21.1` declares `[1.21.1, 1.21.2)` (was open to `1.22)`).
- **Docs** — README “Which jar do I download?” table clarifies MC × loader filenames.

### Player guidance (1.21.1 Fabric)
- Prefer jars whose **filename** contains `fabric-1.21.1` (for example the May 29 **1.11.0** Fabric build, or a correctly named later `1.11.x` Fabric 1.21.1 jar).
- Ignore listings that show a `*-26.1.2-*.jar` under a 1.21.1 filter — that file is for Minecraft 26.1.2 only.

### Publisher follow-up
- Unlist or retag mis-labeled `*-26.1.2-*` files that still show game version 1.21.1 on Modrinth/CurseForge. Do not re-upload from this PR unless intentionally releasing.

## 1.11.2

### Additions
- **Minecraft 26.2 support** — Fabric and NeoForge jars for Minecraft 26.2 (mod version remains 1.11.2).

### Fixes
- **Minecraft 26.1.2 startup crash** — block/item registration now sets registry IDs (`Properties.setId`) on Fabric and NeoForge, fixing `Block id not set` / `Item id not set` / unbound `ResourceKey` crashes.
- **26.1.2 datapacks** — recipes under `data/.../recipe/` with 26.1 ingredient/`id` result schema; pickaxe mineable tag under `tags/block/`.
- **Opener / recycler break parity (26.1.2)** — container contents (and recycler stored XP) drop via block-entity `preRemoveSideEffects`, matching vanilla container behavior.

### Credits
- Registry ID fix approach contributed by **Sami (`0x-sami`)** in PR #9 (completed for Fabric + common in this release).

### Version note
- All workspaces ship as **1.11.2**. Code fixes above apply to the **26.1.2** workspace; 1.20.1 and 1.21.1 are version-aligned releases with no registry API change required.

## 1.11.1

### Fixes
- **Timed loot bags** now open the configured `timed_lt_name` loot table (`mob_loot_bags:time_warped_loot_table` by default) instead of running cursed-bag random commands (warden summons, trial spawners, etc.).
- **Blacklist / whitelist `enable` flag** is preserved across restarts; config load no longer rewrites user files with default values on every launch.
- **Mob drops** credit the attacking player for projectile kills (bows, tridents, etc.), not only direct melee hits.
- Added `timed_lt_name` to default `loot_tables.json` for configurable timed bag rewards.

### Issues addressed
- #7 — Timed bag only spawning wardens
- #6 — blacklist.json `enable` resets to false
- #4 — Bags not dropping (NeoForge 1.21.1 / projectile attribution)
