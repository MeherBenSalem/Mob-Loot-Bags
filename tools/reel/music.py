#!/usr/bin/env python3
"""The reel's music: an original 120 BPM electro-pop cue, synthesized here so it fits the cut bar for bar and carries
no licence. Am - F - C - G (the C major of the mod's Legendary fanfare), four-on-the-floor kick, claps, hats, a
side-chained supersaw pad, a sub bass, a plucked arpeggio with a dotted-eighth delay and, in the second half, a bell
hook.

  python3 tools/reel/music.py preview      # -> .tts/music_preview.wav, the cue with reel.py's default sections

score(sections, length) renders a cue: sections are (start, end, kind) in reel seconds on the 0.5 s beat grid, with
kind one of "intro", "groove", "lift", "break", "outro".
"""
from __future__ import annotations

import sys
from functools import lru_cache
from pathlib import Path

import numpy as np
from scipy.signal import butter, sosfilt

SR = 48000
BPM = 120
BEAT = 60 / BPM
BAR = 4 * BEAT
RNG = np.random.default_rng(7)

PROGRESSION = [("A", ["A3", "C4", "E4"], "A1"), ("F", ["F3", "A3", "C4"], "F1"),
               ("C", ["G3", "C4", "E4"], "C2"), ("G", ["G3", "B3", "D4"], "G1")]
NAMES = {"C": 0, "C#": 1, "D": 2, "D#": 3, "E": 4, "F": 5, "F#": 6, "G": 7, "G#": 8, "A": 9, "A#": 10, "B": 11}


def hz(note: str) -> float:
    name, octave = note[:-1], int(note[-1])
    return 440.0 * 2 ** ((NAMES[name] + 12 * (octave + 1) - 69) / 12)


def shift(note: str, semis: int) -> str:
    name, octave = note[:-1], int(note[-1])
    n = NAMES[name] + 12 * octave + semis
    inv = {v: k for k, v in NAMES.items()}
    return f"{inv[n % 12]}{n // 12}"


def _t(seconds: float) -> np.ndarray:
    return np.arange(int(seconds * SR)) / SR


def _filt(x, kind, freq, order=2):
    sos = butter(order, freq, btype=kind, fs=SR, output="sos")
    return sosfilt(sos, x, axis=0)


# ---------------------------------------------------------------- instruments (mono unless noted)

@lru_cache(None)
def kick() -> np.ndarray:
    t = _t(0.5)
    f = 52 + 120 * np.exp(-t / 0.03) + 35 * np.exp(-t / 0.12)
    body = np.sin(2 * np.pi * np.cumsum(f) / SR) * np.exp(-t / 0.26)
    click = _filt(RNG.standard_normal(len(t)), "highpass", 2500) * np.exp(-t / 0.004) * 0.35
    return np.tanh(1.8 * (body + click)) * 0.9


@lru_cache(None)
def clap() -> np.ndarray:
    t = _t(0.45)
    n = RNG.standard_normal(len(t))
    env = sum(np.exp(-np.clip(t - d, 0, None) / 0.006) * (t >= d) for d in (0.0, 0.011, 0.023)) + 0.55 * np.exp(-t / 0.11) * (t >= 0.03)
    body = _filt(_filt(n, "bandpass", [950, 5200]), "highpass", 600) * env
    tone = np.sin(2 * np.pi * 190 * t) * np.exp(-t / 0.05) * 0.3
    return np.tanh(1.4 * (body * 0.8 + tone)) * 0.75


@lru_cache(None)
def hat(open_: bool = False) -> np.ndarray:
    t = _t(0.35 if open_ else 0.08)
    n = _filt(RNG.standard_normal(len(t)), "highpass", 7500, order=4)
    metal = sum(np.sin(2 * np.pi * f * t) for f in (5300, 7100, 8700, 10400)) * 0.08
    return (n + metal) * np.exp(-t / (0.12 if open_ else 0.022)) * 0.35


@lru_cache(None)
def crash() -> np.ndarray:
    t = _t(2.6)
    n = _filt(RNG.standard_normal((len(t), 2)), "highpass", 3800, order=3)
    return n * np.exp(-t / 0.75)[:, None] * 0.32


