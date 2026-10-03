<template>
  <div class="records-container">
    <h2 class="records-title">积分记录</h2>
    
    <el-table :data="pagedRecords" style="width: 100%" class="records-table">
      <el-table-column prop="id" label="记录ID" width="100">
      </el-table-column>
      <el-table-column prop="type" label="类型" width="120">
        <template #default="scope">
          <span :class="getTypeClass(scope.row.type)">{{ getTypeName(scope.row.type) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="points" label="积分变动" width="120">
        <template #default="scope">
          <span :class="getPointsClass(scope.row)">{{ formatPoints(scope.row) }}</span>
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
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getPointsRecords } from '@/api/points';

// 后端返回全量数组（无分页），allRecords 保存完整数据，pagedRecords 做前端分页
const allRecords = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = computed(() => allRecords.value.length);

const pagedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return allRecords.value.slice(start, start + pageSize.value);
});

// 获取积分记录
const getRecords = async () => {
  try {
    const response = await getPointsRecords(currentPage.value, pageSize.value);
    allRecords.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('获取积分记录失败:', error);
    ElMessage.error('获取积分记录失败');
    allRecords.value = [];
  }
};

// 类型映射：后端使用字符串枚举 INCOME / EXCHANGE（兼容旧的数字类型）
const TYPE_MAP = {
  INCOME: { name: '获取积分', class: 'type-earn' },
  EXCHANGE: { name: '积分兑换', class: 'type-exchange' },
  EXPIRE: { name: '积分过期', class: 'type-expire' },
  1: { name: '获取积分', class: 'type-earn' },
  2: { name: '积分兑换', class: 'type-exchange' },
  3: { name: '积分过期', class: 'type-expire' }
};

// 类型样式
const getTypeClass = (type) => {
  return TYPE_MAP[type]?.class || '';
};

// 类型名称
const getTypeName = (type) => {
  return TYPE_MAP[type]?.name || type || '其他';
};

// 是否为积分扣减记录（points 字段恒为正，方向由 type 决定）
const isDeduct = (row) => {
  const type = row.type;
  return type === 'EXCHANGE' || type === 'EXPIRE' || type === 2 || type === 3;
};

// 积分变动样式
const getPointsClass = (row) => {
  return isDeduct(row) ? 'points-deduct' : 'points-earn';
};

// 积分变动文案：收入 +N，扣减 -N
const formatPoints = (row) => {
  const points = row.points ?? 0;
  return isDeduct(row) ? `-${points}` : `+${points}`;
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString();
};

// 分页处理（前端分页，无需重新请求）
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleCurrentChange = (current) => {
  currentPage.value = current;
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