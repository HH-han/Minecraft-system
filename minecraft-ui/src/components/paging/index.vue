<template>
    <div ref="rootRef" class="pager" role="navigation" aria-label="分页导航">
        <!-- 继续下滑加载 / 加载中 提示 -->
        <transition name="hint">
            <div
                v-if="totalPages > 1 && (isLoading || (hasNext && nearBottom))"
                class="pager__hint"
                :class="{ 'is-loading': isLoading }"
                aria-live="polite"
            >
                <template v-if="isLoading">
                    <span class="pager__spinner" aria-hidden="true"></span>
                    <span>正在加载更多…</span>
                </template>
                <template v-else>
                    <span>继续下滑加载更多</span>
                    <svg class="pager__chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                        stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M6 9l6 6 6-6" />
                    </svg>
                </template>
            </div>
        </transition>

        <div class="pagination">
            <button
                type="button"
                class="pagination-btn"
                :class="{ 'is-disabled': currentPage === 1 }"
                :disabled="currentPage === 1"
                aria-label="第一页"
                @click="goFirst"
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round">
                    <path d="M18 15l-6-6-6 6" />
                </svg>
            </button>
            <button
                type="button"
                class="pagination-btn"
                :class="{ 'is-disabled': currentPage === 1 }"
                :disabled="currentPage === 1"
                aria-label="上一页"
                @click="goPrev"
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round">
                    <path d="M15 18l-6-6 6-6" />
                </svg>
            </button>

            <transition-group name="num" tag="div" class="pagination-nums">
                <template v-for="(page, index) in pages" :key="typeof page === 'number' ? `p-${page}` : `e-${index}`">
                    <span v-if="page === '...'" class="pagination-ellipsis" aria-hidden="true">···</span>
                    <button
                        v-else
                        type="button"
                        class="pagination-num"
                        :class="{ 'is-active': page === currentPage }"
                        :aria-current="page === currentPage ? 'page' : undefined"
                        @click="goPage(page)"
                    >
                        {{ page }}
                    </button>
                </template>
            </transition-group>

            <button
                type="button"
                class="pagination-btn"
                :class="{ 'is-disabled': currentPage === totalPages }"
                :disabled="currentPage === totalPages || totalPages === 0"
                aria-label="下一页"
                @click="goNext"
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round">
                    <path d="M9 18l6-6-6-6" />
                </svg>
            </button>
            <button
                type="button"
                class="pagination-btn"
                :class="{ 'is-disabled': currentPage === totalPages }"
                :disabled="currentPage === totalPages || totalPages === 0"
                aria-label="最后一页"
                @click="goLast"
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round">
                    <path d="M6 15l6-6 6 6" />
                </svg>
            </button>

            <span v-if="showInfo" class="pagination-info">
                第 <b>{{ currentPage }}</b> / {{ totalPages }} 页
            </span>
        </div>
    </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'

const props = defineProps({
    totalPages: {
        type: Number,
        default: 0
    },
    currentPage: {
        type: Number,
        default: 1
    },
    // 父组件数据加载中（可选），用于阻止重复触发并展示加载态
    loading: {
        type: Boolean,
        default: false
    },
    // 是否开启「滑到底部继续下滑自动加载下一页」
    autoLoad: {
        type: Boolean,
        default: true
    },
    // 继续下滑累计阈值（px），越大越不容易误触发
    loadThreshold: {
        type: Number,
        default: 220
    },
    // 是否展示「第 N / M 页」
    showInfo: {
        type: Boolean,
        default: true
    }
})

const emit = defineEmits(['update:currentPage'])

const rootRef = ref(null)
const nearBottom = ref(false)
const pending = ref(false)

// 是否还有下一页
const hasNext = computed(() => props.currentPage < props.totalPages)
// 加载态（父组件 loading 或本地等待翻页结果）
const isLoading = computed(() => props.loading || pending.value)

// 页码序列（超出 7 页时用省略号收缩，贴近首尾时自动补足相邻页）
const pages = computed(() => {
    const total = props.totalPages
    const cur = props.currentPage
    if (total <= 0) return []
    if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1)

    let left = Math.max(2, cur - 1)
    let right = Math.min(total - 1, cur + 1)
    if (cur <= 3) {
        left = 2
        right = Math.min(total - 1, 4)
    } else if (cur >= total - 2) {
        left = Math.max(2, total - 4)
        right = total - 1
    }

    const out = [1]
    if (left > 2) out.push('...')
    for (let p = left; p <= right; p++) out.push(p)
    if (right < total - 1) out.push('...')
    out.push(total)
    return out
})

