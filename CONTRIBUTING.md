# Contributing

Thanks for helping improve Mob Loot Bags.

## How to contribute

1. Fork the repository and create a feature branch from `main`.
2. Make focused changes in the relevant Minecraft version workspace (`1.20.1/`, `1.21.1/`, `26.1.2/`, `26.2/`, or `26.3/`).
3. Build the workspace you changed (`./gradlew build` from that folder, with Java 17 for 1.20.1, 21 for 1.21.1 and
   25 for 26.x). This also runs the unit tests.
4. Open a pull request describing the change and how you tested it.

## Assets and data

Textures, screens, sounds, models, blockstates, language files, loot tables, recipes, tags and advancements are
generated, not edited by hand. Change the generator in `tools/assets/`, then regenerate:

```bash
pip install pillow numpy scipy        # ffmpeg is also needed for the sounds
python3 tools/assets/build.py all     # or one version, e.g. 26.3
python3 tools/assets/template.py      # templates/loot_tables_template.zip
```

Per-version differences (data formats and folder names) live in `PROFILES` in `tools/assets/data.py` and the
emitters in `tools/assets/loot.py`. Commit the regenerated files with the generator change.

## Testing in game

`tools/qa/` drives dev servers over RCON and a real client under Xvfb (software rendering):

```bash
tools/qa/devrun.sh start-server 26.3 fabric
python3 tools/qa/server_smoke.py --run-dir 26.3/fabric/runs/server      # add --mc 1.20.1 on 1.20.1
tools/qa/devrun.sh start-client 26.3 fabric                              # needs Xvfb on :1
python3 tools/qa/client_checks.py --out /tmp/mlb-qa/shots
tools/qa/devrun.sh stop-client; tools/qa/devrun.sh stop-server 26.3
```

The dev server's `server.properties` needs `enable-rcon=true`, `rcon.password=qa`, `rcon.port=25575`,
`online-mode=false` and `white-list=false`, and the QA client must be an operator (`/op LootGoblin`). If you change
gameplay, run both scripts on every workspace you touched, for Fabric and NeoForge/Forge.

## Coding expectations

- Prefer small, reviewable PRs over large mixed changes.
- Match existing naming, package layout, and MultiLoader patterns.
- Do not commit build output, IDE metadata, or local secrets.
- When fixing a bug, say which version/loaders you verified.

## Issues

Use GitHub Issues for bugs and feature requests. Include Minecraft version, mod loader, mod version, and steps to reproduce when reporting bugs.

## License

By contributing, you agree that your contributions are licensed under the Apache License, Version 2.0, unless you state otherwise.
