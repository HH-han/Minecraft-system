<template>
  <div class="about-container">
    <div class="about-header">
      <h1>关于我们</h1>
    </div>
    
    <div class="about-content">
      <div class="about-logo">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=travel%20app%20logo%20modern%20design&image_size=square" alt="应用logo">
      </div>
      
      <div class="about-info">
        <h2>旅行助手</h2>
        <p class="version">版本：v1.0.0</p>
        <p class="description">
          旅行助手是一款专注于为用户提供全方位旅行服务的应用，包括景点推荐、酒店预订、美食探索、票务预订等功能。我们致力于为用户打造便捷、舒适的旅行体验，让每一次出行都充满乐趣。
        </p>
      </div>
      
      <div class="about-section">
        <h3>功能特点</h3>
        <ul class="feature-list">
          <li>• 景点推荐与详情</li>
          <li>• 酒店预订与比较</li>
          <li>• 美食探索与推荐</li>
          <li>• 票务预订与管理</li>
          <li>• 旅行计划制定</li>
          <li>• 在线客服支持</li>
        </ul>
      </div>
      
      <div class="about-section">
        <h3>联系我们</h3>
        <p>邮箱：support@travelassistant.com</p>
        <p>电话：400-123-4567</p>
        <p>地址：北京市朝阳区旅行大厦1001室</p>
      </div>
      
      <div class="about-section">
        <h3>用户协议</h3>
        <p><a href="#">《用户服务协议》</a></p>
        <p><a href="#">《隐私政策》</a></p>
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
.about-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.about-header {
  margin-bottom: 20px;
}

.about-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.about-content {
  background-color: var(--card-background);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.about-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.about-logo img {
  width: 100px;
  height: 100px;
  border-radius: 20px;
}

.about-info {
  text-align: center;
  margin-bottom: 30px;
}

.about-info h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
}

.version {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
}

.description {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-color);
  text-align: left;
}

.about-section {
  margin-bottom: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.about-section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
}

.feature-list {
  margin: 0;
  padding-left: 20px;
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-color);
}

.about-section p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--text-color);
}

.about-section a {
  color: var(--primary-color);
  text-decoration: none;
  transition: all 0.3s ease;
}

.about-section a:hover {
  text-decoration: underline;
}
</style>