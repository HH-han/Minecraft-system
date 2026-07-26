<template>
    <div class="OpenAPI-JSON-container">
        <div class="header">
          <span>OpenAPIJSON</span>
        </div>
        <iframe 
            :src="OpenAPIJSON" 
            class="OpenAPI-JSON-iframe"
            frameborder="0"
            title="OpenAPI JSON"
        ></iframe>
    </div>
</template>

<script setup>
// OpenAPI JSON 地址
const OpenAPIJSON = 'http://localhost:8080/v3/api-docs'
</script>

<style scoped>
.OpenAPI-JSON-container {
  width: 100%;
  height: 80vh;
  overflow: hidden;
}

.header {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  letter-spacing: 1px;
  padding: 12px 0;
}

.header::after {
  content: '';
  flex: 1;
  height: 2px;
  background: linear-gradient(to right, #4a90d9, transparent);
}

.OpenAPI-JSON-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>