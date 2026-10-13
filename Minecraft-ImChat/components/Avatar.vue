<template>
  <view
    class="ui-avatar"
    :class="[shape, sizeClass]"
    :style="rootStyle"
  >
    <image
      v-if="src && !failed"
      class="ui-avatar__img"
      :src="src"
      mode="aspectFill"
      @error="onError"
    />
    <view v-else class="ui-avatar__fallback" :style="{ backgroundColor: bgColor }">
      <text class="ui-avatar__initial" :style="{ fontSize: initialFontSize }">{{ initial }}</text>
    </view>

    <view v-if="online" class="ui-avatar__online"></view>
    <slot />
  </view>
</template>

<script>
/**
 * 通用头像
 *
 * 不依赖 default-avatar.png 等静态资源：加载失败或未设置时，
 * 自动降级为「昵称首字 + 稳定配色」的占位头像。
 */
const COLORS = [
  '#5B8FF9', '#61DDAA', '#F6BD16', '#7262FD',
  '#78D3F8', '#9661BC', '#F6903D', '#008685',
  '#F08BB4', '#5D7092'
]

export default {
  name: 'UiAvatar',
  props: {
    src: { type: String, default: '' },
    name: { type: String, default: '' },
    // round | square
    shape: { type: String, default: 'round' },
    // 尺寸（rpx）
    size: { type: Number, default: 88 },
    online: { type: Boolean, default: false }
  },
  data() {
    return { failed: false }
  },
  computed: {
    rootStyle() {
      return {
        width: `${this.size}rpx`,
        height: `${this.size}rpx`
      }
    },
    sizeClass() {
      return this.size >= 120 ? 'is-lg' : ''
    },
    initial() {
      const name = String(this.name || '').trim()
      if (!name) return '?'
      return Array.from(name)[0].toUpperCase()
    },
    initialFontSize() {
      return `${Math.round(this.size * 0.42)}rpx`
    },
    bgColor() {
      const name = String(this.name || '?')
      let hash = 0
      for (let i = 0; i < name.length; i++) {
        hash = (hash * 31 + name.charCodeAt(i)) % 100000
      }
      return COLORS[hash % COLORS.length]
    }
  },
  watch: {
    src() {
      this.failed = false
    }
  },
  methods: {
    onError() {
      this.failed = true
    }
  }
}
</script>

<style scoped>
.ui-avatar {
  position: relative;
  flex-shrink: 0;
  overflow: visible;
}

.ui-avatar.round {
  border-radius: 50%;
}

.ui-avatar.square {
  border-radius: 16rpx;
}

.ui-avatar__img,
.ui-avatar__fallback {
  width: 100%;
  height: 100%;
  border-radius: inherit;
  display: block;
}

.ui-avatar__fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.ui-avatar__initial {
  color: #ffffff;
  font-weight: 500;
  line-height: 1;
}

.ui-avatar__online {
  position: absolute;
  right: -2rpx;
  bottom: -2rpx;
  width: 18rpx;
  height: 18rpx;
  background-color: #07c160;
  border-radius: 50%;
  border: 3rpx solid #ffffff;
}
</style>
