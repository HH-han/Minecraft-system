<template>
  <div class="payment-analytics">
    <header class="analytics-header">
      <div class="analytics-heading">
        <h2 class="analytics-title">{{ $t('payment.analytics.title') }}</h2>
        <p class="analytics-subtitle">{{ $t('payment.analytics.subtitle') }}</p>
      </div>
      <div class="analytics-actions">
        <div class="range-switch">
          <button
            v-for="item in ranges"
            :key="item.value"
            type="button"
            class="range-btn"
            :class="{ active: range === item.value }"
            @click="range = item.value"
          >
            {{ item.label }}
          </button>
        </div>
        <button
          type="button"
          class="refresh-btn"
          :disabled="loading"
          @click="fetchAnalytics"
        >
          <svg
            class="refresh-icon"
            :class="{ spinning: loading }"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.8"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M21 12a9 9 0 1 1-2.64-6.36" />
            <path d="M21 3v6h-6" />
          </svg>
          {{ $t('payment.analytics.refresh') }}
        </button>
      </div>
    </header>

    <!-- 加载中 -->
    <div v-if="loading && records.length === 0" class="state-box">
      <span class="state-spinner"></span>
      <p>{{ $t('payment.analytics.loading') }}</p>
    </div>

    <!-- 加载失败 -->
    <div v-else-if="error" class="state-box">
      <p class="state-error-text">{{ error }}</p>
      <button type="button" class="retry-btn" @click="fetchAnalytics">
        {{ $t('payment.analytics.retry') }}
      </button>
    </div>

    <!-- 无数据 -->
    <div v-else-if="filteredRecords.length === 0" class="state-box">
      <svg
        class="state-icon"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="1.4"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M4 19V9" />
        <path d="M10 19V5" />
        <path d="M16 19v-7" />
        <path d="M22 19H2" />
      </svg>
      <p>{{ records.length === 0 ? $t('payment.analytics.empty') : $t('payment.analytics.rangeEmpty') }}</p>
      <button
        v-if="records.length > 0 && range !== 'all'"
        type="button"
        class="retry-btn"
        @click="range = 'all'"
      >
        {{ $t('payment.analytics.range.all') }}
      </button>
    </div>

    <template v-else>
      <!-- 概览指标 -->
      <div class="kpi-grid">
        <div v-for="card in summaryCards" :key="card.key" class="kpi-card">
          <div class="kpi-icon" :class="`tone-${card.tone}`">
            <span v-html="card.icon"></span>
          </div>
          <div class="kpi-label">{{ card.label }}</div>
          <div class="kpi-value">{{ card.value }}</div>
          <div class="kpi-hint">{{ card.hint }}</div>
        </div>
      </div>

      <!-- 消费能力 + 支付类型 -->
      <div class="analytics-grid two-col">
        <section class="chart-card ability-card">
          <div class="card-head">
            <div>
              <h3 class="card-title">{{ $t('payment.analytics.ability.title') }}</h3>
              <p class="card-desc">{{ $t('payment.analytics.ability.desc') }}</p>
            </div>
            <span class="ability-level">{{ $t(`payment.analytics.tiers.${ability.levelKey}`) }}</span>
          </div>

          <div class="ability-main">
            <div
              class="ability-ring"
              :style="{ background: `conic-gradient(var(--theme-primary) ${ability.progress}%, var(--theme-bg-surface-2) ${ability.progress}% 100%)` }"
            >
              <div class="ability-ring-inner">
                <strong>{{ Math.round(ability.progress) }}%</strong>
                <span>{{ $t('payment.analytics.ability.level') }}</span>
              </div>
            </div>
            <div class="ability-summary">
              <div class="ability-spent">
                <span>{{ $t('payment.analytics.summary.totalSpent') }}</span>
                <strong>¥{{ formatMoney(allTotalSpent) }}</strong>
              </div>
              <p class="ability-progress-text">
                {{
                  ability.nextKey
                    ? $t('payment.analytics.ability.progress', {
                        level: $t(`payment.analytics.tiers.${ability.nextKey}`),
                        amount: formatMoney(ability.remaining)
                      })
                    : $t('payment.analytics.ability.maxLevel')
                }}
              </p>
            </div>
          </div>

          <div class="ability-stats">
            <div class="ability-stat">
              <span>{{ $t('payment.analytics.ability.avg') }}</span>
              <strong>¥{{ formatMoney(allAverageAmount) }}</strong>
            </div>
            <div class="ability-stat">
              <span>{{ $t('payment.analytics.ability.max') }}</span>
              <strong>¥{{ formatMoney(allMaxAmount) }}</strong>
            </div>
            <div class="ability-stat">
              <span>{{ $t('payment.analytics.ability.count') }}</span>
              <strong>{{ allSuccessRecords.length }}</strong>
            </div>
          </div>
        </section>

        <section class="chart-card">
          <div class="card-head">
            <div>
              <h3 class="card-title">{{ $t('payment.analytics.channels.title') }}</h3>
              <p class="card-desc">{{ $t('payment.analytics.channels.desc') }}</p>
            </div>
          </div>

          <div v-if="channelStats.length === 0" class="chart-empty">
            {{ $t('payment.analytics.channels.empty') }}
          </div>
          <div v-else class="donut-layout">
            <div class="donut-wrap">
              <svg class="donut" viewBox="0 0 120 120" role="img">
                <circle
                  cx="60"
                  cy="60"
                  r="46"
                  fill="none"
                  stroke="var(--theme-bg-surface-2)"
                  stroke-width="14"
                />
                <circle
                  v-for="segment in donutSegments"
                  :key="segment.key"
                  cx="60"
                  cy="60"
                  r="46"
                  fill="none"
                  :stroke="segment.color"
                  stroke-width="14"
                  :stroke-dasharray="`${segment.dash} ${segment.circumference - segment.dash}`"
                  :stroke-dashoffset="-segment.offset"
                  transform="rotate(-90 60 60)"
                />
              </svg>
              <div class="donut-center">
                <span>{{ $t('payment.analytics.channels.totalLabel') }}</span>
                <strong>{{ successRecords.length }}</strong>
              </div>
            </div>
            <ul class="legend-list">
              <li v-for="item in channelStats" :key="item.key" class="legend-item">
                <span class="legend-dot" :style="{ background: item.color }"></span>
                <span class="legend-name">{{ item.label }}</span>
                <span class="legend-percent">{{ formatPercent(item.percent) }}</span>
                <span class="legend-amount">¥{{ formatMoney(item.amount) }}</span>
              </li>
            </ul>
          </div>
        </section>
      </div>

      <!-- 支付频率 + 购买类型 -->
      <div class="analytics-grid two-col">
        <section class="chart-card">
          <div class="card-head">
            <div>
              <h3 class="card-title">{{ $t('payment.analytics.frequency.title') }}</h3>
              <p class="card-desc">{{ $t('payment.analytics.frequency.desc') }}</p>
            </div>
            <span class="card-badge">
              {{ $t('payment.analytics.frequency.perMonth', { count: averagePerMonth }) }}
            </span>
          </div>

          <div class="freq-chart">
            <div v-for="month in monthlyStats" :key="month.key" class="freq-item">
              <div class="freq-bar-wrap">
                <span class="freq-amount">¥{{ formatCompact(month.amount) }}</span>
                <div class="freq-bar" :style="{ height: `${month.barHeight}px` }"></div>
                <span class="freq-count">
                  {{ $t('payment.analytics.frequency.countUnit', { count: month.count }) }}
                </span>
              </div>
              <span class="freq-month">{{ month.label }}</span>
            </div>
          </div>
        </section>

        <section class="chart-card">
          <div class="card-head">
            <div>
              <h3 class="card-title">{{ $t('payment.analytics.purchase.title') }}</h3>
              <p class="card-desc">{{ $t('payment.analytics.purchase.desc') }}</p>
            </div>
          </div>

          <div v-if="typeStats.length === 0" class="chart-empty">
            {{ $t('payment.analytics.purchase.empty') }}
          </div>
          <ul v-else class="type-list">
            <li v-for="item in typeStats" :key="item.key" class="type-item">
              <div class="type-row">
                <span class="type-name">{{ item.label }}</span>
                <span class="type-meta">
                  {{ $t('payment.analytics.frequency.countUnit', { count: item.count }) }}
                  <em>¥{{ formatMoney(item.amount) }}</em>
                </span>
              </div>
              <div class="type-track">
                <div
                  class="type-bar"
                  :style="{ width: `${item.percent}%`, background: item.color }"
                ></div>
              </div>
            </li>
          </ul>
        </section>
      </div>

      <!-- 支付流水 -->
      <section class="chart-card flow-card">
        <div class="card-head">
          <div>
            <h3 class="card-title">{{ $t('payment.analytics.flows.title') }}</h3>
            <p class="card-desc">{{ $t('payment.analytics.flows.desc') }}</p>
          </div>
        </div>

        <div v-if="recentRecords.length === 0" class="chart-empty">
          {{ $t('payment.analytics.flows.empty') }}
        </div>
        <ul v-else class="flow-list">
          <li v-for="item in recentRecords" :key="item.key" class="flow-item">
            <span
              class="flow-avatar"
              :style="{
                background: `color-mix(in srgb, ${channelColor(item.channel)} 14%, transparent)`,
                color: channelColor(item.channel)
              }"
            >
              {{ getChannelLabel(item.channel).charAt(0) }}
            </span>
            <div class="flow-main">
              <div class="flow-title">{{ getTypeLabel(item.itemType) }}</div>
              <div class="flow-sub">
                {{ getChannelLabel(item.channel) }} · {{ formatDateTime(item.payTime) }}
              </div>
            </div>
            <span class="flow-status" :class="statusClass(item.status)">
              {{ getStatusLabel(item.status) }}
            </span>
            <span class="flow-amount" :class="{ muted: item.status !== 'SUCCESS' }">
              ¥{{ formatMoney(item.amount) }}
            </span>
          </li>
        </ul>
      </section>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getOrderList } from '@/api/order.js'
