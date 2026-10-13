<template>
  <view class="contacts-page">
    <CustomNavBar
      title="通讯录"
      title-align="left"
      :show-add="true"
      :menu-items="menuItems"
      @menuItemClick="onMenuItem"
    />

    <!-- 搜索条 -->
    <view class="ct-searchbar">
      <view v-if="!searchVisible" class="ct-search ct-search--entry" @click="openSearch">
        <UiIcon name="search" :size="32" color="#B2B2B2" />
        <text class="ct-search__ph">搜索</text>
      </view>
      <view v-else class="ct-searchbar__row">
        <view class="ct-search">
          <UiIcon name="search" :size="32" color="#B2B2B2" />
          <input
            class="ct-search__input"
            v-model="keyword"
            focus
            placeholder="搜索"
            placeholder-class="ct-search__ph"
            confirm-type="search"
          />
          <view v-if="keyword" class="ct-search__clear" @click="keyword = ''">
            <UiIcon name="close" :size="22" color="#FFFFFF" />
          </view>
        </view>
        <text class="ct-searchbar__cancel" @click="closeSearch">取消</text>
      </view>
    </view>

    <view class="ct-body">
      <scroll-view
        class="ct-list"
        scroll-y
        :scroll-into-view="scrollIntoId"
        scroll-with-animation
        :show-scrollbar="false"
      >
        <!-- 功能入口 -->
        <view class="ct-block">
          <CellItem
            title="新的朋友"
            icon-bg="#FA9D3B"
            :arrow="false"
            @click="goFriendRequests"
          >
            <template #icon>
              <UiIcon name="user-plus" :size="44" color="#FFFFFF" />
            </template>
            <template #right>
              <view v-if="pendingCount > 0" class="ct-badge">
                <text>{{ pendingCount > 99 ? '99+' : pendingCount }}</text>
              </view>
            </template>
          </CellItem>

          <CellItem title="群聊" icon-bg="#07C160" :arrow="true" @click="openGroups">
            <template #icon>
              <UiIcon name="users" :size="44" color="#FFFFFF" />
            </template>
          </CellItem>

          <CellItem title="标签" icon-bg="#10AEFF" :arrow="true" @click="comingSoon('标签')">
            <template #icon>
              <UiIcon name="tag" :size="44" color="#FFFFFF" />
            </template>
          </CellItem>

          <CellItem title="公众号" icon-bg="#2782D7" :arrow="true" @click="comingSoon('公众号')">
            <template #icon>
              <UiIcon name="shield" :size="44" color="#FFFFFF" />
            </template>
          </CellItem>

          <CellItem
            title="服务号"
            icon-bg="#5B9BD5"
            :arrow="true"
            :border="false"
            @click="comingSoon('服务号')"
          >
            <template #icon>
              <UiIcon name="shield" :size="44" color="#FFFFFF" />
            </template>
          </CellItem>
        </view>

        <!-- 星标朋友 -->
        <view v-if="starFriends.length">
          <view class="ct-section">星标朋友</view>
          <view class="ct-block">
            <view
              v-for="friend in starFriends"
              :key="'star-' + friend.id"
              class="ct-row"
              hover-class="ct-row--hover"
              hover-stay-time="60"
              @click="goDetail(friend)"
              @longpress="unstar(friend)"
            >
              <UiAvatar shape="square" :size="80" :src="friend.avatar" :name="friend.name" />
              <text class="ct-row__name">{{ friend.name }}</text>
            </view>
          </view>
        </view>

        <!-- 字母分组 -->
        <view v-for="group in groups" :key="group.letter">
          <view class="ct-section" :id="'letter-' + group.letter">{{ group.letter }}</view>
          <view class="ct-block">
            <view
              v-for="friend in group.list"
              :key="friend.id"
              class="ct-row"
              hover-class="ct-row--hover"
              hover-stay-time="60"
              @click="goDetail(friend)"
            >
              <UiAvatar shape="square" :size="80" :src="friend.avatar" :name="friend.name" />
              <text class="ct-row__name">{{ friend.name }}</text>
            </view>
          </view>
        </view>

        <view v-if="!groups.length" class="ct-empty">
          <text>{{ keyword ? '没有找到相关联系人' : '还没有好友，去添加吧' }}</text>
        </view>

        <view class="ct-footer">{{ totalFriends }}位朋友</view>
        <view class="ct-list__safe"></view>
      </scroll-view>

      <!-- 右侧字母索引 -->
      <view
        v-if="totalFriends"
        class="letter-index"
        @touchstart.stop="onIndexTouchStart"
        @touchmove.stop.prevent="onIndexTouchMove"
        @touchend.stop="onIndexTouchEnd"
        @touchcancel.stop="onIndexTouchEnd"
      >
        <view
          v-for="letter in indexLetters"
          :key="letter"
          class="letter-index__item"
          :class="{
            'is-active': activeLetter === letter,
            'is-empty': !existingLetters.includes(letter)
          }"
          @click.stop="scrollToLetter(letter)"
        >
          <text>{{ letter }}</text>
        </view>
      </view>
    </view>

    <!-- 字母气泡 -->
    <view v-if="showBubble" class="letter-bubble">
      <text>{{ bubbleLetter }}</text>
    </view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import CellItem from '../../components/CellItem.vue'
