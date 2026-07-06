<template>
  <div class="official-website->ne
    <aside class=" sidebar"h1>
    <div class="sidebar-headdr>
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
    /* 主容器 — 干净、留白 */
    .official-website-container {
      display: flex;
      min-height: 100vh;
      max-width: 1440px;
      margin: 0 auto;
      background: #ffffff;
      box-shadow: 0 0 40px rgba(0, 0, 0, 0.03);
      border-radius: 28px;
      margin-top: 20px;
      margin-bottom: 20px;
      overflow: hidden;
      backdrop-filter: blur(2px);
    }

    /* ===== SIDEBAR — 精致毛玻璃效果 ===== */
    .sidebar {
      width: 260px;
      background: rgba(255, 255, 255, 0.75);
      backdrop-filter: saturate(180%) blur(16px);
      -webkit-backdrop-filter: saturate(180%) blur(16px);
      border-right: 1px solid rgba(0, 0, 0, 0.04);
      padding: 32px 0 24px 0;
      display: flex;
      flex-direction: column;
      flex-shrink: 0;
      transition: all 0.2s ease;
    }

    .sidebar-header {
      padding: 0 24px 28px 24px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.03);
    }

    .sidebar-header h2 {
      font-size: 20px;
      font-weight: 600;
      letter-spacing: -0.3px;
      background: linear-gradient(135deg, #1d1d1f 0%, #3a3a3c 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .sidebar-header h2::before {
      content: "⌘";
      font-weight: 300;
      font-size: 22px;
      background: none;
      -webkit-text-fill-color: #86868b;
      opacity: 0.7;
    }

    /* 导航列表 */
    .sidebar-nav {
      flex: 1;
      padding: 16px 12px 0 12px;
    }

    .sidebar-nav ul {
      list-style: none;
      margin: 0;
      padding: 0;
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .sidebar-nav li {
      display: flex;
      align-items: center;
      padding: 10px 16px;
      border-radius: 12px;
      font-size: 15px;
      font-weight: 450;
      color: #1d1d1f;
      cursor: pointer;
      transition: all 0.15s cubic-bezier(0.25, 0.1, 0.25, 1);
      letter-spacing: -0.2px;
      background: transparent;
      position: relative;
    }

    .sidebar-nav li .menu-icon {
      width: 28px;
      font-size: 18px;
      color: #86868b;
      transition: color 0.15s;
      text-align: center;
      margin-right: 14px;
    }

    .sidebar-nav li .menu-text {
      flex: 1;
    }

    /* hover 效果 */
    .sidebar-nav li:hover {
      background: rgba(0, 0, 0, 0.035);
    }

    .sidebar-nav li:hover .menu-icon {
      color: #1d1d1f;
    }

    /* active 状态 — 类似 Apple 侧边栏高亮 */
    .sidebar-nav li.active {
      background: rgba(0, 0, 0, 0.06);
      font-weight: 550;
      box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.5);
    }

    .sidebar-nav li.active .menu-icon {
      color: #0071e3;
    }

    .sidebar-nav li.active::after {
      content: "";
      position: absolute;
      right: 12px;
      top: 50%;
      transform: translateY(-50%);
      width: 6px;
      height: 6px;
      background: #0071e3;
      border-radius: 10px;
      opacity: 0.7;
    }

    /* ===== MAIN CONTENT — 柔和、留白 ===== */
    .main-content {
      flex: 1;
      background: #ffffff;
      padding: 40px 44px 48px 44px;
      display: flex;
      flex-direction: column;
    }

    .content-header {
      margin-bottom: 32px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);
      padding-bottom: 18px;
      display: flex;
      align-items: center;
    }

    .content-header h1 {
      font-size: 28px;
      font-weight: 600;
      letter-spacing: -0.5px;
      color: #1d1d1f;
      margin: 0;
      background: linear-gradient(145deg, #1d1d1f 20%, #3a3a3c 80%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    /* 过渡动画 — 平滑 */
    .fade-enter-active,
    .fade-leave-active {
      transition: opacity 0.25s ease, transform 0.2s ease;
    }
    .fade-enter-from,
    .fade-leave-to {
      opacity: 0;
      transform: translateY(6px);
    }

    /* 子管理组件占位 — 精致卡片风格 */
    .management-placeholder {
      background: #f9f9fb;
      border-radius: 24px;
      padding: 32px 28px;
      border: 1px solid rgba(0, 0, 0, 0.03);
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.02);
      flex: 1;
      transition: all 0.2s;
    }

    .management-placeholder h3 {
      font-size: 18px;
      font-weight: 500;
      color: #1d1d1f;
      margin-bottom: 10px;
    }

    .management-placeholder p {
      color: #86868b;
      font-size: 15px;
      margin-bottom: 20px;
    }

    .mock-controls {
      display: flex;
      flex-wrap: wrap;
      gap: 14px;
      margin-top: 12px;
    }

    .mock-btn {
      background: #ffffff;
      border: 1px solid #d2d2d7;
      padding: 10px 20px;
      border-radius: 40px;
      font-size: 14px;
      font-weight: 450;
      color: #1d1d1f;
      cursor: default;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
      transition: all 0.1s ease;
      display: inline-flex;
      align-items: center;
      gap: 8px;
      background: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(4px);
    }

    .mock-btn i {
      color: #0071e3;
      font-size: 14px;
    }

    .mock-btn:hover {
      background: #ffffff;
      border-color: #0071e3;
      box-shadow: 0 4px 12px rgba(0, 113, 227, 0.08);
      transform: scale(1.01);
    }

    .mock-input {
      background: #ffffff;
      border: 1px solid #d2d2d7;
      border-radius: 40px;
      padding: 10px 18px;
      font-size: 14px;
      width: 220px;
      outline: none;
      transition: border 0.15s;
      font-family: inherit;
    }

    .mock-input:focus {
      border-color: #0071e3;
      box-shadow: 0 0 0 3px rgba(0, 113, 227, 0.15);
    }

    .mock-tag {
      background: #e8e8ed;
      padding: 6px 14px;
      border-radius: 40px;
      font-size: 13px;
      color: #1d1d1f;
      display: inline-flex;
      align-items: center;
      gap: 6px;
    }

    .mock-tag i {
      color: #86868b;
      font-size: 12px;
    }

    /* 特定组件内布局 */
    .flex-row {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      align-items: center;
    }

    .mt-4 {
      margin-top: 16px;
    }

    /* 响应式 */
    @media (max-width: 820px) {
      .official-website-container {
        flex-direction: column;
        border-radius: 20px;
        margin: 12px;
      }
      .sidebar {
        width: 100%;
        border-right: none;
        border-bottom: 1px solid rgba(0, 0, 0, 0.04);
        padding: 16px 0 12px 0;
        backdrop-filter: saturate(180%) blur(20px);
        flex-direction: row;
        flex-wrap: wrap;
        align-items: center;
      }
      .sidebar-header {
        padding: 0 20px 12px 20px;
        border-bottom: none;
        width: 100%;
      }
      .sidebar-header h2 {
        font-size: 18px;
      }
      .sidebar-nav {
        padding: 0 12px 8px 12px;
        width: 100%;
        overflow-x: auto;
      }
      .sidebar-nav ul {
        flex-direction: row;
        flex-wrap: nowrap;
        gap: 4px;
        padding-bottom: 6px;
      }
      .sidebar-nav li {
        white-space: nowrap;
        padding: 8px 14px;
        font-size: 14px;
        flex-shrink: 0;
      }
      .sidebar-nav li .menu-text {
        display: inline;
      }
      .sidebar-nav li.active::after {
        display: none;
      }
      .main-content {
        padding: 24px 20px 32px 20px;
      }
      .content-header h1 {
        font-size: 24px;
      }
    }

    @media (max-width: 480px) {
      .sidebar-nav li {
        font-size: 13px;
        padding: 6px 12px;
      }
      .sidebar-nav li .menu-icon {
        margin-right: 8px;
        font-size: 16px;
        width: 22px;
      }
      .mock-input {
        width: 100%;
      }
      .flex-row {
        flex-direction: column;
        align-items: stretch;
      }
    }

    /* 滚动条美化 (macOS风格) */
    ::-webkit-scrollbar {
      width: 5px;
      height: 5px;
    }
    ::-webkit-scrollbar-track {
      background: transparent;
    }
    ::-webkit-scrollbar-thumb {
      background: #c6c6c8;
      border-radius: 12px;
    }
    ::-webkit-scrollbar-thumb:hover {
      background: #a8a8aa;
    }
</style>