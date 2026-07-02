<template>
  <div class="group-container">
    <HeroSection @create="showCreateModal = true" @join="showSearchModal = true" />

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

import HeroSection from './module/HeroSection.vue'
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

const authStore = useAuthStore()
const currentUserId = computed(() => authStore.userInfo?.id)

const defaultCover = 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800'
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100'

const tabs = [
  { id: 'my-groups', name: '我的群组', icon: new URL('@/assets/icons/groups.svg', import.meta.url).href },
  { id: 'explore', name: '发现群组', icon: new URL('@/assets/icons/search.svg', import.meta.url).href },
  { id: 'notifications', name: '通知', icon: new URL('@/assets/icons/notifications.svg', import.meta.url).href }
]

const activeTab = ref('my-groups')
const exploreFilters = [
  { id: 'all', name: '全部' },
  { id: 'active', name: '进行中' },
  { id: 'ended', name: '已结束' }
]
const activeFilter = ref('all')

const detailTabs = [
  { id: 'members', name: '成员' },
  { id: 'posts', name: '动态' },
  { id: 'trip', name: '行程' },
  { id: 'bills', name: '账单' },
  { id: 'album', name: '相册' },
  { id: 'poll', name: '投票' },
  { id: 'diary', name: '日记' }
]
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
const showSearchModal = ref(false)

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

async function handleCreateGroup(form) {
  try {
    const res = await groupApi.createGroup(form)
    if (res.code === 200) {
      showCreateModal.value = false
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
      alert('申请已提交')
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
  if (!confirm('确定要移除该成员吗？')) return
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
  if (!confirm('确定删除该动态？')) return
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
  if (!confirm('确定删除该行程？')) return
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
      alert('AI行程规划已生成，请查看行程列表')
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
      alert('结算详情：' + JSON.stringify(res.data, null, 2))
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
  if (!confirm('确定删除该照片？')) return
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
  --color-text: #1d1d1f;
  --color-text-secondary: #6e6e73;
  --color-text-dark-bg: #ffffff;
  --color-text-secondary-dark-bg: #a1a1a6;
  --color-primary: #2997ff;
  --color-primary-hover: #0066cc;
  --color-bg: #ffffff;
  --color-bg-secondary: #f5f5f7;
  --color-bg-dark: #000000;
  --color-border: #d2d2d6;
  --color-success: #34c759;
  --color-danger: #ff3b30;
  --color-warning: #ffd60a;

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
  --radius-md: 20px;
  --radius-lg: 24px;
  --radius-xl: 28px;
  --radius-full: 980px;

  --shadow-sm: 0 4px 12px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 8px 24px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 20px 30px -12px rgba(0, 0, 0, 0.1);

  --transition-fast: 0.15s ease;
  --transition-normal: 0.2s ease;
  --transition-slow: 0.3s ease;

  padding: 0 22px;
  font-family: var(--font-family);
  color: var(--color-text);
  scroll-behavior: smooth;
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
  border: none;
  padding: var(--spacing-12) var(--spacing-28);
  border-radius: var(--radius-full);
  font-size: 17px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-4);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-primary:active {
  transform: scale(0.98);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: transparent;
  color: var(--color-primary);
  border: none;
  padding: var(--spacing-12) var(--spacing-28);
  border-radius: var(--radius-full);
  font-size: 17px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-4);
}

.btn-secondary:hover {
  background: rgba(41, 151, 255, 0.1);
}

.btn-secondary:active {
  transform: scale(0.98);
}

.btn-small {
  padding: var(--spacing-8) var(--spacing-16);
  font-size: 14px;
  border-radius: var(--radius-md);
}

@media (max-width: 1199px) and (min-width: 768px) {
  .group-container {
    padding: 0 var(--spacing-20);
  }
}

@media (max-width: 767px) {
  .group-container {
    padding: 0 var(--spacing-16);
  }
}
</style>