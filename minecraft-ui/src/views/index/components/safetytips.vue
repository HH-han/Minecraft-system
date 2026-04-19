<template>
    <div class="safety-tips">
        <h2 class="section-title">安全知识提示</h2>
        <div class="tips-grid">
            <div v-for="tip in safetyTips" :key="tip.id" class="tips-item" @click="openTipDetail(tip)">
                <div class="tips-icon">
                    <svg t="1776522975294" class="icon" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="6403" width="256" height="256">
                        <path
                            d="M893.359817 142.982769c-141.157457 0-270.146168-48.674985-360.19489-133.856209-12.168746-12.168746-31.63874-12.168746-43.807487 0-87.614973 85.181224-219.037433 133.856209-360.19489 133.856209-17.036245 0-31.63874 14.602496-31.63874 31.63874V561.587641c0 124.121212 65.71123 238.507427 172.796197 309.086156l223.904931 146.024955c4.867499 2.433749 12.168746 4.867499 17.036245 4.867499 4.867499 0 12.168746-2.433749 17.036245-4.867499l223.904932-146.024955C861.721076 800.095068 924.998557 683.275104 924.998557 561.587641V174.621509c0-17.036245-14.602496-31.63874-31.63874-31.63874zM569.671165 668.672608c0 21.903743-24.337493 46.241236-58.409982 46.241236-36.506239 0-58.409982-24.337493-58.409982-46.241236 0-31.63874 36.506239-53.542484 36.506239-99.783719 0-58.409982-51.108734-138.723708-51.108734-189.832442 0-38.939988 29.204991-65.71123 73.012477-65.71123s73.012478 26.771242 73.012478 65.71123c0 48.674985-51.108734 131.42246-51.108734 189.832442 2.433749 51.108734 36.506239 65.71123 36.506238 99.783719z"
                            fill="#F5B53A" p-id="6404"></path>
                    </svg>
                    <span>{{ tip.title }}</span>
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
        <transition name="modal">
            <div v-if="showModal" class="modal-overlay" @click.self="closeTipDetail">
                <div class="modal-content" @click.stop>
                    <div class="modal-header">
                        <h2>{{ selectedTip?.title }}</h2>
                        <button class="close-btn" @click="closeTipDetail">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                <line x1="6" y1="6" x2="18" y2="18"></line>
                            </svg>
                        </button>
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
        </transition>
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
    flex-direction: column;
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
    box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

.tips-icon {
    font-size: 24px;
    color: #ff4757;
    margin-top: 4px;
    flex-shrink: 0;
}

.tips-icon svg {
    width: 28px;
    height: 28px;
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
    backdrop-filter: blur(10px);
}

.modal-content {
    width: 90%;
    max-width: 600px;
    max-height: 80vh;
    overflow-y: auto;
    background: #ffffff;
    border-radius: 24px;
    padding: 32px 24px 24px;
    position: relative;
    box-sizing: border-box;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    /* 隐藏滚动条 */
    -ms-overflow-style: none;
    scrollbar-width: none;
}

/* 隐藏WebKit浏览器的滚动条 */
.modal-content::-webkit-scrollbar {
    display: none;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #d2d2d6;
}

.modal-header h2 {
    font-size: 24px;
    font-weight: 700;
    color: #1d1d1f;
    margin: 0;
    line-height: 1.1;
}

.close-btn {
    background: #f5f5f7;
    border: 1px solid #d2d2d6;
    color: #1d1d1f;
    cursor: pointer;
    padding: 8px;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.3s ease;
}

.close-btn:hover {
    background: #e8e8ed;
    border-color: #c7c7cc;
    transform: scale(1.05);
}

.close-btn svg {
    width: 20px;
    height: 20px;
}

.modal-body {
    color: #1d1d1f;
}

.tip-image {
    margin-bottom: 24px;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.tip-image img {
    width: 100%;
    height: auto;
    display: block;
    transition: transform 0.3s ease;
}

.tip-image:hover img {
    transform: scale(1.02);
}

.tip-description {
    font-size: 16px;
    line-height: 1.6;
    margin-bottom: 24px;
    color: #6e6e73;
}

.tip-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    font-size: 14px;
    color: #6e6e73;
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid #d2d2d6;
}

.meta-item {
    background: #f5f5f7;
    padding: 6px 14px;
    border-radius: 16px;
    flex-shrink: 0;
    transition: all 0.2s ease;
}

.meta-item:hover {
    background: #e8e8ed;
    transform: translateY(-2px);
}

/* 模态框动画 */
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
        padding: 24px 20px 20px;
        max-height: 90vh;
        border-radius: 20px;
    }

    .modal-header {
        margin-bottom: 20px;
        padding-bottom: 12px;
    }

    .modal-header h2 {
        font-size: 20px;
    }

    .close-btn {
        width: 32px;
        height: 32px;
        padding: 6px;
    }

    .close-btn svg {
        width: 18px;
        height: 18px;
    }

    .tip-image {
        margin-bottom: 20px;
        border-radius: 12px;
    }

    .tip-description {
        font-size: 14px;
        margin-bottom: 20px;
    }

    .tip-meta {
        margin-top: 20px;
        padding-top: 16px;
        gap: 10px;
    }

    .meta-item {
        padding: 4px 12px;
        border-radius: 14px;
        font-size: 12px;
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