<template>
  <div class="community-content">
    <div class="content-header">
      <h2>社区动态</h2>
      <button class="create-post-btn" @click="showCreateModal = true">发布帖子</button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <button @click="fetchPosts" class="retry-btn">重试</button>
    </div>

    <!-- 帖子列表 -->
    <div v-else class="posts-list">
      <div v-for="post in posts" :key="post.id" class="post-card" @click="goToDetail(post.id)">
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
          <h4 class="post-title">{{ post.title }}</h4>
          <p class="post-text">{{ post.content }}</p>
        </div>
        <div class="post-footer">
          <div class="post-stats">
            <span class="stat-item">
              <svg t="1771774195840" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="5808" width="256" height="256">
                <path
                  d="M511.4368 512m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z"
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
              </svg> {{ post.likeCount || 0 }}</span>
            <span class="stat-item">
              <svg t="1771774227524" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="7673" width="256" height="256">
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
              </svg> {{ post.commentCount || 0 }}</span>
            <span class="stat-item">
              <svg t="1771774293176" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="14939" width="256" height="256">
                <path
                  d="M819.2 563.2c-113.1 0-204.8 91.7-204.8 204.8 0 113.1 91.7 204.8 204.8 204.8 24.2 0 47-4.9 68.6-12.6l48.8 48.8c20 20 52.4 20 72.4 0 20-20 20-52.4 0-72.4l-35.4-35.4c31-35.9 50.4-82.1 50.4-133.2 0-113.1-91.7-204.8-204.8-204.8z"
                  fill="#00A2FF" p-id="14940"></path>
                <path
                  d="M921.6 0H102.4C46.1 0 0 46.1 0 102.4v819.2C0 977.9 46.1 1024 102.4 1024h547.1C566.7 969 512 874.9 512 768c0-169.7 137.5-307.2 307.2-307.2 78.8 0 150.4 29.9 204.8 78.7V102.4C1024 46.1 977.9 0 921.6 0z m-512 716.8H204.8c-28.2 0-51.2-23-51.2-51.2 0-28.2 23-51.2 51.2-51.2h204.8c28.2 0 51.2 23 51.2 51.2 0 28.2-23 51.2-51.2 51.2zM512 512H204.8c-28.2 0-51.2-23-51.2-51.2 0-28.2 23-51.2 51.2-51.2H512c28.2 0 51.2 23 51.2 51.2 0 28.2-23 51.2-51.2 51.2z m307.2-204.8H204.8c-28.2 0-51.2-23-51.2-51.2s23-51.2 51.2-51.2h614.4c28.2 0 51.2 23 51.2 51.2s-23 51.2-51.2 51.2z"
                  fill="#00A2FF" p-id="14941"></path>
              </svg> {{ post.viewCount || 0 }}</span>
          </div>
          <div class="post-tags">
            <span v-for="tag in post.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="posts.length === 0" class="empty-state">
        <p>暂无帖子</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="!loading && !error && posts.length > 0" class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
        上一页
      </button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">
        下一页
      </button>
    </div>

    <!-- 创建帖子模态框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发布帖子</h3>
          <button class="close-btn" @click="showCreateModal = false">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="createPost">
            <div class="form-group">
              <label for="title">标题</label>
              <input type="text" id="title" v-model="newPost.title" required placeholder="请输入标题">
            </div>
            <div class="form-group">
              <label for="content">内容</label>
              <textarea id="content" v-model="newPost.content" required placeholder="请输入内容" rows="5"></textarea>
            </div>
            <div class="form-group">
              <label for="tags">标签（用逗号分隔）</label>
              <input type="text" id="tags" v-model="newPost.tags" placeholder="例如：旅行,摄影,美食">
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="showCreateModal = false">取消</button>
              <button type="submit" class="submit-btn" :disabled="creatingPost">
                {{ creatingPost ? '发布中...' : '发布' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  getPostList,
  createPost as createPostApi
} from '@/api/community';

const router = useRouter();

// 状态管理
const posts = ref([]);
const loading = ref(false);
const error = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const totalPages = ref(1);

// 创建帖子相关
const showCreateModal = ref(false);
const creatingPost = ref(false);
const newPost = ref({
  title: '',
  content: '',
  tags: ''
});

// 获取帖子列表
const fetchPosts = async () => {
  loading.value = true;
  error.value = null;

  try {
    const response = await getPostList({
      current: currentPage.value,
      size: pageSize.value
    });

    if (response.code === 200 && response.data) {
      posts.value = response.data.records || [];
      total.value = response.data.total || 0;
      totalPages.value = Math.ceil(total.value / pageSize.value);
    } else {
      error.value = '获取帖子失败';
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试';
    console.error('获取帖子失败:', err);
  } finally {
    loading.value = false;
  }
};

// 切换页码
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    fetchPosts();
  }
};

// 跳转到详情页
const goToDetail = (postId) => {
  router.push(`/community/detail/${postId}`);
};

// 发布帖子
const createPost = async () => {
  if (!newPost.value.title || !newPost.value.content) {
    return;
  }

  creatingPost.value = true;

  try {
    const response = await createPostApi({
      title: newPost.value.title,
      content: newPost.value.content,
      tags: newPost.value.tags
    });

    if (response.code === 200) {
      showCreateModal.value = false;
      // 重置表单
      newPost.value = {
        title: '',
        content: '',
        tags: ''
      };
      // 重新加载帖子列表
      currentPage.value = 1;
      fetchPosts();
    } else {
      error.value = response.message || '发布失败';
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试';
    console.error('发布帖子失败:', err);
  } finally {
    creatingPost.value = false;
  }
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
  fetchPosts();
});
</script>

<style scoped>
.community-content {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.content-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.create-post-btn {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.3s;
}

.create-post-btn:hover {
  background-color: #45a049;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
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
  padding: 40px 0;
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

/* 帖子列表 */
.posts-list {
  display: grid;
  gap: 20px;
}

.post-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

.post-header {
  margin-bottom: 15px;
}

.post-author {
  display: flex;
  align-items: center;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #3498db;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-right: 12px;
}

.author-info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.post-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.post-content {
  margin-bottom: 15px;
}

.post-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.post-text {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-stats {
  display: flex;
  gap: 15px;
}

.stat-item {
  font-size: 14px;
  color: #999;
}

.stat-item svg {
  width: 16px;
  height: 16px;
  vertical-align: middle;
  margin-right: 4px;
}

.post-tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 12px;
  font-size: 12px;
  background-color: #f0f0f0;
  border-radius: 12px;
  color: #666;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 10px;
}

.page-btn {
  padding: 8px 16px;
  background-color: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.page-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.page-info {
  font-size: 14px;
  color: #666;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.cancel-btn {
  padding: 10px 20px;
  background-color: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.submit-btn {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #45a049;
}

.submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .community-content {
    padding: 10px;
  }

  .content-header h2 {
    font-size: 20px;
  }

  .post-card {
    padding: 15px;
  }

  .post-title {
    font-size: 16px;
  }

  .modal-content {
    width: 95%;
    margin: 20px;
  }
}
</style>