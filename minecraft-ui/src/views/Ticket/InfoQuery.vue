<template>
  <div class="rw-svc-page">
    <ServiceHero :eyebrow="$t('railway.infoPage.eyebrow')" :title="$t('railway.infoPage.title')"
      :desc="$t('railway.infoPage.desc')" :icon="DataAnalysis" tone="deep" />

    <div class="rw-svc-body">
      <section class="rw-svc-card">
        <div class="rw-svc-tabs">
          <button v-for="tab in tabs" :key="tab.value" type="button" class="rw-svc-tab"
            :class="{ 'is-active': activeTab === tab.value }" @click="activeTab = tab.value">
            {{ $t(`railway.infoPage.${tab.label}`) }}
          </button>
        </div>

        <!-- 正晚点 -->
        <template v-if="activeTab === 'status'">
          <div class="rw-if-bar">
            <el-input v-model="trainNo" :placeholder="$t('railway.infoPage.trainNoPlaceholder')" clearable
              class="rw-if-train-input" @keyup.enter="queryStatus">
              <template #prefix>
                <el-icon><Tickets /></el-icon>
              </template>
            </el-input>
            <button type="button" class="rw-svc-btn rw-svc-btn--blue" :disabled="statusLoading"
              @click="queryStatus">
              {{ $t('railway.infoPage.query') }}
            </button>
          </div>

          <div v-if="statusLoading" class="rw-svc-loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            {{ $t('railway.common.loading') }}
          </div>

          <div v-else-if="statusError" class="rw-svc-empty">
            <span class="rw-svc-empty-icon"><el-icon><Warning /></el-icon></span>
            <h4>{{ $t('railway.infoPage.loadFailed') }}</h4>
            <p>{{ statusError }}</p>
          </div>

          <template v-else-if="statusResult">
            <div class="rw-if-result" :class="statusResult.status === 'ON_TIME' ? 'is-ontime' : 'is-delayed'">
              <span class="rw-if-result-icon">
                <el-icon>
                  <component :is="statusResult.status === 'ON_TIME' ? CircleCheckFilled : WarningFilled" />
                </el-icon>
              </span>
              <h3>{{ statusResult.trainNo }}</h3>
              <p>
                {{ statusResult.status === 'ON_TIME'
                  ? $t('railway.infoPage.onTime')
                  : $t('railway.infoPage.delayed', { n: statusResult.delayMinutes }) }}
              </p>
            </div>
          </template>

          <p v-else class="rw-if-tip">{{ $t('railway.infoPage.statusEmpty') }}</p>
        </template>

        <!-- 车站大屏 -->
        <template v-else-if="activeTab === 'board'">
          <div class="rw-if-filters">
            <div class="rw-svc-field">
              <label>{{ $t('railway.infoPage.boardStation') }}</label>
              <StationAutocomplete v-model="boardStation"
                :placeholder="$t('railway.infoPage.boardStation')" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.infoPage.boardDate') }}</label>
              <el-date-picker v-model="boardDate" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
                :disabled-date="disabledBoardDate" :clearable="false" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.infoPage.boardType') }}</label>
              <div class="rw-if-switch">
                <button v-for="option in boardTypes" :key="option.value" type="button"
                  :class="{ 'is-active': boardType === option.value }" @click="boardType = option.value">
                  {{ $t(`railway.infoPage.${option.label}`) }}
                </button>
              </div>
            </div>
            <button type="button" class="rw-svc-btn rw-svc-btn--blue rw-if-query" :disabled="boardLoading"
              @click="queryBoard">
              {{ $t('railway.infoPage.query') }}
            </button>
          </div>

          <div v-if="boardLoading" class="rw-svc-loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            {{ $t('railway.common.loading') }}
          </div>

          <div v-else-if="boardError" class="rw-svc-empty">
            <span class="rw-svc-empty-icon"><el-icon><Warning /></el-icon></span>
            <h4>{{ $t('railway.infoPage.loadFailed') }}</h4>
            <p>{{ boardError }}</p>
          </div>

          <div v-else-if="boardQueried && boardList.length === 0" class="rw-svc-empty">
            <span class="rw-svc-empty-icon"><el-icon><Monitor /></el-icon></span>
            <h4>{{ $t('railway.infoPage.boardEmpty') }}</h4>
            <p>{{ $t('railway.infoPage.boardEmptyDesc') }}</p>
          </div>

          <div v-else-if="boardList.length" class="rw-if-board">
            <div class="rw-if-board-head">
              <span>{{ $t('railway.infoPage.expectedTime') }}</span>
              <span>{{ $t('railway.infoPage.trainNo') }}</span>
              <span>{{ $t('railway.infoPage.route') }}</span>
              <span>{{ $t('railway.infoPage.status') }}</span>
              <span>{{ $t('railway.infoPage.platform') }}</span>
            </div>
            <div v-for="row in boardList" :key="row.serviceId + row.planTime" class="rw-if-board-row">
              <div class="rw-if-time">
                <strong>{{ row.planTime }}</strong>
                <small v-if="row.delayMinutes > 0">{{ row.expectedTime }}</small>
              </div>
              <div class="rw-if-train">
                <strong>{{ row.trainNo }}</strong>
                <small>{{ row.trainType }}</small>
              </div>
              <div class="rw-if-route">
                <span>{{ row.originStation }}</span>
                <em>→</em>
                <span>{{ row.terminalStation }}</span>
              </div>
              <div class="rw-if-status">
                <span class="rw-svc-status" :class="`rw-svc-status--${row.status}`">
                  {{ $t(`railway.infoPage.boardStatus.${row.status}`) }}
                </span>
              </div>
              <div class="rw-if-platform">{{ row.platform }}</div>
            </div>
          </div>

          <p v-else class="rw-if-tip">{{ $t('railway.infoPage.boardEmpty') }}</p>
        </template>

        <!-- 票价查询 -->
        <template v-else>
          <div class="rw-if-filters">
            <div class="rw-svc-field">
              <label>{{ $t('railway.infoPage.faresFrom') }}</label>
              <StationAutocomplete v-model="fareFrom" :placeholder="$t('railway.search.placeholderFrom')" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.infoPage.faresTo') }}</label>
              <StationAutocomplete v-model="fareTo" :placeholder="$t('railway.search.placeholderTo')" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.infoPage.faresDate') }}</label>
              <el-date-picker v-model="fareDate" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD"
                :disabled-date="disabledFareDate" :clearable="false" />
            </div>
            <button type="button" class="rw-svc-btn rw-svc-btn--blue rw-if-query" :disabled="fareLoading"
              @click="queryFares">
              {{ $t('railway.infoPage.query') }}
            </button>
          </div>

          <div v-if="fareLoading" class="rw-svc-loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            {{ $t('railway.common.loading') }}
          </div>

          <div v-else-if="fareError" class="rw-svc-empty">
            <span class="rw-svc-empty-icon"><el-icon><Warning /></el-icon></span>
            <h4>{{ $t('railway.infoPage.loadFailed') }}</h4>
            <p>{{ fareError }}</p>
          </div>

          <div v-else-if="fareQueried && fareList.length === 0" class="rw-svc-empty">
            <span class="rw-svc-empty-icon"><el-icon><Money /></el-icon></span>
            <h4>{{ $t('railway.infoPage.faresEmpty') }}</h4>
            <p>{{ $t('railway.infoPage.faresEmptyDesc') }}</p>
          </div>

          <div v-else-if="fareList.length" class="rw-svc-list">
            <article v-for="train in fareList" :key="train.serviceId" class="rw-if-fare">
              <div class="rw-if-fare-head">
                <div class="rw-if-fare-train">
                  <strong>{{ train.trainNo }}</strong>
                  <span>{{ $t(`railway.list.type.${train.trainType}`) }}</span>
                </div>
                <div class="rw-if-fare-time">
                  <span>{{ train.fromTime }}</span>
                  <em>→</em>
                  <span>{{ train.toTime }}</span>
                  <small v-if="train.arriveDayOffset > 0">{{ $t('railway.infoPage.nextDay') }}</small>
                  <small>{{ formatDuration(train.durationMinutes, $t) }}</small>
                </div>
              </div>
              <div class="rw-if-seats">
                <div v-for="seat in train.seats" :key="seat.seatType" class="rw-if-seat">
                  <em>{{ $t(`railway.list.seat.${seat.seatType}`) }}</em>
                  <strong>¥{{ formatPrice(seat.priceCents) }}</strong>
                  <small :class="{ 'is-soldout': seat.leftSeats <= 0 }">
                    {{ seat.leftSeats > 0
                      ? $t('railway.infoPage.left', { n: seat.leftSeats })
                      : $t('railway.infoPage.soldOut') }}
                  </small>
                </div>
              </div>
            </article>
          </div>

          <p v-else class="rw-if-tip">{{ $t('railway.infoPage.faresEmpty') }}</p>
        </template>
      </section>

      <!-- 快捷入口 -->
      <section class="rw-svc-card" style="margin-top: 20px">
        <div class="rw-svc-card-head">
          <h2 class="rw-svc-card-title">{{ $t('railway.infoPage.quickLinks') }}</h2>
        </div>
        <div class="rw-if-quick">
          <button type="button" class="rw-if-quick-item" @click="$router.push('/ticket/timetable')">
            <span class="rw-if-quick-icon"><el-icon><Clock /></el-icon></span>
            <span>
              <strong>{{ $t('railway.infoPage.timetable') }}</strong>
              <small>{{ $t('railway.infoPage.timetableDesc') }}</small>
            </span>
            <el-icon class="rw-if-quick-arrow"><ArrowRight /></el-icon>
          </button>
          <button type="button" class="rw-if-quick-item" @click="$router.push('/ticket/guide')">
            <span class="rw-if-quick-icon"><el-icon><Reading /></el-icon></span>
            <span>
              <strong>{{ $t('railway.infoPage.guide') }}</strong>
              <small>{{ $t('railway.infoPage.guideDesc') }}</small>
            </span>
            <el-icon class="rw-if-quick-arrow"><ArrowRight /></el-icon>
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import {
  ArrowRight,
  CircleCheckFilled,
  Clock,
  DataAnalysis,
  Loading,
  Money,
  Monitor,
  Reading,
  Tickets,
  Warning,
  WarningFilled,
} from '@element-plus/icons-vue'
import ServiceHero from './components/ServiceHero.vue'
import StationAutocomplete from './components/StationAutocomplete.vue'
import { getRailwayBoard, getRailwayFares, getRailwayStatus } from '@/api/railway'
import { formatDuration, formatPrice, todayString } from './utils'

