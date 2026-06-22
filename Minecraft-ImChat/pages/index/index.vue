<template>
  <view class="chat-list-page">
    <!-- 顶部搜索栏 -->
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
      </view>
    </view>

    <!-- 标签切换 -->
    <view class="tab-bar">
      <view
        :class="['tab-item', { active: activeTab === 'friends' }]"
        @click="switchTab('friends')"
      >
        <text class="tab-text">好友</text>
      </view>
      <view
        :class="['tab-item', { active: activeTab === 'groups' }]"
        @click="switchTab('groups')"
      >
        <text class="tab-text">群聊</text>
      </view>
    </view>

    <!-- 聊天列表 -->
    <scroll-view
      class="chat-list"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 好友列表 -->
      <view v-if="activeTab === 'friends'">
        <view
          v-for="friend in filteredFriends"
          :key="friend.id"
          class="chat-item"
          @click="goToChat(friend)"
        >
          <view class="avatar-wrapper">
            <image
              class="avatar"
              :src="friend.avatar || defaultAvatar"
              mode="aspectFill"
            />
            <view v-if="friend.online" class="online-indicator"></view>
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
              <view v-if="friend.unreadCount > 0" class="unread-badge">
                <text class="unread-count">{{ friend.unreadCount > 99 ? '99+' : friend.unreadCount }}</text>
              </view>
            </view>
          </view>
        </view>

        <view v-if="filteredFriends.length === 0" class="empty-state">
          <text class="empty-icon">💬</text>
          <text class="empty-text">暂无{{ searchKeyword ? '搜索结果' : '好友' }}</text>
          <view v-if="!searchKeyword" class="empty-action" @click="goToAddFriend">
            <text>添加好友</text>
          </view>
        </view>
      </view>

      <!-- 群聊列表 -->
      <view v-if="activeTab === 'groups'">
        <view
          v-for="group in filteredGroups"
          :key="group.id"
          class="chat-item"
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
              <view v-if="group.unreadCount > 0" class="unread-badge">
                <text class="unread-count">{{ group.unreadCount > 99 ? '99+' : group.unreadCount }}</text>
              </view>
            </view>
          </view>
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

    <!-- 悬浮按钮 -->
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
      // 搜索逻辑已在 computed 中处理
    },

    async onRefresh() {
      this.isRefreshing = true
      await Promise.all([this.loadFriends(), this.loadGroups()])
      this.isRefreshing = false
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
  background-color: #f5f5f5;
}

/* 搜索栏 */
.search-bar {
  padding: 20rpx 30rpx;
  background-color: #fff;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #1d1d1f;
}

.search-placeholder {
  color: #a1a1a6;
}

/* 标签栏 */
.tab-bar {
  display: flex;
  padding: 0 30rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #e5e5e5;
}

.tab-item {
  padding: 24rpx 0;
  margin-right: 60rpx;
  position: relative;
}

.tab-text {
  font-size: 32rpx;
  color: #6e6e73;
}

.tab-item.active .tab-text {
  color: #1d1d1f;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background-color: #2997ff;
  border-radius: 2rpx;
}

/* 聊天列表 */
.chat-list {
  flex: 1;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 24rpx 30rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.chat-item:active {
  background-color: #f5f5f5;
}

.avatar-wrapper {
  position: relative;
  margin-right: 24rpx;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background-color: #f5f5f5;
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
  border: 3rpx solid #fff;
  border-radius: 50%;
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
  font-weight: 500;
}

.chat-time {
  font-size: 24rpx;
  color: #a1a1a6;
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
}

.chat-preview.unread {
  color: #1d1d1f;
  font-weight: 500;
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
  padding: 0 10rpx;
}

.unread-count {
  font-size: 22rpx;
  color: #fff;
  font-weight: 600;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #a1a1a6;
  margin-bottom: 40rpx;
}

.empty-action {
  padding: 20rpx 60rpx;
  background-color: #2997ff;
  border-radius: 48rpx;
}

.empty-action text {
  color: #fff;
  font-size: 28rpx;
}

/* 悬浮按钮 */
.fab {
  position: fixed;
  right: 40rpx;
  bottom: 200rpx;
  width: 100rpx;
  height: 100rpx;
  background-color: #2997ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(41, 151, 255, 0.4);
}

.fab:active {
  transform: scale(0.95);
}

.fab-icon {
  font-size: 60rpx;
  color: #fff;
  font-weight: 300;
}
</style>
