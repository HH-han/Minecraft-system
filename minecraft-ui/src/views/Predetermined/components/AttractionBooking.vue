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