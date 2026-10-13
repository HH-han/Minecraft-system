<template>
  <view class="conv-page">
    <CustomNavBar
      title="微信"
      :show-search="true"
      :show-add="true"
      :menu-items="menuItems"
      @search="toggleSearch"
      @menuItemClick="onMenuItem"
    />

    <view v-if="searchVisible" class="conv-page__searchbar">
      <view class="conv-search">
        <UiIcon name="search" :size="32" color="#B2B2B2" />
        <input
          class="conv-search__input"
          v-model="keyword"
          placeholder="搜索"
          placeholder-class="conv-search__ph"
          confirm-type="search"
        />
        <view v-if="keyword" class="conv-search__clear" @click="keyword = ''">
          <UiIcon name="close" :size="22" color="#FFFFFF" />
        </view>
      </view>
      <text class="conv-page__searchbar-cancel" @click="closeSearch">取消</text>
    </view>

    <scroll-view class="conv-list" scroll-y :show-scrollbar="false">
      <view v-if="topList.length" class="conv-group">
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
            <text class="conv-item__name">{{ conv.name }}</text>
            <text class="conv-item__preview">{{ conv.lastMessage || '暂无消息' }}</text>
          </view>
          <view class="conv-item__meta">
            <text class="conv-item__time">{{ formatListTime(conv.lastTime) }}</text>
            <view v-if="conv.unread > 0" class="conv-item__badge">
              <text>{{ conv.unread > 99 ? '99+' : conv.unread }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="topList.length && normalList.length" class="conv-divider"></view>

      <view class="conv-group">
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
            <text class="conv-item__name">{{ conv.name }}</text>
            <text class="conv-item__preview">{{ conv.lastMessage || '暂无消息' }}</text>
          </view>
          <view class="conv-item__meta">
            <text class="conv-item__time">{{ formatListTime(conv.lastTime) }}</text>
            <view v-if="conv.unread > 0" class="conv-item__badge">
              <text>{{ conv.unread > 99 ? '99+' : conv.unread }}</text>
            </view>
          </view>
        </view>
      </view>

      <UiEmpty
        v-if="!list.length"
        icon="💬"
        text="还没有聊天，去添加好友吧"
        action-text="添加朋友"
        @action="goAddFriend"
      />
      <view class="conv-list__safe"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import UiEmpty from '../../components/EmptyState.vue'
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
  components: { CustomNavBar, UiIcon, UiAvatar, UiEmpty },
  data() {
    return {
      friends: [],
      groups: [],
      keyword: '',
      searchVisible: false,
      menuItems: [
        { text: '发起群聊', iconName: 'users', action: 'group' },
        { text: '添加朋友', iconName: 'user-plus', action: 'addFriend' },
        { text: '扫一扫', iconName: 'scan', action: 'scan' }
      ]
    }
  },
  computed: {
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
            name: f.remark || f.username || '未知用户',
            avatar: f.avatar || '',
            online: !!f.online
          }))
        }
        if (groupsRes && groupsRes.code === 200 && Array.isArray(groupsRes.data)) {
          this.groups = groupsRes.data.map((g) => ({
            id: g.id,
            name: g.name || '群聊',
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
      const items = [pinned ? '取消置顶' : '置顶该聊天', '删除该聊天']
      uni.showActionSheet({
        itemList: items,
        success: (res) => {
          if (res.tapIndex === 0) {
            toggleTop(conv.type, conv.id)
          } else if (res.tapIndex === 1) {
            uni.showModal({
              title: '提示',
              content: '删除后该聊天的消息记录将被清空',
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
        uni.showToast({ title: '扫一扫开发中', icon: 'none' })
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
  background-color: #ededed;
}

.conv-page__searchbar {
  display: flex;
  align-items: center;
  padding: 0 24rpx 16rpx;
  gap: 20rpx;
}

.conv-search {
  flex: 1;
  display: flex;
  align-items: center;
  height: 64rpx;
  background-color: #ffffff;
  border-radius: 10rpx;
  padding: 0 20rpx;
}

.conv-search__input {
  flex: 1;
  font-size: 28rpx;
  color: #191919;
  margin-left: 12rpx;
}

.conv-search__ph {
  color: #b2b2b2;
}

.conv-search__clear {
  width: 32rpx;
  height: 32rpx;
  border-radius: 50%;
  background-color: #c8c8cd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.conv-page__searchbar-cancel {
  font-size: 28rpx;
  color: #576b95;
}

.conv-list {
  flex: 1;
  height: 0;
}

.conv-group {
  background-color: #ffffff;
}

.conv-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  position: relative;
}

.conv-item::after {
  content: '';
  position: absolute;
  left: 148rpx;
  right: 0;
  bottom: 0;
  height: 1rpx;
  background-color: #ededed;
  transform: scaleY(0.5);
}

.conv-item:last-child::after {
  display: none;
}

.conv-item--hover {
  background-color: #ececec;
}

.conv-item__body {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
}

.conv-item__name {
  display: block;
  font-size: 32rpx;
  color: #191919;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-item__preview {
  display: block;
  font-size: 26rpx;
  color: #999999;
  margin-top: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-item__meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-left: 16rpx;
  min-height: 96rpx;
  justify-content: center;
}

.conv-item__time {
  font-size: 22rpx;
  color: #b2b2b2;
}

.conv-item__badge {
  min-width: 34rpx;
  height: 34rpx;
  padding: 0 8rpx;
  margin-top: 12rpx;
  background-color: #fa5151;
  border-radius: 17rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.conv-item__badge text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 500;
}

.conv-divider {
  height: 16rpx;
  background-color: #ededed;
}

.conv-list__safe {
  height: env(safe-area-inset-bottom);
}
</style>
