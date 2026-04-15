<template>
  <div class="cache-monitoring">
    <div class="page-header">
      <h2>缓存监控</h2>
      <el-button type="danger" @click="handleClearAllCache" :loading="loading">
        清除所有缓存
      </el-button>
    </div>

    <!-- 缓存统计卡片 -->
    <div class="stats-cards">
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-label">缓存大小</div>
          <div class="stats-value">{{ cacheStats?.size || 0 }}</div>
          <div class="stats-unit">个键</div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-label">命中率</div>
          <div class="stats-value">{{ (cacheStats?.hitRate || 0).toFixed(2) }}</div>
          <div class="stats-unit">%</div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-label">未命中率</div>
          <div class="stats-value">{{ (cacheStats?.missRate || 0).toFixed(2) }}</div>
          <div class="stats-unit">%</div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-label">内存使用</div>
          <div class="stats-value">{{ formatMemory(cacheStats?.memoryUsage || 0) }}</div>
        </div>
      </el-card>
    </div>

    <!-- 缓存详情 -->
    <el-card class="cache-detail-card">
      <template #header>
        <div class="card-header">
          <span>缓存键列表</span>
          <el-input
            v-model="searchPattern"
            placeholder="搜索缓存键"
            style="width: 300px"
            @keyup.enter="fetchCacheKeys"
          >
            <template #append>
              <el-button @click="fetchCacheKeys">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </template>
      
      <el-table :data="cacheKeys" style="width: 100%">
        <el-table-column prop="key" label="缓存键" width="300">
          <template #default="scope">
            <span class="cache-key">{{ scope.row }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="value" label="值">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="fetchCacheDetail(scope.row)"
            >
              查看
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              type="danger"
              size="small"
              @click="handleClearCache(scope.row)"
            >
              清除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 缓存详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="缓存详情"
      width="600px"
    >
      <div class="cache-detail">
        <div class="detail-item">
          <span class="detail-label">缓存键：</span>
          <span class="detail-value">{{ currentCacheKey }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">缓存值：</span>
          <pre class="detail-value">{{ formatCacheValue(currentCacheValue) }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getCacheStats, getCacheKeys, getCacheDetail, clearCache, clearAllCache } from '@/api/cache'

// 状态
const cacheStats = ref(null)
const cacheKeys = ref([])
const searchPattern = ref('')
const loading = ref(false)

// 详情对话框
const detailDialogVisible = ref(false)
const currentCacheKey = ref('')
const currentCacheValue = ref(null)

// 格式化内存
const formatMemory = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化缓存值
const formatCacheValue = (value) => {
  if (value === null || value === undefined) return 'null'
  return JSON.stringify(value, null, 2)
}

// 获取缓存统计信息
const fetchCacheStats = async () => {
  try {
    loading.value = true
    const response = await getCacheStats()
    cacheStats.value = response.data
  } catch (error) {
    ElMessage.error('获取缓存统计信息失败')
    console.error('获取缓存统计信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取缓存键列表
const fetchCacheKeys = async () => {
  try {
    loading.value = true
    const response = await getCacheKeys(searchPattern.value)
    cacheKeys.value = response.data
  } catch (error) {
    ElMessage.error('获取缓存键列表失败')
    console.error('获取缓存键列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取缓存详情
const fetchCacheDetail = async (key) => {
  try {
    const response = await getCacheDetail(key)
    currentCacheKey.value = key
    currentCacheValue.value = response.data
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取缓存详情失败')
    console.error('获取缓存详情失败:', error)
  }
}

// 清除指定缓存
const handleClearCache = async (key) => {
  try {
    await clearCache(key)
    ElMessage.success('缓存清除成功')
    // 重新获取缓存列表
    fetchCacheKeys()
    fetchCacheStats()
  } catch (error) {
    ElMessage.error('缓存清除失败')
    console.error('缓存清除失败:', error)
  }
}

// 清除所有缓存
const handleClearAllCache = async () => {
  try {
    await clearAllCache()
    ElMessage.success('所有缓存清除成功')
    // 重新获取缓存列表和统计信息
    fetchCacheKeys()
    fetchCacheStats()
  } catch (error) {
    ElMessage.error('清除所有缓存失败')
    console.error('清除所有缓存失败:', error)
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchCacheStats()
  fetchCacheKeys()
})
</script>

<style scoped>
.cache-monitoring {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stats-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.stats-item {
  text-align: center;
  padding: 20px 0;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stats-value {
  font-size: 32px;
  font-weight: 600;
  color: #409EFF;
  margin-bottom: 4px;
}

.stats-unit {
  font-size: 12px;
  color: #909399;
}

.cache-detail-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.cache-key {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: #606266;
}

.cache-detail {
  padding: 20px;
}

.detail-item {
  margin-bottom: 16px;
}

.detail-label {
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  display: block;
}

.detail-value {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: #606266;
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  white-space: pre-wrap;
  max-height: 300px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .el-input {
    width: 100% !important;
  }
}
</style>
