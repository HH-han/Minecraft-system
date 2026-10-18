<template>
  <div class="shopping-cart">
    <div class="cart-header">
      <h2 class="cart-title">{{ $t('payment.cart.title') }}</h2>
      <div class="cart-actions">
        <button class="action-link" @click="selectAll">{{ $t('payment.cart.selectAll') }}</button>
        <button class="action-link delete-link" @click="deleteSelected">{{ $t('payment.cart.deleteSelected') }}</button>
      </div>
    </div>
    
    <div class="cart-items">
      <div v-if="cartItems.length === 0" class="empty-cart">
        <div class="empty-icon">
          <svg t="1773668799267" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="20586" width="256" height="256">
                <path d="M509.6448 511.3856m-450.816 0a450.816 450.816 0 1 0 901.632 0 450.816 450.816 0 1 0-901.632 0Z"
                  fill="#d2d2d6" p-id="20587"></path>
                <path
                  d="M277.6576 319.3344c-15.104 0-29.5424-0.7168-43.8272 0.1536-24.9344 1.5872-46.6944-28.5696-34.56-56.6272 4.7616-10.9568 14.4896-16.1792 26.112-16.384 23.808-0.4096 47.6672-0.5632 71.4752 0.1024 19.9168 0.5632 31.0272 9.3696 36.0448 28.8256 2.7648 10.8032 3.1232 22.272 4.5568 33.4336 1.2288 9.984 2.4576 19.968 3.6864 30.3616h442.7776c11.3152 0 22.1184 1.8432 31.488 8.7552 10.9056 8.0384 13.824 20.992 8.2944 37.1712-7.5776 22.0672-15.5136 44.032-23.04 66.1504-8.6528 25.4464-16.384 51.2-25.5488 76.4416-8.8064 24.2176-25.1904 37.5296-52.0192 37.376-107.8272-0.512-215.7056-0.2048-323.5328-0.2048h-9.6256c2.9696 18.0736 6.0416 35.4304 8.6528 52.8896 0.768 5.2224 4.096 4.7616 7.68 4.7616h118.2208c63.488 0 126.976-0.0512 190.4128 0.0512 6.3488 0 12.7488 0.4096 18.944 1.6384 9.3184 1.792 14.8992 8.0384 16.384 17.3056 2.9696 18.5344-5.12 37.3248-29.7984 37.2224-109.7216-0.4096-219.4432-0.5632-329.1648 0.1024-28.5184 0.2048-43.8272-16.7424-50.2272-41.9328-10.1888-40.1408-17.2032-81.0496-25.2416-121.7536-12.288-62.464-24.2688-124.9792-36.4032-187.4432-0.4608-2.56-1.024-5.0688-1.7408-8.3968zM674.816 815.3088c-26.9824 1.2288-61.3888-28.5184-60.0576-58.2656 1.536-33.5872 23.7056-59.2384 58.4704-59.8016 34.5088-0.512 61.2352 24.8832 61.9008 57.9584 0.5632 32-28.0064 60.0576-60.3136 60.1088zM407.6544 812.6976c-32.4608 0.0512-58.9312-25.7536-59.1872-57.7024-0.256-30.9248 26.4704-58.3168 57.1392-58.5728 33.6896-0.256 61.5936 25.9584 61.5424 57.9072-0.0512 32.256-26.624 58.3168-59.4944 58.368z"
                  fill="#6e6e73" p-id="20588"></path>
              </svg>
        </div>
        <div class="empty-text">{{ $t('payment.cart.empty') }}</div>
        <router-link to="/product" class="primary-button">{{ $t('payment.cart.goShopping') }}</router-link>
      </div>
      <div v-else class="items-list">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <div class="item-checkbox">
            <label class="apple-checkbox">
              <input
                type="checkbox"
                v-model="item.selected"
                @change="updateTotal"
              >
              <span class="checkbox-custom"></span>
            </label>
          </div>
          <div class="item-image">
            <img :src="item.image" :alt="item.itemName || item.name">
          </div>
          <div class="item-info">
            <div class="item-top">
              <div class="item-text">
                <div class="item-name">{{ item.itemName || item.name }}</div>
                <div class="item-spec" v-if="item.itemType">
                  {{ item.itemType === 'food' ? $t('payment.food') : item.itemType === 'product' ? $t('payment.product') : item.itemType }}
                </div>
              </div>
              <button class="item-delete" @click="removeItem(item.id)" :aria-label="$t('payment.cart.delete')">
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                </svg>
              </button>
            </div>
            <div class="item-bottom">
              <span class="price">¥{{ item.price }}</span>
              <div class="quantity-control">
                <button
                  class="quantity-btn"
                  @click="decreaseQuantity(item)"
                  :disabled="item.quantity <= 1"
                >−</button>
                <input
                  type="number"
                  v-model.number="item.quantity"
                  class="quantity-input"
                  min="1"
                  @change="updateTotal"
                >
                <button
                  class="quantity-btn"
                  @click="increaseQuantity(item)"
                >+</button>
              </div>
              <span class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="footer-left">
        <label class="apple-checkbox">
          <input 
            type="checkbox" 
            v-model="selectAllFlag"
            @change="toggleSelectAll"
          >
          <span class="checkbox-custom"></span>
        </label>
        <span class="select-label">{{ $t('payment.cart.selectAll') }}</span>
        <span class="selected-count">{{ $t('payment.cart.selectedCount', { count: selectedCount }) }}</span>
      </div>
      <div class="footer-right">
        <div class="total-price">
          <span class="total-label">{{ $t('payment.cart.total') }}</span>
          <span class="price-value">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <button class="primary-button checkout-btn" @click="checkout">{{ $t('payment.cart.checkout') }}</button>
      </div>
    </div>

    <!-- 收货信息：结算后订单需通过物流发货 -->
    <el-dialog
      v-model="addressVisible"
      :title="$t('payment.cart.addressTitle')"
      width="460px"
      :close-on-click-modal="false"
    >
      <el-form ref="addressFormRef" :model="addressForm" :rules="addressRules" label-width="80px">
        <el-form-item :label="$t('payment.cart.receiverName')" prop="receiverName">
          <el-input v-model="addressForm.receiverName" :placeholder="$t('payment.cart.receiverNamePlaceholder')" maxlength="50" />
        </el-form-item>
        <el-form-item :label="$t('payment.cart.receiverPhone')" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" :placeholder="$t('payment.cart.receiverPhonePlaceholder')" maxlength="20" />
        </el-form-item>
        <el-form-item :label="$t('payment.cart.receiverAddress')" prop="receiverAddress">
          <el-input
            v-model="addressForm.receiverAddress"
            type="textarea"
            :rows="2"
            :placeholder="$t('payment.cart.receiverAddressPlaceholder')"
            maxlength="200"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressVisible = false">{{ $t('payment.common.cancel') }}</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmAddress">{{ $t('payment.cart.confirmCheckout') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getCartList, updateCart, deleteFromCart } from '@/api/cart.js'
import { createOrder } from '@/api/order.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const emit = defineEmits(['checkout'])
const router = useRouter()
const { t } = useI18n()

const cartItems = ref([])

// 收货信息弹窗（结算即进入物流发货流程）
const ADDRESS_STORAGE_KEY = 'cart-receiver-info'
const addressVisible = ref(false)
const addressFormRef = ref(null)
const submitting = ref(false)
const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: ''
})
const addressRules = computed(() => ({
  receiverName: [{ required: true, message: t('payment.cart.validation.receiverName'), trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: t('payment.cart.validation.receiverPhone'), trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: t('payment.cart.validation.receiverPhoneInvalid'), trigger: 'blur' }
  ],
  receiverAddress: [{ required: true, message: t('payment.cart.validation.receiverAddress'), trigger: 'blur' }]
}))

