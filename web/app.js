import { LEVELS, CHAPTERS } from './levels.js';
import { getRoute, toggleWall, getStars } from './engine.js';
import { loadSave, saveGame, cleanSave, mergeProgress, STORAGE_KEY } from './storage.js';
import { art, icon, scenery } from './art.js';
import { sound, setSound, suspendSound } from './audio.js';

const $ = id => document.getElementById(id);
let save = loadSave();
let level = LEVELS[save.level - 1];
let walls = [];
let history = [];
let hintUsed = false;
let hintCell = null;
let phase = 'edit';
let route = null;
let step = 0;
let timer = null;
let focusBeforeModal = null;
let nativeHidden = false;
let persistenceAvailable = true;
const reducedMotion = globalThis.matchMedia?.('(prefers-reduced-motion: reduce)').matches;

function persist() {
  save = mergeProgress(save, loadSave());
  save.level = level.id;
  save.draft = {level: level.id, walls, hinted: hintUsed};
  persistenceAvailable = saveGame(save);
  $('save-status').textContent = persistenceAvailable ? 'Прогресс сохранён' : 'Сохранение недоступно';
}

function status(message, warning = false) {
  $('status').textContent = message;
  $('status').classList.toggle('warning', warning);
}

function starsMarkup(count) {
  return [1,2,3].map(n => `<span class="${n <= count ? 'star-earned' : ''}">★</span>`).join('');
}

function cellLabel(cell) {
  const place = `${String.fromCharCode(65 + cell % level.size)}${Math.floor(cell / level.size) + 1}`;
  const type = cell === level.monster ? 'монстр' : cell === level.hero ? 'путешественник' : cell === level.trap ? 'ловушка' : level.rocks.includes(cell) ? 'камень' : level.portals?.includes(cell) ? 'портал' : walls.includes(cell) ? 'ваша стена, нажмите, чтобы убрать' : 'свободно, нажмите, чтобы поставить стену';
  return `${place}: ${type}`;
}

function renderBoard(focusCell) {
  if (focusCell === undefined && document.activeElement?.dataset.cell !== undefined) focusCell = Number(document.activeElement.dataset.cell);
  $('board').style.setProperty('--size', level.size);
  $('monster-layer').style.setProperty('--size', level.size);
  $('board').innerHTML = Array.from({length: level.size * level.size}, (_, cell) => {
    const fixed = cell === level.monster || cell === level.hero || cell === level.trap || level.rocks.includes(cell) || level.portals?.includes(cell);
    const isWall = walls.includes(cell);
    const image = cell === level.hero ? art.hero : cell === level.trap ? art.trap : level.rocks.includes(cell) ? art.rock : level.portals?.includes(cell) ? art.portal : isWall ? art.wall : '';
    const path = route && step > 0 && route.path.slice(0, step + 1).includes(cell);
    return `<button class="cell ${!fixed && !isWall ? 'empty' : ''} ${isWall ? 'user-wall' : ''} ${hintCell === cell ? 'hint-cell' : ''} ${path ? 'path-cell' : ''}" data-cell="${cell}" aria-label="${cellLabel(cell)}" ${fixed || phase !== 'edit' ? 'disabled' : ''} ${!fixed ? `aria-pressed="${isWall}"` : ''}>${image}${!fixed && !isWall ? `<span class="placement-ghost">${art.wall}</span>` : ''}</button>`;
  }).join('');
  $('monster-layer').innerHTML = `<div class="moving-monster ${phase === 'edit' ? 'idle' : ''} ${phase === 'won' ? 'caught' : ''}" id="moving-monster">${art.monster}</div>`;
  positionMonster(route?.path[step] ?? level.monster, false);
  if (focusCell !== undefined) $('board').querySelector(`[data-cell="${focusCell}"]`)?.focus({preventScroll:true});
}

