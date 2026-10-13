<template>
  <div class="records-container">
    <h2 class="records-title">积分记录</h2>

    <el-table :data="pagedRecords" style="width: 100%" class="records-table">
      <el-table-column prop="id" label="记录ID" width="100">
        <template #default="scope">
          <span class="records-id">#{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="130">
        <template #default="scope">
          <span class="type-chip" :class="getTypeClass(scope.row.type)">
            <i class="type-dot"></i>{{ getTypeName(scope.row.type) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="points" label="积分变动" width="130">
        <template #default="scope">
          <span class="points-change" :class="getPointsClass(scope.row)">{{ formatPoints(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200">
        <template #default="scope">
          <span class="records-remark">{{ scope.row.remark || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="190">
        <template #default="scope">
          <span class="records-time">{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <template #empty>
        <div class="records-empty">
          <div class="records-empty__icon">✦</div>
          <p class="records-empty__title">暂无积分记录</p>
          <p class="records-empty__desc">获取或消耗积分后，记录会显示在这里</p>
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
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getPointsRecords } from '@/api/points';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';

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

// 数据变更后自动刷新
useAutoRefresh(getRecords);

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

.records-table {
  margin-bottom: 20px;
}

.records-table :deep(.el-table__header th.el-table__cell) {
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-weight: 600;
  font-size: 13px;
}

.records-table :deep(.el-table__row) {
  font-size: 13.5px;
}

.records-table :deep(td.el-table__cell),
.records-table :deep(th.el-table__cell) {
  border-bottom-color: var(--theme-hairline);
}

.records-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.records-id {
  font-variant-numeric: tabular-nums;
  font-weight: 600;
  color: var(--theme-text-3);
}

.records-remark {
  color: var(--theme-text-2);
}

.records-time {
  font-variant-numeric: tabular-nums;
  color: var(--theme-text-3);
}

/* 类型胶囊 */
.type-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 3px 11px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.type-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.type-earn {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.type-exchange {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.type-expire {
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-3);
}

/* 积分变动 */
.points-change {
  font-variant-numeric: tabular-nums;
  font-weight: 700;
}

.points-earn {
  color: var(--theme-success);
}

.points-deduct {
  color: var(--theme-danger);
}

/* 空状态 */
.records-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 48px 20px;
}

.records-empty__icon {
  font-size: 32px;
  color: var(--theme-primary);
  opacity: 0.5;
}

.records-empty__title {
  margin: 6px 0 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.records-empty__desc {
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