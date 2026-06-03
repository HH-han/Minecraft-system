<template>
  <div class="community-management">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h1>我的帖子</h1>
        <p class="subtitle">管理你在旅行社区分享的内容</p>
      </div>
      <button class="create-btn" @click="showCreateModal = true">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        发布新帖
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="posts.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
        </svg>
      </div>
      <h3>还没有帖子</h3>
      <p>发布你的第一条旅行分享吧</p>
      <button class="create-btn primary" @click="showCreateModal = true">
        发布帖子
      </button>
    </div>

    <!-- 帖子列表 -->
    <div v-else class="posts-grid">
      <div v-for="post in posts" :key="post.id" class="post-card">
        <div class="post-images" v-if="getPostImages(post.images).length > 0">
          <div 
            class="image-carousel" 
            @touchstart="handleTouchStart($event, post.id)" 
            @touchmove="handleTouchMove($event)" 
            @touchend="handleTouchEnd(post.id)"
          >
            <div 
              class="carousel-track" 
              :style="{ transform: `translateX(-${(carouselIndex[post.id] || 0) * 100}%)` }"
            >
              <div 
                v-for="(img, index) in getPostImages(post.images)" 
                :key="index"
                class="carousel-item"
              >
                <img 
                  :src="img" 
                  :alt="`图片${index + 1}`"
                  class="post-image"
                  @error="handleImageError($event, post.id, index)"
                >
              </div>
            </div>
            
            <!-- 左右切换按钮 -->
            <button 
              v-if="getPostImages(post.images).length > 1" 
              class="carousel-btn prev"
              @click.stop="prevImage(post.id)"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M15 19l-7-7 7-7"></path>
              </svg>
            </button>
            <button 
              v-if="getPostImages(post.images).length > 1" 
              class="carousel-btn next"
              @click.stop="nextImage(post.id)"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7"></path>
              </svg>
            </button>
          </div>
          
          <!-- 图片指示器 -->
          <div class="carousel-indicators" v-if="getPostImages(post.images).length > 1">
            <span 
              v-for="(img, index) in getPostImages(post.images)" 
              :key="index"
              class="indicator"
              :class="{ active: (carouselIndex[post.id] || 0) === index }"
              @click.stop="goToImage(post.id, index)"
            ></span>
          </div>
          
          <!-- 图片数量 -->
          <div class="image-count">
            {{ (carouselIndex[post.id] || 0) + 1 }} / {{ getPostImages(post.images).length }}
          </div>
        </div>
        <div class="post-images placeholder" v-else>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <circle cx="8.5" cy="8.5" r="1.5"></circle>
            <polyline points="21 15 16 10 5 21"></polyline>
          </svg>
        </div>
        
        <div class="post-content">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-text">{{ truncateText(post.content, 80) }}</p>
          
          <div class="post-meta">
            <div class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
              </svg>
              <span>{{ post.likeCount || 0 }}</span>
            </div>
            <div class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg>
              <span>{{ post.commentCount || 0 }}</span>
            </div>
            <div class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              <span>{{ formatDate(post.createTime) }}</span>
            </div>
          </div>
        </div>
        
        <div class="post-actions">
          <button class="action-btn" @click="editPost(post)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            编辑
          </button>
          <button class="action-btn danger" @click="confirmDelete(post)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <polyline points="3 6 5 6 21 6"></polyline>
              <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
            </svg>
            删除
          </button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination">
      <button 
        class="page-btn" 
        :disabled="currentPage === 1" 
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <div class="page-numbers">
        <button 
          v-for="page in visiblePages" 
          :key="page"
          :class="['page-number', { active: page === currentPage }]"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
      </div>
      <button 
        class="page-btn" 
        :disabled="currentPage >= totalPages" 
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 创建/编辑帖子模态框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditing ? '编辑帖子' : '发布新帖' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>标题</label>
            <input 
              type="text" 
              v-model="formData.title" 
              placeholder="分享你的旅行故事"
              class="form-input"
            >
          </div>
          
          <div class="form-group">
            <label>内容</label>
            <textarea 
              v-model="formData.content" 
              placeholder="详细描述你的旅行体验..."
              class="form-textarea"
              rows="6"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label>图片（可选，最多9张）</label>
            <div class="image-upload-area">
              <div v-if="selectedImages.length > 0" class="image-preview-list">
                <div v-for="(img, index) in selectedImages" :key="index" class="image-preview-item">
                  <img :src="img.preview" :alt="`图片${index + 1}`">
                  <button class="remove-btn" @click="removeImage(index)">×</button>
                </div>
              </div>
              
              <div v-if="selectedImages.length < 9" class="upload-trigger" @click="triggerFileInput">
                <input 
                  type="file" 
                  ref="fileInputRef" 
                  @change="handleFileSelect" 
                  accept="image/jpeg,image/png,image/gif,image/webp" 
                  multiple 
                  class="file-input"
                >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <circle cx="8.5" cy="8.5" r="1.5"></circle>
                  <polyline points="21 15 16 10 5 21"></polyline>
                </svg>
                <span>添加图片</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn secondary" @click="closeModal">取消</button>
          <button 
            class="btn primary" 
            @click="submitPost" 
            :disabled="isSubmitting || !formData.title || !formData.content"
          >
            {{ isSubmitting ? '发布中...' : (isEditing ? '保存' : '发布') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
      <div class="confirm-dialog">
        <div class="confirm-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
        </div>
        <h3>确认删除</h3>
        <p>确定要删除这篇帖子吗？此操作无法撤销。</p>
        <div class="confirm-actions">
          <button class="btn secondary" @click="showDeleteConfirm = false">取消</button>
          <button class="btn danger" @click="deletePost">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getMyPosts, createPost, updatePost, deletePost as deletePostApi } from '@/api/community';

const posts = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);

