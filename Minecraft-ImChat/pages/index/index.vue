<template>
  <view class="chat-list-page">
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="searchKeyword"
          placeholder="搜索聊天记录"
          placeholder-class="search-placeholder"
          @input="onSearch"
        />
        <view v-if="searchKeyword" class="clear-btn" @click="clearSearch">
          <text>✕</text>
        </view>
      </view>
    </view>

    <view class="tab-bar">
      <view
        :class="['tab-item', { active: activeTab === 'friends' }]"
        @click="switchTab('friends')"
      >
        <text class="tab-text">好友</text>
        <view v-if="friends.length > 0" class="tab-badge">{{ friends.length }}</view>
      </view>
      <view
        :class="['tab-item', { active: activeTab === 'groups' }]"
        @click="switchTab('groups')"
      >
        <text class="tab-text">群聊</text>
        <view v-if="groups.length > 0" class="tab-badge">{{ groups.length }}</view>
      </view>
    </view>

    <scroll-view
      class="chat-list"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view v-if="activeTab === 'friends'">
        <view
          v-for="(friend, index) in filteredFriends"
          :key="friend.id"
          :style="{ animationDelay: `${index * 0.05}s` }"
          class="chat-item fade-in"
          @click="goToChat(friend)"
        >
          <view class="avatar-wrapper">
            <image
              class="avatar"
              :src="friend.avatar || defaultAvatar"
              mode="aspectFill"
            />
            <view v-if="friend.online" class="online-indicator"></view>
            <view v-else class="offline-indicator"></view>
          </view>
          <view class="chat-info">
            <view class="chat-header-row">
              <text class="chat-name">{{ friend.name }}</text>
              <text class="chat-time">{{ friend.time }}</text>
            </view>
            <view class="chat-preview-row">
              <text class="chat-preview" :class="{ unread: friend.unreadCount > 0 }">
                {{ friend.lastMessage || '开始聊天吧' }}
              </text>
              <view v-if="friend.unreadCount > 0" class="unread-badge bounce-in">
                <text class="unread-count">{{ friend.unreadCount > 99 ? '99+' : friend.unreadCount }}</text>
              </view>
            </view>
          </view>
          <view class="chat-arrow">›</view>
        </view>

        <view v-if="filteredFriends.length === 0" class="empty-state">
          <text class="empty-icon">💬</text>
          <text class="empty-text">暂无{{ searchKeyword ? '搜索结果' : '好友' }}</text>
          <view v-if="!searchKeyword" class="empty-action" @click="goToAddFriend">
            <text>添加好友</text>
          </view>
        </view>
      </view>

      <view v-if="activeTab === 'groups'">
        <view
          v-for="(group, index) in filteredGroups"
          :key="group.id"
          :style="{ animationDelay: `${index * 0.05}s` }"
          class="chat-item fade-in"
          @click="goToGroupChat(group)"
        >
          <view class="avatar-wrapper">
            <image
              class="avatar group-avatar"
              :src="group.avatar || defaultGroupAvatar"
              mode="aspectFill"
            />
          </view>
          <view class="chat-info">
            <view class="chat-header-row">
              <text class="chat-name">{{ group.name }}</text>
              <text class="chat-time">{{ group.time }}</text>
            </view>
            <view class="chat-preview-row">
              <text class="chat-preview">{{ group.lastMessage || '群聊消息' }}</text>
              <view v-if="group.unreadCount > 0" class="unread-badge bounce-in">
                <text class="unread-count">{{ group.unreadCount > 99 ? '99+' : group.unreadCount }}</text>
              </view>
            </view>
          </view>
          <view class="chat-arrow">›</view>
        </view>

        <view v-if="filteredGroups.length === 0" class="empty-state">
          <text class="empty-icon">👥</text>
          <text class="empty-text">暂无{{ searchKeyword ? '搜索结果' : '群聊' }}</text>
          <view v-if="!searchKeyword" class="empty-action" @click="goToCreateGroup">
            <text>创建群聊</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="fab" @click="onFabClick">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script>
