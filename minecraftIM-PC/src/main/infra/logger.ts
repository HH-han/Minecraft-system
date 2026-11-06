import fs from 'node:fs';
import path from 'node:path';
import { paths } from './config';

type LogLevel = 'info' | 'warn' | 'error' | 'debug';

const logFile = (): string => path.join(paths.logsDir, 'app.log');

function write(level: LogLevel, message: string, meta?: unknown): void {
  const line = `${new Date().toISOString()} [${level.toUpperCase()}] ${message}${
    meta === undefined ? '' : ` ${JSON.stringify(meta)}`
  }\n`;

  const consoleFn = level === 'error' ? console.error : level === 'warn' ? console.warn : console.log;
  consoleFn(line.trimEnd());

  try {
    fs.mkdirSync(paths.logsDir, { recursive: true });
    fs.appendFileSync(logFile(), line, 'utf8');
  } catch {
    // 日志落盘失败不应影响主流程
  }
}

export const logger = {
  info: (message: string, meta?: unknown) => write('info', message, meta),
  warn: (message: string, meta?: unknown) => write('warn', message, meta),
  error: (message: string, meta?: unknown) => write('error', message, meta),
  debug: (message: string, meta?: unknown) => write('debug', message, meta)
};
