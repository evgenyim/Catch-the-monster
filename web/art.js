// The production Android artwork from the original 2017 main/res bundle.
// Files are copied byte-for-byte; no replacement sprites or generated artwork.
const original = name => `<img src="./assets/original/${name}" alt="" draggable="false">`;
export const art = Object.freeze({
  monster: original('monster.png'),
  hero: original('character.png'),
  trap: original('pit.jpg'),
  rock: original('stone.png'),
  wall: original('obstacle.png'),
  cage: original('cage.png'),
  defeated: original('bloody_monster.png'),
  // Portals were planned but had no original asset. Keep their representation
  // geometric and separate from the unchanged legacy artwork.
  portal: '<span class="portal-tile"><span>↔</span></span>',
});

export function levelTitle(id) {
  if (id <= 8) return `<img src="./assets/original/level${id}.png" alt="" draggable="false">`;
  // Only Level 1–8 title images exist. Reuse the word from Level 1 and add the
  // new number in the matching red italic serif style, without stretching digits.
  return `<span class="level-word"><img src="./assets/original/level1.png" alt="" draggable="false"></span><span class="level-digits">${id}</span>`;
}
