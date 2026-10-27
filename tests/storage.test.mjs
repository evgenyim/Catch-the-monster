import test from 'node:test';
import assert from 'node:assert/strict';
import { cleanSave, loadSave, saveGame, mergeProgress, STORAGE_KEY } from '../web/storage.js';
test('invalid and corrupt saves recover without losing a playable first level', () => {
  assert.equal(cleanSave(null).unlocked, 1);
  assert.equal(loadSave({getItem: () => '{broken'}).level, 1);
  assert.equal(loadSave({getItem: () => {throw Error('blocked');}}).level, 1);
});
test('progress is contiguous, bounded, and records validated stars', () => {
  const save = cleanSave({scores:{1:3,2:2,4:3,25:3,3:99},level:24});
  assert.equal(save.unlocked, 3);
  assert.equal(save.level, 3);
  assert.deepEqual(save.scores,{1:3,2:2,4:3});
});
test('draft and settings round trip; unavailable storage is safe', () => {
  const data = new Map();
  const storage = {getItem:k=>data.get(k),setItem:(k,v)=>data.set(k,v)};
  assert.ok(saveGame({level:1,sound:false,draft:{level:1,walls:[1,1,-2,'a',2],hinted:true}},storage));
  assert.ok(data.has(STORAGE_KEY));
  assert.deepEqual(loadSave(storage).draft.walls,[1,2]);
  assert.equal(loadSave(storage).sound,false);
  assert.equal(saveGame({}, {setItem:()=>{throw Error('full');}}),false);
});
test('a stale tab cannot overwrite earned stars or unlocks', () => {
  const data = new Map();
  const storage = {getItem:k=>data.get(k),setItem:(k,v)=>data.set(k,v)};
  saveGame({scores:{1:3,2:2},level:3},storage);
  saveGame({scores:{1:1},level:1,sound:false},storage);
  const saved = loadSave(storage);
  assert.deepEqual(saved.scores,{1:3,2:2});
  assert.equal(saved.unlocked,3);
  assert.equal(saved.sound,false);
  assert.deepEqual(mergeProgress({scores:{1:3}},{scores:{1:2,2:3}}).scores,{1:3,2:3});
});
test('a throwing localStorage property getter is caught before access', () => {
  const original = Object.getOwnPropertyDescriptor(globalThis, 'localStorage');
  Object.defineProperty(globalThis, 'localStorage', {
    configurable: true,
    get() { throw new Error('Storage access denied by the browser'); },
  });
  try {
    assert.equal(loadSave().level, 1);
    assert.equal(loadSave().unlocked, 1);
    assert.deepEqual(loadSave().scores, {});
    assert.equal(saveGame({ level: 1 }), false);
    // Injected adapters must still work without evaluating the denied getter.
    assert.equal(loadSave({ getItem: () => '{"sound":false}' }).sound, false);
    assert.equal(saveGame({}, { setItem() {} }), true);
  } finally {
    if (original) Object.defineProperty(globalThis, 'localStorage', original);
    else delete globalThis.localStorage;
  }
});
