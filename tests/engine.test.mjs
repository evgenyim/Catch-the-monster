import test from 'node:test';
import assert from 'node:assert/strict';
import { availableCells, getRoute, getStars, toggleWall, validateLevel } from '../web/engine.js';
import { CHAPTERS, LEVELS } from '../web/levels.js';

const fixture = (overrides = {}) => ({
  id: 1, name: 'Test', chapter: 1, size: 3,
  monster: 0, hero: 8, trap: 2, rocks: [], budget: 3, par: 1,
  hint: '', ...overrides,
});

function hasSolutionUsing(level, count) {
  const cells = availableCells(level);
  function search(index, walls) {
    if (walls.length === count) return getRoute(level, walls).outcome === 'win';
    for (let next = index; next <= cells.length - (count - walls.length); next += 1) {
      if (search(next + 1, [...walls, cells[next]])) return true;
    }
    return false;
  }
  return search(0, []);
}

test('campaign has 24 valid, progressively sized puzzles across three chapters', () => {
  assert.equal(LEVELS.length, 24);
  assert.equal(CHAPTERS.length, 3);
  assert.deepEqual(LEVELS.map((level) => level.id), Array.from({ length: 24 }, (_, i) => i + 1));
  for (const level of LEVELS) {
    assert.deepEqual(validateLevel(level), { valid: true, errors: [] }, `level ${level.id}`);
    assert.equal(level.chapter, Math.ceil(level.id / 8));
    assert.equal(level.size, level.chapter + 4);
    assert.ok(level.hint.length > 20);
    assert.equal(typeof level.name, 'string');
    assert.ok(Object.isFrozen(level));
    assert.ok(Object.isFrozen(level.rocks));
    assert.ok(Object.isFrozen(level.solution));
  }
});

for (const level of LEVELS) {
  test(`level ${level.id}: solution wins, empty board loses, and par is minimal`, () => {
    assert.equal(getRoute(level, []).outcome, 'lose', 'A puzzle must require player input.');
    const result = getRoute(level, level.solution);
    assert.equal(result.outcome, 'win');
    assert.equal(result.path[0], level.monster);
    assert.equal(result.path.at(-1), level.trap);
    assert.equal(result.path.includes(level.hero), false);
    assert.equal(getStars(level, level.solution.length, false), 3);
    for (let count = 1; count < level.par; count += 1) {
      assert.equal(hasSolutionUsing(level, count), false, `Cannot beat par ${level.par} with ${count} walls.`);
    }
    if (level.portals) {
      assert.ok(result.path.includes(level.portals[0]), 'Portal puzzles must use the portal in their intended solution.');
      assert.ok(result.path.includes(level.portals[1]));
    }
  });
}

test('shortest path uses diagonals when both adjacent edges are clear', () => {
  assert.deepEqual(getRoute(fixture()), { path: [0, 4, 8], outcome: 'lose' });
});

test('a diagonal cannot cut the corner of either a placed wall or a rock', () => {
  const level = fixture();
  assert.equal(getRoute(level, [1]).path[1], 3);
  assert.equal(getRoute(level, [3]).path[1], 1);
  assert.deepEqual(getRoute(level, [1, 3]), { path: [0], outcome: 'blocked' });
  assert.equal(getRoute(fixture({ rocks: [1] }), []).path[1], 3);
  assert.deepEqual(getRoute(fixture({ rocks: [1, 3] }), []), { path: [0], outcome: 'blocked' });
});

test('pit-free shortest paths win ties, even when a pit route exists', () => {
  const level = fixture({ size: 5, monster: 10, hero: 14, trap: 12 });
  const result = getRoute(level);
  assert.equal(result.outcome, 'lose');
  assert.equal(result.path.length, 5);
  assert.equal(result.path.includes(level.trap), false);
  assert.deepEqual(getRoute(level, [6, 16]), { path: [10, 11, 12], outcome: 'win' });
});

test('an unreachable hero is blocked even when the pit can be reached', () => {
  const level = fixture({ trap: 6, rocks: [5, 7], budget: 2 });
  assert.deepEqual(getRoute(level), { path: [0], outcome: 'blocked' });
});

