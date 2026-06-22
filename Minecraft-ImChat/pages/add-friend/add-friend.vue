<template>
  <view class="add-friend-page">
    <!-- 搜索区域 -->
    <view class="search-section">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="searchKeyword"
          placeholder="输入手机号搜索用户"
          type="number"
          maxlength="11"
          @confirm="searchUser"
        />
      </view>
      <view class="search-btn" @click="searchUser">
        <text>搜索</text>
      </view>
    </view>

    <!-- 搜索结果 -->
    <view v-if="searchResult" class="search-result">
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

    <!-- 添加说明 -->
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

    <!-- 添加历史 -->
    <view v-if="searchHistory.length > 0" class="history-section">
      <view class="history-header">
        <text class="history-title">搜索历史</text>
        <text class="clear-btn" @click="clearHistory">清空</text>
      </view>
      <view class="history-list">
        <view
          v-for="(item, index) in searchHistory"
          :key="index"
          class="history-item"
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

          // 保存搜索历史
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

      // 只保留最近10条
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
  background-color: #f5f5f5;
}

/* 搜索区域 */
.search-section {
  display: flex;
  align-items: center;
  padding: 30rpx;
  background-color: #fff;
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
  margin-right: 20rpx;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
}

.search-btn {
  padding: 16rpx 40rpx;
  background-color: #2997ff;
  border-radius: 36rpx;
}

.search-btn text {
  font-size: 28rpx;
  color: #fff;
}

/* 搜索结果 */
.search-result {
  margin: 30rpx;
  background-color: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}

.result-header {
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.result-title {
  font-size: 28rpx;
  color: #6e6e73;
}

.user-card {
  display: flex;
  align-items: center;
  padding: 30rpx;
}

.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background-color: #f5f5f5;
  margin-right: 24rpx;
}

.user-info {
  flex: 1;
}

.user-name {
  display: block;
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 500;
  margin-bottom: 8rpx;
}

.user-phone {
  font-size: 26rpx;
  color: #6e6e73;
}

.add-btn {
  padding: 16rpx 32rpx;
  background-color: #2997ff;
  border-radius: 36rpx;
}

.add-btn text {
  font-size: 26rpx;
  color: #fff;
}

.add-btn.added {
  background-color: #e5e5e5;
}

.add-btn.added text {
  color: #6e6e73;
}

/* 提示区域 */
.tips-section {
  margin: 30rpx;
  background-color: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}

.tips-header {
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.tips-title {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 500;
}

.tips-list {
  padding: 20rpx 30rpx;
}

.tip-item {
  display: flex;
  align-items: center;
  padding: 16rpx 0;
}

.tip-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
}

.tip-text {
  font-size: 26rpx;
  color: #6e6e73;
}

/* 搜索历史 */
.history-section {
  margin: 30rpx;
  background-color: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.history-title {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 500;
}

.clear-btn {
  font-size: 26rpx;
  color: #2997ff;
}

.history-list {
  display: flex;
  flex-wrap: wrap;
  padding: 20rpx;
  gap: 20rpx;
}

.history-item {
  padding: 16rpx 32rpx;
  background-color: #f5f5f5;
  border-radius: 36rpx;
}

.history-phone {
  font-size: 26rpx;
  color: #6e6e73;
}
</style>
