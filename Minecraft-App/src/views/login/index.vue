<template>
  <view class="login-index">
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: activeIndex === 0 }"
        @click="activeIndex = 0"
      >
        登录
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeIndex === 1 }"
        @click="activeIndex = 1"
      >
        注册
      </view>
    </view>
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

.tabs {
  display: flex;
  border-bottom: 1px solid var(--border-color);
  background-color: var(--card-color);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx;
  font-size: 28rpx;
  color: var(--text-color);
  border-bottom: 2px solid transparent;
}

.tab-item.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

.tab-content {
  flex: 1;
}
</style>