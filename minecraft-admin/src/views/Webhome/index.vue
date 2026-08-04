<template>
  <div class="webhome">
    <!-- 顶部欢迎区 Hero -->
    <section class="hero-band">
      <div class="hero-text">
        <p class="hero-eyebrow">{{ todayStr }}</p>
        <h1 class="hero-title">{{ greeting }}，{{ username }}</h1>
        <p class="hero-subtitle">欢迎回到 Minecraft 旅游管理后台，这里是今日平台的运营概览与关键指标。</p>
      </div>
      <div class="hero-actions">
        <button class="btn-primary" @click="goQuick(12)">
          <el-icon><ShoppingCart /></el-icon>
          <span>查看订单</span>
        </button>
        <button class="btn-ghost" @click="refreshAll">
          <el-icon><Refresh /></el-icon>
          <span>刷新数据</span>
        </button>
        <button class="btn-setting" @click="goSetting(40)">
          <el-icon><Setting /></el-icon>
          <span>进入后台</span>
        </button>
      </div>
    </section>

    <!-- 核心指标卡片 -->
    <section class="kpi-grid">
      <div class="kpi-card" v-for="kpi in kpis" :key="kpi.key">
        <div class="kpi-head">
          <span class="kpi-icon" :style="{ background: kpi.bg, color: kpi.color }">
            <el-icon><component :is="kpi.icon" /></el-icon>
          </span>
          <span class="kpi-trend" :class="kpi.trendDir">
            <el-icon class="trend-arrow"><component :is="kpi.trendDir === 'up' ? ArrowUp : ArrowDown" /></el-icon>
            {{ kpi.trend }}
          </span>
        </div>
        <div class="kpi-value">{{ kpi.display }}</div>
        <div class="kpi-label">{{ kpi.label }}</div>
        <div class="kpi-spark" :ref="el => (sparkRefs[kpi.key] = el)"></div>
      </div>
    </section>

    <!-- 主内容网格 -->
    <section class="main-grid">
      <!-- 营收趋势 -->
      <div class="card chart-card span-8">
        <div class="card-head">
          <div class="card-head-text">
            <h3 class="card-title">营收趋势</h3>
            <p class="card-desc">{{ range }} 内平台营收与订单变化</p>
          </div>
          <div class="seg">
            <button
              v-for="r in ranges"
              :key="r"
              :class="{ active: range === r }"
              @click="range = r"
            >{{ r }}</button>
          </div>
        </div>
        <div class="chart" ref="revenueChart"></div>
      </div>

      <!-- 流量来源 -->
      <div class="card span-4">
        <div class="card-head">
          <div class="card-head-text">
            <h3 class="card-title">流量来源</h3>
            <p class="card-desc">今日访客渠道分布</p>
          </div>
        </div>
        <div class="chart donut" ref="trafficChart"></div>
        <ul class="legend-list">
          <li v-for="(t, i) in traffic" :key="t.name">
            <span class="legend-dot" :style="{ background: trafficColors[i] }"></span>
            <span class="legend-name">{{ t.name }}</span>
            <span class="legend-val">{{ t.value }}%</span>
          </li>
        </ul>
      </div>

      <!-- 最新订单 -->
      <div class="card span-8">
        <div class="card-head">
          <div class="card-head-text">
            <h3 class="card-title">最新订单</h3>
            <p class="card-desc">近期需要关注的订单动态</p>
          </div>
          <a class="link" @click="goQuick(12)">查看全部 ›</a>
        </div>
        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th>订单编号</th>
                <th>用户</th>
                <th>金额</th>
                <th>状态</th>
                <th>时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="o in orders" :key="o.id">
                <td class="mono">{{ o.id }}</td>
                <td>{{ o.user }}</td>
                <td class="amount">{{ o.amount }}</td>
                <td>
                  <span class="status-tag" :class="o.statusType">{{ o.status }}</span>
                </td>
                <td class="muted">{{ o.date }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="card span-4">
        <div class="card-head">
          <div class="card-head-text">
            <h3 class="card-title">快捷入口</h3>
            <p class="card-desc">常用管理功能直达</p>
          </div>
        </div>
        <div class="quick-grid">
          <button
            v-for="q in quickActions"
            :key="q.key"
            class="quick-item"
            @click="goQuick(q.key)"
          >
            <span class="quick-icon" :style="{ background: q.bg, color: q.color }">
              <el-icon><component :is="q.icon" /></el-icon>
            </span>
            <span class="quick-label">{{ q.label }}</span>
          </button>
        </div>
      </div>

      <!-- 系统状态 -->
      <div class="card span-12">
        <div class="card-head">
          <div class="card-head-text">
            <h3 class="card-title">系统状态</h3>
            <p class="card-desc">核心服务实时健康度</p>
          </div>
          <span class="badge-ok">
            <span class="dot"></span>运行正常
          </span>
        </div>
        <div class="status-grid">
          <div class="status-item" v-for="s in statuses" :key="s.label">
            <div class="status-top">
              <span class="status-label">{{ s.label }}</span>
              <span class="status-val">{{ s.value }}%</span>
            </div>
            <div class="status-bar">
              <div class="status-fill" :style="{ width: s.value + '%', background: s.color }"></div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, inject, watch } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router';
