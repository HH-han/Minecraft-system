<template>
  <section v-if="carouselList.length" class="announcement-carousel" aria-labelledby="announcement-heading">
    <!-- 头部：标题 + 轮播控制 + 全部公告入口 -->
    <header class="ann-head">
      <div class="ann-head-main">
        <span class="ann-head-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24">
            <path d="M6 9.5v5a2 2 0 0 0 2 2h1.3l3.8 3.1c.6.5 1.6.1 1.6-.8V5.2c0-.9-1-1.3-1.6-.8L9.3 8.5H8a2 2 0 0 0-2 1Z" />
            <path d="M17.6 9.4a3.6 3.6 0 0 1 0 5.2M20 7a7 7 0 0 1 0 10" />
          </svg>
        </span>

        <div class="ann-head-copy">
          <span class="ann-kicker">{{ t('announcement.carousel.kicker') }}</span>
          <div class="ann-title-row">
            <h3 id="announcement-heading">{{ t('announcement.carousel.title') }}</h3>
            <p class="ann-subtitle">{{ t('announcement.carousel.subtitle') }}</p>
          </div>
        </div>
      </div>

      <div class="ann-head-actions">
        <div v-if="carouselList.length > 1" class="ann-dots">
          <button
            v-for="(item, index) in carouselList"
            :key="item.id"
            type="button"
            class="ann-dot"
            :class="{ 'is-active': index === activeIndex }"
            :aria-label="t('announcement.carousel.goTo', { index: index + 1 })"
            :aria-current="index === activeIndex"
            @click="setActive(index)"
          />
        </div>

        <span v-if="carouselList.length > 1" class="ann-counter">
          <b>{{ pad(activeIndex + 1) }}</b> / {{ pad(carouselList.length) }}
        </span>

        <router-link class="ann-more" to="/announcement">
          {{ t('announcement.carousel.viewAll') }}
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 5l7 7-7 7" /></svg>
        </router-link>
      </div>
    </header>

    <el-carousel
      ref="carouselRef"
      class="ann-carousel"
      height="var(--ann-slide-h)"
      :interval="6000"
      :autoplay="carouselList.length > 1"
      :arrow="carouselList.length > 1 ? 'hover' : 'never'"
      indicator-position="none"
      @change="onChange"
    >
      <el-carousel-item v-for="(item, index) in carouselList" :key="item.id">
        <article
          class="ann-slide"
          role="button"
          tabindex="0"
          :aria-label="item.title"
          @click="goDetail(item.id)"
          @keydown.enter="goDetail(item.id)"
        >
          <!-- 封面：无图时用图标占位，保持左右结构稳定 -->
          <div class="ann-media">
            <img v-if="item.coverImage" :src="item.coverImage" :alt="item.title" loading="lazy">
            <span v-else class="ann-media-fallback" aria-hidden="true">
              <svg viewBox="0 0 24 24">
                <path d="M4 6.5h16v13H4z" />
                <path d="M4 10.5h16M8 4v3M16 4v3" />
              </svg>
            </span>
            <span class="ann-media-shade" aria-hidden="true" />
            <span class="ann-media-index" aria-hidden="true">{{ pad(index + 1) }}</span>
          </div>

          <div class="ann-body">
            <div class="ann-tags">
              <span class="ann-tag" :class="`is-type-${Number(item.type)}`">{{ typeText(item.type) }}</span>
              <span
                v-if="Number(item.level) > 1"
                class="ann-tag"
                :class="Number(item.level) === 3 ? 'is-urgent' : 'is-important'"
              >
                {{ levelText(item.level) }}
              </span>
              <span v-if="Number(item.isTop) === 1" class="ann-tag is-top">{{ t('announcement.carousel.top') }}</span>
              <span v-if="isUnread(item.id)" class="ann-unread" :title="t('announcement.carousel.unread')" />
            </div>

            <h4 class="ann-title">{{ item.title }}</h4>
            <p class="ann-summary">{{ summaryText(item) }}</p>

            <div class="ann-foot">
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
                  {{ t('announcement.carousel.readCount', { count: readCount(item) }) }}
                </span>
                <span v-if="item.expireTime" class="ann-meta-item is-expire">
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <rect x="3.5" y="5" width="17" height="15.5" rx="2.5" />
                    <path d="M3.5 10h17M8 3.5v3M16 3.5v3" />
                  </svg>
                  {{ t('announcement.carousel.deadline', { date: formatDate(item.expireTime, 'short') }) }}
                </span>
              </div>

              <span class="ann-cta">
                {{ t('announcement.carousel.detail') }}
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 5l7 7-7 7" /></svg>
              </span>
            </div>
          </div>
        </article>
      </el-carousel-item>
    </el-carousel>

    <AnnouncementDetailModal v-model:visible="detailVisible" :id="detailId" />
  </section>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAnnouncementStore } from '@/stores/announcementStore'
