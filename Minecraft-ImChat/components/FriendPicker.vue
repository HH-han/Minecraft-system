<template>
  <view class="friend-picker">
    <view class="friend-picker__search">
      <view class="friend-picker__search-box">
        <text class="friend-picker__search-icon">🔍</text>
        <input
          class="friend-picker__search-input"
          v-model="keyword"
          placeholder="搜索"
          placeholder-class="friend-picker__placeholder"
        />
        <view v-if="keyword" class="friend-picker__clear" @click="keyword = ''">
          <text>✕</text>
        </view>
      </view>
    </view>

    <view v-if="selectedList.length" class="friend-picker__selected">
      <view
        v-for="friend in selectedList"
        :key="friend.id"
        class="friend-picker__selected-item"
        @click="remove(friend)"
      >
        <UiAvatar :src="friend.avatar" :name="friend.name" :size="72" />
        <text class="friend-picker__selected-name">{{ friend.name }}</text>
        <view class="friend-picker__selected-remove"><text>✕</text></view>
      </view>
    </view>

    <scroll-view class="friend-picker__list" scroll-y>
      <view
        v-for="friend in filteredFriends"
        :key="friend.id"
        class="friend-picker__item"
        :class="{ selected: isSelected(friend.id) }"
        @click="toggle(friend)"
      >
        <UiAvatar :src="friend.avatar" :name="friend.name" :size="80" />
        <text class="friend-picker__name">{{ friend.name }}</text>
        <view class="friend-picker__checkbox" :class="{ checked: isSelected(friend.id) }">
          <text v-if="isSelected(friend.id)" class="friend-picker__check">✓</text>
        </view>
      </view>

      <UiEmpty v-if="filteredFriends.length === 0" icon="👤" text="暂无可选择的联系人" />
    </scroll-view>
  </view>
</template>

<script>
import { getFriendInfoList } from '../utils/chat-api.js'
import { getUserId } from '../utils/session.js'
import UiAvatar from './Avatar.vue'
import UiEmpty from './EmptyState.vue'

/**
 * 选人组件（发起群聊 / 邀请入群复用）
 *
 * v-model 绑定已选好友对象数组。
 */
export default {
  name: 'FriendPicker',
  components: { UiAvatar, UiEmpty },
  props: {
    modelValue: { type: Array, default: () => [] },
    // 需要排除的 id（如已在群成员中的好友）
    excludeIds: { type: Array, default: () => [] }
  },
  emits: ['update:modelValue', 'loaded'],
  data() {
    return {
      keyword: '',
      friends: []
    }
  },
  computed: {
    selectedList() {
      return this.modelValue
    },
    filteredFriends() {
      const excluded = this.excludeIds.map(String)
      const list = this.friends.filter((f) => !excluded.includes(String(f.id)))
      const kw = this.keyword.trim().toLowerCase()
      if (!kw) return list
      return list.filter((f) => String(f.name).toLowerCase().includes(kw))
    }
  },
  created() {
    this.loadFriends()
  },
  methods: {
    async loadFriends() {
      const userId = getUserId()
      if (!userId) return
      try {
        const res = await getFriendInfoList(userId)
        if (res && res.code === 200 && Array.isArray(res.data)) {
          this.friends = res.data.map((f) => ({
            id: f.friendId,
            name: f.remark || f.username || '未知用户',
            avatar: f.avatar || ''
          }))
          this.$emit('loaded', this.friends)
        }
      } catch (e) {
        console.error('加载好友列表失败:', e)
      }
    },
    isSelected(id) {
      return this.modelValue.some((m) => String(m.id) === String(id))
    },
    toggle(friend) {
      const next = this.modelValue.filter((m) => String(m.id) !== String(friend.id))
      if (next.length === this.modelValue.length) next.push(friend)
      this.$emit('update:modelValue', next)
    },
    remove(friend) {
      this.$emit('update:modelValue', this.modelValue.filter((m) => String(m.id) !== String(friend.id)))
    }
  }
}
</script>

<style scoped>
.friend-picker {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.friend-picker__search {
  padding: 16rpx 24rpx;
}

.friend-picker__search-box {
  display: flex;
  align-items: center;
  height: 68rpx;
  background-color: #f0f0f0;
  border-radius: 34rpx;
  padding: 0 24rpx;
}

.friend-picker__search-icon {
  font-size: 26rpx;
  margin-right: 12rpx;
  opacity: 0.5;
}

.friend-picker__search-input {
  flex: 1;
  font-size: 28rpx;
  color: #191919;
}

.friend-picker__placeholder {
  color: #b2b2b2;
}

.friend-picker__clear {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background-color: #c8c8cd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.friend-picker__clear text {
  font-size: 22rpx;
  color: #ffffff;
}

.friend-picker__selected {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  padding: 8rpx 24rpx 20rpx;
}

.friend-picker__selected-item {
  position: relative;
  width: 96rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.friend-picker__selected-name {
  font-size: 22rpx;
  color: #666666;
  margin-top: 6rpx;
  max-width: 96rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend-picker__selected-remove {
  position: absolute;
  top: -10rpx;
  right: -6rpx;
  width: 32rpx;
  height: 32rpx;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.friend-picker__selected-remove text {
  font-size: 20rpx;
  color: #ffffff;
}

.friend-picker__list {
  flex: 1;
  min-height: 0;
}

.friend-picker__item {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  border-bottom: 1rpx solid #f2f2f2;
}

.friend-picker__item:active {
  background-color: #f0f0f0;
}

.friend-picker__name {
  flex: 1;
  font-size: 30rpx;
  color: #191919;
  margin-left: 24rpx;
}

.friend-picker__checkbox {
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  border: 2rpx solid #d2d2d6;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.friend-picker__checkbox.checked {
  background-color: #07c160;
  border-color: #07c160;
}

.friend-picker__check {
  font-size: 24rpx;
  color: #ffffff;
  line-height: 1;
}
</style>
