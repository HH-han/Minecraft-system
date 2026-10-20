<template>
  <div v-if="hasOrderData" class="order-detail">
    <!-- 订单概览 -->
    <header class="od-hero theme-transition">
      <span class="od-hero__glow" aria-hidden="true"></span>

      <div class="od-hero__body">
        <div class="od-hero__main">
          <span class="od-hero__eyebrow">{{ $t('payment.orderNo') }}</span>

          <div class="od-hero__number">
            <span class="od-hero__no">{{ order.orderNo || '-' }}</span>
            <button
              v-if="order.orderNo"
              type="button"
              class="od-copy"
              :title="$t('payment.orderDetail.copy')"
              :aria-label="$t('payment.orderDetail.copy')"
              @click="copyText(order.orderNo)"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <rect x="9" y="9" width="13" height="13" rx="2"></rect>
                <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
              </svg>
            </button>
          </div>

          <div class="od-hero__tags">
            <span class="od-tag">{{ getItemTypeText(order.itemType) }}</span>
            <span v-if="order.createTime" class="od-tag od-tag--ghost">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              {{ $t('payment.orderDetail.createTime') }} · {{ order.createTime }}
            </span>
            <span v-if="order.payTime" class="od-tag od-tag--ghost">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
              {{ $t('payment.orderDetail.payTime') }} · {{ order.payTime }}
            </span>
          </div>
        </div>

        <div class="od-hero__aside">
          <span class="od-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </span>
          <div class="od-amount">
            <span class="od-amount__label">{{ $t('payment.orderDetail.amountLabel') }}</span>
            <span class="od-amount__value">{{ priceText(order.amount) }}</span>
          </div>
        </div>
      </div>

      <!-- 订单进度 -->
      <div v-if="progressSteps.length" class="od-steps">
        <span class="od-steps__caption">{{ $t('payment.orderDetail.orderProgress') }}</span>
        <ol class="od-steps__list">
          <li
            v-for="step in progressSteps"
            :key="step.key"
            class="od-step"
            :class="{ 'is-done': step.done, 'is-current': step.current }"
          >
            <span class="od-step__dot">
              <svg
                v-if="step.done && !step.current"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="3"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </span>
            <span class="od-step__label">{{ $t(step.label) }}</span>
          </li>
        </ol>
      </div>
    </header>

    <div class="od-layout">
      <div class="od-main">
        <!-- 商品信息 -->
        <section class="od-card">
          <div class="od-card__head">
            <span class="od-card__icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"></path>
                <path d="M3 6h18"></path>
                <path d="M16 10a4 4 0 0 1-8 0"></path>
              </svg>
            </span>
            <h3 class="od-card__title">{{ $t('payment.orderDetail.goodsInfo') }}</h3>
            <span class="od-card__count">
              {{ $t('payment.orderDetail.goodsQuantityUnit', { count: order.quantity }) }}
            </span>
          </div>

          <div class="od-items">
            <article v-for="item in order.items" :key="item.id" class="od-item">
              <div class="od-item__thumb">
                <img v-if="item.image" :src="item.image" :alt="item.name" loading="lazy">
                <span v-else class="od-item__thumb-empty">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="3" width="18" height="18" rx="2"></rect>
                    <circle cx="9" cy="9" r="2"></circle>
                    <path d="m21 15-3.09-3.09a2 2 0 0 0-2.82 0L6 21"></path>
                  </svg>
                </span>
              </div>
              <div class="od-item__body">
                <p class="od-item__name">{{ item.name }}</p>
                <div class="od-item__meta">
                  <span class="od-item__price">{{ priceText(item.price) }}</span>
                  <span class="od-item__qty">×{{ item.quantity }}</span>
                </div>
              </div>
            </article>
            <p v-if="!order.items.length" class="od-items__empty">-</p>
          </div>

          <div v-if="order.remark" class="od-remark">
            <span class="od-remark__tag">{{ $t('payment.orderDetail.remark') }}</span>
            <span class="od-remark__text">{{ order.remark }}</span>
          </div>
        </section>

        <!-- 支付信息（聚合支付网关） -->
        <section v-if="paymentInfo" class="od-card">
          <div class="od-card__head">
            <span class="od-card__icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <rect x="1" y="4" width="22" height="16" rx="3"></rect>
                <line x1="1" y1="10" x2="23" y2="10"></line>
              </svg>
            </span>
            <h3 class="od-card__title">{{ $t('payment.orderDetail.paymentInfo') }}</h3>
          </div>

          <div class="od-fields">
            <div class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.payNo') }}</span>
              <span class="od-field__value od-field__value--mono">
                {{ paymentInfo.payNo || '-' }}
                <button
                  v-if="paymentInfo.payNo"
                  type="button"
                  class="od-copy od-copy--sm"
                  :title="$t('payment.orderDetail.copy')"
                  :aria-label="$t('payment.orderDetail.copy')"
                  @click="copyText(paymentInfo.payNo)"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="9" y="9" width="13" height="13" rx="2"></rect>
                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                  </svg>
                </button>
              </span>
            </div>
            <div class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.purpose') }}</span>
              <span class="od-field__value">{{ paymentPurpose }}</span>
            </div>
            <div class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.channel') }}</span>
              <span class="od-field__value">{{ getChannelText(paymentInfo.channelCode) }}</span>
            </div>
            <div class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.payStatus') }}</span>
              <span class="od-pill">{{ getPayStatusText(paymentInfo) }}</span>
            </div>
            <div class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.totalAmount') }}</span>
              <span class="od-field__value">{{ priceText(paymentInfo.totalAmount) }}</span>
            </div>
            <div v-if="Number(paymentInfo.pointsDeduct) > 0" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.pointsDeduct') }}</span>
              <span class="od-field__value od-field__value--deduct">
                -¥{{ formatMoney(paymentInfo.pointsDeduct) }}
              </span>
            </div>
            <div class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.payAmount') }}</span>
              <span class="od-field__value od-field__value--accent">{{ priceText(paymentInfo.payAmount) }}</span>
            </div>
            <div v-if="Number(paymentInfo.refundedAmount) > 0" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.refundedAmount') }}</span>
              <span class="od-field__value od-field__value--deduct">
                ¥{{ formatMoney(paymentInfo.refundedAmount) }}
              </span>
            </div>
            <div v-if="paymentInfo.payTime" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.payTime') }}</span>
              <span class="od-field__value">{{ formatTime(paymentInfo.payTime) }}</span>
            </div>
            <div v-if="paymentInfo.channelTradeNo" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.tradeNo') }}</span>
              <span class="od-field__value od-field__value--mono">{{ paymentInfo.channelTradeNo }}</span>
            </div>
          </div>
        </section>

        <!-- 收货地址 -->
        <section v-if="isShippableOrder" class="od-card">
          <div class="od-card__head">
            <span class="od-card__icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0Z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
            </span>
            <h3 class="od-card__title">{{ $t('payment.orderDetail.shippingAddress') }}</h3>
          </div>

          <div class="od-address">
            <span class="od-address__avatar">{{ addressInitial }}</span>
            <div class="od-address__body">
              <div class="od-address__line">
                <span class="od-address__name">{{ order.address.name }}</span>
                <span v-if="order.address.phone" class="od-address__phone">
                  {{ order.address.phone }}
                </span>
              </div>
              <p class="od-address__text">{{ order.address.detail }}</p>
            </div>
          </div>
        </section>

        <!-- 物流信息 -->
        <section v-if="hasShippingInfo" class="od-card">
          <div class="od-card__head">
            <span class="od-card__icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 17V7a1 1 0 0 0-1-1H2a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h1"></path>
                <path d="M14 9h4l4 4v4h-2"></path>
                <circle cx="6.5" cy="18" r="2"></circle>
                <circle cx="17.5" cy="18" r="2"></circle>
              </svg>
            </span>
            <h3 class="od-card__title">{{ $t('payment.orderDetail.shippingInfo') }}</h3>
          </div>

          <div class="od-fields">
            <div class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.shippingStatus') }}</span>
              <span class="od-pill">{{ getShippingStatusText(order.shippingStatus) }}</span>
            </div>
            <div v-if="order.carrierName" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.carrier') }}</span>
              <span class="od-field__value">{{ order.carrierName }}</span>
            </div>
            <div v-if="order.trackingNo" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.trackingNo') }}</span>
              <span class="od-field__value od-field__value--mono">
                {{ order.trackingNo }}
                <button
                  type="button"
                  class="od-copy od-copy--sm"
                  :title="$t('payment.orderDetail.copy')"
                  :aria-label="$t('payment.orderDetail.copy')"
                  @click="copyText(order.trackingNo)"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="9" y="9" width="13" height="13" rx="2"></rect>
                    <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
                  </svg>
                </button>
              </span>
            </div>
            <div v-if="order.shipTime" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.shipTime') }}</span>
              <span class="od-field__value">{{ order.shipTime }}</span>
            </div>
            <div v-if="order.receiveTime" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.receiveTime') }}</span>
              <span class="od-field__value">{{ order.receiveTime }}</span>
            </div>
            <div v-if="order.afterSaleStatus > 0" class="od-field">
              <span class="od-field__label">{{ $t('payment.orderDetail.afterSaleStatus') }}</span>
              <span class="od-pill od-pill--warn">{{ getAfterSaleText(order.afterSaleStatus) }}</span>
            </div>
          </div>
        </section>
      </div>

      <!-- 结算侧栏 -->
      <aside class="od-side">
        <section class="od-card od-summary">
          <div class="od-card__head">
            <span class="od-card__icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2l-2 1-2-1-2 1-2-1-2 1-2-1-2 1Z"></path>
                <line x1="8" y1="8" x2="16" y2="8"></line>
                <line x1="8" y1="12" x2="16" y2="12"></line>
                <line x1="8" y1="16" x2="13" y2="16"></line>
              </svg>
            </span>
            <h3 class="od-card__title">{{ $t('payment.orderDetail.costBreakdown') }}</h3>
          </div>

          <div class="od-summary__rows">
            <div class="od-summary__row">
              <span>{{ $t('payment.orderDetail.goodsQuantity') }}</span>
              <span>{{ $t('payment.orderDetail.goodsQuantityUnit', { count: order.quantity }) }}</span>
            </div>
            <template v-if="!isPointsOrder">
              <div class="od-summary__row">
                <span>{{ $t('payment.orderDetail.goodsTotal') }}</span>
                <span>¥{{ formatMoney(order.amount) }}</span>
              </div>
              <div class="od-summary__row">
                <span>{{ $t('payment.orderDetail.freight') }}</span>
                <span>¥{{ formatMoney(order.freight) }}</span>
              </div>
            </template>
          </div>

          <div class="od-summary__total">
            <span class="od-summary__total-label">{{ $t('payment.orderDetail.actuallyPaid') }}</span>
            <span class="od-summary__total-value">{{ priceText(order.amount) }}</span>
          </div>

          <div v-if="order.status === '0'" class="od-actions">
            <button type="button" class="od-btn od-btn--ghost" @click="handleCancelOrder">
              {{ $t('payment.orderDetail.cancelOrder') }}
            </button>
            <button type="button" class="od-btn od-btn--primary" @click="goToPay">
              {{ $t('payment.orderDetail.payNowAction') }}
            </button>
          </div>
          <div v-else-if="canViewLogistics" class="od-actions">
            <button type="button" class="od-btn od-btn--primary" @click="viewLogistics">
              {{ $t('payment.orderDetail.viewLogistics') }}
            </button>
          </div>
        </section>
      </aside>
    </div>
  </div>

  <!-- 无订单数据 -->
  <div v-else class="no-order">
    <div class="no-order__card">
      <span class="no-order__icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round">
          <path d="m7.5 4.27 9 5.15"></path>
          <path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"></path>
          <path d="m3.3 7 8.7 5 8.7-5"></path>
          <path d="M12 22V12"></path>
        </svg>
      </span>
      <h3 class="no-order__title">{{ $t('payment.orderDetail.noData') }}</h3>
      <p class="no-order__desc">{{ $t('payment.orderDetail.noDataDesc') }}</p>
      <button type="button" class="od-btn od-btn--primary no-order__btn" @click="goToHome">
        {{ $t('payment.orderDetail.backHome') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getFoodDetail } from '@/api/food.js'
import { getProductDetail } from '@/api/product.js'
import { getOrderDetail, cancelOrder } from '@/api/order.js'
import { queryPayment } from '@/api/payment.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const emit = defineEmits(['pay'])
const router = useRouter()
const route = useRoute()
const { t, te } = useI18n()

// 国际化键存在时取翻译，否则回退到后端返回值
const tOr = (key, fallback) => (te(key) ? t(key) : fallback)

// 承运商代码 → 名称
const CARRIER_MAP = {
  YTO: 'payment.orderDetail.carriers.YTO',
  ZTO: 'payment.orderDetail.carriers.ZTO',
  STO: 'payment.orderDetail.carriers.STO',
  YD: 'payment.orderDetail.carriers.YD',
  SF: 'payment.orderDetail.carriers.SF',
  JD: 'payment.orderDetail.carriers.JD',
  EMS: 'payment.orderDetail.carriers.EMS',
  YZPY: 'payment.orderDetail.carriers.YZPY',
  DBL: 'payment.orderDetail.carriers.DBL'
}

// 承运商名称（未知代码回退后端返回值）
const getCarrierName = (code) => (CARRIER_MAP[code] ? t(CARRIER_MAP[code]) : '')

// 订单默认结构
const createDefaultOrder = () => ({
  orderNo: '',
  status: '',
  itemType: '',
  createTime: '',
  payTime: '',
  amount: 0,
  quantity: 1,
  freight: 0,
  remark: '',
  address: { name: t('payment.orderDetail.unfilled'), phone: '', detail: t('payment.orderDetail.noAddress') },
  items: [],
  carrierCode: '',
  carrierName: '',
  trackingNo: '',
  shippingStatus: null,
  shipTime: '',
  receiveTime: '',
  afterSaleStatus: 0
})

const order = ref(createDefaultOrder())

// 订单数据是否存在
const hasOrderData = ref(false)

// 聚合支付单信息（来自后端 /api/pay/query）
const paymentInfo = ref(null)

// 支付用途：业务类型 + 支付标题（购买了什么、钱花在哪）
const paymentPurpose = computed(() => {
  if (!paymentInfo.value) return ''
  const bizType = tOr(
    `payment.orderDetail.bizTypes.${paymentInfo.value.bizType}`,
    paymentInfo.value.bizType || ''
  )
  const subject = paymentInfo.value.subject || order.value.items[0]?.name || ''
  return subject ? `${bizType} · ${subject}` : bizType || '-'
})

// 积分兑换订单（无实际支付金额）
const isPointsOrder = computed(() => order.value.itemType === 'points')

// 是否使用物流系统：仅纪念品等实物商品（酒店/景点/美食等不发货）
const SHIPPABLE_ITEM_TYPES = ['product', 'points']
const isShippableOrder = computed(() =>
  SHIPPABLE_ITEM_TYPES.includes(String(order.value.itemType || '').toLowerCase())
)

// 是否展示物流信息：实物商品且已进入物流环节
const hasShippingInfo = computed(
  () =>
    isShippableOrder.value &&
    ['1', '2', '3'].includes(order.value.status) &&
    ((order.value.shippingStatus !== null && order.value.shippingStatus !== undefined) ||
      !!order.value.trackingNo)
)

// 是否可查看物流
const canViewLogistics = computed(
  () => isShippableOrder.value && ['1', '2', '3'].includes(order.value.status)
)

// 订单进度：已取消 / 已退款不展示进度条
const progressSteps = computed(() => {
  const status = String(order.value.status || '')
  if (['4', '5'].includes(status)) return []

  const current = Number(status) || 0
  const steps = [
    { key: 'unpaid', label: 'payment.history.status.unpaid', level: 0 },
    { key: 'paid', label: 'payment.history.status.paid', level: 1 }
  ]
  // 实物商品才有发货环节
  if (isShippableOrder.value) {
    steps.push({ key: 'shipped', label: 'payment.history.status.shipped', level: 2 })
  }
  steps.push({ key: 'completed', label: 'payment.history.status.completed', level: 3 })

  return steps.map((step) => ({
    ...step,
    done: current >= step.level,
    current: current === step.level
  }))
})

// 收货人首字（地址卡头像）
const addressInitial = computed(() => {
  const name = String(order.value.address?.name || '').trim()
  return name ? name.slice(0, 1).toUpperCase() : '?'
})

// 时间格式化：2026-10-11T16:16:02 → 2026-10-11 16:16:02
const formatTime = (time) => (time ? String(time).replace('T', ' ') : '')

// 金额格式化：整数不带小数，非整数保留两位
const formatMoney = (value) => {
  const num = Number(value || 0)
  return Number.isInteger(num) ? String(num) : num.toFixed(2)
}

// 金额展示：积分订单不显示金额
const priceText = (value) => (isPointsOrder.value ? t('payment.points') : `¥${formatMoney(value)}`)

const getItemTypeText = (type) => {
  const map = {
    food: 'payment.orderDetail.itemTypes.food',
    product: 'payment.orderDetail.itemTypes.product',
    points: 'payment.orderDetail.itemTypes.points',
    hotel: 'payment.orderDetail.itemTypes.hotel',
    attraction: 'payment.orderDetail.itemTypes.attraction'
  }
  return map[type] ? t(map[type]) : t('payment.orderDetail.itemTypes.goods')
}

const getStatusText = (status) => {
  const statusMap = {
    '0': 'payment.history.status.unpaid',
    '1': 'payment.history.status.paid',
    '2': 'payment.history.status.shipped',
    '3': 'payment.history.status.completed',
    '4': 'payment.history.status.cancelled',
    '5': 'payment.history.status.refunded'
  }
  return statusMap[status] ? t(statusMap[status]) : t('payment.common.unknownStatus')
}

const getStatusClass = (status) => {
  const classMap = {
    '0': 'unpaid',
    '1': 'paid',
    '2': 'shipping',
    '3': 'completed',
    '4': 'cancelled',
    '5': 'refunded'
  }
  return classMap[status] || 'unpaid'
}

// 物流状态 0-待发货 1-部分发货 2-已发货 3-已签收
const getShippingStatusText = (status) => {
  const map = {
    0: 'payment.orderDetail.shippingStatusLabels.0',
    1: 'payment.orderDetail.shippingStatusLabels.1',
    2: 'payment.orderDetail.shippingStatusLabels.2',
    3: 'payment.orderDetail.shippingStatusLabels.3'
  }
  return t(map[status] ?? 'payment.orderDetail.shippingStatusLabels.0')
}

// 售后状态 0-无 1-售后中 2-售后完成
const getAfterSaleText = (status) => {
  const map = {
    1: 'payment.orderDetail.afterSale.processing',
    2: 'payment.orderDetail.afterSale.completed'
  }
  return t(map[status] || 'payment.orderDetail.afterSale.none')
}

// 支付渠道文案
const getChannelText = (code) => {
  const key = `payment.orderDetail.channels.${String(code || '').toLowerCase()}`
  return tOr(key, code || '-')
}

// 支付状态文案（优先国际化，后端 statusDesc 兜底）
const getPayStatusText = (payment) => {
  const status = String(payment?.status || '')
  return tOr(`payment.orderDetail.statusLabels.${status}`, payment?.statusDesc || status || '-')
}

// 复制文本
const copyText = async (text) => {
  try {
    await navigator.clipboard.writeText(String(text))
    ElMessage.success(t('payment.orderDetail.copied'))
  } catch {
    ElMessage.warning(t('payment.orderDetail.copyFailed'))
  }
}

// 将后端订单数据映射为页面数据
const normalizeOrder = (data) => {
  const status = data.status != null ? String(data.status) : ''
  const amount = Number(data.amount) || 0
  return {
    orderNo: data.orderNo || (data.id != null ? String(data.id) : ''),
    status,
    itemType: data.itemType || '',
    payNo: data.payNo || '',
    createTime: formatTime(data.createTime),
    // 后端无独立支付时间字段，非待支付订单以更新时间近似
    payTime: formatTime(data.payTime || (status !== '0' ? data.updateTime : '')),
    amount,
    quantity: data.quantity || 1,
    freight: Number(data.freight) || 0,
    remark: data.remark || '',
    address: {
      name: data.receiverName || t('payment.orderDetail.unfilled'),
      phone: data.receiverPhone || '',
      detail: data.receiverAddress || t('payment.orderDetail.noAddress')
    },
    items: data.itemName
      ? [
          {
            id: data.itemId,
            name: data.itemName,
            price: amount,
            quantity: data.quantity || 1,
            image: data.image
          }
        ]
      : [],
    carrierCode: data.carrierCode || '',
    carrierName: getCarrierName(data.carrierCode) || data.carrierName || data.carrierCode || '',
    trackingNo: data.trackingNo || '',
    shippingStatus: data.shippingStatus ?? null,
    shipTime: formatTime(data.shipTime),
    receiveTime: formatTime(data.receiveTime),
    afterSaleStatus: data.afterSaleStatus || 0
  }
}

// 按商品类型获取商品详情，转换为订单商品项
const fetchCommodityItem = async (id, commodity) => {
  if (commodity === '0') {
    const { data } = await getFoodDetail(id)
    return data
      ? { id: data.id, name: data.name, price: Number(data.price) || 0, quantity: 1, image: data.coverImage }
      : null
  }
  if (commodity === '1') {
    const { data } = await getProductDetail(id)
    return data
      ? { id: data.id, name: data.name, price: Number(data.price) || 0, quantity: 1, image: data.coverImage }
      : null
  }
  return null
}

// 查询订单关联的聚合支付单（校验归属由后端完成）
const fetchPaymentInfo = async (payNo) => {
  paymentInfo.value = null
  if (!payNo) return
  try {
    const response = await queryPayment(payNo, { silent: true })
    if (response?.code === 200 && response.data) {
      paymentInfo.value = response.data
      // 以支付单的成功支付时间作为准确支付时间
      if (response.data.payTime) {
        order.value.payTime = formatTime(response.data.payTime)
      }
    }
  } catch (err) {
    console.warn('获取支付信息失败:', err)
  }
}

// 获取订单数据
const fetchProductData = async () => {
  const orderId = route.query.orderId
  const id = route.query.id
  const commodity = route.query.commodity

  // 初始设置为false
  hasOrderData.value = false
  paymentInfo.value = null

  if (orderId) {
    // 如果有订单编号，从后端获取订单数据
    try {
      const response = await getOrderDetail(orderId)
      const orderData = response.data

      if (orderData) {
        order.value = normalizeOrder(orderData)

        // 订单缺少商品信息时，按商品类型获取商品详情
        if (!orderData.itemName && id && commodity) {
          const item = await fetchCommodityItem(id, commodity)
          if (item) {
            order.value.items = [item]
            order.value.amount = item.price
          }
        }

        // 查询该订单的支付信息（支付单号由支付成功回调回写）
        await fetchPaymentInfo(order.value.payNo)

        // 设置为有订单数据
        hasOrderData.value = true
      }
    } catch (err) {
      console.error('获取订单数据失败:', err)
      // 如果获取订单失败，尝试用商品数据兜底
      await setDefaultOrderData()
    }
  } else {
    // 如果没有订单编号，使用商品数据
    await setDefaultOrderData()
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchProductData)

// 无订单编号时，根据商品详情构建订单数据
const setDefaultOrderData = async () => {
  const id = route.query.id
  const commodity = route.query.commodity
  if (!id || !commodity) return

  try {
    const item = await fetchCommodityItem(id, commodity)
    if (item) {
      order.value = {
        ...createDefaultOrder(),
        itemType: commodity === '0' ? 'food' : commodity === '1' ? 'product' : '',
        amount: item.price,
        quantity: item.quantity,
        items: [item]
      }
      hasOrderData.value = true
    }
  } catch (err) {
    console.error('获取商品数据失败:', err)
  }
}

const goToPay = () => {
  // 触发支付事件
  emit('pay')
}

const goToHome = () => {
  // 跳转到首页
  router.push('/')
}

// 查看物流
const viewLogistics = () => {
  const orderId = route.query.orderId
  if (orderId) {
    router.push({ path: '/logistics', query: { orderId } })
  }
}

const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm(t('payment.orderDetail.cancelConfirm'), t('payment.common.tip'), {
      confirmButtonText: t('payment.common.confirm'),
      cancelButtonText: t('payment.common.cancel'),
      type: 'warning'
    })
    
    const orderId = route.query.orderId
    if (orderId) {
      await cancelOrder(orderId)
      order.value.status = '4' // 4-已取消
      ElMessage.success(t('payment.orderDetail.cancelSuccess'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error(t('payment.orderDetail.cancelFailed'))
    }
  }
}

// 组件挂载时获取商品数据
onMounted(() => {
  fetchProductData()
})
</script>

<style scoped>
.order-detail {
  --od-gap: 18px;
  color: var(--theme-text-1);
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-variant-numeric: tabular-nums;
}

/* ==================== 订单概览 ==================== */
.od-hero {
  position: relative;
  overflow: hidden;
  margin-bottom: var(--od-gap);
  padding: 22px 24px 18px;
  border: 1px solid var(--theme-hairline);
  border-radius: 20px;
  background:
    radial-gradient(120% 160% at 0% 0%, color-mix(in srgb, var(--theme-primary) 14%, transparent), transparent 58%),
    radial-gradient(90% 120% at 100% 0%, color-mix(in srgb, var(--theme-accent-2) 10%, transparent), transparent 55%),
    var(--theme-bg-surface-2);
  animation: od-rise 0.5s var(--theme-ease) both;
}

.od-hero__glow {
  position: absolute;
  top: -140px;
  right: -60px;
  width: 320px;
  height: 320px;
  border-radius: 50%;
  background: radial-gradient(circle, color-mix(in srgb, var(--theme-primary) 32%, transparent), transparent 66%);
  filter: blur(12px);
  opacity: 0.55;
  pointer-events: none;
}

.od-hero__body {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
}

.od-hero__main {
  min-width: 0;
}

.od-hero__eyebrow {
  display: block;
  margin-bottom: 6px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--theme-text-3);
}

.od-hero__number {
  display: flex;
  align-items: center;
  gap: 10px;
}

.od-hero__no {
  font-family: ui-monospace, 'SFMono-Regular', 'JetBrains Mono', Consolas, monospace;
  font-size: 21px;
  font-weight: 700;
  letter-spacing: 0.02em;
  line-height: 1.2;
  color: var(--theme-text-1);
  word-break: break-all;
}

.od-copy {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  padding: 0;
  border: 1px solid var(--theme-border);
  border-radius: 9px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  cursor: pointer;
  transition:
    color var(--theme-duration-fast) var(--theme-ease),
    background var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    transform var(--theme-duration-fast) var(--theme-ease);
}

.od-copy svg {
  width: 14px;
  height: 14px;
}

.od-copy:hover {
  color: var(--theme-primary);
  border-color: color-mix(in srgb, var(--theme-primary) 40%, transparent);
  background: var(--theme-primary-soft);
  transform: translateY(-1px);
}

.od-copy--sm {
  width: 24px;
  height: 24px;
  border-radius: 8px;
}

.od-copy--sm svg {
  width: 12px;
  height: 12px;
}

.od-hero__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.od-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 11px;
  border-radius: 999px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 12px;
  font-weight: 600;
  line-height: 1.5;
}

