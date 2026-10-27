export const STORAGE_KEY = 'catch-the-monster:v2';
export function cleanSave(value = {}) {
  const scores = {};
  if (value && typeof value === 'object' && value.scores && typeof value.scores === 'object') {
    for (const [key, score] of Object.entries(value.scores)) {
      if (/^([1-9]|1[0-9]|2[0-4])$/.test(key) && Number.isInteger(score) && score >= 1 && score <= 3) scores[key] = score;
    }
  }
  const unlocked = Array.from({length: 24}, (_, i) => i + 1).find(id => !scores[id]) || 24;
  const level = Number.isInteger(value?.level) && value.level >= 1 && value.level <= unlocked ? value.level : unlocked;
  const draft = value?.draft?.level === level && Array.isArray(value.draft.walls)
    ? { level, walls: [...new Set(value.draft.walls.filter(n => Number.isInteger(n) && n >= 0 && n < 49))], hinted: value.draft.hinted === true } : null;
  return { version: 2, scores, level, unlocked, sound: value?.sound !== false, fast: value?.fast === true, draft };
}
export function loadSave(storage) {
  try { return cleanSave(JSON.parse((storage ?? globalThis.localStorage).getItem(STORAGE_KEY)) || {}); }
  catch { return cleanSave(); }
}
export function mergeProgress(current, incoming) {
  const scores = { ...cleanSave(current).scores };
  for (const [id, score] of Object.entries(cleanSave(incoming).scores)) scores[id] = Math.max(scores[id] || 0, score);
  return cleanSave({ ...current, scores });
}
export function saveGame(value, storage) {
  try {
    const target = storage ?? globalThis.localStorage;
    target.setItem(STORAGE_KEY, JSON.stringify(mergeProgress(value, loadSave(target))));
    return true;
  }
  catch { return false; }
}
