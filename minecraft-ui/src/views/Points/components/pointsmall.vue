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
          @click="openExchange(product)"
          :disabled="userPoints < product.pointsPrice || product.stock <= 0"
          class="exchange-button"
        >
          立即兑换
        </el-button>
      </div>
    </div>

    <!-- 收货信息填写：兑换后需通过物流发货，故需收集收件人信息 -->
    <el-dialog
      v-model="dialogVisible"
      title="填写收货信息"
      width="460px"
      :close-on-click-modal="false"
    >
      <div v-if="currentProduct" class="exchange-summary">
        <img :src="currentProduct.imageUrl" alt="商品图片" class="exchange-summary__img">
        <div class="exchange-summary__info">
          <div class="exchange-summary__name">{{ currentProduct.name }}</div>
          <div class="exchange-summary__points">消耗 {{ currentProduct.pointsPrice }} 积分</div>
        </div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收件人" prop="receiver">
          <el-input v-model="form.receiver" placeholder="请输入收件人姓名" maxlength="50" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="20" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input
            v-model="form.address"
            type="textarea"
            :rows="2"
            placeholder="请输入详细收货地址"
            maxlength="200"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmExchange">确认兑换</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getUserPoints as apiGetUserPoints, getProducts as apiGetProducts, exchangeProduct as apiExchangeProduct } from '@/api/points';

const userPoints = ref(0);
const products = ref([]);

// 收货信息弹窗
const RECEIVER_STORAGE_KEY = 'points-receiver-info';
const dialogVisible = ref(false);
const submitting = ref(false);
const formRef = ref(null);
const currentProduct = ref(null);
const form = reactive({
  receiver: '',
  phone: '',
  address: ''
});

const rules = {
  receiver: [{ required: true, message: '请输入收件人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
};

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
    products.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('获取商品列表失败:', error);
    ElMessage.error('获取商品列表失败');
    products.value = [];
  }
};

// 打开兑换弹窗，回填上次填写的收货信息
const openExchange = (product) => {
  currentProduct.value = product;
  try {
    const saved = JSON.parse(localStorage.getItem(RECEIVER_STORAGE_KEY) || '{}');
    form.receiver = saved.receiver || '';
    form.phone = saved.phone || '';
    form.address = saved.address || '';
  } catch {
    form.receiver = '';
    form.phone = '';
    form.address = '';
  }
  dialogVisible.value = true;
};

// 确认兑换（兑换成功后由后端生成物流订单，等待发货）
const confirmExchange = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    await apiExchangeProduct({
      productId: currentProduct.value.id,
      quantity: 1,
      receiver: form.receiver,
      phone: form.phone,
      address: form.address
    });
    localStorage.setItem(RECEIVER_STORAGE_KEY, JSON.stringify({
      receiver: form.receiver,
      phone: form.phone,
      address: form.address
    }));
    ElMessage.success('兑换成功，可在“兑换订单”中查看物流');
    dialogVisible.value = false;
    // 刷新数据
    getUserPoints();
    getProducts();
  } catch (error) {
    console.error('兑换商品失败:', error);
    ElMessage.error(error.response?.data?.message || error.response?.data?.msg || '兑换商品失败');
  } finally {
    submitting.value = false;
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
  color: var(--theme-text-1);
}

.user-points {
  background-color: var(--theme-bg-surface-2);
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: right;
}

.points-label {
  font-size: 16px;
  color: var(--theme-text-2);
  margin-right: 10px;
}

.points-value {
  font-size: 20px;
  font-weight: bold;
  color: var(--theme-primary);
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-item {
  border: 1px solid var(--theme-border);
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s;
  background-color: var(--theme-bg-surface);
}

.product-item:hover {
  box-shadow: var(--theme-shadow-sm);
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
  color: var(--theme-text-1);
}

.product-description {
  font-size: 14px;
  color: var(--theme-text-2);
  margin-bottom: 15px;
  line-height: 1.4;
}

.product-price {
  margin-bottom: 10px;
}

.price-label {
  font-size: 14px;
  color: var(--theme-text-2);
  margin-right: 10px;
}

.price-value {
  font-size: 16px;
  font-weight: bold;
  color: var(--theme-danger);
}

.product-stock {
  margin-bottom: 15px;
}

.stock-label {
  font-size: 14px;
  color: var(--theme-text-2);
  margin-right: 10px;
}

.stock-value {
  font-size: 14px;
  color: var(--theme-success);
}

.exchange-button {
  width: 100%;
  margin-top: 10px;
}

/* 兑换弹窗中的商品摘要 */
.exchange-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  margin-bottom: 18px;
  border-radius: 10px;
  background: var(--theme-warning-soft);
  border: 1px solid var(--theme-border);
}

.exchange-summary__img {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
  background: var(--theme-bg-surface);
}

.exchange-summary__info {
  min-width: 0;
}

.exchange-summary__name {
  font-size: 15px;
  font-weight: 600;
  color: var(--theme-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.exchange-summary__points {
  margin-top: 4px;
  font-size: 13px;
  color: var(--theme-warning);
  font-weight: 600;
}
</style>