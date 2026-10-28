<template>
  <!-- 单个日期选择组件：触发器 + 自定义日历浮层（替代原生 date input） -->
  <div ref="rootRef" class="calendar-picker" :class="{ 'is-open': open, 'is-disabled': disabled }">
    <button type="button" class="cp-trigger" :disabled="disabled" :aria-expanded="open" aria-haspopup="dialog"
      @click.stop="toggle">
      <span class="cp-value" :class="{ 'is-placeholder': !displayText }">{{ displayText || placeholderText }}</span>
      <svg class="cp-trigger-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
        stroke-linecap="round" stroke-linejoin="round">
        <rect x="3" y="5" width="18" height="16" rx="3" />
        <path d="M8 3v4M16 3v4M3 10h18" />
      </svg>
    </button>

    <Teleport to="body">
      <Transition name="cp-pop">
        <div v-if="open" ref="panelRef" class="cp-panel" :style="panelStyle" role="dialog"
          :aria-label="$t('calendar.title')" @click.stop>
          <div class="cp-head">
            <button type="button" class="cp-nav" :aria-label="$t('calendar.prevYear')" @click="shiftYear(-1)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round">
                <path d="m11 17-5-5 5-5" />
                <path d="m18 17-5-5 5-5" />
              </svg>
            </button>
            <button type="button" class="cp-nav" :aria-label="$t('calendar.prevMonth')" @click="shiftMonth(-1)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round">
                <path d="m15 18-6-6 6-6" />
              </svg>
            </button>
            <span class="cp-title">{{ monthTitle }}</span>
            <button type="button" class="cp-nav" :aria-label="$t('calendar.nextMonth')" @click="shiftMonth(1)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round">
                <path d="m9 18 6-6-6-6" />
              </svg>
            </button>
            <button type="button" class="cp-nav" :aria-label="$t('calendar.nextYear')" @click="shiftYear(1)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round">
                <path d="m6 17 5-5-5-5" />
                <path d="m13 17 5-5-5-5" />
              </svg>
            </button>
          </div>

          <div class="cp-weekdays">
            <span v-for="weekday in weekdays" :key="weekday">{{ weekday }}</span>
          </div>

          <div class="cp-grid">
            <button v-for="cell in cells" :key="cell.key" type="button" class="cp-day" :class="{
              'is-outside': !cell.inMonth,
              'is-today': cell.isToday,
              'is-selected': cell.isSelected
            }" :disabled="cell.disabled" @click="pick(cell)">
              {{ cell.day }}
            </button>
          </div>

          <div class="cp-foot">
            <button v-if="clearable && modelValue" type="button" class="cp-foot-btn" @click="clear">
              {{ $t('calendar.clear') }}
            </button>
            <button type="button" class="cp-foot-btn is-primary" @click="pickToday">
              {{ $t('calendar.today') }}
            </button>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  /** 选中的日期，格式 YYYY-MM-DD */
  modelValue: { type: String, default: '' },
  /** 占位文案，未传时使用 calendar.placeholder */
  placeholder: { type: String, default: '' },
  /** 可选最小日期（含），格式 YYYY-MM-DD */
  min: { type: String, default: '' },
  /** 可选最大日期（含），格式 YYYY-MM-DD */
  max: { type: String, default: '' },
  /** 是否显示「清除」 */
  clearable: { type: Boolean, default: true },
  disabled: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'change'])

const { t, locale } = useI18n()

const rootRef = ref(null)
const panelRef = ref(null)
const open = ref(false)
/** 浮层挂载到 body 后使用 fixed 定位，避免被父级 overflow 裁剪 */
const panelStyle = ref({ top: '0px', left: '0px' })

const pad = (n) => String(n).padStart(2, '0')
const toKey = (date) => `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`

const parseDate = (value) => {
  if (!value) return null
  const [year, month, day] = value.split('-').map(Number)
  if (!year || !month || !day) return null
  const date = new Date(year, month - 1, day)
  return Number.isNaN(date.getTime()) ? null : date
}

const today = new Date()
const todayKey = toKey(today)

const viewYear = ref(today.getFullYear())
const viewMonth = ref(today.getMonth())

const selectedDate = computed(() => parseDate(props.modelValue))

