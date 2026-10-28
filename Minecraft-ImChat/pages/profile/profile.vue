<template>
  <view class="me-page" :class="themeClass">
    <scroll-view class="me-scroll" scroll-y :show-scrollbar="false">
      <view
        class="me-hero grid-overlay"
        :style="{ paddingTop: statusBarHeight + 'px' }"
        hover-class="me-hero--hover"
        hover-stay-time="60"
        @click="goEdit"
      >
        <view class="me-hero__glow"></view>

        <view class="me-hero__top">
          <view class="me-hero__eyebrow">
            <view class="me-hero__dot"></view>
            <text>{{ $t('profile.myProfile') }}</text>
          </view>
          <view class="me-hero__edit">
            <UiIcon name="edit" :size="24" />
            <text>{{ $t('profile.editProfile') }}</text>
          </view>
        </view>

        <view class="me-hero__main">
          <view class="me-hero__avatar">
            <UiAvatar shape="square" :size="140" :src="userInfo.avatar" :name="displayName" />
          </view>
          <view class="me-hero__info">
            <text class="me-hero__name">{{ displayName }}</text>
            <text class="me-hero__signature">{{ displaySignature }}</text>
          </view>
        </view>

        <view class="me-hero__stats">
          <view v-for="stat in statItems" :key="stat.key" class="me-stat">
            <text class="me-stat__value">{{ stat.value }}</text>
            <text class="me-stat__label">{{ stat.label }}</text>
          </view>
        </view>

        <view class="me-hero__account">
          <UiIcon name="qr" :size="28" color="var(--text-tertiary)" />
          <text class="me-hero__account-text">{{ $t('profile.idLabel', { id: displayAccount }) }}</text>
          <UiIcon name="chevron-right" :size="24" color="var(--text-disabled)" />
        </view>
      </view>

      <view v-for="group in featureGroups" :key="group.key" class="me-section">
        <text class="section-label">{{ group.label }}</text>
        <view class="group-block">
          <view
            v-for="item in group.items"
            :key="item.key"
            class="row-item me-row"
            hover-class="row-item--hover"
            hover-stay-time="60"
            @click="onFeature(item)"
          >
            <view class="me-row__tile icon-tile" :class="'is-' + item.tone">
              <UiIcon :name="item.icon" :size="34" />
            </view>
            <view class="row-item__body">
              <text class="row-item__title">{{ item.title }}</text>
              <text class="row-item__desc">{{ item.desc }}</text>
            </view>
            <UiIcon name="chevron-right" :size="26" color="var(--text-disabled)" />
          </view>
        </view>
      </view>

      <view class="me-section">
        <view class="group-block">
          <view class="me-logout" hover-class="me-logout--hover" hover-stay-time="60" @click="handleLogout">
            <UiIcon name="logout" :size="32" color="var(--danger-color)" />
            <text>{{ $t('profile.logout') }}</text>
          </view>
        </view>
      </view>

      <view class="safe-bottom-space"></view>
    </scroll-view>

    <FloatingTabBar active="profile" />
  </view>
</template>

<script>
import UiAvatar from '../../components/Avatar.vue'
import UiIcon from '../../components/Icon.vue'
import FloatingTabBar from '../../components/FloatingTabBar.vue'
import { getUserInfo } from '../../utils/storage.js'
import { auth } from '../../utils/auth.js'
import { reset } from '../../store/index.js'

