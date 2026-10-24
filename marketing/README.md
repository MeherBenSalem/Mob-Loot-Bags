# Marketing

Listing copy and artwork for CurseForge and Modrinth.

| Path | What it is |
|------|------------|
| `curseforge/description.md` | The project description (Markdown; also works on Modrinth). Images load from this repo's `main` branch. |
| `curseforge/summary.txt` | The one-line summary (under 250 characters). |
| `images/banner.png` | 1800×600 header banner. |
| `images/icon.png` | 512×512 project icon. |
| `images/card-*.png` | 1600×900 feature cards used in the description and as gallery images. |
| `images/header-*.png`, `images/divider.png` | Transparent section headers and a divider for the description. |
| `images/screenshots/*.jpg` | Real in-game captures (Minecraft 26.3, Fabric) used by the cards. |
| `source/render.cjs` | Renders every PNG above from the screenshots, the mod's own textures and the bundled fonts. |
| `source/backdrop.jpg` | Blurred background for the cards (a downscaled `battle.jpg`). |
| `source/fonts/` | Lilita One and Nunito, both under the SIL Open Font License (licence files included). |

## Regenerating the images

Needs Node.js and Playwright with Chromium:

```bash
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs            # everything
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs card-tiers # one asset
```

The release name, version and the list of supported Minecraft versions and loaders are at the top of
`render.cjs`. Supporting a new Minecraft version means adding one line there, plus one row to the table in
`description.md`. Bag icons are read from `26.3/common/src/main/resources/assets/mob_loot_bags/textures/item/`, so
regenerated textures (see `tools/assets/`) show up in the next render.

## Retaking the screenshots

The captures come from a dev client driven by `tools/qa/client.py` (see `tools/qa/` and `CONTRIBUTING.md`): a
1920×1080 window under Xvfb at GUI scale 3, the dev server's time frozen with `gamerule advance_time false`, and the
HUD hidden with F1 where it isn't part of the shot. Dropped items spin, so `battle.jpg` is assembled from a burst of
frames, each bag taken from the frame where it faces the camera. Every other screenshot is a single unedited frame.

| Screenshot | Scene |
|------------|-------|
| `battle.jpg` | Six tiers of dropped bags with loot beams in a meadow at sunset, with mobs behind them |
| `open.jpg` | Opening a Mythic bag in a cherry grove: the title, the reveal toast and the hotbar |
| `tooltip.jpg` | Shift loot preview of a Legendary bag in the inventory |
| `cursed.jpg` | A Cursed bag ambush in third person |
| `opener-gui.jpg`, `recycler-gui.jpg` | The two machine screens while they work |
| `workshop.jpg` | Both machines in a small hopper setup |
| `boss.jpg` | A Wither dropping a Legendary and a Mythic bag, with the chat announcement |
| `commands.jpg` | `/lootbags odds` and `/lootbags simulate` in chat |
| `advancements.jpg` | The Mob Loot Bags advancement tab |
