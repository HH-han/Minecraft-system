import { build } from 'esbuild';
import { copyFile, mkdir, rm, stat } from 'node:fs/promises';
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

// WebView2 Loader DLL 需要与主进程产物一起分发
const nativeDir = resolve(projectRoot, 'dist/main/native');
await mkdir(nativeDir, { recursive: true });
await copyFile(
  resolve(projectRoot, 'vendor/native/WebView2Loader.dll'),
  resolve(nativeDir, 'WebView2Loader.dll')
).catch(() => {
  console.warn('[build-main] WebView2Loader.dll 未找到，跳过复制');
});

console.log(`[build-main] -> ${outfile}`);
