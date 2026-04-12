<template>
  <div class="page-container">
    <!-- 轮播图 -->
    <Carousel :items="carouselList" :interval="3000" :transitionDuration="600" :showArrows="true" :showIndicators="true"
      :aspectRatio="'16/9'" />

    <!-- 功能导航 -->
    <Index />
    <!-- 推荐景点 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐景点</h2>
        <button class="more-btn" @click="navigateTo('/pages/attraction/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div v-for="attraction in recommendAttractions" :key="attraction.id" class="glass-card attraction-item"
          @click="navigateTo(`/pages/attraction/detail?id=${attraction.id}`)">
          <img :src="attraction.coverImage" :alt="attraction.name" class="item-image">
          <div class="item-info">
            <h3>{{ attraction.name }}</h3>
            <p class="text-secondary">{{ attraction.city }}</p>
            <div class="attraction-price" v-if="attraction.price > 0">¥{{ attraction.price }}</div>
            <div class="attraction-price free" v-else>免费</div>
            <div class="rating">
              <span v-for="i in attraction.rating" :key="i" class="star">★</span>
            </div>
            <div class="attraction-address">{{ attraction.address }}</div>

            <div class="attraction-season" v-if="attraction.season">{{ attraction.season }}</div>
            <div class="attraction-stats stats">
              <div class="attraction-stat">
                <span>{{ attraction.likeCount }} 点赞</span>
                <span>{{ attraction.commentCount }} 评论</span>
                <span>{{ attraction.collectCount }} 收藏</span>
              </div>
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
        <div v-for="hotel in recommendHotels" :key="hotel.id" class="glass-card hotel-item"
          @click="navigateTo(`/pages/hotel/detail?id=${hotel.id}`)">
          <img :src="hotel.coverImage" :alt="hotel.name" class="item-image">
          <div class="item-info">
            <h3>{{ hotel.name }}</h3>
            <p class="text-secondary">{{ hotel.city }}</p>
            <div class="price">¥{{ hotel.price }}/晚</div>
            <div class="hotel-stars">
              <span v-for="i in hotel.starLevel" :key="i" class="star">★</span>
            </div>
            <div class="hotel-address">{{ hotel.address }}</div>
            <div class="hotel-facilities">
              <span v-for="facility in (hotel.facilities ? hotel.facilities.split(',') : [])" :key="facility"
                class="hotel-facility">{{ facility }}</span>
            </div>
            <div class="hotel-stats stats">
              <div class="hotel-stat">
                <span>{{ hotel.likeCount }} 点赞</span>
                <span>{{ hotel.commentCount }} 评论</span>
                <span>{{ hotel.collectCount }} 收藏</span>
              </div>
            </div>
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
        <div v-for="food in recommendFoods" :key="food.id" class="glass-card food-item"
          @click="navigateTo(`/pages/food/detail?id=${food.id}`)">
          <img :src="food.coverImage" :alt="food.name" class="item-image">
          <div class="item-info">
            <h3>{{ food.name }}</h3>
            <p class="text-secondary">{{ food.city }} - {{ food.cuisineType }}</p>
            <div class="food-price">¥{{ food.price }}</div>
            <div class="rating">
              <span v-for="i in food.rating" :key="i" class="star">★</span>
            </div>
            <div class="food-address">{{ food.address }}</div>
            <div class="food-tags">
              <span v-for="tag in (food.tags ? food.tags.split(',') : [])" :key="tag" class="food-tag">{{ tag }}</span>
            </div>
            <div class="food-stats stats">
              <div class="food-stat">
                <span>{{ food.likeCount }} 点赞</span>
                <span>{{ food.commentCount }} 评论</span>
                <span>{{ food.collectCount }} 收藏</span>
              </div>
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
        <div v-for="news in hotNews" :key="news.id" class="glass-card news-item"
          @click="navigateTo(`/pages/news/detail?id=${news.id}`)">
          <div class="news-content">
            <div class="news-image" v-if="news.coverImage">
              <img :src="news.coverImage" :alt="news.title">
            </div>
            <div class="news-info">
              <h3>{{ news.title }}</h3>
              <p class="news-desc">{{ news.description }}</p>
              <div class="news-meta">
                <span class="news-source">{{ news.source }}</span>
                <span class="news-date">{{ news.createTime }}</span>
              </div>
            </div>
          </div>
          <div class="news-stats">
            <span class="news-stat">{{ news.viewCount }} 浏览</span>
            <span class="news-stat">{{ news.likeCount }} 点赞</span>
            <span class="news-stat">{{ news.commentCount }} 评论</span>
            <span class="news-stat">{{ news.collectCount }} 收藏</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref,onMounted,onUnmounted } from 'vue'
