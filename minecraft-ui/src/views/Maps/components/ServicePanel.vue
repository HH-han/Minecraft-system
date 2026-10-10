<template>
  <Teleport to="body">
    <div v-show="visible" class="svc-overlay">
      <header class="svc-header">
        <div class="svc-title">
          <span class="svc-logo">🧰</span>
          <div>
            <h2>高德服务工具箱</h2>
            <p>「Web服务」Key · REST API · {{ ALL_TOOLS.length }} 项能力全覆盖</p>
          </div>
        </div>
        <button class="close-btn" @click="$emit('close')" aria-label="关闭工具箱">✕</button>
      </header>

      <div class="svc-body">
        <aside class="svc-menu">
          <div v-for="g in SERVICE_GROUPS" :key="g.id" class="menu-group">
            <div class="menu-group-title">{{ g.title }}</div>
            <button
              v-for="t in g.tools"
              :key="t.id"
              class="menu-item"
              :class="{ 'is-active': current?.id === t.id }"
              @click="selectTool(t)"
            >
              <span class="menu-icon">{{ t.icon }}</span>
              <span class="menu-name">{{ t.name }}</span>
            </button>
          </div>
          <div class="menu-foot">
            <span class="foot-dot"></span>
            Web服务 Key：db60e30c…f79ba7
          </div>
        </aside>

        <main class="svc-main">
          <template v-if="current">
            <div class="tool-head">
              <h2>{{ current.icon }} {{ current.name }}</h2>
              <p>{{ current.desc }}</p>
            </div>

            <form class="tool-form" @submit.prevent="run">
              <div
                v-for="f in visibleFields"
                :key="f.key"
                class="field"
                :class="{ 'span-2': f.span === 2 }"
              >
                <label>{{ f.label }}<i v-if="f.required">*</i></label>
                <select v-if="f.type === 'select'" v-model="form[f.key]">
                  <option v-for="o in f.options" :key="o.v" :value="o.v">{{ o.n }}</option>
                </select>
                <textarea
                  v-else-if="f.type === 'textarea'"
                  v-model="form[f.key]"
                  :placeholder="f.placeholder || ''"
                  rows="2"
                ></textarea>
                <input
                  v-else
                  v-model="form[f.key]"
                  :placeholder="f.placeholder || ''"
                  autocomplete="off"
                />
                <p v-if="f.hint" class="field-hint">{{ f.hint }}</p>
              </div>

              <div class="form-actions">
                <button type="submit" class="run-btn" :disabled="loading">
                  {{ loading ? '请求中…' : '发起请求' }}
                </button>
                <button type="button" class="ghost-btn" :disabled="loading" @click="resetForm">重置参数</button>
              </div>
            </form>

            <p v-if="error" class="svc-error">⚠️ {{ error }}</p>

            <section v-if="result" class="result">
              <div class="result-head">
                <h3>返回结果</h3>
                <button class="ghost-btn small" @click="showRaw = !showRaw">
                  {{ showRaw ? '格式化视图' : '原始 JSON' }}
                </button>
              </div>

              <pre v-if="showRaw" class="raw-json">{{ pretty(result) }}</pre>

              <template v-else>
                <!-- 静态地图 -->
                <div v-if="current.kind === 'staticmap'" class="staticmap-box">
                  <img :src="result.url" alt="静态地图" />
                  <a :href="result.url" target="_blank" rel="noopener" class="staticmap-link">在新窗口查看原图 ↗</a>
                </div>

                <!-- 地理编码 -->
                <div v-else-if="current.kind === 'geocode'" class="card-list">
                  <div v-for="(g, i) in result" :key="i" class="poi-card" title="点击复制坐标" @click="copy(g.location)">
                    <div class="poi-name">{{ fmt(g.formatted_address) }}</div>
                    <div class="poi-rows">
                      <span>{{ fmt(g.province) }}{{ fmt(g.city) }}{{ fmt(g.district) }}</span>
                      <span v-if="g.adcode">adcode：{{ g.adcode }}</span>
                      <span v-if="g.level">级别：{{ g.level }}</span>
                    </div>
                    <div class="poi-loc">坐标 {{ fmt(g.location) || '—' }}（点击复制）</div>
                  </div>
                </div>

                <!-- 逆地理编码 -->
                <div v-else-if="current.kind === 'regeo'" class="regeo">
                  <div class="regeo-hero">📍 {{ fmt(result.formatted_address) || '—' }}</div>
                  <div class="kv-grid">
                    <div v-for="kv in regeoRows" :key="kv[0]" class="kv">
                      <span>{{ kv[0] }}</span><b>{{ kv[1] || '—' }}</b>
                    </div>
                  </div>
                  <template v-if="result.pois?.length">
                    <div class="sub-head">附近 POI（{{ result.pois.length }}）</div>
                    <div class="card-list">
                      <div v-for="(p, i) in result.pois" :key="i" class="poi-card" @click="copy(p.location)">
                        <div class="poi-name">{{ fmt(p.name) }}</div>
                        <div class="poi-rows">
                          <span>{{ fmt(p.address) }}</span>
                          <span v-if="p.distance">距中心 {{ p.distance }} 米</span>
                        </div>
                      </div>
                    </div>
                  </template>
                </div>

                <!-- IP 定位 -->
                <div v-else-if="current.kind === 'ip'" class="kv-grid">
                  <div v-for="kv in ipRows" :key="kv[0]" class="kv">
                    <span>{{ kv[0] }}</span><b>{{ kv[1] || '—' }}</b>
                  </div>
                </div>

                <!-- 坐标转换 -->
                <div v-else-if="current.kind === 'convert'" class="convert">
                  <div class="kv-grid">
                    <div class="kv"><span>源坐标系</span><b>{{ result.coordsys }}</b></div>
                    <div class="kv"><span>转换结果</span><b>{{ result.locations || '—' }}</b></div>
                  </div>
                  <div class="coord-list">
                    <div v-for="(c, i) in result.list" :key="i" class="coord-item" @click="copy(c)">
                      <span class="coord-idx">{{ i + 1 }}</span>
                      <span class="coord-val">{{ c }}</span>
                      <span class="coord-copy">复制</span>
                    </div>
                  </div>
                </div>

                <!-- POI 搜索 -->
                <div v-else-if="current.kind === 'pois'">
                  <p class="result-meta" v-if="result.count">共 {{ result.count }} 条结果</p>
                  <div class="card-list">
                    <div v-for="p in result.pois" :key="p.id" class="poi-card" title="点击复制坐标" @click="copy(p.location)">
                      <div class="poi-name">
                        {{ fmt(p.name) }}
                        <em v-if="p.distance" class="poi-distance">{{ fmtDist(p.distance) }}</em>
                      </div>
                      <div class="poi-rows"><span>{{ (fmt(p.type) || '').split(';')[0] }}</span></div>
                      <div class="poi-rows"><span>📮 {{ fmt(p.address) || '—' }}</span></div>
                      <div class="poi-rows" v-if="telStr(p)"><span>☎️ {{ telStr(p) }}</span></div>
                      <div class="poi-loc">{{ fmt(p.location) || '—' }}（点击复制）</div>
                    </div>
                  </div>
                </div>

                <!-- 输入提示 -->
                <div v-else-if="current.kind === 'tips'" class="tips-list">
                  <div v-for="(t, i) in result" :key="i" class="tip-item" title="点击复制坐标" @click="copy(t.location)">
                    <span class="tip-name">{{ fmt(t.name) }}</span>
                    <span class="tip-meta">{{ fmt(t.district) }}{{ t.adcode ? `（${t.adcode}）` : '' }}</span>
                  </div>
                </div>

                <!-- 行政区划 -->
                <div v-else-if="current.kind === 'district'" class="district-list">
                  <div
                    v-for="(d, i) in districtRows"
                    :key="i"
                    class="district-item"
                    :style="districtIndent(d.depth)"
                    @click="copy(d.center)"
                  >
                    <span class="district-label">{{ d.name }}</span>
                    <span class="district-meta">{{ d.adcode }} · {{ d.level }}</span>
                  </div>
                </div>

                <!-- 天气 -->
                <div v-else-if="current.kind === 'weather'">
                  <template v-if="result.lives.length">
                    <div class="sub-head">实况天气</div>
                    <div class="weather-grid">
                      <div v-for="(l, i) in result.lives" :key="i" class="weather-card">
                        <div class="weather-city">{{ l.city }}</div>
                        <div class="weather-main">{{ l.weather }} · {{ l.temperature }}℃</div>
                        <div class="weather-meta">{{ l.winddirection }}风 {{ l.windpower }}级 · 湿度 {{ l.humidity }}%</div>
                        <div class="weather-meta">更新于 {{ l.reporttime }}</div>
                      </div>
                    </div>
                  </template>
                  <template v-if="result.forecasts.length">
                    <div class="sub-head">未来预报</div>
                    <div v-for="f in result.forecasts" :key="f.city" class="forecast-block">
                      <div class="weather-city">{{ f.city }} · 更新于 {{ f.reporttime }}</div>
                      <div class="weather-grid">
                        <div v-for="c in f.casts" :key="c.date" class="weather-card">
                          <div class="weather-city">{{ c.date.slice(5) }} {{ weekName(c.week) }}</div>
                          <div class="weather-main">{{ c.daytemp }}℃ / {{ c.nighttemp }}℃</div>
                          <div class="weather-meta">{{ c.dayweather }} → {{ c.nightweather }}</div>
                          <div class="weather-meta">{{ c.daywind }}风 {{ c.daypower }}级</div>
                        </div>
                      </div>
                    </div>
                  </template>
                </div>

                <!-- 路径规划 -->
                <div v-else-if="current.kind === 'route'">
                  <div class="route-summary">
                    <div class="summary-item"><span>方式</span><b>{{ result.modeLabel }}</b></div>
                    <div class="summary-item"><span>总里程</span><b>{{ result.distanceText || '—' }}</b></div>
                    <div class="summary-item"><span>预计耗时</span><b>{{ result.durationText || '—' }}</b></div>
                  </div>
                  <div v-if="result.steps?.length" class="steps-list">
                    <div v-for="(s, i) in result.steps" :key="i" class="step-item" @click="copy(s.location)">
                      <span class="step-index">{{ i + 1 }}</span>
                      <span class="step-text">{{ s.instruction }}<em v-if="s.road">（{{ s.road }}）</em></span>
                      <span v-if="s.distanceText" class="step-distance">{{ s.distanceText }}</span>
                    </div>
                  </div>
                </div>

                <!-- 交通态势 -->
                <div v-else-if="current.kind === 'traffic'">
                  <div v-if="result.evaluation" class="traffic-eval">
                    <div class="traffic-desc">{{ result.evaluation.description || statusText(result.evaluation.status) }}</div>
                    <div class="traffic-bars">
                      <div v-for="b in trafficBars" :key="b.label" class="traffic-bar">
                        <span class="bar-label">{{ b.label }}</span>
                        <div class="bar-track"><div class="bar-fill" :style="{ width: b.pct + '%', background: b.color }"></div></div>
                        <span class="bar-pct">{{ b.pct }}%</span>
                      </div>
                    </div>
                  </div>
                  <template v-if="roads.length">
                    <div class="sub-head">路段详情（{{ roads.length }}）</div>
                    <div class="road-list">
                      <div v-for="(r, i) in roads.slice(0, 60)" :key="i" class="road-item">
                        <span class="road-status" :class="'st-' + (r.status || r.status_code)">{{ statusText(r.status || r.status_code) }}</span>
                        <span class="road-name">{{ r.name }}</span>
                        <span v-if="r.direction" class="road-dir">{{ r.direction }}</span>
                        <span v-if="r.speed" class="road-speed">均速 {{ r.speed }} km/h</span>
                      </div>
                    </div>
                  </template>
                </div>

                <!-- 通用 JSON -->
                <pre v-else class="raw-json">{{ pretty(result) }}</pre>
              </template>
            </section>
          </template>
        </main>
      </div>

      <div v-if="toast" class="svc-toast">{{ toast }}</div>
    </div>
  </Teleport>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { SERVICE_GROUPS, ALL_TOOLS, initialValues } from '../services/tools.js'

