<template>
  <Teleport to="body">
    <div ref="overlayRef" v-show="visible" class="nav-overlay" :class="mapTheme === 'dark' ? 'is-dark' : 'is-light'">
      <!-- 全屏底图 -->
      <div ref="mapElRef" class="nav-map"></div>

      <div v-if="!amapReady && !amapError" class="map-loading">
        <div class="loading-spinner"></div>
        <p>正在加载高德地图服务...</p>
      </div>
      <div v-else-if="amapError" class="map-error">⚠️ {{ amapError }}</div>

      <!-- 左上主卡片：搜索 / 路线 / 结果 / 收藏 / 工具箱 -->
      <aside class="side-card">
        <!-- ====== 搜索态（对应真实高德首页卡片） ====== -->
        <template v-if="panel === 'search'">
          <div class="search-box">
            <input
              v-model="searchText"
              class="search-input"
              type="text"
              placeholder="搜索地点、公交、地铁"
              autocomplete="off"
              @keydown.enter="runTextSearch"
              @focus="onInputFocus('search')"
              @blur="hideTips"
              @input="onInputInput('search', searchText)"
            />
            <button class="sb-btn" title="搜索" @click="runTextSearch">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.search"></svg>
            </button>
            <button class="sb-btn sb-route" title="路线规划" @click="openRoutePanel()">
              <svg class="ic" viewBox="0 0 24 24" fill="currentColor" stroke="none" v-html="I.routeArrow"></svg>
            </button>
          </div>
          <div
            v-if="tipsVisible && tipsField === 'search' && tips.length"
            class="tips-pop"
          >
            <button
              v-for="(t, i) in tips"
              :key="i"
              class="tip-row"
              type="button"
              @mousedown.prevent="pickTip(t)"
            >
              <svg class="ic tip-pin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.pin"></svg>
              <span class="tip-name">{{ t.name }}</span>
              <span class="tip-district">{{ t.district }}</span>
            </button>
          </div>

          <div class="cat-grid">
            <button
              v-for="c in CATEGORIES"
              :key="c.label"
              class="cat"
              type="button"
              @click="onCategory(c)"
            >
              <span class="cat-icon" :style="{ background: c.color }">
                <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="c.icon"></svg>
              </span>
              <span class="cat-name">{{ c.label }}</span>
            </button>
          </div>

          <div class="quick-row">
            <button class="quick" type="button" @click="goPlace('home')">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.home"></svg>
              <span>回家</span>
            </button>
            <i class="quick-sep"></i>
            <button class="quick" type="button" @click="goPlace('company')">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.briefcase"></svg>
              <span>去单位</span>
            </button>
            <i class="quick-sep"></i>
            <button class="quick" type="button" @click="panel = 'favorites'">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.star"></svg>
              <span>收藏夹<em v-if="favorites.length">（{{ favorites.length }}）</em></span>
            </button>
          </div>
        </template>

        <!-- ====== 路线态（对应真实高德路线面板） ====== -->
        <template v-else-if="panel === 'route'">
          <div class="route-head">
            <div class="mode-tabs">
              <button
                v-for="m in MODES"
                :key="m.key"
                class="mode-tab"
                :class="{ 'is-active': mode === m.key }"
                :title="m.label"
                type="button"
                @click="switchMode(m.key)"
              >
                <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="m.icon"></svg>
              </button>
            </div>
            <button class="head-close" title="收起路线面板" type="button" @click="backToSearch">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.x"></svg>
            </button>
          </div>

          <div class="route-card">
            <div class="route-lines">
              <div class="route-line">
                <i class="dot dot-start"></i>
                <input
                  ref="startInputRef"
                  v-model="startText"
                  type="text"
                  placeholder="请输入起点"
                  autocomplete="off"
                  @focus="onInputFocus('start')"
                  @blur="hideTips"
                  @input="onInputInput('start', startText)"
                />
              </div>
              <div v-for="(v, i) in vias" :key="'via-' + i" class="route-line">
                <i class="dot dot-via"></i>
                <input
                  v-model="v.text"
                  type="text"
                  placeholder="请输入途经点"
                  autocomplete="off"
                  @focus="onInputFocus('via-' + i)"
                  @blur="hideTips"
                  @input="onInputInput('via-' + i, v.text)"
                />
                <button class="line-act" title="删除途经点" type="button" @click="removeVia(i)">
                  <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.trash"></svg>
                </button>
              </div>
              <div class="route-line">
                <i class="dot dot-end"></i>
                <input
                  ref="endInputRef"
                  v-model="endText"
                  type="text"
                  placeholder="请输入终点"
                  autocomplete="off"
                  @focus="onInputFocus('end')"
                  @blur="hideTips"
                  @input="onInputInput('end', endText)"
                />
              </div>
            </div>
            <div class="route-side">
              <button
                v-if="mode === 'driving' && vias.length < VIA_LIMIT"
                class="side-act"
                title="添加途经点"
                type="button"
                @click="addVia"
              >
                <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.plusCircle"></svg>
              </button>
              <button class="side-act" title="交换起终点" type="button" @click="swapPoints">
                <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.swap"></svg>
              </button>
            </div>
          </div>

          <!-- 起终点联想下拉 -->
          <div
            v-if="tipsVisible && (tipsField === 'start' || tipsField === 'end' || tipsField.startsWith('via-')) && tips.length"
            class="tips-pop"
          >
            <button
              v-for="(t, i) in tips"
              :key="i"
              class="tip-row"
              type="button"
              @mousedown.prevent="pickTip(t)"
            >
              <svg class="ic tip-pin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.pin"></svg>
              <span class="tip-name">{{ t.name }}</span>
              <span class="tip-district">{{ t.district }}</span>
            </button>
          </div>

          <div v-if="policyOptions.length" class="policy-bar">
            <div class="policy-picker" :class="{ 'is-open': policyOpen }">
              <button
                type="button"
                class="policy-select"
                aria-label="路线策略"
                :aria-expanded="policyOpen"
                @click="policyOpen = !policyOpen"
              >
                <span class="policy-label">策略 · {{ policyLabel }}</span>
                <svg class="ic policy-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.chevronDown"></svg>
              </button>
              <div v-if="policyOpen" class="policy-pop">
                <button
                  v-for="p in policyOptions"
                  :key="p.v"
                  type="button"
                  class="policy-opt"
                  :class="{ 'is-active': p.v === policy }"
                  @click="pickPolicy(p.v)"
                >{{ p.n }}</button>
              </div>
            </div>
          </div>

          <button class="plan-btn" :disabled="loading || !amapReady" type="button" @click="planRoute">
            {{ loading ? '路线规划中…' : '路线规划' }}
          </button>

          <p v-if="pendingHome" class="set-hint">规划成功后将记住该地点为「{{ pendingHome === 'home' ? '家' : '单位' }}」</p>

          <p v-if="amapError" class="nav-error">{{ amapError }}</p>
          <p v-else-if="routeError" class="nav-error">{{ routeError }}</p>

          <!-- 路线结果 -->
          <div v-if="summary" class="route-result">
            <div class="rr-main">
              <span class="rr-time">{{ summary.duration }}</span>
              <span class="rr-dist">{{ summary.km }}</span>
              <span class="rr-mode">{{ modeLabel }}</span>
            </div>
            <label class="globe-sync">
              <input v-model="showOnGlobe" type="checkbox" @change="syncGlobeRoute" />
              同步显示在 3D 地球
            </label>
          </div>

          <div v-if="steps.length" class="steps">
            <div class="steps-header">导航引导（{{ steps.length }} 步）</div>
            <div
              v-for="(s, i) in steps"
              :key="i"
              class="step-item"
              @click="locateStep(s)"
            >
              <span class="step-index">{{ i + 1 }}</span>
              <span class="step-text">
                {{ s.instruction }}
                <em v-if="s.road">（{{ s.road }}）</em>
              </span>
              <span v-if="s.distanceText" class="step-distance">{{ s.distanceText }}</span>
            </div>
          </div>

          <!-- 历史记录 -->
          <div v-else-if="histories.length && !loading" class="history">
            <div class="his-title">历史记录</div>
            <button
              v-for="(h, i) in histories.slice(0, 9)"
              :key="i"
              class="his-item"
              type="button"
              @click="replayHistory(h)"
            >
              <svg class="ic his-clock" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.clock"></svg>
              <span class="his-text">{{ h.s }} → {{ h.e }}</span>
            </button>
          </div>
        </template>

        <!-- ====== 搜索/分类结果态 ====== -->
        <template v-else-if="panel === 'results'">
          <header class="panel-head">
            <button class="head-back" title="返回" type="button" @click="backToSearch">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.chevronLeft"></svg>
            </button>
            <span class="panel-title">{{ resultsTitle }}</span>
            <button class="head-close" title="收起" type="button" @click="backToSearch">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.x"></svg>
            </button>
          </header>
          <p v-if="poiLoading" class="state-tip">搜索中…</p>
          <p v-else-if="poiError" class="nav-error">{{ poiError }}</p>
          <div v-else class="poi-list">
            <div
              v-for="(p, i) in poiResults"
              :key="i"
              class="poi-row"
              @click="focusPoi(p)"
            >
              <div class="poi-main">
                <div class="poi-name">
                  {{ p.name }}
                  <em v-if="p.distance">· {{ fmtDist(p.distance) }}</em>
                </div>
                <div class="poi-addr">{{ p.area }} {{ p.address }}</div>
              </div>
              <button
                class="poi-act"
                :class="{ 'is-fav': isFav(p) }"
                title="收藏"
                type="button"
                @click.stop="toggleFav(p)"
              >
                <svg class="ic" viewBox="0 0 24 24" :fill="isFav(p) ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.star"></svg>
              </button>
              <button class="poi-go" type="button" @click.stop="routeTo(p)">到这去</button>
            </div>
          </div>
        </template>

        <!-- ====== 收藏夹 ====== -->
        <template v-else-if="panel === 'favorites'">
          <header class="panel-head">
            <button class="head-back" title="返回" type="button" @click="backToSearch">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.chevronLeft"></svg>
            </button>
            <span class="panel-title">收藏夹</span>
            <button class="head-close" title="收起" type="button" @click="backToSearch">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.x"></svg>
            </button>
          </header>
          <p v-if="!favorites.length" class="state-tip">暂无收藏，在搜索结果中点击 ☆ 收藏地点</p>
          <div v-else class="poi-list">
            <div
              v-for="(f, i) in favorites"
              :key="i"
              class="poi-row"
              @click="focusFav(f)"
            >
              <div class="poi-main">
                <div class="poi-name">{{ f.name }}</div>
                <div class="poi-addr">{{ f.address }}</div>
              </div>
              <button class="poi-act is-fav" title="取消收藏" type="button" @click.stop="removeFav(f)">
                <svg class="ic" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.star"></svg>
              </button>
              <button class="poi-go" type="button" @click.stop="favToRoute(f)">到这去</button>
            </div>
          </div>
        </template>

        <!-- ====== 服务工具箱 ====== -->
        <template v-else>
          <header class="panel-head">
            <button class="head-back" title="返回" type="button" @click="panel = 'search'">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.chevronLeft"></svg>
            </button>
            <span class="panel-title">服务工具箱</span>
            <button class="head-close" title="收起" type="button" @click="panel = 'search'">
              <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.x"></svg>
            </button>
          </header>
          <ServicePanel :amap="AMapRef" :map="mapRef" />
        </template>
      </aside>

      <!-- 天气徽章（IP 定位城市实况 + 当日温度范围） -->
      <div v-if="weather" class="weather-chip" @click.stop="weatherOpen = !weatherOpen">
        <span class="weather-city">{{ weather.city }}</span>
        <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.chevronDown"></svg>
        <span class="weather-split"></span>
        <span class="weather-main">{{ weather.weather }} {{ weather.tempRange || (weather.temperature != null ? weather.temperature + '℃' : '') }}</span>
        <div v-if="weatherOpen" class="weather-pop" @click.stop>
          <div class="wp-row"><span>天气</span><b>{{ weather.weather }}</b></div>
          <div class="wp-row"><span>实况气温</span><b>{{ weather.temperature != null ? weather.temperature + '℃' : '—' }}</b></div>
          <div class="wp-row"><span>今日温度</span><b>{{ weather.tempRange || '—' }}</b></div>
          <div class="wp-row"><span>风向</span><b>{{ weather.winddirection ? weather.winddirection + '风 ' + weather.windpower + '级' : '—' }}</b></div>
          <div class="wp-row"><span>湿度</span><b>{{ weather.humidity ? weather.humidity + '%' : '—' }}</b></div>
          <div class="wp-row"><span>更新</span><b>{{ weather.reporttime }}</b></div>
        </div>
      </div>

      <!-- 右上工具条 -->
      <div class="top-bar">
        <button class="tool-chip" :class="{ 'is-on': panel === 'tools' }" type="button" @click="panel = panel === 'tools' ? 'search' : 'tools'">
          <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.wrench"></svg>
          <span class="chip-text">工具箱</span>
        </button>
        <button class="tool-chip" :class="{ 'is-on': trafficOn }" type="button" @click="toggleTraffic">
          <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.activity"></svg>
          <span class="chip-text">路况</span>
        </button>
        <button class="tool-chip" :class="{ 'is-on': rangingOn }" type="button" @click="toggleRanging">
          <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.ruler"></svg>
          <span class="chip-text">测距</span>
        </button>
        <button class="tool-chip" type="button" @click="toggleMapTheme" :title="mapTheme === 'dark' ? '切换到白天模式' : '切换到夜间模式'">
          <svg v-if="mapTheme === 'dark'" class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.sun"></svg>
          <svg v-else class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.moon"></svg>
          <span class="chip-text">{{ mapTheme === 'dark' ? '白天' : '夜间' }}</span>
        </button>
        <button class="tool-chip tool-close" title="返回 3D 地球" type="button" @click="$emit('close')">
          <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.x"></svg>
        </button>
      </div>

      <!-- 右下地图控件 -->
      <div class="ctl-stack">
        <button class="ctl-btn" type="button" :title="pitchOn ? '切换 2D' : '切换 3D'" @click="togglePitch">
          <span class="ctl-text">{{ pitchOn ? '2D' : '3D' }}</span>
        </button>
        <button class="ctl-btn" title="定位到我的位置" type="button" @click="locateMe">
          <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.crosshair"></svg>
        </button>
        <button class="ctl-btn" title="全屏" type="button" @click="toggleFullscreen">
          <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.maximize"></svg>
        </button>
        <button class="ctl-btn" title="放大" type="button" @click="map?.zoomIn()">
          <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.plus"></svg>
        </button>
        <button class="ctl-btn ctl-last" title="缩小" type="button" @click="map?.zoomOut()">
          <svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" v-html="I.minus"></svg>
        </button>
      </div>

      <div class="map-attribution">© 高德地图 · GS(2024)0650号 · 本服务由高德开放平台提供</div>

      <div v-if="toast" class="nav-toast">{{ toast }}</div>
    </div>
  </Teleport>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, shallowRef, watch } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { AMAP_KEY, AMAP_VERSION, AMAP_PLUGINS } from '../config.js'