import { queryPayment } from '@/api/payment.js'
import { getMyMemberOrders } from '@/api/member.js'
import { getRailwayOrders } from '@/api/railway.js'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const { t, te, locale } = useI18n()

// 国际化键存在时取翻译，否则回退
const tOr = (key, fallback) => (te(key) ? t(key) : fallback)

/* ==================== 基础状态 ==================== */
const loading = ref(false)
const error = ref('')
// 统一后的支付记录
const records = ref([])
// 时间范围
const range = ref('all')

const RANGE_DAYS = { all: 0, d30: 30, d90: 90 }

const ranges = computed(() => [
  { value: 'all', label: t('payment.analytics.range.all') },
  { value: 'd30', label: t('payment.analytics.range.d30') },
  { value: 'd90', label: t('payment.analytics.range.d90') }
])

const activeRangeLabel = computed(
  () => ranges.value.find((item) => item.value === range.value)?.label || ''
)

/* ==================== 支付渠道 / 购买类型 ==================== */
const CHANNEL_COLORS = {
  WECHAT: 'var(--theme-success)',
  ALIPAY: 'var(--theme-info)',
  UNIONPAY: 'var(--theme-warning)',
  BALANCE: 'var(--theme-accent-2)',
  MOCK: 'var(--theme-text-3)',
  UNKNOWN: 'var(--theme-primary)'
}

