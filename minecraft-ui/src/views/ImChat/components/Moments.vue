<template>
  <div class="moments-overlay">
    <!-- 顶部导航栏 -->
    <header class="moments-header">
      <button class="header-btn" title="返回" @click="close">
        <Icon name="chevron-right" :size="'22px'" class="back-icon" />
      </button>
      <h1 class="header-title">朋友圈</h1>
      <button class="header-btn" title="发表" @click="toggleComposer">
        <Icon name="camera" :size="'20px'" />
      </button>
    </header>

    <!-- 封面 + 个人信息 -->
    <section class="moments-cover">
      <div class="cover-mask"></div>
      <div class="cover-user">
        <span class="cover-name">{{ displayName }}</span>
        <img :src="currentUserAvatar || defaultAvatar" :alt="displayName" class="cover-avatar" />
      </div>
    </section>

    <div class="moments-body">
      <!-- 发布区 -->
      <transition name="composer-fade">
        <div v-if="showComposer" class="composer card">
          <textarea
            v-model="composerText"
            class="composer-input"
            rows="3"
            maxlength="500"
            placeholder="这一刻的想法…"
          ></textarea>

          <div v-if="composerImages.length" class="composer-grid">
            <div
              v-for="(img, index) in composerImages"
              :key="index"
              class="composer-thumb"
            >
              <img :src="img" alt="待发布图片" />
              <button class="thumb-remove" title="移除" @click="removeComposerImage(index)">
                <Icon name="close" :size="'12px'" />
              </button>
            </div>
          </div>

          <div class="composer-footer">
            <label class="composer-add" :class="{ disabled: composerImages.length >= 9 }">
              <Icon name="picture" :size="'18px'" />
              <span>{{ composerImages.length }}/9</span>
              <input
                ref="imageInputRef"
                type="file"
                accept="image/*"
                multiple
                :disabled="composerImages.length >= 9"
                @change="handleComposerImages"
              />
            </label>
            <div class="composer-right">
              <input
                v-model="composerLocation"
                class="composer-location"
                type="text"
                maxlength="50"
                placeholder="所在位置（选填）"
              />
              <button
                class="publish-btn"
                :disabled="publishing || (!composerText.trim() && composerImages.length === 0)"
                @click="handlePublish"
              >
                {{ publishing ? '发布中…' : '发表' }}
              </button>
            </div>
          </div>
        </div>
      </transition>

      <!-- 动态信息流 -->
      <div v-if="loading && !moments.length" class="state-tip">加载中…</div>

      <div v-else-if="!moments.length" class="state-tip">
        <Icon name="camera" :size="'40px'" />
        <p>还没有动态，发布第一条朋友圈吧</p>
      </div>

      <template v-else>
        <article v-for="moment in moments" :key="moment.id" class="moment card">
          <img :src="moment.avatar || defaultAvatar" :alt="moment.nickname" class="moment-avatar" />

          <div class="moment-main">
            <h3 class="moment-name">{{ moment.nickname || moment.username || '用户' }}</h3>
            <p v-if="moment.content" class="moment-content">{{ moment.content }}</p>

            <!-- 九宫格图片 -->
            <div v-if="moment.images && moment.images.length" :class="['moment-images', imageGridClass(moment.images.length)]">
              <img
                v-for="(img, index) in moment.images"
                :key="index"
                :src="img"
                alt="动态图片"
                class="moment-image"
                @click="openPreview(moment.images, index)"
              />
            </div>

            <div class="moment-meta">
              <span v-if="moment.location" class="moment-location">
                <Icon name="map-pin" :size="'13px'" />
                {{ moment.location }}
              </span>
              <span class="moment-time">{{ formatTime(moment.createTime) }}</span>
              <button class="moment-action" @click="toggleActionPanel(moment.id)">
                <Icon name="more" :size="'16px'" />
              </button>
            </div>

            <!-- 操作气泡 -->
            <transition name="pop">
              <div v-if="activeMomentId === moment.id" class="action-pop" @click.stop>
                <button v-if="isMine(moment)" class="action-item danger" @click="handleDeleteMoment(moment)">
                  删除
                </button>
                <button class="action-item" @click="startComment(moment)">
                  {{ isMine(moment) ? '评论' : '评论' }}
                </button>
              </div>
            </transition>

            <!-- 点赞 / 评论区 -->
            <div class="interaction">
              <div class="interaction-actions">
                <button
                  :class="['like-btn', { active: moment.liked }]"
                  @click="handleLike(moment)"
                >
                  <svg class="like-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path
                      d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
                    />
                  </svg>
                  <span>{{ moment.likeCount || 0 }}</span>
                </button>
                <button class="comment-btn" @click="startComment(moment)">
                  <Icon name="message" :size="'15px'" />
                  <span>{{ moment.commentCount || 0 }}</span>
                </button>
              </div>

              <div v-if="(moment.likes && moment.likes.length) || (moment.comments && moment.comments.length)" class="interaction-panel">
                <div v-if="moment.likes && moment.likes.length" class="like-list">
                  <svg class="like-icon small" viewBox="0 0 24 24" width="13" height="13">
                    <path
                      d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
                    />
                  </svg>
                  <span class="like-names">
                    {{ moment.likes.map(u => u.nickname || u.username).join('、') }}
                  </span>
                </div>

                <div v-if="moment.comments && moment.comments.length" class="comment-list">
                  <div
                    v-for="comment in moment.comments"
                    :key="comment.id"
                    class="comment-item"
                  >
                    <span
                      class="comment-user"
                      @click="startComment(moment, { userId: comment.userId, nickname: comment.nickname || comment.username })"
                    >{{ comment.nickname || comment.username }}</span>
                    <template v-if="comment.replyToNickname">
                      <span class="comment-reply">回复</span>
                      <span
                        class="comment-user"
                        @click="startComment(moment, { userId: comment.replyToUserId, nickname: comment.replyToNickname })"
                      >{{ comment.replyToNickname }}</span>
                    </template>
                    <span class="comment-sep">：</span>
                    <span class="comment-text">{{ comment.content }}</span>
                    <button
                      v-if="Number(comment.userId) === Number(currentUserId)"
                      class="comment-delete"
                      @click="handleDeleteComment(moment, comment)"
                    >
                      删除
                    </button>
                  </div>
                </div>
              </div>

              <!-- 评论输入 -->
              <div v-if="commentingMomentId === moment.id" class="comment-input-row">
                <input
                  v-model="commentText"
                  class="comment-input"
                  type="text"
                  maxlength="200"
                  :placeholder="replyTarget ? `回复 ${replyTarget.nickname}：` : '评论…'"
                  @keyup.enter="submitComment(moment)"
                />
                <button class="comment-send" :disabled="!commentText.trim()" @click="submitComment(moment)">
                  发送
                </button>
              </div>
            </div>
          </div>
        </article>

        <div class="load-more">
          <button v-if="hasMore" class="load-more-btn" :disabled="loading" @click="loadMore">
            {{ loading ? '加载中…' : '加载更多' }}
          </button>
          <span v-else class="load-more-end">没有更多了</span>
        </div>
      </template>
    </div>

    <!-- 图片预览 -->
    <div v-if="preview.visible" class="preview-overlay" @click="preview.visible = false">
      <img :src="preview.images[preview.index]" alt="预览图片" class="preview-image" @click.stop />
      <button class="preview-close" @click="preview.visible = false">
        <Icon name="close" :size="'22px'" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Icon from './Icon.vue'
