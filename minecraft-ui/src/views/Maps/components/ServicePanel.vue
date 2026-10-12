<template>
  <div class="svc-root">
    <p class="svc-intro">
      基于「Web服务」Key 的 {{ ALL_TOOLS.length }} 项 REST 服务，结果同步渲染到右侧地图。
    </p>

    <div class="svc-picker" :class="{ 'is-open': pickerOpen }">
      <button
        type="button"
        class="svc-select"
        aria-label="选择服务工具"
        :aria-expanded="pickerOpen"
        @click="pickerOpen = !pickerOpen"
      >
        <span class="svc-picker-label">{{ current ? `${current.icon} ${current.name}` : '选择服务工具' }}</span>
        <span class="svc-picker-arrow">▾</span>
      </button>
      <div v-if="pickerOpen" class="svc-picker-pop">
        <template v-for="g in SERVICE_GROUPS" :key="g.id">
          <div class="svc-picker-group">{{ g.title }}</div>
          <button
            v-for="t in g.tools"
            :key="t.id"
            type="button"
            class="svc-picker-opt"
            :class="{ 'is-active': t.id === selectedId }"
            @click="pickTool(t.id)"
          >{{ t.icon }} {{ t.name }}</button>
        </template>
      </div>
    </div>

    <template v-if="current">
      <div class="tool-head">
        <h3>{{ current.icon }} {{ current.name }}</h3>
        <p>{{ current.desc }}</p>
      </div>

      <form class="tool-form" @submit.prevent="run">
        <div
          v-for="f in visibleFields"
          :key="f.key"
          class="field"
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
          <button type="button" class="ghost-btn" :disabled="loading" @click="resetForm">重置</button>
        </div>
      </form>

      <p v-if="error" class="svc-error">⚠️ {{ error }}</p>

      <section v-if="result" class="result">
        <div class="result-head">
          <h4>返回结果</h4>
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
          <div v-else-if="current.kind === 'geocode'" class="stack">
            <div v-for="(g, i) in result" :key="i" class="poi-card" title="点击复制坐标" @click="copy(g.location)">
              <div class="poi-name">{{ fmt(g.formatted_address) }}</div>
              <div class="poi-rows">
                <span>{{ fmt(g.province) }}{{ fmt(g.city) }}{{ fmt(g.district) }}</span>
                <span v-if="g.adcode">adcode：{{ g.adcode }}</span>
                <span v-if="g.level">级别：{{ g.level }}</span>
              </div>
              <div class="poi-loc">{{ fmt(g.location) || '—' }}（点击复制）</div>
            </div>
          </div>

          <!-- 逆地理编码 -->
          <div v-else-if="current.kind === 'regeo'">
            <div class="regeo-hero">📍 {{ fmt(result.formatted_address) || '—' }}</div>
            <div class="kv-grid">
              <div v-for="kv in regeoRows" :key="kv[0]" class="kv">
                <span>{{ kv[0] }}</span><b>{{ kv[1] || '—' }}</b>
              </div>
            </div>
            <template v-if="result.pois?.length">
              <div class="sub-head">附近 POI（{{ result.pois.length }}）</div>
              <div class="stack">
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
          <div v-else-if="current.kind === 'convert'">
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
            <div class="stack">
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
          <div v-else-if="current.kind === 'tips'" class="stack">
            <div v-for="(t, i) in result" :key="i" class="tip-item" title="点击复制坐标" @click="copy(t.location)">
              <span class="tip-name">{{ fmt(t.name) }}</span>
              <span class="tip-meta">{{ fmt(t.district) }}</span>
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
              <div v-for="(l, i) in result.lives" :key="i" class="weather-card">
                <div class="weather-city">{{ l.city }}</div>
                <div class="weather-main">{{ l.weather }} · {{ l.temperature }}℃</div>
                <div class="weather-meta">{{ l.winddirection }}风 {{ l.windpower }}级 · 湿度 {{ l.humidity }}%</div>
                <div class="weather-meta">更新于 {{ l.reporttime }}</div>
              </div>
            </template>
            <template v-if="result.forecasts.length">
              <div class="sub-head">未来预报</div>
              <div v-for="f in result.forecasts" :key="f.city" class="forecast-block">
                <div class="weather-city">{{ f.city }} · 更新于 {{ f.reporttime }}</div>
                <div v-for="c in f.casts" :key="c.date" class="weather-card">
                  <div class="weather-city">{{ c.date.slice(5) }} {{ weekName(c.week) }}</div>
                  <div class="weather-main">{{ c.daytemp }}℃ / {{ c.nighttemp }}℃</div>
                  <div class="weather-meta">{{ c.dayweather }} → {{ c.nightweather }}</div>
                  <div class="weather-meta">{{ c.daywind }}风 {{ c.daypower }}级</div>
                </div>
              </div>
            </template>
          </div>

          <!-- 路径规划 -->
          <div v-else-if="current.kind === 'route'">
            <div class="route-summary">
              <div class="summary-item"><span>方式</span><b>{{ result.modeLabel }}</b></div>
              <div class="summary-item"><span>总里程</span><b>{{ result.distanceText || '—' }}</b></div>
              <div class="summary-item"><span>耗时</span><b>{{ result.durationText || '—' }}</b></div>
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
                <div v-for="(r, i) in roads.slice(0, 40)" :key="i" class="road-item">
                  <span class="road-status" :class="'st-' + (r.status || r.status_code)">{{ statusText(r.status || r.status_code) }}</span>
                  <span class="road-name">{{ r.name }}</span>
                  <span v-if="r.speed" class="road-speed">{{ r.speed }}km/h</span>
                </div>
              </div>
            </template>
          </div>

          <!-- 通用 JSON -->
          <pre v-else class="raw-json">{{ pretty(result) }}</pre>
        </template>
      </section>
    </template>

    <div v-if="toast" class="svc-toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, shallowRef, watch } from 'vue'
