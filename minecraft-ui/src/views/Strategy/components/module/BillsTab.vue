<template>
  <div class="bills-tab">
    <div class="tab-toolbar">
      <span class="toolbar-title">
        {{ t('strategy.detailTabs.bills') }}
        <em>{{ bills.length }}</em>
      </span>
      <div class="toolbar-actions">
        <button class="btn-secondary" type="button" @click="$emit('settlement')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 19V5M4 19h16" />
            <path d="M8 15V9M12 15V6M16 15v-4" />
          </svg>
          {{ t('strategy.bills.settlement') }}
        </button>
        <button class="btn-primary" type="button" @click="$emit('add')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 5v14M5 12h14" />
          </svg>
          {{ t('strategy.bills.add') }}
        </button>
      </div>
    </div>

    <div v-if="bills.length === 0" class="tab-empty">
      <p>{{ t('strategy.bills.empty') }}</p>
    </div>

    <template v-else>
      <div class="bill-summary">
        <div class="summary-item">
          <span class="summary-label">¥</span>
          <span class="summary-value">{{ totalAmount.toFixed(2) }}</span>
        </div>
        <span class="summary-divider"></span>
        <span class="summary-meta">{{ t('strategy.detailTabs.bills') }} · {{ bills.length }}</span>
      </div>

      <div class="bill-list">
        <div v-for="bill in bills" :key="bill.id" class="bill-item">
          <div class="bill-icon">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M5 4h14v16l-3-2-4 2-4-2-3 2V4Z" />
              <path d="M9 9h6M9 13h4" />
            </svg>
          </div>

          <div class="bill-info">
            <span class="bill-title">{{ bill.title }}</span>
            <span class="bill-payer">{{ t('strategy.bills.payer', { name: bill.payerName || '—' }) }}</span>
          </div>

          <div class="bill-amount">
            <span class="amount">¥{{ Number(bill.amount || 0).toFixed(2) }}</span>
            <span :class="['bill-status', bill.isSettled ? 'settled' : 'unsettled']">
              {{ bill.isSettled ? t('strategy.bills.settled') : t('strategy.bills.unsettled') }}
            </span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  bills: {
    type: Array,
    default: () => []
  }
})

defineEmits(['add', 'settlement'])

const { t } = useI18n()

const totalAmount = computed(() =>
  props.bills.reduce((sum, bill) => sum + Number(bill.amount || 0), 0)
)
</script>

<style scoped>
.bills-tab {
  animation: fade-in var(--transition-normal);
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.tab-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.toolbar-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.toolbar-title em {
  padding: 1px 8px;
  border-radius: var(--radius-full);
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  font-size: 11px;
  font-style: normal;
  font-weight: 600;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.toolbar-actions button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid transparent;
  border-radius: var(--radius-full);
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), border-color var(--transition-fast),
    transform var(--transition-fast);
}

.toolbar-actions svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.btn-primary {
  background: var(--color-primary);
  color: var(--theme-primary-text);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-secondary {
  background: var(--theme-bg-surface);
  border-color: var(--color-border);
  color: var(--color-text);
}

.btn-secondary:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.toolbar-actions button:active {
  transform: scale(0.97);
}

.bill-summary {
  display: flex;
  align-items: baseline;
  gap: 14px;
  padding: 20px 22px;
  margin-bottom: 16px;
  border-radius: var(--radius-md);
  background:
    radial-gradient(120% 160% at 0% 0%, var(--theme-primary-soft) 0%, transparent 60%),
    var(--color-bg-secondary);
  border: 1px solid var(--color-border);
}

.summary-item {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.summary-label {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-primary);
}

.summary-value {
  font-size: 28px;
  font-weight: 750;
  letter-spacing: -0.02em;
  color: var(--color-text);
  line-height: 1;
}

.summary-divider {
  width: 1px;
  height: 18px;
  background: var(--color-border);
}

.summary-meta {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.bill-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.bill-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: border-color var(--transition-normal), box-shadow var(--transition-normal);
}

.bill-item:hover {
  border-color: var(--theme-border-strong);
  box-shadow: var(--shadow-sm);
}

.bill-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  flex-shrink: 0;
  border-radius: 12px;
  background: var(--theme-primary-soft);
  color: var(--color-primary);
}

.bill-icon svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.bill-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.bill-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.bill-payer {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.bill-amount {
  flex-shrink: 0;
  text-align: right;
}

.amount {
  display: block;
  font-size: 17px;
  font-weight: 700;
  color: var(--color-text);
  line-height: 1.2;
}

.bill-status {
  display: inline-block;
  margin-top: 3px;
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-size: 10px;
  font-weight: 600;
}

.bill-status.settled {
  background: var(--theme-success-soft);
  color: var(--color-success);
}

.bill-status.unsettled {
  background: var(--theme-danger-soft);
  color: var(--color-danger);
}

.tab-empty {
  padding: 44px;
  text-align: center;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  font-size: 13px;
}

@media (max-width: 767px) {
  .tab-toolbar {
    align-items: flex-start;
    flex-direction: column;
    gap: 12px;
  }

  .toolbar-actions {
    width: 100%;
  }

  .toolbar-actions button {
    flex: 1;
    justify-content: center;
  }

  .bill-summary {
    padding: 16px;
  }

  .summary-value {
    font-size: 24px;
  }

  .bill-item {
    padding: 12px;
  }
}
</style>
