<template>
  <div class="rw-page">
    <div class="rw-page-inner">
      <!-- 路线概览 -->
      <section class="rw-summary">
        <div class="rw-summary-main">
          <div class="rw-summary-route">
            <h1>{{ fromStation }}</h1>
            <span class="rw-summary-arrow">
              <span class="rw-summary-dash"></span>
              <el-icon><Right /></el-icon>
            </span>
            <h1>{{ toStation }}</h1>
          </div>
          <p class="rw-summary-meta">
            <span class="rw-summary-date">{{ date }} · {{ weekday }}</span>
            <span class="rw-summary-count">{{ $t('railway.list.resultCount', { n: filteredTrains.length }) }}</span>
          </p>
        </div>
        <button type="button" class="rw-change-btn" @click="showSearch = !showSearch">
          <el-icon><EditPen /></el-icon>
          {{ $t('railway.list.changeSearch') }}
        </button>
      </section>

      <div v-if="showSearch" class="rw-search-wrap">
        <TicketSearchForm variant="compact" :initial-from="fromStation" :initial-to="toStation"
          :initial-date="date" :initial-trip-type="tripType" @search="handleSearch" />
      </div>

      <div class="rw-list-layout">
        <!-- 左侧筛选 -->
        <aside class="rw-filters">
          <div class="rw-filter-head">
            <span>{{ $t('railway.list.filter') }}</span>
            <button type="button" class="rw-filter-reset" @click="resetFilters">{{ $t('railway.list.reset') }}</button>
          </div>

          <div class="rw-filter-group">
            <h4>{{ $t('railway.list.departTime') }}</h4>
            <label v-for="range in timeRanges" :key="range.key" class="rw-check">
              <el-checkbox v-model="range.checked" />
              <span>{{ range.label }}</span>
            </label>
          </div>

          <div class="rw-filter-group">
            <h4>{{ $t('railway.list.trainType') }}</h4>
            <label v-for="type in trainTypes" :key="type" class="rw-check">
              <el-checkbox v-model="type.checked" />
              <span>{{ $t(`railway.list.type.${type.value}`) }}</span>
            </label>
          </div>

          <div class="rw-filter-group">
            <label class="rw-check rw-check--single">
              <el-checkbox v-model="onlyAvailable" />
              <span>{{ $t('railway.list.onlyAvailable') }}</span>
            </label>
          </div>
        </aside>

        <!-- 车次列表 -->
        <section class="rw-results">
          <!-- 加载骨架 -->
          <template v-if="loading">
            <div v-for="n in 4" :key="n" class="rw-skeleton">
              <div class="rw-skeleton-line w-40"></div>
              <div class="rw-skeleton-line w-70"></div>
              <div class="rw-skeleton-line w-55"></div>
            </div>
          </template>

          <!-- 错误 -->
          <div v-else-if="error" class="rw-state">
            <span class="rw-state-icon"><el-icon><Warning /></el-icon></span>
            <h3>{{ $t('railway.list.loadFailed') }}</h3>
            <p>{{ error }}</p>
            <button type="button" class="rw-state-btn" @click="load">{{ $t('railway.list.retry') }}</button>
          </div>

          <!-- 空 -->
          <div v-else-if="filteredTrains.length === 0" class="rw-state">
            <span class="rw-state-icon"><el-icon><Ticket /></el-icon></span>
            <h3>{{ $t('railway.list.noTrain') }}</h3>
            <p>{{ $t('railway.list.noTrainDesc') }}</p>
            <button type="button" class="rw-state-btn" @click="goHome">{{ $t('railway.search.query') }}</button>
          </div>

          <!-- 列表 -->
          <template v-else>
            <article v-for="train in filteredTrains" :key="train.serviceId" class="rw-train">
              <div class="rw-train-head">
                <div class="rw-train-no">
                  <strong>{{ train.trainNo }}</strong>
                  <span class="rw-train-type">{{ $t(`railway.list.type.${train.trainType}`) }}</span>
                </div>
                <span class="rw-train-sep"></span>
                <div class="rw-train-end">
                  <span class="rw-train-station">{{ train.fromStationName }}</span>
                  <span class="rw-train-time">{{ train.fromTime }}</span>
                </div>
                <div class="rw-train-duration">
                  <span class="rw-train-dash"></span>
                  <em>{{ formatDuration(train.durationMinutes, $t) }}</em>
                  <span class="rw-train-dash"></span>
                </div>
                <div class="rw-train-end rw-train-end--right">
                  <span class="rw-train-station">
                    {{ train.toStationName }}
                    <i v-if="train.arriveDayOffset > 0" class="rw-train-nextday">
                      {{ $t('railway.list.nextDay') }}
                    </i>
                  </span>
                  <span class="rw-train-time">{{ train.toTime }}</span>
                </div>
              </div>

              <div class="rw-train-seats">
                <button v-for="seat in train.seats" :key="seat.seatType" type="button" class="rw-seat"
                  :class="{ 'is-soldout': seat.leftSeats <= 0 }" @click="book(train, seat)">
                  <span class="rw-seat-name">{{ $t(`railway.list.seat.${seat.seatType}`) }}</span>
                  <strong class="rw-seat-price">¥{{ formatPrice(seat.priceCents) }}</strong>
                  <span class="rw-seat-left">
                    {{ seat.leftSeats > 0 ? $t('railway.list.leftCount', { n: seat.leftSeats }) : $t('railway.list.soldOut') }}
                  </span>
                </button>
              </div>
            </article>
          </template>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { searchRailwayTickets } from '@/api/railway'