import { amapRest } from '../services/amapRest.js'
import ServicePanel from './ServicePanel.vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  /** 打开时默认面板：nav=路线导航 / services=服务工具箱 */
  startTab: { type: String, default: 'nav' }
})

const emit = defineEmits(['close', 'route-change'])

/* ================= 图标（lucide 风格 path） ================= */
const I = {
  search: '<circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/>',
  routeArrow: '<polygon points="3 11 22 2 13 21 11 13 3 11"/>',
  car: '<path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"/><circle cx="7" cy="17" r="2"/><path d="M9 17h6"/><circle cx="17" cy="17" r="2"/>',
  bus: '<path d="M8 6v6"/><path d="M15 6v6"/><path d="M2 12h19.6"/><path d="M18 18h3s.5-1.7.8-2.8c.1-.4.2-.8.2-1.2 0-.4-.1-.8-.2-1.2l-1.4-5C20.6 6.8 19.7 6 18.6 6H4a2 2 0 0 0-2 2v10h3"/><circle cx="7" cy="18" r="2"/><path d="M9 18h5"/><circle cx="16" cy="18" r="2"/>',
  walk: '<circle cx="12" cy="5" r="1"/><path d="m9 20 3-6 3 6"/><path d="m6 8 6 2 6-2"/><path d="M12 10v4"/>',
  bike: '<circle cx="18.5" cy="17.5" r="3.5"/><circle cx="5.5" cy="17.5" r="3.5"/><circle cx="15" cy="5" r="1"/><path d="M12 17.5V14l-3-3 4-3 2 3h2"/>',
  building: '<rect width="16" height="20" x="4" y="2" rx="2"/><path d="M9 22v-4h6v4"/><path d="M8 6h.01"/><path d="M16 6h.01"/><path d="M12 6h.01"/><path d="M12 10h.01"/><path d="M12 14h.01"/><path d="M16 10h.01"/><path d="M16 14h.01"/><path d="M8 10h.01"/><path d="M8 14h.01"/>',
  utensils: '<path d="M3 2v7c0 1.1.9 2 2 2h4a2 2 0 0 0 2-2V2"/><path d="M7 2v20"/><path d="M21 15V2a5 5 0 0 0-5 5v6c0 1.1.9 2 2 2h3Zm0 0v7"/>',
  bag: '<path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/><path d="M3 6h18"/><path d="M16 10a4 4 0 0 1-8 0"/>',
  fuel: '<line x1="3" x2="15" y1="22" y2="22"/><line x1="4" x2="14" y1="9" y2="9"/><path d="M14 22V4a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v18"/><path d="M14 13h2a2 2 0 0 1 2 2v2a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-5l-3-6"/>',
  home: '<path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>',
  landmark: '<line x1="3" x2="21" y1="22" y2="22"/><line x1="6" x2="6" y1="18" y2="11"/><line x1="10" x2="10" y1="18" y2="11"/><line x1="14" x2="14" y1="18" y2="11"/><line x1="18" x2="18" y1="18" y2="11"/><polygon points="12 2 20 7 4 7"/>',
  martini: '<path d="M8 22h8"/><path d="M12 11v11"/><path d="m19 3-7 8-7-8Z"/>',
  briefcase: '<path d="M16 20V4a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/><rect width="20" height="14" x="2" y="6" rx="2"/>',
  star: '<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>',
  clock: '<circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>',
  x: '<path d="M18 6 6 18"/><path d="m6 6 12 12"/>',
  plusCircle: '<circle cx="12" cy="12" r="10"/><path d="M8 12h8"/><path d="M12 8v8"/>',
  swap: '<path d="m21 8-4-4-4 4"/><path d="M17 4v16"/><path d="m3 16 4 4 4-4"/><path d="M7 20V4"/>',
  trash: '<path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>',
  chevronLeft: '<path d="m15 18-6-6 6-6"/>',
  chevronDown: '<path d="m6 9 6 6 6-6"/>',
  crosshair: '<circle cx="12" cy="12" r="10"/><line x1="22" x2="18" y1="12" y2="12"/><line x1="6" x2="2" y1="12" y2="12"/><line x1="12" x2="12" y1="6" y2="2"/><line x1="12" x2="12" y1="22" y2="18"/>',
  maximize: '<path d="M8 3H5a2 2 0 0 0-2 2v3"/><path d="M21 8V5a2 2 0 0 0-2-2h-3"/><path d="M3 16v3a2 2 0 0 0 2 2h3"/><path d="M16 21h3a2 2 0 0 0 2-2v-3"/>',
  moon: '<path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/>',
  sun: '<circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/>',
  wrench: '<path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/>',
  activity: '<path d="M22 12h-2.48a2 2 0 0 0-1.93 1.46l-2.35 8.36a.25.25 0 0 1-.48 0L9.24 2.18a.25.25 0 0 0-.48 0l-2.35 8.36A2 2 0 0 1 4.49 12H2"/>',
  ruler: '<path d="M21.3 15.3a2.4 2.4 0 0 1 0 3.4l-2.6 2.6a2.4 2.4 0 0 1-3.4 0L2.3 8.7a2.41 2.41 0 0 1 0-3.4l2.6-2.6a2.41 2.41 0 0 1 3.4 0Z"/><path d="m14.5 12.5 2-2"/><path d="m11.5 9.5 2-2"/><path d="m8.5 6.5 2-2"/><path d="m17.5 15.5 2-2"/>',
  plus: '<path d="M5 12h14"/><path d="M12 5v14"/>',
  minus: '<path d="M5 12h14"/>',
  pin: '<path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/>'
}