function positionMonster(cell, animate = true) {
  const element = $('moving-monster');
  if (!element) return;
  element.style.transitionDuration = animate && !reducedMotion ? (save.fast ? '.19s' : '.38s') : '0s';
  element.style.left = `${(cell % level.size) / level.size * 100}%`;
  element.style.top = `${Math.floor(cell / level.size) / level.size * 100}%`;
}

function renderControls() {
  $('wall-count').textContent = `${level.budget - walls.length} / ${level.budget}`;
  $('wall-dots').innerHTML = Array.from({length:level.budget}, (_,i) => `<i class="${i >= level.budget - walls.length ? 'used' : ''}"></i>`).join('');
  $('undo').disabled = phase !== 'edit' || history.length === 0;
  $('hint').disabled = phase !== 'edit';
  $('speed').textContent = save.fast ? '2×' : '1×';
  $('speed').setAttribute('aria-label', save.fast ? 'Обычная скорость анимации' : 'Ускорить анимацию');
  $('speed').setAttribute('aria-pressed', String(save.fast));
  const playing = phase === 'running';
  const label = playing ? 'Пауза' : phase === 'paused' ? 'Продолжить' : phase === 'won' || phase === 'lost' ? 'Изменить план' : 'Выпустить монстра';
  $('start-label').textContent = label;
  $('start-icon').innerHTML = icon(playing ? 'pause' : 'play');
  $('sound').innerHTML = icon(save.sound ? 'sound' : 'muted');
  $('sound').setAttribute('aria-label', save.sound ? 'Выключить звук' : 'Включить звук');
  $('sound').setAttribute('aria-pressed', String(save.sound));
}

function renderProgress() {
  const total = Object.values(save.scores).reduce((sum, score) => sum + score, 0);
  $('total-stars').innerHTML = `${total} <span>/ 72 звезды</span>`;
  $('journey-fill').style.width = `${total / 72 * 100}%`;
  $('level-stars').innerHTML = starsMarkup(save.scores[level.id] || 0);
  $('level-stars').setAttribute('aria-label', `Рекорд: ${save.scores[level.id] || 0} из 3 звёзд`);
}

function loadLevel(id, restoreDraft = false) {
  if (id < 1 || id > save.unlocked || id > LEVELS.length) return;
  clearTimeout(timer);
  level = LEVELS[id - 1];
  walls = []; history = []; route = null; step = 0; phase = 'edit'; hintUsed = false; hintCell = null;
  if (restoreDraft && save.draft?.level === id) {
    for (const cell of save.draft.walls) {
      const updated = toggleWall(level, walls, cell);
      if (updated) walls = updated;
    }
    hintUsed = save.draft.hinted;
  }
  $('chapter-name').textContent = `Глава ${level.chapter} · ${CHAPTERS[level.chapter - 1].name}`;
  $('level-name').textContent = level.name;
  $('level-number').textContent = `${String(level.id).padStart(2,'0')} / 24`;
  $('coordinates').innerHTML = Array.from({length:level.size}, (_,i) => `<span>${String.fromCharCode(65 + i)}</span>`).join('');
  $('hint-text').hidden = true;
  $('legend-portal-wrap').hidden = !level.portals;
  document.querySelector('.play-panel').classList.toggle('portal-level', Boolean(level.portals));
  $('mission-copy').textContent = level.portals ? 'Порталы соединяют две клетки. Учти этот короткий путь, когда будешь расставлять стены.' : 'Расставь стены так, чтобы кратчайший путь к путешественнику прошёл через ловушку.';
  renderBoard(); renderControls(); renderProgress(); persist();
  status(level.id === 1 ? 'Начни с верхней тропы. Нажми на клетку, чтобы поставить стену.' : level.portals ? 'Вход в портал мгновенно переносит монстра ко второму порталу.' : `Три звезды: не больше ${level.par} ${wallWord(level.par)}, без подсказок.`);
}

