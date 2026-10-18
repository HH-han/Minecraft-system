<template>
  <div class="rw-page">
    <div class="rw-page-inner">
      <div class="rw-page-title">
        <h1>{{ $t('railway.timetable.title') }}</h1>
        <router-link to="/ticket" class="rw-back">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('railway.common.back') }}
        </router-link>
      </div>

      <div class="rw-panel">
        <div class="rw-mode-switch">
          <button type="button" :class="{ 'is-active': mode === 'timetable' }" @click="changeMode('timetable')">
            {{ $t('railway.search.timetable') }}
          </button>
          <button type="button" :class="{ 'is-active': mode === 'status' }" @click="changeMode('status')">
            {{ $t('railway.search.status') }}
          </button>
        </div>

        <div class="rw-search-bar">
          <el-input v-model="trainNo" :placeholder="$t('railway.timetable.placeholder')" clearable
            class="rw-train-input" @keyup.enter="query">
            <template #prefix>
              <el-icon><Tickets /></el-icon>
            </template>
          </el-input>
          <el-date-picker v-if="mode === 'timetable'" v-model="date" type="date" value-format="YYYY-MM-DD"
            format="YYYY-MM-DD" :placeholder="$t('railway.timetable.date')" class="rw-date-input" />
          <button type="button" class="rw-query-btn" @click="query">
            {{ $t('railway.timetable.query') }}
          </button>
        </div>

        <!-- 空提示 -->
        <p v-if="!result && !loading && !error" class="rw-tip">{{ $t('railway.timetable.empty') }}</p>

        <!-- 加载 -->
        <div v-if="loading" class="rw-state">
          <span class="rw-state-icon"><el-icon class="is-loading"><Loading /></el-icon></span>
          <p>{{ $t('railway.common.loading') }}</p>
        </div>

        <!-- 错误 -->
        <div v-else-if="error" class="rw-state">
          <span class="rw-state-icon"><el-icon><Warning /></el-icon></span>
          <h3>{{ $t('railway.timetable.notFound') }}</h3>
          <p>{{ error }}</p>
        </div>

        <!-- 时刻表 -->
        <template v-else-if="result && mode === 'timetable'">
          <div class="rw-timetable-head">
            <div class="rw-timetable-no">
              <strong>{{ result.trainNo }}</strong>
              <span>{{ $t(`railway.list.type.${result.trainType}`) }}</span>
            </div>
            <span class="rw-timetable-count">{{ result.stops?.length || 0 }} {{ $t('railway.timetable.station') }}</span>
          </div>

          <div class="rw-timeline">
            <div v-for="(stop, index) in result.stops" :key="stop.stopSeq" class="rw-stop"
              :class="{ 'is-first': index === 0, 'is-last': index === result.stops.length - 1 }">
              <span class="rw-stop-seq">{{ stop.stopSeq }}</span>
              <span class="rw-stop-line"><i></i></span>
              <div class="rw-stop-main">
                <strong>{{ stop.stationName }}</strong>
                <span v-if="stop.dayOffset > 0" class="rw-stop-day">
                  {{ $t('railway.timetable.day', { n: stop.dayOffset }) }}
                </span>
              </div>
              <div class="rw-stop-times">
                <span>{{ $t('railway.timetable.arrive') }} {{ stop.arriveTime || '--' }}</span>
                <span>{{ $t('railway.timetable.depart') }} {{ stop.departTime || '--' }}</span>
                <span v-if="stop.stopOver > 0" class="rw-stop-over">
                  {{ $t('railway.timetable.stopOver') }} {{ stop.stopOver }}′
                </span>
              </div>
            </div>
          </div>
        </template>

        <!-- 正晚点 -->
        <template v-else-if="result && mode === 'status'">
          <div class="rw-status-card" :class="result.status === 'ON_TIME' ? 'is-ontime' : 'is-delayed'">
            <span class="rw-status-icon">
              <el-icon><component :is="result.status === 'ON_TIME' ? CircleCheckFilled : WarningFilled" /></el-icon>
            </span>
            <h3>{{ result.trainNo }}</h3>
            <p class="rw-status-text">
              {{ result.status === 'ON_TIME'
                ? $t('railway.status.onTime')
                : $t('railway.status.delayed', { n: result.delayMinutes }) }}
            </p>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled, WarningFilled } from '@element-plus/icons-vue'
