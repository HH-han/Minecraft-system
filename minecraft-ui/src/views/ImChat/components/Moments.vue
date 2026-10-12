<template>
  <div class="moments-pane">
    <!-- 封面 -->
    <section class="moments-cover" title="点击更换封面" @click="selectCover">
      <div class="cover-bg" :style="coverStyle"></div>
      <div class="cover-shade"></div>

      <!-- 悬浮工具按钮 -->
      <div class="cover-toolbar">
        <button class="tool-btn" title="返回" @click.stop="close">
          <Icon name="chevron-right" :size="'20px'" class="back-icon" />
        </button>
        <div class="cover-tools">
          <button class="tool-btn" title="刷新" @click.stop="loadFeed(true)">
            <Icon name="refresh-cw" :size="'18px'" />
          </button>
          <button class="tool-btn" title="消息" @click.stop="handleNotice">
            <Icon name="bell" :size="'18px'" />
          </button>
          <button class="tool-btn" title="发表" @click.stop="openComposer">
            <Icon name="camera" :size="'18px'" />
          </button>
        </div>
      </div>

      <div class="cover-hint">
        <Icon name="camera" :size="'16px'" />
        <span>点击更换封面</span>
      </div>

      <!-- 昵称 + 头像 -->
      <div class="cover-user" @click.stop>
        <span class="cover-name">{{ displayName }}</span>
        <img :src="currentUserAvatar || defaultAvatar" :alt="displayName" class="cover-avatar" />
      </div>
    </section>

    <input
      ref="coverInputRef"
      type="file"
      accept="image/*"
      class="cover-input"
      @change="handleCoverSelect"
    />

    <!-- 动态信息流 -->
    <div class="moments-body">
      <div v-if="loading && !moments.length" class="state-tip">加载中…</div>

      <div v-else-if="!moments.length" class="state-tip">
        <Icon name="camera" :size="'40px'" />
        <p>还没有动态，点击右上角相机发布第一条朋友圈吧</p>
      </div>

      <template v-else>
        <article v-for="moment in moments" :key="moment.id" class="moment">
          <img :src="moment.avatar || defaultAvatar" :alt="moment.nickname" class="moment-avatar" />

          <div class="moment-main">
            <h3 class="moment-name">{{ moment.nickname || moment.username || '用户' }}</h3>
            <p v-if="moment.content" class="moment-content">{{ moment.content }}</p>

            <!-- 九宫格图片 -->
            <div
              v-if="moment.images && moment.images.length"
              :class="['moment-images', imageGridClass(moment.images.length)]"
            >
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
              <button class="moment-action" title="赞和评论" @click.stop="toggleActionPanel(moment.id)">
                <Icon name="more" :size="'16px'" />
              </button>
            </div>

            <!-- 赞 / 评论 操作气泡 -->
            <transition name="pop">
              <div v-if="activeMomentId === moment.id" class="action-pop" @click.stop>
                <button class="action-item" @click="handleLike(moment)">
                  <svg class="like-icon" viewBox="0 0 24 24" width="15" height="15">
                    <path
                      d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
                    />
                  </svg>
                  {{ moment.liked ? '取消' : '赞' }}
                </button>
                <button class="action-item" @click="startComment(moment)">
                  <Icon name="message" :size="'15px'" />
                  评论
                </button>
                <button v-if="isMine(moment)" class="action-item danger" @click="handleDeleteMoment(moment)">
                  <Icon name="trash" :size="'15px'" />
                  删除
                </button>
              </div>
            </transition>

            <!-- 点赞 / 评论区 -->
            <div v-if="(moment.likes && moment.likes.length) || (moment.comments && moment.comments.length)" class="interaction-panel">
              <div v-if="moment.likes && moment.likes.length" class="like-list">
                <svg class="like-icon filled" viewBox="0 0 24 24" width="13" height="13">
                  <path
                    d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
                  />
                </svg>
                <span class="like-names">
                  {{ moment.likes.map(u => u.nickname || u.username).join('，') }}
                </span>
              </div>

              <div v-if="moment.comments && moment.comments.length" class="comment-list">
                <div v-for="comment in moment.comments" :key="comment.id" class="comment-item">
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

            <!-- 评论输入（微信风格） -->
            <div v-if="commentingMomentId === moment.id" class="comment-editor" @click.stop>
              <textarea
                ref="commentInputRef"
                v-model="commentText"
                class="comment-textarea"
                rows="2"
                maxlength="200"
                :placeholder="replyTarget ? `回复 ${replyTarget.nickname}：` : '评论'"
                @keydown.enter.exact.prevent="submitComment(moment)"
              ></textarea>

              <div class="comment-toolbar">
                <button class="tool-btn" type="button" title="语音" @click="handleCommentVoice">
                  <Icon name="mic" :size="'19px'" />
                </button>
                <button class="tool-btn" type="button" title="表情" @click.stop="toggleCommentEmoji">
                  <Icon name="smile" :size="'19px'" />
                </button>
                <button class="tool-btn" type="button" title="图片" @click="handleCommentImage">
                  <Icon name="image" :size="'19px'" />
                </button>
                <button
                  class="comment-send"
                  :class="{ 'is-active': !!commentText.trim() }"
                  :disabled="!commentText.trim()"
                  @click="submitComment(moment)"
                >
                  发送
                </button>
              </div>

              <transition name="pop">
                <div v-if="showCommentEmoji" class="comment-emoji-panel" @click.stop>
                  <button
                    v-for="emoji in emojiList"
                    :key="emoji"
                    class="emoji-item"
                    type="button"
                    @click="insertCommentEmoji(emoji)"
                  >{{ emoji }}</button>
                </div>
              </transition>
            </div>
          </div>
        </article>

        <div class="load-more">
          <button v-if="hasMore" class="load-more-btn" :disabled="loading" @click="loadMore">
            {{ loading ? '加载中…' : '加载更多' }}
          </button>
          <span v-else class="load-more-end">— 没有更多了 —</span>
        </div>
      </template>
    </div>

    <!-- 发表窗口（参考微信发表弹窗） -->
    <transition name="fade">
      <div v-if="showComposer" class="publish-overlay" @click.self="closeComposer">
        <div class="publish-window">
          <!-- 想法输入 + 表情 -->
          <div class="publish-compose">
            <textarea
              v-model="composerText"
              class="publish-input"
              rows="5"
              maxlength="500"
              placeholder="这一刻的想法…"
            ></textarea>

            <div class="publish-emoji-wrap">
              <button class="emoji-btn" type="button" title="表情" @click.stop="toggleEmoji">
                <Icon name="smile" :size="'24px'" />
              </button>
              <transition name="pop">
                <div v-if="showEmoji" class="emoji-panel" @click.stop>
                  <button
                    v-for="emoji in emojiList"
                    :key="emoji"
                    class="emoji-item"
                    type="button"
                    @click="insertEmoji(emoji)"
                  >{{ emoji }}</button>
                </div>
              </transition>
            </div>
          </div>

          <!-- 图片九宫格 -->
          <div class="publish-grid">
            <div v-for="(img, index) in composerImages" :key="index" class="publish-thumb">
              <img :src="img" alt="待发布图片" />
              <button class="thumb-remove" title="移除" @click="removeComposerImage(index)">
                <Icon name="close" :size="'12px'" />
              </button>
            </div>
            <label v-if="composerImages.length < 9" class="publish-add">
              <Icon name="plus" :size="'26px'" />
              <input
                ref="imageInputRef"
                type="file"
                accept="image/*"
                multiple
                @change="handleComposerImages"
              />
            </label>
          </div>

          <!-- 选项行 -->
          <div class="publish-options">
            <div class="option-row">
              <Icon name="map-pin" :size="'18px'" class="option-icon" />
              <span class="option-label">所在位置</span>
              <input
                v-model="composerLocation"
                class="option-input"
                type="text"
                maxlength="50"
                placeholder="不显示"
              />
            </div>

            <button class="option-row option-clickable" type="button" @click="handleRemind">
              <Icon name="at-sign" :size="'18px'" class="option-icon" />
              <span class="option-label">提醒谁看</span>
              <Icon name="chevron-right" :size="'16px'" class="option-arrow" />
            </button>

            <div class="option-row option-clickable" @click.stop="toggleVisibilityMenu">
              <Icon name="user" :size="'18px'" class="option-icon" />
              <span class="option-label">谁可以看</span>
              <span class="option-value">{{ visibilityLabel }}</span>
              <Icon name="chevron-right" :size="'16px'" class="option-arrow" />
              <transition name="pop">
                <div v-if="showVisibility" class="visibility-menu">
                  <button
                    v-for="opt in visibilityOptions"
                    :key="opt.value"
                    class="visibility-item"
                    :class="{ active: visibility === opt.value }"
                    type="button"
                    @click.stop="selectVisibility(opt.value)"
                  >
                    <span>{{ opt.label }}</span>
                    <Icon v-if="visibility === opt.value" name="check" :size="'15px'" />
                  </button>
                </div>
              </transition>
            </div>
          </div>

          <!-- 底部按钮 -->
          <div class="publish-actions">
            <button
              class="publish-submit"
              :disabled="publishing || (!composerText.trim() && composerImages.length === 0)"
              @click="handlePublish"
            >
              {{ publishing ? '发布中…' : '发表' }}
            </button>
            <button class="publish-cancel" @click="closeComposer">取消</button>
          </div>
        </div>
      </div>
    </transition>

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
import { ref, computed, onMounted, onUnmounted, reactive, nextTick } from 'vue'
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
  deleteMomentComment,
  uploadMomentCover,
  getMomentCover
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
const showEmoji = ref(false)
const showVisibility = ref(false)
const visibility = ref(0)

