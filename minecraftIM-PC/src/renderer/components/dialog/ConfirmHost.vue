<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { WarningFilled } from '@element-plus/icons-vue';
import AppDialog from './AppDialog.vue';
import { confirmState, settleConfirm } from './confirm';

/** 全局确认弹窗宿主：在 App.vue 挂载一次，供 confirm() 服务调用 */
const { t } = useI18n();
</script>

<template>
  <AppDialog
    :model-value="confirmState.visible"
    :title="confirmState.title"
    width="380px"
    :confirm-text="confirmState.confirmText || t('common.dialog.confirm')"
    :cancel-text="confirmState.cancelText || t('common.dialog.cancel')"
    :danger="confirmState.tone === 'danger'"
    @update:model-value="(value: boolean) => { if (!value) settleConfirm(false); }"
    @confirm="settleConfirm(true)"
    @cancel="settleConfirm(false)"
  >
    <div class="confirm-body">
      <span class="confirm-icon" :class="`is-${confirmState.tone}`">
        <el-icon :size="17"><WarningFilled /></el-icon>
      </span>
      <p class="confirm-message">{{ confirmState.message }}</p>
    </div>
  </AppDialog>
</template>

<style scoped>
.confirm-body {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 2px 0 12px;
}

.confirm-icon {
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  border-radius: 50%;
  color: #ffffff;
}

.confirm-icon.is-warning {
  background: var(--theme-warning);
}

.confirm-icon.is-danger {
  background: var(--theme-danger);
}

.confirm-message {
  flex: 1;
  min-width: 0;
  margin: 4px 0 0;
  font-size: 13.5px;
  line-height: 1.6;
  color: var(--theme-text-2);
  word-break: break-word;
}
</style>
