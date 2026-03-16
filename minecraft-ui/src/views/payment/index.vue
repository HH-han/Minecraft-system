<template>
  <div class="payment-container">
    <div class="payment-header">
      <h1>支付中心</h1>
      <p>一站式支付解决方案</p>
    </div>

    <div class="payment-wrapper">
      <div class="payment-content">
        <!-- 商品类型选择 -->
        <div class="section">
          <div class="section-header">
            <h2>商品类型选择</h2>
            <span class="section-icon">
              <svg t="1773668378284" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="10302" width="256" height="256">
                <path
                  d="M716.8 0H307.2C137.728 0 0 137.728 0 307.2v409.6c0 169.472 137.728 307.2 307.2 307.2h409.6c169.472 0 307.2-137.728 307.2-307.2V307.2c0-169.472-137.728-307.2-307.2-307.2zM372.736 587.776c14.848 0 23.552 16.384 14.848 28.672l-98.304 140.8c-7.168 10.24-22.528 10.24-29.696 0l-98.304-140.8c-8.704-12.288 0-28.672 14.848-28.672h47.616V436.224h-47.616c-14.848 0-23.552-16.384-14.848-28.672l98.304-140.8c7.168-10.24 22.528-10.24 29.696 0l98.304 140.8c8.704 12.288 0 28.672-14.848 28.672h-47.616v151.552h47.616z m486.4 152.064c-9.216 15.872-25.6 25.088-44.032 25.088H532.48a50.3808 50.3808 0 0 1-46.08-52.736c1.024-27.136 23.552-48.64 50.688-48.64h278.528c17.92 0 34.816 9.728 44.032 25.088 9.216 15.872 9.216 34.816 0 50.688z m0-202.752c-9.216 15.872-25.6 25.088-44.032 25.088H532.48a50.3808 50.3808 0 0 1-46.08-52.736c1.024-27.136 23.552-48.64 50.688-48.64h278.528c17.92 0 34.816 9.728 44.032 25.088 9.216 15.872 9.216 34.816 0 50.688z m-44.032-177.152H532.48A50.3808 50.3808 0 0 1 486.4 307.2c1.024-27.136 23.552-48.64 50.688-48.64h278.528c28.16 0 50.688 22.528 50.688 50.688s-22.528 50.688-50.688 50.688z"
                  fill="#1977FD" p-id="10303"></path>
              </svg>
            </span>
          </div>
          <div class="section-body">
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
        </div>

        <!-- 购物车 -->
        <div class="section">
          <div class="section-header">
            <h2>购物车</h2>
            <span class="section-icon">
              <svg t="1773668799267" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="20586" width="256" height="256">
                <path d="M509.6448 511.3856m-450.816 0a450.816 450.816 0 1 0 901.632 0 450.816 450.816 0 1 0-901.632 0Z"
                  fill="#FF9552" p-id="20587"></path>
                <path
                  d="M277.6576 319.3344c-15.104 0-29.5424-0.7168-43.8272 0.1536-24.9344 1.5872-46.6944-28.5696-34.56-56.6272 4.7616-10.9568 14.4896-16.1792 26.112-16.384 23.808-0.4096 47.6672-0.5632 71.4752 0.1024 19.9168 0.5632 31.0272 9.3696 36.0448 28.8256 2.7648 10.8032 3.1232 22.272 4.5568 33.4336 1.2288 9.984 2.4576 19.968 3.6864 30.3616h442.7776c11.3152 0 22.1184 1.8432 31.488 8.7552 10.9056 8.0384 13.824 20.992 8.2944 37.1712-7.5776 22.0672-15.5136 44.032-23.04 66.1504-8.6528 25.4464-16.384 51.2-25.5488 76.4416-8.8064 24.2176-25.1904 37.5296-52.0192 37.376-107.8272-0.512-215.7056-0.2048-323.5328-0.2048h-9.6256c2.9696 18.0736 6.0416 35.4304 8.6528 52.8896 0.768 5.2224 4.096 4.7616 7.68 4.7616h118.2208c63.488 0 126.976-0.0512 190.4128 0.0512 6.3488 0 12.7488 0.4096 18.944 1.6384 9.3184 1.792 14.8992 8.0384 16.384 17.3056 2.9696 18.5344-5.12 37.3248-29.7984 37.2224-109.7216-0.4096-219.4432-0.5632-329.1648 0.1024-28.5184 0.2048-43.8272-16.7424-50.2272-41.9328-10.1888-40.1408-17.2032-81.0496-25.2416-121.7536-12.288-62.464-24.2688-124.9792-36.4032-187.4432-0.4608-2.56-1.024-5.0688-1.7408-8.3968zM674.816 815.3088c-26.9824 1.2288-61.3888-28.5184-60.0576-58.2656 1.536-33.5872 23.7056-59.2384 58.4704-59.8016 34.5088-0.512 61.2352 24.8832 61.9008 57.9584 0.5632 32-28.0064 60.0576-60.3136 60.1088zM407.6544 812.6976c-32.4608 0.0512-58.9312-25.7536-59.1872-57.7024-0.256-30.9248 26.4704-58.3168 57.1392-58.5728 33.6896-0.256 61.5936 25.9584 61.5424 57.9072-0.0512 32.256-26.624 58.3168-59.4944 58.368z"
                  fill="#FFFFFF" p-id="20588"></path>
              </svg>
            </span>
          </div>
          <div class="section-body">
            <ShoppingCart @checkout="handleCheckout" />
          </div>
        </div>

        <!-- 订单详情 -->
        <div class="section">
          <div class="section-header">
            <h2>订单详情</h2>
            <span class="section-icon">
              <svg t="1773668737283" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="16499" width="256" height="256">
                <path
                  d="M758.723 194.016c27.266 0 49.345-21.366 49.345-47.699V50.92c0-26.35-22.08-47.714-49.345-47.714-27.267 0-49.347 21.365-49.347 47.714v95.398c0.002 26.333 22.082 47.699 49.347 47.699z m-493.448 0c27.267 0 49.346-21.366 49.346-47.699V50.92c0-26.35-22.079-47.714-49.346-47.714-27.266 0-49.344 21.365-49.344 47.714v95.398c0.001 26.333 22.08 47.699 49.344 47.699z"
                  fill="#05C697" p-id="16500"></path>
                <path
                  d="M939.642 98.617h-82.23v47.698c0 52.668-44.188 95.4-98.688 95.4s-98.687-42.732-98.687-95.4V98.617H607.4c0-1.117-40.9 0-95.4 0s-95.398 0.496-95.398 0h-52.639v47.698c0 52.668-44.156 95.4-98.688 95.4-54.499 0-98.688-42.732-98.688-95.4V98.617h-82.23c-27.266 0-49.346 21.367-49.346 47.698v826.78c0 26.333 22.08 47.699 49.346 47.699h855.287c27.266 0 49.345-21.366 49.345-47.7V146.316c-0.002-26.331-22.082-47.698-49.347-47.698zM725.817 861.795H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.195H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.196H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.683 15.961-35.644 35.644-35.644h427.635c19.685 0 35.646 15.96 35.646 35.644 0.001 19.673-15.96 35.634-35.645 35.634z"
                  fill="#05C697" p-id="16501"></path>
              </svg>
            </span>
          </div>
          <div class="section-body">
            <OrderDetail @pay="handlePay" />
          </div>
        </div>

        <!-- 用户评价 -->
        <div class="section">
          <div class="section-header">
            <h2>用户评价</h2>
            <span class="section-icon">
              <svg t="1773668763187" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="17497" width="256" height="256">
                <path
                  d="M869.76 155.946667H154.24c-43.733333 0-79.786667 36.053333-79.786667 79.786666v472.96c0 43.733333 36.053333 79.786667 79.786667 79.786667h255.573333l101.546667 79.786667 109.226667-79.786667h247.893333c43.733333 0 79.786667-34.986667 80.853333-78.613333V235.626667c0.106667-43.626667-35.946667-79.68-79.573333-79.68zM318.4 532.266667c-14.826667 8.533333-33.173333 8.533333-48 0-14.826667-8.533333-24-24.426667-24-41.6s9.173333-32.96 24-41.6c14.826667-8.533333 33.173333-8.533333 48 0 14.826667 8.533333 24 24.426667 24 41.6s-9.173333 32.96-24 41.6z m217.6 0c-14.826667 8.533333-33.173333 8.533333-48 0-14.826667-8.533333-24-24.426667-24-41.6s9.173333-32.96 24-41.6c14.826667-8.533333 33.173333-8.533333 48 0 14.826667 8.533333 24 24.426667 24 41.6s-9.173333 32.96-24 41.6z m217.6 0c-14.826667 8.533333-33.173333 8.533333-48 0-14.826667-8.533333-24-24.426667-24-41.6s9.173333-32.96 24-41.6c14.826667-8.533333 33.173333-8.533333 48 0 14.826667 8.533333 24 24.426667 24 41.6s-9.173333 32.96-24 41.6z"
                  fill="#D4237A" p-id="17498"></path>
              </svg>
            </span>
          </div>
          <div class="section-body">
            <UserReview />
          </div>
        </div>

        <!-- 支付页面 -->


          <div class="section-body" v-if="showPayPage">
            <PayPage />
          </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PayPage from '@/components/Payment/PayPage.vue'
