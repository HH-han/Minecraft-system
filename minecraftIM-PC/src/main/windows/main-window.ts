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
    resizable: true
  });

  // 设置窗口图标（标题栏 / 任务栏）
  const iconPath = path.join(__dirname, 'assets/app.ico');
  try {
    win.setIcon(iconPath);
  } catch (error) {
    logger.warn('set window icon failed', error instanceof Error ? error.message : error);
  }

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
