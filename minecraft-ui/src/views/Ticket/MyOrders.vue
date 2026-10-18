<template>
  <div class="rw-page">
    <div class="rw-page-inner">
      <div class="rw-page-title">
        <h1>{{ $t('railway.orders.title') }}</h1>
        <router-link to="/ticket" class="rw-back">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('railway.common.back') }}
        </router-link>
      </div>

      <!-- 状态筛选 -->
      <div class="rw-tabs">
        <button v-for="tab in tabs" :key="tab.value" type="button" class="rw-tab"
          :class="{ 'is-active': status === tab.value }" @click="changeStatus(tab.value)">
          {{ $t(`railway.orders.${tab.label}`) }}
        </button>
      </div>

      <!-- 加载 -->
      <div v-if="loading" class="rw-state">
        <span class="rw-state-icon"><el-icon class="is-loading"><Loading /></el-icon></span>
        <p>{{ $t('railway.common.loading') }}</p>
      </div>

      <!-- 错误 -->
      <div v-else-if="error" class="rw-state">
        <span class="rw-state-icon"><el-icon><Warning /></el-icon></span>
        <h3>{{ $t('railway.orders.loadFailed') }}</h3>
        <p>{{ error }}</p>
        <button type="button" class="rw-state-btn" @click="load">{{ $t('railway.list.retry') }}</button>
      </div>

      <!-- 空 -->
      <div v-else-if="orders.length === 0" class="rw-state">
        <span class="rw-state-icon"><el-icon><Tickets /></el-icon></span>
        <h3>{{ $t('railway.orders.empty') }}</h3>
        <p>{{ $t('railway.orders.emptyDesc') }}</p>
        <button type="button" class="rw-state-btn" @click="$router.push('/ticket')">
          {{ $t('railway.orders.goBook') }}
        </button>
      </div>

      <template v-else>
        <div class="rw-order-list">
          <article v-for="order in orders" :key="order.id" class="rw-order-card">
            <div class="rw-order-head">
              <div class="rw-order-head-left">
                <strong class="rw-order-train">{{ order.trainNo }}</strong>
                <span class="rw-order-date">{{ order.serviceDate }}</span>
                <span class="rw-order-type">{{ $t(`railway.list.type.${order.trainType}`) }}</span>
              </div>
              <span class="rw-status" :class="`rw-status--${order.status}`">
                {{ $t(`railway.orders.${order.status}`) }}
              </span>
            </div>

            <div class="rw-order-route">
              <div class="rw-order-end">
                <span class="station">{{ order.fromStationName }}</span>
                <span class="time">{{ order.fromTime }}</span>
              </div>
              <div class="rw-order-line">
                <span class="dash"></span>
                <em>{{ $t(`railway.list.seat.${order.seatType}`) }}</em>
                <span class="dash"></span>
              </div>
              <div class="rw-order-end rw-order-end--right">
                <span class="station">
                  {{ order.toStationName }}
                  <i v-if="order.arriveDayOffset > 0" class="rw-nextday">{{ $t('railway.list.nextDay') }}</i>
                </span>
                <span class="time">{{ order.toTime }}</span>
              </div>
            </div>

            <div class="rw-order-tickets">
              <span v-for="ticket in order.tickets" :key="ticket.id" class="rw-ticket-chip">
                <em>{{ ticket.passengerName }}</em>
                <small>{{ ticket.seatNo }}</small>
              </span>
            </div>

            <div class="rw-order-foot">
              <div class="rw-order-meta">
                <span>{{ $t('railway.orders.orderNo') }}：{{ order.orderNo }}</span>
                <span v-if="order.status === 'PENDING'" class="rw-expire">
                  {{ $t('railway.orders.expireHint', { time: formatClock(remainingOf(order)) }) }}
                </span>
              </div>
              <div class="rw-order-actions">
                <span class="rw-order-amount">
                  <small>{{ $t('railway.orders.passengerCount', { n: order.passengerCount }) }}</small>
                  <strong>¥{{ formatPrice(order.totalAmount) }}</strong>
                </span>
                <template v-if="order.status === 'PENDING'">
                  <el-button size="small" @click="handleCancel(order, false)">
                    {{ $t('railway.orders.cancel') }}
                  </el-button>
                  <el-button size="small" type="primary" @click="handlePay(order)">
                    {{ $t('railway.orders.pay') }}
                  </el-button>
                </template>
                <template v-else-if="order.status === 'PAID'">
                  <el-button size="small" @click="handleCancel(order, true)">
                    {{ $t('railway.orders.refund') }}
                  </el-button>
                </template>
              </div>
            </div>
          </article>
        </div>

        <div v-if="total > pageSize" class="rw-pagination">
          <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize"
            :current-page="pageNum" @current-change="handlePageChange" />
        </div>
      </template>
    </div>

    <!-- 系统支付面板 -->
    <div v-if="payVisible" class="rw-pay-overlay" @click.self="closePay">
      <div class="rw-pay-modal" @click.stop>
        <button type="button" class="rw-pay-close" :aria-label="$t('railway.common.close')" @click="closePay">
          <el-icon><Close /></el-icon>
        </button>
        <PayPage order-type="railway" :order-id="String(payOrderInfo?.id || '')" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelRailwayOrder,
  getRailwayOrders,
} from '@/api/railway'
import PayPage from '@/components/Payment/PayPage.vue'
import { formatClock, formatPrice, remainingSeconds } from './utils'

