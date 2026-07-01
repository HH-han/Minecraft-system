<template>
  <div class="tab-content">
    <div class="section-header">
      <h2>通知中心</h2>
      <button v-if="notifications.length > 0" class="mark-all-btn" @click="$emit('markAllRead')">
        全部已读
      </button>
    </div>

    <div v-if="notifications.length === 0" class="empty-state">
      <div class="empty-icon">🔔</div>
      <h3>暂无通知</h3>
      <p>所有消息已处理完毕</p>
    </div>

    <div v-else class="notification-list">
      <div
        v-for="item in notifications"
        :key="item.id"
        :class="['notification-item', { unread: !item.isRead }]"
        @click="$emit('handle', item)"
      >
        <div class="notification-icon">
          {{ getNotificationIcon(item.type) }}
        </div>
        <div class="notification-content">
          <p class="notification-title">{{ item.title }}</p>
          <p class="notification-message">{{ item.message }}</p>
          <span class="notification-time">{{ formatTime(item.createdAt) }}</span>
        </div>
        <button v-if="!item.isRead" class="read-btn" @click.stop="$emit('markAsRead', item.id)">
          ✓
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  notifications: {
    type: Array,
    default: () => []
  }
})

defineEmits(['markAllRead', 'handle', 'markAsRead'])

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return date.toLocaleDateString('zh-CN')
}

function getNotificationIcon(type) {
  const map = {
    group_invite: '👥',
    join_request: '📝',
    system: '🔔',
    post_like: '❤️',
    post_comment: '💬'
  }
  return map[type] || '📢'
}
</script>

<style scoped>
.tab-content {
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-24);
}

.section-header h2 {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.1;
}

.mark-all-btn {
  background: transparent;
  border: none;
  color: var(--color-primary);
  cursor: pointer;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 80px 40px;
  background: var(--color-bg-secondary);
  border-radius: 28px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 22px;
  margin-bottom: 8px;
}

.empty-state p {
  color: var(--color-text-secondary);
  margin-bottom: 24px;
}

.notification-list {
  background: var(--color-bg);
  border-radius: 24px;
  overflow: hidden;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  border-bottom: 1px solid var(--color-border);
  cursor: pointer;
  transition: background 0.2s ease;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item.unread {
  background: rgba(41, 151, 255, 0.05);
}

.notification-item:hover {
  background: var(--color-bg-secondary);
}

.notification-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.notification-message {
  color: var(--color-text-secondary);
  font-size: 14px;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.read-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
}

@media (max-width: 767px) {
  .section-header {
    flex-direction: column;
    gap: var(--spacing-12);
    align-items: flex-start;
  }
}
</style>