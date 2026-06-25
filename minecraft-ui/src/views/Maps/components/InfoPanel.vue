<template>
  <Teleport to="body">
    <Transition name="panel-fade">
      <div v-if="visible" class="info-panel-mask" :class="{ 'hover-mode': isHover }" @click="handleMaskClick">
        <Transition name="panel-slide">
          <div
            v-if="visible"
            class="info-panel"
            :class="{ 'is-dark': isDark, 'is-country': isCountry, 'is-hover': isHover }"
            @click.stop
          >
            <button v-if="!isHover" class="close-btn" @click="$emit('close')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
            <div v-if="isHover" class="hover-badge">预览</div>
            <template v-if="isCountry && country">
              <div class="panel-header">
                <span class="flag-emoji">{{ country.flag }}</span>
                <div>
                  <h2 class="panel-title">{{ country.name }}</h2>
                  <p class="panel-subtitle">{{ country.nameEn }}</p>
                </div>
              </div>
              <div class="panel-content">
                <div class="info-grid">
                  <div class="info-item">
                    <span class="info-icon">🏛️</span>
                    <div class="info-text">
                      <span class="info-label">首都</span>
                      <span class="info-value">{{ country.capital }}</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <span class="info-icon">👥</span>
                    <div class="info-text">
                      <span class="info-label">人口</span>
                      <span class="info-value">{{ country.population }}</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <span class="info-icon">🗣️</span>
                    <div class="info-text">
                      <span class="info-label">官方语言</span>
                      <span class="info-value">{{ country.language }}</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <span class="info-icon">💰</span>
                    <div class="info-text">
                      <span class="info-label">货币</span>
                      <span class="info-value">{{ country.currency }}</span>
                    </div>
                  </div>
                </div>
                <div class="culture-section">
                  <h3 class="section-title">🎭 特色文化</h3>
                  <p class="culture-text">{{ country.culture }}</p>
                </div>
                <div class="highlights-section">
                  <h3 class="section-title">🏆 热门景点</h3>
                  <div class="highlights-list">
                    <span
                      v-for="(h, i) in country.highlights"
                      :key="i"
                      class="highlight-tag"
                    >{{ h }}</span>
                  </div>
                </div>
              </div>
            </template>
            <template v-else-if="continent">
              <div class="panel-header continent-header">
                <div
                  class="continent-color-dot"
                  :style="{ backgroundColor: continentColorStyle }"
                ></div>
                <div>
                  <h2 class="panel-title">{{ continent.name }}</h2>
                  <p class="panel-subtitle">{{ continent.nameEn }}</p>
                </div>
              </div>
              <div class="panel-content">
                <div class="info-grid">
                  <div class="info-item">
                    <span class="info-icon">📐</span>
                    <div class="info-text">
                      <span class="info-label">面积</span>
                      <span class="info-value">{{ continent.area }}</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <span class="info-icon">🌍</span>
                    <div class="info-text">
                      <span class="info-label">国家数量</span>
                      <span class="info-value">{{ continent.countries }} 个</span>
                    </div>
                  </div>
                  <div class="info-item">
                    <span class="info-icon">👥</span>
                    <div class="info-text">
                      <span class="info-label">人口</span>
                      <span class="info-value">{{ continent.population }}</span>
                    </div>
                  </div>
                </div>
                <div class="culture-section">
                  <h3 class="section-title">📖 简介</h3>
                  <p class="culture-text">{{ continent.description }}</p>
                </div>
                <div class="highlights-section">
                  <h3 class="section-title">⭐ 代表国家</h3>
                  <div class="country-list">
                    <div
                      v-for="(h, i) in continent.highlights"
                      :key="i"
                      class="country-item"
                    >
                      <span class="country-dot"></span>
                      <span class="country-name">{{ h.name }}</span>
                    </div>
                  </div>
                </div>
                <button v-if="!isHover" class="explore-btn" @click="$emit('explore', continent)">
                  <span>探索更多国家</span>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="5" y1="12" x2="19" y2="12"/>
                    <polyline points="12 5 19 12 12 19"/>
                  </svg>
                </button>
              </div>
            </template>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  isDark: {
    type: Boolean,
    default: false
  },
  isHover: {
    type: Boolean,
    default: false
  },
  continent: {
    type: Object,
    default: null
  },
  country: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'explore'])

const isCountry = computed(() => !!props.country)

const continentColorStyle = computed(() => {
  if (!props.continent?.color) return '#4a9eff'
  const c = props.continent.color
  return `rgba(${Math.floor(c[0] * 255)}, ${Math.floor(c[1] * 255)}, ${Math.floor(c[2] * 255)}, ${c[3] || 1})`
})

function handleMaskClick() {
  emit('close')
}
</script>