const selectAllFlag = ref(false)

// 记录当前正在支付的购物车条目 ID，支付成功后用于精准移除
// 使用购物车条目主键 ID（而非商品 ID），避免误删同商品的其他购物车项
const pendingCartItemIds = ref([])

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
    await ElMessageBox.confirm(t('payment.cart.confirmDeleteItem'), t('payment.common.tip'), {
      confirmButtonText: t('payment.common.confirm'),
      cancelButtonText: t('payment.common.cancel'),
      type: 'warning'
    })
    
    await deleteFromCart(id)
    cartItems.value = cartItems.value.filter(item => item.id !== id)
    updateTotal()
    ElMessage.success(t('payment.cart.deleteSuccess'))
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除购物车商品失败:', error)
      ElMessage.error(t('payment.cart.deleteFailed'))
    }
  }
}

const deleteSelected = async () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning(t('payment.cart.selectToDelete'))
    return
  }
  try {
    await ElMessageBox.confirm(t('payment.cart.confirmDeleteSelected', { count: selectedItems.length }), t('payment.common.tip'), {
      confirmButtonText: t('payment.common.confirm'),
      cancelButtonText: t('payment.common.cancel'),
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
    ElMessage.success(t('payment.cart.deleteSelectedSuccess'))
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除选中商品失败:', error)
      ElMessage.error(t('payment.cart.deleteFailed'))
    }
  }
}

