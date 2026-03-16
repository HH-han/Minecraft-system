<template>
  <div class="product-detail">
    <!-- 商品图片区域 -->
    <div class="product-image-section">
      <div class="main-image">
        <img :src="product.images[0]" :alt="product.name" />
      </div>
      <div class="thumbnail-images">
        <div 
          v-for="(image, index) in product.images" 
          :key="index"
          class="thumbnail-item"
          :class="{ active: activeImageIndex === index }"
          @click="activeImageIndex = index"
        >
          <img :src="image" :alt="product.name" />
        </div>
      </div>
    </div>

    <!-- 商品信息区域 -->
    <div class="product-info-section">
      <h1 class="product-name">{{ product.name }}</h1>
      <div class="product-price">
        <span class="price-label">价格：</span>
        <span class="price-value">{{ product.price }}元</span>
        <span v-if="product.originalPrice" class="original-price">{{ product.originalPrice }}元</span>
      </div>
      <div class="product-promotion" v-if="product.promotion">
        <span class="promotion-tag">促销</span>
        <span class="promotion-content">{{ product.promotion }}</span>
      </div>

      <!-- 商品类型选择 -->
      <div class="product-options">
        <div class="option-group" v-for="option in product.options" :key="option.name">
          <div class="option-label">{{ option.name }}：</div>
          <div class="option-values">
            <div 
              v-for="value in option.values" 
              :key="value.value"
              class="option-value"
              :class="{ active: selectedOptions[option.name] === value.value }"
              @click="selectOption(option.name, value.value)"
            >
              {{ value.label }}
            </div>
          </div>
        </div>
      </div>

      <!-- 购买数量 -->
      <div class="quantity-section">
        <div class="quantity-label">数量：</div>
        <div class="quantity-control">
          <button class="quantity-btn" @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
          <input type="number" v-model.number="quantity" min="1" class="quantity-input" />
          <button class="quantity-btn" @click="increaseQuantity">+</button>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="add-to-cart-btn" @click="addToCart">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/>
          </svg>
          加入购物车
        </button>
        <button class="buy-now-btn" @click="buyNow">
          立即购买
        </button>
      </div>
    </div>

    <!-- 商品详情标签页 -->
    <div class="product-tabs">
      <div class="tab-header">
        <div 
          v-for="tab in tabs" 
          :key="tab.id"
          class="tab-item"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.name }}
        </div>
      </div>
      <div class="tab-content">
        <!-- 商品详情 -->
        <div v-if="activeTab === 'detail'" class="tab-panel">
          <div v-for="(detail, index) in product.details" :key="index" class="detail-item">
            <h3>{{ detail.title }}</h3>
            <div v-if="detail.images" class="detail-images">
              <img v-for="(img, imgIndex) in detail.images" :key="imgIndex" :src="img" :alt="detail.title" />
            </div>
            <p v-if="detail.content">{{ detail.content }}</p>
          </div>
        </div>

        <!-- 用户评价 -->
        <div v-if="activeTab === 'reviews'" class="tab-panel">
          <div class="review-summary">
            <div class="overall-rating">
              <span class="rating-label">综合评分：</span>
              <span class="rating-value">{{ product.overallRating }}</span>
              <div class="rating-stars">
                <svg v-for="i in 5" :key="i" viewBox="0 0 24 24" width="16" height="16" :fill="i <= Math.floor(product.overallRating) ? '#FFD700' : '#E0E0E0'">
                  <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                </svg>
              </div>
            </div>
            <div class="rating-distribution">
              <div v-for="(count, rating) in product.ratingDistribution" :key="rating" class="rating-item">
                <span class="rating-level">{{ rating }}星</span>
                <div class="rating-bar">
                  <div class="rating-fill" :style="{ width: (count / product.totalReviews * 100) + '%' }"></div>
                </div>
                <span class="rating-count">{{ count }}</span>
              </div>
            </div>
          </div>

          <div class="review-list">
            <div v-for="review in product.reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <span class="reviewer-name">{{ review.username }}</span>
                <div class="review-rating">
                  <svg v-for="i in 5" :key="i" viewBox="0 0 24 24" width="14" height="14" :fill="i <= review.rating ? '#FFD700' : '#E0E0E0'">
                    <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                  </svg>
                </div>
                <span class="review-date">{{ review.date }}</span>
              </div>
              <div class="review-content">{{ review.content }}</div>
              <div class="review-images" v-if="review.images">
                <img v-for="(img, imgIndex) in review.images" :key="imgIndex" :src="img" :alt="review.content" />
              </div>
            </div>
          </div>
        </div>

        <!-- 规格参数 -->
        <div v-if="activeTab === 'specs'" class="tab-panel">
          <div class="specs-table">
            <div v-for="spec in product.specs" :key="spec.name" class="spec-item">
              <div class="spec-name">{{ spec.name }}</div>
              <div class="spec-value">{{ spec.value }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();

// 商品数据
const product = ref({
  id: 1,
  name: '小米 17 Pro 妙享穹顶 徕卡光影大师 第五代骁龙 8 至尊版 白色 12GB+256GB 5G手机',
  price: 4499.1,
  originalPrice: 4999,
  promotion: '政府补贴，限时优惠500元',
  images: [
    'https://img1.miaoshoucdn.com/imgextra/i4/2210585684133675168/TB2a1V2a7CWBuNjy0FhXXbfnpXa_!!2210585684133675168.jpg',
    'https://img1.miaoshoucdn.com/imgextra/i3/2210585684133675168/TB2b5X2a7CWBuNjy0FhXXbfnpXa_!!2210585684133675168.jpg',
    'https://img1.miaoshoucdn.com/imgextra/i2/2210585684133675168/TB2c6X2a7CWBuNjy0FhXXbfnpXa_!!2210585684133675168.jpg'
  ],
  options: [
    {
      name: '颜色',
      values: [
        { label: '白色', value: 'white' },
        { label: '黑色', value: 'black' },
        { label: '紫色', value: 'purple' }
      ]
    },
    {
      name: '存储',
      values: [
        { label: '12GB+256GB', value: '12_256' },
        { label: '12GB+512GB', value: '12_512' },
        { label: '16GB+1TB', value: '16_1024' }
      ]
    }
  ],
  details: [
    {
      title: '产品介绍',
      content: '小米17 Pro是小米公司推出的旗舰智能手机，搭载第五代骁龙8至尊版处理器，采用徕卡光影大师影像系统，拥有出色的拍照能力和流畅的性能体验。'
    },
    {
      title: '核心卖点',
      content: '1. 第五代骁龙8至尊版处理器\n2. 徕卡光影大师影像系统\n3. 6.3英寸黄金尺寸屏幕\n4. 6300mAh大电池+100W快充\n5. 轻薄机身设计'
    }
  ],
  specs: [
    { name: '品牌', value: '小米' },
    { name: '型号', value: '17 Pro' },
    { name: '屏幕尺寸', value: '6.3英寸' },
    { name: '处理器', value: '第五代骁龙8至尊版' },
    { name: '电池容量', value: '6300mAh' },
    { name: '充电功率', value: '100W有线快充' }
  ],
  overallRating: 4.8,
  totalReviews: 100000,
  ratingDistribution: {
    5: 80000,
    4: 15000,
    3: 3000,
    2: 1500,
    1: 500
  },
  reviews: [
    {
      id: 1,
      username: '黑***火',
      rating: 5,
      date: '2024-01-15',
      content: '小米17 Pro完美兼顾手感与旗舰配置，6.3英寸黄金尺寸握持感一流，轻薄机身却塞下大电池与强散热。屏幕清晰通透、高刷丝滑；性能满血输出，日常与游戏都稳如泰山。徕卡全焦段影像实力强悍，逆光夜景表现顶级，续航充电双狂欢，妙享穹顶提升体验，颜值与实力并存，绝对是年度真香旗舰！',
      images: [
        'https://img1.miaoshoucdn.com/imgextra/i4/2210585684133675168/TB2a1V2a7CWBuNjy0FhXXbfnpXa_!!2210585684133675168.jpg'
      ]
    },
    {
      id: 2,
      username: 'jd_1o9d2et7qt5ay6',
      rating: 5,
      date: '2024-01-10',
      content: '小米旗舰果然名不虚传！6.3英寸+192g手感绝了，单手操作毫无压力。骁龙8至尊版性能强劲，澎湃OS 3.0流畅丝滑，6300mAh大电池+100W快充，重度使用一天也毫无焦虑，日常通勤足够了。包装完好，发货快。最惊喜的是妙享穹顶，扫码、切歌、用后置主摄自拍超方便，还能...',
      images: [
        'https://img1.miaoshoucdn.com/imgextra/i3/2210585684133675168/TB2b5X2a7CWBuNjy0FhXXbfnpXa_!!2210585684133675168.jpg'
      ]
    }
  ]
});

// 状态
const activeImageIndex = ref(0);
const activeTab = ref('detail');
const quantity = ref(1);
const selectedOptions = ref({});

// 标签页
const tabs = [
  { id: 'detail', name: '商品详情' },
  { id: 'reviews', name: '用户评价' },
  { id: 'specs', name: '规格参数' }
];

// 选择选项
const selectOption = (optionName, value) => {
  selectedOptions.value[optionName] = value;
};

// 增加数量
const increaseQuantity = () => {
  quantity.value++;
};

// 减少数量
const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

// 加入购物车
const addToCart = () => {
  // 检查是否选择了所有选项
  const allOptionsSelected = product.value.options.every(option => selectedOptions.value[option.name]);
  if (!allOptionsSelected) {
    ElMessage.warning('请选择商品规格');
    return;
  }

  // 模拟加入购物车
  ElMessage.success('已加入购物车');
  console.log('加入购物车:', {
    productId: product.value.id,
    quantity: quantity.value,
    options: selectedOptions.value
  });
};

// 立即购买
const buyNow = () => {
  // 检查是否选择了所有选项
  const allOptionsSelected = product.value.options.every(option => selectedOptions.value[option.name]);
  if (!allOptionsSelected) {
    ElMessage.warning('请选择商品规格');
    return;
  }

  // 跳转到订单确认页面
  router.push({
    path: '/payment',
    query: {
      item: JSON.stringify({
        id: product.value.id,
        name: product.value.name,
        price: product.value.price,
        quantity: quantity.value,
        ticketType: '手机',
        options: selectedOptions.value
      })
    }
  });
};

// 初始化默认选项
onMounted(() => {
  // 设置默认选项
  product.value.options.forEach(option => {
    selectedOptions.value[option.name] = option.values[0].value;
  });
});
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

/* 商品图片区域 */
.product-image-section {
  display: flex;
  margin-bottom: 30px;
}

.main-image {
  flex: 1;
  max-width: 500px;
  margin-right: 20px;
}

.main-image img {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.thumbnail-images {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 80px;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border: 2px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.thumbnail-item.active {
  border-color: #ff6700;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息区域 */
.product-info-section {
  margin-bottom: 40px;
}

.product-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
}

.product-price {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.price-label {
  font-size: 14px;
  color: #666;
}

.price-value {
  font-size: 28px;
  font-weight: 600;
  color: #ff6700;
  margin-right: 10px;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.product-promotion {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
  background-color: #fff3e0;
  border-radius: 4px;
}

.promotion-tag {
  background-color: #ff6700;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 2px;
  margin-right: 8px;
}

.promotion-content {
  font-size: 14px;
  color: #ff6700;
}

/* 商品选项 */
.product-options {
  margin-bottom: 20px;
}

.option-group {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.option-label {
  width: 80px;
  font-size: 14px;
  color: #666;
  margin-right: 10px;
  padding-top: 5px;
}

.option-values {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-value {
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.option-value:hover {
  border-color: #ff6700;
}

.option-value.active {
  border-color: #ff6700;
  background-color: #fff3e0;
  color: #ff6700;
}

/* 数量控制 */
.quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.quantity-label {
  width: 80px;
  font-size: 14px;
  color: #666;
  margin-right: 10px;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.quantity-btn {
  width: 30px;
  height: 30px;
  border: none;
  background-color: #f5f5f5;
  cursor: pointer;
  font-size: 16px;
}

.quantity-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.quantity-input {
  width: 60px;
  height: 30px;
  border: none;
  text-align: center;
  font-size: 14px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 15px;
}

.add-to-cart-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 12px 30px;
  border: 1px solid #ff6700;
  background-color: #fff;
  color: #ff6700;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
}

.add-to-cart-btn:hover {
  background-color: #fff3e0;
}

.buy-now-btn {
  padding: 12px 30px;
  border: none;
  background-color: #ff6700;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
}

.buy-now-btn:hover {
  background-color: #e05a00;
}

/* 标签页 */
.product-tabs {
  border-top: 1px solid #e0e0e0;
  margin-top: 40px;
}

.tab-header {
  display: flex;
  border-bottom: 1px solid #e0e0e0;
}

.tab-item {
  padding: 15px 30px;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
}

.tab-item:hover {
  color: #ff6700;
}

.tab-item.active {
  color: #ff6700;
  border-bottom-color: #ff6700;
}

.tab-panel {
  padding: 20px;
}

/* 商品详情 */
.detail-item {
  margin-bottom: 30px;
}

.detail-item h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
}

.detail-images {
  margin-bottom: 15px;
}

.detail-images img {
  width: 100%;
  height: auto;
  margin-bottom: 10px;
  border-radius: 4px;
}

.detail-item p {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

/* 用户评价 */
.review-summary {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.overall-rating {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.rating-label {
  font-size: 16px;
  color: #333;
  margin-right: 10px;
}

.rating-value {
  font-size: 24px;
  font-weight: 600;
  color: #ff6700;
  margin-right: 10px;
}

.rating-stars {
  display: flex;
  gap: 2px;
}

.rating-distribution {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rating-item {
  display: flex;
  align-items: center;
}

.rating-level {
  width: 40px;
  font-size: 14px;
  color: #666;
}

.rating-bar {
  flex: 1;
  height: 8px;
  background-color: #e0e0e0;
  border-radius: 4px;
  margin: 0 10px;
  overflow: hidden;
}

.rating-fill {
  height: 100%;
  background-color: #ff6700;
  border-radius: 4px;
}

.rating-count {
  width: 60px;
  font-size: 14px;
  color: #999;
  text-align: right;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.reviewer-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-right: 10px;
}

.review-rating {
  display: flex;
  gap: 2px;
  margin-right: 10px;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin-bottom: 10px;
}

.review-images {
  display: flex;
  gap: 10px;
}

.review-images img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

/* 规格参数 */
.specs-table {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.spec-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
}

.spec-name {
  width: 120px;
  font-size: 14px;
  color: #666;
}

.spec-value {
  flex: 1;
  font-size: 14px;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-image-section {
    flex-direction: column;
  }
  
  .main-image {
    max-width: 100%;
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .thumbnail-images {
    flex-direction: row;
    width: 100%;
  }
  
  .thumbnail-item {
    flex: 1;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .add-to-cart-btn,
  .buy-now-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>