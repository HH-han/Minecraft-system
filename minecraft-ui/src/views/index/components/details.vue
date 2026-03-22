<template>
    <div v-if="visible" class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
            <div class="modal-header">
                <h2 class="modal-title">{{ title }}</h2>
                <button class="close-button" @click="closeModal">×</button>
            </div>
            <div class="modal-body">
                <div v-if="loading" class="loading">加载中...</div>
                <div v-else-if="error" class="error">{{ error }}</div>
                <div v-else>
                    <!-- 新闻详情 -->
                    <div v-if="type === 'news'" class="news-details">
                        <div class="details-image">
                            <img :src="data.image || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=news%20article%20placeholder%2C%20simple%20clean%20design&image_size=square'" alt="新闻图片">
                        </div>
                        <h3 class="details-title">{{ data.title }}</h3>
                        <div class="details-meta">
                            <span class="details-date">{{ formatDate(data.createTime) }}</span>
                            <span class="details-view">{{ data.viewCount || 0 }} 阅读</span>
                        </div>
                        <div class="details-content">{{ data.content }}</div>
                    </div>
                    
                    <!-- 酒店详情 -->
                    <div v-if="type === 'hotel'" class="hotel-details">
                        <div class="details-image">
                            <img :src="data.image || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20exterior%20placeholder%2C%20modern%20design&image_size=square'" alt="酒店图片">
                        </div>
                        <h3 class="details-title">{{ data.name }}</h3>
                        <div class="details-meta">
                            <span class="details-location">{{ data.city }} {{ data.address }}</span>
                            <span class="details-price">¥{{ data.price }}/晚</span>
                        </div>
                        <div class="details-content">{{ data.description }}</div>
                    </div>
                    
                    <!-- 美食详情 -->
                    <div v-if="type === 'food'" class="food-details">
                        <div class="details-image">
                            <img :src="data.image || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=delicious%20food%20placeholder%2C%20appetizing%20presentation&image_size=square'" alt="美食图片">
                        </div>
                        <h3 class="details-title">{{ data.name }}</h3>
                        <div class="details-meta">
                            <span class="details-location">{{ data.city }} {{ data.restaurant }}</span>
                            <span class="details-price">¥{{ data.price }}</span>
                        </div>
                        <div class="details-content">{{ data.description }}</div>
                    </div>
                    
                    <!-- 景点详情 -->
                    <div v-if="type === 'attraction'" class="attraction-details">
                        <div class="details-image">
                            <img :src="data.image || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tourist%20attraction%20placeholder%2C%20scenic%20view&image_size=square'" alt="景点图片">
                        </div>
                        <h3 class="details-title">{{ data.name }}</h3>
                        <div class="details-meta">
                            <span class="details-location">{{ data.city }} {{ data.address }}</span>
                            <span class="details-price">¥{{ data.ticketPrice || 0 }}</span>
                        </div>
                        <div class="details-content">{{ data.description }}</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// Props
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    type: {
        type: String,
        default: ''
    },
    data: {
        type: Object,
        default: () => ({})
    },
    loading: {
        type: Boolean,
        default: false
    },
    error: {
        type: String,
        default: ''
    }
});

// Emits
const emit = defineEmits(['close']);

// Computed
const title = computed(() => {
    const titles = {
        news: '新闻详情',
        hotel: '酒店详情',
        food: '美食详情',
        attraction: '景点详情'
    };
    return titles[props.type] || '详情';
});

// Methods
const closeModal = () => {
    emit('close');
};

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
};
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
    backdrop-filter: blur(15px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    border: 1px solid rgba(255, 255, 255, 0.18);
}

.modal-content {
    background: rgba(255, 255, 255, 0.25);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
    border: 1px solid rgba(255, 255, 255, 0.18);
    width: 90%;
    max-width: 800px;
    max-height: 80vh;
    overflow-y: auto;
    animation: slideIn 0.3s ease-in-out;
}

@keyframes slideIn {
    from {
        opacity: 0;
        transform: translateY(-20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-title {
    font-size: 20px;
    font-weight: 600;
    margin: 0;
    color: #333;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.close-button {
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.2);
    font-size: 24px;
    cursor: pointer;
    color: #333;
    padding: 0;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.3s ease;
    backdrop-filter: blur(10px);
}

.close-button:hover {
    background: rgba(255, 255, 255, 0.3);
    border-color: rgba(255, 255, 255, 0.3);
    color: #1677ff;
    transform: scale(1.1);
}

.modal-body {
    padding: 20px;
}

.loading {
    text-align: center;
    padding: 40px;
    color: #666;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.error {
    text-align: center;
    padding: 40px;
    color: #ff4d4f;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 详情通用样式 */
.details-image {
    width: 100%;
    height: 300px;
    overflow: hidden;
    margin-bottom: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.details-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.details-image:hover img {
    transform: scale(1.02);
}

.details-title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 15px;
    color: #333;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.details-meta {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    color: #666;
    margin-bottom: 20px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.details-content {
    font-size: 16px;
    line-height: 1.6;
    color: #555;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .modal-content {
        width: 95%;
        margin: 20px;
        background: rgba(255, 255, 255, 0.3);
        backdrop-filter: blur(15px);
    }
    
    .details-image {
        height: 200px;
    }
    
    .details-title {
        font-size: 18px;
    }
    
    .details-content {
        font-size: 14px;
    }
}
</style>