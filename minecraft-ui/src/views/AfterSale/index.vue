<template>
  <div class="as-page">
    <header class="as-hero">
      <div>
        <p class="as-hero__eyebrow">AFTER-SALE</p>
        <h1 class="as-hero__title">{{ $t('afterSale.title') }}</h1>
        <p class="as-hero__sub">{{ $t('afterSale.subtitle') }}</p>
      </div>
      <div class="as-hero__actions">
        <el-button round @click="fetchList">
          <el-icon class="as-mr"><Refresh /></el-icon>
          {{ $t('afterSale.refresh') }}
        </el-button>
      </div>
    </header>

    <ApplyPanel :order-id="route.query.orderId" @success="onApplySuccess" />

    <section class="as-card as-records">
      <div class="as-records__head">
        <h2 class="as-records__title">
          {{ $t('afterSale.recordsTitle') }}
          <span v-if="total" class="as-records__count">{{ total }}</span>
        </h2>
      </div>
      <el-table
        v-show="loading || list.length > 0"
        v-loading="loading"
        :data="list"
        style="width: 100%"
        :header-cell-style="headerStyle"
        :row-style="rowStyle"
        empty-text=" "
      >
        <el-table-column prop="afterSaleNo" :label="$t('afterSale.table.afterSaleNo')" min-width="180">
          <template #default="{ row }">
            <span class="as-mono">{{ row.afterSaleNo }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="itemName"
          :label="$t('afterSale.table.item')"
          min-width="150"
          show-overflow-tooltip
        />
        <el-table-column prop="quantity" :label="$t('afterSale.table.quantity')" width="72" />
        <el-table-column :label="$t('afterSale.table.type')" width="110">
          <template #default="{ row }">
            <span class="as-chip as-chip--type">{{ typeText(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.table.status')" width="120">
          <template #default="{ row }">
            <span class="as-chip" :class="`as-chip--${statusKey(row.status)}`">
              {{ statusText(row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.table.refundAmount')" width="120">
          <template #default="{ row }">
            <span class="as-amount">¥{{ row.actualAmount ?? row.refundAmount ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.table.createTime')" width="170">
          <template #default="{ row }">
            <span class="as-time">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('afterSale.table.actions')" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row.id)">
              {{ $t('afterSale.table.detail') }}
            </el-button>
            <el-button
              v-if="row.status === 0 || row.status === 1"
              link
              type="danger"
              @click="handleCancel(row)"
            >
              {{ $t('afterSale.table.cancel') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!loading && list.length === 0" class="as-empty">
        <span class="as-empty__icon">
          <el-icon :size="26"><Tickets /></el-icon>
        </span>
        <p class="as-empty__title">{{ $t('afterSale.emptyTitle') }}</p>
        <p class="as-empty__desc">{{ $t('afterSale.emptyDesc') }}</p>
      </div>

      <div v-if="total > 0" class="as-pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :current-page="pageNum"
          :page-size="pageSize"
          @current-change="handlePageChange"
        />
      </div>
    </section>

    <DetailDialog v-model="detailVisible" :id="detailId" @refresh="fetchList" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Tickets } from '@element-plus/icons-vue'
import { cancelAfterSale, getAfterSalePage } from '@/api/afterSale.js'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'
import ApplyPanel from './components/ApplyPanel.vue'
import DetailDialog from './components/DetailDialog.vue'

const route = useRoute()
const router = useRouter()
const { t, te } = useI18n()

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const detailVisible = ref(false)
const detailId = ref(null)

// 0-待审核 1-审核通过 2-已驳回 3-待买家退货 4-待商家收货 5-退款中 6-已完成 7-已关闭
const statusKey = (status) => {
  if (status === 6) return 'done'
  if (status === 2 || status === 7) return 'idle'
  if (status === 3 || status === 4 || status === 5) return 'warn'
  return 'active'
}

const statusText = (row) => {
  const key = `afterSale.status.${row.status}`
  return te(key) ? t(key) : row.statusDesc || '-'
}

const typeText = (row) => {
  const key = `afterSale.type.${row.type}`
  return te(key) ? t(key) : row.typeDesc || row.type
}

const formatTime = (v) => (v ? String(v).replace('T', ' ') : '-')

const headerStyle = () => ({
  background: 'var(--theme-bg-surface-2)',
  color: 'var(--theme-text-2)',
  fontWeight: '600',
  fontSize: '13px'
})

const rowStyle = () => ({ fontSize: '13.5px' })

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getAfterSalePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    const page = res?.data ?? {}
    list.value = page.records ?? []
    total.value = page.total ?? 0
  } catch {
    // 拦截器已提示
  } finally {
    loading.value = false
  }
}

const handlePageChange = (p) => {
  pageNum.value = p
  fetchList()
}

// 数据变更（本地操作 / 管理员后台改动 / 其他用户操作）后自动刷新列表
useAutoRefresh(fetchList)

const clearOrderQuery = () => {
  if (!route.query.orderId) return
  const rest = { ...route.query }
  delete rest.orderId
  router.replace({ query: rest })
}

const openDetail = (id) => {
  detailId.value = id
  detailVisible.value = true
}

const onApplySuccess = () => {
  clearOrderQuery()
  fetchList()
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(
      t('afterSale.cancelConfirmText'),
      t('afterSale.cancelConfirmTitle'),
      { type: 'warning' }
    )
  } catch {
    return
  }
  await cancelAfterSale(row.id)
  ElMessage.success(t('afterSale.cancelSuccess'))
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.as-page {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-ink-3: var(--theme-text-3);
  --tn-accent: var(--theme-primary);
  --tn-accent-2: var(--theme-accent-2);
  --tn-line: var(--theme-hairline);
  --tn-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  max-width: 1120px;
  margin: 0 auto;
  margin-top: 100px;
  padding: 40px 20px 80px;
  font-family: var(--tn-font);
  color: var(--tn-ink);
}

.as-page::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    radial-gradient(1000px 500px at 6% -10%, color-mix(in srgb, var(--theme-primary) 10%, transparent), transparent 60%),
    radial-gradient(800px 440px at 100% 0%, color-mix(in srgb, var(--theme-accent-2) 9%, transparent), transparent 58%),
    var(--theme-bg-app);
}

/* Hero */
.as-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 22px;
  animation: as-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.as-hero__eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: var(--tn-accent);
}

.as-hero__eyebrow::before {
  content: '';
  width: 22px;
  height: 2px;
  border-radius: 2px;
  background: var(--theme-gradient);
}

.as-hero__title {
  margin: 0;
  font-size: clamp(24px, 3.4vw, 34px);
  font-weight: 800;
  letter-spacing: -0.02em;
}

.as-hero__sub {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--tn-ink-2);
}

.as-hero__actions {
  display: flex;
  gap: 10px;
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

.as-records {
  margin-top: 20px;
}

.as-records__head {
  padding: 12px 12px 14px;
}

.as-records__title {
  display: inline-flex;
  align-items: center;
  gap: 9px;
  margin: 0;
  font-size: 17px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--tn-ink);
}

.as-records__count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 20px;
  padding: 0 8px;
  border-radius: 999px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 11.5px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  letter-spacing: 0;
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

.as-chip--type {
  background: color-mix(in srgb, var(--theme-accent-2) 10%, transparent);
  border-color: color-mix(in srgb, var(--theme-accent-2) 18%, transparent);
  color: var(--theme-accent-2);
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

/* Empty */
.as-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 64px 20px;
  text-align: center;
}

.as-empty__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: 6px;
  border-radius: 20px;
  background:
    linear-gradient(135deg, var(--theme-primary-soft), transparent 70%),
    var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
  color: var(--theme-primary);
}

.as-empty__title {
  margin: 6px 0 0;
  font-size: 18px;
  font-weight: 700;
}

.as-empty__desc {
  margin: 0;
  font-size: 14px;
  color: var(--tn-ink-2);
}

/* Pagination */
.as-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 16px 12px 4px;
}

.as-pagination :deep(.el-pagination.is-background .el-pager li),
.as-pagination :deep(.el-pagination.is-background .btn-prev),
.as-pagination :deep(.el-pagination.is-background .btn-next) {
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  color: var(--tn-ink-2);
  transition: background 0.18s ease, color 0.18s ease, box-shadow 0.18s ease;
}

.as-pagination :deep(.el-pagination.is-background .el-pager li:hover),
.as-pagination :deep(.el-pagination.is-background .btn-prev:hover),
.as-pagination :deep(.el-pagination.is-background .btn-next:hover) {
  color: var(--theme-primary);
}

.as-pagination :deep(.el-pagination.is-background .el-pager li.is-active) {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  box-shadow: 0 8px 18px -10px color-mix(in srgb, var(--theme-primary) 90%, transparent);
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

@media (max-width: 720px) {
  .as-page {
    margin-top: 84px;
    padding: 20px 12px 64px;
  }
  .as-hero {
    flex-direction: column;
  }
  .as-hero__actions {
    width: 100%;
  }
}
</style>
