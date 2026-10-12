<template>
    <div class="raiders-app-container">
        <div class="raiders-header">
            <p>探索世界，分享旅程，与志同道合的旅行者一起出发</p>
            <div class="raiders-divider"></div>
        </div>
        <!-- 搜索区域 -->
        <div class="raiders-search-section">
            <div class="raiders-search-box">
                <input type="text" placeholder="搜索目的地，主题，游记...">
                <button><i class="fas fa-search"></i> 搜索</button>
            </div>

            <div class="raiders-search-tips">
                <span>巴厘岛攻略</span>
                <span>日本樱花季</span>
                <span>欧洲自驾游</span>
                <span>东南亚美食</span>
                <span>登山徒步</span>
                <span>亲子旅行</span>
            </div>
        </div>

        <!-- 地点切换 -->
        <div class="raiders-location-switch">
            <button class="raiders-location-btn" :class="{ 'raiders-active': currentLocation === 'domestic' }"
                @click="changeLocation('domestic')">
                <i class="fas fa-flag"></i> 国内游
            </button>
            <button class="raiders-location-btn" :class="{ 'raiders-active': currentLocation === 'abroad' }"
                @click="changeLocation('abroad')">
                <i class="fas fa-globe-asia"></i> 海外游
            </button>
        </div>

        <!-- 卡片内容展示 -->
        <div class="raiders-cards-container">
            <div v-for="(card, index) in filteredCards" :key="index" class="raiders-travel-card">
                <div class="raiders-card-image" :style="{ backgroundImage: `url(${card.image})` }">
                    <div class="raiders-card-tag">
                        <i :class="card.tagIcon"></i> {{ card.tag }}
                    </div>
                </div>

                <div class="raiders-card-content">
                    <h3 class="raiders-card-title">{{ card.title }}</h3>

                    <div class="raiders-card-info">
                        <div class="raiders-card-avatar" :style="{ backgroundImage: `url(${card.avatar})` }"></div>
                        <div class="raiders-card-stats">
                            已有 <strong>{{ card.members }}</strong> 人加入群组
                        </div>
                    </div>

                    <p class="raiders-card-description">{{ card.description }}</p>

                    <div class="raiders-card-actions">
                        <div class="raiders-card-price">
                            ¥{{ card.price }} <span>/人起</span>
                        </div>
                        <button class="raiders-join-btn" @click="showModal(card)">
                            立即入群 <i class="fas fa-arrow-right"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 群组信息模态框 -->
        <div class="raiders-modal-overlay" :class="{ 'raiders-active': showGroupModal }">
            <div class="raiders-modal-container">
                <div class="raiders-modal-header" :style="{ backgroundImage: `url(${currentGroup.image})` }">
                    <h2 class="raiders-modal-title">{{ currentGroup.title }}</h2>
                    <div class="raiders-modal-close" @click="closeModal">
                        <i class="fas fa-times"></i>
                    </div>
                </div>

                <div class="raiders-modal-content">
                    <div class="raiders-group-details">
                        <div class="raiders-group-info">
                            <div class="raiders-info-item">
                                <div class="raiders-info-icon">
                                    <i class="fas fa-users"></i>
                                </div>
                                <div class="raiders-info-content">
                                    <h3>群组名称</h3>
                                    <p>{{ currentGroup.groupName }}</p>
                                </div>
                            </div>

                            <div class="raiders-info-item">
                                <div class="raiders-info-icon">
                                    <i class="fas fa-info-circle"></i>
                                </div>
                                <div class="raiders-info-content">
                                    <h3>群组简介</h3>
                                    <p>{{ currentGroup.groupDescription }}</p>
                                </div>
                            </div>

                            <div class="raiders-info-item">
                                <div class="raiders-info-icon">
                                    <i class="fas fa-calendar-alt"></i>
                                </div>
                                <div class="raiders-info-content">
                                    <h3>行程日期</h3>
                                    <p>{{ currentGroup.dates }}</p>
                                </div>
                            </div>

                            <div class="raiders-info-item">
                                <div class="raiders-info-icon">
                                    <i class="fas fa-map-marked-alt"></i>
                                </div>
                                <div class="raiders-info-content">
                                    <h3>行程亮点</h3>
                                    <p>{{ currentGroup.highlights }}</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="raiders-members-section">
                        <h2>群组成员 ({{ currentGroup.members }})</h2>
                        <div class="raiders-members-grid">
                            <div v-for="(member, index) in groupMembers" :key="index" class="raiders-member-card">
                                <div class="raiders-member-avatar"
                                    :style="{ backgroundImage: `url(${member.avatar})` }"></div>
                                <div class="raiders-member-name">{{ member.name }}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="raiders-action-section">
                    <button class="raiders-confirm-btn">
                        确认加入群组 <i class="fas fa-check"></i>
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getGroupList } from '@/api/group';
import carouselApi from '@/api/carousel'

