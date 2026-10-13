<template>
  <div class="lg-page">
    <header class="lg-hero">
      <div>
        <p class="lg-hero__eyebrow">LOGISTICS</p>
        <h1 class="lg-hero__title">物流详情</h1>
        <p class="lg-hero__sub">实时追踪包裹的每一次流转</p>
      </div>
      <el-button round @click="goBack">
        <el-icon class="lg-mr"><Back /></el-icon>
        返回
      </el-button>
    </header>

    <div v-loading="loading" class="lg-body">
      <div v-if="packages.length === 0 && !loading" class="lg-empty">
        <div class="lg-empty__icon">▣</div>
        <p class="lg-empty__title">暂无物流信息</p>
        <p class="lg-empty__desc">订单发货后可在此查看实时轨迹</p>
      </div>

      <div v-else class="lg-list">
        <article v-for="pkg in packages" :key="pkg.id" class="lg-card">
          <div class="lg-card__head">
            <div class="lg-card__item">
              <img v-if="pkg.itemImage" :src="pkg.itemImage" class="lg-card__thumb" alt="" />
              <div v-else class="lg-card__thumb lg-card__thumb--ph">
                <el-icon :size="22"><Box /></el-icon>
              </div>
              <div class="lg-card__item-info">
                <div class="lg-card__name">{{ pkg.itemName || '商品' }}</div>
                <div class="lg-card__qty">数量 ×{{ pkg.quantity || 1 }}</div>
              </div>
            </div>
            <span class="lg-status" :class="`lg-status--${statusKey(pkg.status)}`">
              {{ pkg.statusDesc || '待发货' }}
            </span>
          </div>

          <div class="lg-card__meta">
            <div class="lg-meta">
              <span class="lg-meta__label">承运商</span>
              <span class="lg-meta__value">{{ pkg.carrierName || pkg.carrierCode || '-' }}</span>
            </div>
            <div class="lg-meta">
              <span class="lg-meta__label">运单号</span>
              <span class="lg-meta__value lg-meta__value--mono">
                {{ pkg.trackingNo || '-' }}
                <button
                  v-if="pkg.trackingNo"
                  type="button"
                  class="lg-copy"
                  title="复制运单号"
                  @click="copyTracking(pkg.trackingNo)"
                >
                  复制
                </button>
              </span>
            </div>
          </div>

          <el-timeline v-if="(tracks[pkg.id] || []).length" class="lg-timeline">
            <el-timeline-item
              v-for="(t, idx) in tracks[pkg.id]"
              :key="idx"
              :timestamp="formatTime(t.trackTime)"
              :type="idx === 0 ? 'primary' : ''"
              :hollow="idx !== 0"
              :size="idx === 0 ? 'large' : 'normal'"
            >
              <div class="lg-track" :class="{ 'lg-track--latest': idx === 0 }">{{ t.context }}</div>
              <div v-if="t.location" class="lg-track__loc">
                <el-icon><Location /></el-icon>{{ t.location }}
              </div>
            </el-timeline-item>
          </el-timeline>
          <div v-else class="lg-timeline-empty">暂无轨迹信息</div>

          <div class="lg-card__actions">
            <el-button
              v-if="pkg.id && pkg.status !== 4"
              type="primary"
              round
              :loading="confirmingId === pkg.id"
              @click="handleConfirm(pkg)"
            >
              确认收货
            </el-button>
            <el-button v-if="pkg.id" round @click="handleSubscribe(pkg)">订阅物流推送</el-button>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Back, Box, Location } from '@element-plus/icons-vue'
import {
  confirmReceive,
  getMyShipping,
  getOrderShipping,
  getTrack,
  subscribeLogistics
} from '@/api/logistics.js'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const packages = ref([])
const tracks = reactive({})
const confirmingId = ref(null)

const goBack = () => router.back()

// 0-待发货 1-已发货 2-运输中 3-派送中 4-已签收 5-拒收 6-退回 7-异常
const statusKey = (status) => {
  if (status === 4) return 'done'
  if (status === 5 || status === 6 || status === 7) return 'danger'
  if (status === 0 || status === null || status === undefined) return 'idle'
  return 'active'
}

const formatTime = (v) => (v ? String(v).replace('T', ' ') : '')

async function copyTracking(no) {
  try {
    await navigator.clipboard.writeText(String(no))
    ElMessage.success('运单号已复制')
  } catch {
    ElMessage.warning('复制失败，请手动复制')
  }
}

