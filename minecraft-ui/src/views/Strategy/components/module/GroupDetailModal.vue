<template>
  <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal modal-large" role="dialog" aria-modal="true">
      <header class="detail-hero">
        <img class="detail-cover" :src="group.coverUrl || defaultCover" :alt="group.name || ''" />
        <div class="detail-scrim" aria-hidden="true"></div>

        <button class="close-btn" type="button" :aria-label="t('strategy.detail.close')" @click="$emit('close')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M6 6l12 12M18 6L6 18" />
          </svg>
        </button>

        <div class="detail-hero-content">
          <span class="detail-eyebrow">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <circle cx="9" cy="8" r="3.2" />
              <path d="M3.5 19a5.5 5.5 0 0 1 11 0" />
              <path d="M16 5.6a3 3 0 0 1 0 5.8M17.6 14.4A5 5 0 0 1 20.5 19" />
            </svg>
            {{ t('strategy.detail.memberCount', { count: members.length }) }}
          </span>
          <h2>{{ group.name }}</h2>
          <span class="detail-destination">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M12 21s7-5.5 7-11a7 7 0 1 0-14 0c0 5.5 7 11 7 11Z" />
              <circle cx="12" cy="10" r="2.5" />
            </svg>
            {{ group.destination || t('strategy.detail.noDestination') }}
          </span>
        </div>
      </header>

      <nav class="detail-tabs">
        <button
          v-for="dt in detailTabs"
          :key="dt.id"
          :class="['detail-tab', { active: activeTab === dt.id }]"
          type="button"
          @click="$emit('update:activeTab', dt.id)"
        >
          {{ dt.name }}
        </button>
      </nav>

      <div class="modal-body detail-body">
        <MembersTab
          v-if="activeTab === 'members'"
          :members="members"
          :is-admin="isAdmin"
          :default-avatar="defaultAvatar"
          @invite="$emit('invite')"
          @remove="$emit('remove-member', $event)"
        />
        <PostsTab
          v-if="activeTab === 'posts'"
          :posts="posts"
          :current-user-id="currentUserId"
          :default-avatar="defaultAvatar"
          @create="$emit('create-post', $event)"
          @delete="$emit('delete-post', $event)"
          @like="$emit('toggle-like', $event)"
          @comment="$emit('show-comments', $event)"
        />
        <TripPlanTab
          v-if="activeTab === 'trip'"
          :trip-plan="tripPlan"
          :is-admin="isAdmin"
          @add="$emit('add-trip')"
          @generate="$emit('generate-trip')"
          @delete="$emit('delete-trip', $event)"
        />
        <BillsTab
          v-if="activeTab === 'bills'"
          :bills="bills"
          @add="$emit('add-bill')"
          @settlement="$emit('get-settlement')"
        />
        <AlbumTab
          v-if="activeTab === 'album'"
          :photos="album"
          :is-admin="isAdmin"
          @upload="$emit('upload-photos', $event)"
          @delete="$emit('delete-photo', $event)"
        />
        <PollTab
          v-if="activeTab === 'poll'"
          :polls="polls"
          @create="$emit('create-poll')"
          @vote="$emit('vote', $event)"
        />
        <DiaryTab
          v-if="activeTab === 'diary'"
          :diary="diary"
          @generate="$emit('generate-diary')"
          @export="$emit('export-diary')"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
import MembersTab from './MembersTab.vue'
import PostsTab from './PostsTab.vue'
import TripPlanTab from './TripPlanTab.vue'
import BillsTab from './BillsTab.vue'
import AlbumTab from './AlbumTab.vue'
import PollTab from './PollTab.vue'
import DiaryTab from './DiaryTab.vue'

defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  group: {
    type: Object,
    default: () => ({})
  },
  detailTabs: {
    type: Array,
    required: true
  },
  activeTab: {
    type: String,
    required: true
  },
  isAdmin: {
    type: Boolean,
    default: false
  },
  currentUserId: {
    type: Number,
    default: null
  },
  members: {
    type: Array,
    default: () => []
  },
  posts: {
    type: Array,
    default: () => []
  },
  tripPlan: {
    type: Array,
    default: () => []
  },
  bills: {
    type: Array,
    default: () => []
  },
  album: {
    type: Array,
    default: () => []
  },
  polls: {
    type: Array,
    default: () => []
  },
  diary: {
    type: Object,
    default: null
  },
  defaultAvatar: {
    type: String,
    default: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100'
  },
  defaultCover: {
    type: String,
    default: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1200'
  }
})

