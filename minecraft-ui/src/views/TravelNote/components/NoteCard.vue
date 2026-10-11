<template>
  <div class="note-card" @click="$emit('click', note)">
    <div class="note-card__cover">
      <img v-if="note.coverUrl" :src="note.coverUrl" :alt="note.title" loading="lazy" />
      <div v-else class="note-card__placeholder">
        <el-icon :size="36"><Picture /></el-icon>
      </div>
      <span v-if="note.isFeatured" class="note-card__badge">精</span>
      <span v-if="note.isTop" class="note-card__badge note-card__badge--top">顶</span>
    </div>

    <div class="note-card__body">
      <h3 class="note-card__title">{{ note.title }}</h3>
      <p v-if="note.summary" class="note-card__summary">{{ note.summary }}</p>

      <div v-if="note.tags && note.tags.length" class="note-card__tags">
        <span v-for="tag in note.tags.slice(0, 3)" :key="tag" class="note-card__tag">#{{ tag }}</span>
      </div>

      <div class="note-card__meta">
        <span v-if="note.destination" class="note-card__dest">
          <el-icon><Location /></el-icon>{{ note.destination }}
        </span>
        <span v-if="note.travelDays" class="note-card__days">{{ note.travelDays }}天</span>
      </div>

      <div class="note-card__footer">
        <div class="note-card__author">
          <el-avatar :size="22" :src="note.avatar">{{ (note.username || '匿')[0] }}</el-avatar>
          <span class="note-card__username">{{ note.username || '匿名用户' }}</span>
        </div>
        <div class="note-card__stats">
          <span><el-icon><View /></el-icon>{{ formatCount(note.viewCount) }}</span>
          <span><el-icon><Pointer /></el-icon>{{ formatCount(note.likeCount) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Location, Picture, Pointer, View } from '@element-plus/icons-vue'

defineProps({
  note: { type: Object, required: true }
})

defineEmits(['click'])

const formatCount = (n) => {
  const num = Number(n) || 0
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  return String(num)
}
</script>

<style scoped>
.note-card {
  break-inside: avoid;
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.note-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--el-box-shadow-light);
}

.note-card__cover {
  position: relative;
  width: 100%;
  aspect-ratio: 4 / 3;
  background: var(--el-fill-color-light);
}

.note-card__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.note-card__placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-placeholder);
}

.note-card__badge {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 12px;
  color: #fff;
  background: linear-gradient(135deg, #f59e0b, #f97316);
}

.note-card__badge--top {
  left: auto;
  right: 10px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.note-card__body {
  padding: 12px 14px 10px;
}

.note-card__title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  color: var(--el-text-color-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-card__summary {
  margin: 6px 0 0;
  font-size: 13px;
  line-height: 1.5;
  color: var(--el-text-color-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-card__tags {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.note-card__tag {
  font-size: 12px;
  color: var(--el-color-primary);
}

.note-card__meta {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.note-card__dest {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.note-card__footer {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid var(--el-border-color-extra-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.note-card__author {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.note-card__username {
  font-size: 12px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.note-card__stats {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

.note-card__stats span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}
</style>