function wallWord(count) { return count === 1 ? 'стены' : 'стен'; }

function placeWall(cell) {
  if (phase !== 'edit') return;
  const next = toggleWall(level, walls, cell);
  if (!next) { status('Стены закончились. Убери одну нажатием или отмени последний ход.', true); return; }
  history.push([...walls]);
  const removed = walls.includes(cell);
  walls = next;
  if (hintCell === cell) hintCell = null;
  sound(removed ? 'remove' : 'place');
  renderBoard(cell); renderControls(); persist();
  status(removed ? 'Стена возвращена в запас.' : walls.length === level.budget ? 'План готов? Выпускай монстра. Стены ещё можно убрать.' : 'Можно поставить ещё стену или выпустить монстра.');
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
  renderBoard(); renderControls(); persist();
  status(clear ? 'Поле очищено. Придумай новый маршрут.' : 'Измени стены и попробуй ещё раз.');
}

function start() {
  if ($('modal').open) return;
  if (phase === 'running') { pause(); return; }
  if (phase === 'paused') {
    phase = 'running'; renderControls(); status('Монстр снова в пути…'); scheduleStep(); return;
  }
  if (phase === 'won' || phase === 'lost') { editAgain(); return; }
  route = getRoute(level, walls);
  if (route.outcome === 'blocked') {
    route = null;
    status('Путь полностью закрыт. Оставь проход к герою через ловушку — запереть монстра недостаточно.', true);
    sound('lose'); return;
  }
  hintCell = null; phase = 'running'; step = 0;
  renderBoard(); renderControls(); persist();
  status('Тс-с… Монстр идёт по кратчайшему пути.');
  scheduleStep();
}

function scheduleStep() {
  clearTimeout(timer);
  if (phase === 'running') timer = setTimeout(tick, save.fast ? 240 : 480);
}

function tick() {
  if (phase !== 'running') return;
  if (step >= route.path.length - 1) { finish(); return; }
  const previous = route.path[step];
  step += 1;
  const current = route.path[step];
  const teleport = level.portals?.includes(previous) && level.portals?.includes(current);
  positionMonster(current, !teleport);
  $('board').querySelector(`[data-cell="${previous}"]`)?.classList.add('path-cell');
  sound(teleport ? 'hint' : 'step');
  scheduleStep();
}

function pause() {
  if (phase !== 'running') return;
  clearTimeout(timer); phase = 'paused'; renderControls(); status('Пауза. Твой план подождёт.');
}

function finish() {
  clearTimeout(timer);
  const won = route.outcome === 'win';
  phase = won ? 'won' : 'lost';
  if (won) {
    const stars = getStars(level, walls.length, hintUsed);
    save.scores[level.id] = Math.max(save.scores[level.id] || 0, stars);
    save = cleanSave(save);
    $('moving-monster').classList.add('caught');
    persist(); renderProgress();
  }
  renderControls();
  status(won ? 'Монстр в ловушке. Путешественник спасён!' : 'Монстр добрался до героя. Попробуй изменить его маршрут.', !won);
  sound(won ? 'win' : 'lose');
  showResult(won);
}

function showModal(type, markup) {
  pause();
  focusBeforeModal = document.activeElement;
  $('modal-content').innerHTML = markup;
  if (!$('modal').open) $('modal').showModal();
  $('modal').scrollTop = 0;
}

function closeModal() {
  $('modal').close();
}

