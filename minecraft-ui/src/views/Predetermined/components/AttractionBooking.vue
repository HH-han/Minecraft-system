<template>
  <div class="attraction-booking">
    <div class="attraction-info">
      <div class="attraction-images">
        <div class="main-image">
          <img :src="attractionData?.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20scenic%20spot%20landscape%20mountain%20lake&image_size=square_hd'" :alt="attractionData?.name || '景点景观'">
        </div>
        <div class="image-thumbs">
          <div class="thumb" v-for="i in 5" :key="i">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=scenic%20view%20tourist%20attraction%20natural%20beauty&image_size=square" :alt="attractionData?.name || '景点图片'">
          </div>
        </div>
      </div>
      <div class="attraction-details">
        <h2 class="attraction-name">{{ attractionData?.name || '国家5A级风景区' }}</h2>
        <div class="attraction-rating">
          <span class="rating">{{ attractionData?.rating || 4.9 }}</span>
          <span class="rating-text">{{ attractionData?.rating >= 4.5 ? '极好' : attractionData?.rating >= 4 ? '很好' : '好' }}</span>
          <span class="review-count">({{ attractionData?.commentCount || 2345 }}条点评)</span>
        </div>
        <div class="attraction-location">
          <i class="location-icon">📍</i>
          <span>{{ attractionData?.address || '风景秀丽的山区，距离市中心约1小时车程' }}</span>
        </div>
        <div class="attraction-tags">
          <span class="tag" v-for="tag in tags" :key="tag">{{ tag }}</span>
        </div>
        <div class="attraction-facilities">
          <span class="facility-label">配套设施：</span>
          <span v-if="loadingFacilities" class="loading-text">加载中...</span>
          <span class="facility-tag" v-for="(facility, index) in displayFacilities" :key="index">{{ facility }}</span>
        </div>
        <div class="attraction-time">
          <i class="time-icon">⏰</i>
          <span>开放时间：08:00-17:30</span>
        </div>
      </div>
    </div>

    <div class="booking-form">
      <h3>门票预订</h3>
      
      <DatePicker 
        :dateFields="dateFields"
        @dateChange="handleDateChange"
      />

      <div v-if="loadingTickets" class="loading-container">
        <span class="loading-text">加载门票中...</span>
      </div>
      <TicketSelector 
        v-else
        :tickets="tickets"
        :initialQuantities="ticketQuantities"
        @quantityChange="handleQuantityChange"
      />

      <div v-if="hasSelectedTickets">
        <BookingForm 
          :totalPrice="totalPrice"
          :showSpecialRequest="false"
          @submit="submitBooking"
        >
          <template #additional-info>
            <div v-for="ticket in selectedTickets" :key="ticket.id">
              <TouristInfoForm 
                :ticketName="ticket.name"
                :quantity="ticketQuantities[ticket.id]"
                :initialTourists="tourists[ticket.id] || []"
                @touristInfoChange="(info) => handleTouristInfoChange(ticket.id, info)"
              />
            </div>
          </template>
          <template #price-items>
            <div class="price-item" v-for="ticket in selectedTickets" :key="ticket.id">
              <span>{{ ticket.name }} ({{ ticketQuantities[ticket.id] }}人)</span>
              <span>¥{{ ticket.price * ticketQuantities[ticket.id] }}</span>
            </div>
          </template>
        </BookingForm>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, computed, onMounted, watch } from 'vue'
import DatePicker from './DatePicker.vue'
import TicketSelector from './TicketSelector.vue'
import TouristInfoForm from './TouristInfoForm.vue'
import BookingForm from './BookingForm.vue'
import { getAttractionFacilities } from '@/api/attractionFacility'

const props = defineProps({
  dateFields: {
    type: Array,
    required: true
  },
  tags: {
    type: Array,
    required: true
  },
  tickets: {
    type: Array,
    required: true
  },
  attractionData: {
    type: Object,
    default: null
  },
  attractionId: {
    type: [Number, null],
    default: null
  }
})

