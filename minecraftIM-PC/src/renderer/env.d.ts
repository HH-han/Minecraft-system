/// <reference types="vite/client" />

import type { AdrenalineBridge } from '@shared/types/adrenaline';

declare module '*.vue' {
  import type { DefineComponent } from 'vue';
  const component: DefineComponent<object, object, unknown>;
  export default component;
}

declare global {
  interface Window {
    /** 由 Adrenaline.js 注入；浏览器预览模式下不存在 */
    adrenaline?: AdrenalineBridge;
  }
}

export {};
