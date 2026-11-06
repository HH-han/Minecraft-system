import { IPC_CHANNELS, IPC_EVENTS } from '../constants';

export interface AppInfo {
  name: string;
  version: string;
  platform: NodeJS.Platform;
  arch: string;
  nodeVersion: string;
  isDev: boolean;
  dataDir: string;
  logDir: string;
}

export interface AuthUser {
  id: string;
  username: string;
  nickname: string;
  role: 'member' | 'admin';
}

export interface AuthState {
  authenticated: boolean;
  user: AuthUser | null;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  user: AuthUser;
  token: string;
}

export interface DataGetRequest {
  key: string;
}

export interface DataSetRequest {
  key: string;
  value: unknown;
}

export interface FileFilter {
  name: string;
  extensions: string[];
}

export interface SelectFileRequest {
  title?: string;
  filters?: FileFilter[];
}

export interface SelectFileResponse {
  canceled: boolean;
  filePaths: string[];
}

/**
 * IPC 调用契约：新增通道时先在此登记请求 / 响应类型，
 * 渲染进程的 bridge 与主进程 handler 会自动获得类型约束。
 */
export interface IpcInvokeMap {
  [IPC_CHANNELS.appGetInfo]: { request: undefined; response: AppInfo };
  [IPC_CHANNELS.appQuit]: { request: undefined; response: void };
  [IPC_CHANNELS.authLogin]: { request: LoginRequest; response: LoginResponse };
  [IPC_CHANNELS.authLogout]: { request: undefined; response: AuthState };
  [IPC_CHANNELS.authGetState]: { request: undefined; response: AuthState };
  [IPC_CHANNELS.dataGet]: { request: DataGetRequest; response: unknown };
  [IPC_CHANNELS.dataSet]: { request: DataSetRequest; response: void };
  [IPC_CHANNELS.fileSelect]: { request: SelectFileRequest | undefined; response: SelectFileResponse };
}

export type IpcInvokeChannel = keyof IpcInvokeMap;
export type IpcRequest<K extends IpcInvokeChannel> = IpcInvokeMap[K]['request'];
export type IpcResponse<K extends IpcInvokeChannel> = IpcInvokeMap[K]['response'];

/** 主进程推送事件契约 */
export interface IpcEventMap {
  [IPC_EVENTS.sessionChanged]: AuthState;
}

export type IpcEventChannel = keyof IpcEventMap;
