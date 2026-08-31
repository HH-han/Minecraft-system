<template>
  <div class="hotel-booking">
    <!-- ====== 产品 Hero：封面 + 信息 ====== -->
    <section class="product-hero card-shell">
      <!-- 封面主图（无缝嵌入卡片，16:9） -->
      <div class="hero-image">
        <img
          :src="hotelData?.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20luxury%20hotel%20exterior%20pool&image_size=square_hd'"
          :alt="hotelData?.name || '酒店外观'"
        >
      </div>

      <!-- 缩略图 row -->
      <div v-if="displayImages?.length" class="hero-thumbs" role="list" aria-label="酒店图集">
        <div
          class="thumb"
          v-for="(img, index) in displayImages"
          :key="index"
          role="listitem"
        >
          <img :src="img" :alt="(hotelData?.name || '酒店图片') + ' ' + (index + 1)">
        </div>
      </div>

      <!-- 信息区 -->
      <div class="hero-details">
        <h2 class="product-name">
          {{ hotelData?.name || '豪华度假酒店' }}
        </h2>

        <!-- 评分 -->
        <div class="rating-row">
          <span class="rating-pill" aria-label="评分">
            {{ hotelData?.rating ?? 4.8 }}
          </span>
          <span class="rating-text">
            {{ (hotelData?.rating ?? 4.8) >= 4.5 ? '超棒' : (hotelData?.rating ?? 4.8) >= 4 ? '很好' : '好' }}
          </span>
          <span class="meta-subtle">
            ({{ hotelData?.commentCount ?? 1234 }}条点评)
          </span>
        </div>

        <!-- 地址 -->
        <div class="meta-row">
          <span class="meta-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 21s-7-6.5-7-12a7 7 0 1 1 14 0c0 5.5-7 12-7 12Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
              <circle cx="12" cy="9" r="2.5" stroke="currentColor" stroke-width="1.8"/>
            </svg>
          </span>
          <span class="meta-text">{{ hotelData?.address || '城市中心，距离地铁站步行5分钟' }}</span>
        </div>

        <!-- 介绍 -->
        <div class="meta-row">
          <span class="meta-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 5h16v14H4z" stroke="currentColor" stroke-width="1.8" rx="2"/>
              <path d="M7 9h10M7 13h10M7 17h6" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
          </span>
          <span class="meta-text">{{ hotelData?.description || '这是一家设施齐全、服务周到的豪华酒店，致力于为每一位住客提供舒适、安心的入住体验。' }}</span>
        </div>

        <!-- 设施标签 -->
        <div v-if="displayFacilities?.length" class="tag-group">
          <span class="tag-label">酒店设施</span>
          <div class="tag-list">
            <span class="tag" v-for="(tag, i) in displayFacilities" :key="i">{{ tag }}</span>
          </div>
        </div>

        <!-- 统计 3 列 -->
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 21s-7-4.5-7-11V6a2 2 0 0 1 2-2h3l2-2h4l2 2h3a2 2 0 0 1 2 2v4c0 6.5-7 11-7 11Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
              </svg>
            </span>
            <span class="stat-value">{{ hotelData?.likeCount ?? 0 }}</span>
            <span class="stat-label">点赞</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
              </svg>
            </span>
            <span class="stat-value">{{ hotelData?.collectCount ?? 0 }}</span>
            <span class="stat-label">收藏</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/>
                <path d="M8 12h8M8 8h8M8 16h5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
              </svg>
            </span>
            <span class="stat-value price-font">¥{{ hotelData?.price ?? 0 }}</span>
            <span class="stat-label">起 / 晚</span>
          </div>
        </div>
      </div>
    </section>

    <!-- ====== 预订详情面板 ====== -->
    <section class="booking-panel card-shell">
      <h3 class="panel-title">预订信息</h3>

      <DatePicker
        :dateFields="dateFields"
        @dateChange="handleDateChange"
      />

      <!-- 住客数量 Stepper -->
      <div class="guest-block">
        <label for="guest-counter" class="field-label">住客</label>
        <div class="stepper" id="guest-counter" role="group" aria-label="住客人数">
          <button
            type="button"
            class="stepper-btn"
            aria-label="减少住客"
            @click="decreaseGuests"
            :disabled="guests <= 1"
          >
            −
          </button>
          <span class="stepper-value" aria-live="polite">{{ guests }}人</span>
          <button
            type="button"
            class="stepper-btn"
            aria-label="增加住客"
            @click="increaseGuests"
            :disabled="guests >= 10"
          >
            +
          </button>
        </div>
      </div>

      <RoomSelector
        :rooms="rooms"
        :selectedRoom="selectedRoom"
        @roomSelect="selectRoom"
      />

      <div v-if="selectedRoom" :key="selectedRoom.id">
        <BookingForm
          :totalPrice="totalPrice + serviceFee"
          @submit="submitBooking"
        >
          <template #price-items>
            <div class="price-item">
              <span>房费（{{ nights }}晚）</span>
              <span>¥{{ totalPrice }}</span>
            </div>
            <div class="price-item">
              <span>服务费</span>
              <span>¥{{ serviceFee }}</span>
            </div>
          </template>
        </BookingForm>
      </div>
    </section>
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
  return Math.max(0, Math.ceil((end - start) / (1000 * 60 * 60 * 24)))
})