const props = defineProps({
  visible: { type: Boolean, default: false }
})

defineEmits(['close'])

const current = ref(null)
const form = reactive({})
const loading = ref(false)
const error = ref('')
const result = ref(null)
const showRaw = ref(false)
const toast = ref('')

/** 按 showIf 条件过滤字段（围栏/猎鹰等多操作工具联动） */
const visibleFields = computed(() =>
  (current.value?.fields || []).filter(f => !f.showIf || f.showIf(form))
)

function selectTool(tool) {
  current.value = tool
  error.value = ''
  result.value = null
  showRaw.value = false
  for (const k of Object.keys(form)) delete form[k]
  Object.assign(form, initialValues(tool))
}

watch(
  () => props.visible,
  v => {
    if (v && !current.value) selectTool(ALL_TOOLS[0])
  }
)

async function run() {
  for (const f of visibleFields.value) {
    if (f.required && !String(form[f.key] ?? '').trim()) {
      error.value = `请先填写「${f.label}」`
      return
    }
  }
  loading.value = true
  error.value = ''
  result.value = null
  try {
    result.value = await current.value.run({ ...form })
  } catch (e) {
    error.value = e?.message || '请求失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function resetForm() {
  if (current.value) selectTool(current.value)
}

/* ---------- 渲染辅助 ---------- */

/** 高德返回中空数组 [] 代表缺失字符串，统一处理 */
function fmt(v) {
  return Array.isArray(v) ? (v.length ? String(v[0]) : '') : (v ?? '')
}

function telStr(p) {
  return fmt(p.tel)
}

function fmtDist(m) {
  const n = Number(m) || 0
  return n >= 1000 ? `${(n / 1000).toFixed(1)} km` : `${Math.round(n)} 米`
}

function pretty(v) {
  return JSON.stringify(v, null, 2)
}

async function copy(text) {
  if (!text) return
  try {
    await navigator.clipboard.writeText(String(text))
    toast.value = `已复制：${text}`
  } catch {
    toast.value = '复制失败，请手动选择文本'
  }
  setTimeout(() => { toast.value = '' }, 1600)
}

const regeoRows = computed(() => {
  const c = result.value?.addressComponent || {}
  return [
    ['省份', fmt(c.province)],
    ['城市', fmt(c.city)],
    ['区县', fmt(c.district)],
    ['adcode', c.adcode || ''],
    ['乡镇/街道', fmt(c.township)],
    ['道路', c.streetNumber?.street || ''],
    ['门牌号', c.streetNumber?.number || '']
  ]
})

const ipRows = computed(() => {
  const r = result.value || {}
  return [
    ['省份', fmt(r.province)],
    ['城市', fmt(r.city)],
    ['adcode', r.adcode || ''],
    ['城市矩形范围', fmt(r.rectangle)]
  ]
})

const districtRows = computed(() => {
  const out = []
  const walk = (list, depth) => {
    for (const d of list || []) {
      out.push({ name: d.name, adcode: d.adcode, level: d.level, center: d.center, depth })
      if (d.districts?.length) walk(d.districts, depth + 1)
    }
  }
  walk(result.value, 0)
  return out
})

const districtIndent = depth => ({ paddingLeft: depth * 18 + 12 + 'px' })

function weekName(w) {
  const m = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
  return m[w] || ''
}

function statusText(s) {
  return { 1: '畅通', 2: '缓行', 3: '拥堵', 4: '严重拥堵' }[String(s)] || '未知'
}

const trafficBars = computed(() => {
  const e = result.value?.evaluation
  if (!e) return []
  const total = (Number(e.expedite) || 0) + (Number(e.congested) || 0) + (Number(e.blocked) || 0) + (Number(e.unknown) || 0) || 1
  return [
    { label: '畅通', value: e.expedite, color: '#30d158' },
    { label: '缓行', value: e.congested, color: '#ffd60a' },
    { label: '拥堵', value: e.blocked, color: '#ff453a' },
    { label: '未采集', value: e.unknown, color: '#8e8e93' }
  ].map(b => ({ ...b, pct: Math.round((Number(b.value) || 0) / total * 100) }))
})

const roads = computed(() => result.value?.roads || [])
</script>

<style scoped>
.svc-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  background: #0d1117;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* ---------- 头部 ---------- */
.svc-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.svc-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.svc-logo {
  font-size: 26px;
}

.svc-title h2 {
  margin: 0;
  font-size: 17px;
  color: #f5f5f7;
}

.svc-title p {
  margin: 2px 0 0;
  font-size: 12px;
  color: #86868b;
}

.close-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.08);
  color: #f5f5f7;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.16);
}

