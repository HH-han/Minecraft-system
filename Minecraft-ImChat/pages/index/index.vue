<template>
  <view class="chat-list-page">
    <CustomNavBar
      title="博览通讯"
      @search="onSearch"
      @menuItemClick="handleMenuItemClick"
    />

    <scroll-view
      class="chat-list"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      :style="{ paddingTop: (statusBarHeight + 44) + 'px' }"
    >
      <view v-if="topChats.length > 0" class="top-section">
        <view class="section-header">
          <text class="section-title">置顶聊天</text>
          <view class="section-action" @click="editTop">
            <text>编辑</text>
          </view>
        </view>
        <view
          v-for="(chat, index) in topChats"
          :key="chat.id"
          :style="{ animationDelay: `${index * 0.03}s` }"
          class="chat-item fade-in"
          @click="goToChat(chat)"
        >
          <view class="avatar-wrapper">
            <image
              class="chat-avatar"
              :src="chat.avatar || defaultAvatar"
              mode="aspectFill"
            />
            <view v-if="chat.type === 'single' && chat.online" class="online-dot"></view>
          </view>
          <view class="chat-info">
            <text class="chat-name">{{ chat.name }}</text>
            <text class="chat-preview">{{ chat.lastMessage || '开始聊天吧' }}</text>
          </view>
          <view class="chat-right">
            <text class="chat-time">{{ chat.time }}</text>
            <view v-if="chat.unreadCount > 0" class="unread-badge">
              <text>{{ chat.unreadCount > 99 ? '99+' : chat.unreadCount }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="topChats.length > 0 && normalChats.length > 0" class="divider-line"></view>

      <view
        v-for="(chat, index) in normalChats"
        :key="chat.id"
        :style="{ animationDelay: `${index * 0.03}s` }"
        class="chat-item fade-in"
        @click="goToChat(chat)"
      >
        <view class="avatar-wrapper">
          <image
            class="chat-avatar"
            :src="chat.avatar || defaultAvatar"
            mode="aspectFill"
          />
          <view v-if="chat.type === 'single' && chat.online" class="online-dot"></view>
          <image v-else-if="chat.type === 'group'" class="group-badge" src="/static/group-icon.png" mode="aspectFill" />
        </view>
        <view class="chat-info">
          <text class="chat-name">{{ chat.name }}</text>
          <text class="chat-preview">{{ chat.lastMessage || '开始聊天吧' }}</text>
        </view>
        <view class="chat-right">
          <text class="chat-time">{{ chat.time }}</text>
          <view v-if="chat.unreadCount > 0" class="unread-badge">
            <text>{{ chat.unreadCount > 99 ? '99+' : chat.unreadCount }}</text>
          </view>
        </view>
      </view>

      <view v-if="normalChats.length === 0 && topChats.length === 0" class="empty-state">
        <text class="empty-icon">💬</text>
        <text class="empty-text">还没有聊天记录</text>
        <view class="empty-action" @click="goToAddFriend">
          <text>添加好友</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getFriendInfoList, getGroupsByUserId } from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'
import CustomNavBar from '../../components/CustomNavBar.vue'

