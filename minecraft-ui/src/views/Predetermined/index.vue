<template>
  <div class="predetermined-container">
    <h1>预订服务</h1>
    
    <!-- 切换选项卡 -->
    <div class="tab-switcher">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'hotel' }" 
        @click="activeTab = 'hotel'"
      >
        酒店预订
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'attraction' }" 
        @click="activeTab = 'attraction'"
      >
        景点预订
      </button>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <HotelBooking v-if="activeTab === 'hotel'" />
      <AttractionBooking v-if="activeTab === 'attraction'" />
    </div>
  </div>
</template>

<script>
import HotelBooking from './components/HotelBooking.vue'
import AttractionBooking from './components/AttractionBooking.vue'

export default {
  components: {
    HotelBooking,
    AttractionBooking
  },
  data() {
    return {
      activeTab: 'hotel'
    }
  }
}
</script>

<style scoped>
.predetermined-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.tab-switcher {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  gap: 20px;
}

.tab-btn {
  padding: 12px 30px;
  border: 2px solid #ff6a00;
  border-radius: 25px;
  background-color: white;
  color: #ff6a00;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  background-color: #fff5ee;
}

.tab-btn.active {
  background-color: #ff6a00;
  color: white;
}

.content-area {
  min-height: 600px;
}

@media (max-width: 768px) {
  .predetermined-container {
    padding: 10px;
  }
  
  h1 {
    font-size: 24px;
  }
  
  .tab-switcher {
    flex-direction: column;
    align-items: center;
  }
  
  .tab-btn {
    width: 80%;
    text-align: center;
  }
}
</style>