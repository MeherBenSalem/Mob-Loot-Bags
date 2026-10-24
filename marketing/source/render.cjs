// Renders the listing artwork in marketing/images/ from real in-game captures, the mod's own textures and the bundled
// OFL fonts. Usage (from the repository root): NODE_PATH="$(npm root -g)" node marketing/source/render.cjs [name...]
// Requires the `playwright` package with a Chromium build available.
const path = require('path');
const fs = require('fs');
const { chromium } = require('playwright');

// The release and the supported Minecraft versions. Adding a version is one line here
// (plus one row in the compatibility table of curseforge/description.md).
const RELEASE = { version: '2.0', name: 'Spoils of the Hunt' };
const VERSIONS = [
  { minecraft: '1.20.1', loaders: ['Fabric', 'Forge'] },
  { minecraft: '1.21.1', loaders: ['Fabric', 'NeoForge'] },
  { minecraft: '26.1.2', loaders: ['Fabric', 'NeoForge'] },
  { minecraft: '26.2', loaders: ['Fabric', 'NeoForge'] },
  { minecraft: '26.3', loaders: ['Fabric', 'NeoForge'] },
];
const LOADERS = ['Fabric', 'Forge', 'NeoForge'].filter((loader) => VERSIONS.some((v) => v.loaders.includes(loader)));

const root = path.resolve(__dirname, '..', '..');
const out = path.join(root, 'marketing', 'images');
const shots = path.join(out, 'screenshots');
const fonts = path.join(__dirname, 'fonts');
const textures = path.join(root, '26.3', 'common', 'src', 'main', 'resources', 'assets', 'mob_loot_bags', 'textures');
const url = (file) => 'file://' + file;
const shot = (name) => url(path.join(shots, name));
const source = (name) => url(path.join(__dirname, name));
// The first frame of a (possibly animated) item texture.
const bagTex = (file) => url(path.join(textures, 'item', file + '.png'));

// Tier colours match BagType.java.
const TIERS = [
  { key: 'common', name: 'Common', color: '#d9d4c7', file: 'common_loot_bag', pct: '60%' },
  { key: 'uncommon', name: 'Uncommon', color: '#5bd45b', file: 'uncommon_loot_bag', pct: '26%' },
  { key: 'rare', name: 'Rare', color: '#4b9cff', file: 'rare_loot_bag', pct: '9.5%' },
  { key: 'epic', name: 'Epic', color: '#b15cff', file: 'epic_loot_bag', pct: '3.5%' },
  { key: 'legendary', name: 'Legendary', color: '#ffb01f', file: 'legendary_loot_bag', pct: '0.9%' },
  { key: 'mythic', name: 'Mythic', color: '#ff4f7b', file: 'mythic_loot_bag', pct: '0.1%' },
];
const SPECIALS = [
  { name: 'Cursed', color: '#9be05a', file: 'cursed_loot_bag', line: 'An ambush, a jackpot or a hex' },
  { name: 'Hourglass', color: '#4fd8c8', file: 'hourglass_loot_bag', line: 'Ripens in 5 to 10 minutes into better bags' },
  { name: 'Locked', color: '#b8c4d6', file: 'locked_loot_bag', line: 'Needs a Diamond Key: netherite-grade loot' },
  { name: 'Summoning', color: '#f0864a', file: 'summoning_loot_bag', line: 'A bag full of spawn eggs' },
];

