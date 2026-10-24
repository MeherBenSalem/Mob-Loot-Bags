"""Original sound effects for Mob Loot Bags, synthesised from noise and additive partials, then encoded to mono OGG.

Requires numpy, scipy and ffmpeg with libvorbis. Deterministic: every random source is seeded.
"""
from __future__ import annotations

import subprocess
import tempfile
import wave
from pathlib import Path

import numpy as np
from scipy import signal

RATE = 44100


def t_axis(seconds):
    return np.arange(int(RATE * seconds)) / RATE


def env_adsr(n, attack, decay, sustain, release, sustain_level=0.6):
    a, d, r = int(attack * RATE), int(decay * RATE), int(release * RATE)
    s = max(0, n - a - d - r)
    return np.concatenate([np.linspace(0, 1, a, endpoint=False), np.linspace(1, sustain_level, d, endpoint=False),
                           np.full(s, sustain_level), np.linspace(sustain_level, 0, r)])[:n]


def exp_decay(n, tau):
    return np.exp(-np.arange(n) / (tau * RATE))


def bandpass(x, lo, hi, order=3):
    sos = signal.butter(order, [lo, hi], btype='band', fs=RATE, output='sos')
    return signal.sosfilt(sos, x)


def lowpass(x, cutoff, order=3):
    sos = signal.butter(order, cutoff, btype='low', fs=RATE, output='sos')
    return signal.sosfilt(sos, x)


def highpass(x, cutoff, order=3):
    sos = signal.butter(order, cutoff, btype='high', fs=RATE, output='sos')
    return signal.sosfilt(sos, x)


def bell(freq, seconds, tau=0.5, brightness=1.0):
    """A small metallic bell: inharmonic partials with faster decay for higher ones."""
    t = t_axis(seconds)
    partials = [(1.0, 1.0, 1.0), (2.0, 0.55, 0.7), (2.76, 0.35 * brightness, 0.45), (5.4, 0.18 * brightness, 0.25),
                (8.93, 0.08 * brightness, 0.15)]
    out = np.zeros_like(t)
    for ratio, amp, life in partials:
        out += amp * np.sin(2 * np.pi * freq * ratio * t) * np.exp(-t / (tau * life))
    attack = np.minimum(1.0, t / 0.003)
    return out * attack


def rustle(seconds, seed, bursts=3, lo=900, hi=6500):
    """Cloth: a few band-limited noise bursts with crackle."""
    rng = np.random.default_rng(seed)
    n = int(RATE * seconds)
    out = np.zeros(n)
    total = np.zeros(n)
    noise = bandpass(rng.standard_normal(n), lo, hi)
    for i in range(bursts):
        start = int(n * (i / bursts) * 0.8 + rng.integers(0, int(0.02 * RATE)))
        length = int(rng.uniform(0.05, 0.11) * RATE)
        e = np.zeros(n)
        seg = np.hanning(length * 2)[:length] ** 0.5 * exp_decay(length, rng.uniform(0.03, 0.06))
        end = min(n, start + length)
        e[start:end] = seg[:end - start]
        total = np.maximum(total, e)
        out += noise * e * rng.uniform(0.6, 1.0)
    crackle = (rng.random(n) > 0.9965).astype(float) * rng.uniform(-1, 1, n)
    out += highpass(crackle, 2500) * 0.25 * total
    return out


def thump(seconds=0.18, freq=95):
    t = t_axis(seconds)
    sweep = freq * (1 + 1.5 * np.exp(-t / 0.02))
    phase = 2 * np.pi * np.cumsum(sweep) / RATE
    return np.sin(phase) * exp_decay(len(t), 0.05)


def place(dest, src, at_seconds, gain=1.0):
    start = int(at_seconds * RATE)
    end = min(len(dest), start + len(src))
    if end > start:
        dest[start:end] += src[:end - start] * gain
    return dest


def normalize(x, peak=0.89, fade_fraction=0.0):
    x = x - np.mean(x)
    m = np.max(np.abs(x)) or 1.0
    fade = max(int(0.01 * RATE), int(len(x) * fade_fraction))
    x = x / m * peak
    x[-fade:] *= np.linspace(1, 0, fade)
    return x


