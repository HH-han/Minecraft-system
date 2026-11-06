import { app } from 'adrenaline-js';
import { registerIpcHandlers } from './ipc';
import { logger } from './infra/logger';
import { openMainWindow } from './windows/main-window';

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
