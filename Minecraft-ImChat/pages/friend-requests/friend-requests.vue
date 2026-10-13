<template>
  <view class="friend-requests">
    <CustomNavBar
      :show-back="true"
      title="新的朋友"
      title-align="center"
      @back="goBack"
    />

    <scroll-view
      class="friend-requests__body"
      scroll-y
      :show-scrollbar="false"
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
    >
      <view v-if="requests.length" class="group">
        <view
          v-for="request in requests"
          :key="request.id"
          class="req"
        >
          <UiAvatar shape="square" :size="96" :src="request.avatar" :name="nameOf(request)" />
          <view class="req__body">
            <view class="req__top">
              <text class="req__name">{{ nameOf(request) }}</text>
              <text class="req__time">{{ formatRelativeTime(request.createTime) }}</text>
            </view>
            <text class="req__msg">{{ messageOf(request) }}</text>
          </view>
          <view class="req__actions">
            <view class="btn btn--accept" hover-class="btn--hover" hover-stay-time="60" @click="handleRequest(request, 'accept')">
              <text>接受</text>
            </view>
            <view class="btn btn--reject" hover-class="btn--reject-hover" hover-stay-time="60" @click="handleRequest(request, 'reject')">
              <text>拒绝</text>
            </view>
          </view>
        </view>
      </view>

      <UiEmpty
        v-else-if="!isLoading"
        icon="👤"
        text="暂无好友申请"
      />

      <view class="friend-requests__safe"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiAvatar from '../../components/Avatar.vue'
import UiEmpty from '../../components/EmptyState.vue'
import {
  getPendingFriendRequests,
  acceptFriendRequest,
  rejectFriendRequest
} from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'
import { formatRelativeTime } from '../../utils/format.js'
import { setPendingRequestCount } from '../../store/index.js'

export default {
  components: { CustomNavBar, UiAvatar, UiEmpty },
  data() {
    return {
      requests: [],
      isLoading: false,
      isRefreshing: false
    }
  },
  onLoad() {
    this.loadRequests()
  },
  methods: {
    formatRelativeTime,
    goBack() {
      uni.navigateBack()
    },
    nameOf(request) {
      return request.username || request.nickname || '对方'
    },
    messageOf(request) {
      return request.message || request.remark || '请求添加你为好友'
    },
    async loadRequests() {
      const userId = getUserId()
      if (!userId) return
      this.isLoading = true
      try {
        const res = await getPendingFriendRequests(userId)
        if (res && res.code === 200 && Array.isArray(res.data)) {
          this.requests = res.data
        } else {
          this.requests = []
        }
      } catch (e) {
        console.error('加载好友申请失败:', e)
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
        if (res && res.code === 200) {
          this.requests = this.requests.filter((r) => r.id !== request.id)
          setPendingRequestCount(this.requests.length)
          uni.showToast({ title: action === 'accept' ? '已接受' : '已拒绝', icon: 'success' })
        } else {
          uni.showToast({ title: (res && res.message) || '操作失败', icon: 'none' })
        }
      } catch (e) {
        console.error('处理好友申请失败:', e)
        uni.showToast({ title: '操作失败', icon: 'none' })
      }
    }
  }
}
</script>

<style scoped>
.friend-requests {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.friend-requests__body {
  flex: 1;
  height: 0;
}

.group {
  background-color: #ffffff;
  margin-top: 20rpx;
}

.req {
  display: flex;
  align-items: flex-start;
  padding: 24rpx 32rpx;
  position: relative;
}

.req::after {
  content: '';
  position: absolute;
  left: 152rpx;
  right: 0;
  bottom: 0;
  height: 1rpx;
  background-color: #ededed;
  transform: scaleY(0.5);
}

.req:last-child::after {
  display: none;
}

.req__body {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
}

.req__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.req__name {
  font-size: 32rpx;
  color: #191919;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.req__time {
  flex-shrink: 0;
  font-size: 24rpx;
  color: #b2b2b2;
}

.req__msg {
  display: block;
  font-size: 26rpx;
  color: #888888;
  margin-top: 10rpx;
  line-height: 1.5;
}

.req__actions {
  flex-shrink: 0;
  margin-left: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.btn {
  min-width: 132rpx;
  height: 60rpx;
  padding: 0 24rpx;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn text {
  font-size: 26rpx;
  font-weight: 500;
}

.btn--accept {
  background-color: #07c160;
}

.btn--accept text {
  color: #ffffff;
}

.btn--hover {
  background-color: #06ad56;
}

.btn--reject {
  background-color: #f2f2f2;
}

.btn--reject text {
  color: #888888;
}

.btn--reject-hover {
  background-color: #e6e6e6;
}

.friend-requests__safe {
  height: calc(24rpx + env(safe-area-inset-bottom));
}
</style>
