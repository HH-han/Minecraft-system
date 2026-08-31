<template>
  <div class="attraction-booking">
    <!-- ========== Product Hero Card ========== -->
    <section class="product-hero card-shell">
      <!-- 16:9 Cover -->
      <div class="hero-image">
        <img
          :src="attractionData?.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20scenic%20spot%20landscape%20mountain%20lake&image_size=landscape_16_9'"
          :alt="attractionData?.name || '景点景观'"
        />
      </div>

      <!-- Thumbnails grid (5 desktop, 3 tablet, 4 mobile) -->
      <div class="hero-thumbs">
        <div
          class="thumb"
          v-for="(img, index) in displayImages"
          :key="index"
          role="img"
          :aria-label="'景点缩略图 ' + (index + 1)"
        >
          <img :src="img" :alt="attractionData?.name || '景点图片'" />
        </div>
      </div>

      <!-- Details -->
      <div class="hero-details">
        <!-- Title + Rating row -->
        <div class="title-row">
          <h2 class="product-name">{{ attractionData?.name || '国家5A级风景区' }}</h2>
          <div class="rating-row">
            <span class="rating-pill" aria-label="评分">{{ attractionData?.rating || 4.9 }}</span>
            <span class="rating-text">{{ (attractionData?.rating || 4.9) >= 4.5 ? '极好' : (attractionData?.rating || 4.9) >= 4 ? '很好' : '好' }}</span>
            <span class="review-count">({{ attractionData?.commentCount || 2345 }}条点评)</span>
          </div>
        </div>

        <!-- Meta: Address + Description -->
        <div class="meta-rows">
          <div class="meta-row">
            <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
            <span class="meta-text">{{ attractionData?.address || '风景秀丽的山区，距离市中心约1小时车程' }}</span>
          </div>
          <div class="meta-row">
            <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
            <span class="meta-text">{{ attractionData?.description || '这是一处风景秀丽的旅游胜地。' }}</span>
          </div>
        </div>

        <!-- Attraction-specific Tags row -->
        <div v-if="tags && tags.length > 0" class="tag-group tag-group--tags">
          <span class="tag-label">景点标签</span>
          <div class="tag-list">
            <span class="tag-chip" v-for="(tag, i) in tags" :key="'tag-'+i">{{ tag }}</span>
          </div>
        </div>

        <!-- Facilities tag group -->
        <div class="tag-group tag-group--facilities">
          <span class="tag-label">配套设施</span>
          <div class="tag-list">
            <span class="tag-chip" v-for="(facility, i) in displayFacilities" :key="'fac-'+i">{{ facility }}</span>
          </div>
        </div>

        <!-- Attraction meta: Open Time + Best Season (NEW 2 stroke SVGs) -->
        <div class="attraction-meta">
          <div class="meta-row">
            <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            <span class="meta-text"><strong>开放时间</strong>：08:00 - 17:30</span>
          </div>
          <div class="meta-row">
            <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19.2 2.5c.7.7 1 1.9.4 3.6-1.3 3.5-3.5 6.4-7.1 8.7-1.2.8-2.5 1.6-3.6 2.5-.7.6-.3 2 .7 2.2"></path>
              <path d="M2 22c1.25-1.25 2.5-2.5 3.5-3.5"></path>
            </svg>
            <span class="meta-text"><strong>最佳季节</strong>：{{ attractionData?.season || '四季皆宜' }}</span>
          </div>
        </div>

        <!-- Stats grid (3 cols) -->
        <div class="stats-grid">
          <div class="stat-item">
            <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"></path>
            </svg>
            <div class="stat-content">
              <span class="stat-value">{{ attractionData?.likeCount || 0 }}</span>
              <span class="stat-label">点赞</span>
            </div>
          </div>
          <div class="stat-item">
            <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="m19 21-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
            </svg>
            <div class="stat-content">
              <span class="stat-value">{{ attractionData?.collectCount || 0 }}</span>
              <span class="stat-label">收藏</span>
            </div>
          </div>
          <div class="stat-item">
            <svg class="stat-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
            </svg>
            <div class="stat-content">
              <span class="stat-value">¥{{ attractionData?.price || 0 }}</span>
              <span class="stat-label">起 / 人</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== Booking Panel Card ========== -->
    <section class="booking-panel card-shell">
      <h3 class="panel-title">门票预订</h3>

      <!-- Date Picker (single visitDate field from module) -->
      <DatePicker
        :dateFields="dateFields"
        @dateChange="handleDateChange"
      />

      <!-- Ticket Selector (replaces Hotel's RoomSelector + Guest Stepper) -->
      <TicketSelector
        :tickets="tickets"
        :initialQuantities="ticketQuantities"
        @quantityChange="handleQuantityChange"
      />

      <!-- Conditional BookingForm with 2 slots -->
      <div v-if="hasSelectedTickets" class="booking-form-wrap">
        <BookingForm
          :totalPrice="totalPrice"
          :showSpecialRequest="false"
          @submit="submitBooking"
        >
          <!-- additional-info: TouristInfoForm for EACH selected ticket -->
          <template #additional-info>
            <div v-for="ticket in selectedTickets" :key="ticket.id" class="tourist-block">
              <TouristInfoForm
                :ticketName="ticket.name"
                :quantity="ticketQuantities[ticket.id]"
                :initialTourists="tourists[ticket.id] || []"
                @touristInfoChange="(info) => handleTouristInfoChange(ticket.id, info)"
              />
            </div>
          </template>
          <!-- price-items: line per ticket type × qty -->
          <template #price-items>
            <div class="price-item" v-for="ticket in selectedTickets" :key="ticket.id">
              <span class="price-item__label">{{ ticket.name }} ({{ ticketQuantities[ticket.id] }}人)</span>
              <span class="price-item__value">¥{{ ticket.price * ticketQuantities[ticket.id] }}</span>
            </div>
          </template>
        </BookingForm>
      </div>
    </section>
  </div>
</template>

<script setup>
import { defineProps, ref, computed, onMounted, watch } from 'vue'
import DatePicker from './DatePicker.vue'
import TicketSelector from './TicketSelector.vue'
import TouristInfoForm from './TouristInfoForm.vue'
import BookingForm from './BookingForm.vue'

const props = defineProps({
  dateFields: {
    type: Array,
    required: true
  },
  tags: {
    type: Array,
    default: () => []
  },
  facilities: {
    type: Array,
    default: () => []
  },
  tickets: {
    type: Array,
    required: true
  },
  attractionData: {
    type: Object,
    default: null
  }
})

const visitDate = ref('')
const ticketQuantities = ref({})
const tourists = ref({})

const displayFacilities = computed(() => {
  const facs = props.facilities && props.facilities.length > 0
    ? props.facilities
    : ['停车场', '休息区', '卫生间', '观光车']
  return [...new Set(facs)]
})

const displayImages = computed(() => {
  const images = props.attractionData?.images || []
  const defaultImages = [
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=scenic%20view%20tourist%20attraction&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20nature%20landscape&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tourist%20spot%20attraction&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=scenic%20area%20beautiful%20view&image_size=square',
    'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tourism%20destination%20nature&image_size=square'
  ]
  
  return images.slice(0, 5).map((img, index) => {
    if (img && img.trim()) return img.trim()
    return defaultImages[index % defaultImages.length]
  })
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

onMounted(() => {
  initTicketQuantities()
})

watch(() => props.tickets, () => {
  initTicketQuantities()
}, { deep: true })
</script>

<style scoped>
/* ===== Base Layout ===== */
.attraction-booking {
  display: flex;
  flex-direction: column;
  gap: 56px;
  width: 100%;
  box-sizing: border-box;
}

/* ===== Shared Card Shell (Apple-white 28px radius) ===== */
.card-shell {
  background: #ffffff;
  border-radius: 28px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 8px 24px -12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  box-sizing: border-box;
}

/* ============================================================
   PRODUCT HERO (upper card)
   ============================================================ */
.product-hero {
  display: flex;
  flex-direction: column;
}

/* --- Hero Cover (16:9 deterministic) --- */
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

/* --- Thumbnails (5 cols grid, padding matches HotelBooking) --- */
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
  cursor: pointer;
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

/* --- Hero Details Section --- */
.hero-details {
  padding: 40px 32px 48px 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  box-sizing: border-box;
}

/* Title + Rating row */
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
.review-count {
  color: #6e6e73;
  font-size: 14px;
}

/* --- Meta rows (Address + Description + OpenTime + Season) --- */
.meta-rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 4px;
}
.attraction-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 4px 0;
}
.meta-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  color: #6e6e73;
  font-size: 14px;
  line-height: 1.6;
}
.meta-row strong {
  color: #1d1d1f;
  font-weight: 600;
  margin-right: 2px;
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

/* --- Tag Groups (2: Attraction Tags + Facilities) --- */
.tag-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px 0;
}
.tag-group--tags {
  border-top: 1px solid #f2f2f4;
  border-bottom: none;
  padding-top: 20px;
  padding-bottom: 8px;
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
  letter-spacing: 0.03em;
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

/* --- Stats Grid (3 cols: like / collect / price-per-person) --- */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding-top: 16px;
}
.stat-item {
  display: flex;
  align-items: center;
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

.booking-form-wrap {
  margin-top: 8px;
}
.tourist-block {
  margin-bottom: 16px;
}
.tourist-block:last-child {
  margin-bottom: 0;
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
.price-item__label {
  color: #6e6e73;
  flex: 1;
  min-width: 0;
  word-break: break-word;
}
.price-item__value {
  font-weight: 600;
  color: #1d1d1f;
  flex-shrink: 0;
}

/* ============================================================
   RESPONSIVE BREAKPOINTS (Apple 3 tiers: 1200 / 768)
   ============================================================ */
/* Tablet 768 - 1199 */
@media (max-width: 1199px) {
  .attraction-booking {
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

/* Mobile <=767 */
@media (max-width: 767px) {
  .attraction-booking {
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
    flex-direction: column;
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
}
</style>
