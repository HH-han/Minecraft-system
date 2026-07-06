<template>
  <div class="news-container">
    <div class="section-header">
      <h2 class="section-title">热门新闻</h2>
      <p class="section-description">最新资讯与精彩故事</p>
    </div>

    <div class="news-grid">
      <div v-for="news in newsList" :key="news.id" class="news-card" @click="viewNewsDetail(news.id)">
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

    <div class="load-more">
      <button class="load-more-btn">查看更多新闻</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getHotNews } from '@/api/news.js';

const emit = defineEmits(['open-news-detail']);

const newsList = ref([]);
const loading = ref(false);
const error = ref('');

const fetchNews = async () => {
    loading.value = true;
    error.value = '';
    try {
        const response = await getHotNews({ limit: 6 });
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
.news-container {
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 40px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: -0.01em;
}

.section-description {
  font-size: 18px;
  color: #6e6e73;
  margin: 0;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.news-card {
  background: #ffffff;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94), box-shadow 0.3s;
}

.news-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.news-card:hover .card-image {
  transform: scale(1.05);
}

.category-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #1d1d1f;
}

.card-content {
  padding: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 12px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-description {
  font-size: 14px;
  line-height: 1.5;
  color: #6e6e73;
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
  padding-top: 16px;
  border-top: 1px solid #f5f5f7;
}

.author {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
}

.stats {
  display: flex;
  gap: 16px;
}

.view-count,
.like-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.load-more {
  text-align: center;
  margin-top: 48px;
}

.load-more-btn {
  background: transparent;
  border: 1px solid #d2d2d6;
  color: #1d1d1f;
  font-size: 14px;
  font-weight: 500;
  padding: 14px 32px;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.3s;
}

.load-more-btn:hover {
  background: #1d1d1f;
  color: #ffffff;
  border-color: #1d1d1f;
}

.loading {
  text-align: center;
  padding: 80px;
  color: #6e6e73;
}

.error {
  text-align: center;
  padding: 80px;
  color: #ff4757;
}

@media (max-width: 768px) {
  .section-title {
    font-size: 28px;
  }
  
  .news-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .news-card {
    border-radius: 20px;
  }
  
  .card-image-wrapper {
    height: 150px;
  }
  
  .card-content {
    padding: 16px;
  }
  
  .card-title {
    font-size: 16px;
  }
  
  .load-more-btn {
    padding: 12px 24px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .news-card {
    border-radius: 16px;
  }
  
  .card-image-wrapper {
    height: 140px;
  }
  
  .card-content {
    padding: 14px;
  }
  
  .card-title {
    font-size: 16px;
  }
  
  .card-description {
    font-size: 13px;
  }
}
</style>