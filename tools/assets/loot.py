"""Loot tables for every bag, written once in a small DSL and emitted in each Minecraft version's JSON schema.

Profiles:
  v3  26.3      entries use "modifier"/"condition", tag entries use "items"
  v2  1.21-26.2 entries use "functions"/"conditions", loot_table entries use "value"
  v1  1.20.1    like v2, but loot_table entries use "name" and enchanting uses "treasure"
Items marked since='1.21' are left out of the 1.20.1 tables.
"""
from __future__ import annotations

from dataclasses import dataclass, field

TIERS = ['common', 'uncommon', 'rare', 'epic', 'legendary', 'mythic']
VERSION_ORDER = {'1.20': 0, '1.21': 1, '26': 2}


@dataclass
class Item:
    name: str
    weight: int = 1
    count: tuple | int | None = None
    levels: tuple | int | None = None        # enchant_with_levels
    random_enchant: bool = False             # enchant_randomly
    potion: str | None = None
    damage: tuple | None = None
    since: str = '1.20'


@dataclass
class Tag:
    name: str
    weight: int = 1
    since: str = '1.20'


@dataclass
class Ref:
    table: str
    weight: int = 1


@dataclass
class Pool:
    rolls: tuple | int
    entries: list
    chance: float | None = None
    bonus_rolls: float | None = None


@dataclass
class Table:
    pools: list = field(default_factory=list)


def I(name, weight=1, count=None, **kw):
    if ':' not in name:
        name = 'minecraft:' + name
    return Item(name, weight, count, **kw)


# ------------------------------------------------------------------ content

def gear(material, pieces, weight, levels, since='1.20'):
    return [I(f'{material}_{p}', weight, levels=levels, since=since) for p in pieces]


ARMOR = ['helmet', 'chestplate', 'leggings', 'boots']
TOOLS = ['sword', 'pickaxe', 'axe']


def bonus(tier, chance):
    return Pool(1, [Tag(f'#mob_loot_bags:bonus/{tier}')], chance=chance)


COMMON = Table([
    Pool((2, 3), [
        I('bread', 12, (1, 3)), I('cooked_beef', 8, (1, 2)), I('cooked_porkchop', 8, (1, 2)), I('cooked_chicken', 8, (1, 3)),
        I('baked_potato', 8, (2, 4)), I('apple', 8, (1, 3)), I('melon_slice', 4, (3, 8)), I('sweet_berries', 4, (3, 8)),
        I('torch', 10, (4, 12)), I('arrow', 10, (4, 12)), I('coal', 10, (2, 6)), I('iron_nugget', 10, (3, 9)),
        I('gold_nugget', 6, (2, 6)), I('copper_ingot', 8, (1, 4)), I('string', 8, (2, 5)), I('leather', 8, (1, 3)),
        I('flint', 5, (1, 3)), I('bone_meal', 6, (2, 6)), I('feather', 5, (2, 5)), I('paper', 5, (2, 5)),
        I('oak_log', 6, (2, 6)), I('ladder', 4, (3, 8)), I('white_wool', 4, (1, 3)), I('lead', 3),
        I('experience_bottle', 3, (1, 2)), I('stone_sword', 3), I('stone_pickaxe', 3), I('stone_axe', 3),
        I('bucket', 2), I('shears', 2), I('fishing_rod', 2),
        I('leather_helmet', 2), I('leather_chestplate', 2), I('leather_leggings', 2), I('leather_boots', 2),
    ]),
    bonus('common', 0.25),
])

UNCOMMON = Table([
    Pool((2, 4), [
        I('iron_ingot', 14, (1, 4)), I('gold_ingot', 10, (1, 3)), I('redstone', 10, (3, 9)), I('lapis_lazuli', 10, (3, 9)),
        I('emerald', 8, (1, 2)), I('copper_ingot', 6, (3, 8)), I('amethyst_shard', 6, (2, 5)), I('quartz', 5, (3, 8)),
        I('glowstone_dust', 5, (2, 6)), I('golden_carrot', 8, (2, 5)), I('cooked_salmon', 6, (2, 4)), I('honey_bottle', 4, (1, 2)),
        I('slime_ball', 4, (1, 3)), I('ender_pearl', 4), I('name_tag', 3), I('saddle', 3), I('lead', 3, (1, 2)),
        I('experience_bottle', 6, (2, 4)), I('book', 4, levels=(5, 15)), I('bow', 4, random_enchant=True), I('crossbow', 3),
        I('shield', 3), I('compass', 2), I('clock', 2),
        I('potion', 2, potion='minecraft:healing'), I('potion', 2, potion='minecraft:swiftness'),
        *gear('iron', TOOLS, 3, (5, 15)), *gear('chainmail', ARMOR, 2, None), *gear('iron', ARMOR, 2, None),
    ]),
    bonus('uncommon', 0.25),
])

