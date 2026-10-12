<template>
  <div class="favorites-pane">
    <!-- 左侧分类导航 -->
    <aside class="fav-side">
      <div class="fav-side-top">
        <div class="fav-search">
          <Icon name="search" :size="'15px'" class="fav-search-icon" />
          <input
            v-model="keyword"
            class="fav-search-input"
            type="text"
            placeholder="搜索收藏"
          />
          <button v-if="keyword" class="fav-search-clear" title="清空" @click="keyword = ''">
            <Icon name="close" :size="'13px'" />
          </button>
        </div>

        <button class="fav-note-btn" @click="openComposer('note')">
          <Icon name="edit" :size="'16px'" />
          <span>写笔记</span>
        </button>
      </div>

      <nav class="fav-cats">
        <button
          v-for="cat in categories"
          :key="cat.key"
          :class="['fav-cat', { active: activeKey === cat.key && !activeTag }]"
          @click="selectCategory(cat.key)"
        >
          <Icon :name="cat.icon" :size="'17px'" class="fav-cat-icon" />
          <span class="fav-cat-label">{{ cat.label }}</span>
          <span v-if="countOf(cat.key) !== null" class="fav-cat-count">{{ countOf(cat.key) }}</span>
        </button>
      </nav>

      <div v-if="tagList.length" class="fav-tags">
        <div class="fav-tags-title">
          <Icon name="tag" :size="'14px'" />
          <span>标签</span>
        </div>
        <div class="fav-tags-wrap">
          <button
            v-for="tag in tagList"
            :key="tag"
            :class="['fav-tag-chip', { active: activeTag === tag }]"
            @click="selectTag(tag)"
          >{{ tag }}</button>
        </div>
      </div>
    </aside>

    <!-- 右侧内容区 -->
    <section class="fav-main">
      <header class="fav-main-head">
        <div class="fav-main-title">
          <h2>{{ currentTitle }}</h2>
          <span class="fav-main-count">{{ list.length }} 项</span>
        </div>
        <div class="fav-main-tools">
          <button
            v-if="canClear"
            class="fav-tool-btn"
            title="清空当前分类"
            @click="handleClear"
          >
            <Icon name="trash" :size="'16px'" />
            <span>清空</span>
          </button>
          <button class="fav-tool-btn" title="新建收藏" @click="openComposer('link')">
            <Icon name="plus" :size="'16px'" />
            <span>链接</span>
          </button>
          <button class="fav-close-btn" title="返回" @click="close">
            <Icon name="chevron-right" :size="'18px'" class="fav-close-icon" />
          </button>
        </div>
      </header>

      <div ref="listRef" class="fav-list">
        <div v-if="loading && !list.length" class="fav-state">加载中…</div>

        <div v-else-if="!list.length" class="fav-state">
          <Icon name="star" :size="'40px'" />
          <p>{{ emptyText }}</p>
        </div>

        <template v-else>
          <article
            v-for="item in list"
            :key="item.id"
            class="fav-card"
            @click="openDetail(item)"
          >
            <div class="fav-card-main">
              <h3 class="fav-card-title">{{ itemTitle(item) }}</h3>
              <p v-if="item.content" class="fav-card-desc">{{ item.content }}</p>
              <div class="fav-card-foot">
                <span class="fav-card-source">{{ item.source || itemTypeLabel(item.type) }}</span>
                <span
                  v-for="tag in item.tags || []"
                  :key="tag"
                  class="fav-card-tag"
                  @click.stop="selectTag(tag)"
                >{{ tag }}</span>
                <span class="fav-card-date">{{ formatDate(item.updateTime || item.createTime) }}</span>
              </div>
            </div>

            <div class="fav-card-thumb">
              <img v-if="item.cover" :src="item.cover" alt="封面" @click.stop="previewImage(item.cover)" />
              <div v-else class="fav-card-thumb-icon">
                <Icon :name="itemTypeIcon(item.type)" :size="'24px'" />
              </div>
            </div>

            <button class="fav-card-del" title="删除" @click.stop="handleDelete(item)">
              <Icon name="trash" :size="'15px'" />
            </button>
          </article>

          <div class="fav-more">
            <button v-if="hasMore" class="fav-more-btn" :disabled="loading" @click="loadMore">
              {{ loading ? '加载中…' : '加载更多' }}
            </button>
            <span v-else class="fav-more-end">— 没有更多了 —</span>
          </div>
        </template>
      </div>
    </section>

    <!-- 新建收藏 -->
    <transition name="fade">
      <div v-if="composer.visible" class="fav-overlay" @click.self="composer.visible = false">
        <div class="fav-window">
          <header class="fav-window-head">
            <button class="fav-window-cancel" @click="composer.visible = false">取消</button>
            <h3>{{ composer.type === 'link' ? '添加链接' : '写笔记' }}</h3>
            <button class="fav-window-submit" :disabled="saving" @click="submitComposer">
              {{ saving ? '保存中…' : '保存' }}
            </button>
          </header>

          <div class="fav-window-body">
            <div class="fav-type-switch">
              <button
                :class="['fav-type-item', { active: composer.type === 'note' }]"
                @click="composer.type = 'note'"
              >笔记</button>
              <button
                :class="['fav-type-item', { active: composer.type === 'link' }]"
                @click="composer.type = 'link'"
              >链接</button>
            </div>

            <input
              v-if="composer.type === 'link'"
              v-model="composer.url"
              class="fav-field"
              type="text"
              placeholder="请输入链接地址，如 https://…"
            />

            <input
              v-model="composer.title"
              class="fav-field"
              type="text"
              maxlength="100"
              :placeholder="composer.type === 'link' ? '标题（选填）' : '标题'"
            />

            <textarea
              v-model="composer.content"
              class="fav-field fav-textarea"
              rows="5"
              maxlength="2000"
              :placeholder="composer.type === 'link' ? '备注（选填）' : '输入笔记内容…'"
            ></textarea>

            <input
              v-model="composer.tags"
              class="fav-field"
              type="text"
              maxlength="100"
              placeholder="标签，用逗号或空格分隔（选填）"
            />

            <div class="fav-cover-row">
              <div class="fav-cover-preview">
                <img v-if="composer.cover" :src="composer.cover" alt="封面预览" />
                <Icon v-else name="image" :size="'22px'" />
              </div>
              <div class="fav-cover-actions">
                <label class="fav-cover-btn">
                  {{ composer.cover ? '更换封面' : '添加封面' }}
                  <input type="file" accept="image/*" @change="handleCoverSelect" />
                </label>
                <button v-if="composer.cover" class="fav-cover-remove" @click="composer.cover = ''">移除</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 收藏详情 -->
    <transition name="fade">
      <div v-if="detail.visible" class="fav-overlay" @click.self="detail.visible = false">
        <div class="fav-window fav-detail-window">
          <header class="fav-window-head">
            <span class="fav-detail-type">
              <Icon :name="itemTypeIcon(detail.item?.type)" :size="'15px'" />
              {{ itemTypeLabel(detail.item?.type) }}
            </span>
            <div class="fav-detail-tools">
              <button class="fav-tool-btn" title="复制" @click="copyDetail">复制</button>
              <button class="fav-tool-btn danger" title="删除" @click="handleDelete(detail.item)">
                <Icon name="trash" :size="'15px'" />
              </button>
              <button class="fav-close-btn" title="关闭" @click="detail.visible = false">
                <Icon name="close" :size="'18px'" />
              </button>
            </div>
          </header>

          <div class="fav-detail-body">
            <img
              v-if="detail.item?.cover"
              :src="detail.item.cover"
              alt="封面"
              class="fav-detail-cover"
              @click="previewImage(detail.item.cover)"
            />
            <h2 v-if="detail.item?.title" class="fav-detail-title">{{ detail.item.title }}</h2>
            <p v-if="detail.item?.content" class="fav-detail-content">{{ detail.item.content }}</p>

            <a
              v-if="detail.item?.url"
              class="fav-detail-link"
              :href="detail.item.url"
              target="_blank"
              rel="noopener noreferrer"
            >
              <Icon name="link" :size="'14px'" />
              {{ detail.item.url }}
            </a>

            <div v-if="detail.item?.fileName" class="fav-detail-file">
              <Icon name="file" :size="'15px'" />
              <span>{{ detail.item.fileName }}</span>
              <span v-if="detail.item.fileSize" class="fav-detail-size">{{ formatSize(detail.item.fileSize) }}</span>
            </div>

            <div v-if="detail.item?.tags && detail.item.tags.length" class="fav-detail-tags">
              <span v-for="tag in detail.item.tags" :key="tag" class="fav-card-tag">{{ tag }}</span>
            </div>

            <div class="fav-detail-meta">
              <span v-if="detail.item?.source">来源：{{ detail.item.source }}</span>
              <span>收藏于 {{ formatFull(detail.item?.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 图片预览 -->
    <div v-if="previewSrc" class="fav-preview" @click="previewSrc = ''">
      <img :src="previewSrc" alt="预览" @click.stop />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Icon from './Icon.vue'
import { uploadFile } from '@/api/upload'
import {
  addFavorite,
  getFavoriteList,
  getRecentFavorites,
  getFavoriteStats,
  getFavoriteTags,
  touchFavorite,
  deleteFavorite,
  clearFavorites
} from '@/api/favorite'

const props = defineProps({
  currentUserId: {
    type: [Number, String],
    default: null
  },
  currentUserName: {
    type: String,
    default: '用户'
  }
})

const emit = defineEmits(['close'])

const LIST_SIZE = 20

const categories = [
  { key: 'all', label: '全部收藏', icon: 'star' },
  { key: 'recent', label: '最近使用', icon: 'clock' },
  { key: 'note', label: '笔记', icon: 'edit' },
  { key: 'link', label: '链接', icon: 'link' },
  { key: 'media', label: '图片与视频', icon: 'image' },
  { key: 'file', label: '文件', icon: 'file' },
  { key: 'chat', label: '聊天记录', icon: 'message' },
  { key: 'voice', label: '语音', icon: 'mic' },
  { key: 'location', label: '位置', icon: 'map-pin' },
  { key: 'miniapp', label: '小程序', icon: 'compass' }
]

const activeKey = ref('all')
const activeTag = ref('')
const keyword = ref('')

const list = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)
const listRef = ref(null)

const stats = ref({ total: 0, types: {} })
const tagList = ref([])

const saving = ref(false)
const previewSrc = ref('')

const composer = reactive({
  visible: false,
  type: 'note',
  title: '',
  content: '',
  url: '',
  tags: '',
  cover: ''
})

const detail = reactive({
  visible: false,
  item: null
})

const currentTitle = computed(() => {
  if (activeTag.value) return `# ${activeTag.value}`
  const cat = categories.find(c => c.key === activeKey.value)
  return cat ? cat.label : '全部收藏'
})

const canClear = computed(() => !activeTag.value && !['all', 'recent'].includes(activeKey.value))

const emptyText = computed(() => {
  if (keyword.value.trim()) return '没有找到相关收藏'
  if (activeTag.value) return `暂无「${activeTag.value}」标签的收藏`
  return '还没有收藏，点击左侧「写笔记」开始吧'
})

// 分类 → 后端 type 参数
const typeParam = computed(() => {
  if (activeKey.value === 'all' || activeKey.value === 'recent') return ''
  if (activeKey.value === 'media') return 'image,video'
  return activeKey.value
})

const countOf = (key) => {
  const types = stats.value.types || {}
  if (key === 'all') return stats.value.total || 0
  if (key === 'recent') return null
  if (key === 'media') return (types.image || 0) + (types.video || 0)
  return types[key] || 0
}

const itemTypeIcon = (type) => {
  const map = {
    note: 'edit',
    link: 'link',
    image: 'image',
    video: 'video-camera',
    file: 'file',
    chat: 'message',
    voice: 'mic',
    location: 'map-pin',
    miniapp: 'compass'
  }
  return map[type] || 'star'
}

const itemTypeLabel = (type) => {
  const map = {
    note: '笔记',
    link: '链接',
    image: '图片',
    video: '视频',
    file: '文件',
    chat: '聊天记录',
    voice: '语音',
    location: '位置',
    miniapp: '小程序'
  }
  return map[type] || '收藏'
}

const itemTitle = (item) => {
  if (!item) return ''
  if (item.title) return item.title
  if (item.type === 'link' || item.type === 'miniapp') return item.url || '链接'
  if (item.type === 'file') return item.fileName || '文件'
  if (item.content) return item.content.slice(0, 40)
  return itemTypeLabel(item.type)
}

const formatDate = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (Number.isNaN(date.getTime())) return ''
  const now = new Date()
  const sameYear = date.getFullYear() === now.getFullYear()
  const monthDay = `${date.getMonth() + 1}月${date.getDate()}日`
  return sameYear ? monthDay : `${date.getFullYear()}年${monthDay}`
}

const formatFull = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (Number.isNaN(date.getTime())) return ''
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

const formatSize = (bytes) => {
  if (!bytes) return ''
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}

const loadStats = async () => {
  if (!props.currentUserId) return
  try {
    const res = await getFavoriteStats(props.currentUserId)
    if (res.code === 200 && res.data) {
      stats.value = { total: res.data.total || 0, types: res.data.types || {} }
    }
  } catch (error) {
    console.error('[Favorites] 统计加载失败:', error)
  }
}

const loadTags = async () => {
  if (!props.currentUserId) return
  try {
    const res = await getFavoriteTags(props.currentUserId)
    if (res.code === 200) {
      tagList.value = res.data || []
    }
  } catch (error) {
    console.error('[Favorites] 标签加载失败:', error)
  }
}

const load = async (reset = false) => {
  if (!props.currentUserId || loading.value) return
  if (reset) {
    page.value = 1
    hasMore.value = true
  }
  loading.value = true
  try {
    const useRecent = activeKey.value === 'recent' && !keyword.value.trim() && !activeTag.value
    let data = []

    if (useRecent) {
      const res = await getRecentFavorites(props.currentUserId, 30)
      if (res.code === 200) data = res.data || []
      hasMore.value = false
    } else {
      const res = await getFavoriteList({
        userId: props.currentUserId,
        type: typeParam.value || undefined,
        tag: activeTag.value || undefined,
        keyword: keyword.value.trim() || undefined,
        page: page.value,
        size: LIST_SIZE
      })
      if (res.code === 200) data = res.data || []
      hasMore.value = data.length >= LIST_SIZE
    }

    list.value = reset ? data : [...list.value, ...data]
  } catch (error) {
    console.error('[Favorites] 收藏加载失败:', error)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (loading.value || !hasMore.value) return
  page.value += 1
  load(false)
}

const selectCategory = (key) => {
  activeTag.value = ''
  if (activeKey.value === key && !activeTag.value) {
    load(true)
    return
  }
  activeKey.value = key
  load(true)
}

const selectTag = (tag) => {
  activeTag.value = activeTag.value === tag ? '' : tag
  load(true)
}

let searchTimer = null
watch(keyword, () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => load(true), 300)
})