const placeholderText = computed(() => props.placeholder || t('calendar.placeholder'))

/** 触发器展示文案：按当前语言做本地化格式化 */
const displayText = computed(() => {
  if (!selectedDate.value) return ''
  return new Intl.DateTimeFormat(locale.value, {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(selectedDate.value)
})

/** 面板标题：2026年10月 / October 2026 */
const monthTitle = computed(() =>
  new Intl.DateTimeFormat(locale.value, { year: 'numeric', month: 'long' })
    .format(new Date(viewYear.value, viewMonth.value, 1))
)

/** 星期表头：以周一为起始（2024-01-01 是周一） */
const weekdays = computed(() => {
  const formatter = new Intl.DateTimeFormat(locale.value, { weekday: 'short' })
  return Array.from({ length: 7 }, (_, index) => formatter.format(new Date(2024, 0, 1 + index)))
})

/** 固定 6 行 x 7 列，避免切换月份时面板高度跳动 */
const cells = computed(() => {
  const firstDay = new Date(viewYear.value, viewMonth.value, 1)
  const offset = (firstDay.getDay() + 6) % 7
  const start = new Date(viewYear.value, viewMonth.value, 1 - offset)
  return Array.from({ length: 42 }, (_, index) => {
    const date = new Date(start.getFullYear(), start.getMonth(), start.getDate() + index)
    const key = toKey(date)
    return {
      key,
      date,
      day: date.getDate(),
      inMonth: date.getMonth() === viewMonth.value,
      isToday: key === todayKey,
      isSelected: key === props.modelValue,
      disabled: Boolean((props.min && key < props.min) || (props.max && key > props.max))
    }
  })
})

/** 打开时把视图对齐到已选日期（无选中则回到当前月） */
const syncView = () => {
  const base = selectedDate.value || new Date()
  viewYear.value = base.getFullYear()
  viewMonth.value = base.getMonth()
}

/** 依据触发器位置计算浮层坐标：右侧溢出时左移，下方空间不足时上翻 */
const positionPanel = () => {
  if (!rootRef.value || !panelRef.value) return
  const rect = rootRef.value.getBoundingClientRect()
  const panelWidth = panelRef.value.offsetWidth
  const panelHeight = panelRef.value.offsetHeight
  const viewportWidth = window.innerWidth
  const viewportHeight = window.innerHeight

  let left = Math.min(rect.left, viewportWidth - panelWidth - 12)
  left = Math.max(12, left)

  let top = rect.bottom + 8
  if (top + panelHeight > viewportHeight - 12 && rect.top - panelHeight - 8 > 12) {
    top = rect.top - panelHeight - 8
  }
  top = Math.max(12, Math.min(top, viewportHeight - panelHeight - 12))

  panelStyle.value = { top: `${top}px`, left: `${left}px` }
}

const toggle = async () => {
  if (props.disabled) return
  open.value = !open.value
  if (!open.value) return
  syncView()
  await nextTick()
  positionPanel()
}

const shiftMonth = (step) => {
  const date = new Date(viewYear.value, viewMonth.value + step, 1)
  viewYear.value = date.getFullYear()
  viewMonth.value = date.getMonth()
}

const shiftYear = (step) => {
  viewYear.value += step
}

const commit = (date) => {
  const key = toKey(date)
  emit('update:modelValue', key)
  emit('change', key)
  open.value = false
}

const pick = (cell) => {
  if (cell.disabled) return
  commit(cell.date)
}

const pickToday = () => {
  if ((props.min && todayKey < props.min) || (props.max && todayKey > props.max)) return
  commit(new Date())
}

const clear = () => {
  emit('update:modelValue', '')
  emit('change', '')
  open.value = false
}

const onDocumentClick = () => {
  open.value = false
}

const onKeydown = (event) => {
  if (event.key === 'Escape') open.value = false
}

const onViewportChange = () => {
  if (open.value) positionPanel()
}

onMounted(() => {
  document.addEventListener('click', onDocumentClick)
  document.addEventListener('keydown', onKeydown)
  window.addEventListener('resize', onViewportChange)
  window.addEventListener('scroll', onViewportChange, true)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocumentClick)
  document.removeEventListener('keydown', onKeydown)
  window.removeEventListener('resize', onViewportChange)
  window.removeEventListener('scroll', onViewportChange, true)
})

// 外部改值时同步视图月份
watch(() => props.modelValue, () => {
  if (open.value) syncView()
})
</script>

<style scoped>
.calendar-picker {
  position: relative;
  display: block;
  min-width: 0;
}

/* 触发器：与搜索栏输入框保持同一视觉 */
.cp-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  width: 100%;
  min-height: 46px;
  padding: 12px 14px;
  border: 1px solid var(--theme-border);
  border-radius: 8px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-family: inherit;
  font-size: 15px;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.25s ease, box-shadow 0.25s ease;
  -webkit-tap-highlight-color: transparent;
}

