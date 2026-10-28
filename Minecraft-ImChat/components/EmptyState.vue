<template>
  <view class="ui-empty">
    <view class="ui-empty__visual" :class="{ 'is-icon': iconName }">
      <UiIcon v-if="iconName" :name="iconName" :size="72" color="var(--accent-color)" />
      <text v-else class="ui-empty__emoji">{{ icon }}</text>
    </view>
    <text class="ui-empty__text">{{ text }}</text>
    <view v-if="actionText" class="ui-empty__action" hover-class="ui-empty__action--hover" @click="$emit('action')">
      <text>{{ actionText }}</text>
    </view>
  </view>
</template>

<script>
import UiIcon from './Icon.vue'

/**
 * 统一空状态
 * iconName 优先使用线性图标；未传时兼容旧版 emoji 图标。
 */
export default {
  name: 'UiEmpty',
  components: { UiIcon },
  props: {
    icon: { type: String, default: '💬' },
    iconName: { type: String, default: '' },
    text: { type: String, default: '暂无内容' },
    actionText: { type: String, default: '' }
  },
  emits: ['action']
}
</script>

<style scoped>
.ui-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 60rpx;
}

.ui-empty__visual {
  width: 152rpx;
  height: 152rpx;
  margin-bottom: 32rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-tertiary);
}

.ui-empty__visual.is-icon {
  background-color: var(--accent-light);
  box-shadow: var(--shadow-sm);
}

.ui-empty__emoji {
  font-size: 96rpx;
  line-height: 1;
  opacity: 0.4;
}

.ui-empty__text {
  font-size: 28rpx;
  color: var(--text-secondary);
  text-align: center;
}

.ui-empty__action {
  margin-top: 44rpx;
  padding: 18rpx 56rpx;
  border-radius: 999rpx;
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.ui-empty__action--hover {
  transform: scale(0.96);
  opacity: 0.9;
}

.ui-empty__action text {
  color: var(--accent-contrast);
  font-size: 28rpx;
  font-weight: 600;
}
</style>