/* ================= 静态配置 ================= */
const MODES = [
  { key: 'driving', label: '驾车', icon: I.car },
  { key: 'transit', label: '公交地铁', icon: I.bus },
  { key: 'walking', label: '步行', icon: I.walk },
  { key: 'riding', label: '骑行', icon: I.bike }
]

const CATEGORIES = [
  { label: '驾车', type: 'route', mode: 'driving', color: '#1677ff', icon: I.car },
  { label: '公共交通', type: 'route', mode: 'transit', color: '#00b578', icon: I.bus },
  { label: '酒店', type: 'poi', keyword: '酒店', color: '#7b5cf5', icon: I.building },
  { label: '步行', type: 'route', mode: 'walking', color: '#12aaec', icon: I.walk },
  { label: '美食', type: 'poi', keyword: '美食', color: '#ff8f1f', icon: I.utensils },
  { label: '商场', type: 'poi', keyword: '商场', color: '#9a5cf5', icon: I.bag },
  { label: '加油', type: 'poi', keyword: '加油站', color: '#f5484d', icon: I.fuel },
  { label: '小区', type: 'poi', keyword: '小区', color: '#2f86fe', icon: I.home },
  { label: '景点', type: 'poi', keyword: '景点', color: '#00b578', icon: I.landmark },
  { label: '休闲娱乐', type: 'poi', keyword: '休闲娱乐', color: '#f06595', icon: I.martini }
]

const DRIVING_POLICIES = [
  { v: 'LEAST_TIME', n: '速度最快' },
  { v: 'LEAST_FEE', n: '费用最低' },
  { v: 'LEAST_DISTANCE', n: '距离最短' }
]
const TRANSIT_POLICIES = [
  { v: 'LEAST_TIME', n: '最快' },
  { v: 'LEAST_FEE', n: '最经济' },
  { v: 'LEAST_TRANSFER', n: '最少换乘' },
  { v: 'LEAST_WALK', n: '最少步行' }
]
/** 各出行方式可用的路线策略（步行/骑行无策略选项） */
const MODE_POLICIES = {
  driving: DRIVING_POLICIES,
  transit: TRANSIT_POLICIES
}

const VIA_LIMIT = 5

/* ================= 状态 ================= */
const overlayRef = ref(null)
const mapElRef = ref(null)
const visible = ref(props.visible)
const amapReady = ref(false)
const amapError = ref('')
const routeError = ref('')
const loading = ref(false)

/** 左侧卡片面板：search=搜索 / route=路线 / results=搜索结果 / favorites=收藏夹 / tools=服务工具箱 */
const panel = ref('search')

const searchText = ref('')
const startText = ref('')
const endText = ref('')
const vias = ref([]) // [{ text, poi }]
const mode = ref('driving')
const policy = ref('LEAST_TIME')
const summary = ref(null)
const steps = ref([])
const showOnGlobe = ref(true)

const startInputRef = ref(null)
const endInputRef = ref(null)

/** 地图主题（dark / light），底图样式与悬浮面板主题同步切换 */
const mapTheme = ref(localStorage.getItem('amap-nav-theme') || 'light')

/** 右上工具与右下控件状态 */
const trafficOn = ref(false)
const rangingOn = ref(false)
const pitchOn = ref(true)
const weatherOpen = ref(false)
const weather = ref(null)
const ipCity = ref('')
const fullscreenOn = ref(false)
const toast = ref('')

/** 历史记录 / 收藏 / 家·单位（localStorage 持久化） */
const histories = ref(JSON.parse(localStorage.getItem('amap-route-history') || '[]'))
const favorites = ref(JSON.parse(localStorage.getItem('amap-fav-places') || '[]'))
const homePlace = ref(JSON.parse(localStorage.getItem('amap-home-place') || 'null'))
const companyPlace = ref(JSON.parse(localStorage.getItem('amap-company-place') || 'null'))
const pendingHome = ref('') // home / company：规划成功后把终点记为该地址

/** 搜索结果 */
const poiResults = ref([])
const poiLoading = ref(false)
const poiError = ref('')
const resultsTitle = ref('')

/** 暴露给工具箱的 AMap 构造器与地图实例 */
const AMapRef = shallowRef(null)
const mapRef = shallowRef(null)

let AMap = null
let map = null
let geocoder = null
let lastService = null
let stepMarker = null
let poiMarkers = []
let startPoi = null
let endPoi = null
let lastRoutePoints = null
let trafficLayer = null
let rangingTool = null
let toastTimer = null
let mapCenter = [120.15, 30.27]

/* ================= 输入联想（Web服务 inputtips，自定义下拉） ================= */
const tips = ref([])
const tipsField = ref('')
const tipsVisible = ref(false)
let tipsTimer = null

function onInputFocus(field) {
  tipsField.value = field
}

function onInputInput(field, text) {
  clearTimeout(tipsTimer)
  const kw = String(text || '').trim()
  if (!kw) {
    hideTips()
    return
  }
  tipsTimer = setTimeout(async () => {
    try {
      const list = await amapRest.inputtips({ keywords: kw, city: ipCity.value || undefined })
      tips.value = (list || []).filter(t => t.name).slice(0, 10)
      tipsField.value = field
      tipsVisible.value = tips.value.length > 0
    } catch {
      hideTips()
    }
  }, 250)
}

function hideTips() {
  tipsVisible.value = false
}

/** 选择联想项：无坐标时用地理编码兜底 */
async function pickTip(t) {
  const field = tipsField.value
  hideTips()
  // inputtips 的 location 是 "lng,lat" 字符串（兼容数组形式），逐位下标会解析成错误坐标
  const loc = Array.isArray(t.location) ? t.location : String(t.location || '').split(',')
  const lnglat = loc.length === 2 && !Number.isNaN(Number(loc[0])) && !Number.isNaN(Number(loc[1]))
    ? { lng: Number(loc[0]), lat: Number(loc[1]) }
    : null
  if (!lnglat) {
    try {
      const d = await amapRest.geocode({ address: (t.district || '') + t.name })
      const g = d.geocodes?.[0]
      if (g?.location) lnglat = { lng: Number(g.location[0]), lat: Number(g.location[1]) }
    } catch { /* 忽略，保持无坐标 */ }
  }
  const poi = lnglat
    ? { lnglat, name: t.name, district: t.district || '', adcode: t.adcode || '' }
    : null

  if (field === 'search') {
    searchText.value = t.name
    if (lnglat && map) {
      map.panTo([lnglat.lng, lnglat.lat])
      map.setZoom(Math.max(map.getZoom(), 15))
      clearPoiMarkers()
      addPoiMarker({ location: lnglat, name: t.name }, true)
    } else if (!lnglat) {
      showToast('未找到该地点的坐标')
    }
    return
  }

  if (field === 'start') {
    startText.value = t.name
    startPoi = poi
    if (!poi) routeError.value = `未找到起点「${t.name}」的坐标`
    else maybeAutoPlan()
  } else if (field === 'end') {
    endText.value = t.name
    endPoi = poi
    if (!poi) routeError.value = `未找到终点「${t.name}」的坐标`
    else maybeAutoPlan()
  } else if (field.startsWith('via-')) {
    const i = Number(field.slice(4))
    if (vias.value[i]) {
      vias.value[i].text = t.name
      vias.value[i].poi = poi
    }
  }
}

/** 起终点均已选定坐标时自动规划（贴近真实高德行为） */
function maybeAutoPlan() {
  if (startPoi && endPoi) planRoute()
}

/* ================= 面板切换 ================= */

function openRoutePanel() {
  panel.value = 'route'
  // 起点自动获取用户当前位置
  if (!startPoi && !startText.value) fillStartFromMyLocation()
}

function backToSearch() {
  panel.value = 'search'
  hideTips()
  clearRoute()
}

watch(panel, () => {
  hideTips()
  policyOpen.value = false
})

/* ---------- 策略选择器（自定义下拉） ---------- */
const policyOpen = ref(false)
const policyOptions = computed(() => MODE_POLICIES[mode.value] || [])
const policyLabel = computed(() => policyOptions.value.find(p => p.v === policy.value)?.n || '')
const modeLabel = computed(() => MODES.find(m => m.key === mode.value)?.label || '')

watch(mode, () => {
  const opts = policyOptions.value
  policy.value = opts.length ? opts[0].v : ''
})

function pickPolicy(v) {
  policy.value = v
  policyOpen.value = false
}

function onDocMouseDown(e) {
  if (!e.target.closest?.('.policy-picker')) policyOpen.value = false
  if (!e.target.closest?.('.weather-chip')) weatherOpen.value = false
}

