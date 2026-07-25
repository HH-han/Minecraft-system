<template>
  <div class="historical-orders">
    <div class="orders-header">
      <h2>历史订单</h2>
      <div class="order-filters">
        <button 
          v-for="filter in filters" 
          :key="filter.value"
          :class="['filter-btn', { active: activeFilter === filter.value }]"
          @click="activeFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>
    
    <div class="orders-list">
      <div v-if="orders.length === 0" class="empty-orders">
        <div class="empty-icon">
          <svg t="1773668737283" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" width="128" height="128">
                <path d="M758.723 194.016c27.266 0 49.345-21.366 49.345-47.699V50.92c0-26.35-22.08-47.714-49.345-47.714-27.267 0-49.347 21.365-49.347 47.714v95.398c0.002 26.333 22.082 47.699 49.347 47.699z m-493.448 0c27.267 0 49.346-21.366 49.346-47.699V50.92c0-26.35-22.079-47.714-49.346-47.714-27.266 0-49.344 21.365-49.344 47.714v95.398c0.001 26.333 22.08 47.699 49.344 49.344z" fill="currentColor"></path>
                <path d="M939.642 98.617h-82.23v47.698c0 52.668-44.188 95.4-98.688 95.4s-98.687-42.732-98.687-95.4V98.617H607.4c0-1.117-40.9 0-95.4 0s-95.398 0.496-95.398 0h-52.639v47.698c0 52.668-44.156 95.4-98.688 95.4-54.499 0-98.688-42.732-98.688-95.4V98.617h-82.23c-27.266 0-49.346 21.367-49.346 47.698v826.78c0 26.333 22.08 47.699 49.346 47.699h855.287c27.266 0 49.345-21.366 49.345-47.7V146.316c-0.002-26.331-22.082-47.698-49.347-47.698zM725.817 861.795H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.195H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.196H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.683 15.961-35.644 35.644-35.644h427.635c19.685 0 35.646 15.96 35.646 35.644 0.001 19.673-15.96 35.634-35.645 35.634z" fill="currentColor"></path>
              </svg>
        </div>
        <div class="empty-text">暂无历史订单</div>
        <router-link to="/payment" class="btn btn-primary">去购物</router-link>
      </div>
      <div v-else class="order-item" v-for="order in filteredOrders" :key="order.id">
        <div class="order-header">
          <div class="order-id">订单编号：{{ order.orderId }}</div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </div>
        </div>
        <div class="order-content">
          <div class="order-info">
            <div class="info-item">
              <span class="label">下单时间</span>
              <span class="value">{{ order.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">支付时间</span>
              <span class="value">{{ order.updateTime || '未支付' }}</span>
            </div>
            <div class="info-item">
              <span class="label">订单金额</span>
              <span class="value price">{{ order.totalPrice }} 元</span>
            </div>
          </div>
          <div class="order-products">
            <div class="product-item" v-for="(item, index) in order.items" :key="index">
              <div class="product-image">
                <img :src="item.image" :alt="item.name">
              </div>
              <div class="product-info">
                <div class="product-name">{{ item.name }}</div>
                <div class="product-price">
                  <span class="price">{{ item.price }} 元</span>
                  <span class="quantity">x{{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="order-footer">
          <div class="order-total">
            共 {{ order.quantity }} 件商品，合计：
            <span class="total-price">{{ order.totalPrice }} 元</span>
          </div>
          <div class="order-actions">
            <button v-if="order.status === '0'" class="btn btn-primary" @click="goToPay(order)">
              立即支付
            </button>
            <button v-if="order.status === '0'" class="btn btn-secondary" @click="handleCancelOrder(order.id)">
              取消订单
            </button>
            <button v-if="order.status === '3'" class="btn btn-secondary" @click="reviewOrder(order.id)">
              评价
            </button>
            <button class="btn btn-link" @click="viewOrderDetail(order.id)">
              查看详情
            </button>
            <button class="btn btn-secondary" @click="viewOrderDetail(order.id)">
              查看物流
            </button>
            <button class="btn btn-delete" @click="handleDeleteOrder(order.id)">
              删除订单
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="orders-pagination" v-if="totalPages > 1">
      <button 
        class="page-btn" 
        :disabled="currentPage === 1"
        @click="currentPage--"
      >上一页</button>
      <span class="page-info">
        {{ currentPage }} / {{ totalPages }}
      </span>
      <button 
        class="page-btn" 
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, cancelOrder, deleteOrder } from '@/api/order.js'

const router = useRouter()

// 订单数据
const orders = ref([])
const loading = ref(false)
const total = ref(0)

// 筛选条件
const filters = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: '0' },
  { label: '已支付', value: '1' },
  { label: '已发货', value: '2' },
  { label: '已完成', value: '3' },
  { label: '已取消', value: '4' }
]

const activeFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

// 筛选订单
const filteredOrders = computed(() => {
  if (activeFilter.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === activeFilter.value)
})

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const response = await getOrderList({ 
      pageNum: currentPage.value, 
      pageSize: pageSize.value 
    })
    
    // 处理响应数据，转换为前端需要的格式
    const orderData = response.data.records || []
    orders.value = orderData.map(order => ({
      id: order.id,
      orderId: order.orderNo,
      status: order.status,
      createTime: order.createTime,
      updateTime: order.updateTime || '',
      totalPrice: order.amount,
      quantity: order.quantity,
      items: [{
        id: order.itemId,
        name: order.itemName,
        price: order.amount,
        quantity: order.quantity,
        image: order.image,
      }]
    }))
    
    total.value = response.data.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 获取订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '待支付',
    '1': '已支付',
    '2': '已发货',
    '3': '已完成',
    '4': '已取消',
    '5': '已退款'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态样式类
const getStatusClass = (status) => {
  const classMap = {
    '0': 'unpaid',
    '1': 'paid',
    '2': 'shipping',
    '3': 'completed',
    '4': 'cancelled',
    '5': 'refunded'
  }
  return classMap[status] || 'unpaid'
}

// 跳转到支付页面
const goToPay = (order) => {
  router.push({
    path: '/payment',
    query: {
      orderId: order.orderId
    }
  })
}

// 取消订单
const handleCancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用 API 取消订单
    await cancelOrder(orderId)
    ElMessage.success('订单已取消')
    
    // 重新获取订单列表
    await fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败，请重试')
    }
  }
}

