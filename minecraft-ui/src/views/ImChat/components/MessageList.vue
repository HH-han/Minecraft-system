<template>
  <div class="message-list" ref="messageList">
    <div 
      v-for="message in messages" 
      :key="message.id"
      :class="['message-item', { mine: message.senderId === currentUserInfo?.id }]"
    >
      <img 
        v-if="message.senderId !== currentUserInfo?.id"
        :src="getSenderAvatar(message)" 
        :alt="getSenderName(message)"
        class="message-avatar"
        @error="handleAvatarError"
      />
      <div class="message-content">
        <div :class="['message-bubble', { mine: message.senderId === currentUserInfo?.id, 'no-blue-bg': isImageMessage(message) || isEmojiMessage(message) }]">
          <p v-if="isTextMessage(message)">{{ message.content }}</p>
          <img 
            v-else-if="isImageMessage(message)" 
            :src="getMessageImageUrl(message.content)" 
            :alt="'图片'"
            class="message-image"
            @click="previewImage(message.content)"
          />
          <span 
            v-else-if="isEmojiMessage(message) && isUnicodeEmoji(message.content)" 
            class="message-emoji-text"
          >{{ message.content }}</span>
          <div 
            v-else-if="isEmojiMessage(message)" 
            class="emoji-container"
          >
            <img 
              :src="getMessageImageUrl(message.content)" 
              :alt="'表情'"
              class="message-emoji-image"
              @error="handleEmojiError"
            />
          </div>
          <div v-else class="message-other">
            <Icon name="file" :size="'24px'" />
            <span>{{ message.content }}</span>
          </div>
        </div>
        <span class="message-time">{{ formatTime(message.createTime) }}</span>
      </div>
      <img 
        v-if="message.senderId === currentUserInfo?.id"
        :src="currentUserInfo?.avatar || defaultAvatar" 
        :alt="currentUserInfo?.username || '用户'"
        class="message-avatar"
        @error="handleAvatarError"
      />
    </div>
    
    <div v-if="messages.length === 0" class="no-messages">
      <Icon name="sms" :size="'64px'" />
      <p>还没有消息，开始聊天吧</p>
    </div>
  </div>
  
  <div v-if="previewImageUrl" class="image-preview" @click="closePreview">
    <img :src="previewImageUrl" :alt="'预览'" class="preview-image" />
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue'
import { getUserInfo } from '@/api/user'
import { getToken } from '@/utils/storage'
import Icon from './Icon.vue'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  selectedContact: {
    type: Object,
    default: null
  },
  onlineUsers: {
    type: Object,
    default: () => ({})
  },
  groupMembers: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['user-loaded'])

const messageList = ref(null)
const currentUserInfo = ref(null)
const defaultAvatar = '/src/assets/defaultimage/moren.webp'
const previewImageUrl = ref('')

const getSenderAvatar = (message) => {
  if (message.senderAvatar) {
    return message.senderAvatar
  }
  
  const senderId = message.senderId
  if (senderId && props.onlineUsers[senderId]) {
    return props.onlineUsers[senderId].avatar || defaultAvatar
  }
  
  if (senderId) {
    const member = props.groupMembers.find(m => m.userId === senderId)
    if (member && member.avatar) {
      return member.avatar
    }
  }
  
  if (!props.selectedContact?.isGroup) {
    return props.selectedContact?.avatar || defaultAvatar
  }
  
  return defaultAvatar
}

