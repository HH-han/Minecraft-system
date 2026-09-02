<template>
  <div class="cards-container">
    <HeroSection @create="openCreate" @browse="activeTab = 'explore'" />

    <section class="cards-main">
      <!-- 页签 -->
      <div class="tab-nav">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          :class="['tab-item', { active: activeTab === tab.id }]"
          @click="switchTab(tab.id)"
        >
          <span>{{ tab.name }}</span>
        </button>
      </div>

      <!-- 搜索区 -->
      <div class="search-bar">
        <input
          v-model="keyword"
          type="text"
          class="search-input"
          placeholder="搜索卡片标题或内容..."
          @keyup.enter="handleSearch"
        />
        <input
          v-model="location"
          type="text"
          class="search-input location-input"
          placeholder="按地点筛选，如：大理"
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">搜索</button>
      </div>

      <!-- 卡片网格 -->
      <CardGrid
        :cards="cardList"
        :loading="loading"
        :has-more="pageNum < pages"
        :empty-title="activeTab === 'my' ? '还没有发布卡片' : '暂无匹配的卡片'"
        :empty-desc="
          activeTab === 'my'
            ? '用第一张旅行卡片，开启你的旅行记忆'
            : '换个关键词试试，或者发布你自己的旅行卡片'
        "
        :show-create="activeTab === 'my'"
        @detail="openDetail"
        @load-more="loadMore"
        @create="openCreate"
      />
    </section>

    <!-- 详情弹窗 -->
    <CardDetailModal
      :visible="detailVisible"
      :card="detailCard"
      :loading="detailLoading"
      :can-edit="canEdit"
      @close="detailVisible = false"
      @edit="openEdit"
      @delete="handleDelete"
    />

    <!-- 新增 / 编辑弹窗 -->
    <CardFormModal
      :visible="formVisible"
      :card="editingCard"
      @close="formVisible = false"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import {
  getCardList,
  getMyCards,
  getCardDetail,
  createCard,
  updateCard,
  deleteCard
} from '@/api/card'
import HeroSection from './module/HeroSection.vue'
import CardGrid from './module/CardGrid.vue'
import CardDetailModal from './module/CardDetailModal.vue'
import CardFormModal from './module/CardFormModal.vue'

const router = useRouter()
const authStore = useAuthStore()
const currentUserId = computed(() => authStore.userInfo?.id)

const tabs = [
  { id: 'explore', name: '发现卡片' },
  { id: 'my', name: '我的卡片' }
]
const activeTab = ref('explore')

// 搜索与分页
const keyword = ref('')
const location = ref('')
const appliedKeyword = ref('')
const appliedLocation = ref('')
const cardList = ref([])
const pageNum = ref(1)
const pages = ref(0)
const loading = ref(false)

// 详情弹窗
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailCard = ref(null)

// 表单弹窗
const formVisible = ref(false)
const editingCard = ref(null)

const canEdit = computed(() => {
  return (
    detailCard.value?.id &&
    currentUserId.value &&
    Number(detailCard.value.userId) === Number(currentUserId.value)
  )
})

onMounted(() => {
  fetchCards(true)
})

function switchTab(tabId) {
  if (activeTab.value === tabId) return
  activeTab.value = tabId
  if (tabId === 'my' && !currentUserId.value) {
    ElMessage.warning('请先登录后查看我的卡片')
    activeTab.value = 'explore'
    router.push('/login')
    return
  }
  handleSearch()
}

function handleSearch() {
  appliedKeyword.value = keyword.value.trim()
  appliedLocation.value = location.value.trim()
  fetchCards(true)
}

async function fetchCards(reset = false) {
  if (reset) {
    pageNum.value = 1
    cardList.value = []
    pages.value = 0
  }
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: 9
    }
    if (appliedKeyword.value) params.keyword = appliedKeyword.value
    if (appliedLocation.value) params.location = appliedLocation.value

    const res =
      activeTab.value === 'my'
        ? await getMyCards(params)
        : await getCardList(params)

    if (res?.code === 200 && res.data) {
      const records = res.data.records || []
      pages.value = Number(res.data.pages) || 0
      cardList.value = reset ? records : [...cardList.value, ...records]
    } else {
      if (reset) cardList.value = []
    }
  } catch (error) {
    console.error('获取卡片列表失败:', error)
    ElMessage.error('获取卡片列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function loadMore() {
  if (pageNum.value >= pages.value) return
  pageNum.value += 1
  fetchCards(false)
}

async function openDetail(card) {
  detailVisible.value = true
  detailLoading.value = true
  detailCard.value = card
  try {
    const res = await getCardDetail(card.id)
    if (res?.code === 200 && res.data) {
      detailCard.value = res.data
    }
  } catch (error) {
    console.error('获取卡片详情失败:', error)
    ElMessage.error('获取卡片详情失败')
  } finally {
    detailLoading.value = false
  }
}

function checkLogin() {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return false
  }
  return true
}

