<template>
  <div v-if="carouselList.length" class="glass-card announcement-carousel">
    <el-carousel
      :interval="5000"
      height="140px"
      :arrow="carouselList.length > 1 ? 'hover' : 'never'"
      :indicator-position="carouselList.length > 1 ? 'outside' : 'none'"
    >
      <el-carousel-item v-for="item in carouselList" :key="item.id">
        <div class="ann-slide" @click="goDetail(item.id)">
          <div v-if="item.coverImage" class="ann-media">
            <img :src="item.coverImage" :alt="item.title" loading="lazy">
          </div>

          <div class="ann-body">
            <div class="ann-tags">
              <span class="ann-tag" :class="`is-type-${item.type}`">{{ typeText(item.type) }}</span>
              <span v-if="item.level > 1" class="ann-tag" :class="item.level === 3 ? 'is-urgent' : 'is-important'">
                {{ levelText(item.level) }}
              </span>
              <span v-if="item.isTop === 1" class="ann-tag is-top">置顶</span>
              <span v-if="isUnread(item.id)" class="ann-unread" title="未读"></span>
            </div>

            <h4 class="ann-title">{{ item.title }}</h4>
            <p class="ann-summary">{{ summaryText(item) }}</p>

            <div class="ann-meta">
              <span v-if="item.creatorName" class="ann-meta-item is-author">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <circle cx="12" cy="8" r="3.4" />
                  <path d="M5 19.5c0-3.2 3.1-5.2 7-5.2s7 2 7 5.2" />
                </svg>
                {{ item.creatorName }}
              </span>
              <span v-if="publishText(item)" class="ann-meta-item">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <circle cx="12" cy="12" r="8.5" />
                  <path d="M12 7.5V12l3 2" />
                </svg>
                {{ publishText(item) }}
              </span>
              <span class="ann-meta-item is-read">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M2.5 12S6 5.8 12 5.8 21.5 12 21.5 12 18 18.2 12 18.2 2.5 12 2.5 12Z" />
                  <circle cx="12" cy="12" r="2.8" />
                </svg>
                {{ readCount(item) }}
              </span>
              <span v-if="item.expireTime" class="ann-meta-item is-expire">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <rect x="3.5" y="5" width="17" height="15.5" rx="2.5" />
                  <path d="M3.5 10h17M8 3.5v3M16 3.5v3" />
                </svg>
                截止 {{ formatDate(item.expireTime, false) }}
              </span>

              <span class="ann-cta">
                查看详情
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 5l7 7-7 7" /></svg>
              </span>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <AnnouncementDetailModal v-model:visible="detailVisible" :id="detailId" />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAnnouncementStore } from '@/stores/announcementStore'
import AnnouncementDetailModal from './AnnouncementDetailModal.vue'

const store = useAnnouncementStore()

const detailVisible = ref(false)
const detailId = ref(null)

const carouselList = computed(() =>
  (store.activeList || []).filter((a) => a.displayMode === 3).slice(0, 5)
)

const TYPE_TEXT = { 1: '系统公告', 2: '活动通知', 3: '维护通知', 4: '版本更新' }

function typeText(type) {
  return TYPE_TEXT[Number(type)] || '公告'
}

function levelText(level) {
  return { 2: '重要', 3: '紧急' }[Number(level)] || '普通'
}

// 未读状态以本地已读缓存为准，打开详情后即时消除红点
function isUnread(id) {
  return !store.readIds.has(Number(id))
}

// 摘要优先取 summary，其次剥离 content 中的 HTML 标签
function summaryText(item) {
  const text = item.summary || stripHtml(item.content)
  return text || '点击查看公告详情'
}

function stripHtml(html) {
  if (!html) return ''
  return String(html).replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim().slice(0, 80)
}

function readCount(item) {
  const count = Number(item.readCount ?? item.viewCount ?? 0)
  return Number.isFinite(count) ? count : 0
}

function publishText(item) {
  return formatDate(item.publishTime || item.createTime, true)
}

function formatDate(value, withTime) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''

  const pad = (num) => String(num).padStart(2, '0')
  const day = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
  return withTime ? `${day} ${pad(date.getHours())}:${pad(date.getMinutes())}` : day
}

function goDetail(id) {
  detailId.value = id
  detailVisible.value = true
}
</script>

<style scoped>
/* ============================================================
   公告轮播卡片
   主题变量随 html[data-theme] 切换（light / dark / game）
   ============================================================ */
.announcement-carousel {
  --ann-bg: var(--theme-bg-glass);
  --ann-border: var(--theme-border);
  --ann-shadow: var(--theme-shadow-lg);
  --ann-title: var(--theme-text-1);
  --ann-text: var(--theme-text-2);
  --ann-muted: var(--theme-text-3);
  --ann-accent: var(--theme-primary);
  --ann-thumb: var(--theme-bg-muted);
  --ann-track: var(--theme-bg-active);
  --ann-nav-bg: var(--theme-bg-glass-strong);
  --ann-nav-border: var(--theme-border);
  --ann-nav-color: var(--theme-text-1);
  --ann-warn: var(--theme-warning);

  position: relative;
  margin: 12px auto;
  padding: 8px 16px 10px;
  overflow: hidden;
  border: 1px solid var(--ann-border);
  border-radius: 18px;
  background: var(--ann-bg);
  box-shadow: var(--ann-shadow);
  -webkit-backdrop-filter: blur(20px);
  backdrop-filter: blur(20px);
  transition: background 0.32s ease, border-color 0.32s ease, box-shadow 0.32s ease;
}

