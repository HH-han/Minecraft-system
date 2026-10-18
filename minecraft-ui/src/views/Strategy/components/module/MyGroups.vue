<template>
  <div class="tab-content">
    <div class="section-head">
      <div class="head-copy">
        <span class="eyebrow">{{ t('strategy.myGroups.eyebrow') }}</span>
        <h2>{{ t('strategy.myGroups.title') }}</h2>
        <p class="head-sub">{{ t('strategy.myGroups.subtitle') }}</p>
      </div>

      <div class="search-bar">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <circle cx="11" cy="11" r="7" />
          <path d="M20 20l-3.6-3.6" />
        </svg>
        <input
          v-model="localKeyword"
          type="text"
          :placeholder="t('strategy.myGroups.searchPlaceholder')"
          @keyup.enter="$emit('search')"
        />
        <button type="button" :aria-label="t('strategy.myGroups.searchPlaceholder')" @click="$emit('search')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M5 12h14M13 6l6 6-6 6" />
          </svg>
        </button>
      </div>
    </div>

    <div v-if="loading" class="group-grid">
      <div v-for="n in 3" :key="n" class="group-card skeleton-card" aria-hidden="true">
        <div class="skeleton-media"></div>
        <div class="skeleton-body">
          <span class="skeleton-line w-60"></span>
          <span class="skeleton-line w-40"></span>
          <span class="skeleton-line w-80"></span>
        </div>
      </div>
    </div>

    <div v-else-if="groups.length === 0" class="empty-state">
      <div class="empty-art">
        <svg viewBox="0 0 120 120" aria-hidden="true">
          <circle cx="60" cy="60" r="54" />
          <path d="M22 84l24-30 16 19 12-15 24 26" />
          <circle cx="82" cy="36" r="8" />
        </svg>
      </div>
      <h3>{{ t('strategy.myGroups.emptyTitle') }}</h3>
      <p>{{ t('strategy.myGroups.emptyDesc') }}</p>
      <button class="btn-primary" type="button" @click="$emit('create')">
        {{ t('strategy.myGroups.create') }}
      </button>
    </div>

    <div v-else class="group-grid">
      <article
        v-for="(group, index) in groups"
        :key="group.id"
        class="group-card"
        :style="{ '--i': index }"
        @click="$emit('detail', group)"
      >
        <div class="card-media">
          <img :src="group.coverUrl || defaultCover" :alt="group.name" loading="lazy" />
          <span :class="['status-badge', group.status === 1 ? 'active' : 'ended']">
            <i class="status-dot"></i>
            {{ group.status === 1 ? t('strategy.myGroups.statusActive') : t('strategy.myGroups.statusEnded') }}
          </span>
          <span class="media-arrow" aria-hidden="true">
            <svg viewBox="0 0 24 24">
              <path d="M7 17L17 7M9 7h8v8" />
            </svg>
          </span>
        </div>

        <div class="card-content">
          <div class="card-title-row">
            <h3>{{ group.name }}</h3>
            <span v-if="group.userRole" :class="['role-tag', group.userRole]">
              {{ getRoleName(group.userRole) }}
            </span>
          </div>

          <p class="destination">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M12 21s7-5.5 7-11a7 7 0 1 0-14 0c0 5.5 7 11 7 11Z" />
              <circle cx="12" cy="10" r="2.5" />
            </svg>
            {{ group.destination || t('strategy.myGroups.noDestination') }}
          </p>

          <div class="card-meta">
            <span class="member-count">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <circle cx="9" cy="8" r="3.2" />
                <path d="M3.5 19a5.5 5.5 0 0 1 11 0" />
                <path d="M16 5.6a3 3 0 0 1 0 5.8M17.6 14.4A5 5 0 0 1 20.5 19" />
              </svg>
              {{ t('strategy.myGroups.memberCount', { current: group.currentMembers || 0, max: group.maxMembers || 20 }) }}
            </span>
            <span v-if="group.startDate" class="date-range">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <rect x="3" y="5" width="18" height="16" rx="3" />
                <path d="M8 3v4M16 3v4M3 10h18" />
              </svg>
              {{ formatDate(group.startDate) }} – {{ formatDate(group.endDate) }}
            </span>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  groups: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  searchKeyword: {
    type: String,
    default: ''
  },
  defaultCover: {
    type: String,
    default: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800'
  }
})

const emit = defineEmits(['search', 'create', 'detail', 'update:searchKeyword'])

const { t, locale } = useI18n()

const localKeyword = ref(props.searchKeyword)

watch(() => props.searchKeyword, (val) => {
  localKeyword.value = val
})

watch(localKeyword, (val) => {
  emit('update:searchKeyword', val)
})

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString(locale.value, { month: 'short', day: 'numeric' })
}

function getRoleName(role) {
  return t(`strategy.roles.${role}`)
}
</script>

<style scoped>
.tab-content {
  animation: fade-in var(--transition-normal);
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 30px;
}

.eyebrow {
  display: block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.24em;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.head-copy h2 {
  font-size: clamp(24px, 2.6vw, 32px);
  font-weight: 750;
  line-height: 1.15;
  letter-spacing: -0.02em;
  color: var(--color-text);
}

.head-sub {
  margin-top: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 6px 6px 16px;
  min-width: 300px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  transition: border-color var(--transition-normal), box-shadow var(--transition-normal);
}

.search-bar:focus-within {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-sm);
}

