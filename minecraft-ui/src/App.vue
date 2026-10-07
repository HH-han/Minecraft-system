<template>
  <!-- Element Plus 语言包随 i18n 联动切换 -->
  <el-config-provider :locale="elementLocale">
    <!-- 设备检测组件（始终渲染但隐藏） -->
    <DeviceDetects ref="deviceDetectsRef" style="display: none;" />
    <!-- PC设备正常显示 -->
    <div>
      <!-- loading 加载中效果-->
      <div id="app">
        <RefreshLoad v-if="isLoading" />
        <router-view v-else />
      </div>
      <!-- 悬浮按钮 -->
      <div>
        <FloatingButton />
      </div>
      <!-- 系统公告弹窗（全局唯一，弹窗队列驱动） -->
      <AnnouncementPopup />
      <!-- 自定义光标 -->
      <MouseStyle />
    </div>
  </el-config-provider>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import RefreshLoad from '@/components/TransitionalComponents/RefreshLoad.vue';
import FloatingButton from '@/components/ComponentButton/FloatingButton.vue';
import DeviceDetects from '@/components/ResponseComponents/DeviceDetects.vue';
import AnnouncementPopup from '@/components/AnnouncementComponents/AnnouncementPopup.vue';
import MouseStyle from '@/views/MouseStyle/index.vue'
import { useAnnouncementStore } from '@/stores/announcementStore'
import { initAnnouncementSse, closeAnnouncementSse } from '@/utils/sse'
import { elementLocale } from '@/locales'

const isLoading = ref(true)
const deviceDetectsRef = ref()
const isMobile = ref(false)

// 公告模块：拉取展示数据 + 初始化 SSE 推送
const announcementStore = useAnnouncementStore()
onMounted(() => {
  announcementStore.fetchActive()
  announcementStore.fetchUnread()
  initAnnouncementSse((ann) => announcementStore.pushRealtime(ann))
})
onUnmounted(() => {
  closeAnnouncementSse()
})

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
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;500;700&display=swap');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Montserrat', 'Noto Sans SC', sans-serif;
}

/* 兼容各浏览器的滚动条样式 */
body{
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  overflow-y: auto;
}

body::-webkit-scrollbar {
  width: 2px;
  height: 150px;
}

body::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

body::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

body::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>
