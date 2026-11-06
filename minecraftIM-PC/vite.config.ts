import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { viteSingleFile } from 'vite-plugin-singlefile';
import { resolve } from 'node:path';

const root = resolve(__dirname, 'src/renderer');

export default defineConfig({
  root,
  // Adrenaline 通过 file:// 加载产物，必须使用相对路径，否则资源 404
  base: './',
  // WebView2 在 file:// 下受 CORS 限制，外部 ES module 会被拦截，
  // 因此将 JS/CSS 全部内联进单个 index.html
  plugins: [vue(), viteSingleFile()],
  resolve: {
    alias: {
      '@': root,
      '@shared': resolve(__dirname, 'src/shared')
    }
  },
  server: {
    port: 5178,
    host: '0.0.0.0'
  },
  build: {
    outDir: resolve(__dirname, 'dist/renderer'),
    emptyOutDir: true,
    target: 'es2020',
    sourcemap: false
  }
});
