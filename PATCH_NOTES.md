# Mob Loot Bags — Patch Notes

## 1.11.2

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