const { t } = useI18n()

const tabs = [
  { value: 'status', label: 'tabStatus' },
  { value: 'board', label: 'tabBoard' },
  { value: 'fares', label: 'tabFares' },
]

const boardTypes = [
  { value: 'DEPARTURE', label: 'departure' },
  { value: 'ARRIVAL', label: 'arrival' },
]

const activeTab = ref('status')

const trainNo = ref('')
const statusResult = ref(null)
const statusLoading = ref(false)
const statusError = ref('')

const boardStation = ref('')
const boardDate = ref(todayString())
const boardType = ref('DEPARTURE')
const boardList = ref([])
const boardLoading = ref(false)
const boardError = ref('')
const boardQueried = ref(false)

const fareFrom = ref('')
const fareTo = ref('')
const fareDate = ref(todayString())
const fareList = ref([])
const fareLoading = ref(false)
const fareError = ref('')
const fareQueried = ref(false)

const disabledBoardDate = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const max = new Date(today)
  max.setDate(max.getDate() + 15)
  return date.getTime() < today.getTime() || date.getTime() > max.getTime()
}

const disabledFareDate = disabledBoardDate

const queryStatus = async () => {
  const keyword = trainNo.value.trim()
  if (!keyword) {
    ElMessage.warning(t('railway.infoPage.statusEmpty'))
    return
  }
  statusLoading.value = true
  statusError.value = ''
  statusResult.value = null
  try {
    const res = await getRailwayStatus(keyword)
    statusResult.value = res?.data || null
  } catch (err) {
    statusError.value = err?.message || t('railway.infoPage.loadFailed')
  } finally {
    statusLoading.value = false
  }
}