const visitDate = ref('')
const ticketQuantities = ref({})
const tourists = ref({})
const attractionFacilities = ref([])
const loadingFacilities = ref(false)
const loadingTickets = ref(false)
const displayFacilities = computed(() => {
  return attractionFacilities.value.length > 0 
    ? attractionFacilities.value.map(f => f.facilityName || f)
    : ['停车场', '休息区', '卫生间', '观光车']
})

const initTicketQuantities = () => {
  const initial = {}
  props.tickets.forEach(ticket => {
    initial[ticket.id] = 0
  })
  ticketQuantities.value = initial
}

const selectedTickets = computed(() => {
  return props.tickets.filter(ticket => ticketQuantities.value[ticket.id] > 0)
})

const hasSelectedTickets = computed(() => {
  return selectedTickets.value.length > 0
})

const totalPrice = computed(() => {
  return props.tickets.reduce((total, ticket) => {
    return total + (ticket.price * (ticketQuantities.value[ticket.id] || 0))
  }, 0)
})

const loadAttractionFacilities = async () => {
  if (!props.attractionId) return
  
  loadingFacilities.value = true
  try {
    const response = await getAttractionFacilities(props.attractionId)
    if (response.code === 200 && response.data) {
      attractionFacilities.value = response.data
    }
  } catch (error) {
    console.error('获取景点设施失败:', error)
  } finally {
    loadingFacilities.value = false
  }
}

const handleDateChange = (dateData) => {
  if (dateData.visitDate) {
    visitDate.value = dateData.visitDate
  }
}

const handleQuantityChange = (quantityData) => {
  const ticketId = Object.keys(quantityData)[0]
  ticketQuantities.value[ticketId] = quantityData[ticketId]
  if (!tourists.value[ticketId]) {
    tourists.value[ticketId] = []
  }
}

const handleTouristInfoChange = (ticketId, info) => {
  tourists.value[ticketId] = info
}

const submitBooking = (bookingData) => {
  if (!visitDate.value) {
    alert('请选择游玩日期')
    return
  }
  if (selectedTickets.value.length === 0) {
    alert('请至少选择一种票种')
    return
  }
  let hasEmptyTouristInfo = false
  selectedTickets.value.forEach(ticket => {
    const touristInfo = tourists.value[ticket.id] || []
    for (let i = 0; i < touristInfo.length; i++) {
      if (!touristInfo[i].name || !touristInfo[i].idCard) {
        hasEmptyTouristInfo = true
      }
    }
  })
  if (hasEmptyTouristInfo) {
    alert('请填写完整的游客信息')
    return
  }
  console.log('预订信息:', {
    ...bookingData,
    visitDate: visitDate.value,
    tickets: selectedTickets.value.map(ticket => ({
      ...ticket,
      quantity: ticketQuantities.value[ticket.id],
      tourists: tourists.value[ticket.id] || []
    }))
  })
  alert('预订提交成功！')
}

let debounceTimer = null

const debounceLoadFacilities = () => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }
  debounceTimer = setTimeout(() => {
    loadAttractionFacilities()
  }, 300)
}

onMounted(() => {
  initTicketQuantities()
  debounceLoadFacilities()
})

watch(() => props.attractionId, () => {
  debounceLoadFacilities()
})

watch(() => props.tickets, () => {
  initTicketQuantities()
}, { deep: true })
</script>