import {
  User, ShoppingCart, Goods, Money, View, Picture, Document, House,
  DataAnalysis, ArrowUp, ArrowDown, Refresh, Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const authStore = useAuthStore()
// 由父级 admin 布局 provide 的菜单切换函数，用于快捷入口跳转
const navigateToMenu = inject('navigateToMenu', () => {})

/* ---------- 问候与日期 ---------- */
const username = authStore.user?.username || '管理员'
const now = new Date()
const hour = now.getHours()
const greeting =
  hour < 6 ? '凌晨好' : hour < 12 ? '上午好' : hour < 14 ? '中午好' : hour < 18 ? '下午好' : '晚上好'
const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
const todayStr = `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 · ${weekDays[now.getDay()]}`

/* ---------- 核心指标 ---------- */
const kpis = reactive([
  {
    key: 'users', label: '用户总数', icon: User,
    target: 128460, display: '0', formatter: (v) => v.toLocaleString(),
    trend: '+12.4%', trendDir: 'up',
    color: '#2997ff', bg: 'rgba(41,151,255,0.12)',
    spark: [820, 932, 901, 1034, 1190, 1280, 1320, 1410, 1380, 1490, 1560, 1620]
  },
  {
    key: 'orders', label: '订单总数', icon: ShoppingCart,
    target: 8642, display: '0', formatter: (v) => v.toLocaleString(),
    trend: '+8.1%', trendDir: 'up',
    color: '#34c759', bg: 'rgba(52,199,89,0.12)',
    spark: [320, 410, 380, 460, 520, 560, 610, 590, 680, 720, 760, 810]
  },
  {
    key: 'revenue', label: '营收总额', icon: Money,
    target: 1284560, display: '¥0', formatter: (v) => '¥' + v.toLocaleString(),
    trend: '+18.6%', trendDir: 'up',
    color: '#5856d6', bg: 'rgba(88,86,214,0.12)',
    spark: [620, 730, 700, 860, 990, 1120, 1280, 1180, 1360, 1490, 1620, 1780]
  },
  {
    key: 'visits', label: '今日访问', icon: View,
    target: 36720, display: '0', formatter: (v) => v.toLocaleString(),
    trend: '-2.3%', trendDir: 'down',
    color: '#ff9500', bg: 'rgba(255,149,0,0.12)',
    spark: [4200, 4600, 4100, 5200, 5800, 5400, 6100, 5600, 5900, 5300, 5100, 4900]
  }
])

/* ---------- 营收趋势 ---------- */
const ranges = ['7天', '30天', '季度']
const range = ref('7天')
const gen30 = () => {
  const x = [], rev = [], ord = []
  for (let i = 1; i <= 30; i++) {
    x.push(`${i}`)
    const base = 15000 + Math.round(Math.sin(i / 3) * 4000) + i * 160
    rev.push(base)
    ord.push(Math.round(base / 95))
  }
  return { x, rev, ord }
}
const revenueData = {
  '7天': {
    x: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    rev: [12400, 15800, 13200, 18900, 22400, 28600, 25200],
    ord: [128, 162, 136, 195, 230, 292, 258]
  },
  '30天': gen30(),
  '季度': {
    x: ['4月', '5月', '6月'],
    rev: [286000, 312000, 348000],
    ord: [2860, 3120, 3480]
  }
}

/* ---------- 流量来源 ---------- */
const trafficColors = ['#2997ff', '#5856d6', '#34c759', '#ff9500']
const traffic = [
  { name: '直接访问', value: 38 },
  { name: '搜索引擎', value: 32 },
  { name: '社交媒体', value: 18 },
  { name: '外部链接', value: 12 }
]

/* ---------- 最新订单 ---------- */
const orders = [
  { id: 'No.20260804-017', user: '林晚风', amount: '¥2,480', status: '已完成', statusType: 'done', date: '08-04 09:21' },
  { id: 'No.20260804-016', user: '陈默之', amount: '¥1,299', status: '待发货', statusType: 'pending', date: '08-04 10:05' },
  { id: 'No.20260804-015', user: '苏青禾', amount: '¥5,640', status: '已完成', statusType: 'done', date: '08-04 10:48' },
  { id: 'No.20260804-014', user: '周屿', amount: '¥880', status: '已取消', statusType: 'cancel', date: '08-04 11:12' },
  { id: 'No.20260804-013', user: '叶昭', amount: '¥3,260', status: '待付款', statusType: 'pending', date: '08-04 11:37' },
  { id: 'No.20260804-012', user: '何故', amount: '¥4,120', status: '已完成', statusType: 'done', date: '08-04 12:04' }
]

/* ---------- 快捷入口 ---------- */
const quickActions = [
  { label: '用户管理', icon: User, key: 3, color: '#2997ff', bg: 'rgba(41,151,255,0.12)' },
  { label: '订单管理', icon: ShoppingCart, key: 12, color: '#34c759', bg: 'rgba(52,199,89,0.12)' },
  { label: '商品管理', icon: Goods, key: 4, color: '#ff9500', bg: 'rgba(255,149,0,0.12)' },
  { label: '景区管理', icon: Picture, key: 6, color: '#5856d6', bg: 'rgba(88,86,214,0.12)' },
  { label: '酒店管理', icon: House, key: 5, color: '#ff2d55', bg: 'rgba(255,45,85,0.12)' },
  { label: '攻略管理', icon: Document, key: 11, color: '#30b0c7', bg: 'rgba(48,176,199,0.12)' },
  { label: '轮播管理', icon: Picture, key: 22, color: '#af52de', bg: 'rgba(175,82,222,0.12)' },
  { label: '网站介绍', icon: DataAnalysis, key: 39, color: '#007aff', bg: 'rgba(0,122,255,0.12)' }
]

/* ---------- 系统状态 ---------- */
const statuses = [
  { label: 'API 服务', value: 98, color: '#34c759' },
  { label: '数据库', value: 96, color: '#2997ff' },
  { label: '缓存服务', value: 99, color: '#5856d6' },
  { label: '文件存储', value: 95, color: '#ff9500' }
]

// 进入后台指定模块：
// - 在后台 shell（/admin）内时，直接调用父级提供的菜单切换；
// - 作为独立首页（/）时，先把目标模块写入 store，再跳转 /admin，后台挂载后会落到该模块。
const goToModule = (key) => {
  if (route.path === '/admin') {
    navigateToMenu(key)
    return
  }
  authStore.currentComponentPath = key
  router.push('/admin')
}

const goQuick = (key) => {
  goToModule(key)
}

const goSetting = (key) => {
  goToModule(key)
}

/* ---------- 数字滚动动画 ---------- */
const animateCount = (item, duration = 1200) => {
  const start = performance.now()
  const step = (t) => {
    const p = Math.min((t - start) / duration, 1)
    const eased = 1 - Math.pow(1 - p, 3)
    const val = Math.round(item.target * eased)
    item.display = item.formatter(val)
    if (p < 1) requestAnimationFrame(step)
  }
  requestAnimationFrame(step)
}

/* ---------- 图表 ---------- */
const revenueChart = ref(null)
const trafficChart = ref(null)
const sparkRefs = {}
let revenueInstance = null
let trafficInstance = null
const sparkInstances = {}
let themeObserver = null

const getTheme = () => document.documentElement.getAttribute('data-theme') || 'light'
const isDark = () => getTheme() === 'dark'
const palette = () =>
  isDark()
    ? { text: '#a1a1a6', grid: 'rgba(255,255,255,0.08)', tipBg: '#1c1c1e', tipText: '#f5f5f7' }
    : { text: '#86868b', grid: 'rgba(0,0,0,0.06)', tipBg: '#ffffff', tipText: '#1d1d1f' }

const initSparklines = () => {
  kpis.forEach((kpi) => {
    const el = sparkRefs[kpi.key]
    if (!el) return
    if (sparkInstances[kpi.key]) {
      sparkInstances[kpi.key].dispose()
    }
    const inst = echarts.init(el)
    inst.setOption({
      grid: { left: 0, right: 0, top: 4, bottom: 0, containLabel: false },
      xAxis: { type: 'category', show: false, data: kpi.spark.map((_, i) => i) },
      yAxis: { type: 'value', show: false },
      tooltip: { show: false },
      series: [
        {
          type: 'line',
          data: kpi.spark,
          smooth: true,
          symbol: 'none',
          lineStyle: { width: 2, color: kpi.color },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: kpi.color + '33' },
              { offset: 1, color: 'transparent' }
            ])
          }
        }
      ]
    })
    sparkInstances[kpi.key] = inst
  })
}

