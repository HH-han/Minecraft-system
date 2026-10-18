<template>
  <div class="rw-home">
    <!-- Hero -->
    <section class="rw-hero">
      <div class="rw-hero-bg" aria-hidden="true">
        <span class="rw-hero-sun"></span>
        <span class="rw-hero-rail rw-hero-rail--1"></span>
        <span class="rw-hero-rail rw-hero-rail--2"></span>
        <span class="rw-hero-rail rw-hero-rail--3"></span>
      </div>

      <div class="rw-hero-inner">
        <div class="rw-hero-copy">
          <span class="rw-hero-badge">CHINA RAILWAY · 12306</span>
          <h1>{{ $t('railway.slogan') }}</h1>
          <p>{{ $t('railway.heroDesc') }}</p>
          <div class="rw-hero-stats">
            <div class="rw-hero-stat">
              <strong>3000+</strong>
              <span>{{ $t('railway.home.feature.coverage') }}</span>
            </div>
            <div class="rw-hero-stat">
              <strong>15</strong>
              <span>{{ $t('railway.search.dateRangeHint') }}</span>
            </div>
            <div class="rw-hero-stat">
              <strong>24h</strong>
              <span>{{ $t('railway.search.queryTitle') }}</span>
            </div>
          </div>
        </div>

        <div class="rw-hero-search">
          <TicketSearchForm variant="hero" :initial-from="preset.from" :initial-to="preset.to"
            :initial-date="preset.date" :initial-trip-type="initialTripType" @search="handleSearch" />
        </div>
      </div>
    </section>

    <!-- 常用服务 -->
    <section class="rw-section">
      <div class="rw-section-head">
        <h2>{{ $t('railway.home.quickTitle') }}</h2>
      </div>
      <div class="rw-quick-grid">
        <button v-for="item in quickServices" :key="item.key" type="button" class="rw-quick-item"
          @click="handleQuick(item.key)">
          <span class="rw-quick-icon">
            <el-icon><component :is="item.icon" /></el-icon>
          </span>
          <span>{{ $t(`railway.home.quick.${item.key}`) }}</span>
        </button>
      </div>
    </section>

    <!-- 热门线路 -->
    <section class="rw-section">
      <div class="rw-section-head">
        <h2>{{ $t('railway.home.popularRoutes') }}</h2>
      </div>
      <div class="rw-route-grid">
        <button v-for="route in popularRoutes" :key="route.from + route.to" type="button" class="rw-route-card"
          @click="searchPopular(route)">
          <div class="rw-route-main">
            <span class="rw-route-city">{{ route.from }}</span>
            <span class="rw-route-line">
              <span class="rw-route-dash"></span>
              <em>{{ route.train }}</em>
              <span class="rw-route-dash"></span>
            </span>
            <span class="rw-route-city">{{ route.to }}</span>
          </div>
          <div class="rw-route-meta">
            <span>{{ formatDuration(route.durationMinutes, $t) }}</span>
            <span>¥{{ route.price }}</span>
          </div>
        </button>
      </div>
    </section>

    <!-- 服务卡片 -->
    <section class="rw-section">
      <div class="rw-banner-grid">
        <article v-for="banner in banners" :key="banner.key" class="rw-banner" :class="`rw-banner--${banner.tone}`">
          <div class="rw-banner-text">
            <h3>{{ $t(`railway.home.banner.${banner.key}Title`) }}</h3>
            <p>{{ $t(`railway.home.banner.${banner.key}Desc`) }}</p>
          </div>
          <span class="rw-banner-icon">
            <el-icon><component :is="banner.icon" /></el-icon>
          </span>
        </article>
      </div>
    </section>

    <!-- 优势 -->
    <section class="rw-section rw-feature-section">
      <div class="rw-section-head">
        <h2>{{ $t('railway.home.feature.title') }}</h2>
      </div>
      <div class="rw-feature-grid">
        <article v-for="feature in features" :key="feature.key" class="rw-feature">
          <span class="rw-feature-icon">
            <el-icon><component :is="feature.icon" /></el-icon>
          </span>
          <h3>{{ $t(`railway.home.feature.${feature.key}`) }}</h3>
          <p>{{ $t(`railway.home.feature.${feature.key}Desc`) }}</p>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import {
  Box,
  Calendar,
  Camera,
  ChatDotRound,
  FirstAidKit,
  KnifeFork,
  MapLocation,
  Search,
  Trophy,
  Umbrella,
  Van,
  Timer,
  Position,
  Sunny,
} from '@element-plus/icons-vue'
import TicketSearchForm from './components/TicketSearchForm.vue'
import { formatDuration, queryString, todayString } from './utils'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const initialTripType = computed(() => queryString(route.query.tripType, 'single'))