import { SERVICE_GROUPS, ALL_TOOLS, initialValues } from '../services/tools.js'
import { createRenderer } from '../services/mapRender.js'

const props = defineProps({
  /** AMap 构造函数（JSAPI 加载完成后传入） */
  amap: { type: Object, default: null },
  /** 地图实例 */
  map: { type: Object, default: null }
})

const current = ref(null)
const selectedId = ref(ALL_TOOLS[0]?.id)
const form = reactive({})
const loading = ref(false)
const error = ref('')
const result = ref(null)
const showRaw = ref(false)
const toast = ref('')

/** 地图渲染器（依赖地图实例，懒创建） */
const renderer = shallowRef(null)

function ensureRenderer() {
  if (!props.amap || !props.map) return null
  if (!renderer.value) renderer.value = createRenderer(props.map, props.amap)
  return renderer.value
}

watch(selectedId, id => {
  const found = ALL_TOOLS.find(t => t.id === id)
  if (found) selectTool(found)
}, { immediate: true })

/* ---------- 服务选择器（自定义下拉，替代原生 select 弹出层） ---------- */
const pickerOpen = ref(false)

function pickTool(id) {
  selectedId.value = id
  pickerOpen.value = false
}

function onDocMouseDown(e) {
  if (!e.target.closest?.('.svc-picker')) pickerOpen.value = false
}

function onDocKeydown(e) {
  if (e.key === 'Escape') pickerOpen.value = false
}

