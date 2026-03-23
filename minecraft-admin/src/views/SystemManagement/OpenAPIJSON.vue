<template>
    <div class="OpenAPI-JSON-container">
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
  height: 100vh;
  overflow: hidden;
}

.OpenAPI-JSON-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>