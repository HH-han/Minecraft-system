<template>
  <view class="profile-page">
    <!-- 顶部个人信息 -->
    <view class="profile-header" :style="{ paddingTop: statusBarHeight + 40 + 'px' }">
      <view class="profile-card" @click="goToProfileDetail">
        <image
          class="profile-avatar"
          :src="userInfo.avatar || defaultAvatar"
          mode="aspectFill"
        />
        <view class="profile-info">
          <text class="profile-name">{{ userInfo.username || '未设置昵称' }}</text>
          <text class="profile-id">账号：{{ userInfo.account || userInfo.id || '' }}</text>
          <view class="profile-meta">
            <view class="status-btn">
              <text class="status-btn-text">+ 状态</text>
            </view>
            <view class="new-msg-badge">
              <text class="new-msg-icon">&#x1F4E5;</text>
              <text class="new-msg-text">5条新消息</text>
              <view class="red-dot"></view>
            </view>
          </view>
        </view>
        <view class="profile-right">
          <view class="qrcode-icon">
            <view class="qrcode-square"></view>
          </view>
          <text class="arrow-icon">&#x203A;</text>
        </view>
      </view>
    </view>

    <!-- 菜单列表 -->
    <view class="menu-container">
      <!-- 第一组：服务、收藏、朋友圈 -->
      <view class="menu-group">
        <view class="menu-item" @click="goToService">
          <view class="menu-icon-wrapper" style="background-color: #07C160;">
            <text class="menu-icon-text">服</text>
          </view>
          <text class="menu-name">服务</text>
          <text class="menu-arrow">&#x203A;</text>
        </view>
        <view class="menu-item" @click="goToFavorites">
          <view class="menu-icon-wrapper" style="background-color: #FF9F24;">
            <text class="menu-icon-text">收</text>
          </view>
          <text class="menu-name">收藏</text>
          <text class="menu-arrow">&#x203A;</text>
        </view>
        <view class="menu-item" @click="goToMoments">
          <view class="menu-icon-wrapper" style="background-color: #10AEFF;">
            <text class="menu-icon-text">圈</text>
          </view>
          <text class="menu-name">朋友圈</text>
          <text class="menu-arrow">&#x203A;</text>
        </view>
      </view>

      <!-- 第二组：作品、小店与卡包 -->
      <view class="menu-group">
        <view class="menu-item" @click="goToWorks">
          <view class="menu-icon-wrapper" style="background-color: #4A90E2;">
            <text class="menu-icon-text">作</text>
          </view>
          <text class="menu-name">作品</text>
          <view class="menu-extra">
            <text class="extra-text">在这里看我的视频号和公众号</text>
            <view class="live-dot"></view>
          </view>
          <text class="menu-arrow">&#x203A;</text>
        </view>
        <view class="menu-item" @click="goToShop">
          <view class="menu-icon-wrapper" style="background-color: #FF6B6B;">
            <text class="menu-icon-text">店</text>
          </view>
          <text class="menu-name">小店与卡包</text>
          <view class="menu-extra">
            <text class="extra-text">[看过]德佑湿厕纸</text>
            <text class="extra-sub">品牌店直播中</text>
            <view class="live-badge"></view>
          </view>
          <text class="menu-arrow">&#x203A;</text>
        </view>
      </view>

      <!-- 第三组：表情 -->
      <view class="menu-group">
        <view class="menu-item" @click="goToEmojis">
          <view class="menu-icon-wrapper" style="background-color: #FFD700;">
            <text class="menu-icon-text">表</text>
          </view>
          <text class="menu-name">表情</text>
          <text class="menu-arrow">&#x203A;</text>
        </view>
      </view>

      <!-- 第四组：设置 -->
      <view class="menu-group">
        <view class="menu-item" @click="goToSettings">
          <view class="menu-icon-wrapper" style="background-color: #999999;">
            <text class="menu-icon-text">设</text>
          </view>
          <text class="menu-name">设置</text>
          <text class="menu-arrow">&#x203A;</text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <view class="logout-btn" @click="handleLogout">
        <text class="logout-text">退出登录</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserInfo, clearUserInfo } from '../../utils/storage.js'

