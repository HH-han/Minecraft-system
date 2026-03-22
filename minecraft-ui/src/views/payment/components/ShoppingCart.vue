<template>
  <div class="shopping-cart">
    <div class="cart-header">
      <h2>购物车</h2>
      <div class="cart-actions">
        <button class="btn btn-secondary" @click="selectAll">全选</button>
        <button class="btn btn-danger" @click="deleteSelected">删除选中</button>
      </div>
    </div>
    
    <div class="cart-items">
      <div v-if="cartItems.length === 0" class="empty-cart">
        <div class="empty-icon">
          <svg t="1773668799267" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="20586" width="256" height="256">
                <path d="M509.6448 511.3856m-450.816 0a450.816 450.816 0 1 0 901.632 0 450.816 450.816 0 1 0-901.632 0Z"
                  fill="#FF9552" p-id="20587"></path>
                <path
                  d="M277.6576 319.3344c-15.104 0-29.5424-0.7168-43.8272 0.1536-24.9344 1.5872-46.6944-28.5696-34.56-56.6272 4.7616-10.9568 14.4896-16.1792 26.112-16.384 23.808-0.4096 47.6672-0.5632 71.4752 0.1024 19.9168 0.5632 31.0272 9.3696 36.0448 28.8256 2.7648 10.8032 3.1232 22.272 4.5568 33.4336 1.2288 9.984 2.4576 19.968 3.6864 30.3616h442.7776c11.3152 0 22.1184 1.8432 31.488 8.7552 10.9056 8.0384 13.824 20.992 8.2944 37.1712-7.5776 22.0672-15.5136 44.032-23.04 66.1504-8.6528 25.4464-16.384 51.2-25.5488 76.4416-8.8064 24.2176-25.1904 37.5296-52.0192 37.376-107.8272-0.512-215.7056-0.2048-323.5328-0.2048h-9.6256c2.9696 18.0736 6.0416 35.4304 8.6528 52.8896 0.768 5.2224 4.096 4.7616 7.68 4.7616h118.2208c63.488 0 126.976-0.0512 190.4128 0.0512 6.3488 0 12.7488 0.4096 18.944 1.6384 9.3184 1.792 14.8992 8.0384 16.384 17.3056 2.9696 18.5344-5.12 37.3248-29.7984 37.2224-109.7216-0.4096-219.4432-0.5632-329.1648 0.1024-28.5184 0.2048-43.8272-16.7424-50.2272-41.9328-10.1888-40.1408-17.2032-81.0496-25.2416-121.7536-12.288-62.464-24.2688-124.9792-36.4032-187.4432-0.4608-2.56-1.024-5.0688-1.7408-8.3968zM674.816 815.3088c-26.9824 1.2288-61.3888-28.5184-60.0576-58.2656 1.536-33.5872 23.7056-59.2384 58.4704-59.8016 34.5088-0.512 61.2352 24.8832 61.9008 57.9584 0.5632 32-28.0064 60.0576-60.3136 60.1088zM407.6544 812.6976c-32.4608 0.0512-58.9312-25.7536-59.1872-57.7024-0.256-30.9248 26.4704-58.3168 57.1392-58.5728 33.6896-0.256 61.5936 25.9584 61.5424 57.9072-0.0512 32.256-26.624 58.3168-59.4944 58.368z"
                  fill="#FFFFFF" p-id="20588"></path>
              </svg>
        </div>
        <div class="empty empty-text">购物车是空的</div>
        <router-link to="/product" class="btn btn-primary">去购物</router-link>
      </div>
      <div v-else class="items-list">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <div class="item-checkbox">
            <label class="ios-checkbox red">
              <input 
                type="checkbox" 
                v-model="item.selected"
                @change="updateTotal"
              >
              <div class="checkbox-wrapper">
                <div class="checkbox-bg"></div>
                <svg class="checkbox-icon" viewBox="0 0 24 24" fill="none">
                  <path 
                    class="check-path" 
                    d="M4 12L10 18L20 6" 
                    stroke="currentColor" 
                    stroke-width="3" 
                    stroke-linecap="round" 
                    stroke-linejoin="round"
                  ></path>
                </svg>
              </div>
            </label>
          </div>
          <div class="item-image">
            <img :src="item.image" :alt="item.itemName || item.name">
          </div>
          <div class="item-info">
            <div class="item-name">{{ item.itemName || item.name }}</div>
            <div class="item-spec" v-if="item.itemType">{{ item.itemType === 'food' ? '美食' : item.itemType === 'product' ? '纪念品' : item.itemType }}</div>
            <div class="item-price">
              <span class="price">{{ item.price }} 元</span>
              <div class="quantity-control">
                <button 
                  class="quantity-btn minus" 
                  @click="decreaseQuantity(item)"
                  :disabled="item.quantity <= 1"
                >-</button>
                <input 
                  type="number" 
                  v-model.number="item.quantity" 
                  class="quantity-input"
                  min="1"
                  @change="updateTotal"
                >
                <button 
                  class="quantity-btn plus" 
                  @click="increaseQuantity(item)"
                >+</button>
              </div>
              <span class="item-total">{{ (item.price * item.quantity).toFixed(2) }} 元</span>
            </div>
          </div>
          <div class="item-actions">
            <button class="btn btn-link" @click="removeItem(item.id)">删除</button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="footer-left">
        <label class="ios-checkbox red">
          <input 
            type="checkbox" 
            v-model="selectAllFlag"
            @change="toggleSelectAll"
          >
          <div class="checkbox-wrapper">
            <div class="checkbox-bg"></div>
            <svg class="checkbox-icon" viewBox="0 0 24 24" fill="none">
              <path 
                class="check-path" 
                d="M4 12L10 18L20 6" 
                stroke="currentColor" 
                stroke-width="3" 
                stroke-linecap="round" 
                stroke-linejoin="round"
              ></path>
            </svg>
          </div>
        </label>
        <span>全选</span>
        <span class="selected-count">已选择 {{ selectedCount }} 件商品</span>
      </div>
      <div class="footer-right">
        <div class="total-price">
          <span>合计：</span>
          <span class="price">{{ totalPrice.toFixed(2) }} 元</span>
        </div>
        <button class="btn btn-primary checkout-btn" @click="checkout">去结算</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCart, deleteFromCart } from '@/api/cart.js'