onMounted(() => {
  document.addEventListener('mousedown', onDocMouseDown)
  document.addEventListener('keydown', onDocKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', onDocMouseDown)
  document.removeEventListener('keydown', onDocKeydown)
})

/** 按 showIf 条件过滤字段（围栏/猎鹰等多操作工具联动） */
const visibleFields = computed(() =>
  (current.value?.fields || []).filter(f => !f.showIf || f.showIf(form))
)

function selectTool(tool) {
  current.value = tool
  error.value = ''
  result.value = null
  showRaw.value = false
  renderer.value?.clear()
  for (const k of Object.keys(form)) delete form[k]
  Object.assign(form, initialValues(tool))
}

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
    const values = { ...form }
    result.value = await current.value.run(values)
    // 成功后把结果渲染到地图（渲染失败不影响结果展示）
    const r = ensureRenderer()
    if (r) {
      r.clear()
      if (current.value.render) {
        try { current.value.render(values, result.value, r) } catch (e) { console.warn('地图渲染失败:', e) }
      }
    }
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

const districtIndent = depth => ({ paddingLeft: depth * 14 + 10 + 'px' })

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
.svc-root {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}

.svc-intro {
  margin: 0;
  font-size: 11.5px;
  line-height: 1.5;
  color: var(--theme-text-3);
}

.svc-select {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-size: 13px;
  font-family: inherit;
  text-align: left;
  outline: none;
  cursor: pointer;
}

.svc-select:focus {
  border-color: var(--theme-primary);
}

/* ---------- 自定义下拉（隐藏滚动条，随明暗主题切换） ---------- */
.svc-picker {
  position: relative;
}

.svc-picker-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.svc-picker-arrow {
  flex-shrink: 0;
  font-size: 11px;
  opacity: 0.7;
  transition: transform 0.18s ease;
}

.svc-picker.is-open .svc-picker-arrow {
  transform: rotate(180deg);
}

.svc-picker.is-open .svc-select {
  border-color: var(--theme-primary);
}

.svc-picker-pop {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  z-index: 30;
  max-height: 320px;
  overflow-y: auto;
  scrollbar-width: none; /* Firefox 隐藏滚动条 */
  padding: 6px;
  border-radius: 12px;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-elevated);
  box-shadow: var(--theme-shadow-lg);
}

/* Chrome / Edge / Safari 隐藏滚动条（保留滚动能力） */
.svc-picker-pop::-webkit-scrollbar {
  display: none;
}

.svc-picker-group {
  padding: 8px 8px 4px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1px;
  color: var(--theme-text-3);
}

.svc-picker-opt {
  display: block;
  width: 100%;
  padding: 8px 10px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--theme-text-1);
  font-size: 12.5px;
  font-family: inherit;
  text-align: left;
  cursor: pointer;
  transition: background 0.15s ease;
}

.svc-picker-opt:hover {
  background: var(--theme-bg-hover);
}

.svc-picker-opt.is-active {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

/* ---------- 工具头 ---------- */
.tool-head h3 {
  margin: 2px 0 4px;
  font-size: 15px;
  color: var(--theme-text-1);
}

.tool-head p {
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  color: var(--theme-text-3);
}

/* ---------- 表单 ---------- */
.tool-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px;
  border-radius: 12px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 0;
}

.field label {
  font-size: 11.5px;
  font-weight: 600;
  color: var(--theme-text-2);
}

.field label i {
  color: var(--theme-danger);
  font-style: normal;
  margin-left: 2px;
}

.field input,
.field textarea,
.field select {
  padding: 8px 11px;
  border: 1px solid var(--theme-border);
  border-radius: 9px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-size: 12.5px;
  font-family: inherit;
  outline: none;
  transition: border-color 0.18s ease;
  resize: vertical;
}

.field textarea {
  min-height: 56px;
}

.field input:focus,
.field textarea:focus,
.field select:focus {
  border-color: var(--theme-primary);
}

.field input::placeholder,
.field textarea::placeholder {
  color: var(--theme-text-3);
}

.field select option {
  background: var(--theme-bg-elevated);
  color: var(--theme-text-1);
}

.field-hint {
  margin: 0;
  font-size: 10.5px;
  color: var(--theme-text-3);
  line-height: 1.4;
}

.form-actions {
  display: flex;
  gap: 8px;
}

.run-btn {
  flex: 1;
  padding: 10px 0;
  border: none;
  border-radius: 10px;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.18s ease;
}

