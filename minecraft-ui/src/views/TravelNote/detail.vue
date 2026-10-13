<template>
  <div v-loading="loading" class="note-detail">
    <template v-if="note">
      <div class="note-detail__main">
        <!-- 头部 -->
        <header class="note-detail__header">
          <h1 class="note-detail__title">{{ note.title }}</h1>
          <div class="note-detail__meta">
            <div class="note-detail__author" @click="author.id && goAuthor(author.id)">
              <el-avatar :size="40" :src="author.avatar">{{ (author.name || '匿')[0] }}</el-avatar>
              <div>
                <div class="note-detail__username">{{ author.name || '匿名用户' }}</div>
                <div class="note-detail__time">{{ formatTime(note.publishTime || note.createTime) }}</div>
              </div>
            </div>
            <div v-if="note.destination" class="note-detail__dest">
              <el-icon><Location /></el-icon>{{ note.destination }}
            </div>
          </div>

          <!-- 行程信息 -->
          <div class="note-detail__trip">
            <span v-if="note.travelDays"><b>{{ note.travelDays }}</b> 天</span>
            <span v-if="note.cost"><b>￥{{ note.cost }}</b> 人均</span>
            <span v-if="note.travelDate">出行 <b>{{ note.travelDate }}</b></span>
            <span v-if="note.cityName">城市 <b>{{ note.cityName }}</b></span>
          </div>

          <!-- 标签 -->
          <div v-if="note.tags && note.tags.length" class="note-detail__tags">
            <el-tag v-for="tag in note.tags" :key="tag" effect="plain" size="small"># {{ tag }}</el-tag>
          </div>
        </header>

        <!-- 媒体图集 -->
        <div v-if="note.mediaList && note.mediaList.length" class="note-detail__gallery">
          <el-image
            v-for="media in note.mediaList"
            :key="media.id"
            :src="media.url"
            :preview-src-list="note.mediaList.map(m => m.url)"
            :initial-index="note.mediaList.indexOf(media)"
            fit="cover"
            class="note-detail__gallery-item"
            hide-on-click-modal
          />
        </div>

        <!-- 正文 -->
        <article v-if="note.contentType === 2" class="note-detail__content note-detail__content--md">
          <pre>{{ note.content }}</pre>
        </article>
        <article v-else class="note-detail__content rich-content" v-html="safeContent" />

        <!-- 结构化关联 -->
        <section v-if="note.relations && note.relations.length" class="note-detail__section">
          <h3>相关推荐</h3>
          <div class="note-detail__relations">
            <div v-for="rel in note.relations" :key="rel.id" class="note-detail__relation">
              <img v-if="rel.bizCover" :src="rel.bizCover" alt="" />
              <div v-else class="note-detail__relation-fallback">{{ bizTypeLabel(rel.bizType)[0] }}</div>
              <div class="note-detail__relation-info">
                <span class="note-detail__relation-type">{{ bizTypeLabel(rel.bizType) }}</span>
                <span class="note-detail__relation-name">{{ rel.bizName }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- 行程轨迹 -->
        <section v-if="note.routes && note.routes.length" class="note-detail__section">
          <h3>行程轨迹</h3>
          <el-timeline class="note-detail__route">
            <el-timeline-item
              v-for="route in note.routes"
              :key="route.id"
              :timestamp="`Day ${route.dayIndex ?? '-'}`"
              placement="top"
            >
              <b>{{ route.poiName }}</b>
              <p v-if="route.remark" class="note-detail__route-remark">{{ route.remark }}</p>
            </el-timeline-item>
          </el-timeline>
        </section>

        <!-- 互动 -->
        <footer class="note-detail__actions">
          <el-button :type="note.isLiked ? 'danger' : 'default'" round @click="toggleLike">
            <el-icon class="mr-1"><Pointer /></el-icon>
            {{ note.likeCount || 0 }}
          </el-button>
          <el-button :type="note.isCollected ? 'warning' : 'default'" round @click="toggleCollect">
            <el-icon class="mr-1"><Star /></el-icon>
            {{ note.collectCount || 0 }}
          </el-button>
          <el-button round @click="copyToPlan">
            <el-icon class="mr-1"><Calendar /></el-icon>
            复制为行程
          </el-button>
          <el-button round @click="reportDialogVisible = true">
            <el-icon class="mr-1"><WarningFilled /></el-icon>
            举报
          </el-button>
        </footer>
      </div>

      <!-- 评论区 -->
      <section class="note-detail__comments">
        <h3>评论 ({{ note.commentCount || 0 }})</h3>

        <div class="note-detail__comment-input">
          <el-input
            v-model="commentText"
            type="textarea"
            :rows="3"
            maxlength="1000"
            show-word-limit
            placeholder="写下你的想法..."
          />
          <div class="note-detail__comment-submit">
            <el-button type="primary" :loading="commentSubmitting" @click="submitComment()">发表评论</el-button>
          </div>
        </div>

        <div v-for="comment in comments" :key="comment.id" class="note-detail__comment">
          <el-avatar :size="34" :src="comment.avatar">{{ (comment.username || '匿')[0] }}</el-avatar>
          <div class="note-detail__comment-body">
            <div class="note-detail__comment-head">
              <span class="note-detail__comment-user">{{ comment.username }}</span>
              <span class="note-detail__comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <p class="note-detail__comment-text">{{ comment.content }}</p>
            <div class="note-detail__comment-ops">
              <el-link type="primary" :underline="false" @click="startReply(comment)">回复</el-link>
              <el-link
                v-if="comment.userId === currentUserId"
                type="danger"
                :underline="false"
                @click="removeComment(comment.id)"
              >
                删除
              </el-link>
            </div>

            <!-- 二级回复 -->
            <div v-if="comment.replies && comment.replies.length" class="note-detail__replies">
              <div v-for="reply in comment.replies" :key="reply.id" class="note-detail__reply">
                <el-avatar :size="26" :src="reply.avatar">{{ (reply.username || '匿')[0] }}</el-avatar>
                <div>
                  <div class="note-detail__comment-head">
                    <span class="note-detail__comment-user">{{ reply.username }}</span>
                    <span v-if="reply.replyUsername" class="note-detail__comment-user">
                      回复 @{{ reply.replyUsername }}
                    </span>
                    <span class="note-detail__comment-time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                  <p class="note-detail__comment-text">{{ reply.content }}</p>
                  <div class="note-detail__comment-ops">
                    <el-link type="primary" :underline="false" @click="startReply(comment, reply)">回复</el-link>
                    <el-link
                      v-if="reply.userId === currentUserId"
                      type="danger"
                      :underline="false"
                      @click="removeComment(reply.id)"
                    >
                      删除
                    </el-link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="comments.length" class="note-detail__comment-more">
          <el-button
            :loading="commentsLoading"
            :disabled="commentsNoMore"
            size="small"
            @click="loadMoreComments"
          >
            {{ commentsNoMore ? '没有更多评论' : '加载更多评论' }}
          </el-button>
        </div>
      </section>
    </template>

    <el-empty v-else-if="!loading" description="笔记不存在或未发布">
      <el-button type="primary" @click="router.push('/travel-note')">返回笔记广场</el-button>
    </el-empty>

    <!-- 回复框 -->
    <el-dialog v-model="replyVisible" title="回复评论" width="480px">
      <el-input v-model="replyText" type="textarea" :rows="3" maxlength="1000" show-word-limit />
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="commentSubmitting" @click="submitReply">回复</el-button>
      </template>
    </el-dialog>

    <!-- 举报 -->
    <el-dialog v-model="reportDialogVisible" title="举报笔记" width="480px">
      <el-input v-model="reportReason" type="textarea" :rows="3" placeholder="请描述举报原因（垃圾广告、违法违规、侵权等）" />
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="reportSubmitting" @click="submitReport">提交举报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Location, Pointer, Star, WarningFilled } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import {
  addNoteComment,
  collectNote,
  copyNoteToPlan,
  deleteNoteComment,
  getNoteComments,
  getNoteDetail,
  likeNote,
  reportNote,
  uncollectNote,
  unlikeNote
} from '@/api/travelNote'
import { useAuthStore } from '@/stores/auth'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const noteId = route.params.id
const loading = ref(true)
const note = ref(null)
const currentUserId = authStore.userInfo?.id ?? null

