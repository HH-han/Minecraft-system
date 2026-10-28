<template>
  <view class="add-friend" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('addFriend.title')"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="add-friend__body" scroll-y :show-scrollbar="false">
      <!-- 搜索输入卡 -->
      <view class="search-card surface-card grid-overlay">
        <view class="search-card__field">
          <UiIcon name="search" :size="34" color="var(--text-tertiary)" />
          <input
            class="search-card__input"
            v-model="keyword"
            type="text"
            :placeholder="$t('addFriend.searchPlaceholder')"
            placeholder-class="search-card__ph"
            confirm-type="search"
            @confirm="searchUser"
          />
          <view v-if="keyword" class="search-card__clear" @click="keyword = ''">
            <UiIcon name="close" :size="18" color="var(--bg-primary)" />
          </view>
        </view>
        <view
          class="search-card__btn"
          hover-class="search-card__btn--hover"
          hover-stay-time="60"
          @click="searchUser"
        >
          <UiIcon name="search" :size="28" color="var(--accent-contrast)" />
          <text>{{ $t('addFriend.search') }}</text>
        </view>
      </view>

      <!-- 搜索结果卡 -->
      <view v-if="searchResult" class="result-card surface-card grid-overlay">
        <text class="block-label">{{ $t('addFriend.searchResult') }}</text>
        <view class="result-card__body">
          <UiAvatar shape="square" :size="104" :src="searchResult.avatar" :name="resultName" />
          <view class="result-card__info">
            <text class="result-card__name">{{ resultName }}</text>
            <text v-if="resultSub" class="result-card__sub">{{ resultSub }}</text>
          </view>
        </view>
        <view v-if="isFriend" class="result-card__btn is-disabled">
          <UiIcon name="check" :size="28" color="var(--text-tertiary)" />
          <text>{{ $t('addFriend.alreadyFriend') }}</text>
        </view>
        <view v-else-if="isAdded" class="result-card__btn is-disabled">
          <UiIcon name="check" :size="28" color="var(--text-tertiary)" />
          <text>{{ $t('addFriend.alreadySent') }}</text>
        </view>
        <view
          v-else
          class="result-card__btn is-primary"
          hover-class="result-card__btn--hover"
          hover-stay-time="60"
          @click="addFriend"
        >
          <UiIcon name="user-plus" :size="30" color="var(--accent-contrast)" />
          <text>{{ $t('addFriend.addToContacts') }}</text>
        </view>
      </view>

      <!-- 搜索历史 -->
      <view v-if="searchHistory.length" class="group-block">
        <view class="history-head">
          <text class="block-label">{{ $t('addFriend.searchHistory') }}</text>
          <text
            class="history-head__clear"
            hover-class="history-head__clear--hover"
            hover-stay-time="60"
            @click="clearHistory"
          >{{ $t('addFriend.clear') }}</text>
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
          <UiIcon name="chevron-right" :size="26" color="var(--text-disabled)" />
        </view>
      </view>

      <!-- 我的账号卡 -->
      <view class="account-card surface-card grid-overlay">
        <text class="block-label">{{ $t('addFriend.myAccount') }}</text>
        <view class="account-card__body">
          <UiAvatar shape="square" :size="88" :src="myProfile.avatar" :name="myName" />
          <view class="account-card__info">
            <text class="account-card__name">{{ myName }}</text>
            <text class="account-card__sub">{{ accountText }}</text>
          </view>
          <view class="account-card__tile">
            <UiIcon name="qr" :size="34" color="var(--accent-color)" />
          </view>
        </view>
      </view>

      <!-- 温馨提示 -->
      <view class="group-block tips-block">
        <text class="block-label tips-block__label">{{ $t('addFriend.tipsTitle') }}</text>
        <view class="tip">
          <view class="tip__icon">
            <UiIcon name="info" :size="28" color="var(--accent-color)" />
          </view>
          <text class="tip__text">{{ $t('addFriend.tip1') }}</text>
        </view>
        <view class="tip">
          <view class="tip__icon">
            <UiIcon name="message" :size="28" color="var(--accent-color)" />
          </view>
          <text class="tip__text">{{ $t('addFriend.tip2') }}</text>
        </view>
      </view>

      <view class="safe-bottom-space--sm"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import { getUserByAccount, getFriendInfoList, sendFriendRequest } from '../../utils/chat-api.js'
