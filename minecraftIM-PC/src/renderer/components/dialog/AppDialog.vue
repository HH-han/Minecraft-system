<script setup lang="ts">
import { onBeforeUnmount, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { Close } from '@element-plus/icons-vue';
import { isTopLayer, popLayer, pushLayer } from './layer';

/**
 * 通用弹窗（替代 el-dialog）：
 * - 主题令牌驱动，适配 light / dark / game 三套主题
 * - 固定头部（标题 + 关闭）、可滚动内容区、右对齐底部操作区
 * - 支持 Esc 关闭、点击遮罩关闭，多弹窗叠加时仅顶层响应
 */
const props = withDefaults(
  defineProps<{
    modelValue: boolean;
    title?: string;
    width?: string;
    showFooter?: boolean;
    showCancel?: boolean;
    showConfirm?: boolean;
    confirmText?: string;
    cancelText?: string;
    confirmLoading?: boolean;
    confirmDisabled?: boolean;
    danger?: boolean;
    closable?: boolean;
    maskClosable?: boolean;
    align?: 'center' | 'top';
    bodyPadding?: string;
  }>(),
  {
    title: '',
    width: '420px',
    showFooter: true,
    showCancel: true,
    showConfirm: true,
    confirmText: '',
    cancelText: '',
    confirmLoading: false,
    confirmDisabled: false,
    danger: false,
    closable: true,
    maskClosable: true,
    align: 'center',
    bodyPadding: '0 20px 4px'
  }
);

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  confirm: [];
  cancel: [];
  closed: [];
}>();

const { t } = useI18n();
const layerId = Symbol('app-dialog');
const panelRef = ref<HTMLElement | null>(null);

function close(): void {
  emit('update:modelValue', false);
}

function handleCancel(): void {
  emit('cancel');
  close();
}

function handleMaskClick(): void {
  if (props.maskClosable) handleCancel();
}

function handleKeydown(event: KeyboardEvent): void {
  if (event.key !== 'Escape') return;
  if (!isTopLayer(layerId)) return;
  event.stopPropagation();
  handleCancel();
}

watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      pushLayer(layerId);
      window.addEventListener('keydown', handleKeydown);
      requestAnimationFrame(() => panelRef.value?.focus());
    } else {
      popLayer(layerId);
      window.removeEventListener('keydown', handleKeydown);
    }
  },
  { immediate: true }
);

onBeforeUnmount(() => {
  popLayer(layerId);
  window.removeEventListener('keydown', handleKeydown);
});
</script>

<template>
  <Teleport to="body">
    <Transition name="dlg" @after-leave="emit('closed')">
      <div
        v-if="modelValue"
        class="app-dialog"
        :class="`app-dialog--${align}`"
        @click.self="handleMaskClick"
      >
        <div
          ref="panelRef"
          class="app-dialog__panel"
          :style="{ width }"
          role="dialog"
          aria-modal="true"
          tabindex="-1"
        >
          <header class="app-dialog__head">
            <slot name="head">
              <h3 class="app-dialog__title">{{ title }}</h3>
            </slot>
            <slot name="head-extra" />
            <button
              v-if="closable"
              type="button"
              class="app-dialog__close"
              :title="t('common.dialog.close')"
              @click="handleCancel"
            >
              <el-icon :size="15"><Close /></el-icon>
            </button>
          </header>

          <div class="app-dialog__body" :style="{ padding: bodyPadding }">
            <slot />
          </div>

          <footer v-if="showFooter" class="app-dialog__foot">
            <slot name="footer">
              <button
                v-if="showCancel"
                type="button"
                class="dlg-btn dlg-btn--ghost"
                :disabled="confirmLoading"
                @click="handleCancel"
              >
                {{ cancelText || t('common.dialog.cancel') }}
              </button>
              <button
                v-if="showConfirm"
                type="button"
                class="dlg-btn"
                :class="danger ? 'dlg-btn--danger' : 'dlg-btn--primary'"
                :disabled="confirmDisabled || confirmLoading"
                @click="emit('confirm')"
              >
                <span v-if="confirmLoading" class="dlg-btn__spinner" />
                <span>{{ confirmText || t('common.dialog.confirm') }}</span>
              </button>
            </slot>
          </footer>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.app-dialog {
  position: fixed;
  inset: 0;
  z-index: 3000;
  display: flex;
  justify-content: center;
  padding: 24px;
  overflow-y: auto;
  background: var(--theme-overlay);
  backdrop-filter: blur(2px);
}

.app-dialog--center {
  align-items: center;
}

.app-dialog--top {
  align-items: flex-start;
  padding-top: 7vh;
}

.app-dialog__panel {
  width: 420px;
  max-width: 100%;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 48px);
  outline: none;
  border-radius: 16px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-elevated);
  color: var(--theme-text-1);
  box-shadow: var(--theme-shadow-lg);
}

.app-dialog__head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 18px 20px 14px;
  flex-shrink: 0;
}

.app-dialog__title {
  flex: 1;
  min-width: 0;
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 0.2px;
  color: var(--theme-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-dialog__close {
  width: 28px;
  height: 28px;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  transition:
    background var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.app-dialog__close:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.app-dialog__body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overscroll-behavior: contain;
}

.app-dialog__foot {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 20px 20px;
  flex-shrink: 0;
}

.dlg-btn {
  height: 34px;
  min-width: 76px;
  padding: 0 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-radius: 8px;
  border: 1px solid transparent;
  font-family: inherit;
  font-size: 13px;
  line-height: 1;
  cursor: pointer;
  transition:
    background var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.dlg-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.dlg-btn--ghost {
  border-color: var(--theme-border);
  background: transparent;
  color: var(--theme-text-2);
}

.dlg-btn--ghost:hover:not(:disabled) {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.dlg-btn--primary {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  box-shadow: 0 10px 20px -14px color-mix(in srgb, var(--theme-primary) 80%, transparent);
}

.dlg-btn--primary:hover:not(:disabled) {
  background: var(--theme-primary-hover);
}

.dlg-btn--danger {
  background: var(--theme-danger);
  color: #ffffff;
  box-shadow: 0 10px 20px -14px color-mix(in srgb, var(--theme-danger) 80%, transparent);
}

.dlg-btn--danger:hover:not(:disabled) {
  filter: brightness(0.94);
}

.dlg-btn__spinner {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid currentColor;
  border-top-color: transparent;
  animation: dlg-spin 0.7s linear infinite;
}

@keyframes dlg-spin {
  to {
    transform: rotate(360deg);
  }
}

.dlg-enter-active,
.dlg-leave-active {
  transition: opacity 0.18s var(--theme-ease);
}

.dlg-enter-from,
.dlg-leave-to {
  opacity: 0;
}

.dlg-enter-active .app-dialog__panel,
.dlg-leave-active .app-dialog__panel {
  transition:
    transform 0.22s var(--theme-ease),
    opacity 0.22s var(--theme-ease);
}

.dlg-enter-from .app-dialog__panel,
.dlg-leave-to .app-dialog__panel {
  opacity: 0;
  transform: translateY(12px) scale(0.98);
}
</style>
