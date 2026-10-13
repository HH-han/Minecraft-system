<template>
  <view class="emoji-panel">
    <scroll-view class="emoji-panel__scroll" scroll-y>
      <view v-if="activeTab === 'system'" class="emoji-panel__group" v-for="group in systemGroups" :key="group.name">
        <text v-if="systemGroups.length > 1" class="emoji-panel__group-name">{{ group.name }}</text>
        <view class="emoji-panel__grid">
          <view
            v-for="(emoji, index) in group.emojis"
            :key="index"
            class="emoji-panel__item"
            @click="onSelectSystem(emoji)"
          >
            <text class="emoji-panel__emoji">{{ emoji }}</text>
          </view>
        </view>
      </view>

      <view v-else class="emoji-panel__grid">
        <view
          v-for="item in customEmojis"
          :key="item.id"
          class="emoji-panel__item"
          @click="onSelectCustom(item)"
        >
          <image class="emoji-panel__image" :src="item.url" mode="aspectFit" />
        </view>
        <view v-if="customEmojis.length === 0" class="emoji-panel__empty">
          <text>暂无自定义表情</text>
        </view>
      </view>
    </scroll-view>

    <view class="emoji-panel__tabs">
      <view
        class="emoji-panel__tab"
        :class="{ active: activeTab === 'system' }"
        @click="activeTab = 'system'"
      >
        <text>表情</text>
      </view>
      <view
        class="emoji-panel__tab"
        :class="{ active: activeTab === 'custom' }"
        @click="activeTab = 'custom'"
      >
        <text>我的表情</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getGroupedSystemEmojis, getEmojiList } from '../utils/chat-api.js'

// 接口不可用时的内置兜底表情
const FALLBACK_EMOJIS = [
  '😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣',
  '😊', '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰',
  '😘', '😗', '😙', '😚', '😋', '😛', '😜', '🤪',
  '😝', '🤗', '🤭', '🤫', '🤔', '🤐', '🤨', '😐',
  '😑', '😶', '😏', '😒', '🙄', '😬', '🤥', '😔',
  '😪', '🤤', '😴', '😷', '🤒', '🤕', '🤢', '🤮',
  '🤧', '🥵', '🥶', '🥴', '😵', '🤯', '🤠', '🥳',
  '😎', '🤓', '🧐', '😕', '😟', '🙁', '☹️', '😮',
  '😯', '😲', '😳', '🥺', '😦', '😧', '😨', '😰',
  '😥', '😢', '😭', '😱', '😖', '😣', '😞', '😓',
  '😩', '😫', '🥱', '😤', '😡', '😠', '🤬', '😈',
  '👿', '💀', '💩', '🤡', '👻', '👽', '🤖', '🎃',
  '😺', '😸', '😹', '😻', '😼', '😽', '🙀', '😿',
  '💋', '🤝', '👍', '👎', '👊', '✊', '🤛', '🤜',
  '🤞', '✌️', '🤟', '🤘', '🤙', '👋', '🤚', '👌',
  '❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍',
  '💔', '💕', '💞', '💓', '💗', '💖', '💘', '💝',
  '💢', '💥', '💫', '💦', '💨', '🍎', '🍊', '🍋',
  '🍌', '🍉', '🍇', '🍓', '🍑', '🍒', '🥝', '🍅',
  '🥑', '🥕', '🌽', '🍔', '🍕', '🌭', '🍿', '🍦',
  '🍩', '🍪', '🎂', '⚽', '🏀', '🎉', '🎁', '🔥'
]

function normalizeEmoji(entry) {
  if (typeof entry === 'string') return entry
  if (!entry) return ''
  return entry.emoji || entry.value || entry.content || entry.text || ''
}

export default {
  name: 'EmojiPanel',
  emits: ['select'],
  data() {
    return {
      activeTab: 'system',
      systemGroups: [{ name: '表情', emojis: FALLBACK_EMOJIS }],
      customEmojis: []
    }
  },
  created() {
    this.loadEmojis()
  },
  methods: {
    async loadEmojis() {
      try {
        const res = await getGroupedSystemEmojis()
        const data = res && res.code === 200 ? res.data : null
        const groups = this.normalizeGroups(data)
        if (groups.length) this.systemGroups = groups
      } catch (e) {
        // 保持兜底表情
      }

      try {
        const res = await getEmojiList()
        if (res && res.code === 200 && Array.isArray(res.data)) {
          this.customEmojis = res.data
            .map((item) => ({
              id: item.id || item.url || Math.random(),
              url: item.url || item.src || item.content || ''
            }))
            .filter((item) => item.url)
        }
      } catch (e) {
        // 忽略
      }
    },

    normalizeGroups(data) {
      if (!data) return []
      if (Array.isArray(data)) {
        const emojis = data.map(normalizeEmoji).filter(Boolean)
        return emojis.length ? [{ name: '表情', emojis }] : []
      }
      if (typeof data === 'object') {
        return Object.keys(data)
          .map((name) => {
            const raw = data[name]
            const emojis = Array.isArray(raw) ? raw.map(normalizeEmoji).filter(Boolean) : []
            return { name, emojis }
          })
          .filter((group) => group.emojis.length)
      }
      return []
    },

    onSelectSystem(emoji) {
      this.$emit('select', emoji)
    },

    onSelectCustom(item) {
      this.$emit('select', { type: 'image', url: item.url })
    }
  }
}
</script>

<style scoped>
.emoji-panel {
  display: flex;
  flex-direction: column;
  height: 460rpx;
  background-color: #f7f7f7;
  border-top: 1rpx solid #e5e5e5;
}

.emoji-panel__scroll {
  flex: 1;
  height: 0;
}

.emoji-panel__group {
  padding: 8rpx 0;
}

.emoji-panel__group-name {
  display: block;
  font-size: 24rpx;
  color: #999999;
  padding: 12rpx 32rpx 4rpx;
}

.emoji-panel__grid {
  display: flex;
  flex-wrap: wrap;
  padding: 16rpx 24rpx;
  box-sizing: border-box;
}

.emoji-panel__item {
  width: 12.5%;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.emoji-panel__item:active {
  transform: scale(0.85);
}

.emoji-panel__emoji {
  font-size: 52rpx;
  line-height: 1;
}

.emoji-panel__image {
  width: 64rpx;
  height: 64rpx;
}

.emoji-panel__empty {
  width: 100%;
  text-align: center;
  padding: 60rpx 0;
}

.emoji-panel__empty text {
  font-size: 26rpx;
  color: #999999;
}

.emoji-panel__tabs {
  display: flex;
  height: 80rpx;
  background-color: #ffffff;
  border-top: 1rpx solid #e5e5e5;
}

.emoji-panel__tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.emoji-panel__tab text {
  font-size: 28rpx;
  color: #666666;
}

.emoji-panel__tab.active text {
  color: #07c160;
  font-weight: 500;
}

.emoji-panel__tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 56rpx;
  height: 4rpx;
  background-color: #07c160;
  border-radius: 2rpx;
}
</style>
