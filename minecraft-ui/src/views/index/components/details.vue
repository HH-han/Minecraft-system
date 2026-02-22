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
            <div class="modal-footer">
                <button class="secondary-button" @click="closeModal">关闭</button>
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
    background-color: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(10px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: white;
    border-radius: 15px;
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
    width: 90%;
    max-width: 800px;
    max-height: 90vh;
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
    border-bottom: 1px solid #e0e0e0;
}

.modal-title {
    font-size: 20px;
    font-weight: 600;
    margin: 0;
    color: #333;
}

.close-button {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #999;
    padding: 0;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.3s ease;
}

.close-button:hover {
    background-color: #f0f0f0;
    color: #333;
}

.modal-body {
    padding: 20px;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    padding: 20px;
    border-top: 1px solid #e0e0e0;
}

.loading {
    text-align: center;
    padding: 40px;
    color: #666;
}

.error {
    text-align: center;
    padding: 40px;
    color: #ff4d4f;
}

/* 详情通用样式 */
.details-image {
    width: 100%;
    height: 300px;
    overflow: hidden;
    margin-bottom: 20px;
    border-radius: 8px;
}

.details-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.details-title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 15px;
    color: #333;
}

.details-meta {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    color: #999;
    margin-bottom: 20px;
}

.details-content {
    font-size: 16px;
    line-height: 1.6;
    color: #666;
}

/* 按钮样式 */
.secondary-button {
    background-color: transparent;
    border: 1px solid #d9d9d9;
    color: #333;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s ease;
}

.secondary-button:hover {
    border-color: #1677ff;
    color: #1677ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .modal-content {
        width: 95%;
        margin: 20px;
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