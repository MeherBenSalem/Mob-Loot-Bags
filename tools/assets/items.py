"""Item textures: the ten loot bags (three of them animated), the Diamond Key and the Fortune Talisman."""
from __future__ import annotations

import math

from bags import sack, emblem, sparkle, SACK
from pixel import canvas, put, get, rgb, mix, stamp

# name: (fabric ramp, rope ramp, hem trim ramp or None, emblem, emblem palette)
BAGS = {
    'common': (['#2a1d13', '#45311f', '#634629', '#86623a', '#a9824f', '#c9a46c'],
               ['#3d2c18', '#6b5534', '#a8895a', '#cfb383'], None,
               'patch', {'o': '#45311f', 'x': '#d8b880', 'S': '#86623a'}),
    'uncommon': (['#11220e', '#1c3818', '#2b5524', '#3e7833', '#579e45', '#7cc45e'],
                 ['#3b250f', '#5e3c1a', '#8b5a2b', '#b07a45'], None,
                 'buckle', {'o': '#3b2a0a', 'G': '#d9b04e'}),
    'rare': (['#0c1533', '#152757', '#203d88', '#2f5cbd', '#467fe3', '#74a9ff'],
             ['#5c3d0a', '#9a6a16', '#d6a02c', '#ffd866'], None,
             'coin', {'o': '#7a520c', 'g': '#b88418', 'G': '#f0c03a', 'H': '#fff0a0'}),
    'epic': (['#1b0b2c', '#2f134e', '#4a1f79', '#6a30ab', '#924cd6', '#bd80ff'],
             ['#4a4f5c', '#7d8494', '#b4bccb', '#e8edf5'], ['#7d8494', '#b4bccb', '#e8edf5'],
             'gem', {'o': '#4d0a48', 'H': '#ffe0ff', 'G': '#ff4fd2'}),
    'legendary': (['#3a1e05', '#6a3907', '#a15f0e', '#d8911c', '#ffc23a', '#ffe9a0'],
                  ['#4d0a14', '#7f1222', '#b51d33', '#e8414f'], ['#7f1222', '#b51d33', '#e8414f'],
                  'star', {'H': '#7f1222', 'G': '#d01e3c', 'r': '#ffe9a0'}),
    'mythic': (['#17040d', '#33081c', '#58102f', '#8a1749', '#c42566', '#ff5a96'],
               ['#5a3a08', '#9e6c12', '#e0a92a', '#ffe27a'], ['#9e6c12', '#e0a92a', '#ffe27a'],
               'eye', {'o': '#ffe27a', 'W': '#ffffff', 'e': '#ff1f62'}),
    'cursed': (['#0b060e', '#170d1d', '#24142d', '#352042', '#4b2e5c', '#674079'],
               ['#5a5547', '#8f8a74', '#c9c3aa', '#efeadb'], None,
               'skull', {'w': '#e8e4cf', 'W': '#7dff4a', 'e': '#141a0e'}),
    'hourglass': (['#082022', '#10363a', '#1a5459', '#277b7f', '#3da39f', '#6fd2c4'],
                  ['#4a3410', '#7a5a20', '#b88a3a', '#e8c36a'], None,
                  'hourglass', {'G': '#8a6528', 'w': '#c8fff5', 's': '#ffcf5a'}),
    'locked': (['#13171c', '#20262f', '#323b46', '#4a5563', '#6a7887', '#9aa7b5'],
               ['#3a3f47', '#5d646e', '#8a929d', '#c3cad3'], None,
               'padlock', {'o': '#c3cad3', 'G': '#e0a92a', 'k': '#5a3a08'}),
    'summoning': (['#281006', '#451f0b', '#6b3213', '#964a1d', '#c66b2c', '#ef9752'],
                  ['#2f3b1e', '#4e6230', '#7a944a', '#a9c46f'], None,
                  'egg', {'c': '#efe8cf', 'C': '#bdb59a', 's': '#4f8a34'}),
}

