<template>
  <view class="add-friend">
    <CustomNavBar
      :show-back="true"
      title="添加朋友"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="add-friend__body" scroll-y :show-scrollbar="false">
      <view class="search-row">
        <view class="search-box">
          <UiIcon name="search" :size="36" color="#B2B2B2" />
          <input
            class="search-box__input"
            v-model="keyword"
            type="text"
            placeholder="手机号/账号"
            placeholder-class="search-box__ph"
            confirm-type="search"
            @confirm="searchUser"
          />
          <view v-if="keyword" class="search-box__clear" @click="keyword = ''">
            <UiIcon name="close" :size="20" color="#FFFFFF" />
          </view>
        </view>
        <view class="search-action" hover-class="search-action--hover" hover-stay-time="60" @click="searchUser">
          <text>搜索</text>
        </view>
      </view>

      <view v-if="searchResult" class="group">
        <view class="group__title">搜索结果</view>
        <view class="result">
          <UiAvatar shape="square" :size="96" :src="searchResult.avatar" :name="resultName" />
          <view class="result__body">
            <text class="result__name">{{ resultName }}</text>
            <text v-if="resultSub" class="result__sub">{{ resultSub }}</text>
          </view>
          <view v-if="isFriend" class="op-btn is-disabled">
            <text>已是好友</text>
          </view>
          <view v-else-if="isAdded" class="op-btn is-disabled">
            <text>已发送</text>
          </view>
          <view v-else class="op-btn is-primary" hover-class="op-btn--hover" hover-stay-time="60" @click="addFriend">
            <text>添加到通讯录</text>
          </view>
        </view>
      </view>

      <view v-if="searchHistory.length" class="group">
        <view class="group__head">
          <text class="group__title">搜索历史</text>
          <text class="group__extra" hover-class="group__extra--hover" hover-stay-time="60" @click="clearHistory">清空</text>
        </view>
        <view
          v-for="(item, index) in searchHistory"
          :key="item.phone + '-' + index"
          class="history"
          hover-class="history--hover"
          hover-stay-time="60"
          @click="selectHistory(item)"
        >
          <UiAvatar shape="square" :size="72" :src="item.avatar" :name="item.username || item.phone" />
          <view class="history__body">
            <text class="history__name">{{ item.username || maskPhone(item.phone) }}</text>
            <text class="history__sub">{{ maskPhone(item.phone) }}</text>
          </view>
        </view>
      </view>

      <view class="group">
        <view class="group__title">温馨提示</view>
        <view class="tip">
          <UiIcon name="info" :size="34" color="#07C160" />
          <text class="tip__text">输入对方手机号或账号即可发送好友请求</text>
        </view>
        <view class="tip">
          <UiIcon name="info" :size="34" color="#07C160" />
          <text class="tip__text">对方同意后即可开始聊天</text>
        </view>
      </view>

      <view class="add-friend__safe"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import { getUserByAccount, getFriendInfoList, sendFriendRequest } from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'
import { maskPhone } from '../../utils/format.js'

const HISTORY_KEY = 'friendSearchHistory'

