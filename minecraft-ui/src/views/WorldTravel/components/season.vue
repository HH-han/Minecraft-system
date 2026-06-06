<template>
    <div class="seasonal-recommendations">
        <h2 class="section-title-conter">当季推荐</h2>
        <div class="month-tabs">
            <button v-for="month in months" :key="month" :class="{ active: currentMonth === month }"
                @click="handleMonthChange(month)" class="month-tab">
                {{ month }}
                <span class="tab-underline"></span>
            </button>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在加载推荐数据...</p>
        </div>
        
        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
            <div class="error-icon">⚠️</div>
            <p>加载失败，请稍后重试</p>
            <button @click="retryFetch" class="retry-button">重试</button>
        </div>
        
        <!-- 数据展示 -->
        <div v-else class="recommendation-grid">
            <div v-for="destination in getRecommendationsForMonth(currentMonth)" :key="destination.id"
                class="destination-card" @click="openDetail(destination)">
                <div class="image-wrapper">
                    <img :src="destination.imageUrl" :alt="destination.name + '图片'" class="destination-image" />
                    <div class="image-overlay"></div>
                    <div class="card-overlay-indicator">
                        <span>查看详情 ›</span>
                    </div>
                </div>
                <div class="destination-info">
                    <h3 class="destination-name">{{ destination.name }}</h3>
                    <p class="destination-desc">{{ destination.description }}</p>
                    <div class="destination-tags">
                        <span v-for="tag in destination.parsedTags" :key="tag" class="tag">{{ tag }}</span>
                    </div>
                </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="getRecommendationsForMonth(currentMonth).length === 0" class="empty-state">
                <p>暂无该月份的推荐数据</p>
            </div>
        </div>
        
        <!-- 详情弹窗（Apple 风格） -->
        <div v-if="selectedDestination" class="detail-modal" @click.self="closeDetail">
            <div class="detail-modal-content" @click.stop>
                <button class="detail-close-btn" @click="closeDetail" aria-label="关闭">×</button>
                <div class="detail-image-container">
                    <img :src="selectedDestination.imageUrl" :alt="selectedDestination.name" class="detail-image" />
                </div>
                <div class="detail-info-container">
                    <h1 class="detail-title">{{ selectedDestination.name }}</h1>
                    <p class="detail-description">{{ selectedDestination.description }}</p>
                    
                    <div class="detail-section">
                        <h3 class="section-label">季节特色</h3>
                        <p class="section-content">{{ selectedDestination.seasonFeatures }}</p>
                    </div>
                    
                    <div class="detail-section">
                        <h3 class="section-label">气候信息</h3>
                        <p class="section-content">{{ selectedDestination.climateInfo }}</p>
                    </div>
                    
                    <div class="detail-section">
                        <h3 class="section-label">推荐天数</h3>
                        <p class="section-content">{{ selectedDestination.recommendedDays }} 天</p>
                    </div>
                    
                    <div class="detail-section">
                        <h3 class="section-label">旅行贴士</h3>
                        <p class="section-content">{{ selectedDestination.travelTips }}</p>
                    </div>
                    
                    <div class="detail-tags">
                        <span v-for="tag in selectedDestination.parsedTags" :key="tag" class="detail-tag">
                            {{ tag }}
                        </span>
                    </div>
                    
                    <div class="detail-actions">
                        <button class="btn-primary" @click="handleVisit(selectedDestination.id)">前往预订</button>
                        <a href="#" class="btn-link">了解更多 ›</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import {
  getAllMonths,
  getRecommendationsByMonth
} from '@/api/monthlyRecommendations'

const currentMonth = ref('一月')
const loading = ref(false)
const error = ref(null)
const selectedDestination = ref(null)

const months = ref(['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'])

// 使用响应式对象存储推荐数据
const recommendations = ref({})

// 处理tags字段（后端返回的是JSON字符串）
const parseTags = (tagsString) => {
    try {
        return JSON.parse(tagsString) || []
    } catch {
        return []
    }
}

// 获取当前月份的推荐数据
const getRecommendationsForMonth = (month) => {
    const monthData = recommendations.value[month] || []
    
    // 为每个目的地添加解析后的tags字段
    return monthData.map(destination => ({
        ...destination,
        name: destination.destinationName,
        imageUrl: destination.imageUrl,
        parsedTags: parseTags(destination.tags)
    }))
}

const handleMonthChange = (month) => {
    currentMonth.value = month
}

const openDetail = (destination) => {
    selectedDestination.value = destination
    document.body.style.overflow = 'hidden'
}

const closeDetail = () => {
    selectedDestination.value = null
    document.body.style.overflow = ''
}

const handleVisit = (destinationId) => {
    closeDetail()
}

// 获取推荐数据
const fetchRecommendations = async (month) => {
    loading.value = true
    error.value = null
    
    try {
        const response = await getRecommendationsByMonth(month)
        
        // 根据后端数据格式处理响应
        if (response.code === 200 && response.data) {
            return response.data
        } else {
            throw new Error(response.message || '获取数据失败')
        }
    } catch (err) {
        console.error('获取推荐数据失败:', err)
        error.value = err.message || '网络错误'
        // API调用失败时返回空数组
        return []
    } finally {
        loading.value = false
    }
}

