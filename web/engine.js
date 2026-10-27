/**
 * Catch the Monster — deterministic, renderer-independent puzzle rules.
 *
 * Every orthogonal or diagonal move costs one step. A diagonal is legal only
 * when BOTH orthogonal neighbors are clear: walls cannot be cut at corners.
 * Portals cost no additional step; a move records both entry and exit in path.
 * The monster chooses the shortest route to the hero. Of equally short routes
 * it prefers one without the pit, then the fixed direction order below. It does
 * not foresee that a pit ends its journey, so a strictly shorter route through
 * the pit catches it. An unreachable hero produces `blocked`, never a victory.
 */

// Clockwise from north; this order is part of the deterministic game rules.
const DIRECTIONS = Object.freeze([
  [-1, 0], [-1, 1], [0, 1], [1, 1],
  [1, 0], [1, -1], [0, -1], [-1, -1],
]);

function wallArray(walls) {
  return Array.isArray(walls) || walls instanceof Set ? [...walls] : [];
}

function inBoard(cell, size) {
  return Number.isInteger(cell) && cell >= 0 && cell < size * size;
}

/** Return validation diagnostics; no exceptions and no state mutation. */
export function validateLevel(level) {
  const errors = [];
  if (!level || typeof level !== 'object') return { valid: false, errors: ['Level must be an object.'] };
  const { size } = level;
  if (!Number.isInteger(size) || size < 3 || size > 12) {
    return { valid: false, errors: ['Board size must be an integer from 3 to 12.'] };
  }
  const occupied = new Set();
  for (const key of ['monster', 'hero', 'trap']) {
    if (!inBoard(level[key], size)) errors.push(`${key} is outside the board.`);
    else if (occupied.has(level[key])) errors.push(`${key} overlaps a protected cell.`);
    occupied.add(level[key]);
  }
  if (level.portals !== undefined && (!Array.isArray(level.portals) || level.portals.length !== 2)) {
    errors.push('Portals must be a pair of cells.');
  } else {
    for (const cell of level.portals ?? []) {
      if (!inBoard(cell, size)) errors.push('Portal is outside the board.');
      else if (occupied.has(cell)) errors.push('Portal overlaps a protected cell.');
      occupied.add(cell);
    }
  }
  if (!Array.isArray(level.rocks)) errors.push('Rocks must be an array.');
  else {
    for (const cell of level.rocks) {
      if (!inBoard(cell, size)) errors.push('Rock is outside the board.');
      else if (occupied.has(cell)) errors.push('Rock overlaps another occupied cell.');
      occupied.add(cell);
    }
  }
  if (!Number.isInteger(level.budget) || level.budget < 1 || level.budget > size * size - occupied.size) {
    errors.push('Wall budget must fit the available cells.');
  }
  if (!Number.isInteger(level.par) || level.par < 1 || level.par > level.budget) errors.push('Par must be between 1 and the wall budget.');
  if (level.solution !== undefined) {
    if (!Array.isArray(level.solution)) errors.push('Solution must be an array.');
    else {
      const unique = new Set();
      for (const cell of level.solution) {
        if (!inBoard(cell, size) || occupied.has(cell) || unique.has(cell)) errors.push('Solution contains an invalid wall.');
        unique.add(cell);
      }
      if (level.solution.length > level.budget) errors.push('Solution exceeds the wall budget.');
    }
  }
  return { valid: errors.length === 0, errors };
}

/** All cells where a player may place a wall, in reading order. */
export function availableCells(level) {
  const protectedCells = new Set([level.monster, level.hero, level.trap, ...level.rocks, ...(level.portals ?? [])]);
  return Array.from({ length: level.size * level.size }, (_, cell) => cell).filter((cell) => !protectedCells.has(cell));
}

/** Toggle a player wall. Invalid placements return null without changing walls. */
export function toggleWall(level, walls, cell) {
  const current = [...new Set(wallArray(walls))];
  if (!inBoard(cell, level.size) || !availableCells(level).includes(cell)) return null;
  if (current.includes(cell)) return current.filter((value) => value !== cell);
  if (current.length >= level.budget) return null;
  return [...current, cell];
}

/** Stars are awarded for a successful run; callers decide whether it was won. */
export function getStars(level, wallsUsed, hintUsed = false) {
  const count = typeof wallsUsed === 'number' ? wallsUsed : wallArray(wallsUsed).length;
  const stars = count <= level.par ? 3 : count <= level.par + 1 ? 2 : 1;
  return hintUsed ? Math.min(stars, 2) : stars;
}

/**
 * Calculate a complete attempt. Portal entries/exits remain consecutive in the
 * returned animation path, although teleportation adds no movement cost.
 */
export function getRoute(level, walls = []) {
  const { size, monster, hero, trap } = level;
  const total = size * size;
  const placed = [...new Set(wallArray(walls))];
  const protectedCells = new Set([monster, hero, trap, ...level.rocks, ...(level.portals ?? [])]);
  if (placed.length > level.budget || placed.some((cell) => !inBoard(cell, size) || protectedCells.has(cell))) {
    return { path: [monster], outcome: 'blocked' };
  }
  const blocked = new Set([...level.rocks, ...placed]);
  // Track pit-free and pit-crossing states separately so safe shortest paths
  // always win a tie, independent of when the pit state enters the BFS queue.
  const distances = new Int16Array(total * 2).fill(-1);
  const previous = new Int16Array(total * 2).fill(-1);
  const entrances = new Int16Array(total * 2).fill(-1);
  const queue = new Int16Array(total * 2);
  let head = 0;
  let tail = 0;
  distances[monster] = 0;
  queue[tail++] = monster;
  while (head < tail) {
    const state = queue[head++];
    const cell = state % total;
    const row = Math.floor(cell / size);
    const col = cell % size;
    for (const [dr, dc] of DIRECTIONS) {
      const nextRow = row + dr;
      const nextCol = col + dc;
      if (nextRow < 0 || nextRow >= size || nextCol < 0 || nextCol >= size) continue;
      const entry = nextRow * size + nextCol;
      if (blocked.has(entry)) continue;
      if (dr && dc && (blocked.has((row + dr) * size + col) || blocked.has(row * size + col + dc))) continue;
      let destination = entry;
      if (level.portals?.[0] === entry) destination = level.portals[1];
      else if (level.portals?.[1] === entry) destination = level.portals[0];
      const hitPit = state >= total || entry === trap || destination === trap;
      const nextState = destination + (hitPit ? total : 0);
      if (distances[nextState] !== -1) continue;
      distances[nextState] = distances[state] + 1;
      previous[nextState] = state;
      entrances[nextState] = entry;
      queue[tail++] = nextState;
    }
  }
  const safeDistance = distances[hero];
  const pitDistance = distances[hero + total];
  if (safeDistance === -1 && pitDistance === -1) return { path: [monster], outcome: 'blocked' };
  let finalState = safeDistance !== -1 && (pitDistance === -1 || safeDistance <= pitDistance) ? hero : hero + total;
  const reversed = [];
  while (finalState !== monster) {
    const destination = finalState % total;
    reversed.push(destination);
    if (entrances[finalState] !== destination) reversed.push(entrances[finalState]);
    finalState = previous[finalState];
  }
  const path = [monster, ...reversed.reverse()];
  const trapIndex = path.indexOf(trap);
  if (trapIndex !== -1) return { path: path.slice(0, trapIndex + 1), outcome: 'win' };
  return { path, outcome: 'lose' };
}
