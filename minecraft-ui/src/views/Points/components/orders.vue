<template>
  <div class="orders-container">
    <h2 class="orders-title">兑换订单</h2>
    
    <el-table :data="orders" style="width: 100%" class="orders-table">
      <el-table-column prop="id" label="订单ID" width="100">
      </el-table-column>
      <el-table-column prop="productName" label="商品名称" min-width="150">
      </el-table-column>
      <el-table-column prop="pointsPrice" label="积分价格" width="100">
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <span :class="getStatusClass(scope.row.status)">{{ getStatusName(scope.row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="兑换时间" width="180">
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
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取兑换订单
const getOrders = async () => {
  try {
    const response = await getExchangeOrders(currentPage.value, pageSize.value);
    orders.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('获取兑换订单失败:', error);
    ElMessage.error('获取兑换订单失败');
    orders.value = [];
  }
};

// 状态样式
const getStatusClass = (status) => {
  if (status === 1) return 'status-pending';
  if (status === 2) return 'status-success';
  if (status === 3) return 'status-failed';
  return '';
};

// 状态名称
const getStatusName = (status) => {
  if (status === 1) return '待处理';
  if (status === 2) return '已完成';
  if (status === 3) return '兑换失败';
  return '未知状态';
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

.status-pending {
  color: #E6A23C;
  font-weight: bold;
}

.status-success {
  color: #67C23A;
  font-weight: bold;
}

.status-failed {
  color: #F56C6C;
  font-weight: bold;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>