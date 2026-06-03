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
            <div class="author-avatar">
              <img 
                v-if="post.avatar && post.avatar.trim() !== ''" 
                :src="post.avatar" 
                alt="用户头像" 
                class="avatar-image"
                @error="handleAvatarError($event)"
              >
              <div v-else class="avatar-placeholder">
                {{ getAvatarInitial(post.username) }}
              </div>
            </div>
            <div class="author-info">
              <h3>{{ post.username || '未知用户' }}</h3>
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
    <div v-if="showCreateModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发布帖子</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label for="title">标题</label>
              <input type="text" id="title" v-model="newPost.title" required placeholder="请输入标题">
            </div>
            <div class="form-group">
              <label for="content">内容</label>
              <textarea id="content" v-model="newPost.content" required placeholder="请输入内容" rows="5"></textarea>
            </div>
            
            <!-- 图片上传区域 -->
            <div class="form-group">
              <label>图片（最多9张，支持 JPG、PNG、GIF、WebP，每张不超过2MB）</label>
              <div class="image-upload-area">
                <!-- 图片预览列表 -->
                <div v-if="selectedImages.length > 0" class="image-preview-list">
                  <div v-for="(img, index) in selectedImages" :key="index" class="image-preview-item">
                    <img :src="img.preview" :alt="`图片${index + 1}`">
                    <div v-if="img.uploading" class="upload-progress">
                      <div class="progress-bar" :style="{ width: img.progress + '%' }"></div>
                    </div>
                    <div v-if="img.error" class="upload-error">
                      <span>上传失败</span>
                    </div>
                    <button type="button" class="remove-image-btn" @click="removeImage(index)" :disabled="img.uploading">×</button>
                  </div>
                </div>
                
                <!-- 上传按钮 -->
                <div v-if="selectedImages.length < 9" class="upload-trigger" @click="triggerFileInput">
                  <input 
                    type="file" 
                    ref="fileInputRef" 
                    @change="handleFileSelect" 
                    accept="image/jpeg,image/png,image/gif,image/webp" 
                    multiple 
                    class="file-input-hidden"
                  >
                  <div class="upload-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                      <circle cx="8.5" cy="8.5" r="1.5"></circle>
                      <polyline points="21 15 16 10 5 21"></polyline>
                    </svg>
                  </div>
                  <span>点击上传图片</span>
                </div>
              </div>
              
              <!-- 错误提示 -->
              <div v-if="uploadError" class="upload-error-message">{{ uploadError }}</div>
            </div>
            
            <div class="form-group">
              <label for="tags">标签（用逗号分隔）</label>
              <input type="text" id="tags" v-model="newPost.tags" placeholder="例如：旅行,摄影,美食">
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeModal">取消</button>
              <button type="submit" class="submit-btn" :disabled="isSubmitting || selectedImages.some(img => img.uploading)">
                {{ isSubmitting ? '发布中...' : '发布' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
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
const isSubmitting = ref(false);
const newPost = ref({
  title: '',
  content: '',
  tags: ''
});

// 图片上传相关
const fileInputRef = ref(null);
const selectedImages = ref([]);
const uploadError = ref('');

// 文件大小限制：2MB
const MAX_FILE_SIZE = 2 * 1024 * 1024;
// 允许的文件类型
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

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

// 触发文件选择
const triggerFileInput = () => {
  fileInputRef.value?.click();
};

// 验证文件
const validateFile = (file) => {
  // 检查文件类型
  if (!ALLOWED_TYPES.includes(file.type)) {
    return '仅支持 JPG、PNG、GIF、WebP 格式的图片';
  }
  
  // 检查文件大小
  if (file.size > MAX_FILE_SIZE) {
    return '图片大小不能超过 2MB';
  }
  
  return null;
};

// 处理文件选择
const handleFileSelect = (event) => {
  const files = event.target.files;
  if (!files || files.length === 0) return;
  
  uploadError.value = '';
  
  // 检查是否超过数量限制
  const remainingSlots = 9 - selectedImages.value.length;
  if (files.length > remainingSlots) {
    uploadError.value = `最多只能选择 ${remainingSlots} 张图片`;
    event.target.value = ''; // 清空输入
    return;
  }
  
  // 处理每个文件
  Array.from(files).forEach(file => {
    const error = validateFile(file);
    if (error) {
      uploadError.value = error;
      return;
    }
    
    // 创建预览
    const reader = new FileReader();
    reader.onload = (e) => {
      const imageData = {
        file: file,
        preview: e.target.result,
        uploading: false,
        progress: 0,
        error: false,
        base64: e.target.result
      };
      selectedImages.value.push(imageData);
    };
    reader.readAsDataURL(file);
  });
  
  // 清空输入，允许重新选择相同文件
  event.target.value = '';
};

// 移除图片
const removeImage = (index) => {
  selectedImages.value.splice(index, 1);
};

// 关闭模态框
const closeModal = () => {
  showCreateModal.value = false;
  // 重置表单
  newPost.value = {
    title: '',
    content: '',
    tags: ''
  };
  selectedImages.value = [];
  uploadError.value = '';
};

// 提交表单
const handleSubmit = async () => {
  if (!newPost.value.title || !newPost.value.content) {
    ElMessage.warning('请填写标题和内容');
    return;
  }
  
  // 检查是否有上传失败的图片
  if (selectedImages.value.some(img => img.error)) {
    ElMessage.warning('请移除上传失败的图片后重试');
    return;
  }
  
  isSubmitting.value = true;
  
  try {
    // 将图片转换为 Base64 数组
    const images = selectedImages.value
      .filter(img => !img.error)
      .map(img => img.base64);
    
    const postData = {
      title: newPost.value.title,
      content: newPost.value.content,
      tags: newPost.value.tags,
      images: images.length > 0 ? JSON.stringify(images) : null
    };
    
    const response = await createPostApi(postData);
    
    if (response.code === 200) {
      ElMessage.success('发布成功');
      closeModal();
      // 重新加载帖子列表
      currentPage.value = 1;
      fetchPosts();
    } else {
      ElMessage.error(response.message || '发布失败');
    }
  } catch (err) {
    console.error('发布帖子失败:', err);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    isSubmitting.value = false;
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

// 获取头像首字母
const getAvatarInitial = (name) => {
  if (!name || name === '未知用户') return '?';
  return name.charAt(0).toUpperCase();
};

// 头像加载失败处理
const handleAvatarError = (event) => {
  const img = event.target;
  const parent = img.parentElement;
  // 隐藏图片，显示占位符
  img.style.display = 'none';
  if (parent && !parent.querySelector('.avatar-placeholder')) {
    const placeholder = document.createElement('div');
    placeholder.className = 'avatar-placeholder';
    placeholder.textContent = '?';
    parent.appendChild(placeholder);
  }
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
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: white;
  background-color: #3498db;
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

/* 图片上传区域 */
.image-upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-preview-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e0e0e0;
}

.image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: rgba(0, 0, 0, 0.3);
}

.upload-progress .progress-bar {
  height: 100%;
  background: #4CAF50;
  transition: width 0.3s ease;
}

.upload-error {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
}

.remove-image-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  line-height: 1;
  transition: background 0.2s;
}

.remove-image-btn:hover:not(:disabled) {
  background: rgba(255, 0, 0, 0.7);
}

.remove-image-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.upload-trigger {
  width: 80px;
  height: 80px;
  border: 2px dashed #d0d0d0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #999;
}

.upload-trigger:hover {
  border-color: #4CAF50;
  color: #4CAF50;
}

.file-input-hidden {
  display: none;
}

.upload-icon svg {
  width: 24px;
  height: 24px;
  margin-bottom: 4px;
}

.upload-trigger span {
  font-size: 11px;
}

.upload-error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 8px;
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