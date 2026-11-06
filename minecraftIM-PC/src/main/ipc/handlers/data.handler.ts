import { ipcMain } from 'adrenaline-js';
import { IPC_CHANNELS } from '../../../shared/constants';
import type { DataGetRequest, DataSetRequest } from '../../../shared/types/ipc';
import { getValue, setValue } from '../../services/data.service';

export function registerDataHandlers(): void {
  ipcMain.handle(IPC_CHANNELS.dataGet, (_event, payload: DataGetRequest) => getValue(payload.key));

  ipcMain.handle(IPC_CHANNELS.dataSet, async (_event, payload: DataSetRequest): Promise<void> => {
    await setValue(payload.key, payload.value);
  });
}