const preset = reactive({
  from: queryString(route.query.fromStation, '北京南'),
  to: queryString(route.query.toStation, '上海虹桥'),
  date: queryString(route.query.date, todayString()),
})

const quickServices = [
  { key: 'special', icon: FirstAidKit },
  { key: 'lost', icon: Search },
  { key: 'car', icon: Van },
  { key: 'luggage', icon: Box },
  { key: 'guide', icon: MapLocation },
  { key: 'scenery', icon: Camera },
  { key: 'feedback', icon: ChatDotRound },
]

const popularRoutes = [
  { from: '北京南', to: '上海虹桥', train: 'G101', durationMinutes: 269, price: '565' },
  { from: '上海虹桥', to: '杭州东', train: 'G1501', durationMinutes: 65, price: '100' },
  { from: '广州南', to: '深圳北', train: 'G1001', durationMinutes: 60, price: '120' },
  { from: '北京西', to: '西安北', train: 'G81', durationMinutes: 265, price: '547' },
]

const banners = [
  { key: 'member', tone: 'blue', icon: Trophy },
  { key: 'food', tone: 'orange', icon: KnifeFork },
  { key: 'insurance', tone: 'mint', icon: Umbrella },
  { key: 'periodic', tone: 'violet', icon: Calendar },
]

const features = [
  { key: 'punctual', icon: Timer },
  { key: 'coverage', icon: Position },
  { key: 'eco', icon: Sunny },
]

const handleSearch = (payload) => {
  router.push({
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

const searchPopular = (item) => {
  preset.from = item.from
  preset.to = item.to
  router.push({
    path: '/ticket/list',
    query: {
      fromStation: item.from,
      toStation: item.to,
      date: preset.date,
    },
  })
}

const handleQuick = () => {
  ElMessage.info(t('railway.search.notOpen'))
}
</script>

<style scoped>
.rw-home {
  width: 100%;
}

/* ============ Hero ============ */
.rw-hero {
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(1000px 520px at 78% -10%, rgba(255, 255, 255, 0.22), transparent 60%),
    linear-gradient(118deg, #1b4fbf 0%, #3b7cff 55%, #63a2ff 100%);
  padding: 54px 24px 62px;
}

.rw-hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.rw-hero-sun {
  position: absolute;
  right: 8%;
  top: -120px;
  width: 340px;
  height: 340px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 209, 102, 0.55), rgba(255, 209, 102, 0) 68%);
  filter: blur(2px);
}

.rw-hero-rail {
  position: absolute;
  left: -10%;
  width: 120%;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.34), transparent);
  transform: rotate(-8deg);
}

.rw-hero-rail--1 {
  top: 34%;
}

.rw-hero-rail--2 {
  top: 48%;
  opacity: 0.6;
}

.rw-hero-rail--3 {
  top: 62%;
  opacity: 0.35;
}

.rw-hero-inner {
  position: relative;
  z-index: 1;
  max-width: 1180px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(0, 0.86fr) minmax(0, 1.14fr);
  gap: 48px;
  align-items: center;
}

.rw-hero-copy {
  color: #ffffff;
}

.rw-hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 5px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.24);
  font-size: 11px;
  letter-spacing: 0.22em;
}

.rw-hero-copy h1 {
  margin: 20px 0 14px;
  font-size: clamp(30px, 3.4vw, 46px);
  line-height: 1.18;
  font-weight: 700;
  letter-spacing: -0.02em;
  text-shadow: 0 8px 30px rgba(9, 34, 78, 0.28);
}

.rw-hero-copy p {
  margin: 0;
  max-width: 460px;
  font-size: 15px;
  line-height: 1.85;
  color: rgba(255, 255, 255, 0.82);
}

.rw-hero-stats {
  display: flex;
  gap: 34px;
  margin-top: 30px;
}

.rw-hero-stat {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.rw-hero-stat strong {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0.01em;
}

.rw-hero-stat span {
  font-size: 11.5px;
  color: rgba(255, 255, 255, 0.7);
}

.rw-hero-search {
  min-width: 0;
}

/* ============ Section ============ */
.rw-section {
  max-width: 1180px;
  margin: 0 auto;
  padding: 40px 24px 0;
}

.rw-section-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 18px;
}

