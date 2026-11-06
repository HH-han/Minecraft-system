import fs from 'node:fs';
import path from 'node:path';
import { app } from 'adrenaline-js';
import { registerIpcHandlers } from './ipc';
import { logger } from './infra/logger';
import { openMainWindow } from './windows/main-window';

// Windows 下 WebView2 的 Loader DLL 随构建产物放在 native/ 目录，
// 在创建窗口前注入 PATH，避免依赖系统级安装。
if (process.platform === 'win32') {
  const nativeDir = path.join(__dirname, 'native');
  const loader = path.join(nativeDir, 'WebView2Loader.dll');
  if (fs.existsSync(loader)) {
    process.env.PATH = `${nativeDir}${path.delimiter}${process.env.PATH ?? ''}`;
  }
}

process.on('uncaughtException', (error) => {
  logger.error('uncaught exception', error instanceof Error ? error.stack : error);
});

process.on('unhandledRejection', (reason) => {
  logger.error('unhandled rejection', reason);
});

async function bootstrap(): Promise<void> {
  await app.whenReady();
  logger.info('adrenaline app ready');

  registerIpcHandlers();
  openMainWindow();
}

app.on('window-all-closed', () => {
  logger.info('all windows closed');
});

bootstrap().catch((error) => {
  logger.error('bootstrap failed', error instanceof Error ? error.stack : error);
  app.quit();
});
