#!/usr/bin/env python3
"""Voice-over for the reel, synthesized offline with Kokoro-82M (Apache-2.0) through kokoro-onnx.

  pip install kokoro-onnx soundfile
  python3 tools/reel/voice.py fetch                 # model + voices from Hugging Face into tools/reel/.tts/ (git-ignored)
  python3 tools/reel/voice.py say "Some line" [voice] [speed]   # one test line -> .tts/test.wav

reel.py calls render(lines) to get each line as a processed 48 kHz stereo clip. Lines are cached by text, voice and
speed under .tts/cache/, so re-cutting the edit does not re-synthesize anything.
"""
from __future__ import annotations

import hashlib
import subprocess
import sys
import urllib.request
from pathlib import Path

import numpy as np

HERE = Path(__file__).resolve().parent
TTS = HERE / ".tts"
REPO = "https://huggingface.co/onnx-community/Kokoro-82M-v1.0-ONNX/resolve/main/"
VOICES = ["am_fenrir", "am_michael", "am_puck"]
VOICE, SPEED = "am_fenrir", 1.08
SR = 48000

# A trailer read: rumble and mud out, presence and air up, levelled by a compressor, then a short room.
CHAIN = ("highpass=f=85,equalizer=f=220:t=q:w=1.0:g=-2.5,equalizer=f=3300:t=q:w=1.1:g=3.5,equalizer=f=8000:t=q:w=1.0:g=2,"
         "acompressor=threshold=-20dB:ratio=3.5:attack=4:release=90:makeup=5dB,aecho=0.85:0.5:23|41:0.10|0.06,"
         "alimiter=limit=0.89")


def fetch() -> None:
    (TTS / "voices").mkdir(parents=True, exist_ok=True)
    for name in ["onnx/model.onnx"] + [f"voices/{v}.bin" for v in VOICES]:
        dest = TTS / ("model.onnx" if name.startswith("onnx/") else name)
        if dest.exists():
            continue
        print("fetching", name)
        urllib.request.urlretrieve(REPO + name, dest)
    # kokoro-onnx reads every voice from one archive: name -> (510, 1, 256) style vectors.
    np.savez(TTS / "voices.npz", **{p.stem: np.fromfile(p, dtype=np.float32).reshape(-1, 1, 256)
                                    for p in sorted((TTS / "voices").glob("*.bin"))})


_kokoro = None


def _engine():
    global _kokoro
    if _kokoro is None:
        from kokoro_onnx import Kokoro
        if not (TTS / "voices.npz").exists():
            fetch()
        _kokoro = Kokoro(str(TTS / "model.onnx"), str(TTS / "voices.npz"))
    return _kokoro


def _process(audio: np.ndarray, sr: int) -> np.ndarray:
    raw = audio.astype(np.float32).tobytes()
    out = subprocess.run(["ffmpeg", "-v", "error", "-f", "f32le", "-ar", str(sr), "-ac", "1", "-i", "-", "-af", CHAIN,
                          "-ar", str(SR), "-ac", "2", "-f", "f32le", "-"], input=raw, capture_output=True, check=True).stdout
    return np.frombuffer(out, np.float32).reshape(-1, 2).copy()


def line(text: str, voice: str = VOICE, speed: float = SPEED) -> np.ndarray:
    """One line as processed 48 kHz stereo float32, cached."""
    digest = hashlib.sha1(f"{voice}|{speed}|{text}".encode()).hexdigest()[:16]
    cache = TTS / "cache" / f"{digest}.npy"
    if cache.exists():
        return np.load(cache)
    lang = "en-gb" if voice.startswith("b") else "en-us"
    audio, sr = _engine().create(text, voice=voice, speed=speed, lang=lang)
    clip = _process(audio, sr)
    cache.parent.mkdir(parents=True, exist_ok=True)
    np.save(cache, clip)
    return clip


def render(lines: list[tuple[float, str]], voice: str = VOICE, speed: float = SPEED) -> list[tuple[float, np.ndarray]]:
    return [(t, line(text, voice, speed)) for t, text in lines]


if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "fetch":
        fetch()
    elif len(sys.argv) > 2 and sys.argv[1] == "say":
        clip = line(sys.argv[2], sys.argv[3] if len(sys.argv) > 3 else VOICE, float(sys.argv[4]) if len(sys.argv) > 4 else SPEED)
        import soundfile
        soundfile.write(TTS / "test.wav", clip, SR)
        print(f"{len(clip) / SR:.2f} s -> {TTS / 'test.wav'}")
    else:
        print(__doc__)
