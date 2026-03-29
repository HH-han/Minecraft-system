<template>
  <div class="collection-container">
    <div class="collection-header">
      <h1>我的收藏</h1>
    </div>
    
    <div class="collection-tabs">
      <div class="tab-item active">全部</div>
      <div class="tab-item">景点</div>
      <div class="tab-item">酒店</div>
      <div class="tab-item">美食</div>
    </div>
    
    <div class="collection-list">
      <div class="collection-item">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20scenic%20landscape&image_size=landscape_4_3" alt="收藏景点" class="collection-image">
        <div class="collection-info">
          <h3>长城</h3>
          <p class="collection-desc">中国著名景点，历史悠久</p>
          <div class="collection-meta">
            <span class="collection-type">景点</span>
            <span class="collection-time">2026-03-28</span>
          </div>
        </div>
        <div class="collection-action">
          <button class="action-btn" @click="removeCollection">
            <span class="icon">❤️</span>
          </button>
        </div>
      </div>
      
      <div class="collection-item">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=luxury%20hotel%20exterior&image_size=landscape_4_3" alt="收藏酒店" class="collection-image">
        <div class="collection-info">
          <h3>豪华酒店</h3>
          <p class="collection-desc">五星级酒店，舒适体验</p>
          <div class="collection-meta">
            <span class="collection-type">酒店</span>
            <span class="collection-time">2026-03-27</span>
          </div>
        </div>
        <div class="collection-action">
          <button class="action-btn" @click="removeCollection">
            <span class="icon">❤️</span>
          </button>
        </div>
      </div>
      
      <div class="collection-item">
        <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=chinese%20food%20dish&image_size=landscape_4_3" alt="收藏美食" class="collection-image">
        <div class="collection-info">
          <h3>北京烤鸭</h3>
          <p class="collection-desc">传统名菜，口感独特</p>
          <div class="collection-meta">
            <span class="collection-type">美食</span>
            <span class="collection-time">2026-03-26</span>
          </div>
        </div>
        <div class="collection-action">
          <button class="action-btn" @click="removeCollection">
            <span class="icon">❤️</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 移除收藏
const removeCollection = () => {
  uni.showModal({
    title: '移除收藏',
    content: '确定要移除这个收藏吗？',
    success: function(res) {
      if (res.confirm) {
        // 移除收藏逻辑
        uni.showToast({
          title: '已移除收藏',
          icon: 'success'
        })
      }
    }
  })
}
</script>

<style scoped>
.collection-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.collection-header {
  margin-bottom: 20px;
}

.collection-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.collection-tabs {
  display: flex;
  background-color: var(--card-background);
  border-radius: 12px;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-item {
  flex: 1;
  padding: 16px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-item.active {
  background-color: var(--primary-color);
  color: white;
}

.collection-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.collection-item {
  display: flex;
  background-color: var(--card-background);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.collection-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.collection-image {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 16px;
}

.collection-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.collection-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
}

.collection-desc {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
  line-height: 1.4;
}

.collection-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-secondary-color);
}

.collection-type {
  background-color: rgba(30, 136, 229, 0.1);
  color: var(--primary-color);
  padding: 2px 8px;
  border-radius: 4px;
}

.collection-action {
  display: flex;
  align-items: center;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.icon {
  font-size: 20px;
  color: #F44336;
}
</style>