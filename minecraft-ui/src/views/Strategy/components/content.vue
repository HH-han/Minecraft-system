<template>
  <div class="group-container">
    <!-- Hero Section -->
    <section class="group-hero">
      <div class="hero-content">
        <h1>旅行群组</h1>
        <p class="hero-subtitle">与志同道合的旅伴一起规划、分享、记录精彩旅程</p>
        <div class="hero-actions">
          <button class="btn-primary" @click="showCreateModal = true">
            创建群组
          </button>
          <button class="btn-secondary" @click="showSearchModal = true">
            加入群组
          </button>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <section class="group-main">
      <!-- Tab Navigation -->
      <div class="tab-nav">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          :class="['tab-item', { active: activeTab === tab.id }]"
          @click="activeTab = tab.id"
        >
          <span class="tab-icon">{{ tab.icon }}</span>
          <span class="tab-text">{{ tab.name }}</span>
        </button>
      </div>

      <!-- My Groups -->
      <div v-if="activeTab === 'my-groups'" class="tab-content">
        <div class="section-header">
          <h2>我的群组</h2>
          <div class="search-bar">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索群组..."
              @keyup.enter="searchGroups"
            />
            <button @click="searchGroups">
              <i class="search-icon">⌕</i>
            </button>
          </div>
        </div>

        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <div v-else-if="myGroups.length === 0" class="empty-state">
          <div class="empty-icon">👥</div>
          <h3>还没有加入任何群组</h3>
          <p>创建或加入一个旅行群组，开始你的旅程</p>
          <button class="btn-primary" @click="showCreateModal = true">创建群组</button>
        </div>

        <div v-else class="group-grid">
          <div
            v-for="group in myGroups"
            :key="group.id"
            class="group-card"
            @click="openGroupDetail(group)"
          >
            <div class="card-image">
              <img :src="group.coverUrl || defaultCover" :alt="group.name" />
              <span v-if="group.status === 1" class="status-badge active">进行中</span>
              <span v-else class="status-badge ended">已结束</span>
            </div>
            <div class="card-content">
              <h3>{{ group.name }}</h3>
              <p class="destination">
                <span class="location-icon">◎</span>
                {{ group.destination || '未设置目的地' }}
              </p>
              <div class="card-meta">
                <span class="member-count">
                  <span class="icon">👤</span>
                  {{ group.currentMembers || 0 }}/{{ group.maxMembers || 20 }}
                </span>
                <span class="date-range" v-if="group.startDate">
                  {{ formatDate(group.startDate) }} - {{ formatDate(group.endDate) }}
                </span>
              </div>
              <div class="card-role" v-if="group.userRole">
                <span :class="['role-tag', group.userRole]">
                  {{ getRoleName(group.userRole) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Explore Groups -->
      <div v-if="activeTab === 'explore'" class="tab-content">
        <div class="section-header">
          <h2>发现群组</h2>
          <div class="filter-tabs">
            <button
              v-for="filter in exploreFilters"
              :key="filter.id"
              :class="['filter-btn', { active: activeFilter === filter.id }]"
              @click="activeFilter = filter.id"
            >
              {{ filter.name }}
            </button>
          </div>
        </div>

        <div v-if="exploreGroups.length === 0" class="empty-state">
          <div class="empty-icon">🔍</div>
          <h3>暂无公开群组</h3>
          <p>成为第一个创建公开群组的人吧</p>
        </div>

        <div v-else class="group-grid">
          <div
            v-for="group in exploreGroups"
            :key="group.id"
            class="group-card"
            @click="openGroupDetail(group)"
          >
            <div class="card-image">
              <img :src="group.coverUrl || defaultCover" :alt="group.name" />
              <span class="member-count-overlay">
                {{ group.currentMembers || 0 }} 成员
              </span>
            </div>
            <div class="card-content">
              <h3>{{ group.name }}</h3>
              <p class="destination">
                <span class="location-icon">◎</span>
                {{ group.destination || '未设置目的地' }}
              </p>
              <p class="description">{{ group.description || '暂无描述' }}</p>
              <div class="card-footer">
                <span class="creator">创建者：{{ group.creatorName }}</span>
                <button
                  v-if="!group.isJoined"
                  class="join-btn"
                  @click.stop="applyToJoin(group.id)"
                >
                  加入
                </button>
                <span v-else class="joined-tag">已加入</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Notifications -->
      <div v-if="activeTab === 'notifications'" class="tab-content">
        <div class="section-header">
          <h2>通知中心</h2>
          <button v-if="notifications.length > 0" class="mark-all-btn" @click="markAllRead">
            全部已读
          </button>
        </div>

        <div v-if="notifications.length === 0" class="empty-state">
          <div class="empty-icon">🔔</div>
          <h3>暂无通知</h3>
          <p>所有消息已处理完毕</p>
        </div>

        <div v-else class="notification-list">
          <div
            v-for="item in notifications"
            :key="item.id"
            :class="['notification-item', { unread: !item.isRead }]"
            @click="handleNotification(item)"
          >
            <div class="notification-icon">
              {{ getNotificationIcon(item.type) }}
            </div>
            <div class="notification-content">
              <p class="notification-title">{{ item.title }}</p>
              <p class="notification-message">{{ item.message }}</p>
              <span class="notification-time">{{ formatTime(item.createdAt) }}</span>
            </div>
            <button v-if="!item.isRead" class="read-btn" @click.stop="markAsRead(item.id)">
              ✓
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Create Group Modal -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>创建群组</h2>
          <button class="close-btn" @click="showCreateModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>群组名称 *</label>
            <input v-model="newGroup.name" type="text" placeholder="给群组起个名字" />
          </div>
          <div class="form-group">
            <label>目的地</label>
            <input v-model="newGroup.destination" type="text" placeholder="旅行目的地" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>开始日期</label>
              <input v-model="newGroup.startDate" type="date" />
            </div>
            <div class="form-group">
              <label>结束日期</label>
              <input v-model="newGroup.endDate" type="date" />
            </div>
          </div>
          <div class="form-group">
            <label>群组封面</label>
            <div class="cover-upload">
              <img v-if="newGroup.coverUrl" :src="newGroup.coverUrl" alt="封面预览" />
              <input
                type="file"
                accept="image/*"
                @change="handleCoverUpload"
                placeholder="上传封面"
              />
            </div>
          </div>
          <div class="form-group">
            <label>群组描述</label>
            <textarea v-model="newGroup.description" placeholder="描述一下这个群组"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>最大人数</label>
              <input v-model.number="newGroup.maxMembers" type="number" min="2" max="100" />
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input v-model="newGroup.isPublic" type="checkbox" />
                设为公开群组
              </label>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="showCreateModal = false">取消</button>
          <button class="btn-primary" @click="createGroup" :disabled="creating">
            {{ creating ? '创建中...' : '创建' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Group Detail Modal -->
    <div v-if="showDetailModal" class="modal-overlay" @click.self="closeGroupDetail">
      <div class="modal modal-large">
        <div class="modal-header">
          <div class="group-info">
            <h2>{{ currentGroup.name }}</h2>
            <span class="group-status">{{ currentGroup.destination }}</span>
          </div>
          <button class="close-btn" @click="closeGroupDetail">×</button>
        </div>

        <!-- Group Tabs -->
        <div class="detail-tabs">
          <button
            v-for="dt in detailTabs"
            :key="dt.id"
            :class="['detail-tab', { active: detailTab === dt.id }]"
            @click="detailTab = dt.id"
          >
            {{ dt.name }}
          </button>
        </div>

        <div class="modal-body detail-body">
          <!-- Members Tab -->
          <div v-if="detailTab === 'members'" class="members-tab">
            <div class="tab-actions" v-if="isGroupAdmin">
              <button class="btn-small" @click="showInviteModal = true">邀请成员</button>
            </div>
            <div class="member-list">
              <div v-for="member in groupMembers" :key="member.id" class="member-item">
                <img :src="member.avatar || defaultAvatar" :alt="member.nickname" class="avatar" />
                <div class="member-info">
                  <span class="name">{{ member.nickname || '未设置昵称' }}</span>
                  <span class="join-time">加入于 {{ formatTime(member.joinTime) }}</span>
                </div>
                <span :class="['role-badge', member.role]">
                  {{ getRoleName(member.role) }}
                </span>
                <div v-if="isGroupAdmin && member.role !== 'owner'" class="member-actions">
                  <button @click="removeMember(member.userId)">移除</button>
                </div>
              </div>
            </div>
          </div>

          <!-- Posts Tab -->
          <div v-if="detailTab === 'posts'" class="posts-tab">
            <div class="post-create">
              <textarea v-model="newPost.content" placeholder="分享你的旅行动态..."></textarea>
              <div class="post-actions">
                <button class="btn-primary btn-small" @click="createPost">发布</button>
              </div>
            </div>
            <div class="post-list">
              <div v-for="post in groupPosts" :key="post.id" class="post-item">
                <div class="post-header">
                  <img :src="post.authorAvatar || defaultAvatar" class="avatar-small" />
                  <div class="post-author">
                    <span class="name">{{ post.authorName }}</span>
                    <span class="time">{{ formatTime(post.createdAt) }}</span>
                  </div>
                  <div v-if="post.userId === currentUserId" class="post-menu">
                    <button @click="deletePost(post.id)">删除</button>
                  </div>
                </div>
                <p class="post-content">{{ post.content }}</p>
                <div class="post-footer">
                  <button :class="['like-btn', { liked: post.isLiked }]" @click="toggleLike(post)">
                    ♥ {{ post.likeCount || 0 }}
                  </button>
                  <button class="comment-btn" @click="showComments(post)">
                    💬 {{ post.commentCount || 0 }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Trip Plan Tab -->
          <div v-if="detailTab === 'trip'" class="trip-tab">
            <div class="tab-actions">
              <button class="btn-small" @click="showAddTripModal = true">添加行程</button>
              <button class="btn-small btn-secondary" @click="generateAITrip">AI智能规划</button>
            </div>
            <div class="trip-timeline">
              <div v-for="(day, index) in tripPlan" :key="index" class="trip-day">
                <div class="day-header">
                  <span class="day-number">Day {{ index + 1 }}</span>
                  <span class="day-date">{{ day.date }}</span>
                </div>
                <div class="day-items">
                  <div v-for="item in day.items" :key="item.id" class="trip-item">
                    <span class="time">{{ item.time }}</span>
                    <span class="activity">{{ item.activity }}</span>
                    <span v-if="item.location" class="location">{{ item.location }}</span>
                    <button v-if="isGroupAdmin" @click="deleteTripItem(item.id)" class="delete-btn">×</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Bills Tab -->
          <div v-if="detailTab === 'bills'" class="bills-tab">
            <div class="tab-actions">
              <button class="btn-small" @click="showAddBillModal = true">添加账单</button>
              <button class="btn-small btn-secondary" @click="getSettlement">查看结算</button>
            </div>
            <div class="bill-list">
              <div v-for="bill in groupBills" :key="bill.id" class="bill-item">
                <div class="bill-info">
                  <span class="bill-title">{{ bill.title }}</span>
                  <span class="bill-payer">付款人：{{ bill.payerName }}</span>
                </div>
                <div class="bill-amount">
                  <span class="amount">¥{{ bill.amount }}</span>
                  <span v-if="bill.isSettled" class="settled">已结清</span>
                  <span v-else class="unsettled">未结清</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Album Tab -->
          <div v-if="detailTab === 'album'" class="album-tab">
            <div class="tab-actions">
              <button class="btn-small" @click="triggerUpload">上传照片</button>
              <input
                type="file"
                ref="fileInput"
                accept="image/*"
                multiple
                style="display: none"
                @change="handlePhotoUpload"
              />
            </div>
            <div class="album-grid">
              <div v-for="photo in groupAlbum" :key="photo.id" class="photo-item">
                <img :src="photo.url" :alt="photo.description" />
                <button v-if="isGroupAdmin" @click="deletePhoto(photo.id)" class="photo-delete">×</button>
              </div>
            </div>
          </div>

          <!-- Poll Tab -->
          <div v-if="detailTab === 'poll'" class="poll-tab">
            <div class="tab-actions">
              <button class="btn-small" @click="showCreatePollModal = true">发起投票</button>
            </div>
            <div class="poll-list">
              <div v-for="poll in groupPolls" :key="poll.id" class="poll-item">
                <div class="poll-header">
                  <span class="poll-title">{{ poll.title }}</span>
                  <span v-if="poll.status === 0" class="poll-status active">进行中</span>
                  <span v-else class="poll-status ended">已结束</span>
                </div>
                <div class="poll-options">
                  <div
                    v-for="(option, idx) in poll.options"
                    :key="idx"
                    :class="['poll-option', { selected: poll.votedOption === idx, 'winner': poll.winningOption === idx }]"
                    @click="vote(poll.id, idx)"
                  >
                    <span class="option-text">{{ option }}</span>
                    <span class="option-count">{{ poll.votes?.[idx] || 0 }}票</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Diary Tab -->
          <div v-if="detailTab === 'diary'" class="diary-tab">
            <div class="tab-actions">
              <button class="btn-small btn-secondary" @click="generateDiary">生成旅行日记</button>
              <button v-if="travelDiary" class="btn-small" @click="exportDiary">导出日记</button>
            </div>
            <div v-if="travelDiary" class="diary-content">
              <h3>{{ travelDiary.title }}</h3>
              <div class="diary-sections">
                <div v-for="section in travelDiary.sections" :key="section.date" class="diary-section">
                  <h4>{{ section.date }}</h4>
                  <p>{{ section.content }}</p>
                  <div v-if="section.photos" class="diary-photos">
                    <img v-for="photo in section.photos" :key="photo" :src="photo" />
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-state small">
              <p>还没有旅行日记，点击上方按钮生成</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Invite Members Modal -->
    <div v-if="showInviteModal" class="modal-overlay" @click.self="showInviteModal = false">
      <div class="modal modal-small">
        <div class="modal-header">
          <h2>邀请成员</h2>
          <button class="close-btn" @click="showInviteModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>输入用户ID（多个用逗号分隔）</label>
            <input v-model="inviteUserIds" type="text" placeholder="1, 2, 3" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="showInviteModal = false">取消</button>
          <button class="btn-primary" @click="inviteMembers">发送邀请</button>
        </div>
      </div>
    </div>

    <!-- Add Trip Modal -->
    <div v-if="showAddTripModal" class="modal-overlay" @click.self="showAddTripModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>添加行程</h2>
          <button class="close-btn" @click="showAddTripModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>日期</label>
            <input v-model="newTrip.date" type="date" />
          </div>
          <div class="form-group">
            <label>时间</label>
            <input v-model="newTrip.time" type="time" />
          </div>
          <div class="form-group">
            <label>活动</label>
            <input v-model="newTrip.activity" type="text" placeholder="活动内容" />
          </div>
          <div class="form-group">
            <label>地点</label>
            <input v-model="newTrip.location" type="text" placeholder="地点（可选）" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="showAddTripModal = false">取消</button>
          <button class="btn-primary" @click="addTripItem">添加</button>
        </div>
      </div>
    </div>

    <!-- Add Bill Modal -->
    <div v-if="showAddBillModal" class="modal-overlay" @click.self="showAddBillModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>添加账单</h2>
          <button class="close-btn" @click="showAddBillModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>账单名称</label>
            <input v-model="newBill.title" type="text" placeholder="如：晚餐" />
          </div>
          <div class="form-group">
            <label>金额</label>
            <input v-model.number="newBill.amount" type="number" placeholder="0.00" />
          </div>
          <div class="form-group">
            <label>说明</label>
            <textarea v-model="newBill.description" placeholder="账单说明"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="showAddBillModal = false">取消</button>
          <button class="btn-primary" @click="addBill">添加</button>
        </div>
      </div>
    </div>

    <!-- Create Poll Modal -->
    <div v-if="showCreatePollModal" class="modal-overlay" @click.self="showCreatePollModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>发起投票</h2>
          <button class="close-btn" @click="showCreatePollModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>投票标题</label>
            <input v-model="newPoll.title" type="text" placeholder="投票主题" />
          </div>
          <div class="form-group">
            <label>选项（每行一个）</label>
            <textarea v-model="newPoll.optionsText" placeholder="选项1&#10;选项2&#10;选项3"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="showCreatePollModal = false">取消</button>
          <button class="btn-primary" @click="createPoll">发起投票</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import * as groupApi from '@/api/travelGroup'
import * as postApi from '@/api/groupPost'
import * as tripApi from '@/api/groupTripPlan'
import * as billApi from '@/api/groupBill'
import * as albumApi from '@/api/groupAlbum'
import * as notificationApi from '@/api/groupNotification'
import * as aiTripApi from '@/api/aiTrip'
import * as locationApi from '@/api/groupLocation'
import * as pollApi from '@/api/groupPoll'
import * as diaryApi from '@/api/travelDiary'

// Store
const authStore = useAuthStore()
const currentUserId = computed(() => authStore.userInfo?.id)

// Default images
const defaultCover = 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800'
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100'

// Tabs
const tabs = [
  { id: 'my-groups', name: '我的群组', icon: '👥' },
  { id: 'explore', name: '发现群组', icon: '🔍' },
  { id: 'notifications', name: '通知', icon: '🔔' }
]

const activeTab = ref('my-groups')
const exploreFilters = [
  { id: 'all', name: '全部' },
  { id: 'active', name: '进行中' },
  { id: 'ended', name: '已结束' }
]
const activeFilter = ref('all')

// Detail tabs
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

// State
const loading = ref(false)
const creating = ref(false)
const myGroups = ref([])
const exploreGroups = ref([])
const notifications = ref([])
const searchKeyword = ref('')

// Modals
const showCreateModal = ref(false)
const showDetailModal = ref(false)
const showInviteModal = ref(false)
const showAddTripModal = ref(false)
const showAddBillModal = ref(false)
const showCreatePollModal = ref(false)
const showSearchModal = ref(false)

// Current group
const currentGroup = ref({})
const isGroupAdmin = computed(() => {
  return currentGroup.value.userRole === 'owner' || currentGroup.value.userRole === 'admin'
})

// Group detail data
const groupMembers = ref([])
const groupPosts = ref([])
const tripPlan = ref([])
const groupBills = ref([])
const groupAlbum = ref([])
const groupPolls = ref([])
const travelDiary = ref(null)

// Forms
const newGroup = ref({
  name: '',
  destination: '',
  startDate: '',
  endDate: '',
  coverUrl: '',
  description: '',
  maxMembers: 20,
  isPublic: false
})

const newPost = ref({ content: '' })
const inviteUserIds = ref('')
const newTrip = ref({ date: '', time: '', activity: '', location: '' })
const newBill = ref({ title: '', amount: 0, description: '' })
const newPoll = ref({ title: '', optionsText: '' })
const fileInput = ref(null)

// Initialize
onMounted(() => {
  fetchMyGroups()
  fetchNotifications()
})

// API calls
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

async function createGroup() {
  if (!newGroup.value.name) {
    alert('请输入群组名称')
    return
  }
  creating.value = true
  try {
    const res = await groupApi.createGroup(newGroup.value)
    if (res.code === 200) {
      showCreateModal.value = false
      newGroup.value = { name: '', destination: '', startDate: '', endDate: '', coverUrl: '', description: '', maxMembers: 20, isPublic: false }
      fetchMyGroups()
    }
  } catch (error) {
    console.error('创建群组失败:', error)
  } finally {
    creating.value = false
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

// Actions
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

async function inviteMembers() {
  if (!inviteUserIds.value) return
  const ids = inviteUserIds.value.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id))
  try {
    const res = await groupApi.inviteMembers(currentGroup.value.id, ids)
    if (res.code === 200) {
      showInviteModal.value = false
      inviteUserIds.value = ''
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

async function createPost() {
  if (!newPost.value.content) return
  try {
    const res = await postApi.createPost(currentGroup.value.id, { content: newPost.value.content })
    if (res.code === 200) {
      newPost.value.content = ''
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
  // TODO: 实现评论功能
  console.log('查看评论:', post.id)
}

async function addTripItem() {
  if (!newTrip.value.date || !newTrip.value.activity) {
    alert('请填写日期和活动')
    return
  }
  try {
    const res = await tripApi.addTripItem(currentGroup.value.id, newTrip.value)
    if (res.code === 200) {
      showAddTripModal.value = false
      newTrip.value = { date: '', time: '', activity: '', location: '' }
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

async function addBill() {
  if (!newBill.value.title || !newBill.value.amount) {
    alert('请填写账单名称和金额')
    return
  }
  try {
    const res = await billApi.addBill(currentGroup.value.id, newBill.value)
    if (res.code === 200) {
      showAddBillModal.value = false
      newBill.value = { title: '', amount: 0, description: '' }
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

function triggerUpload() {
  fileInput.value?.click()
}

async function handlePhotoUpload(event) {
  const files = event.target.files
  if (!files.length) return
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

async function createPoll() {
  if (!newPoll.value.title || !newPoll.value.optionsText) {
    alert('请填写投票标题和选项')
    return
  }
  const options = newPoll.value.optionsText.split('\n').filter(o => o.trim())
  try {
    const res = await pollApi.createPoll({
      groupId: currentGroup.value.id,
      title: newPoll.value.title,
      options: options
    })
    if (res.code === 200) {
      showCreatePollModal.value = false
      newPoll.value = { title: '', optionsText: '' }
      fetchPolls(currentGroup.value.id)
    }
  } catch (error) {
    console.error('创建投票失败:', error)
  }
}

async function vote(pollId, optionIndex) {
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

function handleCoverUpload(event) {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      newGroup.value.coverUrl = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// Watch explore tab
import { watch } from 'vue'
watch(activeTab, (val) => {
  if (val === 'explore' && exploreGroups.value.length === 0) {
    fetchExploreGroups()
  }
})

// Helpers
function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return date.toLocaleDateString('zh-CN')
}

function getRoleName(role) {
  const map = { owner: '群主', admin: '管理员', member: '成员' }
  return map[role] || role
}

function getNotificationIcon(type) {
  const map = {
    group_invite: '👥',
    join_request: '📝',
    system: '🔔',
    post_like: '❤️',
    post_comment: '💬'
  }
  return map[type] || '📢'
}
</script>

<style scoped>
/* Apple Design System Variables - Strictly aligned with official spec */
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
  
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 22px;
  font-family: var(--font-family);
  color: var(--color-text);
  scroll-behavior: smooth;
}

/* Hero Section */
.group-hero {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-60) var(--spacing-40);
  text-align: center;
  margin-bottom: var(--spacing-40);
  position: relative;
  overflow: hidden;
}

.group-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(41, 151, 255, 0.06) 0%, transparent 70%);
  pointer-events: none;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-content h1 {
  font-size: 56px;
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1.05;
  margin-bottom: var(--spacing-12);
  color: var(--color-text);
}

.hero-subtitle {
  font-size: 21px;
  font-weight: 400;
  color: var(--color-text-secondary);
  line-height: 1.4;
  margin-bottom: var(--spacing-32);
}

.hero-actions {
  display: flex;
  gap: var(--spacing-32);
  justify-content: center;
  align-items: center;
}

/* Buttons */
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

.hero-actions .btn-primary::after,
.hero-actions .btn-secondary::after {
  content: '›';
  font-size: 18px;
  font-weight: 300;
  transition: transform var(--transition-fast);
}

.hero-actions .btn-primary:hover::after,
.hero-actions .btn-secondary:hover::after {
  transform: translateX(2px);
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

/* Tab Navigation */
.tab-nav {
  display: flex;
  gap: var(--spacing-8);
  margin-bottom: var(--spacing-32);
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 1px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-8);
  padding: var(--spacing-12) var(--spacing-20);
  background: transparent;
  border: none;
  font-size: 17px;
  font-weight: 400;
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

.tab-icon {
  font-size: 20px;
}

/* Section Header */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-24);
}

.section-header h2 {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.1;
}

.search-bar {
  display: flex;
  align-items: center;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  overflow: hidden;
  transition: box-shadow var(--transition-normal);
}

.search-bar:focus-within {
  box-shadow: var(--shadow-sm);
}

.search-bar input {
  border: none;
  background: transparent;
  padding: var(--spacing-12) var(--spacing-20);
  font-size: 15px;
  outline: none;
  width: 240px;
  color: var(--color-text);
}

.search-bar input::placeholder {
  color: var(--color-text-secondary);
}

.search-bar button {
  background: transparent;
  border: none;
  padding: var(--spacing-12) var(--spacing-20);
  cursor: pointer;
  font-size: 18px;
  color: var(--color-text-secondary);
  transition: color var(--transition-fast);
}

.search-bar button:hover {
  color: var(--color-text);
}

/* Filter Tabs */
.filter-tabs {
  display: flex;
  gap: var(--spacing-8);
}

.filter-btn {
  padding: var(--spacing-8) var(--spacing-16);
  background: var(--color-bg-secondary);
  border: none;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.filter-btn:hover {
  background: rgba(0, 0, 0, 0.06);
  color: var(--color-text);
}

.filter-btn.active {
  background: var(--color-text);
  color: #fff;
}

/* Group Grid */
.group-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-24);
}

.group-card {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  transition: all var(--transition-slow);
  cursor: pointer;
  border: 1px solid transparent;
}

.group-card:hover {
  transform: scale(1.01);
  box-shadow: var(--shadow-lg);
  border-color: rgba(0, 0, 0, 0.04);
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.group-card:hover .card-image img {
  transform: scale(1.03);
}

.status-badge {
  position: absolute;
  top: var(--spacing-12);
  right: var(--spacing-12);
  padding: var(--spacing-4) var(--spacing-12);
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.status-badge.active {
  background: var(--color-success);
  color: #fff;
}

.status-badge.ended {
  background: var(--color-text-secondary);
  color: #fff;
}

.member-count-overlay {
  position: absolute;
  bottom: var(--spacing-12);
  right: var(--spacing-12);
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  color: #fff;
  padding: var(--spacing-4) var(--spacing-10);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.card-content {
  padding: var(--spacing-24) var(--spacing-24) var(--spacing-20);
}

.card-content h3 {
  font-size: 22px;
  font-weight: 600;
  line-height: 1.2;
  margin-bottom: var(--spacing-8);
}

.destination {
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: var(--spacing-12);
  display: flex;
  align-items: center;
  gap: var(--spacing-8);
}

.location-icon {
  color: var(--color-primary);
}

.description {
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: var(--spacing-12);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-12);
}

.member-count {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
}

.card-role {
  margin-top: var(--spacing-8);
}

.role-tag {
  display: inline-block;
  padding: var(--spacing-4) var(--spacing-10);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.role-tag.owner {
  background: var(--color-warning);
  color: #000;
}

.role-tag.admin {
  background: var(--color-primary);
  color: #fff;
}

.role-tag.member {
  background: var(--color-bg);
  color: var(--color-text);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-12);
  border-top: 1px solid var(--color-border);
  margin-top: var(--spacing-8);
}

.creator {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.join-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  padding: var(--spacing-6) var(--spacing-16);
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.join-btn:hover {
  background: var(--color-primary-hover);
}

.join-btn:active {
  transform: scale(0.96);
}

.joined-tag {
  color: var(--color-success);
  font-size: 13px;
  font-weight: 500;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 80px 40px;
  background: var(--color-bg-secondary);
  border-radius: 28px;
}

.empty-state.small {
  padding: 40px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 22px;
  margin-bottom: 8px;
}

.empty-state p {
  color: var(--color-text-secondary);
  margin-bottom: 24px;
}

/* Loading */
.loading-state {
  text-align: center;
  padding: 60px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-bg-secondary);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Notification List */
.notification-list {
  background: var(--color-bg);
  border-radius: 24px;
  overflow: hidden;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  border-bottom: 1px solid var(--color-border);
  cursor: pointer;
  transition: background 0.2s ease;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item.unread {
  background: rgba(41, 151, 255, 0.05);
}

.notification-item:hover {
  background: var(--color-bg-secondary);
}

.notification-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.notification-message {
  color: var(--color-text-secondary);
  font-size: 14px;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.read-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  cursor: pointer;
  flex-shrink: 0;
}

.mark-all-btn {
  background: transparent;
  border: none;
  color: var(--color-primary);
  cursor: pointer;
  font-size: 14px;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-20);
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background: var(--color-bg);
  border-radius: var(--radius-lg);
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  animation: slideUp var(--transition-normal);
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to { 
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-large {
  max-width: 900px;
}

.modal-small {
  max-width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-24);
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  font-size: 22px;
  font-weight: 600;
}

.group-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.group-status {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: var(--color-text-secondary);
  line-height: 1;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all var(--transition-fast);
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--color-text);
}

.modal-body {
  padding: var(--spacing-24);
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-12);
  padding: var(--spacing-16) var(--spacing-24);
  border-top: 1px solid var(--color-border);
}

/* Form */
.form-group {
  margin-bottom: var(--spacing-20);
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: var(--spacing-8);
  color: var(--color-text);
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: var(--spacing-12) var(--spacing-16);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 15px;
  font-family: inherit;
  transition: all var(--transition-normal);
  box-sizing: border-box;
  background: var(--color-bg);
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.1);
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: var(--color-text-secondary);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-row {
  display: flex;
  gap: var(--spacing-16);
}

.form-row .form-group {
  flex: 1;
}

.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: var(--spacing-8);
  cursor: pointer;
}

.checkbox-label input {
  width: auto !important;
  accent-color: var(--color-primary);
}

.cover-upload {
  position: relative;
  width: 100%;
  height: 120px;
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-sm);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
  cursor: pointer;
}

.cover-upload:hover {
  border-color: var(--color-primary);
  background: rgba(41, 151, 255, 0.03);
}

.cover-upload img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-upload input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

/* Detail Tabs */
.detail-tabs {
  display: flex;
  gap: var(--spacing-4);
  padding: 0 var(--spacing-24);
  background: var(--color-bg-secondary);
  overflow-x: auto;
  scrollbar-width: none;
}

.detail-tabs::-webkit-scrollbar {
  display: none;
}

.detail-tab {
  padding: var(--spacing-12) var(--spacing-16);
  background: transparent;
  border: none;
  font-size: 15px;
  font-weight: 400;
  color: var(--color-text-secondary);
  cursor: pointer;
  white-space: nowrap;
  position: relative;
  transition: all var(--transition-normal);
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
}

.detail-tab:hover {
  color: var(--color-text);
  background: rgba(0, 0, 0, 0.03);
}

.detail-tab.active {
  color: var(--color-text);
  font-weight: 500;
}

.detail-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: var(--spacing-16);
  right: var(--spacing-16);
  height: 3px;
  background: var(--color-primary);
  border-radius: 2px;
}

.detail-body {
  min-height: 400px;
}

/* Tab Actions */
.tab-actions {
  display: flex;
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-20);
}

.tab-actions .btn-secondary {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

/* Members */
.member-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-12);
}

.member-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-16);
  padding: var(--spacing-16);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  transition: all var(--transition-normal);
}

.member-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.04);
}

.avatar-small {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(0, 0, 0, 0.04);
}

.member-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.member-info .name {
  font-weight: 500;
  font-size: 15px;
}

.join-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.role-badge {
  padding: var(--spacing-4) var(--spacing-10);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.role-badge.owner {
  background: var(--color-warning);
  color: #000;
}

.role-badge.admin {
  background: var(--color-primary);
  color: #fff;
}

.role-badge.member {
  background: var(--color-bg);
  color: var(--color-text);
}

.member-actions button {
  background: transparent;
  border: none;
  color: var(--color-danger);
  cursor: pointer;
  font-size: 13px;
  padding: var(--spacing-4) var(--spacing-12);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.member-actions button:hover {
  background: rgba(255, 59, 48, 0.1);
}

/* Posts */
.post-create {
  margin-bottom: var(--spacing-24);
}

.post-create textarea {
  width: 100%;
  padding: var(--spacing-16);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 15px;
  font-family: inherit;
  resize: none;
  min-height: 80px;
  box-sizing: border-box;
  transition: all var(--transition-normal);
}

.post-create textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.1);
}

.post-create textarea::placeholder {
  color: var(--color-text-secondary);
}

.post-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-12);
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-20);
}

.post-item {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  padding: var(--spacing-20);
  transition: all var(--transition-normal);
}

.post-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.post-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-12);
}

.post-author {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.post-author .name {
  font-weight: 500;
  font-size: 15px;
}

.post-author .time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.post-menu button {
  background: transparent;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: var(--spacing-4) var(--spacing-8);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.post-menu button:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--color-text);
}

.post-content {
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: var(--spacing-12);
}

.post-footer {
  display: flex;
  gap: var(--spacing-16);
}

.post-footer button {
  background: transparent;
  border: none;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-4) var(--spacing-12);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
  color: var(--color-text-secondary);
}