const emojiList = [
  '😀', '😄', '😁', '😆', '😅', '🤣', '😊', '😍',
  '😘', '😜', '🤔', '😎', '😭', '😡', '🥰', '😴',
  '👍', '👏', '🙏', '🎉', '❤️', '🔥', '🌹', '⭐',
  '💪', '🐶', '🐱', '🌸', '🌈', '✨', '🍰', '☕'
]

const visibilityOptions = [
  { label: '公开', value: 0 },
  { label: '仅好友', value: 1 },
  { label: '私密', value: 2 }
]

const coverUrl = ref('')
const coverInputRef = ref(null)

const activeMomentId = ref(null)
const commentingMomentId = ref(null)
const commentText = ref('')
const replyTarget = ref(null)
const showCommentEmoji = ref(false)
const commentInputRef = ref(null)

const preview = reactive({
  visible: false,
  images: [],
  index: 0
})

const displayName = computed(() => props.currentUserName || '用户')

const visibilityLabel = computed(
  () => visibilityOptions.find(opt => opt.value === visibility.value)?.label || '公开'
)

const coverStyle = computed(() => {
  if (coverUrl.value) {
    return { backgroundImage: `url("${coverUrl.value}")` }
  }
  return {}
})

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

const handleNotice = () => {
  ElMessage.info('暂无新的朋友圈消息')
}

