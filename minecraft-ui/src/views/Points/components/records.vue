<template>
  <div class="records-container">
    <h2 class="records-title">积分记录</h2>
    
    <el-table :data="records" style="width: 100%" class="records-table">
      <el-table-column prop="id" label="记录ID" width="100">
      </el-table-column>
      <el-table-column prop="type" label="类型" width="120">
        <template #default="scope">
          <span :class="getTypeClass(scope.row.type)">{{ getTypeName(scope.row.type) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="points" label="积分变动" width="120">
        <template #default="scope">
          <span :class="getPointsClass(scope.row.points)">{{ scope.row.points > 0 ? '+' : '' }}{{ scope.row.points }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200">
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="180">
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
import { getPointsRecords } from '@/api/points';

const records = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取积分记录
const getRecords = async () => {
  try {
    const response = await getPointsRecords(currentPage.value, pageSize.value);
    records.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('获取积分记录失败:', error);
    ElMessage.error('获取积分记录失败');
    records.value = [];
  }
};

// 类型样式
const getTypeClass = (type) => {
  if (type === 1) return 'type-earn';
  if (type === 2) return 'type-exchange';
  if (type === 3) return 'type-expire';
  return '';
};

// 类型名称
const getTypeName = (type) => {
  if (type === 1) return '获取积分';
  if (type === 2) return '积分兑换';
  if (type === 3) return '积分过期';
  return '其他';
};

// 积分样式
const getPointsClass = (points) => {
  return points > 0 ? 'points-earn' : 'points-deduct';
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString();
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  getRecords();
};

const handleCurrentChange = (current) => {
  currentPage.value = current;
  getRecords();
};

onMounted(() => {
  getRecords();
});
</script>

<style scoped>
.records-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.records-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.records-table {
  margin-bottom: 20px;
}

.type-earn {
  color: #67C23A;
  font-weight: bold;
}

.type-exchange {
  color: #E6A23C;
  font-weight: bold;
}

.type-expire {
  color: #909399;
  font-weight: bold;
}

.points-earn {
  color: #67C23A;
  font-weight: bold;
}

.points-deduct {
  color: #F56C6C;
  font-weight: bold;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>