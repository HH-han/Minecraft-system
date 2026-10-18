<template>
  <div class="rw-search-card" :class="[`rw-search-card--${variant}`]">
    <div v-if="variant === 'hero'" class="rw-tabs">
      <button v-for="tab in tabs" :key="tab.value" type="button" class="rw-tab"
        :class="{ 'is-active': activeTab === tab.value }" @click="activeTab = tab.value">
        {{ $t(`railway.search.${tab.label}`) }}
      </button>
    </div>

    <!-- 车票查询 -->
    <template v-if="activeTab === 'ticket'">
      <div class="rw-trip-types">
        <button v-for="option in tripOptions" :key="option.value" type="button" class="rw-trip-type"
          :class="{ 'is-active': tripType === option.value }" @click="handleTripType(option.value)">
          <span class="rw-radio-dot"></span>
          {{ $t(`railway.search.${option.label}`) }}
        </button>
      </div>

      <div class="rw-fields">
        <div class="rw-stations">
          <div class="rw-field rw-field--station">
            <label>{{ $t('railway.search.from') }}</label>
            <el-autocomplete v-model="fromStation" :fetch-suggestions="fetchStations" value-key="stationName"
              :placeholder="$t('railway.search.placeholderFrom')" :trigger-on-focus="false" :debounce="280"
              clearable class="rw-autocomplete">
              <template #prefix>
                <el-icon class="rw-field-icon"><Location /></el-icon>
              </template>
              <template #default="{ item }">
                <div class="rw-station-option">
                  <span class="rw-station-name">{{ item.stationName }}</span>
                  <span class="rw-station-city">{{ item.cityName }}</span>
                  <span class="rw-station-code">{{ item.stationCode }}</span>
                </div>
              </template>
            </el-autocomplete>
          </div>

          <button type="button" class="rw-swap" :title="$t('railway.search.swap')" @click="swapStations">
            <el-icon><Switch /></el-icon>
          </button>

          <div class="rw-field rw-field--station">
            <label>{{ $t('railway.search.to') }}</label>
            <el-autocomplete v-model="toStation" :fetch-suggestions="fetchStations" value-key="stationName"
              :placeholder="$t('railway.search.placeholderTo')" :trigger-on-focus="false" :debounce="280"
              clearable class="rw-autocomplete">
              <template #prefix>
                <el-icon class="rw-field-icon"><Location /></el-icon>
              </template>
              <template #default="{ item }">
                <div class="rw-station-option">
                  <span class="rw-station-name">{{ item.stationName }}</span>
                  <span class="rw-station-city">{{ item.cityName }}</span>
                  <span class="rw-station-code">{{ item.stationCode }}</span>
                </div>
              </template>
            </el-autocomplete>
          </div>
        </div>

        <div class="rw-field rw-field--date">
          <label>{{ $t('railway.search.date') }}</label>
          <el-date-picker v-model="departDate" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
            :placeholder="$t('railway.search.date')" :disabled-date="disabledDate" :clearable="false"
            class="rw-date-picker" />
        </div>

        <div v-if="tripType === 'round'" class="rw-field rw-field--date">
          <label>{{ $t('railway.search.returnDate') }}</label>
          <el-date-picker v-model="returnDate" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
            :placeholder="$t('railway.search.returnDate')" :disabled-date="disabledDate" :clearable="false"
            class="rw-date-picker" />
        </div>

        <button type="button" class="rw-submit" @click="submit">
          <el-icon class="rw-submit-icon"><Search /></el-icon>
          <span>{{ $t('railway.search.query') }}</span>
        </button>
      </div>

      <div class="rw-options">
        <el-checkbox v-model="studentTicket">{{ $t('railway.search.student') }}</el-checkbox>
        <el-checkbox v-model="highSpeedOnly">{{ $t('railway.search.highSpeed') }}</el-checkbox>
        <span class="rw-range-hint">{{ $t('railway.search.dateRangeHint') }}</span>
      </div>
    </template>

    <!-- 常用查询 -->
    <template v-else-if="activeTab === 'query'">
      <div class="rw-query-grid">
        <button type="button" class="rw-query-card" @click="goTimetable">
          <span class="rw-query-icon"><el-icon><Clock /></el-icon></span>
          <span class="rw-query-main">
            <strong>{{ $t('railway.search.timetable') }}</strong>
            <small>{{ $t('railway.search.timetableDesc') }}</small>
          </span>
          <el-icon class="rw-query-arrow"><ArrowRight /></el-icon>
        </button>
        <button type="button" class="rw-query-card" @click="goStatus">
          <span class="rw-query-icon"><el-icon><Bell /></el-icon></span>
          <span class="rw-query-main">
            <strong>{{ $t('railway.search.status') }}</strong>
            <small>{{ $t('railway.search.statusDesc') }}</small>
          </span>
          <el-icon class="rw-query-arrow"><ArrowRight /></el-icon>
        </button>
      </div>
    </template>

    <!-- 订餐 -->
    <template v-else>
      <div class="rw-food-panel">
        <span class="rw-food-icon"><el-icon><KnifeFork /></el-icon></span>
        <div>
          <strong>{{ $t('railway.search.foodTitle') }}</strong>
          <p>{{ $t('railway.search.foodDesc') }}</p>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getRailwayStations } from '@/api/railway'