const loadCover = async () => {
  if (!props.currentUserId) return
  try {
    const response = await getMomentCover(props.currentUserId)
    if (response.code === 200 && response.data) {
      coverUrl.value = response.data
    }
  } catch (error) {
    console.error('[Moments] 获取封面失败:', error)
  }
}

const selectCover = () => {
  coverInputRef.value?.click()
}

const handleCoverSelect = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  try {
    const response = await uploadMomentCover(props.currentUserId, file)
    if (response.code === 200) {
      coverUrl.value = response.data
      ElMessage.success('封面已更新')
    } else {
      ElMessage.error(response.message || '封面上传失败')
    }
  } catch (error) {
    console.error('[Moments] 封面上传失败:', error)
  }

  event.target.value = ''
}

const openComposer = () => {
  showEmoji.value = false
  showVisibility.value = false
  showComposer.value = true
}

const closeComposer = () => {
  showEmoji.value = false
  showVisibility.value = false
  showComposer.value = false
}

const toggleEmoji = () => {
  showEmoji.value = !showEmoji.value
  if (showEmoji.value) showVisibility.value = false
}

const insertEmoji = (emoji) => {
  composerText.value += emoji
}

const toggleVisibilityMenu = () => {
  showVisibility.value = !showVisibility.value
  if (showVisibility.value) showEmoji.value = false
}

const selectVisibility = (value) => {
  visibility.value = value
  showVisibility.value = false
}

