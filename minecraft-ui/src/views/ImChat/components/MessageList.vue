<template>
  <div class="message-list" ref="messageList">
    <div 
      v-for="message in messages" 
      :key="message.id"
      :class="['message-item', { mine: message.senderId === currentUserInfo?.id }]"
    >
      <img 
        v-if="message.senderId !== currentUserInfo?.id"
        :src="selectedContact?.avatar || defaultAvatar" 
        :alt="selectedContact?.name"
        class="message-avatar"
        @error="handleAvatarError"
      />
      <div class="message-content">
        <div :class="['message-bubble', { mine: message.senderId === currentUserInfo?.id }]">
          <p>{{ message.content }}</p>
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
  }
})

const emit = defineEmits(['user-loaded'])

const messageList = ref(null)
const currentUserInfo = ref(null)
const defaultAvatar = '/src/assets/defaultimage/moren.webp'

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
  /* 隐藏滚动条 */
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
}

.message-item.mine .message-bubble {
  background: #409eff;
}

.message-bubble p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
}

.message-item.mine .message-bubble p {
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
</style>