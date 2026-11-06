import { registerAppHandlers } from './handlers/app.handler';
import { registerAuthHandlers } from './handlers/auth.handler';
import { registerDataHandlers } from './handlers/data.handler';
import { registerFileHandlers } from './handlers/file.handler';
import { logger } from '../infra/logger';

/** IPC 统一注册入口：在 app ready 后调用一次 */
export function registerIpcHandlers(): void {
  registerAppHandlers();
  registerAuthHandlers();
  registerDataHandlers();
  registerFileHandlers();
  logger.info('ipc handlers registered');
}
