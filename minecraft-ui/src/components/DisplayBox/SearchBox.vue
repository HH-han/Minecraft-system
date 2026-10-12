<template>
  <div class="search-box">
    <div ref="shellRef" class="search-shell" :class="{ 'is-focused': isFocused }" @focusin="handleFocusIn"
      @focusout="handleFocusOut">
      <!-- 搜索条 -->
      <div class="search-bar">
        <input ref="inputRef" class="search-field" type="text" autocomplete="off" :placeholder="placeholder"
          :value="modelValue" @input="onInput" @keydown.down.prevent="moveActive(1)"
          @keydown.up.prevent="moveActive(-1)" @keydown.enter.prevent="handleEnter" @keydown.esc="handleEsc" />

        <!-- 清空 -->
        <button v-if="modelValue" class="search-clear" type="button" aria-label="清空搜索" @click="clearInput">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M6 6l12 12M18 6L6 18" />
          </svg>
        </button>

        <span class="search-divider" aria-hidden="true"></span>

        <!-- 提交 -->
        <button class="search-submit" type="button" @click="submit()">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="11" cy="11" r="7" />
            <path d="M20 20l-3.6-3.6" />
          </svg>
          <span>搜索</span>
        </button>
      </div>

      <!-- 联想面板 -->
      <transition name="search-panel">
        <ul v-if="isPanelOpen && panelItems.length" class="search-panel" role="listbox">
          <li v-for="(item, index) in panelItems" :key="item" class="search-panel-item"
            :class="{ 'is-active': index === activeIndex }" :style="{ '--i': index }" role="option"
            :aria-selected="index === activeIndex" @mousedown.prevent @mouseenter="activeIndex = index"
            @click="choose(item)">
            <span class="search-panel-text">
              <template v-for="(part, partIndex) in splitHighlight(item)" :key="partIndex">
                <em v-if="part.match" class="search-panel-hl">{{ part.text }}</em>
                <template v-else>{{ part.text }}</template>
              </template>
            </span>
          </li>
        </ul>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue';

// 未传入 suggestions 时的默认联想词
const DEFAULT_SUGGESTIONS = [
  '三亚海景酒店',
  '北京故宫门票',
  '成都美食攻略',
  '上海迪士尼门票',
  '丽江古城客栈',
  '云南七日游攻略',
  '特价机票查询',
  '杭州西湖民宿',
  '西安兵马俑门票',
  '重庆火锅推荐',
  '海南亲子游',
  '北海道滑雪攻略'
];

// Props 定义
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '搜索...'
  },
  // 可选：由父组件传入联想词，未传时使用内置热门词（见 DEFAULT_SUGGESTIONS）
  suggestions: {
    type: Array,
    default: () => []
  }
});

// Emits 定义
const emit = defineEmits(['update:modelValue', 'search', 'clear']);

// 状态
const isFocused = ref(false);
const isPanelOpen = ref(false);
const activeIndex = ref(-1);
const inputRef = ref(null);
const shellRef = ref(null);

const keyword = computed(() => props.modelValue.trim());

// 联想词来源：父组件传入优先，其次使用内置热门词
const suggestionSource = computed(() => {
  const passed = props.suggestions;
  return Array.isArray(passed) && passed.length ? passed : DEFAULT_SUGGESTIONS;
});

// 联想词：空输入时展示热门词，输入后按命中过滤
const panelItems = computed(() => {
  const list = suggestionSource.value;
  if (!keyword.value) return list.slice(0, 8);

  const lowerKeyword = keyword.value.toLowerCase();
  return list
    .filter((item) => String(item).toLowerCase().includes(lowerKeyword))
    .slice(0, 8);
});

// 命中片段切片：避免使用 v-html，同时安全高亮关键词
const splitHighlight = (text) => {
  const value = String(text ?? '');
  if (!keyword.value) return [{ text: value, match: false }];

  const lowerText = value.toLowerCase();
  const lowerKeyword = keyword.value.toLowerCase();
  const parts = [];
  let cursor = 0;
  let index = lowerText.indexOf(lowerKeyword);

  while (index !== -1) {
    if (index > cursor) parts.push({ text: value.slice(cursor, index), match: false });
    parts.push({ text: value.slice(index, index + keyword.value.length), match: true });
    cursor = index + keyword.value.length;
    index = lowerText.indexOf(lowerKeyword, cursor);
  }

  if (cursor < value.length) parts.push({ text: value.slice(cursor), match: false });
  return parts.length ? parts : [{ text: value, match: false }];
};

