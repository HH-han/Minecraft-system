<template>
  <div class="post-detail">
    <!-- 返回按钮 -->
    <div class="detail-header">
      <button type="button" class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M15 18l-6-6 6-6" />
        </svg>
        <span>{{ t('community.detail.back') }}</span>
      </button>
    </div>

    <!-- 加载骨架屏 -->
    <div v-if="loading" class="detail-skeleton" aria-busy="true">
      <div class="skeleton-main">
        <div class="skeleton-block skeleton-hero"></div>
        <div class="skeleton-body">
          <div class="skeleton-author">
            <span class="skeleton-block skeleton-avatar"></span>
            <div class="skeleton-author-lines">
              <span class="skeleton-block skeleton-line w-30"></span>
              <span class="skeleton-block skeleton-line w-18"></span>
            </div>
          </div>
          <span class="skeleton-block skeleton-line skeleton-title"></span>
          <span class="skeleton-block skeleton-line"></span>
          <span class="skeleton-block skeleton-line"></span>
          <span class="skeleton-block skeleton-line w-60"></span>
        </div>
      </div>
      <div class="skeleton-block skeleton-side"></div>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="state-panel">
      <div class="state-icon state-icon--danger">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 8v5M12 16h.01" />
        </svg>
      </div>
      <h3>{{ t('community.feed.errorTitle') }}</h3>
      <p>{{ t(error) }}</p>
      <button type="button" class="primary-btn" @click="fetchPostDetail">{{ t('community.detail.retry') }}</button>
    </div>

    <!-- 帖子详情 -->
    <div v-else-if="post" class="detail-content">
      <article class="main-content theme-transition">
        <!-- 帖子图片轮播 -->
        <div v-if="postImages.length > 0" class="post-media">
          <div
            class="carousel-wrapper"
            @touchstart="handleTouchStart"
            @touchmove="handleTouchMove"
            @touchend="handleTouchEnd"
          >
            <div class="carousel-container">
              <div class="carousel-track" :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }">
                <div v-for="(img, index) in postImages" :key="index" class="carousel-item">
                  <img
                    :src="img"
                    :alt="t('community.detail.imageAlt', { index: index + 1 })"
                    class="post-image"
                    @error="handleImageError($event)"
                  />
                </div>
              </div>
            </div>

            <!-- 左右切换按钮 -->
            <button
              v-show="postImages.length > 1"
              type="button"
              class="carousel-btn carousel-prev"
              :aria-label="t('community.detail.prevImage')"
              @click="prevImage"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M15 19l-7-7 7-7" />
              </svg>
            </button>
            <button
              v-show="postImages.length > 1"
              type="button"
              class="carousel-btn carousel-next"
              :aria-label="t('community.detail.nextImage')"
              @click="nextImage"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 5l7 7-7 7" />
              </svg>
            </button>

            <!-- 指示器 -->
            <div v-show="postImages.length > 1" class="carousel-indicators">
              <span
                v-for="(_, index) in postImages"
                :key="index"
                class="indicator"
                :class="{ active: currentImageIndex === index }"
                @click="goToImage(index)"
              ></span>
            </div>

            <!-- 图片数量提示 -->
            <div class="image-count">
              {{ t('community.detail.imageOf', { current: currentImageIndex + 1, total: postImages.length }) }}
            </div>
          </div>

          <!-- 缩略图 -->
          <div v-if="postImages.length > 1" class="carousel-thumbs">
            <button
              v-for="(img, index) in postImages"
              :key="index"
              type="button"
              class="thumb"
              :class="{ active: currentImageIndex === index }"
              @click="goToImage(index)"
            >
              <img :src="img" alt="" @error="handleImageError($event)" />
            </button>
          </div>
        </div>

        <div v-else class="post-media post-media--empty">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="18" height="18" rx="4" />
            <circle cx="8.5" cy="8.5" r="1.5" />
            <path d="M21 15l-5-5L5 21" />
          </svg>
          <span>{{ t('community.detail.noImages') }}</span>
        </div>

        <div class="post-body">
          <!-- 作者信息 -->
          <div class="post-meta">
            <div class="meta-author">
              <div class="author-avatar">
                <img
                  v-if="post.avatar && post.avatar.trim() !== ''"
                  :src="post.avatar"
                  alt="avatar"
                  class="avatar-image"
                  @error="handleAvatarError($event)"
                />
                <div v-else class="avatar-placeholder">
                  {{ getAvatarInitial(post.username) }}
                </div>
              </div>
              <div class="meta-author-info">
                <span class="meta-name">{{ post.username || t('community.detail.unknownUser') }}</span>
                <span class="meta-time">{{ formatDate(post.createTime) }}</span>
              </div>
            </div>
            <span v-if="postImages.length" class="meta-chip">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="3" />
                <circle cx="8.5" cy="8.5" r="1.5" />
                <path d="M21 15l-5-5L5 21" />
              </svg>
              {{ t('community.detail.imagesCount', { count: postImages.length }) }}
            </span>
          </div>

          <h1 class="post-title">{{ post.title }}</h1>
          <p class="post-text">{{ post.content }}</p>

          <!-- 帖子标签 -->
          <div v-if="post.tags && post.tags.length" class="post-tags">
            <span v-for="tag in post.tags" :key="tag" class="tag"># {{ tag }}</span>
          </div>

          <!-- 互动按钮区域 -->
          <div class="post-actions">
            <button type="button" class="action-btn" @click="sendGift">
              <svg class="action-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="2" />
                <path d="M12 17.25l-6 3.75 1.5-7.5 6 3.75 6-3.75 1.5 7.5-6-3.75z" />
              </svg>
              <span>{{ t('community.detail.gift') }}</span>
            </button>
            <button type="button" class="action-btn" @click="focusComment">
              <svg class="action-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
              </svg>
              <span>{{ post.commentCount || 0 }}</span>
            </button>
            <button type="button" class="action-btn like-btn" :class="{ liked: isLiked }" @click="toggleLike">
              <svg class="action-icon" viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
              </svg>
              <span>{{ likeCount }}</span>
            </button>
            <button type="button" class="action-btn" @click="sharePost">
              <svg class="action-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
              <span>{{ t('community.detail.share') }}</span>
            </button>
            <button type="button" class="action-btn action-btn--icon" :aria-label="t('community.detail.more')">
              <svg class="action-icon" viewBox="0 0 24 24" fill="currentColor">
                <circle cx="12" cy="12" r="1.6" />
                <circle cx="19" cy="12" r="1.6" />
                <circle cx="5" cy="12" r="1.6" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 评论区 -->
        <section class="comments-section">
          <div class="comments-header">
            <h3>
              {{ t('community.detail.commentSection') }}
              <span class="count-chip">{{ post.commentCount || 0 }}</span>
            </h3>
            <div class="sort-buttons">
              <button
                type="button"
                class="sort-btn"
                :class="{ active: sortType === 'latest' }"
                @click="sortType = 'latest'"
              >{{ t('community.detail.latest') }}</button>
              <button
                type="button"
                class="sort-btn"
                :class="{ active: sortType === 'hot' }"
                @click="sortType = 'hot'"
              >{{ t('community.detail.hot') }}</button>
            </div>
          </div>

          <form class="comment-form" @submit.prevent="submitComment">
            <div class="comment-input-wrapper">
              <input
                v-model="commentContent"
                type="text"
                :placeholder="t('community.detail.commentPlaceholder')"
                class="comment-input"
              />
              <button type="submit" class="submit-comment-btn" :disabled="!commentContent.trim()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M22 2L11 13" />
                  <path d="M22 2l-7 20-4-9-9-4 20-7z" />
                </svg>
                <span>{{ t('community.detail.publish') }}</span>
              </button>
            </div>
          </form>

          <div class="comments-list">
            <article v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-author">
                <div class="author-avatar">
                  <img
                    v-if="comment.avatar && comment.avatar.trim() !== ''"
                    :src="comment.avatar"
                    alt="avatar"
                    class="avatar-image"
                    @error="handleAvatarError($event)"
                  />
                  <div v-else class="avatar-placeholder">
                    {{ getAvatarInitial(comment.username || comment.authorName) }}
                  </div>
                </div>
                <div class="author-info">
                  <div class="author-name-row">
                    <h4>{{ comment.username || comment.authorName || t('community.detail.unknownUser') }}</h4>
                    <span v-if="comment.isVip" class="vip-badge">
                      <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2l2.9 6.2 6.6.9-4.8 4.7 1.2 6.6L12 17.3 6.1 20.4l1.2-6.6L2.5 9.1l6.6-.9z" />
                      </svg>
                      VIP
                    </span>
                  </div>
                  <p class="comment-time">{{ formatDate(comment.createTime) }}</p>
                </div>

                <div class="comment-actions">
                  <button type="button" class="comment-action-btn" @click="likeComment(comment)">
                    <svg class="mini-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                    </svg>
                    <span>{{ comment.likeCount || 0 }}</span>
                  </button>
                  <button type="button" class="comment-action-btn" @click="replyComment(comment)">
                    <svg class="mini-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5.586a1 1 0 0 1 .707.293l5.414 5.414a1 1 0 0 1 .293.707V19a2 2 0 0 1-2 2z" />
                    </svg>
                    <span>{{ t('community.detail.reply') }}</span>
                  </button>
                </div>
              </div>

              <div class="comment-content">{{ comment.content }}</div>
            </article>

            <div v-if="comments.length === 0" class="no-comments">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
              </svg>
              <p>{{ t('community.detail.noComments') }}</p>
            </div>
          </div>
        </section>
      </article>

      <!-- 右侧作者信息卡片 -->
      <aside class="sidebar">
        <div class="author-card theme-transition">
          <div class="author-cover"></div>
          <div class="author-card__body">
            <div class="author-header">
              <div class="author-avatar-large">
                <img
                  v-if="post.avatar && post.avatar.trim() !== ''"
                  :src="post.avatar"
                  alt="avatar"
                  class="avatar-image"
                  @error="handleAvatarError($event)"
                />
                <div v-else class="avatar-placeholder">
                  {{ getAvatarInitial(post.username) }}
                </div>
              </div>
              <div class="author-name-block">
                <h3>{{ post.username || t('community.detail.unknownUser') }}</h3>
                <span class="author-role">{{ t('community.detail.authorRole') }}</span>
              </div>
            </div>

            <p class="author-desc">{{ post.bio || t('community.detail.noBio') }}</p>

            <button
              type="button"
              class="follow-btn"
              :class="{ following: isFollowing }"
              @click="toggleFollow"
            >
              {{ isFollowing ? t('community.detail.following') : `+ ${t('community.detail.follow')}` }}
            </button>

            <div class="author-stats">
              <div class="stat-item">
                <span class="stat-value">{{ post.followerCount || 0 }}</span>
                <span class="stat-label">{{ t('community.detail.followers') }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ post.postCount || 0 }}</span>
                <span class="stat-label">{{ t('community.detail.works') }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ post.likeCount || 0 }}</span>
                <span class="stat-label">{{ t('community.detail.likesReceived') }}</span>
              </div>
            </div>

            <div v-if="authorTags.length" class="author-tags">
              <span v-for="tag in authorTags" :key="tag" class="author-tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <!-- 帖子不存在 -->
    <div v-else class="state-panel">
      <div class="state-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="9" />
          <path d="M9.5 9.5h.01M14.5 9.5h.01M9 15c.8-1 1.8-1.5 3-1.5s2.2.5 3 1.5" />
        </svg>
      </div>
      <p>{{ t('community.detail.notFound') }}</p>
      <button type="button" class="primary-btn" @click="goBack">{{ t('community.detail.back') }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { getPostDetail } from '@/api/community';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';

const route = useRoute();
const router = useRouter();
const { t, locale } = useI18n();

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

      // 解析图片（支持 JSON 数组和逗号分隔两种格式）
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
      error.value = 'community.detail.errorFetch';
    }
  } catch (err) {
    error.value = 'community.detail.errorNetwork';
    console.error('获取帖子详情失败:', err);
  } finally {
    loading.value = false;
  }
};