import TicketSearchForm from './components/TicketSearchForm.vue'
import { formatDuration, formatPrice, queryString, todayString, weekdayLabel } from './utils'

const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()

const showSearch = ref(false)
const trains = ref([])
const loading = ref(false)
const error = ref('')

const fromStation = computed(() => queryString(route.query.fromStation))
const toStation = computed(() => queryString(route.query.toStation))
const date = computed(() => queryString(route.query.date, todayString()))
const tripType = computed(() => queryString(route.query.tripType, 'single'))
const weekday = computed(() => weekdayLabel(date.value, locale.value))

const timeRanges = reactive([
  { key: 'dawn', label: '00:00 - 06:00', from: 0, to: 6, checked: false },
  { key: 'morning', label: '06:00 - 12:00', from: 6, to: 12, checked: false },
  { key: 'afternoon', label: '12:00 - 18:00', from: 12, to: 18, checked: false },
  { key: 'night', label: '18:00 - 24:00', from: 18, to: 24, checked: false },
])

const trainTypes = reactive(
  ['G', 'D', 'Z', 'T', 'K'].map((value) => ({
    value,
    checked: queryString(route.query.highSpeed) === '1' ? ['G', 'D'].includes(value) : false,
  })),
)

const onlyAvailable = ref(false)

const filteredTrains = computed(() =>
  trains.value.filter((train) => {
    const minute = timeToMinutes(train.fromTime)
    const activeRanges = timeRanges.filter((range) => range.checked)
    if (activeRanges.length > 0) {
      const inRange = activeRanges.some((range) => minute >= range.from * 60 && minute < range.to * 60)
      if (!inRange) return false
    }
    const activeTypes = trainTypes.filter((type) => type.checked)
    if (activeTypes.length > 0 && !activeTypes.some((type) => type.value === train.trainType)) {
      return false
    }
    if (onlyAvailable.value) {
      const hasTicket = (train.seats || []).some((seat) => seat.leftSeats > 0)
      if (!hasTicket) return false
    }
    return true
  }),
)

const timeToMinutes = (time) => {
  if (!time) return 0
  const [h, m] = String(time).split(':').map(Number)
  return (h || 0) * 60 + (m || 0)
}

const load = async () => {
  if (!fromStation.value || !toStation.value) {
    router.replace('/ticket')
    return
  }
  loading.value = true
  error.value = ''
  try {
    const res = await searchRailwayTickets({
      fromStation: fromStation.value,
      toStation: toStation.value,
      date: date.value,
    })
    trains.value = res?.data || []
  } catch (err) {
    error.value = err?.message || ''
    trains.value = []
  } finally {
    loading.value = false
  }
}

watch(() => [fromStation.value, toStation.value, date.value], load, { immediate: true })

const handleSearch = (payload) => {
  showSearch.value = false
  router.replace({
    path: '/ticket/list',
    query: {
      fromStation: payload.fromStation,
      toStation: payload.toStation,
      date: payload.date,
      tripType: payload.tripType,
      returnDate: payload.returnDate || undefined,
      highSpeed: payload.highSpeed ? '1' : undefined,
    },
  })
}

