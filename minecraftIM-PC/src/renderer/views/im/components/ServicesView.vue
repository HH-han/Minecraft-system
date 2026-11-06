<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  ArrowLeft,
  Clock,
  Coin,
  CreditCard,
  Film,
  Goods,
  Location,
  Money,
  Phone,
  Present,
  Promotion,
  Refresh,
  ShoppingBag,
  Ticket,
  TrendCharts,
  Van,
  Wallet
} from '@element-plus/icons-vue';
import { getPointsRecords, getUserPoints } from '@/api/user';
import { useAuthStore } from '@/stores/auth';
import { formatFullTime } from '@/utils/format';
import type { PointsRecord } from '@/types/im';

const emit = defineEmits<{
  back: [];
}>();

const { t, locale } = useI18n();
const authStore = useAuthStore();

const points = ref(0);
const records = ref<PointsRecord[]>([]);
const loading = ref(false);

type IconType = typeof Wallet;

interface ServiceItem {
  key: string;
  icon: IconType;
  tone: string;
  soon?: boolean;
}

interface ServiceGroup {
  key: string;
  items: ServiceItem[];
}

const groups = computed<ServiceGroup[]>(() => [
  {
    key: 'finance',
    items: [
      { key: 'recharge', icon: Coin, tone: '#3b82f6' },
      { key: 'records', icon: TrendCharts, tone: '#0ea5e9' },
      { key: 'exchange', icon: Present, tone: '#8b5cf6' },
      { key: 'coupon', icon: Ticket, tone: '#f59e0b' }
    ]
  },
  {
    key: 'life',
    items: [
      { key: 'mobile', icon: Phone, tone: '#22c55e' },
      { key: 'data', icon: Promotion, tone: '#06b6d4' },
      { key: 'utility', icon: Location, tone: '#f97316' },
      { key: 'movie', icon: Film, tone: '#ef4444' }
    ]
  },
  {
    key: 'travel',
    items: [
      { key: 'taxi', icon: Van, tone: '#6366f1' },
      { key: 'flight', icon: Location, tone: '#0ea5e9' },
      { key: 'train', icon: Ticket, tone: '#10b981' },
      { key: 'transit', icon: CreditCard, tone: '#f59e0b' }
    ]
  },
  {
    key: 'shopping',
    items: [
      { key: 'mall', icon: ShoppingBag, tone: '#ec4899' },
      { key: 'orders', icon: Goods, tone: '#8b5cf6' },
      { key: 'address', icon: Location, tone: '#22c55e' },
      { key: 'promotion', icon: Money, tone: '#f59e0b' }
    ]
  }
]);

const pointsLabel = computed(() => Number(points.value || authStore.user?.points || 0));

function recordAmount(record: PointsRecord): number {
  const after = Number(record.afterPoints);
  const before = Number(record.beforePoints);
  if (Number.isFinite(after) && Number.isFinite(before)) return after - before;
  const raw = record.points ?? record.amount ?? 0;
  const value = Number(raw);
  return Number.isFinite(value) ? value : 0;
}

function recordType(record: PointsRecord): 'income' | 'expense' {
  const type = String(record.type ?? '').toUpperCase();
  if (['EXCHANGE', 'EXPENSE', 'SPEND', 'CONSUME', 'OUT'].includes(type)) return 'expense';
  return recordAmount(record) < 0 ? 'expense' : 'income';
}

function recordTypeLabel(record: PointsRecord): string {
  return recordType(record) === 'expense' ? t('im.services.expense') : t('im.services.income');
}

async function loadPoints(): Promise<void> {
  loading.value = true;
  try {
    const [pointsRes, recordsRes] = await Promise.allSettled([
      getUserPoints(),
      getPointsRecords()
    ]);
    if (pointsRes.status === 'fulfilled' && pointsRes.value.data !== undefined) {
      points.value = Number(pointsRes.value.data ?? 0);
    }
    if (recordsRes.status === 'fulfilled') {
      records.value = recordsRes.value.data ?? [];
    }
  } finally {
    loading.value = false;
  }
}

function openService(groupKey: string, itemKey: string): void {
  if (groupKey === 'finance' && itemKey === 'records') {
    void loadPoints();
    return;
  }
  ElMessage.info(t('im.common.comingSoon'));
}

onMounted(() => {
  void loadPoints();
});
</script>