import { uploadFile } from '@/api/upload'
import {
  publishMoment,
  getMomentFeed,
  deleteMoment,
  toggleMomentLike,
  getMomentLikes,
  addMomentComment,
  deleteMomentComment
} from '@/api/moment'

const props = defineProps({
  currentUserId: {
    type: [Number, String],
    default: null
  },
  currentUserName: {
    type: String,
    default: '用户'
  },
  currentUserAvatar: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close'])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const moments = ref([])
const loading = ref(false)
const publishing = ref(false)
const page = ref(1)
const size = 10
const hasMore = ref(true)

const showComposer = ref(false)
const composerText = ref('')
const composerLocation = ref('')
const composerImages = ref([])
const imageInputRef = ref(null)

const activeMomentId = ref(null)
const commentingMomentId = ref(null)
const commentText = ref('')
const replyTarget = ref(null)

const preview = reactive({
  visible: false,
  images: [],
  index: 0
})

const displayName = computed(() => props.currentUserName || '用户')

const isMine = (moment) => Number(moment.userId) === Number(props.currentUserId)

const loadFeed = async (reset = false) => {
  if (loading.value) return
  if (reset) {
    page.value = 1
    hasMore.value = true
  }
  loading.value = true
  try {
    const response = await getMomentFeed(props.currentUserId, page.value, size)
    if (response.code === 200) {
      const list = response.data || []
      moments.value = page.value === 1 ? list : [...moments.value, ...list]
      hasMore.value = list.length >= size
    } else {
      ElMessage.error(response.message || '加载朋友圈失败')
    }
  } catch (error) {
    console.error('[Moments] 加载朋友圈失败:', error)
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (loading.value || !hasMore.value) return
  page.value += 1
  await loadFeed(false)
}

const toggleComposer = () => {
  showComposer.value = !showComposer.value
}

const handleComposerImages = async (event) => {
  const files = Array.from(event.target.files || [])
  if (!files.length) return

  const remaining = 9 - composerImages.value.length
  const selected = files.slice(0, remaining)

  for (const file of selected) {
    try {
      const response = await uploadFile(file)
      if (response.code === 200) {
        composerImages.value.push(response.data)
      } else {
        ElMessage.error(response.message || '图片上传失败')
      }
    } catch (error) {
      console.error('[Moments] 图片上传失败:', error)
    }
  }

  event.target.value = ''
}

const removeComposerImage = (index) => {
  composerImages.value.splice(index, 1)
}

const resetComposer = () => {
  composerText.value = ''
  composerLocation.value = ''
  composerImages.value = []
}

const handlePublish = async () => {
  const content = composerText.value.trim()
  if (!content && composerImages.value.length === 0) return

  publishing.value = true
  try {
    const response = await publishMoment({
      userId: props.currentUserId,
      content,
      images: [...composerImages.value],
      location: composerLocation.value.trim() || null,
      visibility: 0
    })
    if (response.code === 200) {
      moments.value.unshift(response.data)
      resetComposer()
      showComposer.value = false
      ElMessage.success('发布成功')
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    console.error('[Moments] 发布失败:', error)
    ElMessage.error('发布失败')
  } finally {
    publishing.value = false
  }
}

const toggleActionPanel = (momentId) => {
  activeMomentId.value = activeMomentId.value === momentId ? null : momentId
}

const handleLike = async (moment) => {
  try {
    const response = await toggleMomentLike(moment.id, props.currentUserId)
    if (response.code === 200) {
      moment.liked = response.data.liked
      moment.likeCount = response.data.likeCount
      const likesResponse = await getMomentLikes(moment.id)
      if (likesResponse.code === 200) {
        moment.likes = likesResponse.data || []
      }
    }
  } catch (error) {
    console.error('[Moments] 点赞失败:', error)
  }
}

const startComment = (moment, target = null) => {
  activeMomentId.value = null
  commentingMomentId.value = moment.id
  replyTarget.value = target
  commentText.value = ''
}

const submitComment = async (moment) => {
  const content = commentText.value.trim()
  if (!content) return
  try {
    const response = await addMomentComment(moment.id, {
      userId: props.currentUserId,
      content,
      replyToUserId: replyTarget.value ? replyTarget.value.userId : null
    })
    if (response.code === 200) {
      if (!moment.comments) moment.comments = []
      moment.comments.push(response.data)
      moment.commentCount = (moment.commentCount || 0) + 1
      commentText.value = ''
      replyTarget.value = null
      commentingMomentId.value = null
    } else {
      ElMessage.error(response.message || '评论失败')
    }
  } catch (error) {
    console.error('[Moments] 评论失败:', error)
  }
}

const handleDeleteComment = async (moment, comment) => {
  try {
    const response = await deleteMomentComment(comment.id, props.currentUserId)
    if (response.code === 200) {
      moment.comments = moment.comments.filter(c => c.id !== comment.id)
      moment.commentCount = Math.max(0, (moment.commentCount || 1) - 1)
      ElMessage.success('评论已删除')
    }
  } catch (error) {
    console.error('[Moments] 删除评论失败:', error)
  }
}

const handleDeleteMoment = async (moment) => {
  try {
    await ElMessageBox.confirm('确定删除这条动态吗？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    const response = await deleteMoment(moment.id, props.currentUserId)
    if (response.code === 200) {
      moments.value = moments.value.filter(m => m.id !== moment.id)
      ElMessage.success('删除成功')
    }
  } catch (error) {
    console.error('[Moments] 删除动态失败:', error)
  }
  activeMomentId.value = null
}

const imageGridClass = (count) => {
  if (count === 1) return 'grid-one'
  if (count === 2 || count === 4) return 'grid-two'
  return 'grid-three'
}

const openPreview = (images, index) => {
  preview.images = images
  preview.index = index
  preview.visible = true
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (Number.isNaN(date.getTime())) return ''
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 172800000) return '昨天'
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

const close = () => {
  emit('close')
}

const handleGlobalClick = () => {
  activeMomentId.value = null
}

onMounted(() => {
  loadFeed(true)
  document.addEventListener('click', handleGlobalClick)
})

onUnmounted(() => {
  document.removeEventListener('click', handleGlobalClick)
})
</script>

<style scoped>
.moments-overlay {
  position: fixed;
  inset: 0;
  z-index: 1200;
  background: #f5f5f7;
  overflow-y: auto;
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  color: #1d1d1f;
  -webkit-font-smoothing: antialiased;
}

.moments-overlay::-webkit-scrollbar {
  width: 6px;
}

.moments-overlay::-webkit-scrollbar-thumb {
  background: #d2d2d6;
  border-radius: 3px;
}

/* 顶部栏 */
.moments-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 52px;
  padding: 0 12px;
  background: rgba(245, 245, 247, 0.82);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.header-title {
  font-size: 17px;
  font-weight: 600;
  margin: 0;
  letter-spacing: -0.01em;
}

.header-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  color: #1d1d1f;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s ease;
}

.header-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}

.back-icon {
  transform: rotate(180deg);
}

/* 封面 */
.moments-cover {
  position: relative;
  height: 240px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  overflow: visible;
}

.cover-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.15) 0%, rgba(0, 0, 0, 0.45) 100%);
}

