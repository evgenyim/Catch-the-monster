import { LEVELS } from './levels.js';
import { getRoute, toggleWall, getStars } from './engine.js';
import { loadSave, saveGame, cleanSave, mergeProgress, STORAGE_KEY } from './storage.js';
import { art, levelTitle } from './art.js';
import { sound, setSound, suspendSound } from './audio.js';

const $ = id => document.getElementById(id);
let save = loadSave();
const showRulesUpdate = save.rulesChanged;
let level = LEVELS[save.level - 1];
let walls = [];
let history = [];
let hintUsed = false;
let hintCell = null;
let phase = 'edit';
let screen = 'menu';
let route = null;
let step = 0;
let timer = null;
let focusBeforeModal = null;
let nativeHidden = false;
const reducedMotion = globalThis.matchMedia?.('(prefers-reduced-motion: reduce)').matches;

function persist() {
  save = mergeProgress(save, loadSave());
  save.level = level.id;
  save.draft = { level: level.id, walls, hinted: hintUsed };
  const available = saveGame(save);
  $('save-status').textContent = available ? 'Прогресс сохранён' : 'Сохранение недоступно — прогресс останется только до закрытия игры.';
  return available;
}

function status(message, warning = false) {
  $('status').textContent = message;
  $('status').classList.toggle('warning', warning);
}

function setScreen(next) {
  if (next !== 'game') pause();
  screen = next;
  for (const name of ['menu', 'levels', 'game', 'exit']) $(`${name}-screen`).hidden = name !== next;
  window.scrollTo(0, 0);
}

function starsMarkup(count) {
  return [1, 2, 3].map(n => `<span class="${n <= count ? 'star-earned' : ''}">★</span>`).join('');
}

function cellLabel(cell) {
  const coordinate = `${String.fromCharCode(65 + cell % level.size)}${Math.floor(cell / level.size) + 1}`;
  const kind = cell === level.monster ? 'монстр' : cell === level.hero ? 'герой' : cell === level.trap ? 'яма' : level.rocks.includes(cell) ? 'камень' : level.portals?.includes(cell) ? 'портал' : walls.includes(cell) ? 'ваша стена, нажмите, чтобы убрать' : 'свободно, нажмите, чтобы поставить стену';
  return `${coordinate}: ${kind}`;
}

function renderBoard(focusCell) {
  if (focusCell === undefined && document.activeElement?.dataset.cell !== undefined) focusCell = Number(document.activeElement.dataset.cell);
  $('board').style.setProperty('--size', level.size);
  $('board').classList.toggle('large-board', level.size !== 5);
  $('monster-layer').style.setProperty('--size', level.size);
  $('board').innerHTML = Array.from({ length: level.size * level.size }, (_, cell) => {
    const fixed = cell === level.monster || cell === level.hero || cell === level.trap || level.rocks.includes(cell) || level.portals?.includes(cell);
    const isWall = walls.includes(cell);
    const image = cell === level.hero ? (phase === 'lost' ? '' : art.hero) : cell === level.trap ? art.trap : level.rocks.includes(cell) ? art.rock : level.portals?.includes(cell) ? art.portal : isWall ? art.wall : '';
    return `<button class="cell ${!fixed && !isWall ? 'empty' : ''} ${isWall ? 'user-wall' : ''} ${hintCell === cell ? 'hint-cell' : ''}" data-cell="${cell}" aria-label="${cellLabel(cell)}" ${fixed || phase !== 'edit' ? 'disabled' : ''} ${!fixed ? `aria-pressed="${isWall}"` : ''}>${image}${!fixed && !isWall ? `<span class="placement-ghost">${art.wall}</span>` : ''}</button>`;
  }).join('');
  $('monster-layer').innerHTML = `<div class="moving-monster" id="moving-monster">${phase === 'lost' ? art.defeated : art.monster}${phase === 'won' ? `<span class="capture-cage">${art.cage}</span>` : ''}</div>`;
  $('win-banner').hidden = phase !== 'won';
  positionMonster(route?.path[step] ?? level.monster, false);
  if (focusCell !== undefined) $('board').querySelector(`[data-cell="${focusCell}"]:enabled`)?.focus({ preventScroll: true });
}

