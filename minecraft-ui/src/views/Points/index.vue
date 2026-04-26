<template>
  <div class="points-main-container">
    <div class="points-sidebar">
      <h3 class="sidebar-title">积分系统</h3>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="mall">
          <el-icon><Shop /></el-icon>
          <span>积分商城</span>
        </el-menu-item>
        <el-menu-item index="records">
          <el-icon><Document /></el-icon>
          <span>积分记录</span>
        </el-menu-item>
        <el-menu-item index="orders">
          <el-icon><Ticket /></el-icon>
          <span>兑换订单</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="points-content">
      <component :is="currentComponent" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Shop, Document, Ticket } from '@element-plus/icons-vue';
import PointSmall from './components/pointsmall.vue';
import Records from './components/records.vue';
import Orders from './components/orders.vue';

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
  activeMenu.value = key;
};
</script>

<style scoped>
.points-main-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.points-sidebar {
  width: 200px;
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  padding: 20px 0;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.1);
}

.sidebar-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  padding: 0 20px 20px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.sidebar-menu {
  border-right: none;
}

.points-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .points-main-container {
    flex-direction: column;
  }
  
  .points-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #e4e7ed;
    padding: 10px 0;
  }
  
  .sidebar-title {
    padding: 0 10px 10px;
    margin-bottom: 10px;
  }
  
  .sidebar-menu {
    display: flex;
    flex-direction: row;
    overflow-x: auto;
  }
  
  .el-menu-item {
    min-width: 100px;
    text-align: center;
  }
  
  .points-content {
    padding: 10px;
  }
}
</style>