.cover-user {
  position: absolute;
  right: 20px;
  bottom: -18px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.cover-name {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.4);
}

.cover-avatar {
  width: 68px;
  height: 68px;
  border-radius: 10px;
  border: 3px solid #ffffff;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 主体 */
.moments-body {
  max-width: 680px;
  margin: 0 auto;
  padding: 40px 16px 60px;
}

.card {
  background: #ffffff;
  border-radius: 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

/* 发布区 */
.composer {
  padding: 16px;
  margin-bottom: 16px;
}

.composer-input {
  width: 100%;
  border: none;
  resize: none;
  font-size: 15px;
  line-height: 1.6;
  color: #1d1d1f;
  outline: none;
  background: transparent;
  font-family: inherit;
  box-sizing: border-box;
}

.composer-input::placeholder {
  color: #a1a1a6;
}

.composer-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-top: 12px;
}

.composer-thumb {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
}

.composer-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-remove {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.composer-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid #f0f0f2;
}

.composer-add {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 10px;
  background: #f5f5f7;
  color: #2997ff;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s ease;
}

.composer-add:hover {
  background: #e8e8ed;
}

.composer-add.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.composer-add input {
  display: none;
}

.composer-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  justify-content: flex-end;
}

.composer-location {
  flex: 1;
  max-width: 220px;
  padding: 8px 12px;
  border: 1px solid #e5e5ea;
  border-radius: 10px;
  font-size: 13px;
  outline: none;
  font-family: inherit;
  color: #1d1d1f;
  transition: border-color 0.2s ease;
}