// 删除订单
const handleDeleteOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要删除订单吗？此操作不可恢复！', '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    // 调用 API 删除订单
    await deleteOrder(orderId)
    ElMessage.success('订单已删除')
    
    // 重新获取订单列表
    await fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      ElMessage.error('删除订单失败，请重试')
    }
  }
}

// 评价订单
const reviewOrder = (orderId) => {
  ElMessage.info('评价功能开发中')
}

// 查看订单详情
const viewOrderDetail = (orderId) => {
  router.push({
    path: '/payment',
    query: {
      orderId: orderId,
      tab: 'order'
    }
  })
}

// 组件挂载时的逻辑
onMounted(() => {
  fetchOrders()
})

// 监听分页变化
watch(currentPage, () => {
  fetchOrders()
})

// 监听筛选条件变化
watch(activeFilter, () => {
  currentPage.value = 1 // 重置为第一页
  fetchOrders()
})
</script>

<style scoped>
/* Apple Design System - 极简白色设计 */
.historical-orders {
  background: #ffffff;
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 32px;
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #d2d2d6;
}

.orders-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
  letter-spacing: -0.5px;
}

.order-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 10px 20px;
  border: 1px solid #d2d2d6;
  border-radius: 40px;
  background: #ffffff;
  color: #1d1d1f;
  cursor: pointer;
  font-size: 14px;
  font-weight: 400;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
}

.filter-btn:hover {
  border-color: #2997ff;
  color: #2997ff;
  transform: translateY(-1px);
}

.filter-btn.active {
  background: #1d1d1f;
  border-color: #1d1d1f;
  color: #ffffff;
}

.empty-orders {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 0;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: #f5f5f7;
}

.empty-icon svg {
  width: 56px;
  height: 56px;
  color: #6e6e73;
}

.empty-text {
  font-size: 18px;
  color: #6e6e73;
  margin-bottom: 32px;
  font-weight: 400;
}

.orders-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  padding: 20px;
}

.order-item {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06), 
              0 1px 2px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.10), 
              0 4px 12px rgba(0, 0, 0, 0.06);
  border-color: rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #d2d2d6;
}

.order-id {
  font-size: 16px;
  color: #1d1d1f;
  font-weight: 500;
}

.order-status {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
}

/* Apple风格状态标签颜色 */
.order-status.unpaid {
  background: #fff5e6;
  color: #ff9500;
}

.order-status.paid {
  background: #e8f5ff;
  color: #2997ff;
}

