<template>
  <div class="demo">
    <ArcTabs 
      v-model="activeTab" 
      :tabs="tabs"
      @change="handleTabChange"
    >
      <!-- 使用具名插槽，名称对应tab的value -->
      <template #attractions>
        <div class="demo-content">
          <h3>🏔️ 热门景点</h3>
          <p>黄山、九寨沟、张家界...</p>
        </div>
      </template>
      
      <template #hotels>
        <div class="demo-content">
          <h3>🏨 精品酒店</h3>
          <p>希尔顿、悦榕庄、洲际...</p>
        </div>
      </template>
      
      <template #foods>
        <div class="demo-content">
          <h3>🍜 特色美食</h3>
          <p>北京烤鸭、重庆火锅、广式早茶...</p>
        </div>
      </template>
    </ArcTabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Star, HomeFilled, ForkSpoon } from '@element-plus/icons-vue'
import ArcTabs from './TestProject005.vue'

const activeTab = ref('attractions')

const tabs = [
  { label: '热门景点', value: 'attractions', icon: Star, badge: 12 },
  { label: '精品酒店', value: 'hotels', icon: HomeFilled, badge: 8 },
  { label: '特色美食', value: 'foods', icon: ForkSpoon, badge: 5 }
]

const handleTabChange = (value) => {
  console.log('切换到:', value)
}
</script>