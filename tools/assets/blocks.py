"""Block textures for the Bag Opener and the Loot Recycler (16x16 faces; front_on faces are animated)."""
from __future__ import annotations

from PIL import Image

from pixel import canvas, put, get, rect, rgb, mix, stamp, Rng

OAK = ['#24170d', '#33210f', '#422c18', '#543920', '#664629', '#7a5734']
IRON = ['#16181c', '#252a30', '#373e47', '#4f5761', '#6b7480', '#929ca8', '#bcc5cf']
BRASS = ['#4a2e08', '#7a5214', '#b07c26', '#dcab4a', '#f8d67e']
STONE = ['#1f1f24', '#2b2b31', '#38383f', '#47474f', '#585862', '#6c6c77']
GREEN = ['#163a1a', '#24592a', '#36803c', '#52a756', '#86d681']
HAZARD = ['#d9b21c', '#1a1a1a']


def iron_frame(img, ramp=IRON, rivets=True):
    """A bevelled 1px iron border with rivets in the corners."""
    for i in range(16):
        put(img, i, 0, ramp[4])
        put(img, 0, i, ramp[4])
        put(img, i, 15, ramp[1])
        put(img, 15, i, ramp[1])
    put(img, 0, 0, ramp[5])
    put(img, 15, 15, ramp[0])
    if rivets:
        for x, y in ((1, 1), (14, 1), (1, 14), (14, 14)):
            put(img, x, y, ramp[5])
            put(img, x + (1 if x < 8 else -1), y + (1 if y < 8 else -1), ramp[2])


def planks(img, x0, y0, x1, y1, ramp=OAK, seed=1, board=4):
    """Horizontal boards with grain streaks and dark seams."""
    rng = Rng(seed)
    for y in range(y0, y1 + 1):
        row = (y - y0) % board
        for x in range(x0, x1 + 1):
            if row == board - 1:
                put(img, x, y, ramp[0])
                continue
            base = 3 if row == 0 else 2
            put(img, x, y, ramp[base])
        if row != board - 1:
            for _ in range(2):
                gx = rng.randint(x0, x1 - 3)
                length = rng.randint(2, 5)
                tone = rng.choice([1, 4])
                for gx2 in range(gx, min(x1 + 1, gx + length)):
                    put(img, gx2, y, ramp[tone])
    # Nail heads where boards meet the frame.
    for y in range(y0 + 1, y1, board):
        put(img, x0, y, ramp[5])
        put(img, x1, y, ramp[5])


def plate(img, x0, y0, x1, y1, ramp=IRON, seed=3, bevel=True):
    rng = Rng(seed)
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            t = 3 if (x + y) % 7 else 2
            put(img, x, y, ramp[t])
    for _ in range(4):
        put(img, rng.randint(x0 + 1, x1 - 1), rng.randint(y0 + 1, y1 - 1), ramp[2])
    if bevel:
        for x in range(x0, x1 + 1):
            put(img, x, y0, ramp[4])
            put(img, x, y1, ramp[1])
        for y in range(y0, y1 + 1):
            put(img, x0, y, ramp[4])
            put(img, x1, y, ramp[1])


def stone(img, x0, y0, x1, y1, ramp=STONE, seed=5):
    """Cut stone bricks, two courses per 8px."""
    rng = Rng(seed)
    for y in range(y0, y1 + 1):
        course = (y - y0) // 4
        for x in range(x0, x1 + 1):
            offset = 0 if course % 2 == 0 else 4
            seam_y = (y - y0) % 4 == 3
            seam_x = (x - x0 + offset) % 8 == 7
            if seam_y or seam_x:
                put(img, x, y, ramp[0])
            else:
                t = rng.choice([2, 3, 3, 3, 4])
                put(img, x, y, ramp[t])
    for y in range(y0, y1 + 1, 4):
        for x in range(x0, x1 + 1):
            if get(img, x, y) != rgb(ramp[0]):
                put(img, x, y, ramp[4])


# ---------------------------------------------------------------- Bag Opener

def opener_side():
    img = canvas()
    planks(img, 1, 1, 14, 14, seed=11)
    iron_frame(img)
    # Iron strap across the middle.
    for x in range(1, 15):
        put(img, x, 7, IRON[4])
        put(img, x, 8, IRON[2])
    for x in (3, 12):
        put(img, x, 7, IRON[6])
        put(img, x, 8, IRON[3])
    return img


