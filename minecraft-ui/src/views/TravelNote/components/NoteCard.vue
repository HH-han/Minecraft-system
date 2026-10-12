<template>
  <div class="note-card" @click="$emit('click', note)">
    <div class="note-card__cover">
      <img v-if="note.coverUrl" :src="note.coverUrl" :alt="note.title" loading="lazy" />
      <div v-else class="note-card__placeholder">
        <el-icon :size="34"><Picture /></el-icon>
      </div>
      <span class="note-card__scrim" aria-hidden="true"></span>

      <span v-if="note.isFeatured" class="note-card__badge note-card__badge--featured">精</span>
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
        <span v-if="note.travelDays" class="note-card__days">{{ note.travelDays }} 天</span>
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
  position: relative;
  break-inside: avoid;
  border-radius: 18px;
  overflow: hidden;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
  cursor: pointer;
  transition:
    transform 0.32s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.32s cubic-bezier(0.22, 1, 0.36, 1),
    border-color 0.32s ease;
}

.note-card:hover {
  transform: translateY(-6px);
  border-color: color-mix(in srgb, var(--theme-primary) 28%, transparent);
  box-shadow: var(--theme-shadow-lg);
}

/* Cover */
.note-card__cover {
  position: relative;
  width: 100%;
  aspect-ratio: 4 / 3;
  background: var(--theme-bg-surface-2);
  overflow: hidden;
}

.note-card__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.6s cubic-bezier(0.22, 1, 0.36, 1);
}

.note-card:hover .note-card__cover img {
  transform: scale(1.07);
}

.note-card__scrim {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.18) 0%, transparent 38%, rgba(0, 0, 0, 0.28) 100%);
  pointer-events: none;
}

.note-card__placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--theme-text-3);
}

/* Badges */
.note-card__badge {
  position: absolute;
  top: 10px;
  left: 10px;
  min-width: 22px;
  height: 22px;
  padding: 0 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.02em;
  backdrop-filter: blur(6px);
}

.note-card__badge--featured {
  background: var(--theme-warning);
  box-shadow: 0 6px 14px -6px color-mix(in srgb, var(--theme-warning) 90%, transparent);
}

.note-card__badge--top {
  left: auto;
  right: 10px;
  background: var(--theme-danger);
  box-shadow: 0 6px 14px -6px color-mix(in srgb, var(--theme-danger) 90%, transparent);
}

/* Body */
.note-card__body {
  padding: 14px 15px 13px;
}

.note-card__title {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.42;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-card__summary {
  margin: 7px 0 0;
  font-size: 13px;
  line-height: 1.55;
  color: var(--theme-text-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-card__tags {
  margin-top: 9px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.note-card__tag {
  font-size: 12px;
  line-height: 1;
  padding: 4px 9px;
  border-radius: 999px;
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
}

.note-card__meta {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: var(--theme-text-3);
}

.note-card__dest,
.note-card__days {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.note-card__days {
  padding: 2px 8px;
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
}

/* Footer */
.note-card__footer {
  margin-top: 12px;
  padding-top: 11px;
  border-top: 1px solid var(--theme-hairline);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.note-card__author {
  display: flex;
  align-items: center;
  gap: 7px;
  min-width: 0;
}

.note-card__username {
  font-size: 12px;
  color: var(--theme-text-2);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.note-card__stats {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: var(--theme-text-3);
  flex-shrink: 0;
}

.note-card__stats span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}
</style>
