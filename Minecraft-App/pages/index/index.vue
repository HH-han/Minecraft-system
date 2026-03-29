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
      <div class="nav-item" @click="navigateTo('/views/attraction/index')">
        <div class="nav-icon">🏞️</div>
        <div class="nav-text">景点</div>
      </div>
      <div class="nav-item" @click="navigateTo('/views/hotel/index')">
        <div class="nav-icon">🏨</div>
        <div class="nav-text">酒店</div>
      </div>
      <div class="nav-item" @click="navigateTo('/views/ticket/index')">
        <div class="nav-icon">🎫</div>
        <div class="nav-text">票务</div>
      </div>
      <div class="nav-item" @click="navigateTo('/views/food/index')">
        <div class="nav-icon">🍜</div>
        <div class="nav-text">美食</div>
      </div>
      <div class="nav-item" @click="navigateTo('/views/product/index')">
        <div class="nav-icon">🛍️</div>
        <div class="nav-text">特产</div>
      </div>
      <div class="nav-item" @click="navigateTo('/views/travelPlan/index')">
        <div class="nav-icon">📅</div>
        <div class="nav-text">出行计划</div>
      </div>
      <div class="nav-item" @click="navigateTo('/views/groupTravel/index')">
        <div class="nav-icon">👥</div>
        <div class="nav-text">跟团游</div>
      </div>
      <div class="nav-item" @click="navigateTo('/views/news/index')">
        <div class="nav-icon">📰</div>
        <div class="nav-text">新闻</div>
      </div>
    </div>

    <!-- 推荐景点 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐景点</h2>
        <button class="more-btn" @click="navigateTo('/views/attraction/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div 
          v-for="attraction in recommendAttractions" 
          :key="attraction.id" 
          class="glass-card attraction-item"
          @click="navigateTo(`/views/attraction/detail?id=${attraction.id}`)"
        >
          <img :src="attraction.image" :alt="attraction.name" class="item-image">
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
        <button class="more-btn" @click="navigateTo('/views/hotel/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div 
          v-for="hotel in recommendHotels" 
          :key="hotel.id" 
          class="glass-card hotel-item"
          @click="navigateTo(`/views/hotel/detail?id=${hotel.id}`)"
        >
          <img :src="hotel.image" :alt="hotel.name" class="item-image">
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
        <button class="more-btn" @click="navigateTo('/views/food/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div 
          v-for="food in recommendFoods" 
          :key="food.id" 
          class="glass-card food-item"
          @click="navigateTo(`/views/food/detail?id=${food.id}`)"
        >
          <img :src="food.image" :alt="food.name" class="item-image">
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
        <button class="more-btn" @click="navigateTo('/views/news/index')">查看更多</button>
      </div>
      <div class="news-list">
        <div 
          v-for="news in hotNews" 
          :key="news.id" 
          class="glass-card news-item"
          @click="navigateTo(`/views/news/detail?id=${news.id}`)"
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
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
}

.swiper {
  width: 100%;
  height: 200px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: white;
}

.carousel-content h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 500;
}

.carousel-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.nav-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 20px;
  margin-bottom: 24px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-item:hover {
  transform: translateY(-2px);
}

.nav-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.nav-text {
  font-size: 12px;
  color: var(--text-color);
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
  font-size: 18px;
  color: var(--text-color);
}

.more-btn {
  background: none;
  border: none;
  font-size: 14px;
  color: var(--primary-color);
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.more-btn:hover {
  background-color: rgba(30, 136, 229, 0.1);
}

.recommend-list {
  display: flex;
  overflow-x: auto;
  padding-bottom: 10px;
  -webkit-overflow-scrolling: touch;
  gap: 16px;
}

.recommend-list .glass-card {
  flex: 0 0 200px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.recommend-list .glass-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px 0 rgba(31, 38, 135, 0.4);
}

.item-image {
  width: 100%;
  height: 120px;
  border-radius: 8px;
  object-fit: cover;
  margin-bottom: 12px;
}

.item-info h3 {
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rating {
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.star {
  color: #FF9800;
  margin-right: 4px;
}

.price {
  font-size: 16px;
  font-weight: 600;
  color: #F44336;
  margin-top: 8px;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.news-item {
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.news-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px 0 rgba(31, 38, 135, 0.4);
}

.news-item h3 {
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-desc {
  font-size: 14px;
  color: var(--text-secondary-color);
  line-height: 1.4;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-secondary-color);
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