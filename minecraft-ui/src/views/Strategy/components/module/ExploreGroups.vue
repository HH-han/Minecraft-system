<template>
  <div class="tab-content">
    <div class="section-head">
      <div class="head-copy">
        <span class="eyebrow">{{ t('strategy.explore.eyebrow') }}</span>
        <h2>{{ t('strategy.explore.title') }}</h2>
        <p class="head-sub">{{ t('strategy.explore.subtitle') }}</p>
      </div>

      <div class="filter-tabs">
        <button
          v-for="filter in filters"
          :key="filter.id"
          type="button"
          :class="['filter-btn', { active: activeFilter === filter.id }]"
          @click="$emit('update:activeFilter', filter.id)"
        >
          {{ filter.name }}
        </button>
      </div>
    </div>

    <div v-if="filteredGroups.length === 0" class="empty-state">
      <div class="empty-art">
        <svg viewBox="0 0 120 120" aria-hidden="true">
          <circle cx="60" cy="60" r="54" />
          <circle cx="54" cy="54" r="20" />
          <path d="M69 69l17 17" />
          <path d="M38 94h34" />
        </svg>
      </div>
      <h3>{{ t('strategy.explore.emptyTitle') }}</h3>
      <p>{{ t('strategy.explore.emptyDesc') }}</p>
    </div>

    <div v-else class="group-grid">
      <article
        v-for="(group, index) in filteredGroups"
        :key="group.id"
        class="group-card"
        :style="{ '--i': index }"
        @click="$emit('detail', group)"
      >
        <div class="card-media">
          <img :src="group.coverUrl || defaultCover" :alt="group.name" loading="lazy" />
          <span class="member-count-overlay">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <circle cx="9" cy="8" r="3.2" />
              <path d="M3.5 19a5.5 5.5 0 0 1 11 0" />
              <path d="M16 5.6a3 3 0 0 1 0 5.8M17.6 14.4A5 5 0 0 1 20.5 19" />
            </svg>
            {{ t('strategy.explore.memberCount', { count: group.currentMembers || 0 }) }}
          </span>
        </div>

        <div class="card-content">
          <h3>{{ group.name }}</h3>

          <p class="destination">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M12 21s7-5.5 7-11a7 7 0 1 0-14 0c0 5.5 7 11 7 11Z" />
              <circle cx="12" cy="10" r="2.5" />
            </svg>
            {{ group.destination || t('strategy.myGroups.noDestination') }}
          </p>

          <p class="description">{{ group.description || t('strategy.explore.noDescription') }}</p>

          <div class="card-footer">
            <span class="creator">{{ t('strategy.explore.creator', { name: group.creatorName || '—' }) }}</span>
            <button
              v-if="!group.isJoined"
              class="join-btn"
              type="button"
              @click.stop="$emit('join', group.id)"
            >
              {{ t('strategy.explore.join') }}
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M5 12h14M13 6l6 6-6 6" />
              </svg>
            </button>
            <span v-else class="joined-tag">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M5 13l4 4L19 7" />
              </svg>
              {{ t('strategy.explore.joined') }}
            </span>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  groups: {
    type: Array,
    default: () => []
  },
  filters: {
    type: Array,
    required: true
  },
  activeFilter: {
    type: String,
    required: true
  },
  defaultCover: {
    type: String,
    default: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800'
  }
})

defineEmits(['update:activeFilter', 'detail', 'join'])

const { t } = useI18n()

const filteredGroups = computed(() => {
  if (props.activeFilter === 'active') {
    return props.groups.filter(group => group.status === 1)
  }
  if (props.activeFilter === 'ended') {
    return props.groups.filter(group => group.status !== 1)
  }
  return props.groups
})
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

.filter-tabs {
  display: inline-flex;
  gap: 4px;
  padding: 5px;
  border-radius: var(--radius-full);
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
}

.filter-btn {
  padding: 9px 20px;
  border: none;
  border-radius: var(--radius-full);
  background: transparent;
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-secondary);
  cursor: pointer;
  white-space: nowrap;
  transition: color var(--transition-fast), background var(--transition-normal),
    box-shadow var(--transition-normal);
}

.filter-btn:hover {
  color: var(--color-text);
}

.filter-btn.active {
  background: var(--theme-bg-surface);
  color: var(--color-text);
  font-weight: 600;
  box-shadow: var(--shadow-sm);
}

.group-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 22px;
}

.group-card {
  position: relative;
  display: flex;
  flex-direction: column;
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
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 50%, rgba(0, 0, 0, 0.5) 100%);
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

.member-count-overlay {
  position: absolute;
  left: 12px;
  bottom: 12px;
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

.member-count-overlay svg {
  width: 13px;
  height: 13px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px 20px 18px;
}

.card-content h3 {
  font-size: 18px;
  font-weight: 650;
  line-height: 1.3;
  color: var(--color-text);
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.destination {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 10px;
}

.destination svg {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
  fill: none;
  stroke: var(--color-primary);
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.description {
  font-size: 13px;
  line-height: 1.6;
  color: var(--color-text-secondary);
  margin-bottom: 18px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-top: auto;
  padding-top: 14px;
  border-top: 1px solid var(--color-border);
}

.creator {
  font-size: 12px;
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.join-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: var(--theme-primary-text);
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.join-btn svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  transition: transform var(--transition-fast);
}

.join-btn:hover {
  background: var(--color-primary-hover);
}

.join-btn:hover svg {
  transform: translateX(2px);
}

.join-btn:active {
  transform: scale(0.96);
}

.joined-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  flex-shrink: 0;
  padding: 7px 14px;
  border-radius: var(--radius-full);
  background: var(--theme-success-soft);
  color: var(--color-success);
  font-size: 12px;
  font-weight: 600;
}

.joined-tag svg {
  width: 13px;
  height: 13px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

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
}

@media (max-width: 1199px) and (min-width: 768px) {
  .section-head {
    align-items: flex-start;
    flex-direction: column;
    gap: 16px;
  }
}

@media (max-width: 767px) {
  .section-head {
    align-items: stretch;
    flex-direction: column;
    gap: 14px;
    margin-bottom: 20px;
  }

  .filter-tabs {
    display: flex;
    overflow-x: auto;
    scrollbar-width: none;
  }

  .filter-tabs::-webkit-scrollbar {
    display: none;
  }

  .filter-btn {
    flex: 1;
    padding: 9px 12px;
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