const handleRemind = () => {
  ElMessage.info('“提醒谁看”功能暂未开放')
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
  visibility.value = 0
  showEmoji.value = false
  showVisibility.value = false
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
      visibility: visibility.value
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
  activeMomentId.value = null
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
  showCommentEmoji.value = false
  nextTick(() => {
    const el = commentInputRef.value
    const target = Array.isArray(el) ? el[0] : el
    target?.focus?.()
  })
}

const toggleCommentEmoji = () => {
  showCommentEmoji.value = !showCommentEmoji.value
}

const insertCommentEmoji = (emoji) => {
  if (commentText.value.length + emoji.length > 200) return
  commentText.value += emoji
}

const handleCommentVoice = () => {
  ElMessage.info('语音评论功能暂未开放')
}

const handleCommentImage = () => {
  ElMessage.info('图片评论功能暂未开放')
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
      showCommentEmoji.value = false
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
  activeMomentId.value = null
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
  showEmoji.value = false
  showVisibility.value = false
}

onMounted(() => {
  loadFeed(true)
  loadCover()
  document.addEventListener('click', handleGlobalClick)
})

onUnmounted(() => {
  document.removeEventListener('click', handleGlobalClick)
})
</script>

<style scoped>
.moments-pane {
  flex: 1;
  min-width: 0;
  height: 100%;
  background: var(--theme-bg-surface);
  overflow-y: auto;
  position: relative;
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  color: var(--theme-text-1);
  -webkit-font-smoothing: antialiased;
}

.moments-pane::-webkit-scrollbar {
  width: 6px;
}

.moments-pane::-webkit-scrollbar-thumb {
  background: var(--theme-scrollbar-thumb);
  border-radius: 3px;
}

/* 封面 */
.moments-cover {
  position: relative;
  height: 320px;
  overflow: visible;
  cursor: pointer;
}

.cover-bg {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.14);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.cover-shade {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.28) 0%, rgba(0, 0, 0, 0.05) 45%, rgba(0, 0, 0, 0.3) 100%);
}

/* 更换封面提示 */
.cover-hint {
  position: absolute;
  left: 22px;
  bottom: 18px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.28);
  color: #ffffff;
  font-size: 13px;
  opacity: 0;
  transform: translateY(4px);
  transition: opacity 0.2s ease, transform 0.2s ease;
  z-index: 2;
  pointer-events: none;
}

.moments-cover:hover .cover-hint {
  opacity: 1;
  transform: translateY(0);
}

.cover-input {
  display: none;
}

.cover-toolbar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  z-index: 2;
}

.cover-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tool-btn {
  width: 38px;
  height: 38px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.22);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  backdrop-filter: blur(6px);
  transition: background 0.2s ease, transform 0.2s ease;
}

.tool-btn:hover {
  background: rgba(255, 255, 255, 0.38);
  transform: scale(1.05);
}

.back-icon {
  transform: rotate(180deg);
}

.cover-user {
  position: absolute;
  right: 22px;
  bottom: -20px;
  display: flex;
  align-items: center;
  gap: 14px;
  z-index: 2;
}

.cover-name {
  color: #ffffff;
  font-size: 17px;
  font-weight: 600;
  text-shadow: 0 1px 6px rgba(0, 0, 0, 0.45);
}

.cover-avatar {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  border: 3px solid #ffffff;
  object-fit: cover;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.18);
  background: #ffffff;
}

/* 主体 */
.moments-body {
  max-width: 720px;
  margin: 0 auto;
  padding: 26px 24px 70px;
  min-height: 300px;
}

.state-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 70px 0;
  color: var(--theme-text-3);
  font-size: 14px;
}

.state-tip p {
  margin: 0;
}

/* 动态条目 */
.moment {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.moment-avatar {
  width: 44px;
  height: 44px;
  border-radius: 6px;
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
  color: var(--theme-primary);
}

.moment-content {
  margin: 0 0 10px;
  font-size: 15px;
  line-height: 1.6;
  color: var(--theme-text-1);
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
  border-radius: 4px;
  cursor: zoom-in;
  background: var(--theme-bg-surface-2);
  transition: opacity 0.2s ease;
}

.moment-image:hover {
  opacity: 0.9;
}

.moment-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--theme-text-3);
  font-size: 12px;
}

.moment-location {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: var(--theme-primary);
}

.moment-action {
  margin-left: auto;
  border: none;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  cursor: pointer;
  padding: 3px 10px;
  border-radius: 6px;
  transition: background 0.2s ease, color 0.2s ease;
}