def note(name):
    names = {'C': -9, 'C#': -8, 'D': -7, 'D#': -6, 'E': -5, 'F': -4, 'F#': -3, 'G': -2, 'G#': -1, 'A': 0, 'A#': 1, 'B': 2}
    pitch, octave = name[:-1], int(name[-1])
    return 440.0 * 2 ** ((names[pitch] + (octave - 4) * 12) / 12)


def shimmer(seconds, seed, base=3000):
    """A soft cloud of tiny high bells."""
    rng = np.random.default_rng(seed)
    out = np.zeros(int(RATE * seconds))
    for _ in range(int(seconds * 22)):
        f = base * rng.uniform(1.0, 2.2)
        b = bell(f, 0.35, tau=0.12, brightness=0.4) * rng.uniform(0.05, 0.16)
        place(out, b, rng.uniform(0, seconds * 0.8))
    return out


# --------------------------------------------------------------- the sounds

def bag_open(seed):
    out = np.zeros(int(RATE * 0.55))
    place(out, rustle(0.4, seed, bursts=3), 0.0, 0.8)
    place(out, thump(0.2, 120), 0.18, 0.55)
    place(out, highpass(rustle(0.15, seed + 9, bursts=1, lo=2500, hi=9000), 2000), 0.2, 0.5)
    return normalize(out, 0.8)


def bag_rustle(seed):
    return normalize(rustle(0.16, seed, bursts=1, lo=1200, hi=7000), 0.6)


def bag_drop():
    out = np.zeros(int(RATE * 0.9))
    place(out, bell(note('E6'), 0.9, tau=0.35), 0.0, 0.8)
    place(out, bell(note('B6'), 0.7, tau=0.25, brightness=0.6), 0.06, 0.35)
    place(out, thump(0.12, 160), 0.0, 0.25)
    return normalize(out, 0.75)


def reveal(notes, spacing, tail, seed, boom=False, sparkle_gain=0.5):
    total = spacing * len(notes) + tail
    out = np.zeros(int(RATE * total))
    for i, n in enumerate(notes):
        place(out, bell(note(n), tail + 0.4, tau=0.55, brightness=0.8), i * spacing, 0.65)
    if sparkle_gain:
        place(out, shimmer(total * 0.7, seed), spacing * (len(notes) - 1), sparkle_gain)
    if boom:
        b = lowpass(thump(1.2, 48), 400) * 1.4
        place(out, b, 0.0, 0.9)
        rng = np.random.default_rng(seed)
        swell = bandpass(rng.standard_normal(len(out)), 300, 4000) * np.linspace(0, 1, len(out)) ** 2 * 0.15
        out += swell * np.exp(-np.maximum(0, np.arange(len(out)) - len(out) * 0.7) / (0.2 * RATE))
    return normalize(out, 0.85, fade_fraction=0.35)


def cursed():
    t = t_axis(1.6)
    rng = np.random.default_rng(66)
    drone = (np.sin(2 * np.pi * 73.4 * t) + 0.7 * np.sin(2 * np.pi * 77.8 * t) + 0.4 * np.sin(2 * np.pi * 110.0 * t * (1 - 0.04 * t)))
    drone *= env_adsr(len(t), 0.25, 0.3, 0.6, 0.7, 0.7)
    whisper = bandpass(rng.standard_normal(len(t)), 1800, 5200) * (0.5 + 0.5 * np.sin(2 * np.pi * 5.5 * t)) * env_adsr(len(t), 0.4, 0.2, 0.5, 0.6, 0.6)
    out = lowpass(drone, 900) * 0.8 + whisper * 0.35
    place(out, bell(note('F#3'), 1.2, tau=0.6, brightness=1.4), 0.05, 0.3)
    return normalize(out, 0.85)


