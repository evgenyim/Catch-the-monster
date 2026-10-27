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
import { getRoute } from '../web/engine.js';
import { STORAGE_KEY } from '../web/storage.js';

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
  await page.waitForSelector('#board .cell');
  return { page, context };
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

async function playSolution(page, level, touch = false) {
  assert.equal(await page.locator('#level-name').textContent(), level.name);
  assert.equal(await page.locator('#board .cell').count(), level.size * level.size);
  if (level.portals) {
    assert.equal(await page.locator('#legend-portal-wrap').isVisible(), true);
    for (const cell of level.portals) assert.equal(await page.locator(`[data-cell="${cell}"]`).isDisabled(), true);
  }
  for (const cell of level.solution) await page.locator(`[data-cell="${cell}"]`)[touch ? 'tap' : 'click']();
  assert.deepEqual((await wallCells(page)).sort((a, b) => a - b), [...level.solution].sort((a, b) => a - b));
  await page.locator('#start')[touch ? 'tap' : 'click']();
  assert.equal(await page.locator('#hint').isDisabled(), true);
  assert.equal(await page.locator('#board .cell:enabled').count(), 0);
  await page.clock.runFor((getRoute(level, level.solution).path.length + 2) * 500);
  assert.equal(await page.locator('#modal').getAttribute('open'), '');
  assert.equal(await page.locator('.result-stars').getAttribute('aria-label'), 'Получено 3 из 3 звёзд');
  assert.equal((await saved(page)).scores[level.id], 3);
}

