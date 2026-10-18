<template>
  <div class="as-apply">
    <section class="as-card">
      <div class="as-section-head">
        <div>
          <h2 class="as-section-head__title">{{ $t('afterSale.applyPanel.orderListTitle') }}</h2>
          <p class="as-section-head__sub">{{ $t('afterSale.applyPanel.orderListSubtitle') }}</p>
        </div>
        <el-button round size="small" :loading="ordersLoading" @click="fetchOrders">
          <el-icon class="as-mr"><Refresh /></el-icon>
          {{ $t('afterSale.applyPanel.refresh') }}
        </el-button>
      </div>

      <el-table
        v-loading="ordersLoading"
        :data="orders"
        style="width: 100%"
        :header-cell-style="headerStyle"
        :row-style="rowStyle"
        :empty-text="ordersLoading ? ' ' : $t('afterSale.applyPanel.noOrders')"
      >
        <el-table-column :label="$t('afterSale.table.item')" min-width="210">
          <template #default="{ row }">
            <div class="as-order-cell">
              <el-image
                v-if="row.image"
                :src="row.image"
                fit="cover"
                class="as-order-cell__img"
              />
              <span v-else class="as-order-cell__img as-order-cell__img--empty">
                <el-icon :size="18"><Goods /></el-icon>
              </span>
              <div class="as-order-cell__info">
                <div class="as-order-cell__name">{{ row.itemName || '-' }}</div>
                <div class="as-order-cell__meta">×{{ row.quantity ?? 1 }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.applyPanel.orderNo')" min-width="170">
          <template #default="{ row }">
            <span class="as-mono">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.applyPanel.orderAmount')" width="120">
          <template #default="{ row }">
            <span class="as-amount">¥{{ row.amount ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.applyPanel.orderStatus')" width="120">
          <template #default="{ row }">
            <span class="as-chip" :class="`as-chip--${orderStatusKey(row.status)}`">
              {{ orderStatusText(row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.applyPanel.afterSaleStatus')" width="120">
          <template #default="{ row }">
            <span class="as-text">{{ afterSaleStatusText(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.applyPanel.createTime')" width="170">
          <template #default="{ row }">
            <span class="as-time">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.table.actions')" width="170" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="canApply(row)"
              type="primary"
              size="small"
              round
              class="as-apply-btn"
              @click="openApply(row)"
            >
              {{ $t('afterSale.applyPanel.applyAction') }}
            </el-button>
            <span v-else class="as-order-cell__reason">{{ disabledReason(row) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <ApplyFormDialog v-model="applyVisible" :order="currentOrder" @success="onApplied" />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Goods, Refresh } from '@element-plus/icons-vue'
import { getOrderDetail, getOrderList } from '@/api/order.js'
import ApplyFormDialog from './ApplyFormDialog.vue'

const props = defineProps({
  orderId: { type: [Number, String], default: undefined }
})
const emit = defineEmits(['success'])

const { t, te } = useI18n()

const orders = ref([])
const ordersLoading = ref(false)
const applyVisible = ref(false)
const currentOrder = ref(null)
// 从订单页带 orderId 进入时只自动打开一次，刷新订单列表后不再重复打开
let autoSelected = false

// 仅已支付 / 已发货 / 已完成的订单可申请售后
const ELIGIBLE_ORDER_STATUS = ['1', '2', '3']

const isAfterSaleBusy = (order) =>
  Number(order.afterSaleStatus) === 1 || Number(order.afterSaleStatus) === 2

const canApply = (order) =>
  ELIGIBLE_ORDER_STATUS.includes(String(order.status)) && !isAfterSaleBusy(order)

const disabledReason = (order) => {
  const afterSaleStatus = Number(order.afterSaleStatus)
  if (afterSaleStatus === 1) return t('afterSale.applyDialog.orderInAfterSale')
  if (afterSaleStatus === 2) return t('afterSale.applyDialog.orderAfterSaleDone')
  return t('afterSale.applyDialog.orderStatusBlocked')
}

const orderStatusText = (order) => {
  const key = `afterSale.orderStatus.${order.status}`
  return te(key) ? t(key) : String(order.status ?? '-')
}

const afterSaleStatusText = (order) => {
  const key = `afterSale.orderAfterSaleStatus.${Number(order.afterSaleStatus) || 0}`
  return te(key) ? t(key) : '-'
}

const orderStatusKey = (status) => {
  if (String(status) === '3') return 'done'
  if (String(status) === '4' || String(status) === '5') return 'idle'
  if (String(status) === '0') return 'warn'
  return 'active'
}

const formatTime = (v) => (v ? String(v).replace('T', ' ') : '-')

const headerStyle = () => ({
  background: 'var(--theme-bg-surface-2)',
  color: 'var(--theme-text-2)',
  fontWeight: '600',
  fontSize: '13px'
})

const rowStyle = () => ({ fontSize: '13.5px' })

const openApply = (order) => {
  if (!canApply(order)) return
  currentOrder.value = order
  applyVisible.value = true
}

const tryOpenById = (id) => {
  if (!id || autoSelected) return
  autoSelected = true
  const order = orders.value.find((o) => String(o.id) === String(id))
  if (order) openApply(order)
}

// 拉取当前用户的全部订单（分页合并）
const fetchOrders = async () => {
  ordersLoading.value = true
  try {
    const list = []
    let pageNum = 1
    let total = 0
    do {
      const res = await getOrderList({ pageNum, pageSize: 100 })
      const page = res?.data ?? {}
      list.push(...(page.records ?? []))
      total = page.total ?? 0
      pageNum += 1
    } while (list.length < total && pageNum <= 10)

    // 从订单页跳转进入时，订单可能不在已拉取的分页里，单独补取
    if (props.orderId && !list.some((o) => String(o.id) === String(props.orderId))) {
      const res = await getOrderDetail(props.orderId)
      if (res?.data) list.unshift(res.data)
    }
    orders.value = list
    tryOpenById(props.orderId)
  } catch {
    orders.value = []
  } finally {
    ordersLoading.value = false
  }
}

const onApplied = async () => {
  currentOrder.value = null
  await fetchOrders()
  emit('success')
}

watch(
  () => props.orderId,
  (id) => {
    if (id && orders.value.length) tryOpenById(id)
  }
)

onMounted(fetchOrders)
</script>

<style scoped>
.as-apply {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-ink-3: var(--theme-text-3);
  --tn-accent: var(--theme-primary);
  --tn-line: var(--theme-hairline);
}

.as-mr {
  margin-right: 4px;
}

/* Card */
.as-card {
  position: relative;
  padding: 10px 10px 14px;
  border-radius: 22px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--tn-line);
  box-shadow: var(--theme-shadow-lg);
  overflow: hidden;
  animation: as-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) 0.05s both;
}

.as-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--theme-gradient);
  opacity: 0.9;
}

.as-card :deep(.el-table) {
  border-radius: 16px;
  --el-table-border-color: var(--theme-hairline);
  --el-table-row-hover-bg-color: var(--theme-primary-soft);
}

.as-card :deep(.el-table th.el-table__cell) {
  border-bottom: 1px solid var(--theme-hairline);
  letter-spacing: 0.04em;
}

.as-card :deep(.el-table td.el-table__cell) {
  padding: 11px 0;
}

.as-card :deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

.as-card :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.as-card :deep(.el-table__empty-text) {
  color: var(--tn-ink-3);
  font-size: 13px;
}

.as-section-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 12px 14px;
}

.as-section-head__title {
  margin: 0;
  font-size: 17px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--tn-ink);
}

.as-section-head__sub {
  margin: 4px 0 0;
  font-size: 12.5px;
  color: var(--tn-ink-3);
}

/* Order cell */
.as-order-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.as-order-cell__img {
  flex-shrink: 0;
  width: 46px;
  height: 46px;
  border-radius: 12px;
  border: 1px solid var(--tn-line);
}

.as-order-cell__img--empty {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--theme-bg-surface-2);
  color: var(--theme-primary);
}

.as-order-cell__info {
  min-width: 0;
}

.as-order-cell__name {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--tn-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.as-order-cell__meta {
  margin-top: 2px;
  font-size: 12px;
  color: var(--tn-ink-3);
}

.as-order-cell__reason {
  font-size: 12px;
  color: var(--tn-ink-3);
}

.as-apply-btn {
  min-width: 88px;
  border: none;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-weight: 600;
  box-shadow: 0 8px 18px -12px color-mix(in srgb, var(--theme-primary) 85%, transparent);
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.as-apply-btn:hover {
  color: var(--theme-primary-text);
  background: var(--theme-gradient);
  opacity: 0.94;
  transform: translateY(-1px);
  box-shadow: 0 12px 22px -12px color-mix(in srgb, var(--theme-primary) 90%, transparent);
}

.as-mono {
  font-variant-numeric: tabular-nums;
  font-weight: 600;
  color: var(--tn-ink);
}

.as-amount {
  font-variant-numeric: tabular-nums;
  font-weight: 700;
  color: var(--tn-ink);
}

.as-time {
  font-size: 12.5px;
  color: var(--tn-ink-3);
}

.as-text {
  font-size: 13px;
  color: var(--tn-ink-2);
}

/* Chips */
.as-chip {
  display: inline-flex;
  align-items: center;
  padding: 3px 11px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.5;
  border: 1px solid transparent;
  white-space: nowrap;
}

.as-chip--idle {
  background: var(--theme-bg-surface-2);
  border-color: var(--theme-hairline);
  color: var(--theme-text-2);
}
.as-chip--active {
  background: var(--theme-primary-soft);
  border-color: color-mix(in srgb, var(--theme-primary) 22%, transparent);
  color: var(--theme-primary);
}
.as-chip--warn {
  background: var(--theme-warning-soft);
  border-color: color-mix(in srgb, var(--theme-warning) 24%, transparent);
  color: var(--theme-warning);
}
.as-chip--done {
  background: var(--theme-success-soft);
  border-color: color-mix(in srgb, var(--theme-success) 24%, transparent);
  color: var(--theme-success);
}

@keyframes as-rise {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
