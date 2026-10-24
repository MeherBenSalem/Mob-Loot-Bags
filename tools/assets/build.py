#!/usr/bin/env python3
"""Writes every generated texture, sound, model and data file into one or more version workspaces.

  python3 tools/assets/build.py 26.3            # one workspace
  python3 tools/assets/build.py all             # every workspace with a profile in data.PROFILES

Hand-made files are never touched: block loot tables and the legacy cursed sounds stay as they are.
"""
from __future__ import annotations

import json
import shutil
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))

import blocks  # noqa: E402
import data  # noqa: E402
import gui  # noqa: E402
import items  # noqa: E402
import loot  # noqa: E402
import sounds  # noqa: E402
from lang import EN_US  # noqa: E402
from pixel import canvas, put, rgb, mix, strip  # noqa: E402
from PIL import Image  # noqa: E402

ROOT = HERE.parent.parent
CACHE = HERE / '.cache'
NS = data.NS

# Files from 1.x that 2.0 replaces.
OBSOLETE = [
    'assets/mob_loot_bags/textures/item/common_lb.png', 'assets/mob_loot_bags/textures/item/cursed_lb.png',
    'assets/mob_loot_bags/textures/item/epic_lb.png', 'assets/mob_loot_bags/textures/item/key.png',
    'assets/mob_loot_bags/textures/item/legendary_lb.png', 'assets/mob_loot_bags/textures/item/locked_lb.png',
    'assets/mob_loot_bags/textures/item/rare_lb.png', 'assets/mob_loot_bags/textures/item/summon_lb.png',
    'assets/mob_loot_bags/textures/item/timed_lb.png', 'assets/mob_loot_bags/textures/item/uncommon_lb.png',
    'assets/mob_loot_bags/textures/item/unknown_lb.png', 'assets/mob_loot_bags/textures/block/loot_opener_block.png',
    'assets/mob_loot_bags/textures/block/loot_reycle_block.png', 'assets/mob_loot_bags/textures/screens',
    'assets/mob_loot_bags/models/block/loot_bag_opener_block.json', 'assets/mob_loot_bags/models/block/loot_bag_recycle_block.json',
    'data/mob_loot_bags/loot_table/legendary_loot_table.json', 'data/mob_loot_bags/loot_table/locked_loot_table.json',
    'data/mob_loot_bags/loot_table/lootbags', 'data/mob_loot_bags/loot_table/summoning_loot_table.json',
    'data/mob_loot_bags/loot_table/time_warped_loot_table.json', 'data/mob_loot_bags/loot_table/un_common.json',
    'data/mob_loot_bags/loot_tables/legendary_loot_table.json', 'data/mob_loot_bags/loot_tables/locked_loot_table.json',
    'data/mob_loot_bags/loot_tables/lootbags', 'data/mob_loot_bags/loot_tables/summoning_loot_table.json',
    'data/mob_loot_bags/loot_tables/time_warped_loot_table.json', 'data/mob_loot_bags/loot_tables/un_common.json',
]


def write_json(path: Path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2, ensure_ascii=False) + '\n', encoding='utf-8')


def save(img: Image.Image, path: Path):
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path, optimize=True)


def logo(size=64):
    """The mod icon: a Legendary bag over a glowing vignette, ringed by smaller bags and sparkles."""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    c = (size - 1) / 2
    for y in range(size):
        for x in range(size):
            r = max(abs(x - c), abs(y - c))
            corner = (abs(x - c) > c - 4) and (abs(y - c) > c - 4) and ((abs(x - c) - (c - 4)) ** 2 + (abs(y - c) - (c - 4)) ** 2 > 16)
            if corner:
                continue
            d = ((x - c) ** 2 + (y - c * 1.05) ** 2) ** 0.5 / (size * 0.62)
            base = mix('#3b1d4a', '#0d0a14', min(1.0, d))
            if r >= c - 1:
                base = rgb('#e0a92a') if r >= c - 0.5 else rgb('#7a4a0c')
            put(img, x, y, base)
    glow = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    for y in range(size):
        for x in range(size):
            d = ((x - c) ** 2 + (y - c - 4) ** 2) ** 0.5
            a = max(0, 1 - d / (size * 0.36))
            if a > 0:
                glow.putpixel((x, y), (255, 190, 70, int(150 * a * a)))
    img.alpha_composite(glow)
    big = items.bag('legendary').resize((32, 32), Image.NEAREST)
    img.alpha_composite(big, (16, 15))
    for name, (x, y) in (('rare', (5, 34)), ('epic', (44, 34)), ('mythic', (44, 8)), ('uncommon', (5, 8))):
        img.alpha_composite(items.bag(name), (x, y))
    for x, y in ((14, 12), (50, 28), (12, 30), (40, 52), (24, 52), (32, 8)):
        put(img, x, y, '#fff6d0')
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            put(img, x + dx, y + dy, '#ffd166')
    return img


def advancement_background():
    img = canvas()
    blocks.planks(img, 0, 0, 15, 15, ramp=['#120c08', '#1a110b', '#22170e', '#2b1d12', '#342417', '#3e2b1b'], seed=77)
    return img


