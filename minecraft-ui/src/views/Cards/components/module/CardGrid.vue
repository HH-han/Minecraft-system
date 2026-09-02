<template>
  <div class="tab-content">
    <!-- 加载骨架 -->
    <div v-if="loading && cards.length === 0" class="card-grid">
      <div v-for="n in 6" :key="n" class="card-skeleton">
        <div class="skeleton-image"></div>
        <div class="skeleton-body">
          <div class="skeleton-line w-60"></div>
          <div class="skeleton-line w-40"></div>
          <div class="skeleton-line w-90"></div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="cards.length === 0" class="empty-state">
      <div class="empty-icon">🗺️</div>
      <h3>{{ emptyTitle }}</h3>
      <p>{{ emptyDesc }}</p>
      <button v-if="showCreate" class="btn-primary btn-small" @click="$emit('create')">
        发布第一张卡片
      </button>
    </div>

    <!-- 卡片网格 -->
    <div v-else class="card-grid">
      <div
        v-for="card in cards"
        :key="card.id"
        class="travel-card"
        @click="$emit('detail', card)"
      >
        <div class="card-image">
          <img
            v-if="firstImage(card)"
            :src="normalizeImageUrl(firstImage(card))"
            :alt="card.title || 'card'"
            loading="lazy"
          />
          <div v-else class="image-placeholder">
            <span>✈️</span>
          </div>
          <span v-if="tagNames(card).length" class="tag-overlay">
            {{ tagNames(card)[0] }}
          </span>
        </div>
        <div class="card-content">
          <h3>{{ card.title || '未命名卡片' }}</h3>
          <p v-if="card.location" class="location">
            <span class="location-icon">◎</span>
            {{ card.location }}
          </p>
          <p v-if="card.content" class="description">{{ card.content }}</p>
          <div class="card-meta">
            <span class="author">{{ card.username || '旅行者' }}</span>
            <span v-if="card.date" class="date">{{ card.date }}</span>
          </div>
          <div class="card-footer">
            <div class="stats">
              <span class="stat-item">
                <span class="stat-icon">👁</span>{{ card.viewCount || 0 }}
              </span>
              <span class="stat-item">
                <span class="stat-icon">♡</span>{{ card.likeCount || 0 }}
              </span>
            </div>
            <span class="detail-link">查看 ›</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div v-if="cards.length > 0" class="load-more">
      <button
        v-if="hasMore"
        class="btn-secondary"
        :disabled="loading"
        @click="$emit('load-more')"
      >
        {{ loading ? '加载中...' : '加载更多' }}
      </button>
      <span v-else class="no-more">— 已经到底啦 —</span>
    </div>
  </div>
</template>

<script setup>
import { normalizeImageUrl, parseImageList, parseTagNames } from '../../utils.js'

defineProps({
  cards: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  hasMore: {
    type: Boolean,
    default: false
  },
  emptyTitle: {
    type: String,
    default: '暂无卡片'
  },
  emptyDesc: {
    type: String,
    default: '快去发布第一张旅行卡片吧'
  },
  showCreate: {
    type: Boolean,
    default: false
  }
})

defineEmits(['detail', 'load-more', 'create'])

function firstImage(card) {
  return parseImageList(card)[0] || ''
}

function tagNames(card) {
  return parseTagNames(card)
}
</script>

<style scoped>
.tab-content {
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-24);
}

/* Apple 风格产品卡片 */
.travel-card {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  transition: transform var(--transition-slow), box-shadow var(--transition-slow), border-color var(--transition-slow);
  cursor: pointer;
  border: 1px solid transparent;
  display: flex;
  flex-direction: column;
}

.travel-card:hover {
  transform: scale(1.01);
  box-shadow: var(--shadow-lg);
  border-color: rgba(0, 0, 0, 0.04);
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: #e8e8ed;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.travel-card:hover .card-image img {
  transform: scale(1.03);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f7 0%, #e8e8ed 100%);
}

.image-placeholder span {
  font-size: 48px;
  opacity: 0.5;
}

.tag-overlay {
  position: absolute;
  top: var(--spacing-12);
  left: var(--spacing-12);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  color: var(--color-text);
  padding: var(--spacing-4) var(--spacing-12);
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 500;
}

.card-content {
  padding: var(--spacing-24) var(--spacing-24) var(--spacing-20);
  display: flex;
  flex-direction: column;
  flex: 1;
}

.card-content h3 {
  font-size: 22px;
  font-weight: 600;
  line-height: 1.2;
  margin-bottom: var(--spacing-8);
  color: var(--color-text);
}

.location {
  color: var(--color-primary);
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: var(--spacing-12);
  display: flex;
  align-items: center;
  gap: var(--spacing-6);
}

.description {
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: var(--spacing-12);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-12);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-12);
  border-top: 1px solid var(--color-border);
  margin-top: auto;
}

.stats {
  display: flex;
  gap: var(--spacing-16);
}

.stat-item {
  font-size: 13px;
  color: var(--color-text-secondary);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-4);
}

.stat-icon {
  font-size: 13px;
}

.detail-link {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-primary);
  transition: color var(--transition-fast);
}

.travel-card:hover .detail-link {
  text-decoration: underline;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 40px;
  background: var(--color-bg-secondary);
  border-radius: 28px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: var(--spacing-20);
}

.empty-state h3 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: var(--spacing-8);
}

.empty-state p {
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-24);
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
  border: none;
  padding: var(--spacing-12) var(--spacing-28);
  border-radius: var(--radius-full);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-primary:active {
  transform: scale(0.98);
}

.btn-small {
  padding: var(--spacing-8) var(--spacing-20);
  font-size: 14px;
}

/* 骨架屏 */
.card-skeleton {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.skeleton-image {
  height: 200px;
  background: linear-gradient(90deg, #ececf0 25%, #f5f5f7 50%, #ececf0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
}

.skeleton-body {
  padding: var(--spacing-24);
}

.skeleton-line {
  height: 14px;
  border-radius: 6px;
  background: #e3e3e8;
  margin-bottom: var(--spacing-12);
}

.skeleton-line.w-60 { width: 60%; }
.skeleton-line.w-40 { width: 40%; }
.skeleton-line.w-90 { width: 90%; }

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* 加载更多 */
.load-more {
  display: flex;
  justify-content: center;
  margin-top: var(--spacing-40);
}

.btn-secondary {
  background: transparent;
  color: var(--color-primary);
  border: 1px solid var(--color-primary);
  padding: var(--spacing-10) var(--spacing-32);
  border-radius: var(--radius-full);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.btn-secondary:hover {
  background: rgba(41, 151, 255, 0.1);
}

.btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.no-more {
  color: var(--color-text-secondary);
  font-size: 13px;
}

@media (max-width: 1199px) and (min-width: 768px) {
  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: var(--spacing-20);
  }
}

@media (max-width: 767px) {
  .card-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-20);
  }
  .travel-card {
    border-radius: var(--radius-lg);
  }
  .card-content h3 {
    font-size: 20px;
  }
}
</style>
