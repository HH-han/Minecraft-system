<template>
  <div class="page-container">
    <!-- 轮播图 -->
    <div class="carousel">
      <swiper class="swiper" indicator-dots="true" autoplay="true" interval="3000" duration="500">
        <swiper-item v-for="(item, index) in carouselList" :key="index">
          <img :src="item.image" :alt="item.title" class="carousel-image">
          <div class="carousel-content">
            <h3>{{ item.title }}</h3>
            <p>{{ item.subtitle }}</p>
          </div>
        </swiper-item>
      </swiper>
    </div>

    <!-- 功能导航 -->
    <div class="glass-morphism nav-container">
      <div class="nav-item" @click="navigateTo('/pages/attraction/index')">
        <div class="nav-icon">🏞️</div>
        <div class="nav-text">景点</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/hotel/index')">
        <div class="nav-icon">🏨</div>
        <div class="nav-text">酒店</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/ticket/index')">
        <div class="nav-icon">🎫</div>
        <div class="nav-text">票务</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/food/index')">
        <div class="nav-icon">🍜</div>
        <div class="nav-text">美食</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/product/index')">
        <div class="nav-icon">🛍️</div>
        <div class="nav-text">特产</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/travelPlan/index')">
        <div class="nav-icon">📅</div>
        <div class="nav-text">出行计划</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/groupTravel/index')">
        <div class="nav-icon">👥</div>
        <div class="nav-text">跟团游</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/news/index')">
        <div class="nav-icon">📰</div>
        <div class="nav-text">新闻</div>
      </div>
    </div>

    <!-- 推荐景点 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐景点</h2>
        <button class="more-btn" @click="navigateTo('/pages/attraction/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div 
          v-for="attraction in recommendAttractions" 
          :key="attraction.id" 
          class="glass-card attraction-item"
          @click="navigateTo(`/pages/attraction/detail?id=${attraction.id}`)"
        >
          <img :src="attraction.coverImage" :alt="attraction.name" class="item-image">
          <div class="item-info">
            <h3>{{ attraction.name }}</h3>
            <p class="text-secondary">{{ attraction.city }}</p>
            <div class="rating">
              <span class="star">★</span>
              <span>{{ attraction.rating }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐酒店 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐酒店</h2>
        <button class="more-btn" @click="navigateTo('/pages/hotel/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div 
          v-for="hotel in recommendHotels" 
          :key="hotel.id" 
          class="glass-card hotel-item"
          @click="navigateTo(`/pages/hotel/detail?id=${hotel.id}`)"
        >
          <img :src="hotel.coverImage" :alt="hotel.name" class="item-image">
          <div class="item-info">
            <h3>{{ hotel.name }}</h3>
            <p class="text-secondary">{{ hotel.city }}</p>
            <div class="price">¥{{ hotel.price }}/晚</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐美食 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐美食</h2>
        <button class="more-btn" @click="navigateTo('/pages/food/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div 
          v-for="food in recommendFoods" 
          :key="food.id" 
          class="glass-card food-item"
          @click="navigateTo(`/pages/food/detail?id=${food.id}`)"
        >
          <img :src="food.coverImage" :alt="food.name" class="item-image">
          <div class="item-info">
            <h3>{{ food.name }}</h3>
            <p class="text-secondary">{{ food.city }} - {{ food.cuisine }}</p>
            <div class="rating">
              <span class="star">★</span>
              <span>{{ food.rating }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门新闻 -->
    <div class="section">
      <div class="section-header">
        <h2>热门新闻</h2>
        <button class="more-btn" @click="navigateTo('/pages/news/index')">查看更多</button>
      </div>
      <div class="news-list">
        <div 
          v-for="news in hotNews" 
          :key="news.id" 
          class="glass-card news-item"
          @click="navigateTo(`/pages/news/detail?id=${news.id}`)"
        >
          <h3>{{ news.title }}</h3>
          <p class="news-desc">{{ news.description }}</p>
          <div class="news-meta">
            <span class="news-date">{{ news.createTime }}</span>
            <span class="news-view">{{ news.viewCount }} 浏览</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotAttractions } from '@/api/attraction'
import { getRecommendHotels } from '@/api/hotel'
import { getRecommendFoods } from '@/api/food'
import { getHotNews } from '@/api/news'

// 轮播图数据
const carouselList = ref([
  { image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20scenic%20landscape%20of%20mountain%20and%20lake&image_size=landscape_16_9', title: '探索自然风光', subtitle: '领略大自然的壮美景色' },
  { image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20city%20skyline%20at%20night&image_size=landscape_16_9', title: '城市之旅', subtitle: '体验都市的繁华与活力' },
  { image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=traditional%20chinese%20architecture&image_size=landscape_16_9', title: '文化古迹', subtitle: '感受历史的厚重与魅力' }
])

// 推荐数据
const recommendAttractions = ref([])
const recommendHotels = ref([])
const recommendFoods = ref([])
const hotNews = ref([])

// 获取推荐景点
const getRecommendAttractions = async () => {
  try {
    const response = await getHotAttractions(4)
    recommendAttractions.value = response.data
  } catch (error) {
    console.error('获取推荐景点失败:', error)
  }
}

// 获取推荐酒店
const getRecommendHotelsList = async () => {
  try {
    const response = await getRecommendHotels('', 4)
    recommendHotels.value = response.data
  } catch (error) {
    console.error('获取推荐酒店失败:', error)
  }
}

// 获取推荐美食
const getRecommendFoodsList = async () => {
  try {
    const response = await getRecommendFoods('', 4)
    recommendFoods.value = response.data
  } catch (error) {
    console.error('获取推荐美食失败:', error)
  }
}

// 获取热门新闻
const getHotNewsList = async () => {
  try {
    const response = await getHotNews({ limit: 4 })
    hotNews.value = response.data
  } catch (error) {
    console.error('获取热门新闻失败:', error)
  }
}

// 导航到指定页面
const navigateTo = (url) => {
  uni.navigateTo({
    url
  })
}

// 页面加载时获取数据
onMounted(() => {
  getRecommendAttractions()
  getRecommendHotelsList()
  getRecommendFoodsList()
  getHotNewsList()
})
</script>

<style scoped>
.page-container {
  padding: 16px;
  min-height: 100vh;
  background-color: var(--background-color);
}

.carousel {
  margin-bottom: 24px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.4);
  position: relative;
  transition: all 0.3s ease;
}

.carousel:hover {
  box-shadow: 0 16px 48px rgba(31, 38, 135, 0.5);
}

.swiper {
  width: 100%;
  height: 280px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.5s ease;
}

.swiper-item:hover .carousel-image {
  transform: scale(1.05);
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 32px 24px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.4), transparent);
  color: white;
  transition: all 0.3s ease;
}

.carousel-content h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  animation: fadeInUp 0.6s ease;
}