// 模态框状态
const showCreateModal = ref(false);
const isEditing = ref(false);
const editingPostId = ref(null);
const isSubmitting = ref(false);
const formData = ref({
  title: '',
  content: ''
});

// 图片上传相关
const fileInputRef = ref(null);
const selectedImages = ref([]);

// 删除确认
const showDeleteConfirm = ref(false);
const deletingPostId = ref(null);

// 轮播相关状态
const carouselIndex = ref({});
const touchStartX = ref(0);
const touchEndX = ref(0);

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1);

const visiblePages = computed(() => {
  const pages = [];
  const total = totalPages.value;
  const current = currentPage.value;
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i);
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 5; i++) pages.push(i);
    } else if (current >= total - 2) {
      for (let i = total - 4; i <= total; i++) pages.push(i);
    } else {
      for (let i = current - 2; i <= current + 2; i++) pages.push(i);
    }
  }
  
  return pages;
});

// 获取帖子列表
const fetchPosts = async () => {
  loading.value = true;
  try {
    const response = await getMyPosts({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    });
    
    if (response.code === 200) {
      posts.value = response.data?.records || [];
      total.value = response.data?.total || 0;
    } else {
      ElMessage.error('获取帖子失败');
    }
  } catch (error) {
    console.error('获取帖子失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 获取帖子图片
const getPostImages = (imagesStr) => {
  if (!imagesStr) return [];
  return imagesStr.split(',').filter(img => img.trim());
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

// 分页
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    fetchPosts();
  }
};

// 编辑帖子
const editPost = (post) => {
  isEditing.value = true;
  editingPostId.value = post.id;
  formData.value = {
    title: post.title,
    content: post.content
  };
  
  // 如果有图片，解析并显示
  if (post.images) {
    const images = getPostImages(post.images);
    selectedImages.value = images.map(img => ({
      preview: img,
      base64: img
    }));
  }
  
  showCreateModal.value = true;
};

// 关闭模态框
const closeModal = () => {
  showCreateModal.value = false;
  isEditing.value = false;
  editingPostId.value = null;
  formData.value = { title: '', content: '' };
  selectedImages.value = [];
};

// 触发文件选择
const triggerFileInput = () => {
  fileInputRef.value?.click();
};

// 处理文件选择
const handleFileSelect = (event) => {
  const files = event.target.files;
  if (!files || files.length === 0) return;
  
  const remainingSlots = 9 - selectedImages.value.length;
  if (files.length > remainingSlots) {
    ElMessage.warning(`最多只能添加 ${remainingSlots} 张图片`);
    event.target.value = '';
    return;
  }
  
  Array.from(files).forEach(file => {
    if (file.size > 2 * 1024 * 1024) {
      ElMessage.warning('图片大小不能超过 2MB');
      return;
    }
    
    const reader = new FileReader();
    reader.onload = (e) => {
      selectedImages.value.push({
        file: file,
        preview: e.target.result,
        base64: e.target.result
      });
    };
    reader.readAsDataURL(file);
  });
  
  event.target.value = '';
};

// 移除图片
const removeImage = (index) => {
  selectedImages.value.splice(index, 1);
};

// 提交帖子
const submitPost = async () => {
  if (!formData.value.title || !formData.value.content) {
    ElMessage.warning('请填写标题和内容');
    return;
  }
  
  isSubmitting.value = true;
  
  try {
    const images = selectedImages.value
      .filter(img => img.base64.startsWith('data:image'))
      .map(img => img.base64);
    
    const postData = {
      title: formData.value.title,
      content: formData.value.content,
      images: images.length > 0 ? JSON.stringify(images) : null
    };
    
    let response;
    if (isEditing.value) {
      postData.id = editingPostId.value;
      response = await updatePost(postData);
    } else {
      response = await createPost(postData);
    }
    
    if (response.code === 200) {
      ElMessage.success(isEditing.value ? '保存成功' : '发布成功');
      closeModal();
      fetchPosts();
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('提交帖子失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    isSubmitting.value = false;
  }
};

// 确认删除
const confirmDelete = (post) => {
  deletingPostId.value = post.id;
  showDeleteConfirm.value = true;
};

// 删除帖子
const deletePost = async () => {
  try {
    const response = await deletePostApi(deletingPostId.value);
    
    if (response.code === 200) {
      ElMessage.success('删除成功');
      showDeleteConfirm.value = false;
      deletingPostId.value = null;
      fetchPosts();
    } else {
      ElMessage.error(response.message || '删除失败');
    }
  } catch (error) {
    console.error('删除帖子失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

// 图片加载失败
const handleImageError = (event, postId, index) => {
  event.target.style.display = 'none';
};

// 轮播控制函数
const nextImage = (postId) => {
  const images = getPostImages(posts.value.find(p => p.id === postId)?.images);
  const current = carouselIndex.value[postId] || 0;
  carouselIndex.value[postId] = current >= images.length - 1 ? 0 : current + 1;
};

const prevImage = (postId) => {
  const images = getPostImages(posts.value.find(p => p.id === postId)?.images);
  const current = carouselIndex.value[postId] || 0;
  carouselIndex.value[postId] = current <= 0 ? images.length - 1 : current - 1;
};

const goToImage = (postId, index) => {
  carouselIndex.value[postId] = index;
};

// 触摸滑动事件处理
const handleTouchStart = (event, postId) => {
  touchStartX.value = event.touches[0].clientX;
};

const handleTouchMove = (event) => {
  touchEndX.value = event.touches[0].clientX;
};

const handleTouchEnd = (postId) => {
  const diff = touchStartX.value - touchEndX.value;
  if (Math.abs(diff) > 50) {
    if (diff > 0) {
      nextImage(postId);
    } else {
      prevImage(postId);
    }
  }
};

onMounted(() => {
  fetchPosts();
});
</script>

<style scoped>
/* 主容器 */
.community-management {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 22px;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40px;
}

.header-content h1 {
  font-size: 48px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}

.subtitle {
  font-size: 16px;
  color: #6e6e73;
  margin: 0;
}

/* 创建按钮 */
.create-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: #0071e3;
  color: white;
  border: none;
  border-radius: 40px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.2s;
}

.create-btn:hover {
  background: #0077ed;
  transform: scale(1.02);
}

.create-btn svg {
  width: 18px;
  height: 18px;
}

.create-btn.primary {
  background: #0071e3;
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
  border: 3px solid #f5f5f7;
  border-top-color: #0071e3;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  margin-top: 16px;
  color: #6e6e73;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: #f5f5f7;
  border-radius: 28px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  color: #a1a1a6;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-state h3 {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 16px;
  color: #6e6e73;
  margin: 0 0 24px 0;
}

/* 帖子网格 */
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

/* 帖子卡片 */
.post-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.post-card:hover {
  transform: scale(1.01);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 帖子图片 */
.post-images {
  position: relative;
  width: 100%;
  height: 200px;
  background: #f5f5f7;
  overflow: hidden;
}

.image-carousel {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.carousel-track {
  display: flex;
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.carousel-item {
  flex: 0 0 100%;
  width: 100%;
  height: 100%;
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 轮播按钮 */
.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.8);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s, opacity 0.2s;
  opacity: 0;
  z-index: 10;
}

.post-images:hover .carousel-btn {
  opacity: 1;
}

.carousel-btn:hover {
  background: rgba(255, 255, 255, 1);
}

.carousel-btn.prev {
  left: 8px;
}

.carousel-btn.next {
  right: 8px;
}

.carousel-btn svg {
  width: 16px;
  height: 16px;
  color: #1d1d1f;
}

/* 轮播指示器 */
.carousel-indicators {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  z-index: 10;
}

.indicator {
  width: 6px;
  height: 6px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
}

.indicator.active {
  width: 18px;
  border-radius: 3px;
  background: white;
}

/* 图片计数 */
.post-images .image-count {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
}

.post-images.placeholder {
  color: #a1a1a6;
}

.post-images.placeholder svg {
  width: 48px;
  height: 48px;
}

/* 帖子内容 */
.post-content {
  padding: 24px;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.post-text {
  font-size: 14px;
  color: #6e6e73;
  line-height: 1.5;
  margin: 0 0 16px 0;
}

.post-meta {
  display: flex;
  gap: 16px;
  padding-top: 16px;
  border-top: 1px solid #f5f5f7;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6e6e73;
}

.meta-item svg {
  width: 16px;
  height: 16px;
}

/* 帖子操作 */
.post-actions {
  display: flex;
  gap: 8px;
  padding: 0 24px 24px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  background: #f5f5f7;
  color: #1d1d1f;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.action-btn:hover {
  background: #e8e8ed;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.danger {
  background: #ff3b30;
  color: white;
}

.action-btn.danger:hover {
  background: #d62c24;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 40px;
}

.page-btn {
  padding: 8px 16px;
  background: #f5f5f7;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: #1d1d1f;
  cursor: pointer;
  transition: background-color 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #e8e8ed;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-number {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: #1d1d1f;
  cursor: pointer;
  transition: background-color 0.2s;
}

.page-number:hover {
  background: #f5f5f7;
}

.page-number.active {
  background: #0071e3;
  color: white;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 22px;
}

.modal-content {
  background: white;
  border-radius: 24px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid #f5f5f7;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f7;
  border: none;
  border-radius: 50%;
  font-size: 20px;
  color: #6e6e73;
  cursor: pointer;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background: #e8e8ed;
}

.modal-body {
  padding: 28px;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 14px 16px;
  background: #f5f5f7;
  border: 2px solid transparent;
  border-radius: 12px;
  font-size: 16px;
  color: #1d1d1f;
  transition: border-color 0.2s, background-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #0071e3;
  background: white;
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: #a1a1a6;
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
  line-height: 1.5;
}

/* 图片上传 */
.image-upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-preview-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
}

.image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-preview-item .remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 14px;
  cursor: pointer;
}

.upload-trigger {
  width: 80px;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: #f5f5f7;
  border: 2px dashed #d2d2d6;
  border-radius: 12px;
  color: #6e6e73;
  font-size: 11px;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}

.upload-trigger:hover {
  border-color: #0071e3;
  color: #0071e3;
}

.upload-trigger svg {
  width: 24px;
  height: 24px;
}

.file-input {
  display: none;
}

/* 模态框底部 */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 28px;
  border-top: 1px solid #f5f5f7;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 40px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn.primary {
  background: #0071e3;
  color: white;
}

.btn.primary:hover:not(:disabled) {
  background: #0077ed;
}

.btn.primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn.secondary {
  background: #f5f5f7;
  color: #1d1d1f;
}

.btn.secondary:hover {
  background: #e8e8ed;
}

.btn.danger {
  background: #ff3b30;
  color: white;
}

.btn.danger:hover {
  background: #d62c24;
}

/* 确认对话框 */
.confirm-dialog {
  background: white;
  border-radius: 24px;
  padding: 32px;
  text-align: center;
  max-width: 400px;
}

.confirm-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
  color: #ff3b30;
}

.confirm-icon svg {
  width: 100%;
  height: 100%;
}

.confirm-dialog h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px 0;
}

.confirm-dialog p {
  font-size: 14px;
  color: #6e6e73;
  margin: 0 0 24px 0;
}

.confirm-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .community-management {
    padding: 24px 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .header-content h1 {
    font-size: 32px;
  }
  
  .posts-grid {
    grid-template-columns: 1fr;
  }
  
  .post-actions {
    flex-direction: column;
  }
  
  .pagination {
    flex-wrap: wrap;
  }
  
  .page-numbers {
    order: -1;
    width: 100%;
    justify-content: center;
    margin-bottom: 12px;
  }
}
</style>
