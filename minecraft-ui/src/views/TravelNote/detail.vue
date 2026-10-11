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

onMounted(async () => {
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
})

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
.note-detail {
  max-width: 820px;
  margin: 0 auto;
  padding: 32px 16px 64px;
}

.note-detail__title {
  margin: 0 0 16px;
  font-size: 26px;
  line-height: 1.4;
  color: var(--el-text-color-primary);
}

.note-detail__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.note-detail__author {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.note-detail__username {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.note-detail__time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.note-detail__dest {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--el-color-primary);
  font-size: 14px;
}

.note-detail__trip {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 14px;
  padding: 10px 16px;
  border-radius: 10px;
  background: var(--el-fill-color-light);
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.note-detail__trip b {
  color: var(--el-text-color-primary);
  margin-right: 2px;
}

.note-detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.note-detail__gallery {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 10px;
}

.note-detail__gallery-item {
  width: 100%;
  aspect-ratio: 4 / 3;
  border-radius: 10px;
  cursor: zoom-in;
}

.note-detail__content {
  margin-top: 24px;
  font-size: 15px;
  line-height: 1.9;
  color: var(--el-text-color-regular);
  word-break: break-word;
}

.note-detail__content--md pre {
  white-space: pre-wrap;
  font-family: inherit;
  margin: 0;
}

.note-detail__content :deep(img) {
  max-width: 100%;
  border-radius: 10px;
}

.note-detail__section {
  margin-top: 32px;
}

.note-detail__section h3 {
  font-size: 17px;
  margin: 0 0 14px;
}

.note-detail__relations {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}

.note-detail__relation {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 10px;
}

.note-detail__relation img,
.note-detail__relation-fallback {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}

.note-detail__relation-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-fill-color);
  color: var(--el-text-color-secondary);
}

.note-detail__relation-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.note-detail__relation-type {
  font-size: 12px;
  color: var(--el-color-primary);
}

.note-detail__relation-name {
  font-size: 14px;
  color: var(--el-text-color-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.note-detail__route-remark {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.note-detail__actions {
  margin-top: 32px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.note-detail__comments {
  margin-top: 40px;
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 24px;
}

.note-detail__comments > h3 {
  font-size: 17px;
  margin: 0 0 16px;
}

.note-detail__comment-input {
  margin-bottom: 24px;
}

.note-detail__comment-submit {
  margin-top: 8px;
  text-align: right;
}

.note-detail__comment {
  display: flex;
  gap: 10px;
  padding: 12px 0;
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
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.note-detail__comment-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.note-detail__comment-text {
  margin: 4px 0;
  font-size: 14px;
  line-height: 1.7;
  color: var(--el-text-color-regular);
  white-space: pre-wrap;
  word-break: break-word;
}

.note-detail__comment-ops {
  display: flex;
  gap: 12px;
  font-size: 12px;
}

.note-detail__replies {
  margin-top: 8px;
  padding: 10px 12px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.note-detail__reply {
  display: flex;
  gap: 8px;
}

.note-detail__comment-more {
  margin-top: 12px;
  text-align: center;
}
</style>
