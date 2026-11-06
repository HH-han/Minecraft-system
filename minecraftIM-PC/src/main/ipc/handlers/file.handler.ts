import { ipcMain } from 'adrenaline-js';
import { IPC_CHANNELS } from '../../../shared/constants';
import type { SelectFileRequest, SelectFileResponse } from '../../../shared/types/ipc';
import { selectFile } from '../../services/file.service';

export function registerFileHandlers(): void {
  ipcMain.handle(
    IPC_CHANNELS.fileSelect,
    (_event, payload: SelectFileRequest | null): Promise<SelectFileResponse> => selectFile(payload)
  );
}
