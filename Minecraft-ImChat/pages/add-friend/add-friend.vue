<template>
  <view class="add-friend-page">
    <view class="search-section">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="searchKeyword"
          placeholder="输入手机号搜索用户"
          placeholder-class="search-placeholder"
          type="number"
          maxlength="11"
          @confirm="searchUser"
        />
        <view v-if="searchKeyword" class="clear-btn" @click="clearSearch">
          <text>✕</text>
        </view>
      </view>
      <view class="search-btn" @click="searchUser">
        <text>搜索</text>
      </view>
    </view>

    <view v-if="searchResult" class="search-result slide-up">
      <view class="result-header">
        <text class="result-title">搜索结果</text>
      </view>
      <view class="user-card">
        <image
          class="user-avatar"
          :src="searchResult.avatar || defaultAvatar"
          mode="aspectFill"
        />
        <view class="user-info">
          <text class="user-name">{{ searchResult.username }}</text>
          <text class="user-phone">{{ formatPhone(searchResult.phone) }}</text>
        </view>
        <view
          :class="['add-btn', { added: isAdded || isFriend }]"
          @click="addFriend"
        >
          <text>{{ isAdded ? '已发送' : isFriend ? '已是好友' : '添加' }}</text>
        </view>
      </view>
    </view>

    <view class="tips-section">
      <view class="tips-header">
        <text class="tips-title">温馨提示</text>
      </view>
      <view class="tips-list">
        <view class="tip-item">
          <text class="tip-icon">💡</text>
          <text class="tip-text">输入对方的手机号即可发送好友请求</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">💡</text>
          <text class="tip-text">对方同意后即可开始聊天</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">💡</text>
          <text class="tip-text">请礼貌用语，文明交流</text>
        </view>
      </view>
    </view>

    <view v-if="searchHistory.length > 0" class="history-section">
      <view class="history-header">
        <text class="history-title">搜索历史</text>
        <text class="clear-btn" @click="clearHistory">清空</text>
      </view>
      <view class="history-list">
        <view
          v-for="(item, index) in searchHistory"
          :key="index"
          :style="{ animationDelay: `${index * 0.05}s` }"
          class="history-item fade-in"
          @click="selectHistory(item)"
        >
          <text class="history-phone">{{ item.phone }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { sendFriendRequest, getUserByAccount } from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'

export default {
  data() {
    return {
      searchKeyword: '',
      searchResult: null,
      searchHistory: [],
      isAdded: false,
      isFriend: false,
      defaultAvatar: '/static/default-avatar.png'
    }
  },

  onLoad() {
    this.loadHistory()
  },

  methods: {
    async searchUser() {
      if (!this.searchKeyword || this.searchKeyword.length !== 11) {
        uni.showToast({ title: '请输入11位手机号', icon: 'none' })
        return
      }

      uni.showLoading({ title: '搜索中...' })

      try {
        const res = await getUserByAccount(this.searchKeyword)

        if (res.code === 200 && res.data) {
          this.searchResult = res.data
          this.isAdded = false
          this.isFriend = false

          this.saveToHistory(this.searchResult)
        } else {
          this.searchResult = null
          uni.showToast({ title: '未找到该用户', icon: 'none' })
        }
      } catch (e) {
        console.error('搜索用户失败:', e)
        uni.showToast({ title: '搜索失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },

    async addFriend() {
      if (!this.searchResult || this.isAdded || this.isFriend) return

      const userInfo = getUserInfo()
      if (!userInfo) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }

      uni.showModal({
        title: '发送好友请求',
        placeholderText: '请输入验证消息（选填）',
        editable: true,
        placeholderText: '我是...',
        success: async (res) => {
          if (res.confirm) {
            try {
              const res2 = await sendFriendRequest({
                senderId: userInfo.id,
                receiverPhone: this.searchKeyword,
                message: res.content || ''
              })

              if (res2.code === 200) {
                this.isAdded = true
                uni.showToast({ title: '请求已发送', icon: 'success' })
              } else {
                uni.showToast({ title: res2.message || '发送失败', icon: 'none' })
              }
            } catch (e) {
              console.error('发送好友请求失败:', e)
              uni.showToast({ title: '发送失败', icon: 'none' })
            }
          }
        }
      })
    },

    formatPhone(phone) {
      if (!phone) return ''
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    },

    loadHistory() {
      try {
        const history = uni.getStorageSync('friendSearchHistory')
        this.searchHistory = history ? JSON.parse(history) : []
      } catch (e) {
        this.searchHistory = []
      }
    },

    saveToHistory(user) {
      const history = this.searchHistory.filter(h => h.phone !== user.phone)
      history.unshift({
        phone: user.phone,
        username: user.username,
        avatar: user.avatar
      })

      this.searchHistory = history.slice(0, 10)
      uni.setStorageSync('friendSearchHistory', JSON.stringify(this.searchHistory))
    },

    clearHistory() {
      uni.showModal({
        title: '提示',
        content: '确定要清空搜索历史吗？',
        success: (res) => {
          if (res.confirm) {
            this.searchHistory = []
            uni.removeStorageSync('friendSearchHistory')
          }
        }
      })
    },

    clearSearch() {
      this.searchKeyword = ''
    },

    selectHistory(item) {
      this.searchKeyword = item.phone
      this.searchResult = item
      this.isAdded = false
      this.isFriend = false
    }
  }
}
</script>

<style scoped>
.add-friend-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f7;
  padding: 0 32rpx;
  box-sizing: border-box;
}

.search-section {
  display: flex;
  align-items: center;
  padding: 32rpx 0;
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #ffffff;
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
  margin-right: 20rpx;
  border: 1rpx solid #e5e5e5;
  transition: all 0.25s ease;
}

.search-input-wrapper:focus-within {
  border-color: #2997ff;
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

.search-btn {
  padding: 16rpx 36rpx;
  background: linear-gradient(135deg, #2997ff 0%, #0066cc 100%);
  border-radius: 36rpx;
  transition: all 0.2s ease;
}

.search-btn:active {
  transform: scale(0.97);
}

.search-btn text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 500;
}

.search-result {
  margin-top: 24rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.result-header {
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.result-title {
  font-size: 26rpx;
  color: #6e6e73;
  font-weight: 500;
  letter-spacing: -0.2rpx;
}

.user-card {
  display: flex;
  align-items: center;
  padding: 32rpx;
}

.user-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  margin-right: 24rpx;
}

.user-info {
  flex: 1;
}

.user-name {
  display: block;
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  margin-bottom: 8rpx;
  letter-spacing: -0.3rpx;
}

.user-phone {
  font-size: 26rpx;
  color: #6e6e73;
}

.add-btn {
  padding: 16rpx 32rpx;
  background: linear-gradient(135deg, #2997ff 0%, #0066cc 100%);
  border-radius: 36rpx;
  transition: all 0.2s ease;
}

.add-btn:active {
  transform: scale(0.97);
}

.add-btn text {
  font-size: 26rpx;
  color: #ffffff;
  font-weight: 500;
}

.add-btn.added {
  background-color: #e5e5e5;
  background: none;
}

.add-btn.added text {
  color: #6e6e73;
}

.tips-section {
  margin-top: 32rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.tips-header {
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.tips-title {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.3rpx;
}

.tips-list {
  padding: 24rpx 32rpx;
}

.tip-item {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
}

.tip-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
  opacity: 0.6;
}

.tip-text {
  font-size: 26rpx;
  color: #6e6e73;
  line-height: 1.5;
}

.history-section {
  margin-top: 32rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.history-title {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.3rpx;
}

.history-list {
  display: flex;
  flex-wrap: wrap;
  padding: 24rpx;
  gap: 20rpx;
}

.history-item {
  padding: 16rpx 32rpx;
  background-color: #f5f5f7;
  border-radius: 36rpx;
  transition: all 0.2s ease;
}

.history-item:active {
  background-color: #d2d2d6;
}

.history-phone {
  font-size: 26rpx;
  color: #6e6e73;
}

.slide-up {
  animation: slideUp 0.3s ease forwards;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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