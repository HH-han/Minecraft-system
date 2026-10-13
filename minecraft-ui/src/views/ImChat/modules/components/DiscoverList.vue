<template>
  <div class="contact-item" @click="emit('moments')">
    <div class="avatar-wrapper">
      <div class="icon-avatar">
        <Icon name="camera" :size="'20px'" />
      </div>
    </div>
    <div class="contact-info">
      <h4 class="contact-name">朋友圈</h4>
    </div>
    <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
  </div>
  <div class="contact-item" @click="emit('scan')">
    <div class="avatar-wrapper">
      <div class="icon-avatar">
        <Icon name="scan" :size="'20px'" />
      </div>
    </div>
    <div class="contact-info">
      <h4 class="contact-name">扫一扫</h4>
    </div>
    <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
  </div>
  <div class="contact-item" @click="emit('nearby')">
    <div class="avatar-wrapper">
      <div class="icon-avatar">
        <Icon name="map-pin" :size="'20px'" />
      </div>
    </div>
    <div class="contact-info">
      <h4 class="contact-name">附近的人</h4>
    </div>
    <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
  </div>
  <div class="contact-item" @click="emit('shake')">
    <div class="avatar-wrapper">
      <div class="icon-avatar">
        <Icon name="refresh-cw" :size="'20px'" />
      </div>
    </div>
    <div class="contact-info">
      <h4 class="contact-name">摇一摇</h4>
    </div>
    <Icon name="chevron-right" :size="'16px'" class="arrow-icon" />
  </div>
</template>

<script setup>
import Icon from '../../components/Icon.vue'

const emit = defineEmits(['moments', 'scan', 'nearby', 'shake'])
</script>
