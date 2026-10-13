<template>
  <div class="points-main-container">
    <aside class="points-sidebar">
      <div class="sidebar-brand">
        <div class="brand-icon">
          <el-icon>
            <GoldMedal />
          </el-icon>
        </div>
        <div class="brand-text">
          <h3 class="brand-title">积分中心</h3>
          <p class="brand-subtitle">POINTS CENTER</p>
        </div>
      </div>
      <el-menu :default-active="activeMenu" class="sidebar-menu" @select="handleMenuSelect">
        <el-menu-item index="back">
          <el-icon>
            <Back />
          </el-icon>
          <span>返回首页</span>
        </el-menu-item>
        <el-menu-item index="mall">
          <el-icon>
            <Shop />
          </el-icon>
          <span>积分商城</span>
        </el-menu-item>
        <el-menu-item index="records">
          <el-icon>
            <Document />
          </el-icon>
          <span>积分记录</span>
        </el-menu-item>
        <el-menu-item index="orders">
          <el-icon>
            <Ticket />
          </el-icon>
          <span>兑换订单</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <main class="points-content">
      <transition name="fade" mode="out-in">
        <component :is="currentComponent" :key="activeMenu" />
      </transition>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Back, Shop, Document, Ticket, GoldMedal } from '@element-plus/icons-vue';
import PointSmall from './components/pointsmall.vue';
import Records from './components/records.vue';
import Orders from './components/orders.vue';

const router = useRouter();

const activeMenu = ref('mall');

const currentComponent = computed(() => {
  switch (activeMenu.value) {
    case 'mall':
      return PointSmall;
    case 'records':
      return Records;
    case 'orders':
      return Orders;
    default:
      return PointSmall;
  }
});

const handleMenuSelect = (key) => {
  if (key === 'back') {
    // 有历史记录则返回上一页，无历史栈（直接打开/刷新进入）时兜底回首页
    if (window.history.state?.back) {
      router.back();
    } else {
      router.push('/');
    }
    return;
  }
  activeMenu.value = key;
};
</script>

<style scoped>
.points-main-container {
  position: relative;
  display: flex;
  min-height: 100vh;
  background-color: var(--theme-bg-surface-2);
}

/* 环境光背景 */
.points-main-container::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(900px 460px at 4% -6%, var(--theme-primary-soft), transparent 60%),
    radial-gradient(760px 420px at 100% 0%, color-mix(in srgb, var(--theme-accent-2) 9%, transparent), transparent 58%);
}

/* 侧边栏 */
.points-sidebar {
  position: sticky;
  top: 0;
  align-self: flex-start;
  z-index: 2;
  width: 244px;
  height: 100vh;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: color-mix(in srgb, var(--theme-bg-surface) 86%, transparent);
  backdrop-filter: saturate(180%) blur(18px);
  -webkit-backdrop-filter: saturate(180%) blur(18px);
  border-right: 1px solid var(--theme-hairline);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 20px;
  border-bottom: 1px solid var(--theme-hairline);
}

.brand-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
  background: linear-gradient(135deg, color-mix(in srgb, var(--theme-warning) 68%, var(--theme-bg-app)) 0%, var(--theme-warning) 100%);
  box-shadow: 0 8px 18px -8px color-mix(in srgb, var(--theme-warning) 75%, transparent);
  flex-shrink: 0;
}

.brand-title {
  font-size: 17px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
  margin: 0;
  line-height: 1.3;
}

.brand-subtitle {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.16em;
  color: var(--theme-text-3);
  margin: 3px 0 0;
}

.sidebar-menu {
  border-right: none;
  padding: 14px 12px;
  flex: 1;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 46px;
  line-height: 46px;
  border-radius: 12px;
  margin-bottom: 6px;
  color: var(--theme-text-2);
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}

.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 17px;
  margin-right: 10px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  font-weight: 600;
  background: linear-gradient(135deg, color-mix(in srgb, var(--theme-warning) 68%, var(--theme-bg-app)) 0%, var(--theme-warning) 100%);
  box-shadow: 0 10px 22px -12px color-mix(in srgb, var(--theme-warning) 85%, transparent);
}

/* 内容区 */
.points-content {
  position: relative;
  z-index: 1;
  flex: 1;
  min-width: 0;
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 28px 24px 56px;
}

/* 统一子页面容器为卡片，去除子组件自带的居中和双重内边距 */
.points-content :deep(.points-container),
.points-content :deep(.records-container),
.points-content :deep(.orders-container) {
  max-width: none;
  margin: 0;
  padding: 24px;
  background-color: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  border-radius: 20px;
  box-shadow: var(--theme-shadow-sm);
  animation: points-rise 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* 统一子页面标题：左对齐 + 强调竖条 + 分隔线 */
.points-content :deep(.points-title),
.points-content :deep(.records-title),
.points-content :deep(.orders-title) {
  display: flex;
  align-items: center;
  gap: 10px;
  text-align: left;
  font-size: 19px;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--theme-hairline);
}

.points-content :deep(.points-title::before),
.points-content :deep(.records-title::before),
.points-content :deep(.orders-title::before) {
  content: '';
  width: 5px;
  height: 20px;
  border-radius: 3px;
  background: var(--theme-gradient);
}

/* 页面切换淡入淡出 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@keyframes points-rise {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 900px) {
  .points-main-container {
    flex-direction: column;
  }

  .points-sidebar {
    position: sticky;
    top: 0;
    width: 100%;
    height: auto;
    border-right: none;
    border-bottom: 1px solid var(--theme-hairline);
  }

  .sidebar-brand {
    padding: 14px 16px;
  }

  .brand-icon {
    width: 36px;
    height: 36px;
    border-radius: 11px;
    font-size: 18px;
  }

  .brand-subtitle {
    display: none;
  }

  .sidebar-menu {
    display: flex;
    flex-direction: row;
    overflow-x: auto;
    padding: 8px 12px;
    flex: none;
    scrollbar-width: none;
  }

  .sidebar-menu::-webkit-scrollbar {
    display: none;
  }

  .sidebar-menu :deep(.el-menu-item) {
    min-width: 104px;
    margin-bottom: 0;
    margin-right: 6px;
    justify-content: center;
    flex-shrink: 0;
  }

  .points-content {
    padding: 16px 14px 48px;
  }

  .points-content :deep(.points-container),
  .points-content :deep(.records-container),
  .points-content :deep(.orders-container) {
    padding: 16px;
    border-radius: 16px;
  }
}
</style>