import OrderDetail from './components/OrderDetail.vue'
import ShoppingCart from './components/ShoppingCart.vue'
import UserReview from './components/UserReview.vue'
import ProductTypeSelector from './components/ProductTypeSelector.vue'
import { getFoodDetail } from '@/api/food.js'

const route = useRoute()
const selectedOptions = ref({})
const showPayPage = ref(false)
const loading = ref(false)
const error = ref('')
const productData = ref(null)

const handleOptionChange = (options) => {
  selectedOptions.value = options
}

const handleCheckout = () => {
  showPayPage.value = true
}

const handlePay = () => {
  showPayPage.value = true
}

// 获取商品数据
const fetchProductData = async () => {
  const id = route.query.id
  const commodity = route.query.commodity
  
  if (!id || !commodity) return
  
  loading.value = true
  error.value = ''
  
  try {
    if (commodity === '0') {
      // 美食类型，调用food API
      const response = await getFoodDetail(id)
      productData.value = response.data
    } else {
      // 其他类型，这里可以添加相应的API调用
      console.log('其他商品类型:', commodity)
    }
  } catch (err) {
    error.value = err.message || '获取商品数据失败'
    console.error('获取商品数据失败:', err)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取商品数据
onMounted(() => {
  fetchProductData()
})
</script>

<style scoped>
.payment-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  display: flex;
  flex-direction: column;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.payment-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  margin: 20px auto;
  max-width: 800px;
  width: 100%;
}

.payment-header h1 {
  color: #2c3e50;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #ff4d4f 0%, #3498db 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.payment-header p {
  color: #7f8c8d;
  font-size: 16px;
  margin-bottom: 0;
  font-weight: 500;
}

.payment-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  width: 100%;
}

.payment-content {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
}

.section {
  margin-bottom: 40px;
  padding: 25px;
  background: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.section:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #ff4d4f;
}

.section-header h2 {
  font-size: 20px;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.section-icon {
  font-size: 24px;
  line-height: 1;
}

.section-icon svg {
  width: 24px;
  height: 24px;
}

.section-body {
  padding-top: 10px;
}

.type-selector-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
  font-weight: 600;
}

.options-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-item {
  padding: 10px 20px;
  background: #f0f0f0;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
  transition: all 0.3s ease;
}

.option-item:hover {
  background: #e0e0e0;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .payment-container {
    padding: 10px;
  }

  .payment-header {
    padding: 20px;
    margin: 10px auto;
  }

  .payment-header h1 {
    font-size: 24px;
  }

  .payment-content {
    padding: 20px;
  }

  .section {
    padding: 20px;
    margin-bottom: 30px;
  }

  .section-header h2 {
    font-size: 18px;
  }

  .section-icon {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .payment-header h1 {
    font-size: 20px;
  }

  .payment-header p {
    font-size: 14px;
  }

  .payment-content {
    padding: 15px;
  }

  .section {
    padding: 15px;
    margin-bottom: 20px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .section-header h2 {
    font-size: 16px;
  }
}
</style>