try {
  const { page, context } = await openPage();
  assert.equal(await page.locator('#level-name').textContent(), LEVELS[0].name);
  assert.equal(await page.locator('#board .cell').count(), 25);
  await assertNoOverflow(page);
  await screenshot(page, 'desktop-start');
  await page.locator('#open-map').click();
  assert.equal(await page.locator('[data-level]').count(), 24);
  assert.equal(await page.locator('[data-level]:enabled').count(), 1);
  assert.equal(await page.locator('[data-level="2"]').isDisabled(), true);
  await page.keyboard.press('Escape');
  assert.equal(await page.locator('#modal').getAttribute('open'), null);
  await page.locator('[data-cell="1"]').focus();
  await page.keyboard.press('ArrowRight');
  assert.equal(await page.evaluate(() => document.activeElement.dataset.cell), '2');
  checked('fresh game, locked map, modal Escape and keyboard board navigation');

  await page.locator('[data-cell="1"]').click();
  assert.equal(await page.locator('#wall-count').textContent(), '1 / 2');
  await page.locator('#undo').click();
  assert.deepEqual(await wallCells(page), []);
  await page.locator('[data-cell="1"]').click();
  await page.locator('#reset').click();
  assert.deepEqual(await wallCells(page), []);
  await page.locator('#undo').click();
  assert.deepEqual(await wallCells(page), [1]);
  await page.locator('#speed').click();
  await page.locator('#sound').click();
  await page.reload();
  assert.deepEqual(await wallCells(page), [1]);
  assert.equal(await page.locator('#speed').textContent(), '2×');
  assert.equal(await page.locator('#sound').getAttribute('aria-pressed'), 'false');
  assert.equal(await page.locator('#undo').isDisabled(), true);
  checked('wall placement, undo, clear/undo, persisted draft and sound/speed preferences');

  await page.locator('#reset').click();
  await page.locator('#start').click();
  await page.clock.runFor(250);
  await page.locator('#start').click();
  assert.equal(await page.locator('#start-label').textContent(), 'Продолжить');
  const pausedPosition = await page.locator('#moving-monster').getAttribute('style');
  await page.clock.runFor(5000);
  assert.equal(await page.locator('#moving-monster').getAttribute('style'), pausedPosition);
  assert.equal(await page.locator('#modal').getAttribute('open'), null);
  await page.locator('#start').click();
  await page.evaluate(() => window.dispatchEvent(new CustomEvent('appvisibility', { detail: { hidden: true } })));
  assert.equal(await page.locator('#start-label').textContent(), 'Продолжить');
  await page.evaluate(() => window.dispatchEvent(new CustomEvent('appvisibility', { detail: { hidden: false } })));
  await page.locator('#start').click();
  await page.clock.runFor(5000);
  assert.equal(await page.locator('#modal-title').textContent(), 'Он нашёл другую тропу.');
  assert.deepEqual((await saved(page)).scores, {});
  await page.locator('.primary-action[data-action="retry"]').click();
  await page.locator('[data-cell="1"]').click();
  await page.locator('[data-cell="5"]').click();
  await page.locator('#start').click();
  assert.match(await page.locator('#status').textContent(), /Путь полностью закрыт/);
  assert.equal(await page.locator('#modal').getAttribute('open'), null);
  assert.equal(await page.locator('#hint').isEnabled(), true);
  assert.deepEqual((await saved(page)).scores, {});
  checked('loss, retry, pause/resume, native background pause and blocked-path rejection');

  await page.locator('#reset').click();
  await page.locator('[data-cell="2"]').click();
  await page.locator('#hint').click();
  assert.equal(await page.locator('.hint-cell').getAttribute('data-cell'), '2');
  assert.match(await page.locator('#hint-text').textContent(), /убери/i);
  await page.locator('[data-cell="2"]').click();
  await page.locator('#hint').click();
  assert.equal(await page.locator('#hint-text').isVisible(), true);
  assert.equal(await page.locator('.hint-cell').getAttribute('data-cell'), '1');
  await page.locator('[data-cell="1"]').click();
  await page.locator('#start').click();
  await page.clock.runFor(5000);
  assert.equal(await page.locator('.result-stars').getAttribute('aria-label'), 'Получено 2 из 3 звёзд');
  assert.equal((await saved(page)).scores[1], 2);
  await page.locator('[data-action="replay"]').click();
  assert.deepEqual(await wallCells(page), []);
  assert.equal(await page.locator('#hint-text').isVisible(), false);
  assert.equal((await saved(page)).draft.hinted, false);
  await playSolution(page, LEVELS[0]);
  await page.locator('[data-action="next"]').click();
  assert.equal(await page.locator('#level-name').textContent(), LEVELS[1].name);
  await page.locator('#open-map').click();
  assert.equal(await page.locator('[data-level]:enabled').count(), 2);
  assert.equal(await page.locator('[data-level="3"]').isDisabled(), true);
  await page.locator('[data-level="1"]').click();
  assert.equal(await page.locator('#hint-text').isVisible(), false);
  checked('hints remove extras before placement, cap stars, replay independently and unlock only the next level');

  for (const level of LEVELS) {
    await playSolution(page, level);
    if (level.id === 18) await screenshot(page, 'desktop-portal-win');
    if (level.id < LEVELS.length) await page.locator('[data-action="next"]').click();
  }
  assert.equal(await page.locator('#modal-title').textContent(), 'Лес снова в безопасности!');
  assert.equal(Object.values((await saved(page)).scores).reduce((sum, score) => sum + score, 0), 72);
  await screenshot(page, 'desktop-campaign-complete');
  await page.locator('[data-action="map"]').click();
  assert.equal(await page.locator('[data-level]:enabled').count(), 24);
  assert.equal(await page.evaluate(() => window.gameBack()), true);
  assert.equal(await page.locator('#modal').getAttribute('open'), null);
  checked('all 24 levels won through UI, portal routes, score improvement, 72 stars and final unlocks');

  await page.evaluate(() => navigator.serviceWorker.ready);
  await page.reload();
  assert.equal(await page.evaluate(() => Boolean(navigator.serviceWorker.controller)), true);
  assert.equal((await saved(page)).scores[24], 3);
  await context.setOffline(true);
  await page.reload();
  assert.equal(await page.locator('#level-name').textContent(), LEVELS[23].name);
  assert.deepEqual((await wallCells(page)).sort((a, b) => a - b), [...LEVELS[23].solution].sort((a, b) => a - b));
  assert.equal(Object.keys((await saved(page)).scores).length, 24);
  await context.setOffline(false);
  checked('completed campaign and draft persist; service worker supports an offline reload');
  await context.close();

  const mobileOptions = { viewport: { width: 390, height: 844 }, isMobile: true, hasTouch: true, deviceScaleFactor: 2 };
  let mobile = await openPage(mobileOptions);
  await assertNoOverflow(mobile.page);
  await screenshot(mobile.page, 'mobile-start');
  await playSolution(mobile.page, LEVELS[0], true);
  await mobile.page.locator('[data-action="next"]').tap();
  assert.equal(await mobile.page.locator('#level-name').textContent(), LEVELS[1].name);
  await mobile.context.close();
  // A fresh context seeds prior progress before application initialization;
  // editing storage on a live page would be overwritten by its pagehide save.
  mobile = await openPage({
    ...mobileOptions,
    storageState: { cookies: [], origins: [{
      origin: new URL(baseURL).origin,
      localStorage: [{ name: STORAGE_KEY, value: JSON.stringify({
        level: 18, sound: false,
        scores: Object.fromEntries(Array.from({ length: 17 }, (_, index) => [index + 1, 3])),
      }) }],
    }] },
  });
  await assertNoOverflow(mobile.page);
  await screenshot(mobile.page, 'mobile-portals');
  await playSolution(mobile.page, LEVELS[17], true);
  await mobile.page.locator('#close-modal').tap();
  await mobile.page.setViewportSize({ width: 320, height: 740 });
  await assertNoOverflow(mobile.page);
  const smallCell = await mobile.page.locator('[data-cell="1"]').boundingBox();
  assert.ok(smallCell.width >= 24 && smallCell.height >= 24, 'Small-screen cells remain tappable.');
  await mobile.page.setViewportSize({ width: 844, height: 390 });
  await assertNoOverflow(mobile.page);
  checked('mobile touch gameplay, portal win and layouts at 390px, 320px and landscape');
  await mobile.context.close();

  const denied = await openPage({}, (deniedPage) => deniedPage.addInitScript(() => {
    Object.defineProperty(window, 'localStorage', { configurable: true, get() { throw new DOMException('Access denied', 'SecurityError'); } });
  }));
  assert.equal(await denied.page.locator('#save-status').textContent(), 'Сохранение недоступно');
  await denied.page.locator('[data-cell="1"]').click();
  await denied.page.locator('#start').click();
  await denied.page.clock.runFor(5000);
  assert.equal(await denied.page.locator('.result-stars').getAttribute('aria-label'), 'Получено 3 из 3 звёзд');
  await denied.page.locator('[data-action="next"]').click();
  assert.equal(await denied.page.locator('#level-name').textContent(), LEVELS[1].name);
  checked('denied browser storage displays status and keeps the campaign playable in memory');
  await denied.context.close();

  assert.deepEqual(problems, [], 'The browser must report no uncaught errors or console errors.');
  console.log(`Browser smoke passed: ${scenarios} scenarios; all 24 campaign levels; no browser errors.`);
} finally {
  await browser.close();
}
