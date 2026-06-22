<template>
  <view class="friend-requests-page">
    <scroll-view
      class="request-list"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view
        v-for="(request, index) in requests"
        :key="request.id"
        :style="{ animationDelay: `${index * 0.05}s` }"
        class="request-item fade-in"
      >
        <image
          class="request-avatar"
          :src="request.avatar || defaultAvatar"
          mode="aspectFill"
        />
        <view class="request-info">
          <view class="request-header">
            <text class="request-name">{{ request.username }}</text>
            <text class="request-time">{{ formatTime(request.createTime) }}</text>
          </view>
          <text class="request-message">{{ request.message || '请求添加你为好友' }}</text>
          <view class="request-actions">
            <view class="action-btn decline" @click="handleRequest(request, 'reject')">
              <text>拒绝</text>
            </view>
            <view class="action-btn accept" @click="handleRequest(request, 'accept')">
              <text>同意</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="requests.length === 0 && !isLoading" class="empty-state">
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无好友申请</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import {
  getPendingFriendRequests,
  acceptFriendRequest,
  rejectFriendRequest
} from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'

export default {
  data() {
    return {
      requests: [],
      isLoading: false,
      isRefreshing: false,
      defaultAvatar: '/static/default-avatar.png'
    }
  },

  onLoad() {
    this.loadRequests()
  },

  methods: {
    async loadRequests() {
      const userInfo = getUserInfo()
      if (!userInfo?.id) return

      this.isLoading = true

      try {
        const res = await getPendingFriendRequests(userInfo.id)
        if (res.code === 200 && res.data) {
          this.requests = res.data
        }
      } catch (e) {
        console.error('加载好友请求失败:', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.isLoading = false
      }
    },

    async onRefresh() {
      this.isRefreshing = true
      await this.loadRequests()
      this.isRefreshing = false
    },

    async handleRequest(request, action) {
      const api = action === 'accept' ? acceptFriendRequest : rejectFriendRequest

      try {
        const res = await api(request.id)
        if (res.code === 200) {
          const index = this.requests.findIndex(r => r.id === request.id)
          if (index > -1) {
            this.requests.splice(index, 1)
          }

          uni.showToast({
            title: action === 'accept' ? '已同意' : '已拒绝',
            icon: 'success'
          })
        } else {
          uni.showToast({ title: res.message || '操作失败', icon: 'none' })
        }
      } catch (e) {
        console.error('处理好友请求失败:', e)
        uni.showToast({ title: '操作失败', icon: 'none' })
      }
    },

    formatTime(timeStr) {
      if (!timeStr) return ''

      const date = new Date(timeStr)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

      return `${date.getMonth() + 1}/${date.getDate()}`
    }
  }
}
</script>

<style scoped>
.friend-requests-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f7;
}

.request-list {
  flex: 1;
  padding: 16rpx 0;
}

.request-item {
  display: flex;
  align-items: flex-start;
  padding: 32rpx;
  margin: 16rpx 32rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
}

.request-item:active {
  transform: scale(0.99);
  box-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.04);
}

.request-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.request-info {
  flex: 1;
  min-width: 0;
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
  gap: 16rpx;
}

.request-name {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.3rpx;
}

.request-time {
  font-size: 24rpx;
  color: #6e6e73;
  flex-shrink: 0;
}

.request-message {
  display: block;
  font-size: 28rpx;
  color: #6e6e73;
  margin-bottom: 24rpx;
  line-height: 1.5;
}

.request-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
}

.action-btn {
  padding: 12rpx 40rpx;
  border-radius: 36rpx;
  font-size: 26rpx;
  font-weight: 500;
  transition: all 0.2s ease;
}

.action-btn text {
  font-size: 26rpx;
  line-height: 1;
}

.action-btn.decline {
  background-color: #f5f5f7;
}

.action-btn.decline:active {
  background-color: #e8e8ed;
  transform: scale(0.97);
}

.action-btn.decline text {
  color: #6e6e73;
}

.action-btn.accept {
  background: linear-gradient(135deg, #2997ff 0%, #0066cc 100%);
}

.action-btn.accept:active {
  transform: scale(0.97);
}

.action-btn.accept text {
  color: #ffffff;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 240rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 32rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 28rpx;
  color: #6e6e73;
  letter-spacing: -0.2rpx;
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