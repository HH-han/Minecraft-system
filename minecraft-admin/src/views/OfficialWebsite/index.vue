<template>
  <div class="official-website-container">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>官方网站管理</h2>
      </div>
      <nav class="sidebar-nav">
        <ul>
          <li 
            v-for="item in menuItems" 
            :key="item.key"
            :class="{ active: currentView === item.key }"
            @click="switchView(item.key)"
          >
            <span class="menu-icon">{{ item.icon }}</span>
            <span class="menu-text">{{ item.label }}</span>
          </li>
        </ul>
      </nav>
    </aside>

    <main class="main-content">
      <div class="content-header">
        <h1>{{ currentViewTitle }}</h1>
      </div>

      <transition name="fade" mode="out-in">
        <NavigationManagement v-if="currentView === 'navigation'" />
        <HeroManagement v-else-if="currentView === 'hero'" />
        <DestinationsManagement v-else-if="currentView === 'destinations'" />
        <ExperiencesManagement v-else-if="currentView === 'experiences'" />
        <StoriesManagement v-else-if="currentView === 'stories'" />
        <CtaManagement v-else-if="currentView === 'cta'" />
        <FooterManagement v-else-if="currentView === 'footer'" />
        <SettingsManagement v-else-if="currentView === 'settings'" />
      </transition>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import NavigationManagement from './components/NavigationManagement.vue';
import HeroManagement from './components/HeroManagement.vue';
import DestinationsManagement from './components/DestinationsManagement.vue';
import ExperiencesManagement from './components/ExperiencesManagement.vue';
import StoriesManagement from './components/StoriesManagement.vue';
import CtaManagement from './components/CtaManagement.vue';
import FooterManagement from './components/FooterManagement.vue';
import SettingsManagement from './components/SettingsManagement.vue';

const menuItems = [
  { key: 'navigation', label: '导航管理', icon: '📋' },
  { key: 'hero', label: '首页横幅', icon: '🎯' },
  { key: 'destinations', label: '目的地', icon: '🌍' },
  { key: 'experiences', label: '体验活动', icon: '🎪' },
  { key: 'stories', label: '故事分享', icon: '📖' },
  { key: 'cta', label: '行动号召', icon: '🚀' },
  { key: 'footer', label: '页脚信息', icon: '📝' },
  { key: 'settings', label: '网站设置', icon: '⚙️' },
];

const currentView = ref('navigation');

const currentViewTitle = computed(() => {
  const item = menuItems.find(item => item.key === currentView.value);
  return item ? item.label : '官方网站管理';
});

const switchView = (view) => {
  currentView.value = view;
};
</script>

<style scoped>
.official-website-container {
  display: flex;
  height: calc(100vh - 60px);
  gap: 0;
}

.sidebar {
  width: 220px;
  background-color: #1a1a2e;
  color: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #2d2d44;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #e0e0e0;
}

.sidebar-nav {
  flex: 1;
  padding: 10px 0;
}

.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-nav li {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.sidebar-nav li:hover {
  background-color: #2d2d44;
}

.sidebar-nav li.active {
  background-color: #16213e;
  border-left-color: #007bff;
}

.menu-icon {
  font-size: 18px;
  margin-right: 10px;
}

.menu-text {
  font-size: 14px;
  color: #c0c0c0;
}

.sidebar-nav li.active .menu-text {
  color: #fff;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f5f5f5;
}

.content-header {
  padding: 15px 20px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.content-header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>