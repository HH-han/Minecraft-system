<template>
  <div class="hotel-booking">
    <div class="hotel-info">
      <div class="hotel-images">
        <div class="main-image">
          <img :src="hotelData?.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20hotel%20exterior%20building%20with%20pool&image_size=square_hd'" :alt="hotelData?.name || '酒店外观'">
        </div>
        <div class="image-thumbs">
          <div class="thumb" v-for="(img, index) in displayImages" :key="index">
            <img :src="img" :alt="hotelData?.name || '酒店图片'">
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
          <svg t="1778942347251" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10612" width="32" height="32"><path d="M950.784 442.88c0-243.2-197.12-440.32-440.32-440.32S70.144 199.68 70.144 442.88c0 153.6 78.336 288.256 197.632 367.104L510.464 1016.32l242.688-206.336c118.784-78.848 197.632-214.016 197.632-367.104z" fill="#22BCB9" p-id="10613"></path><path d="M292.608 392.96l364.544-120.832c8.192-3.072 17.408 2.048 20.48 10.24a17.44 17.44 0 0 1 0 10.24L556.8 657.152c-3.072 8.192-12.288 13.312-20.48 10.24-4.096-1.024-7.168-4.096-9.216-8.192l-77.824-158.72-158.72-76.8a15.648 15.648 0 0 1-7.168-21.504c1.024-4.096 5.12-7.168 9.216-9.216z" fill="#FFFFFF" p-id="10614"></path></svg>
          <span>{{ hotelData?.address || '城市中心，距离地铁站步行5分钟' }}</span>
        </div>
        <div class="hotel-description">
          <p>{{ hotelData?.description || '这是一家设施齐全、服务周到的豪华酒店。' }}</p>
        </div>
        <div class="hotel-facilities">
          <span class="facility-label">酒店设施：</span>
          <span class="facility-tag" v-for="(tag, index) in displayFacilities" :key="index">{{ tag }}</span>
        </div>
        <div class="hotel-stats">
          <div class="stat-item">
            <span class="stat-icon">
              <svg t="1778942479972" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15264" width="32" height="32"><path d="M511.4368 512m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z" fill="#FF6161" p-id="15265"></path><path d="M286.72 417.8432a256.256 256.256 0 0 0-207.36 105.0624 432.3328 432.3328 0 0 0 285.952 396.032A256.768 256.768 0 0 0 286.72 417.8432z" fill="#FF7D7D" p-id="15266"></path><path d="M342.6304 427.9808h6.7584v293.632h-6.7584a65.3824 65.3824 0 0 1-65.3824-65.3824V493.312a65.3824 65.3824 0 0 1 65.3824-65.3312z" fill="#FFCAC7" p-id="15267"></path><path d="M710.656 390.9632h-136.7552c10.5984-34.9696 24.8832-106.8032-25.088-137.1648C525.312 239.616 469.9136 229.632 471.04 291.84s-2.2528 130.4064-81.1008 136.192v293.632h280.2688a66.8672 66.8672 0 0 0 65.4848-53.4528l40.448-197.1712a66.816 66.816 0 0 0-65.4848-80.0768z" fill="#FFFFFF" p-id="15268"></path></svg>
            </span>
            <span class="stat-value">{{ hotelData?.likeCount || 0 }}</span>
            <span class="stat-label">点赞</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">
              <svg t="1778942513013" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="17483" width="32" height="32"><path d="M517.44768 24.9856c267.4688 5.12 472.61696 207.81056 473.31328 470.1184 0.69632 269.43488-197.34528 476.672-470.56896 478.9248-268.14464 2.21184-474.64448-199.96672-475.87328-473.33376C43.13088 232.67328 249.77408 30.33088 517.44768 24.9856z" fill="#FCEBB8" p-id="17484"></path><path d="M314.44992 763.61728c8.00768-51.69152 12.16512-98.7136 23.4496-143.93344 6.656-26.66496-0.53248-42.35264-18.24768-59.35104-27.62752-26.48064-53.92384-54.41536-80.56832-81.94048-15.81056-16.32256-34.67264-33.54624-26.56256-58.44992 9.23648-28.38528 35.47136-35.328 62.58688-39.05536 34.34496-4.7104 68.42368-11.61216 102.8096-15.83104 27.2384-3.33824 41.984-16.71168 51.79392-42.88512 14.0288-37.31456 31.744-73.5232 50.9952-108.50304 23.57248-42.86464 59.33056-43.25376 83.31264-1.18784 17.2032 30.14656 35.4304 61.11232 44.50304 94.12608 13.14816 47.8208 45.7728 59.84256 88.22784 63.7952 29.30688 2.72384 58.7776 6.47168 87.53152 12.67712 51.79392 11.20256 69.632 55.02976 28.81536 85.27872-102.01088 75.65312-123.1872 167.07584-84.31616 283.72992 13.45536 40.36608-33.34144 73.17504-68.42368 47.26784-93.65504-69.20192-180.61312-72.54016-274.49344-0.47104-33.03424 25.41568-71.0656-0.55296-71.41376-35.26656z" fill="#FEC73C" p-id="17485"></path><path d="M485.0688 412.52864c8.2944 0.55296 19.06688-1.65888 22.44608 7.80288 4.75136 13.23008-6.656 19.2512-16.30208 21.0944-36.59776 7.02464-58.24512 27.42272-63.46752 64.75776-1.26976 9.09312-6.53312 18.37056-15.72864 17.42848-12.30848-1.26976-14.29504-12.63616-14.49984-23.73632-0.77824-40.3456 46.2848-87.47008 87.552-87.3472z" fill="#FEF9EE" p-id="17486"></path></svg>
            </span>
            <span class="stat-value">{{ hotelData?.collectCount || 0 }}</span>
            <span class="stat-label">收藏</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">
              <svg t="1778942547148" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="20992" width="32" height="32"><path d="M0 0h1024v1024H0V0z" fill="#202425" opacity=".01" p-id="20993"></path><path d="M214.152533 142.2336C181.725867 97.041067 213.981867 34.133333 269.585067 34.133333h484.829866c55.6032 0 87.8592 62.907733 55.432534 108.100267l-77.585067 108.066133A136.533333 136.533333 0 0 1 621.329067 307.2h-218.658134a136.533333 136.533333 0 0 1-110.933333-56.900267L214.152533 142.2336z" fill="#FFAA44" p-id="20994"></path><path d="M545.621333 238.933333h-67.242666a409.6 409.6 0 0 0-403.012267 336.315734l-31.470933 173.192533C21.026133 874.154667 117.623467 989.866667 245.418667 989.866667h533.230933c127.761067 0 224.3584-115.712 201.489067-241.425067l-31.470934-173.192533A409.6 409.6 0 0 0 545.5872 238.933333z" fill="#FF7744" p-id="20995"></path><path d="M351.3344 453.7344a34.133333 34.133333 0 0 1 48.264533 0L512 566.135467l112.401067-112.401067a34.133333 34.133333 0 0 1 48.264533 48.264533L594.397867 580.266667H648.533333a34.133333 34.133333 0 1 1 0 68.266666h-102.4v51.2h102.4a34.133333 34.133333 0 1 1 0 68.266667h-102.4v68.266667a34.133333 34.133333 0 1 1-68.266666 0v-68.266667h-102.4a34.133333 34.133333 0 1 1 0-68.266667h102.4V648.533333h-102.4a34.133333 34.133333 0 1 1 0-68.266666h54.135466l-78.267733-78.267734a34.133333 34.133333 0 0 1 0-48.264533z" fill="#FFFFFF" p-id="20996"></path></svg>
            </span>
            <span class="stat-value">¥{{ hotelData?.price || 0 }}</span>
            <span class="stat-label">起/晚</span>
          </div>
        </div>
      </div>
    </div>

    <div class="booking-form">
      <h3>预订信息</h3>

      <DatePicker
        :dateFields="dateFields"
        @dateChange="handleDateChange"
      />

      <div class="guest-info">
        <label>住客</label>
        <div class="guest-selector">
          <button @click="decreaseGuests" :disabled="guests <= 1">-</button>
          <span>{{ guests }}人</span>
          <button @click="increaseGuests" :disabled="guests >= 10">+</button>
        </div>
      </div>

      <RoomSelector
        :rooms="rooms"
        :selectedRoom="selectedRoom"
        @roomSelect="selectRoom"
      />

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

