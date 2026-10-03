<template>
  <div class="orders-container">
    <h2 class="orders-title">兑换订单</h2>
    
    <el-table v-loading="loading" :data="orders" empty-text="暂无兑换订单" style="width: 100%" class="orders-table">
      <el-table-column prop="id" label="订单ID" width="80">
      </el-table-column>
      <el-table-column prop="orderNo" label="订单编号" min-width="220" show-overflow-tooltip>
      </el-table-column>
      <el-table-column prop="productName" label="商品名称" min-width="120">
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="70">
        <template #default="scope">
          x{{ scope.row.quantity ?? 1 }}
        </template>
      </el-table-column>
      <el-table-column prop="pointsUsed" label="消耗积分" width="90">
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" effect="light" round>
            {{ getStatusName(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收货信息" min-width="180">
        <template #default="scope">
          <template v-if="scope.row.receiver || scope.row.phone || scope.row.address">
            <div>{{ scope.row.receiver }}<span v-if="scope.row.phone" class="receiver-phone">{{ scope.row.phone }}</span></div>
            <div v-if="scope.row.address" class="address-text">{{ scope.row.address }}</div>
          </template>
          <span v-else class="empty-text">—</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="兑换时间" width="170">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
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
import { ElMessage } from 'element-plus';
import { getExchangeOrders } from '@/api/points';

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

// 状态对应 el-tag 类型（兼容数字状态码与中文字符串）
const getStatusType = (status) => {
  if (status === 1 || status === '待处理') return 'warning';
  if (status === 2 || status === '已完成') return 'success';
  if (status === 3 || status === '兑换失败' || status === '已取消') return 'danger';
  return 'info';
};

// 状态名称
const getStatusName = (status) => {
  if (status === 1) return '待处理';
  if (status === 2) return '已完成';
  if (status === 3) return '兑换失败';
  // 后端直接返回中文状态字符串
  return status || '未知状态';
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
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.orders-table {
  margin-bottom: 20px;
}

.receiver-phone {
  margin-left: 8px;
  color: #606266;
}

.address-text {
  color: #909399;
  font-size: 12px;
}

.empty-text {
  color: #c0c4cc;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>