import { offsetDate, todayString } from '../utils'

const props = defineProps({
  variant: { type: String, default: 'hero' },
  initialFrom: { type: String, default: '' },
  initialTo: { type: String, default: '' },
  initialDate: { type: String, default: '' },
  initialReturnDate: { type: String, default: '' },
  initialTripType: { type: String, default: 'single' },
})

const emit = defineEmits(['search'])
const router = useRouter()
const { t } = useI18n()

const activeTab = ref('ticket')
const tabs = [
  { value: 'ticket', label: 'tabTicket' },
  { value: 'query', label: 'tabQuery' },
  { value: 'food', label: 'tabFood' },
]

const tripOptions = [
  { value: 'single', label: 'single' },
  { value: 'round', label: 'round' },
  { value: 'transfer', label: 'transfer' },
  { value: 'refund', label: 'refund' },
]

const tripType = ref(['single', 'round'].includes(props.initialTripType) ? props.initialTripType : 'single')
const fromStation = ref(props.initialFrom || '')
const toStation = ref(props.initialTo || '')
const departDate = ref(props.initialDate || todayString())
const returnDate = ref(props.initialReturnDate || offsetDate(1))
const studentTicket = ref(false)
const highSpeedOnly = ref(false)

watch(
  () => props.initialFrom,
  (value) => {
    if (value !== undefined && value !== fromStation.value) fromStation.value = value || ''
  },
)
watch(
  () => props.initialTo,
  (value) => {
    if (value !== undefined && value !== toStation.value) toStation.value = value || ''
  },
)
watch(
  () => props.initialDate,
  (value) => {
    if (value && value !== departDate.value) departDate.value = value
  },
)
watch(
  () => props.initialReturnDate,
  (value) => {
    if (value && value !== returnDate.value) returnDate.value = value
  },
)
watch(
  () => props.initialTripType,
  (value) => {
    if (['single', 'round'].includes(value) && value !== tripType.value) tripType.value = value
  },
)

const fetchStations = async (queryString, callback) => {
  if (!queryString) {
    callback([])
    return
  }
  try {
    const res = await getRailwayStations(queryString)
    const list = (res?.data || []).map((item) => ({
      ...item,
      value: item.stationName,
    }))
    callback(list)
  } catch {
    callback([])
  }
}

const disabledDate = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const max = new Date(today)
  max.setDate(max.getDate() + 15)
  return date.getTime() < today.getTime() || date.getTime() > max.getTime()
}

const swapStations = () => {
  const from = fromStation.value
  fromStation.value = toStation.value
  toStation.value = from
}

const handleTripType = (value) => {
  if (value === 'transfer' || value === 'refund') {
    ElMessage.info(t('railway.search.notOpen'))
    return
  }
  tripType.value = value
}

const submit = () => {
  const from = fromStation.value?.trim()
  const to = toStation.value?.trim()
  if (!from) {
    ElMessage.warning(t('railway.search.needFrom'))
    return
  }
  if (!to) {
    ElMessage.warning(t('railway.search.needTo'))
    return
  }
  if (from === to) {
    ElMessage.warning(t('railway.search.sameCity'))
    return
  }
  if (!departDate.value) {
    ElMessage.warning(t('railway.search.needDate'))
    return
  }
  emit('search', {
    fromStation: from,
    toStation: to,
    date: departDate.value,
    returnDate: tripType.value === 'round' ? returnDate.value : '',
    tripType: tripType.value,
    highSpeed: highSpeedOnly.value,
    student: studentTicket.value,
  })
}

