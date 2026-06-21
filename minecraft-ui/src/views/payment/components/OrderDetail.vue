<template>
  <div class="order-detail" v-if="hasOrderData">
    <div class="order-header">
      <h2 class="order-title">订单详情</h2>
      <div class="order-status" :class="getStatusClass(order.status)">
        {{ getStatusText(order.status) }}
      </div>
    </div>

    <div class="order-info-card">
      <div class="info-item">
        <span class="label">订单编号</span>
        <span class="value">{{ order.orderId }}</span>
      </div>
      <div class="info-item">
        <span class="label">下单时间</span>
        <span class="value">{{ order.createTime }}</span>
      </div>
      <div class="info-item">
        <span class="label">支付时间</span>
        <span class="value">{{ order.payTime || '未支付' }}</span>
      </div>
      <div class="info-item">
        <span class="label">订单金额</span>
        <span class="value price">¥{{ order.totalPrice }}</span>
      </div>
    </div>
    
    <div class="shipping-address-card">
      <h3 class="section-title">收货地址</h3>
      <div class="address-content">
        <div class="name-phone">
          <span class="name">{{ order.address.name }}</span>
          <span class="phone">{{ order.address.phone }}</span>
        </div>
        <div class="address-detail">
          {{ order.address.province }}{{ order.address.city }}{{ order.address.district }}{{ order.address.detail }}
        </div>
      </div>
    </div>
    
    <div class="order-items-card">
      <h3 class="section-title">商品信息</h3>
      <div class="items-list">
        <div v-for="item in order.items" :key="item.id" class="item">
          <div class="item-image">
            <img :src="item.image" :alt="item.name">
          </div>
          <div class="item-info">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-spec" v-if="item.spec">
              {{ item.spec }}
            </div>
            <div class="item-price">
              <span class="price">¥{{ item.price }}</span>
              <span class="quantity">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="order-summary-card">
      <div class="summary-item">
        <span class="summary-label">商品总价</span>
        <span class="summary-value">¥{{ order.totalPrice }}</span>
      </div>
      <div class="summary-item">
        <span class="summary-label">运费</span>
        <span class="summary-value">¥{{ order.shippingFee }}</span>
      </div>
      <div class="summary-item total">
        <span class="summary-label">实付金额</span>
        <span class="summary-value price">¥{{ order.payPrice }}</span>
      </div>
    </div>
    
    <div class="order-actions" v-if="order.status === '0'">
      <button class="btn btn-secondary" @click="handleCancelOrder">取消订单</button>
      <button class="btn btn-primary" @click="goToPay">立即支付</button>
    </div>
  </div>
  
  <!-- 无订单数据页面 -->
  <div class="no-order" v-else>
    <div class="no-order-content">
      <div class="no-order-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="15" y1="9" x2="9" y2="15"></line>
          <line x1="9" y1="9" x2="15" y2="15"></line>
        </svg>
      </div>
      <h3 class="no-order-title">暂无订单数据</h3>
      <p class="no-order-desc">您还没有相关订单信息，请先去浏览商品</p>
      <button class="btn btn-primary" @click="goToHome">返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getFoodDetail } from '@/api/food.js'
import { getProductDetail } from '@/api/product.js'
import { getOrderDetail, cancelOrder } from '@/api/order.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const emit = defineEmits(['pay'])
const router = useRouter()
const route = useRoute()

const order = ref({
  orderId: '20240101001',
  status: 'unpaid',
  createTime: new Date().toLocaleString('zh-CN'),
  payTime: '',
  totalPrice: 0,
  shippingFee: 0,
  payPrice: 0,
  address: {
    name: '张三',
    phone: '13800138000',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detail: '某某街道某某小区1号楼101室'
  },
  items: []
})

// 订单数据是否存在
const hasOrderData = ref(false)

