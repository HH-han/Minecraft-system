<template>
  <view class="ui-empty">
    <text class="ui-empty__icon">{{ icon }}</text>
    <text class="ui-empty__text">{{ text }}</text>
    <view v-if="actionText" class="ui-empty__action" @click="$emit('action')">
      <text>{{ actionText }}</text>
    </view>
  </view>
</template>

<script>
/**
 * 统一空状态
 */
export default {
  name: 'UiEmpty',
  props: {
    icon: { type: String, default: '💬' },
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
  padding: 180rpx 60rpx;
}

.ui-empty__icon {
  font-size: 120rpx;
  line-height: 1;
  opacity: 0.35;
  margin-bottom: 32rpx;
}

.ui-empty__text {
  font-size: 28rpx;
  color: #999999;
}

.ui-empty__action {
  margin-top: 44rpx;
  padding: 18rpx 56rpx;
  background-color: #07c160;
  border-radius: 44rpx;
}

.ui-empty__action:active {
  background-color: #06ad56;
}

.ui-empty__action text {
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 500;
}
</style>
