const CACHE = 'catch-monster-dev';
const FILES = ["./", "./app.js", "./art.js", "./assets/original/bloody_monster.png", "./assets/original/cage.png", "./assets/original/character.png", "./assets/original/ctm_title.png", "./assets/original/level.png", "./assets/original/level1.png", "./assets/original/level2.png", "./assets/original/level3.png", "./assets/original/level4.png", "./assets/original/level5.png", "./assets/original/level6.png", "./assets/original/level7.png", "./assets/original/level8.png", "./assets/original/monster.png", "./assets/original/no_obstacle.png", "./assets/original/obstacle.png", "./assets/original/obstacle_start.png", "./assets/original/pit.jpg", "./assets/original/start_button_background.png", "./assets/original/start_button_background_lock.png", "./assets/original/start_button_background_star.png", "./assets/original/stone.png", "./assets/original/table5.png", "./assets/original/youwin.png", "./audio.js", "./engine.js", "./index.html", "./levels.js", "./manifest.webmanifest", "./storage.js", "./styles.css"];
self.addEventListener('install', event => event.waitUntil(caches.open(CACHE).then(cache => cache.addAll(FILES)).then(() => self.skipWaiting())));
self.addEventListener('activate', event => event.waitUntil(caches.keys().then(keys => Promise.all(keys.filter(key => key.startsWith('catch-monster-') && key !== CACHE).map(key => caches.delete(key)))).then(() => self.clients.claim())));
self.addEventListener('fetch', event => {
  if (event.request.method !== 'GET' || new URL(event.request.url).origin !== self.location.origin) return;
  if (CACHE.endsWith('-dev')) {
    event.respondWith(fetch(event.request).then(response => {
      if (response.ok) { const copy = response.clone(); event.waitUntil(caches.open(CACHE).then(cache => cache.put(event.request, copy))); }
      return response;
    }).catch(() => caches.match(event.request)));
  } else {
    event.respondWith(caches.match(event.request).then(cached => cached || fetch(event.request)));
  }
});