function openMap() {
  showModal('map', `<p class="modal-eyebrow">ТВОЁ ПРИКЛЮЧЕНИЕ</p><h2 id="modal-title">У каждого леса — свои секреты.</h2><p>24 маленьких испытания. Одна большая история.</p>${CHAPTERS.map(chapter => `<section class="chapter-section"><h3>0${chapter.id} / ${chapter.name}<span>${chapter.subtitle}</span></h3><div class="level-grid">${LEVELS.filter(item => item.chapter === chapter.id).map(item => `<button class="level-choice ${item.id === level.id ? 'current' : ''}" data-level="${item.id}" ${item.id > save.unlocked ? 'disabled' : ''} aria-label="Уровень ${item.id}: ${item.name}${item.id > save.unlocked ? ', закрыт' : `, ${save.scores[item.id] || 0} звёзд`}">${item.id > save.unlocked ? icon('lock') : item.id}<span>${item.id > save.unlocked ? '—' : ['★','★','★'].map((star,i) => `<span style="opacity:${i < (save.scores[item.id] || 0) ? 1 : .25}">${star}</span>`).join('')}</span></button>`).join('')}</div></section>`).join('')}<p class="modal-note">Победа открывает следующий уровень. Вернись на знакомую тропу, чтобы улучшить результат: три звезды дают за экономный план без подсказок.</p>`);
}

function help() {
  showModal('help', `<p class="modal-eyebrow">ПАМЯТКА ПУТЕШЕСТВЕННИКА</p><h2 id="modal-title">Хитрость сильнее скорости.</h2><p>Герой заблудился, а любопытный монстр уже взял след. Твоя задача — проложить ему дорогу в ловушку.</p><ol class="help-list"><li><strong>Сначала составь план.</strong> Нажимай на свободные клетки, чтобы поставить стены. Нажми на свою стену ещё раз, чтобы вернуть её в запас.</li><li><strong>Монстр выбирает кратчайший путь.</strong> Он ходит на одну клетку по прямой и по диагонали. Через угол стены или камня пройти нельзя. При равной длине он предпочитает путь без ловушки.</li><li><strong>Путь через ловушку должен быть короче.</strong> Полная блокировка не считается победой: нужно именно поймать монстра.</li><li><strong>Порталы работают в обе стороны.</strong> Шаг на один портал сразу переносит ко второму без дополнительного хода.</li><li><strong>Звёзды — за красивое решение.</strong> Три звезды: не больше указанного числа стен и без подсказки. Подсказки ограничивают результат двумя звёздами в этой попытке. Открой уровень заново на карте, чтобы попробовать самостоятельно.</li></ol><div class="keyboard-help"><span><kbd>R</kbd>Очистить поле</span><span><kbd>Ctrl/⌘ Z</kbd>Отменить</span><span><kbd>Пробел</kbd>Старт / пауза</span><span><kbd>← ↑ ↓ →</kbd>Выбрать клетку</span></div><p class="modal-note">Нет таймера и ограничений на попытки. Прогресс сохраняется на этом устройстве. После первого открытия браузерная игра работает офлайн; Android работает офлайн сразу.</p><div class="modal-actions"><button class="primary-action" data-action="close">Всё понятно. В лес!</button></div>`);
}

function hint() {
  if (phase !== 'edit') return;
  hintUsed = true;
  const extraneous = walls.filter(cell => !level.solution.includes(cell));
  hintCell = extraneous[0] ?? level.solution.find(cell => !walls.includes(cell)) ?? null;
  $('hint-text').hidden = false;
  $('hint-text').textContent = `${level.hint} ${extraneous.length ? 'Сначала убери подсвеченную лишнюю стену, затем нажми подсказку ещё раз.' : hintCell !== null ? 'Подсвеченная клетка — часть одного из решений.' : 'Все нужные стены уже на месте. Выпускай монстра!'} С подсказкой — максимум две звезды.`;
  renderBoard(); persist(); sound('hint');
  status(extraneous.length ? 'Нажми на золотую стену, чтобы убрать её и открыть проход.' : hintCell !== null ? 'Посмотри на золотую клетку. Нажми на неё, чтобы поставить стену.' : 'Всё готово. Проверь свой план.');
}

