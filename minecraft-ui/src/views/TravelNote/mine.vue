<template>
  <div class="note-mine">
    <div class="note-mine__header">
      <h2>我的笔记</h2>
      <el-button type="primary" @click="router.push('/travel-note/editor')">
        <el-icon class="mr-1"><EditPen /></el-icon>
        写笔记
      </el-button>
    </div>

    <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="草稿箱" name="0" />
      <el-tab-pane label="待审核" name="1" />
      <el-tab-pane label="已发布" name="2" />
      <el-tab-pane label="已驳回" name="3" />
      <el-tab-pane label="私密" name="5" />
    </el-tabs>

    <div v-loading="loading" class="note-mine__list">
      <template v-if="notes.length">
        <div v-for="note in notes" :key="note.id" class="note-mine__item" @click="goDetail(note)">
          <div class="note-mine__cover">
            <img v-if="note.coverUrl" :src="note.coverUrl" alt="" />
            <el-icon v-else :size="24"><Picture /></el-icon>
          </div>
          <div class="note-mine__info">
            <div class="note-mine__name">
              {{ note.title }}
              <el-tag v-if="note.isTop" type="danger" size="small" effect="plain">顶</el-tag>
              <el-tag v-if="note.isFeatured" type="warning" size="small" effect="plain">精</el-tag>
            </div>
            <div class="note-mine__summary">{{ note.summary || '暂无摘要' }}</div>
            <div class="note-mine__stats">
              <span><el-icon><View /></el-icon>{{ note.viewCount || 0 }}</span>
              <span><el-icon><Pointer /></el-icon>{{ note.likeCount || 0 }}</span>
              <span><el-icon><Star /></el-icon>{{ note.collectCount || 0 }}</span>
              <span><el-icon><ChatDotRound /></el-icon>{{ note.commentCount || 0 }}</span>
            </div>
          </div>
          <div class="note-mine__side">
            <el-tag :type="statusType(note.status)" size="small">{{ statusLabel(note.status) }}</el-tag>
            <span v-if="note.status === 3 && note.rejectReason" class="note-mine__reject">
              驳回原因：{{ note.rejectReason }}
            </span>
            <span class="note-mine__time">{{ formatTime(note.updateTime || note.createTime) }}</span>
            <div class="note-mine__ops" @click.stop>
              <el-button size="small" @click="goEditor(note)">编辑</el-button>
              <el-popconfirm title="确定删除这篇笔记吗？" @confirm="removeNote(note)">
                <template #reference>
                  <el-button size="small" type="danger" plain>删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </template>
      <el-empty v-else-if="!loading" description="这里还什么都没有">
        <el-button type="primary" @click="router.push('/travel-note/editor')">去写笔记</el-button>
      </el-empty>
    </div>

    <div v-if="total > query.pageSize" class="note-mine__pagination">
      <el-pagination background layout="prev, pager, next" :total="total"
        :page-size="query.pageSize" :current-page="query.pageNum" @current-change="handlePageChange" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, EditPen, Picture, Pointer, Star, View } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { deleteNote, getMyNotes } from '@/api/travelNote'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const activeStatus = ref('all')
const loading = ref(false)
const notes = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })

const STATUS_MAP = {
  0: { label: '草稿', type: 'info' },
  1: { label: '待审核', type: 'warning' },
  2: { label: '已发布', type: 'success' },
  3: { label: '已驳回', type: 'danger' },
  4: { label: '已下架', type: 'info' },
  5: { label: '私密', type: 'info' }
}

const statusLabel = (s) => STATUS_MAP[s]?.label || '未知'
const statusType = (s) => STATUS_MAP[s]?.type || 'info'

onMounted(() => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.replace('/login')
    return
  }
  fetchNotes()
})

async function fetchNotes() {
  loading.value = true
  try {
    const params = { ...query }
    if (activeStatus.value !== 'all') {
      params.status = Number(activeStatus.value)
    }
    const res = await getMyNotes(params)
    if (res.code === 200) {
      notes.value = res.data?.records || []
      total.value = Number(res.data?.total || 0)
    }
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  query.pageNum = 1
  fetchNotes()
}

function handlePageChange(page) {
  query.pageNum = page
  fetchNotes()
}

function goDetail(note) {
  if (note.status === 2) {
    router.push(`/travel-note/detail/${note.id}`)
  }
}

function goEditor(note) {
  router.push(`/travel-note/editor?id=${note.id}`)
}

async function removeNote(note) {
  const res = await deleteNote(note.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    fetchNotes()
  } else {
    ElMessage.error(res.message || '删除失败')
  }
}

function formatTime(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}
</script>

<style scoped>
.note-mine {
  max-width: 920px;
  margin: 0 auto;
  padding: 32px 16px 64px;
}

.note-mine__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.note-mine__header h2 {
  margin: 0;
  font-size: 24px;
}

.note-mine__list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 200px;
}

.note-mine__item {
  display: flex;
  gap: 14px;
  padding: 14px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.note-mine__item:hover {
  box-shadow: var(--el-box-shadow-light);
}

.note-mine__cover {
  width: 128px;
  height: 88px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--el-fill-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-placeholder);
  flex-shrink: 0;
}

.note-mine__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.note-mine__info {
  flex: 1;
  min-width: 0;
}

.note-mine__name {
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.note-mine__summary {
  margin-top: 6px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-mine__stats {
  margin-top: 8px;
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.note-mine__stats span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.note-mine__side {
  width: 200px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.note-mine__reject {
  font-size: 12px;
  color: var(--el-color-danger);
  text-align: right;
}

.note-mine__time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.note-mine__ops {
  display: flex;
  gap: 6px;
}

.note-mine__pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
