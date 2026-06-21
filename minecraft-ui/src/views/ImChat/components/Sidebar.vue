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
        <div class="header-actions">
          <button 
            v-if="currentTab === 'chat'" 
            class="action-btn" 
            @click="onCreateGroup" 
            title="创建群组"
          >
            <Icon name="users" :size="'18px'" />
          </button>
          <button 
            v-if="currentTab === 'chat'" 
            class="action-btn" 
            @click="onOpenFriendRequests" 
            title="好友申请"
          >
            <Icon name="sms" :size="'18px'" />
            <span v-if="friendRequestCount > 0" class="request-badge">{{ friendRequestCount > 99 ? '99+' : friendRequestCount }}</span>
          </button>
          <button 
            v-if="currentTab === 'chat'" 
            class="add-btn" 
            @click="onAddFriend"
            title="添加好友"
          >
            <Icon name="plus" :size="'18px'" />
          </button>
          <button 
            v-if="currentTab === 'contacts'" 
            class="action-btn" 
            @click="onAddFriend"
            title="添加好友"
          >
            <Icon name="plus" :size="'18px'" />
          </button>
        </div>
      </div>
      
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchKeyword" 
          :placeholder="searchPlaceholder"
          class="search-input"
          @input="onSearch"
        />
      </div>
      
      <div v-if="currentTab === 'chat'" class="tabs">
        <button 
          :class="['tab-btn', { active: activeTab === 'friends' }]"
          @click="onTabChange('friends')"
        >
          <Icon name="user" :size="'14px'" />
          <span>好友</span>
        </button>
        <button 
          :class="['tab-btn', { active: activeTab === 'groups' }]"
          @click="onTabChange('groups')"
        >
          <Icon name="users" :size="'14px'" />
          <span>群组</span>
        </button>
      </div>
      
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
                <span v-if="contact.unreadCount > 0" class="unread-badge">{{ contact.unreadCount }}</span>
                <span :class="['online-status', { online: contact.online }]"></span>
              </div>
              <div class="contact-info">
                <h4 class="contact-name">{{ contact.name }}</h4>
                <p class="contact-last-message">{{ contact.lastMessage }}</p>
              </div>
              <span class="contact-time">{{ contact.time }}</span>
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
            <p>暂无{{ activeTab === 'friends' ? '好友' : '群组' }}</p>
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
  'tab-change', 
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

const filteredContacts = computed(() => {
  const contacts = props.activeTab === 'friends' ? props.friends : props.groups
  if (!searchKeyword.value) return contacts
  return contacts.filter(c => c.name.includes(searchKeyword.value))
})

const filteredFriends = computed(() => {
  if (!searchKeyword.value) return props.friends
  return props.friends.filter(f => f.name.includes(searchKeyword.value))
})

const onNavChange = (tab) => {
  currentTab.value = tab
  emit('nav-change', tab)
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

const onSearch = () => {
  emit('search', searchKeyword.value)
}

const onTabChange = (tab) => {
  emit('tab-change', tab)
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

.sidebar-left {
  width: 64px;
  height: 100%;
  background: #f5f5f7;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0;
  flex-shrink: 0;
  border-right: 1px solid #d2d2d6;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  cursor: pointer;
  margin-bottom: 8px;
  overflow: hidden;
  flex-shrink: 0;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.user-avatar:hover {
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  object-fit: cover;
}

.nav-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-top: 16px;
}

.nav-item {
  width: 44px;
  height: 44px;
  border: none;
  background: transparent;
  border-radius: 12px;
  color: #6e6e73;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background: rgba(0, 0, 0, 0.06);
  transform: scale(1.01);
}

.nav-item.active {
  background: #2997ff;
  color: #ffffff;
}

.nav-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  background: #ff3b30;
  color: #ffffff;
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebar-right {
  flex: 1;
  height: 100%;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #d2d2d6;
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #ffffff;
  border-bottom: 1px solid #d2d2d6;
}

.sidebar-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.add-btn {
  width: 32px;
  height: 32px;
  border-radius: 12px;
  border: none;
  background: transparent;
  color: #1d1d1f;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.add-btn:hover {
  background: #f5f5f7;
  transform: scale(1.01);
}

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 12px;
  border: none;
  background: transparent;
  color: #1d1d1f;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: #f5f5f7;
  transform: scale(1.01);
}

.request-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  background: #ff3b30;
  color: #ffffff;
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-box {
  padding: 10px 12px;
  background: #f5f5f7;
}

.search-input {
  width: 100%;
  height: 32px;
  padding: 0 12px;
  background: #ffffff;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  font-size: 14px;
  color: #1d1d1f;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  font-family: Inter, "PingFang SC", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.search-input::placeholder {
  color: #6e6e73;
}

.search-input:focus {
  border-color: #2997ff;
  box-shadow: 0 0 0 3px rgba(41, 151, 255, 0.15);
}

.tabs {
  display: flex;
  padding: 8px 12px;
  background: #ffffff;
  gap: 8px;
}

.tab-btn {
  flex: 1;
  padding: 6px 12px;
  border: none;
  border-radius: 12px;
  background: transparent;
  color: #6e6e73;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  transition: all 0.2s ease;
  font-family: Inter, "PingFang SC", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.tab-btn:hover {
  background: #f5f5f7;
  transform: scale(1.01);
}

.tab-btn.active {
  background: #f5f5f7;
  color: #1d1d1f;
  font-weight: 600;
}

.contacts-actions {
  background: #ffffff;
  padding: 8px 0;
}

.contact-list {
  flex: 1;
  overflow-y: auto;
  -ms-overflow-style: none;
  scrollbar-width: none;
  background: #ffffff;
}

.contact-list::-webkit-scrollbar {
  display: none;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s ease;
  background: #ffffff;
  position: relative;
}

.contact-item:hover {
  background: #f5f5f7;
  transform: scale(1.01);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.contact-item.active {
  background: #f5f5f7;
}

.contact-content {
  flex: 1;
  display: flex;
  align-items: center;
  min-width: 0;
}

.detail-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: #6e6e73;
  cursor: pointer;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.detail-btn:hover {
  background: #f5f5f7;
  color: #1d1d1f;
  transform: scale(1.01);
}

.avatar-wrapper {
  position: relative;
  margin-right: 12px;
}

.contact-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
}

.icon-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #f5f5f7;
  color: #2997ff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.icon-avatar:hover {
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.unread-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: #ff3b30;
  color: #ffffff;
  font-size: 12px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.online-status {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #d2d2d6;
  border: 2px solid #ffffff;
}

.online-status.online {
  background: #34c759;
}

.contact-info {
  flex: 1;
  overflow: hidden;
}

.contact-name {
  font-size: 16px;
  font-weight: 400;
  color: #1d1d1f;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-last-message {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-time {
  font-size: 12px;
  color: #6e6e73;
}

.arrow-icon {
  color: #c7c7cc;
  margin-right: 4px;
}

.section-divider {
  height: 8px;
  background: #f5f5f7;
  border-top: 1px solid #d2d2d6;
  border-bottom: 1px solid #d2d2d6;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #6e6e73;
}

.empty-state .svg-icon {
  margin-bottom: 12px;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}
</style>