.post-footer button:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--color-text);
}

.like-btn.liked {
  color: var(--color-danger);
}

/* Trip Timeline */
.trip-timeline {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-24);
}

.trip-day {
  position: relative;
  padding-left: 80px;
}

.trip-day::before {
  content: '';
  position: absolute;
  left: 32px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: var(--color-border);
}

.day-header {
  position: absolute;
  left: 0;
  width: 60px;
  text-align: right;
}

.day-number {
  display: block;
  font-weight: 700;
  color: var(--color-primary);
  font-size: 20px;
  line-height: 1.2;
}

.day-date {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.day-items {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-12);
}

.trip-item {
  background: var(--color-bg-secondary);
  padding: var(--spacing-12) var(--spacing-16);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-12);
  position: relative;
  transition: all var(--transition-normal);
}

.trip-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.trip-item::before {
  content: '';
  position: absolute;
  left: -56px;
  top: 50%;
  transform: translateY(-50%);
  width: 12px;
  height: 12px;
  background: var(--color-bg);
  border: 2px solid var(--color-primary);
  border-radius: 50%;
}

.trip-item .time {
  font-size: 13px;
  color: var(--color-text-secondary);
  min-width: 50px;
  font-weight: 500;
}

.trip-item .activity {
  flex: 1;
  font-weight: 500;
  font-size: 15px;
}

