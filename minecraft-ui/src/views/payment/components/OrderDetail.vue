<template>
  <div class="order-detail">
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
    
    <div class="order-actions" v-if="order.status === 'unpaid'">
      <button class="btn btn-primary" @click="goToPay">立即支付</button>
      <button class="btn btn-secondary" @click="cancelOrder">取消订单</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getFoodDetail } from '@/api/food.js'
import { getProductDetail } from '@/api/product.js'
import { getOrderDetail } from '@/api/order.js'

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

// 获取订单数据
const fetchProductData = async () => {
  const orderId = route.query.orderId
  const id = route.query.id
  const commodity = route.query.commodity
  
  if (orderId) {
    // 如果有订单编号，从后端获取订单数据
    try {
      const response = await getOrderDetail(orderId)
      const orderData = response.data
      
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
        order.value.items = [{
          id: food.id,
          name: food.name,
          price: food.price,
          quantity: 1,
          image: food.coverImage
        }]
        order.value.totalPrice = food.price
        order.value.payPrice = food.price
      })
    } else if (commodity === '1') {
      // 纪念品类型，调用product API
      getProductDetail(id).then(response => {
        const product = response.data
        order.value.items = [{
          id: product.id,
          name: product.name,
          price: product.price,
          quantity: 1,
          image: product.coverImage
        }]
        order.value.totalPrice = product.price
        order.value.payPrice = product.price
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

const cancelOrder = () => {
  if (confirm('确定要取消订单吗？')) {
    order.value.status = 'cancelled'
  }
}

// 组件挂载时获取商品数据
onMounted(() => {
  fetchProductData()
})
</script>

<style scoped>
.order-detail {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.order-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
}

.order-status.unpaid {
  background: #fff3cd;
  color: #856404;
}

.order-status.paid {
  background: #d1ecf1;
  color: #0c5460;
}

.order-status.shipping {
  background: #e7f3ff;
  color: #004085;
}

.order-status.completed {
  background: #d4edda;
  color: #155724;
}

.order-status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

.order-status.refunded {
  background: #e2e3e5;
  color: #383d41;
}

.order-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
}

.info-item .label {
  width: 80px;
  color: #666;
}

.info-item .value {
  color: #333;
}

.info-item .price {
  color: #ff4d4f;
  font-weight: 600;
}

.shipping-address {
  margin-bottom: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 6px;
}

.shipping-address h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 10px 0;
  color: #333;
}

.address-info {
  font-size: 14px;
}

.name-phone {
  margin-bottom: 8px;
}

.name {
  font-weight: 600;
  margin-right: 15px;
}

.address-detail {
  color: #666;
  line-height: 1.4;
}

.order-items {
  margin-bottom: 20px;
}

.order-items h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 15px 0;
  color: #333;
}

.items-list {
  border: 1px solid #eee;
  border-radius: 6px;
  overflow: hidden;
}

.item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.item:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  margin-right: 15px;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.item-spec {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.item-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price .price {
  color: #ff4d4f;
  font-weight: 600;
}

.item-price .quantity {
  color: #666;
  font-size: 14px;
}

.order-summary {
  margin-bottom: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 6px;
  font-size: 14px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.summary-item.total {
  font-weight: 600;
  padding-top: 10px;
  border-top: 1px solid #eee;
  margin-top: 10px;
}

.summary-item.total .price {
  color: #ff4d4f;
  font-size: 16px;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid #d9d9d9;
  transition: all 0.3s;
}

.btn-primary {
  background: #ff4d4f;
  color: #fff;
  border-color: #ff4d4f;
}

.btn-primary:hover {
  background: #ff7875;
  border-color: #ff7875;
}

.btn-secondary {
  background: #fff;
  color: #333;
}

.btn-secondary:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

@media (max-width: 768px) {
  .order-detail {
    padding: 15px;
  }
  
  .order-header h2 {
    font-size: 16px;
  }
  
  .item {
    padding: 10px;
  }
  
  .item-image {
    width: 60px;
    height: 60px;
  }
  
  .order-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
    text-align: center;
  }
}
</style>