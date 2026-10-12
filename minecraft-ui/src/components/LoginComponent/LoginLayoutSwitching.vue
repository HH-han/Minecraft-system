<template>
    <div
        class="layout-switch"
        role="group"
        :aria-label="`登录页布局切换（当前：${currentName}）`"
    >
        <!-- 滑动指示块：随当前布局平移，激活色取主题主色 -->
        <span class="layout-switch__thumb" :style="{ '--ls-index': activeIndex }"></span>

        <button
            v-for="item in layouts"
            :key="item.id"
            type="button"
            class="layout-switch__option"
            :class="{ 'is-active': item.id === modelValue }"
            :title="item.id === modelValue ? `${item.name}（当前）` : `切换到${item.name}`"
            :aria-label="item.name"
            :aria-pressed="item.id === modelValue"
            @click="handleSelect(item.id)"
        >
            <!-- 统一的「版面示意」图标：外框 + 高亮分区，三种布局一眼可辨 -->
            <svg
                class="layout-switch__icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
            >
                <rect x="3" y="4" width="18" height="16" rx="3" />
                <!-- 图文在左：高亮左分区 -->
                <rect
                    v-if="item.id === 'panel-left'"
                    x="5" y="6" width="6" height="12" rx="2"
                    fill="currentColor" stroke="none"
                />
                <!-- 表单居中：高亮中间分区 -->
                <rect
                    v-else-if="item.id === 'panel-center'"
                    x="9" y="6" width="6" height="12" rx="2"
                    fill="currentColor" stroke="none"
                />
                <!-- 图文在右：高亮右分区 -->
                <rect
                    v-else
                    x="13" y="6" width="6" height="12" rx="2"
                    fill="currentColor" stroke="none"
                />
            </svg>
        </button>
    </div>
</template>

<script setup>
import { computed } from 'vue'

// 布局清单（顺序即分段顺序），与 LoginName.vue 的 class 后缀一一对应
// · panel-left  ：表单贴左边、图文铺满右侧（参考全屏贴边效果）
// · panel-center：居中卡片，即登录页原始布局
// · panel-right ：图文铺满左侧、表单贴右边
const LAYOUTS = [
    { id: 'panel-left', name: '表单在左' },
    { id: 'panel-center', name: '居中卡片' },
    { id: 'panel-right', name: '表单在右' }
]

const props = defineProps({
    modelValue: {
        type: String,
        default: 'panel-center'
    }
})

const emit = defineEmits(['update:modelValue'])

const layouts = LAYOUTS

const currentName = computed(
    () => LAYOUTS.find((item) => item.id === props.modelValue)?.name || '居中卡片'
)

// 滑块位移索引（找不到时兜底为 0）
const activeIndex = computed(() =>
    Math.max(0, LAYOUTS.findIndex((item) => item.id === props.modelValue))
)

function handleSelect(id) {
    if (id !== props.modelValue) emit('update:modelValue', id)
}
</script>

<style scoped>
@import '@/css/Login/login-layout-switch.css';
</style>