import { createOrder } from '@/api/order.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const emit = defineEmits(['checkout'])
const router = useRouter()

const cartItems = ref([])

const selectAllFlag = ref(false)

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((total, item) => total + item.price * item.quantity, 0)
})

const updateTotal = () => {
  // 当单个商品选择状态改变时，更新全选状态
  selectAllFlag.value = cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
}

const toggleSelectAll = () => {
  cartItems.value.forEach(item => {
    item.selected = selectAllFlag.value
  })
}

const selectAll = () => {
  selectAllFlag.value = true
  toggleSelectAll()
}

const increaseQuantity = async (item) => {
  try {
    item.quantity++
    await updateCart(item.id, item.quantity)
    updateTotal()
  } catch (error) {
    console.error('更新购物车失败:', error)
    item.quantity-- // 失败时回滚
  }
}

const decreaseQuantity = async (item) => {
  if (item.quantity > 1) {
    item.quantity--
    try {
      await updateCart(item.id, item.quantity)
      updateTotal()
    } catch (error) {
      console.error('更新购物车失败:', error)
      item.quantity++ // 失败时回滚
    }
  }
}

const removeItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteFromCart(id)
    cartItems.value = cartItems.value.filter(item => item.id !== id)
    updateTotal()
    ElMessage.success('商品已删除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除购物车商品失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const deleteSelected = async () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.length} 件商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 从 localStorage 获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('user'))
    const userId = userInfo?.id || userInfo?.userId
    
    // 逐个删除选中的商品
    for (const item of selectedItems) {
      await deleteFromCart(item.id)
    }
    // 重新获取购物车数据
    await fetchCartData()
    ElMessage.success('选中商品已删除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除选中商品失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const checkout = async () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  // 从 localStorage 获取用户信息
  const userInfo = JSON.parse(localStorage.getItem('user'))
  const userId = userInfo?.id || userInfo?.userId
  
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    // 为每个选中的商品创建单独的订单
    const orderPromises = selectedItems.map(item => {
      // 构建订单请求数据
      const orderRequest = {
        itemType: item.itemType || (item.category === 'food' ? 'food' : 'product'),
        itemId: item.id,
        itemName: item.itemName || item.name,
        image: item.image,
        amount: item.price * item.quantity,
        quantity: item.quantity,
        remark: ''
      }
      
      // 调用创建订单接口
      return createOrder(orderRequest)
    })
    
    // 并行处理所有订单创建请求
    const responses = await Promise.all(orderPromises)
    
    // 检查所有订单是否创建成功
    const failedOrders = responses.filter(response => response.code !== 200)
    if (failedOrders.length > 0) {
      ElMessage.error('部分订单创建失败，请重试')
      return
    }
    
    // 收集所有订单ID
    const orderIds = responses.map(response => response.data.orderId || response.data.id || response.data.orderNo)
    
    // 触发结算事件，传递订单数据
    emit('checkout', {
      orderId: orderIds[0],
      orderIds: orderIds,
      userId: userId,
      cartItems: selectedItems
    })
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败，请检查网络')
  }
}

