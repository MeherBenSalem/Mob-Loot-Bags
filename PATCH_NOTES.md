# Mob Loot Bags — Patch Notes

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