function onDocKeydown(e) {
  if (e.key === 'Escape') {
    policyOpen.value = false
    weatherOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('mousedown', onDocMouseDown)
  document.addEventListener('keydown', onDocKeydown)
  document.addEventListener('fullscreenchange', onFullscreenChange)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', onDocMouseDown)
  document.removeEventListener('keydown', onDocKeydown)
  document.removeEventListener('fullscreenchange', onFullscreenChange)
  clearTimeout(tipsTimer)
  clearTimeout(toastTimer)
  try {
    clearPoiMarkers()
    map?.destroy?.()
  } catch { /* 忽略 */ }
  mapRef.value = null
})

/* ---------- 打开面板（由父组件触发） ---------- */
watch(() => props.visible, v => {
  visible.value = v
  if (v) {
    panel.value = props.startTab === 'services' ? 'tools' : 'route'
    ensureAMap()
  }
})

/* ================= 高德初始化（懒加载，仅首次打开时执行） ================= */

async function ensureAMap() {
  if (amapReady.value || amapError.value) {
    await nextTick()
    map?.resize?.()
    return
  }
  try {
    // IP 定位：用于初始地图中心 / 联想城市限定 / 天气
    let ipInfo = null
    try { ipInfo = await amapRest.ip() } catch { /* 失败则使用默认中心 */ }
    if (ipInfo) {
      ipCity.value = Array.isArray(ipInfo.city) ? (Array.isArray(ipInfo.province) ? '' : ipInfo.province || '') : (ipInfo.city || '')
      if (ipInfo.rectangle) {
        try {
          const [a, b] = ipInfo.rectangle.split('|').map(s => s.split(',').map(Number))
          if (a?.length === 2 && b?.length === 2) mapCenter = [(a[0] + b[0]) / 2, (a[1] + b[1]) / 2]
        } catch { /* 忽略 */ }
      }
    }

    AMap = await AMapLoader.load({ key: AMAP_KEY, version: AMAP_VERSION, plugins: AMAP_PLUGINS })
    AMapRef.value = AMap
    await nextTick()
    map = new AMap.Map(mapElRef.value, {
      zoom: 12,
      center: mapCenter,
      mapStyle: mapTheme.value === 'dark' ? 'amap://styles/dark' : 'amap://styles/normal',
      viewMode: '3D',
      pitch: 45
    })
    mapRef.value = map
    map.addControl(new AMap.Scale())
    geocoder = new AMap.Geocoder()
    amapReady.value = true
    loadWeather(ipInfo)
    // 首次打开路线面板：起点自动回填用户当前位置，地图居中到该位置
    if (panel.value === 'route' && !startPoi && !startText.value) fillStartFromMyLocation()
  } catch (e) {
    amapError.value = '高德服务加载失败：请检查网络连接，或确认 Key 已配置且域名白名单允许当前地址'
    console.error('AMap load failed:', e)
  }
}

/* ---------- 天气徽章 ---------- */

async function loadWeather(ipInfo) {
  try {
    // ip() 无法解析出口 IP 时返回空数组（代理/内网环境常见），需按空值处理
    let adcode = Array.isArray(ipInfo?.adcode) ? '' : (ipInfo?.adcode || '')
    if (!ipInfo) {
      try {
        const info = await amapRest.ip()
        if (!Array.isArray(info?.adcode) && info?.adcode) adcode = info.adcode
      } catch { /* 忽略 */ }
    }
    // IP 定位仍无 adcode：优先静默使用已授权的浏览器定位（不触发权限弹窗），否则用地图中心
    if (!adcode) {
      let loc = null
      try {
        const perm = await navigator.permissions?.query?.({ name: 'geolocation' })
        if (perm?.state === 'granted') {
          loc = await new Promise((resolve, reject) => {
            navigator.geolocation.getCurrentPosition(
              p => resolve([p.coords.longitude, p.coords.latitude]),
              reject,
              { timeout: 5000 }
            )
          })
        }
      } catch { /* 定位不可用则忽略 */ }
      try {
        const r = await amapRest.regeo({ location: (loc || mapCenter).join(',') })
        adcode = r?.addressComponent?.adcode || ''
      } catch { /* 忽略 */ }
    }
    if (!adcode) return
    // base=实况（当前天气/气温/风/湿度），all=当日预报（最低/最高温）
    const [base, all] = await Promise.all([
      amapRest.weather({ city: adcode, extensions: 'base' }).catch(() => null),
      amapRest.weather({ city: adcode, extensions: 'all' }).catch(() => null)
    ])
    const live = base?.lives?.[0] || null
    const cast = all?.forecasts?.[0]?.casts?.[0] || null
    if (!live && !cast) return
    weather.value = {
      city: live?.city || all?.forecasts?.[0]?.city || '',
      weather: live?.weather || cast?.dayweather || '',
      temperature: live ? live.temperature : null,
      tempRange: cast ? `${cast.nighttemp}/${cast.daytemp}°C` : '',
      winddirection: live?.winddirection || '',
      windpower: live?.windpower || '',
      humidity: live?.humidity || '',
      reporttime: live?.reporttime || all?.forecasts?.[0]?.reporttime || ''
    }
  } catch { /* 天气失败不影响主流程 */ }
}

/* ================= 路线规划 ================= */

function switchMode(m) {
  if (mode.value === m) return
  mode.value = m
  clearRoute()
}

function addVia() {
  if (vias.value.length < VIA_LIMIT) vias.value.push({ text: '', poi: null })
}

function removeVia(i) {
  vias.value.splice(i, 1)
}

function swapPoints() {
  ;[startText.value, endText.value] = [endText.value, startText.value]
  ;[startPoi, endPoi] = [endPoi, startPoi]
}

async function planRoute() {
  if (loading.value || !amapReady.value) return
  routeError.value = ''
  loading.value = true
  try {
    const origin = await resolvePoint(startText.value, startPoi, '起点')
    const dest = await resolvePoint(endText.value, endPoi, '终点')
    let viaPois = []
    if (mode.value === 'driving') {
      for (let i = 0; i < vias.value.length; i++) {
        const v = vias.value[i]
        viaPois.push(await resolvePoint(v.text, v.poi, `途经点${i + 1}`))
      }
    }
    const cityCode = origin.adcode ? origin.adcode.slice(0, 4) + '00' : undefined
    if (mode.value === 'transit') {
      await doTransit(origin, dest, cityCode)
    } else if (mode.value === 'walking') {
      await doWalking(origin, dest)
    } else if (mode.value === 'riding') {
      await doRiding(origin, dest)
    } else {
      await doDriving(origin, dest, viaPois)
    }
  } catch (e) {
    routeError.value = e?.message || '路线规划失败，请重试'
  } finally {
    loading.value = false
  }
}

function resolvePoint(text, poi, label) {
  if (poi && poi.lnglat) return Promise.resolve(poi)
  if (!text) return Promise.reject(new Error(`请先选择${label}`))
  return new Promise((resolve, reject) => {
    geocoder.getLocation(text, (status, result) => {
      if (status === 'complete' && result.geocodes?.length) {
        const g = result.geocodes[0]
        resolve({
          lnglat: g.location,
          name: g.formattedAddress || text,
          district: g.district || '',
          adcode: g.adcode || ''
        })
      } else {
        reject(new Error(`未找到${label}「${text}」，请从联想列表中选择`))
      }
    })
  })
}

/** AMap 服务 search 仅接受 LngLat / [lng,lat] / 关键字，普通 {lng,lat} 对象会报 NO_PARAMS */
function asLngLat(ll) {
  const lng = ll?.lng ?? ll?.[0]
  const lat = ll?.lat ?? ll?.[1]
  return lng != null && lat != null ? [Number(lng), Number(lat)] : null
}

function freshService(ctor) {
  if (lastService && lastService.clear) lastService.clear()
  return ctor
}

function doDriving(origin, dest, viaPois = []) {
  const Cls = freshService(AMap.Driving)
  const policyMap = AMap.DrivingPolicy || {}
  lastService = new Cls({
    map,
    autoFitView: true,
    policy: policyMap[policy.value] ?? policyMap.LEAST_TIME,
    ferry: true,
    ...(viaPois.length ? { waypoints: viaPois.map(v => asLngLat(v.lnglat)).filter(Boolean) } : {})
  })
  lastService.search(asLngLat(origin.lnglat), asLngLat(dest.lnglat), (status, result) => {
    if (status !== 'complete') return handleSearchFail(status, result)
    const route = result.routes?.[0]
    if (!route) return handleSearchFail('no_data')
    const pts = []
    for (const s of route.steps || []) {
      for (const p of s.path || []) pts.push([p.lat, p.lng])
    }
    const stepList = (route.steps || []).map(s => ({
      instruction: s.instruction || '继续前行',
      road: s.road || '',
      distance: s.distance,
      location: s.path?.length ? [s.path[0].lng, s.path[0].lat] : null
    }))
    finishRoute(origin, dest, pts, stepList, route.distance, route.time, '驾车')
  })
}

function doWalking(origin, dest) {
  const Cls = freshService(AMap.Walking)
  lastService = new Cls({ map, autoFitView: true })
  lastService.search(asLngLat(origin.lnglat), asLngLat(dest.lnglat), (status, result) => {
    if (status !== 'complete') return handleSearchFail(status, result)
    const route = result.routes?.[0]
    if (!route) return handleSearchFail('no_data')
    const pts = []
    for (const s of route.steps || []) {
      for (const p of s.path || []) pts.push([p.lat, p.lng])
    }
    const stepList = (route.steps || []).map(s => ({
      instruction: s.instruction || '继续步行',
      road: s.road || '',
      distance: s.distance,
      location: s.path?.length ? [s.path[0].lng, s.path[0].lat] : null
    }))
    finishRoute(origin, dest, pts, stepList, route.distance, route.time, '步行')
  })
}

