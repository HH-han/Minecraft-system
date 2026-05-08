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

.search-section {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 22px;
}
</style>