const getSenderName = (message) => {
  if (message.senderName) {
    return message.senderName
  }
  
  const senderId = message.senderId
  if (senderId && props.onlineUsers[senderId]) {
    return props.onlineUsers[senderId].username || '用户'
  }
  
  if (senderId) {
    const member = props.groupMembers.find(m => m.userId === senderId)
    if (member && member.username) {
      return member.username
    }
  }
  
  if (!props.selectedContact?.isGroup) {
    return props.selectedContact?.name || '用户'
  }
  
  return '用户'
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const scrollToBottom = () => {
  if (messageList.value) {
    messageList.value.scrollTop = messageList.value.scrollHeight
  }
}

const handleAvatarError = (e) => {
  e.target.src = defaultAvatar
}

const handleEmojiError = (e) => {
  e.target.style.display = 'none'
  e.target.parentElement.style.display = 'none'
}

const loadCurrentUserInfo = async () => {
  const token = getToken()
  if (token) {
    try {
      const response = await getUserInfo()
      if (response.code === 200) {
        currentUserInfo.value = response.data
        emit('user-loaded', response.data)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
}

const isTextMessage = (message) => {
  const type = message.messageType || 'text'
  return type.toLowerCase() === 'text'
}

const isImageMessage = (message) => {
  const type = message.messageType || 'text'
  return type.toLowerCase() === 'image'
}

const isEmojiMessage = (message) => {
  const type = message.messageType || 'text'
  return type.toLowerCase() === 'emoji'
}

const isUnicodeEmoji = (content) => {
  if (!content) return false
  const emojiRegex = /[\u{1F300}-\u{1F6FF}\u{1F900}-\u{1F9FF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}\u{1F1E6}-\u{1F1FF}]/u
  return emojiRegex.test(content) && !content.startsWith('http') && !content.startsWith('/')
}

const getMessageImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) {
    return url
  }
  return import.meta.env.VITE_API_BASE_URL + url
}

const previewImage = (url) => {
  previewImageUrl.value = getMessageImageUrl(url)
}

const closePreview = () => {
  previewImageUrl.value = ''
}

watch(() => props.messages, async () => {
  await nextTick()
  scrollToBottom()
}, { deep: true })

onMounted(async () => {
  await loadCurrentUserInfo()
  scrollToBottom()
})

defineExpose({ scrollToBottom, currentUserInfo })
</script>

<style scoped>
.message-list {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  padding: 20px;
  -ms-overflow-style: none;
  scrollbar-width: none;
  font-family: Inter, "PingFang SC", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.message-list::-webkit-scrollbar {
  display: none;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
}

.message-item.mine {
  justify-content: flex-end;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.message-content {
  max-width: 60%;
  display: flex;
  flex-direction: column;
}

.message-item.mine .message-content {
  align-items: flex-end;
}

.message-bubble {
  background: #4ab81b;
  padding: 10px 14px;
  border-radius: 18px;
  margin: 0 10px;
  max-width: 100%;
  display: inline-block;
  transition: 0.2s ease;
}

.message-item.mine .message-bubble {
  background: #2997ff;
}

.message-item .message-bubble.no-blue-bg {
  background: none;
  border: none;
  box-shadow: none;
  padding: 0;
}

.message-bubble.emoji-bubble {
  padding: 6px;
  border-radius: 16px;
}

.message-bubble p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  color: #ffffff;
  font-family: Inter, "PingFang SC", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.message-item.mine .message-bubble p {
  color: #ffffff;
}

.message-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 12px;
  cursor: pointer;
  object-fit: cover;
  display: block;
}

.message-emoji-text {
  background: none;
  line-height: 1;
  display: block;
  font-size: 32px;
}

.emoji-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.message-emoji-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 12px;
  background: #f5f5f7;
  transition: transform 0.15s ease;
}

.message-emoji-image:hover {
  transform: scale(1.05);
}

.message-other {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1d1d1f;
  font-size: 14px;
}

.message-item.mine .message-other {
  color: #ffffff;
}

.message-time {
  font-size: 11px;
  color: #6e6e73;
  margin: 4px 10px;
  font-weight: 400;
}

.no-messages {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #6e6e73;
}

.no-messages .svg-icon {
  margin-bottom: 16px;
}

.no-messages p {
  font-size: 14px;
  margin: 0;
}

.image-preview {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  cursor: pointer;
}

.preview-image {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
  border-radius: 12px;
}
</style>