import { dialog } from 'adrenaline-js';
import type { SelectFileRequest, SelectFileResponse } from '../../shared/types/ipc';
import { logger } from '../infra/logger';

export async function selectFile(payload?: SelectFileRequest | null): Promise<SelectFileResponse> {
  try {
    const result = await dialog.showOpenDialog({
      title: payload?.title ?? '选择文件',
      properties: ['openFile'],
      filters: payload?.filters
    });

    return {
      canceled: result.canceled,
      filePaths: result.filePaths
    };
  } catch (error) {
    logger.error('file.select failed', error);
    throw error;
  }
}
