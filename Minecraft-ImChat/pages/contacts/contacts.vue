<template>
  <view class="contacts-page" :class="themeClass">
    <CustomNavBar
      :title="$t('contacts.title')"
      title-align="left"
      :show-add="true"
      :menu-items="menuItems"
      @menuItemClick="onMenuItem"
    />

    <!-- 搜索条 -->
    <view class="ct-searchbar">
      <SearchBar
        v-model="keyword"
        :placeholder="$t('contacts.searchPlaceholder')"
        :autofocus="searchVisible"
        :show-cancel="searchVisible"
        @focus="openSearch"
        @cancel="closeSearch"
      />
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
        <view class="section-label">{{ $t('contacts.shortcuts') }}</view>
        <view class="group-block">
          <CellItem :title="$t('contacts.newFriends')" :arrow="false" @click="goFriendRequests">
            <template #icon>
              <UiIcon name="user-plus" :size="40" color="#FFFFFF" />
            </template>
            <template #right>
              <view v-if="pendingCount > 0" class="ct-badge">
                <text>{{ pendingCount > 99 ? '99+' : pendingCount }}</text>
              </view>
              <UiIcon name="chevron-right" :size="26" color="var(--text-disabled)" class="ct-entry__arrow" />
            </template>
          </CellItem>

          <CellItem :title="$t('contacts.groups')" :arrow="true" @click="openGroups">
            <template #icon>
              <UiIcon name="users" :size="40" color="#FFFFFF" />
            </template>
          </CellItem>

          <CellItem :title="$t('contacts.tags')" :arrow="true" @click="comingSoon($t('contacts.tags'))">
            <template #icon>
              <UiIcon name="tag" :size="40" color="#FFFFFF" />
            </template>
          </CellItem>

          <CellItem :title="$t('contacts.officialAccounts')" :arrow="true" @click="comingSoon($t('contacts.officialAccounts'))">
            <template #icon>
              <UiIcon name="shield" :size="40" color="#FFFFFF" />
            </template>
          </CellItem>

          <CellItem
            :title="$t('contacts.serviceAccounts')"
            :arrow="true"
            :border="false"
            @click="comingSoon($t('contacts.serviceAccounts'))"
          >
            <template #icon>
              <UiIcon name="shield" :size="40" color="#FFFFFF" />
            </template>
          </CellItem>
        </view>

        <!-- 星标朋友 -->
        <view v-if="starFriends.length">
          <view class="section-label">{{ $t('contacts.starred') }}</view>
          <view class="group-block">
            <view
              v-for="friend in starFriends"
              :key="'star-' + friend.id"
              class="row-item"
              hover-class="row-item--hover"
              hover-stay-time="60"
              @click="goDetail(friend)"
              @longpress="unstar(friend)"
            >
              <UiAvatar
                shape="square"
                :size="80"
                :src="friend.avatar"
                :name="friend.name"
                :online="friend.online"
              />
              <view class="row-item__body">
                <text class="row-item__title">{{ friend.name }}</text>
              </view>
              <UiIcon name="star-filled" :size="26" color="var(--warning-color)" />
            </view>
          </view>
        </view>

        <!-- 字母分组 -->
        <view v-for="group in groups" :key="group.letter" class="ct-letter-group">
          <view class="section-label ct-letter" :id="'letter-' + group.letter">
            {{ group.letter }}
          </view>
          <view class="group-block">
            <view
              v-for="friend in group.list"
              :key="friend.id"
              class="row-item"
              hover-class="row-item--hover"
              hover-stay-time="60"
              @click="goDetail(friend)"
            >
              <UiAvatar
                shape="square"
                :size="80"
                :src="friend.avatar"
                :name="friend.name"
                :online="friend.online"
              />
              <view class="row-item__body">
                <text class="row-item__title">{{ friend.name }}</text>
              </view>
            </view>
          </view>
        </view>

        <UiEmpty
          v-if="!groups.length"
          icon-name="contacts"
          :text="keyword ? $t('contacts.emptySearch') : $t('contacts.empty')"
        />

        <view v-if="groups.length" class="ct-footer">
          {{ $t('common.friendsCount', { count: totalFriends }) }}
        </view>
        <view class="safe-bottom-space"></view>
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
    <view v-if="showBubble" class="letter-bubble glass-panel">
      <text>{{ bubbleLetter }}</text>
    </view>

    <FloatingTabBar active="contacts" />
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import CellItem from '../../components/CellItem.vue'
import UiEmpty from '../../components/EmptyState.vue'
import SearchBar from '../../components/SearchBar.vue'
import FloatingTabBar from '../../components/FloatingTabBar.vue'
import { getFriendInfoList, getPendingFriendRequests, getGroupsByUserId } from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'
import { groupContacts, INDEX_LETTERS } from '../../composables/useContactGroups.js'
import { measureRect, resolveLetterByY } from '../../composables/useLetterIndex.js'
import store, { isStarred, toggleStar, setPendingRequestCount, clearUnread } from '../../store/index.js'