function positionMonster(cell, animate = true) {
  const element = $('moving-monster');
  if (!element) return;
  element.style.transitionDuration = animate && !reducedMotion ? (save.fast ? '.2s' : '.4s') : '0s';
  element.style.left = `${cell % level.size / level.size * 100}%`;
  element.style.top = `${Math.floor(cell / level.size) / level.size * 100}%`;
}

function renderControls() {
  const remaining = level.budget - walls.length;
  $('wall-count').textContent = remaining;
  $('wall-tool').classList.toggle('empty', remaining === 0);
  $('wall-tool').disabled = phase !== 'edit';
  $('wall-tool').setAttribute('aria-label', `Осталось стен: ${remaining} из ${level.budget}`);
  $('undo').disabled = phase !== 'edit' || !history.length;
  $('hint').disabled = phase !== 'edit';
  $('speed').textContent = save.fast ? '2×' : '1×';
  $('speed').setAttribute('aria-label', save.fast ? 'Обычная скорость анимации' : 'Ускорить анимацию');
  $('speed').setAttribute('aria-pressed', String(save.fast));
  $('sound').textContent = save.sound ? 'SOUND ON' : 'SOUND OFF';
  $('sound').setAttribute('aria-label', save.sound ? 'Выключить звук' : 'Включить звук');
  $('sound').setAttribute('aria-pressed', String(save.sound));
  const label = phase === 'running' ? 'PAUSE' : phase === 'paused' ? 'RESUME' : phase === 'won' ? (level.id === LEVELS.length ? 'LEVELS' : 'NEXT') : phase === 'lost' ? 'RETRY' : 'START';
  $('start-label').textContent = label;
}

function renderProgress() {
  const total = Object.values(save.scores).reduce((sum, n) => sum + n, 0);
  $('total-stars').textContent = `${total} / 72 ★`;
  $('level-grid').innerHTML = LEVELS.map(item => {
    const locked = item.id > save.unlocked;
    const score = save.scores[item.id] || 0;
    return `<button class="classic-button level-choice ${locked ? 'locked' : score ? 'completed' : ''}" data-level="${item.id}" ${locked ? 'disabled' : ''} aria-label="Уровень ${item.id}: ${item.name}${locked ? ', закрыт' : `, ${score} звёзд`}">${locked ? '' : item.id}</button>`;
  }).join('');
}

function loadLevel(id, { restoreDraft = false, showGame = true } = {}) {
  if (!Number.isInteger(id) || id < 1 || id > save.unlocked || id > LEVELS.length) return;
  clearTimeout(timer);
  level = LEVELS[id - 1];
  walls = []; history = []; route = null; step = 0; phase = 'edit'; hintUsed = false; hintCell = null;
  if (restoreDraft && save.draft?.level === id) {
    for (const cell of save.draft.walls) {
      const next = toggleWall(level, walls, cell);
      if (next) walls = next;
    }
    hintUsed = save.draft.hinted;
  }
  $('level-name').textContent = `Level ${id} — ${level.name}`;
  $('level-heading').innerHTML = levelTitle(id);
  $('hint-text').hidden = true;
  $('result-summary').hidden = true;
  renderBoard(); renderControls(); renderProgress(); persist();
  status(level.portals ? 'Порталы мгновенно переносят монстра. Диагонали рядом со стенами разрешены.' : 'Нажми на клетку, чтобы поставить стену. Диагонали рядом со стенами разрешены.');
  if (showGame) setScreen('game');
}

function selectLevel(id) {
  if (id > save.unlocked || !LEVELS[id - 1]) return;
  // Keep the current attempt when returning from the map, including a restored
  // draft after reload. Explicit REPLAY starts a fresh attempt instead.
  if (id === level.id) setScreen('game');
  else loadLevel(id);
}

function openMap() {
  pause(); persist(); renderProgress(); setScreen('levels');
}

function openMenu() {
  pause(); persist(); setScreen('menu');
}

