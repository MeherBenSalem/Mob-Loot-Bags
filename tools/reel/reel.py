#!/usr/bin/env python3
"""The Mob Loot Bags reel: the edit (shots, beats, words), the voice-over and the mix. Renders with edit.py.

  python3 tools/reel/reel.py audio                          # mix only -> out/reel_audio.wav
  python3 tools/reel/reel.py render [start end] [--draft]   # -> out/mob_loot_bags_reel.mp4

Music is music.py's own 120 BPM cue, so every cut sits on a beat (0.5 s) and the drop lands on the first bag of the
tier line. The voice-over is synthesized offline by voice.py (Kokoro, voice am_fenrir); the music ducks under it.
"""
from __future__ import annotations

import sys
from pathlib import Path

import numpy as np

sys.path.insert(0, str(Path(__file__).resolve().parent))
import audio  # noqa: E402
import music  # noqa: E402
import voice  # noqa: E402
from edit import CREAM, GOLD, GOLD_HI, TIER, WHITE, Clip, Edit, Image_, Text, render  # noqa: E402

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "1.21.1" / "fabric" / "runs" / "reel" / "reel" / "out"
ICON = ROOT / "marketing" / "images" / "icon.png"
TEXTURES = ROOT / "1.21.1" / "common" / "src" / "main" / "resources" / "assets" / "mob_loot_bags" / "textures" / "item"

TIERS = ["common", "uncommon", "rare", "epic", "legendary", "mythic"]
BAG_TEX = {name: f"{name}_loot_bag" for name in TIERS + ["cursed", "hourglass", "locked", "summoning"]}

# Captions: Lilita One, white with a dark outline, the key words in gold or their tier's colour.
CAPTION = dict(y=1150, size=86, color=WHITE, accent=GOLD, outline=9, anim="pop", max_width=900)
TOP = dict(CAPTION, y=360)
SMALL = dict(font="body", size=44, color=CREAM, outline=6, anim="rise", max_width=900)

# The voice-over: (reel seconds, line). Each line starts just after its cut.
VO = [
    (0.30, "Every kill... could be a jackpot."),
    (3.75, "Six tiers. From Common... all the way to Mythic."),
    (7.60, "Hold to open, and the loot goes straight to your inventory."),
    (10.55, "Pull a Mythic... and you'll know it."),
    (13.10, "Hold Shift to preview the loot... and the odds."),
    (15.65, "Bosses drop three Epic bags... or better."),
    (18.60, "Fight in the Nether or the End for better luck."),
    (21.60, "Cursed bags? Jackpot... or ambush."),
    (24.60, "Hourglass bags ripen over time."),
    (26.60, "Locked ones need a Diamond Key."),
    (28.60, "Then automate it all, with the Bag Opener."),
    (31.05, "And turn mob-farm junk into brand-new bags."),
    (35.85, "Mob Loot Bags. Free on CurseForge and Modrinth."),
    (39.10, "What's the best thing you've ever pulled?"),
]

# Music sections on the bar grid (bars start at -0.5 s, 2 s each).
SECTIONS = [(0.0, 3.5, "intro"), (3.5, 19.5, "groove"), (19.5, 21.5, "break"), (21.5, 35.5, "lift"), (35.5, 43.0, "outro")]


def tex(name: str) -> str:
    return str(TEXTURES / f"{BAG_TEX[name]}.png")


# ---------------------------------------------------------------- picture