const goTimetable = () => router.push('/ticket/timetable')
const goStatus = () => router.push({ path: '/ticket/timetable', query: { mode: 'status' } })
</script>

<style scoped>
.rw-search-card {
  background: var(--rw-card);
  border-radius: 20px;
  box-shadow: var(--rw-shadow);
  border: 1px solid rgba(227, 235, 247, 0.9);
  padding: 20px 24px 22px;
  text-align: left;
}

.rw-search-card--compact {
  padding: 16px 18px;
  border-radius: 14px;
  box-shadow: 0 10px 30px -22px rgba(16, 47, 100, 0.4);
}

/* 标签页 */
.rw-tabs {
  display: flex;
  gap: 6px;
  border-bottom: 1px solid var(--rw-border);
  margin-bottom: 16px;
}

.rw-tab {
  border: none;
  background: none;
  padding: 8px 4px 12px;
  margin-right: 18px;
  font-size: 15px;
  color: var(--rw-text-2);
  cursor: pointer;
  position: relative;
  font-family: inherit;
  letter-spacing: 0.04em;
}

.rw-tab.is-active {
  color: var(--rw-blue);
  font-weight: 600;
}

.rw-tab.is-active::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 2px;
  border-radius: 2px;
  background: var(--rw-blue);
}

/* 单程 / 往返 */
.rw-trip-types {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.rw-trip-type {
  border: none;
  background: none;
  padding: 0;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  font-size: 13px;
  color: var(--rw-text-2);
  cursor: pointer;
  font-family: inherit;
}

.rw-radio-dot {
  width: 15px;
  height: 15px;
  border-radius: 50%;
  border: 1.5px solid var(--rw-text-3);
  position: relative;
  transition: border-color 0.2s ease;
}

.rw-trip-type.is-active {
  color: var(--rw-text);
  font-weight: 600;
}

.rw-trip-type.is-active .rw-radio-dot {
  border-color: var(--rw-blue);
}

.rw-trip-type.is-active .rw-radio-dot::after {
  content: '';
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  background: var(--rw-blue);
}

/* 输入区 */
.rw-fields {
  display: flex;
  align-items: flex-end;
  gap: 14px;
}

.rw-stations {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  flex: 1;
  min-width: 0;
}

.rw-field {
  display: flex;
  flex-direction: column;
  gap: 7px;
  min-width: 0;
}

.rw-field label {
  font-size: 12px;
  color: var(--rw-text-3);
  letter-spacing: 0.08em;
  white-space: nowrap;
}

.rw-field--date {
  width: 176px;
  flex-shrink: 0;
}

.rw-autocomplete {
  display: block;
  width: 100%;
}

/* 提高权重覆盖 Element Plus 的 .el-date-editor.el-input 默认 220px 宽 / 32px 高 */
.rw-field--date :deep(.el-date-editor) {
  width: 100%;
  height: 42px;
}

.rw-search-card--compact .rw-field--date :deep(.el-date-editor) {
  height: 38px;
}

.rw-search-card :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: inset 0 0 0 1px var(--rw-border);
  background: var(--rw-soft-2);
  height: 42px;
  padding: 0 12px;
}

.rw-search-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: inset 0 0 0 1px var(--rw-blue);
  background: var(--rw-card);
}

.rw-search-card :deep(.el-input__inner) {
  font-size: 15px;
  font-weight: 500;
  color: var(--rw-text);
}

.rw-field-icon {
  color: var(--rw-text-3);
  font-size: 15px;
}

.rw-station-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 3px 0;
}

.rw-station-name {
  font-size: 14px;
  color: var(--rw-text);
  font-weight: 500;
}

.rw-station-city {
  font-size: 12px;
  color: var(--rw-text-3);
}

.rw-station-code {
  margin-left: auto;
  font-size: 11px;
  color: var(--rw-text-3);
  letter-spacing: 0.08em;
}

.rw-swap {
  position: absolute;
  left: calc(50% - 17px);
  bottom: 4px;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: 1px solid var(--rw-border);
  background: var(--rw-card);
  color: var(--rw-blue);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
  box-shadow: 0 4px 12px -6px rgba(16, 47, 100, 0.35);
  transition: transform 0.25s ease, color 0.2s ease;
}