// 获取订单数据
const fetchProductData = async () => {
  const orderId = route.query.orderId
  const id = route.query.id
  const commodity = route.query.commodity
  
  // 初始设置为false
  hasOrderData.value = false
  
  if (orderId) {
    // 如果有订单编号，从后端获取订单数据
    try {
      const response = await getOrderDetail(orderId)
      const orderData = response.data
      
      if (orderData) {
        // 构建订单信息
        order.value = {
          orderId: orderData.orderNo || orderData.id,
          status: orderData.status,
          createTime: new Date(orderData.createTime).toLocaleString('zh-CN'),
          payTime: orderData.payTime ? new Date(orderData.payTime).toLocaleString('zh-CN') : '',
          totalPrice: 0, // 后端可能没有计算，需要从商品API获取
          shippingFee: 0,
          payPrice: 0,
          address: {
            name: '张三',
            phone: '13800138000',
            province: '北京市',
            city: '北京市',
            district: '朝阳区',
            detail: '某某街道某某小区1号楼101室'
          },
          items: []
        }
        
        // 根据商品类型获取商品详情
        if (id && commodity) {
          if (commodity === '0') {
            // 美食类型，调用food API
            const foodResponse = await getFoodDetail(id)
            const food = foodResponse.data
            order.value.items = [{
              id: food.id,
              name: food.name,
              price: food.price,
              quantity: 1,
              image: food.coverImage
            }]
            order.value.totalPrice = food.price
            order.value.payPrice = food.price
          } else if (commodity === '1') {
            // 纪念品类型，调用product API
            const productResponse = await getProductDetail(id)
            const product = productResponse.data
            order.value.items = [{
              id: product.id,
              name: product.name,
              price: product.price,
              quantity: 1,
              image: product.coverImage
            }]
            order.value.totalPrice = product.price
            order.value.payPrice = product.price
          }
        }
        
        // 设置为有订单数据
        hasOrderData.value = true
      }
    } catch (err) {
      console.error('获取订单数据失败:', err)
      // 如果获取订单失败，使用默认数据
      setDefaultOrderData()
    }
  } else {
    // 如果没有订单编号，使用默认数据
    setDefaultOrderData()
  }
}

// 设置默认订单数据
const setDefaultOrderData = () => {
  const id = route.query.id
  const commodity = route.query.commodity
  
  if (id && commodity) {
    // 根据商品类型获取商品详情
    if (commodity === '0') {
      // 美食类型，调用food API
      getFoodDetail(id).then(response => {
        const food = response.data
        if (food) {
          order.value.items = [{
            id: food.id,
            name: food.name,
            price: food.price,
            quantity: 1,
            image: food.coverImage
          }]
          order.value.totalPrice = food.price
          order.value.payPrice = food.price
          // 设置为有订单数据
          hasOrderData.value = true
        }
      }).catch(err => {
        console.error('获取美食数据失败:', err)
      })
    } else if (commodity === '1') {
      // 纪念品类型，调用product API
      getProductDetail(id).then(response => {
        const product = response.data
        if (product) {
          order.value.items = [{
            id: product.id,
            name: product.name,
            price: product.price,
            quantity: 1,
            image: product.coverImage
          }]
          order.value.totalPrice = product.price
          order.value.payPrice = product.price
          // 设置为有订单数据
          hasOrderData.value = true
        }
      }).catch(err => {
        console.error('获取商品数据失败:', err)
      })
    }
  }
}

const getStatusText = (status) => {
  const statusMap = {
    '0': '待支付',
    '1': '已支付',
    '2': '已发货',
    '3': '已完成',
    '4': '已取消',
    '5': '已退款',
    unpaid: '待支付',
    paid: '已支付',
    shipping: '配送中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return status in statusMap ? statusMap[status] : '未知状态'
}

const getStatusClass = (status) => {
  const classMap = {
    '0': 'unpaid',
    '1': 'paid',
    '2': 'shipping',
    '3': 'completed',
    '4': 'cancelled',
    '5': 'refunded',
    unpaid: 'unpaid',
    paid: 'paid',
    shipping: 'shipping',
    completed: 'completed',
    cancelled: 'cancelled'
  }
  return classMap[status] || 'unpaid'
}

const goToPay = () => {
  // 触发支付事件
  emit('pay')
}

const goToHome = () => {
  // 跳转到首页
  router.push('/')
}

const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const orderId = route.query.orderId
    if (orderId) {
      await cancelOrder(orderId)
      order.value.status = '4' // 4-已取消
      ElMessage.success('订单已取消')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败，请重试')
    }
  }
}

// 组件挂载时获取商品数据
onMounted(() => {
  fetchProductData()
})
</script>

<style scoped>
/* Apple Design System */
.order-detail {
  background: #ffffff;
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #d2d2d6;
}

.order-title {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
  letter-spacing: -0.02em;
}

