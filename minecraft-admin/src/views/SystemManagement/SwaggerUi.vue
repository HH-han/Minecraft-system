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
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.swagger-ui-content {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  max-width: 600px;
  width: 100%;
  text-align: center;
}

.swagger-ui-content h2 {
  margin-bottom: 20px;
  color: #333;
  font-size: 24px;
}

.swagger-ui-content p {
  margin-bottom: 30px;
  color: #666;
  font-size: 16px;
}

.swagger-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.swagger-info {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  text-align: left;
  margin-bottom: 20px;
}

.swagger-info h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 18px;
  text-align: center;
}

.swagger-info p {
  margin-bottom: 10px;
  font-size: 14px;
}

.swagger-info strong {
  color: #333;
}

.swagger-info a {
  color: #409eff;
  text-decoration: none;
}

.swagger-info a:hover {
  text-decoration: underline;
}

.swagger-notice {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .swagger-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .swagger-actions .el-button {
    width: 100%;
    max-width: 300px;
  }
}
</style>