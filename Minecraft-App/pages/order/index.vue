<template>
  <div class="order-container">
    <div class="order-header">
      <h1>我的订单</h1>
    </div>
    
    <div class="order-tabs">
      <div class="tab-item active">全部</div>
      <div class="tab-item">待支付</div>
      <div class="tab-item">待使用</div>
      <div class="tab-item">已完成</div>
      <div class="tab-item">已取消</div>
    </div>
    
    <div class="order-list">
      <div class="order-item">
        <div class="order-header-info">
          <span class="order-id">订单号：20260329001</span>
          <span class="order-status">待支付</span>
        </div>
        <div class="order-content">
          <div class="order-product">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=travel%20ticket%20icon&image_size=square" alt="订单商品" class="product-image">
            <div class="product-info">
              <h3>景点门票</h3>
              <p>数量：2</p>
            </div>
          </div>
          <div class="order-price">¥198.00</div>
        </div>
        <div class="order-actions">
          <button class="action-btn secondary">取消订单</button>
          <button class="action-btn primary">立即支付</button>
        </div>
      </div>
      
      <div class="order-item">
        <div class="order-header-info">
          <span class="order-id">订单号：20260328002</span>
          <span class="order-status">已完成</span>
        </div>
        <div class="order-content">
          <div class="order-product">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20booking%20icon&image_size=square" alt="订单商品" class="product-image">
            <div class="product-info">
              <h3>酒店预订</h3>
              <p>数量：1</p>
            </div>
          </div>
          <div class="order-price">¥398.00</div>
        </div>
        <div class="order-actions">
          <button class="action-btn secondary">查看详情</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 导航到指定页面
const navigateTo = (url) => {
  uni.navigateTo({
    url
  })
}
</script>

<style scoped>
.order-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.order-header {
  margin-bottom: 20px;
}

.order-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.order-tabs {
  display: flex;
  background-color: var(--card-background);
  border-radius: 12px;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-item {
  flex: 1;
  padding: 16px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-item.active {
  background-color: var(--primary-color);
  color: white;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  background-color: var(--card-background);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.order-id {
  font-size: 14px;
  color: var(--text-secondary-color);
}

.order-status {
  font-size: 14px;
  font-weight: 500;
  color: #F44336;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.order-product {
  display: flex;
  align-items: center;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  margin-right: 12px;
}

.product-info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
}

.product-info p {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary-color);
}

.order-price {
  font-size: 16px;
  font-weight: 600;
  color: #F44336;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
}

.action-btn.primary {
  background-color: var(--primary-color);
  color: white;
  border: none;
}

.action-btn.secondary {
  background-color: transparent;
  color: var(--text-color);
}

.action-btn:hover {
  opacity: 0.8;
}
</style>