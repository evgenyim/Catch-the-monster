import test from 'node:test';
import assert from 'node:assert/strict';
import { availableCells, getRoute, getStars, toggleWall, validateLevel } from '../web/engine.js';
import { CHAPTERS, LEVELS } from '../web/levels.js';

const fixture = (overrides = {}) => ({
  id: 1, name: 'Test', chapter: 1, size: 3,
  monster: 0, hero: 8, trap: 2, rocks: [], budget: 3, par: 1,
  hint: '', ...overrides,
});

// Independent reference: compare an ordinary shortest path with a second
// shortest-path search that treats the pit as a wall. This does not share the
// production engine's two-state BFS or its direction tie-breaking logic.
function referenceOutcome(level, walls) {
  function distance(avoidPit) {
    const blocked = new Set([...level.rocks, ...walls, ...(avoidPit ? [level.trap] : [])]);
    const distances = new Map([[level.monster, 0]]);
    const queue = [level.monster];
    for (let index = 0; index < queue.length; index += 1) {
      const cell = queue[index];
      if (cell === level.hero) return distances.get(cell);
      const row = Math.floor(cell / level.size);
      const col = cell % level.size;
      for (let nextRow = Math.max(0, row - 1); nextRow <= Math.min(level.size - 1, row + 1); nextRow += 1) {
        for (let nextCol = Math.max(0, col - 1); nextCol <= Math.min(level.size - 1, col + 1); nextCol += 1) {
          let destination = nextRow * level.size + nextCol;
          if (destination === cell || blocked.has(destination)) continue;
          if (destination === level.portals?.[0]) destination = level.portals[1];
          else if (destination === level.portals?.[1]) destination = level.portals[0];
          if (distances.has(destination)) continue;
          distances.set(destination, distances.get(cell) + 1);
          queue.push(destination);
        }
      }
    }
    return Infinity;
  }
  const shortest = distance(false);
  if (shortest === Infinity) return 'blocked';
  return shortest < distance(true) ? 'win' : 'lose';
}

function hasSolutionUsing(level, count) {
  const cells = availableCells(level);
  function search(index, walls) {
    if (walls.length === count) return referenceOutcome(level, walls) === 'win';
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
    assert.equal(referenceOutcome(level, level.solution), 'win');
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

test('an unobstructed diagonal costs one move', () => {
  assert.deepEqual(getRoute(fixture()), { path: [0, 4, 8], outcome: 'lose' });
});

test('diagonals may pass beside or between placed walls and rocks', () => {
  const level = fixture();
  const expected = { path: [0, 4, 8], outcome: 'lose' };
  for (const walls of [[1], [3], [1, 3]]) assert.deepEqual(getRoute(level, walls), expected);
  for (const rocks of [[1], [3], [1, 3]]) assert.deepEqual(getRoute(fixture({ rocks })), expected);
  assert.deepEqual(getRoute(fixture({ rocks: [1] }), [3]), expected);
  assert.equal(getRoute(level, [4]).path.includes(4), false, 'The destination itself must still be clear.');
});

test('pit-free shortest paths win ties, even when a pit route exists', () => {
  const level = fixture({ size: 5, monster: 10, hero: 14, trap: 12, budget: 4 });
  const result = getRoute(level);
  assert.equal(result.outcome, 'lose');
  assert.equal(result.path.length, 5);
  assert.equal(result.path.includes(level.trap), false);
  assert.equal(referenceOutcome(level, []), 'lose');
  assert.deepEqual(getRoute(level, [2, 7, 17, 22]), { path: [10, 6, 12], outcome: 'win' });
});

test('screenshot regression: walls above and below the pit leave a four-move safe route', () => {
  const level = fixture({ size: 5, monster: 10, hero: 14, trap: 12 });
  assert.deepEqual(getRoute(level, [7, 17]), { path: [10, 6, 2, 8, 14], outcome: 'lose' });
  assert.equal(referenceOutcome(level, [7, 17]), 'lose');
});

test('safe paths win ties even when direction order visits the pit first', () => {
  const level = fixture({ monster: 6, hero: 2, trap: 4, rocks: [1], budget: 3 });
  assert.deepEqual(getRoute(level), { path: [6, 4], outcome: 'win' }, 'The only two-move path must catch the monster.');
  const tie = fixture({ monster: 6, hero: 2, trap: 1, rocks: [4], budget: 3 });
  assert.deepEqual(getRoute(tie), { path: [6, 7, 5, 2], outcome: 'lose' });
});

test('all 32,256 arrangements on a 3×3 board match independent shortest-path rules', () => {
  let checked = 0;
  for (let monster = 0; monster < 9; monster += 1) {
    for (let hero = 0; hero < 9; hero += 1) {
      if (hero === monster) continue;
      for (let trap = 0; trap < 9; trap += 1) {
        if (trap === monster || trap === hero) continue;
        const level = fixture({ monster, hero, trap, budget: 6 });
        const available = availableCells(level);
        for (let mask = 0; mask < 2 ** available.length; mask += 1) {
          const walls = available.filter((_, index) => mask & (1 << index));
          assert.equal(getRoute(level, walls).outcome, referenceOutcome(level, walls), JSON.stringify({ monster, hero, trap, walls }));
          checked += 1;
        }
      }
    }
  }
  assert.equal(checked, 32256);
});

test('an unreachable hero is blocked even when the pit can be reached', () => {
  const level = fixture({ trap: 6, rocks: [4, 5, 7], budget: 2 });
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
  const expected = { path: [0, 1, 5, 8], outcome: 'lose' };
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
