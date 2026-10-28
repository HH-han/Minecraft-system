<template>
  <view class="conv-page" :class="themeClass">
    <CustomNavBar
      :title="$t('common.appName')"
      :subtitle="navSubtitle"
      :show-search="true"
      :show-add="true"
      :menu-items="menuItems"
      @search="toggleSearch"
      @menuItemClick="onMenuItem"
    />

    <view v-if="searchVisible" class="conv-search-wrap">
      <SearchBar
        v-model="keyword"
        show-cancel
        :autofocus="true"
        @cancel="closeSearch"
      />
    </view>

    <scroll-view class="conv-list" scroll-y :show-scrollbar="false">
      <view v-if="list.length" class="conv-summary">
        <view class="conv-summary__left">
          <view class="conv-summary__pulse"></view>
          <text>{{ $t('index.summary', { count: list.length }) }}</text>
        </view>
        <view v-if="unreadTotal" class="conv-summary__unread">
          <text>{{ $t('index.unread', { count: unreadTotal }) }}</text>
        </view>
      </view>

      <view v-if="topList.length" class="conv-group">
        <view class="conv-group__head">
          <UiIcon name="pin" :size="24" color="var(--accent-color)" />
          <text>{{ $t('index.pinned') }}</text>
        </view>
        <view
          v-for="conv in topList"
          :key="convKeyOf(conv)"
          class="conv-item"
          hover-class="conv-item--hover"
          hover-stay-time="60"
          @click="openChat(conv)"
          @longpress="onLongPress(conv)"
        >
          <UiAvatar
            shape="square"
            :src="conv.avatar"
            :name="conv.name"
            :size="96"
            :online="conv.type === 'single' && conv.online"
          />
          <view class="conv-item__body">
            <view class="conv-item__name-row">
              <text class="conv-item__name">{{ conv.name }}</text>
              <view v-if="conv.type === 'group'" class="conv-item__tag">
                <text>{{ $t('index.groupTag') }}</text>
              </view>
            </view>
            <text class="conv-item__preview">{{ conv.lastMessage || $t('index.noMessages') }}</text>
          </view>
          <view class="conv-item__meta">
            <text class="conv-item__time">{{ formatListTime(conv.lastTime) }}</text>
            <view v-if="conv.unread > 0" class="conv-item__badge">
              <text>{{ conv.unread > 99 ? '99+' : conv.unread }}</text>
            </view>
            <UiIcon v-else name="check" :size="22" color="var(--text-disabled)" />
          </view>
        </view>
      </view>

      <view v-if="normalList.length" class="conv-group">
        <view v-if="topList.length" class="conv-group__head">
          <UiIcon name="chat" :size="24" color="var(--text-tertiary)" />
          <text>{{ $t('index.allChats') }}</text>
        </view>
        <view
          v-for="conv in normalList"
          :key="convKeyOf(conv)"
          class="conv-item"
          hover-class="conv-item--hover"
          hover-stay-time="60"
          @click="openChat(conv)"
          @longpress="onLongPress(conv)"
        >
          <UiAvatar
            shape="square"
            :src="conv.avatar"
            :name="conv.name"
            :size="96"
            :online="conv.type === 'single' && conv.online"
          />
          <view class="conv-item__body">
            <view class="conv-item__name-row">
              <text class="conv-item__name">{{ conv.name }}</text>
              <view v-if="conv.type === 'group'" class="conv-item__tag">
                <text>{{ $t('index.groupTag') }}</text>
              </view>
            </view>
            <text class="conv-item__preview">{{ conv.lastMessage || $t('index.noMessages') }}</text>
          </view>
          <view class="conv-item__meta">
            <text class="conv-item__time">{{ formatListTime(conv.lastTime) }}</text>
            <view v-if="conv.unread > 0" class="conv-item__badge">
              <text>{{ conv.unread > 99 ? '99+' : conv.unread }}</text>
            </view>
            <UiIcon v-else name="check" :size="22" color="var(--text-disabled)" />
          </view>
        </view>
      </view>

      <UiEmpty
        v-if="!list.length"
        icon-name="chat"
        :text="$t('index.empty')"
        :action-text="$t('index.addFriend')"
        @action="goAddFriend"
      />
      <view class="conv-list__safe"></view>
    </scroll-view>

    <FloatingTabBar active="chat" />
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import UiEmpty from '../../components/EmptyState.vue'
import SearchBar from '../../components/SearchBar.vue'
import FloatingTabBar from '../../components/FloatingTabBar.vue'
import { getFriendInfoList, getGroupsByUserId } from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'
import { buildConversations, sortConversations } from '../../composables/useConversations.js'
import { formatListTime } from '../../utils/format.js'
import store, {
  convKey,
  clearUnread,
  toggleTop,
  removeConversation,
  isTop,
  syncBadge
} from '../../store/index.js'