.composer-location:focus {
  border-color: #2997ff;
}

.publish-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 999px;
  background: #2997ff;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s ease;
  white-space: nowrap;
}

.publish-btn:hover:not(:disabled) {
  background: #0066cc;
}

.publish-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 状态提示 */
.state-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: #a1a1a6;
  font-size: 14px;
}

.state-tip p {
  margin: 0;
}

/* 动态卡片 */
.moment {
  display: flex;
  gap: 12px;
  padding: 16px;
  margin-bottom: 12px;
}

.moment-avatar {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}

.moment-main {
  flex: 1;
  min-width: 0;
}

.moment-name {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
}

.moment-content {
  margin: 0 0 10px;
  font-size: 15px;
  line-height: 1.6;
  color: #1d1d1f;
  white-space: pre-wrap;
  word-break: break-word;
}

.moment-images {
  display: grid;
  gap: 4px;
  margin-bottom: 10px;
  max-width: 420px;
}

.moment-images.grid-one {
  grid-template-columns: 1fr;
  max-width: 240px;
}

.moment-images.grid-two {
  grid-template-columns: repeat(2, 1fr);
  max-width: 300px;
}

.moment-images.grid-three {
  grid-template-columns: repeat(3, 1fr);
  max-width: 420px;
}

.moment-image {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: 6px;
  cursor: zoom-in;
  background: #f5f5f7;
  transition: opacity 0.2s ease;
}