// 搜索关键词
const searchKeyword = ref('');

// 轮播图数据
const mediaList = ref({ images: []})
// 获取图片背景
const fetchcarousel = async () => {
  try {
    const result = await carouselApi.getCarouselsByGroup('gc')
    if (result && result.list) {
      const filteredList = result.list.filter(item => item.type === 'gc')
      mediaList.value.images = filteredList.map((item) => ({
        image: item.image || '默认图片链接',
        title: item.title || '默认标题',
        location: item.location || '默认位置',
        description: item.description || '默认描述'
      }))
    }
  } catch (error) {
    console.error('获取轮播图数据失败：', error)
  }
}
const currentSlide = ref(0);

// 卡片数据
const travelCards = ref([]);
// 获取数据
const fetchCards = async () => {
    try {
        const params = {
            pageNum: 1,
            pageSize: 10
        };
        const response = await getGroupList(params);
        if (response.data && response.data.records) {
            // 转换数据结构以匹配模板期望
            travelCards.value = response.data.records.map(group => ({
                id: group.id,
                title: group.name,
                image: group.images ? group.images.split(',')[0] : '默认图片链接',
                avatar: 'https://randomuser.me/api/portraits/men/32.jpg', // 团队头像，使用默认值
                members: group.currentMembers || 0,
                description: group.description || '暂无描述',
                price: group.price || 0,
                tag: group.status === 1 ? '招募中' : '已结束',
                tagIcon: group.status === 1 ? 'fas fa-users' : 'fas fa-check-circle',
                type: 'domestic' // 默认类型，可根据实际数据调整
            }));
        } else {
            travelCards.value = [];
        }
    } catch (error) {
        console.error('获取数据失败:', error);
        travelCards.value = [];
    }
};

// 当前地点类型
const currentLocation = ref('domestic');

// 切换地点
const changeLocation = (location) => {
    currentLocation.value = location;
    fetchCards();
};

// 过滤卡片
const filteredCards = computed(() => {
    return travelCards.value.filter(card => card.type === currentLocation.value);
});

// 群组成员数据
const groupMembers = ref([
    { name: '张明', avatar: 'https://randomuser.me/api/portraits/men/32.jpg' },
    { name: '李华', avatar: 'https://randomuser.me/api/portraits/women/44.jpg' },
    // 其他成员数据...
]);

// 模态框状态
const showGroupModal = ref(false);
const currentGroup = ref({});

// 显示模态框
const showModal = (group) => {
    currentGroup.value = group;
    showGroupModal.value = true;
    document.body.style.overflow = 'hidden';
};

// 关闭模态框
const closeModal = () => {
    showGroupModal.value = false;
    document.body.style.overflow = 'auto';
};



// 初始化轮播
onMounted(() => {
    fetchCards();
    fetchcarousel();
});

</script>

<style scoped>
.raiders-app-container {
    font-family: 'Noto Sans SC', sans-serif;
    min-height: 100vh;
    color: var(--theme-text-1);
    margin: 0 auto;
    animation: fadeInUp 1s ease-out;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.raiders-header {
    text-align: center;
    margin: 70px 0 40px 0;
    position: relative;
}

.raiders-header p {
    font-size: 1.2rem;
    color: var(--theme-text-2);
    max-width: 600px;
    margin: 0 auto 30px;
    line-height: 1.6;
}

.raiders-divider {
    height: 3px;
    background: linear-gradient(to right, transparent, var(--theme-primary), transparent);
    width: 80%;
    margin: 20px auto 40px;
    border-radius: 50%;
}

/* 搜索区域样式 */
.raiders-search-section {
    max-width: 800px;
    margin: 20px auto 60px;
    position: relative;
}

.raiders-search-box {
    display: flex;
    height: 70px;
    box-shadow: var(--theme-shadow-lg);
    border-radius: 50px;
    overflow: hidden;
}

.raiders-search-box input {
    flex: 1;
    border: none;
    padding: 0 30px;
    font-size: 1.2rem;
    outline: none;
    background: var(--theme-bg-surface);
}

.raiders-search-box input::placeholder {
    color: var(--theme-text-3);
}

.raiders-search-box button {
    background: var(--theme-gradient);
    border: none;
    padding: 0 40px;
    color: var(--theme-primary-text);
    font-size: 1.2rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    letter-spacing: 1px;
}

.raiders-search-box button:hover {
    background: var(--theme-gradient);
    transform: translateY(-2px);
}

.raiders-search-tips {
    display: flex;
    justify-content: center;
    margin-top: 20px;
    gap: 20px;
    flex-wrap: wrap;
}

.raiders-search-tips span {
    background: var(--theme-bg-glass);
    box-shadow: var(--theme-shadow-sm);
    padding: 8px 20px;
    border-radius: 30px;
    font-size: 0.95rem;
    color: var(--theme-text-1);
    cursor: pointer;
    transition: all 0.2s ease;
}

.raiders-search-tips span:hover {
    background: var(--theme-primary);
    color: var(--theme-primary-text);
    transform: translateY(-2px);
}

/* 地点切换样式 */
.raiders-location-switch {
    display: flex;
    justify-content: center;
    margin-bottom: 40px;
    gap: 30px;
}

.raiders-location-btn {
    background: var(--theme-bg-surface);
    border: none;
    padding: 15px 40px;
    font-size: 1.2rem;
    font-weight: 600;
    border-radius: 50px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: var(--theme-shadow-sm);
    position: relative;
    overflow: hidden;
    z-index: 1;
}

.raiders-location-btn::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 0%;
    height: 100%;
    background: var(--theme-gradient);
    transition: width 0.5s ease;
    z-index: -1;
}