.rw-section-head h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.01em;
  color: var(--rw-text);
  position: relative;
  padding-left: 14px;
}

.rw-section-head h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 3px;
  bottom: 3px;
  width: 4px;
  border-radius: 4px;
  background: linear-gradient(180deg, var(--rw-blue), #8fc0ff);
}

/* 常用服务 */
.rw-quick-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 12px;
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 18px;
  padding: 22px 16px;
  box-shadow: 0 12px 32px -28px rgba(16, 47, 100, 0.45);
}

.rw-quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 10px 6px;
  border: none;
  background: none;
  border-radius: 12px;
  cursor: pointer;
  font-family: inherit;
  font-size: 12.5px;
  color: var(--rw-text-2);
  transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.rw-quick-item:hover {
  background: var(--rw-soft);
  color: var(--rw-blue);
  transform: translateY(-2px);
}

.rw-quick-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  transition: background 0.2s ease, color 0.2s ease;
}

.rw-quick-item:hover .rw-quick-icon {
  background: var(--rw-blue);
  color: #ffffff;
}

/* 热门线路 */
.rw-route-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.rw-route-card {
  border: 1px solid var(--rw-border);
  background: var(--rw-card);
  border-radius: 16px;
  padding: 18px;
  cursor: pointer;
  font-family: inherit;
  text-align: left;
  transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.rw-route-card:hover {
  border-color: rgba(59, 124, 255, 0.5);
  transform: translateY(-3px);
  box-shadow: 0 18px 34px -28px rgba(16, 47, 100, 0.55);
}

.rw-route-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rw-route-city {
  font-size: 15px;
  font-weight: 600;
  color: var(--rw-text);
  white-space: nowrap;
}

.rw-route-line {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
  color: var(--rw-text-3);
}

.rw-route-dash {
  flex: 1;
  height: 1px;
  background: repeating-linear-gradient(90deg, var(--rw-border) 0 4px, transparent 4px 8px);
}

.rw-route-line em {
  font-size: 10px;
  font-style: normal;
  letter-spacing: 0.04em;
  color: var(--rw-blue);
  background: var(--rw-soft);
  padding: 2px 6px;
  border-radius: 999px;
  white-space: nowrap;
}

.rw-route-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 14px;
  font-size: 12px;
  color: var(--rw-text-3);
}

.rw-route-meta span:last-child {
  color: var(--rw-orange);
  font-weight: 700;
}

/* 服务卡片 */
.rw-banner-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.rw-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  border-radius: 18px;
  padding: 22px;
  color: #ffffff;
  overflow: hidden;
  position: relative;
  min-height: 118px;
}

.rw-banner::after {
  content: '';
  position: absolute;
  right: -30px;
  bottom: -50px;
  width: 130px;
  height: 130px;
  border-radius: 50%;
  border: 18px solid rgba(255, 255, 255, 0.12);
}

.rw-banner--blue {
  background: linear-gradient(135deg, #1b4fbf, #3b7cff);
}

.rw-banner--orange {
  background: linear-gradient(135deg, #ef7a16, #ffab4d);
}

.rw-banner--mint {
  background: linear-gradient(135deg, #0d9488, #2dd4bf);
}

.rw-banner--violet {
  background: linear-gradient(135deg, #5b4bd6, #8b7cf6);
}

.rw-banner-text h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.rw-banner-text p {
  margin: 7px 0 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.82);
}

.rw-banner-icon {
  position: relative;
  z-index: 1;
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

/* 优势 */
.rw-feature-section {
  padding-bottom: 8px;
}

.rw-feature-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.rw-feature {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 18px;
  padding: 26px;
  text-align: left;
}

.rw-feature-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 16px;
}

.rw-feature h3 {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 700;
  color: var(--rw-text);
}

.rw-feature p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: var(--rw-text-2);
}

@media (max-width: 1024px) {
  .rw-hero-inner {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .rw-hero-copy {
    max-width: 560px;
  }

  .rw-quick-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .rw-route-grid,
  .rw-banner-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .rw-feature-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .rw-hero {
    padding: 36px 16px 44px;
  }

  .rw-hero-stats {
    gap: 22px;
  }

  .rw-section {
    padding: 30px 16px 0;
  }

  .rw-quick-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    padding: 16px 10px;
  }

  .rw-route-grid,
  .rw-banner-grid {
    grid-template-columns: 1fr;
  }
}
</style>