const css = `
@font-face { font-family: MlbDisplay; src: url('${url(path.join(fonts, 'lilita-one.woff2'))}'); }
@font-face { font-family: MlbBody; font-weight: 600; src: url('${url(path.join(fonts, 'nunito-600.woff2'))}'); }
@font-face { font-family: MlbBody; font-weight: 800; src: url('${url(path.join(fonts, 'nunito-800.woff2'))}'); }
:root { --text:#ece6f5; --dim:#a99fbd; --faint:#6f6684; --gold:#ffc23a; --gold-hi:#ffe9a0; --bg:#0d0a14; --line:rgba(255,194,58,.45); }
* { box-sizing: border-box; }
html, body { margin: 0; background: var(--bg); color: var(--text); font-family: MlbBody, sans-serif; font-weight: 600; }
.stage { position: relative; overflow: hidden; background: radial-gradient(ellipse at 30% 40%, #22163a 0%, #0d0a14 70%); }
.display { font-family: MlbDisplay, sans-serif; letter-spacing: .01em; }
.gold { background: linear-gradient(180deg, #fff6d0 0%, #ffe28a 30%, #ffc23a 62%, #c9861a 100%);
        -webkit-background-clip: text; background-clip: text; color: transparent; }
.glow { filter: drop-shadow(0 0 22px rgba(255,194,58,.35)) drop-shadow(0 3px 0 rgba(0,0,0,.6)); }
.kicker { font-family: MlbBody, sans-serif; font-weight: 800; color: var(--gold); letter-spacing: .2em; font-size: 22px; text-transform: uppercase; }
.rule { height: 3px; border-radius: 2px; background: linear-gradient(90deg, rgba(255,194,58,0), rgba(255,194,58,.95) 15%, rgba(255,79,123,.8) 70%, rgba(255,79,123,0)); }
.gem { display: inline-block; width: 12px; height: 12px; background: var(--gold); transform: rotate(45deg); margin: 0 14px 3px 2px; box-shadow: 0 0 10px rgba(255,194,58,.8); }
.frame { position: relative; border-radius: 14px; overflow: hidden; border: 2px solid rgba(255,226,138,.55);
         box-shadow: 0 0 0 1px rgba(0,0,0,.7), 0 30px 80px rgba(0,0,0,.7), 0 0 70px rgba(255,194,58,.18); background: #000; }
.frame > img { display: block; width: 100%; }
canvas.sparkles { position: absolute; left: 0; top: 0; width: 100%; height: 100%; pointer-events: none; }
.vignette { position: absolute; inset: 0; background: radial-gradient(ellipse at center, rgba(0,0,0,0) 40%, rgba(0,0,0,.78) 100%); }
.backdrop { position: absolute; inset: -40px; background-size: cover; background-position: center; filter: blur(14px) brightness(.38) saturate(1.2); }
.beams { position: absolute; inset: 0; pointer-events: none; }
.beam { position: absolute; bottom: 0; width: 70px; border-radius: 40px; filter: blur(18px); opacity: .32; }
.pill { display: inline-block; border-radius: 999px; border: 2px solid rgba(255,226,138,.6); padding: 7px 20px 6px; margin-right: 12px;
        font-family: MlbBody, sans-serif; font-weight: 800; font-size: 20px; letter-spacing: .08em; color: #fff3d2; background: rgba(20,12,30,.6); }
.label { position: absolute; font-family: MlbBody, sans-serif; font-weight: 800; font-size: 21px; letter-spacing: .04em; color: #fff3d2;
         background: rgba(14,10,22,.88); border: 2px solid rgba(255,194,58,.8); border-radius: 10px; padding: 8px 16px 7px; white-space: nowrap;
         box-shadow: 0 8px 26px rgba(0,0,0,.6); z-index: 2; }
.label small { display: block; font-weight: 600; letter-spacing: 0; font-size: 17px; color: var(--dim); margin-top: 1px; }
ul.points { list-style: none; padding: 0; margin: 0; }
ul.points li { position: relative; padding-left: 30px; margin: 0 0 15px; font-size: 27px; line-height: 1.32; color: var(--text); }
ul.points li::before { content: ''; position: absolute; left: 3px; top: 12px; width: 11px; height: 11px; background: var(--gold); transform: rotate(45deg);
                       box-shadow: 0 0 8px rgba(255,194,58,.7); }
.stats { display: flex; margin-top: 22px; border-radius: 14px; overflow: hidden; border: 2px solid rgba(255,226,138,.35); background: rgba(14,10,22,.85);
         box-shadow: 0 18px 50px rgba(0,0,0,.55); }
.stats > div { flex: 1; padding: 14px 24px 12px; border-left: 2px solid rgba(255,226,138,.15); }
.stats > div:first-child { border-left: 0; }
.stats .t { font-weight: 800; font-size: 16px; letter-spacing: .16em; color: var(--dim); text-transform: uppercase; }
.stats .v { font-family: MlbDisplay, sans-serif; font-size: 34px; color: #fff3d2; margin-top: 2px; white-space: nowrap; }
b.hi { color: #fff3d2; font-weight: 800; }
img.px { image-rendering: pixelated; display: block; }
code, pre { font-family: 'DejaVu Sans Mono', monospace; }
`;