import { getFriendInfoList, getGroupsByUserId } from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'

export default {
  data() {
    return {
      searchKeyword: '',
      activeTab: 'friends',
      isRefreshing: false,
      friends: [],
      groups: [],
      defaultAvatar: '/static/default-avatar.png',
      defaultGroupAvatar: '/static/default-group.png'
    }
  },

  computed: {
    filteredFriends() {
      if (!this.searchKeyword) return this.friends
      const keyword = this.searchKeyword.toLowerCase()
      return this.friends.filter(f =>
        f.name.toLowerCase().includes(keyword)
      )
    },

    filteredGroups() {
      if (!this.searchKeyword) return this.groups
      const keyword = this.searchKeyword.toLowerCase()
      return this.groups.filter(g =>
        g.name.toLowerCase().includes(keyword)
      )
    }
  },

  onLoad() {
    this.loadUserData()
  },

  onShow() {
    this.loadFriends()
    this.loadGroups()
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

    async loadFriends() {
      if (!this.userInfo?.id) return

      try {
        const res = await getFriendInfoList(this.userInfo.id)
        if (res.code === 200 && res.data) {
          this.friends = res.data.map(f => ({
            id: f.friendId,
            name: f.remark || f.username || '未知用户',
            avatar: f.avatar || '',
            lastMessage: '',
            time: '',
            unreadCount: f.unreadCount || 0,
            online: f.online || false
          }))
        }
      } catch (e) {
        console.error('加载好友列表失败:', e)
      }
    },

    async loadGroups() {
      if (!this.userInfo?.id) return

      try {
        const res = await getGroupsByUserId(this.userInfo.id)
        if (res.code === 200 && res.data) {
          this.groups = res.data.map(g => ({
            id: g.id,
            name: g.name,
            avatar: g.avatar || '',
            lastMessage: '',
            time: '',
            unreadCount: 0,
            memberCount: g.memberCount || 0,
            isCreator: g.creatorId === this.userInfo.id
          }))
        }
      } catch (e) {
        console.error('加载群聊列表失败:', e)
      }
    },

    switchTab(tab) {
      this.activeTab = tab
    },

    onSearch() {
    },

    clearSearch() {
      this.searchKeyword = ''
    },

    async onRefresh() {
      this.isRefreshing = true
      await Promise.all([this.loadFriends(), this.loadGroups()])
      this.isRefreshing = false
    },

    loadMore() {
    },

    goToChat(friend) {
      uni.navigateTo({
        url: `/pages/chat/chat?type=single&id=${friend.id}&name=${encodeURIComponent(friend.name)}&avatar=${encodeURIComponent(friend.avatar)}`
      })
    },

    goToGroupChat(group) {
      uni.navigateTo({
        url: `/pages/chat/chat?type=group&id=${group.id}&name=${encodeURIComponent(group.name)}&avatar=${encodeURIComponent(group.avatar)}`
      })
    },

    goToAddFriend() {
      uni.navigateTo({ url: '/pages/add-friend/add-friend' })
    },

    goToCreateGroup() {
      uni.navigateTo({ url: '/pages/create-group/create-group' })
    },

    onFabClick() {
      const actionList = [
        { text: '添加好友', icon: '👤', action: () => this.goToAddFriend() },
        { text: '创建群聊', icon: '👥', action: () => this.goToCreateGroup() }
      ]

      uni.showActionSheet({
        itemList: actionList.map(a => a.text),
        success: (res) => {
          actionList[res.tapIndex].action()
        }
      })
    }
  }
}
</script>

<style scoped>
.chat-list-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f7;
}

.search-bar {
  padding: 20rpx 32rpx;
  background-color: #ffffff;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background-color: #f5f5f7;
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
  transition: all 0.25s ease;
}

.search-input-wrapper:focus-within {
  background-color: #ffffff;
  box-shadow: 0 0 0 3rpx rgba(41, 151, 255, 0.1);
}