def unlock():
    out = np.zeros(int(RATE * 0.55))
    click = highpass(np.random.default_rng(3).standard_normal(int(0.012 * RATE)), 2500) * exp_decay(int(0.012 * RATE), 0.003)
    place(out, click, 0.0, 0.8)
    place(out, click, 0.09, 0.6)
    place(out, bell(note('A5'), 0.45, tau=0.12, brightness=1.6), 0.2, 0.4)
    place(out, thump(0.1, 220), 0.2, 0.3)
    return normalize(out, 0.8)


def hourglass_ripe():
    out = np.zeros(int(RATE * 1.2))
    rng = np.random.default_rng(12)
    sand = bandpass(rng.standard_normal(len(out)), 3000, 9000) * np.exp(-t_axis(1.2) / 0.5) * 0.25
    out += sand
    place(out, bell(note('G5'), 1.0, tau=0.45), 0.05, 0.6)
    place(out, bell(note('D6'), 0.9, tau=0.4), 0.22, 0.45)
    return normalize(out, 0.8)


def machine_opener(seed):
    out = np.zeros(int(RATE * 0.4))
    place(out, lowpass(thump(0.15, 90), 600), 0.0, 0.7)
    rng = np.random.default_rng(seed)
    snip = highpass(rng.standard_normal(int(0.03 * RATE)), 3000) * exp_decay(int(0.03 * RATE), 0.006)
    place(out, snip, 0.08, 0.6)
    place(out, rustle(0.25, seed, bursts=2), 0.1, 0.5)
    return normalize(out, 0.7)


def machine_recycler(seed):
    rng = np.random.default_rng(seed)
    n = int(RATE * 0.5)
    t = t_axis(0.5)
    grit = bandpass(rng.standard_normal(n), 300, 3500) * (0.6 + 0.4 * np.sign(np.sin(2 * np.pi * 26 * t)))
    crunch = (rng.random(n) > 0.997).astype(float)
    crunch = lowpass(crunch * rng.uniform(-1, 1, n), 2500) * 4
    out = (grit * 0.6 + crunch) * env_adsr(n, 0.02, 0.1, 0.3, 0.2, 0.7)
    return normalize(out, 0.65)


SOUNDS = {
    'bag_open_1': lambda: bag_open(1),
    'bag_open_2': lambda: bag_open(2),
    'bag_open_3': lambda: bag_open(3),
    'bag_rustle_1': lambda: bag_rustle(11),
    'bag_rustle_2': lambda: bag_rustle(12),
    'bag_rustle_3': lambda: bag_rustle(13),
    'bag_drop': bag_drop,
    'reveal_rare': lambda: reveal(['C6', 'G6'], 0.09, 0.7, 21, sparkle_gain=0.25),
    'reveal_epic': lambda: reveal(['A5', 'C#6', 'E6'], 0.08, 0.9, 22, sparkle_gain=0.4),
    'reveal_legendary': lambda: reveal(['C5', 'E5', 'G5', 'C6', 'E6'], 0.075, 1.2, 23, sparkle_gain=0.6),
    'reveal_mythic': lambda: reveal(['D4', 'A4', 'D5', 'F#5', 'A5', 'D6'], 0.09, 1.6, 24, boom=True, sparkle_gain=0.7),
    'cursed': cursed,
    'unlock': unlock,
    'hourglass_ripe': hourglass_ripe,
    'machine_opener_1': lambda: machine_opener(31),
    'machine_opener_2': lambda: machine_opener(32),
    'machine_recycler_1': lambda: machine_recycler(41),
    'machine_recycler_2': lambda: machine_recycler(42),
}


def write_ogg(samples, dest: Path):
    dest.parent.mkdir(parents=True, exist_ok=True)
    pcm = (np.clip(samples, -1, 1) * 32767).astype(np.int16)
    with tempfile.NamedTemporaryFile(suffix='.wav', delete=False) as tmp:
        with wave.open(tmp.name, 'wb') as w:
            w.setnchannels(1)
            w.setsampwidth(2)
            w.setframerate(RATE)
            w.writeframes(pcm.tobytes())
        subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-i', tmp.name, '-c:a', 'libvorbis', '-q:a', '5', str(dest)], check=True)


def build(out_dir: Path):
    for name, make in SOUNDS.items():
        write_ogg(make(), out_dir / f'{name}.ogg')
