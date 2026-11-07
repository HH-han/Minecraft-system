<template>
  <div class="rw-svc-page">
    <ServiceHero :eyebrow="$t('railway.memberPage.eyebrow')" :title="$t('railway.memberPage.title')"
      :desc="$t('railway.memberPage.desc')" :icon="Trophy" tone="violet">
      <div v-if="overview" class="rw-svc-hero-badge">
        <el-icon><Medal /></el-icon>
        <strong>{{ overview.member?.levelName }}</strong>
      </div>
    </ServiceHero>

    <div class="rw-svc-body">
      <!-- 未登录 -->
      <section v-if="!loggedIn" class="rw-svc-card">
        <div class="rw-svc-empty">
          <span class="rw-svc-empty-icon"><el-icon><Trophy /></el-icon></span>
          <h4>{{ $t('railway.memberPage.loginTitle') }}</h4>
          <p>{{ $t('railway.memberPage.loginDesc') }}</p>
          <div class="rw-svc-actions" style="justify-content: center">
            <button type="button" class="rw-svc-btn" @click="goLogin">
              {{ $t('railway.memberPage.login') }}
            </button>
          </div>
        </div>
      </section>

      <div v-else-if="loading" class="rw-svc-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        {{ $t('railway.common.loading') }}
      </div>

      <section v-else-if="error" class="rw-svc-card">
        <div class="rw-svc-empty">
          <span class="rw-svc-empty-icon"><el-icon><Warning /></el-icon></span>
          <h4>{{ $t('railway.memberPage.loadFailed') }}</h4>
          <p>{{ error }}</p>
          <div class="rw-svc-actions" style="justify-content: center">
            <button type="button" class="rw-svc-btn rw-svc-btn--ghost" @click="loadOverview">
              {{ $t('railway.list.retry') }}
            </button>
          </div>
        </div>
      </section>

      <template v-else-if="overview">
        <!-- 会员卡 -->
        <section class="rw-mp-card">
          <div class="rw-mp-head">
            <div>
              <span class="rw-mp-eyebrow">
                <el-icon><Medal /></el-icon>
                {{ $t('railway.memberPage.level') }}
              </span>
              <h2 class="rw-mp-level">{{ overview.member?.levelName }}</h2>
              <p class="rw-mp-growth">
                {{ $t('railway.memberPage.growth') }} {{ overview.member?.growth }}
                <span v-if="overview.member?.nextLevelGrowth > 0">
                  · {{ $t('railway.memberPage.nextLevel', { n: overview.member.nextLevelGrowth }) }}
                </span>
                <span v-else> · {{ $t('railway.memberPage.maxLevel') }}</span>
              </p>
              <div class="rw-svc-progress">
                <i :style="{ width: `${progressPercent}%` }"></i>
              </div>
            </div>
            <div class="rw-mp-discount">
              <small>{{ $t('railway.memberPage.discount') }}</small>
              <strong>{{ discountLabel }}</strong>
              <span>{{ $t('railway.memberPage.pointsRate') }} {{ pointsRateLabel }}</span>
            </div>
          </div>

          <div class="rw-mp-stats">
            <div class="rw-mp-stat">
              <small>{{ $t('railway.memberPage.points') }}</small>
              <strong>{{ overview.points }}</strong>
            </div>
            <div class="rw-mp-stat">
              <small>{{ $t('railway.memberPage.totalEarned') }}</small>
              <strong>{{ overview.totalEarned }}</strong>
            </div>
            <div class="rw-mp-stat">
              <small>{{ $t('railway.memberPage.paidOrders') }}</small>
              <strong>{{ overview.paidOrders }}</strong>
            </div>
            <div class="rw-mp-stat">
              <small>{{ $t('railway.memberPage.totalSpent') }}</small>
              <strong>¥{{ formatPrice(overview.totalSpentCents) }}</strong>
            </div>
          </div>
        </section>

        <div class="rw-svc-layout" style="margin-top: 20px">
          <div class="rw-svc-stack">
            <section class="rw-svc-card">
              <div class="rw-svc-card-head">
                <h2 class="rw-svc-card-title">{{ $t('railway.memberPage.points') }}</h2>
              </div>

              <div class="rw-svc-tabs">
                <button v-for="tab in tabs" :key="tab.value" type="button" class="rw-svc-tab"
                  :class="{ 'is-active': activeTab === tab.value }" @click="switchTab(tab.value)">
                  {{ $t(`railway.memberPage.${tab.label}`) }}
                </button>
              </div>

              <!-- 积分明细 -->
              <template v-if="activeTab === 'points'">
                <div v-if="logsLoading" class="rw-svc-loading">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  {{ $t('railway.common.loading') }}
                </div>
                <div v-else-if="logs.length === 0" class="rw-svc-empty">
                  <span class="rw-svc-empty-icon"><el-icon><Coin /></el-icon></span>
                  <h4>{{ $t('railway.memberPage.pointsEmpty') }}</h4>
                  <p>{{ $t('railway.memberPage.pointsEmptyDesc') }}</p>
                </div>
                <template v-else>
                  <div class="rw-svc-list">
                    <div v-for="log in logs" :key="log.id" class="rw-svc-item rw-mp-log">
                      <div class="rw-mp-log-main">
                        <strong>{{ log.remark }}</strong>
                        <small>{{ log.createTime }} · {{ log.bizNo }}</small>
                      </div>
                      <div class="rw-mp-log-value" :class="{ 'is-minus': log.changeValue < 0 }">
                        {{ log.changeValue > 0 ? '+' : '' }}{{ log.changeValue }}
                        <small>{{ log.afterValue }}</small>
                      </div>
                    </div>
                  </div>
                  <div v-if="logTotal > logPageSize" class="rw-svc-pagination">
                    <el-pagination background layout="prev, pager, next" :total="logTotal"
                      :page-size="logPageSize" :current-page="logPageNum" @current-change="handleLogPage" />
                  </div>
                </template>
              </template>

              <!-- 积分兑换 -->
              <template v-else-if="activeTab === 'gifts'">
                <div class="rw-svc-grid-cards">
                  <article v-for="gift in gifts" :key="gift.id" class="rw-mp-gift">
                    <span class="rw-mp-gift-icon">
                      <el-icon><component :is="giftIcon(gift.icon)" /></el-icon>
                    </span>
                    <h4>{{ gift.name }}</h4>
                    <p>{{ gift.description }}</p>
                    <div class="rw-mp-gift-meta">
                      <strong>{{ $t('railway.memberPage.redeemCost', { n: gift.pointsCost }) }}</strong>
                      <small>{{ $t('railway.memberPage.giftStock', { n: gift.stock }) }}</small>
                    </div>
                    <button type="button" class="rw-svc-btn rw-svc-btn--sm"
                      :disabled="gift.stock <= 0 || overview.points < gift.pointsCost || redeeming"
                      @click="redeem(gift)">
                      {{ gift.stock <= 0
                        ? $t('railway.memberPage.stockEmpty')
                        : $t('railway.memberPage.redeem') }}
                    </button>
                  </article>
                </div>
              </template>

              <!-- 会员权益 -->
              <template v-else>
                <h4 class="rw-mp-block-title">{{ $t('railway.memberPage.benefitTitle') }}</h4>
                <div v-if="benefits.length" class="rw-mp-benefits">
                  <span v-for="code in benefits" :key="code" class="rw-mp-benefit">
                    <el-icon><CircleCheckFilled /></el-icon>
                    {{ $t(`railway.memberPage.benefit.${code}`) }}
                  </span>
                </div>
                <p v-else class="rw-svc-note">{{ $t('railway.memberPage.noBenefit') }}</p>
              </template>
            </section>
          </div>

          <aside class="rw-svc-stack">
            <section class="rw-svc-card">
              <div class="rw-svc-card-head">
                <h2 class="rw-svc-card-title">{{ $t('railway.memberPage.recent') }}</h2>
              </div>
              <div v-if="overview.recentLogs?.length" class="rw-mp-recent">
                <div v-for="log in overview.recentLogs" :key="log.id" class="rw-mp-recent-item">
                  <span>{{ log.remark }}</span>
                  <em :class="{ 'is-minus': log.changeValue < 0 }">
                    {{ log.changeValue > 0 ? '+' : '' }}{{ log.changeValue }}
                  </em>
                </div>
              </div>
              <div v-else class="rw-svc-note">{{ $t('railway.memberPage.pointsEmptyDesc') }}</div>
            </section>

            <section class="rw-svc-card">
              <div class="rw-svc-card-head">
                <h2 class="rw-svc-card-title">{{ $t('railway.memberPage.pointsRule') }}</h2>
              </div>
              <ul class="rw-svc-rule-list">
                <li>{{ $t('railway.memberPage.rule1') }}</li>
                <li>{{ $t('railway.memberPage.rule2') }}</li>
                <li>{{ $t('railway.memberPage.rule3') }}</li>
              </ul>
            </section>
          </aside>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Box,
  CircleCheckFilled,
  Coffee,
  Coin,
  KnifeFork,
  Loading,
  Medal,
  Position,
  Present,
  Trophy,
  Warning,
} from '@element-plus/icons-vue'
import ServiceHero from './components/ServiceHero.vue'
import {
  getRailwayMemberGifts,
  getRailwayMemberOverview,
  getRailwayPointLogs,
  redeemRailwayGift,
} from '@/api/railway'
import { formatPrice, hasLogin } from './utils'