import { getUserId, getCurrentProfile } from '../../utils/session.js'
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
      myProfile: { name: '', avatar: '', account: '' },
      // 已是好友判断依据（拿不到则为空集合，按 false 处理）
      friendIds: [],
      friendKeys: []
    }
  },
  computed: {
    resultName() {
      const r = this.searchResult || {}
      return r.nickname || r.username || r.account || this.$t('addFriend.unknownUser')
    },
    resultSub() {
      const r = this.searchResult || {}
      if (r.account && r.phone) {
        return this.$t('addFriend.accountWithPhone', {
          account: r.account,
          phone: maskPhone(r.phone)
        })
      }
      if (r.account) return this.$t('addFriend.accountOnly', { account: r.account })
      if (r.phone) return maskPhone(r.phone)
      return ''
    },
    myName() {
      return this.myProfile.name || this.$t('addFriend.unknownUser')
    },
    accountText() {
      if (!this.myProfile.account) return '-'
      return this.$t('addFriend.accountOnly', { account: this.myProfile.account })
    }
  },
  onLoad() {
    this.loadHistory()
    this.loadFriends()
    this.myProfile = getCurrentProfile()
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
        uni.showToast({ title: this.$t('addFriend.inputRequired'), icon: 'none' })
        return
      }
      uni.showLoading({ title: this.$t('addFriend.searching') })
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
          uni.showToast({ title: this.$t('addFriend.notFound'), icon: 'none' })
        }
      } catch (e) {
        console.error('搜索用户失败:', e)
        uni.showToast({ title: this.$t('addFriend.searchFailed'), icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    addFriend() {
      if (!this.searchResult || this.isAdded || this.isFriend) return
      const userId = getUserId()
      if (!userId) {
        uni.showToast({ title: this.$t('addFriend.loginRequired'), icon: 'none' })
        return
      }
      uni.showModal({
        title: this.$t('addFriend.sendRequestTitle'),
        editable: true,
        placeholderText: this.$t('addFriend.messagePlaceholder'),
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
              uni.showToast({ title: this.$t('addFriend.requestSent'), icon: 'success' })
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('addFriend.sendFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('发送好友请求失败:', e)
            uni.showToast({ title: this.$t('addFriend.sendFailed'), icon: 'none' })
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
        title: this.$t('addFriend.tip'),
        content: this.$t('addFriend.clearConfirm'),
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
  background-color: var(--bg-secondary);
  background-image: radial-gradient(120% 52% at 50% 0%, var(--accent-light) 0%, transparent 62%);
  color: var(--text-primary);
}

.add-friend__body {
  flex: 1;
  height: 0;
}

.block-label {
  display: block;
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 2rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

/* 搜索输入卡 */
.search-card {
  margin: 24rpx 24rpx 0;
  padding: 22rpx;
}

.search-card__field {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  height: 84rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  background-color: var(--bg-input);
  border: 1rpx solid var(--border-light);
}

.search-card__input {
  flex: 1;
  height: 100%;
  margin-left: 14rpx;
  font-size: var(--font-md);
  color: var(--text-primary);
}

.search-card__ph {
  color: var(--text-placeholder);
}

.search-card__clear {
  width: 34rpx;
  height: 34rpx;
  border-radius: 50%;
  background-color: var(--text-disabled);
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-card__btn {
  position: relative;
  z-index: 1;
  height: 84rpx;
  margin-top: 18rpx;
  border-radius: var(--radius-md);
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.search-card__btn text {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--accent-contrast);
}

.search-card__btn--hover {
  transform: scale(0.99);
  opacity: 0.9;
}

/* 搜索结果卡 */
.result-card {
  margin: 20rpx 24rpx 0;
  padding: 26rpx;
}

.result-card__body {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  margin-top: 20rpx;
}

.result-card__info {
  flex: 1;
  min-width: 0;
  margin-left: 22rpx;
  display: flex;
  flex-direction: column;
}

.result-card__name {
  font-size: var(--font-xl);
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.25;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-card__sub {
  margin-top: 8rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
  font-family: var(--font-mono);
}

.result-card__btn {
  position: relative;
  z-index: 1;
  height: 80rpx;
  margin-top: 24rpx;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.result-card__btn text {
  font-size: var(--font-base);
  font-weight: 600;
}

.result-card__btn.is-primary {
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
}

.result-card__btn.is-primary text {
  color: var(--accent-contrast);
}

.result-card__btn--hover {
  transform: scale(0.99);
  opacity: 0.9;
}

.result-card__btn.is-disabled {
  background-color: var(--bg-chip);
  border: 1rpx solid var(--border-light);
}

.result-card__btn.is-disabled text {
  color: var(--text-tertiary);
}

/* 搜索历史 */
.history-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 26rpx 32rpx 6rpx;
}

.history-head__clear {
  font-size: var(--font-sm);
  color: var(--accent-color);
  font-weight: 500;
}

.history-head__clear--hover {
  opacity: 0.6;
}

.history {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  position: relative;
  transition: background-color var(--transition-fast);
}

.history::after {
  content: '';
  position: absolute;
  left: 128rpx;
  right: 32rpx;
  bottom: 0;
  height: 1rpx;
  background-color: var(--divider);
}

.history:last-child::after {
  display: none;
}

.history--hover {
  background-color: var(--bg-hover);
}

.history__body {
  flex: 1;
  min-width: 0;
  margin-left: 22rpx;
  display: flex;
  flex-direction: column;
}

.history__name {
  font-size: var(--font-md);
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history__sub {
  margin-top: 4rpx;
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}

/* 我的账号卡 */
.account-card {
  margin: 20rpx 24rpx 0;
  padding: 26rpx;
}

.account-card__body {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  margin-top: 18rpx;
}

.account-card__info {
  flex: 1;
  min-width: 0;
  margin-left: 20rpx;
  display: flex;
  flex-direction: column;
}

.account-card__name {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-card__sub {
  margin-top: 6rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
  font-family: var(--font-mono);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-card__tile {
  width: 68rpx;
  height: 68rpx;
  flex-shrink: 0;
  border-radius: var(--radius-md);
  background-color: var(--accent-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 温馨提示 */
.tips-block__label {
  padding: 26rpx 32rpx 10rpx;
}

.tip {
  display: flex;
  align-items: center;
  padding: 12rpx 32rpx;
}

.tip:last-child {
  padding-bottom: 26rpx;
}

.tip__icon {
  width: 52rpx;
  height: 52rpx;
  flex-shrink: 0;
  border-radius: var(--radius-sm);
  background-color: var(--accent-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

.tip__text {
  flex: 1;
  margin-left: 18rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
  line-height: 1.5;
}
</style>
