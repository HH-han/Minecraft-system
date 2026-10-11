<template>
  <div class="im-sidebar">
    <div class="sidebar-left">
      <div class="user-avatar" @click="onUserProfile">
        <img 
          :src="currentUserAvatar || defaultAvatar" 
          :alt="currentUserName"
          class="avatar-img"
        />
      </div>
      <div class="nav-list">
        <button 
          :class="['nav-item', { active: currentTab === 'chat' }]"
          @click="onNavChange('chat')"
          title="聊天"
        >
          <Icon name="message" :size="'24px'" />
          <span v-if="unreadTotal > 0" class="nav-badge">{{ unreadTotal > 99 ? '99+' : unreadTotal }}</span>
        </button>
        <button 
          :class="['nav-item', { active: currentTab === 'contacts' }]"
          @click="onNavChange('contacts')"
          title="通讯录"
        >
          <Icon name="user" :size="'24px'" />
        </button>
        <button 
          :class="['nav-item', { active: currentTab === 'discover' }]"
          @click="onNavChange('discover')"
          title="发现"
        >
          <Icon name="compass" :size="'24px'" />
        </button>
        <button 
          :class="['nav-item', { active: currentTab === 'me' }]"
          @click="onNavChange('me')"
          title="我"
        >
          <Icon name="user-circle" :size="'24px'" />
        </button>
      </div>
    </div>

    <div class="sidebar-right">
      <div class="sidebar-header">
        <h2 class="sidebar-title">{{ currentTabTitle }}</h2>
      </div>

      <div class="search-row">
        <div class="search-box">
          <span class="search-icon">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </span>
          <input 
            type="text" 
            v-model="searchKeyword" 
            :placeholder="searchPlaceholder"
            class="search-input"
            @input="onSearch"
          />
        </div>
        <div class="plus-wrap">
          <button 
            :class="['plus-btn', { active: showPlusMenu }]" 
            @click="togglePlusMenu"
            title="更多"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
          </button>
          <transition name="menu">
            <div v-if="showPlusMenu" class="plus-menu">
              <button class="menu-item" @click="onMenuCreateGroup">
                <span class="menu-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                  </svg>
                </span>
                <span class="menu-text">发起群聊</span>
              </button>
              <button class="menu-item" @click="onMenuAddFriend">
                <span class="menu-icon">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
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
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="22 12 16 12 14 15 10 15 8 12 2 12"></polyline>
                    <path d="M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"></path>
                  </svg>
                </span>
                <span class="menu-text">好友申请</span>
                <span v-if="friendRequestCount > 0" class="menu-badge">{{ friendRequestCount > 99 ? '99+' : friendRequestCount }}</span>
              </button>
            </div>
          </transition>
        </div>
      </div>
      <div v-if="showPlusMenu" class="menu-mask" @click="closePlusMenu"></div>
      
      <div v-if="currentTab === 'contacts'" class="contacts-actions">
        <div class="contact-item" @click="onNewFriends">
          <div class="avatar-wrapper">
            <div class="icon-avatar">
              <Icon name="user-plus" :size="'20px'" />
            </div>
          </div>
          <div class="contact-info">
            <h4 class="contact-name">新的朋友</h4>
          </div>
        </div>
        <div class="contact-item" @click="onGroups">
          <div class="avatar-wrapper">
            <div class="icon-avatar">
              <Icon name="users" :size="'20px'" />
            </div>
          </div>
          <div class="contact-info">
            <h4 class="contact-name">群聊</h4>
          </div>
        </div>
        <div class="contact-item" @click="onTags">
          <div class="avatar-wrapper">
            <div class="icon-avatar">
              <Icon name="tag" :size="'20px'" />
            </div>
          </div>
          <div class="contact-info">
            <h4 class="contact-name">标签</h4>
          </div>
        </div>
        <div class="contact-item" @click="onOfficials">
          <div class="avatar-wrapper">
            <div class="icon-avatar">
              <Icon name="building" :size="'20px'" />
            </div>
          </div>
          <div class="contact-info">
            <h4 class="contact-name">公众号</h4>
          </div>
        </div>
        <div class="section-divider"></div>
      </div>
      
      <div class="contact-list">
        <template v-if="currentTab === 'chat'">
          <div 
            v-for="contact in filteredContacts" 
            :key="contact.id"
            :class="['contact-item', { active: selectedContact?.id === contact.id }]"
          >
            <div 
              class="contact-content"
              @click="onSelectContact(contact)"
            >
              <div class="avatar-wrapper">
                <img 
                  :src="contact.avatar || defaultAvatar" 
                  :alt="contact.name"
                  class="contact-avatar"
                />
                <span v-if="contact.isGroup" class="group-badge">
                  <Icon name="users" :size="'10px'" />
                </span>
                <span v-if="contact.unreadCount > 0" class="unread-badge">{{ contact.unreadCount > 99 ? '99+' : contact.unreadCount }}</span>
                <span v-if="!contact.isGroup" :class="['online-status', { online: contact.online }]"></span>
              </div>
              <div class="contact-main">
                <div class="contact-row-top">
                  <h4 class="contact-name">
                    <span class="name-text">{{ contact.name }}</span>
                    <span v-if="contact.isGroup" class="contact-tag">群聊</span>
                  </h4>
                  <span v-if="contact.time" class="contact-time">{{ contact.time }}</span>
                </div>
                <p class="contact-last-message">
                  <span v-if="contact.isGroup && contact.memberCount" class="member-count">{{ contact.memberCount }}人</span>{{ contact.lastMessage }}
                </p>
              </div>
            </div>
            <button 
              class="detail-btn"
              @click.stop="onOpenDetail(contact)"
              title="详情"
            >
              <Icon name="info" :size="'14px'" />
            </button>
          </div>
          
          <div v-if="filteredContacts.length === 0" class="empty-state">
            <Icon name="message" :size="'48px'" />
            <p>暂无联系人</p>
          </div>
        </template>
        
        <template v-if="currentTab === 'contacts'">
          <div 
            v-for="contact in filteredFriends" 
            :key="contact.id"
            class="contact-item"
          >
            <div 
              class="contact-content"
              @click="onSelectFriend(contact)"
            >
              <div class="avatar-wrapper">
                <img 
                  :src="contact.avatar || defaultAvatar" 
                  :alt="contact.name"
                  class="contact-avatar"
                />
              </div>
              <div class="contact-info">
                <h4 class="contact-name">{{ contact.name }}</h4>
              </div>
            </div>
            <button 
              class="detail-btn"
              @click.stop="onOpenDetail(contact)"
              title="详情"
            >
              <Icon name="info" :size="'14px'" />
            </button>
          </div>
          
          <div v-if="filteredFriends.length === 0" class="empty-state">
            <Icon name="user" :size="'48px'" />
            <p>暂无好友</p>
          </div>
        </template>
        
        <template v-if="currentTab === 'discover'">
          <div class="contact-item" @click="onMoments">
            <div class="avatar-wrapper">
              <div class="icon-avatar">
                <Icon name="camera" :size="'20px'" />
              </div>
            </div>
            <div class="contact-info">
              <h4 class="contact-name">朋友圈</h4>
            </div>
            <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
          </div>
          <div class="contact-item" @click="onScan">
            <div class="avatar-wrapper">
              <div class="icon-avatar">
                <Icon name="scan" :size="'20px'" />
              </div>
            </div>
            <div class="contact-info">
              <h4 class="contact-name">扫一扫</h4>
            </div>
            <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
          </div>
          <div class="contact-item" @click="onNearby">
            <div class="avatar-wrapper">
              <div class="icon-avatar">
                <Icon name="map-pin" :size="'20px'" />
              </div>
            </div>
            <div class="contact-info">
              <h4 class="contact-name">附近的人</h4>
            </div>
            <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
          </div>
          <div class="contact-item" @click="onShake">
            <div class="avatar-wrapper">
              <div class="icon-avatar">
                <Icon name="refresh-cw" :size="'20px'" />
              </div>
            </div>
            <div class="contact-info">
              <h4 class="contact-name">摇一摇</h4>
            </div>
            <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
          </div>
        </template>
        
        <template v-if="currentTab === 'me'">
          <div class="contact-item" @click="onProfile">
            <div class="avatar-wrapper">
              <img 
                :src="currentUserAvatar || defaultAvatar" 
                :alt="currentUserName"
                class="contact-avatar"
              />
            </div>
            <div class="contact-info">
              <h4 class="contact-name">{{ currentUserName }}</h4>
              <p class="contact-last-message">个人信息</p>
            </div>
            <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
          </div>
          <div class="section-divider"></div>
          <div class="contact-item" @click="onServices">
            <div class="avatar-wrapper">
              <div class="icon-avatar">
                <Icon name="credit-card" :size="'20px'" />
              </div>
            </div>
            <div class="contact-info">
              <h4 class="contact-name">服务</h4>
            </div>
            <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
          </div>
          <div class="contact-item" @click="onFavorites">
            <div class="avatar-wrapper">
              <div class="icon-avatar">
                <Icon name="star" :size="'20px'" />
              </div>
            </div>
            <div class="contact-info">
              <h4 class="contact-name">收藏</h4>
            </div>
            <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
          </div>
          <div class="contact-item" @click="onSettings">
            <div class="avatar-wrapper">
              <div class="icon-avatar">
                <Icon name="settings" :size="'20px'" />
              </div>
            </div>
            <div class="contact-info">
              <h4 class="contact-name">设置</h4>
            </div>
            <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  friends: {
    type: Array,
    default: () => []
  },
  groups: {
    type: Array,
    default: () => []
  },
  activeTab: {
    type: String,
    default: 'friends'
  },
  selectedContact: {
    type: Object,
    default: null
  },
  friendRequestCount: {
    type: Number,
    default: 0
  },
  currentUserAvatar: {
    type: String,
    default: ''
  },
  currentUserName: {
    type: String,
    default: '用户'
  }
})