defineEmits([
  'close',
  'update:activeTab',
  'invite',
  'remove-member',
  'create-post',
  'delete-post',
  'toggle-like',
  'show-comments',
  'add-trip',
  'generate-trip',
  'delete-trip',
  'add-bill',
  'get-settlement',
  'upload-photos',
  'delete-photo',
  'create-poll',
  'vote',
  'generate-diary',
  'export-diary'
])

const { t } = useI18n()
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: var(--theme-overlay);
  backdrop-filter: blur(16px);
  animation: overlay-in var(--transition-normal);
}

@keyframes overlay-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 960px;
  max-height: 88vh;
  overflow: hidden;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  animation: modal-in var(--transition-slow);
}

@keyframes modal-in {
  from {
    opacity: 0;
    transform: translateY(24px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.detail-hero {
  position: relative;
  flex-shrink: 0;
  height: 220px;
  overflow: hidden;
  background: var(--color-bg-secondary);
}

.detail-cover {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-scrim {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, rgba(8, 10, 14, 0.15) 0%, rgba(8, 10, 14, 0.72) 100%),
    linear-gradient(90deg, rgba(8, 10, 14, 0.45) 0%, transparent 65%);
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: 50%;
  background: rgba(12, 15, 20, 0.4);
  color: #fff;
  cursor: pointer;
  backdrop-filter: blur(10px);
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.close-btn svg {
  width: 17px;
  height: 17px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
}

.close-btn:hover {
  background: rgba(12, 15, 20, 0.66);
  transform: rotate(90deg);
}

.detail-hero-content {
  position: absolute;
  left: 28px;
  right: 80px;
  bottom: 24px;
  z-index: 1;
  color: #fff;
}

.detail-eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.78);
}

.detail-eyebrow svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.detail-hero-content h2 {
  font-size: 30px;
  font-weight: 750;
  line-height: 1.15;
  letter-spacing: -0.02em;
  margin-bottom: 8px;
  text-shadow: 0 2px 16px rgba(0, 0, 0, 0.35);
}

.detail-destination {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.82);
}

.detail-destination svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.detail-tabs {
  display: flex;
  flex-shrink: 0;
  gap: 2px;
  padding: 0 20px;
  border-bottom: 1px solid var(--color-border);
  background: var(--color-bg);
  overflow-x: auto;
  scrollbar-width: none;
}

.detail-tabs::-webkit-scrollbar {
  display: none;
}

.detail-tab {
  position: relative;
  padding: 16px 18px;
  border: none;
  background: transparent;
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  white-space: nowrap;
  cursor: pointer;
  transition: color var(--transition-fast);
}

.detail-tab:hover {
  color: var(--color-text);
}

.detail-tab.active {
  color: var(--color-text);
  font-weight: 650;
}

.detail-tab.active::after {
  content: '';
  position: absolute;
  left: 14px;
  right: 14px;
  bottom: 0;
  height: 2px;
  border-radius: 2px;
  background: var(--color-primary);
}

.modal-body {
  flex: 1;
  padding: 24px 28px 28px;
  overflow-y: auto;
}

.detail-body {
  min-height: 360px;
}

@media (max-width: 767px) {
  .modal-overlay {
    padding: 12px;
  }

  .modal {
    max-width: 100%;
    max-height: 92vh;
    border-radius: var(--radius-md);
  }

  .detail-hero {
    height: 168px;
  }

  .detail-hero-content {
    left: 18px;
    right: 64px;
    bottom: 16px;
  }

  .detail-hero-content h2 {
    font-size: 22px;
  }

  .detail-tabs {
    padding: 0 8px;
  }

  .detail-tab {
    padding: 14px 14px;
    font-size: 13px;
  }

  .modal-body {
    padding: 16px;
  }

  .detail-body {
    min-height: 280px;
  }
}
</style>
