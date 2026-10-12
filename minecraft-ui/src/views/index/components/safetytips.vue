<template>
  <div class="safety-tips">
    <div class="section-header">
      <h2 class="section-title">安全知识提示</h2>
      <p class="section-description">出行必备的安全指南</p>
    </div>

    <div class="tips-grid">
      <div
        v-for="(tip, index) in displayedTips"
        :key="tip.id"
        class="tips-card"
        :class="{ 'tips-card--expanded': isExpanded && index >= DEFAULT_DISPLAY_COUNT }"
        :style="{ animationDelay: `${Math.min(index, DEFAULT_DISPLAY_COUNT) * 0.05}s` }"
        @click="openTipDetail(tip)"
      >
        <div class="tips-icon-wrapper">
          <svg t="1776522975294" class="icon" viewBox="0 0 1024 1024" version="1.1"
              xmlns="http://www.w3.org/2000/svg" p-id="6403" width="48" height="48">
            <path
              d="M893.359817 142.982769c-141.157457 0-270.146168-48.674985-360.19489-133.856209-12.168746-12.168746-31.63874-12.168746-43.807487 0-87.614973 85.181224-219.037433 133.856209-360.19489 133.856209-17.036245 0-31.63874 14.602496-31.63874 31.63874V561.587641c0 124.121212 65.71123 238.507427 172.796197 309.086156l223.904931 146.024955c4.867499 2.433749 12.168746 4.867499 17.036245 4.867499 4.867499 0 12.168746-2.433749 17.036245-4.867499l223.904932-146.024955C861.721076 800.095068 924.998557 683.275104 924.998557 561.587641V174.621509c0-17.036245-14.602496-31.63874-31.63874-31.63874zM569.671165 668.672608c0 21.903743-24.337493 46.241236-58.409982 46.241236-36.506239 0-58.409982-24.337493-58.409982-46.241236 0-31.63874 36.506239-53.542484 36.506239-99.783719 0-58.409982-51.108734-138.723708-51.108734-189.832442 0-38.939988 29.204991-65.71123 73.012477-65.71123s73.012478 26.771242 73.012478 65.71123c0 48.674985-51.108734 131.42246-51.108734 189.832442 2.433749 51.108734 36.506239 65.71123 36.506238 99.783719z"
              fill="#F5B53A" p-id="6404"></path>
          </svg>
        </div>
        <div class="tips-content">
          <h3 class="tips-title">{{ tip.title }}</h3>
          <p class="tips-desc">{{ tip.description }}</p>
        </div>
        <div class="tips-arrow">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="1.5">
            <line x1="5" y1="12" x2="19" y2="12"/>
            <polyline points="12 5 19 12 12 19"/>
          </svg>
        </div>
      </div>
    </div>

    <div v-if="hasMoreData" class="expand-actions">
      <button class="expand-btn" @click="toggleExpand">
        <span>{{ isExpanded ? '收起' : '展开全部' }}</span>
        <svg
          class="expand-arrow"
          :class="{ 'expand-arrow--up': isExpanded }"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <polyline points="6 9 12 15 18 9" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import safetyTipsApi from '@/api/safetytips';

const emit = defineEmits(['open-safety-detail']);

// 默认展示的数据条数
const DEFAULT_DISPLAY_COUNT = 4;

const safetyTips = ref([]);
const isExpanded = ref(false);

// 默认仅展示前若干条，展开后显示全部
const displayedTips = computed(() => {
    if (isExpanded.value || safetyTips.value.length <= DEFAULT_DISPLAY_COUNT) {
        return safetyTips.value;
    }
    return safetyTips.value.slice(0, DEFAULT_DISPLAY_COUNT);
});

// 是否存在折叠数据（用于控制按钮显示）
const hasMoreData = computed(() => safetyTips.value.length > DEFAULT_DISPLAY_COUNT);

const toggleExpand = () => {
    isExpanded.value = !isExpanded.value;
};

const fetchSafetyTips = async () => {
    try {
        const response = await safetyTipsApi.getActiveSafetyTips();
        safetyTips.value = response.data || [];
    } catch (error) {
        console.error('获取安全知识提示失败:', error);
    }
};

const openTipDetail = (tip) => {
    emit('open-safety-detail', tip);
};

onMounted(fetchSafetyTips);
</script>

<style scoped>
/* 深色电影感主题：设计变量由父级 .home-main 统一提供（main.vue） */
.safety-tips {
  margin: 0;
}

.section-header {
  text-align: left;
  margin-bottom: 28px;
}

