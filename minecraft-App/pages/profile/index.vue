<template>
  <div class="profile-container">
    <!-- 用户信息区域 -->
    <div class="user-info">
      <div class="avatar">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square" alt="用户头像">
      </div>
      <div class="user-details">
        <h2>用户名</h2>
        <p>点击登录</p>
      </div>
    </div>
    
    <!-- 功能菜单 -->
    <div class="menu-section">
      <div class="menu-item" @click="navigateTo('/pages/order/index')">
        <div class="menu-icon">📋</div>
        <div class="menu-text">我的订单</div>
        <div class="menu-arrow">›</div>
      </div>
      <div class="menu-item" @click="navigateTo('/pages/collection/index')">
        <div class="menu-icon">❤️</div>
        <div class="menu-text">我的收藏</div>
        <div class="menu-arrow">›</div>
      </div>
      <div class="menu-item" @click="navigateTo('/pages/comment/index')">
        <div class="menu-icon">💬</div>
        <div class="menu-text">我的评论</div>
        <div class="menu-arrow">›</div>
      </div>
      <div class="menu-item" @click="navigateTo('/pages/chat/index')">
        <div class="menu-icon">💭</div>
        <div class="menu-text">在线客服</div>
        <div class="menu-arrow">›</div>
      </div>
    </div>
    
    <!-- 设置区域 -->
    <div class="menu-section">
      <div class="menu-item" @click="navigateTo('/pages/profile/settings')">
        <div class="menu-icon">⚙️</div>
        <div class="menu-text">设置</div>
        <div class="menu-arrow">›</div>
      </div>
      <div class="menu-item" @click="navigateTo('/pages/profile/about')">
        <div class="menu-icon">ℹ️</div>
        <div class="menu-text">关于我们</div>
        <div class="menu-arrow">›</div>
      </div>
    </div>
    
    <!-- 退出登录按钮 -->
    <button class="logout-btn" @click="logout">退出登录</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 导航到指定页面
const navigateTo = (url) => {
  uni.navigateTo({
    url
  })
}

// 退出登录
const logout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出登录吗？',
    success: function(res) {
      if (res.confirm) {
        // 清除登录状态
        uni.removeStorageSync('userInfo')
        uni.removeStorageSync('token')
        // 跳转到登录页
        uni.navigateTo({
          url: '/pages/login/index'
        })
      }
    }
  })
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: var(--card-background);
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 16px;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details h2 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 500;
  color: var(--text-color);
}

.user-details p {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary-color);
}

.menu-section {
  background-color: var(--card-background);
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.menu-icon {
  font-size: 20px;
  margin-right: 16px;
  width: 24px;
  text-align: center;
}

.menu-text {
  flex: 1;
  font-size: 16px;
  color: var(--text-color);
}

.menu-arrow {
  font-size: 18px;
  color: var(--text-secondary-color);
}

.logout-btn {
  width: 100%;
  background-color: #F44336;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 20px;
}

.logout-btn:hover {
  background-color: #D32F2F;
}
</style>