export default {
  components: { UiAvatar, UiIcon, FloatingTabBar },
  data() {
    return {
      statusBarHeight: 20,
      userInfo: {}
    }
  },
  computed: {
    displayName() {
      return (
        this.userInfo.nickname ||
        this.userInfo.username ||
        this.userInfo.account ||
        this.$t('common.notSet')
      )
    },
    displayAccount() {
      return this.userInfo.account || this.userInfo.id || '-'
    },
    displaySignature() {
      return this.userInfo.signature || this.$t('profile.noSignature')
    },
    genderText() {
      const g = this.userInfo.gender
      if (g === 'male' || g === '男') return this.$t('profile.genderMale')
      if (g === 'female' || g === '女') return this.$t('profile.genderFemale')
      if (g === 'hidden' || g === '不显示') return this.$t('profile.genderHidden')
      return this.$t('common.notSet')
    },
    statItems() {
      const info = this.userInfo || {}
      return [
        {
          key: 'experience',
          label: this.$t('profile.statExperience'),
          value: info.experience != null && info.experience !== '' ? String(info.experience) : '-'
        },
        {
          key: 'age',
          label: this.$t('profile.statAge'),
          value: info.age != null && info.age !== '' ? String(info.age) : '-'
        },
        {
          key: 'gender',
          label: this.$t('profile.statGender'),
          value: this.genderText
        }
      ]
    },
    featureGroups() {
      return [
        {
          key: 'content',
          label: this.$t('profile.content'),
          items: [
            {
              key: 'favorites',
              icon: 'star',
              tone: 'warning',
              title: this.$t('profile.favorites'),
              desc: this.$t('profile.favoritesDesc'),
              action: 'favorites'
            },
            {
              key: 'moments',
              icon: 'image',
              tone: 'info',
              title: this.$t('profile.moments'),
              desc: this.$t('profile.momentsDesc'),
              action: 'moments'
            }
          ]
        },
        {
          key: 'services',
          label: this.$t('profile.services'),
          items: [
            {
              key: 'service',
              icon: 'grid',
              tone: 'accent',
              title: this.$t('profile.service'),
              desc: this.$t('profile.serviceDesc'),
              action: 'dev'
            },
            {
              key: 'wallet',
              icon: 'shop',
              tone: 'success',
              title: this.$t('profile.wallet'),
              desc: this.$t('profile.walletDesc'),
              action: 'dev'
            },
            {
              key: 'stickers',
              icon: 'smile',
              tone: 'warning',
              title: this.$t('profile.stickers'),
              desc: this.$t('profile.stickersDesc'),
              action: 'dev'
            }
          ]
        },
        {
          key: 'general',
          label: this.$t('profile.general'),
          items: [
            {
              key: 'settings',
              icon: 'settings',
              tone: 'neutral',
              title: this.$t('profile.settings'),
              desc: this.$t('profile.settingsDesc'),
              action: 'settings'
            }
          ]
        }
      ]
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
    onFeature(item) {
      if (item.action === 'favorites') {
        this.goFavorites()
      } else if (item.action === 'moments') {
        this.goMoments()
      } else if (item.action === 'settings') {
        this.goSettings()
      } else {
        this.toastDev()
      }
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
      uni.showToast({ title: this.$t('common.comingSoon'), icon: 'none' })
    },
    handleLogout() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('profile.logoutConfirm'),
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
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.me-scroll {
  flex: 1;
  height: 0;
}

/* 顶部 Hero */
.me-hero {
  position: relative;
  margin: 16rpx 24rpx 0;
  padding: 0 32rpx 32rpx;
  border-radius: var(--radius-xl);
  background-color: var(--bg-primary);
  background-image: linear-gradient(155deg, var(--accent-light) 0%, transparent 62%);
  border: 1rpx solid var(--glass-border);
  box-shadow: var(--shadow-lg);
  transition: opacity var(--transition-fast);
}

.me-hero--hover {
  opacity: 0.96;
}

.me-hero__glow {
  position: absolute;
  top: -160rpx;
  right: -120rpx;
  width: 360rpx;
  height: 360rpx;
  border-radius: 50%;
  background-image: var(--accent-gradient);
  opacity: 0.16;
  filter: blur(30rpx);
  pointer-events: none;
}

.me-hero__top {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 26rpx;
}

.me-hero__eyebrow {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.me-hero__eyebrow text {
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

.me-hero__dot {
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background-color: var(--accent-color);
  box-shadow: 0 0 0 5rpx var(--accent-light);
}

.me-hero__edit {
  display: flex;
  align-items: center;
  gap: 8rpx;
  height: 52rpx;
  padding: 0 20rpx;
  border-radius: 999rpx;
  background-color: var(--accent-light);
  color: var(--accent-color);
}

.me-hero__edit text {
  font-size: var(--font-xs);
  font-weight: 600;
  color: var(--accent-color);
}

.me-hero__main {
  position: relative;
  display: flex;
  align-items: center;
  margin-top: 30rpx;
}

.me-hero__avatar {
  padding: 6rpx;
  border-radius: 32rpx;
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  flex-shrink: 0;
}

.me-hero__info {
  flex: 1;
  min-width: 0;
  margin-left: 26rpx;
}

.me-hero__name {
  display: block;
  font-size: var(--font-xl);
  font-weight: 700;
  letter-spacing: -0.4rpx;
  line-height: 1.25;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.me-hero__signature {
  display: -webkit-box;
  margin-top: 10rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
  line-height: 1.45;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.me-hero__stats {
  position: relative;
  display: flex;
  gap: 14rpx;
  margin-top: 28rpx;
}

.me-stat {
  flex: 1;
  padding: 16rpx 20rpx;
  border-radius: var(--radius-md);
  background-color: var(--bg-secondary);
  border: 1rpx solid var(--border-light);
}

.me-stat__value {
  display: block;
  font-size: var(--font-md);
  font-weight: 700;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.me-stat__label {
  display: block;
  margin-top: 4rpx;
  font-size: var(--font-xs);
  letter-spacing: 0.6rpx;
  color: var(--text-tertiary);
}

.me-hero__account {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 26rpx;
  padding-top: 22rpx;
  border-top: 1rpx solid var(--divider);
}

.me-hero__account-text {
  flex: 1;
  min-width: 0;
  font-size: var(--font-sm);
  color: var(--text-secondary);
  font-family: var(--font-mono);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 功能分组卡 */
.me-section {
  margin-top: 8rpx;
}

.me-section .section-label {
  display: block;
  padding: 30rpx 40rpx 14rpx;
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

.me-row::after {
  left: 128rpx;
  right: 32rpx;
}

.me-row__tile {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
}

.me-row__tile.is-accent {
  background-image: none;
  background-color: var(--accent-light);
  color: var(--accent-color);
}

.me-row__tile.is-info {
  background-image: none;
  background-color: var(--info-light);
  color: var(--info-color);
}

.me-row__tile.is-warning {
  background-image: none;
  background-color: var(--warning-light);
  color: var(--warning-color);
}

.me-row__tile.is-success {
  background-image: none;
  background-color: var(--success-light);
  color: var(--success-color);
}

.me-row__tile.is-neutral {
  background-image: none;
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
}

.me-logout {
  height: 104rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  transition: background-color var(--transition-fast);
}

.me-logout--hover {
  background-color: var(--danger-light);
}

.me-logout text {
  font-size: var(--font-md);
  color: var(--danger-color);
  font-weight: 500;
}
</style>