export default {
  components: { CustomNavBar, UiIcon, UiAvatar, UiEmpty, SearchBar, FloatingTabBar },
  data() {
    return {
      friends: [],
      groups: [],
      keyword: '',
      searchVisible: false
    }
  },
  computed: {
    menuItems() {
      return [
        { text: this.$t('index.newGroup'), iconName: 'users', action: 'group' },
        { text: this.$t('index.addFriend'), iconName: 'user-plus', action: 'addFriend' },
        { text: this.$t('index.scan'), iconName: 'scan', action: 'scan' }
      ]
    },
    navSubtitle() {
      const pending = store.pendingRequestCount || 0
      return pending > 0 ? this.$t('index.pendingRequests', { count: pending }) : this.$t('common.appSlogan')
    },
    summaries() {
      return store.conversations
    },
    pendingCount() {
      return store.pendingRequestCount
    },
    list() {
      const merged = buildConversations(this.friends, this.groups, this.summaries)
      const kw = this.keyword.trim().toLowerCase()
      const filtered = kw
        ? merged.filter((c) => String(c.name).toLowerCase().includes(kw))
        : merged
      return sortConversations(filtered)
    },
    topList() {
      return this.list.filter((c) => c.isTop)
    },
    normalList() {
      return this.list.filter((c) => !c.isTop)
    },
    unreadTotal() {
      return this.list.reduce((sum, conv) => sum + (conv.unread || 0), 0)
    }
  },
  onShow() {
    this.loadData()
    syncBadge()
  },
  methods: {
    formatListTime,
    convKeyOf(conv) {
      return convKey(conv.type, conv.id)
    },
    async loadData() {
      const userId = getUserId()
      if (!userId) return
      try {
        const [friendsRes, groupsRes] = await Promise.all([
          getFriendInfoList(userId),
          getGroupsByUserId(userId)
        ])
        if (friendsRes && friendsRes.code === 200 && Array.isArray(friendsRes.data)) {
          this.friends = friendsRes.data.map((f) => ({
            id: f.friendId,
            name: f.remark || f.username || this.$t('common.unknownUser'),
            avatar: f.avatar || '',
            online: !!f.online
          }))
        }
        if (groupsRes && groupsRes.code === 200 && Array.isArray(groupsRes.data)) {
          this.groups = groupsRes.data.map((g) => ({
            id: g.id,
            name: g.name || this.$t('common.groupChat'),
            avatar: g.avatar || '',
            memberCount: g.memberCount || 0
          }))
        }
      } catch (e) {
        console.error('加载会话列表失败:', e)
      }
    },
    toggleSearch() {
      this.searchVisible = !this.searchVisible
      if (!this.searchVisible) this.keyword = ''
    },
    closeSearch() {
      this.searchVisible = false
      this.keyword = ''
    },
    openChat(conv) {
      clearUnread(conv.type, conv.id)
      uni.navigateTo({
        url: `/pages/chat/chat?type=${conv.type}&id=${conv.id}&name=${encodeURIComponent(conv.name)}&avatar=${encodeURIComponent(conv.avatar || '')}`
      })
    },
    onLongPress(conv) {
      const pinned = isTop(conv.type, conv.id)
      const items = [
        pinned ? this.$t('index.unpin') : this.$t('index.pin'),
        this.$t('index.deleteChat')
      ]
      uni.showActionSheet({
        itemList: items,
        success: (res) => {
          if (res.tapIndex === 0) {
            toggleTop(conv.type, conv.id)
          } else if (res.tapIndex === 1) {
            uni.showModal({
              title: this.$t('common.tip'),
              content: this.$t('index.deleteConfirm'),
              success: (r) => {
                if (r.confirm) removeConversation(conv.type, conv.id)
              }
            })
          }
        }
      })
    },
    onMenuItem(item) {
      if (item.action === 'group') {
        uni.navigateTo({ url: '/pages/create-group/create-group' })
      } else if (item.action === 'addFriend') {
        this.goAddFriend()
      } else if (item.action === 'scan') {
        uni.showToast({ title: this.$t('index.scanDev'), icon: 'none' })
      }
    },
    goAddFriend() {
      uni.navigateTo({ url: '/pages/add-friend/add-friend' })
    }
  }
}
</script>