.raiders-location-btn:hover::before {
    width: 100%;
}

.raiders-location-btn:hover {
    color: var(--theme-primary-text);
    box-shadow: 0 8px 25px color-mix(in srgb, var(--theme-primary) 40%, transparent);
}

.raiders-location-btn.raiders-active {
    background: var(--theme-gradient);
    color: var(--theme-primary-text);
    box-shadow: 0 8px 25px color-mix(in srgb, var(--theme-primary) 40%, transparent);
}

.raiders-location-btn.raiders-active::before {
    display: none;
}

/* 卡片容器样式 */
.raiders-cards-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 30px;
    margin-bottom: 60px;
    padding: 20px;
}

.raiders-travel-card {
    background: var(--theme-bg-surface);
    border-radius: 20px;
    overflow: hidden;
    box-shadow: var(--theme-shadow-md);
    transition: all 0.4s ease;
    position: relative;
}

.raiders-travel-card:hover {
    transform: translateY(-15px);
    box-shadow: var(--theme-shadow-lg);
}

.raiders-card-image {
    height: 250px;
    width: 100%;
    background-size: cover;
    background-position: center;
    position: relative;
}

.raiders-card-tag {
    position: absolute;
    top: 20px;
    right: 20px;
    background: color-mix(in srgb, var(--theme-highlight) 90%, transparent);
    color: var(--theme-primary-text);
    padding: 8px 15px;
    border-radius: 20px;
    font-size: 0.9rem;
    font-weight: 600;
}

.raiders-card-content {
    padding: 25px;
}

.raiders-card-title {
    font-size: 1.8rem;
    font-weight: 700;
    margin-bottom: 15px;
    color: var(--theme-text-1);
}

.raiders-card-info {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.raiders-card-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background-size: cover;
    margin-right: 15px;
    border: 3px solid var(--theme-primary);
}

.raiders-card-stats {
    color: var(--theme-text-2);
    font-size: 0.95rem;
}

.raiders-card-stats strong {
    color: var(--theme-highlight);
    font-weight: 700;
}

.raiders-card-description {
    color: var(--theme-text-2);
    line-height: 1.6;
    margin-bottom: 25px;
    font-size: 1.05rem;
}

.raiders-card-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.raiders-card-price {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--theme-highlight);
}

.raiders-card-price span {
    font-size: 1rem;
    color: var(--theme-text-3);
    font-weight: normal;
}

.raiders-join-btn {
    background: var(--theme-gradient);
    border: none;
    padding: 12px 30px;
    border-radius: 50px;
    color: var(--theme-primary-text);
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 1.1rem;
    letter-spacing: 1px;
}

.raiders-join-btn:hover {
    background: var(--theme-gradient);
    transform: translateY(-3px);
    box-shadow: 0 5px 15px color-mix(in srgb, var(--theme-primary) 40%, transparent);
}

/* 模态框样式 */
.raiders-modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: var(--theme-overlay);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
    backdrop-filter: blur(5px);
}

.raiders-modal-overlay.raiders-active {
    opacity: 1;
    visibility: visible;
}

.raiders-modal-container {
    background: var(--theme-bg-elevated);
    width: 90%;
    max-width: 900px;
    border-radius: 25px;
    overflow: hidden;
    box-shadow: var(--theme-shadow-lg);
    transform: translateY(50px);
    transition: transform 0.5s ease;
}

