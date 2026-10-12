<template>
  <div v-if="visible" class="modal-overlay" @click.self="onClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3>好友申请</h3>
        <button class="close-btn" @click="onClose">
          <Icon name="close" :size="'14px'" />
        </button>
      </div>
      <div class="modal-body">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="requests.length === 0" class="no-requests">
          <Icon name="user" :size="'48px'" />
          <p>暂无好友申请</p>
        </div>
        <div v-else class="request-list">
          <div v-for="req in requests" :key="req.id" class="request-item">
            <div class="request-info">
              <div class="avatar-wrapper">
                <img 
                  :src="req.avatar || defaultAvatar" 
                  :alt="req.username"
                  class="avatar"
                  @error="handleAvatarError"
                />
              </div>
              <div class="details">
                <div class="name">{{ req.username || '未知用户' }}</div>
                <div class="message">{{ req.message || '申请添加你为好友' }}</div>
                <div class="time">{{ formatTime(req.createTime) }}</div>
              </div>
            </div>
            <div class="actions">
              <button class="btn btn-accept" @click="handleRequest(req.id, 'accept')">
                <Icon name="check" :size="'16px'" />
                同意
              </button>
              <button class="btn btn-reject" @click="handleRequest(req.id, 'reject')">
                <Icon name="close" :size="'16px'" />
                拒绝
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import Icon from './Icon.vue'
import { getPendingFriendRequestsWithInfo, acceptFriendRequest, rejectFriendRequest } from '@/api/chat'
import { useAuthStore } from '@/stores/auth'
import { getUserInfo } from '@/utils/storage'

const authStore = useAuthStore()

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'request-handled'])

const loading = ref(false)
const requests = ref([])
const defaultAvatar = '/src/assets/defaultimage/moren.webp'

watch(() => props.visible, async (newVal) => {
  if (newVal) {
    await loadRequests()
  }
})

const getUserId = () => {
  return authStore.userInfo?.id || getUserInfo()?.id || null
}

const loadRequests = async () => {
  loading.value = true
  try {
    const userId = getUserId()
    if (!userId) {
      console.warn('未获取到用户ID')
      return
    }
    const response = await getPendingFriendRequestsWithInfo(userId)
    if (response.code === 200) {
      requests.value = response.data || []
    }
  } catch (error) {
    console.error('获取好友申请失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAvatarError = (e) => {
  e.target.src = defaultAvatar
}

const handleRequest = async (requestId, status) => {
  try {
    const response = status === 'accept'
      ? await acceptFriendRequest(requestId)
      : await rejectFriendRequest(requestId)

    if (response.code === 200) {
      requests.value = requests.value.filter(r => r.id !== requestId)
      emit('request-handled', { requestId, status })
    }
  } catch (error) {
    console.error('处理好友申请失败:', error)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}

const onClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--theme-overlay);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 420px;
  max-height: 80vh;
  background: var(--theme-bg-elevated);
  border-radius: 24px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: var(--theme-shadow-lg);
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--theme-border);
}

.modal-header h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: var(--theme-text-1);
  letter-spacing: -0.01em;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: var(--theme-bg-surface-2);
  border-radius: 50%;
  color: var(--theme-text-2);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s ease;
}

.close-btn:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.modal-body {
  padding: 16px 24px;
  overflow-y: auto;
  flex: 1;
}

.loading {
  text-align: center;
  padding: 40px;
  color: var(--theme-text-2);
  font-size: 14px;
}

.no-requests {
  text-align: center;
  padding: 40px;
  color: var(--theme-text-2);
}

.no-requests p {
  margin-top: 12px;
  font-size: 14px;
}

.request-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.request-item {
  padding: 14px;
  background: var(--theme-bg-surface-2);
  border-radius: 16px;
  transition: 0.2s ease;
}

.request-item:hover {
  background: var(--theme-bg-hover);
}

.request-info {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.avatar-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--theme-bg-surface-2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.details {
  flex: 1;
  min-width: 0;
}

.name {
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin-bottom: 4px;
}

.message {
  font-size: 13px;
  color: var(--theme-text-2);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.time {
  font-size: 12px;
  color: var(--theme-text-3);
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.btn {
  padding: 6px 16px;
  border: none;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: 0.2s ease;
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.btn-accept {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  border-radius: 40px;
}

.btn-accept:hover {
  background: var(--theme-primary-hover);
}

.btn-reject {
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  border-radius: 12px;
}

.btn-reject:hover {
  background: var(--theme-bg-hover);
}
</style>
