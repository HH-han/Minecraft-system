<template>
  <div class="control-root" :class="{ 'is-dark': isDark }">
    <!-- 左侧：图层 / 视角 / 导航面板 -->
    <button class="panel-toggle" :class="{ 'is-open': panelOpen }" @click="panelOpen = !panelOpen" title="图层控制">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polygon points="12 2 2 7 12 12 22 7 12 2"/>
        <polyline points="2 17 12 22 22 17"/>
        <polyline points="2 12 12 17 22 12"/>
      </svg>
    </button>

    <Transition name="panel-slide">
      <aside v-show="panelOpen" class="layer-panel">
        <div class="panel-section">
          <h3 class="section-title">场景预设</h3>
          <div class="scene-grid">
            <button
              v-for="s in SCENE_PRESETS"
              :key="s.id"
              class="scene-chip"
              :title="'启用组合：' + Object.keys(s.layers).join('、')"
              @click="$emit('applyScene', s)"
            >{{ s.name }}</button>
          </div>
        </div>

        <div class="panel-section">
          <h3 class="section-title">图层控制</h3>
          <div v-for="g in LAYER_GROUPS" :key="g.id" class="layer-group">
            <button class="group-header" @click="toggleGroup(g.id)">
              <span>{{ g.icon }} {{ g.title }}</span>
              <span class="chev" :class="{ 'is-open': !collapsed[g.id] }">▾</span>
            </button>
            <div v-show="!collapsed[g.id]" class="layer-list">
              <label v-for="l in g.layers" :key="l.key" class="layer-row">
                <span class="layer-info">
                  <span class="layer-name">{{ l.icon }} {{ l.name }}</span>
                  <span class="layer-desc">{{ l.desc }}</span>
                </span>
                <button
                  class="switch"
                  :class="{ 'is-on': layers[l.key] }"
                  role="switch"
                  :aria-checked="layers[l.key]"
                  :aria-label="l.name"
                  @click.prevent="$emit('toggleLayer', l.key)"
                >
                  <span class="switch-thumb"></span>
                </button>
              </label>
            </div>
          </div>
        </div>

        <div class="panel-section">
          <h3 class="section-title">视角切换</h3>
          <div class="preset-grid">
            <button
              v-for="p in VIEW_PRESETS"
              :key="p.id"
              class="preset-chip"
              @click="$emit('presetView', p)"
            >{{ p.name }}</button>
          </div>
        </div>

        <button class="nav-btn" @click="$emit('openNavigation')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polygon points="3 11 22 2 13 21 11 13 3 11"/>
          </svg>
          路线导航（高德）
        </button>
        <button class="nav-btn svc-btn" @click="$emit('openServices')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="7" height="7" rx="1"/>
            <rect x="14" y="3" width="7" height="7" rx="1"/>
            <rect x="3" y="14" width="7" height="7" rx="1"/>
            <rect x="14" y="14" width="7" height="7" rx="1"/>
          </svg>
          服务工具箱（19 项）
        </button>
      </aside>
    </Transition>

    <!-- 右侧：缩放 / 主题 / 旋转 -->
    <div class="side-buttons">
      <div class="control-group">
        <button class="control-btn" @click="$emit('zoomIn')" title="放大" aria-label="放大">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
            <line x1="11" y1="8" x2="11" y2="14"/>
            <line x1="8" y1="11" x2="14" y2="11"/>
          </svg>
        </button>
        <button class="control-btn" @click="$emit('zoomOut')" title="缩小" aria-label="缩小">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
            <line x1="8" y1="11" x2="14" y2="11"/>
          </svg>
        </button>
        <div class="control-divider"></div>
        <button class="control-btn" @click="$emit('reset')" title="重置视图" aria-label="重置视图">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
            <path d="M3 3v5h5"/>
          </svg>
        </button>
      </div>
      <div class="control-group">
        <button class="control-btn theme-btn" @click="$emit('toggleTheme')" :title="isDark ? '切换到白天模式' : '切换到暗黑模式'">
          <svg v-if="isDark" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="5"/>
            <line x1="12" y1="1" x2="12" y2="3"/>
            <line x1="12" y1="21" x2="12" y2="23"/>
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
            <line x1="1" y1="12" x2="3" y2="12"/>
            <line x1="21" y1="12" x2="23" y2="12"/>
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
          </svg>
        </button>
        <button
          class="control-btn"
          :class="{ 'is-active': autoRotate }"
          @click="$emit('toggleAutoRotate')"
          title="自动旋转"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polygon points="5 3 19 12 5 21 5 3"/>
          </svg>
        </button>
        <div class="zoom-info">
          <span class="zoom-label">缩放</span>
          <div class="zoom-bar">
            <div class="zoom-fill" :style="{ height: zoomLevelPercent + '%' }"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { VIEW_PRESETS, LAYER_GROUPS, SCENE_PRESETS } from '../config.js'

