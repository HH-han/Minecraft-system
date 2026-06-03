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
      <div class="main-content">
        <!-- 帖子图片轮播 -->
        <div class="post-image-container" v-if="postImages.length > 0">
          <div class="carousel-wrapper">
            <div class="carousel-container" @touchstart="handleTouchStart" @touchmove="handleTouchMove" @touchend="handleTouchEnd">
              <div class="carousel-track" :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }">
                <div v-for="(img, index) in postImages" :key="index" class="carousel-item">
                  <img :src="img" :alt="`图片${index + 1}`" class="post-image" @error="handleImageError($event)">
                </div>
              </div>
            </div>
            
            <!-- 左右切换按钮 -->
            <button class="carousel-prev" @click="prevImage" v-show="postImages.length > 1">
              <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
                <path d="M15 19l-7-7 7-7"></path>
              </svg>
            </button>
            <button class="carousel-next" @click="nextImage" v-show="postImages.length > 1">
              <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
                <path d="M9 5l7 7-7 7"></path>
              </svg>
            </button>
            
            <!-- 指示器 -->
            <div class="carousel-indicators" v-show="postImages.length > 1">
              <span 
                v-for="(_, index) in postImages" 
                :key="index" 
                class="indicator"
                :class="{ active: currentImageIndex === index }"
                @click="goToImage(index)"
              ></span>
            </div>
            
            <!-- 图片数量提示 -->
            <div class="image-count">{{ currentImageIndex + 1 }} / {{ postImages.length }}</div>
          </div>
        </div>
        <div v-else class="post-image-placeholder">
          <svg class="placeholder-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <circle cx="8.5" cy="8.5" r="1.5"></circle>
            <polyline points="21 15 16 10 5 21"></polyline>
          </svg>
        </div>

        <!-- 互动按钮区域 -->
        <div class="post-actions">
          <button class="action-btn" @click="sendGift">
            <svg class="action-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <path d="M12 17.25l-6 3.75 1.5-7.5 6 3.75 6-3.75 1.5 7.5-6-3.75z"></path>
            </svg>
            <span class="action-text">礼物</span>
          </button>
          <button class="action-btn" @click="focusComment">
            <svg class="action-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <span class="action-text">{{ post.commentCount || 0 }}</span>
          </button>
          <button class="action-btn like-btn" :class="{ liked: isLiked }" @click="toggleLike">
            <svg class="action-icon" viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="1.5">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
            </svg>
            <span class="action-text">{{ likeCount }}</span>
          </button>
          <button class="action-btn" @click="sharePost">
            <svg class="action-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7 10 12 15 17 10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            <span class="action-text">分享</span>
          </button>
          <button class="action-btn more-btn">
            <svg class="action-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="12" r="1"></circle>
              <circle cx="19" cy="12" r="1"></circle>
              <circle cx="5" cy="12" r="1"></circle>
            </svg>
          </button>
        </div>

        <!-- 帖子标题和内容 -->
        <div class="post-content">
          <h1 class="post-title">{{ post.title }}</h1>
          <p class="post-text">{{ post.content }}</p>
        </div>

        <!-- 帖子标签 -->
        <div class="post-tags">
          <span v-for="tag in post.tags" :key="tag" class="tag">#{{ tag }}</span>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <div class="comments-header">
            <h3>评论 ({{ post.commentCount || 0 }})</h3>
            <div class="sort-buttons">
              <button 
                class="sort-btn" 
                :class="{ active: sortType === 'latest' }" 
                @click="sortType = 'latest'"
              >最新</button>
              <button 
                class="sort-btn" 
                :class="{ active: sortType === 'hot' }" 
                @click="sortType = 'hot'"
              >最热</button>
            </div>
          </div>

          <div class="comment-form">
            <div class="comment-input-wrapper">
              <input 
                v-model="commentContent" 
                type="text" 
                placeholder="写下你的评论..." 
                class="comment-input"
                @keyup.enter="submitComment"
              >
              <button class="submit-comment-btn" @click="submitComment" :disabled="!commentContent.trim()">
                发布
              </button>
            </div>
          </div>

          <div class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-author">
                <div class="author-avatar">
                  <img 
                    v-if="comment.avatar && comment.avatar.trim() !== ''" 
                    :src="comment.avatar" 
                    alt="用户头像" 
                    class="avatar-image"
                    @error="handleAvatarError($event)"
                  >
                  <div v-else class="avatar-placeholder">
                    {{ getAvatarInitial(comment.username) }}
                  </div>
                </div>
                <div class="author-info">
                  <div class="author-name-row">
                    <h4>{{ comment.username || '未知用户' }}</h4>
                    <span v-if="comment.isVip" class="vip-badge">✨</span>
                  </div>
                  <p class="comment-time">{{ formatDate(comment.createTime) }}</p>
                </div>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-actions">
                <button class="comment-action-btn" @click="likeComment(comment)">
                  <svg class="mini-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                  </svg>
                  <span>{{ comment.likeCount || 0 }}</span>
                </button>
                <button class="comment-action-btn reply-btn" @click="replyComment(comment)">
                  <svg class="mini-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5.586a1 1 0 0 1 .707.293l5.414 5.414a1 1 0 0 1 .293.707V19a2 2 0 0 1-2 2z"></path>
                  </svg>
                  <span>回复</span>
                </button>
              </div>
            </div>

            <div v-if="comments.length === 0" class="no-comments">
              <p>暂无评论，快来抢沙发吧！</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧作者信息卡片 -->
      <div class="sidebar">
        <div class="author-card">
          <div class="author-header">
            <div class="author-avatar-large">
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
            <div class="author-basic">
              <h3>{{ post.username || '未知用户' }}</h3>
              <button class="follow-btn" @click="toggleFollow">
                {{ isFollowing ? '已关注' : '+ 关注' }}
              </button>
            </div>
          </div>
          <div class="author-stats">
            <div class="stat-item">
              <span class="stat-value">{{ post.followerCount || 0 }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ post.postCount || 0 }}</span>
              <span class="stat-label">作品</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ post.likeCount || 0 }}</span>
              <span class="stat-label">获赞</span>
            </div>
          </div>
          <div class="author-desc">
            {{ post.bio || '暂无简介' }}
          </div>
          <div class="author-tags">
            <span v-for="tag in authorTags" :key="tag" class="author-tag">{{ tag }}</span>
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
import { getPostDetail } from '@/api/community';