function openCreate() {
  if (!checkLogin()) return
  editingCard.value = null
  formVisible.value = true
}

function openEdit(card) {
  if (!checkLogin()) return
  detailVisible.value = false
  editingCard.value = card
  formVisible.value = true
}

async function handleSubmit(payload, done) {
  try {
    const res = editingCard.value?.id
      ? await updateCard(payload)
      : await createCard(payload)
    if (res?.code === 200) {
      ElMessage.success(editingCard.value?.id ? '更新成功' : '发布成功')
      formVisible.value = false
      editingCard.value = null
      // 停留在当前页签并刷新列表
      fetchCards(true)
    } else {
      ElMessage.error(res?.message || '保存失败')
    }
  } catch (error) {
    console.error('保存卡片失败:', error)
    ElMessage.error(error?.response?.data?.message || '保存失败，请稍后重试')
  } finally {
    done?.()
  }
}

async function handleDelete(card) {
  if (!card?.id) return
  try {
    await ElMessageBox.confirm('确定删除这张卡片吗？删除后不可恢复。', '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    })
  } catch {
    return
  }
  try {
    const res = await deleteCard(card.id)
    if (res?.code === 200) {
      ElMessage.success('删除成功')
      detailVisible.value = false
      fetchCards(true)
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除卡片失败:', error)
    ElMessage.error(error?.response?.data?.message || '删除失败，请稍后重试')
  }
}
</script>

<style scoped>
.cards-container {
  --color-text: #1d1d1f;
  --color-text-secondary: #6e6e73;
  --color-primary: #2997ff;
  --color-primary-hover: #0066cc;
  --color-bg: #ffffff;
  --color-bg-secondary: #f5f5f7;
  --color-border: #d2d2d7;
  --color-danger: #ff3b30;

  --font-family: 'SF Pro Display', 'SF Pro', 'PingFang SC', -apple-system,
    BlinkMacSystemFont, 'Segoe UI', sans-serif;

  --spacing-4: 4px;
  --spacing-6: 6px;
  --spacing-8: 8px;
  --spacing-10: 10px;
  --spacing-12: 12px;
  --spacing-16: 16px;
  --spacing-20: 20px;
  --spacing-24: 24px;
  --spacing-28: 28px;
  --spacing-32: 32px;
  --spacing-40: 40px;
  --spacing-60: 60px;

  --radius-sm: 12px;
  --radius-md: 20px;
  --radius-lg: 24px;
  --radius-xl: 28px;
  --radius-full: 980px;

  --shadow-lg: 0 20px 30px -12px rgba(0, 0, 0, 0.1);

  --transition-fast: 0.15s ease;
  --transition-normal: 0.2s ease;
  --transition-slow: 0.3s ease;

  padding: 0 22px;
  font-family: var(--font-family);
  color: var(--color-text);
  scroll-behavior: smooth;
}

.cards-main {
  padding-bottom: var(--spacing-40);
}

/* 页签 */
.tab-nav {
  display: flex;
  gap: var(--spacing-8);
  margin-bottom: var(--spacing-24);
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 1px;
}

.tab-item {
  padding: var(--spacing-12) var(--spacing-20);
  background: transparent;
  border: none;
  font-size: 17px;
  font-family: inherit;
  color: var(--color-text-secondary);
  cursor: pointer;
  position: relative;
  transition: all var(--transition-normal);
  border-radius: var(--radius-md) var(--radius-md) 0 0;
}

.tab-item:hover {
  color: var(--color-text);
  background: rgba(0, 0, 0, 0.03);
}

.tab-item.active {
  color: var(--color-text);
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: var(--spacing-8);
  right: var(--spacing-8);
  height: 3px;
  background: var(--color-primary);
  border-radius: 2px;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-32);
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  padding: var(--spacing-10) var(--spacing-20);
  font-size: 15px;
  font-family: inherit;
  color: var(--color-text);
  background: var(--color-bg);
  outline: none;
  transition: border-color var(--transition-fast),
    box-shadow var(--transition-fast);
}

.location-input {
  flex: 0 0 220px;
}

.search-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.15);
}

.search-btn {
  border: none;
  background: var(--color-text);
  color: #fff;
  padding: var(--spacing-10) var(--spacing-28);
  border-radius: var(--radius-full);
  font-size: 15px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.search-btn:hover {
  background: #000;
  transform: scale(1.02);
}

.search-btn:active {
  transform: scale(0.98);
}

@media (max-width: 1199px) and (min-width: 768px) {
  .cards-container {
    padding: 0 var(--spacing-20);
  }
}

@media (max-width: 767px) {
  .cards-container {
    padding: 0 var(--spacing-16);
  }
  .location-input {
    flex: 1 1 100%;
  }
  .search-btn {
    width: 100%;
  }
}
</style>
