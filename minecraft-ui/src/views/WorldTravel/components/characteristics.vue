<template>
    <section class="world-characteristics">
        <!-- 区块标题 -->
        <header class="world-header">
            <span class="world-eyebrow">CULTURE &amp; NATURE</span>
            <h2 class="world-title">全球特色地点探索</h2>
            <p class="world-subtitle">穿越时空的文化与自然奇观</p>
        </header>

        <!-- 筛选工具栏 -->
        <div class="world-toolbar">
            <div class="world-filters" role="tablist" aria-label="按分类筛选特色地点">
                <button v-for="category in categories" :key="category" class="world-filter"
                    :class="{ 'is-active': activeCategory === category }" type="button" role="tab"
                    :aria-selected="activeCategory === category" @click="filterByCategory(category)">
                    {{ category }}
                </button>
            </div>
            <span v-if="!loading && locations.length" class="world-count">共 {{ locations.length }} 处</span>
        </div>

        <!-- 加载骨架 -->
        <div v-if="loading" class="world-grid" aria-busy="true" aria-label="加载中">
            <div v-for="index in 3" :key="index" class="world-card is-skeleton">
                <div class="skeleton-block"></div>
                <div class="world-card-body">
                    <span class="skeleton-line skeleton-line--sm"></span>
                    <span class="skeleton-line skeleton-line--lg"></span>
                    <span class="skeleton-line"></span>
                    <span class="skeleton-line"></span>
                </div>
            </div>
        </div>

        <!-- 地点卡片 -->
        <div v-else-if="locations.length" class="world-grid">
            <article v-for="location in locations" :key="location.id" class="world-card"
                :style="{ '--wc-accent': location.color || DEFAULT_ACCENT }">
                <div class="world-card-media">
                    <img :src="location.image || FALLBACK_IMAGE" :alt="location.name" loading="lazy"
                        @error="handleImageError" />
                    <span v-if="location.country" class="world-card-country">{{ location.country }}</span>
                </div>

                <div class="world-card-body">
                    <span class="world-card-category">{{ location.category || '世界特色' }}</span>
                    <h3 class="world-card-name">{{ location.name }}</h3>

                    <ul v-if="location.tagList.length" class="world-card-tags">
                        <li v-for="tag in location.tagList" :key="tag" class="world-tag">{{ tag }}</li>
                    </ul>

                    <p class="world-card-desc">{{ location.description || '暂无描述' }}</p>

                    <dl class="world-card-facts">
                        <div class="world-fact">
                            <dt>特色</dt>
                            <dd>{{ location.features || '暂无信息' }}</dd>
                        </div>
                        <div class="world-fact">
                            <dt>历史</dt>
                            <dd>{{ location.history || '暂无信息' }}</dd>
                        </div>
                        <div class="world-fact">
                            <dt>人文</dt>
                            <dd>{{ location.culture || '暂无信息' }}</dd>
                        </div>
                    </dl>
                </div>
            </article>
        </div>

        <!-- 空状态 -->
        <div v-else class="world-empty">
            <span class="world-empty-icon" aria-hidden="true">🧭</span>
            <h3 class="world-empty-title">暂未收录该分类的地点</h3>
            <p class="world-empty-hint">换个分类看看，或浏览全部特色地点</p>
            <button v-if="activeCategory !== '全部'" class="world-empty-reset" type="button"
                @click="filterByCategory('全部')">
                查看全部地点
            </button>
        </div>

        <!-- 分页（仅全部数据分页，分类结果为完整列表） -->
        <div v-if="activeCategory === '全部' && totalPages > 1" class="world-paging">
            <Paging :total-pages="totalPages" :current-page="currentPage" @update:current-page="handleCurrentChange" />
        </div>
    </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import {
  getWorldCharacteristicsPage,
  getWorldCharacteristicsByCategory
} from '@/api/worldcharacteristics';
import Paging from '@/components/paging/index.vue';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';

const DEFAULT_ACCENT = '#0d9488';
// src 下的静态资源需经打包器解析，直接写 /assets/... 会 404
const FALLBACK_IMAGE = new URL('@/assets/defaultimage/moren.webp', import.meta.url).href;
// 数据状态
const locations = ref([]);
const categories = ref(['全部', '建筑', '自然景观', '文物', '文化遗产']);
const activeCategory = ref('全部');
const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = ref(0);
const loading = ref(false);

// 分割标签
const splitTags = (tags) => {
  if (!tags) return [];
  return String(tags)
    .split(',')
    .map((tag) => tag.trim())
    .filter(Boolean);
};

// 预处理数据，模板中无需重复解析
const normalize = (records) => {
  if (!Array.isArray(records)) return [];
  return records.filter(Boolean).map((item) => ({
    ...item,
    tagList: splitTags(item.tags)
  }));
};

