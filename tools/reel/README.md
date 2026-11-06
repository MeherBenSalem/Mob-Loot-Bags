# Reel tooling

Everything that produced the promotional reel (`marketing/video/mob_loot_bags_reel.mp4`): a development-only capture
client, the shot list, the voice-over, the music and the editor. Nothing here ships in a mod jar.

## How the footage is made

Every frame is real 1.21.1 Fabric gameplay, rendered by the game itself. `src/v1211` is a dev-only Fabric mod (the
`reelDirector` source set in `1.21.1/fabric/build.gradle`, enabled with `-PreelDirector`) that turns the dev client into
an offline renderer:

- **Virtual time.** The client timer advances by exactly one frame's worth of game ticks per rendered frame, so a slow
  software renderer still produces even 30 fps motion, including in-engine slow motion (`speed` keys). Toasts and the
  enchantment glint, which the game animates by the wall clock, run on the video's clock too.
- **Lockstep.** The integrated server runs a tick only when the client asks for it, so mobs, drops and particles stay in
  step with the frames.
- **Complete frames.** Visible chunk sections are meshed synchronously and the visibility graph is rebuilt in the same
  frame, so nothing pops in.
- **Scripted camera.** Keyframed position, look target (a point or a tagged entity), roll and field of view on
  Hermite splines; first-person shots are aimed by keys too.
- **A phone-shaped HUD.** The hotbar is lifted, titles and toasts get their own bands, and HUD layers can be hidden, so
  nothing the game draws sits under the captions and buttons TikTok and Reels put over a video. GUI scale 4 is allowed
  in a 1080-wide window, and first-person hands are turned in toward the middle so a 9:16 frame keeps them.
- **Capture.** The framebuffer streams to ffmpeg (H.264 4:4:4, CRF 10). Every sound the game starts is logged with its
  frame, position and pitch (`*.sounds.jsonl`), so the mix places the game's own audio exactly.
- **Staging.** Server commands, `!write` (a config file the next `/lootbags reload` picks up), health thresholds,
  the player's inputs (attack, hold Use), real block use (machine screens), screens with a slot hovered and Shift held
  (loot previews).

## Running it

```
tools/reel/client.sh start            # Xvfb + software GL, 1080x1920 window, world "ReelWorld" (seed 20261019)
python3 tools/reel/shots.py list      # every shot
python3 tools/reel/shots.py still hook_kill    # one framing PNG first
python3 tools/reel/shots.py queue hook_kill tiers_line   # queue jobs; the client renders them in order
tools/reel/sheet.sh hook_kill 0.5     # contact sheet of a finished shot
python3 tools/reel/reel.py render     # cut, grade, captions, voice-over, music, mix -> mob_loot_bags_reel.mp4
tools/reel/client.sh stop
```

Shots render into `1.21.1/fabric/runs/reel/reel/out/` (git-ignored). `previs.py` checks where subjects land in the 9:16
frame before a render.

## Voice-over, music and the edit

- `voice.py` synthesizes the narration offline with Kokoro-82M (Apache-2.0) through `kokoro-onnx`
  (`pip install kokoro-onnx soundfile`; `python3 tools/reel/voice.py fetch` downloads the model into the git-ignored
  `tools/reel/.tts/`). Each line gets a trailer chain (EQ, compression, a short room) and is cached by text.
- `music.py` writes an original 120 BPM cue (kick, claps, hats, side-chained supersaw pad, sub bass, plucked arpeggio,
  bell hook) to the edit's sections, so every cut is on a beat and the drop lands on the first bag of the tier line.
- `reel.py` holds the cut, the captions (Lilita One and Nunito from `marketing/source/fonts`, tier colours from the
  mod) and the voice-over script. `edit.py` decodes each shot through the grade, adds zoom, punch-in, flash and whip
  accents, draws the text and the pixel-art bag stickers, and encodes. `audio.py` mixes the game's logged sounds, the
  music (ducked under the voice, with its presence band cut) and the voice-over.