const emit = defineEmits([
  'add-friend', 
  'create-group', 
  'search', 
  'select-contact', 
  'open-friend-requests',
  'nav-change',
  'user-profile',
  'new-friends',
  'view-groups',
  'tags',
  'officials',
  'moments',
  'scan',
  'nearby',
  'shake',
  'services',
  'favorites',
  'settings',
  'select-friend',
  'open-detail'
])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const currentTab = ref('chat')
const searchKeyword = ref('')
const showPlusMenu = ref(false)

const currentTabTitle = computed(() => {
  const titles = {
    chat: '博览通讯',
    contacts: '通讯录',
    discover: '发现',
    me: '我'
  }
  return titles[currentTab.value] || '博览通讯'
})

const searchPlaceholder = computed(() => {
  const placeholders = {
    chat: '搜索好友或群组...',
    contacts: '搜索好友...',
    discover: '搜索...',
    me: '搜索...'
  }
  return placeholders[currentTab.value] || '搜索...'
})

const unreadTotal = computed(() => {
  return props.friends.reduce((sum, f) => sum + (f.unreadCount || 0), 0) + 
         props.friendRequestCount
})

const unifiedContacts = computed(() => {
  const friendsWithType = props.friends.map(f => ({ ...f, isGroup: false }))
  const groupsWithType = props.groups.map(g => ({ ...g, isGroup: true }))
  return [...friendsWithType, ...groupsWithType].sort((a, b) => {
    const nameA = (a.name || '').toLowerCase()
    const nameB = (b.name || '').toLowerCase()
    return nameA.localeCompare(nameB, 'zh-CN')
  })
})

