const CACHE_NAME = 'bier-chiller-v1.1';
const ASSETS = [
  './',
  './index.html',
  './manifest.json',
  './icon-192.png',
  './icon-512.png'
];

// Installiert den Service Worker und speichert die App im Cache
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      console.log('Dateien werden gecached');
      return cache.addAll(ASSETS);
    })
  );
});

// Liefert die Dateien aus dem Cache, wenn keine Internetverbindung besteht
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request).then(response => {
      return response || fetch(event.request);
    })
  );
});

// Löscht alte Caches, falls du später ein Update machst
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys => {
      return Promise.all(
        keys.filter(key => key !== CACHE_NAME).map(key => caches.delete(key))
      );
    })
  );
});