const initRevenueChart = () => {
  if (!revenueChart.value) return
  if (revenueInstance) revenueInstance.dispose()
  revenueInstance = echarts.init(revenueChart.value)
  const C = palette()
  const d = revenueData[range.value]
  revenueInstance.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: C.tipBg,
      borderColor: isDark() ? '#38383a' : '#d2d2d6',
      textStyle: { color: C.tipText, fontFamily: 'Inter' },
      axisPointer: { lineStyle: { color: isDark() ? '#48484a' : '#d2d2d6' } }
    },
    legend: {
      data: ['营收(¥)', '订单(笔)'],
      right: 0,
      top: 0,
      icon: 'roundRect',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { color: C.text, fontFamily: 'Inter' }
    },
    grid: { left: 8, right: 16, top: 36, bottom: 8, containLabel: true },
    xAxis: {
      type: 'category',
      data: d.x,
      boundaryGap: false,
      axisLine: { lineStyle: { color: C.grid } },
      axisTick: { show: false },
      axisLabel: { color: C.text, fontFamily: 'Inter' }
    },
    yAxis: [
      {
        type: 'value',
        splitLine: { lineStyle: { color: C.grid } },
        axisLabel: { color: C.text, fontFamily: 'Inter' }
      },
      {
        type: 'value',
        splitLine: { show: false },
        axisLabel: { color: C.text, fontFamily: 'Inter' }
      }
    ],
    series: [
      {
        name: '营收(¥)',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        showSymbol: false,
        data: d.rev,
        lineStyle: { width: 2.5, color: '#2997ff' },
        itemStyle: { color: '#2997ff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(41,151,255,0.28)' },
            { offset: 1, color: 'transparent' }
          ])
        }
      },
      {
        name: '订单(笔)',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        showSymbol: false,
        data: d.ord,
        lineStyle: { width: 2, color: '#5856d6' },
        itemStyle: { color: '#5856d6' }
      }
    ]
  })
}

