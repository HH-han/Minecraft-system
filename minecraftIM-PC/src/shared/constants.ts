/**
 * 主进程与渲染进程共享的 IPC 通道定义。
 * 通道名只在这里声明，禁止在业务代码里散落字符串字面量。
 */
export const IPC_CHANNELS = {
  appGetInfo: 'app:get-info',
  appQuit: 'app:quit',
  appSetStorageDir: 'app:set-storage-dir',
  dataGet: 'data:get',
  dataSet: 'data:set',
  fileSelect: 'file:select'
} as const;

/** 主进程 -> 渲染进程的推送事件 */
export const IPC_EVENTS = {} as const;

export const APP_DISPLAY_NAME = 'Minecraft IM PC';