function showResult(won) {
  const stars = won ? getStars(level, walls.length, hintUsed) : 0;
  const complete = won && level.id === LEVELS.length;
  const total = Object.values(save.scores).reduce((sum, n) => sum + n, 0);
  const title = complete ? 'Лес снова в безопасности!' : won ? 'Ловко придумано!' : 'Он нашёл другую тропу.';
  const message = won ? complete ? 'Все 24 испытания позади. Возвращайся на любимые уровни и собери полную коллекцию звёзд.' : stars === 3 ? 'Монстр в ловушке, путешественник цел. И ни одной лишней стены.' : 'Путешественник спасён! Можно отправиться дальше или найти более экономный путь.' : 'Путешественник успел спрятаться. Измени расположение стен, чтобы путь через ловушку стал самым коротким.';
  showModal('result', `<div class="result-view"><div class="result-art">${won ? art.trap : art.monster}</div><p class="modal-eyebrow">${complete ? 'ПРИКЛЮЧЕНИЕ ПРОЙДЕНО' : `УРОВЕНЬ ${String(level.id).padStart(2,'0')} · ${won ? 'ПРОЙДЕН' : 'ПОПРОБУЕМ ЕЩЁ'}`}</p><h2 id="modal-title">${title}</h2>${won ? `<div class="result-stars" aria-label="Получено ${stars} из 3 звёзд">${starsMarkup(stars)}</div>` : ''}<p>${message}</p>${won ? `<div class="result-meta"><span><strong>${walls.length}</strong>стен использовано</span><span><strong>${total} / 72</strong>звёзд собрано</span></div>${hintUsed ? '<p class="achievement">С подсказкой: максимум 2 звезды. Рекорд уровня сохранён.</p>' : ''}${[8,16,24].includes(level.id) ? '<p class="achievement">✦ Ещё одна глава твоего приключения завершена.</p>' : ''}` : '<p class="modal-note">Совет: при равной длине монстр обходит ловушку. Перекрой этот обход.</p>'}<div class="modal-actions"><button class="secondary-action" data-action="${won ? 'replay' : 'retry'}">${won ? 'Улучшить результат' : 'Изменить план'}</button><button class="primary-action" data-action="${won ? complete ? 'map' : 'next' : 'retry'}">${complete ? 'Карта приключения' : won ? 'Следующая тропа →' : 'Попробовать ещё'}</button></div></div>`);
  if (won) confetti();
}

