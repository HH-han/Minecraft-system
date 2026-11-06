import { wsBaseUrl, tokenStorageKey } from '@/config';

/** 后端 WS 消息类型（WebSocketHandler） */
export type WsEventType =
  | 'connect'
  | 'connect-success'
  | 'disconnect'
  | 'error'
  | 'heartbeat'
  | 'private-message'
  | 'group-message'
  | 'read-receipt'
  | 'typing'
  | 'message';

export interface WsMessage {
  type: string;
  [key: string]: unknown;
}

type WsListener = (data: WsMessage) => void;

/**
 * WebSocket 客户端，协议与 minecraft-ui 的 utils/websocket.js 一致：
 * 连接 ws://<host>/ws?token=<jwt>，JSON 消息按 type 分发。
 */
class WebSocketService {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private readonly maxReconnectAttempts = 5;
  private readonly reconnectDelay = 3000;
  private heartbeatTimer: ReturnType<typeof setInterval> | null = null;
  private readonly listeners = new Map<string, WsListener[]>();
  private isClosing = false;
  private userId: number | null = null;

  get connected(): boolean {
    return this.ws?.readyState === WebSocket.OPEN;
  }

  connect(userId: number): void {
    const token = localStorage.getItem(tokenStorageKey);
    if (!token || !userId) return;
    this.userId = userId;

    if (this.ws && this.ws.readyState === WebSocket.OPEN) return;
    if (this.ws && this.ws.readyState !== WebSocket.CLOSED) {
      this.isClosing = true;
      this.ws.close(1001, 'Reconnecting');
      this.ws = null;
    }

    const url = `${wsBaseUrl}/ws?token=${encodeURIComponent(token)}`;
    try {
      this.ws = new WebSocket(url);
    } catch {
      return;
    }

    this.ws.onopen = () => {
      this.isClosing = false;
      this.reconnectAttempts = 0;
      this.emit('connect', { type: 'CONNECT', userId });
      this.startHeartbeat();
    };

    this.ws.onmessage = (event) => {
      try {
        const data = JSON.parse(String(event.data)) as WsMessage;
        this.handleMessage(data);
      } catch {
        /* 忽略非 JSON 帧 */
      }
    };

    this.ws.onerror = () => {
      this.emit('error', { type: 'ERROR' });
    };

    this.ws.onclose = (event) => {
      this.stopHeartbeat();
      this.emit('disconnect', { type: 'DISCONNECT', code: event.code, reason: event.reason });
      if (!this.isClosing && event.code !== 1000 && this.reconnectAttempts < this.maxReconnectAttempts) {
        this.reconnect();
      }
      this.isClosing = false;
    };
  }

  private handleMessage(data: WsMessage): void {
    if (!data?.type) return;
    switch (data.type) {
      case 'CONNECT':
        this.emit('connect-success', data);
        break;
      case 'HEARTBEAT':
        this.emit('heartbeat', data);
        break;
      case 'PRIVATE_MESSAGE':
        this.emit('private-message', data);
        break;
      case 'GROUP_MESSAGE':
        this.emit('group-message', data);
        break;
      case 'READ_RECEIPT':
        this.emit('read-receipt', data);
        break;
      case 'TYPING':
        this.emit('typing', data);
        break;
      default:
        this.emit('message', data);
    }
  }

  private startHeartbeat(): void {
    this.stopHeartbeat();
    this.heartbeatTimer = setInterval(() => {
      this.send({ type: 'HEARTBEAT' });
    }, 30000);
  }

  private stopHeartbeat(): void {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
  }

  private reconnect(): void {
    this.reconnectAttempts += 1;
    setTimeout(() => {
      if (this.userId) this.connect(this.userId);
    }, this.reconnectDelay * this.reconnectAttempts);
  }

  send(data: WsMessage): boolean {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return false;
    try {
      this.ws.send(JSON.stringify(data));
      return true;
    } catch {
      return false;
    }
  }

  on(event: WsEventType, callback: WsListener): void {
    const list = this.listeners.get(event) ?? [];
    list.push(callback);
    this.listeners.set(event, list);
  }

  off(event: WsEventType, callback: WsListener): void {
    const list = this.listeners.get(event);
    if (!list) return;
    const index = list.indexOf(callback);
    if (index > -1) list.splice(index, 1);
  }

  private emit(event: WsEventType, data: WsMessage): void {
    const list = this.listeners.get(event);
    if (!list) return;
    for (const callback of [...list]) {
      try {
        callback(data);
      } catch {
        /* 单个监听器异常不影响其他 */
      }
    }
  }

  disconnect(): void {
    this.isClosing = true;
    this.stopHeartbeat();
    if (this.ws) {
      this.ws.close(1000, 'User logout');
      this.ws = null;
    }
    this.listeners.clear();
    this.userId = null;
  }
}

export const wsService = new WebSocketService();