/* 顶部强调细线 */
.announcement-carousel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  pointer-events: none;
  background: linear-gradient(
    90deg,
    transparent,
    color-mix(in srgb, var(--ann-accent) 60%, transparent),
    transparent
  );
}

/* ---------- 单条公告 ---------- */
.ann-slide {
  display: flex;
  align-items: center;
  gap: 18px;
  height: 100%;
  padding: 6px 8px;
  border-radius: 14px;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1), background 0.3s ease;
}

.ann-slide:hover {
  transform: translateY(-2px);
  background: color-mix(in srgb, var(--ann-accent) 7%, transparent);
}

.ann-media {
  flex: none;
  width: 132px;
  height: 92px;
  overflow: hidden;
  border-radius: 12px;
  background: var(--ann-thumb);
  box-shadow: var(--theme-shadow-md);
}

.ann-media img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.22, 1, 0.36, 1);
}

.ann-slide:hover .ann-media img {
  transform: scale(1.06);
}

.ann-body {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 6px;
}

/* ---------- 标签 ---------- */
.ann-tags {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ann-tag {
  --ann-tag: var(--ann-accent);

  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 8px;
  border: 1px solid color-mix(in srgb, var(--ann-tag) 34%, transparent);
  border-radius: 6px;
  background: color-mix(in srgb, var(--ann-tag) 14%, transparent);
  color: var(--ann-tag);
  font-size: 12px;
  line-height: 1;
  white-space: nowrap;
}

.ann-tag.is-type-1 {
  --ann-tag: var(--theme-info);
}

.ann-tag.is-type-2 {
  --ann-tag: var(--theme-warning);
}

.ann-tag.is-type-3 {
  --ann-tag: var(--theme-danger);
}

.ann-tag.is-type-4 {
  --ann-tag: var(--theme-success);
}

.ann-tag.is-important {
  --ann-tag: var(--theme-warning);
}

.ann-tag.is-urgent {
  --ann-tag: var(--theme-danger);
}

.ann-tag.is-top {
  border-color: transparent;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-weight: 500;
}

.ann-unread {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--theme-danger);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--theme-danger) 20%, transparent);
}

/* ---------- 文案 ---------- */
.ann-title {
  margin: 0;
  overflow: hidden;
  color: var(--ann-title);
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.ann-summary {
  margin: 0;
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  color: var(--ann-text);
  font-size: 13px;
  line-height: 1.6;
  transition: color 0.3s ease;
}

/* ---------- 元信息 ---------- */
.ann-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
  color: var(--ann-muted);
  font-size: 12px;
  transition: color 0.3s ease;
}

.ann-meta-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  white-space: nowrap;
}

.ann-meta-item svg {
  width: 13px;
  height: 13px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.6;
  stroke-linecap: round;
  stroke-linejoin: round;
  opacity: 0.85;
}

.ann-meta-item.is-expire {
  color: var(--ann-warn);
}

.ann-cta {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  margin-left: auto;
  color: var(--ann-accent);
  font-weight: 500;
  white-space: nowrap;
}

.ann-cta svg {
  width: 13px;
  height: 13px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  transition: transform 0.25s ease;
}

.ann-slide:hover .ann-cta svg {
  transform: translateX(3px);
}

/* ---------- Element Plus 轮播外观 ---------- */
/* 前缀 .announcement-carousel 用于提升优先级，覆盖 Element Plus 自身样式 */
.announcement-carousel :deep(.el-carousel__indicators--outside) {
  margin-top: 6px;
}

.announcement-carousel :deep(.el-carousel__indicators--outside .el-carousel__button) {
  width: 18px;
  height: 3px;
  border-radius: 2px;
  background: var(--ann-track);
  opacity: 1;
  transition: width 0.3s ease, background 0.3s ease;
}

.announcement-carousel :deep(.el-carousel__indicators--outside .el-carousel__indicator.is-active .el-carousel__button) {
  width: 30px;
  background: var(--ann-accent);
}

.announcement-carousel :deep(.el-carousel__container .el-carousel__arrow) {
  width: 34px;
  height: 34px;
  border: 1px solid var(--ann-nav-border);
  background: var(--ann-nav-bg);
  color: var(--ann-nav-color);
  box-shadow: var(--theme-shadow-md);
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
  transition: background 0.25s ease, transform 0.25s ease;
}

.announcement-carousel :deep(.el-carousel__container .el-carousel__arrow:hover) {
  background: var(--ann-nav-bg);
  transform: scale(1.08);
}

/* ---------- 响应式 ---------- */
@media (max-width: 640px) {
  .announcement-carousel {
    padding: 6px 12px 8px;
    border-radius: 14px;
  }

  .ann-slide {
    gap: 12px;
    padding: 4px 4px;
  }

  .ann-media {
    width: 92px;
    height: 70px;
  }

  .ann-title {
    font-size: 15px;
  }

  .ann-summary {
    -webkit-line-clamp: 1;
  }

  .ann-meta {
    gap: 10px;
  }

  /* 窄屏隐藏次要字段，避免换行溢出 */
  .ann-meta-item.is-author,
  .ann-meta-item.is-read,
  .ann-cta {
    display: none;
  }
}
</style>