.section-title {
  font-size: clamp(22px, 2.4vw, 30px);
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: -0.03em;
  color: var(--wi-txt-1, var(--theme-text-1));
}

.section-description {
  font-size: 14px;
  color: var(--wi-txt-2, var(--theme-text-2));
  margin: 0;
}

.tips-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

.tips-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 20px 22px;
  border-radius: 22px;
  cursor: pointer;
  opacity: 0;
  transform: translateY(30px);
  animation: fadeInUp 0.6s ease-out forwards;
  background: var(--theme-bg-surface);
  border: 1px solid var(--wi-hairline, var(--theme-hairline));
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  transition: transform 0.5s cubic-bezier(0.22, 1, 0.36, 1),
    border-color 0.5s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.5s cubic-bezier(0.22, 1, 0.36, 1);
}

/* 顶部玻璃高光 */
.tips-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--theme-border-strong), transparent);
  opacity: 0.7;
  pointer-events: none;
}

.tips-card:hover {
  transform: translateY(-4px);
  border-color: var(--theme-primary);
  box-shadow: var(--theme-shadow-lg), 0 0 0 1px var(--theme-primary-soft);
}

/* 展开时新增卡片使用更轻量的淡入动画 */
.tips-card--expanded {
  animation: fadeInExpand 0.5s ease-out forwards;
}

@keyframes fadeInExpand {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.tips-icon-wrapper {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: 16px;
  background: var(--theme-warning-soft);
  border: 1px solid var(--theme-warning);
  box-shadow: var(--theme-shadow-md);
}

.tips-icon-wrapper svg {
  width: 34px;
  height: 34px;
}

.tips-content {
  flex: 1;
  min-width: 0;
}

.tips-title {
  font-size: 16.5px;
  font-weight: 600;
  margin: 0 0 6px;
  color: var(--wi-txt-1, var(--theme-text-1));
}

.tips-desc {
  font-size: 13.5px;
  line-height: 1.65;
  color: var(--wi-txt-2, var(--theme-text-2));
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tips-arrow {
  flex-shrink: 0;
  color: var(--wi-txt-3, var(--theme-text-3));
  transition: transform 0.35s cubic-bezier(0.22, 1, 0.36, 1), color 0.35s cubic-bezier(0.22, 1, 0.36, 1);
}

.tips-card:hover .tips-arrow {
  transform: translateX(4px);
  color: var(--wi-azure, var(--theme-primary));
}

.expand-actions {
  display: flex;
  justify-content: center;
  margin-top: 36px;
}

.expand-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 13px 34px;
  border-radius: 980px;
  font-size: 14.5px;
  font-weight: 500;
  color: var(--wi-txt-1, var(--theme-text-1));
  background: var(--theme-bg-surface);
  border: 1px solid var(--wi-hairline-2, var(--theme-border-strong));
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
}

.expand-btn:hover {
  transform: translateY(-2px);
  background: var(--theme-primary-soft);
  border-color: var(--theme-primary);
  box-shadow: var(--theme-shadow-md);
}

.expand-btn:active {
  transform: translateY(0);
}

.expand-arrow {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.expand-arrow--up {
  transform: rotate(180deg);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--theme-overlay);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  width: 90%;
  max-width: 500px;
  background: var(--theme-bg-elevated);
  border-radius: 28px;
  overflow: hidden;
  animation: modalSlideUp 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid var(--theme-hairline);
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.close-btn {
  background: var(--theme-bg-surface-2);
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.close-btn:hover {
  background: var(--theme-bg-hover);
}

.modal-body {
  padding: 24px;
}

.tip-image {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 20px;
}

.tip-image img {
  width: 100%;
  height: auto;
  display: block;
}

.tip-description {
  font-size: 16px;
  line-height: 1.6;
  color: var(--theme-text-2);
  margin: 0;
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from {
  opacity: 0;
}

.modal-enter-from .modal-content {
  transform: scale(0.9) translateY(20px);
  opacity: 0;
}

.modal-leave-to {
  opacity: 0;
}

.modal-leave-to .modal-content {
  transform: scale(0.95) translateY(10px);
  opacity: 0;
}

@media (max-width: 768px) {
  .tips-card {
    padding: 18px;
  }

  .tips-icon-wrapper {
    width: 46px;
    height: 46px;
  }

  .tips-icon-wrapper svg {
    width: 30px;
    height: 30px;
  }

  .tips-title {
    font-size: 15.5px;
  }

  .expand-actions {
    margin-top: 28px;
  }
}
</style>
