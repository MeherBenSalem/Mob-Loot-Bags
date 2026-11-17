#!/usr/bin/env python3
"""Shot jobs for the Mob Loot Bags reel, written as JSON into the reel director's queue.

  python3 tools/reel/shots.py list
  python3 tools/reel/shots.py queue NAME [NAME ...]     # or a prefix ending in '*'
  python3 tools/reel/shots.py still NAME [NAME ...]     # one PNG of a shot's first frame, to check the framing

Coordinates are in the director's world ("ReelWorld", seed 20261019), found with the director's scan jobs:
  Meadow plateau, flat at y=111 around x 4..20, z -194..-182; the valley and a lake lie south, mountains north-west
  Cherry grove valley, flat at y=100 around (-381, -174)
  Nether wastes around (-32, -24) in the Nether
Yaw 0 faces south (+z), 90 west, 180 north, -90 east. Minecraft's FOV is vertical; a 9:16 frame is only ~45 degrees wide.

Drops are real: every kill below runs the mod's own drop code. Each shot writes the mod's config first (a drop chance
of 1 and the tier the shot shows), the way a pack maker would, so the take is the same every time.
"""
from __future__ import annotations

import copy
import json
import math
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
JOBS = ROOT / "1.21.1" / "fabric" / "runs" / "reel" / "reel" / "jobs"

OVERWORLD = "minecraft:overworld"
NETHER = "minecraft:the_nether"
M = "mob_loot_bags:"
BAG = {t: M + t + "lootbag" for t in ("common", "uncommon", "rare", "epic", "legendary", "mythic")}
BAG |= {"cursed": M + "cursed_lootbag", "hourglass": M + "timed_loot_bag", "locked": M + "locked_lootbags",
        "summoning": M + "summoning_lootbags"}
KEY, TALISMAN = M + "diamond_key", M + "fortune_talisman"
OPENER, RECYCLER = M + "loot_bag_opener_block", M + "loot_bag_recycle_block"
TIERS = ["common", "uncommon", "rare", "epic", "legendary", "mythic"]

SUNSET, NIGHT = 12650, 18000  # sunset late enough that zombies no longer burn

SHOTS: dict[str, dict] = {}


def shot(name: str, **job) -> None:
    job.setdefault("dimension", OVERWORLD)
    job["name"] = name
    SHOTS[name] = job


# ---------------------------------------------------------------- staging helpers

def _merge(base: dict, over: dict | None) -> dict:
    out = copy.deepcopy(base)
    for k, v in (over or {}).items():
        out[k] = _merge(out.get(k, {}), v) if isinstance(v, dict) else v
    return out


# Every kill drops a bag, no special bags unless asked, no pity or carriers: the shot decides what drops.
BASE_DROPS = {"hostile_chance": 1.0, "specials": {"cursed": 0.0, "hourglass": 0.0, "locked": 0.0, "summoning": 0.0},
              "pity": {"enabled": False}, "carriers": {"chance": 0.0}}
BASE_BAGS: dict = {}
BASE_MACHINES: dict = {}


def tier(name: str) -> dict:
    """Drop weights that always roll this tier."""
    return {"tier_weights": {t: (1 if t == name else 0) for t in TIERS}}


def config(drops: dict | None = None, bags: dict | None = None, machines: dict | None = None) -> list[str]:
    """Writes the mod's three server config files (defaults plus these values) and reloads them."""
    cmds = []
    for name, base, over in (("drops", BASE_DROPS, drops), ("bags", BASE_BAGS, bags), ("machines", BASE_MACHINES, machines)):
        cmds.append(f"!write config/mob_loot_bags/{name}.json {json.dumps(_merge(base, over))}")
    return cmds + ["lootbags reload"]


def mob(entity: str, x: float, y: float, z: float, yaw: float, tag: str = "reel_mob", ai: bool = False, extra: str = "") -> str:
    nbt = f'Tags:["{tag}"],Rotation:[{yaw}f,0f],PersistenceRequired:1b'
    if not ai:
        nbt += ",NoAI:1b"
    if extra:
        nbt += "," + extra
    return f"summon {entity} {x} {y} {z} {{{nbt}}}"