const openPanel = () => {
  isPanelOpen.value = panelItems.value.length > 0;
};

const closePanel = () => {
  isPanelOpen.value = false;
  activeIndex.value = -1;
};

const handleFocusIn = () => {
  isFocused.value = true;
  openPanel();
};

const handleFocusOut = (event) => {
  if (shellRef.value?.contains(event.relatedTarget)) return;
  isFocused.value = false;
  closePanel();
};

// 键盘上下键在联想词间移动
const moveActive = (step) => {
  const total = panelItems.value.length;
  if (!total) return;

  isPanelOpen.value = true;
  activeIndex.value = (activeIndex.value + step + total) % total;
};

// 输入处理
const onInput = (event) => {
  emit('update:modelValue', event.target.value);
};

// 提交搜索（点击搜索按钮 / 回车 / 选中联想词）
const submit = (value) => {
  const next = String(value ?? props.modelValue).trim();
  if (!next) return;

  emit('update:modelValue', next);
  closePanel();
  emit('search', next);
};

const choose = (item) => submit(item);

// 清空输入
const clearInput = () => {
  emit('update:modelValue', '');
  emit('clear');
  // 清空后保持焦点
  nextTick(() => {
    inputRef.value?.focus();
  });
};

// ESC：优先收起联想面板，其次清空
const handleEsc = () => {
  if (isPanelOpen.value) return closePanel();
  if (props.modelValue) clearInput();
};

// 回车：优先提交高亮项，否则提交当前输入
const handleEnter = () => {
  const active = isPanelOpen.value ? panelItems.value[activeIndex.value] : null;
  submit(active ?? props.modelValue);
};

watch(
  () => props.modelValue,
  () => {
    activeIndex.value = -1;
    if (isFocused.value) openPanel();
  }
);

// 暴露焦点方法（可选，供父组件调用）
defineExpose({
  focus: () => {
    inputRef.value?.focus();
  }
});
</script>

<style scoped>
/* ============================================================
   搜索框
   主题变量随 html[data-theme] 切换（light / dark / game）
   ============================================================ */
.search-box {
  /* 私有变量统一别名到全局主题令牌，随 html[data-theme] 自动切换 */
  --sb-bg: var(--theme-bg-glass);
  --sb-bg-focus: var(--theme-bg-glass-strong);
  --sb-border: var(--theme-border);
  --sb-border-focus: var(--theme-border-strong);
  --sb-shadow: var(--theme-shadow-md);
  --sb-shadow-focus: var(--theme-shadow-lg);
  --sb-hairline: linear-gradient(90deg, transparent, var(--theme-border), transparent);
  --sb-text: var(--theme-text-1);
  --sb-placeholder: var(--theme-text-3);
  --sb-divider: var(--theme-hairline);
  --sb-action: var(--theme-text-1);
  --sb-action-hover: var(--theme-bg-hover);
  --sb-action-ring: var(--theme-border-strong);
  --sb-clear-bg: var(--theme-bg-hover);
  --sb-clear-bg-hover: var(--theme-bg-active);
  --sb-clear-icon: var(--theme-text-2);
  --sb-panel-bg: var(--theme-bg-glass-strong);
  --sb-panel-border: var(--theme-border);
  --sb-panel-shadow: var(--theme-shadow-lg);
  --sb-item-text: var(--theme-text-2);
  --sb-item-hover-bg: var(--theme-bg-hover);
  --sb-item-hover-text: var(--theme-text-1);
  --sb-scrollbar: var(--theme-scrollbar-thumb);
  --sb-hl: var(--theme-highlight);

  display: flex;
  justify-content: center;
  padding: 26px 20px;
}

.search-shell {
  position: relative;
  width: 100%;
  max-width: 640px;
  font-family: 'SF Pro Display', 'PingFang SC', 'HarmonyOS Sans SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

/* ========== 搜索条 ========== */
.search-bar {
  position: relative;
  display: flex;
  align-items: center;
  height: 58px;
  padding: 0 6px 0 22px;
  border: 1px solid var(--sb-border);
  border-radius: 16px;
  background: var(--sb-bg);
  box-shadow: var(--sb-shadow);
  transition: border-color 0.28s ease, box-shadow 0.28s ease, background 0.28s ease;
  box-sizing: border-box;
}

/* 顶部强调细线 */
.search-bar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 12%;
  right: 12%;
  height: 1px;
  pointer-events: none;
  background: var(--sb-hairline);
}

