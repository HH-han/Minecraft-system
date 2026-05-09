<template>
  <div class="im-chat">
    <div v-if="!selectedContact" class="empty-chat">
      <Icon name="chat-round" :size="'80px'" />
      <p>选择一个联系人开始聊天</p>
    </div>
    
    <div v-else class="chat-content">
      <div class="chat-header">
        <div class="header-info">
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
          <button class="action-btn" @click="onVoiceCall">
            <Icon name="phone" :size="'16px'" />
          </button>
          <button class="action-btn" @click="onVideoCall">
            <Icon name="video-camera" :size="'16px'" />
          </button>
          <button class="action-btn" @click="onContactInfo">
            <Icon name="more" :size="'16px'" />
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

const emit = defineEmits(['voice-call', 'video-call', 'contact-info', 'send'])

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
}

.empty-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  background: #fff;
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
  background: #e8e8e8;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background: #fff;
  border-bottom: 1px solid #e0e0e0;
}

.header-info {
  display: flex;
  align-items: center;
}

.header-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.header-text h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.online-status {
  font-size: 13px;
  color: #999;
}

.online-status.online {
  color: #67c23a;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: #f5f5f5;
  color: #666;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background: #e8e8e8;
}
</style>