export default {
  components: { CustomNavBar, UiIcon, UiAvatar },
  data() {
    return {
      keyword: '',
      searchResult: null,
      isFriend: false,
      isAdded: false,
      searchHistory: [],
      // 已是好友判断依据（拿不到则为空集合，按 false 处理）
      friendIds: [],
      friendKeys: []
    }
  },
  computed: {
    resultName() {
      const r = this.searchResult || {}
      return r.nickname || r.username || r.account || '未知用户'
    },
    resultSub() {
      const r = this.searchResult || {}
      if (r.account && r.phone) return `账号：${r.account} · ${maskPhone(r.phone)}`
      if (r.account) return `账号：${r.account}`
      if (r.phone) return maskPhone(r.phone)
      return ''
    }
  },
  onLoad() {
    this.loadHistory()
    this.loadFriends()
  },
  methods: {
    maskPhone,
    goBack() {
      uni.navigateBack()
    },
    async loadFriends() {
      const userId = getUserId()
      if (!userId) return
      try {
        const res = await getFriendInfoList(userId)
        if (res && res.code === 200 && Array.isArray(res.data)) {
          this.friendIds = res.data.map((f) => String(f.friendId))
          this.friendKeys = res.data
            .reduce((acc, f) => acc.concat([f.phone, f.email, f.username]), [])
            .filter((v) => v !== undefined && v !== null && v !== '')
            .map(String)
        }
      } catch (e) {
        console.error('加载好友列表失败:', e)
      }
    },
    checkIsFriend(user) {
      if (!user) return false
      if (this.friendIds.includes(String(user.id))) return true
      const keys = [user.account, user.phone, user.username]
        .filter((v) => v !== undefined && v !== null && v !== '')
        .map(String)
      return keys.some((k) => this.friendKeys.includes(k))
    },
    async searchUser() {
      const kw = this.keyword.trim()
      if (!kw) {
        uni.showToast({ title: '请输入手机号或账号', icon: 'none' })
        return
      }
      uni.showLoading({ title: '搜索中…' })
      try {
        const res = await getUserByAccount(kw)
        if (res && res.code === 200 && res.data) {
          this.searchResult = res.data
          this.isAdded = false
          this.isFriend = this.checkIsFriend(res.data)
          this.saveToHistory(res.data)
        } else {
          this.searchResult = null
          this.isAdded = false
          this.isFriend = false
          uni.showToast({ title: '未找到该用户', icon: 'none' })
        }
      } catch (e) {
        console.error('搜索用户失败:', e)
        uni.showToast({ title: '搜索失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    addFriend() {
      if (!this.searchResult || this.isAdded || this.isFriend) return
      const userId = getUserId()
      if (!userId) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }
      uni.showModal({
        title: '发送好友请求',
        editable: true,
        placeholderText: '请输入验证消息（选填）',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await sendFriendRequest({
              senderId: userId,
              receiverPhone: this.keyword.trim(),
              message: res.content || ''
            })
            if (result && result.code === 200) {
              this.isAdded = true
              uni.showToast({ title: '请求已发送', icon: 'success' })
            } else {
              uni.showToast({ title: (result && result.message) || '发送失败', icon: 'none' })
            }
          } catch (e) {
            console.error('发送好友请求失败:', e)
            uni.showToast({ title: '发送失败', icon: 'none' })
          }
        }
      })
    },
    loadHistory() {
      try {
        const history = uni.getStorageSync(HISTORY_KEY)
        this.searchHistory = history ? JSON.parse(history) : []
      } catch (e) {
        this.searchHistory = []
      }
    },
    saveToHistory(user) {
      if (!user || !user.phone) return
      const history = this.searchHistory.filter((h) => h.phone !== user.phone)
      history.unshift({
        phone: user.phone,
        username: user.username || user.nickname || '',
        avatar: user.avatar || ''
      })
      this.searchHistory = history.slice(0, 10)
      uni.setStorageSync(HISTORY_KEY, JSON.stringify(this.searchHistory))
    },
    selectHistory(item) {
      this.keyword = item.phone
      this.searchUser()
    },
    clearHistory() {
      uni.showModal({
        title: '提示',
        content: '确定要清空搜索历史吗？',
        success: (res) => {
          if (res.confirm) {
            this.searchHistory = []
            uni.removeStorageSync(HISTORY_KEY)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.add-friend {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.add-friend__body {
  flex: 1;
  height: 0;
}

/* 搜索栏 */
.search-row {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  gap: 20rpx;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  height: 88rpx;
  background-color: #ffffff;
  border-radius: 10rpx;
  padding: 0 24rpx;
}

.search-box__input {
  flex: 1;
  font-size: 30rpx;
  color: #191919;
  margin-left: 16rpx;
}

.search-box__ph {
  color: #b2b2b2;
}

.search-box__clear {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background-color: #c8c8cd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-action {
  flex-shrink: 0;
  height: 88rpx;
  padding: 0 40rpx;
  background-color: #07c160;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-action--hover {
  background-color: #06ad56;
}

.search-action text {
  font-size: 30rpx;
  color: #ffffff;
  font-weight: 500;
}

/* 分组 */
.group {
  background-color: #ffffff;
  margin-top: 20rpx;
}

.group__title {
  font-size: 26rpx;
  color: #888888;
  padding: 20rpx 32rpx 12rpx;
}

.group__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx 12rpx;
}

.group__head .group__title {
  padding: 0;
}

.group__extra {
  font-size: 26rpx;
  color: #576b95;
}

.group__extra--hover {
  opacity: 0.6;
}

/* 搜索结果 */
.result {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx 32rpx;
}

.result__body {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
  display: flex;
  flex-direction: column;
}

.result__name {
  font-size: 32rpx;
  color: #191919;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result__sub {
  font-size: 26rpx;
  color: #888888;
  margin-top: 8rpx;
}

.op-btn {
  flex-shrink: 0;
  height: 64rpx;
  padding: 0 28rpx;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.op-btn text {
  font-size: 26rpx;
  font-weight: 500;
}

.op-btn.is-primary {
  background-color: #07c160;
}

.op-btn.is-primary text {
  color: #ffffff;
}

.op-btn--hover {
  background-color: #06ad56;
}

.op-btn.is-disabled {
  background-color: #f2f2f2;
}

.op-btn.is-disabled text {
  color: #b2b2b2;
}

/* 搜索历史 */
.history {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  position: relative;
}

.history::after {
  content: '';
  position: absolute;
  left: 128rpx;
  right: 0;
  bottom: 0;
  height: 1rpx;
  background-color: #ededed;
  transform: scaleY(0.5);
}

.history:last-child::after {
  display: none;
}

.history--hover {
  background-color: #ececec;
}

.history__body {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
  display: flex;
  flex-direction: column;
}

.history__name {
  font-size: 30rpx;
  color: #191919;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history__sub {
  font-size: 24rpx;
  color: #b2b2b2;
  margin-top: 6rpx;
}

/* 温馨提示 */
.tip {
  display: flex;
  align-items: center;
  padding: 18rpx 32rpx;
}

.tip:last-child {
  padding-bottom: 28rpx;
}

.tip__text {
  flex: 1;
  font-size: 26rpx;
  color: #888888;
  line-height: 1.5;
  margin-left: 16rpx;
}

.add-friend__safe {
  height: calc(24rpx + env(safe-area-inset-bottom));
}
</style>
