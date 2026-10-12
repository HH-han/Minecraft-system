<template>
    <!-- 轮播图 -->
    <div class="banner">
      <div class="banner-content-SeamlessCarousel">
        <transition-group>
          <SeamlessCarousel :items="items" :interval="5000" :transitionDuration="700" aspectRatio="16/9" />
        </transition-group>
      </div>
      <!-- 搜索栏 -->
      <div class="homeview-hero-content">
        <h1 class="homeview-hero-title">探索世界的奇迹</h1>
        <p class="homeview-hero-subtitle">发现地球上最令人惊叹的地方，创造终生难忘的回忆</p>

        <div
          ref="searchBoxRef"
          class="hero-search"
          @focusin="handleFocusIn"
          @focusout="handleFocusOut"
        >
          <div class="hero-search__bar" :class="{ 'is-active': focused }">
            <input
              v-model="searchKeyword"
              class="hero-search__input"
              type="text"
              placeholder="搜索目的地 / 景点 / 酒店 / 攻略"
              autocomplete="off"
              @keydown.down.prevent="moveActive(1)"
              @keydown.up.prevent="moveActive(-1)"
              @keydown.enter.prevent="handleEnter"
              @keydown.esc="closeSuggestions"
            >
            <span class="hero-search__divider" aria-hidden="true"></span>
            <button class="hero-search__submit" type="button" @click="runSearch()">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <circle cx="11" cy="11" r="7" />
                <path d="M20 20l-3.6-3.6" />
              </svg>
              <span>搜索</span>
            </button>
          </div>

          <transition name="hero-search-panel">
            <ul v-if="showSuggestions && suggestions.length" class="hero-search__panel" role="listbox">
              <li
                v-for="(item, index) in suggestions"
                :key="item"
                class="hero-search__item"
                :class="{ 'is-active': index === activeIndex }"
                :style="{ '--i': index }"
                role="option"
                :aria-selected="index === activeIndex"
                @mousedown.prevent
                @mouseenter="activeIndex = index"
                @click="runSearch(item)"
              >
                <span class="hero-search__text">
                  <template v-for="(part, partIndex) in splitHighlight(item)" :key="partIndex">
                    <em v-if="part.match" class="hero-search__hl">{{ part.text }}</em>
                    <template v-else>{{ part.text }}</template>
                  </template>
                </span>
              </li>
            </ul>
          </transition>
        </div>
      </div>
    </div>
</template>
<script setup>
import SeamlessCarousel from './components/SeamlessCarousel.vue';
import { computed, ref, watch } from 'vue';

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['search']);

// 站点主题（世界旅行 / 景点 / 酒店 / 攻略）下的热门搜索词
const HOT_KEYWORDS = [
  '云南七日游攻略',
  '巴黎埃菲尔铁塔门票',
  '东京迪士尼乐园攻略',
  '冰岛极光最佳观赏时间',
  '四川九寨沟旅游路线',
  '三亚海景酒店推荐',
  '北京故宫预约',
  '西安兵马俑门票',
  '瑞士少女峰旅游',
  '马尔代夫海岛自由行',
  '成都美食攻略',
  '重庆火锅推荐',
  '土耳其热气球体验',
  '埃及金字塔旅游',
  '挪威峡湾自驾',
  '新疆喀纳斯旅游',
  '西藏布达拉宫',
  '希腊圣托里尼',
  '日本京都赏樱',
  '张家界玻璃栈道'
];

const searchKeyword = ref('');
const focused = ref(false);
const showSuggestions = ref(false);
const activeIndex = ref(-1);
const searchBoxRef = ref(null);

const suggestions = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase();
  const list = keyword
    ? HOT_KEYWORDS.filter((item) => item.toLowerCase().includes(keyword))
    : HOT_KEYWORDS;
  return list.slice(0, 8);
});

// 命中片段切片：避免使用 v-html，同时可以安全高亮关键词
const splitHighlight = (text) => {
  const keyword = searchKeyword.value.trim();
  if (!keyword) return [{ text, match: false }];

  const lowerText = text.toLowerCase();
  const lowerKeyword = keyword.toLowerCase();
  const parts = [];
  let cursor = 0;
  let index = lowerText.indexOf(lowerKeyword);

  while (index !== -1) {
    if (index > cursor) parts.push({ text: text.slice(cursor, index), match: false });
    parts.push({ text: text.slice(index, index + keyword.length), match: true });
    cursor = index + keyword.length;
    index = lowerText.indexOf(lowerKeyword, cursor);
  }

  if (cursor < text.length) parts.push({ text: text.slice(cursor), match: false });
  return parts.length ? parts : [{ text, match: false }];
};

const handleFocusIn = () => {
  focused.value = true;
  if (suggestions.value.length) showSuggestions.value = true;
};

const handleFocusOut = (event) => {
  if (searchBoxRef.value?.contains(event.relatedTarget)) return;
  focused.value = false;
  closeSuggestions();
};

const closeSuggestions = () => {
  showSuggestions.value = false;
  activeIndex.value = -1;
};

const moveActive = (step) => {
  const total = suggestions.value.length;
  if (!total) return;
  showSuggestions.value = true;
  activeIndex.value = (activeIndex.value + step + total) % total;
};

