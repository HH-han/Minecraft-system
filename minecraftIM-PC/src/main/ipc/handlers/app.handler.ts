import { app, dialog, ipcMain } from 'adrenaline-js';
import { IPC_CHANNELS } from '../../../shared/constants';
import type { AppInfo, SetStorageDirRequest } from '../../../shared/types/ipc';
import { getAppInfo, paths, setStorageDir } from '../../infra/config';
import { logger } from '../../infra/logger';

export function registerAppHandlers(): void {
  ipcMain.handle(IPC_CHANNELS.appGetInfo, (): AppInfo => getAppInfo());

  ipcMain.handle(
    IPC_CHANNELS.appSetStorageDir,
    async (_event, payload: SetStorageDirRequest | null): Promise<AppInfo | null> => {
      if (!payload?.kind) return null;
      const kind = payload.kind === 'logs' ? 'logs' : 'data';
      try {
        const result = await dialog.showOpenDialog({
          title: payload.title ?? (kind === 'logs' ? '选择日志目录' : '选择数据目录'),
          defaultPath: kind === 'logs' ? paths.logsDir : paths.dataDir,
          properties: ['openDirectory', 'createDirectory']
        });
        if (result.canceled || !result.filePaths[0]) return null;

        setStorageDir(kind, result.filePaths[0]);
        logger.info('storage dir changed', { kind, dir: result.filePaths[0] });
        return getAppInfo();
      } catch (error) {
        logger.error('set storage dir failed', error instanceof Error ? error.message : error);
        throw error;
      }
    }
  );

  ipcMain.handle(IPC_CHANNELS.appQuit, () => {
    logger.info('quit requested by renderer');
    // 先让 invoke 正常返回，再退出进程
    setTimeout(() => app.quit(), 0);
  });
}
