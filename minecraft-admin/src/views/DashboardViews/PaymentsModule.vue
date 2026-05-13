<template>
  <div class="dashboard-card">
    <h3 class="dashboard-card-title">付款</h3>
    <div class="payments-header">
      <div>
        <div class="payments-amount">￥12,389</div>
        <div class="payments-change negative">-3.8% 最近7天</div>
      </div>
      <div class="payments-chart">
        <div ref="barCanvas" class="bar-chart"></div>
      </div>
    </div>

    <div class="payments-methods">
      <h4>支付方式分布</h4>
      <div class="methods-grid">
        <div class="method">
          <div class="method-info">
            <div class="method-color" style="background-color: #6c5ce7;"></div>
            <div class="method-name">信用卡</div>
          </div>
          <div class="method-percent">15%</div>
        </div>
        <div class="method">
          <div class="method-info">
            <div class="method-color" style="background-color: #0984e3;"></div>
            <div class="method-name">支付宝</div>
          </div>
          <div class="method-percent">30%</div>
        </div>
        <div class="method">
          <div class="method-info">
            <div class="method-color" style="background-color: #00b894;"></div>
            <div class="method-name">微信</div>
          </div>
          <div class="method-percent">55%</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const barCanvas = ref(null)
let chartInstance = null

const initChart = () => {
  if (!barCanvas.value) return
  
  chartInstance = echarts.init(barCanvas.value)
  
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
      data: ['一月', '二月', '三月', '四月', '五月', '六月', '七月'],
      show: false
    },
    yAxis: {
      type: 'value',
      show: false
    },
    series: [{
      type: 'bar',
      data: [1200, 1900, 1500, 1800, 2200, 2400, 2100],
      itemStyle: {
        color: function(params) {
          const colors = ['#6c5ce7', '#00b894']
          return colors[params.dataIndex % 2]
        },
        borderRadius: [8, 8, 0, 0]
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
.payments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.payments-amount {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
}

.payments-change {
  font-weight: 500;
  font-size: 0.9rem;
}

.payments-change.negative {
  color: #d63031;
}

.payments-chart {
  width: 150px;
  height: 60px;
}

.bar-chart {
  width: 100%;
  height: 100%;
}

.payments-methods {
  margin-top: 2rem;
}

.payments-methods h4 {
  font-size: 1rem;
  margin-bottom: 1rem;
  color: #333;
  opacity: 0.8;
}

.methods-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.method {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 10px;
  transition: all 0.3s ease;
}

.method:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.method-info {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.method-color {
  width: 15px;
  height: 15px;
  border-radius: 3px;
}

.method-name {
  font-weight: 500;
  font-size: 0.9rem;
}

.method-percent {
  font-weight: 600;
  color: #333;
}
</style>