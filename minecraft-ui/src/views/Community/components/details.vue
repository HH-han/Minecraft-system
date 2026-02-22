<template>
  <div class="post-detail">
    <!-- 返回按钮 -->
    <div class="detail-header">
      <button class="back-btn" @click="goBack">← 返回列表</button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <button @click="fetchPostDetail" class="retry-btn">重试</button>
    </div>

    <!-- 帖子详情 -->
    <div v-else-if="post" class="detail-content">
      <div class="post-header">
        <div class="post-author">
          <div class="author-avatar">{{ post.authorName?.charAt(0) || '?' }}</div>
          <div class="author-info">
            <h3>{{ post.authorName || '未知用户' }}</h3>
            <p class="post-time">{{ formatDate(post.createTime) }}</p>
          </div>
        </div>
      </div>

      <div class="post-content">
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-text">{{ post.content }}</div>
      </div>

      <div class="post-footer">
        <div class="post-stats">
          <span class="stat-item">
            <svg t="1771774195840" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="5808" width="256" height="256">
              <path d="M511.4368 512m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z"
                fill="#FF6161" p-id="5809"></path>
              <path
                d="M286.464 417.8432c-85.0432 0-160.4096 41.3696-207.104 105.0624 4.5568 182.7328 122.368 337.3056 285.952 396.032 103.2192-33.28 177.92-130.048 177.92-244.3776 0-141.7728-114.944-256.7168-256.768-256.7168z"
                fill="#FF7D7D" p-id="5810"></path>
              <path
                d="M342.6304 721.6128h6.7584V427.9808h-6.7584c-36.096 0-65.3824 29.2864-65.3824 65.3824v162.9184c0.0512 36.0448 29.2864 65.3312 65.3824 65.3312z"
                fill="#FFCAC7" p-id="5811"></path>
              <path
                d="M710.656 390.9632h-136.8064c10.6496-34.9696 24.9344-106.8032-25.088-137.1648-23.5008-14.1824-78.8992-24.1664-77.568 37.9904 1.28 62.1568-2.2528 130.4064-81.152 136.192v293.632h280.2688c31.744 0 59.136-22.3232 65.4848-53.4528l40.2944-196.9152c8.5504-41.472-23.0912-80.2816-65.4336-80.2816z"
                fill="#FFFFFF" p-id="5812"></path>
            </svg>
            {{ post.likeCount || 0 }}</span>
          <span class="stat-item">
            <svg t="1771774227524" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="7673" width="256" height="256">
              <path
                d="M170.666667 136.533333m136.533333 0l512 0q136.533333 0 136.533333 136.533334l0 307.2q0 136.533333-136.533333 136.533333l-512 0q-136.533333 0-136.533333-136.533333l0-307.2q0-136.533333 136.533333-136.533334Z"
                fill="#FC7300" p-id="7674"></path>
              <path
                d="M170.666667 170.666667m136.533333 0l512 0q136.533333 0 136.533333 136.533333l0 307.2q0 136.533333-136.533333 136.533333l-512 0q-136.533333 0-136.533333-136.533333l0-307.2q0-136.533333 136.533333-136.533333Z"
                fill="#FA8511" p-id="7675"></path>
              <path
                d="M170.666667 273.066667h512a136.533333 136.533333 0 0 1 136.533333 136.533333v307.2a136.533333 136.533333 0 0 1-136.533333 136.533333h-188.757334l-67.242666 67.4816L359.1168 853.333333H170.666667a136.533333 136.533333 0 0 1-136.533334-136.533333v-307.2a136.533333 136.533333 0 0 1 136.533334-136.533333z"
                fill="#FBC476" p-id="7676"></path>
              <path
                d="M170.666667 307.2h512a136.533333 136.533333 0 0 1 136.533333 136.533333v307.2a136.533333 136.533333 0 0 1-136.533333 136.533334h-188.757334l-67.242666 67.4816L359.1168 887.466667H170.666667a136.533333 136.533333 0 0 1-136.533334-136.533334v-307.2a136.533333 136.533333 0 0 1 136.533334-136.533333z"
                fill="#FAB85F" p-id="7677"></path>
              <path
                d="M204.8 477.866667m34.133333 0l375.466667 0q34.133333 0 34.133333 34.133333l0 0q0 34.133333-34.133333 34.133333l-375.466667 0q-34.133333 0-34.133333-34.133333l0 0q0-34.133333 34.133333-34.133333Z"
                fill="#FFFFFF" p-id="7678"></path>
              <path
                d="M204.8 648.533333m34.133333 0l273.066667 0q34.133333 0 34.133333 34.133334l0 0q0 34.133333-34.133333 34.133333l-273.066667 0q-34.133333 0-34.133333-34.133333l0 0q0-34.133333 34.133333-34.133334Z"
                fill="#FFFFFF" p-id="7679"></path>
            </svg>
            {{ post.commentCount || 0 }}</span>
          <span class="stat-item">👁
            <svg t="1771774293176" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="14939" width="256" height="256">
              <path
                d="M819.2 563.2c-113.1 0-204.8 91.7-204.8 204.8 0 113.1 91.7 204.8 204.8 204.8 24.2 0 47-4.9 68.6-12.6l48.8 48.8c20 20 52.4 20 72.4 0 20-20 20-52.4 0-72.4l-35.4-35.4c31-35.9 50.4-82.1 50.4-133.2 0-113.1-91.7-204.8-204.8-204.8z"
                fill="#00A2FF" p-id="14940"></path>
              <path
                d="M921.6 0H102.4C46.1 0 0 46.1 0 102.4v819.2C0 977.9 46.1 1024 102.4 1024h547.1C566.7 969 512 874.9 512 768c0-169.7 137.5-307.2 307.2-307.2 78.8 0 150.4 29.9 204.8 78.7V102.4C1024 46.1 977.9 0 921.6 0z m-512 716.8H204.8c-28.2 0-51.2-23-51.2-51.2 0-28.2 23-51.2 51.2-51.2h204.8c28.2 0 51.2 23 51.2 51.2 0 28.2-23 51.2-51.2 51.2zM512 512H204.8c-28.2 0-51.2-23-51.2-51.2 0-28.2 23-51.2 51.2-51.2H512c28.2 0 51.2 23 51.2 51.2 0 28.2-23 51.2-51.2 51.2z m307.2-204.8H204.8c-28.2 0-51.2-23-51.2-51.2s23-51.2 51.2-51.2h614.4c28.2 0 51.2 23 51.2 51.2s-23 51.2-51.2 51.2z"
                fill="#00A2FF" p-id="14941"></path>
            </svg>
            {{ post.viewCount || 0 }}</span>
        </div>
        <div class="post-tags">
          <span v-for="tag in post.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comments-section">
        <h3>评论 ({{ post.commentCount || 0 }})</h3>
        <div class="comment-form">
          <textarea v-model="commentContent" placeholder="写下你的评论..." rows="3"></textarea>
          <button class="submit-comment-btn" @click="submitComment" :disabled="!commentContent.trim()">
            发表评论
          </button>
        </div>

        <div class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-author">
              <div class="author-avatar">{{ comment.authorName?.charAt(0) || '?' }}</div>
              <div class="author-info">
                <h4>{{ comment.authorName || '未知用户' }}</h4>
                <p class="comment-time">{{ formatDate(comment.createTime) }}</p>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>

          <div v-if="comments.length === 0" class="no-comments">
            <p>暂无评论，快来抢沙发吧！</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 帖子不存在 -->
    <div v-else class="not-found">
      <p>帖子不存在或已被删除</p>
      <button class="back-btn" @click="goBack">返回列表</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  getPostDetail
} from '@/api/community';

