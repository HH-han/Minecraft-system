<template>
  <view class="discover-page" :class="themeClass">
    <CustomNavBar
      :title="$t('tabs.discover')"
      :subtitle="$t('discover.subtitle')"
      title-align="left"
    />

    <scroll-view class="discover-scroll" scroll-y :show-scrollbar="false">
      <view class="discover-hero surface-card grid-overlay">
        <view class="discover-hero__glow"></view>
        <view class="discover-hero__main">
          <view class="discover-hero__text">
            <text class="discover-hero__title">{{ $t('discover.heroTitle') }}</text>
            <text class="discover-hero__desc">{{ $t('discover.heroDesc') }}</text>
            <view class="chip chip--accent discover-hero__chip">
              <view class="discover-hero__pulse"></view>
              <text>{{ $t('discover.featureCount', { count: featureCount }) }}</text>
            </view>
          </view>
          <view class="discover-hero__visual">
            <UiIcon name="compass" :size="80" />
          </view>
        </view>
      </view>

      <view v-for="section in sections" :key="section.key" class="discover-section">
        <text class="section-label">{{ section.label }}</text>

        <view class="discover-grid">
          <view
            v-for="item in section.items"
            :key="item.key"
            class="discover-card surface-card pressable"
            hover-class="discover-card--hover"
            hover-stay-time="60"
            @click="onTap(item)"
          >
            <view class="discover-card__top">
              <view class="discover-card__tile icon-tile" :class="'is-' + item.tone">
                <UiIcon :name="item.icon" :size="36" />
              </view>
              <UiIcon name="chevron-right" :size="26" color="var(--text-disabled)" />
            </view>
            <text class="discover-card__title">{{ item.title }}</text>
            <text class="discover-card__desc">{{ item.desc }}</text>
          </view>
        </view>
      </view>

      <view class="discover-foot">
        <UiIcon name="sparkles" :size="24" color="var(--text-tertiary)" />
        <text>{{ $t('discover.footerHint') }}</text>
      </view>

      <view class="safe-bottom-space"></view>
    </scroll-view>

    <FloatingTabBar active="discover" />
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import FloatingTabBar from '../../components/FloatingTabBar.vue'

export default {
  components: { CustomNavBar, UiIcon, FloatingTabBar },
  computed: {
    sections() {
      return [
        {
          key: 'social',
          label: this.$t('discover.social'),
          items: [
            {
              key: 'moments',
              icon: 'image',
              tone: 'accent',
              title: this.$t('discover.moments'),
              desc: this.$t('discover.momentsDesc'),
              action: 'moments'
            },
            {
              key: 'channels',
              icon: 'play',
              tone: 'info',
              title: this.$t('discover.channels'),
              desc: this.$t('discover.channelsDesc'),
              action: 'dev'
            }
          ]
        },
        {
          key: 'explore',
          label: this.$t('discover.explore'),
          items: [
            {
              key: 'nearby',
              icon: 'map-pin',
              tone: 'warning',
              title: this.$t('discover.nearby'),
              desc: this.$t('discover.nearbyDesc'),
              action: 'dev'
            },
            {
              key: 'scan',
              icon: 'scan',
              tone: 'accent',
              title: this.$t('discover.scan'),
              desc: this.$t('discover.scanDesc'),
              action: 'dev'
            },
            {
              key: 'shake',
              icon: 'refresh',
              tone: 'danger',
              title: this.$t('discover.shake'),
              desc: this.$t('discover.shakeDesc'),
              action: 'dev'
            },
            {
              key: 'games',
              icon: 'gamepad',
              tone: 'info',
              title: this.$t('discover.games'),
              desc: this.$t('discover.gamesDesc'),
              action: 'dev'
            }
          ]
        },
        {
          key: 'services',
          label: this.$t('discover.services'),
          items: [
            {
              key: 'shopping',
              icon: 'shop',
              tone: 'warning',
              title: this.$t('discover.shopping'),
              desc: this.$t('discover.shoppingDesc'),
              action: 'dev'
            },
            {
              key: 'miniPrograms',
              icon: 'grid',
              tone: 'neutral',
              title: this.$t('discover.miniPrograms'),
              desc: this.$t('discover.miniProgramsDesc'),
              action: 'dev'
            }
          ]
        }
      ]
    },
    featureCount() {
      return this.sections.reduce((sum, section) => sum + section.items.length, 0)
    }
  },
  methods: {
    onTap(item) {
      if (item.action === 'moments') {
        this.goMoments()
      } else {
        this.toastDev()
      }
    },
    goMoments() {
      uni.navigateTo({ url: '/pages/moments/moments' })
    },
    toastDev() {
      uni.showToast({ title: this.$t('common.comingSoon'), icon: 'none' })
    }
  }
}
</script>

