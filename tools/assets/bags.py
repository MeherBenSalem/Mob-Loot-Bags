"""Loot bag item sprites (16x16). One shared sack silhouette, shaded per material, with tier trims and an emblem.

Run tools/assets/build.py to write every texture; this module only draws.
"""
from __future__ import annotations

import math

from pixel import canvas, put, get, rgb, mix, stamp, grid

# Silhouette. F = gathered top (ruffle), R = rope band, K = hanging rope end, B = body.
SACK = grid('''
................
......F..F......
.....FFFFFFF....
....FFFFFFFF....
.....FFFFFF.....
......RRRR......
.....RRRRRRK....
....BBBBBBBBK...
...BBBBBBBBBBK..
..BBBBBBBBBBBB..
.BBBBBBBBBBBBBB.
.BBBBBBBBBBBBBB.
.BBBBBBBBBBBBBB.
.BBBBBBBBBBBBBB.
..BBBBBBBBBBBB..
...BBBBBBBBBB...
''')

HIGHLIGHT = (4.6, 9.2)


def _filled(x, y):
    return 0 <= y < 16 and 0 <= x < 16 and SACK[y][x] != '.'


def _edge(x, y):
    return any(not _filled(x + dx, y + dy) for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)))


def _body_tone(x, y, top):
    """Tone index 1..top for a body pixel: a soft highlight up-left, falling off to a shadowed lower-right."""
    nx = (x - 7.5) / 7.0
    ny = (y - 11.0) / 5.0
    nz = math.sqrt(max(0.0, 1.0 - min(1.0, nx * nx + ny * ny)))
    d = math.hypot((x - HIGHLIGHT[0]) / 6.5, (y - HIGHLIGHT[1]) / 5.0)
    t = 0.62 * (1.0 - min(1.0, d)) + 0.38 * nz
    if x >= 11:
        t -= 0.08
    if y >= 14:
        t -= 0.18
    bands = [0.18, 0.36, 0.55, 0.74]  # top = 5 for 6-colour ramps
    idx = 1
    for b in bands[:top - 1]:
        if t > b:
            idx += 1
    return min(top, idx)


def _clean(img, region):
    """Replaces body pixels that differ from all four neighbours (single-pixel noise) with the neighbour majority."""
    src = img.copy()
    for (x, y) in region:
        c = src.getpixel((x, y))
        around = [src.getpixel((x + dx, y + dy)) for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)) if (x + dx, y + dy) in region]
        if len(around) >= 3 and all(a != c for a in around):
            best = max(set(around), key=around.count)
            img.putpixel((x, y), best)


def sack(fabric, rope, outline=None, flap=None, trim=None):
    """fabric: 6-colour ramp, dark to light. rope: 4-colour ramp. trim: optional 3-colour ramp for a hem band."""
    fabric = [rgb(c) for c in fabric]
    rope = [rgb(c) for c in rope]
    flap = [rgb(c) for c in (flap or fabric)]
    outline = rgb(outline) if outline else mix(fabric[0], (0, 0, 0, 255), 0.5)
    img = canvas()
    top = len(fabric) - 1
    body = set()
    for y in range(16):
        for x in range(16):
            ch = SACK[y][x]
            if ch == '.':
                continue
            if _edge(x, y) and ch in 'BF':
                put(img, x, y, outline)
            elif ch == 'B':
                put(img, x, y, fabric[_body_tone(x, y, top)])
                body.add((x, y))
            elif ch == 'F':
                lit = x <= 7
                pleat = x % 2 == 0
                idx = top - (0 if lit else 1) - (1 if pleat else 0)
                put(img, x, y, flap[max(2, idx)])
            elif ch == 'R':
                put(img, x, y, rope[2])
            elif ch == 'K':
                put(img, x, y, rope[1])
    _clean(img, body)
    # Rope band: two rows, lit on top, with a knot on the right and its end hanging over the shoulder.
    for x in range(6, 10):
        put(img, x, 5, rope[3])
    for x in range(5, 11):
        put(img, x, 6, rope[2])
    put(img, 5, 6, rope[1])
    put(img, 10, 5, rope[1])
    put(img, 11, 6, rope[2])
    put(img, 12, 7, rope[2])
    put(img, 13, 8, rope[1])
    put(img, 12, 6, rope[1])
    # Shoulder creases under the rope, and a soft sheen.
    for x, y in ((5, 7), (10, 7), (4, 8), (11, 8)):
        put(img, x, y, fabric[max(1, _body_tone(x, y, top) - 1)])
    put(img, 4, 9, fabric[top])
    put(img, 3, 10, mix(fabric[top], fabric[top - 1], 0.5))
    if trim:
        trim = [rgb(c) for c in trim]
        for x in range(2, 14):
            if _filled(x, 13) and not _edge(x, 13):
                put(img, x, 13, trim[1] if x % 3 else trim[2])
            if _filled(x, 14) and not _edge(x, 14):
                put(img, x, 14, trim[0])
    return img


# Emblems drawn over the body; (art, x, y). 'o' is the emblem outline colour.
EMBLEMS = {
    'patch': ('''
oooo
oxSo
oSxo
oooo
''', 6, 9),
    'buckle': ('''
.ooo.
oGGGo
oG.Go
oGGGo
.ooo.
''', 5, 9),
    'coin': ('''
.ooo.
oHGGo
oGHGo
oGGgo
.ooo.
''', 5, 9),
    'gem': ('''
..o..
.oHo.
oHGGo
.oGo.
..o..
''', 5, 9),
    'star': ('''
..H..
.HGH.
HGrGH
.H.H.
''', 5, 9),
    'eye': ('''
.ooo.
oWeWo
.ooo.
''', 5, 10),
    'skull': ('''
.www.
weWew
wwwww
.w.w.
''', 5, 9),
    'hourglass': ('''
oooo
.ss.
..s.
.ss.
oooo
''', 6, 9),
    'padlock': ('''
.oo.
o..o
GGGG
GkGG
GGGG
''', 6, 8),
    'egg': ('''
.cc.
cCsc
cscC
.cc.
''', 6, 9),
}


def emblem(img, name, palette, dx=0, dy=0):
    art, x, y = EMBLEMS[name]
    stamp(img, x + dx, y + dy, art, palette)
    return img


def sparkle(img, x, y, color, size=1):
    """A tiny four-point glint."""
    put(img, x, y, color)
    if size >= 1:
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            c = get(img, x + dx, y + dy)
            if c[3]:
                put(img, x + dx, y + dy, mix(c, color, 0.55))