test('portal entry costs one move, exit is instantaneous, and no automatic bounce occurs', () => {
  const level = fixture({ size: 5, monster: 0, hero: 24, trap: 12, portals: [1, 23] });
  assert.deepEqual(getRoute(level), { path: [0, 1, 23, 24], outcome: 'lose' });
  const reverse = { ...level, monster: 24, hero: 0 };
  assert.deepEqual(getRoute(reverse), { path: [24, 23, 1, 0], outcome: 'lose' });
});

test('the journey ends immediately at a pit after a portal', () => {
  const level = fixture({ size: 5, monster: 0, hero: 24, trap: 18, portals: [1, 12] });
  assert.deepEqual(getRoute(level), { path: [0, 1, 12, 18], outcome: 'win' });
});

test('equally short safe routes use a deterministic direction order', () => {
  const level = fixture({ rocks: [4], trap: 6 });
  const expected = { path: [0, 1, 2, 5, 8], outcome: 'lose' };
  for (let attempt = 0; attempt < 20; attempt += 1) assert.deepEqual(getRoute(level), expected);
});

test('wall controls protect characters, the pit, rocks and portals', () => {
  const level = fixture({ size: 5, hero: 24, trap: 12, rocks: [3], portals: [1, 23], budget: 2 });
  for (const cell of [0, 24, 12, 3, 1, 23, -1, 25, 1.5, NaN]) {
    assert.equal(toggleWall(level, [], cell), null);
  }
  const walls = Object.freeze([6]);
  assert.deepEqual(toggleWall(level, walls, 7), [6, 7]);
  assert.deepEqual(walls, [6]);
  assert.deepEqual(toggleWall(level, new Set([6, 7]), 6), [7]);
  assert.equal(toggleWall(level, [6, 7], 8), null);
  assert.deepEqual(availableCells(level).filter((cell) => [0, 24, 12, 3, 1, 23].includes(cell)), []);
});

test('route rejects impossible placements and respects the wall budget', () => {
  const level = fixture({ budget: 1 });
  for (const walls of [[0], [8], [2], [-1], [9], [1.5], [1, 3]]) {
    assert.equal(getRoute(level, walls).outcome, 'blocked');
  }
  assert.equal(getRoute(fixture({ rocks: [4] }), [4]).outcome, 'blocked');
  assert.equal(getRoute(fixture({ size: 5, hero: 24, portals: [1, 23] }), [1]).outcome, 'blocked');
});

test('routing accepts sets and does not modify frozen input', () => {
  const level = LEVELS[2];
  const walls = Object.freeze([...level.solution]);
  assert.deepEqual(getRoute(level, walls), getRoute(level, new Set(walls)));
  assert.deepEqual(walls, level.solution);
});

test('stars reward fewer walls and acknowledge hint use', () => {
  const level = fixture({ par: 2 });
  assert.equal(getStars(level, 2), 3);
  assert.equal(getStars(level, 3), 2);
  assert.equal(getStars(level, 4), 1);
  assert.equal(getStars(level, 2, true), 2);
  assert.equal(getStars(level, 4, true), 1);
  assert.equal(getStars(level, new Set([1, 3])), 3);
});

test('validation reports malformed board definitions', () => {
  for (const level of [
    null,
    fixture({ size: 0 }),
    fixture({ monster: -1 }),
    fixture({ hero: 0 }),
    fixture({ trap: 9 }),
    fixture({ rocks: [4, 4] }),
    fixture({ rocks: [2] }),
    fixture({ rocks: null }),
    fixture({ portals: [1] }),
    fixture({ portals: [1, 1] }),
    fixture({ portals: [0, 1] }),
    fixture({ portals: [1, 12] }),
    fixture({ budget: 20 }),
    fixture({ par: 4 }),
    fixture({ solution: [0] }),
    fixture({ solution: [1, 1] }),
    fixture({ solution: [1, 3], budget: 1 }),
  ]) {
    const result = validateLevel(level);
    assert.equal(result.valid, false, JSON.stringify(level));
    assert.ok(result.errors.length > 0);
  }
});
