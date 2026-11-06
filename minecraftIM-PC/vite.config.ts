import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'node:path';

const root = resolve(__dirname, 'src/renderer');

export default defineConfig({
  root,
  // Adrenaline 通过 file:// 加载产物，必须使用相对路径，否则资源 404
  base: './',
  plugins: [vue()],
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