// 数据变更后自动刷新
useAutoRefresh(fetchPostDetail);

// 解析图片字符串，支持 JSON 数组和逗号分隔两种格式
const parseImages = (imagesStr) => {
  if (!imagesStr) return [];
  try {
    const images = JSON.parse(imagesStr);
    if (Array.isArray(images)) {
      return images.map(img => img.replace(/`/g, '').trim()).filter(img => img);
    }
  } catch {
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
  commentContent.value = `@${comment.username || comment.authorName} `;
};

const toggleFollow = () => {
  isFollowing.value = !isFollowing.value;
};

const sendGift = () => {
  ElMessage.info(t('community.detail.giftNotImplemented'));
};

const focusComment = () => {
  document.querySelector('.comment-input')?.focus();
};

const sharePost = () => {
  ElMessage.info(t('community.detail.shareNotImplemented'));
};

const goBack = () => {
  router.push('/community');
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return '';
  return date.toLocaleString(locale.value, {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取头像首字母
const getAvatarInitial = (name) => {
  if (!name) return '?';
  return name.charAt(0).toUpperCase();
};

// 头像加载失败处理
const handleAvatarError = (event) => {
  const img = event.target;
  const parent = img.parentElement;
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
  max-width: 1180px;
  margin: 0 auto;
  padding: 24px 24px 64px;
  color: var(--theme-text-1);
}

/* ---------- 返回 ---------- */
.detail-header {
  margin-bottom: 20px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 18px 9px 14px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  font-size: 13.5px;
  cursor: pointer;
  transition:
    color var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    background-color var(--theme-duration-fast) var(--theme-ease),
    transform var(--theme-duration-fast) var(--theme-ease);
}

.back-btn svg {
  width: 16px;
  height: 16px;
}

.back-btn:hover {
  color: var(--theme-primary);
  border-color: color-mix(in srgb, var(--theme-primary) 36%, transparent);
  background: var(--theme-primary-soft);
  transform: translateX(-2px);
}

.back-btn:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

/* ---------- 布局 ---------- */
.detail-content {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 24px;
  align-items: start;
}

.main-content {
  min-width: 0;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  border-radius: var(--theme-radius-lg);
  box-shadow: var(--theme-shadow-sm);
  overflow: hidden;
}

/* ---------- 图片区 ---------- */
.post-media {
  background: var(--theme-bg-surface-2);
}

.carousel-wrapper {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

.carousel-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.carousel-track {
  display: flex;
  height: 100%;
  transition: transform var(--theme-duration-slow) var(--theme-ease);
}

.carousel-item {
  flex: 0 0 100%;
  width: 100%;
  height: 100%;
}

.post-image {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.35);
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.34);
  color: #fff;
  cursor: pointer;
  backdrop-filter: blur(8px);
  transition:
    background-color var(--theme-duration-fast) var(--theme-ease),
    transform var(--theme-duration-fast) var(--theme-ease);
  z-index: 2;
}

.carousel-btn svg {
  width: 18px;
  height: 18px;
}

.carousel-btn:hover {
  background: rgba(0, 0, 0, 0.58);
  transform: translateY(-50%) scale(1.06);
}

.carousel-prev {
  left: 16px;
}

.carousel-next {
  right: 16px;
}

.carousel-indicators {
  position: absolute;
  left: 50%;
  bottom: 18px;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  padding: 6px 10px;
  border-radius: var(--theme-radius-pill);
  background: rgba(0, 0, 0, 0.28);
  backdrop-filter: blur(8px);
  z-index: 2;
}

.indicator {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition:
    width var(--theme-duration) var(--theme-ease),
    background-color var(--theme-duration) var(--theme-ease);
}

.indicator.active {
  width: 20px;
  border-radius: var(--theme-radius-pill);
  background: #fff;
}

.image-count {
  position: absolute;
  right: 16px;
  bottom: 16px;
  padding: 4px 11px;
  border-radius: var(--theme-radius-pill);
  background: rgba(0, 0, 0, 0.38);
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  backdrop-filter: blur(8px);
  z-index: 2;
}

.carousel-thumbs {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  overflow-x: auto;
  background: var(--theme-bg-surface);
  border-bottom: 1px solid var(--theme-hairline);
}

.thumb {
  flex: 0 0 auto;
  width: 58px;
  height: 58px;
  padding: 0;
  border: 2px solid transparent;
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  cursor: pointer;
  overflow: hidden;
  opacity: 0.6;
  transition:
    opacity var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease);
}

.thumb img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.thumb:hover {
  opacity: 1;
}

.thumb.active {
  border-color: var(--theme-primary);
  opacity: 1;
}

.post-media--empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  aspect-ratio: 21 / 9;
  color: var(--theme-text-3);
  background:
    radial-gradient(90% 120% at 50% 0%, var(--theme-primary-soft), transparent 65%),
    var(--theme-bg-surface-2);
}

.post-media--empty svg {
  width: 40px;
  height: 40px;
}

.post-media--empty span {
  font-size: 13px;
}

/* ---------- 正文 ---------- */
.post-body {
  padding: 24px 28px 22px;
}

.post-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  padding-bottom: 18px;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--theme-hairline);
}

.meta-author {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.author-avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  overflow: hidden;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  font-weight: 600;
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--theme-primary) 14%, transparent);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 600;
  color: var(--theme-primary-text);
  background: var(--theme-primary);
}

.meta-author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.meta-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta-time {
  font-size: 12px;
  color: var(--theme-text-3);
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 11px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-3);
  font-size: 12px;
}

.meta-chip svg {
  width: 13px;
  height: 13px;
}

.post-title {
  margin: 0 0 16px;
  font-size: clamp(22px, 2.6vw, 30px);
  font-weight: 700;
  line-height: 1.35;
  letter-spacing: -0.015em;
  color: var(--theme-text-1);
}

.post-text {
  margin: 0;
  font-size: 15.5px;
  line-height: 1.9;
  color: var(--theme-text-2);
  white-space: pre-wrap;
  word-break: break-word;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 20px;
}

.tag {
  padding: 5px 12px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 12.5px;
  font-weight: 500;
}

/* 互动 */
.post-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 26px;
  padding-top: 20px;
  border-top: 1px solid var(--theme-hairline);
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 9px 16px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 13px;
  cursor: pointer;
  transition:
    color var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    background-color var(--theme-duration-fast) var(--theme-ease),
    transform var(--theme-duration-fast) var(--theme-ease);
}

.action-btn:hover {
  color: var(--theme-primary);
  border-color: color-mix(in srgb, var(--theme-primary) 36%, transparent);
  background: var(--theme-primary-soft);
  transform: translateY(-1px);
}

.action-btn.liked {
  color: var(--theme-danger);
  border-color: color-mix(in srgb, var(--theme-danger) 40%, transparent);
  background: var(--theme-danger-soft);
}

.action-icon {
  width: 17px;
  height: 17px;
}

.action-btn--icon {
  margin-left: auto;
  padding: 9px 13px;
}

/* ---------- 评论区 ---------- */
.comments-section {
  padding: 24px 28px 28px;
  border-top: 1px solid var(--theme-hairline);
  background: color-mix(in srgb, var(--theme-bg-surface-2) 32%, transparent);
}

.comments-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 18px;
}

.comments-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 17px;
  font-weight: 650;
  color: var(--theme-text-1);
}

.count-chip {
  padding: 2px 9px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 12px;
  font-weight: 600;
}

.sort-buttons {
  display: flex;
  gap: 2px;
  padding: 3px;
  border: 1px solid var(--theme-hairline);
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
}

.sort-btn {
  padding: 6px 14px;
  border: none;
  border-radius: var(--theme-radius-pill);
  background: transparent;
  color: var(--theme-text-3);
  font-size: 13px;
  cursor: pointer;
  transition:
    color var(--theme-duration-fast) var(--theme-ease),
    background-color var(--theme-duration-fast) var(--theme-ease);
}

.sort-btn.active {
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-weight: 600;
  box-shadow: var(--theme-shadow-sm);
}

.comment-form {
  margin-bottom: 22px;
}

.comment-input-wrapper {
  display: flex;
  gap: 10px;
}

.comment-input {
  flex: 1;
  min-width: 0;
  padding: 12px 18px;
  border: 1.5px solid var(--theme-border);
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font: inherit;
  font-size: 14px;
  outline: none;
  transition:
    border-color var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.comment-input::placeholder {
  color: var(--theme-text-3);
}

.comment-input:focus {
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.submit-comment-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding: 0 22px;
  border: none;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition:
    filter var(--theme-duration-fast) var(--theme-ease),
    opacity var(--theme-duration-fast) var(--theme-ease);
}

.submit-comment-btn svg {
  width: 15px;
  height: 15px;
}

.submit-comment-btn:hover:not(:disabled) {
  filter: brightness(1.06);
}

.submit-comment-btn:disabled {
  cursor: not-allowed;
  opacity: 0.45;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-item {
  padding: 16px;
  border-radius: var(--theme-radius-md);
  transition: background-color var(--theme-duration-fast) var(--theme-ease);
}

.comment-item:hover {
  background: var(--theme-bg-hover);
}

.comment-author {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.author-info {
  flex: 1;
  min-width: 0;
}

.author-name-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.author-info h4 {
  margin: 0 0 3px;
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.vip-badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 1px 6px;
  border-radius: var(--theme-radius-pill);
  background: color-mix(in srgb, var(--theme-warning) 16%, transparent);
  color: var(--theme-warning);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.vip-badge svg {
  width: 10px;
  height: 10px;
}

.comment-time {
  margin: 0;
  font-size: 12px;
  color: var(--theme-text-3);
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
}

.comment-action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  font-size: 12px;
  cursor: pointer;
  transition: color var(--theme-duration-fast) var(--theme-ease);
}

.comment-action-btn:hover {
  color: var(--theme-primary);
}

.mini-icon {
  width: 14px;
  height: 14px;
}

.comment-content {
  margin: 10px 0 0 52px;
  font-size: 14px;
  line-height: 1.7;
  color: var(--theme-text-2);
  word-break: break-word;
}

.no-comments {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 40px 0;
  border: 1px dashed var(--theme-border);
  border-radius: var(--theme-radius-md);
  color: var(--theme-text-3);
}

.no-comments svg {
  width: 30px;
  height: 30px;
  opacity: 0.7;
}

.no-comments p {
  margin: 0;
  font-size: 13px;
}

/* ---------- 侧边作者卡 ---------- */
.sidebar {
  position: sticky;
  top: 88px;
}

.author-card {
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  border-radius: var(--theme-radius-lg);
  box-shadow: var(--theme-shadow-sm);
  overflow: hidden;
}

.author-cover {
  position: relative;
  z-index: 0;
  height: 76px;
  background: var(--theme-gradient);
}

.author-cover::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(255, 255, 255, 0.32) 1px, transparent 1px);
  background-size: 14px 14px;
  opacity: 0.5;
}

.author-card__body {
  padding: 0 20px 20px;
}

.author-header {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-top: -26px;
  margin-bottom: 14px;
}

.author-avatar-large {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  overflow: hidden;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  font-weight: 600;
  box-shadow: 0 0 0 4px var(--theme-bg-surface);
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
  color: var(--theme-primary-text);
  background: var(--theme-primary);
}

.author-name-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-top: 32px;
  min-width: 0;
}

.author-name-block h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 650;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.author-role {
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--theme-text-3);
}

.author-desc {
  margin: 0 0 14px;
  font-size: 13px;
  line-height: 1.7;
  color: var(--theme-text-2);
}

.follow-btn {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 13.5px;
  font-weight: 600;
  cursor: pointer;
  transition:
    filter var(--theme-duration-fast) var(--theme-ease),
    background-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.follow-btn:hover {
  filter: brightness(1.06);
}

.follow-btn.following {
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
}

.author-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid var(--theme-hairline);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
}

.stat-item + .stat-item {
  border-left: 1px solid var(--theme-hairline);
}

.stat-value {
  font-size: 18px;
  font-weight: 650;
  color: var(--theme-text-1);
  line-height: 1.2;
}

.stat-label {
  font-size: 11px;
  letter-spacing: 0.04em;
  color: var(--theme-text-3);
}

.author-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.author-tag {
  padding: 4px 10px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 11.5px;
}

/* ---------- 骨架屏 ---------- */
.detail-skeleton {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 24px;
  align-items: start;
}

.skeleton-main {
  border: 1px solid var(--theme-hairline);
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  overflow: hidden;
}

.skeleton-block {
  display: block;
  border-radius: 8px;
  background: linear-gradient(
    90deg,
    var(--theme-bg-surface-2) 25%,
    var(--theme-bg-hover) 37%,
    var(--theme-bg-surface-2) 63%
  );
  background-size: 400% 100%;
  animation: shimmer 1.4s ease infinite;
}

.skeleton-hero {
  aspect-ratio: 16 / 9;
  border-radius: 0;
}

.skeleton-body {
  padding: 24px 28px 32px;
}

.skeleton-author {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.skeleton-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.skeleton-author-lines {
  display: flex;
  flex-direction: column;
  gap: 7px;
  flex: 1;
}

.skeleton-line {
  height: 12px;
  margin-bottom: 12px;
}

.skeleton-title {
  height: 24px;
  width: 60%;
  margin-bottom: 20px;
}

.skeleton-side {
  height: 320px;
  border-radius: var(--theme-radius-lg);
}

.w-30 {
  width: 30%;
}

.w-18 {
  width: 18%;
}

.w-60 {
  width: 60%;
}

@keyframes shimmer {
  0% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0 50%;
  }
}

/* ---------- 状态面板 ---------- */
.state-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  text-align: center;
  border: 1px dashed var(--theme-border);
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
}

.state-icon {
  width: 56px;
  height: 56px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.state-icon--danger {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
}

.state-icon svg {
  width: 26px;
  height: 26px;
}

.state-panel h3 {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.state-panel p {
  margin: 0 0 22px;
  font-size: 14px;
  color: var(--theme-text-3);
}

.primary-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 24px;
  border: none;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: filter var(--theme-duration-fast) var(--theme-ease);
}

.primary-btn:hover {
  filter: brightness(1.06);
}

.primary-btn:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

/* ---------- 响应式 ---------- */
@media (max-width: 1024px) {
  .detail-content,
  .detail-skeleton {
    grid-template-columns: minmax(0, 1fr) 280px;
  }
}

@media (max-width: 900px) {
  .post-detail {
    padding: 16px 16px 48px;
  }

  .detail-content,
  .detail-skeleton {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
  }

  .post-body {
    padding: 20px;
  }

  .comments-section {
    padding: 20px;
  }

  .post-title {
    font-size: 22px;
  }

  .post-text {
    font-size: 14.5px;
  }

  .comment-content {
    margin-left: 0;
    padding-left: 52px;
  }

  .carousel-wrapper {
    aspect-ratio: 4 / 3;
  }

  .post-media--empty {
    aspect-ratio: 16 / 9;
  }
}

@media (max-width: 640px) {
  .post-actions {
    gap: 8px;
  }

  .action-btn {
    padding: 8px 13px;
    font-size: 12.5px;
  }

  .action-btn--icon {
    display: none;
  }

  .submit-comment-btn span {
    display: none;
  }

  .submit-comment-btn {
    padding: 0 16px;
  }

  .comment-actions {
    gap: 10px;
  }

  .comment-content {
    padding-left: 0;
    margin-left: 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .skeleton-block,
  .carousel-track {
    animation: none;
    transition: none;
  }

  .back-btn,
  .action-btn,
  .comment-item {
    transition: none;
  }
}
</style>