# Texture file name per bag (models reference these).
FILES = {
    'common': 'common_loot_bag', 'uncommon': 'uncommon_loot_bag', 'rare': 'rare_loot_bag', 'epic': 'epic_loot_bag',
    'legendary': 'legendary_loot_bag', 'mythic': 'mythic_loot_bag', 'cursed': 'cursed_loot_bag',
    'hourglass': 'hourglass_loot_bag', 'locked': 'locked_loot_bag', 'summoning': 'summoning_loot_bag',
}

ANIMATED = {'legendary': 2, 'mythic': 2, 'cursed': 3}  # frametime in ticks


def bag(name):
    fabric, rope, trim, emb, palette = BAGS[name]
    img = sack(fabric, rope, trim=trim)
    if name == 'hourglass':
        stamp(img, 5, 8, '''
GGGGG
.wsw.
..s..
.wsw.
.sss.
GGGGG
''', BAGS[name][4])
    else:
        emblem(img, emb, palette)
    if name == 'legendary':
        sparkle(img, 10, 10, '#fffbe8')
    return img


def _body_pixels():
    return [(x, y) for y in range(16) for x in range(16) if SACK[y][x] == 'B']


def frames(name):
    """Animation frames for the animated bags; a single frame for the others."""
    base = bag(name)
    if name == 'legendary':
        # A glint sweeps diagonally across the sack, then rests; the star twinkles.
        out = []
        for i in range(16):
            f = base.copy()
            band = i * 2 - 6
            if 0 <= i < 10:
                for (x, y) in _body_pixels():
                    if x + y - 8 in (band, band + 1) and get(f, x, y)[3]:
                        c = get(f, x, y)
                        put(f, x, y, mix(c, '#fff6d0', 0.55 if x + y - 8 == band else 0.3))
            if i in (11, 12, 13):
                sparkle(f, 7, 10, '#ffffff')
            out.append(f)
        return out
    if name == 'mythic':
        out = []
        pupils = ['#ff1f62', '#ff4a80', '#ff7aa2', '#ffb0c8', '#ff7aa2', '#ff4a80', '#ff1f62', '#d10f48']
        orbit = [(3, 9), (4, 12), (7, 14), (11, 13), (12, 10), (11, 8), (8, 7), (5, 8)]
        for i in range(8):
            f = base.copy()
            put(f, 7, 10, pupils[i])
            put(f, 8, 10, pupils[i])
            x, y = orbit[i]
            sparkle(f, x, y, '#ffd0e0')
            x2, y2 = orbit[(i + 4) % 8]
            put(f, x2, y2, mix(get(f, x2, y2), '#ff9ab8', 0.6))
            out.append(f)
        return out
    if name == 'cursed':
        out = []
        glow = ['#7dff4a', '#5fd636', '#3f9c22', '#2a6a16', '#3f9c22', '#5fd636', '#7dff4a', '#a6ff7a']
        for g in glow:
            f = base.copy()
            put(f, 6, 10, g)
            put(f, 8, 10, g)
            out.append(f)
        return out
    return [base]


def diamond_key():
    img = canvas()
    stamp(img, 0, 0, '''
................
..........oooo..
.........oHHCCo.
........oHCDDCDo
........oCo..oDo
........oCo..oDo
........oCDooDDo
.......oooDDDDo.
......oCDo.oo...
.....oCDo.......
....oCDo........
...oCDo.........
..oCDooo........
..oDooCDo.......
...oooDo........
.....oo.........
''', {'o': '#0b2a33', 'H': '#c9fdff', 'C': '#4fd6e6', 'D': '#1f8ea3'})
    return img


def fortune_talisman():
    img = canvas()
    stamp(img, 2, 0, '''
...cc..cc...
..c......c..
..c......c..
...c....c...
....oooo....
...oGGGGo...
..oGHGGGGo..
.oGGGoEoGgo.
.oGGoEHEogo.
.oGoEEEEEog.
.oGGoEEEogo.
.oGGGoEoGgo.
..oGGGGGgo..
...ogggggo..
....oooo....
''', {'c': '#8a5a2b', 'o': '#5a3a08', 'G': '#f0c03a', 'H': '#fff3b0', 'g': '#b88418',
      'E': '#2fbf5a', })
    put(img, 9, 8, rgb('#a8ffbe'))
    return img
