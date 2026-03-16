<template>
  <div class="hotel-booking">
    <!-- 酒店基本信息 -->
    <div class="hotel-info">
      <div class="hotel-images">
        <div class="main-image">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20hotel%20exterior%20building%20with%20pool&image_size=square_hd" alt="酒店外观">
        </div>
        <div class="image-thumbs">
          <div class="thumb" v-for="i in 5" :key="i">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20room%20interior%20bathroom%20swimming%20pool%20lobby&image_size=square" alt="酒店图片">
          </div>
        </div>
      </div>
      <div class="hotel-details">
        <h2 class="hotel-name">豪华度假酒店</h2>
        <div class="hotel-rating">
          <span class="rating">4.8</span>
          <span class="rating-text">超棒</span>
          <span class="review-count">(1234条点评)</span>
        </div>
        <div class="hotel-location">
          <i class="location-icon">📍</i>
          <span>城市中心，距离地铁站步行5分钟</span>
        </div>
        <div class="hotel-facilities">
          <span class="facility-tag" v-for="facility in facilities" :key="facility">{{ facility }}</span>
        </div>
      </div>
    </div>

    <!-- 预订表单区域 -->
    <div class="booking-form">
      <h3>预订信息</h3>
      
      <!-- 日期选择 -->
      <DatePicker 
        :dateFields="dateFields"
        @dateChange="handleDateChange"
      />

      <!-- 住客信息 -->
      <div class="guest-info">
        <label>住客</label>
        <div class="guest-selector">
          <button @click="decreaseGuests" :disabled="guests <= 1">-</button>
          <span>{{ guests }}人</span>
          <button @click="increaseGuests" :disabled="guests >= 10">+</button>
        </div>
      </div>

      <!-- 房型选择 -->
      <RoomSelector 
        :rooms="rooms"
        :selectedRoom="selectedRoom"
        @roomSelect="selectRoom"
      />

      <!-- 预订表单 -->
      <div v-if="selectedRoom">
        <BookingForm 
          :totalPrice="totalPrice + serviceFee"
          @submit="submitBooking"
        >
          <template #price-items>
            <div class="price-item">
              <span>房费 ({{ nights }}晚)</span>
              <span>¥{{ totalPrice }}</span>
            </div>
            <div class="price-item">
              <span>服务费</span>
              <span>¥{{ serviceFee }}</span>
            </div>
          </template>
        </BookingForm>
      </div>
    </div>
  </div>
</template>

<script>
import DatePicker from './DatePicker.vue'
import RoomSelector from './RoomSelector.vue'
import BookingForm from './BookingForm.vue'

export default {
  components: {
    DatePicker,
    RoomSelector,
    BookingForm
  },
  data() {
    return {
      dateFields: [
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
      ],
      checkInDate: '',
      checkOutDate: '',
      guests: 2,
      facilities: ['免费WiFi', '游泳池', '健身房', '停车场', '餐厅'],
      rooms: [
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
      ],
      selectedRoom: null
    }
  },
  computed: {
    nights() {
      if (!this.checkInDate || !this.checkOutDate) return 0
      const start = new Date(this.checkInDate)
      const end = new Date(this.checkOutDate)
      return Math.ceil((end - start) / (1000 * 60 * 60 * 24))
    },
    totalPrice() {
      return this.selectedRoom ? this.selectedRoom.price * this.nights : 0
    },
    serviceFee() {
      return Math.round(this.totalPrice * 0.1)
    }
  },
  methods: {
    handleDateChange(dateData) {
      if (dateData.checkInDate) {
        this.checkInDate = dateData.checkInDate
      }
      if (dateData.checkOutDate) {
        this.checkOutDate = dateData.checkOutDate
      }
    },
    increaseGuests() {
      if (this.guests < 10) {
        this.guests++
      }
    },
    decreaseGuests() {
      if (this.guests > 1) {
        this.guests--
      }
    },
    selectRoom(room) {
      this.selectedRoom = room
    },
    submitBooking(bookingData) {
      if (!this.checkInDate || !this.checkOutDate) {
        alert('请选择入住和离店日期')
        return
      }
      // 这里可以添加提交预订的逻辑
      console.log('预订信息:', {
        ...bookingData,
        checkInDate: this.checkInDate,
        checkOutDate: this.checkOutDate,
        guests: this.guests,
        room: this.selectedRoom
      })
      alert('预订提交成功！')
    }
  }
}
</script>

<style scoped>
.hotel-booking {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.hotel-info {
  display: flex;
  margin-bottom: 30px;
  gap: 20px;
}

.hotel-images {
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

.hotel-details {
  flex: 1;
}

.hotel-name {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.hotel-rating {
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

.hotel-location {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #666;
}

.location-icon {
  margin-right: 5px;
}

.hotel-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.facility-tag {
  background-color: #f5f5f5;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
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
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.date-picker {
  flex: 1;
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

.guest-info {
  margin-bottom: 20px;
}

.guest-info label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.guest-selector {
  display: flex;
  align-items: center;
  width: 150px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.guest-selector button {
  width: 30px;
  height: 36px;
  border: none;
  background-color: #f5f5f5;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.guest-selector button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.guest-selector span {
  flex: 1;
  text-align: center;
  line-height: 36px;
}

.room-selection {
  margin-bottom: 30px;
}

.room-selection h4 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.room-option {
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

.room-option:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.room-info {
  flex: 1;
}

.room-info h5 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.room-info p {
  color: #666;
  margin-bottom: 10px;
  font-size: 14px;
}

.room-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.room-facilities .facility {
  background-color: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  color: #666;
}

.room-price {
  text-align: right;
  min-width: 150px;
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

.book-btn {
  padding: 8px 20px;
  background-color: #ff6a00;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.book-btn:hover {
  background-color: #ff8c00;
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

.special-request {
  margin-bottom: 20px;
}

.special-request label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.special-request textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  min-height: 80px;
}

.price-summary {
  margin-bottom: 20px;
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
  .hotel-info {
    flex-direction: column;
  }
  
  .date-selection {
    flex-direction: column;
  }
  
  .room-option {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .room-price {
    margin-top: 15px;
    align-self: flex-end;
  }
}
</style>