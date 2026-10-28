<template>
  <view
    class="navbar"
    :class="{ 'is-fixed': fixed, 'is-transparent': transparent }"
    :style="rootStyle"
  >
    <view class="navbar__status" :style="{ height: statusBarHeight + 'px' }"></view>
    <view class="navbar__bar">
      <view class="navbar__side navbar__side--left">
        <view v-if="showBack" class="navbar__btn" hover-class="navbar__btn--hover" @click="$emit('back')">
          <UiIcon name="back" :size="38" color="var(--text-primary)" />
        </view>
        <slot name="left"></slot>
      </view>

      <view class="navbar__title-wrap">
        <text class="navbar__title" :class="`is-${titleAlign}`">{{ title }}</text>
        <text v-if="subtitle" class="navbar__subtitle" :class="`is-${titleAlign}`">{{ subtitle }}</text>
      </view>

      <view class="navbar__side navbar__side--right">
        <view
          v-if="showSearch"
          class="navbar__btn navbar__btn--glass"
          hover-class="navbar__btn--hover"
          @click="$emit('search')"
        >
          <UiIcon name="search" :size="34" color="var(--text-primary)" />
        </view>
        <view
          v-if="showAdd"
          class="navbar__btn navbar__btn--glass"
          hover-class="navbar__btn--hover"
          @click="handleAdd"
        >
          <UiIcon name="plus" :size="36" color="var(--text-primary)" />
        </view>
        <slot name="right"></slot>
      </view>
    </view>
  </view>

  <view v-if="showMenu" class="navbar-menu-mask" @click="closeMenu">
    <view class="navbar-menu" @click.stop>
      <view class="navbar-menu__arrow"></view>
      <view
        v-for="(item, index) in menuItems"
        :key="index"
        class="navbar-menu__item"
        hover-class="navbar-menu__item--hover"
        @click="onMenuItem(item)"
      >
        <view class="navbar-menu__icon">
          <UiIcon v-if="item.iconName" :name="item.iconName" :size="32" color="#FFFFFF" />
          <text v-else>{{ item.icon }}</text>
        </view>
        <text class="navbar-menu__text">{{ item.text }}</text>
        <view v-if="item.badge" class="navbar-menu__badge">
          <text>{{ item.badge > 99 ? '99+' : item.badge }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import UiIcon from './Icon.vue'

/**
 * 自定义导航栏（Game IM 风格）
 *
 * 默认在正常文档流中（fixed=false），页面用 flex 纵向布局即可；
 * 深色/浅色由全局主题变量控制，background 仅在需要覆盖时传入。
 */
export default {
  name: 'CustomNavBar',
  components: { UiIcon },
  props: {
    title: { type: String, default: '' },
    subtitle: { type: String, default: '' },
    showBack: { type: Boolean, default: false },
    showSearch: { type: Boolean, default: false },
    showAdd: { type: Boolean, default: false },
    menuItems: { type: Array, default: () => [] },
    background: { type: String, default: '' },
    transparent: { type: Boolean, default: false },
    color: { type: String, default: '' },
    titleAlign: { type: String, default: 'left' },
    fixed: { type: Boolean, default: false }
  },
  emits: ['back', 'search', 'add', 'menuItemClick'],
  data() {
    return {
      statusBarHeight: 20,
      showMenu: false
    }
  },
  computed: {
    rootStyle() {
      const style = {}
      if (this.background) style.backgroundColor = this.background
      if (this.color) style.color = this.color
      return style
    }
  },
  created() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight || 20
  },
  methods: {
    handleAdd() {
      if (this.menuItems && this.menuItems.length) {
        this.showMenu = true
      } else {
        this.$emit('add')
      }
    },
    closeMenu() {
      this.showMenu = false
    },
    onMenuItem(item) {
      this.closeMenu()
      this.$emit('menuItemClick', item)
    }
  }
}
</script>

<style scoped>
.navbar {
  flex-shrink: 0;
  position: relative;
  color: var(--text-primary);
  background-color: var(--bg-nav);
  backdrop-filter: saturate(180%) blur(24rpx);
  -webkit-backdrop-filter: saturate(180%) blur(24rpx);
  border-bottom: 1rpx solid var(--divider);
}

.navbar.is-transparent {
  background-color: transparent;
  border-bottom-color: transparent;
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
}

.navbar.is-fixed {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.navbar__bar {
  height: 96rpx;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
}

.navbar__side {
  min-width: 96rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.navbar__side--left {
  justify-content: flex-start;
}

.navbar__side--right {
  justify-content: flex-end;
}

.navbar__title-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 8rpx;
}

.navbar__title {
  font-size: 34rpx;
  font-weight: 600;
  letter-spacing: -0.2rpx;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.navbar__title.is-left {
  text-align: left;
}

.navbar__title.is-center {
  text-align: center;
}

.navbar__subtitle {
  margin-top: 2rpx;
  font-size: 20rpx;
  color: var(--text-tertiary);
  letter-spacing: 0.4rpx;
}

.navbar__subtitle.is-center {
  text-align: center;
}

.navbar__btn {
  width: 68rpx;
  height: 68rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  transition: background-color var(--transition-fast), transform var(--transition-fast);
}

.navbar__btn--glass {
  background-color: var(--bg-chip);
}

.navbar__btn--hover {
  background-color: var(--bg-active);
  transform: scale(0.94);
}

/* 弹出菜单 */
.navbar-menu-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.navbar-menu {
  position: absolute;
  top: calc(env(safe-area-inset-top) + 104rpx);
  right: 24rpx;
  min-width: 320rpx;
  padding: 12rpx;
  border-radius: var(--radius-lg);
  background-color: var(--bg-elevated);
  border: 1rpx solid var(--border-light);
  box-shadow: var(--shadow-xl);
  animation: navbarMenuIn 0.18s ease;
}

.navbar-menu__arrow {
  position: absolute;
  top: -10rpx;
  right: 34rpx;
  width: 20rpx;
  height: 20rpx;
  background-color: var(--bg-elevated);
  border-left: 1rpx solid var(--border-light);
  border-top: 1rpx solid var(--border-light);
  transform: rotate(45deg);
}

.navbar-menu__item {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  border-radius: var(--radius-md);
  transition: background-color var(--transition-fast);
}

.navbar-menu__item--hover {
  background-color: var(--bg-hover);
}

.navbar-menu__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60rpx;
  height: 60rpx;
  margin-right: 18rpx;
  border-radius: 16rpx;
  background-image: var(--accent-gradient);
  font-size: 30rpx;
  color: #ffffff;
}

.navbar-menu__text {
  font-size: 28rpx;
  color: var(--text-primary);
}

.navbar-menu__badge {
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  margin-left: auto;
  background-color: var(--danger-color);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.navbar-menu__badge text {
  font-size: 20rpx;
  color: #ffffff;
  font-weight: 600;
}

@keyframes navbarMenuIn {
  from {
    opacity: 0;
    transform: translateY(-16rpx) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
