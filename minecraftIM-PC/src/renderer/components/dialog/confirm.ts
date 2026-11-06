import { reactive } from 'vue';

export interface ConfirmOptions {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  tone?: 'warning' | 'danger';
}

interface ConfirmState {
  visible: boolean;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  tone: 'warning' | 'danger';
}

export const confirmState = reactive<ConfirmState>({
  visible: false,
  title: '',
  message: '',
  tone: 'warning'
});

let resolver: ((value: boolean) => void) | null = null;

/**
 * 自定义确认弹窗（替代 ElMessageBox.confirm）。
 * 由 App 根节点挂载的 ConfirmHost 渲染，返回是否点击了确认。
 */
export function confirm(options: ConfirmOptions): Promise<boolean> {
  resolver?.(false);
  Object.assign(confirmState, {
    confirmText: undefined,
    cancelText: undefined,
    tone: 'warning',
    ...options,
    visible: true
  });
  return new Promise<boolean>((resolve) => {
    resolver = resolve;
  });
}

export function settleConfirm(value: boolean): void {
  const settle = resolver;
  resolver = null;
  confirmState.visible = false;
  settle?.(value);
}