RARE = Table([
    Pool((3, 4), [
        I('diamond', 10, (1, 2)), I('emerald', 10, (2, 5)), I('gold_ingot', 8, (3, 6)), I('iron_block', 4),
        I('lapis_block', 3), I('redstone_block', 3, (1, 2)), I('golden_apple', 8, (1, 2)), I('ender_pearl', 8, (2, 4)),
        I('blaze_rod', 6, (1, 3)), I('experience_bottle', 8, (4, 8)), I('obsidian', 5, (2, 6)), I('spyglass', 3),
        I('name_tag', 3), I('saddle', 2), I('book', 6, levels=(15, 25)),
        I('potion', 2, potion='minecraft:regeneration'), I('potion', 2, potion='minecraft:strength'),
        I('potion', 2, potion='minecraft:fire_resistance'), I('potion', 2, potion='minecraft:night_vision'),
        *gear('iron', TOOLS + ARMOR, 3, (15, 25)), *gear('diamond', ['sword', 'pickaxe'], 2, (10, 20)),
        I('trial_key', 3, since='1.21'), Tag('#minecraft:creeper_drop_music_discs', 3),
    ]),
    bonus('rare', 0.3),
])

EPIC = Table([
    Pool((3, 5), [
        I('diamond', 10, (2, 5)), I('emerald_block', 5), I('gold_block', 5, (1, 2)), I('golden_apple', 8, (2, 3)),
        I('enchanted_golden_apple', 1), I('totem_of_undying', 4), I('netherite_scrap', 5, (1, 2)), I('ancient_debris', 3, (1, 2)),
        I('experience_bottle', 8, (8, 16)), I('ender_pearl', 5, (4, 8)), I('shulker_shell', 4, (1, 2)), I('heart_of_the_sea', 1),
        I('nautilus_shell', 2, (1, 2)), I('echo_shard', 3, (1, 3)), I('book', 6, levels=30),
        I('bow', 2, levels=30), I('crossbow', 2, levels=30), I('trident', 1),
        I('potion', 2, potion='minecraft:strong_healing'), I('potion', 2, potion='minecraft:strong_strength'),
        *gear('diamond', TOOLS + ['shovel'] + ARMOR, 2, (25, 35)),
        I('ominous_trial_key', 2, since='1.21'),
    ]),
    bonus('epic', 0.3),
])

LEGENDARY = Table([
    Pool(1, [
        I('enchanted_golden_apple', 3), I('totem_of_undying', 3), I('elytra', 1), I('netherite_ingot', 3),
        *gear('diamond', ['sword', 'pickaxe', 'chestplate'], 2, 39),
    ]),
    Pool((3, 5), [
        I('netherite_ingot', 6, (1, 2)), I('netherite_scrap', 6, (2, 4)), I('diamond', 8, (4, 10)), I('diamond_block', 4, (1, 2)),
        I('emerald_block', 4, (2, 4)), I('enchanted_golden_apple', 5, (1, 2)), I('totem_of_undying', 6, (1, 2)), I('elytra', 2),
        I('nether_star', 2), I('trident', 2, levels=30), I('beacon', 1), I('dragon_breath', 3, (2, 5)), I('dragon_head', 1),
        I('netherite_upgrade_smithing_template', 3), I('experience_bottle', 6, (16, 32)), I('ancient_debris', 3, (2, 4)),
        I('shulker_shell', 3, (2, 4)), I('golden_apple', 4, (4, 8)), I('book', 5, levels=30),
        *gear('diamond', TOOLS + ARMOR, 3, (35, 39)), I('netherite_sword', 1, levels=30),
        I('heavy_core', 1, since='1.21'), I('mace', 1, since='1.21'),
    ]),
    bonus('legendary', 0.35),
])

MYTHIC = Table([
    Pool(2, [
        *gear('netherite', TOOLS + ARMOR, 3, 39), I('elytra', 4), I('nether_star', 4, (1, 2)), I('beacon', 3),
        I('enchanted_golden_apple', 4, (2, 4)), I('mace', 2, levels=30, since='1.21'),
    ]),
    Pool((4, 6), [
        I('netherite_ingot', 8, (2, 4)), I('nether_star', 4), I('enchanted_golden_apple', 6, (2, 4)), I('totem_of_undying', 5, (2, 3)),
        I('dragon_head', 2), I('heavy_core', 2, since='1.21'), I('trident', 2, levels=39),
        I('netherite_upgrade_smithing_template', 4, (1, 2)), I('diamond_block', 5, (2, 4)), I('emerald_block', 4, (4, 8)),
        I('experience_bottle', 5, (32, 64)), I('echo_shard', 3, (4, 8)), I('shulker_box', 2), I('elytra', 2),
        I('book', 5, levels=39), I('music_disc_pigstep', 1), I('music_disc_otherside', 1),
    ]),
    bonus('mythic', 0.4),
])

