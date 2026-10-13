<template>
  <view class="me-page">
    <!-- 顶部个人信息卡 -->
    <view
      class="me-card"
      :style="{ paddingTop: statusBarHeight + 'px' }"
      hover-class="me-card--hover"
      hover-stay-time="60"
      @click="goEdit"
    >
      <view class="me-card__inner">
        <UiAvatar shape="square" :size="140" :src="userInfo.avatar" :name="displayName" />
        <view class="me-card__info">
          <text class="me-card__name">{{ displayName }}</text>
          <text class="me-card__account">微信号：{{ displayAccount }}</text>
        </view>
        <view class="me-card__right">
          <UiIcon name="qr" :size="40" color="#888888" />
          <UiIcon name="chevron-right" :size="34" color="#C8C8CD" />
        </view>
      </view>
    </view>

    <!-- 组1：服务 / 收藏 / 朋友圈 -->
    <view class="me-group">
      <UiCell title="服务" :icon-bg="'#07C160'" arrow @click="toastDev">
        <template #icon><UiIcon name="grid" :size="42" color="#FFFFFF" /></template>
      </UiCell>
      <UiCell title="收藏" :icon-bg="'#FF9F24'" arrow @click="goFavorites">
        <template #icon><UiIcon name="star" :size="42" color="#FFFFFF" /></template>
      </UiCell>
      <UiCell title="朋友圈" :icon-bg="'#10AEFF'" arrow :border="false" @click="goMoments">
        <template #icon><UiIcon name="image" :size="42" color="#FFFFFF" /></template>
      </UiCell>
    </view>

    <!-- 组2：卡包 / 表情 -->
    <view class="me-group">
      <UiCell title="卡包" :icon-bg="'#FF6B6B'" arrow @click="toastDev">
        <template #icon><UiIcon name="shop" :size="42" color="#FFFFFF" /></template>
      </UiCell>
      <UiCell title="表情" :icon-bg="'#FFC300'" arrow :border="false" @click="toastDev">
        <template #icon><UiIcon name="smile" :size="42" color="#FFFFFF" /></template>
      </UiCell>
    </view>

    <!-- 组3：设置 -->
    <view class="me-group">
      <UiCell title="设置" :icon-bg="'#999999'" arrow :border="false" @click="goSettings">
        <template #icon><UiIcon name="settings" :size="42" color="#FFFFFF" /></template>
      </UiCell>
    </view>

    <!-- 退出登录 -->
    <view class="me-group">
      <view class="me-logout" hover-class="me-logout--hover" hover-stay-time="60" @click="handleLogout">
        <text>退出登录</text>
      </view>
    </view>
  </view>
</template>

<script>
import UiAvatar from '../../components/Avatar.vue'
import UiIcon from '../../components/Icon.vue'
import UiCell from '../../components/CellItem.vue'
import { getUserInfo } from '../../utils/storage.js'
import { auth } from '../../utils/auth.js'
import { reset } from '../../store/index.js'

export default {
  components: { UiAvatar, UiIcon, UiCell },
  data() {
    return {
      statusBarHeight: 20,
      userInfo: {}
    }
  },
  computed: {
    displayName() {
      return this.userInfo.nickname || this.userInfo.username || this.userInfo.account || '未设置昵称'
    },
    displayAccount() {
      return this.userInfo.account || this.userInfo.id || '-'
    }
  },
  onLoad() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight || 20
  },
  onShow() {
    this.loadUserInfo()
  },
  methods: {
    loadUserInfo() {
      const userInfo = getUserInfo()
      if (userInfo) this.userInfo = userInfo
    },
    goEdit() {
      uni.navigateTo({ url: '/pages/profile-edit/profile-edit' })
    },
    goFavorites() {
      uni.navigateTo({ url: '/pages/favorites/favorites' })
    },
    goMoments() {
      uni.navigateTo({ url: '/pages/moments/moments' })
    },
    goSettings() {
      uni.navigateTo({ url: '/pages/settings/settings' })
    },
    toastDev() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    },
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: async (res) => {
          if (!res.confirm) return
          await auth.logout()
          reset()
          uni.reLaunch({ url: '/pages/login/login' })
        }
      })
    }
  }
}
</script>

<style scoped>
.me-page {
  min-height: 100vh;
  background-color: #ededed;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
}

.me-card {
  background-color: #ffffff;
}

.me-card--hover {
  background-color: #ececec;
}

.me-card__inner {
  display: flex;
  align-items: center;
  padding: 40rpx 32rpx;
}

.me-card__info {
  flex: 1;
  min-width: 0;
  margin-left: 28rpx;
}

.me-card__name {
  display: block;
  font-size: 44rpx;
  font-weight: 600;
  color: #191919;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.me-card__account {
  display: block;
  margin-top: 14rpx;
  font-size: 28rpx;
  color: #888888;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.me-card__right {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-shrink: 0;
  margin-left: 16rpx;
}

.me-group {
  background-color: #ffffff;
  margin-top: 20rpx;
}

.me-logout {
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.me-logout--hover {
  background-color: #ececec;
}

.me-logout text {
  font-size: 32rpx;
  color: #fa5151;
}
</style>
