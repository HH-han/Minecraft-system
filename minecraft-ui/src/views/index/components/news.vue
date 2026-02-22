<template>
    <!-- 新闻卡片 -->
    <section class="content-section">
        <h2 class="section-title">热门新闻</h2>
        <div v-if="loading.news" class="loading">加载中...</div>
        <div v-else-if="error.news" class="error">{{ error.news }}</div>
        <div v-else class="card-grid">
            <div v-for="news in newsList" :key="news.id" class="news-card" :style="cardStyle" @click="viewNewsDetail(news.id)">
                <!-- 玻璃拟态背景层 -->
                <div class="glass-background"></div>

                <!-- 卡片内容 -->
                <div class="card-content">
                    <!-- 图片区域 (模拟新闻图) -->
                    <div class="image-section">
                        <div class="category-tag" v-if="news.category">{{ news.category }}</div>
                        <img 
                            :src="news.coverImage || news.image || news.imageUrl || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=news%20article%20placeholder%2C%20simple%20clean%20design&image_size=square'" 
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

// 卡片样式
const cardStyle = {
    '--blur-amount': '10px',
    '--card-bg': 'rgba(255, 255, 255, 0.1)'
};

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
    color: #333;
    border-left: 4px solid #4facfe;
    padding-left: 12px;
}

.card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 20px;
}

/* 卡片容器 — 提供相对定位和圆角阴影 */
.news-card {
    position: relative;
    width: 100%;
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.4);
    color: #1e293b;
    /* 文字色深一些以保证在玻璃背景上可读 */
}

/* 玻璃背景层 — 真正的半透明底色 */
.glass-background {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: var(--card-bg);
    border-radius: 24px;
    z-index: 0;
    /* 增加一点微妙的渐变让玻璃更灵动 */
    background: linear-gradient(135deg,
            rgba(255, 255, 255, 0.35) 0%,
            rgba(255, 255, 255, 0.15) 100%);
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.6);
}

/* 内容区域 — 放在玻璃之上 */
.card-content {
    position: relative;
    z-index: 1;
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
    border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.news-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 0.5s ease;
}

.news-card:hover .news-image {
    transform: scale(1.03);
}

/* 分类标签 */
.category-tag {
    position: absolute;
    top: 16px;
    left: 16px;
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(4px);
    -webkit-backdrop-filter: blur(4px);
    padding: 4px 12px;
    border-radius: 30px;
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.3px;
    color: #0f172a;
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    z-index: 2;
}

/* 文本区域 */
.text-section {
    padding: 20px 18px 22px;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.news-title {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    line-height: 1.4;
    color: #0f172a;
    /* 深色保证可读性 */
    text-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);
}

.news-description {
    margin: 0;
    font-size: 0.9rem;
    line-height: 1.5;
    color: #334155;
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
    margin-top: 12px;
    font-size: 0.8rem;
    color: #1e293b;
    border-top: 1px solid rgba(255, 255, 255, 0.5);
    padding-top: 12px;
}

.author,
.date,
.stats {
    display: flex;
    align-items: center;
    gap: 4px;
    background: rgba(255, 255, 255, 0.3);
    padding: 4px 8px;
    border-radius: 30px;
    backdrop-filter: blur(2px);
    border: 1px solid rgba(255, 255, 255, 0.6);
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
    font-size: 0.75rem;
    color: #1e293b;
}

.icon {
    opacity: 0.8;
}

/* 卡片悬停效果 */
.news-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15), 0 4px 12px rgba(0, 0, 0, 0.1);
    border-color: rgba(255, 255, 255, 0.7);
}

/* 加载和错误状态 */
.loading {
    text-align: center;
    padding: 40px;
    color: #666;
}

.error {
    text-align: center;
    padding: 40px;
    color: #ff4d4f;
}

/* 响应式微调 */
@media (max-width: 768px) {
    .card-grid {
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    }
    
    .section-title {
        font-size: 20px;
    }
    
    .news-card {
        width: 100%;
    }
    
    .text-section {
        padding: 16px 14px 18px;
    }
    
    .news-title {
        font-size: 1.1rem;
    }
    
    .image-section {
        height: 150px;
    }
}
</style>