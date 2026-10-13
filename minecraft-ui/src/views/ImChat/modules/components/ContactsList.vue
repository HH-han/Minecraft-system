<template>
  <div class="section-header" @click="emit('toggle-collapse')">
    <span :class="['section-chevron', { collapsed }]">
      <Icon name="chevron-right" :size="'14px'" />
    </span>
    <span class="section-title">联系人</span>
    <span class="section-count">{{ friendCount }}</span>
  </div>

  <template v-if="!collapsed">
    <div
      :class="['contact-item', 'star-entry', { active: showStarOnly }]"
      @click="emit('toggle-star-entry')"
    >
      <div class="avatar-wrapper">
        <div class="icon-avatar star-avatar">
          <Icon name="star" :size="'18px'" />
        </div>
      </div>
      <div class="contact-info">
        <h4 class="contact-name">星标朋友</h4>
      </div>
      <span v-if="starredCount" class="entry-count">{{ starredCount }}</span>
      <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
    </div>

    <div
      v-for="group in contactGroups"
      :key="group.letter"
      :ref="(el) => setGroupRef(group.letter, el)"
      class="letter-group"
    >
      <div class="letter-header">{{ group.letter }}</div>
      <div
        v-for="friend in group.friends"
        :key="friend.id"
        class="contact-item"
        @click="emit('open-detail', friend)"
      >
        <div class="avatar-wrapper">
          <img :src="friend.avatar || DEFAULT_AVATAR" :alt="friend.name" class="contact-avatar" />
        </div>
        <div class="contact-info">
          <h4 class="contact-name">
            <span class="name-text">{{ friend.name }}</span>
          </h4>
        </div>
        <button
          :class="['star-btn', { active: isStarred(friend.id) }]"
          :title="isStarred(friend.id) ? '取消星标朋友' : '设为星标朋友'"
          @click.stop="emit('toggle-star', friend)"
        >
          <Icon name="star" :size="'16px'" />
        </button>
      </div>
    </div>

    <div v-if="contactGroups.length === 0" class="empty-state">
      <Icon :name="showStarOnly ? 'star' : 'user'" :size="'48px'" />
      <p>{{ showStarOnly ? '暂无星标朋友' : '暂无好友' }}</p>
    </div>
  </template>
</template>

<script setup>
import Icon from '../../components/Icon.vue'
import { DEFAULT_AVATAR } from '../constants'

const props = defineProps({
  contactGroups: {
    type: Array,
    default: () => []
  },
  starredIds: {
    type: Array,
    default: () => []
  },
  starredCount: {
    type: Number,
    default: 0
  },
  friendCount: {
    type: Number,
    default: 0
  },
  collapsed: {
    type: Boolean,
    default: false
  },
  showStarOnly: {
    type: Boolean,
    default: false
  },
  setGroupRef: {
    type: Function,
    default: () => {}
  }
})

const emit = defineEmits(['toggle-collapse', 'toggle-star-entry', 'toggle-star', 'open-detail'])

const isStarred = (id) => props.starredIds.includes(id)
</script>

<style scoped>
/* ---- 联系人分组标题（可折叠） ---- */
.section-header {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 12px 8px;
  cursor: pointer;
  user-select: none;
  background: var(--theme-bg-surface);
  transition: background 0.15s ease;
}

.section-header:hover {
  background: var(--theme-bg-hover);
}

.section-chevron {
  display: flex;
  align-items: center;
  color: var(--theme-text-3);
  transform: rotate(90deg);
  transition: transform 0.18s ease;
}

.section-chevron.collapsed {
  transform: rotate(0deg);
}

.section-title {
  flex: 1;
  font-size: 12.5px;
  color: var(--theme-text-3);
  letter-spacing: 0.3px;
}

.section-count {
  flex-shrink: 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

/* ---- 星标朋友入口 ---- */
.star-entry .star-avatar {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.star-entry.active {
  background: var(--theme-primary-soft);
}

.star-entry.active:hover {
  background: var(--theme-primary-soft);
}

/* ---- 字母分组 ---- */
.letter-group {
  position: relative;
}

.letter-header {
  position: sticky;
  top: 0;
  z-index: 2;
  padding: 3px 12px;
  font-size: 12px;
  font-weight: 500;
  color: var(--theme-text-3);
  background: var(--theme-bg-surface-2);
  border-top: 1px solid var(--theme-hairline);
  border-bottom: 1px solid var(--theme-hairline);
}

/* ---- 星标按钮 ---- */
.star-btn {
  width: 26px;
  height: 26px;
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-left: 2px;
  opacity: 0;
  transition: opacity 0.15s ease, background 0.15s ease, color 0.15s ease;
}

.contact-item:hover .star-btn {
  opacity: 1;
}

.star-btn:hover {
  background: var(--theme-bg-active);
  color: var(--theme-text-2);
}

.star-btn.active {
  opacity: 1;
  color: var(--theme-warning);
}

.star-btn.active :deep(svg) {
  fill: currentColor;
}
</style>
