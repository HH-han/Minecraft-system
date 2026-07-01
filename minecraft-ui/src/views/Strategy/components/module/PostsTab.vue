<template>
  <div class="posts-tab">
    <div class="post-create">
      <textarea v-model="newPostContent" placeholder="分享你的旅行动态..."></textarea>
      <div class="post-actions">
        <button class="btn-primary btn-small" @click="handleCreate">发布</button>
      </div>
    </div>
    <div class="post-list">
      <div v-for="post in posts" :key="post.id" class="post-item">
        <div class="post-header">
          <img :src="post.authorAvatar || defaultAvatar" class="avatar-small" />
          <div class="post-author">
            <span class="name">{{ post.authorName }}</span>
            <span class="time">{{ formatTime(post.createdAt) }}</span>
          </div>
          <div v-if="post.userId === currentUserId" class="post-menu">
            <button @click="$emit('delete', post.id)">删除</button>
          </div>
        </div>
        <p class="post-content">{{ post.content }}</p>
        <div class="post-footer">
          <button :class="['like-btn', { liked: post.isLiked }]" @click="$emit('like', post)">
            ♥ {{ post.likeCount || 0 }}
          </button>
          <button class="comment-btn" @click="$emit('comment', post)">
            💬 {{ post.commentCount || 0 }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  posts: {
    type: Array,
    default: () => []
  },
  currentUserId: {
    type: Number,
    default: null
  },
  defaultAvatar: {
    type: String,
    default: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100'
  }
})

const emit = defineEmits(['create', 'delete', 'like', 'comment'])

const newPostContent = ref('')

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return date.toLocaleDateString('zh-CN')
}

function handleCreate() {
  if (!newPostContent.value) return
  emit('create', newPostContent.value)
  newPostContent.value = ''
}
</script>

<style scoped>
.posts-tab {
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.post-create {
  margin-bottom: var(--spacing-24);
}

.post-create textarea {
  width: 100%;
  padding: var(--spacing-16);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 15px;
  font-family: inherit;
  resize: none;
  min-height: 80px;
  box-sizing: border-box;
  transition: all var(--transition-normal);
}

.post-create textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.1);
}

.post-create textarea::placeholder {
  color: var(--color-text-secondary);
}

.post-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-12);
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-20);
}

.post-item {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  padding: var(--spacing-20);
  transition: all var(--transition-normal);
}

.post-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.post-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-12);
}

.avatar-small {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.04);
}

.post-author {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.post-author .name {
  font-weight: 500;
  font-size: 15px;
}

.post-author .time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.post-menu button {
  background: transparent;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: var(--spacing-4) var(--spacing-8);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.post-menu button:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--color-text);
}

.post-content {
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: var(--spacing-12);
}

.post-footer {
  display: flex;
  gap: var(--spacing-16);
}

.post-footer button {
  background: transparent;
  border: none;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-4) var(--spacing-12);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
  color: var(--color-text-secondary);
}

.post-footer button:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--color-text);
}

.like-btn.liked {
  color: var(--color-danger);
}
</style>