import AnnouncementDetailModal from './AnnouncementDetailModal.vue'

const store = useAnnouncementStore()
const { t, te, d } = useI18n()

const detailVisible = ref(false)
const detailId = ref(null)
const carouselRef = ref(null)
const activeIndex = ref(0)

const carouselList = computed(() =>
  (store.activeList || []).filter((a) => Number(a.displayMode) === 3).slice(0, 5)
)

function pad(num) {
  return String(num).padStart(2, '0')
}

function onChange(index) {
  activeIndex.value = index
}

function setActive(index) {
  carouselRef.value?.setActiveItem(index)
  activeIndex.value = index
}

function typeText(type) {
  const key = `announcement.carousel.type.${Number(type)}`
  return te(key) ? t(key) : t('announcement.carousel.type.0')
}

function levelText(level) {
  const key = `announcement.carousel.level.${Number(level)}`
  return te(key) ? t(key) : t('announcement.carousel.level.1')
}

// 未读状态以本地已读缓存为准，打开详情后即时消除红点
function isUnread(id) {
  return !store.readIds.has(Number(id))
}

// 摘要优先取 summary，其次剥离 content 中的 HTML 标签
function summaryText(item) {
  const text = item.summary || stripHtml(item.content)
  return text || t('announcement.carousel.detail')
}

function stripHtml(html) {
  if (!html) return ''
  return String(html).replace(/<[^>]+>/g, '').replace(/\s+/g, ' ').trim().slice(0, 120)
}

function readCount(item) {
  const count = Number(item.readCount ?? item.viewCount ?? 0)
  return Number.isFinite(count) ? count : 0
}

function publishText(item) {
  return formatDate(item.publishTime || item.createTime, 'long')
}

// 复用 vue-i18n 的日期格式：long 带时间，short 仅日期
function formatDate(value, format) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  return d(date, format)
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
  --ann-slide-h: 244px;

  position: relative;
  margin: 16px auto 44px;
  padding: 18px 20px 20px;
  overflow: hidden;
  border: 1px solid var(--ann-border);
  border-radius: 22px;
  background:
    radial-gradient(120% 150% at 0% 0%, color-mix(in srgb, var(--ann-accent) 9%, transparent), transparent 58%),
    var(--ann-bg);
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
    color-mix(in srgb, var(--ann-accent) 62%, transparent),
    transparent
  );
}

/* ---------- 头部 ---------- */
.ann-head {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 2px 4px 16px;
}

.ann-head-main {
  display: flex;
  align-items: center;
  gap: 13px;
  flex: 1;
  min-width: 0;
}

.ann-head-icon {
  flex: none;
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: 13px;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  box-shadow:
    0 8px 18px -8px color-mix(in srgb, var(--ann-accent) 65%, transparent),
    inset 0 1px 0 rgba(255, 255, 255, 0.25);
}

