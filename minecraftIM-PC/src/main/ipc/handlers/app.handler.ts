import { app, ipcMain } from 'adrenaline-js';
import { IPC_CHANNELS } from '../../shared/constants';
import type { AppInfo } from '../../shared/types/ipc';
import { appInfo } from '../infra/config';
import { logger } from '../infra/logger';

export function registerAppHandlers(): void {
  ipcMain.handle(IPC_CHANNELS.appGetInfo, (): AppInfo => appInfo);

  ipcMain.handle(IPC_CHANNELS.appQuit, () => {
    logger.info('quit requested by renderer');
    // 先让 invoke 正常返回，再退出进程
    setTimeout(() => app.quit(), 0);
  });
}
