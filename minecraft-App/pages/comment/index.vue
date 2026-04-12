<template>
  <div class="comment-container">
    <div class="comment-header">
      <h1>我的评论</h1>
    </div>
    
    <div class="comment-list">
      <div class="comment-item">
        <div class="comment-user">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square" alt="用户头像" class="user-avatar">
          <div class="user-info">
            <h3>用户名</h3>
            <p class="comment-time">2026-03-28 14:30</p>
          </div>
        </div>
        <div class="comment-content">
          <div class="comment-rating">
            <span class="star">★★★★★</span>
          </div>
          <p class="comment-text">这个景点非常棒，风景优美，服务周到，下次还会再来！</p>
          <div class="comment-target">
            <span class="target-label">评论对象：</span>
            <span class="target-name">长城</span>
          </div>
        </div>
        <div class="comment-actions">
          <button class="action-btn">编辑</button>
          <button class="action-btn">删除</button>
        </div>
      </div>
      
      <div class="comment-item">
        <div class="comment-user">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square" alt="用户头像" class="user-avatar">
          <div class="user-info">
            <h3>用户名</h3>
            <p class="comment-time">2026-03-27 10:15</p>
          </div>
        </div>
        <div class="comment-content">
          <div class="comment-rating">
            <span class="star">★★★★☆</span>
          </div>
          <p class="comment-text">酒店环境不错，房间干净整洁，就是价格有点贵。</p>
          <div class="comment-target">
            <span class="target-label">评论对象：</span>
            <span class="target-name">豪华酒店</span>
          </div>
        </div>
        <div class="comment-actions">
          <button class="action-btn">编辑</button>
          <button class="action-btn">删除</button>
        </div>
      </div>
      
      <div class="comment-item">
        <div class="comment-user">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square" alt="用户头像" class="user-avatar">
          <div class="user-info">
            <h3>用户名</h3>
            <p class="comment-time">2026-03-26 18:45</p>
          </div>
        </div>
        <div class="comment-content">
          <div class="comment-rating">
            <span class="star">★★★★★</span>
          </div>
          <p class="comment-text">北京烤鸭真的很好吃，皮脆肉嫩，非常推荐！</p>
          <div class="comment-target">
            <span class="target-label">评论对象：</span>
            <span class="target-name">北京烤鸭</span>
          </div>
        </div>
        <div class="comment-actions">
          <button class="action-btn">编辑</button>
          <button class="action-btn">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 删除评论
const deleteComment = () => {
  uni.showModal({
    title: '删除评论',
    content: '确定要删除这条评论吗？',
    success: function(res) {
      if (res.confirm) {
        // 删除评论逻辑
        uni.showToast({
          title: '评论已删除',
          icon: 'success'
        })
      }
    }
  })
}
</script>

<style scoped>
.comment-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.comment-header {
  margin-bottom: 20px;
}

.comment-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  background-color: var(--card-background);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.comment-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.comment-user {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 12px;
}

.user-info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
}

.comment-time {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary-color);
}

.comment-content {
  margin-bottom: 12px;
}

.comment-rating {
  margin-bottom: 8px;
}

.star {
  font-size: 16px;
  color: #FF9800;
}

.comment-text {
  margin: 0 0 8px 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-color);
}

.comment-target {
  font-size: 14px;
  color: var(--text-secondary-color);
}

.target-label {
  font-weight: 500;
}

.target-name {
  color: var(--primary-color);
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.action-btn {
  padding: 6px 12px;
  background: none;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 14px;
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
</style>