// Deterministic gold sparkles (four-point stars), so renders are reproducible.
const sparkles = (count = 70, seed = 7) => `<canvas class="sparkles"></canvas><script>
(() => { const c = document.currentScript.previousElementSibling; const r = c.getBoundingClientRect();
  c.width = r.width; c.height = r.height; const g = c.getContext('2d'); let s = ${seed};
  const rnd = () => (s = (s * 16807) % 2147483647) / 2147483647;
  for (let i = 0; i < ${count}; i++) { const x = rnd() * c.width, y = rnd() * c.height, a = .2 + rnd() * .65, z = 1.5 + rnd() * 4;
    const h = g.createRadialGradient(x, y, 0, x, y, z * 4); h.addColorStop(0, 'rgba(255,214,120,' + a * .5 + ')'); h.addColorStop(1, 'rgba(255,214,120,0)');
    g.fillStyle = h; g.beginPath(); g.arc(x, y, z * 4, 0, Math.PI * 2); g.fill();
    g.fillStyle = 'rgba(255,246,208,' + a + ')'; g.beginPath(); g.moveTo(x, y - z * 2); g.lineTo(x + z * .45, y); g.lineTo(x, y + z * 2);
    g.lineTo(x - z * .45, y); g.closePath(); g.fill(); g.beginPath(); g.moveTo(x - z * 2, y); g.lineTo(x, y + z * .45); g.lineTo(x + z * 2, y);
    g.lineTo(x, y - z * .45); g.closePath(); g.fill(); } })();
</script>`;

// Soft tier-coloured light columns, the listing's version of the in-game loot beams.
const beams = (items) => `<div class="beams">${items.map(([left, color, height]) =>
  `<div class="beam" style="left:${left}px;height:${height}px;background:linear-gradient(0deg, ${color}, transparent)"></div>`).join('')}</div>`;

const page = (width, height, body, transparent = false) => `<!doctype html><html><head><meta charset="utf-8"><style>${css}
body { width: ${width}px; height: ${height}px; } .stage { width: ${width}px; height: ${height}px; }
${transparent ? 'html, body, .stage { background: transparent; }' : ''}</style></head>
<body><div class="stage">${body}</div></body></html>`;

const bag = (file, size, color, extra = '') => `<img class="px" src="${bagTex(file)}" style="width:${size}px;height:${size}px;object-fit:cover;object-position:top;
  filter:drop-shadow(0 0 ${Math.round(size / 6)}px ${color}aa) drop-shadow(0 ${Math.round(size / 24)}px 0 rgba(0,0,0,.55));${extra}">`;

const header = (title) => page(1400, 120, `
  <div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:22px">
    <div class="rule" style="width:250px;transform:scaleX(-1)"></div>
    ${bag('legendary_loot_bag', 56, '#ffb01f')}
    <div class="display gold glow" style="font-size:58px;white-space:nowrap">${title}</div>
    ${bag('mythic_loot_bag', 56, '#ff4f7b')}
    <div class="rule" style="width:250px"></div>
  </div>`, true);

const divider = () => page(1400, 40, `
  <div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:18px">
    <div class="rule" style="width:420px;transform:scaleX(-1);opacity:.7"></div>
    ${TIERS.map((t) => `<span style="display:inline-block;width:12px;height:12px;transform:rotate(45deg);background:${t.color};box-shadow:0 0 10px ${t.color}"></span>`).join('')}
    <div class="rule" style="width:420px;opacity:.7"></div>
  </div>`, true);

// A feature card: copy on the left, artwork on the right.
const card = ({ kicker, title, points, art, width = 1600, height = 900, artWidth = 960, backdrop = 'backdrop.jpg', beamSet }) => page(width, height, `
  <div class="backdrop" style="background-image:url('${source(backdrop)}')"></div>
  ${beams(beamSet || [[90, '#ffb01f', 520], [300, '#b15cff', 380], [520, '#4b9cff', 460]])}
  <div class="vignette"></div>${sparkles()}
  <div style="position:absolute;left:84px;top:0;bottom:0;width:${width - artWidth - 160}px;display:flex;flex-direction:column;justify-content:center">
    <div class="kicker"><span class="gem"></span>${kicker}</div>
    <div class="display gold glow" style="font-size:70px;line-height:1.02;margin:16px 0 22px">${title}</div>
    <div class="rule" style="width:78%;margin-bottom:30px"></div>
    <ul class="points">${points.map((point) => `<li>${point.replace(/ (\S+)$/, '&nbsp;$1')}</li>`).join('')}</ul>
  </div>
  <div style="position:absolute;right:64px;top:50%;transform:translateY(-50%);width:${artWidth}px">${art}</div>`);