const route = useRoute();
const router = useRouter();

const post = ref(null);
const comments = ref([]);
const loading = ref(false);
const error = ref(null);
const commentContent = ref('');
const sortType = ref('latest');
const isLiked = ref(false);
const likeCount = ref(0);
const isFollowing = ref(false);
const authorTags = ref([]);

// 轮播相关
const postImages = ref([]);
const currentImageIndex = ref(0);
const touchStartX = ref(0);
const touchEndX = ref(0);

const postId = route.params.id;

const fetchPostDetail = async () => {
  if (!postId) return;

  loading.value = true;
  error.value = null;

  try {
    const response = await getPostDetail(postId);

    if (response.code === 200 && response.data) {
      post.value = response.data;
      likeCount.value = response.data.likeCount || 0;
      authorTags.value = response.data.authorTags || [];
      
      // 解析图片（支持JSON数组和逗号分隔两种格式）
      postImages.value = parseImages(response.data.images);
      currentImageIndex.value = 0;
      
      if (response.data.comments) {
        comments.value = response.data.comments;
      } else {
        comments.value = [
          {
            id: 1,
            authorName: '测试用户',
            content: '太棒了一句话：好酷好帅的帖子',
            createTime: '2025-06-28 22:44:56',
            likeCount: 0,
            isVip: true
          },
          {
            id: 2,
            authorName: '另一个用户',
            content: '这个名字很酷',
            createTime: '2025-06-28 18:45:02',
            likeCount: 2,
            isVip: false
          }
        ];
      }
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

// 解析图片字符串，支持JSON数组和逗号分隔两种格式
const parseImages = (imagesStr) => {
  if (!imagesStr) return [];
  try {
    const images = JSON.parse(imagesStr);
    if (Array.isArray(images)) {
      return images.map(img => img.replace(/`/g, '').trim()).filter(img => img);
    }
  } catch (e) {
    // 如果不是JSON数组，尝试按逗号分割
    return imagesStr.split(',').map(img => img.trim()).filter(img => img);
  }
  return [];
};

// 轮播控制函数
const nextImage = () => {
  if (currentImageIndex.value < postImages.value.length - 1) {
    currentImageIndex.value++;
  } else {
    currentImageIndex.value = 0;
  }
};

const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--;
  } else {
    currentImageIndex.value = postImages.value.length - 1;
  }
};

const goToImage = (index) => {
  currentImageIndex.value = index;
};

// 触摸滑动事件处理
const handleTouchStart = (e) => {
  touchStartX.value = e.touches[0].clientX;
};

const handleTouchMove = (e) => {
  touchEndX.value = e.touches[0].clientX;
};

const handleTouchEnd = () => {
  const diff = touchStartX.value - touchEndX.value;
  if (Math.abs(diff) > 50) {
    if (diff > 0) {
      nextImage();
    } else {
      prevImage();
    }
  }
};

// 图片加载错误处理
const handleImageError = (event) => {
  event.target.style.display = 'none';
};

const submitComment = async () => {
  if (!commentContent.value.trim()) return;
  
  console.log('提交评论:', commentContent.value);
  
  const newComment = {
    id: Date.now(),
    authorName: '当前用户',
    content: commentContent.value,
    createTime: new Date().toISOString(),
    likeCount: 0,
    isVip: false
  };
  
  if (sortType.value === 'latest') {
    comments.value.unshift(newComment);
  } else {
    comments.value.push(newComment);
  }
  
  commentContent.value = '';
};

const toggleLike = () => {
  isLiked.value = !isLiked.value;
  likeCount.value += isLiked.value ? 1 : -1;
};

const likeComment = (comment) => {
  comment.likeCount = (comment.likeCount || 0) + 1;
};

const replyComment = (comment) => {
  commentContent.value = `@${comment.authorName} `;
};

const toggleFollow = () => {
  isFollowing.value = !isFollowing.value;
};

const sendGift = () => {
  alert('礼物功能暂未实现');
};

const focusComment = () => {
  document.querySelector('.comment-input')?.focus();
};

const sharePost = () => {
  alert('分享功能暂未实现');
};

const goBack = () => {
  router.push('/community');
};

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

onMounted(() => {
  fetchPostDetail();
});
</script>

<style scoped>
.post-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-header {
  margin-bottom: 20px;
}

.back-btn {
  padding: 8px 16px;
  background-color: #f5f5f5;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: all 0.3s;
}

.back-btn:hover {
  background-color: #e8e8e8;
}

.loading-state, .error-state, .not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top: 3px solid #00A2FF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-state p {
  color: #ff4d4f;
  margin: 0 0 15px 0;
}

.retry-btn {
  padding: 10px 24px;
  background-color: #00A2FF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.not-found p {
  color: #999;
  margin: 0 0 15px 0;
}

.detail-content {
  display: flex;
  gap: 30px;
}

.main-content {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.post-image-container {
  width: 100%;
  max-height: 500px;
  overflow: hidden;
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  max-height: 500px;
  overflow: hidden;
}

.carousel-container {
  position: relative;
  width: 100%;
  overflow: hidden;
}

.carousel-track {
  display: flex;
  transition: transform 0.3s ease;
}

.carousel-item {
  flex: 0 0 100%;
  width: 100%;
}

.post-image {
  width: 100%;
  height: auto;
  display: block;
  max-height: 500px;
  object-fit: cover;
}

.carousel-prev,
.carousel-next {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
  z-index: 10;
}

.carousel-prev:hover,
.carousel-next:hover {
  background: rgba(0, 0, 0, 0.7);
}

.carousel-prev {
  left: 15px;
}

.carousel-next {
  right: 15px;
}

.carousel-prev svg,
.carousel-next svg {
  width: 20px;
  height: 20px;
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 10;
}

.indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.indicator.active {
  width: 24px;
  border-radius: 5px;
  background: white;
}

.image-count {
  position: absolute;
  bottom: 20px;
  right: 20px;
  padding: 4px 10px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 12px;
  border-radius: 12px;
  z-index: 10;
}

.post-image-placeholder {
  width: 100%;
  height: 400px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  width: 64px;
  height: 64px;
  color: #ccc;
}

.post-actions {
  display: flex;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  padding: 10px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.action-btn:hover {
  color: #00A2FF;
}

.action-btn.liked {
  color: #FF6161;
}

.action-icon {
  width: 24px;
  height: 24px;
}

.action-text {
  font-size: 12px;
}

.more-btn {
  margin-left: auto;
}

.post-content {
  padding: 20px;
}

.post-title {
  margin: 0 0 15px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.4;
}

.post-text {
  margin: 0;
  font-size: 15px;
  color: #333;
  line-height: 1.8;
}

.post-tags {
  padding: 0 20px 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag {
  padding: 5px 12px;
  font-size: 13px;
  background: #e8f4fd;
  color: #00A2FF;
  border-radius: 15px;
}

.comments-section {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.comments-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.sort-buttons {
  display: flex;
  gap: 15px;
}

.sort-btn {
  padding: 5px 12px;
  font-size: 14px;
  color: #999;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.sort-btn.active {
  color: #00A2FF;
  background: #e8f4fd;
}

.comment-form {
  margin-bottom: 25px;
}

.comment-input-wrapper {
  display: flex;
  gap: 10px;
}

.comment-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 25px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.comment-input:focus {
  border-color: #00A2FF;
}

.submit-comment-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #00A2FF 0%, #0088e6 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.submit-comment-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.submit-comment-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  padding-bottom: 20px;
  border-bottom: 1px solid #f5f5f5;
}

.comment-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.comment-author {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #999;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  margin-right: 12px;
  overflow: hidden;
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
  background-color: #0088e6;
}

.author-info {
  flex: 1;
}

.author-name-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.author-info h4 {
  margin: 0 0 3px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.vip-badge {
  font-size: 12px;
}

.comment-time {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin-bottom: 10px;
  padding-left: 52px;
}

.comment-actions {
  display: flex;
  gap: 20px;
  padding-left: 52px;
}

.comment-action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 5px 0;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 12px;
  color: #999;
  transition: color 0.3s;
}

.comment-action-btn:hover {
  color: #00A2FF;
}

.mini-icon {
  width: 14px;
  height: 14px;
}

.no-comments {
  text-align: center;
  padding: 40px 0;
  color: #999;
  background: #fafafa;
  border-radius: 8px;
}

.no-comments p {
  margin: 0;
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
}

.author-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.author-header {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.author-avatar-large {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 24px;
  overflow: hidden;
}

.author-avatar-large .avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.author-avatar-large .avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: white;
  background-color: #0088e6;
}

.author-basic {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.author-basic h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.follow-btn {
  align-self: flex-start;
  padding: 6px 16px;
  background: linear-gradient(135deg, #00A2FF 0%, #0088e6 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.follow-btn:hover {
  opacity: 0.9;
}

.author-stats {
  display: flex;
  justify-content: space-around;
  padding: 15px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 15px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.stat-divider {
  width: 1px;
  background: #f0f0f0;
}

.author-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.author-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.author-tag {
  padding: 4px 10px;
  font-size: 12px;
  background: #f5f5f5;
  color: #666;
  border-radius: 10px;
}

@media (max-width: 900px) {
  .detail-content {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .post-detail {
    padding: 10px;
  }
  
  .post-image-container {
    max-height: 400px;
  }
  
  .post-title {
    font-size: 18px;
  }
  
  .post-text {
    font-size: 14px;
  }
  
  .action-btn {
    padding: 10px 15px;
  }
  
  .action-icon {
    width: 20px;
    height: 20px;
  }
  
  .author-card {
    padding: 15px;
  }
  
  .author-avatar-large {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
}
</style>