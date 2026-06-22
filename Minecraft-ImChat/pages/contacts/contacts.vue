<template>
  <view class="contacts-page">
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="searchKeyword"
          placeholder="搜索好友"
          placeholder-class="search-placeholder"
          @input="onSearch"
        />
        <view v-if="searchKeyword" class="clear-btn" @click="clearSearch">
          <text>✕</text>
        </view>
      </view>
    </view>

    <view class="request-entry" @click="goToFriendRequests">
      <view class="entry-icon">
        <text>👥</text>
      </view>
      <view class="entry-info">
        <text class="entry-title">新的朋友</text>
        <view v-if="requestCount > 0" class="request-badge bounce-in">
          <text class="badge-text">{{ requestCount }}</text>
        </view>
      </view>
      <view class="entry-arrow">›</view>
    </view>

    <view class="section-divider"></view>

    <scroll-view class="contact-list" scroll-y>
      <view
        v-for="(friend, index) in filteredFriends"
        :key="friend.id"
        :style="{ animationDelay: `${index * 0.05}s` }"
        class="contact-item fade-in"
        @click="goToChat(friend)"
      >
        <view class="avatar-wrapper">
          <image
            class="contact-avatar"
            :src="friend.avatar || defaultAvatar"
            mode="aspectFill"
          />
          <view v-if="friend.online" class="online-indicator"></view>
          <view v-else class="offline-indicator"></view>
        </view>
        <view class="contact-info">
          <text class="contact-name">{{ friend.name }}</text>
          <text v-if="friend.phone" class="contact-phone">{{ friend.phone }}</text>
        </view>
        <view class="contact-arrow">›</view>
      </view>

      <view v-if="filteredFriends.length === 0" class="empty-state">
        <text class="empty-icon">👤</text>
        <text class="empty-text">暂无{{ searchKeyword ? '搜索结果' : '好友' }}</text>
        <view v-if="!searchKeyword" class="empty-action" @click="goToAddFriend">
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
    },

    clearSearch() {
      this.searchKeyword = ''
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
.contacts-page {
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

.request-entry {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #f0f0f0;
  transition: all 0.2s ease;
  cursor: pointer;
}

.request-entry:active {
  background-color: #f5f5f7;
  transform: scale(0.995);
}

.entry-icon {
  width: 72rpx;
  height: 72rpx;
  background: linear-gradient(135deg, #2997ff 0%, #5856d6 100%);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(41, 151, 255, 0.3);
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
  font-weight: 600;
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

.entry-arrow {
  font-size: 36rpx;
  color: #c7c7cc;
}

.section-divider {
  height: 16rpx;
  background-color: #f5f5f7;
}

.contact-list {
  flex: 1;
  background-color: #ffffff;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f2;
  transition: all 0.2s ease;
  cursor: pointer;
}

.contact-item:active {
  background-color: #f5f5f7;
  transform: scale(0.995);
}

.avatar-wrapper {
  position: relative;
  margin-right: 24rpx;
}

.contact-avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
}

.online-indicator {
  position: absolute;
  bottom: 4rpx;
  right: 4rpx;
  width: 18rpx;
  height: 18rpx;
  background-color: #34c759;
  border: 3rpx solid #ffffff;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.offline-indicator {
  position: absolute;
  bottom: 4rpx;
  right: 4rpx;
  width: 18rpx;
  height: 18rpx;
  background-color: #a1a1a6;
  border: 3rpx solid #ffffff;
  border-radius: 50%;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.contact-info {
  flex: 1;
  overflow: hidden;
}

.contact-name {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.3rpx;
  line-height: 1.4;
}

.contact-phone {
  font-size: 26rpx;
  color: #a1a1a6;
  margin-top: 4rpx;
}

.contact-arrow {
  font-size: 36rpx;
  color: #c7c7cc;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.contact-item:active .contact-arrow {
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
  font-size: 32rpx;
  color: #a1a1a6;
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

.bounce-in {
  animation: bounceIn 0.4s ease;
}

@keyframes bounceIn {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}
</style>