// 加载所有月份
const loadMonths = async () => {
    try {
        const response = await getAllMonths()
        if (response.code === 200 && response.data) {
            months.value = response.data
        }
    } catch (err) {
        console.error('获取月份列表失败:', err)
        // 失败时使用默认月份
    }
}

// 重试获取数据
const retryFetch = () => {
    error.value = null
    fetchRecommendations(currentMonth.value).then((data) => {
        recommendations.value[currentMonth.value] = data
    })
}

// 初始化时获取当前月份的推荐和所有月份
onMounted(async () => {
    await loadMonths()
    await fetchRecommendations(currentMonth.value).then((data) => {
        recommendations.value[currentMonth.value] = data
    })
})

// 监听月份变化，更新推荐数据
watch(currentMonth, (newMonth) => {
    // 如果该月份的数据尚未加载，则获取数据
    if (!recommendations.value[newMonth]) {
        fetchRecommendations(newMonth).then((data) => {
            recommendations.value[newMonth] = data
        })
    }
})
</script>

<style scoped>
.seasonal-recommendations {
    margin: 0 auto;
    padding: 40px 0px 40px 0px;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.section-title-conter {
    text-align: center;
    font-size: 32px;
    font-weight: 600;
    margin-bottom: 40px;
    color: #333;
    position: relative;
    padding-bottom: 15px;
}

.section-title-conter::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 3px;
    background: linear-gradient(90deg, #ff6b6b, #ff8e53);
}

.month-tabs {
    display: flex;
    justify-content: center;
    gap: 8px;
    margin-bottom: 40px;
    flex-wrap: wrap;
}

.month-tab {
    position: relative;
    padding: 8px 20px;
    font-size: 16px;
    font-weight: 500;
    color: #666;
    background: none;
    border: none;
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 20px;
}

.month-tab:hover {
    color: #ff6b6b;
    background: rgba(255, 107, 107, 0.1);
}

.month-tab.active {
    color: #ff6b6b;
    font-weight: 600;
}

.tab-underline {
    position: absolute;
    bottom: -6px;
    left: 50%;
    transform: translateX(-50%) scaleX(0);
    width: 50%;
    height: 2px;
    background: linear-gradient(90deg, #ff6b6b, #ff8e53);
    transition: transform 0.3s ease;
}

.month-tab.active .tab-underline {
    transform: translateX(-50%) scaleX(1);
}

/* 加载状态样式 */
.loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    text-align: center;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #ff6b6b;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 16px;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

.loading-state p {
    color: #666;
    font-size: 16px;
    margin: 0;
}

/* 错误状态样式 */
.error-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    text-align: center;
}

.error-icon {
    font-size: 48px;
    margin-bottom: 16px;
}

.error-state p {
    color: #ff6b6b;
    font-size: 16px;
    margin-bottom: 20px;
}

.retry-button {
    padding: 10px 24px;
    background: #ff6b6b;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.retry-button:hover {
    background: #ff5252;
}

/* 空状态样式 */
.empty-state {
    grid-column: 1 / -1;
    text-align: center;
    padding: 60px 20px;
    color: #999;
    font-size: 16px;
}

.recommendation-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 30px;
    padding: 0 20px;
}

.destination-card {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    background: white;
}

.destination-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 20px rgba(0, 0, 0, 0.15);
}

.image-wrapper {
    position: relative;
    width: 100%;
    height: 200px;
    overflow: hidden;
}

.destination-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.destination-card:hover .destination-image {
    transform: scale(1.05);
}

.image-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.1));
}

.destination-info {
    padding: 20px;
}

.destination-name {
    margin: 0 0 10px;
    font-size: 20px;
    font-weight: 600;
    color: #333;
}

.destination-desc {
    margin: 0 0 15px;
    font-size: 14px;
    color: #666;
    line-height: 1.5;
}

.destination-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.tag {
    padding: 4px 12px;
    font-size: 12px;
    background: #f3f3f3;
    border-radius: 20px;
    color: #666;
}

.card-overlay-indicator {
    position: absolute;
    bottom: 16px;
    right: 16px;
    padding: 6px 12px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
    color: #2997ff;
    opacity: 0;
    transition: opacity 0.3s ease;
    pointer-events: none;
}

.destination-card:hover .card-overlay-indicator {
    opacity: 1;
}

/* ===========================================================
   详情弹窗（Apple 风格）
   设计规范参考：officialwebsite.md
   =========================================================== */

.detail-modal {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    padding: 24px;
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    backdrop-filter: blur(20px) saturate(180%);
    animation: modal-fade-in 0.3s ease-out;
}

@keyframes modal-fade-in {
    from { opacity: 0; }
    to   { opacity: 1; }
}

