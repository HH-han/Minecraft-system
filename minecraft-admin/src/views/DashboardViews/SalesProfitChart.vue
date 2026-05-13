<template>
  <div class="dashboard-card">
    <div class="dashboard-chart-header">
      <h3 class="dashboard-card-title">销售利润</h3>
      <div class="chart-tabs">
        <button v-for="tab in tabs" :key="tab" :class="{ active: activeTab === tab }" @click="activeTab = tab">
          {{ tab }}
        </button>
      </div>
    </div>

    <div class="chart-container">
      <div ref="chartCanvas" class="line-chart"></div>
    </div>

    <div class="chart-footer">
      <div class="chart-stats">
        <div class="stat">
          <div class="amount">￥63,489.50</div>
          <div class="change positive">比去年 +8%</div>
        </div>
        <div class="stat">
          <div class="amount">￥38,496.00</div>
          <div class="change negative">比去年 -6%</div>
        </div>
      </div>
      <button class="view-details">查看详情</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

const chartCanvas = ref(null)
const activeTab = ref('利润')
const tabs = ['利润', '成本']
let chartInstance = null

const initChart = () => {
  if (!chartCanvas.value) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(chartCanvas.value)
  
  const colors = activeTab.value === '利润' ? ['#00b894', 'rgba(0, 184, 148, 0.1)'] : ['#d63031', 'rgba(214, 48, 49, 0.1)']
  
  const option = {
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月'],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#999', fontSize: 12 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { show: false },
      splitLine: { lineStyle: { color: 'rgba(0,0,0,0.05)' } }
    },
    series: [{
      type: 'line',
      data: [45000, 42000, 48000, 51000, 58000, 63000, 62000, 65000],
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: colors[0]
      },
      itemStyle: {
        color: '#fff',
        borderColor: colors[0],
        borderWidth: 2
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: colors[1] },
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

watch(activeTab, initChart)
</script>

<style scoped>
.dashboard-chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.chart-tabs {
  display: flex;
  background: rgba(108, 92, 231, 0.1);
  border-radius: 50px;
  padding: 5px;
}

.chart-tabs button {
  border: none;
  background: none;
  padding: 8px 20px;
  border-radius: 50px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.chart-tabs button.active {
  background: #6c5ce7;
  color: white;
}

.chart-container {
  height: 220px;
  margin: 1rem 0;
}

.line-chart {
  width: 100%;
  height: 100%;
}

.chart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
}

.chart-stats {
  display: flex;
  gap: 2rem;
}

.stat .amount {
  font-weight: 700;
  font-size: 1.2rem;
  margin-bottom: 0.3rem;
}

.stat .change {
  font-size: 0.9rem;
  font-weight: 500;
}

.change.positive {
  color: #00b894;
}

.change.negative {
  color: #d63031;
}

.view-details {
  background: #6c5ce7;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 50px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(108, 92, 231, 0.3);
}

.view-details:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(108, 92, 231, 0.4);
}
</style>