const openComposer = (type) => {
  composer.visible = true
  composer.type = type
  composer.title = ''
  composer.content = ''
  composer.url = ''
  composer.tags = ''
  composer.cover = ''
}

const parseTags = (value) => {
  if (!value) return []
  return value
    .split(/[,，\s]+/)
    .map(t => t.trim())
    .filter(Boolean)
}

const handleCoverSelect = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  try {
    const res = await uploadFile(file)
    if (res.code === 200) {
      composer.cover = res.data
    } else {
      ElMessage.error(res.message || '封面上传失败')
    }
  } catch (error) {
    console.error('[Favorites] 封面上传失败:', error)
  }
  event.target.value = ''
}

const submitComposer = async () => {
  const type = composer.type
  const url = composer.url.trim()
  const title = composer.title.trim()
  const content = composer.content.trim()

  if (type === 'link' && !url) {
    ElMessage.warning('请输入链接地址')
    return
  }
  if (type === 'note' && !title && !content && !composer.cover) {
    ElMessage.warning('请输入笔记内容')
    return
  }

  saving.value = true
  try {
    const res = await addFavorite({
      userId: props.currentUserId,
      type,
      title: title || null,
      content: content || null,
      url: url || null,
      cover: composer.cover || null,
      tags: parseTags(composer.tags),
      source: props.currentUserName || null
    })
    if (res.code === 200) {
      ElMessage.success('已添加到收藏')
      composer.visible = false
      activeKey.value = 'all'
      activeTag.value = ''
      await Promise.all([load(true), loadStats(), loadTags()])
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('[Favorites] 保存失败:', error)
  } finally {
    saving.value = false
  }
}

const openDetail = async (item) => {
  if (!item) return
  detail.item = item
  detail.visible = true
  try {
    await touchFavorite(item.id, props.currentUserId)
  } catch (error) {
    // 忽略最近使用时间更新失败
  }
}

const copyDetail = async () => {
  const item = detail.item
  if (!item) return
  const text = item.url || item.content || item.title || ''
  if (!text) return
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制')
  } catch (error) {
    ElMessage.warning('复制失败，请手动选择')
  }
}

