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

// 复制主进程资源（窗口图标等）
const assetsSrc = resolve(projectRoot, 'src/main/assets');
const assetsDst = resolve(projectRoot, 'dist/main/assets');
try {
  await stat(assetsSrc);
  const { cp } = await import('node:fs/promises');
  await cp(assetsSrc, assetsDst, { recursive: true });
  console.log('[build-main] assets copied -> dist/main/assets');
} catch {
  console.warn('[build-main] src/main/assets 不存在，跳过资源复制');
}

console.log(`[build-main] -> ${outfile}`);
