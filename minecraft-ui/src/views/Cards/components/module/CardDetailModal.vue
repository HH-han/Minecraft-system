<template>
  <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal">
      <div class="modal-header">
        <h2>卡片详情</h2>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>

      <div v-if="loading" class="modal-body loading-body">
        <div class="detail-skeleton skeleton-title"></div>
        <div class="detail-skeleton skeleton-line"></div>
        <div class="detail-skeleton skeleton-line w-80"></div>
        <div class="detail-skeleton skeleton-image"></div>
      </div>

      <div v-else-if="card" class="modal-body">
        <!-- 标题与标签 -->
        <div class="detail-head">
          <h3 class="detail-title">{{ card.title }}</h3>
          <div class="detail-tags">
            <span
              v-for="tag in tagNames(card)"
              :key="tag"
              class="tag-pill"
            >{{ tag }}</span>
          </div>
        </div>

        <!-- 图片墙 -->
        <div v-if="images(card).length" class="image-wall">
          <img
            v-for="(img, index) in images(card)"
            :key="`${img}-${index}`"
            :src="normalizeImageUrl(img)"
            :alt="`card-${index}`"
            loading="lazy"
            @click="previewImage(normalizeImageUrl(img))"
          />
        </div>

        <!-- 正文 -->
        <p v-if="card.content" class="detail-content">{{ card.content }}</p>

        <!-- 信息栏 -->
        <div class="detail-meta">
          <div class="meta-item">
            <span class="meta-label">作者</span>
            <span class="meta-value">{{ card.username || '旅行者' }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">地点</span>
            <span class="meta-value">{{ card.location || '—' }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">旅行日期</span>
            <span class="meta-value">{{ card.date || '—' }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">浏览</span>
            <span class="meta-value">{{ card.viewCount || 0 }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">点赞</span>
            <span class="meta-value">{{ card.likeCount || 0 }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">发布于</span>
            <span class="meta-value">{{ formatTime(card.createdAt) }}</span>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <template v-if="canEdit">
          <button class="btn-danger" @click="$emit('delete', card)">删除</button>
          <button class="btn-primary" @click="$emit('edit', card)">编辑</button>
        </template>
        <button class="btn-secondary" @click="$emit('close')">关闭</button>
      </div>
    </div>

    <!-- 图片预览 -->
    <div v-if="previewUrl" class="image-preview" @click="previewUrl = ''">
      <img :src="previewUrl" alt="preview" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { normalizeImageUrl, parseImageList, parseTagNames, formatTime } from '../../utils.js'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  card: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  },
  canEdit: {
    type: Boolean,
    default: false
  }
})

defineEmits(['close', 'edit', 'delete'])

const previewUrl = ref('')

watch(
  () => props.visible,
  (val) => {
    if (!val) previewUrl.value = ''
  }
)

function images(card) {
  return parseImageList(card)
}

function tagNames(card) {
  return parseTagNames(card)
}

function previewImage(url) {
  previewUrl.value = url
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-24);
  animation: fadeIn var(--transition-fast);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background: var(--color-bg);
  border-radius: var(--radius-xl);
  width: 100%;
  max-width: 720px;
  max-height: 88vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 40px 80px rgba(0, 0, 0, 0.2);
  animation: modalIn var(--transition-normal);
}

@keyframes modalIn {
  from { opacity: 0; transform: translateY(20px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-24) var(--spacing-32);
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  font-size: 22px;
  font-weight: 700;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: var(--color-bg-secondary);
  border-radius: 50%;
  font-size: 18px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.08);
  color: var(--color-text);
}

.modal-body {
  padding: var(--spacing-24) var(--spacing-32);
  overflow-y: auto;
}

.loading-body {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-16);
}

.detail-head {
  margin-bottom: var(--spacing-20);
}

.detail-title {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.15;
  letter-spacing: -0.01em;
  margin-bottom: var(--spacing-12);
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-8);
}

.tag-pill {
  background: rgba(41, 151, 255, 0.1);
  color: var(--color-primary-hover);
  padding: var(--spacing-4) var(--spacing-12);
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 500;
}

.image-wall {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-24);
}

.image-wall img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: var(--radius-md);
  cursor: zoom-in;
  transition: transform var(--transition-fast);
}

.image-wall img:hover {
  transform: scale(1.02);
}

.detail-content {
  font-size: 16px;
  line-height: 1.7;
  color: var(--color-text);
  white-space: pre-wrap;
  margin-bottom: var(--spacing-24);
}

.detail-meta {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-16) var(--spacing-24);
  padding: var(--spacing-20);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.meta-label {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.meta-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-12);
  padding: var(--spacing-20) var(--spacing-32);
  border-top: 1px solid var(--color-border);
}

.modal-footer button {
  border: none;
  padding: var(--spacing-10) var(--spacing-24);
  border-radius: var(--radius-full);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-primary:active {
  transform: scale(0.98);
}

.btn-secondary {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

.btn-secondary:hover {
  background: rgba(0, 0, 0, 0.08);
}

.btn-danger {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-danger);
}

.btn-danger:hover {
  background: rgba(255, 59, 48, 0.18);
}

/* 骨架 */
.detail-skeleton {
  background: #ececf0;
  border-radius: 8px;
  animation: shimmer 1.4s infinite;
  background-image: linear-gradient(90deg, #ececf0 25%, #f5f5f7 50%, #ececf0 75%);
  background-size: 200% 100%;
}

.skeleton-title { height: 32px; width: 50%; }
.skeleton-line { height: 16px; width: 100%; }
.skeleton-line.w-80 { width: 80%; }
.skeleton-image { height: 200px; border-radius: var(--radius-lg); }

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* 图片预览 */
.image-preview {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
  cursor: zoom-out;
  padding: var(--spacing-40);
}

.image-preview img {
  max-width: 100%;
  max-height: 100%;
  border-radius: var(--radius-md);
  object-fit: contain;
}

@media (max-width: 767px) {
  .modal-body {
    padding: var(--spacing-20);
  }
  .modal-header,
  .modal-footer {
    padding-left: var(--spacing-20);
    padding-right: var(--spacing-20);
  }
  .detail-title {
    font-size: 22px;
  }
  .detail-meta {
    grid-template-columns: repeat(2, 1fr);
  }
  .image-wall {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
