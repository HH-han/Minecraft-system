<template>
  <view class="settings-page">
    <CustomNavBar :show-back="true" title="设置" title-align="center" @back="goBack" />

    <view class="settings-group">
      <UiCell title="账号与安全" arrow :border="false" @click="toastDev" />
    </view>

    <view class="settings-group">
      <UiCell title="新消息通知" :clickable="false">
        <template #right>
          <view class="switch" :class="{ active: settings.notification }" @click.stop="toggle('notification')">
            <view class="switch-dot"></view>
          </view>
        </template>
      </UiCell>
      <UiCell title="声音" :clickable="false">
        <template #right>
          <view class="switch" :class="{ active: settings.sound }" @click.stop="toggle('sound')">
            <view class="switch-dot"></view>
          </view>
        </template>
      </UiCell>
      <UiCell title="震动" :clickable="false" :border="false">
        <template #right>
          <view class="switch" :class="{ active: settings.vibration }" @click.stop="toggle('vibration')">
            <view class="switch-dot"></view>
          </view>
        </template>
      </UiCell>
    </view>

    <view class="settings-group">
      <UiCell title="通用" arrow :border="false" @click="toastDev" />
    </view>

    <view class="settings-group">
      <UiCell title="关于" :value="version" arrow :border="false" @click="toastDev" />
    </view>

    <view class="settings-version">
      <text>博览通讯 v{{ version }}</text>
    </view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiCell from '../../components/CellItem.vue'
import appConfig from '../../config/index.js'
import store, { saveSettings } from '../../store/index.js'

export default {
  components: { CustomNavBar, UiCell },
  data() {
    return {
      version: appConfig.APP_VERSION
    }
  },
  computed: {
    settings() {
      return store.settings
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    toggle(key) {
      saveSettings({ [key]: !store.settings[key] })
    },
    toastDev() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background-color: #ededed;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
}

.settings-group {
  background-color: #ffffff;
  margin-top: 20rpx;
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
  background-color: #07c160;
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

.settings-version {
  text-align: center;
  padding: 48rpx 0;
}

.settings-version text {
  font-size: 24rpx;
  color: #888888;
}
</style>
