<template>
  <div class="date-picker-component" ref="rootEl">
    <!-- 触发器：展示选中结果（高德酒店样式：开始/结束两栏 + 住宿天数） -->
    <button type="button" class="dp-trigger" @click="togglePanel" aria-haspopup="dialog"
      :aria-expanded="panelVisible">
      <span class="dp-cell">
        <span class="dp-label">{{ rangeMode ? '入住' : '游玩日期' }}</span>
        <span class="dp-value" :class="{ 'is-placeholder': !startDate }">
          {{ formatDateDisplay(startDate) }}
        </span>
      </span>
      <span class="dp-divider" v-if="rangeMode">
        <span class="dp-nights-badge" v-if="nights > 0">{{ nights }}晚</span>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
          stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M5 12h14M13 6l6 6-6 6" />
        </svg>
      </span>
      <span class="dp-cell" v-if="rangeMode">
        <span class="dp-label">离店</span>
        <span class="dp-value" :class="{ 'is-placeholder': !endDate }">
          {{ formatDateDisplay(endDate) }}
        </span>
      </span>
    </button>

    <!-- 日历面板 -->
    <div v-if="panelVisible" class="dp-panel" role="dialog" :aria-label="rangeMode ? '选择入住和离店日期' : '选择游玩日期'">
      <!-- 头部：月份切换 -->
      <div class="dp-panel-header">
        <button type="button" class="dp-nav-btn" :disabled="!canGoPrev" @click="prevMonth" aria-label="上个月">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round">
            <path d="M15 18l-6-6 6-6" />
          </svg>
        </button>
        <span class="dp-panel-title">{{ currentMonthLabel }}</span>
        <button type="button" class="dp-nav-btn" :disabled="!canGoNext" @click="nextMonth" aria-label="下个月">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round">
            <path d="M9 18l6-6-6-6" />
          </svg>
        </button>
      </div>

      <!-- 星期表头 -->
      <div class="dp-weekdays">
        <span v-for="w in weekLabels" :key="w" class="dp-weekday">{{ w }}</span>
      </div>

      <!-- 日期网格 -->
      <div class="dp-days">
        <template v-for="cell in calendarCells" :key="cell.key">
          <span v-if="cell.type === 'blank'" class="dp-day is-blank"></span>
          <button v-else type="button" class="dp-day" :class="cell.classes" :disabled="cell.disabled"
            @click="selectDate(cell)" @mouseenter="hoverDate = cell.dateStr" @mouseleave="hoverDate = null"
            :aria-label="cell.ariaLabel" :aria-current="cell.dateStr === todayStr ? 'date' : undefined">
            <span class="dp-day-num">{{ cell.day }}</span>
            <span v-if="cell.hint" class="dp-day-hint">{{ cell.hint }}</span>
          </button>
        </template>
      </div>

      <!-- 底部：快捷项 + 提示 -->
      <div class="dp-panel-footer">
        <template v-if="rangeMode">
          <button v-for="opt in quickOptions" :key="opt.label" type="button" class="dp-quick-btn"
            @click="applyQuickOption(opt)">{{ opt.label }}</button>
        </template>
        <span class="dp-footer-tip">{{ footerTip }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  dateFields: {
    type: Array,
    default: () => [
      { name: 'checkInDate', label: '入住日期', value: '', min: '' },
      { name: 'checkOutDate', label: '离店日期', value: '', min: '' }
    ]
  },
  // 日历可往前看多少个月（默认当前月开始）
  maxMonths: {
    type: Number,
    default: 12
  }
})

const emit = defineEmits(['dateChange'])

/* ===================== 字段解析（兼容酒店双字段 / 景点单字段） ===================== */
const isRangeFields = computed(() => props.dateFields.length >= 2)
// 统一按"范围模式"渲染：单字段（景点）退化为仅选一天
const rangeMode = computed(() => isRangeFields.value)

const startField = computed(() => (isRangeFields.value ? props.dateFields[0] : props.dateFields[0]))
const endField = computed(() => (isRangeFields.value ? props.dateFields[1] : null))

/* ===================== 日期工具 ===================== */
const pad = (n) => String(n).padStart(2, '0')
const toStr = (y, m, d) => `${y}-${pad(m)}-${pad(d)}`
const parseStr = (s) => {
  if (!s) return null
  const [y, m, d] = s.split('-').map(Number)
  if (!y || !m || !d) return null
  return { y, m, d }
}
const dstr = (o) => toStr(o.y, o.m, o.d)
const now = new Date()
const todayStr = toStr(now.getFullYear(), now.getMonth() + 1, now.getDate())
const todayObj = parseStr(todayStr)

