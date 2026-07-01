<template>
  <div class="bills-tab">
    <div class="tab-actions">
      <button class="btn-small" @click="$emit('add')">添加账单</button>
      <button class="btn-small btn-secondary" @click="$emit('settlement')">查看结算</button>
    </div>
    <div class="bill-list">
      <div v-for="bill in bills" :key="bill.id" class="bill-item">
        <div class="bill-info">
          <span class="bill-title">{{ bill.title }}</span>
          <span class="bill-payer">付款人：{{ bill.payerName }}</span>
        </div>
        <div class="bill-amount">
          <span class="amount">¥{{ bill.amount }}</span>
          <span v-if="bill.isSettled" class="settled">已结清</span>
          <span v-else class="unsettled">未结清</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  bills: {
    type: Array,
    default: () => []
  }
})

defineEmits(['add', 'settlement'])
</script>

<style scoped>
.bills-tab {
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.tab-actions {
  display: flex;
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-20);
}

.tab-actions button {
  border: none;
  padding: var(--spacing-8) var(--spacing-16);
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-4);
  background: var(--color-primary);
  color: #fff;
}

.tab-actions button:hover {
  background: var(--color-primary-hover);
}

.tab-actions button:active {
  transform: scale(0.98);
}

.tab-actions .btn-secondary {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

.tab-actions .btn-secondary:hover {
  background: rgba(0, 0, 0, 0.04);
}

.bill-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-12);
}

.bill-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-16);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  transition: all var(--transition-normal);
}

.bill-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.bill-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.bill-title {
  font-weight: 500;
  font-size: 15px;
}

.bill-payer {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.bill-amount {
  text-align: right;
}

.amount {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: var(--color-primary);
  line-height: 1.2;
}

.settled {
  font-size: 12px;
  color: var(--color-success);
  font-weight: 500;
}

.unsettled {
  font-size: 12px;
  color: var(--color-danger);
  font-weight: 500;
}
</style>