/* ---------- 翻页逻辑 ---------- */
const goFirst = () => {
    if (props.currentPage !== 1) emit('update:currentPage', 1)
}

const goPrev = () => {
    if (props.currentPage > 1) emit('update:currentPage', props.currentPage - 1)
}

const goPage = (page) => {
    if (page !== props.currentPage) emit('update:currentPage', page)
}

const goNext = () => {
    if (props.currentPage < props.totalPages) emit('update:currentPage', props.currentPage + 1)
}

const goLast = () => {
    if (props.totalPages > 0 && props.currentPage !== props.totalPages) {
        emit('update:currentPage', props.totalPages)
    }
}

/* ---------- 「继续下滑自动加载」---------- */
let accumulated = 0
let lockUntil = 0
let pendingTimer = null
let touchStartY = 0

// 分页条是否已位于（接近）视口底部
const updateNearBottom = () => {
    const el = rootRef.value
    if (!el) {
        nearBottom.value = false
        return
    }
    const rect = el.getBoundingClientRect()
    const viewport = window.innerHeight || document.documentElement.clientHeight
    nearBottom.value = rect.top < viewport && rect.bottom <= viewport + 80
}

const canTrigger = () =>
    props.autoLoad &&
    !isLoading.value &&
    hasNext.value &&
    props.totalPages > 1 &&
    Date.now() > lockUntil

const triggerLoadMore = () => {
    accumulated = 0
    if (!canTrigger()) return
    pending.value = true
    lockUntil = Date.now() + 800
    emit('update:currentPage', props.currentPage + 1)
    // 兜底：父组件未同步更新 currentPage 时也解除本地加载态
    clearTimeout(pendingTimer)
    pendingTimer = window.setTimeout(() => {
        pending.value = false
    }, 1200)
}

const handleWheel = (e) => {
    if (!props.autoLoad) return
    // 未到底部 / 向上滚动时重置累积
    if (!nearBottom.value || !e.deltaY || e.deltaY <= 0) {
        accumulated = 0
        return
    }
    if (!canTrigger()) return
    accumulated += e.deltaY
    if (accumulated >= props.loadThreshold) triggerLoadMore()
}

const handleTouchStart = (e) => {
    touchStartY = e.touches?.[0]?.clientY ?? 0
    accumulated = 0
}

const handleTouchMove = (e) => {
    if (!props.autoLoad || !nearBottom.value) return
    const currentY = e.touches?.[0]?.clientY ?? 0
    // 手指向上滑动（继续下滑）时 dy 为正
    const dy = touchStartY - currentY
    if (dy <= 0) {
        accumulated = 0
        return
    }
    if (!canTrigger()) return
    accumulated = dy
    if (accumulated >= props.loadThreshold) triggerLoadMore()
}

const handleScrollOrResize = () => updateNearBottom()

onMounted(() => {
    updateNearBottom()
    window.addEventListener('scroll', handleScrollOrResize, { passive: true })
    window.addEventListener('resize', handleScrollOrResize)
    window.addEventListener('wheel', handleWheel, { passive: true })
    window.addEventListener('touchstart', handleTouchStart, { passive: true })
    window.addEventListener('touchmove', handleTouchMove, { passive: true })
})

onBeforeUnmount(() => {
    clearTimeout(pendingTimer)
    window.removeEventListener('scroll', handleScrollOrResize)
    window.removeEventListener('resize', handleScrollOrResize)
    window.removeEventListener('wheel', handleWheel)
    window.removeEventListener('touchstart', handleTouchStart)
    window.removeEventListener('touchmove', handleTouchMove)
})

// 翻页完成后清除本地加载态并重新计算位置
watch(
    () => props.currentPage,
    () => {
        pending.value = false
        accumulated = 0
        requestAnimationFrame(updateNearBottom)
    }
)

watch(
    () => props.totalPages,
    () => {
        accumulated = 0
        requestAnimationFrame(updateNearBottom)
    }
)
</script>

<style scoped>
.pager {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 20px 0 8px;
    animation: pager-in 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
}