const initTrafficChart = () => {
  if (!trafficChart.value) return
  if (trafficInstance) trafficInstance.dispose()
  trafficInstance = echarts.init(trafficChart.value)
  const C = palette()
  trafficInstance.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: C.tipBg,
      borderColor: isDark() ? '#38383a' : '#d2d2d6',
      textStyle: { color: C.tipText, fontFamily: 'Inter' },
      formatter: '{b}: {c}%'
    },
    series: [
      {
        type: 'pie',
        radius: ['58%', '82%'],
        center: ['50%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 8, borderColor: isDark() ? '#1c1c1e' : '#ffffff', borderWidth: 3 },
        label: { show: false },
        labelLine: { show: false },
        data: traffic.map((t, i) => ({ name: t.name, value: t.value, itemStyle: { color: trafficColors[i] } }))
      }
    ]
  })
}

const initAll = () => {
  nextTick(() => {
    initSparklines()
    initRevenueChart()
    initTrafficChart()
  })
}

const handleResize = () => {
  revenueInstance?.resize()
  trafficInstance?.resize()
  Object.values(sparkInstances).forEach((i) => i?.resize())
}

const refreshAll = () => {
  initAll()
  kpis.forEach((k) => animateCount(k))
  ElMessage.success('数据已刷新')
}

// 切换时间范围时更新营收图表
const stopRangeWatch = watch(range, () => {
  initRevenueChart()
})

onMounted(() => {
  initAll()
  kpis.forEach((k) => animateCount(k))
  // 监听主题变化，重新渲染图表以适配配色
  themeObserver = new MutationObserver(() => initAll())
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] })
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  themeObserver?.disconnect()
  stopRangeWatch()
  window.removeEventListener('resize', handleResize)
  revenueInstance?.dispose()
  trafficInstance?.dispose()
  Object.values(sparkInstances).forEach((i) => i?.dispose())
})
</script>

