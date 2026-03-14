<template>
    <div class="world-liquid-glass-container">
        <header class="world-page-header">
            <h1>全球特色地点探索</h1>
            <p class="world-subtitle">穿越时空的文化与自然奇观</p>
        </header>

        <div class="world-filter-controls">
            <button v-for="category in categories" :key="category" @click="filterByCategory(category)"
                :class="{ 'world-active': activeCategory === category }">
                {{ category }}
            </button>
        </div>

        <div v-if="loading" class="world-loading">
            <div class="world-loading-spinner"></div>
            <p>加载中...</p>
        </div>

        <div v-else class="world-locations-grid">
            <div v-for="location in filteredLocations" :key="location.id" class="world-location-card"
                :style="{ '--card-accent': location.color || '#3a7bd5' }">
                <div class="world-card-image">
                    <img :src="location.image || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Minecraft%20world%20landscape%20with%20beautiful%20scenery&image_size=landscape_16_9'" :alt="location.name" />
                    <div class="world-image-overlay"></div>
                </div>
                <div class="world-card-content">
                    <div class="world-card-header">
                        <h2>{{ location.name }}</h2>
                        <span class="world-location-country">{{ location.country }}</span>
                    </div>
                    <div class="world-card-tags">
                        <span v-for="tag in splitTags(location.tags)" :key="tag" class="world-tag">{{ tag }}</span>
                    </div>
                    <p class="world-card-description">{{ location.description || '暂无描述' }}</p>
                    <div class="world-card-details">
                        <div class="world-detail-item">
                            <span class="world-detail-label">特色</span>
                            <p>{{ location.features || '暂无信息' }}</p>
                        </div>
                        <div class="world-detail-item">
                            <span class="world-detail-label">历史</span>
                            <p>{{ location.history || '暂无信息' }}</p>
                        </div>
                        <div class="world-detail-item">
                            <span class="world-detail-label">人文</span>
                            <p>{{ location.culture || '暂无信息' }}</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div v-if="filteredLocations.length === 0" class="world-empty">
                <p>暂无数据</p>
            </div>
        </div>
    </div>
    <!-- 分页 -->
    <div class="block-world">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="total" />
    </div>

</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import {
  getWorldCharacteristicsList,
  getWorldCharacteristicsPage,
  getWorldCharacteristicsByCategory
} from '@/api/worldcharacteristics';

// 数据状态
const locations = ref([]);
const categories = ref(['全部', '建筑', '自然景观', '文物', '文化遗产']);
const activeCategory = ref('全部');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 计算过滤后的地点
const filteredLocations = computed(() => {
  return locations.value;
});

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const response = await getWorldCharacteristicsPage(currentPage.value, pageSize.value);
    if (response.code === 200) {
      locations.value = response.data.records;
      total.value = response.data.total;
    }
  } catch (error) {
    console.error('加载数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 按分类过滤
const filterByCategory = async (category) => {
  activeCategory.value = category;
  loading.value = true;
  try {
    if (category === '全部') {
      const response = await getWorldCharacteristicsList();
      if (response.code === 200) {
        locations.value = response.data;
        total.value = response.data.length;
      }
    } else {
      const response = await getWorldCharacteristicsByCategory(category);
      if (response.code === 200) {
        locations.value = response.data;
        total.value = response.data.length;
      }
    }
  } catch (error) {
    console.error('过滤数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  loadData();
};

const handleCurrentChange = (current) => {
  currentPage.value = current;
  loadData();
};

// 分割标签
const splitTags = (tags) => {
  if (!tags) return [];
  return tags.split(',').map(tag => tag.trim());
};

// 组件挂载时加载数据
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.world-liquid-glass-container {
    width: 100%;
    margin: 0 auto;
    padding: 2rem;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    color: #333;
}

.world-page-header {
    text-align: center;
    margin-bottom: 3rem;
}

.world-page-header h1 {
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
    background: linear-gradient(90deg, #3a7bd5, #00d2ff);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
}

.world-subtitle {
    font-size: 1.2rem;
    color: #666;
}

.world-filter-controls {
    display: flex;
    justify-content: center;
    gap: 1rem;
    margin-bottom: 2rem;
}

.world-filter-controls button {
    padding: 0.5rem 1.5rem;
    border: none;
    border-radius: 2rem;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    color: #333;
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 500;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.world-filter-controls button:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
}

.world-filter-controls button.world-active {
    background: linear-gradient(90deg, #3a7bd5, #00d2ff);
    color: white;
}

.world-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4rem;
    color: #666;
}

.world-loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(58, 123, 213, 0.2);
    border-left-color: #3a7bd5;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.world-empty {
    grid-column: 1 / -1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 4rem;
    color: #666;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 1rem;
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.world-locations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 2rem;
}

.world-location-card {
    width: 100%;
    min-width: 350px;
    border-radius: 1rem;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.world-location-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.world-card-image {
    position: relative;
    height: 600px;
    overflow: hidden;
}

.world-card-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.world-location-card:hover .world-card-image img {
    transform: scale(1.05);
}

.world-image-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, transparent 60%, rgba(0, 0, 0, 0.7));
}

.world-card-content {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 20px;
    background-color: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(5px);
    opacity: 1;
    transition: all 0.4s ease;
}

.world-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    position: relative;
}

.world-card-header::after {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 0;
    width: 100%;
    height: 2px;
    background: linear-gradient(90deg, var(--card-accent), transparent);
}

.world-card-header h2 {
    font-size: 1.5rem;
    font-weight: 700;
    margin: 0;
    color: #222;
}

.world-location-country {
    background: var(--card-accent);
    color: white;
    padding: 0.2rem 0.8rem;
    border-radius: 1rem;
    font-size: 0.8rem;
    font-weight: 600;
}

.world-card-tags {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
    margin-bottom: 1rem;
}

.world-tag {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(5px);
    padding: 0.2rem 0.8rem;
    border-radius: 1rem;
    font-size: 0.8rem;
    color: #343434;
}

.world-card-description {
    color: #343434;
    margin-bottom: 1.5rem;
    line-height: 1.6;
}

.world-card-details {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
    gap: 1rem;
}

.world-detail-item {
    background: rgba(255, 255, 255, 0.3);
    padding: 0.8rem;
    border-radius: 0.5rem;
    backdrop-filter: blur(5px);
}

.world-detail-label {
    display: block;
    font-weight: 600;
    margin-bottom: 0.3rem;
    color: var(--card-accent);
}

.world-detail-item p {
    margin: 0;
    font-size: 0.9rem;
    color: #343434;
    line-height: 1.5;
}

.block-world{
    display: flex;
    justify-content: center;
    margin-bottom: 40px;
}
@media (max-width: 768px) {
    .world-locations-grid {
        grid-template-columns: 1fr;
    }

    .world-filter-controls {
        flex-wrap: wrap;
    }
}
</style>