"""Builds templates/loot_tables_template.zip: one starter datapack per Minecraft version, emitted from the same loot DSL
as the mod's own tables so every folder name and JSON key matches that version.

  python3 tools/assets/template.py
"""
from __future__ import annotations

import io
import json
import sys
import zipfile
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from data import PROFILES  # noqa: E402
from loot import I, Pool, Table, emit  # noqa: E402

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / 'templates' / 'loot_tables_template.zip'

# Data pack format of each supported version (from the game's version.json).
PACK = {
    '1.20.1': {'pack_format': 15},
    '1.21.1': {'pack_format': 48},
    '26.1.2': {'min_format': 101, 'max_format': 101},
    '26.2': {'min_format': 107, 'max_format': 107},
    '26.3': {'min_format': 121, 'max_format': 121},
}

# A custom table for bags.json -> loot_tables.legendary.
JACKPOT = Table([
    Pool(1, [I('nether_star', 2), I('elytra', 1), I('beacon', 1), I('enchanted_golden_apple', 3, (2, 4))]),
    Pool((2, 4), [
        I('diamond_block', 4, (1, 3)), I('netherite_ingot', 3, (1, 2)), I('emerald_block', 4, (2, 5)),
        I('experience_bottle', 5, (16, 32)), I('totem_of_undying', 2),
    ]),
    Pool(1, [I('music_disc_pigstep'), I('music_disc_otherside')], chance=0.25),
])

# A themed table: Rare bags dropped in a modded dimension whose id ends in "the_aether".
AETHER_RARE = Table([
    Pool((2, 3), [
        I('feather', 6, (4, 10)), I('glowstone_dust', 5, (4, 12)), I('golden_apple', 3, (1, 2)),
        I('gold_ingot', 5, (2, 6)), I('diamond', 2, (1, 2)), I('ender_pearl', 3, (1, 3)),
    ]),
])

BONUS_RARE = {'replace': False, 'values': ['minecraft:name_tag', 'minecraft:saddle', 'minecraft:spyglass']}

README = """Mob Loot Bags 2.0 - starter datapack
====================================

Pick the folder for your Minecraft version and copy it into <world>/datapacks/ (or a modpack's global datapack
folder), then run /reload. Each folder is a complete datapack in that version's format (1.20.1 uses the plural
folder names loot_tables/ and tags/items/; the paths below use the newer names).

What it shows:

1. A custom table, data/my_pack/{lt}/legendary_jackpot.json.
   Nothing uses it until you point a bag at it in config/mob_loot_bags/bags.json:
       "loot_tables": {{ "legendary": ["my_pack:legendary_jackpot"], ... }}
   List several tables to pick one at random each time. Then /lootbags reload.

2. A dimension theme, data/mob_loot_bags/{lt}/bags/rare/the_aether.json.
   Bags roll mob_loot_bags:bags/<bag>/<dimension path> when it exists, so Rare bags dropped in a dimension
   called <anything>:the_aether use this table. The mod ships themes for the_nether and the_end; a file with the
   same path in your datapack replaces them, and mob_loot_bags:bags/<bag> replaces a bag's default table.

3. Bonus items, data/mob_loot_bags/{tags}/bonus/rare.json.
   Every tier rolls a bonus pool from #mob_loot_bags:bonus/<tier>. Add items there (keep "replace": false)
   to mix modded items into bags without rewriting any table.

Check your work in game:
    /lootbags simulate rare 1000            rolls the table a Rare bag would use and lists the odds
    /lootbags simulate rare 1000 the_aether the same, for the themed table
Hold Shift over a bag to see the same odds in its tooltip (refreshed on /reload).
"""


def build():
    buf = io.BytesIO()
    with zipfile.ZipFile(buf, 'w', zipfile.ZIP_DEFLATED) as zf:
        def add(path, text):
            info = zipfile.ZipInfo(path, date_time=(2026, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            zf.writestr(info, text)

        add('README.txt', README.format(lt='loot_table', tags='tags/item'))
        for version, profile in PROFILES.items():
            base = f'mob_loot_bags_starter_{version}/'
            lt, tags = profile.dir('loot_table'), profile.dir('tags/item')
            files = {
                'pack.mcmeta': {'pack': {'description': f'Mob Loot Bags starter datapack ({version})', **PACK[version]}},
                f'data/my_pack/{lt}/legendary_jackpot.json': emit(JACKPOT, profile.loot),
                f'data/mob_loot_bags/{lt}/bags/rare/the_aether.json': emit(AETHER_RARE, profile.loot),
                f'data/mob_loot_bags/{tags}/bonus/rare.json': BONUS_RARE,
            }
            for path, value in files.items():
                add(base + path, json.dumps(value, indent=2) + '\n')
    OUT.write_bytes(buf.getvalue())
    print(f'wrote {OUT.relative_to(ROOT)} ({len(PROFILES)} versions)')


if __name__ == '__main__':
    build()
