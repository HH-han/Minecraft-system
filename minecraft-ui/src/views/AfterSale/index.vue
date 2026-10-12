<template>
  <div class="as-page">
    <header class="as-hero">
      <div>
        <p class="as-hero__eyebrow">AFTER-SALE</p>
        <h1 class="as-hero__title">售后服务</h1>
        <p class="as-hero__sub">申请退款、退货与换货，跟踪处理进度</p>
      </div>
      <div class="as-hero__actions">
        <el-button round @click="fetchList">
          <el-icon class="as-mr"><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="primary" round @click="openApply">
          <el-icon class="as-mr"><Plus /></el-icon>
          申请售后
        </el-button>
      </div>
    </header>

    <section class="as-card">
      <el-table
        v-loading="loading"
        :data="list"
        style="width: 100%"
        :header-cell-style="headerStyle"
        :row-style="rowStyle"
        empty-text=" "
      >
        <el-table-column prop="afterSaleNo" label="售后单号" min-width="180">
          <template #default="{ row }">
            <span class="as-mono">{{ row.afterSaleNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="itemName" label="商品" min-width="150" show-overflow-tooltip />
        <el-table-column prop="quantity" label="数量" width="72" />
        <el-table-column label="类型" width="110">
          <template #default="{ row }">
            <span class="as-chip as-chip--type">{{ row.typeDesc || row.type }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <span class="as-chip" :class="`as-chip--${statusKey(row.status)}`">
              {{ row.statusDesc || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="退款金额" width="120">
          <template #default="{ row }">
            <span class="as-amount">¥{{ row.actualAmount ?? row.refundAmount ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="170">
          <template #default="{ row }">
            <span class="as-time">{{ formatTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
            <el-button
              v-if="row.status === 0 || row.status === 1"
              link
              type="danger"
              @click="handleCancel(row)"
            >
              撤销
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!loading && list.length === 0" class="as-empty">
        <div class="as-empty__icon">↺</div>
        <p class="as-empty__title">暂无售后记录</p>
        <p class="as-empty__desc">如对订单有疑问，可发起售后申请</p>
      </div>

      <div class="as-pagination">
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

    <ApplyDialog v-model="applyVisible" :order-id="applyOrderId" @success="onApplySuccess" />
    <DetailDialog v-model="detailVisible" :id="detailId" @refresh="fetchList" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { cancelAfterSale, getAfterSalePage } from '@/api/afterSale.js'
import ApplyDialog from './components/ApplyDialog.vue'
import DetailDialog from './components/DetailDialog.vue'

const route = useRoute()

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const applyVisible = ref(false)
const applyOrderId = ref(undefined)
const detailVisible = ref(false)
const detailId = ref(null)

// 0-待审核 1-审核通过 2-已驳回 3-待买家退货 4-待商家收货 5-退款中 6-已完成 7-已关闭
const statusKey = (status) => {
  if (status === 6) return 'done'
  if (status === 2 || status === 7) return 'idle'
  if (status === 3 || status === 4 || status === 5) return 'warn'
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

const openApply = () => {
  applyOrderId.value = route.query.orderId
  applyVisible.value = true
}

const openDetail = (id) => {
  detailId.value = id
  detailVisible.value = true
}

const onApplySuccess = () => {
  fetchList()
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定撤销该售后申请吗？', '撤销确认', { type: 'warning' })
  } catch {
    return
  }
  await cancelAfterSale(row.id)
  ElMessage.success('已撤销')
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
  gap: 16px;
  margin-bottom: 22px;
  animation: as-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.as-hero__eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: var(--tn-accent);
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
  padding: 8px 8px 16px;
  border-radius: 22px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--tn-line);
  box-shadow: var(--theme-shadow-lg);
  animation: as-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) 0.05s both;
}

.as-card :deep(.el-table) {
  border-radius: 16px;
  --el-table-border-color: var(--theme-hairline);
  --el-table-row-hover-bg-color: var(--theme-primary-soft);
}

.as-card :deep(.el-table th.el-table__cell) {
  border-bottom: 1px solid var(--theme-hairline);
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
}

.as-chip--type {
  background: color-mix(in srgb, var(--theme-accent-2) 10%, transparent);
  color: var(--theme-accent-2);
}
.as-chip--idle {
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
}
.as-chip--active {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}
.as-chip--warn {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}
.as-chip--done {
  background: var(--theme-success-soft);
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
  font-size: 42px;
  color: var(--tn-accent);
  opacity: 0.5;
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
