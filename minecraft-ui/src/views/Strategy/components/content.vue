<template>
  <div class="group-container">
    <HeroSection @create="showCreateModal = true" @join="activeTab = 'explore'" />

    <Recommend />

    <section class="group-main">
      <TabNavigation :tabs="tabs" v-model:activeTab="activeTab" />

      <MyGroups
        v-if="activeTab === 'my-groups'"
        :groups="myGroups"
        :loading="loading"
        v-model:search-keyword="searchKeyword"
        :default-cover="defaultCover"
        @search="searchGroups"
        @create="showCreateModal = true"
        @detail="openGroupDetail"
      />

      <ExploreGroups
        v-if="activeTab === 'explore'"
        :groups="exploreGroups"
        :filters="exploreFilters"
        v-model:active-filter="activeFilter"
        :default-cover="defaultCover"
        @detail="openGroupDetail"
        @join="applyToJoin"
      />

      <Notifications
        v-if="activeTab === 'notifications'"
        :notifications="notifications"
        @mark-all-read="markAllRead"
        @handle="handleNotification"
        @mark-as-read="markAsRead"
      />
    </section>

    <CreateGroupModal
      :visible="showCreateModal"
      @close="showCreateModal = false"
      @submit="handleCreateGroup"
    />

    <GroupDetailModal
      :visible="showDetailModal"
      :group="currentGroup"
      :detail-tabs="detailTabs"
      v-model:active-tab="detailTab"
      :is-admin="isGroupAdmin"
      :current-user-id="currentUserId"
      :members="groupMembers"
      :posts="groupPosts"
      :trip-plan="tripPlan"
      :bills="groupBills"
      :album="groupAlbum"
      :polls="groupPolls"
      :diary="travelDiary"
      :default-avatar="defaultAvatar"
      @close="closeGroupDetail"
      @invite="showInviteModal = true"
      @remove-member="removeMember"
      @create-post="handleCreatePost"
      @delete-post="deletePost"
      @toggle-like="toggleLike"
      @show-comments="showComments"
      @add-trip="showAddTripModal = true"
      @generate-trip="generateAITrip"
      @delete-trip="deleteTripItem"
      @add-bill="showAddBillModal = true"
      @get-settlement="getSettlement"
      @upload-photos="handlePhotoUpload"
      @delete-photo="deletePhoto"
      @create-poll="showCreatePollModal = true"
      @vote="handleVote"
      @generate-diary="generateDiary"
      @export-diary="exportDiary"
    />

    <InviteMembersModal
      :visible="showInviteModal"
      @close="showInviteModal = false"
      @submit="handleInviteMembers"
    />

    <AddTripModal
      :visible="showAddTripModal"
      @close="showAddTripModal = false"
      @submit="handleAddTrip"
    />

    <AddBillModal
      :visible="showAddBillModal"
      @close="showAddBillModal = false"
      @submit="handleAddBill"
    />

    <CreatePollModal
      :visible="showCreatePollModal"
      @close="showCreatePollModal = false"
      @submit="handleCreatePoll"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import * as groupApi from '@/api/travelGroup'
import * as postApi from '@/api/groupPost'
import * as tripApi from '@/api/groupTripPlan'
import * as billApi from '@/api/groupBill'
import * as albumApi from '@/api/groupAlbum'
import * as notificationApi from '@/api/groupNotification'
import * as aiTripApi from '@/api/aiTrip'
import * as pollApi from '@/api/groupPoll'
import * as diaryApi from '@/api/travelDiary'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

import HeroSection from './module/HeroSection.vue'
import Recommend from './recommend.vue'
import TabNavigation from './module/TabNavigation.vue'
import MyGroups from './module/MyGroups.vue'
import ExploreGroups from './module/ExploreGroups.vue'
import Notifications from './module/Notifications.vue'
import CreateGroupModal from './module/CreateGroupModal.vue'
import GroupDetailModal from './module/GroupDetailModal.vue'
import InviteMembersModal from './module/InviteMembersModal.vue'
import AddTripModal from './module/AddTripModal.vue'
import AddBillModal from './module/AddBillModal.vue'
import CreatePollModal from './module/CreatePollModal.vue'

const { t } = useI18n()

const authStore = useAuthStore()
const currentUserId = computed(() => authStore.userInfo?.id)

const defaultCover = 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800'
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100'

const tabs = computed(() => [
  { id: 'my-groups', name: t('strategy.tabs.myGroups') },
  { id: 'explore', name: t('strategy.tabs.explore') },
  { id: 'notifications', name: t('strategy.tabs.notifications') }
])

