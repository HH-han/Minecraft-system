<template>
  <!-- 商品详情模态框（仿 Apple 官网视觉语言） -->
  <div v-if="visible" class="modal-overlay" @click.self="closeModal">
    <div
      class="modal-container"
      role="dialog"
      aria-modal="true"
      aria-labelledby="product-modal-title"
      @click.stop
    >
      <button class="close-btn" aria-label="关闭" @click="closeModal">×</button>
      <div class="modal-body">
        <div class="product-image">
          <img :src="product.coverImage" :alt="product.name">
        </div>
        <div class="product-details">
          <h3 id="product-modal-title" class="product-name">{{ product.name }}</h3>
          <p class="product-id">商品编号 {{ product.id }}</p>
          <div class="product-price">¥{{ product.price }}</div>
          <p class="product-description">{{ product.description }}</p>
          <div class="product-actions">
            <button class="btn btn-secondary" @click="handleAddToCart">加入购物车</button>
            <button class="btn btn-primary" @click="buyNow">立即支付</button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Element Plus 弹窗 -->
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="30%"
    center
  >
    <span>{{ dialogMessage }}</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFoodDetail } from '@/api/food.js'
import { getProductDetail } from '@/api/product.js'
import { createOrder } from '@/api/order.js'
import { addToCart } from '@/api/cart.js'
import { ElDialog, ElButton } from 'element-plus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  productId: {
    type: String,
    default: ''
  },
  commodity: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close'])
const router = useRouter()

const product = ref({
  id: '',
  name: '',
  price: 0,
  coverImage: '',
  description: ''
})

// 对话框相关变量
const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogMessage = ref('')

// 监听商品ID变化，获取商品数据
watch(() => props.productId, (newId) => {
  if (newId && props.commodity) {
    fetchProductData()
  }
}, { immediate: true })

// 获取商品数据
const fetchProductData = async () => {
  const id = props.productId
  const commodity = props.commodity

  if (!id || !commodity) return

  try {
    if (commodity === '0') {
      // 美食类型，调用food API
      const response = await getFoodDetail(id)
      product.value = response.data
    } else if (commodity === '1') {
      // 纪念品类型，调用product API
      const response = await getProductDetail(id)
      product.value = response.data
    } else {
      console.log('其他商品类型:', commodity)
    }
  } catch (err) {
    console.error('获取商品数据失败:', err)
  }
}

// 加入购物车
const handleAddToCart = async () => {
  console.log('加入购物车:', product.value)
  try {
    // 从 localStorage 获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('user'))
    const userId = userInfo?.id || userInfo?.userId

    // 调用加入购物车 API
    const cartData = {
      itemId: product.value.id,
      itemName: product.value.name,
      price: product.value.price,
      quantity: 1,
      image: product.value.coverImage,
      itemType: props.commodity === '0' ? 'food' : 'product',
      userId: userId
    }

    await addToCart(cartData)
    dialogTitle.value = '成功'
    dialogMessage.value = '商品已加入购物车'
    dialogVisible.value = true
  } catch (error) {
    console.error('加入购物车失败:', error)
    dialogTitle.value = '失败'
    dialogMessage.value = '加入购物车失败，请重试'
    dialogVisible.value = true
  } finally {
    closeModal()
  }
}

// 立即支付
const buyNow = async () => {
  console.log('立即支付:', product.value)
  try {
    // 创建订单
    const orderRequest = {
      itemType: props.commodity === '0' ? 'food' : 'product',
      itemId: product.value.id,
      itemName: product.value.name,
      amount: parseFloat(product.value.price),
      quantity: 1,
      image: product.value.coverImage,
      remark: ''
    }

    const response = await createOrder(orderRequest)
    const orderId = response.data.id
    console.log('订单创建成功:', orderId)

    // 跳转到支付页面
    router.push({
      path: '/payment',
      query: {
        id: product.value.id,
        commodity: props.commodity,
        image: product.value.coverImage,
        orderId: orderId,
        item: JSON.stringify(product.value)
      }
    })
  } catch (error) {
    console.error('创建订单失败:', error)
    dialogTitle.value = '失败'
    dialogMessage.value = '创建订单失败，请重试'
    dialogVisible.value = true
  } finally {
    closeModal()
  }
}

// 关闭模态框
const closeModal = () => {
  emit('close')
}

// Esc 键关闭模态框（无障碍）
const handleKeydown = (e) => {
  if (e.key === 'Escape' && props.visible) {
    closeModal()
  }
}