def opener_top():
    img = canvas()
    planks(img, 1, 1, 14, 14, seed=12)
    iron_frame(img)
    # Funnel: iron rings narrowing into a dark intake.
    for i, tone in enumerate((4, 3, 2, 1)):
        a, b = 3 + i, 12 - i
        for x in range(a, b + 1):
            put(img, x, a, IRON[tone + 1])
            put(img, x, b, IRON[max(0, tone - 1)])
        for y in range(a, b + 1):
            put(img, a, y, IRON[tone + 1])
            put(img, b, y, IRON[max(0, tone - 1)])
    rect(img, 7, 7, 8, 8, '#07080a')
    for x, y in ((3, 3), (12, 3), (3, 12), (12, 12)):
        put(img, x, y, BRASS[3])
    return img


def opener_bottom():
    img = canvas()
    plate(img, 1, 1, 14, 14, seed=13)
    iron_frame(img)
    # Output chute grate.
    rect(img, 5, 5, 10, 10, IRON[0])
    for x in range(5, 11, 2):
        for y in range(5, 11):
            put(img, x, y, IRON[3])
    return img


def opener_front(active=False, frame=0):
    img = canvas()
    planks(img, 1, 1, 14, 14, seed=14)
    iron_frame(img)
    # Window: dark when idle, lantern-lit when working, with a loot bag inside.
    glow = ['#3b1d06', '#8a4a0e', '#e09a2a', '#ffd76a'] if active else ['#0c0d10', '#121418', '#191c21', '#20242a']
    rect(img, 3, 3, 12, 10, IRON[1])
    rect(img, 4, 4, 11, 9, glow[1])
    rect(img, 5, 5, 10, 9, glow[2])
    for x in range(3, 13):
        put(img, x, 3, IRON[4])
    bag = {'r': BRASS[2] if active else BRASS[3], 'B': '#5a3818' if active else '#4a3420', 'b': '#2e1c0c' if active else '#33241a',
           'h': '#8a5a2b' if active else '#5d4530'}
    stamp(img, 5, 4, '''
..rr..
.rrrr.
.hBBB.
hBBBBb
hBBBBb
.bbbb.
''', bag)
    if active:
        sparks = [((4, 4), (11, 6)), ((11, 4), (4, 7))][frame % 2]
        for x, y in sparks:
            put(img, x, y, glow[3])
    # Brass nameplate with three screws.
    rect(img, 4, 12, 11, 13, BRASS[2])
    for x in range(4, 12):
        put(img, x, 12, BRASS[3])
    for x in (5, 8, 10):
        put(img, x, 13, BRASS[0])
    # Status lamp.
    lamp = ['#3a7a12', '#9cff4a'] if active else ['#3a0c0c', '#6e1a1a']
    put(img, 13, 4, lamp[1])
    put(img, 13, 5, lamp[0])
    return img


# ---------------------------------------------------------------- Loot Recycler

def recycler_side():
    img = canvas()
    stone(img, 0, 0, 15, 15, seed=21)
    # Copper-green recycling emblem on an iron plate.
    rect(img, 3, 3, 12, 12, IRON[2])
    for i in range(3, 13):
        put(img, i, 3, IRON[4])
        put(img, 3, i, IRON[4])
        put(img, i, 12, IRON[1])
        put(img, 12, i, IRON[1])
    stamp(img, 4, 4, '''
...gg...
..gGGg..
.g.GG.g.
.gG..Gg.
gG....Gg
gGGggGGg
.gggggg.
........
''', {'g': GREEN[1], 'G': GREEN[3]})
    put(img, 7, 5, GREEN[4])
    put(img, 5, 9, GREEN[4])
    put(img, 10, 9, GREEN[4])
    return img


def recycler_top(frame=0):
    img = canvas()
    # Hazard-striped rim around a recessed pair of toothed rollers.
    for y in range(16):
        for x in range(16):
            if x < 2 or x > 13 or y < 2 or y > 13:
                put(img, x, y, HAZARD[0] if (x + y) % 4 < 2 else HAZARD[1])
    rect(img, 2, 2, 13, 13, '#0a0b0d')
    for roller_y in (3, 9):
        rect(img, 3, roller_y, 12, roller_y + 3, IRON[2])
        for x in range(3, 13):
            put(img, x, roller_y, IRON[4])
            put(img, x, roller_y + 3, IRON[1])
        for x in range(3 + frame % 2, 13, 2):
            put(img, x, roller_y + 1, IRON[5])
            put(img, x, roller_y + 2, IRON[3])
    for x in range(0, 16):
        put(img, x, 0, IRON[4])
        put(img, x, 15, IRON[1])
    for y in range(0, 16):
        put(img, 0, y, IRON[4])
        put(img, 15, y, IRON[1])
    return img