const TYPE_PALETTE = [
  'var(--theme-info)',
  'var(--theme-success)',
  'var(--theme-warning)',
  'var(--theme-accent-2)',
  'var(--theme-danger)',
  'var(--theme-primary)'
]

const channelColor = (code) => CHANNEL_COLORS[code] || 'var(--theme-primary)'

const getChannelLabel = (code) =>
  code && code !== 'UNKNOWN'
    ? tOr(
        `payment.orderDetail.channels.${String(code).toLowerCase()}`,
        t('payment.analytics.channels.unknown')
      )
    : t('payment.analytics.channels.unknown')

const getTypeLabel = (type) =>
  tOr(`payment.analytics.types.${String(type || 'other').toLowerCase()}`, t('payment.analytics.types.other'))

const getStatusLabel = (status) =>
  tOr(`payment.orderDetail.statusLabels.${status}`, status || '-')

const statusClass = (status) => {
  if (status === 'SUCCESS') return 'success'
  if (['INIT', 'PAYING'].includes(status)) return 'pending'
  if (['REFUNDED', 'REFUNDING'].includes(status)) return 'refunded'
  return 'failed'
}

/* ==================== 时间与金额格式化 ==================== */
const parseTime = (value) => {
  if (!value) return 0
  const ts = new Date(String(value).replace(' ', 'T')).getTime()
  return Number.isNaN(ts) ? 0 : ts
}

const formatDateTime = (value) => {
  if (!value) return '-'
  const text = String(value).replace('T', ' ')
  return text.length >= 16 ? text.slice(0, 16) : text
}

const formatMoney = (value) => {
  const num = Number(value || 0)
  if (!Number.isFinite(num)) return '0'
  return num.toLocaleString(locale.value, {
    minimumFractionDigits: Number.isInteger(num) ? 0 : 2,
    maximumFractionDigits: 2
  })
}

const formatPercent = (value) => `${Number(value || 0).toFixed(1)}%`

// 图表轴上的紧凑金额（1.2万 / 12K）
const formatCompact = (value) => {
  const num = Number(value || 0)
  try {
    return new Intl.NumberFormat(locale.value, {
      notation: 'compact',
      maximumFractionDigits: 1
    }).format(num)
  } catch {
    return String(Math.round(num))
  }
}