const queryBoard = async () => {
  if (!boardStation.value?.trim()) {
    ElMessage.warning(t('railway.infoPage.boardStation'))
    return
  }
  boardLoading.value = true
  boardError.value = ''
  boardQueried.value = true
  try {
    const res = await getRailwayBoard({
      stationName: boardStation.value.trim(),
      date: boardDate.value,
      type: boardType.value,
    })
    boardList.value = res?.data || []
  } catch (err) {
    boardError.value = err?.message || t('railway.infoPage.loadFailed')
    boardList.value = []
  } finally {
    boardLoading.value = false
  }
}

const queryFares = async () => {
  if (!fareFrom.value?.trim()) {
    ElMessage.warning(t('railway.search.needFrom'))
    return
  }
  if (!fareTo.value?.trim()) {
    ElMessage.warning(t('railway.search.needTo'))
    return
  }
  if (fareFrom.value.trim() === fareTo.value.trim()) {
    ElMessage.warning(t('railway.search.sameCity'))
    return
  }
  fareLoading.value = true
  fareError.value = ''
  fareQueried.value = true
  try {
    const res = await getRailwayFares({
      fromStation: fareFrom.value.trim(),
      toStation: fareTo.value.trim(),
      date: fareDate.value,
    })
    fareList.value = res?.data || []
  } catch (err) {
    fareError.value = err?.message || t('railway.infoPage.loadFailed')
    fareList.value = []
  } finally {
    fareLoading.value = false
  }
}
</script>