<style scoped>
.conv-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.conv-search-wrap {
  padding: 8rpx 24rpx 16rpx;
}

.conv-list {
  flex: 1;
  height: 0;
}

.conv-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 40rpx 4rpx;
}

.conv-summary__left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.conv-summary__left text {
  font-size: 22rpx;
  color: var(--text-tertiary);
  letter-spacing: 0.6rpx;
}

.conv-summary__pulse {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background-color: var(--accent-color);
  box-shadow: 0 0 0 6rpx var(--accent-light);
}

.conv-summary__unread {
  height: 38rpx;
  padding: 0 16rpx;
  display: flex;
  align-items: center;
  border-radius: 999rpx;
  background-color: var(--accent-light);
}

.conv-summary__unread text {
  font-size: 20rpx;
  color: var(--accent-color);
  font-weight: 600;
}

.conv-group {
  margin: 16rpx 24rpx 0;
  border-radius: var(--radius-lg);
  background-color: var(--bg-primary);
  border: 1rpx solid var(--border-light);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.conv-group__head {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 22rpx 28rpx 10rpx;
}

.conv-group__head text {
  font-size: 22rpx;
  font-weight: 600;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

.conv-item {
  display: flex;
  align-items: center;
  padding: 24rpx 28rpx;
  position: relative;
  transition: background-color var(--transition-fast);
}

.conv-item::after {
  content: '';
  position: absolute;
  left: 148rpx;
  right: 28rpx;
  bottom: 0;
  height: 1rpx;
  background-color: var(--divider);
}

.conv-item:last-child::after {
  display: none;
}

.conv-item--hover {
  background-color: var(--bg-hover);
}

.conv-item__body {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
}

.conv-item__name-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.conv-item__name {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-item__tag {
  height: 30rpx;
  padding: 0 10rpx;
  display: flex;
  align-items: center;
  border-radius: 8rpx;
  background-color: var(--accent-light);
  flex-shrink: 0;
}

.conv-item__tag text {
  font-size: 18rpx;
  color: var(--accent-color);
  font-weight: 600;
}

.conv-item__preview {
  display: block;
  font-size: 24rpx;
  color: var(--text-secondary);
  margin-top: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-item__meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 14rpx;
  margin-left: 16rpx;
  min-height: 96rpx;
  flex-shrink: 0;
}

.conv-item__time {
  font-size: 20rpx;
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}

.conv-item__badge {
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 10rpx;
  background-image: linear-gradient(135deg, #ff7a7a 0%, #f04444 100%);
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 16rpx rgba(240, 68, 68, 0.32);
}

.conv-item__badge text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 600;
}

.conv-list__safe {
  height: calc(160rpx + env(safe-area-inset-bottom));
}
</style>
