![Mob Loot Bags](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/banner.png)

**Every kill could be a jackpot.** Mobs drop loot bags in six tiers, from Common to Mythic. Hold to open them and
watch the loot roll in, check the odds before you open, automate the grind with the Bag Opener, recycle mob-farm junk
into new bags, and gamble on Cursed, Hourglass and Locked bags. Every rate, table and machine value is yours to tune.

Fabric and NeoForge (Forge on 1.20.1) · Minecraft 1.20.1, 1.21.1, 26.1.2, 26.2 and 26.3 · install on server and client

![What's new in 2.0](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-whats-new.png)

Version 2.0 rebuilds the whole mod. Here's what changed from 1.x:

- A new **Mythic** tier, plus **Bag Luck**, **bad-luck protection**, **boss drops** and mobs that **carry bags**
- **Hold to open** with sound and particles, a **reveal toast**, and **Shift loot previews**
- **Loot beams** on dropped bags, so you can spot a Legendary across a battlefield
- The **Hourglass** bag (replaces Timed) and the **Fortune Talisman**
- A rebuilt **Bag Opener** and **Loot Recycler** with new screens and real automation
- New textures, screens and sounds everywhere, 13 advancements and four commented config files
- Your 1.x world carries over: same item IDs, and your custom settings are migrated

![Six tiers](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-tiers.png)

![Six tiers from Common to Mythic](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-tiers.png)

Each tier rolls its own loot table, from bread and torches in a Common bag up to nether stars, beacons and maxed
netherite gear in a Mythic one. Combine four bags into one of the next tier (Mythic also needs a nether star), or
split one back into four.

![Smarter drops](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-drops.png)

![Hunt for the loot](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-drops.png)

- Hostile mobs drop a bag 18% of the time. Tougher mobs drop more often, from ×0.5 up to ×2.5.
- **Bag Luck** makes the higher tiers likelier. It comes from Looting, the Luck attribute, the mob's health, the
  Nether, the End and Hard difficulty.
- Some zombies, skeletons, piglins and illagers spawn **holding a bag**. Kill them to take it.
- Kills in the Nether and the End roll **themed loot** on top of the normal table.
- Spawner mobs drop a quarter as often, so mob farms stay fair. Pet kills and arrows count for you.

![Opening bags](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-opening.png)

![Hold, rustle, reveal](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-opening.png)

Hold Use to tug a bag open, or sneak to open the whole stack at once. The loot goes straight into your inventory and
a toast shows what you got. Legendary and Mythic bags flash a title on screen and play a fanfare.

![Loot preview](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-preview.png)

![Know what's inside](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-preview.png)

Hold Shift over a bag to see what it can give and how likely each item is. The chances are sampled from the
server's real loot tables, so datapacks and modpack changes show up too.

![Special bags](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-specials.png)

![High risk, high reward](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-specials.png)

- **Cursed**: a gamble. It might summon glowing cursed mobs that each drop a bag, pay out a Legendary jackpot, hex
  you, or set off something chaotic.
- **Hourglass**: ripens in your inventory over 5 to 10 minutes, then opens into two Epic-or-better bags.
- **Locked**: needs a Diamond Key, and holds netherite-grade loot.
- **Summoning**: a bag full of spawn eggs.
- **Fortune Talisman**: carry it and bags drop more often, and better ones.

![Machines](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-machines.png)

![Bag Opener](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-opener.png)

The **Bag Opener** opens bags by itself: 9 input slots, 18 output slots and four redstone modes (always on, on with a
signal, on without one, or one bag per pulse). Hoppers and pipes feed it from the top and sides and empty it from
the bottom. It waits for Hourglass bags to ripen and gives a comparator signal.

![Loot Recycler](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-recycler.png)

The **Loot Recycler** turns mob-farm junk into salvage points, and every 64 points becomes a fresh loot bag. Feed it
bags you don't want and it stores their experience; take it with the button or a sneak-click.

![Luck and bosses](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-luck.png)

![Fortune favours the bold](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-luck.png)

- **Bad-luck protection**: a bag after 12 kills without one, a Rare after 20 bags, a Legendary after 200.
- **Bosses** always drop three Epic or better bags, with a 15% Mythic chance each. This covers the Wither, the Ender
  Dragon, the Warden, the Elder Guardian and any modded boss in `#c:bosses`.
- Legendary and Mythic drops are announced to the server.
- Check any mob's odds with `/lootbags odds`.

![For modpacks](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-modpacks.png)

![Tune everything](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-config.png)

- Four commented files in `config/mob_loot_bags/` (`drops.json`, `bags.json`, `machines.json` and `client.json`).
  `/lootbags reload` applies changes without a restart.
- Every bag's loot table can be replaced by a datapack, and any dimension can get its own themed tables.
- Add items to `#mob_loot_bags:bonus/<tier>` to mix modded loot into a tier without writing JSON.
- Use per-mob or per-tag drop multipliers, a whitelist or blacklist, and switches for passive mobs, pets and fake
  players.
- `/lootbags simulate` opens a bag thousands of times and lists the odds, and `/lootbags give` hands out any bag.
- [Starter datapack for every version](https://github.com/MeherBenSalem/Mob-Loot-Bags/raw/main/templates/loot_tables_template.zip)

![Progression](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-progression.png)

![Thirteen advancements](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/card-advancements.png)

The mod has its own advancement tab, from *Loot Goblin* to *Myth Made Real*. It also tracks statistics for bags
found and opened, and unlocks recipes when you find your first bag. `/lootbags stats` shows your history and how
close your bad-luck protection is.

![Downloads](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-compatibility.png)

| Minecraft | Fabric | NeoForge / Forge | Java |
|-----------|--------|------------------|------|
| 26.3 | ✔ (Fabric API) | ✔ NeoForge | 25 |
| 26.2 | ✔ (Fabric API) | ✔ NeoForge | 25 |
| 26.1.2 | ✔ (Fabric API) | ✔ NeoForge | 25 |
| 1.21.1 | ✔ (Fabric API) | ✔ NeoForge | 21 |
| 1.20.1 | ✔ (Fabric API) | ✔ Forge | 17 |

Pick the file whose name has your Minecraft version and loader, for example `mob_loot_bags-fabric-1.21.1-2.0.0.jar`.

![FAQ](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-faq.png)

**Does it work with my 1.x world?** Yes. Item and block IDs are unchanged, Timed bags become Hourglass bags, old
machines keep their contents, and customised `config/mlb/` settings are copied into the new files once.

**Is it needed on the server and the client?** Both. It adds items, blocks and screens.

**Can I stop mob farms from flooding the server with bags?** Yes. Spawner mobs already drop a quarter as often, and
you can lower the chance per mob, filter mobs, or require a player kill (on by default).

**Can I add modded items?** Yes: add them to `#mob_loot_bags:bonus/<tier>`, replace a table with a datapack, or point
a bag at your own tables in `bags.json`.

**Can I turn off the beams, toasts or previews?** Yes, each player can in `client.json`. Servers can change the
hold time, toasts, titles and announcements in `bags.json` and `drops.json`.

![Gallery](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/header-gallery.png)

![Loot beams at sunset](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/screenshots/battle.jpg)

![A Mythic reveal](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/screenshots/open.jpg)

![A boss drop](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/screenshots/boss.jpg)

![The loot workshop](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/screenshots/workshop.jpg)

![Divider](https://raw.githubusercontent.com/MeherBenSalem/Mob-Loot-Bags/main/marketing/images/divider.png)

[Discord](https://discord.gg/SAmd9fhNhr) · [Source and issues](https://github.com/MeherBenSalem/Mob-Loot-Bags) · [Ko-fi](https://ko-fi.com/nightbeamstudio) · Made by NightBeam Studio
