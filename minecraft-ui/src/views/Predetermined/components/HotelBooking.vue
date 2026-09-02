<template>
  <div class="hotel-booking">
    <!-- ====== 产品 Hero：封面 + 信息（结构对齐 AttractionBooking） ====== -->
    <section class="product-hero card-shell">
      <!-- 封面主图（16:9 无 hover 大放大，Apple 细微） -->
      <div class="hero-image">
        <img
          :src="hotelData?.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=modern%20luxury%20hotel%20exterior%20pool&image_size=landscape_16_9'"
          :alt="hotelData?.name || '酒店外观'"
        />
      </div>

      <!-- 缩略图 grid（padding 顶部留白，对齐 AttractionBooking） -->
      <div v-if="displayImages?.length" class="hero-thumbs" role="list" aria-label="酒店图集">
        <div
          class="thumb"
          v-for="(img, index) in displayImages"
          :key="index"
          role="listitem"
        >
          <img :src="img" :alt="(hotelData?.name || '酒店图片') + ' ' + (index + 1)" />
        </div>
      </div>

      <!-- 信息区 -->
      <div class="hero-details">
        <!-- ===== Title Row（H2 + Rating 包裹，对齐景点） ===== -->
        <div class="title-row">
          <h2 class="product-name">
            {{ hotelData?.name || '豪华度假酒店' }}
          </h2>
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
        </div>

        <!-- 地址（18×18 stroke SVG，无外层 span wrapper，对齐景点 meta） -->
        <div class="meta-row">
          <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M12 21s-7-6.5-7-12a7 7 0 1 1 14 0c0 5.5-7 12-7 12Z"/>
            <circle cx="12" cy="9" r="2.5"/>
          </svg>
          <span class="meta-text">{{ hotelData?.address || '城市中心，距离地铁站步行5分钟' }}</span>
        </div>

        <!-- 介绍（18×18 stroke SVG） -->
        <div class="meta-row">
          <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M4 5h16v14H4z" rx="2"/>
            <path d="M7 9h10M7 13h10M7 17h6"/>
          </svg>
          <span class="meta-text">{{ hotelData?.description || '这是一家设施齐全、服务周到的豪华酒店，致力于为每一位住客提供舒适、安心的入住体验。' }}</span>
        </div>

        <!-- 设施标签组（tag-group 双 border，letter-spacing 0.03em 对齐景点） -->
        <div v-if="displayFacilities?.length" class="tag-group tag-group--facilities">
          <span class="tag-label">酒店设施</span>
          <div class="tag-list">
            <span class="tag-chip" v-for="(tag, i) in displayFacilities" :key="i">{{ tag }}</span>
          </div>
        </div>

        <!-- Stats Grid 3 列 浅灰卡片横排（对齐景点，含 stat-content wrapper + 气泡图标 "起/晚"） -->
        <div class="stats-grid">
          <div class="stat-item">
            <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/>
            </svg>
            <div class="stat-content">
              <span class="stat-value">{{ hotelData?.likeCount ?? 0 }}</span>
              <span class="stat-label">点赞</span>
            </div>
          </div>
          <div class="stat-item">
            <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="m19 21-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
            </svg>
            <div class="stat-content">
              <span class="stat-value">{{ hotelData?.collectCount ?? 0 }}</span>
              <span class="stat-label">收藏</span>
            </div>
          </div>
          <div class="stat-item">
            <!-- 气泡图标 与 景点Attraction 一致（统一 3 张图 3 种语义 SVGs 视觉家族） -->
            <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
            </svg>
            <div class="stat-content">
              <span class="stat-value price-font">¥{{ hotelData?.price ?? 0 }}</span>
              <span class="stat-label">起 / 晚</span>
            </div>
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
/* ===== 容器（与 AttractionBooking 完全一致类名/间距/尺寸） ===== */
.hotel-booking {
  display: flex;
  flex-direction: column;
  gap: 56px;
  width: 100%;
  box-sizing: border-box;
}

/* ===== Card Shell（与景点一致：Apple 多层浅阴影，删除1px border） ===== */
.card-shell {
  background: #ffffff;
  border-radius: 28px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 8px 24px -12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  box-sizing: border-box;
}

/* ============================================================
   PRODUCT HERO (upper card) — 与 AttractionBooking 对称
   ============================================================ */
.product-hero {
  display: flex;
  flex-direction: column;
}

