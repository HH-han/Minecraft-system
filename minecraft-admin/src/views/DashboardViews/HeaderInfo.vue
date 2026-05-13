<template>
  <div class="dashboard-header">
    <div class="header-content">
      <div class="header-left">
        <h1 class="dashboard-title">控制台</h1>
        <p class="dashboard-subtitle">欢迎回来，管理员！以下是今日业务概览</p>
      </div>
      <div class="header-right">
        <div class="date-info">
          <span class="current-date">{{ currentDate }}</span>
          <span class="current-time">{{ currentTime }}</span>
        </div>
      </div>
    </div>

    <div class="stats-grid">
      <div v-for="(stat, index) in statsData" :key="index" class="stat-card"
        :style="{ animationDelay: `${index * 0.1}s` }">
        <div class="stat-icon" :style="{ backgroundColor: stat.bgColor }">
          <span>{{ stat.icon }}</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ stat.prefix }}{{ stat.value }}
            <span v-if="stat.change" class="stat-change" :class="stat.changeClass">
              {{ stat.change }}
            </span>
          </div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const currentDate = ref('')
const currentTime = ref('')
let timeInterval = null

const updateTime = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
  currentTime.value = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const statsData = [
  {
    icon: '👥',
    value: '12,345',
    prefix: '',
    label: '总用户数',
    bgColor: 'rgba(108, 92, 231, 0.1)',
    change: '+12.5%',
    changeClass: 'positive'
  },
  {
    icon: '📋',
    value: '2,847',
    prefix: '',
    label: '今日订单',
    bgColor: 'rgba(0, 184, 148, 0.1)',
    change: '+8.3%',
    changeClass: 'positive'
  },
  {
    icon: '💰',
    value: '156,890',
    prefix: '￥',
    label: '今日营收',
    bgColor: 'rgba(253, 203, 110, 0.1)',
    change: '+15.2%',
    changeClass: 'positive'
  },
  {
    icon: '👁️',
    value: '45,678',
    prefix: '',
    label: '今日访问量',
    bgColor: 'rgba(9, 132, 227, 0.1)',
    change: '-2.1%',
    changeClass: 'negative'
  }
]

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.dashboard-header {
  background-image: linear-gradient(to top, #f3e7e9 0%, #e3eeff 99%, #e3eeff 100%);
  border-radius: 16px;
  padding: 2rem;
  color: #000;
  margin-bottom: 2rem;
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.dashboard-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
}

.dashboard-subtitle {
  font-size: 1rem;
  opacity: 0.9;
  margin: 0;
}

.date-info {
  text-align: right;
}

.current-date {
  display: block;
  font-size: 0.9rem;
  opacity: 0.8;
  margin-bottom: 0.3rem;
}

.current-time {
  display: block;
  font-size: 1.5rem;
  font-weight: 600;
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', monospace;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  animation: slideUp 0.5s ease-out forwards;
  opacity: 0;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  width: 55px;
  height: 55px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.3rem;
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.stat-change {
  font-size: 0.85rem;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 20px;
}

.stat-change.positive {
  background: rgba(0, 184, 148, 0.3);
  color: #00b894;
}

.stat-change.negative {
  background: rgba(214, 48, 49, 0.3);
  color: #d63031;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.85;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 1.5rem;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .date-info {
    text-align: left;
  }

  .dashboard-title {
    font-size: 1.4rem;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .stat-card {
    padding: 1rem;
  }

  .stat-value {
    font-size: 1.2rem;
  }
}
</style>