def item(item_id: str, x: float, y: float, z: float, motion=(0.0, 0.0, 0.0), count: int = 1, tag: str = "reel_item") -> str:
    """A dropped item that never despawns and cannot be picked up, as a bag lying where a mob died."""
    m = ",".join(f"{v}d" for v in motion)
    return (f'summon item {x} {y} {z} {{Item:{{id:"{item_id}",count:{count}}},PickupDelay:32767,Age:-32768,'
            f'Motion:[{m}],Tags:["{tag}"]}}')


def hero(items: dict[str, str] | None = None, xp: int = 0) -> list[str]:
    """The player: unhurt, fed, empty inventory but for `items` (slot -> "id count")."""
    cmds = ["clear @p", "effect clear @p", "effect give @p resistance infinite 4 true", "effect give @p saturation infinite 0 true",
            "effect give @p regeneration infinite 4 true", "attribute @p minecraft:generic.knockback_resistance base set 1",
            f"xp set @p {xp} levels", "xp set @p 0 points"]
    for slot, what in (items or {}).items():
        cmds.append(f"item replace entity @p {slot} with {what}")
    return cmds


def key(t, pos, look=None, tag=None, off=None, rot=None, fov=None, roll=None) -> dict:
    k = {"t": t, "pos": pos}
    for name, value in (("look", look), ("lookTag", tag), ("lookOffset", off), ("rot", rot), ("fov", fov), ("roll", roll)):
        if value is not None:
            k[name] = value
    return k


def ev(t, **kw) -> dict:
    kw["t"] = t
    return kw


def arc(center, radius, a0, a1, y, n, t0, t1, look, fov, fov1=None) -> list[dict]:
    """Keys on a horizontal circular arc around center (angles in degrees, 0 = +x, 90 = +z)."""
    keys = []
    for i in range(n):
        u = i / (n - 1)
        a = math.radians(a0 + (a1 - a0) * u)
        pos = [round(center[0] + radius * math.cos(a), 3), y, round(center[2] + radius * math.sin(a), 3)]
        f = fov if fov1 is None else fov + (fov1 - fov) * u
        keys.append(key(round(t0 + (t1 - t0) * u, 3), pos, look=look, fov=round(f, 2)))
    return keys


def clear_area(x0, y0, z0, x1, y1, z1) -> list[str]:
    """Grass and flowers out of the way of small subjects (dropped bags are a quarter of a block)."""
    return [f"fill {x0} {y0} {z0} {x1} {y1} {z1} minecraft:air replace {b}" for b in
            ("minecraft:short_grass", "minecraft:tall_grass", "minecraft:allium", "minecraft:azure_bluet", "minecraft:oxeye_daisy",
             "minecraft:cornflower", "minecraft:dandelion", "minecraft:poppy", "minecraft:pink_petals")]


# The cinematic look: no HUD, the player visible. HUD shots lift the hotbar and the toasts clear of the phone apps' UI.
CINE = dict(hud="none", hidePlayer=False, gamemode="survival", renderDistance=10, settle=4)
HUD = dict(hud="full", hudHide=["crosshair", "chat", "effects", "misc"], hudOffset=132, titleOffset=54, toasts=True,
           toastOffset=[0, 52],
           hidePlayer=False, gamemode="survival", renderDistance=10, settle=4, camera="player", hand=True, handInset=[28, 10])
TITLES_ONLY = dict(hud="full", hudHide=["crosshair", "chat", "effects", "misc", "hotbar"], hudOffset=60, toasts=True,
                   toastOffset=[0, 52])

# ---------------------------------------------------------------- the hook: a kill drops a Mythic bag (meadow, sunset)

