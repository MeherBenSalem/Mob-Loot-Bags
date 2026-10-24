"""Container screens and the reveal toast, drawn in the vanilla GUI style (not copied from vanilla files)."""
from __future__ import annotations

from PIL import Image

from pixel import put, rect, rgb, mix, stamp

BG = '#c6c6c6'
WHITE = '#ffffff'
DARK = '#555555'
BLACK = '#000000'
SLOT_DARK = '#373737'
SLOT = '#8b8b8b'


def panel(img, w, h):
    """The rounded vanilla-style panel with a 2px light top-left edge and a 2px dark bottom-right edge."""
    rect(img, 3, 3, w - 4, h - 4, BG)
    for x in range(2, w - 3):
        put(img, x, 0, BLACK)
        put(img, x, 1, WHITE)
        put(img, x, 2, WHITE)
    for y in range(2, h - 3):
        put(img, 0, y, BLACK)
        put(img, 1, y, WHITE)
        put(img, 2, y, WHITE)
    for x in range(3, w - 1):
        put(img, x, h - 1, BLACK)
        put(img, x, h - 2, DARK)
        put(img, x, h - 3, DARK)
    for y in range(3, h - 1):
        put(img, w - 1, y, BLACK)
        put(img, w - 2, y, DARK)
        put(img, w - 3, y, DARK)
    # Corners.
    put(img, 1, 1, BLACK)
    put(img, 3, 3, WHITE)
    put(img, w - 3, 0, BLACK)
    put(img, w - 3, 1, BLACK)
    put(img, w - 2, 2, BLACK)
    put(img, w - 3, 2, BG)
    put(img, w - 4, 1, WHITE)
    put(img, 1, h - 3, BLACK)
    put(img, 2, h - 2, BLACK)
    put(img, 2, h - 3, BG)
    put(img, 0, h - 3, (0, 0, 0, 0))
    put(img, w - 2, h - 2, BLACK)
    put(img, w - 4, h - 4, DARK)


def slot(img, x, y, inner=SLOT):
    """An 18x18 slot frame whose item sits at (x + 1, y + 1)."""
    rect(img, x, y, x + 17, y + 17, inner)
    for i in range(17):
        put(img, x + i, y, SLOT_DARK)
        put(img, x, y + i, SLOT_DARK)
    for i in range(1, 18):
        put(img, x + 17, y + i, WHITE)
        put(img, x + i, y + 17, WHITE)
    put(img, x + 17, y, SLOT)
    put(img, x, y + 17, SLOT)


def inset(img, x0, y0, x1, y1, inner):
    rect(img, x0, y0, x1, y1, inner)
    for x in range(x0, x1 + 1):
        put(img, x, y0, SLOT_DARK)
        put(img, x, y1, WHITE)
    for y in range(y0, y1 + 1):
        put(img, x0, y, SLOT_DARK)
        put(img, x1, y, WHITE)
    put(img, x1, y0, inner)
    put(img, x0, y1, inner)


def inventory(img, top):
    for row in range(3):
        for col in range(9):
            slot(img, 7 + col * 18, top + row * 18)
    for col in range(9):
        slot(img, 7 + col * 18, top + 58)


def gradient_bar(img, x, y, w, h, stops):
    for i in range(w):
        t = i / max(1, w - 1)
        seg = min(len(stops) - 2, int(t * (len(stops) - 1)))
        local = t * (len(stops) - 1) - seg
        c = mix(stops[seg], stops[seg + 1], local)
        for j in range(h):
            shade = 1.0 if j == 0 else (0.82 if j == h - 1 else 0.94)
            put(img, x + i, y + j, tuple(int(v * shade) if k < 3 else v for k, v in enumerate(c)))
    for i in range(0, w, 4):
        put(img, x + i, y, mix(rgb(stops[-1]), '#ffffff', 0.6))


def button(img, x, y, w, h, face, light, dark):
    rect(img, x, y, x + w - 1, y + h - 1, BLACK)
    rect(img, x + 1, y + 1, x + w - 2, y + h - 2, face)
    for i in range(1, w - 1):
        put(img, x + i, y + 1, light)
        put(img, x + i, y + h - 2, dark)
    for j in range(1, h - 1):
        put(img, x + 1, y + j, light)
        put(img, x + w - 2, y + j, dark)


MODE_ICONS = {
    # Always on: a lit lamp.
    'always': ('''
..yy..
.yYYy.
yYWWYy
yYWWYy
.yYYy.
..yy..
''', {'y': '#c79a1c', 'Y': '#ffd23a', 'W': '#fffbe0'}),
    # Redstone high: a lit redstone torch.
    'high': ('''
..rr..
.rRRr.
..RR..
..ww..
..ww..
..ww..
''', {'r': '#ff5040', 'R': '#ffd0c0', 'w': '#8a6038'}),
    # Redstone low: an unlit torch.
    'low': ('''
..dd..
.d..d.
..dd..
..ww..
..ww..
..ww..
''', {'d': '#5a1410', 'w': '#6a4a2c'}),
    # Pulse: a square wave.
    'pulse': ('''
......
.rrr..
.r.r..
.r.r..
rr.rrr
......
''', {'r': '#ff3020'}),
}