const monthKey = (date) =>
  `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`

/* ==================== 数据筛选与聚合 ==================== */
const filteredRecords = computed(() => {
  const days = RANGE_DAYS[range.value] || 0
  if (!days) return records.value
  const start = Date.now() - days * 24 * 60 * 60 * 1000
  return records.value.filter((item) => parseTime(item.payTime) >= start)
})

const successRecords = computed(() =>
  filteredRecords.value.filter((item) => item.status === 'SUCCESS')
)

const allSuccessRecords = computed(() =>
  records.value.filter((item) => item.status === 'SUCCESS')
)

const totalSpent = computed(() =>
  successRecords.value.reduce((sum, item) => sum + item.amount, 0)
)

const allTotalSpent = computed(() =>
  allSuccessRecords.value.reduce((sum, item) => sum + item.amount, 0)
)

const allAverageAmount = computed(() =>
  allSuccessRecords.value.length > 0
    ? allTotalSpent.value / allSuccessRecords.value.length
    : 0
)

const allMaxAmount = computed(() =>
  allSuccessRecords.value.reduce((max, item) => Math.max(max, item.amount), 0)
)

/* ==================== 概览指标卡 ==================== */
const ICONS = {
  wallet:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="6" width="18" height="13" rx="2.5"/><path d="M3 10h18"/><circle cx="16.5" cy="14.5" r="1"/></svg>',
  receipt:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6 3h12v18l-3-2-3 2-3-2-3 2V3z"/><path d="M9 8h6"/><path d="M9 12h6"/></svg>',
  chart:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19V5"/><path d="M4 19h16"/><path d="M8 15l3-4 3 2 4-6"/></svg>',
  star:
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3l2.6 5.3 5.9.9-4.3 4.1 1 5.8L12 16.4 6.8 19.1l1-5.8L3.5 9.2l5.9-.9L12 3z"/></svg>'
}

const summaryCards = computed(() => {
  const list = successRecords.value
  const maxRecord = list.reduce(
    (max, item) => (!max || item.amount > max.amount ? item : max),
    null
  )
  return [
    {
      key: 'totalSpent',
      tone: 'primary',
      icon: ICONS.wallet,
      label: t('payment.analytics.summary.totalSpent'),
      value: `¥${formatMoney(totalSpent.value)}`,
      hint: activeRangeLabel.value
    },
    {
      key: 'payCount',
      tone: 'success',
      icon: ICONS.receipt,
      label: t('payment.analytics.summary.payCount'),
      value: String(list.length),
      hint: t('payment.analytics.summary.successHint')
    },
    {
      key: 'avgAmount',
      tone: 'info',
      icon: ICONS.chart,
      label: t('payment.analytics.summary.avgAmount'),
      value: `¥${formatMoney(list.length > 0 ? totalSpent.value / list.length : 0)}`,
      hint: t('payment.analytics.summary.avgHint')
    },
    {
      key: 'maxAmount',
      tone: 'warning',
      icon: ICONS.star,
      label: t('payment.analytics.summary.maxAmount'),
      value: `¥${formatMoney(maxRecord?.amount || 0)}`,
      hint: maxRecord ? formatDateTime(maxRecord.payTime) : '-'
    }
  ]
})

/* ==================== 支付类型（渠道分布） ==================== */
const channelStats = computed(() => {
  const map = new Map()
  successRecords.value.forEach((item) => {
    const key = item.channel || 'UNKNOWN'
    const entry = map.get(key) || { key, amount: 0, count: 0 }
    entry.amount += item.amount
    entry.count += 1
    map.set(key, entry)
  })
  const total = totalSpent.value || 1
  return [...map.values()]
    .sort((a, b) => b.amount - a.amount)
    .map((item) => ({
      ...item,
      label: getChannelLabel(item.key),
      color: channelColor(item.key),
      percent: (item.amount / total) * 100
    }))
})

const donutSegments = computed(() => {
  const circumference = 2 * Math.PI * 46
  let offset = 0
  return channelStats.value.map((item) => {
    const length = (item.percent / 100) * circumference
    const dash = Math.max(0, length - 2)
    const segment = { ...item, circumference, dash, offset }
    offset += length
    return segment
  })
})

