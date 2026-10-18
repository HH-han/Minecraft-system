<template>
  <div class="member-page">
    <div class="member-shell">
      <header class="member-topbar">
        <div class="topbar-title">
          <h1>{{ $t('member.title') }}</h1>
          <p>{{ $t('member.subtitle') }}</p>
        </div>
        <span class="topbar-level">
          <el-icon><Trophy /></el-icon>
          {{ levelName }}
        </span>
      </header>

      <div class="member-layout" v-loading="loading">
        <div class="member-main">
          <!-- 会员卡 -->
          <section class="member-card" :class="`level-${info.level}`">
            <div class="card-top">
              <div class="card-identity">
                <span class="card-emblem">
                  <el-icon><Trophy /></el-icon>
                </span>
                <div class="card-identity-text">
                  <span class="card-level">{{ levelName }}</span>
                  <span class="card-user">{{ info.nickname || info.userId }}</span>
                </div>
              </div>
              <span class="card-badge">
                {{ info.isPaid ? $t('member.paid') : $t('member.notPaid') }}
              </span>
            </div>

            <div class="card-growth">
              <div class="growth-main">
                <span class="growth-label">{{ $t('member.growth') }}</span>
                <span class="growth-value">{{ info.growth }}</span>
              </div>
              <span class="growth-hint">
                <template v-if="info.nextLevelGrowth > 0">
                  {{ $t('member.nextLevel', { value: info.nextLevelGrowth }) }}
                </template>
                <template v-else>{{ $t('member.maxLevel') }}</template>
              </span>
            </div>

            <div
              class="growth-track"
              role="progressbar"
              :aria-label="$t('member.growth')"
              aria-valuemin="0"
              aria-valuemax="100"
              :aria-valuenow="progress"
            >
              <span class="growth-fill" :style="{ width: `${progress}%` }"></span>
            </div>

            <div v-if="info.isPaid && info.expireTime" class="card-expire">
              <el-icon><Clock /></el-icon>
              <span>{{ $t('member.expireTime', { date: formatDate(info.expireTime) }) }}</span>
            </div>
          </section>

          <!-- 权益数据 -->
          <section class="stat-grid">
            <div class="stat-card stat-card--primary">
              <span class="stat-icon"><el-icon><Discount /></el-icon></span>
              <div class="stat-body">
                <span class="stat-value">{{ discountText }}</span>
                <span class="stat-label">{{ $t('member.stats.discount') }}</span>
              </div>
            </div>
            <div class="stat-card stat-card--warning">
              <span class="stat-icon"><el-icon><StarFilled /></el-icon></span>
              <div class="stat-body">
                <span class="stat-value">{{ pointsRateText }}</span>
                <span class="stat-label">{{ $t('member.stats.pointsRate') }}</span>
              </div>
            </div>
            <div class="stat-card stat-card--success">
              <span class="stat-icon"><el-icon><Calendar /></el-icon></span>
              <div class="stat-body">
                <span class="stat-value">
                  {{ $t('member.signDaysValue', { value: info.signContinuous }) }}
                </span>
                <span class="stat-label">{{ $t('member.stats.signDays') }}</span>
              </div>
            </div>
          </section>

          <!-- 每日签到 -->
          <section class="panel sign-panel">
            <span class="sign-icon"><el-icon><Calendar /></el-icon></span>
            <div class="sign-info">
              <div class="sign-head">
                <h2>{{ $t('member.sign.title') }}</h2>
                <span v-if="info.signContinuous > 0" class="sign-streak">
                  {{ $t('member.sign.continuous', { days: info.signContinuous }) }}
                </span>
              </div>
              <p>{{ $t('member.sign.desc') }}</p>
            </div>
            <el-button
              type="primary"
              round
              size="large"
              class="sign-btn"
              :loading="signing"
              :disabled="info.todaySigned === 1"
              @click="handleSign"
            >
              {{ info.todaySigned === 1 ? $t('member.sign.signed') : $t('member.sign.button') }}
            </el-button>
          </section>

          <!-- 套餐 -->
          <section class="panel">
            <div class="panel-head">
              <div class="panel-heading">
                <h2>{{ $t('member.packages.title') }}</h2>
                <p class="panel-subtitle">{{ $t('member.packages.subtitle') }}</p>
              </div>
            </div>

            <div v-if="packages.length" class="package-grid">
              <div
                v-for="pkg in packages"
                :key="pkg.id"
                class="package-card"
                @click="handleOpen(pkg)"
              >
                <div class="package-name">{{ packageName(pkg) }}</div>
                <p class="package-desc">{{ pkg.description }}</p>
                <div class="package-price">
                  <span class="price">¥{{ pkg.price }}</span>
                  <span v-if="pkg.originalPrice" class="original">
                    {{ $t('member.packages.original') }} ¥{{ pkg.originalPrice }}
                  </span>
                </div>
                <div class="package-gifts">
                  <span v-if="pkg.givePoints > 0" class="package-gift">
                    {{ $t('member.packages.givePoints', { value: pkg.givePoints }) }}
                  </span>
                  <span v-if="pkg.giveGrowth > 0" class="package-gift">
                    {{ $t('member.packages.giveGrowth', { value: pkg.giveGrowth }) }}
                  </span>
                </div>
                <el-button type="primary" round class="package-btn">
                  {{ $t('member.packages.open') }}
                </el-button>
              </div>
            </div>
            <el-empty v-else :description="$t('member.packages.empty')" />
          </section>

          <!-- 明细 -->
          <section class="panel">
            <div class="panel-head">
              <div class="panel-heading">
                <h2>{{ activeTab === 'records' ? $t('member.records.title') : $t('member.orders.title') }}</h2>
                <p v-if="activeTab === 'records' && recordTotal" class="panel-subtitle">
                  {{ $t('member.records.total', { total: recordTotal }) }}
                </p>
              </div>
              <div class="tab-group">
                <button
                  type="button"
                  class="tab-btn"
                  :class="{ active: activeTab === 'records' }"
                  @click="activeTab = 'records'"
                >
                  {{ $t('member.records.title') }}
                </button>
                <button
                  type="button"
                  class="tab-btn"
                  :class="{ active: activeTab === 'orders' }"
                  @click="activeTab = 'orders'"
                >
                  {{ $t('member.orders.title') }}
                </button>
              </div>
            </div>

            <template v-if="activeTab === 'records'">
              <el-table :data="records" size="default" class="member-table">
                <el-table-column
                  prop="changeValue"
                  :label="$t('member.records.change')"
                  width="110"
                >
                  <template #default="{ row }">
                    <span :class="row.changeValue >= 0 ? 'value-plus' : 'value-minus'">
                      {{ row.changeValue > 0 ? '+' : '' }}{{ row.changeValue }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('member.records.remark')">
                  <template #default="{ row }">
                    <div class="record-remark">
                      <span>{{ sourceText(row) }}</span>
                      <span v-if="row.remark" class="record-sub">{{ row.remark }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="afterValue"
                  :label="$t('member.growth')"
                  width="110"
                />
                <el-table-column :label="$t('member.records.time')" width="190">
                  <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
                </el-table-column>
                <template #empty>
                  <el-empty :description="$t('member.records.empty')" />
                </template>
              </el-table>

              <div v-if="recordTotal > recordSize" class="pager">
                <el-pagination
                  layout="prev, pager, next"
                  :current-page="recordPage"
                  :page-size="recordSize"
                  :total="recordTotal"
                  @current-change="handleRecordPageChange"
                />
              </div>
            </template>

            <template v-else>
              <el-table :data="orders" size="default" class="member-table">
                <el-table-column prop="orderNo" :label="$t('member.orders.orderNo')" min-width="190" />
                <el-table-column prop="packageName" :label="$t('member.orders.package')" min-width="130" />
                <el-table-column :label="$t('member.orders.amount')" width="110">
                  <template #default="{ row }">¥{{ row.amount }}</template>
                </el-table-column>
                <el-table-column :label="$t('member.orders.status')" width="110">
                  <template #default="{ row }">
                    <span class="status-tag" :class="`status-${row.status}`">
                      {{ orderStatusText(row.status) }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('member.orders.createTime')" width="190">
                  <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
                </el-table-column>
                <el-table-column :label="$t('member.orders.action')" width="150">
                  <template #default="{ row }">
                    <template v-if="row.status === 0">
                      <el-button link type="primary" size="small" @click="handlePay(row)">
                        {{ $t('member.orders.pay') }}
                      </el-button>
                      <el-button link type="danger" size="small" @click="handleCancel(row)">
                        {{ $t('member.orders.cancel') }}
                      </el-button>
                    </template>
                    <span v-else class="empty-text">-</span>
                  </template>
                </el-table-column>
                <template #empty>
                  <el-empty :description="$t('member.orders.empty')" />
                </template>
              </el-table>
            </template>
          </section>
        </div>

        <aside class="member-side">
          <!-- 权益 -->
          <section class="panel">
            <div class="panel-head">
              <div class="panel-heading">
                <h2>{{ $t('member.benefits.title') }}</h2>
              </div>
            </div>
            <div v-if="benefitList.length" class="benefit-list">
              <span v-for="code in benefitList" :key="code" class="benefit-tag">
                <el-icon><Check /></el-icon>
                {{ benefitName(code) }}
              </span>
            </div>
            <p v-else class="empty-text">{{ $t('member.benefits.empty') }}</p>
          </section>

          <!-- 等级体系 -->
          <section class="panel">
            <div class="panel-head">
              <div class="panel-heading">
                <h2>{{ $t('member.levels.title') }}</h2>
              </div>
            </div>
            <div class="level-list">
              <div
                v-for="level in levels"
                :key="level.id"
                class="level-item"
                :class="{ current: level.level === info.level }"
              >
                <span class="level-dot" :class="`dot-${level.level}`"></span>
                <div class="level-text">
                  <span class="level-name">{{ levelNameOf(level.level, level.name) }}</span>
                  <span class="level-threshold">
                    {{ $t('member.levels.threshold', { value: level.growthMin }) }}
                  </span>
                </div>
                <span v-if="level.level === info.level" class="level-current">
                  {{ $t('member.levels.current') }}
                </span>
              </div>
            </div>
          </section>
        </aside>
      </div>
    </div>

    <!-- 支付面板 -->
    <div v-if="payVisible" class="member-pay-overlay" @click.self="closePay">
      <div class="member-pay-modal" @click.stop>
        <button type="button" class="member-pay-close" :aria-label="$t('payment.close')" @click="closePay">
          <el-icon><Close /></el-icon>
        </button>
        <PayPage order-type="member" :order-id="payOrderNo" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Calendar,
  Check,
  Clock,
  Close,
  Discount,
  StarFilled,
  Trophy,
} from '@element-plus/icons-vue'
import PayPage from '@/components/Payment/PayPage.vue'
import {
  getMemberInfo,
  getMemberLevels,
  getMemberPackages,
  openMember,
  cancelMemberOrder,
  signIn,
  getGrowthRecords,
  getMyMemberOrders,
} from '@/api/member'

const router = useRouter()
const { t, te, locale } = useI18n()

const loading = ref(false)
const signing = ref(false)
const activeTab = ref('records')
const payVisible = ref(false)
const payOrderNo = ref('')

const info = ref({
  userId: null,
  nickname: '',
  growth: 0,
  level: 0,
  levelName: '',
  levelMinGrowth: 0,
  nextLevelGrowth: 0,
  discount: 1,
  pointsRate: 1,
  isPaid: 0,
  expireTime: null,
  todaySigned: 0,
  signContinuous: 0,
  benefits: [],
})

const packages = ref([])
const levels = ref([])
const orders = ref([])
const records = ref([])
const recordPage = ref(1)
const recordSize = ref(10)
const recordTotal = ref(0)

const isChinese = computed(() => locale.value.startsWith('zh'))

const levelName = computed(() => levelNameOf(info.value.level, info.value.levelName))

const discountText = computed(() => {
  const discount = Number(info.value.discount ?? 1)
  if (isChinese.value) {
    const value = Math.round(discount * 100) / 10
    return t('member.discountRate', { value })
  }
  const value = Math.round((1 - discount) * 100)
  return t('member.discountOff', { value })
})

const pointsRateText = computed(() => t('member.pointsRate', {
  value: Number(info.value.pointsRate ?? 1),
}))

const progress = computed(() => {
  const growth = Number(info.value.growth) || 0
  const min = Number(info.value.levelMinGrowth) || 0
  const next = Number(info.value.nextLevelGrowth) || 0
  if (!next) return 100
  const span = growth - min + next
  if (span <= 0) return 0
  return Math.min(100, Math.max(0, Math.round(((growth - min) / span) * 100)))
})

const benefitList = computed(() => info.value.benefits || [])

function levelNameOf(level, fallback) {
  const key = `member.levelNames.${level}`
  return te(key) ? t(key) : fallback || ''
}

function benefitName(code) {
  const key = `member.benefits.${code}`
  return te(key) ? t(key) : code
}

function packageName(pkg) {
  const key = `member.packages.types.${pkg.type}`
  return te(key) ? t(key) : pkg.name
}

function orderStatusText(status) {
  const key = `member.orders.statusMap.${status}`
  return te(key) ? t(key) : String(status)
}

function sourceText(row) {
  const key = `member.sources.${row.source}`
  return te(key) ? t(key) : row.remark || '-'
}

function formatDate(value) {
  if (!value) return '-'
  return new Date(value).toLocaleDateString(locale.value)
}

function formatDateTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString(locale.value)
}

async function fetchRecords(page = recordPage.value) {
  const res = await getGrowthRecords({
    pageNum: page,
    pageSize: recordSize.value,
  })
  if (res?.code === 200 && res.data) {
    records.value = res.data.records || []
    recordTotal.value = Number(res.data.total || 0)
    recordPage.value = page
  }
}

async function fetchOrders() {
  const res = await getMyMemberOrders()
  if (res?.code === 200) {
    orders.value = res.data || []
  }
}

async function loadAll() {
  loading.value = true
  try {
    const [infoRes, packageRes, levelRes] = await Promise.all([
      getMemberInfo(),
      getMemberPackages(),
      getMemberLevels(),
    ])
    if (infoRes?.code === 200 && infoRes.data) {
      info.value = infoRes.data
    }
    if (packageRes?.code === 200) {
      packages.value = packageRes.data || []
    }
    if (levelRes?.code === 200) {
      levels.value = levelRes.data || []
    }
    await Promise.all([fetchOrders(), fetchRecords(1)])
  } catch {
    // 错误提示由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}

function handleRecordPageChange(page) {
  fetchRecords(page).catch(() => {})
}

async function handleSign() {
  if (info.value.todaySigned === 1) return
  signing.value = true
  try {
    const res = await signIn()
    if (res?.code === 200 && res.data) {
      info.value = res.data
      ElMessage.success(t('member.sign.success'))
      await fetchRecords(1)
    } else if (res?.message) {
      ElMessage.warning(res.message)
    }
  } catch {
    // 错误提示由请求拦截器统一处理
  } finally {
    signing.value = false
  }
}

async function handleOpen(pkg) {
  try {
    await ElMessageBox.confirm(
      t('member.packages.confirmContent', {
        name: packageName(pkg),
        price: pkg.price,
      }),
      t('member.packages.confirmTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning',
      },
    )
  } catch {
    return
  }

  try {
    const openRes = await openMember({ packageId: pkg.id, payType: 1 })
    if (openRes?.code !== 200 || !openRes.data) {
      ElMessage.error(openRes?.message || t('member.packages.failed'))
      return
    }
    openPay(openRes.data.orderNo)
  } catch {
    // 错误提示由请求拦截器统一处理
  }
}

async function handlePay(order) {
  openPay(order.orderNo)
}

// 打开系统统一支付面板
function openPay(orderNo) {
  if (!orderNo) return
  payOrderNo.value = String(orderNo)
  payVisible.value = true
}

function closePay() {
  payVisible.value = false
  payOrderNo.value = ''
  loadAll().catch(() => {})
}

// 支付成功后 PayPage 会发送 closeModal 消息；点击遮罩发送 closePay 消息
function onPayMessage(event) {
  if (!payVisible.value) return
  if (event.data?.action === 'closeModal' || event.data?.action === 'closePay') {
    closePay()
  }
}

async function handleCancel(order) {
  try {
    await ElMessageBox.confirm(
      t('member.orders.cancelConfirm'),
      t('member.orders.title'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning',
      },
    )
  } catch {
    return
  }

  try {
    const res = await cancelMemberOrder(order.orderNo)
    if (res?.code === 200) {
      ElMessage.success(t('member.orders.cancelSuccess'))
      await fetchOrders()
    } else if (res?.message) {
      ElMessage.warning(res.message)
    }
  } catch {
    // 错误提示由请求拦截器统一处理
  }
}

function goBack() {
  if (window.history.state?.back) {
    router.back()
  } else {
    router.push('/')
  }
}

onMounted(() => {
  loadAll()
  window.addEventListener('message', onPayMessage)
})

onBeforeUnmount(() => {
  window.removeEventListener('message', onPayMessage)
})
</script>

<style scoped>
/* ===== 页面骨架 ===== */
.member-page {
  position: relative;
  min-height: 100vh;
  padding: 26px 20px 72px;
  font-family: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  color: var(--theme-text-1);
  background:
    radial-gradient(900px 480px at 4% -8%, color-mix(in srgb, var(--theme-primary) 10%, transparent), transparent 62%),
    radial-gradient(760px 420px at 100% 0%, color-mix(in srgb, var(--theme-accent-2) 9%, transparent), transparent 58%),
    var(--theme-bg-app);
}

.member-shell {
  width: min(1180px, 100%);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ===== 顶部 ===== */
.member-topbar {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid var(--theme-hairline);
  border-radius: 999px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  font: inherit;
  font-size: 14px;
  cursor: pointer;
  transition:
    color var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    background-color var(--theme-duration-fast) var(--theme-ease);
}

.back-btn:hover {
  color: var(--theme-primary);
  border-color: color-mix(in srgb, var(--theme-primary) 55%, transparent);
}

.back-btn:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

.topbar-title {
  flex: 1;
  min-width: 200px;
}

.topbar-title h1 {
  margin: 0;
  font-size: clamp(22px, 2.6vw, 30px);
  font-weight: 800;
  letter-spacing: -0.02em;
}

.topbar-title p {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--theme-text-2);
}

.topbar-level {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  border: 1px solid color-mix(in srgb, var(--theme-primary) 26%, transparent);
  border-radius: 999px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

/* ===== 双栏布局 ===== */
.member-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 316px;
  gap: 20px;
  align-items: start;
}

.member-main,
.member-side {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

/* ===== 会员卡 ===== */
.member-card {
  --card-a: #6b7687;
  --card-b: #39414e;
  --card-ink: #ffffff;
  --card-muted: rgb(255 255 255 / 78%);
  --card-line: rgb(255 255 255 / 45%);
  --card-glass: rgb(255 255 255 / 16%);
  --card-track: rgb(255 255 255 / 24%);
  --card-fill: linear-gradient(90deg, #ffffff, #ffe8a8);
  --card-glow: rgb(255 255 255 / 18%);

  position: relative;
  isolation: isolate;
  overflow: hidden;
  padding: 28px 30px;
  border-radius: 24px;
  color: var(--card-ink);
  background:
    radial-gradient(120% 160% at 0% 0%, var(--card-glow), transparent 46%),
    radial-gradient(90% 120% at 100% 0%, var(--card-glow), transparent 58%),
    linear-gradient(135deg, var(--card-a), var(--card-b));
  box-shadow: 0 26px 50px -30px rgb(15 23 42 / 60%);
}

.member-card::after {
  content: '';
  position: absolute;
  top: -90px;
  right: -70px;
  z-index: -1;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  background: radial-gradient(circle, rgb(255 255 255 / 26%), transparent 68%);
  pointer-events: none;
}

.member-card.level-1 {
  --card-a: #edf0f5;
  --card-b: #b6c0cd;
  --card-ink: #2f3a48;
  --card-muted: rgb(47 58 72 / 72%);
  --card-line: rgb(47 58 72 / 22%);
  --card-glass: rgb(255 255 255 / 55%);
  --card-track: rgb(47 58 72 / 16%);
  --card-fill: linear-gradient(90deg, #5c6b7d, #33404f);
  --card-glow: rgb(255 255 255 / 30%);
}

.member-card.level-2 {
  --card-a: #f7d982;
  --card-b: #d09a2a;
  --card-ink: #42300a;
  --card-muted: rgb(66 48 10 / 72%);
  --card-line: rgb(66 48 10 / 22%);
  --card-glass: rgb(255 255 255 / 42%);
  --card-track: rgb(66 48 10 / 16%);
  --card-fill: linear-gradient(90deg, #6b4a0c, #3f2c05);
  --card-glow: rgb(255 255 255 / 35%);
}

.member-card.level-3 {
  --card-a: #4a95f0;
  --card-b: #1c4f9e;
  --card-fill: linear-gradient(90deg, #ffffff, #cfe6ff);
}

.member-card.level-4 {
  --card-a: #8b5cf6;
  --card-b: #d1335f;
  --card-fill: linear-gradient(90deg, #ffffff, #ffd9e6);
}

.card-top {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.card-identity {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.card-emblem {
  display: grid;
  place-items: center;
  width: 46px;
  height: 46px;
  border: 1px solid var(--card-line);
  border-radius: 14px;
  background: var(--card-glass);
  font-size: 22px;
  flex: none;
}

.card-identity-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.card-level {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0.01em;
}

.card-user {
  font-size: 13px;
  color: var(--card-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-badge {
  padding: 5px 14px;
  border: 1px solid var(--card-line);
  border-radius: 999px;
  background: var(--card-glass);
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.card-growth {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 14px;
  margin: 26px 0 10px;
}

.growth-main {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.growth-label {
  font-size: 12px;
  letter-spacing: 0.08em;
  color: var(--card-muted);
}

.growth-value {
  font-size: 32px;
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.02em;
  font-variant-numeric: tabular-nums;
}

.growth-hint {
  padding-bottom: 4px;
  font-size: 12px;
  color: var(--card-muted);
  text-align: right;
}

.growth-track {
  position: relative;
  z-index: 1;
  height: 8px;
  overflow: hidden;
  border-radius: 999px;
  background: var(--card-track);
}

.growth-fill {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: var(--card-fill);
  transition: width var(--theme-duration-slow) var(--theme-ease);
}

.card-expire {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 14px;
  font-size: 12px;
  color: var(--card-muted);
}

/* ===== 权益数据 ===== */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(170px, 1fr));
  gap: 14px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  border: 1px solid var(--theme-hairline);
  border-radius: 16px;
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-shadow-sm);
}

.stat-card--primary {
  --stat-accent: var(--theme-primary);
  --stat-soft: var(--theme-primary-soft);
}

.stat-card--warning {
  --stat-accent: var(--theme-warning);
  --stat-soft: var(--theme-warning-soft);
}

.stat-card--success {
  --stat-accent: var(--theme-success);
  --stat-soft: var(--theme-success-soft);
}

.stat-icon {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 13px;
  background: var(--stat-soft);
  color: var(--stat-accent);
  font-size: 20px;
  flex: none;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.stat-value {
  font-size: 19px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--stat-accent);
}

.stat-label {
  font-size: 12px;
  color: var(--theme-text-3);
}

/* ===== 通用面板 ===== */
.panel {
  padding: 20px;
  border: 1px solid var(--theme-hairline);
  border-radius: 20px;
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-shadow-sm);
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.panel-heading {
  min-width: 0;
}

.panel-head h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.panel-subtitle {
  margin: 5px 0 0;
  font-size: 12px;
  color: var(--theme-text-3);
}

/* ===== 签到 ===== */
.sign-panel {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sign-icon {
  display: grid;
  place-items: center;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
  font-size: 22px;
  flex: none;
}

.sign-info {
  flex: 1;
  min-width: 0;
}

.sign-head {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.sign-head h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 800;
}

.sign-streak {
  padding: 3px 10px;
  border-radius: 999px;
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
  font-size: 12px;
  font-weight: 600;
}

.sign-info p {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--theme-text-3);
}

.sign-btn {
  flex: none;
}

/* ===== 权益标签 ===== */
.benefit-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.benefit-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 12px;
  border-radius: 10px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 12.5px;
  font-weight: 600;
}

.empty-text {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-3);
}

/* ===== 套餐 ===== */
.package-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(196px, 1fr));
  gap: 14px;
}

.package-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 18px;
  border: 1px solid var(--theme-hairline);
  border-radius: 16px;
  background: var(--theme-bg-surface-2);
  cursor: pointer;
  transition:
    transform var(--theme-duration) var(--theme-ease),
    border-color var(--theme-duration) var(--theme-ease),
    box-shadow var(--theme-duration) var(--theme-ease);
}

.package-card:hover {
  transform: translateY(-4px);
  border-color: color-mix(in srgb, var(--theme-primary) 55%, transparent);
  box-shadow: 0 18px 34px -22px color-mix(in srgb, var(--theme-primary) 65%, transparent);
}

.package-card:focus-within {
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.package-name {
  font-size: 16px;
  font-weight: 800;
  color: var(--theme-text-1);
}

.package-desc {
  min-height: 34px;
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  color: var(--theme-text-3);
}

.package-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-wrap: wrap;
}

.package-price .price {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--theme-highlight);
}

.package-price .original {
  font-size: 12px;
  color: var(--theme-text-3);
  text-decoration: line-through;
}

.package-gifts {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-height: 22px;
}

.package-gift {
  padding: 3px 9px;
  border-radius: 999px;
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
  font-size: 11.5px;
  font-weight: 600;
}

.package-btn {
  margin-top: auto;
  width: 100%;
}

/* ===== 标签页 ===== */
.tab-group {
  display: inline-flex;
  padding: 3px;
  border: 1px solid var(--theme-hairline);
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
}

.tab-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: var(--theme-text-3);
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition:
    background-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.tab-btn:hover {
  color: var(--theme-text-1);
}

.tab-btn.active {
  background: var(--theme-bg-elevated);
  color: var(--theme-primary);
  box-shadow: var(--theme-shadow-sm);
}

.tab-btn:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

/* ===== 表格 ===== */
.member-table {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: var(--theme-bg-surface-2);
  --el-table-border-color: var(--theme-hairline);
  --el-table-row-hover-bg-color: var(--theme-bg-hover);
  background: transparent;
}

.member-table :deep(th.el-table__cell) {
  font-size: 12px;
  font-weight: 600;
  color: var(--theme-text-3);
}

.value-plus {
  color: var(--theme-success);
  font-weight: 700;
}

.value-minus {
  color: var(--theme-danger);
  font-weight: 700;
}

.record-remark {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.record-sub {
  font-size: 12px;
  color: var(--theme-text-3);
}

.status-tag {
  display: inline-flex;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.status-0 {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.status-1 {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.status-2 {
  background: var(--theme-bg-muted);
  color: var(--theme-text-3);
}

.status-3 {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* ===== 等级时间线 ===== */
.level-list {
  position: relative;
}

.level-list::before {
  content: '';
  position: absolute;
  top: 18px;
  bottom: 18px;
  left: 17px;
  width: 2px;
  border-radius: 2px;
  background: var(--theme-hairline);
}

.level-item {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 10px 12px;
  border: 1px solid transparent;
  border-radius: 12px;
}

.level-item.current {
  border-color: color-mix(in srgb, var(--theme-primary) 24%, transparent);
  background: var(--theme-primary-soft);
}

.level-dot {
  position: relative;
  z-index: 1;
  width: 10px;
  height: 10px;
  margin-top: 4px;
  border-radius: 50%;
  background: #9aa5b1;
  box-shadow: 0 0 0 4px var(--theme-bg-surface);
  flex: none;
}

.level-item.current .level-dot {
  box-shadow:
    0 0 0 4px var(--theme-bg-surface),
    0 0 0 6px color-mix(in srgb, var(--theme-primary) 24%, transparent);
}

.dot-1 {
  background: #b8c1cc;
}

.dot-2 {
  background: #e0b23c;
}

.dot-3 {
  background: #3b8ee0;
}

.dot-4 {
  background: #a259ff;
}

.level-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.level-name {
  font-size: 14px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.level-threshold {
  font-size: 12px;
  color: var(--theme-text-3);
}

.level-current {
  margin-left: auto;
  padding: 2px 8px;
  border-radius: 999px;
  background: var(--theme-bg-surface);
  color: var(--theme-primary);
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

/* ===== 支付面板 ===== */
.member-pay-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: var(--theme-overlay);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
}

.member-pay-modal {
  position: relative;
  width: min(500px, 94vw);
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 22px;
}

/* PayPage 默认以 fixed 居中，这里改为随弹层流式排布 */
.member-pay-modal :deep(.glass-pay-container) {
  position: static;
  transform: none;
  min-height: auto;
  z-index: auto;
}

.member-pay-close {
  position: absolute;
  top: 14px;
  right: 14px;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: var(--theme-bg-hover);
  color: var(--theme-text-2);
  cursor: pointer;
  transition:
    background-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.member-pay-close:hover {
  background: var(--theme-bg-active);
  color: var(--theme-text-1);
}

.member-pay-close:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

/* ===== 动效 ===== */
@keyframes member-rise {
  from {
    opacity: 0;
    transform: translateY(12px);
  }

  to {
    opacity: 1;
    transform: none;
  }
}

.member-main > *,
.member-side > * {
  animation: member-rise var(--theme-duration-slow) var(--theme-ease) both;
}

.member-main > :nth-child(2) {
  animation-delay: 0.04s;
}

.member-main > :nth-child(3) {
  animation-delay: 0.08s;
}

.member-main > :nth-child(4) {
  animation-delay: 0.12s;
}

.member-main > :nth-child(5) {
  animation-delay: 0.16s;
}

.member-side > :nth-child(2) {
  animation-delay: 0.06s;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .member-layout {
    grid-template-columns: minmax(0, 1fr);
  }

  .member-side {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
  }
}

@media (max-width: 720px) {
  .member-page {
    padding: 18px 12px 56px;
  }

  .member-topbar {
    gap: 12px;
  }

  .topbar-level {
    display: none;
  }

  .member-card {
    padding: 22px;
  }

  .card-top {
    flex-direction: column;
  }

  .card-growth {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .growth-hint {
    padding-bottom: 0;
    text-align: left;
  }

  .sign-panel {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }

  .sign-btn {
    width: 100%;
  }

  .member-side {
    grid-template-columns: minmax(0, 1fr);
  }

  .panel {
    padding: 16px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .member-main > *,
  .member-side > * {
    animation: none;
  }

  .package-card,
  .growth-fill {
    transition: none;
  }
}
</style>