import { getFriendInfoList, getPendingFriendRequests, getGroupsByUserId } from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'
import { groupContacts, INDEX_LETTERS } from '../../composables/useContactGroups.js'
import { measureRect, resolveLetterByY } from '../../composables/useLetterIndex.js'
import store, { isStarred, toggleStar, setPendingRequestCount, clearUnread } from '../../store/index.js'

export default {
  components: { CustomNavBar, UiIcon, UiAvatar, CellItem },
  data() {
    return {
      friends: [],
      keyword: '',
      searchVisible: false,
      scrollIntoId: '',
      activeLetter: '',
      bubbleLetter: '',
      showBubble: false,
      touching: false,
      indexRect: null,
      menuItems: [
        { text: '发起群聊', iconName: 'users', action: 'group' },
        { text: '添加朋友', iconName: 'user-plus', action: 'addFriend' }
      ]
    }
  },
  computed: {
    contactResult() {
      return groupContacts(this.friends, this.keyword)
    },
    groups() {
      return this.contactResult.groups
    },
    // 右侧展示完整字母表（微信风格），无数据字母置灰
    indexLetters() {
      return INDEX_LETTERS
    },
    // 实际存在分组的字母
    existingLetters() {
      return this.contactResult.letters
    },
    totalFriends() {
      return this.friends.length
    },
    pendingCount() {
      return store.pendingRequestCount
    },
    starFriends() {
      return this.friends.filter((f) => store.starredIds.map(String).includes(String(f.id)))
    }
  },
  onReady() {
    this.measureIndex()
  },
  onShow() {
    this.loadData()
    this.$nextTick(() => this.measureIndex())
  },
  methods: {
    async loadData() {
      const userId = getUserId()
      if (!userId) return
      try {
        const [friendsRes, requestsRes] = await Promise.all([
          getFriendInfoList(userId),
          getPendingFriendRequests(userId)
        ])
        if (friendsRes && friendsRes.code === 200 && Array.isArray(friendsRes.data)) {
          this.friends = friendsRes.data.map((f) => ({
            id: f.friendId,
            name: f.remark || f.username || '未知用户',
            avatar: f.avatar || '',
            online: !!f.online
          }))
        }
        const count =
          requestsRes && requestsRes.code === 200 && Array.isArray(requestsRes.data)
            ? requestsRes.data.length
            : 0
        setPendingRequestCount(count)
      } catch (e) {
        console.error('加载通讯录失败:', e)
      }
    },
    openSearch() {
      this.searchVisible = true
    },
    closeSearch() {
      this.searchVisible = false
      this.keyword = ''
    },
    onMenuItem(item) {
      if (item.action === 'group') {
        uni.navigateTo({ url: '/pages/create-group/create-group' })
      } else if (item.action === 'addFriend') {
        uni.navigateTo({ url: '/pages/add-friend/add-friend' })
      }
    },
    goFriendRequests() {
      uni.navigateTo({ url: '/pages/friend-requests/friend-requests' })
    },
    comingSoon(name) {
      uni.showToast({ title: `${name}功能开发中`, icon: 'none' })
    },
    async openGroups() {
      const userId = getUserId()
      if (!userId) return
      try {
        const res = await getGroupsByUserId(userId)
        const list = res && res.code === 200 && Array.isArray(res.data) ? res.data : []
        if (!list.length) {
          uni.showToast({ title: '还没有群聊', icon: 'none' })
          return
        }
        uni.showActionSheet({
          itemList: list.map((g) => g.name || '群聊'),
          success: (r) => {
            const group = list[r.tapIndex]
            if (!group) return
            clearUnread('group', group.id)
            uni.navigateTo({
              url: `/pages/chat/chat?type=group&id=${group.id}&name=${encodeURIComponent(
                group.name || '群聊'
              )}&avatar=${encodeURIComponent(group.avatar || '')}`
            })
          }
        })
      } catch (e) {
        console.error('加载群聊失败:', e)
        uni.showToast({ title: '加载群聊失败', icon: 'none' })
      }
    },
    goDetail(friend) {
      uni.navigateTo({
        url: `/pages/contact-detail/contact-detail?type=single&id=${friend.id}&name=${encodeURIComponent(
          friend.name
        )}&avatar=${encodeURIComponent(friend.avatar || '')}`
      })
    },
    unstar(friend) {
      uni.showActionSheet({
        itemList: ['取消星标'],
        success: () => {
          if (isStarred(friend.id)) toggleStar(friend.id)
          uni.showToast({ title: '已取消星标', icon: 'none' })
        }
      })
    },
    async measureIndex() {
      const rect = await measureRect(this, '.letter-index')
      this.indexRect = rect
    },
    // 命中不存在的分组时，滚动到其后最近的存在分组
    resolveTargetLetter(letter) {
      const existing = this.existingLetters
      if (existing.includes(letter)) return letter
      const idx = INDEX_LETTERS.indexOf(letter)
      for (let i = idx + 1; i < INDEX_LETTERS.length; i += 1) {
        if (existing.includes(INDEX_LETTERS[i])) return INDEX_LETTERS[i]
      }
      return existing.length ? existing[existing.length - 1] : ''
    },
    scrollToLetter(letter) {
      this.activeLetter = letter
      const target = this.resolveTargetLetter(letter)
      if (target) {
        this.scrollIntoId = ''
        this.$nextTick(() => {
          this.scrollIntoId = 'letter-' + target
        })
      }
      this.bubbleLetter = letter
      this.showBubble = true
      if (!this.touching) {
        setTimeout(() => {
          this.showBubble = false
        }, 600)
      }
    },
    onIndexTouchStart(e) {
      this.touching = true
      this.handleIndexTouch(e)
    },
    onIndexTouchMove(e) {
      if (!this.touching) return
      this.handleIndexTouch(e)
    },
    onIndexTouchEnd() {
      this.touching = false
      setTimeout(() => {
        this.showBubble = false
      }, 300)
    },
    handleIndexTouch(e) {
      const touch = e.touches && e.touches[0]
      if (!touch) return
      if (!this.indexRect) {
        this.measureIndex()
        return
      }
      const letter = resolveLetterByY(touch.clientY, this.indexRect, this.indexLetters)
      if (letter && letter !== this.activeLetter) {
        this.scrollToLetter(letter)
      }
    }
  }
}
</script>