const props = defineProps({
  isDark: { type: Boolean, default: false },
  autoRotate: { type: Boolean, default: true },
  zoomLevel: { type: Number, default: 1 },
  layers: { type: Object, required: true }
})

defineEmits(['toggleLayer', 'presetView', 'openNavigation', 'openServices', 'zoomIn', 'zoomOut', 'reset', 'toggleTheme', 'toggleAutoRotate', 'applyScene'])

// 分组折叠状态（默认按配置展开基础组）
const collapsed = reactive(Object.fromEntries(LAYER_GROUPS.map(g => [g.id, !g.open])))

function toggleGroup(id) {
  collapsed[id] = !collapsed[id]
}

// 移动端默认收起
const panelOpen = ref(window.innerWidth > 768)

const zoomLevelPercent = computed(() => {
  const level = Math.min(Math.max(props.zoomLevel - 1, 0), 4)
  return (level / 4) * 100
})
</script>

<style scoped>
.control-root {
  position: absolute;
  inset: 0;
  z-index: 10;
  pointer-events: none;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.control-root > * {
  pointer-events: auto;
}

/* ---------- 左侧图层面板 ---------- */
.layer-panel {
  position: absolute;
  left: 20px;
  top: 20px;
  width: 248px;
  max-height: calc(100% - 200px);
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.layer-panel::-webkit-scrollbar {
  display: none;
}

.is-dark .layer-panel {
  background: rgba(29, 29, 31, 0.78);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.4);
}

.panel-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.section-title {
  margin: 0;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #86868b;
}

.is-dark .section-title {
  color: #86868b;
}

.layer-list {
  display: flex;
  flex-direction: column;
}

/* 分组 */
.layer-group {
  display: flex;
  flex-direction: column;
}

.group-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 8px 6px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 12.5px;
  font-weight: 600;
  color: #1d1d1f;
  cursor: pointer;
  transition: background 0.18s ease;
}

.is-dark .group-header {
  color: #f5f5f7;
}

.group-header:hover {
  background: rgba(0, 0, 0, 0.04);
}

.is-dark .group-header:hover {
  background: rgba(255, 255, 255, 0.07);
}

.chev {
  font-size: 11px;
  color: #86868b;
  transition: transform 0.2s ease;
}

.chev.is-open {
  transform: rotate(-180deg);
}

/* 场景预设 */
.scene-grid {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.scene-chip {
  padding: 9px 10px;
  border: 1px solid rgba(10, 132, 255, 0.25);
  border-radius: 10px;
  background: rgba(10, 132, 255, 0.08);
  color: #1d1d1f;
  font-size: 13px;
  font-weight: 600;
  text-align: left;
  cursor: pointer;
  transition: all 0.18s ease;
}

.scene-chip:hover {
  background: rgba(10, 132, 255, 0.18);
  transform: translateY(-1px);
}

.is-dark .scene-chip {
  color: #6db8ff;
  background: rgba(10, 132, 255, 0.14);
  border-color: rgba(10, 132, 255, 0.35);
}

.is-dark .scene-chip:hover {
  background: rgba(10, 132, 255, 0.28);
}

.layer-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 7px 6px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.layer-row:hover {
  background: rgba(0, 0, 0, 0.04);
}

.is-dark .layer-row:hover {
  background: rgba(255, 255, 255, 0.07);
}

.layer-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.layer-name {
  font-size: 13px;
  font-weight: 600;
  color: #1d1d1f;
}

.is-dark .layer-name {
  color: #f5f5f7;
}

.layer-desc {
  font-size: 11px;
  color: #86868b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 开关 */
.switch {
  flex-shrink: 0;
  width: 38px;
  height: 22px;
  border: none;
  border-radius: 11px;
  background: rgba(120, 120, 128, 0.28);
  position: relative;
  cursor: pointer;
  transition: background 0.18s ease;
  padding: 0;
}

.switch.is-on {
  background: #34c759;
}

.switch-thumb {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.25);
  transition: transform 0.18s cubic-bezier(0.16, 1, 0.3, 1);
}

.switch.is-on .switch-thumb {
  transform: translateX(16px);
}

/* 视角预设 */
.preset-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 6px;
}

