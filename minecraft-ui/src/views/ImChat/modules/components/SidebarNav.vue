<template>
  <div class="sidebar-left">
    <div class="user-avatar" @click="onUserProfile">
      <img :src="avatar || DEFAULT_AVATAR" :alt="userName" class="avatar-img" />
    </div>
    <div class="nav-list">
      <button
        :class="['nav-item', { active: isTabActive('chat') }]"
        @click="onNavChange('chat')"
        title="聊天"
      >
        <Icon name="message" :size="'24px'" />
        <span v-if="unreadTotal > 0" class="nav-badge">{{ unreadTotal > 99 ? '99+' : unreadTotal }}</span>
      </button>
      <button
        :class="['nav-item', { active: isTabActive('contacts') }]"
        @click="onNavChange('contacts')"
        title="通讯录"
      >
        <Icon name="user" :size="'24px'" />
      </button>
      <button
        :class="['nav-item', { active: isTabActive('discover') }]"
        @click="onNavChange('discover')"
        title="发现"
      >
        <Icon name="compass" :size="'24px'" />
      </button>
      <button :class="['nav-item', { active: favoritesActive }]" @click="onFavorites" title="收藏">
        <Icon name="star" :size="'24px'" />
      </button>
      <button
        :class="['nav-item', { active: isTabActive('me') }]"
        @click="onNavChange('me')"
        title="我"
      >
        <Icon name="user-circle" :size="'24px'" />
      </button>
    </div>
  </div>
</template>

<script setup>
import Icon from '../../components/Icon.vue'
import { DEFAULT_AVATAR } from '../constants'

const props = defineProps({
  avatar: {
    type: String,
    default: ''
  },
  userName: {
    type: String,
    default: '用户'
  },
  currentTab: {
    type: String,
    default: 'chat'
  },
  favoritesActive: {
    type: Boolean,
    default: false
  },
  unreadTotal: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['user-profile', 'nav-change', 'favorites'])

// 收藏面板由父组件独立控制，其打开时左侧导航不再高亮任何 tab，避免出现双高亮
const isTabActive = (tab) => !props.favoritesActive && props.currentTab === tab

const onUserProfile = () => emit('user-profile')
const onNavChange = (tab) => emit('nav-change', tab)
const onFavorites = () => emit('favorites')
</script>

<style scoped>
/* ============ 左侧导航竖栏 ============ */
.sidebar-left {
  width: 64px;
  height: 100%;
  box-sizing: border-box; /* 无全局 border-box 重置，避免纵向 padding 撑高竖栏 */
  background: var(--theme-bg-surface-2);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0 12px;
  flex-shrink: 0;
  border-right: 1px solid var(--theme-border);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  margin-bottom: 10px;
  overflow: hidden;
  flex-shrink: 0;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.user-avatar:hover {
  transform: scale(1.02);
  box-shadow: var(--theme-shadow-sm);
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.nav-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding-top: 14px;
}

.nav-item {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 10px;
  color: var(--theme-text-2);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: background 0.18s ease, color 0.18s ease;
}

.nav-item:hover {
  background: var(--theme-bg-hover);
}

.nav-item.active {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

.nav-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--theme-danger);
  color: var(--theme-primary-text);
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}
</style>
