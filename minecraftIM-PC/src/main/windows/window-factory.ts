import { BrowserWindow } from 'adrenaline-js';
import type { BrowserWindowOptions } from 'adrenaline-js';

/**
 * 窗口工厂：统一创建、追踪、广播。
 * 多窗口场景（设置窗 / 弹窗）都从这里创建，便于统一注入配置。
 */
const windows = new Set<BrowserWindow>();

export function createWindow(options: BrowserWindowOptions): BrowserWindow {
  const win = new BrowserWindow(options);
  windows.add(win);
  win.on('closed', () => windows.delete(win));
  return win;
}

export function getOpenWindows(): BrowserWindow[] {
  return Array.from(windows);
}

/** 向所有窗口推送事件（例如会话状态变化） */
export function broadcast(channel: string, payload: unknown): void {
  for (const win of windows) {
    try {
      win.webContents.send(channel, payload);
    } catch {
      // 窗口已销毁时忽略
    }
  }
}
