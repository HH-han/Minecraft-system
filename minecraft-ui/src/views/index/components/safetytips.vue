<template>
  <div class="safety-tips">
    <div class="section-header">
      <h2 class="section-title">安全知识提示</h2>
      <p class="section-description">出行必备的安全指南</p>
    </div>

    <div class="tips-grid">
      <div v-for="tip in safetyTips" :key="tip.id" class="tips-card" @click="openTipDetail(tip)">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import safetyTipsApi from '@/api/safetytips';

const emit = defineEmits(['open-safety-detail']);

const safetyTips = ref([]);

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
.safety-tips {
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 40px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: -0.01em;
}

.section-description {
  font-size: 18px;
  color: #6e6e73;
  margin: 0;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.tips-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: #ffffff;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  border: 1px solid #f5f5f7;
}

.tips-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border-color: #e8e8ed;
}

.tips-icon-wrapper {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fef3c7;
  border-radius: 16px;
  flex-shrink: 0;
}

.tips-content {
  flex: 1;
  min-width: 0;
}

.tips-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 6px;
  color: #1d1d1f;
}

.tips-desc {
  font-size: 14px;
  line-height: 1.5;
  color: #6e6e73;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tips-arrow {
  color: #999;
  flex-shrink: 0;
  transition: transform 0.3s;
}

.tips-card:hover .tips-arrow {
  transform: translateX(4px);
  color: #2997ff;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
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
  background: #ffffff;
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
  border-bottom: 1px solid #f5f5f7;
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.close-btn {
  background: #f5f5f7;
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
  background: #e8e8ed;
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
  color: #6e6e73;
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
  .section-title {
    font-size: 28px;
  }
  
  .tips-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .tips-card {
    padding: 20px;
    border-radius: 16px;
  }
  
  .tips-icon-wrapper {
    width: 48px;
    height: 48px;
  }
  
  .tips-icon-wrapper svg {
    width: 40px;
    height: 40px;
  }
  
  .tips-title {
    font-size: 16px;
  }
  
  .modal-content {
    border-radius: 24px;
    margin: 20px;
  }
  
  .modal-header {
    padding: 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .tips-card {
    padding: 16px;
    border-radius: 12px;
  }
  
  .tips-icon-wrapper {
    width: 40px;
    height: 40px;
  }
  
  .tips-icon-wrapper svg {
    width: 32px;
    height: 32px;
  }
  
  .tips-title {
    font-size: 16px;
  }
  
  .tips-desc {
    font-size: 13px;
  }
}
</style>