const filteredContacts = computed(() => {
  if (!searchKeyword.value) return unifiedContacts.value
  return unifiedContacts.value.filter(c => c.name.includes(searchKeyword.value))
})

const filteredFriends = computed(() => {
  if (!searchKeyword.value) return props.friends
  return props.friends.filter(f => f.name.includes(searchKeyword.value))
})

const onNavChange = (tab) => {
  currentTab.value = tab
  emit('nav-change', tab)
}

const togglePlusMenu = () => {
  showPlusMenu.value = !showPlusMenu.value
}

const closePlusMenu = () => {
  showPlusMenu.value = false
}

const onAddFriend = () => {
  emit('add-friend')
}

const onCreateGroup = () => {
  emit('create-group')
}

const onOpenFriendRequests = () => {
  emit('open-friend-requests')
}

const onMenuCreateGroup = () => {
  closePlusMenu()
  onCreateGroup()
}

const onMenuAddFriend = () => {
  closePlusMenu()
  onAddFriend()
}

const onMenuFriendRequests = () => {
  closePlusMenu()
  onOpenFriendRequests()
}

const onSearch = () => {
  emit('search', searchKeyword.value)
}

const onSelectContact = (contact) => {
  emit('select-contact', contact)
}

const onSelectFriend = (friend) => {
  emit('select-friend', friend)
}

