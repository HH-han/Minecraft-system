<template>
    <div class="safety-tips">
        <h2 class="section-title">安全知识提示</h2>
        <div class="tips-grid">
            <div 
                v-for="tip in safetyTips" 
                :key="tip.id" 
                class="tips-item" 
                @click="openTipDetail(tip)"
            >
                <div class="tips-icon">
                    <i class="el-icon-warning-outline"></i>
                </div>
                <div class="tips-content">
                    <h3 class="tips-title">{{ tip.title }}</h3>
                    <p class="tips-desc">{{ tip.description }}</p>
                </div>
            </div>
        </div>

        <!-- 安全提示详情模态框 -->
        <div v-if="showModal" class="modal-overlay" @click.self="closeTipDetail">
            <div class="modal-content glass-card">
                <div class="modal-header">
                    <h2>{{ selectedTip?.title }}</h2>
                    <button class="close-btn" @click="closeTipDetail">×</button>
                </div>
                <div class="modal-body">
                    <div class="tip-image" v-if="selectedTip?.imageUrl">
                        <img :src="selectedTip.imageUrl" :alt="selectedTip.title" />
                    </div>
                    <div class="tip-description">
                        {{ selectedTip?.description }}
                    </div>
                    <div class="tip-meta">
                        <span class="meta-item">分类ID: {{ selectedTip?.categoryId }}</span>
                        <span class="meta-item">排序权重: {{ selectedTip?.sortWeight }}</span>
                        <span class="meta-item">状态: {{ selectedTip?.status === 1 ? '启用' : '禁用' }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import safetyTipsApi from '@/api/safetytips';

const safetyTips = ref([]);
const showModal = ref(false);
const selectedTip = ref(null);

// 获取安全知识提示数据
const fetchSafetyTips = async () => {
  try {
    const response = await safetyTipsApi.getActiveSafetyTips();
    safetyTips.value = response.data || [];
  } catch (error) {
    console.error('获取安全知识提示失败:', error);
  }
};

// 打开安全提示详情
const openTipDetail = (tip) => {
  selectedTip.value = tip;
  showModal.value = true;
};

// 关闭安全提示详情
const closeTipDetail = () => {
  showModal.value = false;
  selectedTip.value = null;
};

onMounted(fetchSafetyTips);
</script>

<style scoped>
.safety-tips {
    padding: 40px 20px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
    border-radius: 16px;
    margin: 20px 0;
}

.section-title {
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 30px;
    color: #fff;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.tips-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
}

.tips-item {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    cursor: pointer;
    transition: all 0.3s ease;
}

.tips-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
    background: rgba(255, 255, 255, 0.15);
}

.tips-icon {
    font-size: 24px;
    color: #ff4757;
    margin-top: 4px;
}

.tips-content {
    flex: 1;
}

.tips-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 8px;
    color: #fff;
}

.tips-desc {
    font-size: 14px;
    line-height: 1.5;
    color: rgba(255, 255, 255, 0.8);
    margin: 0;
}

/* 模态框样式 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-content {
    width: 90%;
    max-width: 600px;
    max-height: 80vh;
    overflow-y: auto;
    border-radius: 16px;
    padding: 24px;
}

.glass-card {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(15px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.modal-header h2 {
    font-size: 20px;
    font-weight: 600;
    color: #fff;
    margin: 0;
}

.close-btn {
    background: none;
    border: none;
    font-size: 24px;
    color: #fff;
    cursor: pointer;
    padding: 0;
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: background 0.3s;
}

.close-btn:hover {
    background: rgba(255, 255, 255, 0.1);
}

.modal-body {
    color: #fff;
}

.tip-image {
    margin-bottom: 20px;
    border-radius: 8px;
    overflow: hidden;
}

.tip-image img {
    width: 100%;
    height: auto;
    display: block;
}

.tip-description {
    font-size: 16px;
    line-height: 1.6;
    margin-bottom: 20px;
    color: rgba(255, 255, 255, 0.9);
}

.tip-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.7);
}

.meta-item {
    background: rgba(255, 255, 255, 0.1);
    padding: 4px 12px;
    border-radius: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .safety-tips {
        padding: 20px 10px;
    }
    
    .section-title {
        font-size: 20px;
    }
    
    .tips-grid {
        grid-template-columns: 1fr;
    }
    
    .modal-content {
        width: 95%;
        padding: 20px;
    }
}
</style>