/* ==================== 购买类型分布 ==================== */
const typeStats = computed(() => {
  const map = new Map()
  successRecords.value.forEach((item) => {
    const key = item.itemType || 'other'
    const entry = map.get(key) || { key, amount: 0, count: 0 }
    entry.amount += item.amount
    entry.count += 1
    map.set(key, entry)
  })
  const total = totalSpent.value || 1
  return [...map.values()]
    .sort((a, b) => b.amount - a.amount)
    .map((item, index) => ({
      ...item,
      label: getTypeLabel(item.key),
      color: TYPE_PALETTE[index % TYPE_PALETTE.length],
      percent: Math.max((item.amount / total) * 100, 2)
    }))
})

/* ==================== 支付频率（近 6 个月） ==================== */
const monthlyStats = computed(() => {
  const now = new Date()
  const months = []
  for (let i = 5; i >= 0; i -= 1) {
    const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
    months.push({
      key: monthKey(date),
      label: date.toLocaleDateString(locale.value, { month: 'short' }),
      count: 0,
      amount: 0
    })
  }
  const map = new Map(months.map((item) => [item.key, item]))
  successRecords.value.forEach((item) => {
    const ts = parseTime(item.payTime)
    if (!ts) return
    const entry = map.get(monthKey(new Date(ts)))
    if (!entry) return
    entry.count += 1
    entry.amount += item.amount
  })
  const maxAmount = Math.max(...months.map((item) => item.amount), 0)
  return months.map((item) => ({
    ...item,
    barHeight: maxAmount > 0 ? Math.max(4, Math.round((item.amount / maxAmount) * 108)) : 4
  }))
})

const averagePerMonth = computed(() => {
  const count = monthlyStats.value.reduce((sum, item) => sum + item.count, 0)
  return Math.round((count / 6) * 10) / 10
})

/* ==================== 消费能力 ==================== */
const ABILITY_TIERS = [
  { key: 'newcomer', threshold: 0 },
  { key: 'light', threshold: 500 },
  { key: 'active', threshold: 2000 },
  { key: 'senior', threshold: 5000 },
  { key: 'master', threshold: 10000 }
]

const ability = computed(() => {
  const spent = allTotalSpent.value
  let index = 0
  ABILITY_TIERS.forEach((tier, tierIndex) => {
    if (spent >= tier.threshold) index = tierIndex
  })
  const current = ABILITY_TIERS[index]
  const next = ABILITY_TIERS[index + 1] || null
  const progress = next
    ? Math.min(100, ((spent - current.threshold) / (next.threshold - current.threshold)) * 100)
    : 100
  return {
    levelKey: current.key,
    nextKey: next?.key || '',
    progress,
    remaining: next ? Math.max(0, next.threshold - spent) : 0
  }
})

/* ==================== 最近支付流水 ==================== */
const recentRecords = computed(() =>
  [...filteredRecords.value]
    .sort((a, b) => parseTime(b.payTime) - parseTime(a.payTime))
    .slice(0, 10)
)

/* ==================== 数据拉取 ==================== */
// 分页拉取全部商城订单
const fetchAllMallOrders = async () => {
  const size = 100
  const list = []
  let pageNum = 1
  let total = 0
  do {
    const response = await getOrderList({ pageNum, pageSize: size })
    list.push(...(response?.data?.records || []))
    total = response?.data?.total || 0
    pageNum += 1
  } while (list.length < total && pageNum <= 20)
  return list
}

// 分页拉取全部火车票订单
const fetchAllRailwayOrders = async () => {
  const size = 50
  const list = []
  let pageNum = 1
  let total = 0
  do {
    const response = await getRailwayOrders({ pageNum, pageSize: size })
    list.push(...(response?.data?.records || []))
    total = response?.data?.total || 0
    pageNum += 1
  } while (list.length < total && pageNum <= 20)
  return list
}

// 批量查询商城订单对应的聚合支付单（用户归属由后端校验）
const loadMallPayments = async (mallList) => {
  const targets = mallList.filter((order) => order.payNo)
  if (targets.length === 0) return {}
  const results = await Promise.all(
    targets.map(async (order) => {
      try {
        const response = await queryPayment(order.payNo, { silent: true })
        return {
          id: order.id,
          payment: response?.code === 200 ? response.data : null
        }
      } catch {
        return { id: order.id, payment: null }
      }
    })
  )
  const map = {}
  results.forEach(({ id, payment }) => {
    if (payment) map[id] = payment
  })
  return map
}

const MALL_PAID_STATUS = ['1', '2', '3']
const MEMBER_STATUS_MAP = { 0: 'INIT', 1: 'SUCCESS', 2: 'CLOSED', 3: 'REFUNDED' }
const MEMBER_CHANNEL_MAP = { 1: 'ALIPAY', 2: 'WECHAT', 3: 'BALANCE' }
const RAILWAY_STATUS_MAP = {
  PENDING: 'INIT',
  PAID: 'SUCCESS',
  CANCELLED: 'CLOSED',
  REFUNDED: 'REFUNDED'
}