.ann-head-icon svg {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.ann-head-copy {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.ann-kicker {
  color: var(--ann-muted);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.22em;
  line-height: 1.2;
  text-transform: uppercase;
}

.ann-title-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  min-width: 0;
}

.ann-head h3 {
  margin: 0;
  color: var(--ann-title);
  font-size: 19px;
  font-weight: 700;
  letter-spacing: 0.01em;
  line-height: 1.3;
  white-space: nowrap;
}

.ann-subtitle {
  margin: 0;
  overflow: hidden;
  color: var(--ann-muted);
  font-size: 12.5px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ann-head-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: none;
}

/* 页码点 */
.ann-dots {
  display: flex;
  align-items: center;
  gap: 6px;
}

.ann-dot {
  width: 6px;
  height: 6px;
  padding: 0;
  border: 0;
  border-radius: 999px;
  background: var(--ann-track);
  cursor: pointer;
  transition: width 0.3s var(--theme-ease), background 0.3s ease;
}

.ann-dot:hover {
  background: color-mix(in srgb, var(--ann-accent) 55%, transparent);
}

.ann-dot.is-active {
  width: 22px;
  background: var(--theme-gradient);
}

.ann-counter {
  color: var(--ann-muted);
  font-size: 12px;
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.08em;
}

.ann-counter b {
  color: var(--ann-title);
  font-weight: 600;
}

.ann-more {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 7px 13px;
  border: 1px solid var(--ann-border);
  border-radius: 999px;
  background: var(--theme-bg-muted);
  color: var(--ann-text);
  font-size: 12.5px;
  font-weight: 500;
  text-decoration: none;
  white-space: nowrap;
  transition: color 0.25s ease, background 0.25s ease, border-color 0.25s ease;
}

.ann-more:hover {
  border-color: color-mix(in srgb, var(--ann-accent) 40%, var(--ann-border));
  background: color-mix(in srgb, var(--ann-accent) 10%, transparent);
  color: var(--ann-accent);
}

.ann-more svg {
  width: 12px;
  height: 12px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  transition: transform 0.25s ease;
}

.ann-more:hover svg {
  transform: translateX(3px);
}

/* ---------- 单条公告 ---------- */
.ann-slide {
  display: grid;
  grid-template-columns: minmax(0, 34%) minmax(0, 1fr);
  height: 100%;
  overflow: hidden;
  border: 1px solid var(--ann-border);
  border-radius: 16px;
  background: color-mix(in srgb, var(--theme-bg-elevated) 55%, transparent);
  cursor: pointer;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.ann-slide:hover {
  border-color: color-mix(in srgb, var(--ann-accent) 42%, var(--ann-border));
  box-shadow: 0 18px 40px -26px color-mix(in srgb, var(--ann-accent) 55%, transparent);
}

.ann-slide:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

/* 封面 */
.ann-media {
  position: relative;
  height: 100%;
  min-width: 0;
  overflow: hidden;
  background: var(--ann-thumb);
}

.ann-media img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s var(--theme-ease);
}

.ann-slide:hover .ann-media img {
  transform: scale(1.06);
}

.ann-media-shade {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(to right, transparent 72%, color-mix(in srgb, var(--theme-bg-elevated) 45%, transparent)),
    linear-gradient(to top, rgba(0, 0, 0, 0.38), transparent 52%);
  pointer-events: none;
}

.ann-media-index {
  position: absolute;
  left: 14px;
  bottom: 10px;
  color: rgba(255, 255, 255, 0.82);
  font-size: 34px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.04em;
  line-height: 1;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.35);
  pointer-events: none;
}

.ann-media-fallback {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  background:
    radial-gradient(120% 120% at 20% 20%, color-mix(in srgb, var(--ann-accent) 16%, transparent), transparent 62%),
    var(--ann-thumb);
  color: color-mix(in srgb, var(--ann-accent) 58%, transparent);
}

.ann-media-fallback svg {
  width: 44px;
  height: 44px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.4;
  stroke-linecap: round;
  stroke-linejoin: round;
}

/* 正文 */
.ann-body {
  display: flex;
  flex-direction: column;
  gap: 9px;
  min-width: 0;
  height: 100%;
  padding: 22px 26px;
}

/* ---------- 标签 ---------- */
.ann-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
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
  animation: ann-pulse 2s ease-out infinite;
}

/* ---------- 文案 ---------- */
.ann-title {
  display: -webkit-box;
  margin: 0;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  color: var(--ann-title);
  font-size: 19px;
  font-weight: 650;
  line-height: 1.35;
  transition: color 0.3s ease;
}

.ann-slide:hover .ann-title {
  color: var(--ann-accent);
}

.ann-summary {
  display: -webkit-box;
  flex: 1;
  margin: 0;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  color: var(--ann-text);
  font-size: 13px;
  line-height: 1.7;
  transition: color 0.3s ease;
}