<style scoped>
.discover-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.discover-scroll {
  flex: 1;
  height: 0;
}

.discover-hero {
  position: relative;
  margin: 16rpx 24rpx 0;
  padding: 32rpx;
  background-color: var(--bg-primary);
  background-image: linear-gradient(150deg, var(--accent-light) 0%, transparent 58%);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
}

.discover-hero__glow {
  position: absolute;
  top: -140rpx;
  right: -100rpx;
  width: 340rpx;
  height: 340rpx;
  border-radius: 50%;
  background-image: var(--accent-gradient);
  opacity: 0.18;
  filter: blur(30rpx);
  pointer-events: none;
}

.discover-hero__main {
  position: relative;
  display: flex;
  align-items: center;
}

.discover-hero__text {
  flex: 1;
  min-width: 0;
}

.discover-hero__title {
  display: block;
  font-size: var(--font-xl);
  font-weight: 700;
  letter-spacing: -0.4rpx;
  color: var(--text-primary);
}

.discover-hero__desc {
  display: block;
  margin-top: 10rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
  line-height: 1.5;
}

.discover-hero__chip {
  margin-top: 22rpx;
}

.discover-hero__pulse {
  width: 10rpx;
  height: 10rpx;
  margin-right: 10rpx;
  border-radius: 50%;
  background-color: var(--accent-color);
  box-shadow: 0 0 0 5rpx var(--accent-light);
}

.discover-hero__visual {
  width: 128rpx;
  height: 128rpx;
  margin-left: 20rpx;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--accent-color);
  background-color: var(--accent-light);
  border: 1rpx solid var(--glass-border);
}

.discover-section {
  margin-top: 8rpx;
}

.discover-section .section-label {
  display: block;
  padding: 30rpx 40rpx 14rpx;
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

.discover-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 0 24rpx;
}

.discover-card {
  width: calc(50% - 8rpx);
  box-sizing: border-box;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  border-radius: var(--radius-lg);
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
}

.discover-card--hover {
  background-color: var(--bg-hover);
}

.discover-card__top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.discover-card__tile {
  width: 84rpx;
  height: 84rpx;
  border-radius: 24rpx;
}

.discover-card__tile.is-accent {
  background-image: none;
  background-color: var(--accent-light);
  color: var(--accent-color);
}

.discover-card__tile.is-info {
  background-image: none;
  background-color: var(--info-light);
  color: var(--info-color);
}

.discover-card__tile.is-warning {
  background-image: none;
  background-color: var(--warning-light);
  color: var(--warning-color);
}

.discover-card__tile.is-danger {
  background-image: none;
  background-color: var(--danger-light);
  color: var(--danger-color);
}

.discover-card__tile.is-neutral {
  background-image: none;
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
}

.discover-card__title {
  display: block;
  margin-top: 22rpx;
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.3;
}

.discover-card__desc {
  display: block;
  margin-top: 6rpx;
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.discover-foot {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  padding: 44rpx 0 16rpx;
}

.discover-foot text {
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  letter-spacing: 0.6rpx;
}
</style>