const activeTab = ref('my-groups')
const exploreFilters = computed(() => [
  { id: 'all', name: t('strategy.filters.all') },
  { id: 'active', name: t('strategy.filters.active') },
  { id: 'ended', name: t('strategy.filters.ended') }
])
const activeFilter = ref('all')

const detailTabs = computed(() => [
  { id: 'members', name: t('strategy.detailTabs.members') },
  { id: 'posts', name: t('strategy.detailTabs.posts') },
  { id: 'trip', name: t('strategy.detailTabs.trip') },
  { id: 'bills', name: t('strategy.detailTabs.bills') },
  { id: 'album', name: t('strategy.detailTabs.album') },
  { id: 'poll', name: t('strategy.detailTabs.poll') },
  { id: 'diary', name: t('strategy.detailTabs.diary') }
])
const detailTab = ref('members')

const loading = ref(false)
const myGroups = ref([])
const exploreGroups = ref([])
const notifications = ref([])
const searchKeyword = ref('')

const showCreateModal = ref(false)
const showDetailModal = ref(false)
const showInviteModal = ref(false)
const showAddTripModal = ref(false)
const showAddBillModal = ref(false)
const showCreatePollModal = ref(false)

const currentGroup = ref({})
const isGroupAdmin = computed(() => {
  return currentGroup.value.userRole === 'owner' || currentGroup.value.userRole === 'admin'
})

const groupMembers = ref([])
const groupPosts = ref([])
const tripPlan = ref([])
const groupBills = ref([])
const groupAlbum = ref([])
const groupPolls = ref([])
const travelDiary = ref(null)

onMounted(() => {
  fetchMyGroups()
  fetchNotifications()
})

watch(activeTab, (val) => {
  if (val === 'explore' && exploreGroups.value.length === 0) {
    fetchExploreGroups()
  }
})