def _saw(f: float, t: np.ndarray, harmonics: int, tilt: float = 1.0, phase: float = 0.0) -> np.ndarray:
    out = np.zeros_like(t)
    for k in range(1, harmonics + 1):
        if f * k > 15000:
            break
        out += np.sin(2 * np.pi * f * k * t + phase * k) / k ** tilt
    return out


@lru_cache(None)
def pad(notes: tuple[str, ...], seconds: float) -> np.ndarray:
    """Stereo supersaw chord: five detuned voices per note, soft attack."""
    t = _t(seconds)
    out = np.zeros((len(t), 2))
    for i, note in enumerate(notes):
        for v, cents in enumerate((-14, -6, 0, 7, 15)):
            f = hz(note) * 2 ** (cents / 1200)
            w = _saw(f, t, 18, tilt=1.15, phase=RNG.uniform(0, 6.28))
            pan = 0.5 + 0.38 * ((v - 2) / 2) * (1 if i % 2 == 0 else -1)
            out[:, 0] += w * np.cos(pan * np.pi / 2)
            out[:, 1] += w * np.sin(pan * np.pi / 2)
    env = np.minimum(1, t / 0.06) * np.minimum(1, (seconds - t) / 0.12)
    out = _filt(out, "lowpass", 4200) * env[:, None]
    return out / (len(notes) * 5) * 0.9


@lru_cache(None)
def bass(note: str, seconds: float) -> np.ndarray:
    t = _t(seconds)
    f = hz(note)
    sub = np.sin(2 * np.pi * f * t) * 0.7
    top = _saw(f * 2, t, 14, tilt=1.2) * 0.8
    env = np.minimum(1, t / 0.004) * np.exp(-t / 0.9) * np.minimum(1, (seconds - t) / 0.02)
    return np.tanh(1.3 * (sub + top)) * env * 0.55


@lru_cache(None)
def pluck(note: str, seconds: float = 0.5) -> np.ndarray:
    """Plucked synth: each harmonic dies faster than the one below, so the attack is bright and the tail round."""
    t = _t(seconds)
    f = hz(note)
    out = np.zeros_like(t)
    for k in range(1, 16):
        if f * k > 14000:
            break
        out += np.sin(2 * np.pi * f * k * t) / k * np.exp(-t * (5 + 2.6 * k))
    return out * np.minimum(1, t / 0.002) * 0.42


@lru_cache(None)
def bell(note: str, seconds: float = 1.6) -> np.ndarray:
    t = _t(seconds)
    f = hz(note)
    out = np.zeros_like(t)
    for ratio, amp, tau in ((1, 1.0, 0.9), (2.0, 0.45, 0.5), (3.0, 0.22, 0.3), (4.16, 0.18, 0.18), (5.43, 0.1, 0.12)):
        out += amp * np.sin(2 * np.pi * f * ratio * t) * np.exp(-t / tau)
    return out * np.minimum(1, t / 0.001) * 0.3


def riser(seconds: float) -> np.ndarray:
    """Noise swept upward: crossfades between fixed bands (no filter restarts, so no clicks), plus a rising tone."""
    t = _t(seconds)
    u = t / seconds
    n = RNG.standard_normal(len(t))
    centres = np.geomspace(400, 9000, 7)
    pos = u * (len(centres) - 1)
    out = np.zeros_like(n)
    for i, c in enumerate(centres):
        w = np.clip(1 - np.abs(pos - i), 0, 1)
        out += _filt(n, "bandpass", [c * 0.7, min(c * 1.45, 20000)]) * w
    tone = np.sin(2 * np.pi * np.cumsum(220 * 2 ** (2 * u)) / SR) * 0.25
    return (out * 0.6 + tone) * u ** 2 * 0.5


def boom(seconds: float = 2.2) -> np.ndarray:
    t = _t(seconds)
    f = 32 + 70 * np.exp(-t / 0.08)
    s = np.sin(2 * np.pi * np.cumsum(f) / SR) * np.exp(-t / 0.7)
    n = _filt(RNG.standard_normal(len(t)), "lowpass", 900) * np.exp(-t / 0.18) * 0.4
    return np.tanh(1.6 * (s + n)) * 0.8


# ---------------------------------------------------------------- the score

