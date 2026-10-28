<template>
  <view class="friend-requests" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('friendRequests.title')"
      :subtitle="navSubtitle"
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
      <view v-if="requests.length" class="req-list">
        <view
          v-for="request in requests"
          :key="request.id"
          class="req-card surface-card grid-overlay"
        >
          <view class="req-card__head">
            <UiAvatar shape="square" :size="96" :src="request.avatar" :name="nameOf(request)" />
            <view class="req-card__info">
              <view class="req-card__top">
                <text class="req-card__name">{{ nameOf(request) }}</text>
                <text class="req-card__time">{{ timeText(request.createTime) }}</text>
              </view>
              <text class="req-card__msg text-ellipsis-2">{{ messageOf(request) }}</text>
            </view>
          </view>

          <view class="req-card__actions">
            <view
              class="req-btn req-btn--accept"
              hover-class="req-btn--accept-hover"
              hover-stay-time="60"
              @click="handleRequest(request, 'accept')"
            >
              <UiIcon name="check" :size="28" color="var(--accent-contrast)" />
              <text>{{ $t('friendRequests.accept') }}</text>
            </view>
            <view
              class="req-btn req-btn--reject"
              hover-class="req-btn--reject-hover"
              hover-stay-time="60"
              @click="handleRequest(request, 'reject')"
            >
              <UiIcon name="close" :size="26" color="var(--text-secondary)" />
              <text>{{ $t('friendRequests.reject') }}</text>
            </view>
          </view>
        </view>
      </view>

      <UiEmpty
        v-else-if="!isLoading"
        icon-name="user-plus"
        :text="$t('friendRequests.empty')"
      />

      <view class="safe-bottom-space--sm"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiAvatar from '../../components/Avatar.vue'
import UiEmpty from '../../components/EmptyState.vue'
import UiIcon from '../../components/Icon.vue'
import {
  getPendingFriendRequests,
  acceptFriendRequest,
  rejectFriendRequest
} from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'
import { setPendingRequestCount } from '../../store/index.js'

export default {
  components: { CustomNavBar, UiAvatar, UiEmpty, UiIcon },
  data() {
    return {
      requests: [],
      isLoading: false,
      isRefreshing: false
    }
  },
  computed: {
    navSubtitle() {
      return this.requests.length
        ? this.$t('friendRequests.summary', { count: this.requests.length })
        : ''
    }
  },
  onLoad() {
    this.loadRequests()
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    nameOf(request) {
      return request.username || request.nickname || this.$t('friendRequests.defaultName')
    },
    messageOf(request) {
      return request.message || request.remark || this.$t('friendRequests.defaultMessage')
    },
    parseTime(value) {
      if (!value && value !== 0) return null
      if (value instanceof Date) return value
      if (typeof value === 'number') return new Date(value)
      const normalized = String(value).replace(/-/g, '/').replace('T', ' ').replace(/\.\d+Z?$/, '')
      const date = new Date(normalized)
      return isNaN(date.getTime()) ? null : date
    },
    timeText(value) {
      const date = this.parseTime(value)
      if (!date) return ''
      const diff = Date.now() - date.getTime()
      if (diff < 60000) return this.$t('friendRequests.justNow')
      if (diff < 3600000) {
        return this.$t('common.minutesAgo', { count: Math.floor(diff / 60000) })
      }
      if (diff < 86400000) {
        return this.$t('common.hoursAgo', { count: Math.floor(diff / 3600000) })
      }
      if (diff < 604800000) {
        return this.$t('common.daysAgo', { count: Math.floor(diff / 86400000) })
      }
      return this.$t('friendRequests.dateShort', {
        month: date.getMonth() + 1,
        day: date.getDate()
      })
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
        uni.showToast({ title: this.$t('friendRequests.loadFailed'), icon: 'none' })
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
          uni.showToast({
            title: action === 'accept'
              ? this.$t('friendRequests.accepted')
              : this.$t('friendRequests.rejected'),
            icon: 'success'
          })
        } else {
          uni.showToast({
            title: (res && res.message) || this.$t('friendRequests.operationFailed'),
            icon: 'none'
          })
        }
      } catch (e) {
        console.error('处理好友申请失败:', e)
        uni.showToast({ title: this.$t('friendRequests.operationFailed'), icon: 'none' })
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
  background-color: var(--bg-secondary);
  background-image: radial-gradient(120% 52% at 50% 0%, var(--accent-light) 0%, transparent 64%);
  color: var(--text-primary);
}

.friend-requests__body {
  flex: 1;
  height: 0;
}

.req-list {
  padding: 24rpx 24rpx 0;
}

.req-card {
  padding: 26rpx;
  margin-bottom: 20rpx;
  background-color: var(--bg-primary);
}

.req-card__head {
  display: flex;
  align-items: flex-start;
  position: relative;
  z-index: 1;
}

.req-card__info {
  flex: 1;
  min-width: 0;
  margin-left: 22rpx;
}

.req-card__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.req-card__name {
  flex: 1;
  min-width: 0;
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.req-card__time {
  flex-shrink: 0;
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}

.req-card__msg {
  margin-top: 10rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
  line-height: 1.5;
}

.req-card__actions {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid var(--divider);
  position: relative;
  z-index: 1;
}

.req-btn {
  flex: 1;
  height: 76rpx;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  transition: opacity var(--transition-fast), transform var(--transition-fast);
}

.req-btn text {
  font-size: var(--font-base);
  font-weight: 600;
}

.req-btn--accept {
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
}

.req-btn--accept text {
  color: var(--accent-contrast);
}

.req-btn--accept-hover {
  transform: scale(0.98);
  opacity: 0.9;
}

.req-btn--reject {
  background-color: var(--bg-chip);
  border: 1rpx solid var(--border-light);
}

.req-btn--reject text {
  color: var(--text-secondary);
}

.req-btn--reject-hover {
  background-color: var(--bg-hover);
  transform: scale(0.98);
}
</style>
