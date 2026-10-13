<template>
  <div class="orders-container">
    <h2 class="orders-title">兑换订单</h2>

    <el-table v-loading="loading" :data="orders" style="width: 100%" class="orders-table">
      <el-table-column prop="id" label="订单ID" width="90">
        <template #default="scope">
          <span class="orders-id">#{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="订单编号" min-width="220" show-overflow-tooltip>
        <template #default="scope">
          <span class="orders-no">{{ scope.row.orderNo }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="productName" label="商品名称" min-width="130">
        <template #default="scope">
          <span class="orders-product">{{ scope.row.productName || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="80">
        <template #default="scope">
          <span class="orders-qty">×{{ scope.row.quantity ?? 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="pointsUsed" label="消耗积分" width="100">
        <template #default="scope">
          <span class="orders-points">{{ scope.row.pointsUsed }}</span>
        </template>
      </el-table-column>
      <el-table-column label="物流状态" width="110">
        <template #default="scope">
          <el-tag :type="getShippingType(scope.row)" effect="light" round>
            {{ scope.row.statusDesc || getStatusName(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收货信息" min-width="190">
        <template #default="scope">
          <template v-if="scope.row.receiver || scope.row.phone || scope.row.address">
            <div class="receiver-name">{{ scope.row.receiver }}<span v-if="scope.row.phone" class="receiver-phone">{{ scope.row.phone }}</span></div>
            <div v-if="scope.row.address" class="address-text">{{ scope.row.address }}</div>
          </template>
          <span v-else class="empty-text">—</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="兑换时间" width="175">
        <template #default="scope">
          <span class="orders-time">{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="scope">
          <el-button
            v-if="scope.row.orderId && scope.row.shippingStatus > 0"
            type="primary"
            link
            size="small"
            @click="viewLogistics(scope.row)"
          >
            查看物流
          </el-button>
          <span v-else class="empty-text">待发货</span>
        </template>
      </el-table-column>
      <template #empty>
        <div class="orders-empty">
          <div class="orders-empty__icon">◫</div>
          <p class="orders-empty__title">暂无兑换订单</p>
          <p class="orders-empty__desc">在积分商城兑换商品后，订单会显示在这里</p>
        </div>
      </template>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getExchangeOrders } from '@/api/points';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';

const router = useRouter();

const orders = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取兑换订单
const getOrders = async () => {
  loading.value = true;
  try {
    const response = await getExchangeOrders(currentPage.value, pageSize.value);
    // 后端返回 MyBatis-Plus 分页对象：{ records: [], total, current, size, ... }
    const pageData = response.data || {};
    orders.value = Array.isArray(pageData.records) ? pageData.records : [];
    total.value = pageData.total || 0;
  } catch (error) {
    console.error('获取兑换订单失败:', error);
    ElMessage.error('获取兑换订单失败');
    orders.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 数据变更后自动刷新
useAutoRefresh(getOrders);

// 物流状态标签类型（后端回填 statusDesc / shippingStatus）
const getShippingType = (row) => {
  const desc = row.statusDesc || '';
  if (desc === '已签收') return 'success';
  if (desc === '已发货' || desc === '部分发货') return 'primary';
  return 'warning';
};

// 状态名称
const getStatusName = (status) => {
  if (status === 1) return '待处理';
  if (status === 2) return '已完成';
  if (status === 3) return '兑换失败';
  // 后端直接返回中文状态字符串
  return status || '未知状态';
};

// 查看物流
const viewLogistics = (row) => {
  router.push({ path: '/logistics', query: { orderId: row.orderId } });
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString();
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  getOrders();
};

const handleCurrentChange = (current) => {
  currentPage.value = current;
  getOrders();
};

onMounted(() => {
  getOrders();
});
</script>

<style scoped>
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.orders-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 19px;
  font-weight: 800;
  letter-spacing: -0.01em;
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--theme-hairline);
  color: var(--theme-text-1);
}

.orders-table {
  margin-bottom: 20px;
}

.orders-table :deep(.el-table__header th.el-table__cell) {
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-weight: 600;
  font-size: 13px;
}

.orders-table :deep(.el-table__row) {
  font-size: 13.5px;
}

.orders-table :deep(td.el-table__cell),
.orders-table :deep(th.el-table__cell) {
  border-bottom-color: var(--theme-hairline);
}

.orders-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.orders-id {
  font-variant-numeric: tabular-nums;
  font-weight: 600;
  color: var(--theme-text-3);
}

.orders-no {
  font-variant-numeric: tabular-nums;
  font-weight: 600;
  letter-spacing: 0.01em;
  color: var(--theme-text-1);
}

.orders-product {
  font-weight: 600;
  color: var(--theme-text-1);
}

.orders-qty {
  font-variant-numeric: tabular-nums;
  color: var(--theme-text-2);
}

.orders-points {
  font-variant-numeric: tabular-nums;
  font-weight: 700;
  color: var(--theme-warning);
}

.orders-time {
  font-variant-numeric: tabular-nums;
  color: var(--theme-text-3);
}

.receiver-name {
  font-weight: 600;
  color: var(--theme-text-1);
}

.receiver-phone {
  margin-left: 8px;
  font-weight: 500;
  font-variant-numeric: tabular-nums;
  color: var(--theme-text-2);
}

.address-text {
  margin-top: 3px;
  color: var(--theme-text-3);
  font-size: 12px;
  line-height: 1.5;
}

.empty-text {
  color: var(--theme-text-3);
}

/* 空状态 */
.orders-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 48px 20px;
}

.orders-empty__icon {
  font-size: 32px;
  color: var(--theme-primary);
  opacity: 0.5;
}

.orders-empty__title {
  margin: 6px 0 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.orders-empty__desc {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-2);
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>