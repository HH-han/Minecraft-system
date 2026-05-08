<template>
    <div class="travel-page">
        <header class="page-header">
            <h1>旅行计划</h1>
            <p class="subtitle">规划您的完美旅程</p>
        </header>
        <main>
            <Content />
        </main>
        <footer>
            <BottomPage />
        </footer>
    </div>
</template>
<script setup>
import Content from './components/content.vue';
import BottomPage from '@/components/DisplayBox/BottomPage.vue';
</script>
<style scoped>
.travel-page {
    min-height: 100vh;
    background: #ffffff;
}

.page-header {
    text-align: center;
    padding: 60px 20px 40px;
    background: linear-gradient(180deg, #f5f5f7 0%, #ffffff 100%);
}

.page-header h1 {
    font-size: 56px;
    font-weight: 700;
    color: #1d1d1f;
    letter-spacing: -0.02em;
    margin-bottom: 8px;
}

.subtitle {
    font-size: 28px;
    color: #6e6e73;
    font-weight: 400;
}
</style>
