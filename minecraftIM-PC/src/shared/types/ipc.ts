import { IPC_CHANNELS } from '../constants';

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

export type StorageDirKind = 'data' | 'logs';

export interface SetStorageDirRequest {
  kind: StorageDirKind;
  /** 目录选择弹窗标题（由渲染层传入，便于国际化） */
  title?: string;
}

/**
 * IPC 调用契约：新增通道时先在此登记请求 / 响应类型，
 * 渲染进程的 bridge 与主进程 handler 会自动获得类型约束。
 */
export interface IpcInvokeMap {
  [IPC_CHANNELS.appGetInfo]: { request: undefined; response: AppInfo };
  [IPC_CHANNELS.appQuit]: { request: undefined; response: void };
  [IPC_CHANNELS.appSetStorageDir]: {
    request: SetStorageDirRequest;
    response: AppInfo | null;
  };
  [IPC_CHANNELS.dataGet]: { request: DataGetRequest; response: unknown };
  [IPC_CHANNELS.dataSet]: { request: DataSetRequest; response: void };
  [IPC_CHANNELS.fileSelect]: { request: SelectFileRequest | undefined; response: SelectFileResponse };
}

export type IpcInvokeChannel = keyof IpcInvokeMap;
export type IpcRequest<K extends IpcInvokeChannel> = IpcInvokeMap[K]['request'];
export type IpcResponse<K extends IpcInvokeChannel> = IpcInvokeMap[K]['response'];

/** 主进程推送事件契约 */
export interface IpcEventMap {}

export type IpcEventChannel = keyof IpcEventMap;