.order-status {
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.order-status.unpaid {
  background: rgba(255, 149, 0, 0.12);
  color: #ff9500;
}

.order-status.paid {
  background: rgba(52, 199, 89, 0.12);
  color: #34c759;
}

.order-status.shipping {
  background: rgba(41, 151, 255, 0.12);
  color: #2997ff;
}

.order-status.completed {
  background: rgba(52, 199, 89, 0.12);
  color: #34c759;
}

.order-status.cancelled {
  background: rgba(142, 142, 147, 0.12);
  color: #8e8e93;
}

.order-status.refunded {
  background: rgba(142, 142, 147, 0.12);
  color: #8e8e93;
}

/* 订单信息卡片 */
.order-info-card {
  background: #f5f5f7;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 16px;
  font-weight: 400;
  color: #6e6e73;
}

.info-item .value {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 16px;
  font-weight: 400;
  color: #1d1d1f;
}

.info-item .price {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

/* 收货地址卡片 */
.shipping-address-card {
  background: #f5f5f7;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 24px;
}

.section-title {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 16px 0;
  letter-spacing: -0.01em;
}

.address-content {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.name-phone {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}

.name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

.phone {
  font-size: 16px;
  font-weight: 400;
  color: #6e6e73;
}

.address-detail {
  font-size: 16px;
  font-weight: 400;
  color: #6e6e73;
  line-height: 1.5;
}

/* 商品信息卡片 */
.order-items-card {
  background: #f5f5f7;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 24px;
}

.items-list {
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
}

.item {
  display: flex;
  padding: 20px;
  border-bottom: 1px solid #f5f5f7;
  transition: background 0.2s ease;
}

.item:hover {
  background: #fafafa;
}

.item:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  margin-right: 20px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f7;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;
}

.item-name {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #1d1d1f;
  line-height: 1.4;
}

.item-spec {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 14px;
  color: #6e6e73;
  background: #f5f5f7;
  padding: 4px 12px;
  border-radius: 8px;
  align-self: flex-start;
}

.item-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price .price {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.item-price .quantity {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 14px;
  color: #6e6e73;
  background: #f5f5f7;
  padding: 4px 12px;
  border-radius: 12px;
}

/* 订单摘要卡片 */
.order-summary-card {
  background: #f5f5f7;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 24px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.summary-label {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 16px;
  font-weight: 400;
  color: #6e6e73;
}

.summary-value {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 16px;
  font-weight: 400;
  color: #1d1d1f;
}

.summary-item.total {
  border-top: 1px solid #d2d2d6;
  padding-top: 16px;
  margin-top: 4px;
}

.summary-item.total .summary-label {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.summary-item.total .summary-value {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
}

.summary-item.total .price {
  color: #1d1d1f;
}

/* 操作按钮 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

.btn {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  padding: 12px 28px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
  letter-spacing: -0.01em;
}

.btn-primary {
  background: #1d1d1f;
  color: #ffffff;
}

.btn-primary:hover {
  background: #333336;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-secondary {
  background: #f5f5f7;
  color: #1d1d1f;
}

.btn-secondary:hover {
  background: #e8e8ed;
}

/* 无订单数据页面 */
.no-order {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 480px;
  background: #ffffff;
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.no-order-content {
  max-width: 400px;
  width: 100%;
  text-align: center;
}

.no-order-icon {
  margin-bottom: 24px;
  color: #d2d2d6;
}

.no-order-icon svg {
  width: 64px;
  height: 64px;
}

.no-order-title {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 12px 0;
  letter-spacing: -0.01em;
}

.no-order-desc {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 16px;
  font-weight: 400;
  color: #6e6e73;
  margin: 0 0 32px 0;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 767px) {
  .order-detail {
    padding: 24px;
    border-radius: 20px;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .order-title {
    font-size: 24px;
  }

  .section-title {
    font-size: 20px;
  }

  .order-info-card,
  .shipping-address-card,
  .order-items-card,
  .order-summary-card {
    padding: 20px;
    border-radius: 20px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 16px;
  }

  .item-image {
    width: 100%;
    height: 200px;
    margin-right: 0;
    border-radius: 16px;
  }

  .item-info {
    width: 100%;
  }

  .name-phone {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .order-actions {
    flex-direction: column;
    gap: 12px;
  }

  .btn {
    width: 100%;
    text-align: center;
    padding: 14px 28px;
  }

  .no-order {
    padding: 32px;
    min-height: 400px;
  }

  .no-order-icon svg {
    width: 56px;
    height: 56px;
  }

  .no-order-title {
    font-size: 20px;
  }

  .no-order-desc {
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .order-detail {
    padding: 20px;
    border-radius: 16px;
  }

  .order-title {
    font-size: 20px;
  }

  .section-title {
    font-size: 18px;
  }

  .order-info-card,
  .shipping-address-card,
  .order-items-card,
  .order-summary-card {
    padding: 16px;
    border-radius: 16px;
  }

  .info-item .label,
  .info-item .value {
    font-size: 15px;
  }

  .item-name {
    font-size: 15px;
  }

  .item-price .price {
    font-size: 16px;
  }

  .summary-item.total .summary-value {
    font-size: 20px;
  }

  .no-order {
    padding: 24px;
    min-height: 320px;
  }

  .no-order-icon svg {
    width: 48px;
    height: 48px;
  }

  .no-order-title {
    font-size: 18px;
  }

  .no-order-desc {
    font-size: 14px;
  }
}
</style>