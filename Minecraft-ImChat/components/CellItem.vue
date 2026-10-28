<template>
  <view
    class="ui-cell"
    :class="{ 'is-clickable': clickable, 'no-border': !border }"
    :hover-class="clickable ? 'ui-cell--hover' : 'none'"
    :hover-stay-time="60"
    @click="onClick"
  >
    <view v-if="icon || $slots.icon" class="ui-cell__icon" :style="iconStyle">
      <slot name="icon">
        <text class="ui-cell__icon-text">{{ icon }}</text>
      </slot>
    </view>

    <view class="ui-cell__body">
      <view class="ui-cell__title-row">
        <text class="ui-cell__title">{{ title }}</text>
        <slot name="title-extra"></slot>
      </view>
      <text v-if="subtitle" class="ui-cell__subtitle">{{ subtitle }}</text>
    </view>

    <view class="ui-cell__right">
      <slot name="right">
        <text v-if="value" class="ui-cell__value">{{ value }}</text>
      </slot>
      <UiIcon
        v-if="arrow"
        class="ui-cell__arrow"
        name="chevron-right"
        :size="26"
        color="var(--text-disabled)"
      />
    </view>
  </view>
</template>

<script>
import UiIcon from './Icon.vue'

/**
 * 通用列表行
 * 颜色全部走主题变量，图标默认使用品牌渐变底。
 */
export default {
  name: 'UiCell',
  components: { UiIcon },
  props: {
    title: { type: String, default: '' },
    subtitle: { type: String, default: '' },
    value: { type: String, default: '' },
    icon: { type: String, default: '' },
    iconBg: { type: String, default: '' },
    arrow: { type: Boolean, default: false },
    clickable: { type: Boolean, default: true },
    border: { type: Boolean, default: true }
  },
  emits: ['click'],
  computed: {
    iconStyle() {
      return this.iconBg ? { backgroundImage: 'none', backgroundColor: this.iconBg } : {}
    }
  },
  methods: {
    onClick(e) {
      this.$emit('click', e)
    }
  }
}
</script>

<style scoped>
.ui-cell {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  background-color: transparent;
  position: relative;
}

.ui-cell::after {
  content: '';
  position: absolute;
  left: 128rpx;
  right: 32rpx;
  bottom: 0;
  height: 1rpx;
  background-color: var(--divider);
}

.ui-cell.no-border::after {
  display: none;
}

.ui-cell--hover {
  background-color: var(--bg-hover);
}

.ui-cell__icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  flex-shrink: 0;
  background-image: var(--accent-gradient);
  box-shadow: var(--shadow-sm);
}

.ui-cell__icon-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
  line-height: 1;
}

.ui-cell__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.ui-cell__title-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.ui-cell__title {
  font-size: 30rpx;
  color: var(--text-primary);
  line-height: 1.4;
}

.ui-cell__subtitle {
  font-size: 24rpx;
  color: var(--text-secondary);
  margin-top: 6rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ui-cell__right {
  display: flex;
  align-items: center;
  margin-left: 16rpx;
  flex-shrink: 0;
  max-width: 60%;
}

.ui-cell__value {
  font-size: 26rpx;
  color: var(--text-tertiary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ui-cell__arrow {
  margin-left: 8rpx;
}
</style>