<script setup>
import { defineProps, ref, computed } from 'vue'
import DatePicker from './DatePicker.vue'
import RoomSelector from './RoomSelector.vue'
import BookingForm from './BookingForm.vue'

const props = defineProps({
  dateFields: {
    type: Array,
    required: true
  },
  facilities: {
    type: Array,
    default: () => []
  },
  rooms: {
    type: Array,
    required: true
  },
  hotelData: {
    type: Object,
    default: null
  }
})

const checkInDate = ref('')
const checkOutDate = ref('')
const guests = ref(2)
const selectedRoom = ref(null)

const nights = computed(() => {
  if (!checkInDate.value || !checkOutDate.value) return 0
  const start = new Date(checkInDate.value)
  const end = new Date(checkOutDate.value)
  return Math.ceil((end - start) / (1000 * 60 * 60 * 24))
})

const totalPrice = computed(() => {
  return selectedRoom.value ? selectedRoom.value.price * nights.value : 0
})

const serviceFee = computed(() => {
  return Math.round(totalPrice.value * 0.1)
})

const displayFacilities = computed(() => {
  const facs = props.facilities && props.facilities.length > 0 ? props.facilities : ['停车场', '免费WiFi', '餐厅', '健身房']
  return [...new Set(facs)]
})

const displayImages = computed(() => {
  const images = props.hotelData?.images || []
  const defaultImages = [
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20room%20interior&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20bathroom&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20swimming%20pool&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20lobby&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20restaurant&image_size=square'
  ]
  
  return images.slice(0, 5).map((img, index) => {
    if (img && img.trim()) return img.trim()
    return defaultImages[index % defaultImages.length]
  })
})

