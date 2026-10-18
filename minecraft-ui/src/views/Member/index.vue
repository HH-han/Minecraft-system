<template>
  <div class="member-page">
    <div class="member-shell" v-loading="loading">
      <header class="member-topbar">
        <button class="back-btn" type="button" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          <span>{{ $t('member.back') }}</span>
        </button>
        <div class="topbar-title">
          <h1>{{ $t('member.title') }}</h1>
          <p>{{ $t('member.subtitle') }}</p>
        </div>
      </header>

      <!-- 会员卡 -->
      <section class="member-card" :class="`level-${info.level}`">
        <div class="card-top">
          <div class="card-identity">
            <div class="card-level">{{ levelName }}</div>
            <div class="card-user">{{ info.nickname || info.userId }}</div>
          </div>
          <span class="card-badge">
            {{ info.isPaid ? $t('member.paid') : $t('member.notPaid') }}
          </span>
        </div>

        <div class="card-growth">
          <span>
            {{ $t('member.growth') }}
            <strong>{{ info.growth }}</strong>
          </span>
          <span v-if="info.nextLevelGrowth > 0">
            {{ $t('member.nextLevel', { value: info.nextLevelGrowth }) }}
          </span>
          <span v-else>{{ $t('member.maxLevel') }}</span>
        </div>

        <el-progress
          :percentage="progress"
          :stroke-width="10"
          :show-text="false"
          color="#ffd66b"
        />

        <div v-if="info.isPaid && info.expireTime" class="card-expire">
          {{ $t('member.expireTime', { date: formatDate(info.expireTime) }) }}
        </div>
      </section>

      <!-- 权益数据 -->
      <section class="stat-grid">
        <div class="stat-card">
          <span class="stat-value">{{ discountText }}</span>
          <span class="stat-label">{{ $t('member.stats.discount') }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ pointsRateText }}</span>
          <span class="stat-label">{{ $t('member.stats.pointsRate') }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">
            {{ $t('member.signDaysValue', { value: info.signContinuous }) }}
          </span>
          <span class="stat-label">{{ $t('member.stats.signDays') }}</span>
        </div>
      </section>

      <!-- 签到与权益 -->
      <section class="panel sign-panel">
        <div class="sign-info">
          <h2>{{ $t('member.sign.title') }}</h2>
          <p>{{ $t('member.sign.desc') }}</p>
          <p v-if="info.signContinuous > 0" class="sign-continuous">
            {{ $t('member.sign.continuous', { days: info.signContinuous }) }}
          </p>
        </div>
        <el-button
          type="primary"
          round
          size="large"
          :loading="signing"
          :disabled="info.todaySigned === 1"
          @click="handleSign"
        >
          {{ info.todaySigned === 1 ? $t('member.sign.signed') : $t('member.sign.button') }}
        </el-button>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h2>{{ $t('member.benefits.title') }}</h2>
        </div>
        <div v-if="benefitList.length" class="benefit-list">
          <span v-for="code in benefitList" :key="code" class="benefit-tag">
            {{ benefitName(code) }}
          </span>
        </div>
        <p v-else class="empty-text">{{ $t('member.benefits.empty') }}</p>
      </section>

      <!-- 套餐 -->
      <section class="panel">
        <div class="panel-head">
          <div>
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
              <span v-if="pkg.givePoints > 0">
                {{ $t('member.packages.givePoints', { value: pkg.givePoints }) }}
              </span>
              <span v-if="pkg.giveGrowth > 0">
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
          <h2>{{ activeTab === 'records' ? $t('member.records.title') : $t('member.orders.title') }}</h2>
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

      <!-- 等级体系 -->
      <section class="panel">
        <div class="panel-head">
          <h2>{{ $t('member.levels.title') }}</h2>
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
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import {
  getMemberInfo,
  getMemberLevels,
  getMemberPackages,
  openMember,
  payMemberOrder,
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
    const payRes = await payMemberOrder(openRes.data.orderNo)
    if (payRes?.code === 200) {
      if (payRes.data) info.value = payRes.data
      ElMessage.success(t('member.packages.success'))
      await Promise.all([fetchOrders(), fetchRecords(1)])
    } else {
      ElMessage.error(payRes?.message || t('member.packages.failed'))
    }
  } catch {
    // 错误提示由请求拦截器统一处理
  }
}

