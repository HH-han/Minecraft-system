<template>
  <div class="order-detail">
    <div class="order-header">
      <h2>订单详情</h2>
      <div class="order-status" :class="order.status">
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const emit = defineEmits(['pay'])
const router = useRouter()

const order = ref({
  orderId: '20240101001',
  status: 'unpaid',
  createTime: '2024-01-01 12:00:00',
  payTime: '',
  totalPrice: 199.99,
  shippingFee: 0,
  payPrice: 199.99,
  address: {
    name: '张三',
    phone: '13800138000',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detail: '某某街道某某小区1号楼101室'
  },
  items: [
    {
      id: 1,
      name: 'Minecraft  Java版',
      spec: '标准版',
      price: 165,
      quantity: 1,
      image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20Java%20Edition%20game%20cover&size=512x512'
    },
    {
      id: 2,
      name: 'Minecraft 皮肤包',
      spec: '基础包',
      price: 34.99,
      quantity: 1,
      image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20skin%20pack&size=512x512'
    }
  ]
})

const getStatusText = (status) => {
  const statusMap = {
    unpaid: '待支付',
    paid: '已支付',
    shipping: '配送中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return status in statusMap ? statusMap[status] : '未知状态'
}

const goToPay = () => {
  // 触发支付事件
  emit('pay')
  router.push('/payment')
}

const cancelOrder = () => {
  if (confirm('确定要取消订单吗？')) {
    order.value.status = 'cancelled'
  }
}
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