<template>
    <!-- 新闻卡片 -->
    <section class="content-section">
        <h2 class="section-title">热门新闻</h2>
        <div v-if="loading.news" class="loading">加载中...</div>
        <div v-else-if="error.news" class="error">{{ error.news }}</div>
        <div v-else class="card-grid">
            <div v-for="news in newsList" :key="news.id" class="news-card" @click="viewNewsDetail(news.id)">
                <!-- 卡片内容 -->
                <div class="card-content">
                    <!-- 图片区域 (模拟新闻图) -->
                    <div class="image-section">
                        <div class="category-tag" v-if="news.category">{{ news.category }}</div>
                        <img 
                            :src="getCoverImage(news.coverImage)" 
                            :alt="news.title"
                            class="news-image"
                            loading="lazy"
                        >
                    </div>

                    <!-- 文本区域 -->
                    <div class="text-section">
                        <h3 class="news-title">{{ news.title }}</h3>
                        <p class="news-description">{{ news.content || news.description || '' }}</p>

                        <!-- 元信息：作者 & 时间 & 统计数据 -->
                        <div class="meta-info">
                            <span class="author">
                                <svg class="icon" viewBox="0 0 24 24" width="16" height="16">
                                    <path
                                        d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"
                                        fill="currentColor" />
                                </svg>
                                {{ news.source || '未知作者' }}
                            </span>
                            <div class="stats">
                                <span class="view-count">
                                    <svg class="icon" viewBox="0 0 24 24" width="14" height="14">
                                        <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" fill="currentColor"/>
                                    </svg>
                                    {{ news.viewCount || 0 }}
                                </span>
                                <span class="like-count">
                                    <svg class="icon" viewBox="0 0 24 24" width="14" height="14">
                                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="currentColor"/>
                                    </svg>
                                    {{ news.likeCount || 0 }}
                                </span>
                                <span class="comment-count">
                                    <svg class="icon" viewBox="0 0 24 24" width="14" height="14">
                                        <path d="M21.99 4c0-1.1-.89-2-1.99-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18zM18 14H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z" fill="currentColor"/>
                                    </svg>
                                    {{ news.commentCount || 0 }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getHotNews, getNewsDetail } from '@/api/news.js';

// 响应式数据
const newsList = ref([]);

// 加载状态
const loading = ref({
    news: false,
});

// 错误信息
const error = ref({
    news: '',
});

// 模态框状态
const modalVisible = ref(false);
const modalType = ref('');
const modalData = ref({});
const modalLoading = ref(false);
const modalError = ref('');

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
};

// 获取新闻数据
const fetchNews = async () => {
    loading.value.news = true;
    error.value.news = '';
    try {
        const response = await getHotNews({ limit: 6 });
        if (response.code === 200 && response.data) {
            newsList.value = response.data;
        } else {
            error.value.news = '获取新闻失败';
        }
    } catch (err) {
        error.value.news = '网络错误，请稍后重试';
        console.error('获取新闻失败:', err);
    } finally {
        loading.value.news = false;
    }
};

// 获取有效封面图片URL
const getCoverImage = (coverImage) => {
    if (!coverImage || coverImage.includes('via.placeholder.com')) {
        return 'https://placehold.co/400x300?text=No+Image';
    }
    return coverImage;
};

// 查看新闻详情
const viewNewsDetail = (newsId) => {
    console.log('查看新闻详情:', newsId);
    // 这里可以添加查看详情的逻辑，比如打开模态框或跳转到详情页
};

// 组件挂载时获取数据
onMounted(() => {
    fetchNews();
});
</script>

<style scoped>
.content-section {
    margin-bottom: 40px;
}

.section-title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 20px;
    color: #1d1d1f;
    border-left: 4px solid #2997ff;
    padding-left: 12px;
}

.card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 24px;
}

/* 卡片容器 */
.news-card {
    width: 100%;
    background: #ffffff;
    border: 1px solid #d2d2d6;
    border-radius: 24px;
    overflow: hidden;
    transition: transform 0.2s, box-shadow 0.2s;
    cursor: pointer;
}

.news-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0,0,0,0.1);
}

/* 内容区域 */
.card-content {
    display: flex;
    flex-direction: column;
    height: 100%;
}

/* 图片区域 */
.image-section {
    position: relative;
    width: 100%;
    height: 180px;
    overflow: hidden;
    border-bottom: 1px solid #d2d2d6;
}

.news-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 0.3s ease;
}

.news-card:hover .news-image {
    transform: scale(1.05);
}

/* 分类标签 */
.category-tag {
    position: absolute;
    top: 16px;
    left: 16px;
    background: #2997ff;
    color: white;
    padding: 4px 12px;
    border-radius: 30px;
    font-size: 12px;
    font-weight: 600;
    z-index: 2;
}

/* 文本区域 */
.text-section {
    padding: 20px 18px 22px;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.news-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    line-height: 1.4;
    color: #1d1d1f;
}

.news-description {
    margin: 0;
    font-size: 14px;
    line-height: 1.5;
    color: #6e6e73;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
}

/* 元信息区域 */
.meta-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 8px;
    font-size: 12px;
    color: #6e6e73;
    border-top: 1px solid #d2d2d6;
    padding-top: 12px;
}

.author,
.stats {
    display: flex;
    align-items: center;
    gap: 4px;
}

.stats {
    gap: 12px;
}

.view-count,
.like-count,
.comment-count {
    display: flex;
    align-items: center;
    gap: 2px;
    font-size: 12px;
    color: #6e6e73;
}

.icon {
    opacity: 0.8;
    color: #6e6e73;
}

/* 加载和错误状态 */
.loading {
    text-align: center;
    padding: 40px;
    color: #6e6e73;
}

.error {
    text-align: center;
    padding: 40px;
    color: #ff4d4f;
}

/* 响应式微调 */
@media (max-width: 768px) {
    .card-grid {
        grid-template-columns: 1fr;
        gap: 20px;
    }
    
    .section-title {
        font-size: 20px;
    }
    
    .news-card {
        border-radius: 20px;
    }
    
    .text-section {
        padding: 16px 14px 18px;
    }
    
    .news-title {
        font-size: 16px;
    }
    
    .image-section {
        height: 150px;
    }
}

@media (max-width: 480px) {
    .news-card {
        border-radius: 16px;
    }
    
    .text-section {
        padding: 14px 12px 16px;
    }
    
    .image-section {
        height: 140px;
    }
}
</style>