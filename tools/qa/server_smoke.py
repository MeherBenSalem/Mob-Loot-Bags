#!/usr/bin/env python3
"""Server-side QA for Mob Loot Bags 2.0, driven over RCON against a running dev server.

  python3 tools/qa/server_smoke.py --run-dir 26.3/fabric/runs/server

Checks: every bag table rolls loot (/lootbags simulate), /lootbags odds, the Bag Opener and Loot Recycler working on
their own (fed with /item), mob and boss drops (with player credit temporarily disabled in drops.json), the spawner
tag from the mixin, and config reloads. Restores drops.json afterwards. Exits non-zero if any check failed.

The checks run around x 0-20, z 0-20. With --with-player, a connected player must be in survival or creative within
32 blocks of that area: spawners ignore spectators, and the test chunks must be loaded.
"""
from __future__ import annotations

import argparse
import re
import sys
import time
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from rcon import wait_for  # noqa: E402

FAILURES = []


def check(name, ok, detail=''):
    print(('PASS ' if ok else 'FAIL ') + name + (f'  [{detail}]' if detail and not ok else ''))
    if not ok:
        FAILURES.append(name)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--run-dir', required=True)
    ap.add_argument('--port', type=int, default=25575)
    ap.add_argument('--with-player', action='store_true', help='a QA client is online: also test spawners')
    args = ap.parse_args()
    run = Path(args.run_dir)
    r = wait_for(args.port)
    c = r.command

    def wait(seconds):
        time.sleep(seconds)

    # Config files exist.
    cfg = run / 'config' / 'mob_loot_bags'
    for name in ('drops.json', 'bags.json', 'machines.json', 'client.json'):
        check(f'config {name} generated', (cfg / name).exists())

    # Every bag table rolls something.
    for bag in ('common', 'uncommon', 'rare', 'epic', 'legendary', 'mythic', 'hourglass', 'locked', 'summoning'):
        out = c(f'lootbags simulate {bag} 200')
        check(f'simulate {bag}', 'items per bag' in out and '%' in out, out[:200])
    out = c('lootbags simulate rare 200 the_nether')
    check('simulate themed table', 'bags/rare/the_nether' in out, out[:200])
    out = c('lootbags simulate cursed 10')
    check('simulate cursed refuses', 'outcome' in out, out[:200])

    out = c('lootbags odds minecraft:zombie')
    check('odds zombie', 'chance to drop a bag' in out, out[:200])
    out = c('lootbags odds minecraft:wither')
    check('odds boss', 'always drops' in out, out[:300])
    out = c('lootbags odds minecraft:cow')
    check('odds passive is 0%', '0.0%' in out, out[:200])

    # A test area in force-loaded chunks above spawn.
    c('forceload add 0 0 32 32')
    c('fill 0 120 0 24 120 24 minecraft:stone')
    c('fill 0 121 0 24 126 24 minecraft:air')
    c('gamerule doMobLoot true')

    # Bag Opener: 5 Rare bags in, loot out.
    c('setblock 4 121 4 mob_loot_bags:loot_bag_opener_block[facing=north]')
    c('item replace block 4 121 4 container.0 with mob_loot_bags:rarelootbag 5')
    wait(8)
    out = c('data get block 4 121 4 Items')
    remaining = re.search(r'count: (\d+), Slot: 0b', out) or re.search(r'Slot: 0b, count: (\d+)', out)
    check('opener consumed bags', remaining is None or int(remaining.group(1)) < 5, out[:300])
    check('opener produced loot', re.search(r'Slot: (9|1\d|2\d)b', out) is not None, out[:300])

    # Pulse mode: mode 3 opens one bag per redstone pulse.
    c('setblock 8 121 4 mob_loot_bags:loot_bag_opener_block[facing=north]')
    c('data merge block 8 121 4 {mode:3}')
    c('item replace block 8 121 4 container.0 with mob_loot_bags:commonlootbag 4')
    wait(3)
    out = c('data get block 8 121 4 Items')
    check('pulse mode waits for redstone', 'count: 4' in out, out[:200])
    c('setblock 9 121 4 minecraft:redstone_block')
    wait(3)
    c('setblock 9 121 4 minecraft:air')
    wait(1)
    out = c('data get block 8 121 4 Items')
    check('pulse mode opens one bag', re.search(r'count: 3, Slot: 0b|Slot: 0b, count: 3', out) is not None, out[:200])

    # Loot Recycler: junk to salvage points to a Common bag; bags to stored experience.
    c('setblock 12 121 4 mob_loot_bags:loot_bag_recycle_block[facing=north]')
    c('item replace block 12 121 4 container.0 with minecraft:rotten_flesh 64')
    c('item replace block 12 121 4 container.1 with minecraft:bone 8')
    c('item replace block 12 121 4 container.2 with mob_loot_bags:legendarylootbag 1')
    wait(28)
    out = c('data get block 12 121 4')
    check('recycler made a bag', 'mob_loot_bags:commonlootbag' in out, out[:400])
    xp = re.search(r'xp: (\d+)', out)
    check('recycler stored bag xp', xp is not None and int(xp.group(1)) >= 200, out[:400])

    # Hopper below the opener pulls loot out of the bottom.
    c('setblock 4 120 4 minecraft:hopper')
    c('setblock 4 119 4 minecraft:chest')
    wait(6)
    out = c('data get block 4 119 4 Items')
    check('hopper extracts opener output', 'id:' in out, out[:200])

    # Mob drops, with player credit off and a 100% chance for the test.
    drops = cfg / 'drops.json'
    original = drops.read_text(encoding='utf-8')
    try:
        patched = original.replace('"require_player": true', '"require_player": false').replace('"hostile_chance": 0.18', '"hostile_chance": 1.0')
        drops.write_text(patched, encoding='utf-8')
        out = c('lootbags reload')
        check('reload', 'reloaded' in out, out)
        c('kill @e[type=item]')
        c('summon minecraft:zombie 20 121 20 {NoAI:1b}')
        c('kill @e[type=minecraft:zombie]')
        wait(1)
        found = 0
        for bag in ('commonlootbag', 'uncommonlootbag', 'rarelootbag', 'epiclootbag', 'legendarylootbag', 'mythiclootbag',
                    'cursed_lootbag', 'timed_loot_bag', 'locked_lootbags', 'summoning_lootbags'):
            out = c(f'execute if entity @e[type=item,x=20,y=121,z=20,distance=..8,nbt={{Item:{{id:"mob_loot_bags:{bag}"}}}}]')
            found += int(re.search(r'count: (\d+)', out, re.I).group(1)) if 'passed' in out.lower() else 0
        check('zombie dropped exactly one bag', found == 1, str(found))

        c('kill @e[type=item]')
        c('summon minecraft:elder_guardian 20 121 12 {NoAI:1b}')
        c('kill @e[type=minecraft:elder_guardian]')
        wait(1)
        bags = 0
        for tier in ('commonlootbag', 'uncommonlootbag', 'rarelootbag', 'epiclootbag', 'legendarylootbag', 'mythiclootbag'):
            out = c(f'execute as @e[type=item,x=20,y=121,z=12,distance=..8,nbt={{Item:{{id:"mob_loot_bags:{tier}"}}}}] '
                    f'run data get entity @s Item.count')
            count = sum(int(n) for n in re.findall(r'entity data: (\d+)', out))
            if tier in ('commonlootbag', 'uncommonlootbag', 'rarelootbag'):
                check(f'boss drops no {tier}', count == 0, out)
            bags += count
        check('boss dropped 3 bags', bags == 3, str(bags))
    finally:
        drops.write_text(original, encoding='utf-8')
        c('lootbags reload')

    # Spawner tagging from the mixin (spawners need a player in range: only with --with-player).
    if not args.with_player:
        r.close()
        print(f'\n{len(FAILURES)} failure(s)' + (': ' + ', '.join(FAILURES) if FAILURES else ''))
        sys.exit(1 if FAILURES else 0)
    c('setblock 4 121 16 minecraft:spawner{SpawnData:{entity:{id:"minecraft:zombie"}},Delay:0s,MinSpawnDelay:20s,MaxSpawnDelay:40s,RequiredPlayerRange:64s}')
    c('time set midnight')
    tagged = False
    for _ in range(10):
        wait(3)
        out = c('data get entity @e[type=minecraft:zombie,limit=1,sort=nearest,x=4,y=121,z=16] Tags')
        if 'mob_loot_bags.spawner' in out:
            tagged = True
            break
    check('spawner mobs are tagged', tagged, out if not tagged else '')
    c('setblock 4 121 16 minecraft:air')
    c('kill @e[type=minecraft:zombie]')
    c('time set day')

    r.close()
    print(f'\n{len(FAILURES)} failure(s)' + (': ' + ', '.join(FAILURES) if FAILURES else ''))
    sys.exit(1 if FAILURES else 0)


if __name__ == '__main__':
    main()