.preset-chip {
  padding: 7px 0;
  font-size: 12px;
  font-weight: 500;
  color: #1d1d1f;
  background: rgba(0, 0, 0, 0.04);
  border: 1px solid transparent;
  border-radius: 9px;
  cursor: pointer;
  transition: all 0.18s ease;
}

.preset-chip:hover {
  background: rgba(0, 122, 255, 0.12);
  color: #007aff;
}

.is-dark .preset-chip {
  color: #f5f5f7;
  background: rgba(255, 255, 255, 0.08);
}

.is-dark .preset-chip:hover {
  background: rgba(10, 132, 255, 0.25);
  color: #6db8ff;
}

/* 导航按钮 */
.nav-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 11px 0;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #0a84ff, #0055d4);
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(10, 132, 255, 0.35);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.nav-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(10, 132, 255, 0.45);
}

.nav-btn:active {
  transform: scale(0.98);
}

.nav-btn.svc-btn {
  background: linear-gradient(135deg, #30d158, #1a8f3c);
  box-shadow: 0 4px 14px rgba(48, 209, 88, 0.3);
}

.nav-btn.svc-btn:hover {
  box-shadow: 0 6px 18px rgba(48, 209, 88, 0.42);
}

.nav-btn svg {
  width: 16px;
  height: 16px;
}

/* 面板收起按钮 */
.panel-toggle {
  position: absolute;
  left: 20px;
  top: 20px;
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  color: #1d1d1f;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: opacity 0.2s ease;
  z-index: 1;
}

.panel-toggle.is-open {
  opacity: 0;
  pointer-events: none;
}

.is-dark .panel-toggle {
  background: rgba(29, 29, 31, 0.78);
  border-color: rgba(255, 255, 255, 0.1);
  color: #f5f5f7;
}

.panel-toggle svg {
  width: 18px;
  height: 18px;
}

.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.2s ease;
}

.panel-slide-enter-from,
.panel-slide-leave-to {
  transform: translateX(-16px);
  opacity: 0;
}

/* ---------- 右侧按钮组 ---------- */
.side-buttons {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 6px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.is-dark .control-group {
  background: rgba(29, 29, 31, 0.72);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.3);
}

.control-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 8px;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.18s cubic-bezier(0.16, 1, 0.3, 1);
}

.is-dark .control-btn {
  color: #f5f5f7;
}

.control-btn:hover {
  background: rgba(0, 0, 0, 0.06);
}

.is-dark .control-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.control-btn:active {
  transform: scale(0.94);
}

.control-btn svg {
  width: 18px;
  height: 18px;
}

.control-btn.is-active {
  background: rgba(0, 122, 255, 0.12);
  color: #007aff;
}

.is-dark .control-btn.is-active {
  background: rgba(10, 132, 255, 0.2);
  color: #0a84ff;
}

.control-divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.06);
  margin: 4px 0;
}

.is-dark .control-divider {
  background: rgba(255, 255, 255, 0.08);
}

.zoom-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 10px 0 6px;
}

.zoom-label {
  font-size: 10px;
  font-weight: 500;
  color: #6e6e73;
  letter-spacing: 0.3px;
}

.is-dark .zoom-label {
  color: #86868b;
}

.zoom-bar {
  width: 4px;
  height: 60px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 2px;
  overflow: hidden;
  position: relative;
}

.is-dark .zoom-bar {
  background: rgba(255, 255, 255, 0.12);
}

.zoom-fill {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, #007aff, #5ac8fa);
  border-radius: 2px;
  transition: height 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

/* ---------- 响应式 ---------- */
@media (max-width: 767px) {
  .layer-panel {
    left: 12px;
    top: 60px;
    width: 230px;
    max-height: calc(100% - 140px);
    padding: 12px;
  }

  .panel-toggle {
    left: 12px;
    top: 12px;
  }

  .side-buttons {
    right: 12px;
  }

  .control-btn {
    width: 36px;
    height: 36px;
  }

  .preset-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