async function fetchMyGroups() {
  loading.value = true
  try {
    const res = await groupApi.getMyGroups()
    if (res.code === 200) {
      myGroups.value = res.data || []
    }
  } catch (error) {
    console.error('获取群组列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchMyGroups)

async function searchGroups() {
  if (!searchKeyword.value) {
    fetchMyGroups()
    return
  }
  loading.value = true
  try {
    const res = await groupApi.searchPublicGroups(searchKeyword.value)
    if (res.code === 200) {
      myGroups.value = res.data || []
    }
  } catch (error) {
    console.error('搜索群组失败:', error)
  } finally {
    loading.value = false
  }
}

async function fetchExploreGroups() {
  try {
    const res = await groupApi.searchPublicGroups('')
    if (res.code === 200) {
      exploreGroups.value = res.data || []
    }
  } catch (error) {
    console.error('获取公开群组失败:', error)
  }
}

async function fetchNotifications() {
  try {
    const res = await notificationApi.getNotifications()
    if (res.code === 200) {
      notifications.value = res.data || []
    }
  } catch (error) {
    console.error('获取通知失败:', error)
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchNotifications)

async function handleCreateGroup(form) {
  try {
    const res = await groupApi.createGroup(form)
    if (res.code === 200) {
      showCreateModal.value = false
      ElMessage.success(t('strategy.toast.createSuccess'))
      fetchMyGroups()
    }
  } catch (error) {
    console.error('创建群组失败:', error)
  }
}

async function openGroupDetail(group) {
  currentGroup.value = group
  showDetailModal.value = true
  detailTab.value = 'members'
  await Promise.all([
    fetchMembers(group.id),
    fetchPosts(group.id),
    fetchTripPlan(group.id),
    fetchBills(group.id),
    fetchAlbum(group.id),
    fetchPolls(group.id)
  ])
}

function closeGroupDetail() {
  showDetailModal.value = false
  currentGroup.value = {}
}

async function fetchMembers(groupId) {
  try {
    const res = await groupApi.getMembers(groupId)
    if (res.code === 200) {
      groupMembers.value = res.data || []
    }
  } catch (error) {
    console.error('获取成员失败:', error)
  }
}

async function fetchPosts(groupId) {
  try {
    const res = await postApi.getPostList(groupId)
    if (res.code === 200) {
      groupPosts.value = res.data || []
    }
  } catch (error) {
    console.error('获取动态失败:', error)
  }
}

async function fetchTripPlan(groupId) {
  try {
    const res = await tripApi.getTripPlanByDay(groupId)
    if (res.code === 200) {
      tripPlan.value = res.data || []
    }
  } catch (error) {
    console.error('获取行程失败:', error)
  }
}

async function fetchBills(groupId) {
  try {
    const res = await billApi.getBillList(groupId)
    if (res.code === 200) {
      groupBills.value = res.data || []
    }
  } catch (error) {
    console.error('获取账单失败:', error)
  }
}

async function fetchAlbum(groupId) {
  try {
    const res = await albumApi.getAlbumList(groupId)
    if (res.code === 200) {
      groupAlbum.value = res.data || []
    }
  } catch (error) {
    console.error('获取相册失败:', error)
  }
}

async function fetchPolls(groupId) {
  try {
    const res = await pollApi.getPollList(groupId)
    if (res.code === 200) {
      groupPolls.value = res.data || []
    }
  } catch (error) {
    console.error('获取投票失败:', error)
  }
}

async function applyToJoin(groupId) {
  try {
    const res = await groupApi.applyToJoin(groupId)
    if (res.code === 200) {
      ElMessage.success(t('strategy.toast.applySuccess'))
      fetchExploreGroups()
    }
  } catch (error) {
    console.error('申请加入失败:', error)
  }
}

async function handleInviteMembers(ids) {
  try {
    const res = await groupApi.inviteMembers(currentGroup.value.id, ids)
    if (res.code === 200) {
      showInviteModal.value = false
      fetchMembers(currentGroup.value.id)
    }
  } catch (error) {
    console.error('邀请成员失败:', error)
  }
}

async function removeMember(userId) {
  try {
    await ElMessageBox.confirm(
      t('strategy.confirm.removeMember'),
      t('strategy.confirm.title'),
      { type: 'warning' }
    )
  } catch {
    return
  }
  try {
    const res = await groupApi.removeMember(currentGroup.value.id, userId)
    if (res.code === 200) {
      fetchMembers(currentGroup.value.id)
    }
  } catch (error) {
    console.error('移除成员失败:', error)
  }
}

async function handleCreatePost(content) {
  try {
    const res = await postApi.createPost(currentGroup.value.id, { content })
    if (res.code === 200) {
      fetchPosts(currentGroup.value.id)
    }
  } catch (error) {
    console.error('发布动态失败:', error)
  }
}

async function deletePost(postId) {
  try {
    await ElMessageBox.confirm(
      t('strategy.confirm.deletePost'),
      t('strategy.confirm.title'),
      { type: 'warning' }
    )
  } catch {
    return
  }
  try {
    const res = await postApi.deletePost(currentGroup.value.id, postId)
    if (res.code === 200) {
      fetchPosts(currentGroup.value.id)
    }
  } catch (error) {
    console.error('删除动态失败:', error)
  }
}

async function toggleLike(post) {
  try {
    const res = await postApi.toggleLike(currentGroup.value.id, post.id)
    if (res.code === 200) {
      post.isLiked = !post.isLiked
      post.likeCount = (post.likeCount || 0) + (post.isLiked ? 1 : -1)
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

function showComments(post) {
  console.log('查看评论:', post.id)
}

async function handleAddTrip(form) {
  try {
    const res = await tripApi.addTripItem(currentGroup.value.id, form)
    if (res.code === 200) {
      showAddTripModal.value = false
      fetchTripPlan(currentGroup.value.id)
    }
  } catch (error) {
    console.error('添加行程失败:', error)
  }
}

async function deleteTripItem(tripId) {
  try {
    await ElMessageBox.confirm(
      t('strategy.confirm.deleteTrip'),
      t('strategy.confirm.title'),
      { type: 'warning' }
    )
  } catch {
    return
  }
  try {
    const res = await tripApi.deleteTripItem(currentGroup.value.id, tripId)
    if (res.code === 200) {
      fetchTripPlan(currentGroup.value.id)
    }
  } catch (error) {
    console.error('删除行程失败:', error)
  }
}

async function generateAITrip() {
  try {
    const res = await aiTripApi.generateTrip({
      groupId: currentGroup.value.id,
      destination: currentGroup.value.destination
    })
    if (res.code === 200) {
      ElMessage.success(t('strategy.toast.aiTripReady'))
      fetchTripPlan(currentGroup.value.id)
    }
  } catch (error) {
    console.error('AI规划失败:', error)
  }
}

async function handleAddBill(form) {
  try {
    const res = await billApi.addBill(currentGroup.value.id, form)
    if (res.code === 200) {
      showAddBillModal.value = false
      fetchBills(currentGroup.value.id)
    }
  } catch (error) {
    console.error('添加账单失败:', error)
  }
}

async function getSettlement() {
  try {
    const res = await billApi.getSettlement(currentGroup.value.id)
    if (res.code === 200) {
      ElMessageBox.alert(JSON.stringify(res.data, null, 2), t('strategy.bills.settlement'))
    }
  } catch (error) {
    console.error('获取结算失败:', error)
  }
}

async function handlePhotoUpload(files) {
  try {
    const res = await albumApi.uploadPhotos(currentGroup.value.id, files)
    if (res.code === 200) {
      fetchAlbum(currentGroup.value.id)
    }
  } catch (error) {
    console.error('上传照片失败:', error)
  }
}

async function deletePhoto(photoId) {
  try {
    await ElMessageBox.confirm(
      t('strategy.confirm.deletePhoto'),
      t('strategy.confirm.title'),
      { type: 'warning' }
    )
  } catch {
    return
  }
  try {
    const res = await albumApi.deletePhoto(currentGroup.value.id, photoId)
    if (res.code === 200) {
      fetchAlbum(currentGroup.value.id)
    }
  } catch (error) {
    console.error('删除照片失败:', error)
  }
}

async function handleCreatePoll({ title, options }) {
  try {
    const res = await pollApi.createPoll({
      groupId: currentGroup.value.id,
      title,
      options
    })
    if (res.code === 200) {
      showCreatePollModal.value = false
      fetchPolls(currentGroup.value.id)
    }
  } catch (error) {
    console.error('创建投票失败:', error)
  }
}

async function handleVote(pollId, optionIndex) {
  try {
    const res = await pollApi.vote(pollId, { optionId: optionIndex })
    if (res.code === 200) {
      fetchPolls(currentGroup.value.id)
    }
  } catch (error) {
    console.error('投票失败:', error)
  }
}

async function generateDiary() {
  try {
    const res = await diaryApi.generateDiary(currentGroup.value.id)
    if (res.code === 200) {
      travelDiary.value = res.data
    }
  } catch (error) {
    console.error('生成日记失败:', error)
  }
}

async function exportDiary() {
  if (!travelDiary.value?.id) return
  try {
    await diaryApi.exportDiary(travelDiary.value.id, 'json')
  } catch (error) {
    console.error('导出日记失败:', error)
  }
}

async function markAsRead(id) {
  try {
    await notificationApi.markAsRead(id)
    const item = notifications.value.find(n => n.id === id)
    if (item) item.isRead = true
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

async function markAllRead() {
  try {
    await notificationApi.markAllRead()
    notifications.value.forEach(n => n.isRead = true)
  } catch (error) {
    console.error('全部已读失败:', error)
  }
}

function handleNotification(item) {
  markAsRead(item.id)
  if (item.type === 'group_invite' || item.type === 'join_request') {
    activeTab.value = 'my-groups'
  }
}
</script>

<style scoped>
.group-container {
  --color-text: var(--theme-text-1);
  --color-text-secondary: var(--theme-text-2);
  --color-primary: var(--theme-primary);
  --color-primary-hover: var(--theme-primary-hover);
  --color-bg: var(--theme-bg-app);
  --color-bg-secondary: var(--theme-bg-surface-2);
  --color-border: var(--theme-border);
  --color-success: var(--theme-success);
  --color-danger: var(--theme-danger);
  --color-warning: var(--theme-warning);

  --font-family: 'SF Pro Display', 'SF Pro', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  --spacing-2: 2px;
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
  --spacing-48: 48px;
  --spacing-60: 60px;
  --spacing-80: 80px;

  --radius-sm: 12px;
  --radius-md: 16px;
  --radius-lg: 22px;
  --radius-xl: 28px;
  --radius-full: 980px;

  --shadow-sm: var(--theme-shadow-sm);
  --shadow-md: var(--theme-shadow-md);
  --shadow-lg: var(--theme-shadow-lg);

  --transition-fast: var(--theme-duration-fast) ease;
  --transition-normal: var(--theme-duration) var(--theme-ease);
  --transition-slow: var(--theme-duration-slow) var(--theme-ease);

  display: flex;
  flex-direction: column;
  gap: 48px;
  font-family: var(--font-family);
  color: var(--color-text);
  scroll-behavior: smooth;
}

.group-main {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

@media (max-width: 767px) {
  .group-container {
    gap: 32px;
  }

  .group-main {
    gap: 20px;
  }
}
</style>
