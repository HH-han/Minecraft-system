<template>
  <!-- 详情模态框（仿 Apple 官网视觉语言，支持美食/纪念品/酒店/景点） -->
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

          <!-- 商品类（美食/纪念品）：编号 + 大价格 + 描述 -->
          <template v-if="isShopItem">
            <p class="product-id">商品编号 {{ product.id }}</p>
            <div class="product-price">¥{{ product.price }}</div>
            <p class="product-description">{{ product.description }}</p>
          </template>

          <!-- 预订类（酒店/景点） -->
          <template v-else>
            <div v-if="product.rating" class="modal-rating">
              <span class="rating">{{ product.rating }}</span>
              <span class="rating-text">{{ ratingText }}</span>
              <span v-if="product.commentCount" class="review-count">({{ product.commentCount }}条点评)</span>
            </div>
            <div v-if="product.address" class="modal-location">
              <i class="location-icon" aria-hidden="true">📍</i>
              <span>{{ product.address }}</span>
            </div>
            <div v-if="product.description" class="info-section">
              <h3 class="section-title">{{ isHotel ? '酒店介绍' : '景点介绍' }}</h3>
              <p class="section-text">{{ product.description }}</p>
            </div>
            <!-- 酒店设施 -->
            <div v-if="isHotel && product.facilities?.length" class="info-section">
              <h3 class="section-title">酒店设施</h3>
              <div class="tag-list">
                <span v-for="(facility, index) in product.facilities" :key="index" class="tag">{{ facility }}</span>
              </div>
            </div>
            <!-- 景点标签 -->
            <div v-if="isAttraction && product.tags?.length" class="info-section">
              <h3 class="section-title">景点标签</h3>
              <div class="tag-list">
                <span v-for="(tag, index) in product.tags" :key="index" class="tag">{{ tag }}</span>
              </div>
            </div>
            <!-- 酒店房型 -->
            <div v-if="isHotel && product.rooms?.length" class="info-section">
              <h3 class="section-title">房型列表</h3>
              <div class="sub-list">
                <div v-for="(room, index) in product.rooms" :key="room.id || index" class="sub-item">
                  <div class="sub-info">
                    <h4>{{ room.name }}</h4>
                    <p class="sub-desc">{{ room.description }}</p>
                    <div v-if="room.facilities?.length" class="tag-list">
                      <span v-for="(facility, fIndex) in room.facilities" :key="fIndex" class="tag tag-blue">{{ facility }}</span>
                    </div>
                  </div>
                  <div class="sub-price">¥{{ room.price }}</div>
                </div>
              </div>
            </div>
            <!-- 景点门票 -->
            <div v-if="isAttraction && product.tickets?.length" class="info-section">
              <h3 class="section-title">门票列表</h3>
              <div class="sub-list">
                <div v-for="(ticket, index) in product.tickets" :key="ticket.id || index" class="sub-item">
                  <div class="sub-info">
                    <h4>{{ ticket.name }}</h4>
                    <p class="sub-desc">{{ ticket.description }}</p>
                    <div v-if="ticket.rules?.length" class="tag-list">
                      <span v-for="(rule, rIndex) in ticket.rules" :key="rIndex" class="tag tag-blue">{{ rule }}</span>
                    </div>
                  </div>
                  <div class="sub-price">¥{{ ticket.price }}</div>
                </div>
              </div>
            </div>
            <!-- 统计 -->
            <div class="modal-stats">
              <div v-if="product.collectCount != null" class="stat-item">
                <span class="stat-label">收藏数</span>
                <span class="stat-value">{{ product.collectCount }}</span>
              </div>
              <div v-if="product.likeCount != null" class="stat-item">
                <span class="stat-label">点赞数</span>
                <span class="stat-value">{{ product.likeCount }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">价格</span>
                <span class="stat-value">¥{{ product.price }}</span>
              </div>
              <div v-if="isAttraction" class="stat-item">
                <span class="stat-label">最佳季节</span>
                <span class="stat-value">{{ product.season || '四季皆宜' }}</span>
              </div>
            </div>
          </template>

          <!-- 操作按钮 -->
          <div class="product-actions">
            <template v-if="isShopItem">
              <button class="btn btn-secondary" @click="handleAddToCart">加入购物车</button>
              <button class="btn btn-primary" @click="buyNow">立即支付</button>
            </template>
            <template v-else>
              <button class="btn btn-secondary" @click="closeModal">关闭</button>
              <button class="btn btn-primary" @click="handleBook">前往预订</button>
            </template>
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
import { ref, watch, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getFoodDetail } from '@/api/food.js'
import { getProductDetail } from '@/api/product.js'
import { getHotelDetail } from '@/api/hotel.js'
import { getAttractionDetail } from '@/api/attraction.js'
import { createOrder } from '@/api/order.js'
import { addToCart } from '@/api/cart.js'
import { useBookingStore } from '@/stores/bookingStore.js'
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
const bookingStore = useBookingStore()

// 类型判定：0 美食 / 1 纪念品 / 2 酒店 / 3 景点
const isShopItem = computed(() => props.commodity === '0' || props.commodity === '1')
const isHotel = computed(() => props.commodity === '2')
const isAttraction = computed(() => props.commodity === '3')

// 评分文案
const ratingText = computed(() => {
  const r = product.value.rating
  if (isAttraction.value) return r >= 4.5 ? '极好' : r >= 4 ? '很好' : '好'
  return r >= 4.5 ? '超棒' : r >= 4 ? '很好' : '好'
})

const product = ref({
  id: '',
  name: '',
  price: 0,
  coverImage: '',
  description: '',
  rating: 0,
  commentCount: 0,
  address: '',
  collectCount: 0,
  likeCount: 0,
  season: '',
  facilities: [],
  rooms: [],
  tags: [],
  tickets: []
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
      // 美食类型
      const response = await getFoodDetail(id)
      product.value = response.data
    } else if (commodity === '1') {
      // 纪念品类型
      const response = await getProductDetail(id)
      product.value = response.data
    } else if (commodity === '2') {
      // 酒店类型
      const response = await getHotelDetail(id)
      product.value = response.data
    } else if (commodity === '3') {
      // 景点类型
      const response = await getAttractionDetail(id)
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

// 前往预订（酒店/景点）
const handleBook = () => {
  if (isHotel.value) {
    bookingStore.bookHotel(product.value)
  } else if (isAttraction.value) {
    bookingStore.bookAttraction(product.value)
  }
  router.push('/predetermined')
  closeModal()
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
  /* 隐藏滚动条（Firefox / IE & 旧 Edge） */
  scrollbar-width: none;
  -ms-overflow-style: none;
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

/* 隐藏滚动条（Chrome / Safari / 新 Edge） */
.modal-container::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
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

/* ===== 主体上下布局 ===== */
.modal-body {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 2rem;
}

/* ===== 商品图片（顶部全宽横图） ===== */
.product-image {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  border-radius: 20px;
  overflow: hidden;
  background: #f5f5f7;
  flex-shrink: 0;
}

.product-image img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-image img:hover {
  transform: scale(1.02);
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

/* ===== 评分（酒店/景点） ===== */
.modal-rating {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  margin-bottom: 0.25rem;
}

.modal-rating .rating {
  background: #2997ff;
  color: #ffffff;
  padding: 0.25rem 0.75rem;
  border-radius: 980px;
  font-weight: 600;
  font-size: 0.9375rem;
}

.modal-rating .rating-text {
  color: #2997ff;
  font-weight: 600;
  font-size: 0.9375rem;
}

.modal-rating .review-count {
  color: #6e6e73;
  font-size: 0.875rem;
}

/* ===== 地址 ===== */
.modal-location {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6e6e73;
  font-size: 0.9375rem;
}

.modal-location .location-icon {
  font-style: normal;
}

/* ===== 信息区块（介绍/设施/标签/房型/门票） ===== */
.info-section {
  margin-top: 1.25rem;
}

.section-title {
  font-size: 1.125rem; /* 18px */
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 0.5rem 0;
}

.section-text {
  color: #6e6e73;
  line-height: 1.5;
  font-size: 0.9375rem;
  margin: 0;
}

/* ===== 标签 ===== */
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  background: #f5f5f7;
  padding: 0.25rem 0.75rem;
  border-radius: 980px;
  font-size: 0.75rem;
  color: #6e6e73;
}

.tag-blue {
  background: rgba(41, 151, 255, 0.1);
  color: #2997ff;
}

/* ===== 房型/门票列表 ===== */
.sub-list {
  display: flex;
  flex-direction: column;
  gap: 0.625rem;
  max-height: 200px;
  overflow-y: auto;
  padding-right: 0.25rem;
}

.sub-list::-webkit-scrollbar {
  width: 4px;
}

.sub-list::-webkit-scrollbar-thumb {
  background: #d2d2d6;
  border-radius: 2px;
}

.sub-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.875rem 1rem;
  background: #f5f5f7;
  border-radius: 14px;
}

.sub-info {
  flex: 1;
  min-width: 0;
}

.sub-info h4 {
  font-size: 1rem;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 0.25rem 0;
}

.sub-desc {
  font-size: 0.8125rem;
  color: #6e6e73;
  margin: 0 0 0.375rem 0;
  line-height: 1.4;
}

.sub-price {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1d1d1f;
  margin-left: 1rem;
  white-space: nowrap;
}

/* ===== 统计 ===== */
.modal-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-top: 1.25rem;
  padding-top: 1rem;
  border-top: 1px solid #d2d2d6;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.8125rem;
  color: #6e6e73;
}

.stat-value {
  font-size: 1rem;
  font-weight: 600;
  color: #1d1d1f;
}

/* ===== 操作按钮（胶囊形 CTA） ===== */
.product-actions {
  display: flex;
  gap: 1rem;
  margin-top: auto;
  padding-top: 1.5rem;
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
    gap: 1.75rem;
    padding: 1.75rem;
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
    gap: 1.25rem;
    padding: 1.25rem;
  }

  .product-image {
    aspect-ratio: 16 / 10;
    border-radius: 16px;
  }

  .product-name {
    font-size: 1.25rem;
  }

  .product-price {
    font-size: 1.25rem;
  }

  .product-description {
    font-size: 0.9375rem;
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