function placeWall(cell) {
  if (phase !== 'edit' || screen !== 'game') return;
  const next = toggleWall(level, walls, cell);
  if (!next) { status('Стены закончились. Убери одну нажатием или используй UNDO.', true); return; }
  history.push([...walls]);
  const removed = walls.includes(cell);
  walls = next;
  if (hintCell === cell) hintCell = null;
  sound(removed ? 'remove' : 'place');
  renderBoard(cell); renderControls(); persist();
  status(removed ? 'Стена возвращена в запас.' : 'Поставь ещё стены или нажми START. При равных путях монстр обходит яму.');
}

function undo() {
  if (phase !== 'edit' || !history.length) return;
  walls = history.pop(); hintCell = null;
  sound('remove'); renderBoard(); renderControls(); persist(); status('Последний ход отменён.');
}

function editAgain(clear = false) {
  clearTimeout(timer);
  phase = 'edit'; route = null; step = 0; hintCell = null;
  if (clear) { if (walls.length) history.push([...walls]); walls = []; }
  $('result-summary').hidden = true;
  renderBoard(); renderControls(); persist();
  status(clear ? 'Поле очищено. Расставь стены заново.' : 'Измени стены и попробуй ещё раз.');
}

function start() {
  if ($('modal').open || screen !== 'game') return;
  if (phase === 'running') { pause(); return; }
  if (phase === 'paused') {
    phase = 'running'; renderControls(); status('Монстр снова в пути.'); scheduleStep(); return;
  }
  if (phase === 'won') {
    if (level.id < LEVELS.length) loadLevel(level.id + 1);
    else openMap();
    return;
  }
  if (phase === 'lost') { editAgain(); return; }
  route = getRoute(level, walls);
  if (route.outcome === 'blocked') {
    route = null;
    status('Путь полностью закрыт. Оставь проход к герою через яму — запереть монстра недостаточно.', true);
    sound('lose'); return;
  }
  hintCell = null; phase = 'running'; step = 0;
  $('hint-text').hidden = true;
  $('result-summary').hidden = true;
  renderBoard(); renderControls(); persist();
  status('Монстр идёт по кратчайшему пути.');
  scheduleStep();
}

function scheduleStep() {
  clearTimeout(timer);
  if (phase === 'running') timer = setTimeout(tick, save.fast ? 300 : 600);
}

function tick() {
  if (phase !== 'running') return;
  if (step >= route.path.length - 1) { finish(); return; }
  const previous = route.path[step++];
  const current = route.path[step];
  const teleport = level.portals?.includes(previous) && level.portals?.includes(current);
  positionMonster(current, !teleport);
  sound(teleport ? 'hint' : 'step');
  scheduleStep();
}

function pause() {
  if (phase !== 'running') return;
  clearTimeout(timer); phase = 'paused'; renderControls(); status('Пауза. Нажми RESUME, чтобы продолжить.');
}

function finish() {
  clearTimeout(timer);
  const won = route.outcome === 'win';
  phase = won ? 'won' : 'lost';
  if (won) {
    const stars = getStars(level, walls.length, hintUsed);
    save.scores[level.id] = Math.max(save.scores[level.id] || 0, stars);
    save = cleanSave(save);
    persist(); renderProgress();
  }
  renderBoard(); renderControls();
  status(won ? 'Монстр пойман!' : 'Монстр добрался до героя. Попробуй другой маршрут.', !won);
  $('result-summary').hidden = false;
  $('result-stars').hidden = !won;
  const stars = won ? getStars(level, walls.length, hintUsed) : 0;
  $('result-stars').innerHTML = starsMarkup(stars);
  $('result-stars').setAttribute('aria-label', `Получено ${stars} из 3 звёзд`);
  $('result-message').textContent = won
    ? `${level.id === LEVELS.length ? 'Все 24 уровня пройдены! ' : ''}${hintUsed ? 'С подсказкой — максимум две звезды. ' : ''}${stars < 3 ? `Для трёх звёзд используй не больше ${level.par} стен без подсказки. ` : ''}${level.id === LEVELS.length ? 'LEVELS — выбор уровня' : 'NEXT — дальше'}, REPLAY — новая попытка.`
    : 'При равных путях монстр обходит яму. Путь через неё должен быть строго короче.';
  $('replay').textContent = won ? 'REPLAY' : 'RETRY';
  sound(won ? 'win' : 'lose');
}

