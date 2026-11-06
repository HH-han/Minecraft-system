import path from 'node:path';
import { BrowserWindow } from 'adrenaline-js';
import { createWindow } from './window-factory';
import { APP_DISPLAY_NAME } from '../../shared/constants';
import { logger } from '../infra/logger';

/**
 * 主窗口：单例创建。
 * 渲染层产物通过 file:// 加载；设置 ADREN_RENDERER_URL 可切换到本地服务地址
 * （Adrenaline 默认拒绝远程 URL，需要同时设置 ADREN_CEF_ALLOW_REMOTE=1）。
 */
const rendererEntry = path.join(__dirname, '../renderer/index.html');

let mainWindow: BrowserWindow | null = null;

export function openMainWindow(): BrowserWindow {
  if (mainWindow) return mainWindow;

  const win = createWindow({
    width: 1180,
    height: 760,
    minWidth: 960,
    minHeight: 640,
    title: APP_DISPLAY_NAME,
    resizable: true,
    lowEndDeviceMode: false
  });

  const rendererUrl = process.env.ADREN_RENDERER_URL;
  if (rendererUrl) {
    logger.info('loading renderer from url', rendererUrl);
    win.loadURL(rendererUrl);
  } else {
    win.loadFile(rendererEntry);
  }

  win.on('closed', () => {
    mainWindow = null;
  });

  mainWindow = win;
  return win;
}

export function getMainWindow(): BrowserWindow | null {
  return mainWindow;
}
