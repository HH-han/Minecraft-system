import { defineConfig } from '@vben/vite-config';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      server: {
        open: true,
        proxy: {
          '/api': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/api/, ''),
            // mock代理目标地址
            target: 'http://localhost:5320/api',
            ws: true,
          },
          // 后端上传目录代理（Spring Boot 默认在 8080 端口提供 /upload 静态资源）
          '/upload': {
            changeOrigin: true,
            target: 'http://localhost:8080',
          },
        },
      },
    },
  };
});