import { getHotAttractions} from '@/api/attraction'
import { getRecommendHotels} from '@/api/hotel'
import { getRecommendFoods } from '@/api/food'
import { getHotNews } from '@/api/news'
import carouselApi from '@/api/carousel'
import Carousel from '../carousel/Carousel.vue'
import Index from '@/components/index/index.vue'

// 轮播图数据
const carouselList = ref([])
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
    const response = await getHotNews({
      limit: 4
    })
    hotNews.value = response.data
  } catch (error) {
    console.error('获取热门新闻失败:', error)
  }
}

// 获取轮播图数据
const getCarouselData = async () => {
  try {
    const response = await carouselApi.getHomeTopCarousels()
    carouselList.value = response.data
  } catch (error) {
    console.error('获取轮播图数据失败:', error)
  }
}

// 页面加载时获取数据
onMounted(() => {
  getCarouselData()
  getRecommendAttractions()
  getRecommendHotelsList()
  getRecommendFoodsList()
  getHotNewsList()
})

// 页面卸载时清理
onUnmounted(() => { })
</script>

<style scoped>
/* CSS 变量定义 */
:root {
  --background-color: #f5f5f5;
  --text-color: #333333;
  --primary-color: #007aff;
  --text-secondary-color: #666666;
}

.page-container {
  min-height: 100vh;
  background-color: var(--background-color);
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

.section {
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
  padding: 8px;
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
  margin-right: 0
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
  gap: 8px;
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

.attraction-price {
  font-size: 16px;
  font-weight: 700;
  color: #F44336;
  margin: 8px 0;
}

.attraction-price.free {
  color: #4CAF50;
}

.attraction-address {
  font-size: 12px;
  color: var(--text-secondary-color);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attraction-season {
  font-size: 12px;
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
  margin-bottom: 8px;
  display: inline-block;
}

.attraction-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.stats {
  justify-content: flex-end;
}

.attraction-stat {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary-color);
}

.attraction-stat span {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.attraction-item:hover .attraction-stat span {
  color: var(--primary-color);
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #F44336;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price::after {
  content: '';
}

.hotel-stars {
  display: flex;
  align-items: center;
  margin: 8px 0;
}

.hotel-stars .star {
  color: #FF9800;
  font-size: 14px;
  margin-right: 2px;
}

.hotel-address {
  font-size: 12px;
  color: var(--text-secondary-color);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hotel-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.hotel-facility {
  font-size: 11px;
  background: rgba(30, 136, 229, 0.1);
  color: var(--primary-color);
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.hotel-stats {
  display: flex;
  align-items: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  flex-direction: row;
  gap: 8px;
}

.hotel-stat {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary-color);
}

.hotel-stat span {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.hotel-item:hover .hotel-stat span {
  color: var(--primary-color);
}

.food-price {
  font-size: 18px;
  font-weight: 700;
  color: #F44336;
  margin: 8px 0;
}

.food-address {
  font-size: 12px;
  color: var(--text-secondary-color);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.food-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.food-tag {
  font-size: 11px;
  background: rgba(30, 136, 229, 0.1);
  color: var(--primary-color);
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.food-stats {
  display: flex;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.food-stat {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary-color);
}

.food-stat span {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.food-item:hover .food-stat span {
  color: var(--primary-color);
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

.news-content {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.news-image {
  flex: 0 0 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.news-item:hover .news-image img {
  transform: scale(1.05);
}

.news-info {
  flex: 1;
  min-width: 0;
}

.news-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
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
  margin: 0 0 12px 0;
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
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary-color);
  margin-bottom: 8px;
}

.news-source {
  background: rgba(30, 136, 229, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
  color: var(--primary-color);
  font-weight: 500;
  font-size: 11px;
}

.news-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-stats {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  font-size: 12px;
  color: var(--text-secondary-color);
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.news-stat {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.news-item:hover .news-stat {
  color: var(--primary-color);
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
    flex: 0 0 290px;
  }

  .item-image {
    height: 100px;
  }
}
</style>