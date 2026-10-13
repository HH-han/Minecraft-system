<template>
  <div class="contacts-actions">
    <div class="contact-item" @click="emit('new-friends')">
      <div class="avatar-wrapper">
        <div class="icon-avatar">
          <Icon name="user-plus" :size="'20px'" />
        </div>
      </div>
      <div class="contact-info">
        <h4 class="contact-name">新的朋友</h4>
      </div>
      <span v-if="friendRequestCount > 0" class="entry-badge">{{
        friendRequestCount > 99 ? '99+' : friendRequestCount
      }}</span>
      <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
    </div>
    <div class="contact-item" @click="emit('view-groups')">
      <div class="avatar-wrapper">
        <div class="icon-avatar">
          <Icon name="users" :size="'20px'" />
        </div>
      </div>
      <div class="contact-info">
        <h4 class="contact-name">群聊</h4>
      </div>
      <span v-if="groupsCount" class="entry-count">{{ groupsCount }}</span>
      <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
    </div>
    <div class="contact-item" @click="emit('tags')">
      <div class="avatar-wrapper">
        <div class="icon-avatar">
          <Icon name="tag" :size="'20px'" />
        </div>
      </div>
      <div class="contact-info">
        <h4 class="contact-name">标签</h4>
      </div>
      <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
    </div>
    <div class="contact-item" @click="emit('officials')">
      <div class="avatar-wrapper">
        <div class="icon-avatar">
          <Icon name="building" :size="'20px'" />
        </div>
      </div>
      <div class="contact-info">
        <h4 class="contact-name">公众号、服务号</h4>
      </div>
      <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
    </div>
  </div>
</template>

<script setup>
import Icon from '../../components/Icon.vue'

defineProps({
  friendRequestCount: {
    type: Number,
    default: 0
  },
  groupsCount: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['new-friends', 'view-groups', 'tags', 'officials'])
</script>

<style scoped>
.contacts-actions {
  background: var(--theme-bg-surface);
  padding: 2px 0 6px;
}
</style>