.order-status.shipping {
  background: #e3f2ff;
  color: #007aff;
}

.order-status.completed {
  background: #e8f5e9;
  color: #34c759;
}

.order-status.cancelled {
  background: #ffebee;
  color: #ff3b30;
}

.order-status.refunded {
  background: #f3e5f5;
  color: #af52de;
}

.order-content {
  margin-bottom: 20px;
}

.order-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item .label {
  font-size: 14px;
  color: #6e6e73;
  font-weight: 400;
}

.info-item .value {
  font-size: 16px;
  color: #1d1d1f;
  font-weight: 400;
}

.info-item .price {
  color: #1d1d1f;
  font-weight: 600;
}

.order-products {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #ffffff;
  padding: 16px;
  border-radius: 16px;
  min-width: 240px;
  flex: 1;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.product-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.product-image {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f5f7;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.product-item:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 16px;
  color: #1d1d1f;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.product-price .price {
  color: #1d1d1f;
  font-weight: 600;
}

.product-price .quantity {
  color: #6e6e73;
  background: #f5f5f7;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.order-footer {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  padding-top: 20px;
  border-top: 1px solid #d2d2d6;
  flex-direction: column;
  gap: 20px;
}

.order-total {
  font-size: 16px;
  color: #1d1d1f;
  font-weight: 400;
}

.total-price {
  color: #1d1d1f;
  font-weight: 700;
  margin-left: 8px;
  font-size: 20px;
}

.order-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn {
  padding: 10px 24px;
  border-radius: 40px;
  font-size: 14px;
  cursor: pointer;
  border: none;
  font-weight: 400;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-primary {
  background: #1d1d1f;
  color: #ffffff;
}

.btn-primary:hover {
  background: #333333;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.btn-secondary {
  background: #f5f5f7;
  color: #1d1d1f;
  border: 1px solid #d2d2d6;
}

.btn-secondary:hover {
  background: #e8e8ed;
  border-color: #b8b8bf;
}

.btn-delete {
  background: #ffffff;
  color: #ff3b30;
  border: 1px solid #ff3b30;
}

.btn-delete:hover {
  background: #ff3b30;
  color: #ffffff;
}

.btn-link {
  background: #0887ff;
  color: #ffffff;
  padding: 10px 16px;
}

.btn-link:hover {
  text-decoration: underline;
}

.orders-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #d2d2d6;
}

.page-btn {
  padding: 12px 24px;
  border: 1px solid #d2d2d6;
  border-radius: 40px;
  background: #ffffff;
  color: #1d1d1f;
  cursor: pointer;
  font-size: 14px;
  font-weight: 400;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
}

.page-btn:hover:not(:disabled) {
  border-color: #2997ff;
  color: #2997ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(41, 151, 255, 0.12);
}

.page-btn:disabled {
  color: #d2d2d6;
  cursor: not-allowed;
  border-color: #e8e8ed;
  background: #f5f5f7;
}

.page-info {
  font-size: 16px;
  color: #1d1d1f;
  font-weight: 500;
}

/* 响应式设计 - Apple风格 */
@media (max-width: 767px) {
  .historical-orders {
    padding: 24px;
    border-radius: 20px;
  }
  
  .orders-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .orders-header h2 {
    font-size: 28px;
  }
  
  .order-filters {
    width: 100%;
    gap: 8px;
  }
  
  .filter-btn {
    flex: 1;
    text-align: center;
    padding: 10px 16px;
  }
  
  .order-info {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .order-products {
    flex-direction: column;
  }
  
  .product-item {
    width: 100%;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .order-actions {
    width: 100%;
    justify-content: flex-start;
    gap: 8px;
  }
  
  .btn {
    padding: 10px 20px;
    font-size: 13px;
  }
  
  .orders-pagination {
    gap: 16px;
  }
  
  .page-btn {
    padding: 10px 20px;
  }
}

@media (max-width: 480px) {
  .historical-orders {
    padding: 20px;
    border-radius: 16px;
  }
  
  .orders-header h2 {
    font-size: 24px;
  }
  
  .order-item {
    padding: 20px;
    border-radius: 20px;
  }
  
  .order-id {
    font-size: 14px;
  }
  
  .product-item {
    padding: 12px;
  }
  
  .product-image {
    width: 56px;
    height: 56px;
  }
  
  .product-name {
    font-size: 14px;
  }
  
  .total-price {
    font-size: 18px;
  }
}
</style>