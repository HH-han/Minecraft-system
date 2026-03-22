<template>
  <div v-if="visible" class="modal-overlay" @click.self="closeModal">
    <div class="modal-container">
      <div class="modal-header">
        <h2>商品详情</h2>
        <button class="close-btn" @click="closeModal">×</button>
      </div>
      <div class="modal-body">
        <div class="product-info">
          <div class="product-image">
            <img :src="product.coverImage" :alt="product.name" class="image-main">
          </div>
          <div class="product-details">
            <h3 class="product-name">{{ product.name }}</h3>
            <div class="product-id">商品ID: {{ product.id }}</div>
            <div class="product-price">¥{{ product.price }}</div>
            <div class="product-description">{{ product.description }}</div>
            <div class="product-actions">
              <button class="btn btn-cart" @click="handleAddToCart">加入购物车</button>
              <button class="btn btn-pay" @click="buyNow">立即支付</button>
            </div>
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
import { ref, watch, onMounted } from 'vue'
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

// 组件挂载时获取商品数据
onMounted(() => {
  if (props.productId && props.commodity) {
    fetchProductData()
  }
})
</script>

<style scoped>
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
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  animation: fadeIn 0.3s ease;
}

.modal-container {
  max-width: 900px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.18);
  animation: slideIn 0.3s ease;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  border-radius: 20px 20px 0 0;
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #7f8c8d;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #2c3e50;
  transform: rotate(90deg);
}

.modal-body {
  padding: 30px;
}

.product-info {
  display: flex;
  flex-wrap: nowrap;
  gap: 30px;
  flex-direction: column;
}

.product-image {
  flex: 0 0 35%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  border-radius: 12px;
}

.image-main {
  min-width: 100%;
  max-height: 350px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.image-main:hover {
  transform: scale(1.02);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
}

.product-details {
  flex: 1;
  min-width: 300px;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 15px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.product-id {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 20px;
  opacity: 0.8;
}

.product-price {
  font-size: 28px;
  font-weight: 700;
  color: #e74c3c;
  margin-bottom: 30px;
  text-shadow: 0 2px 4px rgba(231, 76, 60, 0.2);
}

.product-description {
  font-size: 16px;
  line-height: 1.6;
  color: #34495e;
  margin-bottom: 30px;
  min-height: 80px;
  opacity: 0.9;
}

.product-actions {
  display: flex;
  gap: 15px;
  margin-top: auto;
}

.btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.btn-cart {
  background: rgba(52, 152, 219, 0.8);
  color: white;
  border: 1px solid rgba(52, 152, 219, 0.3);
}

.btn-cart:hover {
  background: rgba(41, 128, 185, 0.9);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(52, 152, 219, 0.4);
}

.btn-pay {
  background: rgba(231, 76, 60, 0.8);
  color: white;
  border: 1px solid rgba(231, 76, 60, 0.3);
}

.btn-pay:hover {
  background: rgba(192, 57, 43, 0.9);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(231, 76, 60, 0.4);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 768px) {
  .modal-container {
    width: 95%;
    max-height: 90vh;
    border-radius: 16px;
  }
  
  .modal-header {
    padding: 15px 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .product-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .product-image {
    flex: 1;
    padding: 15px;
  }
  
  .product-details {
    min-width: auto;
  }
  
  .product-name {
    font-size: 20px;
  }
  
  .product-price {
    font-size: 24px;
  }
  
  .product-actions {
    flex-direction: column;
  }
  
  .btn {
    padding: 10px 20px;
    border-radius: 8px;
  }
}
</style>