function doRiding(origin, dest) {
  const Cls = freshService(AMap.Riding)
  lastService = new Cls({ map, autoFitView: true })
  lastService.search(asLngLat(origin.lnglat), asLngLat(dest.lnglat), (status, result) => {
    if (status !== 'complete') return handleSearchFail(status, result)
    const route = result.routes?.[0]
    if (!route) return handleSearchFail('no_data')
    const pts = []
    for (const s of route.steps || []) {
      for (const p of s.path || []) pts.push([p.lat, p.lng])
    }
    const stepList = (route.steps || []).map(s => ({
      instruction: s.instruction || '继续骑行',
      road: s.road || '',
      distance: s.distance,
      location: s.path?.length ? [s.path[0].lng, s.path[0].lat] : null
    }))
    finishRoute(origin, dest, pts, stepList, route.distance, route.time, '骑行')
  })
}

function doTransit(origin, dest, cityCode) {
  const Cls = freshService(AMap.Transfer)
  const policyMap = AMap.TransferPolicy || {}
  lastService = new Cls({
    map,
    autoFitView: true,
    city: cityCode || origin.district || '杭州',
    policy: policyMap[policy.value] ?? policyMap.LEAST_TIME,
    nightflag: false
  })
  lastService.search(asLngLat(origin.lnglat), asLngLat(dest.lnglat), (status, result) => {
    if (status !== 'complete') return handleSearchFail(status, result)
    const plan = result.plans?.[0]
    if (!plan) return handleSearchFail('no_data')
    const stepList = []
    let distance = 0
    for (const seg of plan.segments || []) {
      if (seg.walking && seg.walking.distance > 0) {
        distance += seg.walking.distance
        const p0 = seg.walking.steps?.[0]?.path?.[0]
        stepList.push({
          instruction: `步行 ${fmtDist(seg.walking.distance)}`,
          road: '',
          distance: seg.walking.distance,
          location: p0 ? [p0.lng, p0.lat] : null
        })
      }
      const line = seg.bus?.buslines?.[0]
      if (line) {
        distance += line.distance || 0
        stepList.push({
          instruction: `乘坐 ${line.name}（${line.departure_stop?.name || ''} 上车 → ${line.arrival_stop?.name || ''} 下车）`,
          road: '',
          distance: line.distance || 0,
          location: line.departure_stop?.location
            ? [line.departure_stop.location.lng, line.departure_stop.location.lat]
            : null
        })
      }
      const rail = seg.railway
      if (rail) {
        distance += rail.distance || 0
        stepList.push({
          instruction: `乘坐 ${rail.name || '城际列车'}（${rail.departure_stop?.name || ''} → ${rail.arrival_stop?.name || ''}）`,
          road: '',
          distance: rail.distance || 0,
          location: rail.departure_stop?.location
            ? [rail.departure_stop.location.lng, rail.departure_stop.location.lat]
            : null
        })
      }
    }
    finishRoute(origin, dest, [[origin.lnglat.lat, origin.lnglat.lng], [dest.lnglat.lat, dest.lnglat.lng]],
      stepList, distance || plan.distance, plan.time, '公交')
  })
}

function handleSearchFail(status, result) {
  // 输出真实失败原因（如配额超限/参数非法），便于排查
  console.warn('[NavigationPanel] 路线规划失败:', status, result?.info || result || '')
  routeError.value = status === 'no_data'
    ? '未找到可达路线，请尝试调整起终点或出行方式'
    : '路线规划服务异常，请稍后重试'
}

function finishRoute(origin, dest, rawPoints, stepList, distanceM, timeS, modeLabelArg) {
  // 采样抽稀（保留 ≤600 点）以保证 3D 地球渲染流畅
  const step = Math.max(1, Math.ceil(rawPoints.length / 600))
  const points = rawPoints.filter((_, i) => i % step === 0 || i === rawPoints.length - 1)
  steps.value = stepList.map(s => ({
    ...s,
    distanceText: s.distance ? fmtDist(s.distance) : ''
  }))
  summary.value = {
    km: fmtDist(distanceM),
    duration: fmtDuration(timeS),
    text: `${modeLabelArg} · ${fmtDist(distanceM)} · ${fmtDuration(timeS)}`
  }
  lastRoutePoints = points

  saveHistory()
  savePendingPlace()
  syncGlobeRoute()
}

/* ---------- 历史 / 家·单位 记忆 ---------- */

function saveHistory() {
  const s = startText.value?.trim()
  const e = endText.value?.trim()
  if (!s || !e) return
  const rest = histories.value.filter(h => !(h.s === s && h.e === e && h.m === mode.value))
  histories.value = [{ s, e, m: mode.value }, ...rest].slice(0, 12)
  localStorage.setItem('amap-route-history', JSON.stringify(histories.value))
}

function replayHistory(h) {
  startText.value = h.s
  endText.value = h.e
  startPoi = null
  endPoi = null
  vias.value = []
  if (MODES.some(m => m.key === h.m)) mode.value = h.m
  pendingHome.value = ''
  planRoute()
}

/** 归一化起终点坐标（兼容 AMap.LngLat 与数组） */
function toLL(poi) {
  const ll = poi?.lnglat
  if (!ll) return null
  const lng = ll.lng ?? ll[0]
  const lat = ll.lat ?? ll[1]
  return lng != null && lat != null ? { lng: Number(lng), lat: Number(lat) } : null
}

function savePendingPlace() {
  if (!pendingHome.value) return
  const ll = toLL(endPoi)
  if (!ll) return
  const place = { name: endText.value?.trim() || ll.lng + ',' + ll.lat, lng: ll.lng, lat: ll.lat }
  if (pendingHome.value === 'home') {
    homePlace.value = place
    localStorage.setItem('amap-home-place', JSON.stringify(place))
  } else {
    companyPlace.value = place
    localStorage.setItem('amap-company-place', JSON.stringify(place))
  }
  pendingHome.value = ''
}

/** 回家 / 去单位：已设置则从当前位置规划；未设置则引导输入并记忆 */
function goPlace(kind) {
  const place = kind === 'home' ? homePlace.value : companyPlace.value
  panel.value = 'route'
  if (place) {
    pendingHome.value = ''
    endText.value = place.name
    endPoi = { lnglat: { lng: place.lng, lat: place.lat }, name: place.name, district: '', adcode: '' }
    planFromMyLocation()
  } else {
    pendingHome.value = kind
    startText.value = ''
    startPoi = null
    endText.value = ''
    endPoi = null
    routeError.value = ''
    nextTick(() => endInputRef.value?.focus())
  }
}

/** 定位当前位置并回填起点，地图同步居中显示用户位置；成功返回坐标，定位失败返回 null */
async function fillStartFromMyLocation() {
  if (!amapReady.value || !map) return null
  let pos = null
  try {
    pos = await geolocateP()
  } catch {
    return null
  }
  let name = ''
  try { name = await regeoName(pos) } catch { name = '' }
  startText.value = name || '我的位置'
  startPoi = { lnglat: pos, name: startText.value, district: '', adcode: '' }
  // 地图当前显示位置 = 用户当前地址
  map.panTo(pos)
  map.setZoom(Math.max(map.getZoom() || 12, 14))
  clearPoiMarkers()
  addPoiMarker({ location: { lng: pos.lng, lat: pos.lat }, name: startText.value }, true)
  maybeAutoPlan()
  return pos
}

/** 从当前位置出发规划路线（定位失败时退化为地图中心） */
async function planFromMyLocation() {
  if (!amapReady.value || !map) {
    planRoute()
    return
  }
  loading.value = true
  let pos = await fillStartFromMyLocation()
  if (!pos) {
    pos = map.getCenter?.() || null
    if (pos) {
      let name = ''
      try { name = await regeoName(pos) } catch { name = '' }
      startText.value = name || '我的位置'
      startPoi = { lnglat: pos, name: startText.value, district: '', adcode: '' }
    }
  }
  loading.value = false
  planRoute()
}

function geolocateP() {
  return new Promise((resolve, reject) => {
    const geo = new AMap.Geolocation({ enableHighAccuracy: true, timeout: 8000 })
    geo.getCurrentPosition((status, result) => {
      if (status !== 'complete' || !result.position) reject(new Error('定位失败'))
      else resolve(result.position)
    })
  })
}

function regeoName(pos) {
  return new Promise((resolve, reject) => {
    geocoder.getAddress(pos, (s, res) => {
      if (s === 'complete') resolve(res.regeocode?.formattedAddress || '')
      else reject(new Error('逆地理失败'))
    })
  })
}

function syncGlobeRoute() {
  if (summary.value && showOnGlobe.value && lastRoutePoints?.length > 1) {
    emit('route-change', { points: lastRoutePoints, summary: summary.value })
  } else {
    emit('route-change', null)
  }
}

function clearRoute() {
  if (lastService && lastService.clear) lastService.clear()
  if (stepMarker && map) {
    map.remove(stepMarker)
    stepMarker = null
  }
  steps.value = []
  summary.value = null
  lastRoutePoints = null
  routeError.value = ''
  emit('route-change', null)
}

/* ---------- 导航引导 ---------- */

function locateStep(step) {
  if (!step.location || !map) return
  map.panTo(step.location)
  map.setZoom(15)
  if (stepMarker) map.remove(stepMarker)
  stepMarker = new AMap.Marker({
    position: step.location,
    title: '当前引导位置'
  })
  map.add(stepMarker)
}

/* ================= 地点搜索（REST） ================= */

function fmtStr(v) {
  return Array.isArray(v) ? (v.length ? String(v[0]) : '') : (v ?? '')
}