const fetchPackages = async () => {
  const orderId = route.query.orderId
  const trackingNo = route.query.trackingNo
  loading.value = true
  try {
    if (orderId) {
      const res = await getOrderShipping(orderId)
      packages.value = res?.data ?? []
    } else if (trackingNo) {
      packages.value = [{ id: 0, trackingNo, status: null, statusDesc: '运输中' }]
    } else {
      // 未指定订单时展示当前用户全部包裹
      const res = await getMyShipping()
      packages.value = res?.data ?? []
    }

    await Promise.all(
      packages.value.map(async (pkg) => {
        if (!pkg.trackingNo) return
        try {
          const res = await getTrack(pkg.trackingNo)
          tracks[pkg.id] = res?.data ?? []
        } catch {
          tracks[pkg.id] = []
        }
      })
    )
  } catch {
    // 错误提示由 request 拦截器统一处理
  } finally {
    loading.value = false
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchPackages)

const handleConfirm = async (pkg) => {
  try {
    await ElMessageBox.confirm('确认已收到该包裹？', '确认收货', { type: 'warning' })
  } catch {
    return
  }
  confirmingId.value = pkg.id
  try {
    await confirmReceive(pkg.id)
    ElMessage.success('确认收货成功')
    await fetchPackages()
  } finally {
    confirmingId.value = null
  }
}

const handleSubscribe = async (pkg) => {
  await subscribeLogistics(pkg.id)
  ElMessage.success('订阅成功')
}

onMounted(fetchPackages)
</script>

<style scoped>
.lg-page {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-ink-3: var(--theme-text-3);
  --tn-accent: var(--theme-primary);
  --tn-accent-2: var(--theme-accent-2);
  --tn-line: var(--theme-hairline);
  --tn-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  max-width: 900px;
  margin: 0 auto;
  margin-top: 100px;
  padding: 40px 20px 80px;
  font-family: var(--tn-font);
  color: var(--tn-ink);
}

.lg-page::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    radial-gradient(900px 460px at 8% -10%, color-mix(in srgb, var(--theme-primary) 10%, transparent), transparent 60%),
    radial-gradient(760px 420px at 100% 0%, color-mix(in srgb, var(--theme-accent-2) 9%, transparent), transparent 58%),
    var(--theme-bg-app);
}

/* Hero */
.lg-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 22px;
  animation: lg-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.lg-hero__eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: var(--tn-accent);
}

.lg-hero__eyebrow::before {
  content: '';
  width: 22px;
  height: 2px;
  border-radius: 2px;
  background: var(--theme-gradient);
}

.lg-hero__title {
  margin: 0;
  font-size: clamp(24px, 3.4vw, 34px);
  font-weight: 800;
  letter-spacing: -0.02em;
}

.lg-hero__sub {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--tn-ink-2);
}

.lg-mr {
  margin-right: 4px;
}

/* List & card */
.lg-body {
  min-height: 240px;
}

.lg-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.lg-card {
  padding: 22px 24px;
  border-radius: 22px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--tn-line);
  box-shadow: var(--theme-shadow-lg);
  animation: lg-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.lg-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.lg-card__item {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.lg-card__thumb {
  width: 54px;
  height: 54px;
  border-radius: 14px;
  object-fit: cover;
  border: 1px solid var(--tn-line);
  flex-shrink: 0;
}

.lg-card__thumb--ph {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-3);
}

.lg-card__name {
  font-size: 15px;
  font-weight: 700;
  color: var(--tn-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.lg-card__qty {
  margin-top: 3px;
  font-size: 12px;
  color: var(--tn-ink-3);
}

/* Status chip */
.lg-status {
  flex-shrink: 0;
  padding: 5px 14px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.lg-status--idle {
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
}
.lg-status--active {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}
.lg-status--done {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}
.lg-status--danger {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
}

/* Meta */
.lg-card__meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 14px 20px;
  margin-top: 16px;
  padding: 14px 18px;
  border-radius: 16px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.lg-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
  font-size: 13px;
}

.lg-meta__label {
  font-size: 12px;
  letter-spacing: 0.02em;
  color: var(--tn-ink-3);
}

.lg-meta__value {
  color: var(--tn-ink);
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.lg-meta__value--mono {
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.02em;
}

.lg-copy {
  padding: 2px 10px;
  border-radius: 999px;
  border: 1px solid color-mix(in srgb, var(--theme-primary) 35%, transparent);
  background: var(--theme-primary-soft);
  color: var(--tn-accent);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.lg-copy:hover {
  background: var(--tn-accent);
  color: var(--theme-primary-text);
}

/* Timeline */
.lg-timeline {
  margin-top: 22px;
  padding-left: 4px;
}

.lg-timeline :deep(.el-timeline-item__timestamp) {
  color: var(--tn-ink-3);
  font-weight: 600;
}

.lg-timeline :deep(.el-timeline-item__tail) {
  border-left-color: var(--theme-hairline);
}

.lg-timeline :deep(.el-timeline-item__node--large) {
  box-shadow: 0 0 0 4px var(--theme-primary-soft);
}

.lg-track {
  font-size: 13.5px;
  line-height: 1.6;
  color: var(--tn-ink-2);
}

.lg-track--latest {
  color: var(--tn-ink);
  font-weight: 700;
}

.lg-track__loc {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  margin-top: 3px;
  font-size: 12px;
  color: var(--tn-ink-3);
}

.lg-timeline-empty {
  margin-top: 18px;
  padding: 20px;
  text-align: center;
  font-size: 13px;
  color: var(--tn-ink-3);
  border-radius: 14px;
  background: var(--theme-bg-surface-2);
}

/* Actions */
.lg-card__actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid var(--tn-line);
}

/* Empty */
.lg-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 80px 20px;
  text-align: center;
}

.lg-empty__icon {
  font-size: 44px;
  color: var(--tn-accent);
  opacity: 0.5;
}

.lg-empty__title {
  margin: 6px 0 0;
  font-size: 18px;
  font-weight: 700;
}

.lg-empty__desc {
  margin: 0;
  font-size: 14px;
  color: var(--tn-ink-2);
}

@keyframes lg-rise {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 720px) {
  .lg-page {
    margin-top: 84px;
    padding: 20px 12px 64px;
  }
  .lg-card {
    padding: 18px 16px;
    border-radius: 20px;
  }
  .lg-card__meta {
    grid-template-columns: 1fr;
    gap: 10px;
  }
}
</style>