.cp-trigger:hover {
  border-color: color-mix(in srgb, var(--theme-primary) 45%, var(--theme-border));
}

.cp-trigger:focus-visible,
.calendar-picker.is-open .cp-trigger {
  outline: none;
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.calendar-picker.is-disabled .cp-trigger {
  opacity: 0.55;
  cursor: not-allowed;
}

.cp-value {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cp-value.is-placeholder {
  color: var(--theme-text-3);
}

.cp-trigger-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
  color: var(--theme-text-2);
  transition: color 0.25s ease;
}

.calendar-picker.is-open .cp-trigger-icon {
  color: var(--theme-primary);
}

/* 日历浮层：挂载到 body，fixed 定位由脚本按触发器位置计算 */
.cp-panel {
  position: fixed;
  z-index: 1500;
  width: min(296px, calc(100vw - 28px));
  padding: 12px;
  border: 1px solid var(--theme-border);
  border-radius: 14px;
  background: var(--theme-bg-elevated);
  box-shadow: var(--theme-shadow-lg);
}

.cp-head {
  display: flex;
  align-items: center;
  gap: 2px;
  margin-bottom: 4px;
}

.cp-title {
  flex: 1;
  text-align: center;
  font-size: 14px;
  font-weight: 700;
  color: var(--theme-text-1);
  white-space: nowrap;
}

.cp-nav {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--theme-text-2);
  cursor: pointer;
  flex-shrink: 0;
  transition: background 0.2s ease, color 0.2s ease;
}

.cp-nav svg {
  width: 16px;
  height: 16px;
}

.cp-nav:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-primary);
}

.cp-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
  padding: 6px 0 4px;
  font-size: 12px;
  color: var(--theme-text-3);
  text-align: center;
}

.cp-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.cp-day {
  height: 34px;
  border: 1px solid transparent;
  border-radius: 9px;
  background: transparent;
  color: var(--theme-text-1);
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
  transition: background 0.18s ease, color 0.18s ease, transform 0.18s ease;
}

.cp-day:hover:not(:disabled) {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.cp-day:active:not(:disabled) {
  transform: scale(0.94);
}

.cp-day.is-outside {
  color: var(--theme-text-3);
  opacity: 0.5;
}

.cp-day.is-today {
  border-color: color-mix(in srgb, var(--theme-primary) 55%, transparent);
  color: var(--theme-primary);
  font-weight: 700;
}

.cp-day.is-selected {
  background: var(--theme-primary);
  border-color: var(--theme-primary);
  color: var(--theme-primary-text);
  font-weight: 700;
  box-shadow: 0 4px 10px color-mix(in srgb, var(--theme-primary) 35%, transparent);
}

.cp-day:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.cp-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid var(--theme-hairline);
}

.cp-foot-btn {
  padding: 4px 8px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--theme-text-2);
  font-family: inherit;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.cp-foot-btn:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.cp-foot-btn.is-primary {
  color: var(--theme-primary);
  font-weight: 600;
}

.cp-foot-btn.is-primary:hover {
  background: var(--theme-primary-soft);
}

/* 浮层动效 */
.cp-pop-enter-active,
.cp-pop-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.cp-pop-enter-from,
.cp-pop-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}

@media (max-width: 480px) {
  .cp-panel {
    padding: 10px;
  }

  .cp-day {
    height: 32px;
  }
}

@media (prefers-reduced-motion: reduce) {

  .cp-trigger,
  .cp-day,
  .cp-pop-enter-active,
  .cp-pop-leave-active {
    transition: none !important;
  }
}
</style>
