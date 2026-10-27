/**
 * Browser regression suite against the real HTTP application.
 * Start `npm run dev`, then run `node tests/ui-smoke.mjs`.
 * Optional: GAME_URL, PLAYWRIGHT_MODULE, PLAYWRIGHT_CHANNEL,
 * PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH and UI_SCREENSHOT_DIR.
 * Playwright is intentionally optional; the core game has no npm dependencies.
 */
import assert from 'node:assert/strict';
import { mkdir } from 'node:fs/promises';
import { isAbsolute, resolve, join } from 'node:path';
import { pathToFileURL } from 'node:url';
import { LEVELS } from '../web/levels.js';
import { availableCells, getRoute } from '../web/engine.js';
import { STORAGE_KEY, RULES_REVISION } from '../web/storage.js';

const moduleName = process.env.PLAYWRIGHT_MODULE || 'playwright';
const { chromium } = await import(isAbsolute(moduleName) ? pathToFileURL(moduleName).href : moduleName);
const baseURL = process.env.GAME_URL || 'http://127.0.0.1:4173';
const screenshots = process.env.UI_SCREENSHOT_DIR && resolve(process.env.UI_SCREENSHOT_DIR);
if (screenshots) await mkdir(screenshots, { recursive: true });
const browser = await chromium.launch({
  headless: true,
  ...(process.env.PLAYWRIGHT_CHANNEL ? { channel: process.env.PLAYWRIGHT_CHANNEL } : {}),
  ...(process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH ? { executablePath: process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH } : {}),
});
const problems = [];
let scenarios = 0;
const first = LEVELS[0];
const arbitraryWall = availableCells(first)[0];
const extraWall = availableCells(first).find((cell) => !first.solution.includes(cell));

function checked(message) {
  scenarios += 1;
  console.log(`✓ ${message}`);
}

async function openPage(options = {}, beforeLoad) {
  const context = await browser.newContext({ viewport: { width: 1440, height: 1050 }, reducedMotion: 'reduce', ...options });
  const page = await context.newPage();
  page.setDefaultTimeout(10_000);
  page.on('pageerror', (error) => problems.push(error.message));
  page.on('console', (message) => { if (message.type() === 'error') problems.push(message.text()); });
  await page.clock.install({ time: new Date('2026-01-01T12:00:00Z') });
  await page.clock.pauseAt(new Date('2026-01-01T12:00:01Z'));
  if (beforeLoad) await beforeLoad(page);
  const response = await page.goto(baseURL);
  assert.ok(response?.ok(), 'The game server must return HTTP 200.');
  await page.waitForSelector('#menu-screen');
  return { page, context };
}

async function enterLevel(page, id, touch = false) {
  const action = touch ? 'tap' : 'click';
  if (await page.locator('#menu-screen').isVisible()) await page.locator('#menu-play')[action]();
  else if (await page.locator('#game-screen').isVisible()) await page.locator('#open-map')[action]();
  await page.locator(`[data-level="${id}"]`)[action]();
  assert.equal(await page.locator('#game-screen').isVisible(), true);
  assert.ok((await page.locator('#level-name').textContent()).includes(LEVELS[id - 1].name));
}

async function saved(page) {
  return page.evaluate((key) => JSON.parse(localStorage.getItem(key)), STORAGE_KEY);
}

async function wallCells(page) {
  return page.locator('#board .user-wall').evaluateAll((cells) => cells.map((cell) => Number(cell.dataset.cell)));
}

async function assertNoOverflow(page) {
  const dimensions = await page.evaluate(() => ({ document: document.documentElement.scrollWidth, viewport: innerWidth }));
  assert.ok(dimensions.document <= dimensions.viewport + 1, `Horizontal overflow: ${JSON.stringify(dimensions)}`);
}

async function screenshot(page, name) {
  if (screenshots) await page.screenshot({ path: join(screenshots, `${name}.png`), fullPage: true });
}

async function finishRun(page, level, walls) {
  await page.clock.runFor((getRoute(level, walls).path.length + 2) * 650);
  assert.equal(await page.locator('#modal').getAttribute('open'), null, 'Results stay on the original game screen.');
}

async function placeSolution(page, level, touch = false) {
  for (const cell of level.solution) await page.locator(`[data-cell="${cell}"]`)[touch ? 'tap' : 'click']();
  assert.deepEqual((await wallCells(page)).sort((a, b) => a - b), [...level.solution].sort((a, b) => a - b));
}