const totalPrice = computed(() => {
  return selectedRoom.value ? selectedRoom.value.price * nights.value : 0
})

const serviceFee = computed(() => {
  return Math.round(totalPrice.value * 0.1)
})

const displayFacilities = computed(() => {
  const facs = props.facilities && props.facilities.length > 0
    ? props.facilities
    : ['停车场', '免费WiFi', '餐厅', '健身房']
  return [...new Set(facs)]
})

const displayImages = computed(() => {
  const images = props.hotelData?.images || []
  const defaultImages = [
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=minimal%20hotel%20room%20interior&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=minimal%20hotel%20bathroom&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=minimal%20hotel%20swimming%20pool&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=minimal%20hotel%20lobby&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=minimal%20hotel%20restaurant&image_size=square'
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
  if (guests.value < 10) guests.value++
}

const decreaseGuests = () => {
  if (guests.value > 1) guests.value--
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
/* ===== 容器 ===== */
.hotel-booking {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 56px;
  box-sizing: border-box;
}

.card-shell {
  background: #ffffff;
  border-radius: 28px;
  border: 1px solid #d2d2d6;
  overflow: hidden;
  box-shadow: none;
}

/* ===== Hero ===== */
.product-hero {
  display: flex;
  flex-direction: column;
}

.hero-image {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

.hero-image img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.hero-image:hover img {
  transform: scale(1.015);
}

/* 缩略图 */
.hero-thumbs {
  padding: 0 32px;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  margin-top: 24px;
  box-sizing: border-box;
}

.thumb {
  position: relative;
  aspect-ratio: 1 / 1;
  overflow: hidden;
  border-radius: 14px;
  background: #f5f5f7;
  border: 2px solid transparent;
  transition: transform 0.2s ease, border-color 0.2s ease;
  cursor: default;
}

.thumb:hover {
  transform: scale(1.01);
  border-color: #2997ff;
}

.thumb img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Hero 信息区 */
.hero-details {
  padding: 40px 32px 48px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-name {
  margin: 0;
  font-size: 32px;
  line-height: 1.1;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: #1d1d1f;
}

/* 评分行 */
.rating-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.rating-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  padding: 4px 12px;
  background: #2997ff;
  color: #ffffff;
  border-radius: 40px;
  font-size: 13px;
  font-weight: 600;
  line-height: 1.4;
}

.rating-text {
  font-size: 14px;
  font-weight: 600;
  color: #2997ff;
}

.meta-subtle {
  font-size: 13px;
  color: #6e6e73;
}

/* Meta 行：地址 / 描述 */
.meta-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.meta-icon {
  flex-shrink: 0;
  margin-top: 2px;
  width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #2997ff;
}

.meta-text {
  font-size: 15px;
  line-height: 1.5;
  color: #1d1d1f;
}

/* 设施标签组 */
.tag-group {
  margin-top: 4px;
  padding: 8px 0 4px;
  border-top: 1px solid #f2f2f4;
  border-bottom: 1px solid #f2f2f4;
}

.tag-label {
  display: block;
  margin-bottom: 10px;
  margin-top: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 4px;
}

.tag {
  background: #f5f5f7;
  color: #1d1d1f;
  border-radius: 40px;
  padding: 6px 16px;
  font-size: 13px;
  line-height: 1.4;
}

/* 统计 */
.stats-grid {
  margin-top: 8px;
  padding-top: 24px;
  border-top: 1px solid #d2d2d6;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}

.stat-icon {
  color: #2997ff;
  width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.2;
}

.price-font {
  letter-spacing: -0.01em;
}

.stat-label {
  font-size: 12px;
  color: #6e6e73;
}

/* ===== 预订面板 ===== */
.booking-panel {
  padding: 40px 32px;
  box-sizing: border-box;
}

.panel-title {
  margin: 0 0 24px;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.2;
  color: #1d1d1f;
  letter-spacing: -0.005em;
}

/* 住客 stepper */
.guest-block {
  margin-bottom: 32px;
}

.field-label {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.stepper {
  display: inline-flex;
  align-items: center;
  width: 140px;
  height: 40px;
  border-radius: 40px;
  background: #ffffff;
  border: 1px solid #d2d2d6;
  overflow: hidden;
  transition: border-color 0.2s ease;
}

.stepper:hover {
  border-color: #c7c7cc;
}

.stepper-btn {
  width: 44px;
  height: 40px;
  border: none;
  background: transparent;
  color: #6e6e73;
  font-size: 20px;
  font-weight: 400;
  cursor: pointer;
  line-height: 1;
  transition: color 0.2s ease, background 0.2s ease;
  flex-shrink: 0;
}

.stepper-btn:hover:not(:disabled) {
  color: #2997ff;
  background: rgba(41, 151, 255, 0.06);
}

.stepper-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.stepper-value {
  flex: 1;
  text-align: center;
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1;
  user-select: none;
}

.stepper-btn:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: -2px;
}

/* ===== 响应式：平板（≤1199） ===== */
@media (max-width: 1199px) {
  .hotel-booking {
    gap: 40px;
  }

  .hero-thumbs {
    padding: 0 24px;
    grid-template-columns: repeat(3, 1fr);
    margin-top: 20px;
  }

  .hero-details {
    padding: 32px 24px 40px;
  }

  .product-name {
    font-size: 28px;
  }

  .booking-panel {
    padding: 32px 24px;
  }
}

/* ===== 响应式：手机（≤767） ===== */
@media (max-width: 767px) {
  .hotel-booking {
    gap: 32px;
  }

  .card-shell {
    border-radius: 24px;
  }

  .hero-thumbs {
    padding: 0 16px;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;
    margin-top: 16px;
  }

  .thumb {
    border-radius: 12px;
  }

  .hero-details {
    padding: 24px 16px 32px;
    gap: 14px;
  }

  .product-name {
    font-size: 24px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    padding-top: 20px;
  }

  .stat-item {
    flex-direction: row;
    align-items: center;
    gap: 10px;
  }

  .stat-icon {
    order: 1;
  }
  .stat-label {
    order: 2;
    margin-left: auto;
  }
  .stat-value {
    order: 3;
  }

  .booking-panel {
    padding: 24px 16px;
  }

  .panel-title {
    font-size: 20px;
    margin-bottom: 20px;
  }

  .guest-block {
    margin-bottom: 24px;
  }
}
</style>