HOOK_P = [13.5, 111, -186.5]   # the player, facing west into the sunset
HOOK_Z = [11.3, 111, -186.5]   # the zombie, facing the player
# Plants out of the way, and the workshop's machines gone (other shots build them here).
MEADOW_CLEAR = clear_area(2, 111, -194, 22, 112, -180) + ["fill 6 111 -193 15 115 -187 minecraft:air"]

shot("hook_kill", duration=4.2, preroll=2.0, timing="smooth", playerPos=HOOK_P, playerRot=[90, 0], **CINE,
     setup=["time set %d" % SUNSET] + MEADOW_CLEAR + config(drops=tier("mythic")) +
     hero({"weapon.mainhand": "minecraft:netherite_sword"}) + [mob("minecraft:zombie", *HOOK_Z, -90)],
     events=[ev(-1.5, face="reel_mob", faceOffset=[0, 0.1, 0]), ev(-1.0, tag="reel_mob", health=0.05),
             ev(0.35, input="attack")],
     speed=[{"t": 0, "v": 1}, {"t": 0.40, "v": 1}, {"t": 0.55, "v": 0.3}, {"t": 1.7, "v": 0.3}, {"t": 2.3, "v": 1}],
     keys=[key(0, [17.0, 112.0, -190.0], look=[11.6, 112.0, -186.4], fov=56),
           key(1.6, [16.4, 111.8, -189.4], look=[11.6, 111.9, -186.4], fov=50),
           key(4.2, [15.4, 111.6, -188.7], look=[11.5, 111.8, -186.4], fov=44)])

# ---------------------------------------------------------------- six tiers pop up in a row (meadow, sunset)

# A row of bags receding west into the sunset, zig-zagging so each stays clear of the one behind it: one pops up on
# every beat (120 BPM), nearest first, while the camera eases in.
POP = 0.5
LINE_X = [14.2, 13.1, 12.0, 10.9, 9.8, 8.7]
LINE_Z = [-186.15, -186.95, -186.15, -186.95, -186.15, -186.55]


def line_shot(name: str, items: list[str], time: int, duration: float, **extra) -> None:
    shot(name, duration=duration, preroll=0.5, timing="smooth", playerPos=[18.5, 111, -192.5], **CINE, **extra,
         setup=["time set %d" % time] + MEADOW_CLEAR + hero(),
         events=[ev(0.2 + POP * i, cmd=item(it, LINE_X[i], 111.05, LINE_Z[i], motion=(0, 0.22, 0))) for i, it in enumerate(items)],
         keys=[key(0, [17.9, 111.75, -186.55], look=[8.0, 111.6, -186.55], fov=50),
               key(duration, [17.5, 111.8, -186.55], look=[8.0, 111.75, -186.55], fov=48)])


line_shot("tiers_line", [BAG[t] for t in TIERS], SUNSET, 4.0)

# ---------------------------------------------------------------- hold to open: first person, a Legendary bag

shot("open_legendary", duration=4.2, preroll=1.5, playerPos=[12.5, 111, -183.5], playerRot=[0, 14], **HUD,
     setup=["time set %d" % SUNSET] + config() +
     hero({"hotbar.0": BAG["legendary"] + " 3", "hotbar.8": "minecraft:netherite_sword"}),
     events=[ev(-1.0, slot=0), ev(0.35, input="use"), ev(1.35, input="release")],
     aim=[key(0, [0, 0, 0], rot=[84, 9]), key(4.2, [0, 0, 0], rot=[92, 6])])

# ---------------------------------------------------------------- a Mythic bag opened, third person (cherry grove)

GROVE_P = [-381.5, 100, -173.5]
shot("open_mythic", duration=4.4, preroll=1.5, timing="smooth", playerPos=GROVE_P, playerRot=[0, -8], **(CINE | TITLES_ONLY),
     setup=["time set 6000"] + clear_area(-386, 100, -178, -377, 101, -169) + config() +
     hero({"hotbar.0": BAG["mythic"] + " 1"}) + ["advancement revoke @p only mob_loot_bags:open_mythic"],
     events=[ev(-1.0, slot=0), ev(0.4, input="use"), ev(1.4, input="release")],
     keys=arc([GROVE_P[0], 0, GROVE_P[2]], 4.2, 120, 60, 101.6, 4, 0, 4.4, [GROVE_P[0], 101.8, GROVE_P[2]], 60, 54))

