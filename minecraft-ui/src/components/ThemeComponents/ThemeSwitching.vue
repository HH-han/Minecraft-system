<template>
    <label class="switch" :title="`当前主题：${currentThemeName}（点击切换）`">
        <input id="input" type="checkbox" :checked="isDark" @change="handleToggle" />
        <div class="slider round">
            <div class="sun-moon">
                <svg id="moon-dot-1" class="moon-dot" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="moon-dot-2" class="moon-dot" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="moon-dot-3" class="moon-dot" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="light-ray-1" class="light-ray" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="light-ray-2" class="light-ray" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="light-ray-3" class="light-ray" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>

                <svg id="cloud-1" class="cloud-dark" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="cloud-2" class="cloud-dark" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="cloud-3" class="cloud-dark" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="cloud-4" class="cloud-light" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="cloud-5" class="cloud-light" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
                <svg id="cloud-6" class="cloud-light" viewBox="0 0 100 100">
                    <circle cx="50" cy="50" r="50"></circle>
                </svg>
            </div>
            <div class="stars">
                <svg id="star-1" class="star" viewBox="0 0 20 20">
                    <path
                        d="M 0 10 C 10 10,10 10 ,0 10 C 10 10 , 10 10 , 10 20 C 10 10 , 10 10 , 20 10 C 10 10 , 10 10 , 10 0 C 10 10,10 10 ,0 10 Z">
                    </path>
                </svg>
                <svg id="star-2" class="star" viewBox="0 0 20 20">
                    <path
                        d="M 0 10 C 10 10,10 10 ,0 10 C 10 10 , 10 10 , 10 20 C 10 10 , 10 10 , 20 10 C 10 10 , 10 10 , 10 0 C 10 10,10 10 ,0 10 Z">
                    </path>
                </svg>
                <svg id="star-3" class="star" viewBox="0 0 20 20">
                    <path
                        d="M 0 10 C 10 10,10 10 ,0 10 C 10 10 , 10 10 , 10 20 C 10 10 , 10 10 , 20 10 C 10 10 , 10 10 , 10 0 C 10 10,10 10 ,0 10 Z">
                    </path>
                </svg>
                <svg id="star-4" class="star" viewBox="0 0 20 20">
                    <path
                        d="M 0 10 C 10 10,10 10 ,0 10 C 10 10 , 10 10 , 10 20 C 10 10 , 10 10 , 20 10 C 10 10 , 10 10 , 10 0 C 10 10,10 10 ,0 10 Z">
                    </path>
                </svg>
            </div>
        </div>
    </label>
</template>

<script setup>
import { computed } from 'vue'
import { useThemeStore } from '@/stores/themeStore'

// 统一走主题 store，切换顺序：亮色 → 暗色 → 游戏 → 亮色
const themeStore = useThemeStore()

// 开关的选中态：dark / game 均显示为「深色」
const isDark = computed(() => themeStore.isDark)
const currentThemeName = computed(() => themeStore.currentThemeInfo?.name || '亮色')

function handleToggle() {
  themeStore.toggleTheme()
}
</script>

<style scoped>
@import '@/css/Switchthemes/theme-switch.css';
</style>