function showHelp() {
  pause();
  focusBeforeModal = document.activeElement;
  $('modal-content').innerHTML = `<h2 id="modal-title">HELP</h2><h3>Stop the monster</h3><div class="help-sprites">${art.monster}${art.hero}</div><p>Монстр идёт к герою по кратчайшему пути. Все восемь направлений стоят один ход. <strong>По диагонали рядом со стеной и между стенами ходить можно</strong>: свободной должна быть только клетка назначения.</p><h3>Block him</h3><div class="help-sprites">${art.rock}${art.wall}</div><p>Нажми на свободную клетку, чтобы поставить стену. Повторное нажатие уберёт её. Число на ромбе показывает запас стен; красные камни передвигать нельзя. UNDO отменяет ход, TRY AGAIN очищает поле.</p><h3>And catch him!</h3><div class="help-sprites">${art.trap}${art.cage}</div><p><strong>При равных путях монстр выбирает маршрут без ямы.</strong> Сделай путь через яму строго короче обхода и нажми START. Полностью запереть монстра — не победа.</p><p>В поздних уровнях два портала соединены: вход стоит один шаг, перенос на выход не добавляет ход. Повторного переноса с выхода нет.</p><p>Три звезды — за минимальное число стен без HINT. Одна лишняя стена даёт две звезды, больше — одну. Подсказка ограничивает результат двумя звёздами. REPLAY начинает новую попытку, старый рекорд сохраняется.</p><p>Стрелки выбирают клетку, Enter/пробел нажимают кнопку. <kbd>R</kbd> очищает поле, <kbd>Ctrl/⌘ Z</kbd> отменяет ход, пробел вне кнопок запускает или приостанавливает монстра. При сворачивании игра ставится на паузу.</p><p>Игра работает офлайн. Результаты хранятся только на этом устройстве; если браузер запретил сохранение, игра всё равно работает до закрытия вкладки.</p><div class="help-actions"><button class="classic-button" data-action="close">BACK</button></div>`;
  if (!$('modal').open) $('modal').showModal();
  $('modal').scrollTop = 0;
}

function hint() {
  if (phase !== 'edit') return;
  hintUsed = true;
  const extraneous = walls.filter(cell => !level.solution.includes(cell));
  hintCell = extraneous[0] ?? level.solution.find(cell => !walls.includes(cell)) ?? null;
  $('hint-text').hidden = false;
  $('hint-text').textContent = `${level.hint} ${extraneous.length ? 'Сначала убери подсвеченную лишнюю стену, затем снова нажми HINT.' : hintCell !== null ? 'Подсвеченная клетка — часть одного из решений.' : 'Все нужные стены уже на месте. Нажми START.'} С подсказкой — максимум две звезды.`;
  renderBoard(); persist(); sound('hint');
  status(extraneous.length ? 'Нажми на подсвеченную стену, чтобы убрать её.' : hintCell !== null ? 'Нажми на жёлтую клетку, чтобы поставить стену.' : 'Всё готово. Проверь свой план.');
}

function exitGame() {
  pause();
  const available = persist();
  suspendSound();
  if (location.hostname === 'appassets.androidplatform.net') {
    location.assign('/game/exit');
  } else {
    $('exit-screen').querySelector('p').textContent = available ? 'Игра остановлена. Прогресс сохранён. Эту вкладку можно закрыть.' : 'Игра остановлена. Сохранение недоступно: закрытие вкладки удалит прогресс этой сессии.';
    setScreen('exit');
  }
}

function onHidden() {
  if (document.hidden || nativeHidden) { pause(); suspendSound(); persist(); }
}

