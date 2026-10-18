<template>
  <div class="posts-tab">
    <div class="post-create">
      <img :src="defaultAvatar" class="composer-avatar" alt="" />
      <div class="composer-body">
        <textarea
          v-model="newPostContent"
          :placeholder="t('strategy.posts.placeholder')"
          rows="3"
        ></textarea>
        <div class="post-actions">
          <button class="btn-primary" type="button" :disabled="!newPostContent.trim()" @click="handleCreate">
            {{ t('strategy.posts.publish') }}
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M5 12h14M13 6l6 6-6 6" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <div v-if="posts.length === 0" class="tab-empty">
      <p>{{ t('strategy.posts.empty') }}</p>
    </div>

    <div v-else class="post-list">
      <article v-for="post in posts" :key="post.id" class="post-item">
        <header class="post-header">
          <img :src="post.authorAvatar || defaultAvatar" class="avatar-small" alt="" />
          <div class="post-author">
            <span class="name">{{ post.authorName }}</span>
            <span class="time">{{ formatTime(post.createdAt) }}</span>
          </div>
          <button
            v-if="post.userId === currentUserId"
            class="post-menu-btn"
            type="button"
            @click="$emit('delete', post.id)"
          >
            {{ t('strategy.posts.delete') }}
          </button>
        </header>

        <p class="post-content">{{ post.content }}</p>

        <footer class="post-footer">
          <button
            :class="['action-btn', 'like-btn', { liked: post.isLiked }]"
            type="button"
            :title="t('strategy.posts.like')"
            @click="$emit('like', post)"
          >
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M12 20s-7-4.6-7-9.6A4.4 4.4 0 0 1 12 7a4.4 4.4 0 0 1 7 3.4c0 5-7 9.6-7 9.6Z" />
            </svg>
            {{ post.likeCount || 0 }}
          </button>
          <button class="action-btn" type="button" :title="t('strategy.posts.comment')" @click="$emit('comment', post)">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M21 12a8 8 0 0 1-8 8H7l-4 3V12a8 8 0 0 1 8-8h2a8 8 0 0 1 8 8Z" />
            </svg>
            {{ post.commentCount || 0 }}
          </button>
        </footer>
      </article>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

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

const { t, locale } = useI18n()

const newPostContent = ref('')

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return t('strategy.notifications.justNow')
  if (diff < 3600000) return t('strategy.notifications.minutesAgo', { count: Math.floor(diff / 60000) })
  if (diff < 86400000) return t('strategy.notifications.hoursAgo', { count: Math.floor(diff / 3600000) })
  return date.toLocaleDateString(locale.value)
}

function handleCreate() {
  if (!newPostContent.value.trim()) return
  emit('create', newPostContent.value)
  newPostContent.value = ''
}
</script>

<style scoped>
.posts-tab {
  animation: fade-in var(--transition-normal);
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.post-create {
  display: flex;
  gap: 12px;
  padding: 16px;
  margin-bottom: 20px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.composer-avatar {
  width: 38px;
  height: 38px;
  flex-shrink: 0;
  border-radius: 50%;
  object-fit: cover;
}

.composer-body {
  flex: 1;
  min-width: 0;
}

.post-create textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--theme-bg-surface);
  font-family: inherit;
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text);
  resize: none;
  box-sizing: border-box;
  transition: border-color var(--transition-normal), box-shadow var(--transition-normal);
}

.post-create textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: var(--theme-ring);
}

.post-create textarea::placeholder {
  color: var(--color-text-secondary);
}

.post-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: var(--theme-primary-text);
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), opacity var(--transition-fast),
    transform var(--transition-fast);
}

.btn-primary svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.btn-primary:hover:not(:disabled) {
  background: var(--color-primary-hover);
}

.btn-primary:active:not(:disabled) {
  transform: scale(0.96);
}

.btn-primary:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.post-item {
  padding: 18px 20px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: border-color var(--transition-normal), box-shadow var(--transition-normal);
}

.post-item:hover {
  border-color: var(--theme-border-strong);
  box-shadow: var(--shadow-sm);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.avatar-small {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--theme-hairline);
}

.post-author {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.post-author .name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.post-author .time {
  font-size: 11px;
  color: var(--color-text-secondary);
}

.post-menu-btn {
  padding: 5px 10px;
  border: none;
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--color-text-secondary);
  font-family: inherit;
  font-size: 12px;
  cursor: pointer;
  transition: color var(--transition-fast), background var(--transition-fast);
}

.post-menu-btn:hover {
  background: var(--theme-danger-soft);
  color: var(--color-danger);
}

.post-content {
  font-size: 14px;
  line-height: 1.7;
  color: var(--color-text);
  margin-bottom: 14px;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-footer {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border: none;
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--color-text-secondary);
  font-family: inherit;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: background var(--transition-fast), color var(--transition-fast);
}

.action-btn svg {
  width: 15px;
  height: 15px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.action-btn:hover {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

.like-btn.liked {
  color: var(--color-danger);
}

.like-btn.liked svg {
  fill: var(--theme-danger-soft);
  stroke: var(--color-danger);
}

.tab-empty {
  padding: 44px;
  text-align: center;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  font-size: 13px;
}

@media (max-width: 767px) {
  .post-create {
    padding: 12px;
  }

  .composer-avatar {
    display: none;
  }

  .post-item {
    padding: 14px;
  }
}
</style>