<style scoped>
.rw-if-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rw-if-train-input {
  max-width: 300px;
}

.rw-if-bar :deep(.el-input__wrapper) {
  border-radius: 10px;
  background: var(--rw-soft-2);
  box-shadow: inset 0 0 0 1px var(--rw-border);
  height: 40px;
}

.rw-if-tip {
  margin: 30px 0 10px;
  text-align: center;
  font-size: 13px;
  color: var(--rw-text-3);
}

/* 正晚点 */
.rw-if-result {
  margin-top: 22px;
  padding: 36px 20px;
  border-radius: 16px;
  text-align: center;
}

.rw-if-result.is-ontime {
  background: rgba(16, 185, 129, 0.08);
  border: 1px solid rgba(16, 185, 129, 0.24);
}

.rw-if-result.is-delayed {
  background: rgba(255, 138, 31, 0.08);
  border: 1px solid rgba(255, 138, 31, 0.24);
}

.rw-if-result-icon {
  font-size: 40px;
  display: inline-flex;
}

.is-ontime .rw-if-result-icon {
  color: var(--rw-success);
}

.is-delayed .rw-if-result-icon {
  color: var(--rw-orange);
}

.rw-if-result h3 {
  margin: 10px 0 6px;
  font-size: 22px;
  color: var(--rw-text);
}

.rw-if-result p {
  margin: 0;
  font-size: 13.5px;
  color: var(--rw-text-2);
}

/* 筛选 */
.rw-if-filters {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr)) auto;
  gap: 14px;
  align-items: end;
}

.rw-if-query {
  margin-bottom: 1px;
}

.rw-if-switch {
  display: inline-flex;
  gap: 4px;
  padding: 4px;
  border-radius: 10px;
  background: var(--rw-soft-2);
  height: 40px;
  align-items: center;
}

.rw-if-switch button {
  border: none;
  background: none;
  height: 32px;
  padding: 0 16px;
  border-radius: 8px;
  font-family: inherit;
  font-size: 12.5px;
  color: var(--rw-text-2);
  cursor: pointer;
}

.rw-if-switch button.is-active {
  background: var(--rw-card);
  color: var(--rw-blue);
  font-weight: 600;
  box-shadow: 0 2px 8px -5px rgba(16, 47, 100, 0.4);
}

/* 大屏 */
.rw-if-board {
  margin-top: 20px;
  border: 1px solid var(--rw-border);
  border-radius: 14px;
  overflow: hidden;
}

.rw-if-board-head,
.rw-if-board-row {
  display: grid;
  grid-template-columns: 100px 110px minmax(0, 1.5fr) 110px 100px;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
}