# ---------------------------------------------------------------- Shift loot preview in the inventory

shot("preview", duration=4.0, preroll=0.5, playerPos=GROVE_P, playerRot=[180, 4], hud="none", hidePlayer=False,
     gamemode="survival", camera="player", renderDistance=8, settle=4,
     setup=["time set 6000"] + config() + hero({
         "inventory.0": BAG["common"] + " 12", "inventory.1": BAG["uncommon"] + " 7", "inventory.2": BAG["rare"] + " 4",
         "inventory.3": BAG["epic"] + " 3", "inventory.4": BAG["legendary"] + " 2", "inventory.5": BAG["mythic"] + " 1",
         "inventory.6": BAG["cursed"] + " 1", "inventory.7": BAG["locked"] + " 2", "inventory.8": KEY + " 2",
         "hotbar.0": "minecraft:netherite_sword", "hotbar.1": "minecraft:diamond 14", "hotbar.2": "minecraft:emerald 23",
         "hotbar.3": "minecraft:golden_apple 3", "hotbar.4": "minecraft:ender_pearl 9", "inventory.9": TALISMAN,
         "inventory.10": BAG["summoning"] + " 1", "inventory.11": BAG["hourglass"] + " 1"}),
     # Inventory menu slots: 9-35 are the main inventory (top row first), so 13 is the Legendary and 14 the Mythic.
     events=[ev(-0.4, screen="inventory"), ev(-0.3, hover=13), ev(0.7, shift=True), ev(2.3, hover=14)])

# ---------------------------------------------------------------- a boss drops three bags (Wither, meadow at night)

WITHER = [10.4, 112.4, -186.5]
shot("boss_wither", duration=4.6, preroll=2.0, timing="smooth", playerPos=[13.3, 111, -186.5], playerRot=[90, -20], gamma=1.0,
     **CINE,
     setup=["time set %d" % NIGHT] + MEADOW_CLEAR +
     config(drops={"bosses": {"bags": 3, "min_tier": "epic", "mythic_chance": 0.5},
                   "tier_weights": {"common": 0, "uncommon": 0, "rare": 0, "epic": 1, "legendary": 1, "mythic": 0}}) +
     hero({"weapon.mainhand": "minecraft:netherite_sword"}) + [mob("minecraft:wither", *WITHER, -90, extra="NoGravity:1b")],
     events=[ev(-1.5, face="reel_mob", faceOffset=[0, -0.6, 0]), ev(-1.0, tag="reel_mob", health=0.003),
             ev(0.4, input="attack")],
     speed=[{"t": 0, "v": 1}, {"t": 0.45, "v": 1}, {"t": 0.6, "v": 0.35}, {"t": 1.9, "v": 0.35}, {"t": 2.5, "v": 1}],
     keys=[key(0, [15.6, 111.35, -189.0], look=[10.6, 113.6, -186.4], fov=62),
           key(1.8, [15.2, 111.45, -189.0], look=[10.6, 112.6, -186.4], fov=60),
           key(4.6, [14.4, 111.75, -188.6], look=[10.5, 111.75, -186.4], fov=54)])

# ---------------------------------------------------------------- Cursed bag: an ambush (meadow at night)