const commentText = ref('')
const commentSubmitting = ref(false)
const comments = ref([])
const commentsPage = ref(1)
const commentsNoMore = ref(false)
const commentsLoading = ref(false)

const replyVisible = ref(false)
const replyText = ref('')
const replyTarget = ref(null)
const replyParent = ref(null)

const reportDialogVisible = ref(false)
const reportReason = ref('')
const reportSubmitting = ref(false)

const author = computed(() => ({
  id: note.value?.userId,
  name: note.value?.username,
  avatar: note.value?.avatar
}))

const BIZ_TYPES = { 1: '景点', 2: '酒店', 3: '美食', 4: '商品', 5: '行程' }
const bizTypeLabel = (type) => BIZ_TYPES[type] || '相关'

const safeContent = computed(() => note.value?.content || '')

const loadNote = async () => {
  try {
    const res = await getNoteDetail(noteId)
    if (res.code === 200) {
      note.value = res.data
      fetchComments()
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadNote)

// 数据变更后自动刷新
useAutoRefresh(loadNote)

async function fetchComments() {
  commentsLoading.value = true
  try {
    const res = await getNoteComments(noteId, { pageNum: commentsPage.value, pageSize: 10 })
    if (res.code === 200) {
      const records = res.data?.records || []
      comments.value = commentsPage.value === 1 ? records : [...comments.value, ...records]
      commentsNoMore.value = comments.value.length >= Number(res.data?.total || 0)
    }
  } finally {
    commentsLoading.value = false
  }
}

function loadMoreComments() {
  commentsPage.value += 1
  fetchComments()
}

async function toggleLike() {
  if (!ensureLogin()) return
  const res = note.value.isLiked ? await unlikeNote(noteId) : await likeNote(noteId)
  if (res.code === 200) {
    note.value.isLiked = !note.value.isLiked
    note.value.likeCount = Math.max((note.value.likeCount || 0) + (note.value.isLiked ? 1 : -1), 0)
  } else {
    ElMessage.warning(res.message)
  }
}

async function toggleCollect() {
  if (!ensureLogin()) return
  const res = note.value.isCollected ? await uncollectNote(noteId) : await collectNote(noteId)
  if (res.code === 200) {
    note.value.isCollected = !note.value.isCollected
    note.value.collectCount = Math.max((note.value.collectCount || 0) + (note.value.isCollected ? 1 : -1), 0)
    ElMessage.success(note.value.isCollected ? '已收藏' : '已取消收藏')
  } else {
    ElMessage.warning(res.message)
  }
}

async function copyToPlan() {
  if (!ensureLogin()) return
  try {
    await ElMessageBox.confirm('将根据本笔记生成一份出行计划，是否继续？', '复制为行程', { type: 'info' })
  } catch {
    return
  }
  const res = await copyNoteToPlan(noteId)
  if (res.code === 200) {
    ElMessage.success(res.message || '已复制到我的出行计划')
  } else {
    ElMessage.error(res.message || '复制失败')
  }
}

async function submitComment() {
  if (!ensureLogin()) return
  if (!commentText.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }
  commentSubmitting.value = true
  try {
    const res = await addNoteComment(noteId, { content: commentText.value.trim() })
    if (res.code === 200) {
      ElMessage.success('评论成功')
      commentText.value = ''
      commentsPage.value = 1
      comments.value = []
      if (note.value) note.value.commentCount = (note.value.commentCount || 0) + 1
      fetchComments()
    } else {
      ElMessage.error(res.message || '评论失败')
    }
  } finally {
    commentSubmitting.value = false
  }
}

function startReply(comment, reply) {
  if (!ensureLogin()) return
  replyTarget.value = comment
  replyParent.value = reply || comment
  replyText.value = ''
  replyVisible.value = true
}

async function submitReply() {
  if (!replyText.value.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }
  commentSubmitting.value = true
  try {
    const res = await addNoteComment(noteId, {
      parentId: replyTarget.value.id,
      replyUid: replyParent.value.userId,
      content: replyText.value.trim()
    })
    if (res.code === 200) {
      ElMessage.success('回复成功')
      replyVisible.value = false
      if (note.value) note.value.commentCount = (note.value.commentCount || 0) + 1
      commentsPage.value = 1
      comments.value = []
      fetchComments()
    } else {
      ElMessage.error(res.message || '回复失败')
    }
  } finally {
    commentSubmitting.value = false
  }
}

async function removeComment(commentId) {
  try {
    await ElMessageBox.confirm('确定删除该评论吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  const res = await deleteNoteComment(commentId)
  if (res.code === 200) {
    if (note.value) note.value.commentCount = Math.max((note.value.commentCount || 0) - 1, 0)
    commentsPage.value = 1
    comments.value = []
    fetchComments()
  } else {
    ElMessage.error(res.message || '删除失败')
  }
}

async function submitReport() {
  if (!ensureLogin()) return
  reportSubmitting.value = true
  try {
    const res = await reportNote(noteId, reportReason.value.trim())
    if (res.code === 200) {
      ElMessage.success(res.message || '举报成功')
      reportDialogVisible.value = false
      reportReason.value = ''
    } else {
      ElMessage.error(res.message || '举报失败')
    }
  } finally {
    reportSubmitting.value = false
  }
}

function ensureLogin() {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return false
  }
  return true
}

function goAuthor(userId) {
  router.push(`/community?userId=${userId}`)
}

function formatTime(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}
</script>

<style scoped>
/* ============ tokens & shell ============ */
.note-detail {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-ink-3: var(--theme-text-3);
  --tn-accent: var(--theme-primary);
  --tn-accent-2: var(--theme-accent-2);
  --tn-line: var(--theme-border);
  --tn-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  max-width: 880px;
  margin: 0 auto;
  margin-top: 100px;
  padding: 40px 20px 80px;
  font-family: var(--tn-font);
  color: var(--tn-ink);
}

.note-detail::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    radial-gradient(900px 460px at 8% -10%, var(--theme-primary-soft), transparent 60%),
    radial-gradient(760px 420px at 100% 0%, color-mix(in srgb, var(--theme-accent-2) 9%, transparent), transparent 58%),
    var(--theme-bg-surface-2);
}

.note-detail__main {
  padding: 36px 40px 32px;
  border-radius: 24px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--tn-line);
  box-shadow: var(--theme-shadow-lg);
  animation: nd-rise 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* ============ header ============ */
.note-detail__title {
  margin: 0 0 18px;
  font-size: clamp(26px, 4vw, 38px);
  font-weight: 800;
  line-height: 1.25;
  letter-spacing: -0.02em;
  color: var(--tn-ink);
}

.note-detail__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--tn-line);
}