const runSearch = (keyword) => {
  const value = (keyword ?? searchKeyword.value).trim();
  if (!value) return;

  searchKeyword.value = value;
  closeSuggestions();
  emit('search', value);
  console.log('搜索关键词:', value);
};

const handleEnter = () => {
  if (!showSuggestions.value) return runSearch();

  const active = suggestions.value[activeIndex.value];
  runSearch(active ?? searchKeyword.value);
};

watch(searchKeyword, () => {
  activeIndex.value = -1;
  showSuggestions.value = focused.value && suggestions.value.length > 0;
});
</script>
<style scoped>
@import "@/css/Home/paging.css";
@import "@/css/Home/HomeViews.css";

/* ============================================================
   Hero 搜索框
   暗色胶囊搜索条 + 命中高亮的联想面板
   ============================================================ */
.hero-search {
  position: relative;
  width: min(640px, 100%);
  font-family: 'SF Pro Display', 'PingFang SC', 'HarmonyOS Sans SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

/* ---------- 搜索条 ---------- */
.hero-search__bar {
  display: flex;
  align-items: center;
  height: 58px;
  padding: 0 6px 0 22px;
  border-radius: 16px;
  background: var(--theme-bg-glass-strong);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-md);
  -webkit-backdrop-filter: blur(14px) saturate(1.4);
  backdrop-filter: blur(14px) saturate(1.4);
  transition: border-color 0.28s ease, background 0.28s ease, box-shadow 0.28s ease;
}

.hero-search__bar.is-active {
  background: var(--theme-bg-glass-strong);
  border-color: var(--theme-border-strong);
  box-shadow: var(--theme-shadow-lg), var(--theme-ring);
}

.hero-search__input {
  flex: 1 1 auto;
  min-width: 0;
  height: 100%;
  padding: 0;
  border: 0;
  outline: none;
  background: transparent;
  color: var(--theme-text-1);
  font-size: 16px;
  letter-spacing: 0.01em;
}

.hero-search__input::placeholder {
  color: var(--theme-text-3);
}

.hero-search__input:focus {
  outline: none;
  box-shadow: none;
}

.hero-search__divider {
  flex: none;
  width: 1px;
  height: 26px;
  background: var(--theme-hairline);
  transition: background 0.28s ease;
}

.hero-search__bar.is-active .hero-search__divider {
  background: var(--theme-border);
}

.hero-search__submit {
  flex: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 46px;
  margin-left: 6px;
  padding: 0 18px;
  border: 0;
  border-radius: 12px;
  background: transparent;
  color: var(--theme-text-1);
  font: inherit;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.04em;
  cursor: pointer;
  transition: background 0.24s ease, transform 0.24s ease;
}

.hero-search__submit svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
}

.hero-search__submit:hover {
  background: var(--theme-bg-hover);
}

.hero-search__submit:active {
  transform: scale(0.97);
}

.hero-search__submit:focus-visible {
  outline: 1px solid var(--theme-primary);
  outline-offset: 2px;
}

/* ---------- 联想面板 ---------- */
.hero-search__panel {
  position: absolute;
  top: calc(100% + 10px);
  left: 0;
  right: 0;
  z-index: 20;
  max-height: 348px;
  margin: 0;
  padding: 10px 0;
  overflow-y: auto;
  list-style: none;
  border-radius: 14px;
  background: var(--theme-bg-glass-strong);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-lg);
  -webkit-backdrop-filter: blur(18px);
  backdrop-filter: blur(18px);
}

.hero-search__panel::-webkit-scrollbar {
  width: 6px;
}

.hero-search__panel::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: var(--theme-scrollbar-thumb);
}

.hero-search__item {
  display: flex;
  align-items: center;
  padding: 11px 22px;
  color: var(--theme-text-2);
  font-size: 16px;
  line-height: 1.5;
  cursor: pointer;
  animation: heroSearchItemIn 0.34s cubic-bezier(0.22, 1, 0.36, 1) both;
  animation-delay: calc(var(--i, 0) * 34ms);
  transition: background 0.18s ease, color 0.18s ease;
}

.hero-search__item.is-active,
.hero-search__item:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

/* 命中关键词 */
.hero-search__hl {
  color: var(--theme-highlight);
  font-style: normal;
  font-weight: 500;
}

@keyframes heroSearchItemIn {
  from {
    opacity: 0;
    transform: translateY(-4px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-search-panel-enter-active,
.hero-search-panel-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.hero-search-panel-enter-from,
.hero-search-panel-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.985);
}

/* ---------- Hero 文案卡片（样式来源于 HomeViews.css，此处仅补过渡） ---------- */
.homeview-hero-content,
.homeview-hero-title,
.homeview-hero-subtitle {
  transition: background 0.32s ease, border-color 0.32s ease, box-shadow 0.32s ease,
    color 0.32s ease, text-shadow 0.32s ease;
}

/* ---------- 响应式 ---------- */
@media (max-width: 768px) {
  .hero-search__bar {
    height: 52px;
    padding-left: 18px;
  }

  .hero-search__input {
    font-size: 15px;
  }

  .hero-search__submit {
    height: 42px;
    padding: 0 14px;
    font-size: 15px;
  }

  .hero-search__item {
    padding: 10px 18px;
    font-size: 15px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .hero-search__item {
    animation: none;
  }
}
</style>
