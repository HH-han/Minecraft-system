<template>
    <div class="ticket-page">
        <header class="page-header">
            <h1>机票预订</h1>
            <p class="subtitle">选择您的出行方案</p>
        </header>
        <main>
            <div class="search-section">
                <SearchBox />
            </div>
            <Content />
        </main>
        <footer>
            <BottomPage />
        </footer>
    </div>
</template>
<script setup>
import SearchBox from '@/components/DisplayBox/SearchBox.vue';
import Content from './components/content.vue';
import BottomPage from '@/components/DisplayBox/BottomPage.vue';
</script>
<style scoped>
.ticket-page {
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

.search-section {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 22px;
}
</style>