<style scoped>
.webhome {
  --wh-bg: #f5f5f7;
  --wh-card: #ffffff;
  --wh-text: #1d1d1f;
  --wh-sub: #6e6e73;
  --wh-border: #d2d2d6;
  --wh-accent: #2997ff;
  --wh-accent-hover: #0066cc;
  --wh-soft: #f5f5f7;
  padding: 24px;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display',
    'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  color: var(--wh-text);
  background: transparent;
  -webkit-font-smoothing: antialiased;
}

/* 暗色主题适配 */
:global([data-theme='dark']) .webhome {
  --wh-bg: #000000;
  --wh-card: #1c1c1e;
  --wh-text: #f5f5f7;
  --wh-sub: #a1a1a6;
  --wh-border: #38383a;
  --wh-soft: #2c2c2e;
}

/* ---------- Hero ---------- */
.hero-band {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f5f5f7 100%);
  border: 1px solid var(--wh-border);
  border-radius: 28px;
  padding: 32px 36px;
  margin-bottom: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}
:global([data-theme='dark']) .hero-band {
  background: linear-gradient(135deg, #1c1c1e 0%, #2c2c2e 100%);
}
.hero-eyebrow {
  font-size: 14px;
  color: var(--wh-sub);
  margin: 0 0 6px;
  letter-spacing: 0.2px;
}
.hero-title {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -0.6px;
  line-height: 1.1;
  margin: 0 0 8px;
}
.hero-subtitle {
  font-size: 15px;
  color: var(--wh-sub);
  margin: 0;
  max-width: 640px;
  line-height: 1.5;
}
.hero-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}
.btn-primary,
.btn-ghost,
.btn-setting {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 22px;
  border-radius: 980px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  font-family: inherit;
}
.btn-primary {
  background: var(--wh-accent);
  color: #ffffff;
}
.btn-primary:hover {
  background: var(--wh-accent-hover);
  transform: translateY(-1px);
}
.btn-ghost {
  background: var(--wh-card);
  color: var(--wh-text);
  border-color: var(--wh-border);
}
.btn-ghost:hover {
  border-color: var(--wh-accent);
  color: var(--wh-accent);
}

/* ---------- KPI ---------- */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
.kpi-card {
  background: var(--wh-card);
  border: 1px solid var(--wh-border);
  border-radius: 24px;
  padding: 24px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.kpi-card:hover {
  transform: scale(1.01);
  box-shadow: 0 20px 30px -16px rgba(0, 0, 0, 0.12);
}
.kpi-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.kpi-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
.kpi-trend {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 13px;
  font-weight: 600;
}
.kpi-trend.up {
  color: #34c759;
}
.kpi-trend.down {
  color: #ff3b30;
}
.trend-arrow {
  font-size: 12px;
}
.kpi-value {
  font-size: 30px;
  font-weight: 700;
  letter-spacing: -0.8px;
  line-height: 1.1;
}
.kpi-label {
  font-size: 14px;
  color: var(--wh-sub);
  margin-top: 4px;
}
.kpi-spark {
  width: 100%;
  height: 44px;
  margin-top: 12px;
}

/* ---------- 主网格 ---------- */
.main-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 16px;
}
.card {
  background: var(--wh-card);
  border: 1px solid var(--wh-border);
  border-radius: 24px;
  padding: 24px;
  transition: box-shadow 0.2s ease;
}
.card:hover {
  box-shadow: 0 12px 24px -16px rgba(0, 0, 0, 0.12);
}
.span-4 {
  grid-column: span 4;
}
.span-8 {
  grid-column: span 8;
}
.span-12 {
  grid-column: span 12;
}
.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}
.card-title {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: -0.3px;
  margin: 0 0 4px;
}
.card-desc {
  font-size: 13px;
  color: var(--wh-sub);
  margin: 0;
}
.link {
  color: var(--wh-accent);
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.2s ease;
}
.link:hover {
  color: var(--wh-accent-hover);
  text-decoration: underline;
}