<style scoped>
.attraction-booking {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading-container {
  padding: 20px;
  text-align: center;
}

.loading-text {
  color: #999;
  font-size: 14px;
}

.attraction-info {
  display: flex;
  margin-bottom: 30px;
  gap: 20px;
}

.attraction-images {
  flex: 1;
}

.main-image {
  width: 100%;
  height: 400px;
  overflow: hidden;
  border-radius: 15px;
  margin-bottom: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.main-image:hover img {
  transform: scale(1.05);
}

.image-thumbs {
  display: flex;
  gap: 10px;
}

.thumb {
  width: 80px;
  height: 80px;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.thumb:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attraction-details {
  flex: 1;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.attraction-name {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.attraction-rating {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.rating {
  background-color: #ff6a00;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: bold;
  margin-right: 10px;
  box-shadow: 0 2px 8px rgba(255, 106, 0, 0.3);
}

.rating-text {
  color: #ff6a00;
  font-weight: bold;
  margin-right: 10px;
}

.review-count {
  color: #666;
}

.attraction-location {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #666;
}

.location-icon {
  margin-right: 8px;
}

.attraction-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.tag {
  background: rgba(255, 255, 255, 0.3);
  padding: 6px 14px;
  border-radius: 15px;
  font-size: 12px;
  color: #666;
  border: 1px solid rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.tag:hover {
  background: rgba(255, 106, 0, 0.1);
  color: #ff6a00;
  border-color: rgba(255, 106, 0, 0.3);
}

.attraction-facilities {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.facility-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.facility-tag {
  background: rgba(52, 144, 222, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: #3490de;
  border: 1px solid rgba(52, 144, 222, 0.2);
}

.attraction-time {
  display: flex;
  align-items: center;
  color: #666;
}

.time-icon {
  margin-right: 8px;
}

.booking-form {
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.booking-form h3 {
  margin-bottom: 25px;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.date-selection {
  margin-bottom: 25px;
}

.date-picker label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.date-picker input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.date-picker input:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

.ticket-selection {
  margin-bottom: 35px;
}

.ticket-selection h4 {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.ticket-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  margin-bottom: 15px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.ticket-option:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 106, 0, 0.3);
}

.ticket-info {
  flex: 1;
}

.ticket-info h5 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.ticket-info p {
  color: #666;
  margin-bottom: 12px;
  font-size: 14px;
}

.ticket-rules {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.ticket-rules .rule {
  background: rgba(255, 255, 255, 0.3);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.ticket-price {
  text-align: right;
  min-width: 200px;
}

.price {
  margin-bottom: 12px;
}

.price-value {
  font-size: 24px;
  font-weight: bold;
  color: #ff6a00;
}

.price-unit {
  font-size: 14px;
  color: #666;
}

.ticket-quantity {
  display: flex;
  align-items: center;
  width: 120px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  margin-left: auto;
  background: rgba(255, 255, 255, 0.3);
}

.ticket-quantity button {
  width: 30px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.ticket-quantity button:hover {
  background: rgba(255, 106, 0, 0.1);
}

.ticket-quantity button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ticket-quantity span {
  flex: 1;
  text-align: center;
  line-height: 40px;
  font-weight: 500;
}

.booking-details {
  background: rgba(255, 255, 255, 0.3);
  padding: 25px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.booking-details h4 {
  margin-bottom: 25px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.form-group.half {
  flex: 1;
}

.tourist-info {
  margin-top: 25px;
  padding-top: 25px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.tourist-info h5 {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.tourist-form {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 15px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.price-summary {
  margin-top: 25px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: #333;
}

.price-item.total {
  font-weight: bold;
  font-size: 18px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  color: #ff6a00;
}

.submit-btn {
  width: 100%;
  padding: 16px;
  margin-top: 25px;
  background-color: #ff6a00;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 18px;
  font-weight: bold;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(255, 106, 0, 0.3);
}

.submit-btn:hover {
  background-color: #ff8c00;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 106, 0, 0.4);
}

@media (max-width: 768px) {
  .attraction-info {
    flex-direction: column;
  }
  
  .attraction-details {
    padding: 20px;
  }
  
  .booking-form {
    padding: 20px;
  }
  
  .ticket-option {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .ticket-price {
    margin-top: 15px;
    align-self: flex-end;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .form-group.half {
    width: 100%;
  }
}
</style>