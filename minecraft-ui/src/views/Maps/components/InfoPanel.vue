<template>
  <Teleport to="body">
    <Transition name="panel-fade">
      <div v-if="visible" class="info-panel-mask" :class="{ 'hover-mode': isHover }" @click="handleMaskClick">
        <Transition name="panel-slide">
          <div
            v-if="visible"
            class="info-panel"
            :class="{ 'is-country': isCountry, 'is-hover': isHover }"
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
  background: var(--theme-overlay);
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
  background: var(--theme-bg-glass-strong);
  backdrop-filter: saturate(180%) blur(30px);
  -webkit-backdrop-filter: saturate(180%) blur(30px);
  border-radius: 18px;
  box-shadow: var(--theme-shadow-lg);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.info-panel.is-hover {
  border: 1px solid var(--theme-primary);
  box-shadow: var(--theme-shadow-lg);
}

.hover-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 4px 12px;
  background: var(--theme-primary-soft);
  border-radius: 980px;
  font-size: 11px;
  font-weight: 600;
  color: var(--theme-primary);
  letter-spacing: 0.3px;
  z-index: 10;
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
  background: var(--theme-bg-active);
  border-radius: 50%;
  color: var(--theme-text-3);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
  z-index: 10;
}

.close-btn:hover {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
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
  border-bottom: 1px solid var(--theme-border);
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
  box-shadow: var(--theme-shadow-sm);
}

.panel-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.3px;
  line-height: 1.2;
}

.panel-subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--theme-text-2);
  letter-spacing: 0.2px;
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
  background: var(--theme-bg-surface-2);
  border-radius: 12px;
  transition: background 0.2s ease;
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
  color: var(--theme-text-3);
  letter-spacing: 0.2px;
}

.info-value {
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-text-1);
  word-break: break-word;
  line-height: 1.3;
}

.section-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--theme-text-1);
  letter-spacing: -0.1px;
}

.culture-section {
  margin-bottom: 24px;
}

.culture-text {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: var(--theme-text-1);
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
  background: var(--theme-bg-surface-2);
  border-radius: 980px;
  font-size: 12px;
  font-weight: 500;
  color: var(--theme-text-1);
  transition: background 0.2s ease;
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
  background: var(--theme-bg-hover);
}

.country-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--theme-primary);
  flex-shrink: 0;
}

.country-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--theme-text-1);
}

.explore-btn {
  width: 100%;
  padding: 12px;
  border: none;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
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
  background: var(--theme-primary-hover);
}

.explore-btn:active {
  transform: scale(0.98);
  background: var(--theme-primary-hover);
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
