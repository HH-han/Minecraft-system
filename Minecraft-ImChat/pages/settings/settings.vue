<template>
  <view class="settings-page">
    <view class="nav-bar">
      <view class="nav-back" @click="goBack">
        <text>‹</text>
      </view>
      <text class="nav-title">设置</text>
      <view class="nav-right"></view>
    </view>

    <view class="settings-list">
      <view class="settings-group">
        <view class="settings-item" @click="goToAccount">
          <view class="item-left">
            <text class="item-icon">👤</text>
            <text class="item-text">账号与安全</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goToPrivacy">
          <view class="item-left">
            <text class="item-icon">🔒</text>
            <text class="item-text">隐私</text>
          </view>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goToNoti">
          <view class="item-left">
            <text class="item-icon">🔔</text>
            <text class="item-text">新消息通知</text>
          </view>
          <view class="item-right">
            <view :class="['switch', { active: notificationEnabled }]" @click="toggleNotification">
              <view class="switch-dot"></view>
            </view>
          </view>
        </view>

        <view class="settings-item" @click="goToGeneral">
          <view class="item-left">
            <text class="item-icon">⚙️</text>
            <text class="item-text">通用</text>
          </view>
          <text class="item-arrow">›</text>
        </view>
      </view>

      <view class="settings-group">
        <view class="settings-item" @click="goToStorage">
          <view class="item-left">
            <text class="item-icon">💾</text>
            <text class="item-text">存储空间</text>
          </view>
          <text class="item-right-text">52.3MB</text>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goToLanguage">
          <view class="item-left">
            <text class="item-icon">🌐</text>
            <text class="item-text">多语言</text>
          </view>
          <text class="item-right-text">简体中文</text>
          <text class="item-arrow">›</text>
        </view>

        <view class="settings-item" @click="goToAbout">
          <view class="item-left">
            <text class="item-icon">ℹ️</text>
            <text class="item-text">关于微信</text>
          </view>
          <text class="item-right-text">版本 8.0.0</text>
          <text class="item-arrow">›</text>
        </view>
      </view>

      <view class="settings-group">
        <view class="settings-item" @click="goToHelp">
          <view class="item-left">
            <text class="item-icon">❓</text>
            <text class="item-text">帮助与反馈</text>
          </view>
          <text class="item-arrow">›</text>
        </view>
      </view>
    </view>

    <view class="version-info">
      <text>微信 8.0.0</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      notificationEnabled: true
    }
  },

  methods: {
    goBack() {
      uni.navigateBack()
    },

    goToAccount() {
      uni.showToast({ title: '开发中', icon: 'none' })
    },

    goToPrivacy() {
      uni.showToast({ title: '开发中', icon: 'none' })
    },

    toggleNotification() {
      this.notificationEnabled = !this.notificationEnabled
      uni.showToast({
        title: this.notificationEnabled ? '已开启通知' : '已关闭通知',
        icon: 'none'
      })
    },

    goToNoti() {
      uni.showToast({ title: '开发中', icon: 'none' })
    },

    goToGeneral() {
      uni.showToast({ title: '开发中', icon: 'none' })
    },

    goToStorage() {
      uni.showToast({ title: '开发中', icon: 'none' })
    },

    goToLanguage() {
      uni.showToast({ title: '开发中', icon: 'none' })
    },

    goToAbout() {
      uni.showToast({ title: '开发中', icon: 'none' })
    },

    goToHelp() {
      uni.showToast({ title: '开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background-color: #f7f7f7;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  background-color: #ffffff;
  border-bottom: 1rpx solid #f0f0f0;
}

.nav-back,
.nav-right {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-back text {
  font-size: 48rpx;
  color: #07C160;
  font-weight: 300;
}

.nav-title {
  font-size: 32rpx;
  color: #000000;
  font-weight: 600;
}

.settings-list {
  padding: 24rpx;
}

.settings-group {
  background-color: #ffffff;
  border-radius: 8rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.settings-item:last-child {
  border-bottom: none;
}

.settings-item:active {
  background-color: #f5f5f5;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.item-icon {
  font-size: 36rpx;
}

.item-text {
  font-size: 30rpx;
  color: #333333;
  font-weight: 500;
}

.item-arrow {
  font-size: 36rpx;
  color: #cccccc;
  margin-left: 16rpx;
}

.item-right {
  display: flex;
  align-items: center;
}

.item-right-text {
  font-size: 26rpx;
  color: #999999;
  margin-right: 16rpx;
}

.switch {
  width: 80rpx;
  height: 48rpx;
  background-color: #e5e5e5;
  border-radius: 24rpx;
  position: relative;
  transition: background-color 0.3s ease;
}

.switch.active {
  background-color: #07C160;
}

.switch-dot {
  width: 40rpx;
  height: 40rpx;
  background-color: #ffffff;
  border-radius: 50%;
  position: absolute;
  top: 4rpx;
  left: 4rpx;
  transition: transform 0.3s ease;
  box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}

.switch.active .switch-dot {
  transform: translateX(32rpx);
}

.version-info {
  text-align: center;
  padding: 48rpx 0;
}

.version-info text {
  font-size: 24rpx;
  color: #999999;
}
</style>