.search-shell.is-focused .search-bar {
  background: var(--sb-bg-focus);
  border-color: var(--sb-border-focus);
  box-shadow: var(--sb-shadow-focus);
}

.search-field {
  flex: 1 1 auto;
  min-width: 0;
  height: 100%;
  padding: 0;
  border: none;
  outline: none;
  background: transparent;
  color: var(--sb-text);
  font-size: 16px;
  font-weight: 400;
  line-height: 1.5;
  letter-spacing: 0.01em;
}

.search-field::placeholder {
  color: var(--sb-placeholder);
  font-weight: 300;
  letter-spacing: 0.3px;
}

.search-field:focus {
  outline: none;
  box-shadow: none;
}

.search-divider {
  flex: none;
  width: 1px;
  height: 26px;
  background: var(--sb-divider);
  transition: background 0.28s ease;
}

/* 清空按钮 */
.search-clear {
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  margin-right: 12px;
  padding: 0;
  border: none;
  border-radius: 50%;
  background: var(--sb-clear-bg);
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
}

.search-clear svg {
  width: 13px;
  height: 13px;
  fill: none;
  stroke: var(--sb-clear-icon);
  stroke-width: 2.2;
  stroke-linecap: round;
}

.search-clear:hover {
  background: var(--sb-clear-bg-hover);
  transform: scale(1.08);
}

.search-clear:active {
  transform: scale(0.94);
}

/* 提交按钮 */
.search-submit {
  flex: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 46px;
  margin-left: 6px;
  padding: 0 18px;
  border: none;
  border-radius: 12px;
  background: transparent;
  color: var(--sb-action);
  font-family: inherit;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.04em;
  cursor: pointer;
  transition: background 0.24s ease, transform 0.24s ease;
}

.search-submit svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
}

.search-submit:hover {
  background: var(--sb-action-hover);
}

.search-submit:active {
  transform: scale(0.97);
}

.search-submit:focus-visible {
  outline: 1px solid var(--sb-action-ring);
  outline-offset: 2px;
}

/* ========== 联想面板 ========== */
.search-panel {
  position: absolute;
  top: calc(100% + 10px);
  left: 0;
  right: 0;
  z-index: 20;
  max-height: 340px;
  margin: 0;
  padding: 10px 0;
  overflow-y: auto;
  list-style: none;
  border: 1px solid var(--sb-panel-border);
  border-radius: 14px;
  background: var(--sb-panel-bg);
  box-shadow: var(--sb-panel-shadow);
  -webkit-backdrop-filter: blur(18px);
  backdrop-filter: blur(18px);
  transition: background 0.32s ease, border-color 0.32s ease, box-shadow 0.32s ease;
}

.search-panel::-webkit-scrollbar {
  width: 6px;
}

.search-panel::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: var(--sb-scrollbar);
}

.search-panel-item {
  display: flex;
  align-items: center;
  padding: 11px 22px;
  color: var(--sb-item-text);
  font-size: 16px;
  line-height: 1.5;
  cursor: pointer;
  animation: searchItemIn 0.34s cubic-bezier(0.22, 1, 0.36, 1) both;
  animation-delay: calc(var(--i, 0) * 34ms);
  transition: background 0.18s ease, color 0.18s ease;
}

.search-panel-item.is-active,
.search-panel-item:hover {
  background: var(--sb-item-hover-bg);
  color: var(--sb-item-hover-text);
}

.search-panel-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 命中关键词 */
.search-panel-hl {
  color: var(--sb-hl);
  font-style: normal;
  font-weight: 500;
}

@keyframes searchItemIn {
  from {
    opacity: 0;
    transform: translateY(-4px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-panel-enter-active,
.search-panel-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.search-panel-enter-from,
.search-panel-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.985);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .search-box {
    padding: 20px 16px;
  }

  .search-bar {
    height: 52px;
    padding-left: 18px;
  }

  .search-field {
    font-size: 15px;
  }

  .search-submit {
    height: 42px;
    padding: 0 14px;
    font-size: 15px;
  }

  .search-panel-item {
    padding: 10px 18px;
    font-size: 15px;
  }
}

@media (prefers-reduced-motion: reduce) {

  .search-bar,
  .search-clear,
  .search-submit,
  .search-panel-item {
    transition: none;
  }

  .search-panel-item {
    animation: none;
  }
}
</style>