import { getRailwayStatus, getRailwayTimetable } from '@/api/railway'
import { queryString } from './utils'

const route = useRoute()
const { t } = useI18n()

const mode = ref(queryString(route.query.mode, 'timetable') === 'status' ? 'status' : 'timetable')
const trainNo = ref(queryString(route.query.trainNo, ''))
const date = ref('')
const result = ref(null)
const loading = ref(false)
const error = ref('')

const changeMode = (value) => {
  mode.value = value
  result.value = null
  error.value = ''
}

const query = async () => {
  const keyword = trainNo.value.trim()
  if (!keyword) {
    ElMessage.warning(t('railway.timetable.empty'))
    return
  }
  loading.value = true
  error.value = ''
  result.value = null
  try {
    if (mode.value === 'timetable') {
      const res = await getRailwayTimetable({ trainNo: keyword, date: date.value || undefined })
      result.value = res?.data || null
    } else {
      const res = await getRailwayStatus(keyword)
      result.value = res?.data || null
    }
  } catch (err) {
    error.value = err?.message || t('railway.timetable.notFound')
  } finally {
    loading.value = false
  }
}

watch(
  () => [route.query.trainNo, route.query.mode],
  ([queryTrainNo, queryMode]) => {
    const nextNo = queryString(queryTrainNo, '')
    const nextMode = queryString(queryMode, 'timetable') === 'status' ? 'status' : 'timetable'
    if (nextMode !== mode.value) {
      mode.value = nextMode
      result.value = null
    }
    if (nextNo) {
      trainNo.value = nextNo
      query()
    }
  },
  { immediate: true },
)
</script>

<style scoped>
.rw-page {
  padding: 30px 24px 0;
}

.rw-page-inner {
  max-width: 860px;
  margin: 0 auto;
}

.rw-page-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.rw-page-title h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--rw-text);
}

.rw-back {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--rw-text-2);
  text-decoration: none;
}

.rw-back:hover {
  color: var(--rw-blue);
}

.rw-panel {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 18px;
  padding: 22px 24px 26px;
}

.rw-mode-switch {
  display: inline-flex;
  gap: 4px;
  background: var(--rw-soft-2);
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 18px;
}

.rw-mode-switch button {
  border: none;
  background: none;
  padding: 7px 18px;
  border-radius: 8px;
  font-size: 13px;
  color: var(--rw-text-2);
  cursor: pointer;
  font-family: inherit;
}

.rw-mode-switch button.is-active {
  background: var(--rw-card);
  color: var(--rw-blue);
  font-weight: 600;
  box-shadow: 0 2px 8px -4px rgba(16, 47, 100, 0.35);
}

.rw-search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
}

.rw-train-input {
  flex: 1;
  max-width: 300px;
}

.rw-search-bar :deep(.rw-date-input) {
  width: 160px;
  flex-shrink: 0;
}

.rw-search-bar :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: inset 0 0 0 1px var(--rw-border);
  background: var(--rw-soft-2);
  height: 40px;
}

.rw-query-btn {
  height: 40px;
  padding: 0 28px;
  border: none;
  border-radius: 10px;
  background: var(--rw-orange);
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  letter-spacing: 0.08em;
}

.rw-query-btn:hover {
  background: var(--rw-orange-hover);
}

.rw-tip {
  margin: 26px 0 6px;
  font-size: 13px;
  color: var(--rw-text-3);
  text-align: center;
}

