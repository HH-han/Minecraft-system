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
        <div class="empty-text">购物车是空的</div>
        <router-link to="/product" class="btn btn-primary">去购物</router-link>
      </div>
      <div v-else class="items-list">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <div class="item-checkbox">
            <input 
              type="checkbox" 
              v-model="item.selected"
              @change="updateTotal"
            >
          </div>
          <div class="item-image">
            <img :src="item.image" :alt="item.name">
          </div>
          <div class="item-info">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-spec" v-if="item.spec">{{ item.spec }}</div>
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
        <input 
          type="checkbox" 
          v-model="selectAllFlag"
          @change="toggleSelectAll"
        >
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
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'

const emit = defineEmits(['checkout'])
const router = useRouter()

const cartItems = ref([
  {
    id: 1,
    name: 'Minecraft  Java版',
    spec: '标准版',
    price: 165,
    quantity: 1,
    selected: true,
    image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20Java%20Edition%20game%20cover&size=512x512'
  },
  {
    id: 2,
    name: 'Minecraft 皮肤包',
    spec: '基础包',
    price: 34.99,
    quantity: 2,
    selected: false,
    image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20skin%20pack&size=512x512'
  }
])

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

const increaseQuantity = (item) => {
  item.quantity++
  updateTotal()
}

const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    item.quantity--
    updateTotal()
  }
}

const removeItem = (id) => {
  if (confirm('确定要删除这个商品吗？')) {
    cartItems.value = cartItems.value.filter(item => item.id !== id)
    updateTotal()
  }
}

const deleteSelected = () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    alert('请选择要删除的商品')
    return
  }
  if (confirm(`确定要删除选中的 ${selectedItems.length} 件商品吗？`)) {
    cartItems.value = cartItems.value.filter(item => !item.selected)
    updateTotal()
  }
}

const checkout = () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    alert('请选择要结算的商品')
    return
  }
  // 触发结算事件
  emit('checkout')
  // 跳转到订单确认页面
  router.push('/payment')
}

// 初始化全选状态
watch(cartItems, () => {
  updateTotal()
}, { deep: true, immediate: true })
</script>

<style scoped>
.shopping-cart {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.cart-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.cart-actions {
  min-width: 350px;
  display: flex;
  gap: 10px;
}

.btn {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid #d9d9d9;
  transition: all 0.3s;
}

.btn-secondary {
  background: #fff;
  color: #333;
}

.btn-secondary:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.btn-danger {
  background: #fff;
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.btn-danger:hover {
  background: #ff4d4f;
  color: #fff;
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 0;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-icon svg {
  width: 24px;
  height: 24px;
}

.empty.empty-text {
  font-size: 16px;
  color: #999;
  margin-bottom: 20px;
}

.btn-primary {
  background: #ff4d4f;
  color: #fff;
  border-color: #ff4d4f;
  padding: 8px 20px;
  text-decoration: none;
}

.btn-primary:hover {
  background: #ff7875;
  border-color: #ff7875;
}

.items-list {
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-checkbox {
  margin-right: 15px;
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
  gap: 8px;
}

.item-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
}

.item-spec {
  font-size: 12px;
  color: #999;
}

.item-price {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.price {
  color: #ff4d4f;
  font-weight: 600;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-btn {
  width: 28px;
  height: 28px;
  border: 1px solid #d9d9d9;
  background: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  border-radius: 4px;
  transition: all 0.3s;
}

.quantity-btn:hover:not(:disabled) {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.quantity-btn:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

.quantity-input {
  width: 50px;
  height: 28px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  text-align: center;
  font-size: 14px;
}

.quantity-input:focus {
  outline: none;
  border-color: #ff4d4f;
}

.item-total {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.item-actions {
  margin-left: 15px;
}

.btn-link {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 14px;
  padding: 0;
  text-decoration: underline;
}

.btn-link:hover {
  color: #ff4d4f;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
  margin-top: 20px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
}

.selected-count {
  color: #666;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-price {
  font-size: 16px;
}

.total-price .price {
  font-size: 18px;
}

.checkout-btn {
  padding: 10px 30px;
  font-size: 16px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .shopping-cart {
    padding: 15px;
  }
  
  .cart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .cart-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .cart-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    padding: 15px;
    border: 1px solid #eee;
    border-radius: 8px;
    margin-bottom: 10px;
  }
  
  .cart-item:last-child {
    margin-bottom: 0;
  }
  
  .item-info {
    width: 100%;
  }
  
  .item-price {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .quantity-control {
    align-self: flex-start;
  }
  
  .cart-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .footer-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .checkout-btn {
    width: 120px;
  }
}
</style>