.moment-action:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

/* 操作气泡 */
.action-pop {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.action-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 14px;
  border: none;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.action-item:hover {
  background: var(--theme-bg-hover);
}

.action-item.danger {
  color: var(--theme-danger);
}

/* 互动面板 */
.interaction-panel {
  margin-top: 8px;
  background: var(--theme-bg-surface-2);
  border-radius: 8px;
  padding: 8px 12px;
}

.like-list {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding-bottom: 6px;
}

.like-icon {
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linejoin: round;
  flex-shrink: 0;
}

.like-icon.filled {
  fill: var(--theme-primary);
  stroke: var(--theme-primary);
  margin-top: 2px;
}

.like-names {
  font-size: 13px;
  color: var(--theme-primary);
  line-height: 1.5;
  word-break: break-word;
}

.comment-list {
  border-top: 1px solid var(--theme-border);
  padding-top: 6px;
}

.comment-item {
  font-size: 13px;
  line-height: 1.6;
  color: var(--theme-text-1);
  word-break: break-word;
}

.comment-user {
  color: var(--theme-primary);
  font-weight: 500;
  cursor: pointer;
}

.comment-reply,
.comment-sep {
  color: var(--theme-text-2);
  margin: 0 2px;
}

.comment-text {
  color: var(--theme-text-1);
}

.comment-delete {
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  font-size: 12px;
  cursor: pointer;
  margin-left: 6px;
}

.comment-delete:hover {
  color: var(--theme-danger);
}

.comment-editor {
  position: relative;
  margin-top: 8px;
  padding: 10px 12px 8px;
  border: 1px solid var(--theme-border);
  border-radius: 8px;
  background: var(--theme-bg-surface);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.comment-editor:focus-within {
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.comment-textarea {
  box-sizing: border-box;
  width: 100%;
  min-height: 46px;
  max-height: 140px;
  padding: 0;
  border: none;
  font-size: 14px;
  line-height: 1.6;
  color: var(--theme-text-1);
  font-family: inherit;
  background: transparent;
  outline: none;
  resize: none;
}

.comment-textarea::placeholder {
  color: var(--theme-text-3);
}

.comment-toolbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  margin-top: 4px;
}

.tool-btn {
  display: inline-flex;
  width: 32px;
  height: 32px;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--theme-text-2);
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.tool-btn:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.comment-send {
  min-width: 58px;
  height: 32px;
  padding: 0 16px;
  margin-left: 6px;
  border: none;
  border-radius: 6px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-3);
  font-size: 14px;
  font-family: inherit;
  cursor: not-allowed;
  transition: background 0.2s ease, color 0.2s ease;
}

.comment-send.is-active {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  cursor: pointer;
}

.comment-send.is-active:hover {
  background: var(--theme-primary-hover);
}

.comment-emoji-panel {
  position: absolute;
  right: 8px;
  bottom: 46px;
  z-index: 20;
  display: grid;
  width: 288px;
  max-height: 196px;
  padding: 8px;
  overflow-y: auto;
  grid-template-columns: repeat(8, 1fr);
  gap: 2px;
  border-radius: 10px;
  background: var(--theme-bg-elevated);
  box-shadow: var(--theme-shadow-md);
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 18px 0 8px;
}

.load-more-btn {
  padding: 9px 28px;
  border: none;
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-primary);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.load-more-btn:hover:not(:disabled) {
  background: var(--theme-bg-hover);
}

.load-more-end {
  color: var(--theme-text-3);
  font-size: 13px;
}

/* 发表窗口 */
.publish-overlay {
  position: fixed;
  inset: 0;
  z-index: 1300;
  background: var(--theme-overlay);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.publish-window {
  width: 500px;
  max-width: calc(100vw - 40px);
  max-height: calc(100vh - 60px);
  background: var(--theme-bg-elevated);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 想法输入 */
.publish-compose {
  padding: 22px 24px 10px;
}

.publish-input {
  width: 100%;
  min-height: 110px;
  border-radius: 12px;
  border: none;
  resize: none;
  font-size: 15px;
  line-height: 1.7;
  border: none;
  color: var(--theme-text-1);
  outline: none;
  background: transparent;
  font-family: inherit;
  box-sizing: border-box;
}

.publish-input::placeholder {
  color: var(--theme-text-3);
}

/* 表情 */
.publish-emoji-wrap {
  position: relative;
  display: inline-flex;
  margin-top: 6px;
}

.emoji-btn {
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  padding: 2px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: color 0.2s ease;
}

.emoji-btn:hover {
  color: var(--theme-text-2);
}

.emoji-panel {
  position: absolute;
  left: 0;
  top: 36px;
  width: 300px;
  max-height: 200px;
  overflow-y: auto;
  padding: 8px;
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 4px;
  background: var(--theme-bg-elevated);
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  box-shadow: var(--theme-shadow-md);
  z-index: 3;
}

.emoji-item {
  border: none;
  background: transparent;
  font-size: 18px;
  line-height: 1;
  padding: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.15s ease;
}

.emoji-item:hover {
  background: var(--theme-bg-hover);
}

/* 图片九宫格 */
.publish-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  padding: 12px 24px 4px;
}

.publish-thumb {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
}

.publish-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-remove {
  position: absolute;
  top: 5px;
  right: 5px;
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

.publish-add {
  aspect-ratio: 1;
  background: var(--theme-bg-surface-2);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--theme-text-3);
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.publish-add:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-2);
}

.publish-add input {
  display: none;
}

/* 选项行 */
.publish-options {
  margin: 18px 24px 4px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
}

.option-row {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 0 16px;
  height: 52px;
  border: none;
  background: transparent;
  font-size: 15px;
  color: var(--theme-text-1);
  font-family: inherit;
  text-align: left;
  box-sizing: border-box;
}

.option-row + .option-row {
  border-top: 1px solid var(--theme-hairline);
}

.option-clickable {
  cursor: pointer;
  transition: background 0.2s ease;
}

.option-clickable:hover {
  background: var(--theme-bg-hover);
}

.option-row:first-child {
  border-top-left-radius: 9px;
  border-top-right-radius: 9px;
}

.option-row:last-child {
  border-bottom-left-radius: 9px;
  border-bottom-right-radius: 9px;
}

.option-icon {
  color: var(--theme-text-1);
  flex-shrink: 0;
}

.option-label {
  flex: 1;
  min-width: 0;
}

.option-value {
  color: var(--theme-text-3);
  font-size: 14px;
}

.option-arrow {
  color: var(--theme-text-3);
  flex-shrink: 0;
}

.option-input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  background: transparent;
  text-align: right;
  font-size: 14px;
  color: var(--theme-text-1);
  font-family: inherit;
}

