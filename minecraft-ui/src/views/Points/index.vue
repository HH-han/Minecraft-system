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
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 侧边栏 */
.points-sidebar {
  width: 220px;
  flex-shrink: 0;
  background-color: #fff;
  border-right: 1px solid #eef0f4;
  position: sticky;
  top: 0;
  align-self: flex-start;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 22px 20px;
  border-bottom: 1px solid #f0f2f5;
}

.brand-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
  background: linear-gradient(135deg, #fcd34d 0%, #f59e0b 100%);
  box-shadow: 0 4px 10px rgba(245, 158, 11, 0.35);
  flex-shrink: 0;
}

.brand-title {
  font-size: 17px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  line-height: 1.3;
}

.brand-subtitle {
  font-size: 10px;
  letter-spacing: 1px;
  color: #9ca3af;
  margin: 2px 0 0;
}

.sidebar-menu {
  border-right: none;
  padding: 12px;
  flex: 1;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 46px;
  line-height: 46px;
  border-radius: 10px;
  margin-bottom: 6px;
  color: #4b5563;
  font-size: 14px;
}

.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 17px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #f7f8fa;
  color: #f59e0b;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  box-shadow: 0 6px 14px rgba(245, 158, 11, 0.35);
}

/* 内容区 */
.points-content {
  flex: 1;
  min-width: 0;
  padding: 24px;
}

/* 统一子页面容器为白色卡片，去除子组件自带的居中和双重内边距 */
.points-content :deep(.points-container),
.points-content :deep(.records-container),
.points-content :deep(.orders-container) {
  max-width: none;
  margin: 0;
  padding: 24px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.06);
}

/* 统一子页面标题：左对齐 + 分隔线 */
.points-content :deep(.points-title),
.points-content :deep(.records-title),
.points-content :deep(.orders-title) {
  text-align: left;
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}

/* 积分商城：我的积分条 */
.points-content :deep(.user-points) {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
  background: linear-gradient(135deg, #fff7e6 0%, #fffbf0 100%);
  border: 1px solid #ffe7ba;
  border-radius: 10px;
  padding: 14px 20px;
  margin-bottom: 20px;
}

.points-content :deep(.points-label) {
  margin-right: 0;
}

.points-content :deep(.points-value) {
  color: #f59e0b;
  font-size: 22px;
}

/* 商品卡片悬停效果 */
.points-content :deep(.product-item) {
  border-radius: 12px;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}

.points-content :deep(.product-item:hover) {
  transform: translateY(-4px);
  border-color: #ffd591;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
}

.points-content :deep(.price-value) {
  color: #f59e0b;
}

/* 兑换按钮与主题色统一 */
.points-content :deep(.exchange-button.el-button--primary) {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  border: none;
}

.points-content :deep(.exchange-button.el-button--primary:hover),
.points-content :deep(.exchange-button.el-button--primary:focus) {
  background: linear-gradient(135deg, #fcd34d 0%, #f59e0b 100%);
  opacity: 0.92;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .points-main-container {
    flex-direction: column;
  }

  .points-sidebar {
    position: static;
    width: 100%;
    height: auto;
    border-right: none;
    border-bottom: 1px solid #eef0f4;
  }

  .sidebar-brand {
    padding: 14px 16px;
  }

  .brand-icon {
    width: 36px;
    height: 36px;
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
  }

  .sidebar-menu :deep(.el-menu-item) {
    min-width: 104px;
    margin-bottom: 0;
    margin-right: 6px;
    justify-content: center;
    flex-shrink: 0;
  }

  .points-content {
    padding: 12px;
  }

  .points-content :deep(.points-container),
  .points-content :deep(.records-container),
  .points-content :deep(.orders-container) {
    padding: 16px;
  }
}
</style>