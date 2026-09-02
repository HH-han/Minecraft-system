<template>
  <div class="cards-page">
    <main>
      <Content />
    </main>
    <footer>
      <BottomPage />
    </footer>
  </div>
</template>

<script setup>
import Content from './content.vue'
import BottomPage from '@/components/DisplayBox/BottomPage.vue'
</script>