TABLES = {'common': COMMON, 'uncommon': UNCOMMON, 'rare': RARE, 'epic': EPIC, 'legendary': LEGENDARY, 'mythic': MYTHIC}

# Dimension themes: the tier's table, plus a themed pool.
THEME_ROLLS = {'common': 1, 'uncommon': 1, 'rare': (1, 2), 'epic': 2, 'legendary': (2, 3), 'mythic': 3}


def nether(tier):
    r = TIERS.index(tier)
    entries = [
        I('quartz', 8, (4, 12)), I('gold_ingot', 6, (2, 6)), I('gold_nugget', 8, (6, 18)), I('blaze_rod', 6, (1, 3)),
        I('magma_cream', 5, (1, 3)), I('ghast_tear', 3), I('nether_wart', 5, (2, 6)), I('glowstone_dust', 5, (4, 10)),
        I('crying_obsidian', 3, (1, 3)), I('gilded_blackstone', 2, (1, 2)), I('spectral_arrow', 4, (4, 8)),
        I('potion', 3, potion='minecraft:long_fire_resistance'),
    ]
    if r >= 2:
        entries += [I('ancient_debris', 1 + r, (1, 2)), I('piglin_banner_pattern', 1)]
    if r >= 3:
        entries += [I('netherite_scrap', r, (1, 3)), I('netherite_upgrade_smithing_template', 2)]
    if r >= 4:
        entries += [I('music_disc_pigstep', 1), I('wither_skeleton_skull', 1), I('netherite_ingot', 2)]
    return entries


def end(tier):
    r = TIERS.index(tier)
    entries = [
        I('ender_pearl', 8, (2, 6)), I('chorus_fruit', 6, (4, 10)), I('popped_chorus_fruit', 4, (2, 6)), I('end_rod', 4, (2, 6)),
        I('purpur_block', 3, (4, 8)), I('ender_eye', 4, (1, 3)), I('phantom_membrane', 3, (1, 3)),
    ]
    if r >= 2:
        entries += [I('shulker_shell', 2 + r, (1, 2)), I('end_crystal', 2), I('dragon_breath', 2, (1, 3))]
    if r >= 4:
        entries += [I('elytra', 2), I('shulker_box', 1)]
    if r >= 5:
        entries += [I('dragon_head', 1)]
    return entries


THEMES = {'the_nether': nether, 'the_end': end}


def themed(tier, theme):
    return Table([
        Pool(1, [Ref(f'mob_loot_bags:bags/{tier}')]),
        Pool(THEME_ROLLS[tier], THEMES[theme](tier)),
    ])


HOURGLASS = Table([
    Pool(2, [I('mob_loot_bags:epiclootbag', 6), I('mob_loot_bags:legendarylootbag', 3), I('mob_loot_bags:mythiclootbag', 1)]),
    Pool(1, [I('experience_bottle', 1, (8, 16))]),
])

LOCKED = Table([
    Pool(1, gear('netherite', TOOLS + ARMOR, 1, 39)),
    Pool((2, 3), [
        I('netherite_ingot', 4, (1, 2)), I('diamond', 6, (4, 8)), I('emerald_block', 4, (1, 3)), I('ancient_debris', 4, (2, 4)),
        I('netherite_upgrade_smithing_template', 3), I('enchanted_golden_apple', 2), I('totem_of_undying', 3),
    ]),
])

SPAWN_EGGS = [
    ('creeper', 20), ('skeleton', 15), ('spider', 8), ('zombie', 20), ('slime', 8), ('ghast', 4), ('zombified_piglin', 8),
    ('enderman', 8), ('cave_spider', 6), ('silverfish', 4), ('blaze', 5), ('magma_cube', 4), ('bat', 6), ('witch', 6),
    ('endermite', 4), ('guardian', 3), ('shulker', 2), ('pig', 12), ('sheep', 12), ('cow', 12), ('chicken', 12),
    ('squid', 6), ('wolf', 6), ('mooshroom', 3), ('ocelot', 4), ('horse', 6), ('rabbit', 6), ('polar_bear', 3),
    ('llama', 4), ('villager', 3), ('bee', 6), ('cat', 6), ('cod', 6), ('dolphin', 3), ('donkey', 5), ('drowned', 6),
    ('evoker', 1), ('fox', 5), ('glow_squid', 4), ('goat', 5), ('husk', 6), ('mule', 3), ('panda', 3), ('parrot', 4),
    ('phantom', 4), ('pillager', 4), ('pufferfish', 4), ('ravager', 1), ('salmon', 6), ('strider', 4), ('stray', 6),
    ('tropical_fish', 5), ('turtle', 4), ('vindicator', 2), ('wandering_trader', 2), ('wither_skeleton', 1), ('axolotl', 4),
    ('frog', 5), ('tadpole', 5), ('allay', 2), ('camel', 3), ('sniffer', 2), ('hoglin', 3), ('piglin', 4),
]
SPAWN_EGGS_121 = [('armadillo', 4), ('breeze', 2), ('bogged', 4)]