.run-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ghost-btn {
  padding: 10px 14px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  background: transparent;
  color: var(--theme-text-2);
  font-size: 12.5px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.ghost-btn:hover:not(:disabled) {
  background: var(--theme-bg-hover);
}

.ghost-btn.small {
  padding: 4px 10px;
  font-size: 10.5px;
  border-radius: 7px;
}

/* ---------- 结果区 ---------- */
.svc-error {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  background: var(--theme-danger-soft);
  border: 1px solid var(--theme-danger);
  color: var(--theme-danger);
  font-size: 12px;
  line-height: 1.5;
}

.result {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.result-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.result-head h4 {
  margin: 0;
  font-size: 12px;
  color: var(--theme-text-3);
  letter-spacing: 1px;
}

.result-meta {
  margin: 0;
  font-size: 11.5px;
  color: var(--theme-text-3);
}

.sub-head {
  margin: 8px 0 6px;
  font-size: 11.5px;
  font-weight: 600;
  letter-spacing: 1px;
  color: var(--theme-text-3);
}

.raw-json {
  margin: 0;
  padding: 12px;
  max-height: 320px;
  overflow: auto;
  border-radius: 10px;
  background: var(--theme-bg-app);
  border: 1px solid var(--theme-border);
  color: var(--theme-primary);
  font-size: 11px;
  line-height: 1.5;
  font-family: 'SF Mono', Menlo, Consolas, monospace;
  white-space: pre-wrap;
  word-break: break-all;
}

.stack {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.poi-card {
  padding: 10px 12px;
  border-radius: 10px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  cursor: pointer;
  transition: background 0.18s ease;
}

.poi-card:hover {
  background: var(--theme-primary-soft);
}

.poi-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-text-1);
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.poi-distance {
  font-style: normal;
  font-size: 10.5px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 999px;
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.poi-rows {
  display: flex;
  flex-wrap: wrap;
  gap: 3px 10px;
  margin-top: 4px;
  font-size: 11.5px;
  color: var(--theme-text-2);
}

.poi-loc {
  margin-top: 5px;
  font-size: 10.5px;
  color: var(--theme-primary);
  font-family: 'SF Mono', Menlo, Consolas, monospace;
}

/* 键值 */
.regeo-hero {
  padding: 12px;
  border-radius: 10px;
  background: var(--theme-primary-soft);
  border: 1px solid var(--theme-primary);
  color: var(--theme-text-1);
  font-size: 13.5px;
  font-weight: 600;
  line-height: 1.5;
}

.kv-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
}

.kv {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 8px 10px;
  border-radius: 9px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  min-width: 0;
}

.kv span {
  font-size: 10.5px;
  color: var(--theme-text-3);
}

.kv b {
  font-size: 12px;
  color: var(--theme-text-1);
  word-break: break-all;
}

/* 坐标转换 */
.coord-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.coord-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 10px;
  border-radius: 9px;
  background: var(--theme-bg-surface);
  cursor: pointer;
  transition: background 0.18s ease;
}

.coord-item:hover {
  background: var(--theme-primary-soft);
}

.coord-idx {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 10px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.coord-val {
  flex: 1;
  font-family: 'SF Mono', Menlo, Consolas, monospace;
  font-size: 11.5px;
  color: var(--theme-text-1);
}

.coord-copy {
  font-size: 10.5px;
  color: var(--theme-primary);
}

/* 输入提示 */
.tip-item {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 9px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  cursor: pointer;
  transition: background 0.18s ease;
}

.tip-item:hover {
  background: var(--theme-primary-soft);
}

.tip-name {
  font-size: 12.5px;
  color: var(--theme-text-1);
  font-weight: 500;
}

.tip-meta {
  flex-shrink: 0;
  font-size: 10.5px;
  color: var(--theme-text-3);
}

/* 行政区划 */
.district-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.district-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 6px 10px;
  border-radius: 7px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.district-item:hover {
  background: var(--theme-primary-soft);
}

.district-label {
  font-size: 12.5px;
  color: var(--theme-text-1);
}

.district-meta {
  flex-shrink: 0;
  font-size: 10.5px;
  color: var(--theme-text-3);
}

/* 天气 */
.weather-card {
  padding: 12px;
  border-radius: 10px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  margin-bottom: 8px;
}

.weather-city {
  font-size: 11.5px;
  color: var(--theme-text-3);
  margin-bottom: 4px;
}

.weather-main {
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-primary);
  margin-bottom: 4px;
}

.weather-meta {
  font-size: 11.5px;
  color: var(--theme-text-2);
  line-height: 1.5;
}

.forecast-block {
  margin-bottom: 6px;
}

/* 路线 */
.route-summary {
  display: flex;
  gap: 8px;
}

.summary-item {
  flex: 1;
  padding: 9px 10px;
  border-radius: 10px;
  background: var(--theme-primary-soft);
  border: 1px solid var(--theme-primary);
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.summary-item span {
  font-size: 10.5px;
  color: var(--theme-text-3);
}

.summary-item b {
  font-size: 13px;
  color: var(--theme-primary);
}

.steps-list {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 7px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.step-item:hover {
  background: var(--theme-bg-hover);
}

.step-index {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 10px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 1px;
}

.step-text {
  flex: 1;
  font-size: 11.5px;
  color: var(--theme-text-2);
  line-height: 1.45;
}

.step-text em {
  font-style: normal;
  color: var(--theme-text-3);
}

.step-distance {
  flex-shrink: 0;
  font-size: 10.5px;
  color: var(--theme-text-3);
}

/* 交通态势 */
.traffic-eval {
  padding: 12px;
  border-radius: 10px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
}

.traffic-desc {
  font-size: 12.5px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin-bottom: 10px;
  line-height: 1.5;
}

.traffic-bars {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.traffic-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bar-label {
  flex-shrink: 0;
  width: 42px;
  font-size: 11px;
  color: var(--theme-text-2);
}

.bar-track {
  flex: 1;
  height: 7px;
  border-radius: 4px;
  background: var(--theme-bg-hover);
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.bar-pct {
  flex-shrink: 0;
  width: 36px;
  text-align: right;
  font-size: 10.5px;
  color: var(--theme-text-3);
}

.road-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.road-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 8px;
  background: var(--theme-bg-surface);
  font-size: 11.5px;
}

.road-status {
  flex-shrink: 0;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 10px;
  font-weight: 600;
  background: var(--theme-bg-hover);
  color: var(--theme-text-2);
}

.road-status.st-1 { background: var(--theme-success-soft); color: var(--theme-success); }
.road-status.st-2 { background: var(--theme-warning-soft); color: var(--theme-warning); }
.road-status.st-3 { background: var(--theme-danger-soft); color: var(--theme-danger); }
.road-status.st-4 { background: var(--theme-danger-soft); color: var(--theme-danger); }

.road-name {
  flex: 1;
  color: var(--theme-text-1);
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.road-speed {
  flex-shrink: 0;
  font-size: 10.5px;
  color: var(--theme-text-3);
}

/* 静态地图 */
.staticmap-box {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px;
  border-radius: 10px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
}

.staticmap-box img {
  display: block;
  width: 100%;
  border-radius: 8px;
  background: var(--theme-bg-elevated);
}

.staticmap-link {
  font-size: 11.5px;
  color: var(--theme-primary);
  text-decoration: none;
}

.staticmap-link:hover {
  text-decoration: underline;
}

/* Toast（Marker 标签在地图层，需全局样式，见下方非 scoped 块） */
.svc-toast {
  position: fixed;
  bottom: 36px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2100;
  padding: 10px 20px;
  border-radius: 999px;
  background: var(--theme-text-1);
  border: 1px solid var(--theme-border);
  color: var(--theme-bg-app);
  font-size: 13px;
  box-shadow: var(--theme-shadow-lg);
  max-width: 80vw;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ---------- 结果 / 收藏 / 列表样式统一由全局主题令牌驱动 ---------- */
</style>

<style>
/* AMap Marker label 渲染在地图容器内（脱离 Vue 作用域），需全局样式 */
.svc-pin-label {
  padding: 3px 9px;
  border-radius: 8px;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  border: none;
  box-shadow: var(--theme-shadow-md);
}
</style>
