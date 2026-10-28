<template>
  <view
    class="ui-avatar"
    :class="[shape, sizeClass, { 'has-ring': ring }]"
    :style="rootStyle"
  >
    <image
      v-if="src && !failed"
      class="ui-avatar__img"
      :src="src"
      mode="aspectFill"
      @error="onError"
    />
    <view v-else class="ui-avatar__fallback" :style="fallbackStyle">
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
 * 未设置或加载失败时，降级为「昵称首字 + 稳定双色渐变」占位头像。
 * 主题切换由 CSS 变量完成（边框、在线点描边）。
 */
const GRADIENTS = [
  ['#4F8BFF', '#7C5CFF'],
  ['#22C55E', '#0EA5A4'],
  ['#F59E0B', '#EF4444'],
  ['#8B5CF6', '#EC4899'],
  ['#06B6D4', '#3B82F6'],
  ['#F97316', '#F43F5E'],
  ['#10B981', '#65A30D'],
  ['#64748B', '#334155']
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
    online: { type: Boolean, default: false },
    // 强调描边（用于群主/当前用户等场景）
    ring: { type: Boolean, default: false }
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
      return `${Math.round(this.size * 0.4)}rpx`
    },
    fallbackStyle() {
      const name = String(this.name || '?')
      let hash = 0
      for (let i = 0; i < name.length; i++) {
        hash = (hash * 31 + name.charCodeAt(i)) % 100000
      }
      const pair = GRADIENTS[hash % GRADIENTS.length]
      return {
        backgroundImage: `linear-gradient(135deg, ${pair[0]} 0%, ${pair[1]} 100%)`
      }
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
  border-radius: 22rpx;
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
  box-shadow: inset 0 0 0 1rpx rgba(255, 255, 255, 0.16);
}

.ui-avatar__initial {
  color: #ffffff;
  font-weight: 600;
  line-height: 1;
  letter-spacing: 0.5rpx;
  text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.18);
}

.ui-avatar.has-ring::after {
  content: '';
  position: absolute;
  inset: -6rpx;
  border-radius: inherit;
  border: 3rpx solid var(--accent-color);
  opacity: 0.85;
  pointer-events: none;
}

.ui-avatar__online {
  position: absolute;
  right: -2rpx;
  bottom: -2rpx;
  width: 18rpx;
  height: 18rpx;
  background-color: var(--success-color);
  border-radius: 50%;
  border: 3rpx solid var(--bg-primary);
}
</style>