class Track:
    def __init__(self, length: float):
        self.n = int(length * SR)
        self.buses = {name: np.zeros((self.n, 2)) for name in ("drums", "kick", "bass", "pad", "pluck", "bell", "fx")}

    def add(self, bus: str, clip: np.ndarray, at: float, gain: float = 1.0, pan: float = 0.0) -> None:
        i = int(round(at * SR))
        if i >= self.n or i + len(clip) <= 0:
            return
        if clip.ndim == 1:
            a = (pan + 1) * np.pi / 4
            clip = np.stack([clip * np.cos(a), clip * np.sin(a)], axis=1) * np.sqrt(2)
        j0 = max(0, -i)
        j1 = min(len(clip), self.n - i)
        self.buses[bus][i + j0:i + j1] += clip[j0:j1] * gain


def _chord_at(bar: int):
    return PROGRESSION[bar % len(PROGRESSION)]


ARP = [0, 1, 2, 3, 2, 1, 0, 1, 2, 3, 4, 3, 2, 1, 2, 3]  # 16ths over root, third, fifth, octave, tenth
HOOK = [  # (eighth in bar, interval above the chord root in scale steps as notes) per chord
    {"A": [(0, "E5"), (2, "A5"), (3, "G5"), (4, "E5"), (6, "C5"), (7, "D5")]},
    {"F": [(0, "C5"), (2, "F5"), (3, "E5"), (4, "C5"), (6, "A4"), (7, "C5")]},
    {"C": [(0, "E5"), (2, "G5"), (3, "A5"), (4, "G5"), (6, "E5"), (7, "D5")]},
    {"G": [(0, "D5"), (2, "G5"), (3, "B5"), (4, "D6"), (5, "B5"), (6, "G5")]},
]


