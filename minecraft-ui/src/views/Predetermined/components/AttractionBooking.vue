<template>
  <div class="attraction-booking">
    <!-- 景点基本信息 -->
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
        <div class="attraction-time">
          <i class="time-icon">⏰</i>
          <span>开放时间：08:00-17:30</span>
        </div>
      </div>
    </div>

    <!-- 预订表单区域 -->
    <div class="booking-form">
      <h3>门票预订</h3>
      
      <!-- 日期选择 -->
      <DatePicker 
        :dateFields="dateFields"
        @dateChange="handleDateChange"
      />

      <!-- 票种选择 -->
      <TicketSelector 
        :tickets="tickets"
        :initialQuantities="ticketQuantities"
        @quantityChange="handleQuantityChange"
      />

      <!-- 预订表单 -->
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

<script>
import DatePicker from './DatePicker.vue'
import TicketSelector from './TicketSelector.vue'
import TouristInfoForm from './TouristInfoForm.vue'
import BookingForm from './BookingForm.vue'

export default {
  components: {
    DatePicker,
    TicketSelector,
    TouristInfoForm,
    BookingForm
  },
  props: {
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
    }
  },
  data() {
    return {
      visitDate: '',
      ticketQuantities: {
        1: 0,
        2: 0,
        3: 0,
        4: 0
      },
      tourists: {}
    }
  },
  computed: {
    selectedTickets() {
      return this.tickets.filter(ticket => this.ticketQuantities[ticket.id] > 0)
    },
    hasSelectedTickets() {
      return this.selectedTickets.length > 0
    },
    totalPrice() {
      return this.tickets.reduce((total, ticket) => {
        return total + (ticket.price * this.ticketQuantities[ticket.id])
      }, 0)
    }
  },
  methods: {
    handleDateChange(dateData) {
      if (dateData.visitDate) {
        this.visitDate = dateData.visitDate
      }
    },
    handleQuantityChange(quantityData) {
      const ticketId = Object.keys(quantityData)[0]
      this.ticketQuantities[ticketId] = quantityData[ticketId]
      // 初始化游客信息数组
      if (!this.tourists[ticketId]) {
        this.tourists[ticketId] = []
      }
    },
    handleTouristInfoChange(ticketId, info) {
      this.tourists[ticketId] = info
    },
    submitBooking(bookingData) {
      if (!this.visitDate) {
        alert('请选择游玩日期')
        return
      }
      if (this.selectedTickets.length === 0) {
        alert('请至少选择一种票种')
        return
      }
      // 检查游客信息
      let hasEmptyTouristInfo = false
      this.selectedTickets.forEach(ticket => {
        const touristInfo = this.tourists[ticket.id] || []
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
      // 这里可以添加提交预订的逻辑
      console.log('预订信息:', {
        ...bookingData,
        visitDate: this.visitDate,
        tickets: this.selectedTickets.map(ticket => ({
          ...ticket,
          quantity: this.ticketQuantities[ticket.id],
          tourists: this.tourists[ticket.id] || []
        }))
      })
      alert('预订提交成功！')
    }
  }
}
</script>

<style scoped>
.attraction-booking {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
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
  border-radius: 8px;
  margin-bottom: 10px;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-thumbs {
  display: flex;
  gap: 10px;
}

.thumb {
  width: 80px;
  height: 80px;
  overflow: hidden;
  border-radius: 4px;
  cursor: pointer;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attraction-details {
  flex: 1;
}

.attraction-name {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.attraction-rating {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.rating {
  background-color: #ff6a00;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: bold;
  margin-right: 10px;
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
  margin-bottom: 10px;
  color: #666;
}

.location-icon {
  margin-right: 5px;
}

.attraction-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 10px;
}

.tag {
  background-color: #f5f5f5;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
}

.attraction-time {
  display: flex;
  align-items: center;
  color: #666;
}

.time-icon {
  margin-right: 5px;
}

.booking-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.booking-form h3 {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
}

.date-selection {
  margin-bottom: 20px;
}

.date-picker label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.date-picker input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.ticket-selection {
  margin-bottom: 30px;
}

.ticket-selection h4 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.ticket-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: white;
  border-radius: 8px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  transition: all 0.3s ease;
}

.ticket-option:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ticket-info {
  flex: 1;
}

.ticket-info h5 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.ticket-info p {
  color: #666;
  margin-bottom: 10px;
  font-size: 14px;
}

.ticket-rules {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.ticket-rules .rule {
  background-color: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  color: #666;
}

.ticket-price {
  text-align: right;
  min-width: 200px;
}

.price {
  margin-bottom: 10px;
}

.price-value {
  font-size: 20px;
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
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  margin-left: auto;
}

.ticket-quantity button {
  width: 30px;
  height: 36px;
  border: none;
  background-color: #f5f5f5;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.ticket-quantity button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ticket-quantity span {
  flex: 1;
  text-align: center;
  line-height: 36px;
}

.booking-details {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.booking-details h4 {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.form-group.half {
  flex: 1;
}

.tourist-info {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.tourist-info h5 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: bold;
}

.tourist-form {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.price-summary {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.price-item.total {
  font-weight: bold;
  font-size: 16px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #ddd;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  margin-top: 20px;
  background-color: #ff6a00;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: #ff8c00;
}

@media (max-width: 768px) {
  .attraction-info {
    flex-direction: column;
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