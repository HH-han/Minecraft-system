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
            <div class="modal-content" @click.stop>
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
    padding: 0;
}

.section-title {
    text-align: center;
    font-size: 28px;
    font-weight: 700;
    margin-bottom: 40px;
    color: #1d1d1f;
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
    background: #2997ff;
    border-radius: 2px;
}

.tips-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 24px;
}

.tips-item {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    padding: 20px;
    background: #ffffff;
    border: 1px solid #d2d2d6;
    border-radius: 16px;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
}

.tips-item:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0,0,0,0.1);
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
    background: #f5f5f7;
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
    color: #1d1d1f;
}

.tips-desc {
    font-size: 14px;
    line-height: 1.5;
    color: #6e6e73;
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
    background: rgba(0, 0, 0, 0.5);
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
    background: #ffffff;
    border-radius: 20px;
    padding: 24px;
    position: relative;
    box-sizing: border-box;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
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
    color: #1d1d1f;
    margin: 0;
}

.close-btn {
    background: #f5f5f7;
    border: 1px solid #d2d2d6;
    font-size: 24px;
    color: #1d1d1f;
    cursor: pointer;
    padding: 0;
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.2s ease;
}

.close-btn:hover {
    background: #e8e8ed;
    border-color: #c7c7cc;
}

.modal-body {
    color: #1d1d1f;
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
    color: #6e6e73;
}

.tip-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    font-size: 14px;
    color: #6e6e73;
    margin-top: 20px;
}

.meta-item {
    background: #f5f5f7;
    padding: 4px 12px;
    border-radius: 12px;
    flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
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
        border-radius: 12px;
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
        border-radius: 16px;
    }

    .modal-header h2 {
        font-size: 18px;
    }

    .tip-description {
        font-size: 14px;
    }
}

@media (max-width: 480px) {
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
}
</style>