def score(sections: list[tuple[float, float, str]], length: float, first_bar: float = -0.5,
          accents: tuple[float, ...] = ()) -> np.ndarray:
    """Renders the cue. Bar k starts at first_bar + k * BAR; `accents` get a crash and a boom."""
    tr = Track(length)

    def kind_at(t):
        for a, b, k in sections:
            if a <= t < b:
                return k
        return None

    bar = 0
    while first_bar + bar * BAR < length:
        b0 = first_bar + bar * BAR
        name, chord, root = _chord_at(bar)
        for step in range(16):
            t = b0 + step * BEAT / 4
            if t < 0 or t >= length:
                continue
            kind = kind_at(t)
            if kind is None:
                continue
            beat_on = step % 4 == 0
            full = kind in ("groove", "lift")
            # Drums
            if beat_on and (full or kind == "outro" and t < sections[-1][0] + BAR):
                tr.add("kick", kick(), t)
            if kind == "intro" and beat_on and step in (0, 8):
                tr.add("kick", kick(), t, 0.55)
            if full and step in (4, 12):
                tr.add("drums", clap(), t, 0.8)
            if full:
                if step % 2 == 0:
                    tr.add("drums", hat(), t, 0.55 if step % 4 else 0.3, pan=0.25)
                elif kind == "lift":
                    tr.add("drums", hat(), t, 0.3, pan=-0.2)
                if step % 4 == 2 and kind == "lift":
                    tr.add("drums", hat(True), t, 0.35, pan=0.3)
            # Bass: eighths on the root, an octave jump on the off-beat before the next bar
            if full and step % 2 == 0:
                note = shift(root, 12) if step in (6, 14) else root
                tr.add("bass", bass(note, BEAT / 2 * 0.92), t)
            # Pad: the whole bar
            if step == 0 and kind in ("intro", "groove", "lift", "break", "outro"):
                tr.add("pad", pad(tuple(chord), BAR + 0.15), t, 0.75 if kind in ("intro", "break") else 0.6)
            # Pluck arpeggio
            if kind in ("groove", "lift", "break"):
                tones = [chord[0], chord[1], chord[2], shift(chord[0], 12), shift(chord[1], 12)]
                note = shift(tones[ARP[step]], 12)
                tr.add("pluck", pluck(note), t, 0.5 if kind != "break" else 0.4, pan=-0.3 if step % 2 else 0.3)
        # Bell hook over the lift sections, on eighths
        for eighth, note in HOOK[bar % 4][name]:
            t = b0 + eighth * BEAT / 2
            if 0 <= t < length and kind_at(t) in ("lift", "outro") and (kind_at(t) == "lift" or eighth == 0):
                tr.add("bell", bell(note), t, 0.55, pan=0.15)
        bar += 1

    # Builds: a riser and a snare roll into every groove or lift that follows something quieter, and accents.
    for i, (a, b, kind) in enumerate(sections):
        prev = sections[i - 1][2] if i else None
        if kind in ("groove", "lift") and prev in ("intro", "break", None):
            rise = min(2.0, a)
            if rise > 0.5:
                tr.add("fx", riser(rise), a - rise, 0.9)
                for k in range(int(rise / (BEAT / 4))):
                    t = a - rise + k * BEAT / 4
                    if t >= a - rise / 2 or k % 2 == 0:
                        tr.add("drums", clap(), t, 0.15 + 0.5 * (k * BEAT / 4) / rise)
            tr.add("fx", crash(), a, 0.9)
            tr.add("fx", boom(), a, 0.7)
    for a in accents:
        tr.add("fx", crash(), a, 0.8)
        tr.add("fx", boom(), a, 0.8)

    # Side-chain: everything but the drums ducks under the kick.
    duck = np.ones(tr.n)
    k = tr.buses["kick"].mean(axis=1)
    env = np.abs(k)
    hits = np.flatnonzero((env[1:] > 0.3) & (env[:-1] <= 0.3))
    shape = 1 - 0.65 * np.exp(-_t(0.3) / 0.09)
    for h in hits:
        m = min(len(shape), tr.n - h)
        duck[h:h + m] = np.minimum(duck[h:h + m], shape[:m])
    for name in ("bass", "pad", "pluck"):
        tr.buses[name] *= duck[:, None]

    # Delay on the pluck (dotted eighth, ping-pong), a little room on pad and bells.
    d = int(0.375 * SR)
    pl = tr.buses["pluck"]
    wet = np.zeros_like(pl)
    for i, g in enumerate((0.38, 0.22, 0.12), start=1):
        wet[i * d:, (i % 2)] += pl[:-i * d, (i + 1) % 2] * g
    tr.buses["pluck"] = pl + _filt(wet, "lowpass", 5000)
    for name in ("pad", "bell"):
        x = tr.buses[name]
        tail = np.zeros_like(x)
        for delay, g in ((0.031, 0.25), (0.053, 0.2), (0.089, 0.14), (0.137, 0.09)):
            j = int(delay * SR)
            tail[j:] += x[:-j][:, ::-1] * g
        tr.buses[name] = x + tail

    # In the intro and breaks the music sits under a low-pass, opening toward the next section.
    lp = np.zeros((tr.n, 2))
    music = sum(tr.buses[b] * g for b, g in (("bass", 0.6), ("pad", 1.9), ("pluck", 2.1), ("bell", 1.6)))
    closed = _filt(music, "lowpass", 900)
    mix_amt = np.zeros(tr.n)
    for a, b, kind in sections:
        i0, i1 = int(a * SR), min(tr.n, int(b * SR))
        if kind in ("intro", "break"):
            u = np.linspace(0, 1, i1 - i0)
            mix_amt[i0:i1] = 1 - u ** 3
    lp = closed * mix_amt[:, None] + music * (1 - mix_amt[:, None])
    out = lp + tr.buses["drums"] * 1.5 + tr.buses["kick"] * 0.6 + tr.buses["fx"] * 0.8
    out = _filt(out, "highpass", 28)
    # Glue: gentle bus compression by a smoothed envelope, then a soft clip.
    env = np.sqrt(_filt(out.mean(axis=1) ** 2, "lowpass", 8)) + 1e-6
    gain = np.minimum(1, (0.22 / env) ** 0.35)
    out *= gain[:, None]
    out = np.tanh(out * 1.6) / np.tanh(1.6)
    return (out / max(1e-6, np.abs(out).max()) * 0.85).astype(np.float32)


if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "preview":
        import soundfile
        secs = [(0.0, 3.5, "intro"), (3.5, 19.5, "groove"), (19.5, 21.5, "break"), (21.5, 35.5, "lift"), (35.5, 43.0, "outro")]
        audio = score(secs, 43.0, accents=(0.0,))
        dest = Path(__file__).resolve().parent / ".tts" / "music_preview.wav"
        soundfile.write(dest, audio, SR)
        print(dest)
    else:
        print(__doc__)
