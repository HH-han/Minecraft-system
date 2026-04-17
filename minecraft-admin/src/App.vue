<template>
  <!-- 设备检测组件（始终渲染但隐藏） -->
  <!-- Test hot reload in App.vue -->
  <DeviceDetects ref="deviceDetectsRef" style="display: none;" />
  <div v-if="isMobile">
    <DeviceDetects />
  </div>
  <!-- PC设备正常显示 -->
  <div v-else>
    <!-- loading 加载中效果-->
    <div id="app">
      <RefreshLoad v-if="isLoading" />
      <router-view v-else />
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import RefreshLoad from '@/components/TransitionalComponents/RefreshLoad.vue';
import DeviceDetects from '@/components/ResponseComponents/DeviceDetects.vue';

const isLoading = ref(true)
const deviceDetectsRef = ref()
const isMobile = ref(false)

onMounted(() => {
  // 模拟加载过程
  setTimeout(() => {
    isLoading.value = false
    
    // 获取DeviceDetects组件中的isMobile状态
    if (deviceDetectsRef.value) {
      isMobile.value = deviceDetectsRef.value.isMobile
    }
  }, 1000)
})
</script>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
