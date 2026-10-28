<template>
  <view class="settings-page" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('settings.title')"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="settings-scroll" scroll-y :show-scrollbar="false">
      <text class="section-label">{{ $t('settings.appearance') }}</text>

      <view class="group-block">
        <view class="theme-card">
          <view class="theme-card__head">
            <view class="icon-tile">
              <UiIcon name="moon" :size="38" color="#FFFFFF" />
            </view>
            <view class="theme-card__text">
              <text class="theme-card__title">{{ $t('settings.theme') }}</text>
              <text class="theme-card__desc">{{ $t('settings.themeDesc') }}</text>
            </view>
          </view>

          <view class="theme-options">
            <view
              v-for="option in themeOptions"
              :key="option.value"
              class="theme-option"
              :class="{ active: settings.theme === option.value }"
              @click="switchTheme(option.value)"
            >
              <view class="theme-option__preview" :class="`is-${option.value}`">
                <view class="theme-option__bar"></view>
                <view class="theme-option__bubble"></view>
              </view>
              <view class="theme-option__label">
                <text>{{ option.label }}</text>
                <view v-if="settings.theme === option.value" class="theme-option__check">
                  <UiIcon name="check" :size="20" color="#FFFFFF" />
                </view>
              </view>
            </view>
          </view>
        </view>

        <view class="row-item">
          <view class="icon-tile icon-tile--plain">
            <UiIcon name="globe" :size="36" color="var(--text-secondary)" />
          </view>
          <view class="row-item__body">
            <text class="row-item__title">{{ $t('settings.language') }}</text>
            <text class="row-item__desc">{{ $t('settings.languageDesc') }}</text>
          </view>
          <view class="lang-options">
            <view
              v-for="item in locales"
              :key="item.code"
              class="lang-chip"
              :class="{ active: localeCode === item.code }"
              @click="switchLocale(item.code)"
            >{{ item.label }}</view>
          </view>
        </view>
      </view>

      <text class="section-label">{{ $t('settings.notifications') }}</text>

      <view class="group-block">
        <UiCell :title="$t('settings.notification')" :clickable="false" :border="true">
          <template #right>
            <view class="switch" :class="{ active: settings.notification }" @click.stop="toggle('notification')">
              <view class="switch-dot"></view>
            </view>
          </template>
        </UiCell>
        <UiCell :title="$t('settings.sound')" :clickable="false" :border="true">
          <template #right>
            <view class="switch" :class="{ active: settings.sound }" @click.stop="toggle('sound')">
              <view class="switch-dot"></view>
            </view>
          </template>
        </UiCell>
        <UiCell :title="$t('settings.vibration')" :clickable="false" :border="false">
          <template #right>
            <view class="switch" :class="{ active: settings.vibration }" @click.stop="toggle('vibration')">
              <view class="switch-dot"></view>
            </view>
          </template>
        </UiCell>
      </view>

      <text class="section-label">{{ $t('settings.general') }}</text>

      <view class="group-block">
        <UiCell :title="$t('settings.account')" arrow :border="true" @click="toastDev" />
        <UiCell :title="$t('settings.privacy')" arrow :border="false" @click="toastDev" />
      </view>

      <view class="group-block">
        <UiCell :title="$t('settings.about')" :value="'v' + version" arrow :border="false" @click="toastDev" />
      </view>

      <view class="settings-footer">
        <UiIcon name="sparkles" :size="28" color="var(--text-tertiary)" />
        <text>{{ $t('common.appName') }} v{{ version }}</text>
      </view>
      <view class="safe-bottom-space--sm"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiCell from '../../components/CellItem.vue'
import UiIcon from '../../components/Icon.vue'
import appConfig from '../../config/index.js'
import store, { saveSettings, setTheme } from '../../store/index.js'
import { SUPPORTED_LOCALES, setLocale } from '../../locales/index.js'