def bag_opener():
    img = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
    w, h = 176, 184
    panel(img, w, h)
    for i in range(9):
        slot(img, 7 + i * 18, 17, inner='#9a8e72')
    inset(img, 7, 37, 168, 45, '#6f6f6f')
    for row in range(2):
        for col in range(9):
            slot(img, 7 + col * 18, 49 + row * 18)
    inventory(img, 101)
    # Down arrows between the input row and the progress track.
    for x in (25, 79, 133, 151):
        stamp(img, x, 35, '''
.a.
''', {'a': '#8b8b8b'})
    # Progress fills: working (gold), output full (red), waiting on an unripe Hourglass bag (teal).
    gradient_bar(img, 0, 184, 160, 5, ['#b06a10', '#f0b030', '#ffe27a'])
    gradient_bar(img, 0, 189, 160, 5, ['#7a1010', '#c02a2a', '#ff6a5a'])
    gradient_bar(img, 0, 194, 160, 5, ['#14605c', '#2fa39b', '#7ee8dc'])
    # Mode buttons: normal row then hover row.
    for m, name in enumerate(('always', 'high', 'low', 'pulse')):
        for hover in range(2):
            x, y = 176 + m * 12, hover * 12
            button(img, x, y, 12, 12, '#8b8b8b' if not hover else '#a8b4ff',
                   '#c6c6c6' if not hover else '#dfe4ff', '#555555' if not hover else '#5560a8')
            art, pal = MODE_ICONS[name]
            stamp(img, x + 3, y + 3, art, pal)
    return img


def loot_recycler():
    img = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
    w, h = 176, 166
    panel(img, w, h)
    for r in range(3):
        for c in range(3):
            slot(img, 7 + c * 18, 17 + r * 18)
    for i in range(3):
        slot(img, 115, 17 + i * 18, inner='#9a8e72')
    inventory(img, 83)
    # Empty arrow (outline), salvage gauge track, and the experience panel.
    stamp(img, 66, 36, '''
..............1.......
..............11......
..............1.1.....
11111111111111...1....
1.................1...
1..................1..
1...................1.
1....................1
1...................1.
1..................1..
1.................1...
11111111111111...1....
..............1.1.....
..............11......
..............1.......
''', {'1': '#8b8b8b'})
    inset(img, 93, 17, 108, 70, '#3a3a3a')
    for y in range(22, 70, 6):
        put(img, 94, y, '#6a6a6a')
        put(img, 95, y, '#6a6a6a')
    inset(img, 137, 17, 170, 51, '#a8a8a8')
    stamp(img, 146, 20, '''
....oooo....
..ooGGGGoo..
.oGGLLGGGGo.
.oGLWLGGGGo.
oGGLLGGGGGGo
oGGGGGGGGGgo
oGGGGGGGGggo
oGGGGGGGGggo
.oGGGGGGggo.
.oggGGgggo..
..ooggggoo..
....oooo....
''', {'o': '#1d4a06', 'G': '#7ed321', 'g': '#4f9a12', 'L': '#d4ff8a', 'W': '#ffffff'})
    # Arrow fill (salvage flowing), gauge fill, and the Take button states.
    stamp(img, 176, 0, '''
..............g.......
..............gg......
..............ggg.....
ggggggggggggggggGg....
gGGGGGGGGGGGGGGGGGg...
gGGGGGGGGGGGGGGGGGGg..
gGGGGGGGGGGGGGGGGGGGg.
gGGGGGGGGGGGGGGGGGGGGg
gGGGGGGGGGGGGGGGGGGGg.
gGGGGGGGGGGGGGGGGGGg..
gGGGGGGGGGGGGGGGGGg...
ggggggggggggggggGg....
..............ggg.....
..............gg......
..............g.......
''', {'g': '#3f8f2a', 'G': '#8fe060'})
    for y in range(52):
        t = y / 51
        c = mix('#b8ff7a', '#2f8a20', t)
        for x in range(14):
            edge = x in (0, 13)
            put(img, 198 + x, y, mix(c, '#123d0a', 0.35) if edge else c)
    for x, y in ((201, 6), (206, 14), (203, 25), (208, 33), (202, 42)):
        put(img, x, y, '#e8ffd0')
    button(img, 176, 52, 30, 14, '#6f6f6f', '#a8a8a8', '#3a3a3a')
    button(img, 176, 66, 30, 14, '#4f8a2e', '#9ade6a', '#2a5418')
    button(img, 176, 80, 30, 14, '#3a3a3a', '#555555', '#262626')
    return img


def reveal_toast():
    """Nine-sliced background for the reveal toast (160x32, 4px border)."""
    img = Image.new('RGBA', (160, 32), (0, 0, 0, 0))
    rect(img, 1, 1, 158, 30, (20, 18, 26, 236))
    for x in range(1, 159):
        put(img, x, 0, '#4a4458')
        put(img, x, 31, '#0a090d')
        put(img, x, 1, (60, 54, 74, 255))
    for y in range(1, 31):
        put(img, 0, y, '#4a4458')
        put(img, 159, y, '#0a090d')
    for x in range(2, 158):
        put(img, x, 30, (12, 11, 16, 255))
    return img


TOAST_MCMETA = {"gui": {"scaling": {"type": "nine_slice", "width": 160, "height": 32, "border": 4}}}
