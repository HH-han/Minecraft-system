<template>
  <div class="sidebar-header">
    <h2 class="sidebar-title">{{ title }}</h2>
  </div>

  <div class="search-row">
    <div class="search-box">
      <span class="search-icon">
        <svg
          width="14"
          height="14"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
      </span>
      <input
        type="text"
        :value="keyword"
        :placeholder="placeholder"
        class="search-input"
        @input="onInput"
      />
    </div>
    <div class="plus-wrap">
      <button
        :class="['plus-btn', { active: showPlusMenu }]"
        @click="togglePlusMenu"
        title="更多"
      >
        <svg
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
        >
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        <span v-if="friendRequestCount > 0" class="plus-dot"></span>
      </button>
      <transition name="menu">
        <div v-if="showPlusMenu" class="plus-menu">
          <button class="menu-item" @click="onMenuCreateGroup">
            <span class="menu-icon">
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"
                ></path>
              </svg>
            </span>
            <span class="menu-text">发起群聊</span>
          </button>
          <button class="menu-item" @click="onMenuAddFriend">
            <span class="menu-icon">
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="8.5" cy="7" r="4"></circle>
                <line x1="20" y1="8" x2="20" y2="14"></line>
                <line x1="23" y1="11" x2="17" y2="11"></line>
              </svg>
            </span>
            <span class="menu-text">添加朋友</span>
          </button>
          <button class="menu-item" @click="onMenuFriendRequests">
            <span class="menu-icon">
              <svg
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <polyline points="22 12 16 12 14 15 10 15 8 12 2 12"></polyline>
                <path
                  d="M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"
                ></path>
              </svg>
            </span>
            <span class="menu-text">好友申请</span>
            <span v-if="friendRequestCount > 0" class="menu-badge">{{
              friendRequestCount > 99 ? '99+' : friendRequestCount
            }}</span>
          </button>
        </div>
      </transition>
    </div>
  </div>
  <div v-if="showPlusMenu" class="menu-mask" @click="closePlusMenu"></div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  title: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '搜索...'
  },
  keyword: {
    type: String,
    default: ''
  },
  friendRequestCount: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits([
  'update:keyword',
  'search',
  'create-group',
  'add-friend',
  'open-friend-requests'
])

const showPlusMenu = ref(false)

const onInput = (event) => {
  const value = event.target.value
  emit('update:keyword', value)
  emit('search', value)
}

const togglePlusMenu = () => {
  showPlusMenu.value = !showPlusMenu.value
}

const closePlusMenu = () => {
  showPlusMenu.value = false
}

const onMenuCreateGroup = () => {
  closePlusMenu()
  emit('create-group')
}

const onMenuAddFriend = () => {
  closePlusMenu()
  emit('add-friend')
}

const onMenuFriendRequests = () => {
  closePlusMenu()
  emit('open-friend-requests')
}
</script>

<style scoped>
.sidebar-header {
  padding: 14px 16px 4px;
}

.sidebar-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin: 0;
  letter-spacing: 0.2px;
}

/* ---- 搜索行（微信风：圆角搜索框 + 圆形「+」） ---- */
.search-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 10px;
  display: flex;
  align-items: center;
  color: var(--theme-text-3);
  pointer-events: none;
}

.search-box .search-input {
  width: 100%;
  height: 32px;
  padding: 0 12px 0 32px;
  background: var(--theme-bg-surface-2);
  border: 1px solid transparent;
  border-radius: 8px;
  font-size: 13px;
  color: var(--theme-text-1);
  outline: none;
  box-sizing: border-box;
  transition: background 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
  font-family: inherit;
}

.search-box .search-input::placeholder {
  color: var(--theme-text-3);
}

.search-box .search-input:focus {
  background: var(--theme-bg-surface);
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.plus-wrap {
  position: relative;
  z-index: 60;
  flex-shrink: 0;
}

.plus-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  transition: background 0.18s ease, border-color 0.18s ease, transform 0.18s ease;
}

.plus-dot {
  position: absolute;
  top: -1px;
  right: -1px;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--theme-danger);
  border: 1.5px solid var(--theme-bg-surface);
  box-sizing: border-box;
}

.plus-btn:hover {
  background: var(--theme-bg-hover);
}

.plus-btn.active {
  background: var(--theme-bg-active);
  border-color: var(--theme-border-strong);
}

.menu-mask {
  position: fixed;
  inset: 0;
  z-index: 40;
  background: transparent;
}

.plus-menu {
  position: absolute;
  top: 40px;
  right: 0;
  min-width: 156px;
  padding: 6px;
  background: var(--theme-bg-elevated);
  border-radius: 10px;
  box-shadow: var(--theme-shadow-md);
  z-index: 61;
}

.menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border: none;
  background: transparent;
  border-radius: 7px;
  font-size: 13.5px;
  color: var(--theme-text-1);
  cursor: pointer;
  text-align: left;
  transition: background 0.15s ease;
}

.menu-item:hover {
  background: var(--theme-bg-hover);
}

.menu-icon {
  display: flex;
  align-items: center;
  color: var(--theme-text-2);
  flex-shrink: 0;
}

.menu-text {
  flex: 1;
  white-space: nowrap;
}

.menu-badge {
  margin-left: auto;
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

.menu-enter-active,
.menu-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.menu-enter-from,
.menu-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}
</style>
