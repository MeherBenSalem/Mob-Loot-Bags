#!/usr/bin/env python3
"""In-game checks with a real QA client online (see devrun.sh): opening bags by holding Use, sneak-opening a stack,
locked and hourglass bags, the cursed bag, the machine screens, and spawner tagging. Captures go to --out.

  python3 tools/qa/client_checks.py --out /tmp/mlb-qa/shots
"""
from __future__ import annotations

import argparse
import re
import sys
import time
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from client import Session, PLAYER  # noqa: E402

FAILURES = []


def check(name, ok, detail=''):
    print(('PASS ' if ok else 'FAIL ') + name + (f'  [{detail[:300]}]' if detail and not ok else ''))
    if not ok:
        FAILURES.append(name)


def count_in(inventory, item):
    return sum(int(n) for n in re.findall(r'count: (\d+), Slot: \d+b, id: "' + re.escape(item) + '"', inventory)) + \
        sum(int(n) for n in re.findall(r'id: "' + re.escape(item) + r'", count: (\d+)', inventory))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--out', required=True)
    args = ap.parse_args()
    out = Path(args.out)
    s = Session()
    c = s.cmd
    inv = lambda: c(f'data get entity {PLAYER} Inventory')

    for cmd in ('gamerule advance_time false', 'time set 6000', 'weather clear', f'gamemode survival {PLAYER}',
                f'effect give {PLAYER} minecraft:resistance infinite 5 true', f'effect give {PLAYER} minecraft:saturation infinite 1 true',
                f'clear {PLAYER}', 'kill @e[type=!minecraft:player]'):
        c(cmd)
    # Look up at the sky, so holding Use never hits a block (a machine would open its screen instead).
    here = re.findall(r'-?\d+\.\d+', c(f'data get entity {PLAYER} Pos'))
    c(f'tp {PLAYER} {here[0]} {here[1]} {here[2]} 0 -60')
    # A freshly joined client spends its first click on grabbing the mouse.
    s.click(1)
    s.wait(2)

    # Hold Use on a Rare bag.
    c(f'item replace entity {PLAYER} hotbar.0 with mob_loot_bags:rarelootbag 3')
    s.key('1')
    s.wait(1)
    # 16 ticks open one bag; stay well short of the 36 a second one needs, even when a busy machine lags the client.
    s.hold(3, 1.1)
    s.wait(0.7)
    s.shot(out / 'check-open-rare.png')
    i = inv()
    check('holding Use opens one bag', count_in(i, 'mob_loot_bags:rarelootbag') == 2, i)
    check('loot went into the inventory', len(re.findall(r'id: "minecraft:', i)) >= 1, i)

    # A short tap does not open it.
    s.hold(3, 0.2)
    s.wait(0.5)
    check('a tap does not open a bag', count_in(inv(), 'mob_loot_bags:rarelootbag') == 2)

    # Sneak opens the whole stack.
    c(f'clear {PLAYER}')
    c(f'item replace entity {PLAYER} hotbar.0 with mob_loot_bags:commonlootbag 5')
    s.wait(0.5)
    s.hold(3, 1.5, sneak=True)
    s.wait(0.8)
    s.shot(out / 'check-open-stack.png')
    check('sneak opens the stack', count_in(inv(), 'mob_loot_bags:commonlootbag') == 0, inv())

    # Locked: refuses without a key, opens with one and uses it up.
    c(f'clear {PLAYER}')
    c(f'item replace entity {PLAYER} hotbar.0 with mob_loot_bags:locked_lootbags 1')
    s.wait(0.5)
    s.hold(3, 1.5)
    s.wait(0.5)
    check('locked bag needs a key', count_in(inv(), 'mob_loot_bags:locked_lootbags') == 1)
    c(f'item replace entity {PLAYER} hotbar.5 with mob_loot_bags:diamond_key 2')
    s.wait(0.5)
    s.hold(3, 1.5)
    s.wait(0.8)
    i = inv()
    check('key unlocks the bag', count_in(i, 'mob_loot_bags:locked_lootbags') == 0, i)
    check('one key used', count_in(i, 'mob_loot_bags:diamond_key') == 1, i)
    check('locked bag gives netherite gear', 'netherite_' in i, i)
    # Use was still held when the last bag in the hand opened: the loot must not land in that hand (and get used).
    equipment = c(f'data get entity {PLAYER} equipment')
    check('loot skips the emptied hand', 'Slot: 0b' not in i and 'netherite' not in equipment, i + equipment)

    # Hourglass: an unripe bag refuses; a ripe one opens into bags.
    c(f'clear {PLAYER}')
    c(f'item replace entity {PLAYER} hotbar.0 with mob_loot_bags:timed_loot_bag 1')
    s.wait(1.5)
    i = inv()
    check('hourglass timer started', 'ripe_at' in i, i)
    s.hold(3, 1.5)
    s.wait(0.5)
    check('unripe hourglass refuses', count_in(inv(), 'mob_loot_bags:timed_loot_bag') == 1)
    c(f'item replace entity {PLAYER} hotbar.0 with mob_loot_bags:timed_loot_bag[custom_data={{mob_loot_bags:{{ripe_at:0L,ripen_ticks:100L}}}}] 1')
    s.wait(1)
    s.hold(3, 1.5)
    s.wait(0.8)
    i = inv()
    check('ripe hourglass opens into bags', count_in(i, 'mob_loot_bags:timed_loot_bag') == 0 and 'lootbag' in i, i)

    # 1.x Timed bags (lb_timer + Unbreaking) convert to ripe Hourglass bags.
    c(f'clear {PLAYER}')
    c(f'item replace entity {PLAYER} hotbar.0 with mob_loot_bags:timed_loot_bag[custom_data={{is_active:1b,lb_timer:0}},'
      f'enchantments={{"minecraft:unbreaking":1}}] 1')
    s.wait(1.5)
    i = inv()
    check('1.x ripe timed bag migrates', 'ripe_at' in i and 'unbreaking' not in i and 'lb_timer' not in i, i)

    # Cursed: some outcome happens and the bag is used.
    c(f'clear {PLAYER}')
    c('difficulty normal')
    c(f'item replace entity {PLAYER} hotbar.0 with mob_loot_bags:cursed_lootbag 1')
    s.wait(0.5)
    s.hold(3, 1.5)
    s.wait(2)
    s.shot(out / 'check-cursed.png')
    check('cursed bag used', count_in(inv(), 'mob_loot_bags:cursed_lootbag') == 0)
    c('kill @e[type=!minecraft:player]')

    # Machines: open each screen by using the block.
    c(f'clear {PLAYER}')
    c(f'gamemode creative {PLAYER}')
    pos = re.findall(r'-?\d+\.\d+', c(f'data get entity {PLAYER} Pos'))
    x, y, z = (int(float(v)) for v in pos[:3])
    c(f'fill {x - 3} {y - 1} {z - 6} {x + 3} {y - 1} {z + 2} minecraft:stone')
    c(f'fill {x - 3} {y} {z - 6} {x + 3} {y + 4} {z + 2} minecraft:air')
    c(f'setblock {x} {y} {z - 3} mob_loot_bags:loot_bag_opener_block[facing=south]')
    c(f'setblock {x + 2} {y} {z - 3} mob_loot_bags:loot_bag_recycle_block[facing=south]')
    c(f'item replace block {x} {y} {z - 3} container.0 with mob_loot_bags:epiclootbag 12')
    c(f'item replace block {x} {y} {z - 3} container.1 with mob_loot_bags:rarelootbag 30')
    c(f'item replace block {x + 2} {y} {z - 3} container.0 with minecraft:rotten_flesh 64')
    c(f'item replace block {x + 2} {y} {z - 3} container.1 with minecraft:bone 40')
    c(f'item replace block {x + 2} {y} {z - 3} container.2 with mob_loot_bags:uncommonlootbag 6')
    s.tp(x + 0.5, y, z + 0.5, facing=(x + 0.5, y + 0.5, z - 2.5))
    s.wait(5)
    s.click(3)
    s.wait(1.5)
    s.shot(out / 'check-opener-gui.png')
    s.key('Escape')
    s.wait(0.5)
    s.tp(x + 2.5, y, z + 0.5, facing=(x + 2.5, y + 0.5, z - 2.5))
    s.wait(1)
    s.click(3)
    s.wait(1.5)
    s.shot(out / 'check-recycler-gui.png')
    s.key('Escape')

    # Spawners: their mobs carry the spawner tag (a player must be in range).
    c(f'setblock {x - 2} {y} {z - 5} minecraft:spawner{{SpawnData:{{entity:{{id:"minecraft:zombie"}}}},Delay:0s,'
      'MinSpawnDelay:20s,MaxSpawnDelay:40s,RequiredPlayerRange:32s}')
    c('time set midnight')
    tagged = False
    for _ in range(15):
        s.wait(2)
        o = c(f'data get entity @e[type=minecraft:zombie,limit=1,sort=nearest,x={x},y={y},z={z}] Tags')
        if 'mob_loot_bags.spawner' in o:
            tagged = True
            break
    check('spawner mobs are tagged', tagged, o)
    c(f'setblock {x - 2} {y} {z - 5} minecraft:air')
    c('kill @e[type=minecraft:zombie]')
    c('time set 6000')

    print(f'\n{len(FAILURES)} failure(s)' + (': ' + ', '.join(FAILURES) if FAILURES else ''))
    return 1 if FAILURES else 0


if __name__ == '__main__':
    sys.exit(main())
