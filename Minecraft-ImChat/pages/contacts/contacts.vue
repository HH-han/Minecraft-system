<template>
  <view class="contacts-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="searchKeyword"
          placeholder="搜索好友"
          @input="onSearch"
        />
      </view>
    </view>

    <!-- 好友请求入口 -->
    <view class="request-entry" @click="goToFriendRequests">
      <view class="entry-icon">
        <text>👥</text>
      </view>
      <view class="entry-info">
        <text class="entry-title">新的朋友</text>
        <view v-if="requestCount > 0" class="request-badge">
          <text class="badge-text">{{ requestCount }}</text>
        </view>
      </view>
    </view>

    <!-- 分组列表 -->
    <view class="section-divider"></view>

    <!-- 好友列表 -->
    <scroll-view class="contact-list" scroll-y>
      <view
        v-for="friend in filteredFriends"
        :key="friend.id"
        class="contact-item"
        @click="goToChat(friend)"
      >
        <image
          class="contact-avatar"
          :src="friend.avatar || defaultAvatar"
          mode="aspectFill"
        />
        <view class="contact-info">
          <text class="contact-name">{{ friend.name }}</text>
        </view>
        <view class="contact-status">
          <view v-if="friend.online" class="online-dot"></view>
        </view>
      </view>

      <view v-if="filteredFriends.length === 0" class="empty-state">
        <text class="empty-icon">👤</text>
        <text class="empty-text">暂无好友</text>
        <view class="empty-action" @click="goToAddFriend">
          <text>添加好友</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getFriendInfoList, getPendingFriendRequests } from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'

export default {
  data() {
    return {
      searchKeyword: '',
      friends: [],
      requestCount: 0,
      defaultAvatar: '/static/default-avatar.png'
    }
  },

  computed: {
    filteredFriends() {
      if (!this.searchKeyword) return this.friends

      const keyword = this.searchKeyword.toLowerCase()
      return this.friends.filter(f =>
        f.name.toLowerCase().includes(keyword)
      )
    }
  },

  onLoad() {
    this.loadUserInfo()
  },

  onShow() {
    this.loadFriends()
    this.loadFriendRequests()
  },

  methods: {
    loadUserInfo() {
      const userInfo = getUserInfo()
      if (userInfo) {
        this.userInfo = userInfo
      }
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
            online: f.online || false,
            phone: f.phone || ''
          }))
        }
      } catch (e) {
        console.error('加载好友列表失败:', e)
      }
    },

    async loadFriendRequests() {
      if (!this.userInfo?.id) return

      try {
        const res = await getPendingFriendRequests(this.userInfo.id)
        if (res.code === 200 && res.data) {
          this.requestCount = res.data.length
        }
      } catch (e) {
        console.error('加载好友请求失败:', e)
      }
    },

    onSearch() {
      // 搜索逻辑已在 computed 中处理
    },

    goToChat(friend) {
      uni.navigateTo({
        url: `/pages/chat/chat?type=single&id=${friend.id}&name=${encodeURIComponent(friend.name)}&avatar=${encodeURIComponent(friend.avatar)}`
      })
    },

    goToFriendRequests() {
      uni.navigateTo({ url: '/pages/friend-requests/friend-requests' })
    },

    goToAddFriend() {
      uni.navigateTo({ url: '/pages/add-friend/add-friend' })
    }
  }
}
</script>

<style scoped>
/* 仿 Apple 官网设计规范 */
.contacts-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f7;
}

/* 搜索栏 */
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
}

.search-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
  opacity: 0.6;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #1d1d1f;
}

/* 好友请求入口 */
.request-entry {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #f0f0f0;
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.request-entry:active {
  background-color: #f5f5f7;
}

.entry-icon {
  width: 72rpx;
  height: 72rpx;
  background-color: #f5f5f7;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.entry-icon text {
  font-size: 36rpx;
}

.entry-info {
  flex: 1;
  display: flex;
  align-items: center;
}

.entry-title {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 500;
  letter-spacing: -0.3rpx;
}

.request-badge {
  margin-left: 16rpx;
  min-width: 36rpx;
  height: 36rpx;
  background-color: #ff3b30;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 12rpx;
}

.badge-text {
  font-size: 24rpx;
  color: #ffffff;
  font-weight: 600;
}

/* 分隔线 */
.section-divider {
  height: 16rpx;
  background-color: #f5f5f7;
}

/* 联系人列表 */
.contact-list {
  flex: 1;
  background-color: #ffffff;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.contact-item:active {
  background-color: #f5f5f7;
}

.contact-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  margin-right: 24rpx;
}

.contact-info {
  flex: 1;
}

.contact-name {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 500;
  letter-spacing: -0.3rpx;
}

.contact-status {
  padding: 0 16rpx;
}

.online-dot {
  width: 16rpx;
  height: 16rpx;
  background-color: #34c759;
  border-radius: 50%;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
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
  cursor: pointer;
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
</style>