const previewImage = (src) => {
  previewSrc.value = src
}

const handleDelete = async (item) => {
  if (!item) return
  try {
    await ElMessageBox.confirm('确定删除这条收藏吗？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    const res = await deleteFavorite(item.id, props.currentUserId)
    if (res.code === 200) {
      list.value = list.value.filter(i => i.id !== item.id)
      if (detail.item && detail.item.id === item.id) {
        detail.visible = false
      }
      await Promise.all([loadStats(), loadTags()])
      ElMessage.success('已删除')
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('[Favorites] 删除失败:', error)
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm(`确定清空「${currentTitle.value}」下的全部收藏吗？`, '提示', {
      confirmButtonText: '清空',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    const res = await clearFavorites(props.currentUserId, typeParam.value || undefined)
    if (res.code === 200) {
      ElMessage.success('已清空')
      await Promise.all([load(true), loadStats(), loadTags()])
    } else {
      ElMessage.error(res.message || '清空失败')
    }
  } catch (error) {
    console.error('[Favorites] 清空失败:', error)
  }
}

const close = () => {
  emit('close')
}

onMounted(() => {
  load(true)
  loadStats()
  loadTags()
})

onUnmounted(() => {
  if (searchTimer) clearTimeout(searchTimer)
})
</script>

<style scoped>
.favorites-pane {
  flex: 1;
  min-width: 0;
  height: 100%;
  display: flex;
  background: var(--theme-bg-surface);
  overflow: hidden;
  position: relative;
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  color: var(--theme-text-1);
  -webkit-font-smoothing: antialiased;
}

/* ============ 左侧分类栏 ============ */
.fav-side {
  width: 240px;
  flex-shrink: 0;
  height: 100%;
  background: var(--theme-bg-surface-2);
  border-right: 1px solid var(--theme-hairline);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.fav-side-top {
  padding: 14px 12px 8px;
  flex-shrink: 0;
}

.fav-search {
  position: relative;
  display: flex;
  align-items: center;
}

.fav-search-icon {
  position: absolute;
  left: 10px;
  color: var(--theme-text-3);
  pointer-events: none;
}

.fav-search-input {
  width: 100%;
  height: 32px;
  padding: 0 28px 0 30px;
  border: 1px solid transparent;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  font-size: 13px;
  color: var(--theme-text-1);
  outline: none;
  box-sizing: border-box;
  font-family: inherit;
  transition: background 0.2s ease, border-color 0.2s ease;
}

.fav-search-input:focus {
  background: var(--theme-bg-surface);
  border-color: var(--theme-primary);
}

.fav-search-clear {
  position: absolute;
  right: 8px;
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  display: flex;
  align-items: center;
  cursor: pointer;
}

.fav-note-btn {
  margin-top: 10px;
  width: 100%;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: none;
  border-radius: 8px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-size: 13.5px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: var(--theme-shadow-sm);
  transition: background 0.18s ease, box-shadow 0.18s ease;
}

.fav-note-btn:hover {
  background: var(--theme-bg-hover);
}

.fav-cats {
  flex: 1;
  overflow-y: auto;
  padding: 4px 8px 8px;
}

.fav-cat {
  width: 100%;
  height: 38px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 10px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--theme-text-1);
  font-size: 13.5px;
  cursor: pointer;
  text-align: left;
  transition: background 0.15s ease, color 0.15s ease;
}

.fav-cat:hover {
  background: var(--theme-bg-hover);
}

.fav-cat.active {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

.fav-cat-icon {
  flex-shrink: 0;
  color: var(--theme-text-2);
}

.fav-cat.active .fav-cat-icon {
  color: var(--theme-primary-text);
}

.fav-cat-label {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.fav-cat-count {
  flex-shrink: 0;
  font-size: 12px;
  color: var(--theme-text-3);
}

.fav-cat.active .fav-cat-count {
  color: var(--theme-primary-text);
}

.fav-tags {
  flex-shrink: 0;
  max-height: 180px;
  overflow-y: auto;
  border-top: 1px solid var(--theme-hairline);
  padding: 10px 12px 14px;
}

.fav-tags-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12.5px;
  color: var(--theme-text-3);
  margin-bottom: 8px;
}

.fav-tags-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.fav-tag-chip {
  padding: 3px 10px;
  border: none;
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 12px;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.fav-tag-chip:hover {
  background: var(--theme-bg-hover);
}

.fav-tag-chip.active {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

/* ============ 右侧内容区 ============ */
.fav-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.fav-main-head {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 22px 12px;
  border-bottom: 1px solid var(--theme-hairline);
}

.fav-main-title {
  display: flex;
  align-items: baseline;
  gap: 10px;
  min-width: 0;
}

.fav-main-title h2 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.fav-main-count {
  font-size: 12.5px;
  color: var(--theme-text-3);
  flex-shrink: 0;
}

.fav-main-tools {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.fav-tool-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 30px;
  padding: 0 12px;
  border: none;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.18s ease, color 0.18s ease;
}

.fav-tool-btn:hover {
  background: var(--theme-bg-hover);
}

.fav-tool-btn.danger {
  color: var(--theme-danger);
}

.fav-close-btn {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.18s ease;
}

.fav-close-btn:hover {
  background: var(--theme-bg-hover);
}

.fav-close-icon {
  transform: rotate(180deg);
}

.fav-list {
  flex: 1;
  overflow-y: auto;
  padding: 14px 22px 30px;
}

.fav-list::-webkit-scrollbar,
.fav-cats::-webkit-scrollbar,
.fav-tags::-webkit-scrollbar {
  width: 6px;
}

.fav-list::-webkit-scrollbar-thumb,
.fav-cats::-webkit-scrollbar-thumb,
.fav-tags::-webkit-scrollbar-thumb {
  background: var(--theme-scrollbar-thumb);
  border-radius: 3px;
}

.fav-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 90px 0;
  color: var(--theme-text-3);
  font-size: 14px;
}

.fav-state p {
  margin: 0;
}

/* 收藏卡片 */
.fav-card {
  position: relative;
  display: flex;
  align-items: stretch;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.16s ease;
}

.fav-card:hover {
  background: var(--theme-bg-hover);
}

.fav-card-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
}

.fav-card-title {
  margin: 0;
  font-size: 14.5px;
  font-weight: 500;
  color: var(--theme-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fav-card-desc {
  margin: 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: break-word;
}

.fav-card-foot {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--theme-text-3);
  flex-wrap: wrap;
}

.fav-card-source {
  flex-shrink: 0;
}

.fav-card-tag {
  flex-shrink: 0;
  padding: 1px 8px;
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 11.5px;
}

.fav-card-date {
  margin-left: auto;
  flex-shrink: 0;
}

.fav-card-thumb {
  width: 62px;
  height: 62px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.fav-card-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.fav-card-thumb-icon {
  color: var(--theme-primary);
}

.fav-card-del {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 26px;
  height: 26px;
  border: none;
  border-radius: 6px;
  background: var(--theme-bg-glass-strong);
  color: var(--theme-text-3);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  box-shadow: var(--theme-shadow-sm);
  transition: opacity 0.15s ease, color 0.15s ease;
}

.fav-card:hover .fav-card-del {
  opacity: 1;
}

.fav-card-del:hover {
  color: var(--theme-danger);
}

.fav-more {
  text-align: center;
  padding: 18px 0 8px;
}

.fav-more-btn {
  padding: 8px 26px;
  border: none;
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-primary);
  font-size: 13.5px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.fav-more-btn:hover:not(:disabled) {
  background: var(--theme-bg-hover);
}

.fav-more-end {
  color: var(--theme-text-3);
  font-size: 12.5px;
}

/* ============ 弹窗 ============ */
.fav-overlay {
  position: fixed;
  inset: 0;
  z-index: 1300;
  background: var(--theme-overlay);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.fav-window {
  width: 520px;
  max-width: calc(100vw - 40px);
  max-height: calc(100vh - 80px);
  background: var(--theme-bg-elevated);
  border-radius: 14px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: var(--theme-shadow-lg);
}

.fav-window-head {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 13px 18px;
  border-bottom: 1px solid var(--theme-hairline);
}

.fav-window-head h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.fav-window-cancel {
  border: none;
  background: transparent;
  color: var(--theme-text-2);
  font-size: 14px;
  cursor: pointer;
}

.fav-window-cancel:hover {
  color: var(--theme-text-1);
}

.fav-window-submit {
  padding: 7px 20px;
  border: none;
  border-radius: 999px;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.18s ease;
}

.fav-window-submit:hover:not(:disabled) {
  background: var(--theme-primary-hover);
}

.fav-window-submit:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.fav-window-body {
  padding: 16px 18px 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fav-type-switch {
  display: inline-flex;
  padding: 3px;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  align-self: flex-start;
}

.fav-type-item {
  border: none;
  background: transparent;
  padding: 5px 18px;
  border-radius: 6px;
  font-size: 13px;
  color: var(--theme-text-2);
  cursor: pointer;
  transition: background 0.18s ease, color 0.18s ease;
}

.fav-type-item.active {
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  box-shadow: var(--theme-shadow-sm);
}

.fav-field {
  width: 100%;
  padding: 9px 12px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  font-size: 14px;
  color: var(--theme-text-1);
  background: var(--theme-bg-surface);
  outline: none;
  box-sizing: border-box;
  font-family: inherit;
  transition: border-color 0.18s ease;
}

.fav-field:focus {
  border-color: var(--theme-primary);
}

.fav-textarea {
  resize: none;
  line-height: 1.6;
}

.fav-cover-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.fav-cover-preview {
  width: 66px;
  height: 66px;
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--theme-text-3);
  overflow: hidden;
  flex-shrink: 0;
}

.fav-cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fav-cover-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.fav-cover-btn {
  padding: 7px 14px;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.fav-cover-btn:hover {
  background: var(--theme-bg-hover);
}

.fav-cover-btn input {
  display: none;
}

.fav-cover-remove {
  border: none;
  background: transparent;
  color: var(--theme-danger);
  font-size: 13px;
  cursor: pointer;
}

/* 详情 */
.fav-detail-window {
  width: 560px;
}

.fav-detail-type {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13.5px;
  color: var(--theme-text-2);
}

.fav-detail-tools {
  display: flex;
  align-items: center;
  gap: 8px;
}

.fav-detail-body {
  padding: 18px 20px 24px;
  overflow-y: auto;
}

.fav-detail-cover {
  width: 100%;
  max-height: 320px;
  object-fit: contain;
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  cursor: zoom-in;
}

.fav-detail-title {
  margin: 14px 0 10px;
  font-size: 17px;
  font-weight: 600;
}

.fav-detail-content {
  margin: 0 0 14px;
  font-size: 14.5px;
  line-height: 1.7;
  color: var(--theme-text-1);
  white-space: pre-wrap;
  word-break: break-word;
}

.fav-detail-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
  color: var(--theme-primary);
  font-size: 13.5px;
  word-break: break-all;
  text-decoration: none;
}

.fav-detail-link:hover {
  text-decoration: underline;
}

.fav-detail-file {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  font-size: 13.5px;
  color: var(--theme-text-2);
  margin-bottom: 14px;
}

.fav-detail-size {
  color: var(--theme-text-3);
}

.fav-detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 14px;
}

.fav-detail-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

/* 图片预览 */
.fav-preview {
  position: fixed;
  inset: 0;
  z-index: 1400;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
}

.fav-preview img {
  max-width: 90vw;
  max-height: 88vh;
  object-fit: contain;
  border-radius: 6px;
}

/* 过渡 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ============ 响应式 ============ */
@media (max-width: 768px) {
  .favorites-pane {
    flex-direction: column;
  }

  .fav-side {
    width: 100%;
    height: auto;
    flex-shrink: 0;
    border-right: none;
    border-bottom: 1px solid var(--theme-hairline);
  }

  .fav-cats {
    display: flex;
    gap: 6px;
    overflow-x: auto;
    overflow-y: hidden;
    padding: 8px 10px;
    flex: none;
  }

  .fav-cat {
    width: auto;
    flex-shrink: 0;
    padding: 0 12px;
  }

  .fav-cat-label {
    flex: none;
  }

  .fav-tags {
    max-height: 120px;
  }

  .fav-list {
    padding: 12px 14px 24px;
  }

  .fav-main-head {
    padding: 12px 14px 10px;
  }

  .fav-window {
    width: 100%;
    max-width: 100%;
  }
}
</style>