async function playSolution(page, level, touch = false) {
  assert.ok((await page.locator('#level-name').textContent()).includes(level.name));
  assert.equal(await page.locator('#board .cell').count(), level.size * level.size);
  if (level.portals) {
    for (const cell of level.portals) assert.equal(await page.locator(`[data-cell="${cell}"]`).isDisabled(), true);
  }
  await placeSolution(page, level, touch);
  await page.locator('#start')[touch ? 'tap' : 'click']();
  assert.equal(await page.locator('#hint').isDisabled(), true);
  assert.equal(await page.locator('#board .cell:enabled').count(), 0);
  await finishRun(page, level, level.solution);
  assert.equal(await page.locator('#result-summary .result-stars').getAttribute('aria-label'), 'Получено 3 из 3 звёзд');
  assert.equal((await saved(page)).scores[level.id], 3);
  assert.match(await page.locator('#start').textContent(), level.id === LEVELS.length ? /LEVELS/i : /NEXT/i);
}

try {
  const { page, context } = await openPage();
  await assertNoOverflow(page);
  await screenshot(page, 'desktop-menu');
  assert.equal(await page.locator('#game-screen').isVisible(), false);
  await page.locator('#menu-help').click();
  assert.equal(await page.locator('#modal').getAttribute('open'), '');
  await page.keyboard.press('Escape');
  assert.equal(await page.locator('#modal').getAttribute('open'), null);
  await page.locator('#menu-play').click();
  assert.equal(await page.locator('[data-level]').count(), LEVELS.length);
  assert.equal(await page.locator('[data-level]:enabled').count(), 1);
  assert.equal(await page.locator('[data-level="2"]').isDisabled(), true);
  await screenshot(page, 'desktop-levels');
  assert.equal(await page.evaluate(() => window.gameBack()), true);
  assert.equal(await page.locator('#menu-screen').isVisible(), true);
  assert.equal(await page.evaluate(() => window.gameBack()), false);
  await enterLevel(page, first.id);
  await assertNoOverflow(page);
  await screenshot(page, 'desktop-start');
  const enabled = await page.locator('#board .cell:enabled').evaluateAll((cells) => cells.map((cell) => Number(cell.dataset.cell)));
  const from = enabled.find((cell) => enabled.includes(cell + 1) && Math.floor(cell / first.size) === Math.floor((cell + 1) / first.size));
  assert.notEqual(from, undefined, 'The introductory level must allow keyboard navigation.');
  await page.locator(`[data-cell="${from}"]`).focus();
  await page.keyboard.press('ArrowRight');
  assert.equal(await page.evaluate(() => Number(document.activeElement.dataset.cell)), from + 1);
  checked('original main menu, Help/Escape, locked level menu, native Back and keyboard board navigation');

  await page.locator(`[data-cell="${arbitraryWall}"]`).click();
  assert.deepEqual(await wallCells(page), [arbitraryWall]);
  await page.locator('#undo').click();
  assert.deepEqual(await wallCells(page), []);
  await page.locator(`[data-cell="${arbitraryWall}"]`).click();
  await page.locator('#reset').click();
  assert.deepEqual(await wallCells(page), []);
  await page.locator('#undo').click();
  assert.deepEqual(await wallCells(page), [arbitraryWall]);
  await page.locator('#speed').click();
  await page.locator('#sound').click();
  await page.reload();
  await enterLevel(page, first.id);
  assert.deepEqual(await wallCells(page), [arbitraryWall]);
  assert.equal(await page.locator('#speed').textContent(), '2×');
  assert.equal(await page.locator('#sound').getAttribute('aria-pressed'), 'false');
  assert.equal(await page.locator('#undo').isDisabled(), true);
  checked('wall placement, undo, Try again/undo, persisted draft and sound/speed preferences');

  await page.locator('#reset').click();
  await page.locator('#start').click();
  await page.clock.runFor(250);
  await page.locator('#start').click();
  assert.match(await page.locator('#start').textContent(), /RESUME/i);
  const pausedPosition = await page.locator('#moving-monster').getAttribute('style');
  await page.clock.runFor(5000);
  assert.equal(await page.locator('#moving-monster').getAttribute('style'), pausedPosition);
  await page.locator('#start').click();
  await page.evaluate(() => window.dispatchEvent(new CustomEvent('appvisibility', { detail: { hidden: true } })));
  assert.match(await page.locator('#start').textContent(), /RESUME/i);
  await page.evaluate(() => window.dispatchEvent(new CustomEvent('appvisibility', { detail: { hidden: false } })));
  await page.locator('#start').click();
  await finishRun(page, first, []);
  assert.match(await page.locator('#start').textContent(), /RETRY/i);
  assert.deepEqual((await saved(page)).scores, {});
  assert.ok(await page.locator('#moving-monster img').getAttribute('src').then((src) => src.includes('bloody_monster')));
  await page.locator('#start').click();
  assert.equal(await page.locator('#hint').isEnabled(), true);
  checked('safe shortest-route loss, original loss artwork, retry, pause/resume and native background pause');

  await page.locator(`[data-cell="${extraWall}"]`).click();
  await page.locator('#hint').click();
  assert.equal(await page.locator('.hint-cell').getAttribute('data-cell'), String(extraWall));
  assert.match(await page.locator('#hint-text').textContent(), /убери/i);
  await page.locator(`[data-cell="${extraWall}"]`).click();
  await page.locator('#hint').click();
  assert.equal(await page.locator('.hint-cell').getAttribute('data-cell'), String(first.solution[0]));
  await placeSolution(page, first);
  await page.locator('#start').click();
  await finishRun(page, first, first.solution);
  assert.equal(await page.locator('#result-summary .result-stars').getAttribute('aria-label'), 'Получено 2 из 3 звёзд');
  assert.equal((await saved(page)).scores[first.id], 2);
  await page.locator('#start').click();
  assert.ok((await page.locator('#level-name').textContent()).includes(LEVELS[1].name));
  await page.locator('#open-map').click();
  assert.equal(await page.locator('[data-level]:enabled').count(), 2);
  assert.equal(await page.locator('[data-level="3"]').isDisabled(), true);
  await page.locator(`[data-level="${first.id}"]`).click();
  assert.deepEqual(await wallCells(page), []);
  assert.equal(await page.locator('#hint-text').isVisible(), false);
  assert.equal((await saved(page)).draft.hinted, false);
  checked('hints remove extras before placement, cap stars, new attempts reset hints and only next level unlocks');

  for (const level of LEVELS) {
    await playSolution(page, level);
    if (level.id === 18) await screenshot(page, 'desktop-portal-win');
    if (level.id < LEVELS.length) await page.locator('#start').click();
  }
  assert.equal(Object.values((await saved(page)).scores).reduce((sum, score) => sum + score, 0), 72);
  await screenshot(page, 'desktop-campaign-complete');
  await page.locator('#start').click();
  assert.equal(await page.locator('#levels-screen').isVisible(), true);
  assert.equal(await page.locator('[data-level]:enabled').count(), LEVELS.length);
  await page.locator(`[data-level="${LEVELS.length}"]`).click();
  const final = LEVELS.at(-1);
  await page.locator('#reset').click();
  await placeSolution(page, final);
  checked('all 24 levels won through UI, original inline victory, portal routes, 72 stars and final level menu');

  await page.evaluate(() => navigator.serviceWorker.ready);
  await page.reload();
  await enterLevel(page, final.id);
  assert.equal(await page.evaluate(() => Boolean(navigator.serviceWorker.controller)), true);
  assert.equal((await saved(page)).scores[final.id], 3);
  await context.setOffline(true);
  await page.reload();
  await enterLevel(page, final.id);
  assert.deepEqual((await wallCells(page)).sort((a, b) => a - b), [...final.solution].sort((a, b) => a - b));
  assert.equal(Object.keys((await saved(page)).scores).length, LEVELS.length);
  const brokenImages = await page.locator('img').evaluateAll((images) => images.filter((img) => img.offsetParent !== null && (!img.complete || !img.naturalWidth)).map((img) => img.src));
  assert.deepEqual(brokenImages, [], 'Original raster assets are also cached for offline use.');
  await context.setOffline(false);
  checked('completed campaign, draft and original artwork persist through real offline reload');
  await context.close();

  const mobileOptions = { viewport: { width: 390, height: 844 }, isMobile: true, hasTouch: true, deviceScaleFactor: 2 };
  let mobile = await openPage(mobileOptions);
  await assertNoOverflow(mobile.page);
  await screenshot(mobile.page, 'mobile-menu');
  await enterLevel(mobile.page, first.id, true);
  await assertNoOverflow(mobile.page);
  await screenshot(mobile.page, 'mobile-start');
  await playSolution(mobile.page, first, true);
  await mobile.page.locator('#start').tap();
  assert.ok((await mobile.page.locator('#level-name').textContent()).includes(LEVELS[1].name));
  await mobile.context.close();
  const portalLevel = LEVELS.find((level) => level.portals);
  mobile = await openPage({
    ...mobileOptions,
    storageState: { cookies: [], origins: [{ origin: new URL(baseURL).origin, localStorage: [{ name: STORAGE_KEY, value: JSON.stringify({
      level: portalLevel.id, sound: false, rulesRevision: RULES_REVISION,
      scores: Object.fromEntries(LEVELS.filter((level) => level.id < portalLevel.id).map((level) => [level.id, 3])),
    }) }] }] },
  });
  await enterLevel(mobile.page, portalLevel.id, true);
  await assertNoOverflow(mobile.page);
  await screenshot(mobile.page, 'mobile-portals');
  await playSolution(mobile.page, portalLevel, true);
  await mobile.page.setViewportSize({ width: 320, height: 740 });
  await assertNoOverflow(mobile.page);
  const smallCell = await mobile.page.locator('[data-cell="1"]').boundingBox();
  assert.ok(smallCell.width >= 24 && smallCell.height >= 24, 'Small-screen cells remain tappable.');
  await mobile.page.setViewportSize({ width: 844, height: 390 });
  await assertNoOverflow(mobile.page);
  checked('mobile touch gameplay, portal win and original layouts at 390px, 320px and landscape');
  await mobile.context.close();

  const blockedSetup = LEVELS.map((level) => {
    const row = Math.floor(level.monster / level.size);
    const column = level.monster % level.size;
    const walls = availableCells(level).filter((cell) => Math.abs(Math.floor(cell / level.size) - row) <= 1 && Math.abs(cell % level.size - column) <= 1);
    return { level, walls };
  }).find(({ level, walls }) => walls.length <= level.budget && getRoute(level, walls).outcome === 'blocked');
  assert.ok(blockedSetup, 'The campaign must provide an in-budget blocked attempt for this UI regression.');
  const blockedScores = Object.fromEntries(LEVELS.filter((level) => level.id < blockedSetup.level.id).map((level) => [level.id, 3]));
  const blocked = await openPage({ storageState: { cookies: [], origins: [{ origin: new URL(baseURL).origin, localStorage: [{ name: STORAGE_KEY, value: JSON.stringify({
    rulesRevision: RULES_REVISION, level: blockedSetup.level.id, scores: blockedScores,
  }) }] }] } });
  await enterLevel(blocked.page, blockedSetup.level.id);
  for (const cell of blockedSetup.walls) await blocked.page.locator(`[data-cell="${cell}"]`).click();
  await blocked.page.locator('#start').click();
  assert.match(await blocked.page.locator('#status').textContent(), /Путь полностью закрыт/);
  assert.equal(await blocked.page.locator('#hint').isEnabled(), true);
  assert.deepEqual((await saved(blocked.page)).scores, blockedScores);
  assert.equal(await blocked.page.locator('#modal').getAttribute('open'), null);
  checked('blocking every monster exit leaves editing active and never awards a win');
  await blocked.context.close();

  const denied = await openPage({}, (deniedPage) => deniedPage.addInitScript(() => {
    Object.defineProperty(window, 'localStorage', { configurable: true, get() { throw new DOMException('Access denied', 'SecurityError'); } });
  }));
  await enterLevel(denied.page, first.id);
  assert.match(await denied.page.locator('#save-status').textContent(), /^Сохранение недоступно/);
  await placeSolution(denied.page, first);
  await denied.page.locator('#start').click();
  await finishRun(denied.page, first, first.solution);
  assert.equal(await denied.page.locator('#result-summary .result-stars').getAttribute('aria-label'), 'Получено 3 из 3 звёзд');
  await denied.page.locator('#start').click();
  assert.ok((await denied.page.locator('#level-name').textContent()).includes(LEVELS[1].name));
  checked('denied browser storage displays status and keeps the campaign playable in memory');
  await denied.context.close();

  assert.deepEqual(problems, [], 'The browser must report no uncaught errors or console errors.');
  console.log(`Browser smoke passed: ${scenarios} scenarios; all 24 campaign levels; no browser errors.`);
} finally {
  await browser.close();
}