const checkout = () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning(t('payment.cart.selectToCheckout'))
    return
  }
  // 从 localStorage 获取用户信息
  const userInfo = JSON.parse(localStorage.getItem('user'))
  const userId = userInfo?.id || userInfo?.userId

  if (!userId) {
    ElMessage.warning(t('payment.cart.loginRequired'))
    router.push('/login')
    return
  }

  // 回填上次填写的收货信息
  try {
    const saved = JSON.parse(localStorage.getItem(ADDRESS_STORAGE_KEY) || '{}')
    addressForm.receiverName = saved.receiverName || ''
    addressForm.receiverPhone = saved.receiverPhone || ''
    addressForm.receiverAddress = saved.receiverAddress || ''
  } catch {
    addressForm.receiverName = ''
    addressForm.receiverPhone = ''
    addressForm.receiverAddress = ''
  }
  addressVisible.value = true
}

// 确认收货信息后创建订单（订单携带收货信息，进入物流发货流程）
const confirmAddress = async () => {
  try {
    await addressFormRef.value?.validate()
  } catch {
    return
  }

  const userInfo = JSON.parse(localStorage.getItem('user'))
  const userId = userInfo?.id || userInfo?.userId
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) return

  localStorage.setItem(ADDRESS_STORAGE_KEY, JSON.stringify({
    receiverName: addressForm.receiverName,
    receiverPhone: addressForm.receiverPhone,
    receiverAddress: addressForm.receiverAddress
  }))

  submitting.value = true
  try {
    // 为每个选中的商品创建单独的订单
    const orderPromises = selectedItems.map(item => {
      const orderRequest = {
        itemType: item.itemType || (item.category === 'food' ? 'food' : 'product'),
        itemId: item.id,
        itemName: item.itemName || item.name,
        image: item.image,
        amount: item.price * item.quantity,
        quantity: item.quantity,
        remark: '',
        receiverName: addressForm.receiverName,
        receiverPhone: addressForm.receiverPhone,
        receiverAddress: addressForm.receiverAddress
      }
      return createOrder(orderRequest)
    })

    const responses = await Promise.all(orderPromises)

    const failedOrders = responses.filter(response => response.code !== 200)
    if (failedOrders.length > 0) {
      ElMessage.error(t('payment.cart.orderCreatePartialFailed'))
      return
    }

    const orderIds = responses.map(response => response.data.orderId || response.data.id || response.data.orderNo)

    // 记录本次结算的购物车条目 ID，待支付成功后精准移除
    pendingCartItemIds.value = selectedItems.map(item => item.id)

    addressVisible.value = false
    // 触发结算事件，传递订单数据
    emit('checkout', {
      orderId: orderIds[0],
      orderIds: orderIds,
      userId: userId,
      cartItems: selectedItems
    })
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error(t('payment.cart.orderCreateFailed'))
  } finally {
    submitting.value = false
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

// 数据变更后自动刷新
useAutoRefresh(fetchCartData)

// 支付成功后移除已支付的购物车条目
// 仅在收到 PayPage 支付成功消息且存在待处理条目时执行，避免误删
const removePaidCartItems = async () => {
  const ids = pendingCartItemIds.value
  if (ids.length === 0) return

  try {
    // 逐个删除已支付的购物车条目（按购物车条目主键 ID，精准匹配）
    await Promise.all(ids.map(id => deleteFromCart(id)))
    ElMessage.success(t('payment.cart.paidSuccess'))
    // 重新拉取购物车列表，确保与后端一致
    await fetchCartData()
  } catch (error) {
    console.error('移除已支付购物车商品失败:', error)
    ElMessage.error(t('payment.cart.cartUpdateFailed'))
  } finally {
    // 无论成功失败，清空待处理列表，避免重复处理
    pendingCartItemIds.value = []
  }
}

// 监听 PayPage 支付成功后发出的 closeModal 消息
// 该消息仅在支付流程成功完成后由 PayPage 发出，用户手动关闭模态框不会触发此消息
const handlePaymentMessage = (event) => {
  if (event.data?.action === 'closeModal' && pendingCartItemIds.value.length > 0) {
    removePaidCartItems()
  }
}

// 组件挂载时获取购物车数据并注册支付成功监听
onMounted(() => {
  fetchCartData()
  window.addEventListener('message', handlePaymentMessage)
})

// 组件卸载前移除事件监听，避免内存泄漏与重复触发
onBeforeUnmount(() => {
  window.removeEventListener('message', handlePaymentMessage)
})

// 初始化全选状态
watch(cartItems, () => {
  updateTotal()
}, { deep: true, immediate: true })
</script>

<style scoped>
.shopping-cart {
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
  color: var(--theme-text-1);
}

/* Header */
.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--theme-hairline);
}