SUMMONING = Table([
    Pool((1, 2), [I(f'{mob}_spawn_egg', w, (1, 2) if w >= 8 else None) for mob, w in SPAWN_EGGS]
         + [I(f'{mob}_spawn_egg', w, since='1.21') for mob, w in SPAWN_EGGS_121]),
])

SPECIALS = {'hourglass': HOURGLASS, 'locked': LOCKED, 'summoning': SUMMONING}


# ------------------------------------------------------------------ emitters

def _number(value, profile):
    if isinstance(value, tuple):
        if profile == 'v3':
            return {'type': 'minecraft:uniform', 'min': value[0], 'max': value[1]}
        return {'type': 'minecraft:uniform', 'min': value[0], 'max': value[1]}
    return value


def _included(entry, profile):
    since = getattr(entry, 'since', '1.20')
    if profile == 'v1':
        return since == '1.20'
    return True


def _item(entry: Item, profile):
    mods = []
    if entry.potion:
        mods.append(('set_potion', {'id': entry.potion}))
    if entry.count is not None:
        mods.append(('set_count', {'count': _number(entry.count, profile)}))
    if entry.levels is not None:
        if profile == 'v1':
            mods.append(('enchant_with_levels', {'levels': _number(entry.levels, profile), 'treasure': True}))
        else:
            mods.append(('enchant_with_levels', {'levels': _number(entry.levels, profile), 'options': '#minecraft:on_random_loot'}))
    if entry.random_enchant:
        mods.append(('enchant_randomly', {} if profile == 'v1' else {'options': '#minecraft:on_random_loot'}))
    out = {'type': 'minecraft:item', 'name': entry.name}
    if entry.weight != 1:
        out['weight'] = entry.weight
    if mods:
        if profile == 'v3':
            ms = [{'type': 'minecraft:' + kind, **args} for kind, args in mods]
            out['modifier'] = ms[0] if len(ms) == 1 else ms
        else:
            out['functions'] = [{'function': 'minecraft:' + kind, **args} for kind, args in mods]
    return out


def _entry(entry, profile):
    if isinstance(entry, Item):
        return _item(entry, profile)
    if isinstance(entry, Tag):
        out = {'type': 'minecraft:tag', 'expand': True}
        if profile == 'v3':
            out['items'] = entry.name
        else:
            out['name'] = entry.name[1:]
        if entry.weight != 1:
            out['weight'] = entry.weight
        return out
    if isinstance(entry, Ref):
        out = {'type': 'minecraft:loot_table', ('name' if profile == 'v1' else 'value'): entry.table}
        if entry.weight != 1:
            out['weight'] = entry.weight
        return out
    raise TypeError(entry)


def _pool(pool: Pool, profile):
    out = {'rolls': _number(pool.rolls, profile), 'entries': [_entry(e, profile) for e in pool.entries if _included(e, profile)]}
    if pool.chance is not None:
        if profile == 'v3':
            out['condition'] = {'type': 'minecraft:random_chance', 'chance': pool.chance}
        else:
            out['conditions'] = [{'condition': 'minecraft:random_chance', 'chance': pool.chance}]
    return out


def emit(table: Table, profile, random_sequence=None):
    out = {'type': 'minecraft:chest', 'pools': [_pool(p, profile) for p in table.pools]}
    if random_sequence and profile != 'v1':
        out['random_sequence'] = random_sequence
    return out


def all_tables(profile):
    """{relative path under loot_table/: json} for every bag table."""
    result = {}
    for tier, table in TABLES.items():
        result[f'bags/{tier}.json'] = emit(table, profile, f'mob_loot_bags:bags/{tier}')
        for theme in THEMES:
            result[f'bags/{tier}/{theme}.json'] = emit(themed(tier, theme), profile, f'mob_loot_bags:bags/{tier}/{theme}')
    for name, table in SPECIALS.items():
        result[f'bags/{name}.json'] = emit(table, profile, f'mob_loot_bags:bags/{name}')
    return result
