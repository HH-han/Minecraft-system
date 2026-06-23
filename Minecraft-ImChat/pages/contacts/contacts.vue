<template>
  <view class="contacts-page">
    <CustomNavBar title="通讯录" :fixed="false" @search="onSearch" @add="onAdd" />

    <!-- 主内容区 -->
    <view class="content-wrapper">
      <scroll-view
        class="contact-scroll"
        scroll-y
        :scroll-into-view="scrollToId"
        scroll-with-animation
        @scroll="onScroll"
      >

        <!-- 功能入口 -->
        <view class="func-list">
          <view
            class="func-item"
            v-for="(item, index) in funcEntries"
            :key="index"
            @click="handleFuncClick(item.action)"
          >
            <view class="func-icon-wrapper" :style="{ backgroundColor: item.color }">
              <text class="func-icon-text">{{ item.icon }}</text>
            </view>
            <text class="func-name">{{ item.name }}</text>
            <view v-if="item.action === 'friendRequests' && requestCount > 0" class="func-badge">
              <text class="func-badge-text">{{ requestCount }}</text>
            </view>
          </view>
        </view>

        <!-- 企业联系人 -->
        <view class="section-header">我的企业及企业联系人</view>
        <view class="enterprise-list">
          <view
            class="enterprise-item"
            v-for="(item, index) in enterpriseList"
            :key="index"
            @click="handleEnterpriseClick(item.action)"
          >
            <view class="enterprise-icon-wrapper" :style="{ backgroundColor: item.color }">
              <text class="enterprise-icon-text">{{ item.icon }}</text>
            </view>
            <text class="enterprise-name">{{ item.name }}</text>
          </view>
        </view>

        <!-- 星标朋友 -->
        <view v-if="starFriends.length > 0">
          <view class="section-header" id="letter-STAR">星标朋友</view>
          <view class="contact-group">
            <view
              class="contact-row"
              v-for="friend in starFriends"
              :key="friend.id"
              @click="goToContact(friend)"
            >
              <image
                class="contact-avatar"
                :src="friend.avatar || defaultAvatar"
                mode="aspectFill"
              />
              <view class="contact-info">
                <text class="contact-name">{{ friend.name }}</text>
                <text v-if="friend.desc" class="contact-desc">{{ friend.desc }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 按字母分组的好友 -->
        <view v-for="group in sortedFriends" :key="group.letter">
          <view class="section-header" :id="'letter-' + group.letter">{{ group.letter }}</view>
          <view class="contact-group">
            <view
              class="contact-row"
              v-for="friend in group.list"
              :key="friend.id"
              @click="goToContact(friend)"
            >
              <image
                class="contact-avatar"
                :src="friend.avatar || defaultAvatar"
                mode="aspectFill"
              />
              <view class="contact-info">
                <text class="contact-name">{{ friend.name }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 底部好友数量 -->
        <view class="footer-count">{{ totalFriends }}位朋友</view>
      </scroll-view>

      <!-- 右侧字母索引 -->
      <view
        class="letter-index"
        @touchstart.stop="onIndexTouchStart"
        @touchmove.stop.prevent="onIndexTouchMove"
        @touchend.stop="onIndexTouchEnd"
      >
        <view
          class="letter-item"
          v-for="letter in indexLetters"
          :key="letter"
          :class="{ active: currentLetter === letter }"
          @click.stop="scrollToLetter(letter)"
        >
          <text class="letter-text">{{ letter }}</text>
        </view>
      </view>
    </view>

    <!-- 字母提示 -->
    <view class="letter-toast" v-if="showLetterToast">
      <text class="letter-toast-text">{{ currentLetter }}</text>
    </view>
  </view>
</template>

<script>
import { getFirstLetter } from '../../utils/pinyin.js'
import { getFriendInfoList, getPendingFriendRequests } from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'
import CustomNavBar from '../../components/CustomNavBar.vue'

export default {
  components: {
    CustomNavBar
  },
  data() {
    return {
      userInfo: null,
      searchKeyword: '',
      friends: [],
      requestCount: 0,
      starIds: [],
      defaultAvatar: '/static/default-avatar.png',
      scrollToId: '',
      currentLetter: '',
      showLetterToast: false,
      touching: false,
      indexRect: null,
      indexItemHeight: 0,
      indexLetters: ['↑', '☆', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#'],
      funcEntries: [
        { name: '新的朋友', icon: '新', color: '#FA9D3B', action: 'friendRequests' },
        { name: '仅聊天的朋友', icon: '聊', color: '#FA9D3B', action: 'chatOnlyFriends' },
        { name: '群聊', icon: '群', color: '#07C160', action: 'groupChat' },
        { name: '标签', icon: '标', color: '#10AEFF', action: 'tags' },
        { name: '公众号', icon: '公', color: '#2782D7', action: 'officialAccounts' },
        { name: '服务号', icon: '服', color: '#5B9BD5', action: 'serviceAccounts' }
      ],
      enterpriseList: [
        { name: '企业微信联系人', icon: '企', color: '#4A90E2', action: 'enterpriseContact' },
        { name: '路尚', icon: '路', color: '#5B9BD5', action: 'enterpriseDetail' }
      ]
    }
  },

  computed: {
    sortedFriends() {
      const groups = {}
      this.friends.forEach(friend => {
        const letter = getFirstLetter(friend.name)
        if (!groups[letter]) {
          groups[letter] = []
        }
        groups[letter].push(friend)
      })

      const letters = Object.keys(groups).sort((a, b) => {
        if (a === '#') return 1
        if (b === '#') return -1
        return a.localeCompare(b)
      })

      return letters.map(letter => ({
        letter,
        list: groups[letter].sort((a, b) => a.name.localeCompare(b.name, 'zh-CN'))
      }))
    },

    starFriends() {
      return this.friends.filter(f => this.starIds.includes(f.id)).map(f => ({
        ...f,
        desc: f.remark ? `生日:${f.remark}` : ''
      }))
    },

    totalFriends() {
      return this.friends.length
    }
  },

  onLoad() {
    this.loadUserInfo()
    this.calcIndexLayout()
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
            remark: f.remark || '',
            username: f.username || ''
          }))
          this.loadStarContacts()
        }
      } catch (e) {
        console.error('加载好友列表失败:', e)
        uni.showToast({ title: '加载好友失败', icon: 'none' })
      }
    },

    loadStarContacts() {
      let starIds = uni.getStorageSync('starContactIds') || []
      // 首次无星标时，自动将前3个好友设为星标用于展示
      if (starIds.length === 0 && this.friends.length > 0) {
        starIds = this.friends.slice(0, Math.min(3, this.friends.length)).map(f => f.id)
        uni.setStorageSync('starContactIds', starIds)
      }
      this.starIds = starIds
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

    calcIndexLayout() {
      setTimeout(() => {
        const query = uni.createSelectorQuery().in(this)
        query.select('.letter-index').boundingClientRect(rect => {
          if (rect && rect.height > 0) {
            this.indexRect = rect
            this.indexItemHeight = rect.height / this.indexLetters.length
          }
        }).exec()
      }, 500)
    },

    scrollToLetter(letter) {
      if (letter === '↑') {
        this.scrollToId = 'letter-TOP'
      } else if (letter === '☆') {
        this.scrollToId = 'letter-STAR'
      } else {
        this.scrollToId = 'letter-' + letter
      }
      this.currentLetter = letter
      this.showLetterToast = true
      setTimeout(() => {
        if (!this.touching) {
          this.showLetterToast = false
        }
      }, 800)
    },

    onIndexTouchStart(e) {
      this.touching = true
      this.updateLetterFromTouch(e)
    },

    onIndexTouchMove(e) {
      if (!this.touching) return
      this.updateLetterFromTouch(e)
    },

    onIndexTouchEnd() {
      this.touching = false
      setTimeout(() => {
        this.showLetterToast = false
      }, 300)
    },

    updateLetterFromTouch(e) {
      if (!this.indexRect || !this.indexItemHeight) {
        this.calcIndexLayout()
        return
      }
      const touch = e.touches[0]
      const relativeY = touch.clientY - this.indexRect.top
      let index = Math.floor(relativeY / this.indexItemHeight)
      index = Math.max(0, Math.min(index, this.indexLetters.length - 1))
      const letter = this.indexLetters[index]
      if (this.currentLetter !== letter) {
        this.scrollToLetter(letter)
      }
    },

    onScroll() {
      // 可以在这里实现滚动时高亮对应字母
    },

    onSearch() {
      uni.showToast({ title: '搜索功能开发中', icon: 'none' })
    },

    onAdd() {
      uni.navigateTo({ url: '/pages/add-friend/add-friend' })
    },

    handleFuncClick(action) {
      const map = {
        friendRequests: () => uni.navigateTo({ url: '/pages/friend-requests/friend-requests' }),
        chatOnlyFriends: () => uni.showToast({ title: '仅聊天的朋友', icon: 'none' }),
        groupChat: () => uni.navigateTo({ url: '/pages/create-group/create-group' }),
        tags: () => uni.showToast({ title: '标签功能开发中', icon: 'none' }),
        officialAccounts: () => uni.showToast({ title: '公众号功能开发中', icon: 'none' }),
        serviceAccounts: () => uni.showToast({ title: '服务号功能开发中', icon: 'none' })
      }
      if (map[action]) map[action]()
    },

    handleEnterpriseClick(action) {
      const map = {
        enterpriseContact: () => uni.showToast({ title: '企业微信联系人', icon: 'none' }),
        enterpriseDetail: () => uni.showToast({ title: '路尚企业详情', icon: 'none' })
      }
      if (map[action]) map[action]()
    },

    goToContact(friend) {
      uni.navigateTo({
        url: `/pages/contact-detail/contact-detail?type=single&id=${friend.id}&name=${encodeURIComponent(friend.name)}&avatar=${encodeURIComponent(friend.avatar || '')}`
      })
    }
  }
}
</script>