.carousel-content p {
  margin: 0;
  font-size: 16px;
  opacity: 0.95;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  animation: fadeInUp 0.8s ease 0.2s forwards;
  opacity: 0;
}

/* 轮播指示器样式 */
.swiper .uni-swiper-dots {
  bottom: 24px;
}

.swiper .uni-swiper-dot {
  width: 12px;
  height: 12px;
  margin: 0 6px;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.swiper .uni-swiper-dot-active {
  width: 24px;
  background: white;
  border-radius: 6px;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .swiper {
    height: 200px;
  }
  
  .carousel-content {
    padding: 24px 16px;
  }
  
  .carousel-content h3 {
    font-size: 20px;
  }
  
  .carousel-content p {
    font-size: 14px;
  }
}

.nav-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 20px;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-item:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 16px rgba(30, 136, 229, 0.3);
  border-color: rgba(30, 136, 229, 0.5);
}

.nav-icon {
  font-size: 32px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.nav-item:hover .nav-icon {
  transform: scale(1.1);
}

.nav-text {
  font-size: 14px;
  color: var(--text-color);
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-item:hover .nav-text {
  color: var(--primary-color);
}

.section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header h2::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(to bottom, var(--primary-color), #0056b3);
  border-radius: 2px;
}

.more-btn {
  background: rgba(30, 136, 229, 0.1);
  border: 1px solid rgba(30, 136, 229, 0.3);
  font-size: 12px;
  color: var(--primary-color);
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.more-btn:hover {
  background-color: var(--primary-color);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(30, 136, 229, 0.4);
}

.recommend-list {
  display: flex;
  overflow-x: auto;
  padding-bottom: 16px;
  -webkit-overflow-scrolling: touch;
  gap: 20px;
  margin-top: 16px;
}

.recommend-list .glass-card {
  flex: 0 0 220px;
  padding: 0;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.recommend-list .glass-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(30, 136, 229, 0.3);
  border-color: rgba(30, 136, 229, 0.3);
}

.item-image {
  width: 100%;
  height: 140px;
  border-radius: 16px 16px 0 0;
  object-fit: cover;
  margin-bottom: 0;
  transition: all 0.3s ease;
}

.recommend-list .glass-card:hover .item-image {
  transform: scale(1.05);
}

.item-info {
  padding: 16px;
}

.item-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.3;
}

.text-secondary {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
  line-height: 1.4;
}

.rating {
  display: flex;
  align-items: center;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.star {
  color: #FF9800;
  margin-right: 6px;
  font-size: 16px;
}

.rating span {
  font-size: 14px;
  font-weight: 500;
  color: #FF9800;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #F44336;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price::after {
  content: '/晚';
  font-size: 12px;
  font-weight: 500;
  color: var(--text-secondary-color);
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.news-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(30, 136, 229, 0.3);
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(30, 136, 229, 0.3);
}

.news-item h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.news-desc {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: var(--text-secondary-color);
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.news-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-view {
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(30, 136, 229, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  color: var(--primary-color);
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .swiper {
    height: 150px;
  }
  
  .nav-container {
    grid-template-columns: repeat(4, 1fr);
    gap: 12px;
    padding: 16px;
  }
  
  .recommend-list .glass-card {
    flex: 0 0 150px;
  }
  
  .item-image {
    height: 100px;
  }
}
</style>