<style scoped>
.contacts-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

/* 搜索条 */
.ct-searchbar {
  padding: 0 24rpx 16rpx;
}

.ct-searchbar__row {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.ct-search {
  flex: 1;
  display: flex;
  align-items: center;
  height: 64rpx;
  background-color: #ffffff;
  border-radius: 10rpx;
  padding: 0 20rpx;
}

.ct-search--entry {
  color: #b2b2b2;
}

.ct-search__ph {
  font-size: 28rpx;
  color: #b2b2b2;
  margin-left: 12rpx;
}

.ct-search__input {
  flex: 1;
  font-size: 28rpx;
  color: #191919;
  margin-left: 12rpx;
}

.ct-search__clear {
  width: 32rpx;
  height: 32rpx;
  border-radius: 50%;
  background-color: #c8c8cd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ct-searchbar__cancel {
  font-size: 28rpx;
  color: #576b95;
}

/* 内容区 */
.ct-body {
  flex: 1;
  height: 0;
  position: relative;
  display: flex;
}

.ct-list {
  flex: 1;
  height: 100%;
}

.ct-block {
  background-color: #ffffff;
}

.ct-section {
  height: 48rpx;
  line-height: 48rpx;
  padding-left: 32rpx;
  font-size: 24rpx;
  color: #888888;
  background-color: #ededed;
}

.ct-row {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  position: relative;
}

.ct-row::after {
  content: '';
  position: absolute;
  left: 148rpx;
  right: 0;
  bottom: 0;
  height: 1rpx;
  background-color: #ededed;
  transform: scaleY(0.5);
}

.ct-row:last-child::after {
  display: none;
}

.ct-row--hover {
  background-color: #ececec;
}

.ct-row__name {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
  font-size: 32rpx;
  color: #191919;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ct-badge {
  min-width: 34rpx;
  height: 34rpx;
  padding: 0 8rpx;
  background-color: #fa5151;
  border-radius: 17rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ct-badge text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 500;
}

.ct-empty {
  padding: 120rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: #b2b2b2;
}

.ct-footer {
  text-align: center;
  padding: 40rpx 0;
  font-size: 26rpx;
  color: #b2b2b2;
}

.ct-list__safe {
  height: env(safe-area-inset-bottom);
}

/* 右侧字母索引 */
.letter-index {
  width: 44rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0;
}

.letter-index__item {
  width: 44rpx;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.letter-index__item text {
  font-size: 20rpx;
  color: #7f7f7f;
  line-height: 1;
}

.letter-index__item.is-active text {
  color: #07c160;
  font-weight: 600;
}

/* 字母气泡 */
.letter-bubble {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120rpx;
  height: 120rpx;
  background-color: rgba(51, 51, 51, 0.85);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.letter-bubble text {
  font-size: 56rpx;
  color: #ffffff;
  font-weight: 600;
}
</style>