const router = useRouter()
const route = useRoute()
const { t, locale } = useI18n()

const tabs = [
  { value: 'points', label: 'tabPoints' },
  { value: 'gifts', label: 'tabGifts' },
  { value: 'benefits', label: 'tabBenefits' },
]

const giftIconMap = {
  Coffee,
  KnifeFork,
  Box,
  Position,
  Present,
}

const loggedIn = ref(hasLogin())
const loading = ref(false)
const error = ref('')
const overview = ref(null)
const activeTab = ref('points')

const gifts = ref([])
const redeeming = ref(false)

const logs = ref([])
const logsLoading = ref(false)
const logPageNum = ref(1)
const logPageSize = 8
const logTotal = ref(0)
const logsLoaded = ref(false)

const benefits = computed(() => overview.value?.member?.benefits || [])

const progressPercent = computed(() => {
  const member = overview.value?.member
  if (!member) return 0
  if (!member.nextLevelGrowth) return 100
  const base = member.levelMinGrowth || 0
  const span = member.growth - base + member.nextLevelGrowth
  if (span <= 0) return 0
  return Math.min(100, Math.max(0, Math.round(((member.growth - base) / span) * 100)))
})

const discountLabel = computed(() => {
  const discount = Number(overview.value?.member?.discount ?? 1)
  if (locale.value === 'en-US') {
    return t('railway.memberPage.discountValue', { n: Math.round((1 - discount) * 100) })
  }
  return t('railway.memberPage.discountValue', {
    n: (discount * 10).toFixed(1).replace(/\.0$/, ''),
  })
})

