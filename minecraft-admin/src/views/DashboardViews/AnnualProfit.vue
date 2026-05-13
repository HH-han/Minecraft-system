<template>
  <div class="dashboard-card">
    <h3 class="dashboard-card-title">年利润</h3>
    <div class="profit-header">
      <div>
        <div class="profit-label">转化率</div>
        <div class="profit-rate">18.4%</div>
      </div>
      <div class="profit-chart">
        <div ref="lineCanvas" class="mini-chart"></div>
      </div>
    </div>
    
    <div class="profit-stats">
      <div class="profit-stat">
        <div class="stat-header">
          <span>已加入购物车</span>
          <span class="stat-amount">￥21,120.70</span>
        </div>
        <div class="stat-change positive">比去年 +13.2%</div>
      </div>
      
      <div class="profit-stat">
        <div class="stat-header">
          <span>已购买</span>
          <span class="stat-amount">￥16,100.00</span>
        </div>
        <div class="stat-change negative">比去年 -7.4%</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const lineCanvas = ref(null)
let chartInstance = null

const initChart = () => {
  if (!lineCanvas.value) return
  
  chartInstance = echarts.init(lineCanvas.value)
  
  const option = {
    grid: {
      left: '0%',
      right: '0%',
      bottom: '0%',
      top: '0%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['Q1', 'Q2', 'Q3', 'Q4'],
      show: false
    },
    yAxis: {
      type: 'value',
      show: false
    },
    series: [{
      type: 'line',
      data: [15000, 18000, 21000, 24000],
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: {
        width: 2,
        color: '#6c5ce7'
      },
      itemStyle: {
        color: '#fff',
        borderColor: '#6c5ce7',
        borderWidth: 2
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(108, 92, 231, 0.2)' },
          { offset: 1, color: 'transparent' }
        ])
      }
    }]
  }
  
  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.profit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.profit-label {
  font-size: 0.9rem;
  color: #666;
  opacity: 0.7;
  margin-bottom: 0.3rem;
}

.profit-rate {
  font-size: 1.8rem;
  font-weight: 700;
  color: #6c5ce7;
}

.profit-chart {
  width: 150px;
  height: 80px;
}

.mini-chart {
  width: 100%;
  height: 100%;
}

.profit-stats {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.profit-stat {
  background: rgba(255, 255, 255, 0.7);
  padding: 1rem;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.profit-stat:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.stat-amount {
  font-weight: 700;
  color: #333;
}

.stat-change {
  font-size: 0.9rem;
  font-weight: 500;
}

.stat-change.positive {
  color: #00b894;
}

.stat-change.negative {
  color: #d63031;
}
</style>