// 从后端获取购物车数据
const fetchCartData = async () => {
  try {
    // 从 localStorage 获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('user'))
    const userId = userInfo?.id || userInfo?.userId
    
    // 传递 userId 到 API
    const response = await getCartList({ userId })
    cartItems.value = response.data || []
  } catch (error) {
    console.error('获取购物车数据失败:', error)
    cartItems.value = [] // 失败时也设置为空数组
  }
}

// 组件挂载时获取购物车数据
onMounted(() => {
  fetchCartData()
})

// 初始化全选状态
watch(cartItems, () => {
  updateTotal()
}, { deep: true, immediate: true })
</script>

<style scoped>
.shopping-cart {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  padding: 30px;
  margin-bottom: 30px;
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.shopping-cart::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.shopping-cart:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.18);
  background: rgba(255, 255, 255, 0.12);
}

.shopping-cart:hover::before {
  opacity: 1;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 25px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
}

.cart-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #ff4d4f, #ff7875);
  border-radius: 3px;
}

.cart-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  gap: 12px;
}

.cart-header h2::before {
  content: '';
  font-size: 28px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.cart-actions {
  min-width: 350px;
  display: flex;
  gap: 15px;
  align-items: center;
}

.btn {
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.2);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

.btn-secondary:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.btn-danger {
  background: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
  border-color: rgba(255, 77, 79, 0.3);
}

.btn-danger:hover {
  background: #ff4d4f;
  color: #fff;
  border-color: #ff4d4f;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100px 0;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.empty-cart::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 77, 79, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.1);
    opacity: 0.8;
  }
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.empty-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.empty-icon svg {
  width: 56px;
  height: 56px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.empty.empty-text {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 30px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
  font-weight: 500;
}

.btn-primary {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: #fff;
  border-color: #ff4d4f;
  padding: 10px 24px;
  text-decoration: none;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(255, 77, 79, 0.3);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%);
  border-color: #ff7875;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 77, 79, 0.4);
}

.items-list {
  margin-bottom: 25px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 24px;
  margin-bottom: 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.cart-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.05), transparent);
  transition: left 0.5s ease;
}

.cart-item:hover {
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 77, 79, 0.3);
}

.cart-item:hover::before {
  left: 100%;
}

.item-checkbox {
  margin-right: 20px;
  flex-shrink: 0;
}