function normalizePoi(p) {
  const raw = typeof p.location === 'string'
    ? p.location.split(',').map(Number)
    : (Array.isArray(p.location) ? p.location.map(Number) : null)
  return {
    id: p.id || p.name,
    name: fmtStr(p.name),
    address: fmtStr(p.address),
    area: [fmtStr(p.pname), fmtStr(p.cityname), fmtStr(p.adname)].filter(Boolean).join(' '),
    distance: p.distance || null,
    tel: fmtStr(p.tel),
    location: raw && raw.length === 2 && !Number.isNaN(raw[0]) ? { lng: raw[0], lat: raw[1] } : null
  }
}

function runTextSearch() {
  const kw = searchText.value.trim()
  if (!kw) return
  searchPoi(kw, `“${kw}” 的搜索结果`, false)
}

function onCategory(c) {
  if (c.type === 'route') {
    panel.value = 'route'
    if (mode.value !== c.mode) switchMode(c.mode)
  } else {
    searchPoi(c.keyword, `${c.label} · 附近推荐`, true)
  }
}

async function searchPoi(keyword, title, around) {
  if (!amapReady.value) return
  poiLoading.value = true
  poiError.value = ''
  poiResults.value = []
  panel.value = 'results'
  resultsTitle.value = title
  try {
    let pois
    if (around) {
      const c = map.getCenter()
      const d = await amapRest.placeAround({
        location: `${c.lng},${c.lat}`,
        keywords: keyword,
        radius: 15000,
        offset: 12,
        page: 1
      })
      pois = d.pois
    } else {
      const d = await amapRest.placeText({
        keywords: keyword,
        city: ipCity.value || undefined,
        citylimit: false,
        offset: 12,
        page: 1
      })
      pois = d.pois
    }
    poiResults.value = (pois || []).map(normalizePoi).filter(p => p.location)
    if (!poiResults.value.length) poiError.value = '未找到相关地点，换个关键词试试'
    renderPoiMarkers()
  } catch (e) {
    poiError.value = e?.message || '搜索失败，请稍后重试'
  } finally {
    poiLoading.value = false
  }
}

/* ---------- 地图标注 ---------- */

function clearPoiMarkers() {
  if (map && poiMarkers.length) map.remove(poiMarkers)
  poiMarkers = []
}

function addPoiMarker(p, center = false) {
  if (!map || !AMap || !p.location) return null
  const marker = new AMap.Marker({
    position: [p.location.lng, p.location.lat],
    title: p.name,
    label: {
      content: `<span class="nav-pin-label${center ? ' is-center' : ''}">${p.name}</span>`,
      direction: 'top',
      offset: new AMap.Pixel(0, -4)
    }
  })
  map.add(marker)
  poiMarkers.push(marker)
  return marker
}

function renderPoiMarkers() {
  clearPoiMarkers()
  poiResults.value.slice(0, 12).forEach(p => addPoiMarker(p))
}

function focusPoi(p) {
  if (!p.location || !map) return
  map.panTo([p.location.lng, p.location.lat])
  map.setZoom(Math.max(map.getZoom() || 12, 15))
  clearPoiMarkers()
  addPoiMarker(p, true)
}

function routeTo(p) {
  if (!p.location) return
  panel.value = 'route'
  endText.value = p.name
  endPoi = { lnglat: { lng: p.location.lng, lat: p.location.lat }, name: p.name, district: p.area, adcode: '' }
  routeError.value = ''
  // 起点为空时自动定位当前位置，并触发自动规划
  if (!startPoi && !startText.value) fillStartFromMyLocation()
}

/* ---------- 收藏夹 ---------- */

function isFav(p) {
  return !!p.location && favorites.value.some(f => f.name === p.name && f.lng === p.location.lng)
}

function persistFavorites() {
  localStorage.setItem('amap-fav-places', JSON.stringify(favorites.value))
}

function toggleFav(p) {
  if (!p.location) return
  const i = favorites.value.findIndex(f => f.name === p.name && f.lng === p.location.lng)
  if (i >= 0) {
    favorites.value.splice(i, 1)
    showToast('已取消收藏')
  } else {
    favorites.value.unshift({ name: p.name, address: p.address, lng: p.location.lng, lat: p.location.lat })
    showToast('已加入收藏夹')
  }
  persistFavorites()
}

function removeFav(f) {
  favorites.value = favorites.value.filter(x => !(x.name === f.name && x.lng === f.lng))
  persistFavorites()
  showToast('已取消收藏')
}

function focusFav(f) {
  focusPoi({ name: f.name, location: { lng: f.lng, lat: f.lat } })
}

function favToRoute(f) {
  panel.value = 'route'
  endText.value = f.name
  endPoi = { lnglat: { lng: f.lng, lat: f.lat }, name: f.name, district: '', adcode: '' }
  routeError.value = ''
}

/* ================= 右上工具条 ================= */

function toggleMapTheme() {
  mapTheme.value = mapTheme.value === 'dark' ? 'light' : 'dark'
  localStorage.setItem('amap-nav-theme', mapTheme.value)
  map?.setMapStyle(mapTheme.value === 'dark' ? 'amap://styles/dark' : 'amap://styles/normal')
}

function toggleTraffic() {
  if (!map || !AMap) return
  trafficOn.value = !trafficOn.value
  if (trafficOn.value) {
    if (!trafficLayer) trafficLayer = new AMap.TrafficLayer({ zIndex: 10 })
    trafficLayer.setMap(map)
    showToast('已显示实时路况')
  } else {
    trafficLayer?.setMap(null)
    showToast('已关闭实时路况')
  }
}

function toggleRanging() {
  if (!map || !AMap) return
  rangingOn.value = !rangingOn.value
  if (rangingOn.value) {
    rangingTool = new AMap.RangingTool(map)
    showToast('点击地图开始测距，再次点击「测距」结束')
  } else {
    try { rangingTool?.turnOff?.() } catch { /* 部分版本无 turnOff */ }
    rangingTool = null
  }
}

function togglePitch() {
  if (!map) return
  pitchOn.value = !pitchOn.value
  map.setPitch(pitchOn.value ? 45 : 0)
}

function onFullscreenChange() {
  fullscreenOn.value = !!document.fullscreenElement
}

function toggleFullscreen() {
  try {
    if (!document.fullscreenElement) overlayRef.value?.requestFullscreen?.()
    else document.exitFullscreen?.()
  } catch { /* 忽略 */ }
}

async function locateMe() {
  if (!amapReady.value) return
  try {
    const pos = await geolocateP()
    map.panTo(pos)
    map.setZoom(Math.max(map.getZoom() || 12, 14))
    clearPoiMarkers()
    addPoiMarker({ location: { lng: pos.lng, lat: pos.lat }, name: '我的位置' }, true)
  } catch {
    showToast('定位失败，请检查浏览器定位权限')
  }
}

/* ---------- Toast ---------- */

function showToast(msg) {
  toast.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.value = '' }, 1800)
}

/* ---------- 格式化 ---------- */

function fmtDist(m) {
  const n = Number(m) || 0
  return n >= 1000 ? `${(n / 1000).toFixed(1)} 公里` : `${Math.round(n)} 米`
}

function fmtDuration(s) {
  const min = Math.round(s / 60)
  if (min < 60) return `${min} 分钟`
  return `${Math.floor(min / 60)} 小时 ${min % 60} 分`
}
</script>

<style scoped>
.nav-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: #f7f8fa;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', 'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.nav-overlay .ic {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.nav-map {
  position: absolute;
  inset: 0;
}

/* ---------- 加载 / 错误 ---------- */
.map-loading,
.map-error {
  position: absolute;
  inset: 0;
  z-index: 30;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  background: #f7f8fa;
  color: #4e5969;
  font-size: 13px;
}

.map-error {
  font-size: 14px;
  line-height: 1.6;
  padding: 0 24px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(22, 119, 255, 0.18);
  border-top-color: #1677ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ================= 左上主卡片 ================= */
.side-card {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 20;
  width: 340px;
  max-height: calc(100% - 24px);
  overflow-y: auto;
  scrollbar-width: none; /* Firefox 隐藏滚动条 */
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 6px 24px rgba(29, 33, 41, 0.12);
}

.side-card::-webkit-scrollbar {
  display: none;
}

/* ---------- 搜索框 ---------- */
.search-box {
  display: flex;
  align-items: center;
  gap: 4px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f1f3;
}

.search-input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: #1d2129;
  font-family: inherit;
}

.search-input::placeholder {
  color: #86909c;
}

.sb-btn {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #4e5969;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.sb-btn:hover {
  background: #f2f3f5;
  color: #1d2129;
}

.sb-btn.sb-route {
  color: #1677ff;
}

.sb-btn.sb-route:hover {
  background: #e8f3ff;
}

/* ---------- 分类宫格 ---------- */
.cat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  row-gap: 14px;
  padding: 12px 2px 10px;
}

.cat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 0;
}

.cat-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.cat-icon .ic {
  width: 20px;
  height: 20px;
}

.cat:hover .cat-icon {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(29, 33, 41, 0.18);
}

.cat-name {
  font-size: 12px;
  color: #1d2129;
  line-height: 1;
}

.cat:hover .cat-name {
  color: #1677ff;
}

/* ---------- 回家 / 去单位 / 收藏夹 ---------- */
.quick-row {
  display: flex;
  align-items: center;
  border-top: 1px solid #f0f1f3;
  padding-top: 10px;
}

.quick {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: none;
  background: transparent;
  padding: 6px 0;
  font-size: 13px;
  color: #1d2129;
  cursor: pointer;
  border-radius: 8px;
  font-family: inherit;
  transition: background 0.15s ease;
}

.quick .ic {
  width: 16px;
  height: 16px;
  color: #4e5969;
}

.quick:hover {
  background: #f7f8fa;
}

.quick:hover .ic {
  color: #1677ff;
}

.quick em {
  font-style: normal;
  color: #86909c;
}

.quick-sep {
  width: 1px;
  height: 14px;
  background: #e5e6eb;
  flex-shrink: 0;
}