const resetFilters = () => {
  timeRanges.forEach((range) => {
    range.checked = false
  })
  trainTypes.forEach((type) => {
    type.checked = false
  })
  onlyAvailable.value = false
}

const goHome = () => router.push('/ticket')

const book = (train, seat) => {
  if (seat.leftSeats <= 0) return
  router.push({
    path: '/ticket/order',
    query: {
      serviceId: train.serviceId,
      fromSeq: train.fromStopSeq,
      toSeq: train.toStopSeq,
      seatType: seat.seatType,
      fromStation: train.fromStationName,
      toStation: train.toStationName,
      trainNo: train.trainNo,
      date: date.value,
      fromTime: train.fromTime,
      toTime: train.toTime,
      price: seat.priceCents,
      dayOffset: train.arriveDayOffset,
    },
  })
}
</script>

<style scoped>
.rw-page {
  padding: 30px 24px 0;
}

.rw-page-inner {
  max-width: 1180px;
  margin: 0 auto;
}

/* 路线概览 */
.rw-summary {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  padding: 22px 24px;
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 18px;
  box-shadow: 0 12px 32px -30px rgba(16, 47, 100, 0.45);
}

.rw-summary-route {
  display: flex;
  align-items: center;
  gap: 16px;
}

.rw-summary-route h1 {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--rw-text);
}

.rw-summary-arrow {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--rw-blue);
}

.rw-summary-dash {
  width: 46px;
  height: 1px;
  background: repeating-linear-gradient(90deg, var(--rw-border) 0 5px, transparent 5px 10px);
}

.rw-summary-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--rw-text-2);
}

.rw-summary-date {
  font-weight: 600;
  color: var(--rw-blue);
}

.rw-summary-count {
  padding-left: 14px;
  border-left: 1px solid var(--rw-border);
}

.rw-change-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid var(--rw-border);
  background: var(--rw-soft-2);
  color: var(--rw-text-2);
  padding: 8px 16px;
  border-radius: 999px;
  font-size: 13px;
  cursor: pointer;
  font-family: inherit;
  transition: color 0.2s ease, border-color 0.2s ease;
  flex-shrink: 0;
}

.rw-change-btn:hover {
  color: var(--rw-blue);
  border-color: var(--rw-blue);
}

.rw-search-wrap {
  margin-top: 16px;
}

/* 布局 */
.rw-list-layout {
  display: grid;
  grid-template-columns: 210px minmax(0, 1fr);
  gap: 20px;
  margin-top: 20px;
  align-items: start;
}

/* 筛选 */
.rw-filters {
  position: sticky;
  top: 138px;
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 16px;
  padding: 18px 18px 12px;
}

.rw-filter-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  font-weight: 600;
  color: var(--rw-text);
  padding-bottom: 12px;
  border-bottom: 1px solid var(--rw-border);
}

.rw-filter-reset {
  border: none;
  background: none;
  font-size: 12px;
  color: var(--rw-text-3);
  cursor: pointer;
  font-family: inherit;
}

.rw-filter-reset:hover {
  color: var(--rw-blue);
}

.rw-filter-group {
  padding: 14px 0 10px;
  border-bottom: 1px solid var(--rw-border);
}

.rw-filter-group:last-child {
  border-bottom: none;
}

.rw-filter-group h4 {
  margin: 0 0 10px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
  color: var(--rw-text-3);
}

.rw-check {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--rw-text-2);
  padding: 4px 0;
  cursor: pointer;
}

.rw-check :deep(.el-checkbox) {
  height: auto;
  margin-right: 0;
}

.rw-check :deep(.el-checkbox__label) {
  display: none;
}

/* 车次卡片 */
.rw-results {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}