const resolveTotalPages = (pages, total) => {
  return Number(pages) || Math.ceil((Number(total) || 0) / pageSize.value) || 0;
};

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const response = await getWorldCharacteristicsPage(currentPage.value, pageSize.value);
    if (response.code === 200) {
      const data = response.data || {};
      locations.value = normalize(data.records);
      totalPages.value = resolveTotalPages(data.pages, data.total);
    }
  } catch (error) {
    console.error('加载世界特色地点失败:', error);
    locations.value = [];
    totalPages.value = 0;
  } finally {
    loading.value = false;
  }
};

// 数据变更后自动刷新
useAutoRefresh(loadData)

// 按分类过滤
const filterByCategory = async (category) => {
  activeCategory.value = category;
  currentPage.value = 1;
  loading.value = true;
  try {
    if (category === '全部') {
      const response = await getWorldCharacteristicsPage(1, pageSize.value);
      if (response.code === 200) {
        const data = response.data || {};
        locations.value = normalize(data.records);
        totalPages.value = resolveTotalPages(data.pages, data.total);
      }
    } else {
      const response = await getWorldCharacteristicsByCategory(category);
      if (response.code === 200) {
        locations.value = normalize(response.data);
        totalPages.value = 0;
      }
    }
  } catch (error) {
    console.error('按分类加载世界特色地点失败:', error);
    locations.value = [];
    totalPages.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleCurrentChange = (current) => {
  currentPage.value = current;
  loadData();
};

// 图片加载失败时回退到默认图
const handleImageError = (event) => {
  const image = event.target;
  if (image.src !== FALLBACK_IMAGE) image.src = FALLBACK_IMAGE;
};

// 组件挂载时加载数据
onMounted(loadData);
</script>

<style scoped>
.world-characteristics {
    --world-accent: var(--theme-primary);
    --world-hairline: var(--theme-hairline);
    --world-title: var(--theme-text-1);
    --world-text: var(--theme-text-2);
    --world-muted: var(--theme-text-3);

    padding: 48px 24px;
    color: var(--world-title);
    font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

/* ========== 区块标题 ========== */
.world-header {
    text-align: center;
    margin-bottom: 34px;
}

.world-eyebrow {
    display: inline-block;
    margin-bottom: 10px;
    color: var(--world-accent);
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.28em;
}

.world-title {
    position: relative;
    margin: 0 0 14px;
    color: var(--world-title);
    font-size: clamp(1.75rem, 3vw, 2.35rem);
    font-weight: 700;
    letter-spacing: -0.01em;
}

.world-title::after {
    content: '';
    display: block;
    width: 56px;
    height: 4px;
    margin: 14px auto 0;
    border-radius: 2px;
    background: linear-gradient(90deg, var(--world-accent),
            color-mix(in srgb, var(--world-accent) 55%, var(--theme-bg-surface)));
}

.world-subtitle {
    margin: 0;
    color: var(--world-text);
    font-size: 15px;
}

/* ========== 筛选工具栏 ========== */
.world-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 28px;
}

.world-filters {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.world-filter {
    padding: 9px 20px;
    border: 1.5px solid transparent;
    border-radius: 999px;
    background: var(--theme-bg-surface-2);
    color: var(--theme-text-2);
    font-size: 14px;
    font-weight: 500;
    font-family: inherit;
    cursor: pointer;
    transition: color 0.25s ease, background 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease;
}

.world-filter:hover {
    color: var(--world-accent);
    background: color-mix(in srgb, var(--world-accent) 9%, transparent);
}

.world-filter.is-active {
    color: var(--theme-primary-text);
    border-color: transparent;
    background: linear-gradient(135deg, var(--world-accent),
            color-mix(in srgb, var(--world-accent) 62%, var(--theme-bg-surface)));
    box-shadow: 0 8px 20px -10px color-mix(in srgb, var(--world-accent) 85%, transparent);
}

.world-count {
    padding: 6px 14px;
    border: 1px solid color-mix(in srgb, var(--world-accent) 22%, transparent);
    border-radius: 999px;
    background: color-mix(in srgb, var(--world-accent) 9%, transparent);
    color: var(--world-accent);
    font-size: 13px;
    font-weight: 600;
    white-space: nowrap;
}

/* ========== 卡片网格 ========== */
.world-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 26px;
}

.world-card {
    --wc-accent: var(--world-accent);

    display: flex;
    flex-direction: column;
    overflow: hidden;
    border: 1px solid var(--world-hairline);
    border-radius: 18px;
    background: var(--theme-bg-surface);
    box-shadow: var(--theme-shadow-sm);
    transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1), box-shadow 0.3s ease, border-color 0.3s ease;
}

.world-card:hover {
    transform: translateY(-6px);
    border-color: color-mix(in srgb, var(--wc-accent) 45%, transparent);
    box-shadow: var(--theme-shadow-lg);
}

/* 图片区 */
.world-card-media {
    position: relative;
    aspect-ratio: 16 / 10;
    overflow: hidden;
    background: var(--theme-bg-surface-2);
}

.world-card-media::after {
    content: '';
    position: absolute;
    inset: 0;
    pointer-events: none;
    background: linear-gradient(to top, rgba(15, 23, 42, 0.5), transparent 60%);
}

.world-card-media img {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s cubic-bezier(0.22, 1, 0.36, 1);
}

.world-card:hover .world-card-media img {
    transform: scale(1.06);
}

.world-card-country {
    position: absolute;
    left: 14px;
    bottom: 12px;
    z-index: 1;
    padding: 5px 12px;
    border: 1px solid rgba(255, 255, 255, 0.25);
    border-radius: 999px;
    background: rgba(15, 23, 42, 0.5);
    color: #ffffff;
    font-size: 12px;
    font-weight: 600;
    -webkit-backdrop-filter: blur(8px);
    backdrop-filter: blur(8px);
}

/* 卡片内容 */
.world-card-body {
    display: flex;
    flex: 1;
    flex-direction: column;
    gap: 10px;
    padding: 18px 20px 22px;
}

.world-card-category {
    color: var(--wc-accent);
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.14em;
}

.world-card-name {
    margin: 0;
    color: var(--world-title);
    font-size: 20px;
    font-weight: 700;
    letter-spacing: -0.01em;
}

.world-card-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin: 0;
    padding: 0;
    list-style: none;
}

