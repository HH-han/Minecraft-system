<template>
  <view class="discover-page">
    <CustomNavBar title="发现" title-align="left" />

    <view class="discover-group">
      <UiCell title="朋友圈" :icon-bg="'#10AEFF'" arrow @click="goMoments">
        <template #icon><UiIcon name="image" :size="42" color="#FFFFFF" /></template>
      </UiCell>
      <UiCell title="视频号" :icon-bg="'#FF9500'" arrow :border="false" @click="toastDev">
        <template #icon><UiIcon name="play" :size="42" color="#FFFFFF" /></template>
      </UiCell>
    </view>

    <view class="discover-group">
      <UiCell title="扫一扫" :icon-bg="'#07C160'" arrow @click="toastDev">
        <template #icon><UiIcon name="scan" :size="42" color="#FFFFFF" /></template>
      </UiCell>
      <UiCell title="摇一摇" :icon-bg="'#4A90E2'" arrow :border="false" @click="toastDev">
        <template #icon><UiIcon name="refresh" :size="42" color="#FFFFFF" /></template>
      </UiCell>
    </view>

    <view class="discover-group">
      <UiCell title="附近" :icon-bg="'#007AFF'" arrow @click="toastDev">
        <template #icon><UiIcon name="map-pin" :size="42" color="#FFFFFF" /></template>
      </UiCell>
      <UiCell title="购物" :icon-bg="'#FF3B30'" arrow @click="toastDev">
        <template #icon><UiIcon name="shop" :size="42" color="#FFFFFF" /></template>
      </UiCell>
      <UiCell title="游戏" :icon-bg="'#AF52DE'" arrow @click="toastDev">
        <template #icon><UiIcon name="gamepad" :size="42" color="#FFFFFF" /></template>
      </UiCell>
      <UiCell title="小程序" :icon-bg="'#1D1D1F'" arrow :border="false" @click="toastDev">
        <template #icon><UiIcon name="grid" :size="42" color="#FFFFFF" /></template>
      </UiCell>
    </view>

    <view class="discover-safe"></view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiCell from '../../components/CellItem.vue'
import UiIcon from '../../components/Icon.vue'

export default {
  components: { CustomNavBar, UiCell, UiIcon },
  methods: {
    goMoments() {
      uni.navigateTo({ url: '/pages/moments/moments' })
    },
    toastDev() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.discover-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #ededed;
}

.discover-group {
  background-color: #ffffff;
  margin-top: 20rpx;
}

.discover-safe {
  height: calc(40rpx + env(safe-area-inset-bottom));
}
</style>