const pointsRateLabel = computed(() => {
  const rate = Number(overview.value?.member?.pointsRate ?? 1)
  return t('railway.memberPage.pointsRateValue', { n: rate.toFixed(1).replace(/\.0$/, '') })
})

const giftIcon = (icon) => giftIconMap[icon] || Present

const loadOverview = async () => {
  if (!hasLogin()) {
    loggedIn.value = false
    return
  }
  loggedIn.value = true
  loading.value = true
  error.value = ''
  try {
    const res = await getRailwayMemberOverview()
    overview.value = res?.data || null
    logs.value = overview.value?.recentLogs || []
    logsLoaded.value = false
    if (activeTab.value === 'points') {
      await loadLogs()
    }
  } catch (err) {
    error.value = err?.message || t('railway.memberPage.loadFailed')
  } finally {
    loading.value = false
  }
}

const loadGifts = async () => {
  try {
    const res = await getRailwayMemberGifts()
    gifts.value = res?.data || []
  } catch {
    gifts.value = []
  }
}

const loadLogs = async () => {
  logsLoading.value = true
  try {
    const res = await getRailwayPointLogs({ pageNum: logPageNum.value, pageSize: logPageSize })
    logs.value = res?.data?.records || []
    logTotal.value = Number(res?.data?.total || 0)
    logsLoaded.value = true
  } catch {
    logs.value = []
    logTotal.value = 0
  } finally {
    logsLoading.value = false
  }
}

const switchTab = (value) => {
  activeTab.value = value
  if (value === 'points' && !logsLoaded.value) {
    loadLogs()
  }
  if (value === 'gifts' && gifts.value.length === 0) {
    loadGifts()
  }
}

const handleLogPage = (value) => {
  logPageNum.value = value
  loadLogs()
}

const redeem = async (gift) => {
  try {
    await ElMessageBox.confirm(
      t('railway.memberPage.redeemConfirm', { n: gift.pointsCost, name: gift.name }),
      t('railway.common.confirm'),
      { type: 'warning' },
    )
    redeeming.value = true
    const res = await redeemRailwayGift({ giftId: gift.id })
    ElMessage.success(t('railway.memberPage.redeemSuccess', { code: res?.data?.redeemCode || '' }))
    await Promise.all([loadOverview(), loadGifts()])
  } catch (err) {
    if (err !== 'cancel' && err !== 'close') {
      ElMessage.error(err?.message || t('railway.memberPage.loadFailed'))
    }
  } finally {
    redeeming.value = false
  }
}

const goLogin = () => router.push({ path: '/login', query: { redirect: route.fullPath } })

onMounted(() => {
  loadGifts()
  loadOverview()
})
</script>