<style scoped>
.info-panel-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  z-index: 1000;
  padding: 20px;
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.info-panel {
  width: 380px;
  max-height: 85vh;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: saturate(180%) blur(30px);
  -webkit-backdrop-filter: saturate(180%) blur(30px);
  border-radius: 18px;
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.18);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.info-panel.is-dark {
  background: rgba(28, 28, 30, 0.92);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.info-panel.is-hover {
  border: 1px solid rgba(0, 122, 255, 0.2);
  box-shadow:
    0 8px 30px rgba(0, 122, 255, 0.08),
    0 20px 60px rgba(0, 0, 0, 0.15);
}

.info-panel.is-dark.is-hover {
  border-color: rgba(10, 132, 255, 0.25);
  box-shadow:
    0 8px 30px rgba(10, 132, 255, 0.12),
    0 20px 60px rgba(0, 0, 0, 0.5);
}

.hover-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 4px 12px;
  background: rgba(0, 122, 255, 0.12);
  border-radius: 980px;
  font-size: 11px;
  font-weight: 600;
  color: #007aff;
  letter-spacing: 0.3px;
  z-index: 10;
}

.is-dark .hover-badge {
  background: rgba(10, 132, 255, 0.2);
  color: #0a84ff;
}

.info-panel-mask.hover-mode {
  background: transparent;
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
  pointer-events: none;
}

.info-panel-mask.hover-mode .info-panel {
  pointer-events: auto;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 30px;
  height: 30px;
  border: none;
  background: rgba(120, 120, 128, 0.12);
  border-radius: 50%;
  color: #86868b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  z-index: 10;
}

.is-dark .close-btn {
  background: rgba(255, 255, 255, 0.1);
  color: #98989d;
}

.close-btn:hover {
  background: rgba(255, 59, 48, 0.15);
  color: #ff3b30;
  transform: rotate(90deg);
}

.close-btn svg {
  width: 14px;
  height: 14px;
}

.panel-header {
  padding: 32px 24px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.is-dark .panel-header {
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

.continent-header {
  padding-top: 34px;
}

.flag-emoji {
  font-size: 44px;
  line-height: 1;
}

.continent-color-dot {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.panel-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.3px;
  line-height: 1.2;
}

.is-dark .panel-title {
  color: #f5f5f7;
}

.panel-subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6e6e73;
  letter-spacing: 0.2px;
}

.is-dark .panel-subtitle {
  color: #86868b;
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px 24px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px;
  background: #f5f5f7;
  border-radius: 12px;
  transition: background 0.2s ease;
}

.is-dark .info-item {
  background: rgba(255, 255, 255, 0.06);
}

.info-icon {
  font-size: 18px;
  line-height: 1;
  flex-shrink: 0;
}

.info-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.info-label {
  font-size: 11px;
  color: #6e6e73;
  letter-spacing: 0.2px;
}

.is-dark .info-label {
  color: #86868b;
}

.info-value {
  font-size: 13px;
  font-weight: 600;
  color: #1d1d1f;
  word-break: break-word;
  line-height: 1.3;
}

.is-dark .info-value {
  color: #f5f5f7;
}

.section-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.1px;
}

.is-dark .section-title {
  color: #f5f5f7;
}

.culture-section {
  margin-bottom: 24px;
}

.culture-text {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #1d1d1f;
}

.is-dark .culture-text {
  color: #f5f5f7;
}

.highlights-section {
  margin-bottom: 20px;
}

.highlights-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.highlight-tag {
  padding: 6px 14px;
  background: #f5f5f7;
  border-radius: 980px;
  font-size: 12px;
  font-weight: 500;
  color: #1d1d1f;
  transition: background 0.2s ease;
}

.is-dark .highlight-tag {
  background: rgba(255, 255, 255, 0.08);
  color: #f5f5f7;
}

.country-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.country-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.country-item:hover {
  background: #f5f5f7;
}

.is-dark .country-item:hover {
  background: rgba(255, 255, 255, 0.06);
}

.country-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #007aff;
  flex-shrink: 0;
}

.country-name {
  font-size: 13px;
  font-weight: 500;
  color: #1d1d1f;
}

.is-dark .country-name {
  color: #f5f5f7;
}

.explore-btn {
  width: 100%;
  padding: 12px;
  border: none;
  background: #007aff;
  color: #fff;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: -0.1px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  margin-top: 8px;
  font-family: inherit;
}

.explore-btn:hover {
  background: #0071e3;
}

.explore-btn:active {
  transform: scale(0.98);
  background: #006edc;
}

.explore-btn svg {
  width: 14px;
  height: 14px;
  transition: transform 0.2s ease;
}

.explore-btn:hover svg {
  transform: translateX(3px);
}

.panel-fade-enter-active,
.panel-fade-leave-active {
  transition: opacity 0.3s ease;
}

.panel-fade-enter-from,
.panel-fade-leave-to {
  opacity: 0;
}

.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: transform 0.35s cubic-bezier(0.16, 1, 0.3, 1);
}

.panel-slide-enter-from,
.panel-slide-leave-to {
  transform: translateX(100%);
}

@media (max-width: 480px) {
  .info-panel {
    width: 100%;
    max-height: 90vh;
    border-radius: 16px 16px 0 0;
    align-self: flex-end;
  }
  .info-panel-mask {
    padding: 0;
    align-items: flex-end;
  }
}
</style>
