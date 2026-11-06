import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import pkg from '../../../package.json';
import type { AppInfo } from '../../shared/types/ipc';

/**
 * 环境相关配置。业务服务只依赖这里暴露的路径与常量，
 * 不直接读取 process.env / os.homedir()，便于测试与迁移。
 */
export const isDev = process.env.NODE_ENV === 'development';

const appDataBase =
  process.platform === 'win32'
    ? (process.env.APPDATA ?? path.join(os.homedir(), 'AppData', 'Roaming'))
    : path.join(os.homedir(), '.config');

const appRoot = path.join(appDataBase, pkg.name);
const settingsFile = path.join(appRoot, 'settings.json');

/** 用户在设置页自定义的存储目录，持久化在 appRoot/settings.json */
interface PathOverrides {
  dataDir?: string;
  logsDir?: string;
}

function readOverrides(): PathOverrides {
  try {
    const parsed = JSON.parse(fs.readFileSync(settingsFile, 'utf8')) as PathOverrides;
    return parsed && typeof parsed === 'object' ? parsed : {};
  } catch {
    return {};
  }
}

const overrides = readOverrides();

export const paths = {
  appRoot,
  dataDir: overrides.dataDir || path.join(appRoot, 'data'),
  logsDir: overrides.logsDir || path.join(appRoot, 'logs')
};

function persistPaths(): void {
  try {
    fs.mkdirSync(appRoot, { recursive: true });
    const payload: PathOverrides = { dataDir: paths.dataDir, logsDir: paths.logsDir };
    fs.writeFileSync(settingsFile, JSON.stringify(payload, null, 2), 'utf8');
  } catch (error) {
    console.warn('persist storage paths failed', error);
  }
}

/** 切换数据 / 日志目录并落盘，返回是否成功 */
export function setStorageDir(kind: 'data' | 'logs', dir: string): void {
  const target = path.resolve(dir);
  fs.mkdirSync(target, { recursive: true });
  if (kind === 'logs') {
    paths.logsDir = target;
  } else {
    paths.dataDir = target;
  }
  persistPaths();
}

export function getAppInfo(): AppInfo {
  return {
    name: pkg.name,
    version: pkg.version,
    platform: process.platform,
    arch: process.arch,
    nodeVersion: process.versions.node,
    isDev,
    dataDir: paths.dataDir,
    logDir: paths.logsDir
  };
}