$('board').addEventListener('click', event => {
  const button = event.target.closest('[data-cell]');
  if (button) placeWall(Number(button.dataset.cell));
});
$('board').addEventListener('keydown', event => {
  const direction = { ArrowLeft: -1, ArrowRight: 1, ArrowUp: -level.size, ArrowDown: level.size }[event.key];
  if (!direction || phase !== 'edit') return;
  event.preventDefault();
  const source = event.target.closest('[data-cell]');
  if (!source) return;
  let cell = Number(source.dataset.cell);
  while (cell + direction >= 0 && cell + direction < level.size * level.size) {
    if (Math.abs(direction) === 1 && Math.floor(cell / level.size) !== Math.floor((cell + direction) / level.size)) break;
    cell += direction;
    const target = $('board').querySelector(`[data-cell="${cell}"]`);
    if (!target.disabled) { target.focus(); break; }
  }
});
$('level-grid').addEventListener('click', event => {
  const button = event.target.closest('[data-level]');
  if (button && !button.disabled) selectLevel(Number(button.dataset.level));
});
$('menu-play').addEventListener('click', openMap);
$('menu-help').addEventListener('click', showHelp);
$('menu-exit').addEventListener('click', exitGame);
$('return-menu').addEventListener('click', openMenu);
$('back-menu').addEventListener('click', openMenu);
$('game-menu').addEventListener('click', openMenu);
$('start').addEventListener('click', start);
$('undo').addEventListener('click', undo);
$('reset').addEventListener('click', () => { sound('remove'); editAgain(true); });
$('wall-tool').addEventListener('click', () => status('Нажми на свободную клетку, чтобы поставить стену. Нажатие на свою стену убирает её.'));
$('replay').addEventListener('click', () => phase === 'won' ? loadLevel(level.id) : editAgain());
$('speed').addEventListener('click', () => { save.fast = !save.fast; renderControls(); persist(); });
$('sound').addEventListener('click', () => { save.sound = !save.sound; setSound(save.sound); sound('click'); renderControls(); persist(); });
$('help').addEventListener('click', showHelp);
$('hint').addEventListener('click', hint);
$('open-map').addEventListener('click', openMap);
$('close-modal').addEventListener('click', () => $('modal').close());
$('modal').addEventListener('close', () => focusBeforeModal?.focus?.({ preventScroll: true }));
$('modal').addEventListener('click', event => {
  if (event.target.closest('[data-action="close"]')) $('modal').close();
  if (event.target === $('modal')) {
    const rect = $('modal').getBoundingClientRect();
    if (event.clientX < rect.left || event.clientX > rect.right || event.clientY < rect.top || event.clientY > rect.bottom) $('modal').close();
  }
});
document.addEventListener('keydown', event => {
  if ($('modal').open || event.altKey || event.target.closest('input,textarea,select,[contenteditable]')) return;
  if (event.key === 'Escape') { event.preventDefault(); window.gameBack(); return; }
  if (screen !== 'game') return;
  if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'z') { event.preventDefault(); undo(); }
  else if (!event.ctrlKey && !event.metaKey && event.key.toLowerCase() === 'r') { event.preventDefault(); editAgain(true); }
  else if (event.code === 'Space' && !event.target.closest('button,a')) { event.preventDefault(); start(); }
});
document.addEventListener('visibilitychange', onHidden);
window.addEventListener('appvisibility', event => { nativeHidden = Boolean(event.detail?.hidden); onHidden(); });
window.addEventListener('pagehide', () => { clearTimeout(timer); persist(); suspendSound(); });
window.addEventListener('pageshow', event => { if (event.persisted && phase === 'running') pause(); });
window.addEventListener('storage', event => { if (event.key === STORAGE_KEY) { save = mergeProgress(save, loadSave()); renderProgress(); } });
window.gameBack = () => {
  if ($('modal').open) { $('modal').close(); return true; }
  if (phase === 'running') { pause(); return true; }
  if (screen === 'game') { openMap(); return true; }
  if (screen === 'levels' || screen === 'exit') { openMenu(); return true; }
  return false;
};

setSound(save.sound);
loadLevel(save.level, { restoreDraft: true, showGame: false });
$('rules-update').hidden = !showRulesUpdate;
setScreen('menu');
if ('serviceWorker' in navigator && location.hostname !== 'appassets.androidplatform.net') {
  navigator.serviceWorker.register('./sw.js').catch(() => { /* Offline caching is optional. */ });
}