def recycler_bottom():
    img = canvas()
    stone(img, 0, 0, 15, 15, seed=23)
    return img


def recycler_front(active=False, frame=0):
    img = canvas()
    stone(img, 0, 0, 15, 15, seed=24)
    # Furnace-style mouth with a grate; it glows and throws sparks while grinding.
    rect(img, 3, 6, 12, 13, IRON[1])
    inner = ['#2a0e04', '#6a2408', '#c24a10', '#ff8a2a', '#ffd27a'] if active else ['#0b0b0d', '#121216', '#18181d', '#1e1e24', '#24242b']
    rect(img, 4, 7, 11, 12, inner[1])
    rect(img, 5, 9, 10, 12, inner[2])
    if active:
        rect(img, 6, 10, 9, 12, inner[3])
        sparks = [((5, 8), (9, 7)), ((7, 7), (10, 8)), ((6, 8), (8, 7))][frame % 3]
        for x, y in sparks:
            put(img, x, y, inner[4])
    for x in range(4, 12, 2):
        for y in range(7, 13):
            put(img, x, y, IRON[3] if y % 2 else IRON[2])
    for x in range(3, 13):
        put(img, x, 6, IRON[4])
    # Gauge dial: the needle swings to the right while working.
    rect(img, 5, 1, 10, 4, IRON[2])
    for x in range(5, 11):
        put(img, x, 1, IRON[4])
    rect(img, 6, 2, 9, 4, '#e8e2cc')
    needle = (8, 2) if active else (6, 3)
    put(img, 7, 4, '#1a1a1a')
    put(img, needle[0], needle[1], '#c0281e')
    put(img, (needle[0] + 7) // 2, (needle[1] + 4) // 2, '#c0281e')
    # Rivets.
    for x, y in ((2, 6), (13, 6), (2, 13), (13, 13)):
        put(img, x, y, IRON[5])
    return img


FACES = {
    'bag_opener_side': opener_side,
    'bag_opener_top': opener_top,
    'bag_opener_bottom': opener_bottom,
    'bag_opener_front': lambda: opener_front(False),
    'loot_recycler_side': recycler_side,
    'loot_recycler_bottom': recycler_bottom,
    'loot_recycler_front': lambda: recycler_front(False),
}

ANIMATED_FACES = {
    'bag_opener_front_on': ([opener_front(True, i) for i in range(2)], 5),
    'loot_recycler_front_on': ([recycler_front(True, i) for i in range(3)], 3),
    'loot_recycler_top': ([recycler_top(i) for i in range(2)], 4),
}


def iso(top, left, right, scale=8):
    """A quick isometric cube preview (top, left face, right face)."""
    size = 16 * scale
    t = top.resize((size, size), Image.NEAREST).rotate(45, expand=True, resample=Image.NEAREST)
    t = t.resize((t.width, t.height // 2), Image.NEAREST)
    l = left.resize((size, size), Image.NEAREST)
    r = right.resize((size, size), Image.NEAREST)
    w = t.width
    out = Image.new('RGBA', (w, t.height + size + size // 2), (0, 0, 0, 0))
    from PIL import ImageEnhance
    l = ImageEnhance.Brightness(l).enhance(0.8)
    r = ImageEnhance.Brightness(r).enhance(0.62)
    half = w // 2
    lw = l.transform((half, size + t.height // 2), Image.AFFINE, (size / half, 0, 0, -0.5 * size / half, 1, 0), Image.NEAREST)
    rw = r.transform((half, size + t.height // 2), Image.AFFINE, (size / half, 0, 0, 0.5 * size / half, 1, -t.height // 2 * 1.0), Image.NEAREST)
    out.alpha_composite(lw, (0, t.height // 2))
    out.alpha_composite(rw, (half, t.height // 2))
    out.alpha_composite(t, (0, 0))
    return out