.rw-if-board-head {
  background: var(--rw-soft-2);
  font-size: 11.5px;
  color: var(--rw-text-3);
  letter-spacing: 0.06em;
}

.rw-if-board-row {
  border-top: 1px solid var(--rw-border);
  transition: background 0.16s ease;
}

.rw-if-board-row:hover {
  background: var(--rw-soft-2);
}

.rw-if-time {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.rw-if-time strong {
  font-size: 16px;
  color: var(--rw-text);
  font-variant-numeric: tabular-nums;
}

.rw-if-time small {
  font-size: 11px;
  color: var(--rw-orange);
}

.rw-if-train {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.rw-if-train strong {
  font-size: 14px;
  color: var(--rw-text);
}

.rw-if-train small {
  font-size: 10.5px;
  color: var(--rw-blue);
}

.rw-if-route {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12.5px;
  color: var(--rw-text-2);
  min-width: 0;
}

.rw-if-route span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rw-if-route em {
  font-style: normal;
  color: var(--rw-text-3);
}

.rw-if-platform {
  font-size: 12.5px;
  color: var(--rw-text-2);
  font-variant-numeric: tabular-nums;
}

/* 票价 */
.rw-if-fare {
  border: 1px solid var(--rw-border);
  border-radius: 14px;
  background: var(--rw-soft-2);
  padding: 15px 17px;
}

.rw-if-fare-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
}

.rw-if-fare-train {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.rw-if-fare-train strong {
  font-size: 17px;
  color: var(--rw-text);
}

.rw-if-fare-train span {
  font-size: 11.5px;
  color: var(--rw-blue);
}

.rw-if-fare-time {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 13px;
  color: var(--rw-text-2);
  font-variant-numeric: tabular-nums;
}

.rw-if-fare-time em {
  font-style: normal;
  color: var(--rw-text-3);
}

.rw-if-fare-time small {
  font-size: 11px;
  color: var(--rw-text-3);
}

.rw-if-seats {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 13px;
}

.rw-if-seat {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 10px;
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
}

.rw-if-seat em {
  font-style: normal;
  font-size: 11.5px;
  color: var(--rw-text-3);
}

.rw-if-seat strong {
  font-size: 14px;
  color: var(--rw-orange);
}

.rw-if-seat small {
  font-size: 10.5px;
  color: var(--rw-success);
}

.rw-if-seat small.is-soldout {
  color: var(--rw-text-3);
}

/* 快捷入口 */
.rw-if-quick {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.rw-if-quick-item {
  display: flex;
  align-items: center;
  gap: 13px;
  padding: 16px 18px;
  border-radius: 14px;
  border: 1px solid var(--rw-border);
  background: var(--rw-soft-2);
  cursor: pointer;
  font-family: inherit;
  text-align: left;
  transition: border-color 0.18s ease, transform 0.18s ease;
}

.rw-if-quick-item:hover {
  border-color: var(--rw-blue);
  transform: translateY(-2px);
}

.rw-if-quick-icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  border-radius: 12px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 19px;
}

.rw-if-quick-item strong {
  display: block;
  font-size: 13.5px;
  color: var(--rw-text);
}

.rw-if-quick-item small {
  display: block;
  margin-top: 4px;
  font-size: 11.5px;
  color: var(--rw-text-3);
}

.rw-if-quick-arrow {
  margin-left: auto;
  color: var(--rw-text-3);
}

@media (max-width: 960px) {
  .rw-if-filters {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .rw-if-query {
    grid-column: 1 / -1;
    justify-self: flex-end;
  }

  .rw-if-board-head {
    display: none;
  }

  .rw-if-board-row {
    grid-template-columns: 90px 1fr auto;
    row-gap: 6px;
  }

  .rw-if-route {
    grid-column: 1 / -1;
  }

  .rw-if-platform {
    display: none;
  }
}

@media (max-width: 640px) {
  .rw-if-bar {
    flex-wrap: wrap;
  }

  .rw-if-train-input {
    max-width: none;
    flex: 1;
  }

  .rw-if-filters,
  .rw-if-quick {
    grid-template-columns: 1fr;
  }

  .rw-if-fare-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