/* --- Hero Cover (16:9 deterministic，无 hover 大放大动画) --- */
.hero-image {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  background: #f5f5f7;
}
.hero-image img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* --- Thumbnails (padding 顶部留白 20px 与景点一致，取消 margin-top) --- */
.hero-thumbs {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  padding: 20px 32px 0 32px;
  box-sizing: border-box;
}
.thumb {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 14px;
  overflow: hidden;
  background: #f5f5f7;
  cursor: default;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.thumb:hover {
  transform: scale(1.005);
  box-shadow: 0 8px 20px -10px rgba(0, 0, 0, 0.15);
}
.thumb:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
}
.thumb img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* --- Hero Details Section（与景点完全一致 padding/gap） --- */
.hero-details {
  padding: 40px 32px 48px 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  box-sizing: border-box;
}

/* Title + Rating 包裹层 */
.title-row {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.product-name {
  margin: 0;
  font-size: 32px;
  line-height: 1.2;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.01em;
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
  background: #2997ff;
  color: #ffffff;
  padding: 4px 14px;
  border-radius: 40px;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.01em;
}
.rating-text {
  color: #2997ff;
  font-weight: 600;
  font-size: 14px;
}
.meta-subtle {
  color: #6e6e73;
  font-size: 14px;
}

/* --- Meta rows (Address + Description，18×18 stroke SVG，与景点尺寸/颜色一致) --- */
.meta-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  color: #6e6e73;
  font-size: 14px;
  line-height: 1.6;
}
.meta-icon {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  margin-top: 2px;
  color: #2997ff;
}
.meta-text {
  flex: 1;
  min-width: 0;
}

/* --- Facilities Tag Group（与景点.tag-group--facilities 完全同结构） --- */
.tag-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px 0;
}
.tag-group--facilities {
  border-top: 1px solid #f2f2f4;
  border-bottom: 1px solid #f2f2f4;
}
.tag-label {
  font-size: 12px;
  font-weight: 600;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.03em; /* 与景点一致 0.03，Apple 大写字母间距 */
}
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.tag-chip {
  display: inline-flex;
  align-items: center;
  padding: 5px 14px;
  background: #f5f5f7;
  color: #1d1d1f;
  font-size: 12px;
  border-radius: 40px;
  line-height: 1.4;
  border: 1px solid transparent;
  transition: background 0.2s ease;
}
.tag-chip:hover {
  background: #ebebef;
}

/* --- Stats Grid (3 cols，浅灰卡片 横排，与景点完全一致) --- */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding-top: 16px;
}
.stat-item {
  display: flex;
  align-items: center;  /* 与景点：row 横排 左icon右内容 */
  gap: 12px;
  padding: 16px;
  background: #f5f5f7;
  border-radius: 16px;
  box-sizing: border-box;
  min-width: 0;
}
.stat-icon {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  color: #2997ff;
}
.stat-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.stat-value {
  font-size: 18px;
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
  line-height: 1.3;
}

/* ============================================================
   BOOKING PANEL (lower card)
   ============================================================ */
.booking-panel {
  padding: 40px 32px;
  box-sizing: border-box;
}
.panel-title {
  margin: 0 0 24px 0;
  font-size: 22px;
  line-height: 1.3;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.005em;
}

/* 住客 stepper（维持原样，景点没有 stepper） */
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

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  font-size: 14px;
  color: #1d1d1f;
  line-height: 1.5;
  gap: 12px;
}

/* ============================================================
   RESPONSIVE BREAKPOINTS（与 AttractionBooking 三档 100% 对齐）
   ============================================================ */
/* Tablet 768 - 1199 */
@media (max-width: 1199px) {
  .hotel-booking {
    gap: 40px;
  }
  .hero-thumbs {
    grid-template-columns: repeat(3, 1fr);
    padding: 16px 24px 0 24px;
    gap: 10px;
  }
  .hero-details {
    padding: 32px 24px 40px 24px;
    gap: 14px;
  }
  .product-name {
    font-size: 28px;
  }
  .booking-panel {
    padding: 32px 24px;
  }
}

/* Mobile <=767 （stats-grid：3 列 竖排小卡片，与景点一致；取消旧 1 列 order flex） */
@media (max-width: 767px) {
  .hotel-booking {
    gap: 32px;
  }
  .card-shell {
    border-radius: 24px;
  }
  .hero-thumbs {
    grid-template-columns: repeat(4, 1fr);
    padding: 12px 16px 0 16px;
    gap: 8px;
  }
  .thumb {
    border-radius: 12px;
  }
  .hero-details {
    padding: 24px 16px 32px 16px;
    gap: 12px;
  }
  .product-name {
    font-size: 24px;
  }
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  .stat-item {
    flex-direction: column;  /* 手机：icon上、文字下，竖排 */
    align-items: flex-start;
    gap: 6px;
    padding: 12px 10px;
    border-radius: 12px;
  }
  .stat-icon {
    width: 18px;
    height: 18px;
  }
  .stat-value {
    font-size: 16px;
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
