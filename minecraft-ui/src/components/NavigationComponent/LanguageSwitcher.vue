<template>
  <div ref="rootRef" class="language-switcher">
    <!-- 触发按钮 -->
    <button type="button" class="ls-trigger" :class="{ 'is-open': visible }" :title="$t('common.language')"
      @click.stop="visible = !visible">
      <svg class="ls-globe" viewBox="0 0 1024 1024" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="512" cy="512" r="400" stroke="currentColor" stroke-width="64" />
        <ellipse cx="512" cy="512" rx="190" ry="400" stroke="currentColor" stroke-width="64" />
        <path d="M136 392h752M136 632h752" stroke="currentColor" stroke-width="64" stroke-linecap="round" />
      </svg>
      <span class="ls-label">{{ currentLabel }}</span>
      <svg class="ls-arrow" :class="{ 'is-open': visible }" viewBox="0 0 1024 1024" fill="currentColor"
        xmlns="http://www.w3.org/2000/svg">
        <path
          d="M512 674.6c-12.8 0-25.6-4.9-35.4-14.6L182 365.4c-19.5-19.5-19.5-51.2 0-70.7 19.5-19.5 51.2-19.5 70.7 0L512 554l259.4-259.4c19.5-19.5 51.2-19.5 70.7 0 19.5 19.5 19.5 51.2 0 70.7L547.4 660c-9.8 9.8-22.6 14.6-35.4 14.6z" />
      </svg>
    </button>

    <!-- 自定义下拉面板 -->
    <Transition name="ls-pop">
      <ul v-if="visible" class="ls-menu" role="listbox" @click.stop>
        <li v-for="item in SUPPORTED_LOCALES" :key="item.value" role="option"
          :aria-selected="item.value === locale" class="ls-item" :class="{ 'is-active': item.value === locale }"
          @click="handleSelect(item.value)">
          <span class="ls-check">
            <svg v-if="item.value === locale" viewBox="0 0 1024 1024" fill="currentColor"
              xmlns="http://www.w3.org/2000/svg">
              <path
                d="M406.6 749.2c-12.8 0-25.6-4.9-35.3-14.6L205.1 568.3c-19.5-19.5-19.5-51.2 0-70.7 19.5-19.5 51.2-19.5 70.7 0l130.8 130.8 312.5-312.6c19.5-19.5 51.2-19.5 70.7 0 19.5 19.5 19.5 51.2 0 70.7L441.9 734.6c-9.7 9.8-22.5 14.6-35.3 14.6z" />
            </svg>
          </span>
          <span>{{ item.label }}</span>
        </li>
      </ul>
    </Transition>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { SUPPORTED_LOCALES, setLocale } from '@/locales';

const { locale } = useI18n();
const visible = ref(false);
const rootRef = ref(null);

const currentLabel = computed(
  () => SUPPORTED_LOCALES.find((l) => l.value === locale.value)?.label ?? locale.value
);

const handleSelect = (lang) => {
  visible.value = false;
  if (lang !== locale.value) setLocale(lang);
};

/** 点击组件外部时收起面板 */
const onClickOutside = (e) => {
  if (rootRef.value && !rootRef.value.contains(e.target)) visible.value = false;
};
/** Esc 键收起 */
const onKeydown = (e) => {
  if (e.key === 'Escape') visible.value = false;
};

onMounted(() => {
  document.addEventListener('click', onClickOutside);
  document.addEventListener('keydown', onKeydown);
});
onBeforeUnmount(() => {
  document.removeEventListener('click', onClickOutside);
  document.removeEventListener('keydown', onKeydown);
});
</script>

<style scoped>
.language-switcher {
  position: relative;
  display: inline-flex;
}

/* ===== 触发按钮：白色实体胶囊（浅色/深色导航栏均清晰可见） ===== */
.ls-trigger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.95);
  color: #334155;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
  white-space: nowrap;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: background 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease, transform 0.25s ease, color 0.25s ease;
}

.ls-trigger:hover {
  background: #ffffff;
  border-color: rgba(0, 162, 255, 0.4);
  color: #00a2ff;
  box-shadow: 0 4px 14px rgba(0, 162, 255, 0.18);
  transform: translateY(-1px);
}

.ls-trigger:active {
  transform: translateY(0);
}

.ls-trigger.is-open {
  background: #ffffff;
  border-color: rgba(0, 162, 255, 0.5);
  color: #00a2ff;
  box-shadow: 0 4px 14px rgba(0, 162, 255, 0.2);
}

/* 地球图标：品牌蓝，hover 时旋转，呼应「全球语言」语义 */
.ls-globe {
  width: 17px;
  height: 17px;
  flex-shrink: 0;
  color: #00a2ff;
  transition: transform 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.ls-trigger:hover .ls-globe {
  transform: rotate(180deg);
}

.ls-label {
  line-height: 1;
}

/* 下拉箭头：展开时翻转 */
.ls-arrow {
  width: 11px;
  height: 11px;
  flex-shrink: 0;
  opacity: 0.6;
  transition: transform 0.3s ease;
}

.ls-arrow.is-open {
  transform: rotate(180deg);
}

/* ===== 下拉面板 ===== */
.ls-menu {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  z-index: 1000;
  margin: 0;
  padding: 6px;
  min-width: 148px;
  list-style: none;
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.16), 0 2px 8px rgba(0, 0, 0, 0.08);
  transform-origin: top right;
}

.ls-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 12px;
  border-radius: 9px;
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #303133;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.ls-item:hover {
  background: rgba(0, 162, 255, 0.1);
  color: #00a2ff;
}

/* 当前语言高亮 */
.ls-item.is-active {
  color: #00a2ff;
  font-weight: 600;
  background: rgba(0, 162, 255, 0.12);
}

/* 勾选标记：未选中项保留占位对齐 */
.ls-check {
  display: inline-flex;
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.ls-check svg {
  width: 14px;
  height: 14px;
}

/* ===== 展开/收起动画 ===== */
.ls-pop-enter-active,
.ls-pop-leave-active {
  transition: opacity 0.2s ease, transform 0.2s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.ls-pop-enter-from,
.ls-pop-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.96);
}
</style>