const minLimit = computed(() => {
  // 各字段 min 取较晚者（默认今天）
  const mins = props.dateFields.map(f => f.min).filter(Boolean)
  if (!mins.length) return todayStr
  return mins.sort()[mins.length - 1]
})
const maxLimit = computed(() => {
  // 最小 min + maxMonths 个月
  const min = parseStr(minLimit.value)
  if (!min) return null
  let y = min.y
  let m = min.m + props.maxMonths - 1
  y += Math.floor((m - 1) / 12)
  m = ((m - 1) % 12) + 1
  return toStr(y, m, min.d)
})

/* ===================== 组件状态 ===================== */
const panelVisible = ref(false)
const startDate = ref('') // 选中开始（入住）
const endDate = ref('')   // 选中结束（离店）
const hoverDate = ref(null) // 范围预览 hover
const rootEl = ref(null)

// 从 props 初始化（模块层可能传入已选值）
const initFromProps = () => {
  startDate.value = startField.value?.value || ''
  endDate.value = endField.value?.value || ''
}
initFromProps()

// 当前展示的月份（默认跟随 startDate 或 min）
const viewYear = ref((parseStr(startDate.value) || parseStr(minLimit.value) || todayObj).y)
const viewMonth = ref((parseStr(startDate.value) || parseStr(minLimit.value) || todayObj).m)

/* ===================== 日历渲染 ===================== */
const weekLabels = ['日', '一', '二', '三', '四', '五', '六']

const currentMonthLabel = computed(() => `${viewYear.value}年${viewMonth.value}月`)

const monthDays = computed(() => new Date(viewYear.value, viewMonth.value, 0).getDate())
const firstDayOfWeek = computed(() => new Date(viewYear.value, viewMonth.value - 1, 1).getDay())

const nights = computed(() => {
  const s = parseStr(startDate.value)
  const e = parseStr(endDate.value)
  if (!s || !e) return 0
  const ms = new Date(e.y, e.m - 1, e.d) - new Date(s.y, s.m - 1, s.d)
  return Math.max(0, Math.round(ms / 86400000))
})

const fmtDisplay = (s) => {
  const o = parseStr(s)
  if (!o) return ''
  return `${o.m}月${o.d}日`
}

const formatDateDisplay = (s) => (s ? fmtDisplay(s) : (rangeMode.value ? '待选' : '请选择'))

// 今天 / 明天（快捷选项用）
const addDays = (o, n) => {
  const dt = new Date(o.y, o.m - 1, o.d + n)
  return { y: dt.getFullYear(), m: dt.getMonth() + 1, d: dt.getDate() }
}
const quickOptions = computed(() => [
  { label: '今天入住', start: todayStr, end: dstr(addDays(todayObj, 1)) },
  { label: '明天入住', start: dstr(addDays(todayObj, 1)), end: dstr(addDays(todayObj, 2)) },
  { label: '周末入住', start: nextWeekend(), end: nextWeekendPlus() },
  { label: '下周入住', start: nextMonday(), end: nextMondayPlus() }
])

function nextWeekend() {
  // 本周六（若已过，取下周六）
  const dt = new Date(todayObj.y, todayObj.m - 1, todayObj.d)
  const offset = (6 - dt.getDay() + 7) % 7 || 7
  const r = addDays(todayObj, offset)
  return dstr(r)
}
function nextWeekendPlus() {
  const dt = new Date(todayObj.y, todayObj.m - 1, todayObj.d)
  const offset = (6 - dt.getDay() + 7) % 7 || 7
  return dstr(addDays(todayObj, offset + 1))
}
function nextMonday() {
  const dt = new Date(todayObj.y, todayObj.m - 1, todayObj.d)
  const offset = (8 - dt.getDay()) % 7 || 7
  return dstr(addDays(todayObj, offset))
}
function nextMondayPlus() {
  const dt = new Date(todayObj.y, todayObj.m - 1, todayObj.d)
  const offset = (8 - dt.getDay()) % 7 || 7
  return dstr(addDays(todayObj, offset + 1))
}

// 单元格状态：今天 / 范围内 / 边界 / hover 预览
const inRange = (dateStr) => {
  if (!startDate.value) return false
  const end = endDate.value || (hoverDate.value && hoverDate.value > startDate.value ? hoverDate.value : null)
  if (!end) return false
  return dateStr > startDate.value && dateStr < end
}