.detail-modal-content {
    position: relative;
    width: 100%;
    max-width: 980px;
    max-height: 90vh;
    background: #ffffff;
    border-radius: 24px;
    box-shadow: 0 30px 60px -20px rgba(0, 0, 0, 0.25),
                0 10px 20px -10px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    display: flex;
    flex-direction: row;
    font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text",
                 "Helvetica Neue", "PingFang SC", "Microsoft YaHei", sans-serif;
    color: #1d1d1f;
    animation: modal-pop-in 0.35s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes modal-pop-in {
    from { opacity: 0; transform: translateY(24px) scale(0.98); }
    to   { opacity: 1; transform: translateY(0) scale(1); }
}

.detail-close-btn {
    position: absolute;
    top: 16px;
    right: 16px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.08);
    color: #1d1d1f;
    border: none;
    font-size: 20px;
    line-height: 1;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 5;
    transition: background 0.2s ease, transform 0.2s ease;
}

.detail-close-btn:hover {
    background: rgba(0, 0, 0, 0.16);
    transform: scale(1.05);
}

.detail-image-container {
    flex: 1 1 50%;
    min-height: 420px;
    background: #f5f5f7;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

.detail-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
}

.detail-image-container:hover .detail-image {
    transform: scale(1.01);
}

.detail-info-container {
    flex: 1 1 50%;
    padding: 48px 40px 40px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    overflow-y: auto;
    max-height: 90vh;
}

.detail-title {
    font-size: 36px;
    font-weight: 700;
    line-height: 1.1;
    letter-spacing: -0.01em;
    color: #1d1d1f;
    margin: 0;
}

.detail-description {
    font-size: 17px;
    font-weight: 400;
    line-height: 1.47;
    color: #1d1d1f;
    margin: 0;
}

.detail-section {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 16px;
    background: #f5f5f7;
    border-radius: 12px;
}

.section-label {
    font-size: 12px;
    font-weight: 500;
    color: #6e6e73;
    letter-spacing: 0.02em;
    margin: 0;
    text-transform: uppercase;
}

.section-content {
    font-size: 15px;
    font-weight: 400;
    line-height: 1.5;
    color: #1d1d1f;
    margin: 0;
}

.detail-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.detail-tag {
    padding: 6px 14px;
    font-size: 13px;
    font-weight: 500;
    background: #f5f5f7;
    border-radius: 20px;
    color: #6e6e73;
    transition: all 0.2s ease;
}

.detail-tag:hover {
    background: #e8e8ed;
    color: #1d1d1f;
}

.detail-actions {
    margin-top: 8px;
    display: flex;
    align-items: center;
    gap: 32px;
}

.btn-primary {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 12px 28px;
    background: #000000;
    color: #ffffff;
    border: none;
    border-radius: 40px;
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.01em;
    cursor: pointer;
    transition: background 0.2s ease, transform 0.2s ease;
}

.btn-primary:hover {
    background: #1d1d1f;
    transform: translateY(-1px);
}

.btn-link {
    font-size: 16px;
    font-weight: 400;
    color: #2997ff;
    text-decoration: none;
    cursor: pointer;
    transition: color 0.2s ease;
}

.btn-link:hover {
    color: #0066cc;
    text-decoration: underline;
    text-underline-offset: 4px;
}

/* 响应式：平板 */
@media (max-width: 880px) {
    .detail-modal-content {
        flex-direction: column;
        max-width: 640px;
    }
    .detail-image-container {
        min-height: 280px;
        flex: 1 1 auto;
    }
    .detail-info-container {
        padding: 32px;
    }
    .detail-title { font-size: 28px; }
}

/* 响应式：手机 */
@media (max-width: 480px) {
    .detail-modal {
        padding: 0;
        align-items: flex-end;
    }
    .detail-modal-content {
        border-radius: 20px 20px 0 0;
        max-height: 92vh;
        overflow-y: auto;
        -webkit-overflow-scrolling: touch;
    }
    .detail-image-container { min-height: 200px; }
    .detail-info-container {
        padding: 24px 20px 32px;
        gap: 16px;
    }
    .detail-title { font-size: 24px; }
    .detail-description { font-size: 15px; }
    .detail-actions {
        flex-direction: column;
        align-items: stretch;
        gap: 16px;
    }
    .btn-primary {
        width: 100%;
    }
}

@media (max-width: 768px) {
    .section-title-conter {
        font-size: 26px;
        margin-bottom: 30px;
    }

    .month-tabs {
        gap: 6px;
        margin-bottom: 30px;
    }

    .month-tab {
        padding: 6px 15px;
        font-size: 14px;
    }

    .recommendation-grid {
        grid-template-columns: 1fr;
        gap: 20px;
        padding: 0 10px;
    }

    .image-wrapper {
        height: 180px;
    }
    
    .loading-state,
    .error-state {
        padding: 40px 20px;
    }
    
    .loading-spinner {
        width: 32px;
        height: 32px;
    }
    
    .error-icon {
        font-size: 36px;
    }
}
</style>