/* ---------- 联想下拉 ---------- */
.tips-pop {
  margin: -4px 0 0;
  display: flex;
  flex-direction: column;
  border: 1px solid #f0f1f3;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(29, 33, 41, 0.08);
}

.tip-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 12px;
  border: none;
  background: #fff;
  cursor: pointer;
  text-align: left;
  font-family: inherit;
  transition: background 0.12s ease;
}

.tip-row + .tip-row {
  border-top: 1px solid #f7f8fa;
}

.tip-row:hover {
  background: #f7f8fa;
}

.tip-pin {
  width: 14px;
  height: 14px;
  color: #86909c;
}

.tip-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: #1d2129;
}

.tip-district {
  flex-shrink: 0;
  font-size: 11.5px;
  color: #86909c;
  max-width: 40%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ---------- 路线头部（模式图标） ---------- */
.route-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.mode-tabs {
  display: flex;
  align-items: center;
  gap: 4px;
}

.mode-tab {
  width: 42px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #4e5969;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.mode-tab .ic {
  width: 21px;
  height: 21px;
}

.mode-tab:hover {
  background: #f2f3f5;
}

.mode-tab.is-active {
  background: #e8f3ff;
  color: #1677ff;
}

.head-close {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #4e5969;
  cursor: pointer;
  transition: background 0.15s ease;
}

.head-close:hover {
  background: #f2f3f5;
  color: #1d2129;
}

.head-back {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #4e5969;
  cursor: pointer;
  transition: background 0.15s ease;
}

.head-back:hover {
  background: #f2f3f5;
  color: #1d2129;
}

/* ---------- 起终点输入卡 ---------- */
.route-card {
  display: flex;
  gap: 10px;
  padding: 8px 12px;
  background: #f2f3f5;
  border-radius: 12px;
}

.route-lines {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.route-line {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 0;
}

.route-line + .route-line {
  border-top: 1px solid #e5e6eb;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot-start { background: #00b578; }
.dot-end { background: #f5484d; }
.dot-via { background: #86909c; }

.route-line input {
  flex: 1;
  min-width: 0;
  border: none;
  outline: none;
  background: transparent;
  font-size: 13.5px;
  color: #1d2129;
  font-family: inherit;
}

.route-line input::placeholder {
  color: #86909c;
}

.line-act {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: #86909c;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.line-act .ic {
  width: 14px;
  height: 14px;
}

.line-act:hover {
  background: #e5e6eb;
  color: #f5484d;
}

.route-side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
  padding-left: 10px;
  border-left: 1px solid #e5e6eb;
  flex-shrink: 0;
}

.side-act {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: #4e5969;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.side-act .ic {
  width: 18px;
  height: 18px;
}

.side-act:hover {
  background: #e5e6eb;
  color: #1677ff;
}

/* ---------- 策略选择 ---------- */
.policy-bar {
  display: flex;
}

.policy-picker {
  position: relative;
  width: 100%;
}

.policy-select {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e5e6eb;
  border-radius: 10px;
  background: #fff;
  color: #1d2129;
  font-size: 12.5px;
  font-family: inherit;
  text-align: left;
  outline: none;
  cursor: pointer;
  transition: border-color 0.15s ease;
}

.policy-select:hover,
.policy-picker.is-open .policy-select {
  border-color: #1677ff;
}

.policy-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.policy-arrow {
  width: 14px;
  height: 14px;
  color: #86909c;
  transition: transform 0.18s ease;
}

.policy-picker.is-open .policy-arrow {
  transform: rotate(180deg);
}

.policy-pop {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  z-index: 30;
  padding: 6px;
  border-radius: 12px;
  border: 1px solid #f0f1f3;
  background: #fff;
  box-shadow: 0 8px 24px rgba(29, 33, 41, 0.14);
}

.policy-opt {
  display: block;
  width: 100%;
  padding: 8px 10px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #1d2129;
  font-size: 12.5px;
  font-family: inherit;
  text-align: left;
  cursor: pointer;
  transition: background 0.15s ease;
}

.policy-opt:hover {
  background: #f2f3f5;
}

.policy-opt.is-active {
  background: #e8f3ff;
  color: #1677ff;
  font-weight: 600;
}

/* ---------- 规划按钮 / 提示 ---------- */
.plan-btn {
  padding: 11px 0;
  border: none;
  border-radius: 10px;
  background: #1677ff;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.15s ease, transform 0.15s ease;
}

.plan-btn:hover:not(:disabled) {
  background: #0e5fd8;
}

.plan-btn:active:not(:disabled) {
  transform: scale(0.99);
}

.plan-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.set-hint {
  margin: 0;
  padding: 8px 12px;
  border-radius: 10px;
  background: #e8f7ee;
  border: 1px solid rgba(0, 181, 120, 0.3);
  color: #00875a;
  font-size: 12px;
  line-height: 1.5;
}

.nav-error {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  background: #ffece8;
  border: 1px solid rgba(245, 72, 77, 0.3);
  color: #cb2634;
  font-size: 12px;
  line-height: 1.5;
}

/* ---------- 路线结果 ---------- */
.route-result {
  padding: 12px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f0f7ff, #e8f3ff);
  border: 1px solid #d6e8ff;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rr-main {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-wrap: wrap;
}

.rr-time {
  font-size: 20px;
  font-weight: 700;
  color: #1677ff;
}

.rr-dist {
  font-size: 13px;
  color: #4e5969;
}

.rr-mode {
  margin-left: auto;
  font-size: 11px;
  font-weight: 600;
  color: #1677ff;
  background: #fff;
  border: 1px solid #d6e8ff;
  border-radius: 999px;
  padding: 2px 10px;
}

.globe-sync {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #4e5969;
  cursor: pointer;
  user-select: none;
}

.globe-sync input {
  accent-color: #1677ff;
}

.steps {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.steps-header {
  font-size: 11.5px;
  font-weight: 600;
  letter-spacing: 0.5px;
  color: #86909c;
  padding: 2px 4px;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 8px 8px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s ease;
}

.step-item:hover {
  background: #f7f8fa;
}

.step-index {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #e8f3ff;
  color: #1677ff;
  font-size: 10.5px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 1px;
}

.step-text {
  flex: 1;
  font-size: 12.5px;
  color: #1d2129;
  line-height: 1.5;
}

.step-text em {
  font-style: normal;
  color: #86909c;
}

.step-distance {
  flex-shrink: 0;
  font-size: 11px;
  color: #86909c;
}

/* ---------- 历史记录 ---------- */
.history {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.his-title {
  font-size: 11.5px;
  font-weight: 600;
  letter-spacing: 0.5px;
  color: #86909c;
  padding: 2px 4px 4px;
}

.his-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 8px;
  border: none;
  border-radius: 10px;
  background: transparent;
  cursor: pointer;
  text-align: left;
  font-family: inherit;
  transition: background 0.15s ease;
}

.his-item:hover {
  background: #f7f8fa;
}

.his-clock {
  width: 16px;
  height: 16px;
  color: #86909c;
}

.his-text {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: #1d2129;
}

/* ---------- 结果 / 收藏列表 ---------- */
.panel-head {
  display: flex;
  align-items: center;
  gap: 6px;
  padding-bottom: 4px;
}

.panel-title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
}

.state-tip {
  margin: 0;
  padding: 20px 0;
  text-align: center;
  font-size: 12.5px;
  color: #86909c;
}

.poi-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.poi-row {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 9px 8px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s ease;
}

.poi-row:hover {
  background: #f7f8fa;
}

.poi-main {
  flex: 1;
  min-width: 0;
}

.poi-name {
  font-size: 13px;
  font-weight: 600;
  color: #1d2129;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.poi-name em {
  font-style: normal;
  font-weight: 400;
  font-size: 11px;
  color: #00b578;
}

.poi-addr {
  margin-top: 2px;
  font-size: 11.5px;
  color: #86909c;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.poi-act {
  width: 28px;
  height: 28px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #86909c;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.poi-act .ic {
  width: 15px;
  height: 15px;
}

.poi-act:hover {
  background: #fff7e6;
  color: #f7ba1e;
}

.poi-act.is-fav {
  color: #f7ba1e;
}

.poi-go {
  flex-shrink: 0;
  padding: 4px 10px;
  border: 1px solid #1677ff;
  border-radius: 999px;
  background: #fff;
  color: #1677ff;
  font-size: 11.5px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.poi-go:hover {
  background: #1677ff;
  color: #fff;
}

/* ================= 天气徽章 ================= */
.weather-chip {
  position: absolute;
  top: 12px;
  left: 400px;
  z-index: 20;
  display: flex;
  align-items: center;
  gap: 4px;
  height: 36px;
  padding: 0 12px;
  border: none;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 10px rgba(29, 33, 41, 0.1);
  color: #1d2129;
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
}

.weather-chip .ic {
  width: 14px;
  height: 14px;
  color: #86909c;
}

.weather-city {
  font-weight: 600;
}

.weather-split {
  width: 1px;
  height: 14px;
  margin: 0 4px;
  background: #e5e6eb;
}

.weather-pop {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  min-width: 200px;
  padding: 10px 14px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 24px rgba(29, 33, 41, 0.14);
  cursor: default;
}

.wp-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 4px 0;
  font-size: 12.5px;
}

.wp-row span {
  color: #86909c;
}

.wp-row b {
  color: #1d2129;
  font-weight: 600;
}

/* ================= 右上工具条 ================= */
.top-bar {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 20;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 12px;
  border: none;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 10px rgba(29, 33, 41, 0.1);
  color: #1d2129;
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.tool-chip .ic {
  width: 16px;
  height: 16px;
}

.tool-chip:hover {
  color: #1677ff;
}

.tool-chip.is-on {
  background: #e8f3ff;
  color: #1677ff;
}

.tool-close {
  width: 36px;
  padding: 0;
  justify-content: center;
}

/* ================= 右下地图控件 ================= */
.ctl-stack {
  position: absolute;
  right: 12px;
  bottom: 34px;
  z-index: 20;
  width: 36px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(29, 33, 41, 0.12);
  overflow: hidden;
}

.ctl-btn {
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-bottom: 1px solid #f0f1f3;
  background: #fff;
  color: #1d2129;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
  font-family: inherit;
}

.ctl-btn:hover {
  background: #f7f8fa;
  color: #1677ff;
}

.ctl-btn.ctl-last {
  border-bottom: none;
}

.ctl-btn .ic {
  width: 16px;
  height: 16px;
}

.ctl-text {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

/* ================= 版权条 / Toast ================= */
.map-attribution {
  position: absolute;
  left: 74px;
  bottom: 8px;
  z-index: 15;
  font-size: 10.5px;
  color: rgba(29, 33, 41, 0.45);
  pointer-events: none;
  user-select: none;
}

.nav-toast {
  position: absolute;
  bottom: 72px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 40;
  padding: 9px 18px;
  border-radius: 999px;
  background: rgba(29, 33, 41, 0.92);
  color: #fff;
  font-size: 13px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.25);
  max-width: 80vw;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  pointer-events: none;
}

/* AMap Marker label 渲染在地图容器内（脱离 Vue 作用域），需全局样式 */
:global(.nav-pin-label) {
  display: inline-block;
  padding: 3px 9px;
  border-radius: 8px;
  background: rgba(22, 119, 255, 0.92);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.25);
}

:global(.nav-pin-label.is-center) {
  background: rgba(245, 72, 77, 0.95);
}

/* ================= 夜间主题（跟随底图 mapTheme） ================= */
/* 配色对齐 ServicePanel 基础深色样式：面板 #1c1c1e、文字 #f5f5f7、强调 #0a84ff */
.is-dark.nav-overlay {
  background: #131417;
}

.is-dark .map-loading,
.is-dark .map-error {
  background: #131417;
  color: #aeb7c4;
}

/* ---------- 左上主卡片 ---------- */
.is-dark .side-card {
  background: #1c1c1e;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.5);
}

.is-dark .search-box {
  border-bottom-color: rgba(255, 255, 255, 0.1);
}

.is-dark .search-input {
  color: #f5f5f7;
}

.is-dark .search-input::placeholder,
.is-dark .route-line input::placeholder {
  color: #86868b;
}

.is-dark .sb-btn,
.is-dark .head-close,
.is-dark .head-back,
.is-dark .mode-tab {
  color: #aeb7c4;
}

.is-dark .sb-btn:hover,
.is-dark .head-close:hover,
.is-dark .head-back:hover,
.is-dark .mode-tab:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #f5f5f7;
}

.is-dark .sb-btn.sb-route {
  color: #0a84ff;
}

.is-dark .sb-btn.sb-route:hover {
  background: rgba(10, 132, 255, 0.16);
}

.is-dark .cat-name {
  color: #f5f5f7;
}

.is-dark .cat:hover .cat-name {
  color: #0a84ff;
}

.is-dark .quick-row {
  border-top-color: rgba(255, 255, 255, 0.1);
}

.is-dark .quick {
  color: #f5f5f7;
}

.is-dark .quick .ic {
  color: #aeb7c4;
}

.is-dark .quick:hover {
  background: rgba(255, 255, 255, 0.06);
}

.is-dark .quick:hover .ic {
  color: #0a84ff;
}

.is-dark .quick em {
  color: #86868b;
}

.is-dark .quick-sep {
  background: rgba(255, 255, 255, 0.14);
}

/* ---------- 联想下拉 ---------- */
.is-dark .tips-pop {
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.5);
}

.is-dark .tip-row {
  background: #1c1c1e;
}

.is-dark .tip-row + .tip-row {
  border-top-color: rgba(255, 255, 255, 0.06);
}

.is-dark .tip-row:hover {
  background: rgba(255, 255, 255, 0.06);
}

.is-dark .tip-pin,
.is-dark .tip-district {
  color: #86868b;
}

.is-dark .tip-name {
  color: #f5f5f7;
}

/* ---------- 路线面板 ---------- */
.is-dark .mode-tab.is-active {
  background: rgba(10, 132, 255, 0.18);
  color: #0a84ff;
}

.is-dark .route-card {
  background: rgba(255, 255, 255, 0.06);
}

.is-dark .route-line + .route-line {
  border-top-color: rgba(255, 255, 255, 0.08);
}

.is-dark .route-line input {
  color: #f5f5f7;
}

.is-dark .line-act {
  color: #86868b;
}

.is-dark .line-act:hover {
  background: rgba(255, 255, 255, 0.1);
}

.is-dark .route-side {
  border-left-color: rgba(255, 255, 255, 0.08);
}

.is-dark .side-act {
  color: #aeb7c4;
}

.is-dark .side-act:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #0a84ff;
}

.is-dark .policy-select {
  border-color: rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.06);
  color: #f5f5f7;
}