const calendarCells = computed(() => {
  const cells = []
  // 前置空白
  for (let i = 0; i < firstDayOfWeek.value; i++) {
    cells.push({ type: 'blank', key: `b${i}` })
  }
  for (let day = 1; day <= monthDays.value; day++) {
    const dateStr = toStr(viewYear.value, viewMonth.value, day)
    const disabled = dateStr < minLimit.value || (!!maxLimit.value && dateStr > maxLimit.value)
    const isStart = dateStr === startDate.value
    const isEnd = rangeMode.value && dateStr === endDate.value
    const isToday = dateStr === todayStr
    const inR = rangeMode.value && inRange(dateStr)
    // hover 预览时临时起止
    const tempEnd = rangeMode.value && !endDate.value && hoverDate.value && hoverDate.value > startDate.value
      ? hoverDate.value : null
    const isTempEnd = tempEnd && dateStr === tempEnd

    const classes = {
      'is-start': isStart,
      'is-end': isEnd || !!isTempEnd,
      'in-range': inR,
      'is-today': isToday && !isStart && !isEnd,
      'is-single-selected': !rangeMode.value && isStart,
      'is-disabled': disabled
    }

    let hint = ''
    if (rangeMode.value) {
      if (isStart) hint = '入住'
      else if (isEnd) hint = '离店'
      else if (!startDate.value && isToday) hint = '今天'
      else if (isTempEnd) hint = `${nightsPreview.value}晚`
    } else if (isStart && isToday) hint = '今天'

    cells.push({
      type: 'day',
      key: dateStr,
      day,
      dateStr,
      disabled,
      classes,
      hint,
      ariaLabel: `${viewYear.value}年${viewMonth.value}月${day}日`
    })
  }
  return cells
})

// hover 预览晚数
const nightsPreview = computed(() => {
  if (!startDate.value || !hoverDate.value || hoverDate.value <= startDate.value) return 0
  const s = parseStr(startDate.value)
  const e = parseStr(hoverDate.value)
  const ms = new Date(e.y, e.m - 1, e.d) - new Date(s.y, s.m - 1, s.d)
  return Math.max(0, Math.round(ms / 86400000))
})

/* ===================== 交互 ===================== */
const togglePanel = () => {
  panelVisible.value = !panelVisible.value
  if (panelVisible.value) {
    // 打开时定位到含选中日期或当前 min 的月份
    const target = parseStr(startDate.value) || parseStr(minLimit.value) || todayObj
    viewYear.value = target.y
    viewMonth.value = target.m
  }
}

const emitChange = () => {
  if (rangeMode.value) {
    if (startDate.value) emit('dateChange', { [startField.value.name]: startDate.value })
    if (endDate.value) emit('dateChange', { [endField.value.name]: endDate.value })
  } else if (startDate.value) {
    emit('dateChange', { [startField.value.name]: startDate.value })
  }
}

const selectDate = (cell) => {
  if (cell.disabled) return
  if (!rangeMode.value) {
    // 单日选择（景点）
    startDate.value = cell.dateStr
    emitChange()
    panelVisible.value = false
    return
  }
  // 范围选择逻辑
  if (!startDate.value || endDate.value || cell.dateStr < startDate.value) {
    // 开始新选择
    startDate.value = cell.dateStr
    endDate.value = ''
  } else if (cell.dateStr === startDate.value) {
    // 点击同一天，视为只选入住（保持）
    return
  } else {
    endDate.value = cell.dateStr
    emitChange()
    // 选完范围自动关闭（高德行为）
    panelVisible.value = false
  }
}

const applyQuickOption = (opt) => {
  if (opt.start < minLimit.value) return
  startDate.value = opt.start
  endDate.value = opt.end
  emitChange()
  panelVisible.value = false
}

const canGoPrev = computed(() => {
  const min = parseStr(minLimit.value)
  return viewYear.value > min.y || (viewYear.value === min.y && viewMonth.value > min.m)
})

const canGoNext = computed(() => {
  const max = parseStr(maxLimit.value)
  if (!max) return true
  return viewYear.value < max.y || (viewYear.value === max.y && viewMonth.value < max.m)
})

const prevMonth = () => {
  if (!canGoPrev.value) return
  viewMonth.value--
  if (viewMonth.value === 0) {
    viewMonth.value = 12
    viewYear.value--
  }
}

const nextMonth = () => {
  if (!canGoNext.value) return
  viewMonth.value++
  if (viewMonth.value === 13) {
    viewMonth.value = 1
    viewYear.value++
  }
}

/* ===================== 外部点击关闭 ===================== */
const handleClickOutside = (e) => {
  if (rootEl.value && !rootEl.value.contains(e.target)) {
    panelVisible.value = false
  }
}
onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside))

// 外部字段变化时同步（例如模块重置）
watch(() => props.dateFields, () => {
  initFromProps()
}, { deep: true })
</script>

<style scoped>
/* ============ 触发器（高德样式：入住 | 1晚 | 离店） ============ */
.date-picker-component {
  position: relative;
  margin-bottom: 32px;
}