const { t } = useI18n()

const tabs = [
  { value: '', label: 'all' },
  { value: 'PENDING', label: 'PENDING' },
  { value: 'PAID', label: 'PAID' },
  { value: 'CANCELLED', label: 'CANCELLED' },
  { value: 'REFUNDED', label: 'REFUNDED' },
]

const status = ref('')
const orders = ref([])
const loading = ref(false)
const error = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 秒级刷新待支付倒计时
const now = ref(Date.now())
let ticker = null

const payVisible = ref(false)
const payOrderInfo = ref(null)

const remainingOf = (order) => {
  // 读取 now 以便倒计时每秒响应式更新
  void now.value
  return remainingSeconds(order.expireTime)
}

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await getRailwayOrders({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: status.value || undefined,
    })
    orders.value = res?.data?.records || []
    total.value = Number(res?.data?.total || 0)
  } catch (err) {
    error.value = err?.message || ''
    orders.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const onPayMessage = (event) => {
  if (event.data?.action === 'closeModal' && payVisible.value) {
    closePay()
  }
}

onMounted(() => {
  load()
  window.addEventListener('message', onPayMessage)
  ticker = setInterval(() => {
    now.value = Date.now()
  }, 1000)
})

onBeforeUnmount(() => {
  if (ticker) clearInterval(ticker)
  window.removeEventListener('message', onPayMessage)
})

const changeStatus = (value) => {
  status.value = value
  pageNum.value = 1
  load()
}

const handlePageChange = (page) => {
  pageNum.value = page
  load()
}

const handlePay = (order) => {
  payOrderInfo.value = order
  payVisible.value = true
}

const closePay = () => {
  payVisible.value = false
  payOrderInfo.value = null
  load()
}

const handleCancel = async (order, isRefund) => {
  try {
    await ElMessageBox.confirm(
      isRefund ? t('railway.orders.refundConfirm') : t('railway.orders.cancelConfirm'),
      isRefund ? t('railway.orders.refund') : t('railway.orders.cancel'),
      {
        confirmButtonText: t('railway.common.confirm'),
        cancelButtonText: t('railway.common.cancel'),
        type: 'warning',
      },
    )
    await cancelRailwayOrder(order.id)
    ElMessage.success(isRefund ? t('railway.orders.refundSuccess') : t('railway.orders.cancelSuccess'))
    await load()
  } catch {
    // 取消操作
  }
}
</script>

<style scoped>
.rw-page {
  padding: 30px 24px 0;
}

.rw-page-inner {
  max-width: 1000px;
  margin: 0 auto;
}

.rw-page-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.rw-page-title h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--rw-text);
}

.rw-back {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--rw-text-2);
  text-decoration: none;
}

.rw-back:hover {
  color: var(--rw-blue);
}

/* 状态标签 */
.rw-tabs {
  display: flex;
  gap: 6px;
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 14px;
  padding: 6px;
  margin-bottom: 18px;
  overflow-x: auto;
}

.rw-tab {
  border: none;
  background: none;
  padding: 8px 18px;
  border-radius: 10px;
  font-size: 13px;
  color: var(--rw-text-2);
  cursor: pointer;
  font-family: inherit;
  white-space: nowrap;
  transition: background 0.2s ease, color 0.2s ease;
}

.rw-tab:hover {
  color: var(--rw-blue);
}

.rw-tab.is-active {
  background: var(--rw-blue);
  color: #ffffff;
  font-weight: 600;
}

/* 订单卡片 */
.rw-order-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.rw-order-card {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 16px;
  padding: 20px 22px;
  transition: box-shadow 0.2s ease;
}

.rw-order-card:hover {
  box-shadow: 0 18px 36px -32px rgba(16, 47, 100, 0.55);
}

.rw-order-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 14px;
  border-bottom: 1px dashed var(--rw-border);
}

.rw-order-head-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rw-order-train {
  font-size: 17px;
  font-weight: 700;
  color: var(--rw-text);
}

.rw-order-date {
  font-size: 12.5px;
  color: var(--rw-text-2);
}

.rw-order-type {
  font-size: 10.5px;
  color: var(--rw-blue);
  background: var(--rw-soft);
  padding: 2px 8px;
  border-radius: 999px;
}

