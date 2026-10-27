import { cp, mkdir, rm, readdir, readFile, writeFile, stat } from 'node:fs/promises';
import { createHash } from 'node:crypto';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
const root = fileURLToPath(new URL('../', import.meta.url));
const source = path.join(root, 'web');
const output = path.join(root, 'dist');
await rm(output, { recursive: true, force: true });
await mkdir(output, { recursive: true });
await cp(source, output, { recursive: true });
const files = await readdir(output, { recursive: true });
const digest = createHash('sha256');
for (const file of files.sort()) {
  if (file !== 'sw.js' && (await stat(path.join(output, file))).isFile()) digest.update(file).update(await readFile(path.join(output, file)));
}
const fingerprint = digest.digest('hex').slice(0, 16);
const serviceWorker = await readFile(path.join(output, 'sw.js'), 'utf8');
await writeFile(path.join(output, 'sw.js'), serviceWorker.replace('catch-monster-dev', `catch-monster-${fingerprint}`));
console.log(`Built browser game: ${output} (${files.length} files, no runtime dependencies)`);
console.log('Offline cache fingerprint:', fingerprint);
