<template>
    <div ref="wrapRef" class="theme-switch-wrap">
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
                @click="handleSelect(item.id, $event)"
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

        <!-- 内置主题配色：仅亮色主题可用，非亮色时保留占位但不可点 -->
        <button
            type="button"
            class="theme-switch__palette"
            :class="{ 'is-open': paletteOpen }"
            :disabled="!isLight"
            :aria-expanded="paletteOpen"
            aria-haspopup="dialog"
            :title="isLight ? `内置主题配色（当前：${currentPresetName}）` : '内置主题配色仅亮色主题可用'"
            :aria-label="isLight ? '内置主题配色' : '内置主题配色（仅亮色主题可用）'"
            @click="togglePalette"
        >
            <svg
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
                    d="M12 21a9 9 0 1 1 9-9c0 1.657-1.343 3-3 3h-1.6a2.4 2.4 0 0 0-2.4 2.4c0 .884.717 1.6 1.6 1.6H12Z"
                />
                <path d="M7.5 12h.01M10 8h.01M14.5 8h.01M17 11.5h.01" />
            </svg>
        </button>

        <!-- 色板面板：命名色板网格 + 自定义取色 -->
        <div
            v-if="paletteOpen"
            class="theme-palette"
            role="dialog"
            aria-label="内置主题配色"
        >
            <p class="theme-palette__title">内置主题</p>
            <div class="theme-palette__grid">
                <button
                    v-for="preset in presetItems"
                    :key="preset.id"
                    type="button"
                    class="theme-palette__item"
                    :class="{ 'is-active': preset.id === currentPreset }"
                    :title="preset.name"
                    :aria-pressed="preset.id === currentPreset"
                    @click="handlePreset(preset.id)"
                >
                    <span class="theme-palette__dot" :style="{ '--tp-dot': preset.color }">
                        <svg
                            class="theme-palette__check"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            stroke-width="3"
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            aria-hidden="true"
                        >
                            <path d="M20 6 9 17l-5-5" />
                        </svg>
                    </span>
                    <span class="theme-palette__name">{{ preset.name }}</span>
                </button>

                <label
                    class="theme-palette__item theme-palette__item--custom"
                    :class="{ 'is-active': currentPreset === 'custom' }"
                    :title="`自定义（当前：${customColor}）`"
                >
                    <span
                        class="theme-palette__dot theme-palette__dot--custom"
                        :style="{ '--tp-dot': customColor }"
                    >
                        <svg
                            class="theme-palette__check"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            stroke-width="3"
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            aria-hidden="true"
                        >
                            <path d="M20 6 9 17l-5-5" />
                        </svg>
                    </span>
                    <span class="theme-palette__name">自定义</span>
                    <input
                        type="color"
                        class="theme-palette__picker"
                        :value="customColor"
                        aria-label="自定义主题色"
                        @input="handleCustomInput"
                        @change="handleCustomChange"
                    />
                </label>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'
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

// 内置主题色板（仅亮色主题可用；custom 单独渲染为取色器，故从网格中排除）
const presetItems = computed(() => themeStore.presets.filter((p) => p.id !== 'custom'))
const currentPreset = computed(() => themeStore.currentPreset)
const currentPresetName = computed(() => themeStore.currentPresetInfo?.name || '默认')
const customColor = computed(() => themeStore.customColor)
const isLight = computed(() => themeStore.isLight)

const paletteOpen = ref(false)
const wrapRef = ref(null)

// 切换主题：动画圆心取按钮中心（鼠标点击与键盘回车表现一致）
function handleSelect(id, event) {
    if (id === currentTheme.value) return
    const rect = event?.currentTarget?.getBoundingClientRect()
    const origin = rect
        ? { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 }
        : undefined
    themeStore.setTheme(id, origin)
}

function togglePalette() {
    if (!isLight.value) return
    paletteOpen.value = !paletteOpen.value
}

function closePalette() {
    paletteOpen.value = false
}

function handlePreset(id) {
    themeStore.setPreset(id)
}

// 取色器实时预览：input 会在拖拽中高频触发，用 rAF 合并为「每帧最多一次」DOM 写入
let previewFrame = 0
let pendingColor = ''

function handleCustomInput(event) {
    pendingColor = event.target.value
    if (previewFrame) return
    previewFrame = requestAnimationFrame(() => {
        previewFrame = 0
        themeStore.previewCustomColor(pendingColor)
    })
}

// 取色器确定：落盘（localStorage），并丢弃尚未执行的预览帧
function handleCustomChange(event) {
    if (previewFrame) {
        cancelAnimationFrame(previewFrame)
        previewFrame = 0
    }
    themeStore.commitCustomColor(event.target.value)
}

// 离开亮色主题（或切到 dark / game）时自动收起面板
watch(isLight, (light) => {
    if (!light) closePalette()
})

function onDocPointerDown(event) {
    if (!wrapRef.value?.contains(event.target)) closePalette()
}

function onKeydown(event) {
    if (event.key === 'Escape') closePalette()
}

// 点击面板外部 / 按 Esc 关闭
watch(paletteOpen, (open) => {
    if (open) {
        document.addEventListener('pointerdown', onDocPointerDown, true)
        document.addEventListener('keydown', onKeydown)
    } else {
        document.removeEventListener('pointerdown', onDocPointerDown, true)
        document.removeEventListener('keydown', onKeydown)
    }
})

onBeforeUnmount(() => {
    if (previewFrame) cancelAnimationFrame(previewFrame)
    document.removeEventListener('pointerdown', onDocPointerDown, true)
    document.removeEventListener('keydown', onKeydown)
})
</script>

<style scoped>
@import '@/css/Switchthemes/theme-switch.css';
</style>
