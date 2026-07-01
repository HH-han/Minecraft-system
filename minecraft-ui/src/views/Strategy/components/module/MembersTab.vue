<template>
  <div class="members-tab">
    <div class="tab-actions" v-if="isAdmin">
      <button class="btn-small" @click="$emit('invite')">邀请成员</button>
    </div>
    <div class="member-list">
      <div v-for="member in members" :key="member.id" class="member-item">
        <img :src="member.avatar || defaultAvatar" :alt="member.nickname" class="avatar" />
        <div class="member-info">
          <span class="name">{{ member.nickname || '未设置昵称' }}</span>
          <span class="join-time">加入于 {{ formatTime(member.joinTime) }}</span>
        </div>
        <span :class="['role-badge', member.role]">
          {{ getRoleName(member.role) }}
        </span>
        <div v-if="isAdmin && member.role !== 'owner'" class="member-actions">
          <button @click="$emit('remove', member.userId)">移除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  members: {
    type: Array,
    default: () => []
  },
  isAdmin: {
    type: Boolean,
    default: false
  },
  defaultAvatar: {
    type: String,
    default: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100'
  }
})

defineEmits(['invite', 'remove'])

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}

function getRoleName(role) {
  const map = { owner: '群主', admin: '管理员', member: '成员' }
  return map[role] || role
}
</script>

<style scoped>
.members-tab {
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.tab-actions {
  display: flex;
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-20);
}

.tab-actions .btn-secondary {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

.member-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-12);
}

.member-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-16);
  padding: var(--spacing-16);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  transition: all var(--transition-normal);
}

.member-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.04);
}

.member-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.member-info .name {
  font-weight: 500;
  font-size: 15px;
}

.join-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.role-badge {
  padding: var(--spacing-4) var(--spacing-10);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.role-badge.owner {
  background: var(--color-warning);
  color: #000;
}

.role-badge.admin {
  background: var(--color-primary);
  color: #fff;
}

.role-badge.member {
  background: var(--color-bg);
  color: var(--color-text);
}

.member-actions button {
  background: transparent;
  border: none;
  color: var(--color-danger);
  cursor: pointer;
  font-size: 13px;
  padding: var(--spacing-4) var(--spacing-12);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.member-actions button:hover {
  background: rgba(255, 59, 48, 0.1);
}
</style>