export default {
  components: { CustomNavBar, UiCell, UiIcon },
  data() {
    return {
      version: appConfig.APP_VERSION,
      locales: SUPPORTED_LOCALES
    }
  },
  computed: {
    settings() {
      return store.settings
    },
    themeOptions() {
      return [
        { value: 'light', label: this.$t('settings.themeLight') },
        { value: 'dark', label: this.$t('settings.themeDark') }
      ]
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    switchTheme(theme) {
      setTheme(theme)
    },
    switchLocale(code) {
      setLocale(code)
    },
    toggle(key) {
      saveSettings({ [key]: !store.settings[key] })
    },
    toastDev() {
      uni.showToast({ title: this.$t('common.comingSoon'), icon: 'none' })
    }
  }
}
</script>

<style scoped>
.settings-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.settings-scroll {
  flex: 1;
  height: 0;
}

.group-block {
  margin: 0 24rpx;
}

.section-label {
  display: block;
  padding: 32rpx 40rpx 14rpx;
  font-size: 22rpx;
  font-weight: 600;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

.theme-card {
  padding: 28rpx;
}

.theme-card__head {
  display: flex;
  align-items: center;
}

.theme-card__text {
  margin-left: 22rpx;
  display: flex;
  flex-direction: column;
}

.theme-card__title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-primary);
}

.theme-card__desc {
  margin-top: 4rpx;
  font-size: 22rpx;
  color: var(--text-tertiary);
}

.theme-options {
  display: flex;
  gap: 20rpx;
  margin-top: 24rpx;
}

.theme-option {
  flex: 1;
  padding: 12rpx;
  border-radius: var(--radius-md);
  border: 2rpx solid var(--border-light);
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
}

.theme-option.active {
  border-color: var(--accent-color);
  box-shadow: 0 0 0 4rpx var(--accent-light);
}

.theme-option__preview {
  height: 112rpx;
  border-radius: 14rpx;
  position: relative;
  overflow: hidden;
  padding: 14rpx;
}

.theme-option__preview.is-light {
  background-color: #f2f4f8;
}

.theme-option__preview.is-dark {
  background-color: #0f131a;
}

.theme-option__bar {
  height: 18rpx;
  width: 60%;
  border-radius: 9rpx;
}

.is-light .theme-option__bar {
  background-color: #ffffff;
}

.is-dark .theme-option__bar {
  background-color: #232a36;
}

.theme-option__bubble {
  position: absolute;
  right: 14rpx;
  bottom: 14rpx;
  width: 54rpx;
  height: 26rpx;
  border-radius: 14rpx;
}

.is-light .theme-option__bubble {
  background-color: #c8f7b0;
}

.is-dark .theme-option__bubble {
  background-color: #2a6f4f;
}

.theme-option__label {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12rpx;
  padding: 0 4rpx;
}

.theme-option__label text {
  font-size: 24rpx;
  color: var(--text-secondary);
}

.theme-option.active .theme-option__label text {
  color: var(--accent-color);
  font-weight: 600;
}

.theme-option__check {
  width: 30rpx;
  height: 30rpx;
  border-radius: 50%;
  background-image: var(--accent-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
}

.lang-options {
  display: flex;
  gap: 10rpx;
  flex-shrink: 0;
}

.lang-chip {
  height: 52rpx;
  padding: 0 20rpx;
  border-radius: 999rpx;
  display: flex;
  align-items: center;
  font-size: 24rpx;
  color: var(--text-secondary);
  background-color: var(--bg-chip);
  transition: all var(--transition-fast);
}

.lang-chip.active {
  color: var(--accent-color);
  background-color: var(--accent-light);
  font-weight: 600;
}

.switch {
  width: 84rpx;
  height: 48rpx;
  background-color: var(--bg-tertiary);
  border: 1rpx solid var(--border-light);
  border-radius: 24rpx;
  position: relative;
  transition: background-color 0.3s ease;
}

.switch.active {
  background-image: var(--accent-gradient);
  border-color: transparent;
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
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.24);
}

.switch.active .switch-dot {
  transform: translateX(36rpx);
}

.settings-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  padding: 44rpx 0 24rpx;
}

.settings-footer text {
  font-size: 22rpx;
  color: var(--text-tertiary);
  letter-spacing: 0.6rpx;
}
</style>