.cart-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--theme-text-1);
  margin: 0;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.cart-actions {
  display: flex;
  gap: 20px;
  align-items: center;
}

/* Action Links */
.action-link {
  background: none;
  border: none;
  color: var(--theme-primary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.2s ease;
  font-family: inherit;
  display: inline-flex;
  align-items: center;
}

.action-link:hover {
  background: var(--theme-primary-soft);
}

.delete-link {
  color: var(--theme-text-2);
}

.delete-link:hover {
  color: var(--theme-danger);
  background: var(--theme-danger-soft);
}

/* Primary Button */
.primary-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  border: none;
  border-radius: 12px;
  padding: 12px 28px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  text-align: center;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
}

.primary-button:hover {
  background: var(--theme-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--theme-shadow-md);
}

.primary-button:active {
  transform: translateY(0) scale(0.98);
}

/* Empty Cart */
.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 72px 0;
  text-align: center;
}

.empty-icon {
  width: 110px;
  height: 110px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--theme-bg-surface-2);
}

.empty-icon svg {
  width: 60%;
  height: 60%;
}

.empty-text {
  font-size: 17px;
  color: var(--theme-text-1);
  margin-bottom: 8px;
  font-weight: 600;
}

.empty-cart .primary-button {
  margin-top: 20px;
}

