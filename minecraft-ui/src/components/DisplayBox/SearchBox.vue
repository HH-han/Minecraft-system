<template>
  <div class="search-box-wrapper">
    <div class="search-container" :class="{ 'focused': isFocused }">
      <div class="search-glass">
        <!-- 搜索图标 -->
        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24"
          fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>

        <!-- 输入框 -->
        <input ref="inputRef" type="text" class="search-input" :placeholder="placeholder" :value="modelValue"
          @input="onInput" @focus="isFocused = true" @blur="isFocused = false" @keydown.esc="handleEsc" />

        <!-- 清空按钮（有文字时显示） -->
        <button v-if="modelValue" class="clear-button" @click="clearInput" aria-label="清空搜索">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>

        <!-- 装饰性的光晕和微交互元素 -->
        <div class="glass-highlight"></div>
      </div>

      <!-- 可选的搜索提示（仅在聚焦且有输入时展示，模拟搜索建议） -->
      <transition name="fade">
        <div v-if="isFocused && modelValue" class="search-hint">
          <div class="hint-item" v-for="i in 3" :key="i">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            <span>{{ modelValue }} 相关建议 {{ i }}</span>
          </div>
        </div>
      </transition>
    </div>
  </div>

</template>

<script setup>
import { ref, defineProps, defineEmits, nextTick } from 'vue';

// Props 定义
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '搜索...'
  }
});

// Emits 定义
const emit = defineEmits(['update:modelValue', 'search', 'clear']);

// 状态
const isFocused = ref(false);
const inputRef = ref(null);

// 输入处理
const onInput = (event) => {
  emit('update:modelValue', event.target.value);
};

// 清空输入
const clearInput = () => {
  emit('update:modelValue', '');
  emit('clear');
  // 清空后保持焦点
  nextTick(() => {
    inputRef.value?.focus();
  });
};

// ESC 键清空
const handleEsc = () => {
  if (props.modelValue) {
    clearInput();
  }
};

// 暴露焦点方法（可选，供父组件调用）
defineExpose({
  focus: () => {
    inputRef.value?.focus();
  }
});
</script>

<style scoped>
.search-box-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px;
}
/* 容器样式 - 提供适当的间距和定位上下文 */
.search-container {
  position: relative;
  width: 100%;
  max-width: 400px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  transition: transform 0.2s ease;
}

.search-container.focused {
  transform: scale(1.02);
}

/* 玻璃拟态主体 */
.search-glass {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  height: 56px;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(12px) saturate(180%);
  -webkit-backdrop-filter: blur(12px) saturate(180%);
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.05), inset 0 1px 2px rgba(255, 255, 255, 0.6);
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.3, 1.1);
  box-sizing: border-box;
}

/* 聚焦状态下的玻璃效果增强 */
.focused .search-glass {
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(255, 255, 255, 0.8);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15), 0 0 0 2px rgba(255, 255, 255, 0.3);
}

/* 搜索图标 */
.search-icon {
  flex-shrink: 0;
  color: rgba(60, 60, 70, 0.7);
  margin-right: 12px;
  transition: color 0.2s;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.1));
}

.focused .search-icon {
  color: rgba(30, 30, 40, 0.9);
}

/* 输入框 */
.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  font-size: 16px;
  font-weight: 400;
  color: #1e1e2a;
  padding: 0;
  margin: 0;
  line-height: 1.5;
  min-width: 0;
  /* 防止flex溢出 */
}

.search-input::placeholder {
  color: rgba(60, 60, 70, 0.5);
  font-weight: 300;
  letter-spacing: 0.3px;
  transition: color 0.2s;
}

.focused .search-input::placeholder {
  color: rgba(40, 40, 50, 0.6);
}

/* 清空按钮 */
.clear-button {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  border-radius: 50%;
  color: rgba(40, 40, 50, 0.7);
  cursor: pointer;
  padding: 0;
  margin-left: 8px;
  transition: all 0.2s;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.clear-button:hover {
  background: rgba(0, 0, 0, 0.2);
  color: #1e1e2a;
  transform: scale(1.1);
  border-color: rgba(255, 255, 255, 0.6);
}

.clear-button:active {
  transform: scale(0.95);
}

/* 玻璃高光装饰 */
.glass-highlight {
  position: absolute;
  top: 0;
  left: 10%;
  width: 80%;
  height: 30%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.5) 0%, rgba(255, 255, 255, 0) 60%);
  border-radius: 28px 28px 0 0;
  pointer-events: none;
  opacity: 0.6;
  transition: opacity 0.3s;
}

.focused .glass-highlight {
  opacity: 1;
}

/* 搜索提示下拉框 */
.search-hint {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  padding: 8px 0;
  z-index: 10;
  overflow: hidden;
  animation: hintAppear 0.2s ease;
}

.hint-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: #2c2c3a;
  font-size: 14px;
  transition: background 0.15s;
  cursor: default;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.hint-item:last-child {
  border-bottom: none;
}

.hint-item:hover {
  background: rgba(255, 255, 255, 0.3);
}

.hint-item svg {
  color: rgba(60, 60, 70, 0.7);
  flex-shrink: 0;
}

/* 简单动画 */
@keyframes hintAppear {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* 暗色背景适配（如果需要在暗色背景上使用，文字颜色会自动适应，但为了演示效果，我们保留亮色风格） */
/* 如果需要强制适配深色模式，可以添加媒体查询 */
@media (prefers-color-scheme: dark) {
  .search-glass {
    background: rgba(30, 30, 40, 0.4);
    border-color: rgba(255, 255, 255, 0.2);
  }

  .search-input {
    color: #f0f0f0;
  }

  .search-input::placeholder {
    color: rgba(220, 220, 240, 0.5);
  }

  .search-icon {
    color: rgba(220, 220, 240, 0.6);
  }
}
</style>