.rw-swap:hover {
  transform: rotate(180deg);
  color: var(--rw-orange);
}

.rw-submit {
  height: 46px;
  padding: 0 34px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #ff9a2e, var(--rw-orange));
  color: #ffffff;
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 0.28em;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  flex-shrink: 0;
  box-shadow: 0 12px 24px -14px rgba(255, 138, 31, 0.9);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  font-family: inherit;
}

.rw-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 30px -14px rgba(255, 138, 31, 0.95);
}

.rw-submit-icon {
  font-size: 17px;
}

.rw-options {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-top: 14px;
  flex-wrap: wrap;
}

.rw-options :deep(.el-checkbox__label) {
  font-size: 12.5px;
  color: var(--rw-text-2);
}

.rw-options :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: var(--rw-blue);
}

.rw-range-hint {
  margin-left: auto;
  font-size: 12px;
  color: var(--rw-text-3);
}

/* 常用查询 / 订餐 */
.rw-query-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  padding-top: 4px;
}

.rw-query-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px;
  border-radius: 14px;
  border: 1px solid var(--rw-border);
  background: var(--rw-soft-2);
  cursor: pointer;
  text-align: left;
  font-family: inherit;
  transition: border-color 0.2s ease, transform 0.2s ease;
}

.rw-query-card:hover {
  border-color: var(--rw-blue);
  transform: translateY(-2px);
}

.rw-query-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 19px;
  flex-shrink: 0;
}

.rw-query-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.rw-query-main strong {
  font-size: 14px;
  color: var(--rw-text);
}

.rw-query-main small {
  font-size: 12px;
  color: var(--rw-text-3);
  line-height: 1.5;
}

.rw-query-arrow {
  margin-left: auto;
  color: var(--rw-text-3);
}

.rw-food-panel {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px 6px;
}

.rw-food-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: var(--rw-soft);
  color: var(--rw-orange);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.rw-food-panel strong {
  font-size: 15px;
  color: var(--rw-text);
}

.rw-food-panel p {
  margin: 5px 0 0;
  font-size: 12.5px;
  color: var(--rw-text-3);
  line-height: 1.6;
}

/* Hero 变体：卡片宽度有限，站点独占一行，日期与查询按钮排第二行 */
.rw-search-card--hero .rw-fields {
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 12px 14px;
}

.rw-search-card--hero .rw-stations {
  flex: 1 1 100%;
}

.rw-search-card--hero .rw-field--date {
  flex: 1 1 150px;
  width: auto;
  min-width: 0;
  max-width: 220px;
}

.rw-search-card--hero .rw-submit {
  flex: 0 0 auto;
  min-width: 132px;
  margin-left: auto;
}

/* 紧凑模式 */
.rw-search-card--compact .rw-fields {
  gap: 10px;
}

.rw-search-card--compact :deep(.el-input__wrapper) {
  height: 38px;
}

.rw-search-card--compact .rw-submit {
  height: 40px;
  padding: 0 24px;
  font-size: 15px;
  letter-spacing: 0.16em;
}

.rw-search-card--compact .rw-field--date {
  width: 150px;
}

.rw-search-card--compact .rw-options {
  margin-top: 10px;
}

.rw-search-card--compact .rw-swap {
  bottom: 2px;
}

@media (max-width: 960px) {
  .rw-fields {
    flex-wrap: wrap;
  }

  .rw-stations {
    flex-basis: 100%;
  }

  .rw-field--date {
    flex: 1;
    width: auto;
    min-width: 140px;
  }

  .rw-submit {
    flex: 1;
    min-width: 140px;
  }
}

@media (max-width: 640px) {
  .rw-search-card {
    padding: 16px;
    border-radius: 16px;
  }

  .rw-stations {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .rw-swap {
    display: none;
  }

  .rw-field--date {
    flex-basis: 100%;
  }

  .rw-search-card--hero .rw-field--date {
    max-width: none;
  }

  .rw-submit {
    flex-basis: 100%;
  }

  .rw-trip-types {
    gap: 14px;
  }

  .rw-query-grid {
    grid-template-columns: 1fr;
  }

  .rw-range-hint {
    margin-left: 0;
    flex-basis: 100%;
  }
}
</style>
