<template>
  <div class="order-detail" v-if="hasOrderData">
    <div class="order-header">
      <h2>订单详情</h2>
      <div class="order-status" :class="getStatusClass(order.status)">
        {{ getStatusText(order.status) }}
      </div>
    </div>

    <div class="order-info">
      <div class="info-item">
        <span class="label">订单编号：</span>
        <span class="value">{{ order.orderId }}</span>
      </div>
      <div class="info-item">
        <span class="label">下单时间：</span>
        <span class="value">{{ order.createTime }}</span>
      </div>
      <div class="info-item">
        <span class="label">支付时间：</span>
        <span class="value">{{ order.payTime || '未支付' }}</span>
      </div>
      <div class="info-item">
        <span class="label">订单金额：</span>
        <span class="value price">{{ order.totalPrice }} 元</span>
      </div>
    </div>
    
    <div class="shipping-address">
      <h3>收货地址</h3>
      <div class="address-content">
        <div class="address-info">
          <div class="name-phone">
            <span class="name">{{ order.address.name }}</span>
            <span class="phone">{{ order.address.phone }}</span>
          </div>
          <div class="address-detail">
            {{ order.address.province }}{{ order.address.city }}{{ order.address.district }}{{ order.address.detail }}
          </div>
        </div>
      </div>
    </div>
    
    <div class="order-items">
      <h3>商品信息</h3>
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
              <span class="price">{{ item.price }} 元</span>
              <span class="quantity">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="order-summary">
      <div class="summary-item">
        <span>商品总价：</span>
        <span>{{ order.totalPrice }} 元</span>
      </div>
      <div class="summary-item">
        <span>运费：</span>
        <span>{{ order.shippingFee }} 元</span>
      </div>
      <div class="summary-item total">
        <span>实付金额：</span>
        <span class="price">{{ order.payPrice }} 元</span>
      </div>
    </div>
    
    <div class="order-actions" v-if="order.status === '0'">
      <button class="btn btn-primary" @click="goToPay">立即支付</button>
      <button class="btn btn-secondary" @click="handleCancelOrder">取消订单</button>
    </div>
  </div>
  
  <!-- 无订单数据页面 -->
  <div class="no-order" v-else>
    <div class="no-order-content">
      <div class="no-order-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="15" y1="9" x2="9" y2="15"></line>
          <line x1="9" y1="9" x2="15" y2="15"></line>
        </svg>
      </div>
      <h3>暂无订单数据</h3>
      <p>您还没有相关订单信息，请先去浏览商品</p>
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
.order-detail {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
  transition: all 0.3s ease;
}

.order-detail:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.order-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.order-status {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 14px;
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

.order-status.refunded {
  background: rgba(226, 227, 229, 0.2);
  color: #95a5a6;
  border: 1px solid rgba(149, 165, 166, 0.3);
}

.order-info {
  margin-bottom: 25px;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  align-items: center;
}

.info-item .label {
  width: 100px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.info-item .value {
  color: rgba(255, 255, 255, 0.9);
  flex: 1;
}

.info-item .price {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 16px;
}

.shipping-address {
  margin-bottom: 25px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.shipping-address h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.address-info {
  font-size: 14px;
}

.name-phone {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.name {
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

.phone {
  color: rgba(255, 255, 255, 0.7);
}

.address-detail {
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
}

.order-items {
  margin-bottom: 25px;
}

.order-items h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 15px 0;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.items-list {
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.05);
}

.item {
  display: flex;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.item:hover {
  background: rgba(255, 255, 255, 0.05);
}

.item:last-child {
  border-bottom: none;
}

.item-image {
  width: 100px;
  height: 100px;
  margin-right: 20px;
  flex-shrink: 0;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.item-image:hover img {
  transform: scale(1.05);
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 10px;
}

.item-name {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.4;
  font-weight: 500;
}

.item-spec {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.05);
  padding: 4px 12px;
  border-radius: 8px;
  align-self: flex-start;
}

.item-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.item-price .price {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 16px;
}

.item-price .quantity {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  background: rgba(255, 255, 255, 0.05);
  padding: 4px 12px;
  border-radius: 12px;
}

.order-summary {
  margin-bottom: 25px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  font-size: 14px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  align-items: center;
}

.summary-item span:first-child {
  color: rgba(255, 255, 255, 0.7);
}

.summary-item span:last-child {
  color: rgba(255, 255, 255, 0.9);
}

.summary-item.total {
  font-weight: 600;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  margin-top: 15px;
  margin-bottom: 0;
}

.summary-item.total span:first-child {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
}

.summary-item.total .price {
  color: #ff4d4f;
  font-size: 20px;
  font-weight: 700;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn {
  padding: 10px 24px;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  font-weight: 500;
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.2);
}

.btn-primary {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: #fff;
  border-color: #ff4d4f;
  box-shadow: 0 4px 16px rgba(255, 77, 79, 0.3);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%);
  border-color: #ff7875;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 77, 79, 0.4);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

.btn-secondary:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

@media (max-width: 768px) {
  .order-detail {
    padding: 20px;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .order-header h2 {
    font-size: 18px;
  }
  
  .item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    padding: 20px;
  }
  
  .item-image {
    width: 80px;
    height: 80px;
  }
  
  .item-info {
    width: 100%;
  }
  
  .name-phone {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .order-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .btn {
    width: 100%;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .order-detail {
    padding: 15px;
  }
  
  .info-item .label {
    width: 80px;
  }
  
  .shipping-address,
  .order-summary {
    padding: 15px;
  }
  
  .item {
    padding: 15px;
  }
}

/* 无订单数据页面样式 */
.no-order {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
  padding: 40px;
  text-align: center;
}

.no-order-content {
  max-width: 400px;
  width: 100%;
}

.no-order-icon {
  margin-bottom: 24px;
  color: rgba(255, 255, 255, 0.5);
  animation: pulse 2s infinite;
}

.no-order-icon svg {
  width: 80px;
  height: 80px;
}

.no-order h3 {
  font-size: 20px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 12px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.no-order p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 32px 0;
  line-height: 1.5;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 0.7;
  }
}

@media (max-width: 768px) {
  .no-order {
    padding: 30px;
    min-height: 400px;
  }
  
  .no-order-icon svg {
    width: 60px;
    height: 60px;
  }
  
  .no-order h3 {
    font-size: 18px;
  }
  
  .no-order p {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .no-order {
    padding: 20px;
    min-height: 350px;
  }
  
  .no-order-icon svg {
    width: 50px;
    height: 50px;
  }
  
  .no-order h3 {
    font-size: 16px;
  }
}
</style>