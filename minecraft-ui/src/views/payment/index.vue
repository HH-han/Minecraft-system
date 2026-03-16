<template>
  <div class="payment-container">
    <div class="payment-header">
      <h1>支付相关页面</h1>
      <p>所有功能模块</p>
    </div>
    <div class="payment-content">
      <!-- 商品类型选择 -->
      <div class="section">
        <h2>商品类型选择</h2>
        <ProductTypeSelector @optionChange="handleOptionChange" />
        <div class="selected-options" v-if="Object.keys(selectedOptions).length > 0">
          <h3>已选择的选项：</h3>
          <div class="options-list">
            <div v-for="(value, key) in selectedOptions" :key="key" class="option-item">
              {{ key }}: {{ value }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- 购物车 -->
      <div class="section">
        <h2>购物车</h2>
        <ShoppingCart @checkout="handleCheckout" />
      </div>
      
      <!-- 订单详情 -->
      <div class="section">
        <h2>订单详情</h2>
        <OrderDetail @pay="handlePay" />
      </div>
      
      <!-- 用户评价 -->
      <div class="section">
        <h2>用户评价</h2>
        <UserReview />
      </div>
      
      <!-- 支付页面 -->
      <div class="section" v-if="showPayPage">
        <h2>支付页面</h2>
        <PayPage />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import PayPage from './components/PayPage.vue'
import OrderDetail from './components/OrderDetail.vue'
import ShoppingCart from './components/ShoppingCart.vue'
import UserReview from './components/UserReview.vue'
import ProductTypeSelector from './components/ProductTypeSelector.vue'

const selectedOptions = ref({})
const showPayPage = ref(false)

const handleOptionChange = (options) => {
  selectedOptions.value = options
}

const handleCheckout = () => {
  showPayPage.value = true
}

const handlePay = () => {
  showPayPage.value = true
}
</script>

<style scoped>
.payment-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.payment-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px 0;
}

.payment-header h1 {
  color: #2c3e50;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 10px;
}

.payment-header p {
  color: #7f8c8d;
  font-size: 16px;
  margin-bottom: 20px;
}



.payment-content {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section {
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.section h2 {
  font-size: 20px;
  color: #2c3e50;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #ff4d4f;
}

.type-selector-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.type-selector-container h2 {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px 0;
}

.selected-options {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.selected-options h3 {
  font-size: 16px;
  color: #333;
  margin: 0 0 15px 0;
}

.options-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-item {
  padding: 8px 16px;
  background: #f0f0f0;
  border-radius: 16px;
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .payment-container {
    padding: 10px;
  }
  
  .payment-header h1 {
    font-size: 24px;
  }
  
  .nav-tabs {
    gap: 5px;
  }
  
  .tab-btn {
    padding: 6px 12px;
    font-size: 13px;
  }
  
  .payment-content {
    padding: 15px;
  }
}
</style>