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
              <span class="label">下单时间：</span>
              <span class="value">{{ order.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">支付时间：</span>
              <span class="value">{{ order.updateTime || '未支付' }}</span>
            </div>
            <div class="info-item">
              <span class="label">订单金额：</span>
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
.historical-orders {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
  transition: all 0.3s ease;
}

.historical-orders:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.15);
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.orders-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.order-filters {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.filter-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.1);
}

.filter-btn.active {
  background: rgba(255, 77, 79, 0.1);
  border-color: #ff4d4f;
  color: #ff4d4f;
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.2);
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
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.empty-icon svg {
  width: 48px;
  height: 48px;
  color: rgba(255, 255, 255, 0.5);
}

.empty-text {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 25px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.order-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.order-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.order-id {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.order-status.unpaid {
  background: rgba(255, 243, 205, 0.2);
  color: #ffd700;
  border: 1px solid rgba(255, 215, 0, 0.3);
}

.order-status.paid {
  background: rgba(209, 236, 241, 0.2);
  color: #4ecdc4;
  border: 1px solid rgba(78, 205, 196, 0.3);
}

.order-status.shipping {
  background: rgba(231, 243, 255, 0.2);
  color: #4a90e2;
  border: 1px solid rgba(74, 144, 226, 0.3);
}

.order-status.completed {
  background: rgba(212, 237, 218, 0.2);
  color: #2ecc71;
  border: 1px solid rgba(46, 204, 113, 0.3);
}

.order-status.cancelled {
  background: rgba(248, 215, 218, 0.2);
  color: #e74c3c;
  border: 1px solid rgba(231, 76, 60, 0.3);
}

.order-content {
  margin-bottom: 15px;
}

.order-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.info-item .value {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.info-item .price {
  color: #ff4d4f;
  font-weight: 600;
}

.order-products {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.05);
  padding: 10px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  min-width: 200px;
  flex: 1;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-image:hover img {
  transform: scale(1.05);
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.product-price .price {
  color: #ff4d4f;
  font-weight: 600;
}

.product-price .quantity {
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 8px;
  border-radius: 8px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.order-total {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.total-price {
  color: #ff4d4f;
  font-weight: 600;
  margin-left: 5px;
  font-size: 16px;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.1);
}

.btn-primary {
  background-image: linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%);
  color: #fff;
  border-color: #00aaff94;
  box-shadow: 0 4px 12px rgba(77, 193, 255, 0.3);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%);
  border-color: #66a6ff;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(77, 193, 255, 0.4);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

.btn-secondary:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.btn-delete {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
  border-color: rgba(231, 76, 60, 0.3);
}

.btn-delete:hover {
  background: rgba(231, 76, 60, 0.2);
  border-color: rgba(231, 76, 60, 0.5);
  color: #e74c3c;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
}

.btn-link {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

.btn-link:hover {
  color: #ff4d4f;
  background: none;
  transform: none;
  box-shadow: none;
}

.orders-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.page-btn:hover:not(:disabled) {
  border-color: #ff4d4f;
  color: #ff4d4f;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.2);
  background: rgba(255, 77, 79, 0.05);
}

.page-btn:disabled {
  color: rgba(255, 255, 255, 0.3);
  cursor: not-allowed;
  border-color: rgba(255, 255, 255, 0.1);
}

.page-info {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

@media (max-width: 768px) {
  .historical-orders {
    padding: 20px;
  }
  
  .orders-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .order-filters {
    width: 100%;
  }
  
  .filter-btn {
    flex: 1;
    text-align: center;
  }
  
  .order-info {
    grid-template-columns: 1fr;
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
    gap: 15px;
  }
  
  .order-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .btn {
    flex: 1;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .historical-orders {
    padding: 15px;
  }
  
  .orders-header h2 {
    font-size: 18px;
  }
  
  .order-item {
    padding: 15px;
  }
  
  .order-id {
    font-size: 12px;
  }
  
  .product-item {
    padding: 8px;
  }
  
  .product-image {
    width: 50px;
    height: 50px;
  }
}
</style>