/* iOS 风格 Checkbox 样式 */
/* From Uiverse.io by Tsiangana */ 
.checkbox-container {
  display: flex;
  gap: 20px;
  padding: 10px;
  background: #f8fafc;
  border-radius: 12px;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.1),
    0 2px 4px -2px rgba(0, 0, 0, 0.05);
}

.ios-checkbox {
  --checkbox-size: 28px;
  --checkbox-color: #3b82f6;
  --checkbox-bg: #dbeafe;
  --checkbox-border: #93c5fd;

  position: relative;
  display: inline-block;
  cursor: pointer;
  user-select: none;
  -webkit-tap-highlight-color: transparent;
}

.ios-checkbox input {
  display: none;
}

.checkbox-wrapper {
  position: relative;
  width: var(--checkbox-size);
  height: var(--checkbox-size);
  border-radius: 8px;
  transition: transform 0.2s ease;
}

.checkbox-bg {
  position: absolute;
  inset: 0;
  border-radius: 8px;
  border: 2px solid var(--checkbox-border);
  background: white;
  transition: all 0.2s ease;
}

.checkbox-icon {
  position: absolute;
  inset: 0;
  margin: auto;
  width: 80%;
  height: 80%;
  color: white;
  transform: scale(0);
  transition: all 0.2s ease;
}

.check-path {
  stroke-dasharray: 40;
  stroke-dashoffset: 40;
  transition: stroke-dashoffset 0.3s ease 0.1s;
}

/* Checked State */
.ios-checkbox input:checked + .checkbox-wrapper .checkbox-bg {
  background: var(--checkbox-color);
  border-color: var(--checkbox-color);
}

.ios-checkbox input:checked + .checkbox-wrapper .checkbox-icon {
  transform: scale(1);
}

.ios-checkbox input:checked + .checkbox-wrapper .check-path {
  stroke-dashoffset: 0;
}

/* Hover Effects */
.ios-checkbox:hover .checkbox-wrapper {
  transform: scale(1.05);
}

/* Active Animation */
.ios-checkbox:active .checkbox-wrapper {
  transform: scale(0.95);
}

/* Focus Styles */
.ios-checkbox input:focus + .checkbox-wrapper .checkbox-bg {
  box-shadow: 0 0 0 4px var(--checkbox-bg);
}

.ios-checkbox.red {
  --checkbox-color: #ef4444;
  --checkbox-bg: #fee2e2;
  --checkbox-border: #fca5a5;
}