/* Cart Items */
.items-list {
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  align-items: stretch;
  padding: 18px;
  margin-bottom: 14px;
  border-radius: 18px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.cart-item:hover {
  border-color: var(--theme-border-strong);
  box-shadow: var(--theme-shadow-md);
  transform: translateY(-1px);
}

/* Apple Checkbox */
.item-checkbox {
  margin-right: 16px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.apple-checkbox {
  position: relative;
  display: inline-block;
  cursor: pointer;
  user-select: none;
}

.apple-checkbox input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkbox-custom {
  display: inline-block;
  width: 22px;
  height: 22px;
  border: 2px solid var(--theme-border-strong);
  border-radius: 50%;
  background: var(--theme-bg-surface);
  transition: all 0.2s ease;
  position: relative;
}

.apple-checkbox input:checked + .checkbox-custom {
  background: var(--theme-primary);
  border-color: var(--theme-primary);
}

.apple-checkbox input:checked + .checkbox-custom::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 3px;
  width: 6px;
  height: 10px;
  border: solid var(--theme-primary-text);
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.apple-checkbox:hover .checkbox-custom {
  border-color: var(--theme-primary);
}

/* Item Image */
.item-image {
  width: 100px;
  height: 100px;
  margin-right: 18px;
  flex-shrink: 0;
  border-radius: 14px;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.cart-item:hover .item-image img {
  transform: scale(1.04);
}

/* Item Info */
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
  gap: 12px;
}

.item-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.item-text {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 16px;
  color: var(--theme-text-1);
  line-height: 1.4;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.item-spec {
  display: inline-block;
  font-size: 12px;
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
  padding: 3px 10px;
  border-radius: 8px;
  margin-top: 6px;
  font-weight: 500;
}

/* Delete button (top-right of info) */
.item-delete {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.item-delete:hover {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
}

/* Bottom row: unit price | quantity | subtotal */
.item-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.price {
  color: var(--theme-text-2);
  font-weight: 500;
  font-size: 14px;
  flex-shrink: 0;
}

/* Quantity Control */
.quantity-control {
  display: flex;
  align-items: center;
  gap: 2px;
  background: var(--theme-bg-surface-2);
  border-radius: 10px;
  padding: 3px;
}

.quantity-btn {
  width: 30px;
  height: 30px;
  border: none;
  background: transparent;
  color: var(--theme-text-1);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.15s ease;
  font-family: inherit;
}

.quantity-btn:hover:not(:disabled) {
  background: var(--theme-bg-surface);
  color: var(--theme-primary);
  box-shadow: var(--theme-shadow-sm);
}

.quantity-btn:active:not(:disabled) {
  transform: scale(0.9);
}

.quantity-btn:disabled {
  color: var(--theme-text-3);
  cursor: not-allowed;
}

.quantity-input {
  width: 40px;
  height: 30px;
  border: none;
  text-align: center;
  font-size: 14px;
  background: transparent;
  color: var(--theme-text-1);
  font-weight: 600;
  font-family: inherit;
  -moz-appearance: textfield;
}

.quantity-input::-webkit-outer-spin-button,
.quantity-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.quantity-input:focus {
  outline: none;
}

.item-total {
  font-size: 18px;
  font-weight: 700;
  color: var(--theme-text-1);
  flex-shrink: 0;
  letter-spacing: -0.01em;
}

/* Cart Footer */
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-radius: 16px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
  margin-top: 20px;
  gap: 16px;
  flex-wrap: wrap;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.select-label {
  font-size: 15px;
  color: var(--theme-text-1);
  font-weight: 500;
}

.selected-count {
  font-size: 13px;
  color: var(--theme-text-2);
  padding: 4px 12px;
  background: var(--theme-bg-surface);
  border-radius: 8px;
  border: 1px solid var(--theme-hairline);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.total-label {
  font-size: 13px;
  color: var(--theme-text-2);
  font-weight: 400;
}

.price-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.02em;
  line-height: 1.1;
}

.checkout-btn {
  min-width: 110px;
}

/* Responsive */
@media (max-width: 767px) {
  .cart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }
  .cart-title { font-size: 22px; }
  .cart-actions { width: 100%; gap: 12px; }

  .cart-item {
    flex-wrap: wrap;
    padding: 16px;
    gap: 12px;
  }
  .item-checkbox { margin-right: 12px; }
  .item-image { width: 80px; height: 80px; margin-right: 14px; }
  .item-info { flex: 1; min-width: 180px; gap: 10px; }
  .item-bottom { gap: 8px; }
  .item-total { font-size: 16px; }

  .cart-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
    padding: 18px;
  }
  .footer-left { width: 100%; }
  .footer-right {
    width: 100%;
    justify-content: space-between;
    align-items: center;
  }
  .total-price { align-items: flex-start; }
  .checkout-btn { flex: 1; min-width: 0; }

  .empty-icon { width: 90px; height: 90px; }
}

@media (max-width: 480px) {
  .cart-title { font-size: 20px; }
  .cart-item { padding: 14px; border-radius: 16px; }
  .item-image { width: 68px; height: 68px; }
  .item-name { font-size: 15px; }
  .price { font-size: 13px; }
  .item-total { font-size: 16px; }
  .quantity-btn { width: 28px; height: 28px; font-size: 15px; }
  .quantity-input { width: 34px; height: 28px; }
  .price-value { font-size: 22px; }
  .checkout-btn { padding: 11px 24px; font-size: 14px; }
  .cart-footer { padding: 16px; border-radius: 14px; }
}
</style>