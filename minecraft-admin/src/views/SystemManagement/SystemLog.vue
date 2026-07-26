<template>
    <div class="SystemLog-container">
        <div class="header">
          <span>System Log</span>
        </div>
        <iframe 
            :src="SystemLog" 
            class="SystemLog-iframe"
            frameborder="0"
            title="System Log"
        ></iframe>
    </div>
</template>

<script setup>
// System Log 地址
const SystemLog = 'http://localhost:8080/logs/tourism-backend.html'
</script>

<style scoped>
.SystemLog-container {
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

.SystemLog-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>