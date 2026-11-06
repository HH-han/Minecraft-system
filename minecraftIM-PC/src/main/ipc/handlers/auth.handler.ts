import { ipcMain } from 'adrenaline-js';
import { IPC_CHANNELS, IPC_EVENTS } from '../../../shared/constants';
import type { AuthState, LoginRequest, LoginResponse } from '../../../shared/types/ipc';
import { getAuthState, login, logout } from '../../services/auth.service';
import { broadcast } from '../../windows/window-factory';

export function registerAuthHandlers(): void {
  ipcMain.handle(IPC_CHANNELS.authLogin, async (_event, payload: LoginRequest): Promise<LoginResponse> => {
    const result = await login(payload);
    broadcast(IPC_EVENTS.sessionChanged, getAuthState());
    return result;
  });

  ipcMain.handle(IPC_CHANNELS.authLogout, (): AuthState => {
    const state = logout();
    broadcast(IPC_EVENTS.sessionChanged, state);
    return state;
  });

  ipcMain.handle(IPC_CHANNELS.authGetState, (): AuthState => getAuthState());
}