export default {
  components: {
    CustomNavBar
  },
  data() {
    return {
      isRefreshing: false,
      topChats: [],
      normalChats: [],
      defaultAvatar: '/static/default-avatar.png',
      userInfo: null,
    }
  },

  onLoad() {
    this.loadUserData()
  },

  onShow() {
    this.loadChats()
  },

  methods: {
    async loadUserData() {
      const userInfo = getUserInfo()
      if (!userInfo) {
        uni.reLaunch({ url: '/pages/login/login' })
        return
      }
      this.userInfo = userInfo
    },

    async loadChats() {
      if (!this.userInfo?.id) return

      try {
        const [friendsRes, groupsRes] = await Promise.all([
          getFriendInfoList(this.userInfo.id),
          getGroupsByUserId(this.userInfo.id)
        ])

        const friends = friendsRes.code === 200 && friendsRes.data
          ? friendsRes.data.map(f => ({
              id: f.friendId,
              name: f.remark || f.username || '未知用户',
              avatar: f.avatar || '',
              type: 'single',
              lastMessage: '',
              time: '',
              unreadCount: f.unreadCount || 0,
              online: f.online || false,
              isTop: false
            }))
          : []

        const groups = groupsRes.code === 200 && groupsRes.data
          ? groupsRes.data.map(g => ({
              id: g.id,
              name: g.name,
              avatar: g.avatar || '',
              type: 'group',
              lastMessage: '',
              time: '',
              unreadCount: 0,
              isTop: false
            }))
          : []

        this.topChats = [...friends, ...groups].filter(c => c.isTop)
        this.normalChats = [...friends, ...groups].filter(c => !c.isTop)
      } catch (e) {
        console.error('加载聊天列表失败:', e)
      }
    },

    async onRefresh() {
      this.isRefreshing = true
      await this.loadChats()
      this.isRefreshing = false
    },

    showSearch() {
      uni.navigateTo({ url: '/pages/add-friend/add-friend' })
    },

    editTop() {
      uni.showToast({ title: '编辑模式开发中', icon: 'none' })
    },

    goToChat(chat) {
      uni.navigateTo({
        url: `/pages/chat/chat?type=${chat.type}&id=${chat.id}&name=${encodeURIComponent(chat.name)}&avatar=${encodeURIComponent(chat.avatar)}`
      })
    },

    goToAddFriend() {
      uni.navigateTo({ url: '/pages/add-friend/add-friend' })
    },

    handleMenuItemClick(item) {
      const actionMap = {
        groupChat: () => uni.navigateTo({ url: '/pages/create-group/create-group' }),
        addFriend: () => uni.navigateTo({ url: '/pages/add-friend/add-friend' }),
        scan: () => uni.showToast({ title: '扫一扫开发中', icon: 'none' }),
        pay: () => uni.showToast({ title: '收付款开发中', icon: 'none' })
      }
      if (actionMap[item.action]) {
        actionMap[item.action]()
      }
    },

    onSearch() {
      uni.showToast({ title: '搜索功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.chat-list-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f7f7f7;
}

.chat-list {
  flex: 1;
  padding-top: calc(var(--status-bar-height, 44px) + 128rpx);
}

.top-section {
  background-color: #ffffff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 32rpx;
}

.section-title {
  font-size: 24rpx;
  color: #999999;
}

.section-action text {
  font-size: 24rpx;
  color: #07C160;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #f0f0f0;
}

.chat-item:active {
  background-color: #f5f5f5;
}

.avatar-wrapper {
  position: relative;
  margin-right: 24rpx;
}

.chat-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 8rpx;
  background-color: #f0f0f0;
}

.online-dot {
  position: absolute;
  bottom: 4rpx;
  right: 4rpx;
  width: 16rpx;
  height: 16rpx;
  background-color: #07C160;
  border-radius: 50%;
  border: 2rpx solid #ffffff;
}

.group-badge {
  position: absolute;
  bottom: -4rpx;
  right: -4rpx;
  width: 32rpx;
  height: 32rpx;
}

.chat-info {
  flex: 1;
  overflow: hidden;
}

.chat-name {
  display: block;
  font-size: 30rpx;
  color: #000000;
  font-weight: 500;
  margin-bottom: 6rpx;
}

.chat-preview {
  font-size: 26rpx;
  color: #999999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-left: 16rpx;
}

.chat-time {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 8rpx;
}

.unread-badge {
  min-width: 32rpx;
  height: 32rpx;
  background-color: #FF3B30;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
}

.unread-badge text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 600;
}

.divider-line {
  height: 16rpx;
  background-color: #f7f7f7;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 32rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
  margin-bottom: 40rpx;
}

.empty-action {
  padding: 20rpx 56rpx;
  background-color: #07C160;
  border-radius: 40rpx;
}

.empty-action text {
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 500;
}

.fab {
  position: fixed;
  right: 40rpx;
  bottom: 200rpx;
  width: 100rpx;
  height: 100rpx;
  background-color: #07C160;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(7, 193, 96, 0.3);
}

.fab:active {
  transform: scale(0.95);
}

.fab-icon {
  font-size: 48rpx;
  color: #ffffff;
  font-weight: 300;
}

.fade-in {
  animation: fadeInUp 0.3s ease forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>