<style scoped>
.rw-mp-card {
  position: relative;
  overflow: hidden;
  border-radius: 22px;
  padding: 26px 28px;
  color: #ffffff;
  background:
    radial-gradient(600px 260px at 88% -30%, rgba(255, 255, 255, 0.28), transparent 65%),
    linear-gradient(128deg, #2a1f7a 0%, #5b4bd6 58%, #8b7cf6 100%);
  box-shadow: 0 24px 46px -34px rgba(63, 47, 168, 0.85);
}

.rw-mp-card::after {
  content: '';
  position: absolute;
  right: -40px;
  bottom: -90px;
  width: 220px;
  height: 220px;
  border-radius: 50%;
  border: 26px solid rgba(255, 255, 255, 0.1);
}

.rw-mp-head {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
}

.rw-mp-eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 11.5px;
  letter-spacing: 0.14em;
  color: rgba(255, 255, 255, 0.72);
}

.rw-mp-level {
  margin: 8px 0 0;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.rw-mp-growth {
  margin: 8px 0 12px;
  font-size: 12.5px;
  color: rgba(255, 255, 255, 0.78);
}

.rw-mp-head .rw-svc-progress {
  max-width: 320px;
}

.rw-mp-discount {
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex-shrink: 0;
}

.rw-mp-discount small {
  font-size: 11.5px;
  color: rgba(255, 255, 255, 0.72);
}

.rw-mp-discount strong {
  font-size: 28px;
  font-weight: 700;
  color: #ffd166;
  line-height: 1.1;
}

.rw-mp-discount span {
  font-size: 11.5px;
  color: rgba(255, 255, 255, 0.7);
}

.rw-mp-stats {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 24px;
}

.rw-mp-stat {
  border-radius: 13px;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.16);
}

.rw-mp-stat small {
  display: block;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.72);
}

.rw-mp-stat strong {
  display: block;
  margin-top: 5px;
  font-size: 20px;
  font-weight: 700;
}

.rw-mp-log {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.rw-mp-log-main {
  min-width: 0;
}

.rw-mp-log-main strong {
  display: block;
  font-size: 13.5px;
  color: var(--rw-text);
}

.rw-mp-log-main small {
  display: block;
  margin-top: 4px;
  font-size: 11px;
  color: var(--rw-text-3);
}

.rw-mp-log-value {
  font-size: 17px;
  font-weight: 700;
  color: var(--rw-success);
  text-align: right;
  flex-shrink: 0;
}

.rw-mp-log-value small {
  display: block;
  font-size: 10.5px;
  font-weight: 400;
  color: var(--rw-text-3);
}

.rw-mp-log-value.is-minus {
  color: var(--rw-orange);
}

.rw-mp-gift {
  display: flex;
  flex-direction: column;
  gap: 8px;
  border: 1px solid var(--rw-border);
  border-radius: 16px;
  padding: 16px;
  background: var(--rw-soft-2);
}

.rw-mp-gift-icon {
  width: 42px;
  height: 42px;
  border-radius: 13px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.rw-mp-gift h4 {
  margin: 2px 0 0;
  font-size: 14px;
  color: var(--rw-text);
}

.rw-mp-gift p {
  margin: 0;
  font-size: 11.5px;
  line-height: 1.6;
  color: var(--rw-text-3);
  min-height: 36px;
}

.rw-mp-gift-meta {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
}

.rw-mp-gift-meta strong {
  font-size: 13px;
  color: var(--rw-orange);
}

.rw-mp-gift-meta small {
  font-size: 11px;
  color: var(--rw-text-3);
}

.rw-mp-block-title {
  margin: 0 0 12px;
  font-size: 14px;
  color: var(--rw-text);
}

.rw-mp-benefits {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.rw-mp-benefit {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 14px;
  border-radius: 10px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  font-size: 12.5px;
  font-weight: 500;
}

.rw-mp-recent {
  display: flex;
  flex-direction: column;
  gap: 9px;
}

.rw-mp-recent-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding-bottom: 9px;
  border-bottom: 1px dashed var(--rw-border);
  font-size: 12.5px;
  color: var(--rw-text-2);
}

.rw-mp-recent-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.rw-mp-recent-item em {
  font-style: normal;
  font-weight: 700;
  color: var(--rw-success);
}

.rw-mp-recent-item em.is-minus {
  color: var(--rw-orange);
}

@media (max-width: 760px) {
  .rw-mp-card {
    padding: 20px;
  }

  .rw-mp-head {
    flex-direction: column;
  }

  .rw-mp-discount {
    text-align: left;
  }

  .rw-mp-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