CURSE_P = [12.5, 111, -187.5]
shot("cursed", duration=4.6, preroll=1.5, timing="smooth", playerPos=CURSE_P, playerRot=[-30, 10], gamma=1.0, **CINE,
     setup=["time set %d" % NIGHT] + MEADOW_CLEAR +
     config(bags={"cursed": {"outcomes": {"ambush": 1, "jackpot": 0, "hex": 0, "chaos": 0}, "ambush_min": 4, "ambush_max": 4,
                             "ambush_mobs": ["minecraft:zombie", "minecraft:skeleton", "minecraft:husk", "minecraft:stray"]}}) +
     hero({"hotbar.0": BAG["cursed"] + " 1"}),
     events=[ev(-1.0, slot=0), ev(0.3, input="use"), ev(1.3, input="release")],
     keys=arc([CURSE_P[0], 0, CURSE_P[2]], 5.4, 75, 35, 114.4, 4, 0, 4.6, [CURSE_P[0], 111.8, CURSE_P[2]], 64, 60))

# ---------------------------------------------------------------- special bags, the key and the talisman (meadow, night)

line_shot("specials_line", [BAG["cursed"], BAG["hourglass"], BAG["locked"], BAG["summoning"], KEY, TALISMAN], NIGHT, 4.0, gamma=1.0)

# ---------------------------------------------------------------- Hourglass: ripens, then opens into better bags

shot("hourglass", duration=4.4, preroll=3.5, playerPos=[12.5, 111, -183.5], playerRot=[0, 14], **HUD,
     setup=["time set %d" % SUNSET] + config(bags={"hourglass": {"min_minutes": 0.1, "max_minutes": 0.1}}) +
     hero({"hotbar.0": BAG["hourglass"] + " 1", "hotbar.8": "minecraft:netherite_sword"}),
     events=[ev(-4.0, slot=0), ev(1.5, input="use"), ev(2.5, input="release")],
     aim=[key(0, [0, 0, 0], rot=[64, 10]), key(4.4, [0, 0, 0], rot=[56, 8])])

# ---------------------------------------------------------------- Locked bag + Diamond Key (key in the off hand)

shot("locked", duration=3.8, preroll=1.5, playerPos=[12.5, 111, -183.5], playerRot=[0, 14], **HUD,
     setup=["time set %d" % SUNSET] + config() +
     hero({"hotbar.0": BAG["locked"] + " 2", "weapon.offhand": KEY + " 3", "hotbar.8": "minecraft:netherite_sword"}),
     events=[ev(-1.0, slot=0), ev(0.3, input="use"), ev(1.3, input="release")],
     aim=[key(0, [0, 0, 0], rot=[110, 10]), key(3.8, [0, 0, 0], rot=[116, 7])])

# ---------------------------------------------------------------- the machines (a small hopper workshop, meadow)

# Each machine on the ground with a hopper and a chest of input on top: bags for the Bag Opener, junk for the Recycler.
OPX, OPZ, RCX = 9, -190, 12
JUNK = ["minecraft:rotten_flesh 64", "minecraft:bone 64", "minecraft:string 64", "minecraft:arrow 64", "minecraft:spider_eye 32",
        "minecraft:gunpowder 48", "minecraft:rotten_flesh 64", "minecraft:bone 64"]
BAGS_IN = [BAG["common"] + " 16", BAG["uncommon"] + " 8", BAG["rare"] + " 6", BAG["epic"] + " 4", BAG["common"] + " 16",
           BAG["rare"] + " 6", BAG["uncommon"] + " 8", BAG["legendary"] + " 2", BAG["common"] + " 16"]
WORKSHOP = (MEADOW_CLEAR + [f"fill {OPX - 2} 111 {OPZ - 2} {RCX + 2} 116 {OPZ + 2} minecraft:air"]
            + [f"setblock {x} 111 {OPZ} {b}" for x, b in ((OPX, OPENER), (RCX, RECYCLER))]
            + [f"setblock {x} 112 {OPZ} minecraft:hopper[facing=down]" for x in (OPX, RCX)]
            + [f"setblock {x} 113 {OPZ} minecraft:chest[facing=south]" for x in (OPX, RCX)]
            + [f"item replace block {OPX} 113 {OPZ} container.{i} with {s}" for i, s in enumerate(BAGS_IN)]
            + [f"item replace block {RCX} 113 {OPZ} container.{i} with {s}" for i, s in enumerate(JUNK)])

