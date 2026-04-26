<template>
  <div class="points-container">
    <h2 class="points-title">积分商城</h2>
    <div class="user-points">
      <span class="points-label">我的积分：</span>
      <span class="points-value">{{ userPoints }}</span>
    </div>
    
    <div class="product-list">
      <div v-for="product in products" :key="product.id" class="product-item">
        <img :src="product.imageUrl" alt="商品图片" class="product-image">
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-description">{{ product.description }}</p>
        <div class="product-price">
          <span class="price-label">积分价格：</span>
          <span class="price-value">{{ product.pointsPrice }}</span>
        </div>
        <div class="product-stock">
          <span class="stock-label">库存：</span>
          <span class="stock-value">{{ product.stock }}</span>
        </div>
        <el-button 
          type="primary" 
          @click="exchangeProduct(product)"
          :disabled="userPoints < product.pointsPrice || product.stock <= 0"
          class="exchange-button"
        >
          立即兑换
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getUserPoints as apiGetUserPoints, getProducts as apiGetProducts, exchangeProduct as apiExchangeProduct } from '@/api/points';

const userPoints = ref(0);
const products = ref([]);

// 获取用户积分
const getUserPoints = async () => {
  try {
    const response = await apiGetUserPoints();
    userPoints.value = response.data;
  } catch (error) {
    console.error('获取用户积分失败:', error);
    ElMessage.error('获取用户积分失败');
  }
};

// 获取商品列表
const getProducts = async () => {
  try {
    const response = await apiGetProducts();
    products.value = response.data;
  } catch (error) {
    console.error('获取商品列表失败:', error);
    ElMessage.error('获取商品列表失败');
  }
};

// 兑换商品
const exchangeProduct = async (product) => {
  try {
    const response = await apiExchangeProduct(product.id);
    ElMessage.success('兑换成功');
    // 刷新数据
    getUserPoints();
    getProducts();
  } catch (error) {
    console.error('兑换商品失败:', error);
    ElMessage.error(error.response?.data?.message || '兑换商品失败');
  }
};

onMounted(() => {
  getUserPoints();
  getProducts();
});
</script>

<style scoped>
.points-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.points-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.user-points {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: right;
}

.points-label {
  font-size: 16px;
  color: #666;
  margin-right: 10px;
}

.points-value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s;
  background-color: #fff;
}

.product-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 15px;
}

.product-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.product-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
  line-height: 1.4;
}

.product-price {
  margin-bottom: 10px;
}

.price-label {
  font-size: 14px;
  color: #666;
  margin-right: 10px;
}

.price-value {
  font-size: 16px;
  font-weight: bold;
  color: #F56C6C;
}

.product-stock {
  margin-bottom: 15px;
}

.stock-label {
  font-size: 14px;
  color: #666;
  margin-right: 10px;
}

.stock-value {
  font-size: 14px;
  color: #67C23A;
}

.exchange-button {
  width: 100%;
  margin-top: 10px;
}
</style>