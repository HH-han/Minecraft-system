<template>
  <div class="hotel-booking">
    <!-- 酒店基本信息 -->
    <div class="hotel-info">
      <div class="hotel-images">
        <div class="main-image">
          <img :src="hotelData?.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20hotel%20exterior%20building%20with%20pool&image_size=square_hd'" :alt="hotelData?.name || '酒店外观'">
        </div>
        <div class="image-thumbs">
          <div class="thumb" v-for="i in 5" :key="i">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20room%20interior%20bathroom%20swimming%20pool%20lobby&image_size=square" :alt="hotelData?.name || '酒店图片'">
          </div>
        </div>
      </div>
      <div class="hotel-details">
        <h2 class="hotel-name">{{ hotelData?.name || '豪华度假酒店' }}</h2>
        <div class="hotel-rating">
          <span class="rating">{{ hotelData?.rating || 4.8 }}</span>
          <span class="rating-text">{{ hotelData?.rating >= 4.5 ? '超棒' : hotelData?.rating >= 4 ? '很好' : '好' }}</span>
          <span class="review-count">({{ hotelData?.commentCount || 1234 }}条点评)</span>
        </div>
        <div class="hotel-location">
          <i class="location-icon">📍</i>
          <span>{{ hotelData?.address || '城市中心，距离地铁站步行5分钟' }}</span>
        </div>
        <div class="hotel-facilities">
          <span class="facility-tag" v-for="(tag, index) in (hotelData?.tags || facilities)" :key="index">{{ tag }}</span>
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
  props: {
    dateFields: {
      type: Array,
      required: true
    },
    facilities: {
      type: Array,
      required: true
    },
    rooms: {
      type: Array,
      required: true
    },
    hotelData: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      checkInDate: '',
      checkOutDate: '',
      guests: 2,
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

.hotel-details {
  flex: 1;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hotel-name {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.hotel-rating {
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

.hotel-location {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  color: #666;
}

.location-icon {
  margin-right: 8px;
}

.hotel-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.facility-tag {
  background: rgba(255, 255, 255, 0.3);
  padding: 6px 14px;
  border-radius: 15px;
  font-size: 12px;
  color: #666;
  border: 1px solid rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.facility-tag:hover {
  background: rgba(255, 106, 0, 0.1);
  color: #ff6a00;
  border-color: rgba(255, 106, 0, 0.3);
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
  display: flex;
  gap: 20px;
  margin-bottom: 25px;
}

.date-picker {
  flex: 1;
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

.guest-info {
  margin-bottom: 25px;
}

.guest-info label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.guest-selector {
  display: flex;
  align-items: center;
  width: 150px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.3);
}

.guest-selector button {
  width: 30px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.3);
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.guest-selector button:hover {
  background: rgba(255, 106, 0, 0.1);
}

.guest-selector button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.guest-selector span {
  flex: 1;
  text-align: center;
  line-height: 40px;
  font-weight: 500;
}

.room-selection {
  margin-bottom: 35px;
}

.room-selection h4 {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.room-option {
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

.room-option:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 106, 0, 0.3);
}

.room-info {
  flex: 1;
}

.room-info h5 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.room-info p {
  color: #666;
  margin-bottom: 12px;
  font-size: 14px;
}

.room-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.room-facilities .facility {
  background: rgba(255, 255, 255, 0.3);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.room-price {
  text-align: right;
  min-width: 150px;
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

.book-btn {
  padding: 10px 24px;
  background-color: #ff6a00;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 106, 0, 0.3);
}

.book-btn:hover {
  background-color: #ff8c00;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 106, 0, 0.4);
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

.special-request {
  margin-bottom: 25px;
}

.special-request label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.special-request textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  min-height: 100px;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.special-request textarea:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

.price-summary {
  margin-bottom: 25px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.3);
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
  .hotel-info {
    flex-direction: column;
  }
  
  .hotel-details {
    padding: 20px;
  }
  
  .booking-form {
    padding: 20px;
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