async function handlePay(order) {
  try {
    const res = await payMemberOrder(order.orderNo)
    if (res?.code === 200) {
      if (res.data) info.value = res.data
      ElMessage.success(t('member.orders.paySuccess'))
      await Promise.all([fetchOrders(), fetchRecords(1)])
    } else if (res?.message) {
      ElMessage.warning(res.message)
    }
  } catch {
    // 错误提示由请求拦截器统一处理
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

onMounted(loadAll)
</script>

<style scoped>
.member-page {
  min-height: 100vh;
  padding: 28px 20px 56px;
  background-color: var(--theme-bg-surface-2, #f5f6f8);
  background-image:
    radial-gradient(900px 460px at 4% -6%, var(--theme-primary-soft, rgb(64 158 255 / 12%)), transparent 60%),
    radial-gradient(760px 420px at 100% 0%, color-mix(in srgb, var(--theme-warning, #e6a23c) 10%, transparent), transparent 58%);
}

.member-shell {
  width: min(1120px, 100%);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.member-topbar {
  display: flex;
  align-items: center;
  gap: 18px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid var(--theme-hairline, rgb(0 0 0 / 8%));
  border-radius: 999px;
  background: var(--theme-bg-surface, #fff);
  color: var(--theme-text-2, #4b5563);
  font-size: 14px;
  cursor: pointer;
  transition: 0.2s ease;
}

.back-btn:hover {
  color: var(--theme-primary, #409eff);
  border-color: var(--theme-primary, #409eff);
}

.topbar-title h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--theme-text-1, #111827);
}

.topbar-title p {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--theme-text-3, #6b7280);
}

.member-card {
  position: relative;
  overflow: hidden;
  padding: 26px 28px;
  border-radius: 20px;
  color: #fff;
  background: linear-gradient(135deg, #7c8798, #4c5666);
  box-shadow: 0 18px 40px -24px rgb(15 23 42 / 55%);
}

.member-card::after {
  content: '';
  position: absolute;
  top: -60%;
  right: -8%;
  width: 320px;
  height: 320px;
  border-radius: 50%;
  background: rgb(255 255 255 / 14%);
}

.member-card.level-1 {
  background: linear-gradient(135deg, #d9dee6, #97a2b0);
}

.member-card.level-2 {
  background: linear-gradient(135deg, #f6d06b, #d09a2a);
}

.member-card.level-3 {
  background: linear-gradient(135deg, #79d6ff, #2f7be4);
}

.member-card.level-4 {
  background: linear-gradient(135deg, #b07cff, #ff6b6b);
}

.card-top {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.card-level {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.card-user {
  margin-top: 6px;
  font-size: 13px;
  opacity: 0.85;
}

.card-badge {
  padding: 5px 14px;
  border: 1px solid rgb(255 255 255 / 55%);
  border-radius: 999px;
  background: rgb(255 255 255 / 18%);
  font-size: 12px;
  white-space: nowrap;
}

.card-growth {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin: 22px 0 10px;
  font-size: 13px;
  opacity: 0.95;
}

.card-growth strong {
  font-size: 20px;
}

.card-expire {
  position: relative;
  z-index: 1;
  margin-top: 12px;
  font-size: 12px;
  opacity: 0.88;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.stat-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 18px;
  border-radius: 16px;
  border: 1px solid var(--theme-hairline, rgb(0 0 0 / 6%));
  background: var(--theme-bg-surface, #fff);
  box-shadow: var(--theme-shadow-sm, 0 6px 18px -12px rgb(15 23 42 / 25%));
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--theme-primary, #409eff);
}

.stat-label {
  font-size: 12px;
  color: var(--theme-text-3, #6b7280);
}

.panel {
  padding: 22px;
  border-radius: 18px;
  border: 1px solid var(--theme-hairline, rgb(0 0 0 / 6%));
  background: var(--theme-bg-surface, #fff);
  box-shadow: var(--theme-shadow-sm, 0 6px 18px -12px rgb(15 23 42 / 25%));
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 18px;
}

.panel-head h2 {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--theme-text-1, #111827);
}

.panel-subtitle {
  margin: 6px 0 0;
  font-size: 12px;
  color: var(--theme-text-3, #6b7280);
}

.sign-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.sign-info h2 {
  margin: 0;
  font-size: 17px;
  color: var(--theme-text-1, #111827);
}

.sign-info p {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--theme-text-3, #6b7280);
}

.sign-continuous {
  color: var(--theme-warning, #e6a23c) !important;
}

.benefit-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.benefit-tag {
  padding: 8px 14px;
  border-radius: 999px;
  background: var(--theme-primary-soft, rgb(64 158 255 / 12%));
  color: var(--theme-primary, #409eff);
  font-size: 13px;
}

.empty-text {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-3, #9ca3af);
}

.package-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.package-card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
  border: 1px solid var(--theme-hairline, rgb(0 0 0 / 8%));
  border-radius: 16px;
  background: var(--theme-bg-surface-2, #fafafa);
  cursor: pointer;
  transition: 0.22s ease;
}

.package-card:hover {
  transform: translateY(-3px);
  border-color: var(--theme-primary, #409eff);
  box-shadow: 0 16px 30px -20px rgb(64 158 255 / 65%);
}

.package-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1, #111827);
}

.package-desc {
  min-height: 34px;
  margin: 0;
  font-size: 12px;
  color: var(--theme-text-3, #6b7280);
}

.package-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.package-price .price {
  font-size: 26px;
  font-weight: 700;
  color: var(--theme-danger, #f56c6c);
}

.package-price .original {
  font-size: 12px;
  color: var(--theme-text-3, #9ca3af);
  text-decoration: line-through;
}

.package-gifts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: var(--theme-warning, #b7791f);
}

.package-btn {
  margin-top: auto;
  width: 100%;
}

.tab-group {
  display: inline-flex;
  padding: 3px;
  border-radius: 999px;
  background: var(--theme-bg-surface-2, #f3f4f6);
}

.tab-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: var(--theme-text-3, #6b7280);
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s ease;
}

.tab-btn.active {
  background: var(--theme-bg-surface, #fff);
  color: var(--theme-primary, #409eff);
  box-shadow: 0 4px 10px -6px rgb(15 23 42 / 35%);
}

.member-table {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: var(--theme-bg-surface-2, #fafafa);
  background: transparent;
}

.value-plus {
  color: var(--theme-success, #67c23a);
  font-weight: 600;
}

.value-minus {
  color: var(--theme-danger, #f56c6c);
  font-weight: 600;
}

.record-remark {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.record-sub {
  font-size: 12px;
  color: var(--theme-text-3, #9ca3af);
}

.status-tag {
  display: inline-flex;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
}

.status-0 {
  background: rgb(230 162 60 / 15%);
  color: #b7791f;
}

.status-1 {
  background: rgb(103 194 58 / 15%);
  color: #4e9a2f;
}

.status-2 {
  background: rgb(148 163 184 / 18%);
  color: #64748b;
}

.status-3 {
  background: rgb(245 108 108 / 15%);
  color: #d94a4a;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.level-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(190px, 1fr));
  gap: 12px;
}

.level-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border: 1px solid var(--theme-hairline, rgb(0 0 0 / 8%));
  border-radius: 14px;
  background: var(--theme-bg-surface-2, #fafafa);
}

.level-item.current {
  border-color: var(--theme-primary, #409eff);
  background: var(--theme-primary-soft, rgb(64 158 255 / 10%));
}

.level-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #9aa5b1;
  flex: none;
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
  gap: 3px;
}

.level-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1, #111827);
}

.level-threshold {
  font-size: 12px;
  color: var(--theme-text-3, #6b7280);
}

.level-current {
  margin-left: auto;
  font-size: 11px;
  color: var(--theme-primary, #409eff);
}

@media (max-width: 900px) {
  .member-page {
    padding: 20px 12px 40px;
  }

  .stat-grid,
  .package-grid {
    grid-template-columns: 1fr;
  }

  .sign-panel {
    flex-direction: column;
    align-items: flex-start;
  }

  .panel-head {
    flex-direction: column;
  }
}
</style>
