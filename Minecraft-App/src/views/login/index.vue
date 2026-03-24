<template>
  <view class="login-index">
    <view class="tab-content">
      <LoginComponent v-if="activeIndex === 0" />
      <RegisterComponent v-else-if="activeIndex === 1" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import LoginComponent from './login.vue';
import RegisterComponent from './register.vue';

const activeIndex = ref(0);
</script>

<style scoped>
.login-index {
  min-height: 100vh;
  background-color: var(--bg-color);
}
</style>