# The machine screens: the player right-clicks each block with an empty hand.
GUI = dict(hud="none", hidePlayer=False, gamemode="survival", camera="player", renderDistance=8, settle=4, guiScale=4)
shot("opener_gui", duration=4.0, preroll=2.0, playerPos=[OPX + 0.5, 111, OPZ + 2.5], playerRot=[180, 25], **GUI,
     setup=["time set %d" % SUNSET] + config() + hero() + WORKSHOP +
     [f"item replace block {OPX} 111 {OPZ} container.{i} with {b}" for i, b in enumerate(BAGS_IN)],
     events=[ev(-1.2, useOn=[OPX, 111, OPZ], useFace="south")])
# Sped up for the clip (one item a tick), so a fresh Common bag drops out of the junk on camera.
shot("recycler_gui", duration=4.0, preroll=4.0, playerPos=[RCX + 0.5, 111, OPZ + 2.5], playerRot=[180, 25], **GUI,
     setup=["time set %d" % SUNSET] + config(machines={"recycler": {"ticks_per_item": 1}}) + hero() + WORKSHOP +
     [f"item replace block {RCX} 111 {OPZ} container.{i} with {j}" for i, j in enumerate(JUNK[:9])],
     events=[ev(-1.2, useOn=[RCX, 111, OPZ], useFace="south")])

# ---------------------------------------------------------------- Nether kill: themed Nether Spoils

# A magma-block island on the shore of the lava sea (spreadplayers near (-32, -24)); the sea lies east and south.
NETHER_P = [-40.5, 35, -30.5]
NETHER_Z = [NETHER_P[0] - 2.2, NETHER_P[1], NETHER_P[2]]
shot("nether_kill", dimension=NETHER, duration=3.6, preroll=2.0, timing="smooth", playerPos=NETHER_P, playerRot=[90, 0], **CINE,
     setup=config(drops=tier("epic")) + hero({"weapon.mainhand": "minecraft:netherite_sword"}) +
     [mob("minecraft:wither_skeleton", *NETHER_Z, -90)],
     events=[ev(-1.5, face="reel_mob", faceOffset=[0, 0.1, 0]), ev(-1.0, tag="reel_mob", health=0.05), ev(0.35, input="attack")],
     keys=[key(0, [NETHER_P[0] + 3.6, NETHER_P[1] + 1.1, NETHER_P[2] + 3.8], look=[NETHER_P[0] - 1.9, NETHER_P[1] + 1.0, NETHER_P[2] - 0.1],
               fov=56),
           key(3.6, [NETHER_P[0] + 2.9, NETHER_P[1] + 0.8, NETHER_P[2] + 3.1], look=[NETHER_P[0] - 2.0, NETHER_P[1] + 0.6, NETHER_P[2]],
               fov=48)])


# ---------------------------------------------------------------- queue

def queue(names: list[str], still: bool = False) -> None:
    JOBS.mkdir(parents=True, exist_ok=True)
    chosen = []
    for pattern in names:
        if pattern.endswith("*"):
            chosen += [n for n in SHOTS if n.startswith(pattern[:-1])]
        elif pattern in SHOTS:
            chosen.append(pattern)
        else:
            raise SystemExit(f"unknown shot {pattern}")
    batch = int(time.time())  # later batches sort after earlier ones
    for i, name in enumerate(chosen):
        job = dict(SHOTS[name])
        if still:
            job["type"] = "still"
            job["name"] = name + "_still"
        (JOBS / f"{batch}_{i:03d}_{job['name']}.json").write_text(json.dumps(job, indent=1))
        print("queued", job["name"])


if __name__ == "__main__":
    if len(sys.argv) < 2 or sys.argv[1] == "list":
        print("\n".join(SHOTS))
    elif sys.argv[1] in ("queue", "still"):
        queue(sys.argv[2:], still=sys.argv[1] == "still")