.moment-image:hover {
  opacity: 0.9;
}

.moment-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #a1a1a6;
  font-size: 12px;
}

.moment-location {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: #2997ff;
}

.moment-action {
  margin-left: auto;
  border: none;
  background: transparent;
  color: #a1a1a6;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 6px;
  transition: background 0.2s ease;
}

.moment-action:hover {
  background: #f5f5f7;
  color: #1d1d1f;
}

/* 操作气泡 */
.action-pop {
  position: relative;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 6px;
}

.action-item {
  padding: 5px 14px;
  border: none;
  border-radius: 8px;
  background: #f5f5f7;
  color: #1d1d1f;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.action-item:hover {
  background: #e8e8ed;
}

.action-item.danger {
  color: #ff3b30;
}

/* 互动区 */
.interaction {
  margin-top: 10px;
}

.interaction-actions {
  display: flex;
  gap: 16px;
}

.like-btn,
.comment-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: transparent;
  color: #6e6e73;
  font-size: 13px;
  cursor: pointer;
  padding: 2px 0;
  transition: color 0.2s ease;
}

.like-btn:hover,
.comment-btn:hover {
  color: #2997ff;
}

.like-btn.active {
  color: #ff3b30;
}

.like-icon {
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linejoin: round;
}

.like-btn.active .like-icon {
  fill: currentColor;
}

.like-icon.small {
  color: #ff3b30;
  fill: currentColor;
  stroke: none;
  flex-shrink: 0;
}

.interaction-panel {
  margin-top: 8px;
  background: #f5f5f7;
  border-radius: 10px;
  padding: 8px 12px;
}

.like-list {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding-bottom: 6px;
}

.like-names {
  font-size: 13px;
  color: #2997ff;
  line-height: 1.5;
  word-break: break-word;
}

.comment-list {
  border-top: 1px solid #e5e5ea;
  padding-top: 6px;
}

.like-list + .comment-list {
  border-top: 1px solid #e5e5ea;
}

.comment-item {
  font-size: 13px;
  line-height: 1.6;
  color: #1d1d1f;
  word-break: break-word;
}

.comment-user {
  color: #2997ff;
  font-weight: 500;
}

.comment-reply,
.comment-sep {
  color: #6e6e73;
  margin: 0 2px;
}

.comment-text {
  color: #1d1d1f;
}

.comment-delete {
  border: none;
  background: transparent;
  color: #a1a1a6;
  font-size: 12px;
  cursor: pointer;
  margin-left: 6px;
}

.comment-delete:hover {
  color: #ff3b30;
}

.comment-input-row {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.comment-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e5ea;
  border-radius: 999px;
  font-size: 13px;
  outline: none;
  font-family: inherit;
  color: #1d1d1f;
  transition: border-color 0.2s ease;
}

.comment-input:focus {
  border-color: #2997ff;
}

.comment-send {
  padding: 0 16px;
  border: none;
  border-radius: 999px;
  background: #2997ff;
  color: #ffffff;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.comment-send:hover:not(:disabled) {
  background: #0066cc;
}

.comment-send:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 12px 0 24px;
}

.load-more-btn {
  padding: 9px 28px;
  border: none;
  border-radius: 999px;
  background: #ffffff;
  color: #2997ff;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: background 0.2s ease;
}

.load-more-btn:hover:not(:disabled) {
  background: #f0f7ff;
}

.load-more-end {
  color: #a1a1a6;
  font-size: 13px;
}

/* 图片预览 */
.preview-overlay {
  position: fixed;
  inset: 0;
  z-index: 1300;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  max-width: 90vw;
  max-height: 88vh;
  object-fit: contain;
  border-radius: 6px;
}

.preview-close {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

/* 过渡动画 */
.composer-fade-enter-active,
.composer-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.composer-fade-enter-from,
.composer-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.pop-enter-active,
.pop-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.pop-enter-from,
.pop-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@media (max-width: 768px) {
  .moments-cover {
    height: 180px;
  }

  .cover-avatar {
    width: 56px;
    height: 56px;
  }

  .moment-images.grid-three {
    max-width: 100%;
  }
}
</style>