/* ---------- 主体 ---------- */
.svc-body {
  flex: 1;
  display: flex;
  min-height: 0;
}

.svc-menu {
  width: 264px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 12px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.03);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.menu-group-title {
  padding: 0 10px 4px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #86868b;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 9px 10px;
  border: 1px solid transparent;
  border-radius: 10px;
  background: transparent;
  color: #d1d1d6;
  font-size: 13px;
  text-align: left;
  cursor: pointer;
  transition: all 0.18s ease;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.07);
}

.menu-item.is-active {
  background: rgba(10, 132, 255, 0.2);
  border-color: rgba(10, 132, 255, 0.5);
  color: #6db8ff;
}

.menu-icon {
  flex-shrink: 0;
  font-size: 15px;
}

.menu-foot {
  margin-top: auto;
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #86868b;
}

.foot-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #30d158;
  box-shadow: 0 0 6px rgba(48, 209, 88, 0.8);
}

.svc-main {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  padding: 22px 26px 40px;
}

.tool-head h2 {
  margin: 0 0 6px;
  font-size: 20px;
  color: #f5f5f7;
}

.tool-head p {
  margin: 0 0 18px;
  font-size: 13px;
  line-height: 1.55;
  color: #8e8e93;
  max-width: 720px;
}

/* ---------- 表单 ---------- */
.tool-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 14px;
  padding: 16px;
  margin-bottom: 16px;
  max-width: 860px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.field.span-2 {
  grid-column: span 2;
}

