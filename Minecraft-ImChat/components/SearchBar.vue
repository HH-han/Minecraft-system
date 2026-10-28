<template>
  <view class="search-bar">
    <view class="search-bar__field">
      <UiIcon name="search" :size="32" color="var(--text-tertiary)" />
      <input
        class="search-bar__input"
        :value="modelValue"
        :placeholder="placeholder || $t('common.search')"
        placeholder-class="search-bar__placeholder"
        confirm-type="search"
        :focus="autofocus"
        @input="onInput"
        @confirm="$emit('confirm', $event)"
        @focus="$emit('focus')"
      />
      <view v-if="modelValue" class="search-bar__clear" @click="clear">
        <UiIcon name="close" :size="20" color="var(--bg-primary)" />
      </view>
    </view>
    <text v-if="showCancel" class="search-bar__cancel" @click="$emit('cancel')">
      {{ cancelText || $t('common.cancel') }}
    </text>
  </view>
</template>

<script>
import UiIcon from './Icon.vue'

/**
 * 通用搜索条
 */
export default {
  name: 'SearchBar',
  components: { UiIcon },
  props: {
    modelValue: { type: String, default: '' },
    placeholder: { type: String, default: '' },
    showCancel: { type: Boolean, default: false },
    cancelText: { type: String, default: '' },
    autofocus: { type: Boolean, default: false }
  },
  emits: ['update:modelValue', 'confirm', 'focus', 'cancel'],
  methods: {
    onInput(e) {
      this.$emit('update:modelValue', e.detail.value)
    },
    clear() {
      this.$emit('update:modelValue', '')
    }
  }
}
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.search-bar__field {
  flex: 1;
  display: flex;
  align-items: center;
  height: 72rpx;
  padding: 0 24rpx;
  border-radius: 999rpx;
  background-color: var(--bg-input);
  border: 1rpx solid var(--border-light);
  transition: border-color var(--transition-fast), background-color var(--transition-fast);
}

.search-bar__field:focus-within {
  border-color: var(--accent-color);
  background-color: var(--bg-primary);
}

.search-bar__input {
  flex: 1;
  margin-left: 12rpx;
  font-size: 28rpx;
  color: var(--text-primary);
}

.search-bar__placeholder {
  color: var(--text-placeholder);
}

.search-bar__clear {
  width: 34rpx;
  height: 34rpx;
  border-radius: 50%;
  background-color: var(--text-disabled);
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-bar__cancel {
  font-size: 28rpx;
  color: var(--accent-color);
  font-weight: 500;
  padding: 0 4rpx;
}
</style>
