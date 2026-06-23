<template>
  <view>
    <view class="nav-wrapper" :class="{ 'nav-fixed': fixed }">
      <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
      <view class="custom-nav" :style="fixed ? { top: statusBarHeight + 'px' } : {}">
        <view class="nav-content">
          <text class="nav-title">{{ title }}</text>
          <view class="nav-actions">
            <view class="nav-btn" @click="handleSearch">
              <text class="nav-icon">
                <svg t="1782225552081" class="icon" viewBox="0 0 1026 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="18713" width="200" height="200"><path d="M1024 955.733333L716.8 650.971429c53.638095-68.266667 87.771429-156.038095 87.771429-248.685715C804.571429 180.419048 624.152381 0 402.285714 0S0 177.980952 0 399.847619c0 221.866667 180.419048 402.285714 402.285714 402.285714 95.085714 0 180.419048-31.695238 248.685715-87.771428l307.2 307.2 65.828571-65.828572z m-950.857143-555.885714C73.142857 219.428571 221.866667 70.704762 402.285714 70.704762S731.428571 219.428571 731.428571 399.847619c0 85.333333-34.133333 168.228571-92.647619 229.180952-63.390476 63.390476-146.285714 99.961905-236.495238 99.961905-180.419048 0-329.142857-146.285714-329.142857-329.142857z" fill="#999999" p-id="18714"></path></svg>
              </text>
            </view>
            <view class="nav-btn" @click="handleAdd">
              <text class="nav-icon">
                <svg t="1782225593860" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="19704" width="200" height="200"><path d="M924.549 337.707c-22.553-53.321-54.833-101.202-95.943-142.312-41.11-41.11-88.991-73.39-142.313-95.943-55.221-23.357-113.861-35.2-174.293-35.2S392.928 76.095 337.707 99.451c-53.321 22.553-101.202 54.833-142.312 95.943-41.11 41.11-73.39 88.991-95.943 142.312-23.357 55.222-35.2 113.862-35.2 174.294s11.843 119.072 35.199 174.293c22.553 53.321 54.833 101.202 95.943 142.313 41.11 41.11 88.991 73.391 142.312 95.943 55.221 23.356 113.862 35.199 174.293 35.199s119.072-11.843 174.293-35.199c53.321-22.553 101.202-54.833 142.313-95.943s73.391-88.991 95.943-142.313c23.356-55.221 35.199-113.861 35.199-174.293s-11.842-119.072-35.198-174.293zM512 896.252c-211.877 0-384.252-172.375-384.252-384.252S300.123 127.748 512 127.748 896.252 300.123 896.252 512 723.877 896.252 512 896.252z" fill="#8a8a8a" p-id="19705"></path><path d="M740 480H544V284c0-15.464-14.327-28-32-28s-32 12.536-32 28v196H284c-15.464 0-28 14.327-28 32s12.536 32 28 32h196v196c0 15.464 14.327 28 32 28s32-12.536 32-28V544h196c15.464 0 28-14.327 28-32s-12.536-32-28-32z" fill="#8a8a8a" p-id="19706"></path></svg>
              </text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 弹出菜单 -->
    <view v-if="showMenu && menuItems.length > 0" class="menu-overlay" @click="closeMenu">
      <view class="popup-menu" @click.stop>
        <view class="menu-arrow"></view>
        <view
          v-for="(item, index) in menuItems"
          :key="index"
          class="menu-item"
          @click="handleMenuItemClick(item)"
        >
          <text class="menu-icon">{{ item.icon }}</text>
          <text class="menu-text">{{ item.text }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CustomNavBar',
  props: {
    title: {
      type: String,
      default: ''
    },
    
    searchIcon: {
      type: String,
      default: ''
    },
    fixed: {
      type: Boolean,
      default: true
    },
    menuItems: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      statusBarHeight: 20,
      showMenu: false,
      menuItems: [
        { text: '发起群聊', icon: '💬', action: 'groupChat' },
        { text: '添加朋友', icon: '👤', action: 'addFriend' },
        { text: '扫一扫', icon: '📷', action: 'scan' },
        { text: '收付款', icon: '💰', action: 'pay' }
      ]
    }
  },
  onLoad() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight || 20
  },
  methods: {
    handleSearch() {
      this.$emit('search')
    },
    handleAdd() {
      if (this.menuItems.length > 0) {
        this.showMenu = true
      } else {
        this.$emit('add')
      }
    },
    closeMenu() {
      this.showMenu = false
    },
    handleMenuItemClick(item) {
      this.closeMenu()
      this.$emit('menuItemClick', item)
    }
  }
}
</script>

<style scoped>
.nav-wrapper {
  flex-shrink: 0;
}

.nav-wrapper.nav-fixed {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.status-bar {
  background-color: #f5f5f7;
}

.nav-wrapper.nav-fixed .status-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
}

.custom-nav {
  background-color: #f5f5f7;
}

.nav-wrapper.nav-fixed .custom-nav {
  position: fixed;
  left: 0;
  right: 0;
}

.nav-content {
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 0 20rpx;
}

.nav-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #000000;
}

.nav-actions {
  position: absolute;
  right: 20rpx;
  top: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.nav-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-icon {
  display: inline-block;
  width: 36rpx;
  height: 36rpx;
  position: relative;
  font-size: 32rpx;
  line-height: 36rpx;
  text-align: center;
}

.nav-icon svg {
  width: 100%;
  height: 100%;
}

/* 弹出菜单样式 */
.menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding-top: calc(var(--status-bar-height, 44px) + 100rpx);
  padding-right: 32rpx;
}

.popup-menu {
  background-color: rgba(0, 0, 0, 0.85);
  border-radius: 16rpx;
  padding: 12rpx 0;
  min-width: 280rpx;
  animation: slideDown 0.2s ease;
}

.menu-arrow {
  position: absolute;
  top: -12rpx;
  right: 40rpx;
  width: 0;
  height: 0;
  border-left: 16rpx solid transparent;
  border-right: 16rpx solid transparent;
  border-bottom: 16rpx solid rgba(0, 0, 0, 0.85);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  transition: background-color 0.2s;
}

.menu-item:active {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

.menu-text {
  font-size: 30rpx;
  color: #ffffff;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>