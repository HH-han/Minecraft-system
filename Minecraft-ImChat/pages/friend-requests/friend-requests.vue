<template>
  <view class="friend-requests-page">
    <scroll-view class="request-list" scroll-y>
      <view
        v-for="request in requests"
        :key="request.id"
        class="request-item"
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

    async handleRequest(request, action) {
      const api = action === 'accept' ? acceptFriendRequest : rejectFriendRequest

      try {
        const res = await api(request.id)
        if (res.code === 200) {
          // 从列表中移除
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
  background-color: #f5f5f5;
}

.request-list {
  flex: 1;
}

.request-item {
  display: flex;
  padding: 30rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.request-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background-color: #f5f5f5;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.request-info {
  flex: 1;
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.request-name {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 500;
}

.request-time {
  font-size: 24rpx;
  color: #a1a1a6;
}

.request-message {
  display: block;
  font-size: 26rpx;
  color: #6e6e73;
  margin-bottom: 20rpx;
  line-height: 1.4;
}

.request-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
}

.action-btn {
  padding: 12rpx 40rpx;
  border-radius: 36rpx;
  font-size: 26rpx;
}

.action-btn.decline {
  background-color: #f5f5f5;
}

.action-btn.decline text {
  color: #6e6e73;
}

.action-btn.accept {
  background-color: #2997ff;
}

.action-btn.accept text {
  color: #fff;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 200rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #a1a1a6;
}
</style>