export default {
  components: { CustomNavBar, UiIcon, UiAvatar, CellItem, UiEmpty, SearchBar, FloatingTabBar },
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
      indexRect: null
    }
  },
  computed: {
    menuItems() {
      return [
        { text: this.$t('contacts.newGroup'), iconName: 'users', action: 'group' },
        { text: this.$t('contacts.addFriend'), iconName: 'user-plus', action: 'addFriend' }
      ]
    },
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
            name: f.remark || f.username || this.$t('common.unknownUser'),
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
      uni.showToast({ title: this.$t('contacts.devToast', { name }), icon: 'none' })
    },
    async openGroups() {
      const userId = getUserId()
      if (!userId) return
      try {
        const res = await getGroupsByUserId(userId)
        const list = res && res.code === 200 && Array.isArray(res.data) ? res.data : []
        if (!list.length) {
          uni.showToast({ title: this.$t('contacts.noGroups'), icon: 'none' })
          return
        }
        uni.showActionSheet({
          itemList: list.map((g) => g.name || this.$t('common.groupChat')),
          success: (r) => {
            const group = list[r.tapIndex]
            if (!group) return
            clearUnread('group', group.id)
            uni.navigateTo({
              url: `/pages/chat/chat?type=group&id=${group.id}&name=${encodeURIComponent(
                group.name || this.$t('common.groupChat')
              )}&avatar=${encodeURIComponent(group.avatar || '')}`
            })
          }
        })
      } catch (e) {
        console.error('加载群聊失败:', e)
        uni.showToast({ title: this.$t('contacts.loadGroupsFailed'), icon: 'none' })
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
        itemList: [this.$t('contacts.unstar')],
        success: () => {
          if (isStarred(friend.id)) toggleStar(friend.id)
          uni.showToast({ title: this.$t('contacts.unstarred'), icon: 'none' })
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
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

/* 搜索条 */
.ct-searchbar {
  padding: 12rpx 24rpx 4rpx;
}

.ct-entry__arrow {
  margin-left: 10rpx;
}

.ct-badge {
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 10rpx;
  box-sizing: border-box;
  border-radius: 999rpx;
  background-color: var(--danger-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.ct-badge text {
  font-size: var(--font-xs);
  font-weight: 600;
  color: var(--bg-primary);
  line-height: 1;
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

.ct-letter {
  padding-top: 24rpx;
  padding-bottom: 8rpx;
  letter-spacing: 4rpx;
}

.ct-footer {
  text-align: center;
  padding: 36rpx 0 8rpx;
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  letter-spacing: 1rpx;
}

/* 右侧字母索引 */
.letter-index {
  width: 52rpx;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0;
  margin-bottom: 160rpx;
}

.letter-index__item {
  width: 44rpx;
  flex: 1;
  margin: 1rpx 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  transition: background-color var(--transition-fast);
}

.letter-index__item text {
  font-size: 18rpx;
  color: var(--text-tertiary);
  line-height: 1;
}

.letter-index__item.is-empty text {
  color: var(--text-disabled);
}

.letter-index__item.is-active {
  background-color: var(--accent-light);
}

.letter-index__item.is-active text {
  color: var(--accent-color);
  font-weight: 700;
}

/* 字母气泡 */
.letter-bubble {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 130rpx;
  height: 130rpx;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.letter-bubble text {
  font-size: var(--font-display);
  font-weight: 700;
  color: var(--accent-color);
}
</style>
