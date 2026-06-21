<template>
  <div class="contact-detail" v-if="contact">
    <div class="detail-header">
      <button class="close-btn" @click="onClose">
        <Icon name="close" :size="'18px'" />
      </button>
    </div>

    <div class="detail-content">
      <div class="avatar-section">
        <img 
          :src="contact.avatar || defaultAvatar" 
          :alt="contact.name"
          class="detail-avatar"
        />
        <div class="name-section">
          <h3 class="detail-name">{{ contact.name }}</h3>
          <button v-if="!isGroup" class="edit-remark-btn" @click="onEditFriendRemark">
            <Icon name="edit" :size="'14px'" />
          </button>
        </div>
      </div>

      <div class="info-section">
        <div class="info-item">
          <span class="info-label">ID</span>
          <span class="info-value">{{ contact.id }}</span>
        </div>
        <div class="info-item" v-if="isGroup">
          <span class="info-label">成员数</span>
          <span class="info-value">{{ contact.memberCount || 0 }}</span>
        </div>
        <div class="info-item" v-if="!isGroup && contact.phone">
          <span class="info-label">手机号</span>
          <span class="info-value">{{ contact.phone }}</span>
        </div>
        <div class="info-item" v-if="contact.email">
          <span class="info-label">邮箱</span>
          <span class="info-value">{{ contact.email }}</span>
        </div>
        <div class="info-item" v-if="contact.online !== undefined">
          <span class="info-label">状态</span>
          <span :class="['status-badge', { online: contact.online }]">
            {{ contact.online ? '在线' : '离线' }}
          </span>
        </div>
        <div class="info-item" v-if="contact.createTime">
          <span class="info-label">添加时间</span>
          <span class="info-value">{{ formatTime(contact.createTime) }}</span>
        </div>
      </div>

      <div class="action-section">
        <button class="action-btn primary" @click="onSendMessage">
          <Icon name="message" :size="'16px'" />
          <span>发送消息</span>
        </button>
        <button class="action-btn secondary" v-if="isGroup" @click="onInviteFriend">
          <Icon name="user-plus" :size="'16px'" />
          <span>邀请好友</span>
        </button>
        <button class="action-btn secondary" v-if="isGroup" @click="onEditGroup">
          <Icon name="settings" :size="'16px'" />
          <span>{{ contact.isCreator ? '编辑群资料' : '设置备注' }}</span>
        </button>
        <button class="action-btn danger" v-if="!isGroup" @click="onDeleteContact">
          <Icon name="trash" :size="'16px'" />
          <span>删除好友</span>
        </button>
        <button class="action-btn danger" v-if="isGroup && contact.isCreator" @click="onDeleteGroup">
          <Icon name="trash" :size="'16px'" />
          <span>解散群组</span>
        </button>
        <button class="action-btn secondary" v-if="isGroup && !contact.isCreator" @click="onLeaveGroup">
          <Icon name="log-out" :size="'16px'" />
          <span>退出群组</span>
        </button>
      </div>
    </div>
  </div>
  <div class="contact-detail-empty" v-else>
    <Icon name="info" :size="'64px'" />
    <p>选择联系人查看详情</p>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { updateFriendRemark } from '@/api/chat'
import { useAuthStore } from '@/stores/auth'
import Icon from './Icon.vue'

const props = defineProps({
  contact: {
    type: Object,
    default: null
  }
})

const emit = defineEmits([
  'close',
  'send-message',
  'delete-contact',
  'delete-group',
  'leave-group',
  'invite-friend',
  'update-remark',
  'edit-group',
  'edit-friend-remark'
])

const authStore = useAuthStore()
const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const isGroup = computed(() => {
  return props.contact?.isGroup || props.contact?.groupId
})

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const onClose = () => {
  emit('close')
}

const onSendMessage = () => {
  emit('send-message', props.contact)
}

const onDeleteContact = () => {
  ElMessageBox.confirm('确定要删除此好友吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    emit('delete-contact', props.contact)
  }).catch(() => {})
}

const onDeleteGroup = () => {
  ElMessageBox.confirm('确定要解散此群组吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    emit('delete-group', props.contact)
  }).catch(() => {})
}

const onLeaveGroup = () => {
  ElMessageBox.confirm('确定要退出此群组吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    emit('leave-group', props.contact)
  }).catch(() => {})
}

const onInviteFriend = () => {
  emit('invite-friend', props.contact)
}

const onEditGroup = () => {
  emit('edit-group', props.contact)
}

const onEditFriendRemark = () => {
  emit('edit-friend-remark', props.contact)
}
</script>

<style scoped>
.contact-detail {
  width: 320px;
  height: 100%;
  background: #ffffff;
  border-left: 1px solid #d2d2d6;
  display: flex;
  flex-direction: column;
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.contact-detail-empty {
  width: 320px;
  height: 100%;
  background: #ffffff;
  border-left: 1px solid #d2d2d6;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #6e6e73;
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.contact-detail-empty .svg-icon {
  margin-bottom: 16px;
  opacity: 0.4;
}

.contact-detail-empty p {
  margin: 0;
  font-size: 15px;
  letter-spacing: 0.2px;
}

.detail-header {
  display: flex;
  justify-content: flex-end;
  padding: 16px;
  border-bottom: 1px solid #d2d2d6;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: #6e6e73;
  cursor: pointer;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s ease;
}

.close-btn:hover {
  background: #f5f5f7;
  color: #1d1d1f;
}

.detail-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0;
  border-bottom: 1px solid #f5f5f7;
  margin-bottom: 24px;
}

.detail-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.name-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-name {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
  letter-spacing: -0.2px;
}

.edit-remark-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: #6e6e73;
  cursor: pointer;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s ease;
}

.edit-remark-btn:hover {
  color: #2997ff;
  background: #f5f5f7;
}

.remark-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  font-size: 14px;
  margin-top: 12px;
  outline: none;
  box-sizing: border-box;
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  transition: 0.2s ease;
}

.remark-input:focus {
  border-color: #2997ff;
}

.info-section {
  margin-bottom: 32px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f7;
}

.info-label {
  font-size: 14px;
  color: #6e6e73;
}

.info-value {
  font-size: 14px;
  color: #1d1d1f;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  background: #f5f5f7;
  color: #6e6e73;
  font-weight: 500;
}

.status-badge.online {
  background: #e8f5e9;
  color: #34c759;
}

.action-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  width: 100%;
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: 0.2s ease;
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.action-btn.primary {
  background: #1d1d1f;
  color: #ffffff;
  border-radius: 40px;
}

.action-btn.primary:hover {
  background: #000000;
  transform: scale(1.02);
}

.action-btn.secondary {
  background: #f5f5f7;
  color: #1d1d1f;
}

.action-btn.secondary:hover {
  background: #e8e8ed;
  transform: scale(1.02);
}

.action-btn.danger {
  background: #ffffff;
  color: #ff3b30;
  border: 1px solid #ff3b30;
}

.action-btn.danger:hover {
  background: #fff5f5;
  transform: scale(1.02);
}
</style>
