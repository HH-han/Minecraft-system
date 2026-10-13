<template>
  <div class="contact-item" @click="emit('user-profile')">
    <div class="avatar-wrapper">
      <img :src="avatar || DEFAULT_AVATAR" :alt="userName" class="contact-avatar" />
    </div>
    <div class="contact-info">
      <h4 class="contact-name">{{ userName }}</h4>
      <p class="contact-last-message">个人信息</p>
    </div>
    <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
  </div>
  <div class="section-divider"></div>
  <div class="contact-item" @click="emit('services')">
    <div class="avatar-wrapper">
      <div class="icon-avatar">
        <Icon name="credit-card" :size="'20px'" />
      </div>
    </div>
    <div class="contact-info">
      <h4 class="contact-name">服务</h4>
    </div>
    <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
  </div>
  <div class="contact-item" @click="emit('settings')">
    <div class="avatar-wrapper">
      <div class="icon-avatar">
        <Icon name="settings" :size="'20px'" />
      </div>
    </div>
    <div class="contact-info">
      <h4 class="contact-name">设置</h4>
    </div>
    <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
  </div>
</template>

<script setup>
import Icon from '../../components/Icon.vue'
import { DEFAULT_AVATAR } from '../constants'

defineProps({
  avatar: {
    type: String,
    default: ''
  },
  userName: {
    type: String,
    default: '用户'
  }
})

const emit = defineEmits(['user-profile', 'services', 'settings'])
</script>

<style scoped>
.section-divider {
  height: 8px;
  background: var(--theme-bg-surface-2);
  border-top: 1px solid var(--theme-hairline);
  border-bottom: 1px solid var(--theme-hairline);
}
</style>
