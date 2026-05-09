<template>
  <div class="im-sidebar">
    <div class="sidebar-header">
      <h2 class="sidebar-title">消息</h2>
      <div class="header-actions">
        <button class="action-btn" @click="onOpenFriendRequests" title="好友申请">
          <Icon name="sms" :size="'18px'" />
          <span v-if="friendRequestCount > 0" class="request-badge">{{ friendRequestCount > 99 ? '99+' : friendRequestCount }}</span>
        </button>
        <button class="add-btn" @click="onAddFriend">
          <Icon name="plus" :size="'18px'" />
        </button>
      </div>
    </div>
    
    <div class="search-box">
      <input 
        type="text" 
        v-model="searchKeyword" 
        placeholder="搜索好友或群组..."
        class="search-input"
        @input="onSearch"
      />
    </div>
    
    <div class="tabs">
      <button 
        :class="['tab-btn', { active: activeTab === 'friends' }]"
        @click="onTabChange('friends')"
      >
        <Icon name="user" :size="'16px'" />
        <span>好友</span>
      </button>
      <button 
        :class="['tab-btn', { active: activeTab === 'groups' }]"
        @click="onTabChange('groups')"
      >
        <Icon name="users" :size="'16px'" />
        <span>群组</span>
      </button>
    </div>
    
    <div class="contact-list">
      <div 
        v-for="contact in filteredContacts" 
        :key="contact.id"
        :class="['contact-item', { active: selectedContact?.id === contact.id }]"
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
      
      <div v-if="filteredContacts.length === 0" class="empty-state">
        <Icon name="message" :size="'48px'" />
        <p>暂无{{ activeTab === 'friends' ? '好友' : '群组' }}</p>
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
  }
})

const emit = defineEmits(['add-friend', 'search', 'tab-change', 'select-contact', 'open-friend-requests'])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const searchKeyword = ref('')

const filteredContacts = computed(() => {
  const contacts = props.activeTab === 'friends' ? props.friends : props.groups
  if (!searchKeyword.value) return contacts
  return contacts.filter(c => c.name.includes(searchKeyword.value))
})

const onAddFriend = () => {
  emit('add-friend')
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
</script>

<style scoped>
.im-sidebar {
  width: 360px;
  height: 100%;
  background: #fff;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e0e0e0;
}

.sidebar-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.add-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  color: #fff;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-btn:hover {
  background: #e8e8e8;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  color: #666;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.action-btn:hover {
  background: #e0e0e0;
}

.request-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-box {
  padding: 12px 16px;
  border-bottom: 1px solid #e0e0e0;
}

.search-input {
  width: 100%;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #e0e0e0;
  border-radius: 18px;
  font-size: 14px;
  outline: none;
}

.tabs {
  display: flex;
  padding: 8px 16px;
  gap: 8px;
}

.tab-btn {
  flex: 1;
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background: #f5f5f5;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s;
}

.tab-btn.active {
  background: #409eff;
  color: #fff;
}

.contact-list {
  flex: 1;
  overflow-y: auto;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.contact-list::-webkit-scrollbar {
  display: none;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f0f0f0;
}

.contact-item:hover {
  background: #f8f9fa;
}

.contact-item.active {
  background: #e8f4fd;
}

.avatar-wrapper {
  position: relative;
  margin-right: 12px;
}

.contact-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.unread-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: #f56c6c;
  color: #fff;
  font-size: 12px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.online-status {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ccc;
  border: 2px solid #fff;
}

.online-status.online {
  background: #67c23a;
}

.contact-info {
  flex: 1;
  overflow: hidden;
}

.contact-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-last-message {
  font-size: 13px;
  color: #999;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-time {
  font-size: 12px;
  color: #bbb;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
}

.empty-state .svg-icon {
  margin-bottom: 12px;
}

.empty-state p {
  margin: 0;
}
</style>