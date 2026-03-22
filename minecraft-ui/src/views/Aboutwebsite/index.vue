<template>
  <div class="about-container">
    <!-- 切换按钮 -->
    <div class="switcher-container">
      <div class="switcher glass-card">
        <button 
          class="switch-btn" 
          :class="{ active: currentComponent === 'aboutweb' }"
          @click="currentComponent = 'aboutweb'"
        >
          关于我们
        </button>
        <button 
          class="switch-btn" 
          :class="{ active: currentComponent === 'website' }"
          @click="currentComponent = 'website'"
        >
          网站介绍
        </button>
      </div>
    </div>
    
    <!-- 内容区域 -->
    <div class="content-container glass-card">
      <component :is="currentComponent === 'aboutweb' ? Aboutweb : WebsiteIntroduction" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Aboutweb from './components/Aboutweb.vue';
import WebsiteIntroduction from './components/WebsiteIntroduction.vue';

// 当前显示的组件
const currentComponent = ref('aboutweb');
</script>

<style scoped>
.about-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

/* 玻璃拟态样式 */
.glass-card {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
}

/* 切换器容器 */
.switcher-container {
  width: 100%;
  max-width: 600px;
}

.switcher {
  display: flex;
  border-radius: 10px;
  overflow: hidden;
  padding: 0.5rem;
}

.switch-btn {
  flex: 1;
  padding: 1rem 2rem;
  border: none;
  background: transparent;
  color: #666;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.switch-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #333;
}

.switch-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 内容容器 */
.content-container {
  width: 100%;
  min-height: 80vh;
  border-radius: 15px;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .about-container {
    padding: 1rem;
  }
  
  .switcher {
    flex-direction: column;
  }
  
  .switch-btn {
    padding: 0.8rem 1.5rem;
  }
  
  .content-container {
    min-height: 70vh;
  }
}
</style>