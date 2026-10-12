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
    background: var(--theme-bg-app);
}

.page-header {
    text-align: center;
    padding: 60px 20px 40px;
    background: linear-gradient(180deg, var(--theme-bg-surface-2) 0%, var(--theme-bg-app) 100%);
}

.page-header h1 {
    font-size: 56px;
    font-weight: 700;
    color: var(--theme-text-1);
    letter-spacing: -0.02em;
    margin-bottom: 8px;
}

.subtitle {
    font-size: 28px;
    color: var(--theme-text-2);
    font-weight: 400;
}
</style>