<style scoped>
.contacts-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #EDEDED;
}

/* 内容区 */
.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: row;
  overflow: hidden;
  position: relative;
}

.contact-scroll {
  flex: 1;
  height: 100%;
}

/* 搜索入口 */
.search-entry {
  padding: 16rpx 24rpx;
  background-color: #EDEDED;
}

.search-box {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #FFFFFF;
  border-radius: 8rpx;
  padding: 16rpx 0;
  gap: 8rpx;
}

.search-box-icon {
  font-size: 28rpx;
  color: #B2B2B2;
}

.search-placeholder {
  font-size: 28rpx;
  color: #B2B2B2;
}

/* 功能入口 */
.func-list {
  background-color: #FFFFFF;
  margin-bottom: 16rpx;
}

.func-item {
  display: flex;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.func-item:last-child {
  border-bottom: none;
}

.func-item:active {
  background-color: #E5E5E5;
}

.func-icon-wrapper {
  width: 72rpx;
  height: 72rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.func-icon-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.func-name {
  font-size: 32rpx;
  color: #000000;
  flex: 1;
}

.func-badge {
  min-width: 36rpx;
  height: 36rpx;
  background-color: #FF3B30;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 10rpx;
  margin-left: 16rpx;
}

.func-badge-text {
  font-size: 24rpx;
  color: #FFFFFF;
  font-weight: 600;
}

/* 企业联系人 */
.enterprise-list {
  background-color: #FFFFFF;
  margin-bottom: 16rpx;
}

.enterprise-item {
  display: flex;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.enterprise-item:last-child {
  border-bottom: none;
}

.enterprise-item:active {
  background-color: #E5E5E5;
}

.enterprise-icon-wrapper {
  width: 72rpx;
  height: 72rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.enterprise-icon-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.enterprise-name {
  font-size: 32rpx;
  color: #000000;
}

/* 分隔标题 */
.section-header {
  background-color: #EDEDED;
  padding: 12rpx 30rpx;
  font-size: 24rpx;
  color: #7F7F7F;
}

/* 联系人组 */
.contact-group {
  background-color: #FFFFFF;
  margin-bottom: 16rpx;
}

.contact-row {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.contact-row:last-child {
  border-bottom: none;
}

.contact-row:active {
  background-color: #E5E5E5;
}

.contact-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 8rpx;
  background-color: #E5E5E5;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.contact-info {
  flex: 1;
  overflow: hidden;
}

.contact-name {
  font-size: 32rpx;
  color: #000000;
  line-height: 1.4;
}

.contact-desc {
  font-size: 26rpx;
  color: #7F7F7F;
  margin-top: 4rpx;
  display: block;
}

/* 底部计数 */
.footer-count {
  text-align: center;
  padding: 40rpx 0;
  font-size: 26rpx;
  color: #B2B2B2;
}

/* 右侧字母索引 */
.letter-index {
  width: 44rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 10;
}

.letter-item {
  width: 44rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.letter-text {
  font-size: 20rpx;
  color: #7F7F7F;
  line-height: 1;
}

.letter-item.active .letter-text {
  color: #07C160;
  font-weight: 600;
}

/* 字母提示 */
.letter-toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120rpx;
  height: 120rpx;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.letter-toast-text {
  font-size: 56rpx;
  color: #FFFFFF;
  font-weight: 600;
}
</style>
