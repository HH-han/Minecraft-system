<template>
  <div class="login-log-container">
    <div class="header">
      <h2>登录日志</h2>
      <div class="search-bar">
        <div class="search-input-group">
          <input
            v-model="searchParams.keyword"
            type="text"
            placeholder="请输入用户名或IP地址"
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="clear-btn" @click="searchParams.keyword = ''" v-if="searchParams.keyword">
            ×
          </button>
        </div>
        
        <div class="date-range-picker">
          <input
            v-model="searchParams.startTime"
            type="date"
            class="date-input"
            placeholder="开始日期"
          />
          <span class="date-separator">至</span>
          <input
            v-model="searchParams.endTime"
            type="date"
            class="date-input"
            placeholder="结束日期"
          />
        </div>
        
        <select v-model="searchParams.status" class="status-select">
          <option value="">全部状态</option>
          <option value="1">成功</option>
          <option value="0">失败</option>
        </select>
        
        <button class="search-btn" @click="handleSearch">
          <span class="search-icon">🔍</span>
          搜索
        </button>
        
        <button class="reset-btn" @click="resetSearch">
          <span class="reset-icon">↻</span>
          重置
        </button>
      </div>
    </div>
    
    <div class="log-table-container">
      <div class="statistics">
        <div class="stat-card">
          <div class="stat-value">{{ statistics.total }}</div>
          <div class="stat-label">总登录次数</div>
        </div>
        <div class="stat-card success">
          <div class="stat-value">{{ statistics.success }}</div>
          <div class="stat-label">成功登录</div>
        </div>
        <div class="stat-card danger">
          <div class="stat-value">{{ statistics.fail }}</div>
          <div class="stat-label">失败登录</div>
        </div>
        <div class="stat-card warning">
          <div class="stat-value">{{ statistics.uniqueUsers }}</div>
          <div class="stat-label">独立用户</div>
        </div>
      </div>
      
      <table class="log-table" v-if="logData.length > 0">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户ID</th>
            <th>用户名</th>
            <th>登录状态</th>
            <th>登录IP</th>
            <th>登录地点</th>
            <th>登录时间</th>
            <th>设备信息</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in logData" :key="item.id">
            <td>{{ item.id }}</td>
            <td >{{ item.userId }}</td>
            <td>{{ item.username }}</td>
            <td>
              <span class="status-tag" :class="item.status === '1' ? 'success' : 'danger'">
                {{ item.status === '1' ? '成功' : '失败' }}
              </span>
              <span v-if="item.status === '0'" class="error-msg">({{ item.msg }})</span>
            </td>
            <td>{{ item.ipaddr }}</td>
            <td>{{ item.loginLocation || '未知' }}</td>
            <td>{{ formatDateTime(item.loginTime) }}</td>
            <td>{{ getDeviceInfo(item) }}</td>
            <td>
              <button class="detail-btn" @click="showDetail(item)">
                详情
              </button>
              <button class="block-btn" @click="handleBlock(item.ipaddr)" v-if="item.status === '0'">
                封禁IP
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="empty-state" v-else>
        <div class="empty-icon">📄</div>
        <p>暂无登录日志数据</p>
      </div>
      
      <div class="pagination" v-if="pagination.total > 0">
        <div class="pagination-controls">
          <select v-model="pagination.size" @change="handleSizeChange" class="page-size-select">
            <option value="10">10条/页</option>
            <option value="20">20条/页</option>
            <option value="50">50条/页</option>
            <option value="100">100条/页</option>
          </select>
          
          <button 
            class="page-btn" 
            :disabled="pagination.current === 1"
            @click="handleCurrentChange(pagination.current - 1)"
          >
            上一页
          </button>
          
          <template v-for="page in visiblePages" :key="page">
            <button
              v-if="page === '...'"
              class="page-ellipsis"
              disabled
            >
              ...
            </button>
            <button
              v-else
              class="page-btn"
              :class="{ active: page === pagination.current }"
              @click="handleCurrentChange(page)"
            >
              {{ page }}
            </button>
          </template>
          
          <button 
            class="page-btn" 
            :disabled="pagination.current === totalPages"
            @click="handleCurrentChange(pagination.current + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
    
    <!-- 日志详情对话框 -->
    <div class="modal-overlay" v-if="detailDialogVisible" @click.self="detailDialogVisible = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>登录日志详情</h3>
          <button class="modal-close" @click="detailDialogVisible = false">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-row">
            <span class="detail-label">日志ID:</span>
            <span class="detail-value">{{ currentLog.id }}</span>
          </div>
          <div class="detail-row"> 
            <span class="detail-label">用户ID:</span>
            <span class="detail-value">{{ currentLog.userId }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">用户名:</span>
            <span class="detail-value">{{ currentLog.username }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">登录状态:</span>
            <span class="detail-value">
              <span class="status-tag" :class="currentLog.status === '1' ? 'success' : 'danger'">
                {{ currentLog.status === '1' ? '成功' : '失败' }}
              </span>
            </span>
          </div>
          <div class="detail-row" v-if="currentLog.status === '0'">
            <span class="detail-label">错误信息:</span>
            <span class="detail-value error-msg">{{ currentLog.msg }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">IP地址:</span>
            <span class="detail-value">{{ currentLog.ipaddr }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">登录地点:</span>
            <span class="detail-value">{{ currentLog.loginLocation || '未知' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">登录时间:</span>
            <span class="detail-value">{{ formatDateTime(currentLog.loginTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">浏览器:</span>
            <span class="detail-value">{{ currentLog.browser || '未知' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">操作系统:</span>
            <span class="detail-value">{{ currentLog.os || '未知' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">登录消息:</span>
            <span class="detail-value">{{ currentLog.msg || '无' }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-btn danger" @click="handleBlock(currentLog.ipaddr)" v-if="currentLog.status === '0'">
            封禁此IP
          </button>
          <button class="modal-btn" @click="detailDialogVisible = false">关闭</button>
        </div>
      </div>
    </div>
    
    <!-- IP封禁确认对话框 -->
    <div class="modal-overlay" v-if="blockDialogVisible" @click.self="blockDialogVisible = false">
      <div class="modal-content small">
        <div class="modal-header">
          <h3>确认封禁IP</h3>
          <button class="modal-close" @click="blockDialogVisible = false">×</button>
        </div>
        <div class="modal-body">
          <p>确定要封禁IP地址 <strong>{{ blockIP }}</strong> 吗？</p>
          <div class="block-duration">
            <label>封禁时长:</label>
            <select v-model="blockDuration">
              <option value="1">1小时</option>
              <option value="24">1天</option>
              <option value="168">1周</option>
              <option value="720">1个月</option>
              <option value="8760">永久</option>
            </select>
          </div>
          <div class="block-reason">
            <label>封禁原因:</label>
            <textarea v-model="blockReason" placeholder="请输入封禁原因(可选)"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-btn danger" @click="confirmBlock">
            确认封禁
          </button>
          <button class="modal-btn" @click="blockDialogVisible = false">取消</button>
        </div>
      </div>
    </div>
    
    <div class="loading-overlay" v-if="loading">
      <div class="loading-spinner"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import loginLogApi from '@/api/loginlog'

// 搜索参数
const searchParams = ref({
  keyword: '',
  startTime: '',
  endTime: '',
  status: ''
})

// 分页参数
const pagination = ref({
  current: 1,
  size: 20,
  total: 0
})

// 统计信息
const statistics = ref({
  total: 0,
  success: 0,
  fail: 0,
  uniqueUsers: 0
})

// 表格数据
const logData = ref([])
const loading = ref(false)

// 详情对话框
const detailDialogVisible = ref(false)
const currentLog = ref({})

// IP封禁相关
const blockDialogVisible = ref(false)
const blockIP = ref('')
const blockDuration = ref('24')
const blockReason = ref('')

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(pagination.value.total / pagination.value.size)
})

// 计算可见的页码
const visiblePages = computed(() => {
  const pages = []
  const current = pagination.value.current
  const total = totalPages.value
  const range = 2 // 显示当前页前后各2页
  
  pages.push(1)
  
  if (current - range > 2) {
    pages.push('...')
  }
  
  for (let i = Math.max(2, current - range); i <= Math.min(current + range, total - 1); i++) {
    pages.push(i)
  }
  
  if (current + range < total - 1) {
    pages.push('...')
  }
  
  if (total > 1) {
    pages.push(total)
  }
  
  return pages
})

// 获取登录方式文本
const getLoginType = (type) => {
  const typeMap = {
    '1': '账号密码',
    '2': '手机验证码',
    '3': '第三方登录',
    '4': 'LDAP'
  }
  return typeMap[type] || '未知'
}

// 获取设备信息
const getDeviceInfo = (item) => {
  const parts = []
  if (item.browser) parts.push(item.browser)
  if (item.os) parts.push(item.os)
  return parts.length > 0 ? parts.join(' / ') : '未知'
}

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return '未知时间'
  const d = new Date(date)
  if (isNaN(d.getTime())) return '未知时间'
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取日志列表
const fetchLogs = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      keyword: searchParams.value.keyword,
      status: searchParams.value.status,
      startTime: searchParams.value.startTime,
      endTime: searchParams.value.endTime
    }
    
    const res = await loginLogApi.getLoginLogs(params)
    logData.value = res.data.list
    pagination.value.total = res.data.total
    statistics.value = res.data.statistics || {
      total: 0,
      success: 0,
      fail: 0,
      uniqueUsers: 0
    }
  } catch (error) {
    console.error('获取登录日志失败:', error)
    // 这里可以添加错误提示
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.current = 1
  fetchLogs()
}

// 重置搜索
const resetSearch = () => {
  searchParams.value = {
    keyword: '',
    startTime: '',
    endTime: '',
    status: ''
  }
  handleSearch()
}

// 分页大小改变
const handleSizeChange = () => {
  pagination.value.current = 1
  fetchLogs()
}

// 当前页改变
const handleCurrentChange = (page) => {
  pagination.value.current = page
  fetchLogs()
}

// 查看详情
const showDetail = async (row) => {
  try {
    const res = await loginLogApi.getLoginLogDetail(row.id)
    currentLog.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取日志详情失败:', error)
    // 这里可以添加错误提示
  }
}

// 处理IP封禁
const handleBlock = (ip) => {
  blockIP.value = ip
  blockDialogVisible.value = true
}

// 确认封禁IP
const confirmBlock = async () => {
  try {
    await loginLogApi.blockIPAddress({
      ip: blockIP.value,
      duration: blockDuration.value,
      reason: blockReason.value
    })
    blockDialogVisible.value = false
    // 这里可以添加成功提示
    fetchLogs() // 刷新数据
  } catch (error) {
    console.error('封禁IP失败:', error)
    // 这里可以添加错误提示
  }
}

// 初始化加载数据
onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
/* 基础样式 */
.login-log-container {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin: 0 auto;
  padding: 20px;
  color: #333;
}

.header {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.header h2 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

/* 搜索栏样式 */
.search-bar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input-group {
  position: relative;
  flex: 1;
  min-width: 200px;
}

.search-input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.clear-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
  font-size: 16px;
}

.clear-btn:hover {
  color: #666;
}

.date-range-picker {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.date-separator {
  color: #777;
}

.status-select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
  min-width: 120px;
}

.search-btn, .reset-btn {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
}

.search-btn {
  background-color: #3498db;
  color: white;
}

.search-btn:hover {
  background-color: #2980b9;
}

.reset-btn {
  background-color: #f1f1f1;
  color: #555;
}

.reset-btn:hover {
  background-color: #e0e0e0;
}

/* 统计卡片样式 */
.statistics {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.stat-card {
  flex: 1;
  min-width: 200px;
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-left: 4px solid #3498db;
}

.stat-card.success {
  border-left-color: #4caf50;
}

.stat-card.danger {
  border-left-color: #f44336;
}

.stat-card.warning {
  border-left-color: #ff9800;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 表格样式 */
.log-table-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.log-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.log-table th {
  background-color: #f8f9fa;
  padding: 12px 15px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #e9ecef;
}

.log-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #e9ecef;
  vertical-align: middle;
}

.log-table tr:hover {
  background-color: #f8f9fa;
}

.status-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.success {
  background-color: #e8f5e9;
  color: #388e3c;
}

.status-tag.danger {
  background-color: #ffebee;
  color: #d32f2f;
}

.error-msg {
  font-size: 12px;
  color: #d32f2f;
  margin-left: 5px;
}

.detail-btn, .block-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: background-color 0.3s;
  margin-right: 5px;
}

.detail-btn {
  background-color: #3498db;
  color: white;
}

.detail-btn:hover {
  background-color: #2980b9;
}

.block-btn {
  background-color: #f44336;
  color: white;
}

.block-btn:hover {
  background-color: #d32f2f;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 50px 0;
  color: #777;
}

.empty-icon {
  font-size: 50px;
  margin-bottom: 15px;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.pagination-controls {
  display: flex;
  gap: 5px;
}

.page-size-select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 10px;
}

.page-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
}

.page-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.page-btn.active {
  background-color: #3498db;
  color: white;
  border-color: #3498db;
}

.page-ellipsis {
  padding: 8px 12px;
  border: none;
  background: none;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 700px;
  max-width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s;
}

.modal-content.small {
  width: 500px;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2c3e50;
}

.modal-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #777;
  padding: 5px;
}

.modal-close:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.detail-row {
  display: flex;
  margin-bottom: 15px;
}

.detail-label {
  width: 120px;
  font-weight: 600;
  color: #555;
}

.detail-value {
  flex: 1;
  color: #333;
}

.error-msg {
  white-space: pre-wrap;
  word-break: break-all;
  background-color: #f8f9fa;
  padding: 8px;
  border-radius: 4px;
  display: inline-block;
  max-width: 100%;
  overflow-x: auto;
}

.modal-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  text-align: right;
}

.modal-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-left: 10px;
}

.modal-btn.danger {
  background-color: #f44336;
  color: white;
}

.modal-btn.danger:hover {
  background-color: #d32f2f;
}

.modal-btn:hover {
  background-color: #2980b9;
}

/* IP封禁表单样式 */
.block-duration, .block-reason {
  margin: 15px 0;
}

.block-duration select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-left: 10px;
}

.block-reason textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-height: 80px;
  margin-top: 5px;
  font-family: inherit;
}

/* 加载动画 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>