// An exact region (x, y, w, h in 1920x1080 pixels) of a capture, scaled to W pixels wide.
const region = (file, x, y, w, h, W) => `<div style="position:relative;width:${W}px;height:${Math.round(W * h / w)}px;overflow:hidden">
    <img src="${shot(file)}" style="position:absolute;max-width:none;width:${W * 1920 / w}px;left:${-x * W / w}px;top:${-y * W / w}px;image-rendering:${w < 900 ? 'pixelated' : 'auto'}"></div>`;
const framed = (inner, style = '') => `<div class="frame" style="${style}">${inner}</div>`;
const label = (text, style, small = '') => `<div class="label" style="${style}">${text}${small ? `<small>${small}</small>` : ''}</div>`;
const stats = (items) => `<div class="stats">${items.map(([t, v, flex]) =>
  `<div${flex ? ` style="flex:${flex}"` : ''}><div class="t">${t}</div><div class="v">${v}</div></div>`).join('')}</div>`;

const versionText = VERSIONS.map((v) => v.minecraft).join(' · ');

const assets = {
  banner: () => page(1800, 600, `
    <img src="${shot('battle.jpg')}" style="position:absolute;right:-80px;top:-150px;width:1460px;-webkit-mask-image:linear-gradient(90deg,transparent 0%,#000 36%);mask-image:linear-gradient(90deg,transparent 0%,#000 36%)">
    <div style="position:absolute;inset:0;background:linear-gradient(90deg,rgba(13,10,20,.97) 0%,rgba(13,10,20,.9) 32%,rgba(13,10,20,.35) 58%,rgba(13,10,20,0) 78%)"></div>
    <div style="position:absolute;inset:0;background:linear-gradient(0deg,rgba(13,10,20,.75) 0%,rgba(13,10,20,0) 32%)"></div>
    <div class="vignette" style="opacity:.6"></div>${sparkles(60, 3)}
    <div style="position:absolute;left:84px;top:62px;width:940px">
      <div style="display:flex;gap:10px;align-items:flex-end;margin-bottom:6px">
        ${TIERS.map((t, i) => bag(t.file, 52 + i * 8, t.color)).join('')}
      </div>
      <div class="kicker" style="margin-top:14px"><span class="gem"></span>${RELEASE.version} · ${RELEASE.name}</div>
      <div class="display gold glow" style="font-size:112px;line-height:.95;margin:14px 0 20px;white-space:nowrap">Mob Loot Bags</div>
      <div class="rule" style="width:640px;margin-bottom:22px"></div>
      <div style="font-size:31px;color:#fff3d2;line-height:1.3;font-weight:800">Every kill could be a jackpot. Six tiers, special bags,<br>machines and loot you can preview.</div>
      <div style="margin-top:28px">${LOADERS.map((l) => `<span class="pill">${l}</span>`).join('')}
        <span style="font-size:22px;color:var(--dim);margin-left:6px;font-weight:800">${versionText}</span></div>
    </div>`),

  icon: () => page(512, 512, `
    <div style="position:absolute;inset:0;border-radius:96px;overflow:hidden;background:radial-gradient(circle at 50% 42%,#3d2259 0%,#170f24 58%,#0a0710 100%)">
      ${beams([[150, '#ffb01f', 420], [290, '#ff4f7b', 380]])}${sparkles(26, 11)}
      <div style="position:absolute;left:96px;top:86px">${bag('legendary_loot_bag', 320, '#ffb01f')}</div>
      <div style="position:absolute;left:28px;top:300px">${bag('rare_loot_bag', 128, '#4b9cff')}</div>
      <div style="position:absolute;left:356px;top:300px">${bag('mythic_loot_bag', 128, '#ff4f7b')}</div>
    </div>
    <div style="position:absolute;inset:0;border-radius:96px;border:6px solid #ffc23a;box-shadow:inset 0 0 40px rgba(255,194,58,.35)"></div>`),

  'card-tiers': () => card({
    kicker: 'Six tiers', title: 'Common to<br>Mythic',
    points: ['Every hostile mob can drop a bag: 18% base, more from tougher mobs', 'Each tier rolls its own loot table, from supplies to netherite',
      'New <b class="hi">Mythic</b> tier: nether stars, maxed netherite gear, beacons', 'Legendary and Mythic bags shimmer, beam and announce themselves',
      'Craft four bags into one of the next tier, or split one back'],
    art: `<div style="display:flex;justify-content:space-between;align-items:flex-end;padding:0 20px;height:420px">
      ${TIERS.map((t, i) => `<div style="display:flex;flex-direction:column;align-items:center;width:150px">
        <div style="height:${150 + i * 34}px;width:24px;margin-bottom:-20px;border-radius:12px;background:linear-gradient(0deg,${t.color},transparent);filter:blur(5px);opacity:.75"></div>
        ${bag(t.file, 128, t.color)}
        <div class="display" style="font-size:30px;color:${t.color};margin-top:12px;text-shadow:0 2px 0 #000">${t.name}</div>
        <div style="font-size:20px;color:var(--dim);font-weight:800">${t.pct}</div></div>`).join('')}</div>
      ${stats([['Base chance', '18%'], ['Mob health', '×0.5–2.5'], ['Tiers', '6'], ['Special bags', '4']])}` }),

  'card-drops': () => card({
    kicker: 'Smarter drops', title: 'Hunt for<br>the loot',
    points: ['Tougher mobs drop more often, and better bags', '<b class="hi">Bag carriers</b>: some mobs spawn holding a bag. Take it from them',
      'Bags drop with a chime and a beam of their tier’s colour', 'Spawner mobs drop a quarter as often, so farms stay fair',
      'Nether and End kills roll <b class="hi">themed loot</b>'],
    art: framed(region('battle.jpg', 120, 140, 1680, 945, 960)) + stats([['Bag Luck', 'Looting · Luck · Health'], ['Nether', '+0.5'], ['End', '+1'], ['Hard', '+0.25']]) }),

  'card-opening': () => card({
    kicker: 'Opening bags', title: 'Hold, rustle,<br>reveal',
    points: ['Hold Use to tug a bag open, with sound and particles', 'Sneak to open the whole stack at once',
      'Loot goes straight to your inventory', 'A reveal toast shows what you got', 'Legendary and Mythic bags flash a title and play a fanfare'],
    art: `<div style="position:relative">${framed(region('open-legendary.jpg', 0, 0, 1920, 1080, 960))}
      <div class="frame" style="position:absolute;right:-26px;top:-36px;width:420px">${region('open-legendary.jpg', 1428, 108, 492, 112, 420)}</div></div>` }),

  'card-preview': () => card({
    kicker: 'Loot preview', title: 'Know what’s<br>inside',
    points: ['Hold Shift over a bag to see what it can give', 'Chances come from the server’s real loot tables',
      'Datapack and modded tables show up too', 'Themed bags preview their themed loot'],
    art: framed(region('tooltip.jpg', 520, 120, 1000, 760, 960)) }),

  'card-specials': () => card({
    kicker: 'Special bags', title: 'High risk,<br>high reward',
    points: SPECIALS.map((s) => `<b class="hi" style="color:${s.color}">${s.name}</b>: ${s.line}`)
      .concat(['A <b class="hi">Fortune Talisman</b> makes every bag likelier and better']),
    art: `<div style="position:relative">${framed(region('cursed.jpg', 0, 60, 1920, 1000, 960))}
      <div style="position:absolute;left:24px;bottom:24px;display:flex;gap:14px;background:rgba(14,10,22,.85);border:2px solid rgba(255,194,58,.7);border-radius:14px;padding:14px 18px">
        ${SPECIALS.map((s) => bag(s.file, 88, s.color)).join('')}</div>
      ${label('Cursed ambush', 'right:20px;top:20px', 'kill them for bags')}</div>` }),

  'card-opener': () => card({
    kicker: 'Automation', title: 'Bag<br>Opener',
    points: ['9 input slots, 18 output slots', 'Four redstone modes: always, on, off and one bag per pulse',
      'Hoppers and pipes feed the sides and empty the bottom', 'Waits for Hourglass bags to ripen', 'Comparator output'],
    art: `<div style="position:relative">${framed(region('opener-gui.jpg', 600, 230, 720, 620, 600), 'width:600px')}
      <div class="frame" style="position:absolute;right:0;top:120px;width:470px">${region('workshop.jpg', 0, 0, 1920, 1080, 470)}</div></div>` }),

  'card-recycler': () => card({
    kicker: 'Automation', title: 'Loot<br>Recycler',
    points: ['Grinds mob-farm junk into salvage points', 'Every 64 points becomes a fresh loot bag',
      'Bags you do not want become stored experience', 'Take the experience with a button or a sneak-click', 'Every value is in machines.json'],
    art: `<div style="position:relative">${framed(region('recycler-gui.jpg', 600, 260, 720, 560, 600), 'width:600px')}
      <div class="frame" style="position:absolute;right:0;top:120px;width:470px">${region('workshop.jpg', 0, 0, 1920, 1080, 470)}</div></div>` }),

  'card-luck': () => card({
    kicker: 'Bag Luck, pity and bosses', title: 'Fortune favours<br>the bold',
    points: ['Looting, Luck, mob health, the Nether, the End and Hard difficulty raise your tier odds',
      'Bad-luck protection: a bag after 12 empty kills, a Rare after 20 bags, a Legendary after 200',
      '<b class="hi">Bosses</b> always drop three Epic or better bags, 15% Mythic each', 'Check any mob with <b class="hi">/lootbags odds</b>'],
    art: framed(region('boss.jpg', 0, 0, 1920, 1080, 960)) }),

  'card-config': () => card({
    kicker: 'Made for modpacks', title: 'Tune<br>everything',
    points: ['Four commented config files, reloaded with /lootbags reload', 'Every loot table can be overridden by a datapack',
      'Add items to <b class="hi">#mob_loot_bags:bonus/&lt;tier&gt;</b> without touching JSON', '/lootbags simulate rolls a bag 10,000 times in a second',
      '1.x configs migrate on first start'],
    art: framed(region('commands.jpg', 0, 430, 1240, 650, 960)) }),

  'card-advancements': () => card({
    kicker: 'Progression', title: 'Thirteen<br>advancements',
    points: ['From Loot Goblin to Myth Made Real', 'Stats for bags found and bags opened', 'Recipes unlock as you find your first bag',
      '/lootbags stats shows your history and pity counters'],
    art: framed(region('advancements.jpg', 340, 150, 1240, 780, 960)) }),
};