.od-tag--ghost {
  background: color-mix(in srgb, var(--theme-bg-surface) 70%, transparent);
  border: 1px solid var(--theme-hairline);
  color: var(--theme-text-2);
  font-weight: 500;
}

.od-tag svg {
  width: 13px;
  height: 13px;
  opacity: 0.8;
}

.od-hero__aside {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  flex-shrink: 0;
  text-align: right;
}

.od-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 13px;
  border-radius: 999px;
  font-size: 12.5px;
  font-weight: 600;
  line-height: 1.4;
}

.od-status::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  box-shadow: 0 0 0 3px color-mix(in srgb, currentColor 18%, transparent);
}

.od-status.unpaid {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.od-status.paid {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.od-status.shipping {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.od-status.completed {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.od-status.cancelled {
  background: var(--theme-bg-muted);
  color: var(--theme-text-3);
}

.od-status.refunded {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
}

.od-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.od-amount__label {
  font-size: 12px;
  font-weight: 500;
  color: var(--theme-text-3);
}

.od-amount__value {
  font-size: 32px;
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.03em;
  color: var(--theme-text-1);
}

/* 订单进度 */
.od-steps {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 18px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px dashed color-mix(in srgb, var(--theme-text-3) 32%, transparent);
}

.od-steps__caption {
  flex-shrink: 0;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  color: var(--theme-text-3);
}

.od-steps__list {
  display: flex;
  flex: 1;
  align-items: center;
  min-width: 0;
  margin: 0;
  padding: 0;
  list-style: none;
}

.od-step {
  display: flex;
  flex: 1;
  align-items: center;
  gap: 8px;
  min-width: 0;
  font-size: 12.5px;
  font-weight: 500;
  color: var(--theme-text-3);
}

.od-step__dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 16px;
  height: 16px;
  border: 2px solid color-mix(in srgb, var(--theme-text-3) 42%, transparent);
  border-radius: 50%;
  background: var(--theme-bg-surface);
  color: var(--theme-primary-text);
  transition:
    background var(--theme-duration) var(--theme-ease),
    border-color var(--theme-duration) var(--theme-ease),
    box-shadow var(--theme-duration) var(--theme-ease);
}

.od-step__dot svg {
  width: 9px;
  height: 9px;
}

.od-step__label {
  white-space: nowrap;
}

.od-step:not(:last-child)::after {
  content: '';
  flex: 1;
  min-width: 10px;
  height: 2px;
  margin: 0 10px;
  border-radius: 2px;
  background: color-mix(in srgb, var(--theme-text-3) 26%, transparent);
  transition: background var(--theme-duration) var(--theme-ease);
}

.od-step.is-done {
  color: var(--theme-text-2);
}

.od-step.is-done .od-step__dot {
  border-color: var(--theme-primary);
  background: var(--theme-primary);
}

.od-step.is-done:not(:last-child)::after {
  background: color-mix(in srgb, var(--theme-primary) 55%, transparent);
}

.od-step.is-current .od-step__dot {
  box-shadow: var(--theme-ring);
}

.od-step.is-current .od-step__label {
  color: var(--theme-primary);
  font-weight: 700;
}

/* ==================== 布局 ==================== */
.od-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: var(--od-gap);
  align-items: start;
}

.od-main {
  display: flex;
  flex-direction: column;
  gap: var(--od-gap);
  min-width: 0;
}

.od-side {
  position: sticky;
  top: 24px;
  display: flex;
  flex-direction: column;
  gap: var(--od-gap);
}

/* ==================== 卡片 ==================== */
.od-card {
  padding: 18px 20px;
  border: 1px solid var(--theme-hairline);
  border-radius: 16px;
  background: var(--theme-bg-surface-2);
  animation: od-rise 0.5s var(--theme-ease) both;
  animation-delay: 0.06s;
}

.od-main .od-card:nth-child(2) { animation-delay: 0.1s; }
.od-main .od-card:nth-child(3) { animation-delay: 0.14s; }
.od-main .od-card:nth-child(4) { animation-delay: 0.18s; }
.od-side .od-card { animation-delay: 0.1s; }

.od-card__head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.od-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  border-radius: 9px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.od-card__icon svg {
  width: 15px;
  height: 15px;
}

.od-card__title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.od-card__count {
  margin-left: auto;
  padding: 3px 10px;
  border: 1px solid var(--theme-hairline);
  border-radius: 999px;
  background: var(--theme-bg-surface);
  font-size: 12px;
  font-weight: 500;
  color: var(--theme-text-2);
  white-space: nowrap;
}

/* ==================== 商品 ==================== */
.od-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.od-items__empty {
  margin: 0;
  padding: 18px 12px;
  border: 1px dashed var(--theme-hairline);
  border-radius: 12px;
  text-align: center;
  font-size: 14px;
  color: var(--theme-text-3);
}

.od-item {
  display: flex;
  gap: 14px;
  padding: 12px;
  border: 1px solid var(--theme-hairline);
  border-radius: 12px;
  background: var(--theme-bg-surface);
  transition:
    transform var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.od-item:hover {
  transform: translateY(-1px);
  border-color: color-mix(in srgb, var(--theme-primary) 30%, transparent);
  box-shadow: var(--theme-shadow-sm);
}

.od-item__thumb {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  overflow: hidden;
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-3);
}

.od-item__thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.od-item__thumb-empty svg {
  width: 22px;
  height: 22px;
}

.od-item__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;
  min-width: 0;
  padding: 2px 0;
}

.od-item__name {
  display: -webkit-box;
  overflow: hidden;
  margin: 0;
  font-size: 14.5px;
  font-weight: 600;
  line-height: 1.45;
  color: var(--theme-text-1);
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.od-item__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.od-item__price {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.od-item__qty {
  padding: 2px 9px;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  font-size: 12.5px;
  font-weight: 600;
  color: var(--theme-text-2);
}

.od-remark {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-top: 12px;
  padding: 10px 12px;
  border: 1px dashed color-mix(in srgb, var(--theme-warning) 40%, transparent);
  border-radius: 10px;
  background: var(--theme-bg-surface);
  font-size: 13px;
  line-height: 1.55;
  color: var(--theme-text-2);
}

.od-remark__tag {
  flex-shrink: 0;
  padding: 2px 8px;
  border-radius: 6px;
  background: var(--theme-warning-soft);
  font-size: 11px;
  font-weight: 600;
  color: var(--theme-warning);
}

.od-remark__text {
  word-break: break-word;
}

/* ==================== 信息栅格 ==================== */
.od-fields {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 14px 26px;
}

.od-field {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 0;
}

.od-field__label {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.01em;
  color: var(--theme-text-3);
}

.od-field__value {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
  color: var(--theme-text-1);
  word-break: break-all;
}

.od-field__value--mono {
  font-family: ui-monospace, 'SFMono-Regular', 'JetBrains Mono', Consolas, monospace;
  font-size: 13px;
  letter-spacing: 0.01em;
}

.od-field__value--accent {
  font-weight: 700;
  color: var(--theme-primary);
}

.od-field__value--deduct {
  color: var(--theme-success);
}

.od-pill {
  display: inline-flex;
  align-items: center;
  align-self: flex-start;
  padding: 2px 10px;
  border-radius: 999px;
  background: var(--theme-primary-soft);
  font-size: 12.5px;
  font-weight: 600;
  color: var(--theme-primary);
}

.od-pill--warn {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

/* ==================== 收货地址 ==================== */
.od-address {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.od-address__avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: var(--theme-gradient);
  box-shadow: var(--theme-shadow-sm);
  font-size: 17px;
  font-weight: 700;
  color: var(--theme-primary-text);
}

.od-address__body {
  min-width: 0;
}

.od-address__line {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.od-address__name {
  font-size: 15px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.od-address__phone {
  padding: 2px 9px;
  border: 1px solid var(--theme-hairline);
  border-radius: 999px;
  background: var(--theme-bg-surface);
  font-size: 12.5px;
  letter-spacing: 0.02em;
  color: var(--theme-text-2);
}

.od-address__text {
  margin: 6px 0 0;
  font-size: 13.5px;
  line-height: 1.6;
  color: var(--theme-text-2);
  word-break: break-word;
}

/* ==================== 结算 ==================== */
.od-summary {
  padding-bottom: 18px;
}

.od-summary__rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.od-summary__row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  font-size: 13.5px;
  color: var(--theme-text-2);
}

.od-summary__row > span:last-child {
  font-weight: 500;
  color: var(--theme-text-1);
}

.od-summary__total {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px dashed color-mix(in srgb, var(--theme-text-3) 32%, transparent);
}

.od-summary__total-label {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--theme-text-2);
}

.od-summary__total-value {
  font-size: 25px;
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.03em;
  color: var(--theme-text-1);
}

/* ==================== 按钮 ==================== */
.od-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.od-btn {
  display: inline-flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 11px 18px;
  border: 1px solid transparent;
  border-radius: 12px;
  font: inherit;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.4;
  cursor: pointer;
  transition:
    transform var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease),
    background var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    filter var(--theme-duration-fast) var(--theme-ease);
}

.od-btn:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

.od-btn--primary {
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  box-shadow: var(--theme-shadow-sm);
}

.od-btn--primary:hover {
  transform: translateY(-1px);
  box-shadow: var(--theme-shadow-md);
  filter: brightness(1.05);
}

.od-btn--ghost {
  border-color: var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
}

.od-btn--ghost:hover {
  background: var(--theme-bg-hover);
}

/* ==================== 空状态 ==================== */
.no-order {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 420px;
  padding: 32px 16px;
}

.no-order__card {
  width: 100%;
  max-width: 380px;
  padding: 36px 28px;
  border: 1px dashed var(--theme-border);
  border-radius: 20px;
  background: var(--theme-bg-surface-2);
  text-align: center;
  animation: od-rise 0.5s var(--theme-ease) both;
}

.no-order__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  border-radius: 22px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.no-order__icon svg {
  width: 34px;
  height: 34px;
}

.no-order__title {
  margin: 0 0 10px;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.no-order__desc {
  margin: 0 0 26px;
  font-size: 14px;
  line-height: 1.6;
  color: var(--theme-text-2);
}

.no-order__btn {
  flex: none;
  padding: 11px 28px;
}

/* ==================== 入场动效 ==================== */
@keyframes od-rise {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ==================== 响应式 ==================== */
@media (max-width: 1180px) {
  .od-layout {
    grid-template-columns: minmax(0, 1fr);
  }

  /* 单列时结算卡上移，保证金额与支付入口优先可见 */
  .od-side {
    position: static;
    order: -1;
  }
}

@media (max-width: 767px) {
  .od-hero {
    padding: 18px 18px 16px;
    border-radius: 16px;
  }

  .od-hero__body {
    flex-direction: column;
    gap: 16px;
  }

  .od-hero__aside {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    text-align: left;
  }

  .od-amount {
    align-items: flex-end;
  }

  .od-amount__value {
    font-size: 27px;
  }

  .od-steps {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .od-steps__list {
    width: 100%;
  }

  .od-step {
    font-size: 12px;
  }

  .od-step:not(:last-child)::after {
    margin: 0 8px;
  }

  .od-card {
    padding: 16px;
    border-radius: 14px;
  }

  .od-item {
    padding: 10px;
  }

  .od-item__thumb {
    width: 56px;
    height: 56px;
  }

  .od-fields {
    gap: 12px 20px;
  }

  .od-actions {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .order-detail {
    --od-gap: 14px;
  }

  .od-hero__no {
    font-size: 17px;
  }

  .od-hero__tags {
    gap: 6px;
  }

  .od-tag {
    padding: 3px 9px;
    font-size: 11.5px;
  }

  .od-step {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
    font-size: 11.5px;
  }

  .od-step:not(:last-child)::after {
    display: none;
  }

  .od-step__label {
    white-space: normal;
  }

  .od-summary__total-value {
    font-size: 22px;
  }

  .no-order__card {
    padding: 28px 20px;
  }

  .no-order__icon {
    width: 60px;
    height: 60px;
    border-radius: 18px;
  }

  .no-order__icon svg {
    width: 28px;
    height: 28px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .od-hero,
  .od-card,
  .no-order__card {
    animation: none;
  }

  .od-item,
  .od-btn,
  .od-copy {
    transition: none;
  }
}
</style>