.world-tag {
    padding: 3px 10px;
    border-radius: 6px;
    background: var(--theme-bg-surface-2);
    color: var(--theme-text-2);
    font-size: 12px;
}

.world-card-desc {
    margin: 0;
    display: -webkit-box;
    overflow: hidden;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    color: var(--world-text);
    font-size: 14px;
    line-height: 1.7;
}

/* 特色 / 历史 / 人文 */
.world-card-facts {
    margin: 4px 0 0;
    padding: 0;
    border-top: 1px solid var(--world-hairline);
}

.world-fact {
    display: flex;
    gap: 12px;
    padding: 9px 0;
    border-bottom: 1px solid var(--theme-hairline);
}

.world-fact:last-child {
    padding-bottom: 0;
    border-bottom: none;
}

.world-fact dt {
    flex: none;
    min-width: 36px;
    padding-top: 1px;
    color: var(--wc-accent);
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 0.06em;
}

.world-fact dd {
    margin: 0;
    display: -webkit-box;
    overflow: hidden;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    color: var(--theme-text-2);
    font-size: 13px;
    line-height: 1.6;
}

/* ========== 加载骨架 ========== */
.world-card.is-skeleton {
    pointer-events: none;
}

.skeleton-block {
    aspect-ratio: 16 / 10;
    background: var(--theme-bg-surface-2);
}

.skeleton-line {
    display: block;
    height: 12px;
    border-radius: 6px;
    background: var(--theme-bg-surface-2);
}

.skeleton-line--sm {
    width: 34%;
    height: 10px;
}

.skeleton-line--lg {
    width: 62%;
    height: 18px;
}

.skeleton-block,
.skeleton-line {
    position: relative;
    overflow: hidden;
}

.skeleton-block::after,
.skeleton-line::after {
    content: '';
    position: absolute;
    inset: 0;
    transform: translateX(-100%);
    background: linear-gradient(90deg, transparent, var(--theme-bg-glass-strong), transparent);
    animation: world-shimmer 1.4s ease-in-out infinite;
}

@keyframes world-shimmer {
    to {
        transform: translateX(100%);
    }
}

/* ========== 空状态 ========== */
.world-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 72px 24px;
    text-align: center;
}

.world-empty-icon {
    margin-bottom: 14px;
    font-size: 44px;
}

.world-empty-title {
    margin: 0 0 8px;
    color: var(--theme-text-1);
    font-size: 18px;
    font-weight: 600;
}

.world-empty-hint {
    margin: 0 0 22px;
    color: var(--world-muted);
    font-size: 14px;
}

.world-empty-reset {
    padding: 10px 24px;
    border: none;
    border-radius: 999px;
    background: var(--world-accent);
    color: var(--theme-primary-text);
    font-size: 14px;
    font-weight: 600;
    font-family: inherit;
    cursor: pointer;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.world-empty-reset:hover {
    transform: translateY(-1px);
    box-shadow: 0 10px 22px -10px color-mix(in srgb, var(--world-accent) 85%, transparent);
}

/* ========== 分页 ========== */
.world-paging {
    display: flex;
    justify-content: center;
    margin-top: 36px;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
    .world-characteristics {
        padding: 36px 16px;
    }

    .world-toolbar {
        flex-direction: column;
        align-items: flex-start;
    }

    .world-grid {
        grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
        gap: 18px;
    }

    .world-card-name {
        font-size: 18px;
    }

    .world-card-body {
        padding: 16px 16px 18px;
    }
}

@media (max-width: 420px) {
    .world-grid {
        grid-template-columns: 1fr;
    }
}

@media (prefers-reduced-motion: reduce) {

    .world-card,
    .world-card-media img,
    .world-filter,
    .world-empty-reset {
        transition: none;
    }

    .world-card:hover {
        transform: none;
    }

    .world-card:hover .world-card-media img {
        transform: none;
    }

    .skeleton-block::after,
    .skeleton-line::after {
        animation: none;
    }
}
</style>