// 组件挂载时获取商品数据
onMounted(() => {
  if (props.productId && props.commodity) {
    fetchProductData()
  }
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
/* ===== 色彩与字体令牌（仿 Apple 官网规范） ===== */
.modal-overlay,
.modal-container,
.modal-body,
.product-details,
.btn {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC',
    'Inter', 'Helvetica Neue', Arial, sans-serif;
}

/* ===== 遮罩层 ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* ===== 模态容器（卡片） ===== */
.modal-container {
  position: relative;
  max-width: 880px;
  width: 90%;
  max-height: 85vh;
  overflow-y: auto;
  background: #ffffff;
  border: 1px solid #d2d2d6;
  border-radius: 28px;
  box-shadow: 0 20px 50px -12px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.4s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(24px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-container::-webkit-scrollbar {
  width: 8px;
}

.modal-container::-webkit-scrollbar-track {
  background: transparent;
}

.modal-container::-webkit-scrollbar-thumb {
  background: #d2d2d6;
  border-radius: 4px;
}

/* ===== 关闭按钮 ===== */
.close-btn {
  position: absolute;
  top: 1rem;
  right: 1.25rem;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f7;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  line-height: 1;
  color: #6e6e73;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 10;
}

.close-btn:hover {
  background: #e8e8ed;
  color: #1d1d1f;
}

.close-btn:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
}

/* ===== 主体两栏布局 ===== */
.modal-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2.5rem;
  padding: 2.5rem;
}

/* ===== 商品图片 ===== */
.product-image {
  border-radius: 20px;
  overflow: hidden;
  background: #f5f5f7;
  min-height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  max-height: 420px;
  object-fit: cover;
  border-radius: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.product-image img:hover {
  transform: scale(1.01);
  box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

/* ===== 商品信息栏 ===== */
.product-details {
  display: flex;
  flex-direction: column;
  padding: 0.5rem 0;
}

.product-name {
  font-size: 1.75rem; /* 28px 卡片标题 */
  font-weight: 600;
  line-height: 1.2;
  color: #1d1d1f;
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.01em;
}

.product-id {
  font-size: 0.875rem; /* 14px 辅助文字 */
  font-weight: 400;
  color: #6e6e73;
  margin: 0 0 1.5rem 0;
}

.product-price {
  font-size: 1.75rem; /* 28px */
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 1.5rem;
  letter-spacing: -0.01em;
}

.product-description {
  font-size: 1.0625rem; /* 17px 正文 */
  font-weight: 400;
  line-height: 1.5;
  color: #6e6e73;
  margin: 0 0 2rem 0;
  min-height: 4rem;
  flex: 1;
}

/* ===== 操作按钮（胶囊形 CTA） ===== */
.product-actions {
  display: flex;
  gap: 1rem;
  margin-top: auto;
}

.btn {
  flex: 1;
  padding: 0.75rem 1.75rem; /* 12px 28px */
  border: none;
  border-radius: 40px;
  font-size: 1rem; /* 16px */
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
}

/* 次要按钮：浅灰底深色字 */
.btn-secondary {
  background: #f5f5f7;
  color: #1d1d1f;
}

.btn-secondary:hover {
  background: #e8e8ed;
  transform: scale(1.01);
  box-shadow: 0 8px 20px -8px rgba(0, 0, 0, 0.15);
}

/* 主按钮：蓝色强调色 */
.btn-primary {
  background: #2997ff;
  color: #ffffff;
}

.btn-primary:hover {
  background: #0066cc;
  transform: scale(1.01);
  box-shadow: 0 8px 20px -8px rgba(41, 151, 255, 0.5);
}

.btn:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
}

/* ===== 响应式 ===== */
@media (max-width: 1199px) {
  .modal-body {
    gap: 2rem;
    padding: 2rem;
  }

  .product-name {
    font-size: 1.5rem;
  }

  .product-price {
    font-size: 1.5rem;
  }
}

@media (max-width: 767px) {
  .modal-container {
    width: 95%;
    max-height: 90vh;
    border-radius: 20px;
  }

  .modal-body {
    grid-template-columns: 1fr;
    gap: 1.5rem;
    padding: 1.5rem;
  }

  .product-image {
    min-height: 220px;
  }

  .product-image img {
    max-height: 280px;
  }

  .product-name {
    font-size: 1.375rem;
  }

  .product-price {
    font-size: 1.375rem;
  }

  .product-description {
    font-size: 1rem;
    min-height: auto;
  }

  .product-actions {
    flex-direction: column;
  }

  .btn {
    padding: 0.625rem 1.5rem;
  }
}
</style>
