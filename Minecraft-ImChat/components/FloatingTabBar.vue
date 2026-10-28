<template>
  <view class="floating-tabbar">
    <view class="floating-tabbar__capsule">
      <view
        v-for="item in items"
        :key="item.key"
        class="floating-tabbar__item"
        hover-class="floating-tabbar__item--hover"
        hover-stay-time="60"
        @click="onTap(item)"
      >
        <view
          class="floating-tabbar__icon-wrap"
          :class="{ 'is-active': active === item.key }"
        >
          <UiIcon
            :name="item.icon"
            :size="44"
            :color="active === item.key ? 'var(--accent-color)' : 'var(--text-tertiary)'"
          />
          <view v-if="badgeOf(item.key)" class="floating-tabbar__badge">
            <text>{{ badgeOf(item.key) > 99 ? '99+' : badgeOf(item.key) }}</text>
          </view>
        </view>
        <text
          class="floating-tabbar__label"
          :class="{ 'is-active': active === item.key }"
        >{{ item.text }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import UiIcon from './Icon.vue'
import store from '../store/index.js'

/**
 * 悬浮底部导航栏（胶囊样式）
 *
 * 替代原生 tabBar：保留 pages.json 的 tabBar 配置以维持 uni.switchTab 能力，
 * 组件挂载时隐藏原生 tabBar，自身以悬浮胶囊形式覆盖在页面底部。
 */
const TABS = [
  { key: 'chat', textKey: 'tabs.chat', icon: 'chat', url: '/pages/index/index' },
  { key: 'contacts', textKey: 'tabs.contacts', icon: 'contacts', url: '/pages/contacts/contacts' },
  { key: 'discover', textKey: 'tabs.discover', icon: 'discover', url: '/pages/discover/discover' },
  { key: 'profile', textKey: 'tabs.profile', icon: 'me', url: '/pages/profile/profile' }
]

export default {
  name: 'FloatingTabBar',
  components: { UiIcon },
  props: {
    active: { type: String, default: 'chat' }
  },
  computed: {
    items() {
      return TABS.map((item) => ({ ...item, text: this.$t(item.textKey) }))
    },
    // 会话未读总数（会话 tab）
    chatUnread() {
      return Object.values(store.conversations || {}).reduce(
        (sum, conv) => sum + (conv.unread || 0),
        0
      )
    },
    // 待处理好友申请数（通讯录 tab）
    contactsBadge() {
      return store.pendingRequestCount || 0
    }
  },
  mounted() {
    this.hideNativeTabBar()
  },
  methods: {
    badgeOf(key) {
      if (key === 'chat') return this.chatUnread
      if (key === 'contacts') return this.contactsBadge
      return 0
    },
    hideNativeTabBar() {
      if (typeof uni.hideTabBar !== 'function') return
      uni.hideTabBar({
        animation: false,
        fail: () => {
          setTimeout(() => {
            uni.hideTabBar({ animation: false, fail: () => {} })
          }, 50)
        }
      })
    },
    onTap(item) {
      if (item.key === this.active) return
      uni.switchTab({ url: item.url })
    }
  }
}
</script>

<style scoped>
.floating-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: calc(24rpx + env(safe-area-inset-bottom));
  display: flex;
  justify-content: center;
  z-index: 999;
  pointer-events: none;
}

.floating-tabbar__capsule {
  pointer-events: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 686rpx;
  height: 112rpx;
  padding: 0 18rpx;
  box-sizing: border-box;
  background-color: var(--glass-bg);
  border: 1rpx solid var(--glass-border);
  border-radius: 999rpx;
  box-shadow: var(--shadow-xl);
  backdrop-filter: saturate(180%) blur(28rpx);
  -webkit-backdrop-filter: saturate(180%) blur(28rpx);
}

.floating-tabbar__item {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  transition: opacity var(--transition-fast);
}

.floating-tabbar__item--hover {
  opacity: 0.65;
}

.floating-tabbar__icon-wrap {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 96rpx;
  height: 54rpx;
  border-radius: 999rpx;
  transition: background-color var(--transition-normal), box-shadow var(--transition-normal);
}

.floating-tabbar__icon-wrap.is-active {
  background-color: var(--accent-light);
  box-shadow: 0 0 24rpx var(--accent-light);
}

.floating-tabbar__badge {
  position: absolute;
  top: -8rpx;
  right: -10rpx;
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  box-sizing: border-box;
  background-color: var(--danger-color);
  border: 3rpx solid var(--bg-primary);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.floating-tabbar__badge text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 600;
  line-height: 1;
}

.floating-tabbar__label {
  margin-top: 4rpx;
  font-size: 20rpx;
  line-height: 1.2;
  letter-spacing: 0.4rpx;
  color: var(--text-tertiary);
  transition: color var(--transition-fast);
}

.floating-tabbar__label.is-active {
  color: var(--accent-color);
  font-weight: 600;
}
</style>