/* ---------- 底部元信息 ---------- */
.ann-foot {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: auto;
  padding-top: 11px;
  border-top: 1px solid var(--theme-hairline);
}

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
  gap: 4px;
  margin-left: auto;
  color: var(--ann-accent);
  font-size: 12.5px;
  font-weight: 600;
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
.ann-carousel :deep(.el-carousel__container) {
  border-radius: 16px;
}

.ann-carousel :deep(.el-carousel__item) {
  border-radius: 16px;
  overflow: hidden;
}

.ann-carousel :deep(.el-carousel__arrow) {
  width: 32px;
  height: 32px;
  border: 1px solid var(--ann-nav-border);
  background: var(--ann-nav-bg);
  color: var(--ann-nav-color);
  box-shadow: var(--theme-shadow-md);
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
  transition: background 0.25s ease, transform 0.25s ease;
}

.ann-carousel :deep(.el-carousel__arrow:hover) {
  background: var(--ann-nav-bg);
  transform: scale(1.08);
}

@keyframes ann-pulse {
  0% {
    box-shadow: 0 0 0 0 color-mix(in srgb, var(--theme-danger) 42%, transparent);
  }
  70% {
    box-shadow: 0 0 0 6px transparent;
  }
  100% {
    box-shadow: 0 0 0 0 transparent;
  }
}

/* ---------- 响应式 ---------- */
@media (max-width: 992px) {
  .announcement-carousel {
    --ann-slide-h: 232px;
  }

  .ann-slide {
    grid-template-columns: minmax(0, 38%) minmax(0, 1fr);
  }

  .ann-subtitle {
    display: none;
  }

  .ann-meta-item.is-author {
    display: none;
  }

  .ann-body {
    padding: 20px 22px;
  }
}

@media (max-width: 768px) {
  .announcement-carousel {
    --ann-slide-h: 208px;

    width: calc(100% - 32px);
    margin-bottom: 32px;
    padding: 14px 14px 16px;
    border-radius: 18px;
  }

  .ann-head {
    gap: 10px;
    padding: 2px 2px 12px;
  }

  .ann-head-icon {
    width: 36px;
    height: 36px;
    border-radius: 11px;
  }

  .ann-head-icon svg {
    width: 18px;
    height: 18px;
  }

  .ann-kicker {
    display: none;
  }

  .ann-head h3 {
    font-size: 17px;
  }

  .ann-dots,
  .ann-counter {
    display: none;
  }

  .ann-more {
    padding: 6px 11px;
    font-size: 12px;
  }

  .ann-slide {
    grid-template-columns: 112px minmax(0, 1fr);
  }

  .ann-body {
    gap: 7px;
    padding: 15px 15px;
  }

  .ann-title {
    font-size: 16px;
  }

  .ann-summary {
    font-size: 12.5px;
  }

  .ann-meta {
    gap: 10px;
  }

  /* 窄屏隐藏次要字段，避免换行溢出 */
  .ann-meta-item.is-author,
  .ann-meta-item.is-read,
  .ann-meta-item.is-expire,
  .ann-cta {
    display: none;
  }

  .ann-media-index {
    left: 10px;
    bottom: 8px;
    font-size: 24px;
  }

  .ann-media-fallback svg {
    width: 34px;
    height: 34px;
  }
}

@media (max-width: 480px) {
  .announcement-carousel {
    --ann-slide-h: 172px;

    width: calc(100% - 24px);
  }

  .ann-slide {
    grid-template-columns: 92px minmax(0, 1fr);
  }

  .ann-body {
    padding: 12px 12px;
  }

  .ann-tag {
    height: 18px;
    padding: 0 6px;
    font-size: 11px;
  }

  .ann-title {
    font-size: 15px;
  }

  .ann-summary {
    -webkit-line-clamp: 1;
  }
}

/* ---------- 无障碍：尊重系统动效偏好 ---------- */
@media (prefers-reduced-motion: reduce) {
  .ann-slide,
  .ann-slide:hover,
  .ann-media img,
  .ann-slide:hover .ann-media img,
  .ann-cta svg,
  .ann-slide:hover .ann-cta svg,
  .ann-unread {
    transition: none;
    transform: none;
    animation: none;
  }
}
</style>