@keyframes pager-in {
    from {
        opacity: 0;
        transform: translateY(14px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 继续下滑 / 加载中 提示 */
.pager__hint {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    height: 26px;
    padding: 0 14px;
    border-radius: 999px;
    font-size: 12.5px;
    color: var(--theme-text-3);
    background: var(--theme-bg-surface-2);
    border: 1px solid var(--theme-hairline);
}

.pager__hint.is-loading {
    color: var(--theme-primary);
    background: var(--theme-primary-soft);
    border-color: transparent;
}

.pager__chevron {
    width: 14px;
    height: 14px;
    animation: chevron-bounce 1.4s ease-in-out infinite;
}

@keyframes chevron-bounce {
    0%,
    100% {
        transform: translateY(-2px);
    }
    50% {
        transform: translateY(2px);
    }
}

.pager__spinner {
    width: 13px;
    height: 13px;
    border: 2px solid color-mix(in srgb, var(--theme-primary) 30%, transparent);
    border-top-color: var(--theme-primary);
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

/* 提示显隐动画 */
.hint-enter-active,
.hint-leave-active {
    transition: opacity 0.25s ease, transform 0.25s ease;
}

.hint-enter-from,
.hint-leave-to {
    opacity: 0;
    transform: translateY(-4px);
}

/* 控制条 */
.pagination {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    flex-wrap: wrap;
}

.pagination-nums {
    position: relative;
    display: flex;
    align-items: center;
    gap: 6px;
}

.pagination-btn,
.pagination-num {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 36px;
    min-width: 36px;
    padding: 0 8px;
    border: 1px solid var(--theme-border);
    border-radius: 9px;
    background: var(--theme-bg-surface);
    color: var(--theme-text-2);
    font-size: 14px;
    font-family: inherit;
    cursor: pointer;
    transition:
        transform 0.25s cubic-bezier(0.4, 0, 0.2, 1),
        border-color 0.25s ease,
        color 0.25s ease,
        background-color 0.25s ease,
        box-shadow 0.25s ease;
}

.pagination-btn:hover:not(.is-disabled),
.pagination-num:hover:not(.is-active) {
    border-color: var(--theme-primary);
    color: var(--theme-primary);
    background: var(--theme-primary-soft);
    transform: translateY(-2px);
    box-shadow: 0 6px 14px color-mix(in srgb, var(--theme-primary) 18%, transparent);
}

.pagination-btn:active:not(.is-disabled),
.pagination-num:active:not(.is-active) {
    transform: translateY(0) scale(0.96);
}

.pagination-btn:focus-visible,
.pagination-num:focus-visible {
    outline: 2px solid var(--theme-primary);
    outline-offset: 2px;
}

.pagination-btn.is-disabled {
    opacity: 0.4;
    cursor: not-allowed;
}

.pagination-btn svg {
    width: 16px;
    height: 16px;
}

.pagination-num.is-active {
    background: var(--theme-gradient);
    border-color: var(--theme-primary);
    color: var(--theme-primary-text);
    font-weight: 600;
    box-shadow: 0 6px 18px color-mix(in srgb, var(--theme-primary) 32%, transparent);
    animation: num-pop 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

@keyframes num-pop {
    0% {
        transform: scale(0.86);
    }
    60% {
        transform: scale(1.06);
    }
    100% {
        transform: scale(1);
    }
}

.pagination-ellipsis {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 36px;
    min-width: 24px;
    color: var(--theme-text-3);
    font-size: 14px;
    letter-spacing: 0.08em;
    user-select: none;
}

.pagination-info {
    margin-left: 12px;
    color: var(--theme-text-3);
    font-size: 13px;
}

.pagination-info b {
    color: var(--theme-text-1);
    font-weight: 700;
}

/* 页码切换动画 */
.num-move,
.num-enter-active,
.num-leave-active {
    transition: opacity 0.2s ease, transform 0.2s ease;
}

.num-enter-from,
.num-leave-to {
    opacity: 0;
    transform: scale(0.8);
}

.num-leave-active {
    position: absolute;
}

@media (max-width: 640px) {
    .pagination {
        gap: 5px;
    }

    .pagination-btn,
    .pagination-num {
        height: 32px;
        min-width: 32px;
        font-size: 13px;
    }

    .pagination-info {
        margin-left: 6px;
        width: 100%;
        text-align: center;
    }
}
</style>
