<template>
  <view class="navbar" :class="{ 'is-fixed': fixed }" :style="{ backgroundColor: background }">
    <view class="navbar__status" :style="{ height: statusBarHeight + 'px' }"></view>
    <view class="navbar__bar">
      <view class="navbar__side navbar__side--left">
        <view v-if="showBack" class="navbar__btn" @click="$emit('back')">
          <text class="navbar__back">‹</text>
        </view>
      </view>

      <text
        class="navbar__title"
        :class="[`is-${titleAlign}`, { 'is-placeholder': showBack && titleAlign === 'center' }]"
        :style="{ color }"
      >{{ title }}</text>

      <view class="navbar__side navbar__side--right">
        <view v-if="showSearch" class="navbar__btn" @click="$emit('search')">
          <svg class="navbar__svg" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
            <path d="M1024 955.733333L716.8 650.971429c53.638095-68.266667 87.771429-156.038095 87.771429-248.685715C804.571429 180.419048 624.152381 0 402.285714 0S0 177.980952 0 399.847619c0 221.866667 180.419048 402.285714 402.285714 402.285714 95.085714 0 180.419048-31.695238 248.685715-87.771428l307.2 307.2 65.828571-65.828572z m-950.857143-555.885714C73.142857 219.428571 221.866667 70.704762 402.285714 70.704762S731.428571 219.428571 731.428571 399.847619c0 85.333333-34.133333 168.228571-92.647619 229.180952-63.390476 63.390476-146.285714 99.961905-236.495238 99.961905-180.419048 0-329.142857-146.285714-329.142857-329.142857z" fill="currentColor"></path>
          </svg>
        </view>
        <view v-if="showAdd" class="navbar__btn" @click="handleAdd">
          <svg class="navbar__svg" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
            <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z m0 832c-211.9 0-384-172.1-384-384s172.1-384 384-384 384 172.1 384 384-172.1 384-384 384z" fill="currentColor"></path>
            <path d="M740 480H544V284c0-15.5-14.3-28-32-28s-32 12.5-32 28v196H284c-15.5 0-28 14.3-28 32s12.5 32 28 32h196v196c0 15.5 14.3 28 32 28s32-12.5 32-28V544h196c15.5 0 28-14.3 28-32s-12.5-32-28-32z" fill="currentColor"></path>
          </svg>
        </view>
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
        @click="onMenuItem(item)"
      >
        <view class="navbar-menu__icon">
          <UiIcon v-if="item.iconName" :name="item.iconName" :size="34" color="#FFFFFF" />
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
 * 自定义导航栏（微信风格）
 *
 * 默认在正常文档流中（fixed=false），页面用 flex 纵向布局即可，无需计算 padding。
 * 菜单项支持 iconName（UiIcon 名称，优先）或 icon（字符）两种写法。
 */
export default {
  name: 'CustomNavBar',
  components: { UiIcon },
  props: {
    title: { type: String, default: '' },
    showBack: { type: Boolean, default: false },
    showSearch: { type: Boolean, default: false },
    showAdd: { type: Boolean, default: false },
    menuItems: { type: Array, default: () => [] },
    background: { type: String, default: '#EDEDED' },
    color: { type: String, default: '#191919' },
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
}

.navbar.is-fixed {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.navbar__bar {
  height: 88rpx;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
}

.navbar__side {
  min-width: 88rpx;
  display: flex;
  align-items: center;
}

.navbar__side--left {
  justify-content: flex-start;
}

.navbar__side--right {
  justify-content: flex-end;
}

.navbar__title {
  flex: 1;
  font-size: 34rpx;
  font-weight: 600;
  letter-spacing: -0.3rpx;
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

.navbar__btn {
  min-width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.navbar__btn:active {
  opacity: 0.5;
}

.navbar__svg {
  width: 44rpx;
  height: 44rpx;
}

.navbar__back {
  font-size: 60rpx;
  line-height: 1;
  font-weight: 300;
  color: #191919;
  margin-top: -6rpx;
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
  top: calc(env(safe-area-inset-top) + 100rpx);
  right: 24rpx;
  min-width: 300rpx;
  background-color: #4c4c4c;
  border-radius: 16rpx;
  padding: 12rpx 0;
  animation: navbarMenuIn 0.18s ease;
}

.navbar-menu__arrow {
  position: absolute;
  top: -12rpx;
  right: 32rpx;
  width: 0;
  height: 0;
  border-left: 14rpx solid transparent;
  border-right: 14rpx solid transparent;
  border-bottom: 14rpx solid #4c4c4c;
}

.navbar-menu__item {
  display: flex;
  align-items: center;
  padding: 22rpx 32rpx;
}

.navbar-menu__item:active {
  background-color: rgba(255, 255, 255, 0.08);
}

.navbar-menu__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34rpx;
  margin-right: 18rpx;
  font-size: 34rpx;
}

.navbar-menu__text {
  font-size: 30rpx;
  color: #ffffff;
}

.navbar-menu__badge {
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  margin-left: auto;
  background-color: #fa5151;
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
    transform: translateY(-16rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
