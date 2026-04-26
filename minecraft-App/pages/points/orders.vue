<template>
  <view class="orders-container">
    <view class="header">
      <text class="title">兑换订单</text>
    </view>
    
    <view class="orders-list">
      <view 
        v-for="order in orders" 
        :key="order.id" 
        class="order-item"
      >
        <view class="order-header">
          <text class="order-no">订单号: {{ order.orderNo }}</text>
          <text :class="['order-status', order.status]" :style="{ backgroundColor: getStatusColor(order.status) }">
            {{ getStatusText(order.status) }}
          </text>
        </view>
        <view class="order-content">
          <text class="product-name">{{ order.productName }}</text>
          <text class="order-info">数量: {{ order.quantity }}</text>
          <text class="order-info">使用积分: {{ order.pointsUsed }}</text>
          <text class="order-info">收货人: {{ order.receiver }}</text>
          <text class="order-info">联系电话: {{ order.phone }}</text>
          <text class="order-info">收货地址: {{ order.address }}</text>
        </view>
        <view class="order-footer">
          <text class="order-time">{{ formatTime(order.createTime) }}</text>
        </view>
      </view>
      <view v-if="orders.length === 0" class="empty">
        <text>暂无兑换订单</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getExchangeOrders } from '@/api/points'

export default {
  data() {
    return {
      orders: []
    }
  },
  onLoad() {
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        const res = await getExchangeOrders({ page: 1, size: 100 })
        this.orders = res.records
      } catch (error) {
        console.error('获取订单列表失败', error)
      }
    },
    getStatusText(status) {
      const statusMap = {
        'PENDING': '处理中',
        'SUCCESS': '成功',
        'FAILED': '失败',
        'CANCELLED': '已取消'
      }
      return statusMap[status] || status
    },
    getStatusColor(status) {
      const colorMap = {
        'PENDING': '#ff9800',
        'SUCCESS': '#4CAF50',
        'FAILED': '#f44336',
        'CANCELLED': '#9e9e9e'
      }
      return colorMap[status] || '#9e9e9e'
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.orders-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background-color: white;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
  position: sticky;
  top: 0;
  z-index: 10;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.orders-list {
  padding: 20rpx;
}

.order-item {
  background-color: white;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 15rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
  padding-bottom: 15rpx;
  border-bottom: 1rpx solid #eee;
}

.order-no {
  font-size: 24rpx;
  color: #666;
}

.order-status {
  font-size: 22rpx;
  color: white;
  padding: 5rpx 15rpx;
  border-radius: 15rpx;
}

.order-content {
  margin-bottom: 15rpx;
}

.product-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.order-info {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 5rpx;
  display: block;
}

.order-footer {
  padding-top: 15rpx;
  border-top: 1rpx solid #eee;
}

.order-time {
  font-size: 20rpx;
  color: #999;
}

.empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 24rpx;
}
</style>
