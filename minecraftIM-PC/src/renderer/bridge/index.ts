import { IPC_CHANNELS } from '@shared/constants';
import type { AdrenalineBridge } from '@shared/types/adrenaline';
import type {
  AppInfo,
  AuthState,
  IpcEventChannel,
  IpcEventMap,
  IpcInvokeChannel,
  IpcRequest,
  IpcResponse,
  LoginRequest,
  LoginResponse
} from '@shared/types/ipc';

/** 无参数通道调用时不要求传 payload */
type InvokeArgs<K extends IpcInvokeChannel> = IpcRequest<K> extends undefined
  ? []
  : [payload: IpcRequest<K>];

export function hasNativeBridge(): boolean {
  return typeof window !== 'undefined' && typeof window.adrenaline?.invoke === 'function';
}

function getBridge(): AdrenalineBridge | null {
  return typeof window !== 'undefined' ? (window.adrenaline ?? null) : null;
}

/** 浏览器预览模式的兜底数据，保证 UI 可在 `vite` 中独立调试 */
const MOCK_APP_INFO: AppInfo = {
  name: 'minecraft-im-pc',
  version: '0.1.0',
  platform: 'win32',
  arch: 'x64',
  nodeVersion: 'browser-preview',
  isDev: true,
  dataDir: '(浏览器预览模式无本地目录)',
  logDir: '(浏览器预览模式无本地目录)'
};

let mockAuthState: AuthState = { authenticated: false, user: null };

async function mockInvoke(channel: IpcInvokeChannel, payload: unknown): Promise<unknown> {
  switch (channel) {
    case IPC_CHANNELS.appGetInfo:
      return MOCK_APP_INFO;
    case IPC_CHANNELS.appQuit:
      return undefined;
    case IPC_CHANNELS.authGetState:
      return mockAuthState;
    case IPC_CHANNELS.authLogin: {
      const request = payload as LoginRequest;
      if (!request.username?.trim() || !request.password) {
        throw new Error('用户名和密码不能为空');
      }
      const user = {
        id: `mock-${request.username}`,
        username: request.username,
        nickname: request.username,
        role: 'member' as const
      };
      mockAuthState = { authenticated: true, user };
      return { user, token: 'mock-token' } satisfies LoginResponse;
    }
    case IPC_CHANNELS.authLogout:
      mockAuthState = { authenticated: false, user: null };
      return mockAuthState;
    case IPC_CHANNELS.dataGet: {
      const { key } = payload as { key: string };
      const raw = localStorage.getItem(`mock-data:${key}`);
      return raw ? (JSON.parse(raw) as unknown) : undefined;
    }
    case IPC_CHANNELS.dataSet: {
      const { key, value } = payload as { key: string; value: unknown };
      localStorage.setItem(`mock-data:${key}`, JSON.stringify(value));
      return undefined;
    }
    case IPC_CHANNELS.fileSelect:
      return { canceled: true, filePaths: [] };
    default:
      throw new Error(`[bridge] 未实现的模拟通道: ${String(channel)}`);
  }
}

export async function invoke<K extends IpcInvokeChannel>(
  channel: K,
  ...args: InvokeArgs<K>
): Promise<IpcResponse<K>> {
  const payload = args[0] as IpcRequest<K> | undefined;
  const native = getBridge();
  if (!native) {
    return (await mockInvoke(channel, payload)) as IpcResponse<K>;
  }
  return native.invoke<IpcResponse<K>>(channel, payload);
}

type UnknownListener = (payload: unknown) => void;

const pushListeners = new Map<IpcEventChannel, Set<UnknownListener>>();

/** 订阅主进程推送；返回取消订阅函数 */
export function onPush<K extends IpcEventChannel>(
  channel: K,
  listener: (payload: IpcEventMap[K]) => void
): () => void {
  const native = getBridge();
  if (!native) return () => {};

  let listeners = pushListeners.get(channel);
  if (!listeners) {
    const created = new Set<UnknownListener>();
    pushListeners.set(channel, created);
    native.on(channel, (data) => {
      pushListeners.get(channel)?.forEach((callback) => callback(data));
    });
    listeners = created;
  }

  const wrapped = listener as UnknownListener;
  listeners.add(wrapped);

  return () => {
    listeners?.delete(wrapped);
  };
}
