<template>
    <div class="safety-tips">
        <h2 class="section-title">安全知识提示</h2>
        <div class="tips-grid">
            <div v-for="tip in safetyTips" :key="tip.id" class="tips-item" @click="openTipDetail(tip)">
                <div class="tips-icon">
                    <i class="el-icon-warning-outline"></i>
                </div>
                <div class="tips-body">
                    <div class="tips-image" v-if="tip.imageUrl">
                        <img :src="tip.imageUrl" :alt="tip.title" />
                    </div>
                    <div class="tips-content">
                        <h3 class="tips-title">{{ tip.title }}</h3>
                        <p class="tips-desc">{{ tip.description }}</p>
                    </div>
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
    padding: 60px 40px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
    border-radius: 20px;
    margin: 40px 0;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
    text-align: center;
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 40px;
    color: #fff;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    position: relative;
    padding-bottom: 15px;
}

.section-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 3px;
    background: linear-gradient(90deg, #ff4757, #ff6b81);
    border-radius: 2px;
}

.tips-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 24px;
    max-width: 1200px;
    margin: 0 auto;
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
    font-size: 28px;
    color: #ff4757;
    margin-top: 4px;
    flex-shrink: 0;
}

.tips-body {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.tips-image {
    width: 100%;
    border-radius: 8px;
    overflow: hidden;
    aspect-ratio: 16/9;
    background: rgba(255, 255, 255, 0.05);
}

.tips-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

.tips-content {
    flex: 1;
}

.tips-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 8px;
    color: #fff;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.tips-desc {
    font-size: 14px;
    line-height: 1.5;
    color: rgba(255, 255, 255, 0.8);
    margin: 0;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
}

/* 模态框样式 */
.modal-overlay {
position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    padding: 20px;
}

.modal-content {
    width: 90%;
    max-width: 600px;
    max-height: 80vh;
    overflow-y: auto;
    border-radius: 16px;
    padding: 24px;
    position: relative;
    box-sizing: border-box;
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
    margin-top: 20px;
}

.meta-item {
    background: rgba(255, 255, 255, 0.1);
    padding: 4px 12px;
    border-radius: 12px;
    flex-shrink: 0;
}

/* 模态框滚动条样式 */
.modal-content::-webkit-scrollbar {
    width: 6px;
}

.modal-content::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .safety-tips {
        padding: 40px 20px;
        margin: 20px 0;
    }

    .section-title {
        font-size: 24px;
        margin-bottom: 30px;
    }

    .tips-grid {
        grid-template-columns: 1fr;
        gap: 20px;
    }

    .tips-item {
        padding: 16px;
    }

    .tips-icon {
        font-size: 24px;
    }

    .tips-title {
        font-size: 16px;
    }

    .modal-overlay {
        padding: 20px;
    }

    .modal-content {
        width: 100%;
        padding: 20px;
        max-height: 90vh;
    }
}

@media (max-width: 480px) {
    .safety-tips {
        padding: 30px 15px;
    }

    .section-title {
        font-size: 22px;
        margin-bottom: 25px;
    }

    .tips-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }

    .tips-icon {
        margin-top: 0;
    }

    .modal-overlay {
        padding: 15px;
    }

    .modal-content {
        padding: 16px;
    }

    .modal-header h2 {
        font-size: 18px;
    }

    .tip-description {
        font-size: 14px;
    }
}
</style>