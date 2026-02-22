<template>
    <!-- 轮播图 -->
    <div class="banner">
      <div class="banner-content-SeamlessCarousel">
        <transition-group>
          <SeamlessCarousel :items="slides" :interval="5000" :transitionDuration="700" aspectRatio="16/9" />
        </transition-group>
      </div>
      <!-- 搜索栏 -->
      <div class="homeview-hero-content">
        <h1 class="homeview-hero-title">探索世界的奇迹</h1>
        <p class="homeview-hero-subtitle">发现地球上最令人惊叹的地方，创造终生难忘的回忆</p>
        <div class="search_input">
          <input v-model="searchKeyword" type="text" placeholder="🔍 输入关键词" @keyup.enter="search">
          <button class="search_button-home-5" @click="search">搜索</button>
        </div>
      </div>
    </div>
</template>
<script setup>
import SeamlessCarousel from './components/SeamlessCarousel.vue';
import { carouselItems } from '@/views/index/HomeImage.js';

const slides = carouselItems;
</script>
<style scoped>
@import "@/css/Home/paging.css";
@import "@/css/Home/HomeViews.css";
</style>