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

export const paths = {
  appRoot,
  dataDir: path.join(appRoot, 'data'),
  logsDir: path.join(appRoot, 'logs')
};

export const appInfo: AppInfo = {
  name: pkg.name,
  version: pkg.version,
  platform: process.platform,
  arch: process.arch,
  nodeVersion: process.versions.node,
  isDev,
  dataDir: paths.dataDir,
  logDir: paths.logsDir
};