.search-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
  opacity: 0.5;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #1d1d1f;
  background: transparent;
  border: none;
  outline: none;
}

.search-placeholder {
  color: #a1a1a6;
}

.clear-btn {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #d2d2d6;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.clear-btn:active {
  background-color: #a1a1a6;
}

.clear-btn text {
  font-size: 24rpx;
  color: #ffffff;
  line-height: 1;
}

.tab-bar {
  display: flex;
  padding: 0 32rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #e5e5e5;
}

.tab-item {
  display: flex;
  align-items: center;
  padding: 28rpx 0;
  margin-right: 64rpx;
  position: relative;
  cursor: pointer;
  transition: all 0.25s ease;
}

.tab-text {
  font-size: 32rpx;
  color: #6e6e73;
  font-weight: 500;
  letter-spacing: -0.3rpx;
  transition: all 0.25s ease;
}

.tab-item.active .tab-text {
  color: #1d1d1f;
  font-weight: 700;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background-color: #2997ff;
  border-radius: 2rpx 2rpx 0 0;
}

.tab-badge {
  min-width: 32rpx;
  height: 32rpx;
  background-color: #ff3b30;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 8rpx;
  padding: 0 8rpx;
}

.tab-badge text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 600;
}

.chat-list {
  flex: 1;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #f0f0f2;
  transition: all 0.2s ease;
  cursor: pointer;
}

.chat-item:active {
  background-color: #f5f5f7;
  transform: scale(0.995);
}

.avatar-wrapper {
  position: relative;
  margin-right: 24rpx;
}

.avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  transition: transform 0.25s ease;
}

.group-avatar {
  border-radius: 24rpx;
}

.online-indicator {
  position: absolute;
  bottom: 4rpx;
  right: 4rpx;
  width: 20rpx;
  height: 20rpx;
  background-color: #34c759;
  border: 3rpx solid #ffffff;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.offline-indicator {
  position: absolute;
  bottom: 4rpx;
  right: 4rpx;
  width: 20rpx;
  height: 20rpx;
  background-color: #a1a1a6;
  border: 3rpx solid #ffffff;
  border-radius: 50%;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.chat-info {
  flex: 1;
  overflow: hidden;
}

.chat-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.chat-name {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.3rpx;
}

.chat-time {
  font-size: 24rpx;
  color: #a1a1a6;
  flex-shrink: 0;
}

.chat-preview-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-preview {
  font-size: 28rpx;
  color: #6e6e73;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.chat-preview.unread {
  color: #1d1d1f;
  font-weight: 600;
}

.unread-badge {
  min-width: 36rpx;
  height: 36rpx;
  background-color: #ff3b30;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 16rpx;
  padding: 0 12rpx;
}

.unread-count {
  font-size: 22rpx;
  color: #ffffff;
  font-weight: 600;
}

.chat-arrow {
  font-size: 36rpx;
  color: #c7c7cc;
  margin-left: 16rpx;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.chat-item:active .chat-arrow {
  color: #2997ff;
  transform: translateX(8rpx);
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
  opacity: 0.5;
}

.empty-text {
  font-size: 28rpx;
  color: #6e6e73;
  margin-bottom: 40rpx;
}

.empty-action {
  padding: 20rpx 56rpx;
  background-color: #2997ff;
  border-radius: 40rpx;
  transition: all 0.2s ease;
}

.empty-action:active {
  background-color: #0066cc;
  transform: scale(0.98);
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
  width: 112rpx;
  height: 112rpx;
  background: linear-gradient(135deg, #2997ff 0%, #5856d6 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(41, 151, 255, 0.4);
  transition: all 0.25s ease;
  cursor: pointer;
}

.fab:active {
  transform: scale(0.95);
  box-shadow: 0 4rpx 12rpx rgba(41, 151, 255, 0.4);
}

.fab-icon {
  font-size: 56rpx;
  color: #ffffff;
  font-weight: 300;
  line-height: 1;
}

.fade-in {
  animation: fadeInUp 0.4s ease forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>