.note-detail__author {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.note-detail__username {
  font-size: 14px;
  font-weight: 700;
  color: var(--tn-ink);
}

.note-detail__time {
  font-size: 12px;
  color: var(--tn-ink-3);
}

.note-detail__dest {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 999px;
  background: var(--theme-primary-soft);
  color: var(--tn-accent);
  font-size: 14px;
  font-weight: 600;
}

.note-detail__trip {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.note-detail__trip span {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;
  padding: 8px 16px;
  border-radius: 14px;
  font-size: 13px;
  color: var(--tn-ink-2);
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

.note-detail__trip b {
  margin-right: 2px;
  font-size: 15px;
  font-weight: 800;
  color: var(--tn-ink);
}

.note-detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.note-detail__tags :deep(.el-tag) {
  border: none;
  background: var(--theme-primary-soft);
  color: var(--tn-accent);
  font-weight: 500;
}

/* ============ gallery ============ */
.note-detail__gallery {
  margin-top: 24px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.note-detail__gallery-item {
  width: 100%;
  aspect-ratio: 4 / 3;
  border-radius: 16px;
  cursor: zoom-in;
  overflow: hidden;
  transition:
    transform 0.4s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.4s ease;
}

.note-detail__gallery-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--theme-shadow-lg);
}

/* ============ content ============ */
.note-detail__content {
  margin-top: 28px;
  font-size: 16px;
  line-height: 1.95;
  color: var(--theme-text-1);
  word-break: break-word;
}

.note-detail__content--md pre {
  white-space: pre-wrap;
  font-family: inherit;
  margin: 0;
}

.note-detail__content :deep(img) {
  max-width: 100%;
  border-radius: 14px;
  margin: 8px 0;
}

.note-detail__content :deep(h2) {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.01em;
  margin: 28px 0 12px;
}

.note-detail__content :deep(p) {
  margin: 0 0 14px;
}

.note-detail__content :deep(blockquote) {
  margin: 16px 0;
  padding: 12px 18px;
  border-left: 3px solid var(--tn-accent);
  background: var(--theme-primary-soft);
  border-radius: 0 12px 12px 0;
  color: var(--tn-ink-2);
}

/* ============ sections ============ */
.note-detail__section {
  margin-top: 36px;
}

.note-detail__section h3,
.note-detail__comments > h3 {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 800;
  margin: 0 0 18px;
}

.note-detail__section h3::before,
.note-detail__comments > h3::before {
  content: '';
  width: 4px;
  height: 18px;
  border-radius: 3px;
  background: linear-gradient(180deg, var(--tn-accent), var(--tn-accent-2));
}

.note-detail__relations {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  gap: 12px;
}

.note-detail__relation {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--tn-line);
  border-radius: 16px;
  background: var(--theme-bg-surface);
  transition: all 0.28s cubic-bezier(0.22, 1, 0.36, 1);
}

.note-detail__relation:hover {
  transform: translateY(-2px);
  border-color: color-mix(in srgb, var(--theme-primary) 25%, transparent);
  box-shadow: var(--theme-shadow-md);
}

.note-detail__relation img,
.note-detail__relation-fallback {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  object-fit: cover;
  flex-shrink: 0;
}

.note-detail__relation-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--theme-bg-surface-2);
  color: var(--tn-ink-2);
  font-weight: 700;
}