const HEADERS = {
  'header-whats-new': 'What’s New in 2.0', 'header-tiers': 'Six Tiers', 'header-drops': 'Smarter Drops', 'header-opening': 'Opening Bags',
  'header-preview': 'Loot Preview', 'header-specials': 'Special Bags', 'header-machines': 'Machines', 'header-luck': 'Luck & Bosses',
  'header-modpacks': 'For Modpacks', 'header-progression': 'Progression', 'header-compatibility': 'Downloads', 'header-faq': 'FAQ',
  'header-gallery': 'Gallery',
};
for (const [name, title] of Object.entries(HEADERS)) {
  assets[name] = () => header(title);
}
assets.divider = divider;

(async () => {
  const wanted = process.argv.slice(2);
  const names = wanted.length ? wanted : Object.keys(assets);
  const browser = await chromium.launch();
  const context = await browser.newContext({ deviceScaleFactor: 1 });
  for (const name of names) {
    if (!assets[name]) {
      console.error('unknown asset', name);
      continue;
    }
    const html = assets[name]();
    const file = path.join(__dirname, `.render-${name}.html`);
    fs.writeFileSync(file, html);
    const p = await context.newPage();
    await p.goto(url(file));
    await p.evaluate(() => document.fonts.ready);
    await p.waitForTimeout(150);
    const { width, height } = await p.evaluate(() => ({ width: document.body.scrollWidth, height: document.body.scrollHeight }));
    await p.setViewportSize({ width, height });
    const transparent = name.startsWith('header') || name === 'divider';
    await p.screenshot({ path: path.join(out, `${name}.png`), omitBackground: transparent || name === 'icon' });
    await p.close();
    fs.unlinkSync(file);
    console.log('rendered', name);
  }
  await browser.close();
})();