/* Animation */
@keyframes bounce {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.ios-checkbox input:checked + .checkbox-wrapper {
  animation: bounce 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}


/* Animation */
@keyframes bounce {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.ios-checkbox input:checked + .checkbox-wrapper {
  animation: bounce 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.item-image {
  width: 110px;
  height: 110px;
  margin-right: 24px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.18);
  position: relative;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.item-image::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 77, 79, 0.2), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 1;
}

.item-image:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

.item-image:hover::before {
  opacity: 1;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.item-image:hover img {
  transform: scale(1.1);
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.item-name {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.95);
  line-height: 1.4;
  font-weight: 500;
  transition: color 0.3s ease;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cart-item:hover .item-name {
  color: #fff;
}

.item-spec {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 77, 79, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  align-self: flex-start;
  border: 1px solid rgba(255, 77, 79, 0.2);
  transition: all 0.3s ease;
}

.cart-item:hover .item-spec {
  background: rgba(255, 77, 79, 0.2);
  border-color: rgba(255, 77, 79, 0.4);
}

.item-price {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.price {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 18px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 24px;
  transition: all 0.3s ease;
}

.cart-item:hover .quantity-control {
  border-color: rgba(255, 77, 79, 0.3);
}

.quantity-btn {
  width: 36px;
  height: 36px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  border-radius: 50%;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.quantity-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 77, 79, 0.2);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.3s ease, height 0.3s ease;
}

.quantity-btn:hover:not(:disabled) {
  border-color: #ff4d4f;
  color: #ff4d4f;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

.quantity-btn:hover:not(:disabled)::before {
  width: 100px;
  height: 100px;
}

.quantity-btn:disabled {
  color: rgba(255, 255, 255, 0.3);
  cursor: not-allowed;
  border-color: rgba(255, 255, 255, 0.05);
}

.quantity-input {
  width: 70px;
  height: 36px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 18px;
  text-align: center;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  font-weight: 500;
}

.quantity-input:focus {
  outline: none;
  border-color: #ff4d4f;
  box-shadow: 0 0 0 3px rgba(255, 77, 79, 0.2);
  background: rgba(255, 255, 255, 0.15);
}

.item-total {
  font-size: 18px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.95);
  min-width: 110px;
  text-align: right;
  transition: color 0.3s ease;
}

.cart-item:hover .item-total {
  color: #ff4d4f;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.item-actions {
  margin-left: 20px;
  flex-shrink: 0;
}

.btn-link {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.btn-link:hover {
  color: #ff4d4f;
  background: rgba(255, 77, 79, 0.1);
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.15);
  margin-top: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.cart-footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 77, 79, 0.3), transparent);
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  position: relative;
  z-index: 1;
}

.footer-left span:first-of-type {
  margin-left: 8px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
  transition: color 0.3s ease;
}

.footer-left:hover span:first-of-type {
  color: #fff;
}

.selected-count {
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 77, 79, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid rgba(255, 77, 79, 0.2);
  transition: all 0.3s ease;
}

.footer-left:hover .selected-count {
  background: rgba(255, 77, 79, 0.2);
  border-color: rgba(255, 77, 79, 0.4);
  color: #ff4d4f;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
  position: relative;
  z-index: 1;
}

.total-price {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.95);
  font-weight: 500;
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.total-price span:first-of-type {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
}

.total-price .price {
  font-size: 28px;
  font-weight: 700;
  margin-left: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  animation: pricePulse 2s ease-in-out infinite;
}

@keyframes pricePulse {
  0%, 100% {
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }
  50% {
    text-shadow: 0 0 15px rgba(255, 77, 79, 0.5);
  }
}

.checkout-btn {
  padding: 14px 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: #fff;
  border: 1px solid #ff4d4f;
  box-shadow: 0 4px 16px rgba(255, 77, 79, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.checkout-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.5s ease, height 0.5s ease;
}

.checkout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 77, 79, 0.4);
  background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%);
  border-color: #ff7875;
}

.checkout-btn:hover::before {
  width: 200px;
  height: 200px;
}

.checkout-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

@media (max-width: 768px) {
  .shopping-cart {
    padding: 20px;
  }
  
  .cart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .cart-actions {
    width: 100%;
    justify-content: space-between;
    min-width: unset;
  }
  
  .cart-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 15px;
  }
  
  .item-info {
    width: 100%;
  }
  
  .item-price {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    width: 100%;
  }
  
  .quantity-control {
    align-self: flex-start;
  }
  
  .item-total {
    align-self: flex-start;
    text-align: left;
    margin-top: 10px;
  }
  
  .cart-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    padding: 20px;
  }
  
  .footer-right {
    width: 100%;
    justify-content: space-between;
    align-items: center;
  }
  
  .checkout-btn {
    width: 140px;
    padding: 10px 24px;
  }
  
  .empty-icon {
    width: 100px;
    height: 100px;
  }
  
  .empty-icon svg {
    width: 40px;
    height: 40px;
  }
}

@media (max-width: 480px) {
  .shopping-cart {
    padding: 15px;
  }
  
  .cart-header h2 {
    font-size: 18px;
  }
  
  .cart-item {
    padding: 15px;
  }
  
  .item-image {
    width: 80px;
    height: 80px;
  }
  
  .cart-footer {
    padding: 15px;
  }
  
  .total-price .price {
    font-size: 20px;
  }
  
  .checkout-btn {
    width: 120px;
    padding: 8px 20px;
    font-size: 14px;
  }
}
</style>