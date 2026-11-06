/** Adrenaline.js 注入到渲染进程的桥接 API 类型声明 */
export interface AdrenalineBridge {
  send(channel: string, data?: unknown): void;
  on(channel: string, listener: (data: unknown) => void): void;
  invoke<T = unknown>(channel: string, data?: unknown): Promise<T>;
}
