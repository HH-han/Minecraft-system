<template>
  <div class="community-content">
    <header class="content-header">
      <div class="header-copy">
        <span class="eyebrow">{{ t('community.feed.eyebrow') }}</span>
        <div class="title-row">
          <h2>{{ t('community.feed.title') }}</h2>
          <span v-if="!loading && !error && total > 0" class="post-count">
            {{ t('community.feed.postCount', { count: total }) }}
          </span>
        </div>
        <p class="header-subtitle">{{ t('community.feed.subtitle') }}</p>
      </div>
      <button type="button" class="create-post-btn" @click="showCreateModal = true">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
          <path d="M12 5v14M5 12h14" />
        </svg>
        <span>{{ t('community.feed.createPost') }}</span>
      </button>
    </header>

    <!-- 加载骨架屏（下滑追加时保留列表） -->
    <div v-if="loading && !appending" class="skeleton-grid" aria-busy="true">
      <div v-for="n in 4" :key="n" class="skeleton-card">
        <div class="skeleton-author">
          <span class="skeleton-block skeleton-avatar"></span>
          <div class="skeleton-author-lines">
            <span class="skeleton-block skeleton-line w-40"></span>
            <span class="skeleton-block skeleton-line w-24"></span>
          </div>
        </div>
        <span class="skeleton-block skeleton-line skeleton-title"></span>
        <span class="skeleton-block skeleton-line"></span>
        <span class="skeleton-block skeleton-line w-70"></span>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="state-card">
      <div class="state-icon state-icon--danger">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 8v5M12 16h.01" />
        </svg>
      </div>
      <h3>{{ t('community.feed.errorTitle') }}</h3>
      <p>{{ t(error) }}</p>
      <button type="button" class="primary-btn" @click="fetchPosts">{{ t('community.feed.retry') }}</button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="posts.length === 0" class="state-card">
      <div class="state-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 20h9" />
          <path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4z" />
        </svg>
      </div>
      <h3>{{ t('community.feed.emptyTitle') }}</h3>
      <p>{{ t('community.feed.emptyDesc') }}</p>
      <button type="button" class="primary-btn" @click="showCreateModal = true">
        {{ t('community.feed.emptyAction') }}
      </button>
    </div>

    <!-- 帖子列表 -->
    <div v-else class="posts-grid">
      <article
        v-for="(post, index) in posts"
        :key="post.id"
        class="post-card theme-transition"
        :style="{ animationDelay: `${Math.min(index, 8) * 45}ms` }"
        :aria-label="post.title"
        role="button"
        tabindex="0"
        @click="goToDetail(post.id)"
        @keydown.enter="goToDetail(post.id)"
        @keydown.space.prevent="goToDetail(post.id)"
      >
        <div v-if="post._images.length" class="post-cover">
          <img
            :src="post._images[0]"
            :alt="post.title"
            loading="lazy"
            @error="handleCoverError"
          />
          <span v-if="post._images.length > 1" class="cover-badge">
            {{ t('community.feed.imageCount', { count: post._images.length }) }}
          </span>
        </div>

        <div class="post-header">
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
          <div class="author-info">
            <h3>{{ post.username || t('community.feed.unknownUser') }}</h3>
            <p class="post-time">{{ formatDate(post.createTime) }}</p>
          </div>
          <svg class="card-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M7 17L17 7M9 7h8v8" />
          </svg>
        </div>

        <div class="post-content">
          <h4 class="post-title">{{ post.title }}</h4>
          <p class="post-text">{{ post.content }}</p>
        </div>

        <div class="post-footer">
          <div class="post-stats">
            <span class="stat-item" :title="t('community.feed.likes')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
              </svg>
              {{ post.likeCount || 0 }}
            </span>
            <span class="stat-item" :title="t('community.feed.comments')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
              </svg>
              {{ post.commentCount || 0 }}
            </span>
            <span class="stat-item" :title="t('community.feed.collects')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
              </svg>
              {{ post.collectCount || 0 }}
            </span>
          </div>
          <div v-if="post.tags && post.tags.length" class="post-tags">
            <span v-for="tag in post.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </article>
    </div>

    <!-- 分页 -->
    <Paging
      v-if="totalPages > 0"
      :total-pages="totalPages"
      :current-page="currentPage"
      :loading="loading"
      @update:current-page="handlePageChange"
      @load-more="handleLoadMore"
    />

    <!-- 创建帖子模态框 -->
    <transition name="modal">
      <div v-if="showCreateModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal-content" role="dialog" aria-modal="true" :aria-label="t('community.create.title')">
          <div class="modal-header">
            <div class="modal-title-group">
              <span class="eyebrow">{{ t('community.create.eyebrow') }}</span>
              <h3>{{ t('community.create.title') }}</h3>
            </div>
            <button type="button" class="close-btn" :aria-label="t('community.create.cancel')" @click="closeModal">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="modal-body">
            <form @submit.prevent="handleSubmit">
              <div class="form-group">
                <label for="title">{{ t('community.create.postTitle') }}</label>
                <input
                  id="title"
                  v-model="newPost.title"
                  type="text"
                  required
                  :placeholder="t('community.create.titlePlaceholder')"
                />
              </div>

              <div class="form-group">
                <label for="content">{{ t('community.create.content') }}</label>
                <textarea
                  id="content"
                  v-model="newPost.content"
                  required
                  rows="5"
                  :placeholder="t('community.create.contentPlaceholder')"
                ></textarea>
              </div>

              <div class="form-group">
                <div class="label-row">
                  <label>{{ t('community.create.images') }}</label>
                  <span class="field-counter">{{ selectedImages.length }}/9</span>
                </div>
                <p class="field-hint">{{ t('community.create.imagesHint') }}</p>

                <div class="image-upload-area">
                  <div v-if="selectedImages.length > 0" class="image-preview-list">
                    <div v-for="(img, index) in selectedImages" :key="index" class="image-preview-item">
                      <img :src="img.preview" :alt="`${t('community.create.images')} ${index + 1}`" />
                      <div v-if="img.uploading" class="upload-progress">
                        <div class="progress-bar" :style="{ width: img.progress + '%' }"></div>
                      </div>
                      <div v-if="img.error" class="upload-error">
                        <span>{{ t('community.create.uploadFailed') }}</span>
                      </div>
                      <button
                        type="button"
                        class="remove-image-btn"
                        :disabled="img.uploading"
                        :aria-label="t('community.create.removeImage')"
                        @click="removeImage(index)"
                      >
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round">
                          <path d="M18 6L6 18M6 6l12 12" />
                        </svg>
                      </button>
                    </div>
                  </div>

                  <button
                    v-if="selectedImages.length < 9"
                    type="button"
                    class="upload-trigger"
                    @click="triggerFileInput"
                  >
                    <input
                      ref="fileInputRef"
                      type="file"
                      accept="image/jpeg,image/png,image/gif,image/webp"
                      multiple
                      class="file-input-hidden"
                      @change="handleFileSelect"
                    />
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="3" y="3" width="18" height="18" rx="4" />
                      <circle cx="8.5" cy="8.5" r="1.5" />
                      <path d="M21 15l-5-5L5 21" />
                    </svg>
                    <span>{{ t('community.create.upload') }}</span>
                  </button>
                </div>

                <div v-if="uploadError" class="upload-error-message">{{ uploadError }}</div>
              </div>

              <div class="form-group">
                <label for="tags">{{ t('community.create.tags') }}</label>
                <input
                  id="tags"
                  v-model="newPost.tags"
                  type="text"
                  :placeholder="t('community.create.tagsPlaceholder')"
                />
              </div>

              <div class="form-actions">
                <button type="button" class="cancel-btn" @click="closeModal">
                  {{ t('community.create.cancel') }}
                </button>
                <button
                  type="submit"
                  class="submit-btn"
                  :disabled="isSubmitting || selectedImages.some(img => img.uploading)"
                >
                  {{ isSubmitting ? t('community.create.submitting') : t('community.create.submit') }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  getPostList,
  createPost as createPostApi
} from '@/api/community';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';
import Paging from '@/components/paging/index.vue';