.search-bar > svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  fill: none;
  stroke: var(--color-text-secondary);
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.search-bar input {
  flex: 1;
  min-width: 0;
  border: none;
  background: transparent;
  padding: 8px 6px;
  font-family: inherit;
  font-size: 14px;
  color: var(--color-text);
  outline: none;
}

.search-bar input::placeholder {
  color: var(--color-text-secondary);
}

.search-bar button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  border: none;
  border-radius: 50%;
  background: var(--color-primary);
  color: var(--theme-primary-text);
  cursor: pointer;
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.search-bar button:hover {
  background: var(--color-primary-hover);
}

.search-bar button:active {
  transform: scale(0.94);
}

.search-bar button svg {
  width: 16px;
  height: 16px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.group-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 22px;
}

.group-card {
  position: relative;
  background: var(--theme-bg-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--transition-slow), box-shadow var(--transition-slow),
    border-color var(--transition-slow);
  animation: card-in 0.5s var(--theme-ease) both;
  animation-delay: calc(var(--i, 0) * 60ms);
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.group-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
  border-color: var(--theme-border-strong);
}

.card-media {
  position: relative;
  aspect-ratio: 16 / 10;
  overflow: hidden;
  background: var(--color-bg-secondary);
}

.card-media::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 45%, rgba(0, 0, 0, 0.55) 100%);
  pointer-events: none;
}

.card-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s var(--theme-ease);
}

.group-card:hover .card-media img {
  transform: scale(1.06);
}

.status-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 11px;
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  background: rgba(12, 15, 20, 0.5);
  backdrop-filter: blur(10px);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.status-badge.active {
  color: #d1fae5;
}

.status-badge.active .status-dot {
  background: #34d399;
  box-shadow: 0 0 0 3px rgba(52, 211, 153, 0.25);
}

.status-badge.ended {
  color: rgba(255, 255, 255, 0.62);
}

.status-badge.ended .status-dot {
  background: rgba(255, 255, 255, 0.5);
}

.media-arrow {
  position: absolute;
  right: 12px;
  bottom: 12px;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  color: #111827;
  opacity: 0;
  transform: translateY(6px);
  transition: opacity var(--transition-normal), transform var(--transition-normal);
}

.group-card:hover .media-arrow {
  opacity: 1;
  transform: translateY(0);
}

.media-arrow svg {
  width: 16px;
  height: 16px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.9;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.card-content {
  padding: 20px 20px 18px;
}

.card-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.card-content h3 {
  font-size: 18px;
  font-weight: 650;
  line-height: 1.3;
  color: var(--color-text);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.role-tag {
  flex-shrink: 0;
  padding: 3px 9px;
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: 600;
  line-height: 1.5;
}

.role-tag.owner {
  background: var(--theme-warning-soft);
  color: var(--color-warning);
}

.role-tag.admin {
  background: var(--theme-primary-soft);
  color: var(--color-primary);
}

.role-tag.member {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
}

.destination {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 16px;
}

.destination svg,
.member-count svg,
.date-range svg {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.destination svg {
  color: var(--color-primary);
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid var(--color-border);
  font-size: 12px;
  color: var(--color-text-secondary);
}

.member-count,
.date-range {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  white-space: nowrap;
}

/* 骨架屏 */
.skeleton-card {
  cursor: default;
  animation: none;
}

.skeleton-card:hover {
  transform: none;
  box-shadow: none;
  border-color: var(--color-border);
}

.skeleton-media,
.skeleton-line {
  background: linear-gradient(
    90deg,
    var(--color-bg-secondary) 25%,
    var(--theme-bg-hover) 37%,
    var(--color-bg-secondary) 63%
  );
  background-size: 400% 100%;
  animation: shimmer 1.4s ease infinite;
}

.skeleton-media {
  aspect-ratio: 16 / 10;
}

.skeleton-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-line {
  height: 12px;
  border-radius: 6px;
}

.skeleton-line.w-60 {
  width: 60%;
  height: 16px;
}

.skeleton-line.w-40 {
  width: 40%;
}

.skeleton-line.w-80 {
  width: 80%;
}

@keyframes shimmer {
  from {
    background-position: 100% 50%;
  }
  to {
    background-position: 0 50%;
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 64px 32px;
  background: var(--color-bg-secondary);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-xl);
}

.empty-art svg {
  width: 96px;
  height: 96px;
  margin-bottom: 20px;
  fill: none;
  stroke: var(--color-primary);
  stroke-width: 1.6;
  stroke-linecap: round;
  stroke-linejoin: round;
  opacity: 0.7;
}

.empty-state h3 {
  font-size: 19px;
  font-weight: 650;
  color: var(--color-text);
  margin-bottom: 6px;
}

.empty-state p {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 22px;
}

.btn-primary {
  padding: 11px 24px;
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: var(--theme-primary-text);
  font-family: inherit;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
  transform: translateY(-1px);
}

.btn-primary:active {
  transform: scale(0.97);
}

@media (max-width: 1199px) and (min-width: 768px) {
  .section-head {
    align-items: flex-start;
    flex-direction: column;
    gap: 16px;
  }

  .search-bar {
    min-width: 0;
    width: 100%;
  }
}

@media (max-width: 767px) {
  .section-head {
    align-items: stretch;
    flex-direction: column;
    gap: 14px;
    margin-bottom: 20px;
  }

  .search-bar {
    min-width: 0;
    width: 100%;
  }

  .group-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .card-content {
    padding: 16px;
  }

  .empty-state {
    padding: 44px 22px;
  }
}
</style>
