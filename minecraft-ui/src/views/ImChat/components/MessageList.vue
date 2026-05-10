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
        <div :class="['message-bubble', { mine: message.senderId === currentUserInfo?.id }]">
          <p v-if="isTextMessage(message)">{{ message.content }}</p>
          <img 
            v-else-if="isImageMessage(message)" 
            :src="getMessageImageUrl(message.content)" 
            :alt="'图片'"
            class="message-image"
            @click="previewImage(message.content)"
          />
          <img 
            v-else-if="isEmojiMessage(message)" 
            :src="getMessageImageUrl(message.content)" 
            :alt="'表情'"
            class="message-emoji"
          />
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
}

.message-list::-webkit-scrollbar {
  display: none;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
}

.message-item.mine {
  flex-direction: row-reverse;
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
  background: #fff;
  padding: 10px 14px;
  border-radius: 18px;
  margin: 0 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  max-width: 100%;
}

.message-item.mine .message-bubble {
  background: #409eff;
}

.message-bubble p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-item.mine .message-bubble p {
  color: #fff;
}

.message-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  object-fit: cover;
}

.message-emoji {
  width: 60px;
  height: 60px;
  object-fit: contain;
}

.message-other {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.message-item.mine .message-other {
  color: #fff;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin: 4px 10px;
}

.no-messages {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #999;
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
  border-radius: 8px;
}
</style>