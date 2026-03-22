<template>
  <div class="predetermined-container">
    <h1>预订服务</h1>

    <!-- 切换选项卡 -->
    <div class="tab-switcher">
      <button class="tab-btn" :class="{ active: activeTab === 'hotel' }" @click="activeTab = 'hotel'">
        酒店预订
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'attraction' }" @click="activeTab = 'attraction'">
        景点预订
      </button>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <HotelBooking v-if="activeTab === 'hotel'" :dateFields="hotelDateFields" :facilities="hotelFacilities"
        :rooms="hotelRooms" :hotelData="hotelData" />
      <AttractionBooking v-if="activeTab === 'attraction'" :dateFields="attractionDateFields" :tags="attractionTags"
        :tickets="attractionTickets" :attractionData="attractionData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import HotelBooking from './components/HotelBooking.vue'
import AttractionBooking from './components/AttractionBooking.vue'
import { useBookingStore } from '@/stores/bookingStore.js'

const bookingStore = useBookingStore()
const activeTab = ref('hotel')
const attractionData = ref(null)
const hotelData = ref(null)
// 酒店数据
const hotelDateFields = [
  {
    name: 'checkInDate',
    label: '入住日期',
    value: '',
    min: new Date().toISOString().split('T')[0]
  },
  {
    name: 'checkOutDate',
    label: '离店日期',
    value: '',
    min: new Date().toISOString().split('T')[0]
  }
];
const hotelFacilities = ['免费WiFi', '游泳池', '健身房', '停车场', '餐厅'];
const hotelRooms = [
  {
    id: 1,
    name: '豪华大床房',
    description: '20平方米，1张1.8米大床，城市景观',
    facilities: ['免费WiFi', '空调', '电视', '独立卫浴'],
    price: 888
  },
  {
    id: 2,
    name: '豪华双床房',
    description: '25平方米，2张1.2米单人床，城市景观',
    facilities: ['免费WiFi', '空调', '电视', '独立卫浴'],
    price: 988
  },
  {
    id: 3,
    name: '行政套房',
    description: '40平方米，1张2米大床，行政礼遇，城市景观',
    facilities: ['免费WiFi', '空调', '电视', '独立卫浴', '迷你吧', '行政酒廊'],
    price: 1688
  }
];
// 景点数据
const attractionDateFields = [
  {
    name: 'visitDate',
    label: '游玩日期',
    value: '',
    min: new Date().toISOString().split('T')[0]
  }
];
const attractionTags = ['自然风光', '5A景区', '避暑胜地', '亲子游', '摄影天堂'];
const attractionTickets = [
  {
    id: 1,
    name: '成人票',
    description: '适用于18-60周岁成人',
    rules: ['有效期当天一次入园', '需携带身份证'],
    price: 120
  },
  {
    id: 2,
    name: '儿童票',
    description: '适用于1.2-1.5米儿童',
    rules: ['有效期当天一次入园', '需携带身份证或户口本'],
    price: 60
  },
  {
    id: 3,
    name: '老人票',
    description: '适用于60周岁以上老人',
    rules: ['有效期当天一次入园', '需携带身份证'],
    price: 60
  },
  {
    id: 4,
    name: '学生票',
    description: '适用于全日制学生',
    rules: ['有效期当天一次入园', '需携带学生证'],
    price: 80
  }
];

// 组件挂载时从 Pinia 获取数据
onMounted(() => {
  // 从 Pinia 中获取数据
  activeTab.value = bookingStore.getActiveTab
  attractionData.value = bookingStore.getAttractionData
  hotelData.value = bookingStore.getHotelData
  
  // 清除 Pinia 中的数据，避免重复使用
  bookingStore.clearData()
})
</script>

<style scoped>
.predetermined-container {
  margin: 0 auto;
  padding: 20px;
  background-image: url('@/assets/scenery/scenery-2.webp');
  background-size: cover;
  background-position: center;
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