const route = useRoute();
const router = useRouter();

// 状态管理
const post = ref(null);
const comments = ref([]);
const loading = ref(false);
const error = ref(null);
const commentContent = ref('');

// 获取帖子ID
const postId = route.params.id;

// 获取帖子详情
const fetchPostDetail = async () => {
  if (!postId) return;

  loading.value = true;
  error.value = null;

  try {
    const response = await getPostDetail(postId);

    if (response.code === 200 && response.data) {
      post.value = response.data;
    } else {
      error.value = '获取帖子详情失败';
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试';
    console.error('获取帖子详情失败:', err);
  } finally {
    loading.value = false;
  }
};

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) return;

  // 这里可以调用评论接口，暂时模拟
  console.log('提交评论:', commentContent.value);
  commentContent.value = '';
  // 模拟评论成功
  alert('评论功能暂未实现');
};

// 返回列表
const goBack = () => {
  router.push('/community');
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 初始化加载
onMounted(() => {
  fetchPostDetail();
});
</script>

<style scoped>
.post-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.detail-header {
  margin-bottom: 30px;
}

.back-btn {
  padding: 10px 20px;
  background-color: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background-color: #e0e0e0;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 60px 0;
  color: #e74c3c;
}

.retry-btn {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 帖子详情 */
.detail-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.post-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.post-author {
  display: flex;
  align-items: center;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #3498db;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  margin-right: 15px;
}

.author-info h3 {
  margin: 0 0 6px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.post-time {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.post-content {
  margin-bottom: 30px;
}

.post-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
  line-height: 1.3;
}

.post-text {
  margin: 0;
  font-size: 16px;
  color: #333;
  line-height: 1.6;
}

.post-footer {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.post-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.stat-item {
  font-size: 16px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 5px;
}

.stat-item svg {
  width: 16px;
  height: 16px;
  vertical-align: middle;
}

.post-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag {
  padding: 6px 16px;
  font-size: 14px;
  background-color: #f0f0f0;
  border-radius: 16px;
  color: #666;
}

/* 评论区 */
.comments-section {
  margin-top: 40px;
}

.comments-section h3 {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.comment-form {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.comment-form textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  margin-bottom: 10px;
  box-sizing: border-box;
}

.submit-comment-btn {
  padding: 10px 24px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.submit-comment-btn:hover:not(:disabled) {
  background-color: #45a049;
}

.submit-comment-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
}

.comment-author {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.comment-author .author-avatar {
  width: 36px;
  height: 36px;
  font-size: 14px;
  margin-right: 10px;
}

.comment-author h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.comment-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.no-comments {
  text-align: center;
  padding: 40px 0;
  color: #999;
  background-color: #f9f9f9;
  border-radius: 8px;
}

/* 未找到 */
.not-found {
  text-align: center;
  padding: 100px 0;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-detail {
    padding: 10px;
  }

  .detail-content {
    padding: 20px;
  }

  .post-title {
    font-size: 20px;
  }

  .post-text {
    font-size: 14px;
  }

  .author-avatar {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }

  .loading-state,
  .error-state,
  .not-found {
    padding: 60px 0;
  }
}
</style>