.raiders-modal-overlay.raiders-active .raiders-modal-container {
    transform: translateY(0);
}

.raiders-modal-header {
    height: 300px;
    background-size: cover;
    background-position: center;
    position: relative;
    padding: 30px;
    display: flex;
    align-items: flex-end;
}

.raiders-modal-title {
    color: white;
    font-size: 2.5rem;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
    font-weight: 700;
}

.raiders-modal-close {
    position: absolute;
    top: 25px;
    right: 25px;
    background: rgba(255, 255, 255, 0.25);
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 1.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
    backdrop-filter: blur(5px);
}

.raiders-modal-close:hover {
    background: color-mix(in srgb, var(--theme-highlight) 80%, transparent);
    transform: rotate(90deg);
}

.raiders-modal-content {
    padding: 40px;
    display: grid;
    grid-template-columns: 1fr 350px;
    gap: 40px;
}

.raiders-group-info {
    margin-bottom: 30px;
}

.raiders-info-item {
    display: flex;
    align-items: center;
    margin-bottom: 25px;
}

.raiders-info-icon {
    width: 60px;
    height: 60px;
    background: var(--theme-bg-surface-2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20px;
    font-size: 1.5rem;
    color: var(--theme-primary);
}

.raiders-info-content h3 {
    font-size: 1.4rem;
    margin-bottom: 5px;
    color: var(--theme-text-1);
}

.raiders-info-content p {
    color: var(--theme-text-2);
    line-height: 1.6;
    max-width: 350px;
}

.raiders-members-section h2 {
    font-size: 1.6rem;
    margin-bottom: 25px;
    color: var(--theme-text-1);
    padding-bottom: 15px;
    border-bottom: 2px solid var(--theme-border);
}

.raiders-members-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 20px;
}

.raiders-member-card {
    text-align: center;
}

.raiders-member-avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    background-size: cover;
    margin: 0 auto 15px;
    border: 3px solid var(--theme-primary);
}

.raiders-member-name {
    font-weight: 600;
    color: var(--theme-text-1);
}

.raiders-action-section {
    background: var(--theme-bg-surface-2);
    padding: 30px;
    text-align: center;
    border-radius: 0 0 25px 25px;
}

.raiders-confirm-btn {
    background: linear-gradient(45deg, var(--theme-highlight), var(--theme-warning));
    border: none;
    padding: 16px 50px;
    border-radius: 50px;
    color: var(--theme-primary-text);
    font-size: 1.2rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    letter-spacing: 1px;
    box-shadow: 0 5px 20px color-mix(in srgb, var(--theme-highlight) 30%, transparent);
}

.raiders-confirm-btn:hover {
    background: linear-gradient(45deg, var(--theme-highlight), var(--theme-warning));
    transform: translateY(-3px);
    box-shadow: 0 8px 25px color-mix(in srgb, var(--theme-highlight) 40%, transparent);
}

/* 响应式设计 */
@media (max-width: 992px) {
    .raiders-modal-content {
        grid-template-columns: 1fr;
    }

    .raiders-cards-container {
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    }

    .raiders-header h1 {
        font-size: 2.8rem;
    }
}

@media (max-width: 768px) {
    .raiders-carousel-container {
        height: 400px;
    }

    .raiders-carousel-caption {
        left: 30px;
        bottom: 30px;
    }

    .raiders-carousel-caption h2 {
        font-size: 2rem;
    }

    .raiders-search-box {
        height: 60px;
    }

    .raiders-search-box button {
        padding: 0 30px;
    }

    .raiders-header h1 {
        font-size: 2.2rem;
    }

    .raiders-location-btn {
        padding: 12px 30px;
        font-size: 1rem;
    }
}

@media (max-width: 576px) {
    .raiders-carousel-container {
        height: 300px;
    }

    .raiders-carousel-caption {
        left: 20px;
        bottom: 20px;
    }

    .raiders-carousel-caption h2 {
        font-size: 1.5rem;
    }

    .raiders-carousel-caption p {
        font-size: 1rem;
    }

    .raiders-search-box {
        flex-direction: column;
        height: auto;
        border-radius: 15px;
    }

    .raiders-search-box input {
        padding: 20px;
        width: 100%;
    }

    .raiders-search-box button {
        padding: 20px;
        width: 100%;
    }

    .raiders-location-switch {
        flex-direction: column;
        align-items: center;
        gap: 15px;
    }

    .raiders-cards-container {
        grid-template-columns: 1fr;
    }
}
</style>