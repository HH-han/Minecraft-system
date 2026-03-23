<template>
    <div class="swagger-ui-container">
        <iframe 
            :src="swaggerUrl" 
            class="swagger-iframe"
            frameborder="0"
            title="Swagger UI"
        ></iframe>
    </div>
</template>

<script setup>
// Swagger UI 地址
const swaggerUrl = 'http://localhost:8080/swagger-ui/index.html'
</script>

<style scoped>
.swagger-ui-container {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.swagger-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>