.rw-status {
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.rw-status--PENDING {
  color: var(--rw-orange);
  background: rgba(255, 138, 31, 0.12);
}

.rw-status--PAID {
  color: var(--rw-success);
  background: rgba(16, 185, 129, 0.12);
}

.rw-status--CANCELLED {
  color: var(--rw-text-3);
  background: var(--rw-soft);
}

.rw-status--REFUNDED {
  color: #8b7cf6;
  background: rgba(139, 124, 246, 0.12);
}

/* 路线 */
.rw-order-route {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0 6px;
}

.rw-order-end {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 92px;
}

.rw-order-end--right {
  align-items: flex-end;
  text-align: right;
}

.rw-order-end .station {
  font-size: 15px;
  font-weight: 600;
  color: var(--rw-text);
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.rw-order-end .time {
  font-size: 20px;
  font-weight: 700;
  color: var(--rw-text);
  font-variant-numeric: tabular-nums;
}

.rw-order-line {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 70px;
}

.rw-order-line .dash {
  flex: 1;
  height: 1px;
  background: repeating-linear-gradient(90deg, var(--rw-border) 0 5px, transparent 5px 10px);
}

.rw-order-line em {
  font-style: normal;
  font-size: 11px;
  color: var(--rw-blue);
  background: var(--rw-soft);
  padding: 2px 8px;
  border-radius: 999px;
  white-space: nowrap;
}

.rw-nextday {
  font-style: normal;
  font-size: 10px;
  font-weight: 600;
  color: var(--rw-orange);
  background: rgba(255, 138, 31, 0.12);
  padding: 1px 5px;
  border-radius: 4px;
}

/* 车票 */
.rw-order-tickets {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 0 12px;
}

.rw-ticket-chip {
  display: inline-flex;
  align-items: baseline;
  gap: 7px;
  padding: 5px 12px;
  border-radius: 8px;
  background: var(--rw-soft-2);
  border: 1px solid var(--rw-border);
  font-size: 12px;
}

.rw-ticket-chip em {
  font-style: normal;
  color: var(--rw-text);
  font-weight: 500;
}

.rw-ticket-chip small {
  color: var(--rw-text-3);
  font-size: 11px;
}

/* 底部 */
.rw-order-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-top: 14px;
  border-top: 1px dashed var(--rw-border);
  flex-wrap: wrap;
}

.rw-order-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 11.5px;
  color: var(--rw-text-3);
}

.rw-expire {
  color: var(--rw-orange);
}

.rw-order-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.rw-order-amount {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-right: 6px;
}

.rw-order-amount small {
  font-size: 11.5px;
  color: var(--rw-text-3);
}

.rw-order-amount strong {
  font-size: 20px;
  font-weight: 700;
  color: var(--rw-orange);
  font-variant-numeric: tabular-nums;
}

/* 分页 */
.rw-pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 状态占位 */
.rw-state {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 18px;
  padding: 60px 24px;
  text-align: center;
  color: var(--rw-text-2);
}

.rw-state-icon {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  margin-bottom: 14px;
}

.rw-state h3 {
  margin: 0 0 6px;
  font-size: 17px;
  color: var(--rw-text);
}

.rw-state p {
  margin: 0 0 18px;
  font-size: 13px;
}

.rw-state-btn {
  border: none;
  background: var(--rw-blue);
  color: #ffffff;
  padding: 9px 24px;
  border-radius: 999px;
  font-size: 13px;
  cursor: pointer;
  font-family: inherit;
}

/* 系统支付面板 */
.rw-pay-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
}

.rw-pay-modal {
  position: relative;
  width: min(500px, 94vw);
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 22px;
}

/* 支付组件默认以 fixed 居中，这里改为随弹层流式排布 */
.rw-pay-modal :deep(.glass-pay-container) {
  position: static;
  transform: none;
  min-height: auto;
  z-index: auto;
}

.rw-pay-close {
  position: absolute;
  top: 14px;
  right: 14px;
  z-index: 10;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: var(--theme-bg-hover);
  color: var(--theme-text-2);
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
}

.rw-pay-close:hover {
  background: var(--theme-bg-active);
  color: var(--theme-text-1);
  transform: rotate(90deg);
}

@media (max-width: 640px) {
  .rw-page {
    padding: 16px 14px 0;
  }

  .rw-page-title h1 {
    font-size: 20px;
  }

  .rw-order-card {
    padding: 16px;
  }

  .rw-order-route {
    gap: 8px;
  }

  .rw-order-end {
    min-width: 70px;
  }

  .rw-order-end .time {
    font-size: 18px;
  }

  .rw-order-foot {
    flex-direction: column;
    align-items: flex-start;
  }

  .rw-order-actions {
    margin-left: 0;
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
