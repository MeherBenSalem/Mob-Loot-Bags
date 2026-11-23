#!/usr/bin/env node
'use strict';

const assert = require('assert');
const { detect, detectRequired } = require('./detect-jar-meta.js');

const cases = [
  // The 8 NightBeam convention jars (mob_loot_bags-{loader}-{minecraft}-{version}.jar)
  ['mob_loot_bags-fabric-1.20.1-2.0.3.jar', 'fabric', '1.20.1'],
  ['mob_loot_bags-forge-1.20.1-2.0.3.jar', 'forge', '1.20.1'],
  ['mob_loot_bags-fabric-1.21.1-2.0.3.jar', 'fabric', '1.21.1'],
  ['mob_loot_bags-neoforge-1.21.1-2.0.3.jar', 'neoforge', '1.21.1'],
  ['mob_loot_bags-fabric-26.2-2.0.3.jar', 'fabric', '26.2'],
  ['mob_loot_bags-neoforge-26.2-2.0.3.jar', 'neoforge', '26.2'],
  ['mob_loot_bags-fabric-26.3-2.0.3.jar', 'fabric', '26.3'],
  ['mob_loot_bags-neoforge-26.3-2.0.3.jar', 'neoforge', '26.3'],
  ['releases/mob_loot_bags-forge-1.20.1-2.0.3.jar', 'forge', '1.20.1'],
  // A calendar patch release keeps all three parts
  ['mob_loot_bags-fabric-26.2.1-2.0.3.jar', 'fabric', '26.2.1'],
  // Older forge-style names
  ['mob_loot_bags-1.10.1-forge-1.20.1.jar', 'forge', '1.20.1'],
  ['mob_loot_bags-1.10.1-neoforge-1.21.1.jar', 'neoforge', '1.21.1'],
];

let failed = 0;
for (const [name, loader, gv] of cases) {
  const got = detect(name);
  try {
    assert.strictEqual(got.loader, loader, `${name} loader`);
    assert.strictEqual(got.gv, gv, `${name} gv`);
    console.log('ok', name, '->', got);
  } catch (e) {
    failed += 1;
    console.error('FAIL', name, 'got', got, e.message);
  }
}

// Regression: must NOT fall back to 1.21.1 for unparseable / ambiguous names
try {
  detectRequired('mob_loot_bags-fabric-mystery.jar');
  failed += 1;
  console.error('FAIL expected throw for mystery jar');
} catch (e) {
  console.log('ok refuses default fallback:', e.message.split('\n')[0]);
}

// Historical bug: old regex matched only 26.\\d+ then fell through to default 1.21.1
{
  const got = detect('mob_loot_bags-neoforge-26.2.1-2.0.3.jar');
  assert.strictEqual(got.gv, '26.2.1');
  assert.notStrictEqual(got.gv, '1.21.1');
  console.log('ok a three-part calendar version is not mis-detected as 1.21.1');
}

// 1.20.1 ships Forge (CurseForge loader 7498), never NeoForge
{
  const { LOADER_IDS } = require('./detect-jar-meta.js');
  const got = detect('mob_loot_bags-forge-1.20.1-2.0.3.jar');
  assert.strictEqual(got.loader, 'forge');
  assert.strictEqual(LOADER_IDS[got.loader], 7498);
  console.log('ok 1.20.1 Forge jar maps to CurseForge loader 7498');
}

if (failed) {
  console.error(`${failed} test(s) failed`);
  process.exit(1);
}
console.log('All detect-jar-meta tests passed');