.rw-train {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 16px;
  padding: 20px 22px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.rw-train:hover {
  border-color: rgba(59, 124, 255, 0.45);
  box-shadow: 0 20px 40px -34px rgba(16, 47, 100, 0.6);
  transform: translateY(-2px);
}

.rw-train-head {
  display: flex;
  align-items: center;
  gap: 18px;
}

.rw-train-no {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 74px;
}

.rw-train-no strong {
  font-size: 19px;
  font-weight: 700;
  letter-spacing: 0.02em;
  color: var(--rw-text);
}

.rw-train-type {
  display: inline-flex;
  align-self: flex-start;
  padding: 2px 8px;
  border-radius: 999px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  font-size: 10.5px;
  letter-spacing: 0.06em;
}

.rw-train-sep {
  width: 1px;
  height: 38px;
  background: var(--rw-border);
}

.rw-train-end {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 92px;
}

.rw-train-end--right {
  align-items: flex-end;
  text-align: right;
}

.rw-train-station {
  font-size: 15px;
  font-weight: 600;
  color: var(--rw-text);
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.rw-train-time {
  font-size: 22px;
  font-weight: 700;
  color: var(--rw-text);
  letter-spacing: -0.01em;
  font-variant-numeric: tabular-nums;
}

.rw-train-nextday {
  font-style: normal;
  font-size: 10px;
  font-weight: 600;
  color: var(--rw-orange);
  background: rgba(255, 138, 31, 0.12);
  padding: 1px 5px;
  border-radius: 4px;
}

.rw-train-duration {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--rw-text-3);
  min-width: 80px;
}

.rw-train-dash {
  flex: 1;
  height: 1px;
  background: repeating-linear-gradient(90deg, var(--rw-border) 0 4px, transparent 4px 8px);
}

.rw-train-duration em {
  font-style: normal;
  font-size: 11.5px;
  color: var(--rw-text-3);
  white-space: nowrap;
}

.rw-train-seats {
  display: flex;
  gap: 10px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed var(--rw-border);
  flex-wrap: wrap;
}

.rw-seat {
  display: flex;
  align-items: baseline;
  gap: 8px;
  padding: 9px 16px;
  border-radius: 10px;
  border: 1px solid var(--rw-border);
  background: var(--rw-soft-2);
  cursor: pointer;
  font-family: inherit;
  transition: border-color 0.18s ease, background 0.18s ease, transform 0.18s ease;
}

.rw-seat:hover {
  border-color: var(--rw-blue);
  background: var(--rw-soft);
  transform: translateY(-1px);
}

.rw-seat-name {
  font-size: 12.5px;
  color: var(--rw-text-2);
}

.rw-seat-price {
  font-size: 15px;
  font-weight: 700;
  color: var(--rw-orange);
  font-variant-numeric: tabular-nums;
}

.rw-seat-left {
  font-size: 11px;
  color: var(--rw-text-3);
}

.rw-seat.is-soldout {
  opacity: 0.55;
  cursor: not-allowed;
}

.rw-seat.is-soldout .rw-seat-price {
  color: var(--rw-text-3);
}

/* 状态 */
.rw-state {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 18px;
  padding: 60px 24px;
  text-align: center;
  color: var(--rw-text-2);
}

.rw-state-icon {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  margin-bottom: 16px;
}

.rw-state h3 {
  margin: 0 0 6px;
  font-size: 17px;
  color: var(--rw-text);
}

.rw-state p {
  margin: 0 0 18px;
  font-size: 13px;
}

.rw-state-btn {
  border: none;
  background: var(--rw-blue);
  color: #ffffff;
  padding: 9px 24px;
  border-radius: 999px;
  font-size: 13px;
  cursor: pointer;
  font-family: inherit;
}

/* 骨架 */
.rw-skeleton {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 16px;
  padding: 22px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.rw-skeleton-line {
  height: 14px;
  border-radius: 6px;
  background: linear-gradient(90deg, var(--rw-soft) 25%, var(--rw-soft-2) 37%, var(--rw-soft) 63%);
  background-size: 400% 100%;
  animation: rw-shimmer 1.4s ease infinite;
}

.w-40 {
  width: 40%;
}

.w-55 {
  width: 55%;
}

.w-70 {
  width: 70%;
}

@keyframes rw-shimmer {
  0% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0 50%;
  }
}

@media (max-width: 900px) {
  .rw-list-layout {
    grid-template-columns: 1fr;
  }

  .rw-filters {
    position: static;
  }

  .rw-train-head {
    flex-wrap: wrap;
    gap: 12px;
  }

  .rw-train-duration {
    flex-basis: 100%;
    order: 5;
  }
}

@media (max-width: 640px) {
  .rw-page {
    padding: 16px 14px 0;
  }

  .rw-summary {
    flex-direction: column;
    align-items: flex-start;
    padding: 18px;
  }

  .rw-summary-route h1 {
    font-size: 21px;
  }

  .rw-train {
    padding: 16px;
  }

  .rw-train-time {
    font-size: 19px;
  }

  .rw-seat {
    flex: 1;
    justify-content: space-between;
  }
}
</style>
