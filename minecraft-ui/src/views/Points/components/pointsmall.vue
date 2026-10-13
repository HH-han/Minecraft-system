<template>
  <div class="points-container">
    <h2 class="points-title">积分商城</h2>

    <div class="user-points">
      <span class="points-icon"><el-icon :size="22"><GoldMedal /></el-icon></span>
      <div class="points-meta">
        <span class="points-label">我的积分</span>
        <span class="points-value">{{ userPoints }}</span>
      </div>
      <span class="points-hint">攒积分，兑好物</span>
    </div>

    <div v-if="products.length" class="product-list">
      <article
        v-for="product in products"
        :key="product.id"
        class="product-item"
        :class="{ 'is-out': product.stock <= 0 }"
      >
        <div class="product-media">
          <img :src="product.imageUrl" alt="商品图片" class="product-image">
          <span class="product-badge">
            <el-icon><GoldMedal /></el-icon>
            <span class="price-value">{{ product.pointsPrice }}</span>
          </span>
          <span v-if="product.stock <= 0" class="product-soldout">已售罄</span>
        </div>
        <div class="product-body">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-description">{{ product.description }}</p>
          <div class="product-meta">
            <span class="product-stock">库存 {{ product.stock }}</span>
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
      </article>
    </div>

    <div v-else class="product-empty">
      <div class="product-empty__icon"><el-icon :size="34"><GoldMedal /></el-icon></div>
      <p class="product-empty__title">暂无可兑换商品</p>
      <p class="product-empty__desc">商城正在补货中，稍后再来看看吧</p>
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
import { GoldMedal } from '@element-plus/icons-vue';
import { getUserPoints as apiGetUserPoints, getProducts as apiGetProducts, exchangeProduct as apiExchangeProduct } from '@/api/points';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';

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

// 数据变更后自动刷新
useAutoRefresh(getUserPoints);
useAutoRefresh(getProducts);

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
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 19px;
  font-weight: 800;
  letter-spacing: -0.01em;
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--theme-hairline);
  color: var(--theme-text-1);
}

/* 我的积分条 */
.user-points {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  border-radius: 16px;
  margin-bottom: 22px;
  overflow: hidden;
  background:
    linear-gradient(120deg, color-mix(in srgb, var(--theme-warning) 16%, transparent), transparent 66%),
    var(--theme-bg-surface-2);
  border: 1px solid color-mix(in srgb, var(--theme-warning) 28%, transparent);
}

.points-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  border-radius: 13px;
  color: #fff;
  background: linear-gradient(135deg, color-mix(in srgb, var(--theme-warning) 62%, var(--theme-bg-app)) 0%, var(--theme-warning) 100%);
  box-shadow: 0 8px 18px -10px color-mix(in srgb, var(--theme-warning) 80%, transparent);
}

.points-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.points-label {
  font-size: 12.5px;
  font-weight: 500;
  letter-spacing: 0.02em;
  color: var(--theme-text-2);
}

.points-value {
  font-size: 24px;
  font-weight: 800;
  line-height: 1.15;
  letter-spacing: -0.02em;
  font-variant-numeric: tabular-nums;
  color: var(--theme-warning);
}

.points-hint {
  margin-left: auto;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

/* 商品网格 */
.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.product-item {
  display: flex;
  flex-direction: column;
  border: 1px solid var(--theme-border);
  border-radius: 16px;
  overflow: hidden;
  background-color: var(--theme-bg-surface);
  transition:
    transform 0.3s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.3s cubic-bezier(0.22, 1, 0.36, 1),
    border-color 0.3s ease;
}

.product-item:hover {
  transform: translateY(-4px);
  border-color: color-mix(in srgb, var(--theme-warning) 50%, transparent);
  box-shadow: var(--theme-shadow-md);
}

.product-media {
  position: relative;
  overflow: hidden;
}

.product-image {
  display: block;
  width: 100%;
  height: 190px;
  object-fit: cover;
  transition: transform 0.45s cubic-bezier(0.22, 1, 0.36, 1);
}

.product-item:hover .product-image {
  transform: scale(1.05);
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, color-mix(in srgb, var(--theme-warning) 62%, var(--theme-bg-app)) 0%, var(--theme-warning) 100%);
  box-shadow: 0 6px 16px -8px rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
}

.product-badge .price-value {
  font-variant-numeric: tabular-nums;
}

.product-soldout {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #fff;
  background: color-mix(in srgb, var(--theme-text-1) 55%, transparent);
}

.product-item.is-out .product-image {
  filter: grayscale(0.6);
}

.product-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding: 16px 16px 18px;
}

.product-name {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: -0.01em;
  margin: 0;
  color: var(--theme-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-description {
  margin: 8px 0 0;
  font-size: 13px;
  line-height: 1.55;
  color: var(--theme-text-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  align-items: center;
  margin: 14px 0;
  padding-top: 14px;
  border-top: 1px dashed var(--theme-hairline);
}

.product-stock {
  font-size: 12.5px;
  font-weight: 500;
  color: var(--theme-text-3);
}

.exchange-button {
  width: 100%;
  margin-top: auto;
  border: none;
  background: linear-gradient(135deg, color-mix(in srgb, var(--theme-warning) 68%, var(--theme-bg-app)) 0%, var(--theme-warning) 100%);
}

.exchange-button:not(.is-disabled):hover,
.exchange-button:not(.is-disabled):focus {
  background: linear-gradient(135deg, color-mix(in srgb, var(--theme-warning) 84%, var(--theme-bg-app)) 0%, var(--theme-warning) 100%);
}

/* 空状态 */
.product-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 64px 20px;
  text-align: center;
}

.product-empty__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  margin-bottom: 6px;
  border-radius: 50%;
  color: var(--theme-warning);
  background: var(--theme-warning-soft);
}

.product-empty__title {
  margin: 6px 0 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.product-empty__desc {
  margin: 0;
  font-size: 13.5px;
  color: var(--theme-text-2);
}

/* 兑换弹窗中的商品摘要 */
.exchange-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  margin-bottom: 18px;
  border-radius: 12px;
  background:
    linear-gradient(120deg, color-mix(in srgb, var(--theme-warning) 12%, transparent), transparent 70%),
    var(--theme-bg-surface-2);
  border: 1px solid color-mix(in srgb, var(--theme-warning) 24%, transparent);
}

.exchange-summary__img {
  width: 52px;
  height: 52px;
  border-radius: 10px;
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