<template>
  <section class="services-view">
    <header class="services-head">
      <button class="im-icon-btn" :title="t('im.common.back')" @click="emit('back')">
        <el-icon :size="16"><ArrowLeft /></el-icon>
      </button>
      <h2 class="services-title">{{ t('im.services.title') }}</h2>
      <button class="im-icon-btn" :title="t('im.services.refresh')" @click="loadPoints">
        <el-icon :size="15" :class="{ spinning: loading }"><Refresh /></el-icon>
      </button>
    </header>

    <div class="im-scroll services-scroll">
      <div class="wallet-card">
        <div class="wallet-card__glow" aria-hidden="true" />
        <div class="wallet-card__top">
          <span class="wallet-card__label">
            <el-icon :size="15"><Wallet /></el-icon>
            {{ t('im.services.pointsBalance') }}
          </span>
          <span class="wallet-card__badge">{{ t('im.services.wallet') }}</span>
        </div>
        <p class="wallet-card__value">{{ pointsLabel }}</p>
        <div class="wallet-card__foot">
          <span>{{ t('im.services.pointsTip') }}</span>
          <button class="wallet-card__action" @click="openService('finance', 'recharge')">
            {{ t('im.services.recharge') }}
          </button>
        </div>
      </div>

      <div v-for="group in groups" :key="group.key" class="service-card">
        <h4 class="service-card__title">{{ t(`im.services.groups.${group.key}`) }}</h4>
        <div class="service-grid">
          <button
            v-for="item in group.items"
            :key="item.key"
            class="service-item"
            @click="openService(group.key, item.key)"
          >
            <span class="service-item__icon" :style="{ background: item.tone }">
              <el-icon :size="18"><component :is="item.icon" /></el-icon>
            </span>
            <span class="service-item__label">
              {{ t(`im.services.items.${item.key}`) }}
            </span>
          </button>
        </div>
      </div>

      <div class="service-card records-card">
        <div class="records-head">
          <h4 class="service-card__title">{{ t('im.services.records') }}</h4>
          <span class="records-count">
            {{ t('im.services.recordsCount', { count: records.length }) }}
          </span>
        </div>

        <div v-if="records.length" class="records-list">
          <div v-for="(record, index) in records" :key="record.id ?? index" class="record-row">
            <span class="record-icon" :class="recordType(record)">
              <el-icon :size="14"><Clock /></el-icon>
            </span>
            <div class="record-body">
              <p class="record-desc">
                {{ record.description || record.remark || recordTypeLabel(record) }}
              </p>
              <p class="record-time">{{ formatFullTime(record.createTime, locale) }}</p>
            </div>
            <span class="record-amount" :class="recordType(record)">
              {{ recordAmount(record) > 0 ? '+' : '' }}{{ recordAmount(record) }}
            </span>
          </div>
        </div>

        <div v-else class="im-empty records-empty">
          <div class="im-empty__icon">
            <el-icon :size="26"><TrendCharts /></el-icon>
          </div>
          <p class="im-empty__title">{{ t('im.services.noRecords') }}</p>
          <p class="im-empty__desc">{{ t('im.services.noRecordsDesc') }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.services-view {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: var(--theme-bg-app);
  overflow: hidden;
}

.services-head {
  height: 64px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 24px;
  border-bottom: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
}

.services-title {
  margin: 0;
  flex: 1;
  min-width: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.services-scroll {
  padding: 22px 26px 40px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.spinning {
  animation: spin 900ms linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ---- 积分钱包卡 ---- */
.wallet-card {
  position: relative;
  padding: 22px 24px;
  border-radius: 18px;
  overflow: hidden;
  color: #fff;
  background: var(--theme-wallet-gradient);
  box-shadow: 0 24px 48px -28px color-mix(in srgb, var(--theme-primary) 75%, transparent);
}

.wallet-card__glow {
  position: absolute;
  right: -80px;
  top: -120px;
  width: 260px;
  height: 260px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.16);
}

.wallet-card__glow::after {
  content: '';
  position: absolute;
  left: -180px;
  top: 200px;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.wallet-card__top {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  opacity: 0.92;
}

.wallet-card__label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.wallet-card__badge {
  padding: 3px 10px;
  border-radius: 99px;
  background: rgba(255, 255, 255, 0.22);
  font-size: 10.5px;
  letter-spacing: 0.6px;
}

.wallet-card__value {
  position: relative;
  margin: 10px 0 16px;
  font-size: 40px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.wallet-card__foot {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 11.5px;
  opacity: 0.88;
}

.wallet-card__action {
  border: none;
  padding: 7px 16px;
  border-radius: 99px;
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.wallet-card__action:hover {
  background: rgba(255, 255, 255, 0.34);
}

/* ---- 服务分组 ---- */
.service-card {
  padding: 18px 22px 22px;
  border-radius: 16px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-shadow-sm);
}

.service-card__title {
  margin: 0 0 14px;
  font-size: 13px;
  font-weight: 700;
  color: var(--theme-text-2);
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 9px;
  padding: 14px 8px;
  border-radius: 12px;
  border: 1px solid transparent;
  background: transparent;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    transform var(--theme-duration-fast) var(--theme-ease);
}

.service-item:hover {
  background: var(--theme-bg-hover);
  border-color: var(--theme-hairline);
  transform: translateY(-1px);
}

.service-item__icon {
  width: 42px;
  height: 42px;
  border-radius: 13px;
  display: grid;
  place-items: center;
  color: #fff;
  box-shadow: 0 10px 20px -14px rgba(15, 23, 42, 0.55);
}

.service-item__label {
  font-size: 12px;
  color: var(--theme-text-2);
  text-align: center;
}

/* ---- 积分明细 ---- */
.records-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.records-head .service-card__title {
  margin: 0;
}

.records-count {
  font-size: 11px;
  color: var(--theme-text-3);
}

.records-list {
  display: flex;
  flex-direction: column;
}

.record-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.record-row:last-child {
  border-bottom: none;
}

.record-icon {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  border-radius: 10px;
  display: grid;
  place-items: center;
}

.record-icon.income {
  color: var(--theme-success);
  background: var(--theme-success-soft);
}

.record-icon.expense {
  color: var(--theme-danger);
  background: var(--theme-danger-soft);
}

.record-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.record-desc {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.record-time {
  margin: 0;
  font-size: 11px;
  color: var(--theme-text-3);
}

.record-amount {
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.record-amount.income {
  color: var(--theme-success);
}

.record-amount.expense {
  color: var(--theme-danger);
}

.records-empty {
  padding: 26px 10px;
  height: auto;
}

@media (max-width: 900px) {
  .service-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