const handleDateChange = (dateData) => {
  if (dateData.checkInDate) {
    checkInDate.value = dateData.checkInDate
  }
  if (dateData.checkOutDate) {
    checkOutDate.value = dateData.checkOutDate
  }
}

const increaseGuests = () => {
  if (guests.value < 10) {
    guests.value++
  }
}

const decreaseGuests = () => {
  if (guests.value > 1) {
    guests.value--
  }
}

const selectRoom = (room) => {
  selectedRoom.value = room
}

const submitBooking = (bookingData) => {
  if (!checkInDate.value || !checkOutDate.value) {
    alert('请选择入住和离店日期')
    return
  }
  console.log('预订信息:', {
    ...bookingData,
    checkInDate: checkInDate.value,
    checkOutDate: checkOutDate.value,
    guests: guests.value,
    room: selectedRoom.value
  })
  alert('预订提交成功！')
}
</script>

<style scoped>
.hotel-booking {
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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.hotel-name {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #1a1a1a;
}

.hotel-rating {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.rating {
  background-color: #ff6a00;
  color: #ffffff;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: bold;
  margin-right: 10px;
  box-shadow: 0 2px 8px rgba(255, 106, 0, 0.3);
}

.rating-text {
  color: #ff6a00;
  font-weight: 600;
  margin-right: 10px;
}

.review-count {
  color: #5a5a5a;
}

.hotel-location {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  color: #4a4a4a;
}

.location-icon {
  margin-right: 8px;
}

.hotel-description {
  margin-bottom: 15px;
}

.hotel-description p {
  color: #4a4a4a;
  line-height: 1.7;
  font-size: 14px;
  margin: 0;
}

.hotel-facilities {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.facility-label {
  font-size: 14px;
  color: #3d3d3d;
  font-weight: 600;
}

.facility-tag {
  background: rgba(255, 106, 0, 0.08);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: #ff6a00;
  border: 1px solid rgba(255, 106, 0, 0.2);
}

.hotel-stats {
  display: flex;
  gap: 25px;
  padding-top: 15px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
}

.stat-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 4px;
}

.stat-icon {
  font-size: 18px;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #1a1a1a;
}

.stat-label {
  font-size: 12px;
  color: #666666;
}

.booking-form {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.booking-form h3 {
  margin-bottom: 25px;
  font-size: 20px;
  font-weight: bold;
  color: #1a1a1a;
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
  font-weight: 600;
  color: #3d3d3d;
}

.date-picker input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.8);
  color: #333333;
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
  font-weight: 600;
  color: #3d3d3d;
}

.guest-selector {
  display: flex;
  align-items: center;
  width: 150px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.8);
}

.guest-selector button {
  width: 30px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  color: #333333;
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
  font-weight: 600;
  color: #1a1a1a;
}

.room-selection {
  margin-bottom: 35px;
}

.room-selection h4 {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #1a1a1a;
}

.room-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  margin-bottom: 15px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.room-option:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: rgba(255, 106, 0, 0.2);
}

.room-info {
  flex: 1;
}

.room-info h5 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #1a1a1a;
}

.room-info p {
  color: #5a5a5a;
  margin-bottom: 12px;
  font-size: 14px;
}

.room-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.room-facilities .facility {
  background: rgba(255, 106, 0, 0.08);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #ff6a00;
  border: 1px solid rgba(255, 106, 0, 0.15);
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
  color: #5a5a5a;
}

.book-btn {
  padding: 10px 24px;
  background-color: #ff6a00;
  color: #ffffff;
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
  background: rgba(255, 255, 255, 0.9);
  padding: 25px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.booking-details h4 {
  margin-bottom: 25px;
  font-size: 18px;
  font-weight: bold;
  color: #1a1a1a;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #3d3d3d;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.8);
  color: #333333;
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
  font-weight: 600;
  color: #3d3d3d;
}

.special-request textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  min-height: 100px;
  background: rgba(255, 255, 255, 0.8);
  color: #333333;
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
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: #3d3d3d;
}

.price-item.total {
  font-weight: bold;
  font-size: 18px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  color: #ff6a00;
}

.submit-btn {
  width: 100%;
  padding: 16px;
  background-color: #ff6a00;
  color: #ffffff;
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

  .hotel-stats {
    gap: 15px;
  }
}
</style>