const onOpenDetail = (contact) => {
  emit('open-detail', contact)
}

const onUserProfile = () => {
  emit('user-profile')
}

const onNewFriends = () => {
  emit('new-friends')
}

const onGroups = () => {
  emit('view-groups')
}

const onTags = () => {
  emit('tags')
}

const onOfficials = () => {
  emit('officials')
}

const onMoments = () => {
  emit('moments')
}

const onScan = () => {
  emit('scan')
}

const onNearby = () => {
  emit('nearby')
}

const onShake = () => {
  emit('shake')
}

const onServices = () => {
  emit('services')
}

const onFavorites = () => {
  emit('favorites')
}

const onSettings = () => {
  emit('settings')
}

const onProfile = () => {
  emit('user-profile')
}
</script>

<style scoped>
.im-sidebar {
  width: 320px;
  height: 100%;
  background: #ffffff;
  display: flex;
  overflow: hidden;
  font-family: Inter, "PingFang SC", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* ============ 左侧导航竖栏 ============ */
.sidebar-left {
  width: 64px;
  height: 100%;
  background: #f5f5f7;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0 12px;
  flex-shrink: 0;
  border-right: 1px solid #e6e6e9;
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  color: #6e6e73;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: background 0.18s ease, color 0.18s ease;
}

.nav-item:hover {
  background: rgba(0, 0, 0, 0.06);
}

.nav-item.active {
  background: #2997ff;
  color: #ffffff;
}

.nav-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: #ff3b30;
  color: #ffffff;
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

/* ============ 右侧列表面板 ============ */
.sidebar-right {
  flex: 1;
  height: 100%;
  min-width: 0;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e6e6e9;
  overflow: hidden;
}

.sidebar-header {
  padding: 14px 16px 4px;
}

.sidebar-title {
  font-size: 17px;
  font-weight: 600;
  color: #1d1d1f;
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
  color: #b0b0b5;
  pointer-events: none;
}

.search-box .search-input {
  width: 100%;
  height: 32px;
  padding: 0 12px 0 32px;
  background: #f0f0f2;
  border: 1px solid transparent;
  border-radius: 8px;
  font-size: 13px;
  color: #1d1d1f;
  outline: none;
  box-sizing: border-box;
  transition: background 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
  font-family: inherit;
}

.search-box .search-input::placeholder {
  color: #b0b0b5;
}

.search-box .search-input:focus {
  background: #ffffff;
  border-color: #2997ff;
  box-shadow: 0 0 0 3px rgba(41, 151, 255, 0.12);
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
  border: 1px solid #dcdce0;
  background: #ffffff;
  color: #3a3a3c;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.18s ease, border-color 0.18s ease, transform 0.18s ease;
}

.plus-btn:hover {
  background: #f0f0f2;
}

.plus-btn.active {
  background: #ececef;
  border-color: #c9c9ce;
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
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.14), 0 0 0 1px rgba(0, 0, 0, 0.04);
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
  color: #1d1d1f;
  cursor: pointer;
  text-align: left;
  transition: background 0.15s ease;
}