.is-dark .policy-select:hover,
.is-dark .policy-picker.is-open .policy-select {
  border-color: #0a84ff;
}

.is-dark .policy-arrow {
  color: #86868b;
}

.is-dark .policy-pop {
  border-color: rgba(255, 255, 255, 0.1);
  background: #1c1c1e;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);
}

.is-dark .policy-opt {
  color: #f5f5f7;
}

.is-dark .policy-opt:hover {
  background: rgba(255, 255, 255, 0.06);
}

.is-dark .policy-opt.is-active {
  background: rgba(10, 132, 255, 0.18);
  color: #0a84ff;
}

.is-dark .plan-btn {
  background: #0a84ff;
}

.is-dark .plan-btn:hover:not(:disabled) {
  background: #0071e3;
}

.is-dark .set-hint {
  background: rgba(48, 209, 88, 0.12);
  border-color: rgba(48, 209, 88, 0.35);
  color: #7ee2a0;
}

.is-dark .nav-error {
  background: rgba(255, 69, 58, 0.14);
  border-color: rgba(255, 69, 58, 0.35);
  color: #ff9f9a;
}

/* ---------- 路线结果 ---------- */
.is-dark .route-result {
  background: linear-gradient(135deg, rgba(10, 132, 255, 0.14), rgba(10, 132, 255, 0.07));
  border-color: rgba(10, 132, 255, 0.3);
}

.is-dark .rr-time {
  color: #0a84ff;
}

.is-dark .rr-dist,
.is-dark .globe-sync {
  color: #aeb7c4;
}

.is-dark .rr-mode {
  background: transparent;
  border-color: rgba(10, 132, 255, 0.4);
  color: #0a84ff;
}

.is-dark .steps-header,
.is-dark .step-text em,
.is-dark .step-distance {
  color: #86868b;
}

.is-dark .step-item:hover {
  background: rgba(255, 255, 255, 0.06);
}

.is-dark .step-index {
  background: rgba(10, 132, 255, 0.18);
  color: #0a84ff;
}

.is-dark .step-text {
  color: #f5f5f7;
}

/* ---------- 历史 / 结果 / 收藏列表 ---------- */
.is-dark .his-title {
  color: #86868b;
}

.is-dark .his-item:hover {
  background: rgba(255, 255, 255, 0.06);
}

.is-dark .his-clock {
  color: #86868b;
}

.is-dark .his-text {
  color: #f5f5f7;
}

.is-dark .panel-title {
  color: #f5f5f7;
}

.is-dark .state-tip {
  color: #86868b;
}

.is-dark .poi-row:hover {
  background: rgba(255, 255, 255, 0.06);
}

.is-dark .poi-name {
  color: #f5f5f7;
}

.is-dark .poi-addr {
  color: #86868b;
}

.is-dark .poi-act {
  color: #86868b;
}

.is-dark .poi-act:hover {
  background: rgba(255, 214, 10, 0.14);
  color: #ffd60a;
}

.is-dark .poi-act.is-fav {
  color: #ffd60a;
}

.is-dark .poi-go {
  background: transparent;
  color: #0a84ff;
}

.is-dark .poi-go:hover {
  background: #0a84ff;
  color: #fff;
}

/* ---------- 天气徽章 ---------- */
.is-dark .weather-chip {
  background: #1c1c1e;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.45);
  color: #f5f5f7;
}

.is-dark .weather-chip .ic {
  color: #86868b;
}

.is-dark .weather-split {
  background: rgba(255, 255, 255, 0.15);
}

.is-dark .weather-pop {
  background: #1c1c1e;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.5);
}

.is-dark .wp-row span {
  color: #86868b;
}

.is-dark .wp-row b {
  color: #f5f5f7;
}

/* ---------- 右上工具条 / 右下控件 ---------- */
.is-dark .tool-chip {
  background: #1c1c1e;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.45);
  color: #f5f5f7;
}

.is-dark .tool-chip:hover {
  color: #0a84ff;
}

.is-dark .tool-chip.is-on {
  background: rgba(10, 132, 255, 0.2);
  color: #0a84ff;
}

.is-dark .ctl-stack {
  background: #1c1c1e;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.is-dark .ctl-btn {
  border-bottom-color: rgba(255, 255, 255, 0.08);
  background: #1c1c1e;
  color: #f5f5f7;
}

.is-dark .ctl-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #0a84ff;
}

.is-dark .map-attribution {
  color: rgba(255, 255, 255, 0.45);
}

/* ================= 响应式 ================= */
@media (max-width: 1100px) {
  .weather-chip {
    display: none;
  }
}

@media (max-width: 760px) {
  .side-card {
    width: calc(100vw - 24px);
  }

  .chip-text {
    display: none;
  }

  .tool-chip {
    width: 36px;
    padding: 0;
    justify-content: center;
  }

  .map-attribution {
    display: none;
  }
}
</style>
