<template>
  <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
    <div class="modal modal-large">
      <div class="modal-header">
        <div class="group-info">
          <h2>{{ group.name }}</h2>
          <span class="group-status">{{ group.destination }}</span>
        </div>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>

      <div class="detail-tabs">
        <button
          v-for="dt in detailTabs"
          :key="dt.id"
          :class="['detail-tab', { active: activeTab === dt.id }]"
          @click="$emit('update:activeTab', dt.id)"
        >
          {{ dt.name }}
        </button>
      </div>

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
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-20);
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background: var(--color-bg);
  border-radius: var(--radius-lg);
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  animation: slideUp var(--transition-normal);
}

.modal-large {
  max-width: 900px;
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to { 
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-24);
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  font-size: 22px;
  font-weight: 600;
}

.group-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.group-status {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: var(--color-text-secondary);
  line-height: 1;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all var(--transition-fast);
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--color-text);
}

.modal-body {
  padding: var(--spacing-24);
  overflow-y: auto;
  flex: 1;
}

.detail-body {
  min-height: 400px;
}

.detail-tabs {
  display: flex;
  gap: var(--spacing-4);
  padding: 0 var(--spacing-24);
  background: var(--color-bg-secondary);
  overflow-x: auto;
  scrollbar-width: none;
}

.detail-tabs::-webkit-scrollbar {
  display: none;
}

.detail-tab {
  padding: var(--spacing-12) var(--spacing-16);
  background: transparent;
  border: none;
  font-size: 15px;
  font-weight: 400;
  color: var(--color-text-secondary);
  cursor: pointer;
  white-space: nowrap;
  position: relative;
  transition: all var(--transition-normal);
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
}

.detail-tab:hover {
  color: var(--color-text);
  background: rgba(0, 0, 0, 0.03);
}

.detail-tab.active {
  color: var(--color-text);
  font-weight: 500;
}

.detail-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: var(--spacing-16);
  right: var(--spacing-16);
  height: 3px;
  background: var(--color-primary);
  border-radius: 2px;
}

@media (max-width: 1199px) and (min-width: 768px) {
  .modal-large {
    max-width: 700px;
  }
}

@media (max-width: 767px) {
  .modal {
    max-width: 100%;
    border-radius: var(--radius-lg);
    margin: var(--spacing-8);
  }
  .modal-large {
    max-width: 100%;
  }
  .detail-tabs {
    padding: 0 var(--spacing-12);
    gap: var(--spacing-4);
  }
  .detail-tab {
    padding: var(--spacing-12) var(--spacing-12);
    font-size: 14px;
  }
  .modal-body {
    padding: var(--spacing-16);
  }
  .modal-header {
    padding: var(--spacing-16);
  }
}
</style>