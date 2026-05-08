<template>
  <div class="about-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">关于</h2>
      </div>
      <nav class="sidebar-nav">
        <button class="nav-item" @click="handleBackClick">
          <svg t="1777778547445" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
            p-id="11472">
            <path
              d="M512 0c281.6 0 512 230.4 512 512s-230.4 512-512 512-512-230.4-512-512 230.4-512 512-512z m0 960c249.6 0 448-198.4 448-448s-198.4-448-448-448-448 198.4-448 448 198.4 448 448 448z"
              fill="#777777" p-id="11473"></path>
            <path
              d="M588.8 262.4c6.4-6.4 32-6.4 44.8 0 6.4 12.8 6.4 38.4 0 44.8L428.8 512l204.8 204.8c12.8 12.8 12.8 32 0 44.8-12.8 12.8-32 12.8-44.8 0L364.8 537.6c-12.8-12.8-12.8-32 0-44.8l224-230.4z"
              fill="#777777" p-id="11474"></path>
          </svg>
          <span>返回首页</span>
        </button>
        <button class="nav-item" :class="{ active: currentComponent === 'aboutweb' }"
          @click="currentComponent = 'aboutweb'">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          <span>关于我们</span>
        </button>
        <button class="nav-item" :class="{ active: currentComponent === 'website' }"
          @click="currentComponent = 'website'">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="2" y1="12" x2="22" y2="12"></line>
            <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
          </svg>
          <span>网站介绍</span>
        </button>
        <button class="nav-item" :class="{ active: currentComponent === 'privacy' }"
          @click="currentComponent = 'privacy'">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
          </svg>
          <span>隐私政策</span>
        </button>
        <button class="nav-item" :class="{ active: currentComponent === 'terms' }" @click="currentComponent = 'terms'">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
          <span>服务条款</span>
        </button>
        <button class="nav-item" :class="{ active: currentComponent === 'cookie' }"
          @click="currentComponent = 'cookie'">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <circle cx="8" cy="9" r="1"></circle>
            <circle cx="15" cy="9" r="1"></circle>
            <circle cx="9" cy="14" r="1"></circle>
            <circle cx="14" cy="14" r="1"></circle>
          </svg>
          <span>Cookie政策</span>
        </button>
      </nav>
    </aside>

    <main class="main-content">
      <component :is="getComponent()" />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Aboutweb from './components/Aboutweb.vue';
import WebsiteIntroduction from './components/WebsiteIntroduction.vue';
import PrivacyPolicy from './components/PrivacyPolicy.vue';
import TermsOfService from './components/TermsOfService.vue';
import CookiePolicy from './components/CookiePolicy.vue';

const componentMap = {
  aboutweb: Aboutweb,
  website: WebsiteIntroduction,
  privacy: PrivacyPolicy,
  terms: TermsOfService,
  cookie: CookiePolicy
};

const handleBackClick = () => {
  window.location.href = '/';
};

const currentComponent = ref('aboutweb');

const getComponent = () => componentMap[currentComponent.value] || Aboutweb;
</script>

<style scoped>
.about-layout {
  display: flex;
  min-height: 100vh;
  background: #ffffff;
}

.sidebar {
  width: 280px;
  background: #f5f5f7;
  border-right: 1px solid #e5e5e7;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  height: 100vh;
}

.sidebar-header {
  padding: 40px 24px 24px;
  border-bottom: 1px solid #e5e5e7;
}

.sidebar-title {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
}

.sidebar-nav {
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: none;
  background: transparent;
  color: #6e6e73;
  font-size: 15px;
  font-weight: 500;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  width: 100%;
}

.nav-item svg {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-item:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #1d1d1f;
}

.nav-item.active {
  background: #ffffff;
  color: #2997ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.main-content {
  flex: 1;
  min-width: 0;
}

@media (max-width: 768px) {
  .about-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: auto;
    position: static;
    border-right: none;
    border-bottom: 1px solid #e5e5e7;
  }

  .sidebar-header {
    padding: 24px 20px 16px;
  }

  .sidebar-title {
    font-size: 22px;
  }

  .sidebar-nav {
    flex-direction: row;
    padding: 0 12px 12px;
    overflow-x: auto;
  }

  .nav-item {
    flex-direction: column;
    padding: 12px 16px;
    gap: 8px;
    font-size: 13px;
    white-space: nowrap;
  }

  .nav-item span {
    display: block;
  }
}
</style>
