<template>
  <div
    v-for="contact in contacts"
    :key="contact.id"
    :class="['contact-item', { active: selectedId === contact.id }]"
  >
    <div class="contact-content" @click="emit('select-contact', contact)">
      <div class="avatar-wrapper">
        <img :src="contact.avatar || DEFAULT_AVATAR" :alt="contact.name" class="contact-avatar" />
        <span v-if="contact.isGroup" class="group-badge">
          <Icon name="users" :size="'10px'" />
        </span>
        <span v-if="contact.unreadCount > 0" class="unread-badge">{{
          contact.unreadCount > 99 ? '99+' : contact.unreadCount
        }}</span>
        <span v-if="!contact.isGroup" :class="['online-status', { online: contact.online }]"></span>
      </div>
      <div class="contact-main">
        <div class="contact-row-top">
          <h4 class="contact-name">
            <span class="name-text">{{ contact.name }}</span>
            <span v-if="contact.isGroup" class="contact-tag">群聊</span>
          </h4>
          <span v-if="contact.time" class="contact-time">{{ contact.time }}</span>
        </div>
        <p class="contact-last-message">
          <span v-if="contact.isGroup && contact.memberCount" class="member-count">{{ contact.memberCount }}人</span>{{ contact.lastMessage }}
        </p>
      </div>
    </div>
    <button class="detail-btn" @click.stop="emit('open-detail', contact)" title="详情">
      <Icon name="info" :size="'14px'" />
    </button>
  </div>

  <div v-if="contacts.length === 0" class="empty-state">
    <Icon name="message" :size="'48px'" />
    <p>暂无联系人</p>
  </div>
</template>

<script setup>
import Icon from '../../components/Icon.vue'
import { DEFAULT_AVATAR } from '../constants'

defineProps({
  contacts: {
    type: Array,
    default: () => []
  },
  selectedId: {
    type: [String, Number],
    default: ''
  }
})

const emit = defineEmits(['select-contact', 'open-detail'])
</script>

<style scoped>
.contact-content {
  flex: 1;
  display: flex;
  align-items: center;
  min-width: 0;
}

.contact-main {
  flex: 1;
  min-width: 0;
}

.contact-row-top {
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-time {
  flex-shrink: 0;
  font-size: 11.5px;
  color: var(--theme-text-3);
}

.contact-tag {
  flex-shrink: 0;
  font-size: 10px;
  color: var(--theme-text-3);
  background: var(--theme-bg-surface-2);
  padding: 1px 5px;
  border-radius: 4px;
  font-weight: 400;
  line-height: 1.4;
}

.member-count {
  font-size: 12px;
  color: var(--theme-text-3);
  margin-right: 6px;
}

.detail-btn {
  width: 26px;
  height: 26px;
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-left: 2px;
  transition: background 0.15s ease, color 0.15s ease;
}

.detail-btn:hover {
  background: var(--theme-bg-active);
  color: var(--theme-text-2);
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--theme-danger);
  color: var(--theme-primary-text);
  font-size: 11px;
  font-weight: 600;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.group-badge {
  position: absolute;
  bottom: -3px;
  right: -3px;
  width: 16px;
  height: 16px;
  background: var(--theme-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1.5px solid var(--theme-bg-surface);
  color: var(--theme-primary-text);
  box-sizing: border-box;
}

.online-status {
  position: absolute;
  bottom: -1px;
  right: -1px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--theme-bg-surface-2);
  border: 2px solid var(--theme-bg-surface);
  box-sizing: border-box;
}

.online-status.online {
  background: var(--theme-success);
}
</style>
