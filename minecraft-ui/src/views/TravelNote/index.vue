<template>
  <div class="note-square">
    <!-- 顶部操作区 -->
    <div class="note-square__header">
      <el-tabs v-model="activeTab" class="note-square__tabs" @tab-change="handleTabChange">
        <el-tab-pane label="推荐" name="recommend" />
        <el-tab-pane label="最新" name="latest" />
        <el-tab-pane label="关注" name="follow" />
        <el-tab-pane label="同城" name="city" />
      </el-tabs>

      <div class="note-square__actions">
        <el-input
          v-model="keyword"
          class="note-square__search"
          placeholder="搜索目的地 / 标题 / 关键词"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="goEditor()">
          <el-icon class="mr-1"><EditPen /></el-icon>
          写笔记
        </el-button>
        <el-button @click="goMine">我的笔记</el-button>
      </div>
    </div>

    <!-- 热门标签 -->
    <div v-if="hotTags.length" class="note-square__tags">
      <el-tag
        v-for="tag in hotTags"
        :key="tag.id"
        :type="query.tagId === tag.id ? 'primary' : 'info'"
        effect="plain"
        class="note-square__tag"
        @click="handleTagClick(tag)"
      >
        # {{ tag.name }}
      </el-tag>
    </div>

    <!-- 瀑布流 -->
    <div v-loading="loading" class="note-square__waterfall">
      <template v-if="notes.length">
        <NoteCard v-for="note in notes" :key="note.id" :note="note" @click="goDetail" />
      </template>
      <el-empty v-else-if="!loading" description="暂无笔记，快来发布第一篇吧～" />
    </div>

    <!-- 加载更多 -->
    <div v-if="notes.length" class="note-square__more">
      <el-button :loading="loading" :disabled="noMore" @click="loadMore">
        {{ noMore ? '没有更多了' : '加载更多' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { EditPen, Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import NoteCard from './components/NoteCard.vue'
import { getHotTags, getNotePage } from '@/api/travelNote'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('recommend')
const keyword = ref('')
const loading = ref(false)
const notes = ref([])
const hotTags = ref([])
const noMore = ref(false)

const query = reactive({
  pageNum: 1,
  pageSize: 12,
  tab: 'recommend',
  keyword: '',
  tagId: null,
  cityId: null
})

onMounted(() => {
  fetchNotes()
  fetchHotTags()
})

async function fetchNotes() {
  loading.value = true
  try {
    const res = await getNotePage({ ...query })
    if (res.code === 200) {
      const records = res.data?.records || []
      if (query.pageNum === 1) {
        notes.value = records
      } else {
        notes.value = [...notes.value, ...records]
      }
      noMore.value = notes.value.length >= Number(res.data?.total || 0)
    }
  } finally {
    loading.value = false
  }
}

async function fetchHotTags() {
  const res = await getHotTags(12)
  if (res.code === 200) {
    hotTags.value = res.data || []
  }
}

function resetAndFetch() {
  query.pageNum = 1
  noMore.value = false
  fetchNotes()
}

function handleTabChange(tab) {
  query.tab = tab
  if (tab === 'follow' && !authStore.isAuthenticated) {
    ElMessage.warning('登录后可查看关注作者的笔记')
  }
  resetAndFetch()
}

function handleSearch() {
  query.keyword = keyword.value.trim()
  resetAndFetch()
}

function handleTagClick(tag) {
  query.tagId = query.tagId === tag.id ? null : tag.id
  resetAndFetch()
}

function loadMore() {
  query.pageNum += 1
  fetchNotes()
}

function goDetail(note) {
  router.push(`/travel-note/detail/${note.id}`)
}

function goEditor() {
  router.push('/travel-note/editor')
}

function goMine() {
  router.push('/travel-note/mine')
}
</script>

<style scoped>
.note-square {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 16px 48px;
}

.note-square__header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.note-square__tabs {
  flex: 1;
  min-width: 280px;
}

.note-square__tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.note-square__actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.note-square__search {
  width: 240px;
}

.note-square__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 14px 0 4px;
}

.note-square__tag {
  cursor: pointer;
}

.note-square__waterfall {
  margin-top: 16px;
  columns: 4 240px;
  column-gap: 16px;
  min-height: 240px;
}

.note-square__more {
  margin-top: 24px;
  text-align: center;
}

@media (max-width: 768px) {
  .note-square__search {
    width: 160px;
  }
}
</style>