.trip-item .location {
  font-size: 13px;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.trip-item .delete-btn {
  background: transparent;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
  font-size: 18px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all var(--transition-fast);
}

.trip-item .delete-btn:hover {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-danger);
}

/* Bills */
.bill-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-12);
}

.bill-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-16);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  transition: all var(--transition-normal);
}

.bill-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.bill-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.bill-title {
  font-weight: 500;
  font-size: 15px;
}

.bill-payer {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.bill-amount {
  text-align: right;
}

.amount {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: var(--color-primary);
  line-height: 1.2;
}

.settled {
  font-size: 12px;
  color: var(--color-success);
  font-weight: 500;
}

.unsettled {
  font-size: 12px;
  color: var(--color-danger);
  font-weight: 500;
}

/* Album */
.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: var(--spacing-12);
}

.photo-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: var(--radius-sm);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.photo-item:hover {
  transform: scale(1.02);
  box-shadow: var(--shadow-md);
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.photo-item:hover img {
  transform: scale(1.05);
}

.photo-delete {
  position: absolute;
  top: var(--spacing-8);
  right: var(--spacing-8);
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  color: #fff;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  opacity: 0;
  transition: all var(--transition-fast);
}

.photo-item:hover .photo-delete {
  opacity: 1;
}

.photo-delete:hover {
  background: var(--color-danger);
}

/* Poll */
.poll-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-20);
}