.field label {
  font-size: 12px;
  font-weight: 600;
  color: #aeb7c4;
}

.field label i {
  color: #ff453a;
  font-style: normal;
  margin-left: 2px;
}

.field input,
.field textarea,
.field select {
  padding: 9px 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  color: #f5f5f7;
  font-size: 13px;
  font-family: inherit;
  outline: none;
  transition: border-color 0.18s ease;
  resize: vertical;
}

.field textarea {
  min-height: 62px;
}

.field input:focus,
.field textarea:focus,
.field select:focus {
  border-color: #0a84ff;
}

.field input::placeholder,
.field textarea::placeholder {
  color: #6e6e73;
}

.field select option {
  background: #1c1c1e;
  color: #f5f5f7;
}

.field-hint {
  margin: 0;
  font-size: 11px;
  color: #6e6e73;
  line-height: 1.4;
}

.form-actions {
  grid-column: span 2;
  display: flex;
  gap: 10px;
}

.run-btn {
  padding: 11px 28px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #0a84ff, #0055d4);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.18s ease, opacity 0.18s ease;
}

.run-btn:hover:not(:disabled) {
  transform: translateY(-1px);
}

.run-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ghost-btn {
  padding: 11px 18px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 12px;
  background: transparent;
  color: #d1d1d6;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.ghost-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.08);
}