function confetti() {
  if (reducedMotion) return;
  for (let i = 0; i < 24; i++) {
    const bit = document.createElement('i');
    bit.className = 'confetti';
    bit.style.setProperty('--x', `${(i * 37) % 100}vw`);
    bit.style.setProperty('--delay', `${(i % 6) * .1}s`);
    bit.style.setProperty('--color', ['#91a96b','#dca159','#c4cda1','#d78862'][i % 4]);
    document.body.append(bit); setTimeout(() => bit.remove(), 3500);
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
  const direction = {ArrowLeft:-1,ArrowRight:1,ArrowUp:-level.size,ArrowDown:level.size}[event.key];
  if (!direction || phase !== 'edit') return;
  event.preventDefault();
  let cell = Number(event.target.closest('[data-cell]')?.dataset.cell);
  if (!Number.isInteger(cell)) return;
  while (cell + direction >= 0 && cell + direction < level.size * level.size) {
    if (Math.abs(direction) === 1 && Math.floor(cell / level.size) !== Math.floor((cell + direction) / level.size)) break;
    cell += direction;
    const target = $('board').querySelector(`[data-cell="${cell}"]`);
    if (!target.disabled) { target.focus(); break; }
  }
});
$('start').addEventListener('click', start);
$('undo').addEventListener('click', undo);
$('reset').addEventListener('click', () => { sound('remove'); editAgain(true); });
$('speed').addEventListener('click', () => { save.fast = !save.fast; renderControls(); persist(); });
$('sound').addEventListener('click', () => { save.sound = !save.sound; setSound(save.sound); sound('click'); renderControls(); persist(); });
$('help').addEventListener('click', help);
$('hint').addEventListener('click', hint);
$('open-map').addEventListener('click', openMap);
document.querySelector('.brand').addEventListener('click', event => {event.preventDefault();openMap();});
$('about').addEventListener('click', () => showModal('about', `<p class="modal-eyebrow">CATCH THE MONSTER · 2.0</p><h2 id="modal-title">Старый замысел.<br>Новое приключение.</h2><p>Эта игра выросла из Android-проекта 2017 года: герой, монстр, поле и поиск кратчайшего пути. Мы сохранили идею и собрали новую кампанию из 24 головоломок.</p><p>Без рекламы, покупок и регистрации. Все результаты хранятся только на твоём устройстве. Звук можно отключить кнопкой вверху.</p><p class="modal-note">Сделай паузу, попробуй другой маршрут и не бойся ошибаться. У леса всегда есть ещё одна тропа.</p><div class="modal-actions"><button class="primary-action" data-action="close">Вернуться в лес</button></div>`));
$('close-modal').addEventListener('click', closeModal);
$('modal').addEventListener('close', () => { focusBeforeModal?.focus?.({preventScroll:true}); });
$('modal').addEventListener('click', event => {
  const levelButton = event.target.closest('[data-level]');
  if (levelButton && !levelButton.disabled) { closeModal(); loadLevel(Number(levelButton.dataset.level)); return; }
  const action = event.target.closest('[data-action]')?.dataset.action;
  if (action === 'close') closeModal();
  if (action === 'next') {closeModal(); loadLevel(level.id + 1);}
  if (action === 'retry') {closeModal(); editAgain();}
  if (action === 'replay') {closeModal(); loadLevel(level.id);}
  if (action === 'map') {closeModal(); openMap();}
  if (event.target === $('modal')) {
    const rect = $('modal').getBoundingClientRect();
    if (event.clientX < rect.left || event.clientX > rect.right || event.clientY < rect.top || event.clientY > rect.bottom) closeModal();
  }
});
document.addEventListener('keydown', event => {
  if ($('modal').open || event.altKey || event.target.closest('input,textarea,select,[contenteditable]')) return;
  if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'z') {event.preventDefault();undo();}
  else if (!event.ctrlKey && !event.metaKey && event.key.toLowerCase() === 'r') {event.preventDefault();editAgain(true);}
  else if (event.code === 'Space' && !event.target.closest('button,a')) {event.preventDefault();start();}
});
document.addEventListener('visibilitychange', onHidden);
window.addEventListener('appvisibility', event => { nativeHidden = Boolean(event.detail?.hidden); onHidden(); });
window.addEventListener('pagehide', () => {clearTimeout(timer);persist();suspendSound();});
window.addEventListener('pageshow', event => { if (event.persisted && phase === 'running') pause(); });
window.addEventListener('storage', event => {
  if (event.key === STORAGE_KEY) { save = mergeProgress(save, loadSave()); renderProgress(); }
});
window.gameBack = () => {
  if ($('modal').open) {closeModal();return true;}
  if (phase === 'running') {pause();return true;}
  return false;
};

$('brand-icon').innerHTML = art.leaf;
$('help').innerHTML = icon('help');
$('map-icon').innerHTML = icon('map');
$('undo').innerHTML = icon('undo');
$('reset').innerHTML = icon('reset');
$('hint-icon').innerHTML = icon('hint');
$('wall-icon').innerHTML = art.wall;
$('character-scene').innerHTML = scenery();
for (const name of ['hero','monster','trap','rock','portal']) $(`legend-${name}`).innerHTML = art[name];
setSound(save.sound);
loadLevel(save.level, true);
if ('serviceWorker' in navigator && location.hostname !== 'appassets.androidplatform.net') {
  navigator.serviceWorker.register('./sw.js').catch(() => { /* Offline caching is optional; gameplay stays available. */ });
}