.dp-trigger {
  display: flex;
  align-items: stretch;
  width: 100%;
  height: 56px;
  padding: 8px 12px;
  box-sizing: border-box;
  background: #ffffff;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  cursor: pointer;
  text-align: left;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  font-family: inherit;
}

.dp-trigger:hover {
  border-color: #c7c7cc;
}

.dp-trigger:focus-visible,
.dp-trigger.is-open {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
  border-color: #2997ff;
}

.dp-cell {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 2px;
  padding: 0 8px;
}

.dp-label {
  font-size: 11px;
  font-weight: 500;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.dp-value {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dp-value.is-placeholder {
  color: #aeaeb2;
  font-weight: 400;
}

.dp-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  align-self: center;
  width: 56px;
  color: #2997ff;
}

.dp-divider svg {
  width: 20px;
  height: 20px;
}

.dp-nights-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  padding: 2px 8px;
  background: rgba(41, 151, 255, 0.1);
  color: #2997ff;
  font-size: 12px;
  font-weight: 600;
  border-radius: 40px;
  white-space: nowrap;
}

/* ============ 日历面板 ============ */
.dp-panel {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  z-index: 100;
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08), 0 16px 40px -12px rgba(0, 0, 0, 0.18);
  padding: 16px;
  box-sizing: border-box;
  animation: dp-fade-in 0.18s ease;
}

@keyframes dp-fade-in {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}

.dp-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.dp-panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
}

.dp-nav-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 10px;
  background: #f5f5f7;
  color: #6e6e73;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.dp-nav-btn:hover:not(:disabled) {
  background: #ebebef;
  color: #1d1d1f;
}

.dp-nav-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.dp-nav-btn svg {
  width: 16px;
  height: 16px;
}

.dp-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 4px;
}

.dp-weekday {
  text-align: center;
  font-size: 12px;
  font-weight: 500;
  color: #6e6e73;
  padding: 6px 0;
}

.dp-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.dp-day {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 6px 0 4px;
  min-height: 44px;
  border: none;
  background: transparent;
  border-radius: 10px;
  cursor: pointer;
  font-family: inherit;
  transition: background 0.15s ease, color 0.15s ease;
}

.dp-day.is-blank {
  cursor: default;
  pointer-events: none;
}

.dp-day.is-disabled {
  color: #c7c7cc;
  cursor: not-allowed;
}

.dp-day-num {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  line-height: 1.5;
}

.dp-day.is-disabled .dp-day-num {
  color: #c7c7cc;
}

.dp-day:not(.is-disabled):not(.is-start):not(.is-end):hover {
  background: #f5f5f7;
}

/* 今天 */
.dp-day.is-today .dp-day-num {
  color: #2997ff;
  font-weight: 600;
}

/* 范围内 */
.dp-day.in-range {
  background: rgba(41, 151, 255, 0.08);
  border-radius: 0;
}

/* 开始/结束（圆角只留外侧，胶囊连接效果） */
.dp-day.is-start {
  background: #2997ff;
  border-radius: 10px 0 0 10px;
}

.dp-day.is-end {
  background: #2997ff;
  border-radius: 0 10px 10px 0;
}

/* 单选（景点）或首尾同格 */
.dp-day.is-start.is-end,
.dp-day.is-single-selected {
  background: #2997ff;
  border-radius: 10px;
}

.dp-day.is-start .dp-day-num,
.dp-day.is-end .dp-day-num {
  color: #ffffff;
  font-weight: 600;
}

.dp-day-hint {
  font-size: 10px;
  line-height: 1.4;
  color: #2997ff;
  white-space: nowrap;
}

.dp-day.is-start .dp-day-hint,
.dp-day.is-end .dp-day-hint {
  color: rgba(255, 255, 255, 0.9);
}

.dp-day.in-range .dp-day-hint {
  color: #2997ff;
}

/* ============ 底部 ============ */
.dp-panel-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f2f2f4;
  flex-wrap: wrap;
}

.dp-quick-btn {
  padding: 5px 12px;
  border: 1px solid #d2d2d6;
  border-radius: 40px;
  background: #ffffff;
  color: #1d1d1f;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}

.dp-quick-btn:hover {
  border-color: #2997ff;
  color: #2997ff;
  background: rgba(41, 151, 255, 0.06);
}

.dp-footer-tip {
  margin-left: auto;
  font-size: 12px;
  color: #6e6e73;
}

/* ============ 响应式 ============ */
@media (max-width: 767px) {
  .date-picker-component {
    margin-bottom: 24px;
  }

  .dp-trigger {
    height: 52px;
  }

  .dp-panel {
    left: -16px;
    right: -16px;
  }

  .dp-day {
    min-height: 40px;
  }
}
</style>
