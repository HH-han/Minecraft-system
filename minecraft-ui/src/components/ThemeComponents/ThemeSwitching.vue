<template>
    <div
        class="theme-switch"
        role="group"
        :aria-label="`主题切换（当前：${currentName}）`"
    >
        <!-- 滑动指示块：随当前主题平移，激活色取各主题主色 -->
        <span class="theme-switch__thumb" :style="{ '--ts-index': activeIndex }"></span>

        <button
            v-for="item in themes"
            :key="item.id"
            type="button"
            class="theme-switch__option"
            :class="{ 'is-active': item.id === currentTheme }"
            :title="item.id === currentTheme ? `${item.name}（当前）` : `切换到${item.name}`"
            :aria-label="item.name"
            :aria-pressed="item.id === currentTheme"
            @click="handleSelect(item.id)"
        >
            <!-- 亮色：太阳 -->
            <svg
                v-if="item.id === 'light'"
                class="theme-switch__icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
            >
                <circle cx="12" cy="12" r="4" />
                <path
                    d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"
                />
            </svg>
            <!-- 暗色：月亮 -->
            <svg
                v-else-if="item.id === 'dark'"
                class="theme-switch__icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
            >
                <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
            </svg>
            <!-- 游戏：手柄 -->
            <svg
                v-else
                class="theme-switch__icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
            >
                <path
                    d="M6 11h4M8 9v4M15 12h.01M18 10h.01M17.32 5H6.68a4 4 0 0 0-3.978 3.59c-.006.052-.01.101-.017.152C2.604 9.416 2 14.456 2 16a3 3 0 0 0 3 3c1 0 1.5-.5 2-1l1.414-1.414A2 2 0 0 1 9.828 16h4.344a2 2 0 0 1 1.414.586L17 18c.5.5 1 1 2 1a3 3 0 0 0 3-3c0-1.545-.604-6.584-.685-7.258-.007-.05-.011-.1-.017-.151A4 4 0 0 0 17.32 5Z"
                />
            </svg>
        </button>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useThemeStore } from '@/stores/themeStore'

// 统一走主题 store：light → dark → game，三态分段控件直接点选
const themeStore = useThemeStore()

const themes = computed(() => themeStore.themes)
const currentTheme = computed(() => themeStore.currentTheme)
const currentName = computed(() => themeStore.currentThemeInfo?.name || '亮色')

// 滑块位移索引（找不到时兜底为 0）
const activeIndex = computed(() =>
    Math.max(0, themes.value.findIndex((t) => t.id === currentTheme.value))
)

function handleSelect(id) {
    if (id !== currentTheme.value) themeStore.setTheme(id)
}
</script>

<style scoped>
@import '@/css/Switchthemes/theme-switch.css';
</style>