.note-detail__relation-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.note-detail__relation-type {
  font-size: 12px;
  font-weight: 600;
  color: var(--tn-accent);
}

.note-detail__relation-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--tn-ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.note-detail__route :deep(.el-timeline-item__timestamp) {
  color: var(--tn-ink-3);
  font-weight: 600;
}

.note-detail__route-remark {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--tn-ink-2);
}

/* ============ actions ============ */
.note-detail__actions {
  margin-top: 36px;
  padding-top: 24px;
  border-top: 1px solid var(--tn-line);
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* ============ comments ============ */
.note-detail__comments {
  margin-top: 24px;
  padding: 32px 40px;
  border-radius: 24px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--tn-line);
  box-shadow: var(--theme-shadow-lg);
  animation: nd-rise 0.55s cubic-bezier(0.22, 1, 0.36, 1) 0.06s both;
}

.note-detail__comment-input {
  margin-bottom: 22px;
}

.note-detail__comment-input :deep(.el-textarea__inner) {
  border-radius: 14px;
}

.note-detail__comment-submit {
  margin-top: 10px;
  text-align: right;
}

.note-detail__comment {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.note-detail__comment:last-child {
  border-bottom: none;
}

.note-detail__comment-body {
  flex: 1;
  min-width: 0;
}

.note-detail__comment-head {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.note-detail__comment-user {
  font-size: 13px;
  font-weight: 700;
  color: var(--tn-ink);
}

.note-detail__comment-time {
  font-size: 12px;
  color: var(--tn-ink-3);
}

.note-detail__comment-text {
  margin: 6px 0;
  font-size: 14px;
  line-height: 1.7;
  color: var(--theme-text-1);
  white-space: pre-wrap;
  word-break: break-word;
}

.note-detail__comment-ops {
  display: flex;
  gap: 12px;
  font-size: 12px;
}

.note-detail__replies {
  margin-top: 10px;
  padding: 12px 14px;
  background: var(--theme-bg-surface-2);
  border-radius: 14px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.note-detail__reply {
  display: flex;
  gap: 10px;
}

.note-detail__comment-more {
  margin-top: 16px;
  text-align: center;
}

@keyframes nd-rise {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 720px) {
  .note-detail {
    margin-top: 84px;
    padding: 20px 12px 64px;
  }
  .note-detail__main,
  .note-detail__comments {
    padding: 22px 18px;
    border-radius: 20px;
  }
}
</style>
