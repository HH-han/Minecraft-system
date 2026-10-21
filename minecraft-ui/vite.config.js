import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import { extraAppConfigPlugin } from './build/plugins/extra-app-config'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const root = __dirname

  return {
    plugins: [vue(), extraAppConfigPlugin({ mode, root })],
    resolve: {
      alias: {
        '@': resolve(root, 'src')
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern-compiler',
          additionalData: `@use "@/assets/styles/variables.scss" as *;`
        }
      }
    },
    server: {
      port: 8090,
      host: '0.0.0.0',
      open: true,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true
        },
        '/ws': {
          target: 'ws://localhost:8080',
          ws: true
        }
      }
    },
    preview: {
      port: 8091,
      host: '0.0.0.0'
    },
    build: {
      outDir: 'dist',
      // 构建前清空输出目录，避免历史产物残留
      emptyOutDir: true,
      // 生产包不输出 sourcemap，避免源码泄露
      sourcemap: false,
      // 移除调试器语句
      minify: 'esbuild',
      target: 'es2020',
      chunkSizeWarningLimit: 2000,
      rollupOptions: {
        output: {
          // 产物目录规范：入口 jse/、异步 chunk js/、样式与静态资源按扩展名归档
          entryFileNames: 'jse/index-[name]-[hash].js',
          chunkFileNames: 'js/[name]-[hash].js',
          assetFileNames: '[ext]/[name]-[hash][extname]',
          manualChunks: (id) => {
            if (id.includes('node_modules')) {
              if (id.includes('three') || id.includes('globe.gl') || id.includes('amap')) {
                return 'globe'
              } else if (id.includes('vue') || id.includes('vue-router') || id.includes('pinia')) {
                return 'vendor'
              } else if (id.includes('element-plus')) {
                return 'element'
              } else if (id.includes('axios') || id.includes('dayjs') || id.includes('lodash')) {
                return 'utils'
              }
              return 'vendor'
            }
          }
        }
      }
    },
    // 构建期间移除 debugger；console 保留，避免误删业务日志
    esbuild: {
      drop: mode === 'production' ? ['debugger'] : []
    }
  }
})
