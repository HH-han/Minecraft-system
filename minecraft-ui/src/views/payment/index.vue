<template>
  <div class="payment-container">
    <div class="payment-wrapper">
      <!-- 侧边栏导航 -->
      <aside class="payment-sidebar">
        <div class="sidebar-header">
          <div class="sidebar-badge">PAY</div>
          <h1 class="sidebar-title">支付中心</h1>
          <p class="sidebar-subtitle">一站式支付解决方案</p>
        </div>
        <nav class="sidebar-nav">
          <button
            v-for="item in navItems"
            :key="item.id"
            class="nav-item"
            :class="{ active: activeTab === item.id, 'nav-home': item.id === 'home' }"
            @click="switchTab(item.id)"
          >
            <span class="nav-indicator"></span>
            <span class="nav-icon" v-html="item.icon"></span>
            <span class="nav-text">{{ item.title }}</span>
          </button>
        </nav>
      </aside>

      <!-- 主内容区 -->
      <main class="payment-content">
        <!-- 页面标题栏 -->
        <header class="page-header">
          <div class="page-title-group">
            <h2 class="page-title">{{ getActiveTabTitle() }}</h2>
            <p class="page-desc">管理您的商品、订单与支付流程</p>
          </div>
        </header>

        <!-- 商品类型选择 -->
        <div v-if="activeTab === 'product'" class="section apple-card">
          <ProductTypeSelector @optionChange="handleOptionChange" />
          <div class="selected-options" v-if="Object.keys(selectedOptions).length > 0">
            <h3>已选择的选项</h3>
            <div class="options-list">
              <div v-for="(value, key) in selectedOptions" :key="key" class="option-item">
                <span class="option-key">{{ key }}</span>
                <span class="option-sep">·</span>
                <span class="option-val">{{ value }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 购物车 -->
        <div v-if="activeTab === 'cart'" class="section apple-card">
          <ShoppingCart @checkout="handleCheckout" />
        </div>

        <!-- 订单详情 -->
        <div v-if="activeTab === 'order'" class="section apple-card">
          <OrderDetail @pay="handlePay" />
        </div>
        <!-- 预定服务 -->
        <div v-if="activeTab === 'predetermined'" class="section apple-card">
          <Predetermined />
        </div>
        <!-- 历史订单 -->
        <div v-if="activeTab === 'history'" class="section apple-card">
          <HistoricalOrders />
        </div>
        <!-- 用户评价 -->
        <div v-if="activeTab === 'review'" class="section apple-card">
          <UserReview />
        </div>
      </main>

      <!-- 支付模态框 -->
      <div v-if="showPayModal" class="modal-overlay" @click="closePayModal">
        <div class="modal-content" @click.stop>
          <button class="modal-close" @click="closePayModal" aria-label="关闭">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
          <PayPage
            :orderId="paymentData.orderId"
            :orderIds="paymentData.orderIds"
            :cartItems="paymentData.cartItems"
            :userId="paymentData.userId"
            :predetermined="paymentData.predetermined"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'
import PayPage from '@/components/Payment/PayPage.vue'
import OrderDetail from './components/OrderDetail.vue'
import ShoppingCart from './components/ShoppingCart.vue'
import UserReview from './components/UserReview.vue'
import ProductTypeSelector from './components/ProductTypeSelector.vue'
import HistoricalOrders from './components/HistoricalOrders.vue'
import Predetermined from './components/Predetermined.vue'
import { getFoodDetail } from '@/api/food.js'

const store = useAuthStore()

const route = useRoute()
const router = useRouter()
const activeTab = ref(store.pageState.payment?.activeTab || 'product')
const selectedOptions = ref({})
const loading = ref(false)
const error = ref('')
const productData = ref(null)
const showPayModal = ref(false)

// 监听activeTab变化，更新store
watch(activeTab, (newValue) => {
  store.updatePageState('payment', { activeTab: newValue })
}, { immediate: true })

// 监听来自PayPage的closeModal消息
onMounted(() => {
  window.addEventListener('message', (event) => {
    if (event.data.action === 'closeModal') {
      closePayModal()
    }
  })
})

// 导航项配置
const navItems = [
  {
    id: 'home',
    title: '返回首页',
    icon: `<svg t="1774174358646" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="16082" width="24" height="24"><path d="M913.6 280L660 112c-88-73.6-216.8-73.6-303.2 0L104.8 277.6l-3.2 2.4C57.6 317.6 32 372 32 428.8v352c0 111.2 70.4 176 188.8 176h575.2c118.4 1.6 188.8-62.4 188.8-176v-352c0-56.8-25.6-111.2-71.2-148.8z m-34.4 483.2c-0.8 61.6-27.2 86.4-96 87.2H233.6c-70.4 0-96-26.4-96-87.2V432c0-30.4 13.6-61.6 38.4-82.4l239.2-152.8 3.2-1.6c50.4-44 129.6-44 180 0L840 351.2c24.8 20.8 38.4 52 38.4 82.4v329.6z" p-id="16083" fill="currentColor"></path><path d="M508 480c-28.8 0-52.8 20.8-52.8 44.8v202.4c0 24 24 44.8 52.8 44.8 28.8 0 52.8-20.8 52.8-44.8V524c0-25.6-24-44-52.8-44z" p-id="16084" fill="currentColor"></path></svg>`
  },
  {
    id: 'product',
    title: '商品类型选择',
    icon: `<svg t="1773668378284" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="24" height="24"><path d="M716.8 0H307.2C137.728 0 0 137.728 0 307.2v409.6c0 169.472 137.728 307.2 307.2 307.2h409.6c169.472 0 307.2-137.728 307.2-307.2V307.2c0-169.472-137.728-307.2-307.2-307.2zM372.736 587.776c14.848 0 23.552 16.384 14.848 28.672l-98.304 140.8c-7.168 10.24-22.528 10.24-29.696 0l-98.304-140.8c-8.704-12.288 0-28.672 14.848-28.672h47.616V436.224h-47.616c-14.848 0-23.552-16.384-14.848-28.672l98.304-140.8c7.168-10.24 22.528-10.24 29.696 0l98.304 140.8c8.704 12.288 0 28.672-14.848 28.672h-47.616v151.552h47.616z m486.4 152.064c-9.216 15.872-25.6 25.088-44.032 25.088H532.48a50.3808 50.3808 0 0 1-46.08-52.736c1.024-27.136 23.552-48.64 50.688-48.64h278.528c17.92 0 34.816 9.728 44.032 25.088 9.216 15.872 9.216 34.816 0 50.688z m0-202.752c-9.216 15.872-25.6 25.088-44.032 25.088H532.48a50.3808 50.3808 0 0 1-46.08-52.736c1.024-27.136 23.552-48.64 50.688-48.64h278.528c17.92 0 34.816 9.728 44.032 25.088 9.216 15.872 9.216 34.816 0 50.688z m-44.032-177.152H532.48A50.3808 50.3808 0 0 1 486.4 307.2c1.024-27.136 23.552-48.64 50.688-48.64h278.528c28.16 0 50.688 22.528 50.688 50.688s-22.528 50.688-50.688 50.688z" fill="currentColor"></path></svg>`
  },
  {
    id: 'cart',
    title: '购物车',
    icon: `<svg t="1774153902264" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="14151" width="24" height="24"><path d="M373.72928 753.55136c-55.76192 0-103.68-45.17888-116.5312-109.87008L164.75136 179.69664c-4.4032-21.91872-18.75968-37.82144-34.12992-37.82144h-44.98432c-20.28032 0-42.11712-12.51328-42.11712-39.9872s21.83168-39.9872 42.11712-39.9872h44.98432c56.63232 0 93.34272 34.1248 106.16832 98.68288l92.4416 463.99488c4.76672 23.65952 30.19264 48.9984 44.4928 48.9984h543.19104c20.28544 0 42.11712 12.51328 42.11712 39.99232 0 27.46368-21.83168 39.97696-42.11712 39.97696l-543.18592 0.00512z m159.4368-609.73568c-20.28032 0-42.10688-12.51328-42.10688-39.9872 0-27.4688 21.82656-39.98208 42.10688-39.98208h23.26528c20.28032 0 42.10688 12.51328 42.10688 39.98208 0 27.47392-21.82656 39.9872-42.10688 39.9872h-23.26528zM821.73952 988.24192c-52.9152 0-95.96416-43.05408-95.96416-95.97952 0-52.9152 43.04896-95.96416 95.96416-95.96416s95.96416 43.04896 95.96416 95.96416c0 52.92032-43.04896 95.97952-95.96416 95.97952z m0-138.03008a41.77408 41.77408 0 0 0-29.71648 12.34432 41.75872 41.75872 0 0 0-12.3392 29.70624c0 23.19872 18.8672 42.07616 42.05568 42.07616s42.0608-18.87232 42.0608-42.06592c0-23.1936-18.87232-42.0608-42.0608-42.0608zM428.7488 988.24192c-52.92032 0-95.9744-43.05408-95.9744-95.97952 0-52.9152 43.05408-95.96416 95.9744-95.96416s95.9744 43.04896 95.9744 95.96416c0 52.92032-43.04896 95.97952-95.9744 95.97952z m0-138.03008c-23.18848 0-42.05568 18.87232-42.05568 42.0608 0 23.1936 18.8672 42.06592 42.05568 42.06592s42.05056-18.87232 42.05056-42.06592c0.00512-23.1936-18.86208-42.0608-42.05056-42.0608z" p-id="14152" fill="currentColor"></path><path d="M471.7056 610.18112c-46.11584 0-85.79072-35.5072-94.34624-84.4288L318.20288 186.86976c-5.376-30.49984 2.08384-61.6704 20.46464-85.5296 18.2528-23.81824 45.12768-37.4784 73.74848-37.4784 25.14432 0 36.85376 11.06944 36.85376 34.83136 0 24.9088-17.23392 30.53568-45.78816 39.85408l-0.57344 0.17408c-16.90624 5.12-27.02848 22.89664-23.4752 40.5504l53.97504 299.99104c3.072 17.0496 17.8176 29.41952 35.1488 29.41952h314.96192c17.3312 0 32.0768-12.36992 35.1488-29.41952l53.97504-299.99104c3.55328-17.65376-6.56896-35.4304-23.4752-40.5504l-0.57344-0.17408c-28.55424-9.3184-45.78816-14.94528-45.78816-39.85408 0-23.76192 11.70944-34.83136 36.85376-34.83136 28.6208 0 55.49568 13.66016 73.74848 37.4784 18.3808 23.8592 25.84064 55.02976 20.46464 85.5296l-59.15648 338.88256c-8.55552 48.9216-48.2304 84.4288-94.34624 84.4288H471.7056z" p-id="14153" fill="currentColor"></path></svg>`
  },
  {
    id: 'predetermined',
    title: '预定服务',
    icon: `<svg t="1791201697028" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6975" width="24" height="24"><path d="M663.505455 158.464V133.213091a75.752727 75.752727 0 1 1 151.505454 0v25.250909a151.505455 151.505455 0 0 1 151.528727 151.505455H57.483636a151.505455 151.505455 0 0 1 151.505455-151.505455V133.213091a75.752727 75.752727 0 1 1 151.505454 0v25.250909H663.505455zM57.483636 410.973091H966.516364v353.559273a202.007273 202.007273 0 0 1-202.007273 202.007272H259.467636a202.007273 202.007273 0 0 1-202.007272-202.007272V410.996364z m431.080728 285.719273l-71.424-71.447273a50.501818 50.501818 0 0 0-71.447273 71.447273l107.147636 107.124363c9.472 9.495273 22.341818 14.824727 35.723637 14.801455 12.916364 0 25.832727-4.933818 35.723636-14.801455l178.548364-178.548363a50.501818 50.501818 0 1 0-71.424-71.447273l-142.848 142.871273z" fill="#707070" p-id="6976"></path></svg>`
  },
  {
    id: 'order',
    title: '订单详情',
    icon: `<svg t="1773668737283" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="24" height="24"><path d="M758.723 194.016c27.266 0 49.345-21.366 49.345-47.699V50.92c0-26.35-22.08-47.714-49.345-47.714-27.267 0-49.347 21.365-49.347 47.714v95.398c0.002 26.333 22.082 47.699 49.347 47.699z m-493.448 0c27.267 0 49.346-21.366 49.346-47.699V50.92c0-26.35-22.079-47.714-49.346-47.714-27.266 0-49.344 21.365-49.344 47.714v95.398c0.001 26.333 22.08 47.699 49.344 47.699z" fill="currentColor"></path><path d="M939.642 98.617h-82.23v47.698c0 52.668-44.188 95.4-98.688 95.4s-98.687-42.732-98.687-95.4V98.617H607.4c0-1.117-40.9 0-95.4 0s-95.398 0.496-95.398 0h-52.639v47.698c0 52.668-44.156 95.4-98.688 95.4-54.499 0-98.688-42.732-98.688-95.4V98.617h-82.23c-27.266 0-49.346 21.367-49.346 47.698v826.78c0 26.333 22.08 47.699 49.346 47.699h855.287c27.266 0 49.345-21.366 49.345-47.7V146.316c-0.002-26.331-22.082-47.698-49.347-47.698zM725.817 861.795H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.195H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.196H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.683 15.961-35.644 35.644-35.644h427.635c19.685 0 35.646 15.96 35.646 35.644 0.001 19.673-15.96 35.634-35.645 35.634z" fill="currentColor"></path></svg>`
  },
  {
    id: 'history',
    title: '历史订单',
    icon: `<svg t="1774153814024" class="icon" viewBox="0 0 1075 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4231" width="24" height="24"><path d="M773.812916 671.855204h88.0362c9.266968 0 18.533937 4.633484 27.800905 13.900452 4.633484 9.266968 4.633484 18.533937 0 27.800905-4.633484 9.266968-13.900452 13.900452-27.800905 13.900453H746.012012c-18.533937 0-27.800905-13.900452-27.800905-27.800905v-115.837104c0-9.266968 4.633484-18.533937 13.900452-23.167421 9.266968-4.633484 18.533937-4.633484 27.800905 0s13.900452 13.900452 13.900452 23.167421v88.036199zM607.007487 991.565611H120.49165c-64.868778 0-120.470588-50.968326-120.470589-115.837104V120.470588C0.021061 55.60181 50.989387 4.633484 120.49165 4.633484h653.321266c64.868778 0 120.470588 50.968326 120.470589 115.837104V417.013575c111.20362 55.60181 176.072398 166.80543 176.072398 287.276018 0 176.072398-143.638009 319.710407-324.343891 319.710407-46.334842-4.633484-92.669683-13.900452-139.004525-32.434389z m-106.570136-78.769231c-50.968326-60.235294-83.402715-134.371041-83.402715-213.140271 0-176.072398 143.638009-319.710407 324.343891-319.710408 23.167421 0 46.334842 4.633484 69.502263 9.266969V115.837104c0-18.533937-13.900452-37.067873-37.067874-37.067873H111.224681c-9.266968 0-18.533937 4.633484-23.167421 9.266968s-9.266968 13.900452-9.266968 23.167421l4.633484 764.524887c0 18.533937 13.900452 37.067873 37.067874 37.067873h379.945701z m240.941176 32.434389c88.036199 0 171.438914-46.334842 217.773756-125.104072s46.334842-171.438914 0-245.574661-129.737557-125.104072-217.773756-125.104072c-139.004525 0-250.208145 111.20362-250.208144 245.57466 0 139.004525 111.20362 250.208145 250.208144 250.208145zM222.428301 231.674208h333.61086c23.167421 0 41.701357 18.533937 41.701357 37.067873s-18.533937 37.067873-41.701357 37.067874H222.428301c-23.167421 0-41.701357-18.533937-41.701357-37.067874s18.533937-37.067873 41.701357-37.067873z m0 236.307692h125.104073c23.167421 0 41.701357 18.533937 41.701357 37.067874s-18.533937 37.067873-41.701357 37.067873H222.428301c-23.167421 0-41.701357-18.533937-41.701357-37.067873s18.533937-37.067874 41.701357-37.067874z" fill="currentColor"></path></svg>`
  },
  {
    id: 'review',
    title: '用户评价',
    icon: `<svg t="1773668763187" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="24" height="24"><path d="M869.76 155.946667H154.24c-43.733333 0-79.786667 36.053333-79.786667 79.786666v472.96c0 43.733333 36.053333 79.786667 79.786667 79.786667h255.573333l101.546667 79.786667 109.226667-79.786667h247.893333c43.733333 0 79.786667-34.986667 80.853333-78.613333V235.626667c0.106667-43.626667-35.946667-79.68-79.573333-79.68zM318.4 532.266667c-14.826667 8.533333-33.173333 8.533333-48 0-14.826667-8.533333-24-24.426667-24-41.6s9.173333-32.96 24-41.6c14.826667-8.533333 33.173333-8.533333 48 0 14.826667 8.533333 24 24.426667 24 41.6s-9.173333 32.96-24 41.6z m217.6 0c-14.826667 8.533333-33.173333 8.533333-48 0-14.826667-8.533333-24-24.426667-24-41.6s9.173333-32.96 24-41.6c14.826667-8.533333 33.173333-8.533333 48 0 14.826667 8.533333 24 24.426667 24 41.6s-9.173333 32.96-24 41.6z m217.6 0c-14.826667 8.533333-33.173333 8.533333-48 0-14.826667-8.533333-24-24.426667-24-41.6s9.173333-32.96 24-41.6c14.826667-8.533333 33.173333-8.533333 48 0 14.826667 8.533333 24 24.426667 24 41.6s-9.173333 32.96-24 41.6z" fill="currentColor"></path></svg>`
  },
]

// 切换标签
const switchTab = (tabId) => {
  if (tabId === 'home') {
    router.push('/')
  } else {
    activeTab.value = tabId
  }
}

// 获取当前标签标题
const getActiveTabTitle = () => {
  const tab = navItems.find(item => item.id === activeTab.value)
  return tab ? tab.title : '支付中心'
}

// 处理选项变化
const handleOptionChange = (options) => {
  selectedOptions.value = options
}

// 支付相关数据
const paymentData = ref({
  orderId: '',
  orderIds: [],
  userId: '',
  cartItems: []
})

// 处理 checkout
const handleCheckout = (data) => {
  paymentData.value = data
  showPayModal.value = true
}

// 处理支付
const handlePay = () => {
  // 从路由中获取订单ID
  const orderId = route.query.orderId || ''
  if (orderId) {
    paymentData.value.orderId = orderId
    paymentData.value.orderIds = [orderId]
  }
  showPayModal.value = true
}

// 关闭支付模态框
const closePayModal = () => {
  showPayModal.value = false
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
/* ==================== Base Container ==================== */
.payment-container {
  min-height: 100vh;
  background:
    radial-gradient(circle at 12% 8%, rgba(41, 151, 255, 0.06), transparent 40%),
    radial-gradient(circle at 88% 92%, rgba(175, 82, 222, 0.05), transparent 40%),
    #f5f5f7;
  padding: 28px;
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  scroll-behavior: smooth;
  color: #1d1d1f;
}

.payment-wrapper {
  display: flex;
  gap: 28px;
  max-width: 1240px;
  margin: 0 auto;
  width: 100%;
  align-items: flex-start;
}

/* ==================== Sidebar ==================== */
.payment-sidebar {
  width: 264px;
  min-width: 264px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
  padding: 28px 20px;
  height: fit-content;
  position: sticky;
  top: 24px;
  z-index: 100;
}

.sidebar-header {
  margin-bottom: 24px;
  padding-bottom: 22px;
  border-bottom: 1px solid #e5e5ea;
}

.sidebar-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: linear-gradient(135deg, #2997ff, #5e5ce6);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.08em;
  margin-bottom: 16px;
  box-shadow: 0 6px 16px rgba(41, 151, 255, 0.35);
}

.sidebar-title {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 6px 0;
  color: #1d1d1f;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.sidebar-subtitle {
  font-size: 13px;
  color: #6e6e73;
  margin: 0;
  font-weight: 400;
  line-height: 1.5;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 16px;
  border-radius: 12px;
  background: transparent;
  border: 1px solid transparent;
  color: #6e6e73;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: left;
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
  font-family: inherit;
}

.nav-indicator {
  position: absolute;
  left: -1px;
  top: 50%;
  transform: translateY(-50%) scaleY(0);
  width: 3px;
  height: 22px;
  border-radius: 0 3px 3px 0;
  background: #2997ff;
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-item:hover {
  background: #f5f5f7;
  color: #1d1d1f;
}

.nav-item.active {
  background: rgba(41, 151, 255, 0.1);
  color: #2997ff;
  font-weight: 600;
}

.nav-item.active .nav-indicator {
  transform: translateY(-50%) scaleY(1);
}

.nav-item.active .nav-icon {
  color: #2997ff;
}

/* 返回首页特殊样式 */
.nav-item.nav-home {
  background: linear-gradient(135deg, rgba(255, 59, 48, 0.06), rgba(255, 149, 0, 0.06));
  border: 1px solid rgba(255, 59, 48, 0.15);
  color: #ff3b30;
  margin-bottom: 12px;
  border-radius: 12px;
}

.nav-item.nav-home:hover {
  background: linear-gradient(135deg, rgba(255, 59, 48, 0.1), rgba(255, 149, 0, 0.1));
  border-color: rgba(255, 59, 48, 0.25);
}

.nav-item.nav-home .nav-icon {
  color: #ff3b30;
}

.nav-item.nav-home.active {
  background: linear-gradient(135deg, rgba(255, 59, 48, 0.12), rgba(255, 149, 0, 0.12));
  color: #ff3b30;
}

.nav-item.nav-home.active .nav-indicator {
  background: #ff3b30;
}

.nav-icon {
  flex-shrink: 0;
  color: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-icon svg {
  width: 19px;
  height: 19px;
}

.nav-text {
  flex: 1;
}

/* ==================== Page Header ==================== */
.page-header {
  margin-bottom: 24px;
  padding: 28px 32px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 6px 0;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.page-desc {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
  font-weight: 400;
}

/* ==================== Main Content ==================== */
.payment-content {
  width: 100%;
  flex: 1;
  min-width: 0;
}

.apple-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
  padding: 32px;
  margin-bottom: 24px;
  transition: box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.section {
  animation: fadeInUp 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ==================== Selected Options ==================== */
.selected-options {
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #e5e5ea;
}

.selected-options h3 {
  font-size: 17px;
  color: #1d1d1f;
  margin: 0 0 14px 0;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.options-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f5f5f7;
  border-radius: 20px;
  font-size: 13px;
  color: #1d1d1f;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #e5e5ea;
}

.option-key {
  color: #6e6e73;
  font-weight: 400;
}

.option-sep {
  color: #8e8e93;
}

.option-val {
  color: #2997ff;
  font-weight: 600;
}

/* ==================== Modal ==================== */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  cursor: pointer;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  position: relative;
  max-width: 600px;
  width: 92%;
  max-height: 88vh;
  overflow-y: auto;
  background: #ffffff;
  border-radius: 22px;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.28);
  cursor: default;
  animation: modalSlideIn 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 10;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.06);
  color: #6e6e73;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-close:hover {
  background: rgba(0, 0, 0, 0.12);
  color: #1d1d1f;
  transform: rotate(90deg);
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(24px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ==================== Responsive ==================== */
@media (max-width: 1199px) {
  .payment-wrapper { gap: 22px; }
  .payment-sidebar { width: 232px; min-width: 232px; padding: 24px 18px; }
  .sidebar-title { font-size: 23px; }
  .page-header { padding: 24px 28px; }
  .page-title { font-size: 25px; }
  .apple-card { padding: 28px; }
  .nav-item { padding: 12px 14px; font-size: 14px; }
}

@media (max-width: 767px) {
  .payment-container { padding: 16px; }
  .payment-wrapper { flex-direction: column; gap: 20px; }

  .payment-sidebar {
    width: 100%;
    min-width: unset;
    position: static;
    padding: 20px;
  }
  .sidebar-header { margin-bottom: 18px; padding-bottom: 18px; }
  .sidebar-badge { width: 38px; height: 38px; font-size: 11px; margin-bottom: 12px; }
  .sidebar-title { font-size: 22px; }
  .sidebar-subtitle { font-size: 13px; }

  .sidebar-nav {
    flex-direction: row;
    overflow-x: auto;
    padding-bottom: 6px;
    gap: 8px;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;
  }
  .sidebar-nav::-webkit-scrollbar { display: none; }

  .nav-item {
    flex-shrink: 0;
    padding: 10px 14px;
    font-size: 13px;
    border-radius: 10px;
  }
  .nav-item.nav-home { margin-bottom: 0; }
  .nav-indicator { display: none; }
  .nav-icon svg { width: 17px; height: 17px; }
  .nav-text { display: none; }

  .page-header { padding: 20px 22px; margin-bottom: 18px; }
  .page-title { font-size: 22px; }
  .page-desc { font-size: 13px; }

  .apple-card { padding: 22px; margin-bottom: 18px; border-radius: 18px; }
  .selected-options h3 { font-size: 16px; }
  .option-item { padding: 7px 14px; font-size: 12px; }

  .modal-content { width: 95%; max-height: 92vh; border-radius: 18px; }
  .modal-close { top: 12px; right: 12px; width: 32px; height: 32px; }
}

@media (max-width: 480px) {
  .payment-container { padding: 12px; }
  .payment-sidebar { padding: 18px 16px; }
  .sidebar-title { font-size: 20px; }
  .page-header { padding: 18px; }
  .page-title { font-size: 20px; }
  .apple-card { padding: 18px; border-radius: 16px; }
  .selected-options { margin-top: 20px; padding-top: 18px; }
  .options-list { gap: 8px; }
}
</style>