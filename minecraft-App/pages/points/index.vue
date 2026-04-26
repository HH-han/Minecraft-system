<template>
  <view class="points-container">
    <view class="points-header">
      <view class="points-info">
        <text class="points-label">我的积分</text>
        <text class="points-value">{{ userInfo?.points || 0 }}</text>
      </view>
    </view>
    
    <view class="products-section">
      <text class="section-title">积分兑换商品</text>
      <view class="products-list">
        <view 
          v-for="product in products" 
          :key="product.id" 
          class="product-item"
          @click="exchangeProduct(product)"
        >
          <image :src="product.imageUrl || 'https://via.placeholder.com/300x200'" class="product-image"></image>
          <view class="product-info">
            <text class="product-name">{{ product.name }}</text>
            <text class="product-description">{{ product.description }}</text>
            <view class="product-footer">
              <text class="points-price">{{ product.pointsPrice }} 积分</text>
              <text class="stock">库存: {{ product.stock }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 兑换弹窗 -->
    <uni-popup ref="exchangePopup" type="center" :mask-click="false">
      <view class="exchange-popup">
        <text class="popup-title">兑换商品</text>
        <view class="popup-content">
          <view class="form-item">
            <text class="label">收货人</text>
            <input v-model="exchangeForm.receiver" placeholder="请输入收货人姓名" class="input" />
          </view>
          <view class="form-item">
            <text class="label">手机号</text>
            <input v-model="exchangeForm.phone" placeholder="请输入手机号" class="input" />
          </view>
          <view class="form-item">
            <text class="label">收货地址</text>
            <textarea v-model="exchangeForm.address" placeholder="请输入收货地址" class="textarea"></textarea>
          </view>
          <view class="form-item">
            <text class="label">兑换数量</text>
            <view class="quantity-control">
              <button @click="decreaseQuantity" class="quantity-btn">-</button>
              <text class="quantity">{{ exchangeForm.quantity }}</text>
              <button @click="increaseQuantity" class="quantity-btn">+</button>
            </view>
          </view>
          <view class="form-item">
            <text class="label">所需积分</text>
            <text class="total-points">{{ (currentProduct?.pointsPrice || 0) * exchangeForm.quantity }} 积分</text>
          </view>
        </view>
        <view class="popup-footer">
          <button @click="closePopup" class="cancel-btn">取消</button>
          <button @click="submitExchange" class="confirm-btn">确认兑换</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { getPointsProducts, exchangeProduct } from '@/api/points'
import { getUserInfo } from '@/api/user'

export default {
  data() {
    return {
      userInfo: null,
      products: [],
      currentProduct: null,
      exchangeForm: {
        receiver: '',
        phone: '',
        address: '',
        quantity: 1
      }
    }
  },
  onLoad() {
    this.loadUserInfo()
    this.loadProducts()
  },
  methods: {
    async loadUserInfo() {
      try {
        const res = await getUserInfo()
        this.userInfo = res.data
      } catch (error) {
        console.error('获取用户信息失败', error)
      }
    },
    async loadProducts() {
      try {
        const res = await getPointsProducts({ page: 1, size: 100 })
        this.products = res.records
      } catch (error) {
        console.error('获取商品列表失败', error)
      }
    },
    exchangeProduct(product) {
      this.currentProduct = product
      this.exchangeForm.quantity = 1
      this.$refs.exchangePopup.open()
    },
    closePopup() {
      this.$refs.exchangePopup.close()
    },
    decreaseQuantity() {
      if (this.exchangeForm.quantity > 1) {
        this.exchangeForm.quantity--
      }
    },
    increaseQuantity() {
      if (this.currentProduct && this.exchangeForm.quantity < this.currentProduct.stock) {
        this.exchangeForm.quantity++
      }
    },
    async submitExchange() {
      if (!this.exchangeForm.receiver || !this.exchangeForm.phone || !this.exchangeForm.address) {
        uni.showToast({ title: '请填写完整信息', icon: 'none' })
        return
      }
      
      try {
        await exchangeProduct({
          productId: this.currentProduct.id,
          quantity: this.exchangeForm.quantity,
          address: this.exchangeForm.address,
          phone: this.exchangeForm.phone,
          receiver: this.exchangeForm.receiver
        })
        
        uni.showToast({ title: '兑换成功', icon: 'success' })
        this.$refs.exchangePopup.close()
        this.loadProducts()
        this.loadUserInfo()
      } catch (error) {
        uni.showToast({ title: error.response?.data?.message || '兑换失败', icon: 'none' })
      }
    }
  }
}
</script>

<style scoped>
.points-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.points-header {
  background: linear-gradient(135deg, #4CAF50, #45a049);
  padding: 30rpx;
  color: white;
  text-align: center;
}

.points-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.points-label {
  font-size: 28rpx;
  margin-bottom: 10rpx;
}

.points-value {
  font-size: 60rpx;
  font-weight: bold;
}

.products-section {
  padding: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  color: #333;
}

.products-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.product-item {
  width: 48%;
  background-color: white;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.product-item:hover {
  transform: translateY(-5rpx);
}

.product-image {
  width: 100%;
  height: 200rpx;
  object-fit: cover;
}

.product-info {
  padding: 15rpx;
}

.product-name {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  color: #333;
}

.product-description {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 15rpx;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.points-price {
  font-size: 26rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.stock {
  font-size: 22rpx;
  color: #999;
}

.exchange-popup {
  width: 90%;
  max-width: 600rpx;
  background-color: white;
  border-radius: 12rpx;
  padding: 30rpx;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30rpx;
  color: #333;
}

.form-item {
  margin-bottom: 25rpx;
}

.label {
  display: block;
  font-size: 26rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.input {
  width: 100%;
  padding: 15rpx;
  border: 1rpx solid #ddd;
  border-radius: 6rpx;
  font-size: 24rpx;
}

.textarea {
  width: 100%;
  padding: 15rpx;
  border: 1rpx solid #ddd;
  border-radius: 6rpx;
  font-size: 24rpx;
  min-height: 120rpx;
}

.quantity-control {
  display: flex;
  align-items: center;
}

.quantity-btn {
  width: 60rpx;
  height: 60rpx;
  border: 1rpx solid #ddd;
  background-color: #f5f5f5;
  border-radius: 6rpx;
  font-size: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity {
  margin: 0 30rpx;
  font-size: 26rpx;
  font-weight: bold;
}

.total-points {
  font-size: 28rpx;
  font-weight: bold;
  color: #ff6b6b;
}

.popup-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 40rpx;
}

.cancel-btn {
  flex: 1;
  height: 80rpx;
  margin-right: 20rpx;
  border: 1rpx solid #ddd;
  background-color: white;
  border-radius: 6rpx;
  font-size: 28rpx;
  color: #333;
}

.confirm-btn {
  flex: 1;
  height: 80rpx;
  background-color: #4CAF50;
  border: none;
  border-radius: 6rpx;
  font-size: 28rpx;
  color: white;
}
</style>
