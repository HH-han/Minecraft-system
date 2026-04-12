<template>
  <div class="settings-container">
    <div class="settings-header">
      <h1>设置</h1>
    </div>
    
    <div class="settings-list">
      <div class="settings-item">
        <div class="settings-label">账号与安全</div>
        <div class="settings-action">
          <span class="settings-desc">修改密码、绑定手机</span>
          <span class="settings-arrow">›</span>
        </div>
      </div>
      
      <div class="settings-item">
        <div class="settings-label">隐私设置</div>
        <div class="settings-action">
          <span class="settings-desc">消息通知、隐私保护</span>
          <span class="settings-arrow">›</span>
        </div>
      </div>
      
      <div class="settings-item">
        <div class="settings-label">通用设置</div>
        <div class="settings-action">
          <span class="settings-desc">语言、主题</span>
          <span class="settings-arrow">›</span>
        </div>
      </div>
      
      <div class="settings-item">
        <div class="settings-label">清除缓存</div>
        <div class="settings-action">
          <span class="settings-desc">12.3MB</span>
          <span class="settings-arrow">›</span>
        </div>
      </div>
      
      <div class="settings-item">
        <div class="settings-label">检查更新</div>
        <div class="settings-action">
          <span class="settings-desc">当前版本：v1.0.0</span>
          <span class="settings-arrow">›</span>
        </div>
      </div>
      
      <div class="settings-item">
        <div class="settings-label">意见反馈</div>
        <div class="settings-action">
          <span class="settings-desc">提交问题与建议</span>
          <span class="settings-arrow">›</span>
        </div>
      </div>
    </div>
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
</script>

<style scoped>
.settings-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.settings-header {
  margin-bottom: 20px;
}

.settings-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.settings-list {
  background-color: var(--card-background);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.settings-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.settings-item:last-child {
  border-bottom: none;
}

.settings-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.settings-label {
  font-size: 16px;
  color: var(--text-color);
  font-weight: 500;
}

.settings-action {
  display: flex;
  align-items: center;
  gap: 12px;
}

.settings-desc {
  font-size: 14px;
  color: var(--text-secondary-color);
}

.settings-arrow {
  font-size: 18px;
  color: var(--text-secondary-color);
}
</style>