const router = useRouter();
const { t, locale } = useI18n();

// 状态管理
const posts = ref([]);
const loading = ref(false);
// 下滑追加加载中：列表保持挂载，仅底部提示加载，避免滚动位置被重置
const appending = ref(false);
const error = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const totalPages = ref(0);

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

// 解析图片字符串，兼容 JSON 数组与逗号分隔两种格式
const parseImages = (imagesStr) => {
  if (!imagesStr) return [];
  try {
    const images = JSON.parse(imagesStr);
    if (Array.isArray(images)) {
      return images.map(img => img.replace(/`/g, '').trim()).filter(Boolean);
    }
  } catch {
    return imagesStr.split(',').map(img => img.trim()).filter(Boolean);
  }
  return [];
};

// 获取帖子列表（append=true 时在已有数据之后追加，不重置）
const fetchPosts = async (page = currentPage.value, { append = false } = {}) => {
  appending.value = append;
  loading.value = true;
  error.value = null;

  try {
    const response = await getPostList({
      current: page,
      size: pageSize.value
    });

    if (response.code === 200 && response.data) {
      const records = (response.data.records || []).map(item => ({
        ...item,
        _images: parseImages(item.images)
      }));
      posts.value = append ? [...posts.value, ...records] : records;
      total.value = Number(response.data.total) || 0;
      totalPages.value = Math.ceil(total.value / pageSize.value);
    } else {
      error.value = 'community.feed.errorFetch';
    }
  } catch (err) {
    error.value = 'community.feed.errorNetwork';
    console.error('获取帖子失败:', err);
  } finally {
    loading.value = false;
    appending.value = false;
  }
};

// 数据变更后自动刷新
useAutoRefresh(fetchPosts);

// 分页切换（点击页码/上下页：跳页并替换数据）
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchPosts(page);
};

// 下滑继续加载（在已有数据之后追加新数据）
const handleLoadMore = (page) => {
  currentPage.value = page;
  fetchPosts(page, { append: true });
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
  if (!ALLOWED_TYPES.includes(file.type)) {
    return t('community.create.invalidType');
  }

  if (file.size > MAX_FILE_SIZE) {
    return t('community.create.tooLarge');
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
    uploadError.value = t('community.create.tooMany', { count: remainingSlots });
    event.target.value = '';
    return;
  }

  // 处理每个文件
  Array.from(files).forEach(file => {
    const fileError = validateFile(file);
    if (fileError) {
      uploadError.value = fileError;
      return;
    }

    // 创建预览
    const reader = new FileReader();
    reader.onload = (e) => {
      selectedImages.value.push({
        file,
        preview: e.target.result,
        uploading: false,
        progress: 0,
        error: false,
        base64: e.target.result
      });
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
    ElMessage.warning(t('community.create.needTitleContent'));
    return;
  }

  if (selectedImages.value.some(img => img.error)) {
    ElMessage.warning(t('community.create.removeFailedImages'));
    return;
  }

  isSubmitting.value = true;

  try {
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
      ElMessage.success(t('community.create.success'));
      closeModal();
      currentPage.value = 1;
      fetchPosts();
    } else {
      ElMessage.error(response.message || t('community.create.failed'));
    }
  } catch (err) {
    console.error('发布帖子失败:', err);
    ElMessage.error(t('community.feed.errorNetwork'));
  } finally {
    isSubmitting.value = false;
  }
};

// 格式化日期
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

// 封面加载失败处理
const handleCoverError = (event) => {
  event.target.style.display = 'none';
  event.target.parentElement?.classList.add('is-broken');
};

// 初始化加载
onMounted(() => {
  fetchPosts();
});
</script>

<style scoped>
.community-content {
  max-width: 1180px;
  margin: 0 auto;
  padding: 32px 24px 64px;
  color: var(--theme-text-1);
}

/* ---------- 页头 ---------- */
.content-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 28px;
}

.eyebrow {
  display: inline-block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.22em;
  text-transform: uppercase;
  color: var(--theme-primary);
}

.title-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 6px;
}

.title-row h2 {
  margin: 0;
  font-size: clamp(26px, 3vw, 34px);
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1.15;
  color: var(--theme-text-1);
}

.post-count {
  padding: 4px 12px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.header-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--theme-text-3);
}

.create-post-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  padding: 11px 22px;
  border: none;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 10px 24px -14px color-mix(in srgb, var(--theme-primary) 80%, transparent);
  transition:
    transform var(--theme-duration) var(--theme-ease),
    box-shadow var(--theme-duration) var(--theme-ease),
    filter var(--theme-duration) var(--theme-ease);
}

.create-post-btn svg {
  width: 16px;
  height: 16px;
}

.create-post-btn:hover {
  transform: translateY(-2px);
  filter: brightness(1.06);
  box-shadow: 0 16px 30px -16px color-mix(in srgb, var(--theme-primary) 90%, transparent);
}

.create-post-btn:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

/* ---------- 帖子网格 ---------- */
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  align-items: stretch;
}

.post-card {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 20px;
  border: 1px solid var(--theme-hairline);
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  cursor: pointer;
  overflow: hidden;
  animation: card-in 0.5s var(--theme-ease) both;
  transition:
    transform var(--theme-duration) var(--theme-ease),
    box-shadow var(--theme-duration) var(--theme-ease),
    border-color var(--theme-duration) var(--theme-ease),
    background-color var(--theme-duration) var(--theme-ease);
}

.post-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: radial-gradient(120% 90% at 100% 0%, var(--theme-primary-soft), transparent 58%);
  opacity: 0;
  pointer-events: none;
  transition: opacity var(--theme-duration) var(--theme-ease);
}

.post-card:hover {
  transform: translateY(-4px);
  border-color: color-mix(in srgb, var(--theme-primary) 38%, transparent);
  box-shadow: var(--theme-shadow-md);
}

.post-card:hover::before {
  opacity: 1;
}

.post-card:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(14px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 封面 */
.post-cover {
  position: relative;
  margin: -20px -20px 18px;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
}

.post-cover img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  transition: transform var(--theme-duration-slow) var(--theme-ease);
}

.post-card:hover .post-cover img {
  transform: scale(1.04);
}

.cover-badge {
  position: absolute;
  right: 12px;
  bottom: 12px;
  padding: 4px 10px;
  border-radius: var(--theme-radius-pill);
  background: color-mix(in srgb, #000 46%, transparent);
  color: #fff;
  font-size: 11px;
  font-weight: 500;
  backdrop-filter: blur(6px);
}

/* 作者行 */
.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.author-avatar {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
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
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-primary-text);
  background: var(--theme-primary);
}

.author-info {
  min-width: 0;
  flex: 1;
}

.author-info h3 {
  margin: 0 0 3px;
  font-size: 14.5px;
  font-weight: 600;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.post-time {
  margin: 0;
  font-size: 12px;
  color: var(--theme-text-3);
}

.card-arrow {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
  color: var(--theme-text-3);
  opacity: 0;
  transform: translate(-4px, 4px);
  transition:
    opacity var(--theme-duration) var(--theme-ease),
    transform var(--theme-duration) var(--theme-ease),
    color var(--theme-duration) var(--theme-ease);
}

.post-card:hover .card-arrow {
  opacity: 1;
  transform: translate(0, 0);
  color: var(--theme-primary);
}

/* 正文 */
.post-content {
  flex: 1;
  margin-bottom: 18px;
}

.post-title {
  margin: 0 0 10px;
  font-size: 18px;
  font-weight: 650;
  line-height: 1.45;
  color: var(--theme-text-1);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: var(--theme-text-2);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}

/* 底部 */
.post-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid var(--theme-hairline);
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--theme-text-3);
}

.stat-item svg {
  width: 15px;
  height: 15px;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  padding: 3px 10px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 11.5px;
  font-weight: 500;
  white-space: nowrap;
}

/* ---------- 骨架屏 ---------- */
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.skeleton-card {
  padding: 20px;
  border: 1px solid var(--theme-hairline);
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
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

.skeleton-author {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.skeleton-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
}

.skeleton-author-lines {
  display: flex;
  flex-direction: column;
  gap: 7px;
  flex: 1;
}

.skeleton-line {
  height: 11px;
  margin-bottom: 10px;
}

.skeleton-title {
  height: 18px;
  margin-bottom: 14px;
}

.w-70 {
  width: 70%;
}

.w-40 {
  width: 40%;
}

.w-24 {
  width: 24%;
}

@keyframes shimmer {
  0% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0 50%;
  }
}

/* ---------- 状态卡片 ---------- */
.state-card {
  padding: 64px 24px;
  text-align: center;
  border: 1px dashed var(--theme-border);
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
}

.state-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
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

.state-card h3 {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.state-card p {
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
  transition: filter var(--theme-duration) var(--theme-ease);
}

.primary-btn:hover {
  filter: brightness(1.06);
}

.primary-btn:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

/* ---------- 模态框 ---------- */
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: color-mix(in srgb, var(--theme-overlay) 88%, transparent);
  backdrop-filter: blur(6px);
}

.modal-content {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 620px;
  max-height: calc(100vh - 48px);
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-xl);
  background: var(--theme-bg-elevated);
  box-shadow: var(--theme-shadow-lg);
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 20px 24px 16px;
  border-bottom: 1px solid var(--theme-hairline);
}

.modal-title-group h3 {
  margin: 4px 0 0;
  font-size: 19px;
  font-weight: 650;
  color: var(--theme-text-1);
}

.close-btn {
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-3);
  cursor: pointer;
  transition:
    background-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease),
    transform var(--theme-duration-fast) var(--theme-ease);
}

.close-btn svg {
  width: 16px;
  height: 16px;
}

.close-btn:hover {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
  transform: rotate(90deg);
}

.modal-body {
  padding: 22px 24px 24px;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.label-row label {
  margin-bottom: 0;
}

.field-counter {
  font-size: 12px;
  color: var(--theme-text-3);
}

.field-hint {
  margin: 6px 0 10px;
  font-size: 12px;
  color: var(--theme-text-3);
  line-height: 1.5;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 11px 14px;
  box-sizing: border-box;
  border: 1.5px solid var(--theme-border);
  border-radius: var(--theme-radius-md);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font: inherit;
  font-size: 14px;
  outline: none;
  transition:
    border-color var(--theme-duration-fast) var(--theme-ease),
    background-color var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: var(--theme-text-3);
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: var(--theme-primary);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-ring);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
  line-height: 1.6;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 26px;
}

.cancel-btn,
.submit-btn {
  padding: 10px 22px;
  border-radius: var(--theme-radius-pill);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition:
    background-color var(--theme-duration-fast) var(--theme-ease),
    filter var(--theme-duration-fast) var(--theme-ease);
}

.cancel-btn {
  border: 1px solid var(--theme-border);
  background: transparent;
  color: var(--theme-text-2);
}

.cancel-btn:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.submit-btn {
  border: none;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-weight: 600;
}

.submit-btn:hover:not(:disabled) {
  filter: brightness(1.06);
}

.submit-btn:disabled {
  cursor: not-allowed;
  opacity: 0.55;
}

/* ---------- 图片上传 ---------- */
.image-upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-preview-list {
  display: contents;
}

.image-preview-item {
  position: relative;
  width: 92px;
  height: 92px;
  border-radius: var(--theme-radius-md);
  overflow: hidden;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface-2);
}

.image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-progress {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 4px;
  background: rgba(0, 0, 0, 0.3);
}

.upload-progress .progress-bar {
  height: 100%;
  background: var(--theme-primary);
  transition: width 0.3s ease;
}

.upload-error {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: color-mix(in srgb, var(--theme-danger) 55%, transparent);
  color: #fff;
  font-size: 12px;
}

.remove-image-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.remove-image-btn svg {
  width: 12px;
  height: 12px;
}

.remove-image-btn:hover:not(:disabled) {
  background: var(--theme-danger);
}

.remove-image-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.upload-trigger {
  width: 92px;
  height: 92px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: 1.5px dashed var(--theme-border-strong);
  border-radius: var(--theme-radius-md);
  background: transparent;
  color: var(--theme-text-3);
  font-size: 11px;
  cursor: pointer;
  transition:
    border-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease),
    background-color var(--theme-duration-fast) var(--theme-ease);
}

.upload-trigger svg {
  width: 22px;
  height: 22px;
}

.upload-trigger:hover {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
}

.file-input-hidden {
  display: none;
}

.upload-error-message {
  margin-top: 10px;
  font-size: 12px;
  color: var(--theme-danger);
}

/* 模态框过渡 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.22s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-content {
  animation: modal-in 0.3s var(--theme-ease);
}

@keyframes modal-in {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.98);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ---------- 响应式 ---------- */
@media (max-width: 768px) {
  .community-content {
    padding: 20px 16px 48px;
  }

  .content-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .create-post-btn {
    width: 100%;
    justify-content: center;
  }

  .posts-grid,
  .skeleton-grid {
    grid-template-columns: 1fr;
  }

  .modal-overlay {
    padding: 12px;
    align-items: flex-end;
  }

  .modal-content {
    max-height: calc(100vh - 24px);
  }
}

@media (prefers-reduced-motion: reduce) {
  .post-card,
  .skeleton-block,
  .modal-enter-active .modal-content {
    animation: none;
  }

  .post-card,
  .create-post-btn,
  .close-btn,
  .post-cover img {
    transition: none;
  }
}
</style>
