import { build } from 'esbuild';
import { rm } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import { dirname, resolve } from 'node:path';

const projectRoot = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const outfile = resolve(projectRoot, 'dist/main/index.js');

await rm(resolve(projectRoot, 'dist/main'), { recursive: true, force: true });

await build({
  entryPoints: [resolve(projectRoot, 'src/main/index.ts')],
  outfile,
  bundle: true,
  platform: 'node',
  format: 'cjs',
  target: 'node20',
  // 原生模块保持外置，由 node_modules 解析 .node 产物
  external: ['adrenaline-js'],
  sourcemap: false,
  logLevel: 'info'
});

console.log(`[build-main] -> ${outfile}`);