/* 时刻表 */
.rw-timetable-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24px;
  padding: 16px 18px;
  border-radius: 14px;
  background: var(--rw-soft-2);
}

.rw-timetable-no {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.rw-timetable-no strong {
  font-size: 22px;
  font-weight: 700;
  color: var(--rw-text);
}

.rw-timetable-no span {
  font-size: 12px;
  color: var(--rw-blue);
}

.rw-timetable-count {
  font-size: 12px;
  color: var(--rw-text-3);
}

.rw-timeline {
  margin-top: 18px;
}

.rw-stop {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 60px;
}

.rw-stop-seq {
  width: 22px;
  font-size: 11px;
  color: var(--rw-text-3);
  text-align: right;
  font-variant-numeric: tabular-nums;
}

.rw-stop-line {
  width: 12px;
  align-self: stretch;
  position: relative;
  display: flex;
  justify-content: center;
}

.rw-stop-line::before {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  width: 1px;
  background: var(--rw-border);
}

.rw-stop.is-first .rw-stop-line::before {
  top: 50%;
}

.rw-stop.is-last .rw-stop-line::before {
  bottom: 50%;
}

.rw-stop-line i {
  position: relative;
  z-index: 1;
  align-self: center;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--rw-card);
  border: 2px solid var(--rw-blue);
}

.rw-stop.is-first .rw-stop-line i,
.rw-stop.is-last .rw-stop-line i {
  background: var(--rw-blue);
}

.rw-stop-main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.rw-stop-main strong {
  font-size: 15px;
  font-weight: 600;
  color: var(--rw-text);
}

.rw-stop-day {
  font-size: 10.5px;
  color: var(--rw-orange);
  background: rgba(255, 138, 31, 0.12);
  padding: 1px 6px;
  border-radius: 4px;
}

.rw-stop-times {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12.5px;
  color: var(--rw-text-2);
  font-variant-numeric: tabular-nums;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.rw-stop-over {
  color: var(--rw-text-3);
  font-size: 11.5px;
}

/* 正晚点 */
.rw-status-card {
  margin-top: 24px;
  padding: 40px 24px;
  border-radius: 16px;
  text-align: center;
}

.rw-status-card.is-ontime {
  background: rgba(16, 185, 129, 0.08);
  border: 1px solid rgba(16, 185, 129, 0.24);
}

.rw-status-card.is-delayed {
  background: rgba(255, 138, 31, 0.08);
  border: 1px solid rgba(255, 138, 31, 0.26);
}

.rw-status-icon {
  font-size: 42px;
  display: inline-flex;
}

.is-ontime .rw-status-icon {
  color: var(--rw-success);
}

.is-delayed .rw-status-icon {
  color: var(--rw-orange);
}

.rw-status-card h3 {
  margin: 12px 0 6px;
  font-size: 22px;
  font-weight: 700;
  color: var(--rw-text);
}

.rw-status-text {
  margin: 0;
  font-size: 14px;
  color: var(--rw-text-2);
}

/* 状态占位 */
.rw-state {
  padding: 44px 16px 20px;
  text-align: center;
  color: var(--rw-text-2);
}

.rw-state-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 12px;
}

.rw-state h3 {
  margin: 0 0 6px;
  font-size: 16px;
  color: var(--rw-text);
}

.rw-state p {
  margin: 0;
  font-size: 13px;
}

@media (max-width: 640px) {
  .rw-page {
    padding: 16px 14px 0;
  }

  .rw-page-title h1 {
    font-size: 20px;
  }

  .rw-panel {
    padding: 16px;
  }

  .rw-search-bar {
    flex-wrap: wrap;
  }

  .rw-train-input {
    max-width: none;
    flex-basis: 100%;
  }

  .rw-date-input {
    flex: 1;
  }

  .rw-stop-times {
    flex-direction: column;
    align-items: flex-end;
    gap: 2px;
  }
}
</style>