export default {
  data() {
    return {
      statusBarHeight: 20,
      userInfo: {},
      defaultAvatar: '/static/default-avatar.png'
    }
  },

  onLoad() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight || 20
    this.loadUserInfo()
  },

  onShow() {
    this.loadUserInfo()
  },

  methods: {
    loadUserInfo() {
      const userInfo = getUserInfo()
      if (userInfo) {
        this.userInfo = userInfo
      }
    },

    goToProfileDetail() {
      uni.showToast({ title: '个人资料', icon: 'none' })
    },

    goToService() {
      uni.showToast({ title: '服务', icon: 'none' })
    },

    goToFavorites() {
      uni.showToast({ title: '收藏', icon: 'none' })
    },

    goToMoments() {
      uni.showToast({ title: '朋友圈', icon: 'none' })
    },

    goToWorks() {
      uni.showToast({ title: '作品', icon: 'none' })
    },

    goToShop() {
      uni.showToast({ title: '小店与卡包', icon: 'none' })
    },

    goToEmojis() {
      uni.showToast({ title: '表情', icon: 'none' })
    },

    goToSettings() {
      uni.navigateTo({ url: '/pages/settings/settings' })
    },

    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            clearUserInfo()
            uni.reLaunch({ url: '/pages/login/login' })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #EDEDED;
  padding-bottom: 40rpx;
}

/* 个人信息头部 */
.profile-header {
  background-color: #FFFFFF;
  padding: 30rpx;
  padding-bottom: 40rpx;
  margin-bottom: 20rpx;
}

.profile-card {
  display: flex;
  align-items: center;
}

.profile-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  background-color: #E5E5E5;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.profile-info {
  flex: 1;
  overflow: hidden;
}

.profile-name {
  font-size: 36rpx;
  font-weight: 600;
  color: #000000;
  line-height: 1.4;
  display: block;
  margin-bottom: 8rpx;
}

.profile-id {
  font-size: 28rpx;
  color: #7F7F7F;
  display: block;
  margin-bottom: 16rpx;
}

.profile-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.status-btn {
  padding: 6rpx 16rpx;
  border: 1rpx solid #E5E5E5;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-btn-text {
  font-size: 24rpx;
  color: #7F7F7F;
}

.new-msg-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.new-msg-icon {
  font-size: 24rpx;
}

.new-msg-text {
  font-size: 24rpx;
  color: #7F7F7F;
}

.red-dot {
  width: 16rpx;
  height: 16rpx;
  background-color: #FF3B30;
  border-radius: 50%;
}

.profile-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-shrink: 0;
}

.qrcode-icon {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qrcode-square {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid #B2B2B2;
  border-radius: 6rpx;
  position: relative;
}

.qrcode-square::before {
  content: '';
  position: absolute;
  top: 4rpx;
  left: 4rpx;
  width: 8rpx;
  height: 8rpx;
  background-color: #B2B2B2;
}

.qrcode-square::after {
  content: '';
  position: absolute;
  bottom: 4rpx;
  right: 4rpx;
  width: 8rpx;
  height: 8rpx;
  background-color: #B2B2B2;
}

.arrow-icon {
  font-size: 48rpx;
  color: #B2B2B2;
  line-height: 1;
}

/* 菜单容器 */
.menu-container {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.menu-group {
  background-color: #FFFFFF;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 28rpx 30rpx;
  border-bottom: 1rpx solid #F5F5F5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background-color: #E5E5E5;
}

.menu-icon-wrapper {
  width: 64rpx;
  height: 64rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.menu-icon-text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.menu-name {
  font-size: 32rpx;
  color: #000000;
  flex-shrink: 0;
}

.menu-extra {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-right: 12rpx;
  overflow: hidden;
}

.extra-text {
  font-size: 26rpx;
  color: #B2B2B2;
  text-align: right;
}

.extra-sub {
  font-size: 24rpx;
  color: #B2B2B2;
  text-align: right;
}

.live-dot {
  width: 16rpx;
  height: 16rpx;
  background-color: #FF3B30;
  border-radius: 50%;
  margin-top: 4rpx;
}

.live-badge {
  width: 48rpx;
  height: 48rpx;
  background: linear-gradient(135deg, #4A90E2 0%, #5B9BD5 100%);
  border-radius: 50%;
  margin-top: 4rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.live-badge::before {
  content: '播';
  font-size: 20rpx;
  color: #FFFFFF;
}

.menu-arrow {
  font-size: 40rpx;
  color: #B2B2B2;
  line-height: 1;
  flex-shrink: 0;
}

/* 退出登录 */
.logout-section {
  margin-top: 40rpx;
  padding: 0 30rpx;
}

.logout-btn {
  height: 88rpx;
  background-color: #FFFFFF;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-btn:active {
  background-color: #E5E5E5;
}

.logout-text {
  font-size: 32rpx;
  color: #000000;
}
</style>