const resolveMallStatus = (order, payment) => {
  if (payment?.status) return String(payment.status)
  const status = String(order.status ?? '')
  if (MALL_PAID_STATUS.includes(status)) return 'SUCCESS'
  if (status === '4') return 'CLOSED'
  if (status === '5') return 'REFUNDED'
  return 'INIT'
}

const normalizeMallOrder = (order, payment) => ({
  key: `mall-${order.id}`,
  itemType: String(order.itemType || 'other').toLowerCase(),
  channel: String(payment?.channelCode || '').toUpperCase() || 'UNKNOWN',
  status: resolveMallStatus(order, payment),
  amount: Number(payment?.payAmount ?? order.amount ?? 0) || 0,
  payTime: payment?.payTime || order.updateTime || order.createTime || ''
})

const normalizeMemberOrder = (order) => ({
  key: `member-${order.id}`,
  itemType: 'member',
  channel: MEMBER_CHANNEL_MAP[order.payType] || 'UNKNOWN',
  status: MEMBER_STATUS_MAP[order.status] || 'INIT',
  amount: Number(order.amount || 0) || 0,
  payTime: order.payTime || order.createTime || ''
})

const normalizeRailwayOrder = (order) => ({
  key: `railway-${order.id}`,
  itemType: 'railway',
  channel: 'UNKNOWN',
  status: RAILWAY_STATUS_MAP[String(order.status || '').toUpperCase()] || 'INIT',
  // 车票订单金额单位为分
  amount: (Number(order.totalAmount || 0) || 0) / 100,
  payTime: order.payTime || order.createTime || ''
})

const fetchAnalytics = async () => {
  loading.value = true
  error.value = ''
  try {
    const [mallList, memberList, railwayList] = await Promise.all([
      fetchAllMallOrders().catch(() => []),
      getMyMemberOrders()
        .then((response) => (response?.code === 200 ? response.data || [] : []))
        .catch(() => []),
      fetchAllRailwayOrders().catch(() => [])
    ])

    const paymentMap = await loadMallPayments(mallList)

    records.value = [
      ...mallList.map((order) => normalizeMallOrder(order, paymentMap[order.id])),
      ...memberList.map(normalizeMemberOrder),
      ...railwayList.map(normalizeRailwayOrder)
    ]
  } catch (err) {
    console.error('获取支付分析数据失败:', err)
    error.value = t('payment.analytics.loadFailed')
  } finally {
    loading.value = false
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchAnalytics)

onMounted(fetchAnalytics)
</script>

<style scoped>
.payment-analytics {
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif;
  color: var(--theme-text-1);
}

/* ==================== 头部 ==================== */
.analytics-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--theme-hairline);
}

.analytics-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--theme-text-1);
  margin: 0 0 6px 0;
  letter-spacing: -0.02em;
}

.analytics-subtitle {
  font-size: 13px;
  color: var(--theme-text-2);
  margin: 0;
  line-height: 1.5;
}

.analytics-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.range-switch {
  display: inline-flex;
  gap: 2px;
  padding: 4px;
  border-radius: 12px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.range-btn {
  border: none;
  background: transparent;
  color: var(--theme-text-2);
  font-size: 13px;
  font-weight: 500;
  font-family: inherit;
  padding: 7px 14px;
  border-radius: 9px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
}

.range-btn:hover {
  color: var(--theme-text-1);
}

.range-btn.active {
  background: var(--theme-bg-surface);
  color: var(--theme-primary);
  font-weight: 600;
  box-shadow: var(--theme-shadow-sm);
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  border-radius: 12px;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-size: 13px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
}

.refresh-btn:hover:not(:disabled) {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
}

.refresh-btn:disabled {
  cursor: not-allowed;
  color: var(--theme-text-3);
}

.refresh-icon {
  width: 15px;
  height: 15px;
}

.refresh-icon.spinning {
  animation: spin 0.9s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ==================== 状态 ==================== */
.state-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 72px 20px;
  text-align: center;
  color: var(--theme-text-2);
  font-size: 14px;
}

.state-box p {
  margin: 0;
  line-height: 1.6;
}

.state-icon {
  width: 64px;
  height: 64px;
  color: var(--theme-text-3);
}

.state-error-text {
  color: var(--theme-danger);
}

.state-spinner {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 3px solid var(--theme-bg-surface-2);
  border-top-color: var(--theme-primary);
  animation: spin 0.8s linear infinite;
}

.retry-btn {
  padding: 8px 20px;
  border-radius: 10px;
  border: 1px solid var(--theme-border);
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s ease;
}

.retry-btn:hover {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  border-color: var(--theme-primary);
}

/* ==================== 指标卡 ==================== */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.kpi-card {
  position: relative;
  padding: 18px;
  border-radius: 16px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  box-shadow: var(--theme-shadow-sm);
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.kpi-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 100% 0%, color-mix(in srgb, var(--theme-primary) 8%, transparent), transparent 60%);
  pointer-events: none;
}

.kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--theme-shadow-md);
}

.kpi-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 10px;
  margin-bottom: 12px;
}

.kpi-icon :deep(svg) {
  width: 18px;
  height: 18px;
}

.kpi-icon.tone-primary {
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
}

.kpi-icon.tone-success {
  color: var(--theme-success);
  background: var(--theme-success-soft);
}

.kpi-icon.tone-info {
  color: var(--theme-info);
  background: var(--theme-info-soft);
}

.kpi-icon.tone-warning {
  color: var(--theme-warning);
  background: var(--theme-warning-soft);
}

.kpi-label {
  font-size: 12px;
  color: var(--theme-text-3);
  margin-bottom: 6px;
}

.kpi-value {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--theme-text-1);
  font-variant-numeric: tabular-nums;
  line-height: 1.2;
  word-break: break-all;
}

.kpi-hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--theme-text-2);
}

/* ==================== 图表卡片 ==================== */
.analytics-grid {
  display: grid;
  gap: 16px;
  margin-bottom: 18px;
}

.analytics-grid.two-col {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.chart-card {
  padding: 22px;
  border-radius: 18px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  box-shadow: var(--theme-shadow-sm);
}

.flow-card {
  margin-bottom: 24px;
}

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin: 0 0 4px 0;
  letter-spacing: -0.01em;
}

.card-desc {
  font-size: 12px;
  color: var(--theme-text-3);
  margin: 0;
  line-height: 1.5;
}

.card-badge {
  flex-shrink: 0;
  padding: 4px 12px;
  border-radius: 20px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 12px;
  font-weight: 600;
}

.chart-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 140px;
  color: var(--theme-text-3);
  font-size: 13px;
}

/* ==================== 消费能力 ==================== */
.ability-level {
  flex-shrink: 0;
  padding: 5px 14px;
  border-radius: 20px;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 12px;
  font-weight: 600;
  box-shadow: var(--theme-shadow-sm);
}

.ability-main {
  display: flex;
  align-items: center;
  gap: 22px;
  margin-bottom: 18px;
}

.ability-ring {
  position: relative;
  width: 108px;
  height: 108px;
  flex-shrink: 0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.5s ease;
}

.ability-ring::before {
  content: '';
  position: absolute;
  inset: 9px;
  border-radius: 50%;
  background: var(--theme-bg-glass-strong);
}

.ability-ring-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.ability-ring-inner strong {
  font-size: 22px;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.02em;
}

.ability-ring-inner span {
  font-size: 11px;
  color: var(--theme-text-3);
}

.ability-summary {
  flex: 1;
  min-width: 0;
}

.ability-spent {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 10px;
}

.ability-spent span {
  font-size: 12px;
  color: var(--theme-text-3);
}

.ability-spent strong {
  font-size: 28px;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.03em;
  font-variant-numeric: tabular-nums;
  line-height: 1.15;
  word-break: break-all;
}

.ability-progress-text {
  margin: 0;
  font-size: 12px;
  color: var(--theme-text-2);
  line-height: 1.6;
}

.ability-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  padding-top: 16px;
  border-top: 1px solid var(--theme-hairline);
}

.ability-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ability-stat span {
  font-size: 11px;
  color: var(--theme-text-3);
}

.ability-stat strong {
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
  font-variant-numeric: tabular-nums;
  word-break: break-all;
}

/* ==================== 支付类型环形图 ==================== */
.donut-layout {
  display: flex;
  align-items: center;
  gap: 22px;
}

.donut-wrap {
  position: relative;
  width: 164px;
  height: 164px;
  flex-shrink: 0;
}

.donut {
  width: 100%;
  height: 100%;
}

.donut-center {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  pointer-events: none;
}

.donut-center span {
  font-size: 11px;
  color: var(--theme-text-3);
}

.donut-center strong {
  font-size: 26px;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.02em;
}

