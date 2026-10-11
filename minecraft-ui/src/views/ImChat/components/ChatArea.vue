<template>
  <div class="im-chat">
    <div v-if="!selectedContact" class="empty-chat">
      <Icon name="chat-round" :size="'80px'" />
      <p>选择一个联系人开始聊天</p>
    </div>
    
    <div v-else class="chat-content">
      <div class="chat-header">
        <div class="header-info">
          <button class="back-btn" title="返回列表" @click="emit('back')">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="m15 18-6-6 6-6" />
            </svg>
          </button>
          <img 
            :src="selectedContact.avatar || defaultAvatar" 
            :alt="selectedContact.name"
            class="header-avatar"
          />
          <div class="header-text">
            <h3>{{ selectedContact.name }}</h3>
            <span :class="['online-status', { online: selectedContact.online }]">
              {{ selectedContact.online ? '在线' : '离线' }}
            </span>
          </div>
        </div>
        <div class="header-actions">
          <button class="action-btn" title="语音通话" @click="onVoiceCall">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72c.13.96.36 1.9.7 2.81a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.91.34 1.85.57 2.81.7A2 2 0 0 1 22 16.92z" />
            </svg>
          </button>
          <button class="action-btn" title="视频通话" @click="onVideoCall">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8"
              stroke-linecap="round" stroke-linejoin="round">
              <polygon points="23 7 16 12 23 17 23 7" />
              <rect x="1" y="5" width="15" height="14" rx="2" ry="2" />
            </svg>
          </button>
          <button class="action-btn" title="更多" @click="onContactInfo">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <circle cx="5" cy="12" r="1.8" />
              <circle cx="12" cy="12" r="1.8" />
              <circle cx="19" cy="12" r="1.8" />
            </svg>
          </button>
        </div>
      </div>
      
      <MessageList
        ref="messageListRef"
        :messages="messages"
        :current-user-id="currentUserId"
        :current-user-name="currentUserName"
        :current-user-avatar="currentUserAvatar"
        :selected-contact="selectedContact"
      />
      
      <ChatInput @send="onSend" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import MessageList from './MessageList.vue'
import ChatInput from './ChatInput.vue'
import Icon from './Icon.vue'

const props = defineProps({
  selectedContact: {
    type: Object,
    default: null
  },
  messages: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['voice-call', 'video-call', 'contact-info', 'send', 'back'])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const messageListRef = ref(null)

const onVoiceCall = () => {
  emit('voice-call')
}

const onVideoCall = () => {
  emit('video-call')
}

const onContactInfo = () => {
  emit('contact-info')
}

const onSend = (content) => {
  emit('send', content)
}

defineExpose({ messageListRef })
</script>

<style scoped>
.im-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  font-family: Inter, "PingFang SC", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.empty-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #6e6e73;
  background: #f5f5f5;
}

.empty-chat .svg-icon {
  margin-bottom: 20px;
}

.empty-chat p {
  font-size: 16px;
  margin: 0;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: #f5f5f5;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  padding: 0 20px;
  background: #ffffff;
  border-bottom: 1px solid #ededed;
  flex-shrink: 0;
}

.header-info {
  display: flex;
  align-items: center;
  min-width: 0;
}

.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  flex-shrink: 0;
}

.header-text {
  display: flex;
  align-items: baseline;
  gap: 8px;
  min-width: 0;
}

.header-text h3 {
  font-size: 15px;
  font-weight: 500;
  color: #1d1d1f;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.online-status {
  font-size: 12px;
  color: #9a9aa0;
  flex-shrink: 0;
}

.online-status.online {
  color: #34c759;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.action-btn {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #4a4a4f;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s ease, color 0.15s ease;
}

.action-btn:hover {
  background: #f0f0f2;
  color: #1d1d1f;
}

/* ===== 返回按钮：仅移动端显示 ===== */
.back-btn {
  display: none;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  margin-right: 8px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #1d1d1f;
  cursor: pointer;
  flex-shrink: 0;
  transition: background 0.15s ease;
}

.back-btn:hover {
  background: #f0f0f2;
}

/* ===== 响应式适配 ===== */
@media (max-width: 768px) {
  .back-btn {
    display: flex;
  }

  .chat-header {
    padding: 0 12px;
  }

  .header-avatar {
    width: 32px;
    height: 32px;
    margin-right: 8px;
  }

  .header-text h3 {
    font-size: 15px;
  }

  .header-actions {
    gap: 2px;
  }
}
</style>