def build(version: str):
    profile = data.PROFILES[version]
    res = ROOT / version / 'common' / 'src' / 'main' / 'resources'
    assets = res / 'assets' / NS
    datadir = res / 'data' / NS
    for rel in OBSOLETE:
        p = res / rel
        if p.is_dir():
            shutil.rmtree(p)
        elif p.exists():
            p.unlink()

    # Item textures (animated bags get a frame strip and .mcmeta).
    for key, file in items.FILES.items():
        frames = items.frames(key)
        path = assets / 'textures' / 'item' / f'{file}.png'
        save(strip(frames), path)
        meta = path.with_suffix('.png.mcmeta')
        if len(frames) > 1:
            write_json(meta, {'animation': {'frametime': items.ANIMATED[key]}})
        elif meta.exists():
            meta.unlink()
    save(items.diamond_key(), assets / 'textures' / 'item' / 'diamond_key.png')
    save(items.fortune_talisman(), assets / 'textures' / 'item' / 'fortune_talisman.png')

    # Block textures.
    for name, make in blocks.FACES.items():
        save(make(), assets / 'textures' / 'block' / f'{name}.png')
    for name, (frames, frametime) in blocks.ANIMATED_FACES.items():
        path = assets / 'textures' / 'block' / f'{name}.png'
        save(strip(frames), path)
        write_json(path.with_suffix('.png.mcmeta'), {'animation': {'frametime': frametime}})

    # GUI textures.
    save(gui.bag_opener(), assets / 'textures' / 'gui' / 'container' / 'bag_opener.png')
    save(gui.loot_recycler(), assets / 'textures' / 'gui' / 'container' / 'loot_recycler.png')
    toast = assets / 'textures' / 'gui' / 'sprites' / 'toast' / 'reveal.png'
    save(gui.reveal_toast(), toast)
    write_json(toast.with_suffix('.png.mcmeta'), gui.TOAST_MCMETA)
    save(advancement_background(), assets / 'textures' / 'gui' / 'advancements' / 'backgrounds' / 'loot_bags.png')
    icon = logo().resize((256, 256), Image.NEAREST)
    save(icon, assets / 'textures' / 'logo.png')
    save(icon, res / 'logo.png')

    # Sounds (synthesised once into the cache, then copied).
    CACHE.mkdir(exist_ok=True)
    for name, make in sounds.SOUNDS.items():
        cached = CACHE / f'{name}.ogg'
        if not cached.exists():
            sounds.write_ogg(make(), cached)
        target = assets / 'sounds' / f'{name}.ogg'
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(cached, target)
    write_json(assets / 'sounds.json', data.sounds_json())

    # Models, blockstates, client item definitions, translations.
    model_dir = assets / 'models' / 'item'
    for old in model_dir.glob('*.json'):
        old.unlink()
    for item_id, model in data.item_models().items():
        write_json(model_dir / f'{item_id}.json', model)
    for name, model in data.block_models().items():
        write_json(assets / 'models' / 'block' / f'{name}.json', model)
    for name, state in data.blockstates().items():
        write_json(assets / 'blockstates' / f'{name}.json', state)
    items_dir = assets / 'items'
    if items_dir.exists():
        shutil.rmtree(items_dir)
    if profile.items_json:
        for item_id, definition in data.item_definitions().items():
            write_json(items_dir / f'{item_id}.json', definition)
    write_json(assets / 'lang' / 'en_us.json', dict(sorted(EN_US.items())))

    # Data: loot tables, recipes, tags, advancements.
    loot_dir = datadir / profile.dir('loot_table')
    bags_dir = loot_dir / 'bags'
    if bags_dir.exists():
        shutil.rmtree(bags_dir)
    for rel, table in loot.all_tables(profile.loot).items():
        write_json(loot_dir / rel, table)
    recipe_dir = datadir / profile.dir('recipe')
    if recipe_dir.exists():
        shutil.rmtree(recipe_dir)
    for name, recipe in data.recipes(profile).items():
        write_json(recipe_dir / f'{name}.json', recipe)
    for (kind, name), value in data.tags(profile).items():
        if value is None:
            continue
        write_json(datadir / profile.dir(kind) / f'{name}.json', value)
    mineable = res / 'data' / 'minecraft' / profile.dir('tags/block') / 'mineable' / 'pickaxe.json'
    write_json(mineable, {'values': [f'{NS}:loot_bag_opener_block', f'{NS}:loot_bag_recycle_block']})
    adv_dir = datadir / profile.dir('advancement')
    if adv_dir.exists():
        shutil.rmtree(adv_dir)
    for name, adv in data.advancements(profile).items():
        write_json(adv_dir / f'{name}.json', adv)
    print(f'{version}: assets and data written to {res}')


if __name__ == '__main__':
    targets = sys.argv[1:] or ['26.3']
    if targets == ['all']:
        targets = [v for v in data.PROFILES if (ROOT / v).is_dir()]
    for target in targets:
        build(target)