.poll-item {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  padding: var(--spacing-20);
}

.poll-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-16);
}

.poll-title {
  font-weight: 600;
  font-size: 17px;
  line-height: 1.3;
}

.poll-status {
  padding: var(--spacing-4) var(--spacing-10);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.poll-status.active {
  background: var(--color-success);
  color: #fff;
}

.poll-status.ended {
  background: var(--color-text-secondary);
  color: #fff;
}

.poll-options {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-8);
}

.poll-option {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-12) var(--spacing-16);
  background: var(--color-bg);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 2px solid transparent;
}

.poll-option:hover {
  border-color: var(--color-primary);
  background: rgba(41, 151, 255, 0.03);
}

.poll-option.selected {
  border-color: var(--color-primary);
  background: rgba(41, 151, 255, 0.1);
}

.poll-option.winner {
  background: rgba(52, 199, 89, 0.1);
  border-color: var(--color-success);
}

.poll-option.winner.selected {
  border-color: var(--color-success);
}

.option-text {
  font-size: 15px;
  font-weight: 400;
}

.option-count {
  color: var(--color-text-secondary);
  font-size: 13px;
  font-weight: 500;
}

/* Diary */
.diary-content h3 {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: var(--spacing-20);
}

.diary-sections {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-24);
}

.diary-section {
  padding: var(--spacing-20);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
}