.ghost-btn.small {
  padding: 5px 12px;
  font-size: 11px;
  border-radius: 8px;
}

/* ---------- 结果区 ---------- */
.svc-error {
  margin: 0 0 16px;
  padding: 11px 14px;
  border-radius: 10px;
  background: rgba(255, 69, 58, 0.14);
  border: 1px solid rgba(255, 69, 58, 0.3);
  color: #ff9f9a;
  font-size: 13px;
  line-height: 1.5;
}

.result {
  max-width: 960px;
}

.result-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.result-head h3 {
  margin: 0;
  font-size: 14px;
  color: #8ea8c9;
  letter-spacing: 1px;
}

.result-meta {
  margin: 0 0 10px;
  font-size: 12px;
  color: #86868b;
}

.sub-head {
  margin: 18px 0 10px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #86868b;
}

.raw-json {
  margin: 0;
  padding: 14px;
  max-height: 480px;
  overflow: auto;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #a5d6ff;
  font-size: 12px;
  line-height: 1.55;
  font-family: 'SF Mono', Menlo, Consolas, monospace;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 卡片 */
.card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 10px;
}

.poi-card {
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  cursor: pointer;
  transition: background 0.18s ease, transform 0.18s ease;
}

.poi-card:hover {
  background: rgba(10, 132, 255, 0.12);
  transform: translateY(-1px);
}