.option-input::placeholder {
  color: var(--theme-text-3);
}

/* 可见范围菜单（向上弹出，避免被弹窗底边裁切） */
.visibility-menu {
  position: absolute;
  right: 12px;
  bottom: 58px;
  width: 132px;
  padding: 6px;
  background: var(--theme-bg-elevated);
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  box-shadow: var(--theme-shadow-md);
  z-index: 3;
}

.visibility-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 8px 10px;
  border: none;
  border-radius: 7px;
  background: transparent;
  font-size: 14px;
  color: var(--theme-text-1);
  cursor: pointer;
  transition: background 0.15s ease;
}

.visibility-item:hover {
  background: var(--theme-bg-hover);
}

.visibility-item.active {
  color: var(--theme-primary);
}

/* 底部按钮 */
.publish-actions {
  display: flex;
  justify-content: center;
  gap: 14px;
  padding: 20px 24px 24px;
}

.publish-submit {
  width: 140px;
  height: 44px;
  border: none;
  border-radius: 6px;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s ease;
}

.publish-submit:hover:not(:disabled) {
  background: var(--theme-primary-hover);
}

.publish-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.publish-cancel {
  width: 140px;
  height: 44px;
  border: none;
  border-radius: 6px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.publish-cancel:hover {
  background: var(--theme-bg-hover);
}

/* 图片预览 */
.preview-overlay {
  position: fixed;
  inset: 0;
  z-index: 1400;
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
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
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
    height: 200px;
  }

  .cover-avatar {
    width: 58px;
    height: 58px;
  }

  .moments-body {
    padding: 22px 16px 60px;
  }

  .publish-window {
    width: 100%;
  }
}
</style>