.diary-section h4 {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-primary);
  margin-bottom: var(--spacing-8);
}

.diary-section p {
  line-height: 1.8;
  color: var(--color-text);
  font-size: 15px;
}

.diary-photos {
  display: flex;
  gap: var(--spacing-8);
  margin-top: var(--spacing-12);
  flex-wrap: wrap;
}

.diary-photos img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  transition: transform var(--transition-normal);
}

.diary-photos img:hover {
  transform: scale(1.03);
}

/* Responsive */
@media (max-width: 1199px) and (min-width: 768px) {
  .group-container {
    padding: 0 var(--spacing-20);
  }

  .hero-content h1 {
    font-size: 44px;
  }

  .hero-subtitle {
    font-size: 19px;
  }

  .group-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: var(--spacing-20);
  }

  .card-content {
    padding: var(--spacing-20) var(--spacing-20) var(--spacing-16);
  }

  .modal-large {
    max-width: 700px;
  }

  .section-header {
    flex-direction: column;
    gap: var(--spacing-16);
    align-items: flex-start;
  }
}

@media (max-width: 767px) {
  .group-container {
    padding: 0 var(--spacing-16);
  }

  .group-hero {
    padding: var(--spacing-40) var(--spacing-24);
    border-radius: var(--radius-lg);
  }

  .hero-content h1 {
    font-size: 36px;
    letter-spacing: -0.01em;
  }

  .hero-subtitle {
    font-size: 17px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-16);
  }

  .tab-nav {
    overflow-x: auto;
    gap: var(--spacing-4);
    padding-bottom: var(--spacing-8);
  }

  .tab-item {
    flex-shrink: 0;
    padding: var(--spacing-12) var(--spacing-16);
    font-size: 15px;
  }

  .section-header {
    flex-direction: column;
    gap: var(--spacing-12);
    align-items: flex-start;
  }

  .search-bar input {
    width: 100%;
  }

  .group-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-20);
  }

  .group-card {
    border-radius: var(--radius-lg);
  }

  .card-content {
    padding: var(--spacing-20) var(--spacing-20) var(--spacing-16);
  }

  .card-content h3 {
    font-size: 20px;
  }

  .modal {
    max-width: 100%;
    border-radius: var(--radius-lg);
    margin: var(--spacing-8);
  }

  .modal-large {
    max-width: 100%;
  }

  .detail-tabs {
    padding: 0 var(--spacing-12);
    gap: var(--spacing-4);
  }

  .detail-tab {
    padding: var(--spacing-12) var(--spacing-12);
    font-size: 14px;
  }

  .modal-body {
    padding: var(--spacing-16);
  }

  .modal-header {
    padding: var(--spacing-16);
  }

  .modal-footer {
    padding: var(--spacing-12) var(--spacing-16);
  }

  .form-row {
    flex-direction: column;
  }

  .trip-day {
    padding-left: 50px;
  }

  .trip-day::before {
    left: 16px;
  }

  .day-header {
    width: 40px;
  }

  .day-number {
    font-size: 16px;
  }

  .trip-item::before {
    left: -36px;
  }

  .album-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  }

  .diary-photos img {
    width: 80px;
    height: 80px;
  }

  .hero-actions .btn-primary,
  .hero-actions .btn-secondary {
    width: 100%;
    justify-content: center;
    padding: var(--spacing-12) var(--spacing-24);
  }
}
</style>