/* 分段控制 */
.seg {
  display: inline-flex;
  background: var(--wh-soft);
  border-radius: 980px;
  padding: 3px;
}
.seg button {
  border: none;
  background: transparent;
  padding: 6px 14px;
  font-size: 13px;
  font-weight: 500;
  color: var(--wh-sub);
  border-radius: 980px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
}
.seg button.active {
  background: var(--wh-card);
  color: var(--wh-text);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.chart {
  width: 100%;
  height: 280px;
}
.chart.donut {
  height: 200px;
}

/* 流量图例 */
.legend-list {
  list-style: none;
  margin: 8px 0 0;
  padding: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 16px;
}
.legend-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.legend-name {
  color: var(--wh-sub);
}
.legend-val {
  margin-left: auto;
  font-weight: 600;
}

/* ---------- 表格 ---------- */
.table-wrap {
  overflow-x: auto;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.data-table th {
  text-align: left;
  font-weight: 500;
  color: var(--wh-sub);
  padding: 10px 12px;
  border-bottom: 1px solid var(--wh-border);
  white-space: nowrap;
}
.data-table td {
  padding: 14px 12px;
  border-bottom: 1px solid var(--wh-border);
  white-space: nowrap;
}
.data-table tbody tr:last-child td {
  border-bottom: none;
}
.data-table tbody tr {
  transition: background 0.15s ease;
}
.data-table tbody tr:hover {
  background: var(--wh-soft);
}
.mono {
  font-family: 'SF Mono', 'Inter', monospace;
  color: var(--wh-sub);
}
.amount {
  font-weight: 600;
}
.muted {
  color: var(--wh-sub);
}
.status-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 980px;
  font-size: 12px;
  font-weight: 500;
}
.status-tag.done {
  background: rgba(52, 199, 89, 0.14);
  color: #248a3d;
}
.status-tag.pending {
  background: rgba(255, 149, 0, 0.14);
  color: #c93400;
}
.status-tag.cancel {
  background: rgba(255, 59, 48, 0.14);
  color: #c40c0c;
}
:global([data-theme='dark']) .status-tag.done {
  color: #30d158;
}
:global([data-theme='dark']) .status-tag.pending {
  color: #ff9f0a;
}
:global([data-theme='dark']) .status-tag.cancel {
  color: #ff453a;
}

/* ---------- 快捷入口 ---------- */
.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.quick-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border: 1px solid var(--wh-border);
  border-radius: 16px;
  background: transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  color: var(--wh-text);
}
.quick-item:hover {
  border-color: var(--wh-accent);
  background: var(--wh-soft);
  transform: translateY(-1px);
}
.quick-icon {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}
.quick-label {
  font-size: 14px;
  font-weight: 500;
}

/* ---------- 系统状态 ---------- */
.badge-ok {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #248a3d;
  background: rgba(52, 199, 89, 0.12);
  padding: 5px 12px;
  border-radius: 980px;
}
:global([data-theme='dark']) .badge-ok {
  color: #30d158;
}
.badge-ok .dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #34c759;
  box-shadow: 0 0 0 3px rgba(52, 199, 89, 0.2);
  animation: pulse 1.6s infinite;
}
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(52, 199, 89, 0.4); }
  70% { box-shadow: 0 0 0 6px rgba(52, 199, 89, 0); }
  100% { box-shadow: 0 0 0 0 rgba(52, 199, 89, 0); }
}
.status-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}
.status-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}
.status-label {
  color: var(--wh-sub);
}
.status-val {
  font-weight: 600;
}
.status-bar {
  height: 8px;
  background: var(--wh-soft);
  border-radius: 980px;
  overflow: hidden;
}
.status-fill {
  height: 100%;
  border-radius: 980px;
  transition: width 0.6s ease;
}

/* ---------- 响应式 ---------- */
@media (max-width: 1199px) {
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .span-8 {
    grid-column: span 12;
  }
  .span-4 {
    grid-column: span 6;
  }
  .status-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 767px) {
  .webhome {
    padding: 16px;
  }
  .hero-band {
    flex-direction: column;
    align-items: flex-start;
    padding: 24px;
  }
  .hero-title {
    font-size: 26px;
  }
  .kpi-grid {
    grid-template-columns: 1fr;
  }
  .span-4,
  .span-8 {
    grid-column: span 12;
  }
  .status-grid {
    grid-template-columns: 1fr;
  }
  .quick-grid {
    grid-template-columns: 1fr;
  }
  .hero-actions {
    width: 100%;
  }
  .btn-primary,
  .btn-ghost {
    flex: 1;
    justify-content: center;
  }
}
</style>