.poi-name {
  font-size: 14px;
  font-weight: 600;
  color: #f5f5f7;
  display: flex;
  align-items: center;
  gap: 8px;
}

.poi-distance {
  font-style: normal;
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(48, 209, 88, 0.18);
  color: #7ee2a0;
}

.poi-rows {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 12px;
  margin-top: 5px;
  font-size: 12px;
  color: #aeb7c4;
}

.poi-loc {
  margin-top: 7px;
  font-size: 11px;
  color: #6db8ff;
  font-family: 'SF Mono', Menlo, Consolas, monospace;
}

/* 逆地理 / IP / 键值 */
.regeo-hero {
  padding: 16px;
  border-radius: 14px;
  background: rgba(10, 132, 255, 0.14);
  border: 1px solid rgba(10, 132, 255, 0.35);
  color: #f5f5f7;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.5;
}

.kv-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 8px;
  margin-top: 12px;
}

.kv {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.kv span {
  font-size: 11px;
  color: #86868b;
}

.kv b {
  font-size: 13px;
  color: #f5f5f7;
  word-break: break-all;
}

/* 坐标转换 */
.coord-list {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.coord-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 12px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.05);
  cursor: pointer;
  transition: background 0.18s ease;
}

.coord-item:hover {
  background: rgba(10, 132, 255, 0.12);
}

.coord-idx {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(10, 132, 255, 0.25);
  color: #6db8ff;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.coord-val {
  flex: 1;
  font-family: 'SF Mono', Menlo, Consolas, monospace;
  font-size: 12.5px;
  color: #f5f5f7;
}

.coord-copy {
  font-size: 11px;
  color: #6db8ff;
}

/* 输入提示 */
.tips-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-width: 640px;
}

.tip-item {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  cursor: pointer;
  transition: background 0.18s ease;
}

.tip-item:hover {
  background: rgba(10, 132, 255, 0.12);
}

.tip-name {
  font-size: 13.5px;
  color: #f5f5f7;
  font-weight: 500;
}

.tip-meta {
  flex-shrink: 0;
  font-size: 11.5px;
  color: #8e8e93;
}

/* 行政区划 */
.district-list {
  display: flex;
  flex-direction: column;
  gap: 3px;
  max-width: 560px;
}

.district-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 7px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.district-item:hover {
  background: rgba(10, 132, 255, 0.12);
}

.district-label {
  font-size: 13px;
  color: #f5f5f7;
}

.district-meta {
  font-size: 11px;
  color: #86868b;
}

/* 天气 */
.weather-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 10px;
}