def build() -> Edit:
    c: list[Clip] = []
    mark: dict[str, int] = {}

    def add(label: str | None, clip: Clip) -> None:
        if label:
            mark[label] = len(c)
        c.append(clip)

    # Hook: a sword kill, and a Mythic bag drops with its lightning.
    add("hook", Clip("hook_kill", 0.0, 3.5, zoom=(1.0, 1.05), center=(0.55, 0.55), exposure=0.12))
    # Six tiers, one per beat from the drop.
    add("tiers", Clip("tiers_line", 0.2, 4.0, zoom=(1.0, 1.03), flash=0.5, exposure=0.12))
    # Hold to open (first person), then a Mythic opened in the cherry grove.
    add("open", Clip("open_legendary", 0.1, 3.0, whip=1, exposure=0.1))
    add("mythic", Clip("open_mythic", 0.1, 2.5, zoom=(1.0, 1.04)))
    # Shift loot preview.
    add("preview", Clip("preview", 0.3, 2.5, zoom=(1.0, 1.02), whip=-1))
    # A Wither drops three bags.
    add("boss", Clip("boss_wither", 0.0, 3.0, zoom=(1.0, 1.04), punch=0.06, exposure=0.3))
    # The Nether: better luck, themed loot.
    add("nether", Clip("nether_kill", 0.1, 1.5, zoom=(1.0, 1.04), whip=1, exposure=0.12))
    # Special bags, two per beat, then a Cursed bag's ambush.
    add("specials", Clip("specials_line", 0.2, 1.5, speed=2.0, exposure=0.35))
    add("cursed", Clip("cursed", 0.6, 3.0, zoom=(1.0, 1.05), flash=0.6, exposure=0.3))
    # Hourglass and Locked bags (first person).
    add("hourglass", Clip("hourglass", 0.8, 2.0, whip=-1, exposure=0.1))
    add("locked", Clip("locked", 0.1, 2.0, whip=1, exposure=0.1))
    # The machines.
    add("opener", Clip("opener_gui", 0.3, 2.5, zoom=(1.0, 1.03), whip=-1))
    add("recycler", Clip("recycler_gui", 0.4, 2.5, zoom=(1.0, 1.03), whip=1))
    # The peak: the best moments again, one per beat.
    add("montage", Clip("hook_kill", 0.45, 0.5, punch=0.08, flash=0.4))
    add(None, Clip("boss_wither", 1.0, 0.5, punch=0.08))
    add(None, Clip("open_mythic", 1.35, 0.5, punch=0.08))
    add(None, Clip("tiers_line", 3.0, 0.5, punch=0.08))
    # End card over the tier line's beams, slowed and dimmed.
    add("end", Clip("tiers_line", 3.2, 6.5, speed=0.12, zoom=(1.08, 1.14), dim=0.55, blur=0, fade_out=0.6))

    starts = np.cumsum([0] + [x.dur for x in c])

    def at(label: str) -> float:
        return float(starts[mark[label]])

    t: list[Text] = []
    im: list[Image_] = []
    # Hook: legible from the first frame (the thumbnail).
    t.append(Text("MOBS DROP\n*LOOT BAGS*", 0.0, at("tiers") - 0.05, **{**TOP, "size": 104, "anim": "punch", "line_gap": 1.0}))
    # Tiers: each name pops with its bag, in its colour.
    tiers0 = at("tiers")
    t.append(Text("*6* TIERS", tiers0 + 0.05, at("open") - 0.05, **TOP))
    for i, name in enumerate(TIERS):
        a = tiers0 + 0.5 * i
        b = a + 0.5 if i < 5 else at("open") - 0.05
        t.append(Text(name.upper(), a + 0.03, b, **{**CAPTION, "y": 1250, "color": TIER[name], "anim": "slam"}))
        im.append(Image_(tex(name), a + 0.03, b, y=1095, width=150, pixel=True, anim="pop"))
    t.append(Text("HOLD TO *OPEN*", at("open") + 0.1, at("mythic") - 0.05, **{**CAPTION, "y": 1090}))
    t.append(Text("LOOT STRAIGHT TO YOUR INVENTORY", at("open") + 1.0, at("mythic") - 0.05, **{**SMALL, "y": 1185}))
    t.append(Text("PULL A *MYTHIC*", at("mythic") + 0.08, at("mythic") + 1.15, **{**TOP, "accent": TIER["mythic"]}))
    t.append(Text("SHIFT TO *PREVIEW*", at("preview") + 0.1, at("boss") - 0.05, **TOP))
    t.append(Text("BOSSES DROP *3* BAGS", at("boss") + 0.1, at("nether") - 0.05, **TOP))
    t.append(Text("EPIC OR BETTER", at("boss") + 0.6, at("nether") - 0.05, **{**SMALL, "y": 450, "color": TIER["epic"]}))
    t.append(Text("NETHER & END:\n*BETTER LUCK*", at("nether") + 0.08, at("specials") - 0.05, **{**TOP, "line_gap": 1.0}))
    t.append(Text("*SPECIAL* BAGS", at("specials") + 0.05, at("cursed") - 0.05, **TOP))
    t.append(Text("CURSED · HOURGLASS · LOCKED · SUMMONING", at("specials") + 0.2, at("cursed") - 0.05, **{**SMALL, "y": 450, "size": 38}))
    t.append(Text("*CURSED*: JACKPOT\nOR AMBUSH?", at("cursed") + 0.1, at("hourglass") - 0.05,
                  **{**TOP, "accent": TIER["cursed"], "line_gap": 1.0}))
    t.append(Text("*HOURGLASS* BAGS RIPEN", at("hourglass") + 0.1, at("locked") - 0.05, **{**TOP, "y": 640, "accent": TIER["hourglass"]}))
    t.append(Text("*LOCKED*? USE A KEY", at("locked") + 0.1, at("opener") - 0.05, **{**TOP, "y": 640, "accent": TIER["locked"]}))
    t.append(Text("*BAG OPENER*", at("opener") + 0.1, at("recycler") - 0.05, **TOP))
    t.append(Text("AUTOMATE IT ALL", at("opener") + 0.5, at("recycler") - 0.05, **{**SMALL, "y": 450}))
    t.append(Text("*LOOT RECYCLER*", at("recycler") + 0.1, at("montage") - 0.05, **TOP))
    t.append(Text("MOB-FARM JUNK INTO NEW BAGS", at("recycler") + 0.5, at("montage") - 0.05, **{**SMALL, "y": 450}))
    t.append(Text("13 ADVANCEMENTS · COMMANDS · DATAPACKS", at("montage") + 0.05, at("end") - 0.05,
                  **{**SMALL, "y": 1250, "size": 40}))
    t.append(Text("FULLY *CONFIGURABLE*", at("montage") + 0.05, at("end") - 0.05, **{**CAPTION, "y": 1160}))
    # End card.
    end = at("end")
    stop = end + 6.5
    im.append(Image_(str(ICON), end + 0.1, stop, y=510, width=290, glow=(255, 194, 58), anim="pop"))
    t.append(Text("MOB LOOT BAGS", end + 0.3, stop, y=770, size=120, color=WHITE, outline=10, anim="pop",
                  gradient=((255, 246, 208), (255, 194, 58)), max_width=980))
    t.append(Text("2.0  ·  SPOILS OF THE HUNT", end + 0.6, stop, y=875, font="body", size=40, color=GOLD_HI, tracking=0.12,
                  outline=5, anim="rise"))
    t.append(Text("FREE ON *CURSEFORGE*\n& *MODRINTH*", end + 0.9, stop, y=1030, size=74, color=WHITE, accent=GOLD, outline=8,
                  anim="pop", line_gap=1.0))
    t.append(Text("FABRIC  ·  FORGE  ·  NEOFORGE", end + 1.2, stop, y=1170, font="body", size=36, color=CREAM, tracking=0.08,
                  outline=5, anim="rise"))
    t.append(Text("1.20.1  ·  1.21.1  ·  26.1.2  ·  26.2  ·  26.3", end + 1.2, stop, y=1220, font="body", size=34, color=CREAM,
                  tracking=0.06, outline=5, anim="rise"))
    t.append(Text("WHAT'S YOUR *BEST PULL*?", end + 3.4, stop, y=245, size=66, color=WHITE, accent=GOLD, outline=8, anim="pop",
                  max_width=980))
    t.append(Text("TELL US IN THE COMMENTS", end + 3.6, stop, y=318, font="body", size=38, color=CREAM, tracking=0.08, outline=5,
                  anim="rise"))
    for i, name in enumerate(TIERS):
        im.append(Image_(tex(name), end + 1.6 + 0.08 * i, stop, y=1335, width=96, x=int(1080 // 2 + (i - 2.5) * 118), pixel=True, anim="pop"))

    edit = Edit(clips=c, texts=t, images=im, flashes=[(at("tiers"), 0.5, None), (at("end"), 0.6, None)])
    edit.marks = {k: at(k) for k in mark}
    return edit


# ---------------------------------------------------------------- sound

# Game sounds per clip are boosted or cut by event: the mod's own sounds forward, background machines out.
BOOST = {"mob_loot_bags:bag": 1.5, "bag.rustle": 0.55, "mob_loot_bags:reveal": 1.6, "mob_loot_bags:unlock": 1.5, "mob_loot_bags:cursed": 1.4,
         "mob_loot_bags:hourglass": 1.5, "lightning": 0.8, "thunder": 0.5, "toast": 1.3, "challenge_complete": 1.2}


def mix(edit: Edit) -> np.ndarray:
    total = edit.duration + 0.4
    n = int(total * audio.SR)
    game = np.zeros((n, 2), np.float32)
    fx = np.zeros((n, 2), np.float32)
    marks = edit.marks

    cue = music.score(SECTIONS, total, accents=(0.0, marks["end"]))
    tail = int((edit.duration - 1.4) * audio.SR)
    env = np.ones(n, np.float32)
    env[tail:] = np.linspace(1, 0, n - tail) ** 1.6
    cue = cue[:n] * env[:, None]

    starts = np.cumsum([0] + [c.dur for c in edit.clips])
    machine_shots = {"workshop", "opener_gui", "recycler_gui"}
    for clip, t0 in zip(edit.clips, starts):
        if clip.image is not None:
            continue
        # Outside the machine shots, the workshop's blocks are not in frame: their sounds stay out.
        skip = set() if clip.shot in machine_shots else {"block"}
        audio.game_sounds(game, clip.shot, clip.src, clip.src + clip.dur * clip.speed, t0, clip.speed, gain=1.0, boost=BOOST,
                          skip_sources=skip)
    # The tier and specials lines summon their bags; give each the mod's drop chime, pitched by tier as the mod does.
    chime = audio.load(str(audio.event_file("mob_loot_bags:bag.drop")))
    for i in range(6):
        audio.place(fx, audio.repitch(chime, 0.8 + 0.12 * i), marks["tiers"] + 0.5 * i, 0.55)
    for i in range(6):  # the specials line runs at double speed: a bag every eighth note
        audio.place(fx, audio.repitch(chime, 1.0 + 0.05 * i), marks["specials"] + 0.25 * i, 0.22)
    for i, clip in enumerate(edit.clips):
        if clip.whip:
            audio.place(fx, audio.whoosh(0.45), starts[i] - 0.28, 0.4, pan=0.4 * clip.whip)
    audio.place(fx, audio.load(str(audio.event_file("mob_loot_bags:bag.reveal.mythic"))), marks["end"] + 0.15, 0.5)

    # Voice-over: synthesized lines, a touch of stereo width, the music ducked under them.
    vo = np.zeros((n, 2), np.float32)
    for t, clip in voice.render(VO):
        audio.place(vo, clip, t, 1.0)
    level = np.convolve(np.abs(vo[:, 0]), np.ones(2400) / 2400, mode="same")
    speech = np.clip(level / (np.percentile(level[level > 1e-4], 90) + 1e-9) * 1.6, 0, 1)
    # Held through short gaps between words and opened ~0.1 s before each line, so consonant onsets stay clear.
    hold = np.convolve(speech, np.ones(14400) / 14400, mode="same")
    under = np.clip(np.maximum(speech, hold) * 1.6, 0, 1)[:, None]
    # Under the voice: the music drops and loses its 1.5-5 kHz band, the game sounds step back.
    presence = music._filt(cue, "bandpass", [1500, 5000])
    cue = (cue - presence * 0.6 * under) * (1 - 0.6 * under)
    game = game * (1 - 0.45 * under)

    vo_gain = 0.98 / (np.abs(vo).max() + 1e-9) * 0.85
    mixdown = cue * 0.62 + game * 0.8 + fx * 0.75 * (1 - 0.5 * under) + vo * vo_gain
    peak = np.abs(mixdown).max()
    mixdown = mixdown / max(1.0, peak / 0.98)
    return audio.limiter(mixdown * 1.3, 0.95)


def main() -> None:
    edit = build()
    wav = OUT / "reel_audio.wav"
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    if not args or args[0] in ("audio", "render"):
        audio.write_wav(wav, mix(edit))
        print(f"audio -> {wav} ({edit.duration:.2f} s)")
    if args and args[0] == "render":
        start = float(args[1]) if len(args) > 1 else 0.0
        stop = float(args[2]) if len(args) > 2 else None
        draft = "--draft" in sys.argv
        target = OUT / ("reel_draft.mp4" if draft else "mob_loot_bags_reel.mp4")
        render(edit, target, wav, start, stop, crf=24 if draft else 18, preset="veryfast" if draft else "slow")
        print(f"video -> {target}")


if __name__ == "__main__":
    main()
