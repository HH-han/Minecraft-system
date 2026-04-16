<template>
  <div class="online-users">
    <div class="page-header">
      <h2>在线用户监控</h2>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-label">在线用户</div>
          <div class="stats-value">{{ userStats?.onlineCount || 0 }}</div>
          <div class="stats-unit">人</div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-label">今日登录</div>
          <div class="stats-value">{{ userStats?.todayLoginCount || 0 }}</div>
          <div class="stats-unit">次</div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-label">总用户数</div>
          <div class="stats-value">{{ userCount?.totalCount?.totalCount || 0 }}</div>
          <div class="stats-unit">人</div>
        </div>
      </el-card>
      <el-card class="stats-card">
        <div class="stats-item">
          <div class="stats-label">活跃用户</div>
          <div class="stats-value">{{ userStats?.activeCount || 0 }}</div>
          <div class="stats-unit">人</div>
        </div>
      </el-card>
    </div>

    <!-- 登录趋势图表 -->
    <el-card class="chart-card">
      <template #header>
        <span>登录趋势</span>
      </template>
      <div ref="trendChart" class="chart"></div>
    </el-card>

    <!-- 在线用户列表 -->
    <el-card class="users-card">
      <template #header>
        <div class="card-header">
          <span>在线用户列表</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名或昵称"
            style="width: 300px"
            @keyup.enter="fetchOnlineUsers"
          >
            <template #append>
              <el-button @click="fetchOnlineUsers">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </template>
      
      <el-table :data="onlineUsers" style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ scope.row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="loginIp" label="登录IP" width="150" />
        <el-table-column prop="loginTime" label="登录时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.loginTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastActivityTime" label="最后活动" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.lastActivityTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status === 'online' ? '在线' : '空闲' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="viewUserActivity(scope.row.id)"
            >
              活动
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleForceLogout(scope.row.id)"
            >
              下线
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
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
    </el-card>

    <!-- 用户活动对话框 -->
    <el-dialog
      v-model="activityDialogVisible"
      title="用户活动日志"
      width="600px"
    >
      <div class="activity-log">
        <div v-if="userActivity.length > 0">
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in userActivity"
              :key="index"
              type="primary"
              :timestamp="activity.split(' - ')[0]"
            >
              {{ activity.split(' - ')[1] }}
            </el-timeline-item>
          </el-timeline>
        </div>
        <div v-else class="no-activity">
          暂无活动日志
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getOnlineUsers, getUserStats, forceLogout, getUserActivity } from '@/api/onlineUsers'

// 状态
const onlineUsers = ref([])
const userStats = ref(null)
const userActivity = ref([])
const searchKeyword = ref('')
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 图表
const trendChart = ref(null)
let trendChartInstance = null

// 对话框
const activityDialogVisible = ref(false)

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN')
}

// 获取角色标签类型
const getRoleType = (role) => {
  switch (role) {
    case 'admin': return 'danger'
    case 'vip': return 'warning'
    default: return 'info'
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  return status === 'online' ? 'success' : 'info'
}

// 获取在线用户列表
const fetchOnlineUsers = async () => {
  try {
    loading.value = true
    const response = await getOnlineUsers({
      page: currentPage.value,
      size: pageSize.value
    })
    onlineUsers.value = response.data
    total.value = onlineUsers.value.length
  } catch (error) {
    ElMessage.error('获取在线用户列表失败')
    console.error('获取在线用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取用户统计信息
const fetchUserStats = async () => {
  try {
    const response = await getUserStats()
    userStats.value = response.data
    nextTick(() => {
      initTrendChart()
    })
  } catch (error) {
    ElMessage.error('获取用户统计信息失败')
    console.error('获取用户统计信息失败:', error)
  }
}

// 初始化趋势图表
const initTrendChart = () => {
  if (!trendChart.value || !userStats.value?.loginTrend) return
  
  if (trendChartInstance) {
    trendChartInstance.dispose()
  }
  
  trendChartInstance = echarts.init(trendChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}次'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: userStats.value.loginTrend.timePoints
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [{
      name: '登录次数',
      type: 'line',
      smooth: true,
      data: userStats.value.loginTrend.loginCounts,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(64, 158, 255, 0.3)'
          }, {
            offset: 1, color: 'rgba(64, 158, 255, 0.1)'
          }]
        }
      },
      lineStyle: {
        color: '#409EFF'
      },
      itemStyle: {
        color: '#409EFF'
      }
    }]
  }
  
  trendChartInstance.setOption(option)
  
  // 响应式调整
  window.addEventListener('resize', () => {
    trendChartInstance.resize()
  })
}

// 查看用户活动
const viewUserActivity = async (userId) => {
  try {
    const response = await getUserActivity({ userId })
    userActivity.value = response.data
    activityDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户活动日志失败')
    console.error('获取用户活动日志失败:', error)
  }
}

// 强制用户下线
const handleForceLogout = async (userId) => {
  try {
    await forceLogout(userId)
    ElMessage.success('用户已被强制下线')
    // 重新获取用户列表
    fetchOnlineUsers()
    fetchUserStats()
  } catch (error) {
    ElMessage.error('强制用户下线失败')
    console.error('强制用户下线失败:', error)
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchOnlineUsers()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchOnlineUsers()
}

// 页面加载时获取数据
onMounted(() => {
  fetchUserStats()
  fetchOnlineUsers()
})
</script>

<style scoped>
.online-users {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
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

.chart-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.chart {
  height: 300px;
  width: 100%;
}

.users-card {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.activity-log {
  max-height: 400px;
  overflow-y: auto;
}

.no-activity {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .el-input {
    width: 100% !important;
  }
  
  .pagination {
    justify-content: center;
  }
}
</style>