.legend-list {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.legend-item {
  display: grid;
  grid-template-columns: 10px minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.legend-name {
  color: var(--theme-text-2);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.legend-percent {
  color: var(--theme-text-3);
  font-variant-numeric: tabular-nums;
}

.legend-amount {
  color: var(--theme-text-1);
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

/* ==================== 支付频率柱状图 ==================== */
.freq-chart {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  height: 208px;
  padding-top: 6px;
}

.freq-item {
  flex: 1;
  min-width: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.freq-bar-wrap {
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  gap: 5px;
  min-height: 0;
}

.freq-amount {
  font-size: 10px;
  color: var(--theme-text-3);
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.freq-bar {
  width: 100%;
  max-width: 38px;
  min-height: 4px;
  border-radius: 8px 8px 4px 4px;
  background: linear-gradient(180deg, var(--theme-primary), color-mix(in srgb, var(--theme-primary) 55%, var(--theme-accent-2)));
  transition: height 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.freq-count {
  font-size: 10px;
  color: var(--theme-text-2);
  white-space: nowrap;
}

.freq-month {
  font-size: 12px;
  color: var(--theme-text-3);
  font-weight: 500;
}

/* ==================== 购买类型 ==================== */
.type-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.type-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 7px;
}

.type-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--theme-text-1);
}

.type-meta {
  font-size: 11px;
  color: var(--theme-text-3);
  white-space: nowrap;
}

.type-meta em {
  font-style: normal;
  color: var(--theme-text-1);
  font-weight: 600;
  margin-left: 8px;
  font-variant-numeric: tabular-nums;
}

.type-track {
  height: 8px;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  overflow: hidden;
}

.type-bar {
  height: 100%;
  min-width: 4px;
  border-radius: 8px;
  transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ==================== 支付流水 ==================== */
.flow-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.flow-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 13px 4px;
  border-top: 1px solid var(--theme-hairline);
}

.flow-item:first-child {
  border-top: none;
  padding-top: 2px;
}

.flow-avatar {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
}

.flow-main {
  flex: 1;
  min-width: 0;
}

.flow-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--theme-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.flow-sub {
  font-size: 11px;
  color: var(--theme-text-3);
  margin-top: 3px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.flow-status {
  flex-shrink: 0;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
}

.flow-status.success {
  color: var(--theme-success);
  background: var(--theme-success-soft);
}

.flow-status.pending {
  color: var(--theme-warning);
  background: var(--theme-warning-soft);
}

.flow-status.refunded {
  color: var(--theme-accent-2);
  background: color-mix(in srgb, var(--theme-accent-2) 12%, transparent);
}

.flow-status.failed {
  color: var(--theme-danger);
  background: var(--theme-danger-soft);
}

.flow-amount {
  flex-shrink: 0;
  min-width: 86px;
  text-align: right;
  font-size: 15px;
  font-weight: 700;
  color: var(--theme-text-1);
  font-variant-numeric: tabular-nums;
}

.flow-amount.muted {
  color: var(--theme-text-3);
  font-weight: 500;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1023px) {
  .analytics-grid.two-col {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 767px) {
  .analytics-header {
    align-items: flex-start;
    gap: 14px;
  }
  .analytics-title {
    font-size: 22px;
  }
  .analytics-actions {
    width: 100%;
    justify-content: space-between;
  }
  .range-btn {
    padding: 7px 10px;
    font-size: 12px;
  }
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
  }
  .kpi-card {
    padding: 14px;
  }
  .kpi-value {
    font-size: 20px;
  }
  .chart-card {
    padding: 18px;
    border-radius: 16px;
  }
  .ability-main {
    gap: 16px;
  }
  .ability-ring {
    width: 92px;
    height: 92px;
  }
  .ability-ring-inner strong {
    font-size: 18px;
  }
  .ability-spent strong {
    font-size: 23px;
  }
  .donut-layout {
    flex-direction: column;
    align-items: stretch;
  }
  .donut-wrap {
    align-self: center;
  }
  .flow-item {
    gap: 10px;
  }
  .flow-status {
    display: none;
  }
  .flow-amount {
    min-width: 72px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .analytics-title {
    font-size: 20px;
  }
  .analytics-subtitle {
    font-size: 12px;
  }
  .refresh-btn {
    padding: 8px 12px;
  }
  .ability-stats {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
  .freq-chart {
    height: 180px;
    gap: 6px;
  }
  .freq-count {
    font-size: 9px;
  }
  .flow-avatar {
    width: 34px;
    height: 34px;
    border-radius: 10px;
    font-size: 13px;
  }
}
</style>