.weather-card {
  padding: 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.weather-city {
  font-size: 12px;
  color: #8ea8c9;
  margin-bottom: 6px;
}

.weather-main {
  font-size: 18px;
  font-weight: 700;
  color: #6db8ff;
  margin-bottom: 6px;
}

.weather-meta {
  font-size: 12px;
  color: #aeb7c4;
  line-height: 1.6;
}

.forecast-block {
  margin-bottom: 14px;
}

/* 路线 */
.route-summary {
  display: flex;
  gap: 10px;
  max-width: 560px;
  margin-bottom: 14px;
}

.summary-item {
  flex: 1;
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(10, 132, 255, 0.12);
  border: 1px solid rgba(10, 132, 255, 0.28);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-item span {
  font-size: 11px;
  color: #8ea8c9;
}

.summary-item b {
  font-size: 15px;
  color: #6db8ff;
}

.steps-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 720px;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.step-item:hover {
  background: rgba(255, 255, 255, 0.07);
}

.step-index {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(10, 132, 255, 0.25);
  color: #6db8ff;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 1px;
}

.step-text {
  flex: 1;
  font-size: 12.5px;
  color: #e5e5ea;
  line-height: 1.5;
}

.step-text em {
  font-style: normal;
  color: #8e8e93;
}

.step-distance {
  flex-shrink: 0;
  font-size: 11px;
  color: #86868b;
}

/* 交通态势 */
.traffic-eval {
  max-width: 640px;
  padding: 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.traffic-desc {
  font-size: 14px;
  font-weight: 600;
  color: #f5f5f7;
  margin-bottom: 12px;
  line-height: 1.5;
}

.traffic-bars {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.traffic-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.bar-label {
  flex-shrink: 0;
  width: 46px;
  font-size: 12px;
  color: #aeb7c4;
}

.bar-track {
  flex: 1;
  height: 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.bar-pct {
  flex-shrink: 0;
  width: 40px;
  text-align: right;
  font-size: 11.5px;
  color: #8e8e93;
}

.road-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
  max-width: 720px;
}

.road-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.04);
  font-size: 12.5px;
}

.road-status {
  flex-shrink: 0;
  padding: 2px 9px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.08);
  color: #d1d1d6;
}

.road-status.st-1 { background: rgba(48, 209, 88, 0.2); color: #7ee2a0; }
.road-status.st-2 { background: rgba(255, 214, 10, 0.2); color: #ffe066; }
.road-status.st-3 { background: rgba(255, 69, 58, 0.22); color: #ff9f9a; }
.road-status.st-4 { background: rgba(255, 45, 85, 0.3); color: #ff7b92; }

.road-name {
  flex: 1;
  color: #f5f5f7;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.road-dir,
.road-speed {
  flex-shrink: 0;
  font-size: 11.5px;
  color: #8e8e93;
}

/* 静态地图 */
.staticmap-box {
  display: inline-flex;
  flex-direction: column;
  gap: 10px;
  padding: 14px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.staticmap-box img {
  display: block;
  max-width: 100%;
  border-radius: 10px;
  background: #1c1c1e;
}

.staticmap-link {
  font-size: 12px;
  color: #6db8ff;
  text-decoration: none;
}

.staticmap-link:hover {
  text-decoration: underline;
}

/* Toast */
.svc-toast {
  position: fixed;
  bottom: 36px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2100;
  padding: 10px 20px;
  border-radius: 999px;
  background: rgba(28, 28, 30, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.14);
  color: #f5f5f7;
  font-size: 13px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.4);
  max-width: 80vw;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ---------- 响应式 ---------- */
@media (max-width: 900px) {
  .svc-body {
    flex-direction: column;
  }

  .svc-menu {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 6px;
    max-height: 172px;
    padding: 10px;
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  }

  .menu-group {
    width: 100%;
  }

  .menu-group-title {
    display: none;
  }

  .menu-group .menu-item {
    display: inline-flex;
    width: auto;
    padding: 7px 12px;
  }

  .menu-foot {
    display: none;
  }

  .svc-main {
    padding: 16px;
  }

  .tool-form {
    grid-template-columns: 1fr;
  }

  .field.span-2,
  .form-actions {
    grid-column: span 1;
  }

  .route-summary {
    flex-direction: column;
  }
}
</style>