.menu-item:hover {
  background: #f0f0f2;
}

.menu-icon {
  display: flex;
  align-items: center;
  color: #4a4a4f;
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
  background: #ff3b30;
  color: #ffffff;
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

/* ============ 列表 ============ */
.contacts-actions {
  background: #ffffff;
  padding: 2px 0;
}

.contact-list {
  flex: 1;
  overflow-y: auto;
  background: #ffffff;
  scroll-behavior: smooth;
}

.contact-list::-webkit-scrollbar {
  width: 6px;
}

.contact-list::-webkit-scrollbar-track {
  background: transparent;
}

.contact-list::-webkit-scrollbar-thumb {
  background: #d2d2d6;
  border-radius: 3px;
}

.contact-list::-webkit-scrollbar-thumb:hover {
  background: #a1a1a6;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 9px 12px;
  cursor: pointer;
  background: #ffffff;
  position: relative;
  transition: background 0.15s ease;
}

.contact-item:hover {
  background: #f0f0f2;
}

.contact-item.active {
  background: #e4e4e8;
}

.contact-item.active:hover {
  background: #dededf;
}

.contact-content {
  flex: 1;
  display: flex;
  align-items: center;
  min-width: 0;
}

.avatar-wrapper {
  position: relative;
  margin-right: 10px;
  flex-shrink: 0;
}

.contact-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.icon-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #eef1f6;
  color: #2997ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.contact-main {
  flex: 1;
  min-width: 0;
}

.contact-row-top {
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-info {
  flex: 1;
  min-width: 0;
}

.contact-name {
  flex: 1;
  min-width: 0;
  font-size: 14.5px;
  font-weight: 400;
  color: #1d1d1f;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.name-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.contact-time {
  flex-shrink: 0;
  font-size: 11.5px;
  color: #b2b2b6;
}

.contact-tag {
  flex-shrink: 0;
  font-size: 10px;
  color: #8a8a8f;
  background: #f0f0f2;
  padding: 1px 5px;
  border-radius: 4px;
  font-weight: 400;
  line-height: 1.4;
}

.member-count {
  font-size: 12px;
  color: #9a9aa0;
  margin-right: 6px;
}

.contact-last-message {
  font-size: 12.5px;
  color: #9a9aa0;
  margin: 3px 0 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-btn {
  width: 26px;
  height: 26px;
  border: none;
  background: transparent;
  color: #c4c4c8;
  cursor: pointer;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-left: 2px;
  transition: background 0.15s ease, color 0.15s ease;
}

.detail-btn:hover {
  background: #e4e4e8;
  color: #6e6e73;
}

.arrow-icon {
  color: #c7c7cc;
  flex-shrink: 0;
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: #ff3b30;
  color: #ffffff;
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.group-badge {
  position: absolute;
  bottom: -3px;
  right: -3px;
  width: 16px;
  height: 16px;
  background: #2997ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1.5px solid #ffffff;
  color: #ffffff;
  box-sizing: border-box;
}

.online-status {
  position: absolute;
  bottom: -1px;
  right: -1px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #d2d2d6;
  border: 2px solid #ffffff;
  box-sizing: border-box;
}

.online-status.online {
  background: #34c759;
}

.section-divider {
  height: 8px;
  background: #f5f5f7;
  border-top: 1px solid #ececf0;
  border-bottom: 1px solid #ececf0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #9a9aa0;
}

.empty-state .svg-icon {
  margin-bottom: 12px;
}

.empty-state p {
  margin: 0;
  font-size: 13px;
}
</style>
