<template>
  <div class="news-container">
    <div class="section-header">
      <h2 class="section-title">热门新闻</h2>
      <p class="section-description">最新资讯与精彩故事</p>
    </div>

    <div class="news-grid">
      <div
        v-for="(news, index) in displayedNews"
        :key="news.id"
        class="news-card"
        :class="{ 'news-card--expanded': isExpanded && index >= DEFAULT_DISPLAY_COUNT }"
        :style="{ animationDelay: `${Math.min(index, DEFAULT_DISPLAY_COUNT) * 0.05}s` }"
        @click="viewNewsDetail(news.id)"
      >
        <div class="card-image-wrapper">
          <img 
            :src="getCoverImage(news.coverImage)" 
            :alt="news.title"
            class="card-image"
            loading="lazy"
          />
          <div v-if="news.category" class="category-tag">{{ news.category }}</div>
        </div>
        <div class="card-content">
          <h3 class="card-title">{{ news.title }}</h3>
          <p class="card-description">{{ news.content || news.description || '' }}</p>
          <div class="card-meta">
            <span class="author">
              <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
              {{ news.source || '未知来源' }}
            </span>
            <div class="stats">
              <span class="view-count">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                </svg>
                {{ news.viewCount || 0 }}
              </span>
              <span class="like-count">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                </svg>
                {{ news.likeCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="hasMoreData" class="expand-actions">
      <button class="expand-btn" @click="toggleExpand">
        <span>{{ isExpanded ? '收起' : '展开全部' }}</span>
        <svg
          class="expand-arrow"
          :class="{ 'expand-arrow--up': isExpanded }"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <polyline points="6 9 12 15 18 9" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getHotNews } from '@/api/news.js';

const emit = defineEmits(['open-news-detail']);

// 默认展示的数据条数
const DEFAULT_DISPLAY_COUNT = 4;

const newsList = ref([]);
const loading = ref(false);
const error = ref('');
const isExpanded = ref(false);

// 默认仅展示前若干条，展开后显示全部
const displayedNews = computed(() => {
    if (isExpanded.value || newsList.value.length <= DEFAULT_DISPLAY_COUNT) {
        return newsList.value;
    }
    return newsList.value.slice(0, DEFAULT_DISPLAY_COUNT);
});

// 是否存在折叠数据（用于控制按钮显示）
const hasMoreData = computed(() => newsList.value.length > DEFAULT_DISPLAY_COUNT);

const toggleExpand = () => {
    isExpanded.value = !isExpanded.value;
};

const fetchNews = async () => {
    loading.value = true;
    error.value = '';
    try {
        const response = await getHotNews({ limit: 20 });
        if (response.code === 200 && response.data) {
            newsList.value = response.data;
        } else {
            error.value = '获取新闻失败';
        }
    } catch (err) {
        error.value = '网络错误，请稍后重试';
        console.error('获取新闻失败:', err);
    } finally {
        loading.value = false;
    }
};

const getCoverImage = (coverImage) => {
    if (!coverImage || coverImage.includes('via.placeholder.com')) {
        return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=news%20article%20placeholder%2C%20simple%20clean%20design&image_size=square';
    }
    return coverImage;
};

const viewNewsDetail = (newsId) => {
    emit('open-news-detail', newsId);
};

onMounted(() => {
    fetchNews();
});
</script>

<style scoped>
/* 深色电影感主题：设计变量由父级 .home-main 统一提供（main.vue） */
.news-container {
  margin: 0;
}

.section-header {
  text-align: left;
  margin-bottom: 28px;
}

.section-title {
  font-size: clamp(22px, 2.4vw, 30px);
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: -0.03em;
  color: var(--wi-txt-1, var(--theme-text-1));
}

.section-description {
  font-size: 14px;
  color: var(--wi-txt-2, var(--theme-text-2));
  margin: 0;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.news-card {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  opacity: 0;
  display: flex;
  flex-direction: column;
  transform: translateY(30px);
  animation: fadeInUp 0.6s ease-out forwards;
  background: var(--theme-bg-surface);
  border: 1px solid var(--wi-hairline, var(--theme-hairline));
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  transition: transform 0.5s cubic-bezier(0.22, 1, 0.36, 1),
    border-color 0.5s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.5s cubic-bezier(0.22, 1, 0.36, 1);
}

/* 顶部玻璃高光 */
.news-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 1px;
  z-index: 2;
  background: linear-gradient(90deg, transparent, var(--theme-border-strong), transparent);
  opacity: 0.7;
  pointer-events: none;
}

.news-card:hover {
  transform: translateY(-6px);
  border-color: var(--theme-primary);
  box-shadow: var(--theme-shadow-lg), 0 0 0 1px var(--theme-primary-soft);
}

/* 展开时新增卡片使用更轻量的淡入动画 */
.news-card--expanded {
  animation: fadeInExpand 0.5s ease-out forwards;
}

@keyframes fadeInExpand {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  height: 172px;
  overflow: hidden;
}

/* 图片底部压暗，向卡片底色过渡 */
.card-image-wrapper::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 45%, var(--theme-bg-glass) 100%);
  pointer-events: none;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.22, 1, 0.36, 1);
}

.news-card:hover .card-image {
  transform: scale(1.06);
}

.category-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 1;
  padding: 5px 12px;
  border-radius: 980px;
  font-size: 11.5px;
  font-weight: 600;
  letter-spacing: 0.04em;
  color: var(--wi-txt-1, var(--theme-text-1));
  background: var(--theme-bg-glass-strong);
  border: 1px solid var(--theme-border);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.card-content {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding: 20px;
}

.card-title {
  font-size: 16.5px;
  font-weight: 600;
  line-height: 1.4;
  margin: 0 0 10px;
  color: var(--wi-txt-1, var(--theme-text-1));
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-description {
  font-size: 13.5px;
  line-height: 1.65;
  color: var(--wi-txt-2, var(--theme-text-2));
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: auto;
  padding-top: 14px;
  border-top: 1px solid var(--theme-hairline);
}

.author {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12.5px;
  color: var(--wi-txt-3, var(--theme-text-3));
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.stats {
  display: flex;
  gap: 14px;
  flex-shrink: 0;
}

.view-count,
.like-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--wi-txt-3, var(--theme-text-3));
}

.expand-actions {
  display: flex;
  justify-content: center;
  margin-top: 36px;
}

.expand-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 13px 34px;
  border-radius: 980px;
  font-size: 14.5px;
  font-weight: 500;
  color: var(--wi-txt-1, var(--theme-text-1));
  background: var(--theme-bg-surface);
  border: 1px solid var(--wi-hairline-2, var(--theme-border-strong));
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
}

.expand-btn:hover {
  transform: translateY(-2px);
  background: var(--theme-primary-soft);
  border-color: var(--theme-primary);
  box-shadow: var(--theme-shadow-md);
}

.expand-btn:active {
  transform: translateY(0);
}

.expand-arrow {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.expand-arrow--up {
  transform: rotate(180deg);
}

.loading {
  text-align: center;
  padding: 60px 0;
  color: var(--wi-txt-2, var(--theme-text-2));
}

.error {
  text-align: center;
  padding: 60px 0;
  color: var(--theme-danger);
}

@media (max-width: 768px) {
  .news-grid {
    grid-template-columns: 1fr;
  }

  .card-image-wrapper {
    height: 180px;
  }

  .expand-actions {
    margin-top: 28px;
  }
}
</style>
