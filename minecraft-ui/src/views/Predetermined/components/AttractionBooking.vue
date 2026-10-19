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
          :initialContact="prefill?.contact"
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

    <!-- ========== 推荐景点（推荐接口） ========== -->
    <section class="recommend-section card-shell" aria-labelledby="attraction-recommend-title">
      <header class="recommend-head">
        <div class="recommend-copy">
          <span class="recommend-eyebrow">{{ $t('predetermined.attractionRecommend.eyebrow') }}</span>
          <h3 id="attraction-recommend-title" class="recommend-title">
            {{ $t('predetermined.attractionRecommend.title') }}
          </h3>
          <p class="recommend-desc">{{ $t('predetermined.attractionRecommend.subtitle') }}</p>
        </div>
        <button
          type="button"
          class="recommend-refresh"
          :class="{ 'is-loading': recommendLoading }"
          :disabled="recommendLoading"
          @click="loadRecommendations"
        >
          <svg class="refresh-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
            stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M21 12a9 9 0 1 1-2.64-6.36" />
            <path d="M21 3v6h-6" />
          </svg>
          {{ $t('predetermined.refresh') }}
        </button>
      </header>

      <!-- 骨架屏 -->
      <div v-if="recommendLoading" class="recommend-grid" aria-hidden="true">
        <div class="rec-skeleton" v-for="n in 4" :key="n">
          <div class="rec-sk-media"></div>
          <div class="rec-sk-line"></div>
          <div class="rec-sk-line rec-sk-line--short"></div>
        </div>
      </div>

      <div v-else-if="visibleRecommendations.length" class="recommend-grid">
        <article
          v-for="item in visibleRecommendations"
          :key="item.itemId"
          class="rec-card"
          role="button"
          tabindex="0"
          @click="openRecommendation(item)"
          @keydown.enter.prevent="openRecommendation(item)"
          @keydown.space.prevent="openRecommendation(item)"
        >
          <div class="rec-media">
            <img :src="item.image || fallbackImage" :alt="item.name" loading="lazy" />
            <span v-if="item.ruleType === 'PIN'" class="rec-pin">{{ $t('predetermined.pin') }}</span>
            <span class="rec-loc" v-if="formatLocation(item)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"
                stroke-linejoin="round" aria-hidden="true">
                <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
                <circle cx="12" cy="10" r="3" />
              </svg>
              {{ formatLocation(item) }}
            </span>
          </div>
          <div class="rec-body">
            <h4 class="rec-name">{{ item.name }}</h4>
            <div class="rec-tags" v-if="(item.tags || []).length">
              <span v-for="tag in (item.tags || []).slice(0, 3)" :key="tag">{{ tag }}</span>
            </div>
            <div class="rec-foot">
              <span class="rec-price" v-if="Number(item.price) > 0">
                ¥{{ item.price }}<small>/{{ $t('predetermined.perPerson') }}</small>
              </span>
              <span class="rec-price is-free" v-else>{{ $t('predetermined.free') }}</span>
              <span class="rec-rating" v-if="item.rating">★ {{ item.rating }}</span>
              <span class="rec-rating is-empty" v-else>{{ $t('predetermined.noRating') }}</span>
            </div>
          </div>
        </article>
      </div>

      <div v-else class="recommend-empty">
        <p>{{ recommendError || $t('predetermined.attractionRecommend.empty') }}</p>
        <button v-if="recommendError" type="button" class="recommend-retry" @click="loadRecommendations">
          {{ $t('predetermined.retry') }}
        </button>
      </div>
    </section>

    <!-- 推荐景点详情弹窗 -->
    <Select
      :visible="detailVisible"
      :productId="detailProductId"
      commodity="3"
      @close="detailVisible = false"
    />
  </div>
</template>

<script setup>
import { defineProps, ref, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import DatePicker from './DatePicker.vue'
import TicketSelector from './TicketSelector.vue'
import TouristInfoForm from './TouristInfoForm.vue'
import BookingForm from './BookingForm.vue'
import Select from '@/components/Payment/Select.vue'
import { createTicketBooking } from '@/api/booking.js'
import { getClientRecommendations } from '@/api/clientRecommendation'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

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
  },
  // 修改预订时的回填数据
  prefill: {
    type: Object,
    default: null
  }
})

const visitDate = ref('')
const ticketQuantities = ref({})
const tourists = ref({})

const { t } = useI18n()

/* ===================== 推荐景点 ===================== */
const fallbackImage = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=scenic%20tourist%20attraction%20landscape&image_size=landscape_4_3'
const recommendations = ref([])
const recommendLoading = ref(false)
const recommendError = ref('')
const detailVisible = ref(false)
const detailProductId = ref('')

// 排除当前正在预订的景点，最多展示 4 个
const visibleRecommendations = computed(() =>
  recommendations.value
    .filter(item => String(item.itemId) !== String(props.attractionData?.id))
    .slice(0, 4)
)

// 加载推荐景点（与景点推荐页同源接口）
const loadRecommendations = async () => {
  recommendLoading.value = true
  recommendError.value = ''
  try {
    const res = await getClientRecommendations('attraction', { limit: 8 })
    recommendations.value = res?.data || []
  } catch (error) {
    console.error('加载景点推荐失败:', error)
    recommendError.value = t('predetermined.attractionRecommend.error')
    recommendations.value = []
  } finally {
    recommendLoading.value = false
  }
}

useAutoRefresh(loadRecommendations)

// 地点展示（省市重复时只展示一个）
const formatLocation = (item) => {
  if (item.province && item.city && item.province !== item.city && !item.province.includes(item.city)) {
    return `${item.province} · ${item.city}`
  }
  return item.city || item.province || ''
}

// 打开景点详情弹窗（与景点列表页一致）
const openRecommendation = (item) => {
  if (!item?.itemId) return
  detailProductId.value = String(item.itemId)
  detailVisible.value = true
}

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

// 用回填数据初始化游玩日期 / 票种数量
const applyPrefill = () => {
  if (!props.prefill) return
  if (props.prefill.visitDate) {
    visitDate.value = props.prefill.visitDate
  }
  const items = props.prefill.items || []
  items.forEach(item => {
    if (props.tickets.some(t => t.id === item.ticketId)) {
      ticketQuantities.value[item.ticketId] = item.quantity || 0
    }
  })
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

const submitting = ref(false)

const submitBooking = async (bookingData) => {
  if (!visitDate.value) {
    ElMessage.warning('请选择游玩日期')
    return
  }
  if (selectedTickets.value.length === 0) {
    ElMessage.warning('请至少选择一种票种')
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
    ElMessage.warning('请填写完整的游客信息')
    return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    const response = await createTicketBooking({
      ...bookingData,
      visitDate: visitDate.value,
      items: selectedTickets.value.map(ticket => ({
        ticketId: ticket.id,
        quantity: ticketQuantities.value[ticket.id]
      }))
    })
    if (response && response.code === 200) {
      ElMessageBox.alert(`预订编号：${response.data}`, '预订提交成功', {
        confirmButtonText: '确定',
        type: 'success'
      }).catch(() => {})
    } else {
      ElMessage.error(response?.message || '预订提交失败，请稍后重试')
    }
  } catch (error) {
    console.error('预订提交失败:', error)
    ElMessage.error('预订提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  initTicketQuantities()
  applyPrefill()
  loadRecommendations()
})

watch(() => props.tickets, () => {
  initTicketQuantities()
  applyPrefill()
}, { deep: true })

watch(() => props.prefill, applyPrefill, { deep: true })
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
  background: var(--theme-bg-surface);
  border-radius: 28px;
  box-shadow: var(--theme-shadow-sm);
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
  background: var(--theme-bg-surface-2);
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
  background: var(--theme-bg-surface-2);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.thumb:hover {
  transform: scale(1.005);
  box-shadow: var(--theme-shadow-md);
}
.thumb:focus-visible {
  outline: 2px solid var(--theme-primary);
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
  color: var(--theme-text-1);
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
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  padding: 4px 14px;
  border-radius: 40px;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.01em;
}
.rating-text {
  color: var(--theme-primary);
  font-weight: 600;
  font-size: 14px;
}
.review-count {
  color: var(--theme-text-2);
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
  color: var(--theme-text-2);
  font-size: 14px;
  line-height: 1.6;
}
.meta-row strong {
  color: var(--theme-text-1);
  font-weight: 600;
  margin-right: 2px;
}
.meta-icon {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  margin-top: 2px;
  color: var(--theme-primary);
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
  border-top: 1px solid var(--theme-hairline);
  border-bottom: none;
  padding-top: 20px;
  padding-bottom: 8px;
}
.tag-group--facilities {
  border-top: 1px solid var(--theme-hairline);
  border-bottom: 1px solid var(--theme-hairline);
}
.tag-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--theme-text-2);
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
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font-size: 12px;
  border-radius: 40px;
  line-height: 1.4;
  border: 1px solid transparent;
  transition: background 0.2s ease;
}
.tag-chip:hover {
  background: var(--theme-bg-hover);
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
  background: var(--theme-bg-surface-2);
  border-radius: 16px;
  box-sizing: border-box;
  min-width: 0;
}
.stat-icon {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  color: var(--theme-primary);
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
  color: var(--theme-text-1);
  line-height: 1.2;
}
.stat-label {
  font-size: 12px;
  color: var(--theme-text-2);
  line-height: 1.3;
}

/* ============================================================
   BOOKING PANEL (lower card)
   ============================================================ */
.booking-panel {
  padding: 40px 32px;
  box-sizing: border-box;
  /* 允许日期面板浮层溢出卡片，避免被 card-shell 的 overflow:hidden 裁切 */
  overflow: visible;
}
.panel-title {
  position: relative;
  margin: 0 0 24px 0;
  padding-left: 14px;
  font-size: 22px;
  line-height: 1.3;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.005em;
}
.panel-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 4px;
  height: 20px;
  border-radius: 2px;
  background: var(--theme-gradient);
  transform: translateY(-50%);
}

.booking-form-wrap {
  margin-top: 32px;
  padding-top: 32px;
  border-top: 1px solid var(--theme-hairline);
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
  color: var(--theme-text-1);
  line-height: 1.5;
  gap: 12px;
}
.price-item__label {
  color: var(--theme-text-2);
  flex: 1;
  min-width: 0;
  word-break: break-word;
}
.price-item__value {
  font-weight: 600;
  color: var(--theme-text-1);
  flex-shrink: 0;
}

/* ============================================================
   RECOMMENDATIONS（推荐景点）
   ============================================================ */
.recommend-section {
  padding: 40px 32px 44px;
  box-sizing: border-box;
}

.recommend-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 28px;
}

.recommend-copy {
  min-width: 0;
}

.recommend-eyebrow {
  display: inline-block;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--theme-primary);
}

.recommend-title {
  margin: 8px 0 0;
  font-size: 24px;
  line-height: 1.25;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.01em;
}

.recommend-desc {
  margin: 8px 0 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--theme-text-2);
}

.recommend-refresh {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 36px;
  padding: 0 16px;
  border: 1px solid var(--theme-border-strong);
  border-radius: 999px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-size: 13px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}
.recommend-refresh:hover:not(:disabled) {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
}
.recommend-refresh:disabled {
  cursor: default;
  opacity: 0.6;
}
.recommend-refresh:focus-visible {
  outline: 2px solid var(--theme-primary);
  outline-offset: 2px;
}
.refresh-icon {
  width: 14px;
  height: 14px;
}
.recommend-refresh.is-loading .refresh-icon {
  animation: rec-spin 0.9s linear infinite;
}
@keyframes rec-spin {
  to { transform: rotate(360deg); }
}

/* --- 卡片网格 --- */
.recommend-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  gap: 20px;
}

.rec-card {
  position: relative;
  display: flex;
  flex-direction: column;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}
.rec-card:hover {
  transform: translateY(-4px);
  border-color: transparent;
  box-shadow: var(--theme-shadow-md);
}
.rec-card:focus-visible {
  outline: 2px solid var(--theme-primary);
  outline-offset: 2px;
}

.rec-media {
  position: relative;
  aspect-ratio: 16 / 10;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
}
.rec-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.4s ease;
}
.rec-card:hover .rec-media img {
  transform: scale(1.04);
}
.rec-media::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(6, 10, 18, 0) 45%, rgba(6, 10, 18, 0.55) 100%);
  pointer-events: none;
}

.rec-pin {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 2;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, var(--theme-danger), var(--theme-warning));
  box-shadow: 0 4px 10px rgba(15, 23, 42, 0.18);
}

.rec-loc {
  position: absolute;
  left: 12px;
  bottom: 10px;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  max-width: calc(100% - 24px);
  font-size: 12px;
  color: #fff;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rec-loc svg {
  width: 13px;
  height: 13px;
  flex-shrink: 0;
}

.rec-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  padding: 14px 16px 16px;
}

.rec-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.35;
  color: var(--theme-text-1);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rec-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.rec-tags span {
  padding: 3px 9px;
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 11px;
  line-height: 1.4;
  white-space: nowrap;
}

.rec-foot {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  margin-top: auto;
  padding-top: 4px;
}

.rec-price {
  font-size: 17px;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.01em;
}
.rec-price.is-free {
  color: var(--theme-success);
}
.rec-price small {
  margin-left: 2px;
  font-size: 11px;
  font-weight: 400;
  color: var(--theme-text-2);
}

.rec-rating {
  font-size: 12px;
  font-weight: 600;
  color: var(--theme-warning);
  white-space: nowrap;
}
.rec-rating.is-empty {
  font-weight: 400;
  color: var(--theme-text-3);
}

/* --- 骨架屏 --- */
.rec-skeleton {
  border: 1px solid var(--theme-hairline);
  border-radius: 20px;
  overflow: hidden;
  background: var(--theme-bg-surface);
}
.rec-sk-media {
  position: relative;
  aspect-ratio: 16 / 10;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
}
.rec-sk-line {
  position: relative;
  height: 12px;
  margin: 14px 16px 0;
  border-radius: 6px;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
}
.rec-sk-line--short {
  width: 45%;
  margin-top: 10px;
  margin-bottom: 16px;
}
.rec-sk-media::after,
.rec-sk-line::after {
  content: '';
  position: absolute;
  inset: 0;
  transform: translateX(-100%);
  background: linear-gradient(90deg, transparent, var(--theme-bg-hover), transparent);
  animation: rec-shimmer 1.4s infinite;
}
@keyframes rec-shimmer {
  100% { transform: translateX(100%); }
}

/* --- 空态 --- */
.recommend-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 48px 16px;
  border-radius: 16px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 14px;
  text-align: center;
}
.recommend-empty p {
  margin: 0;
}
.recommend-retry {
  height: 34px;
  padding: 0 18px;
  border: 1px solid var(--theme-border-strong);
  border-radius: 999px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s ease;
}
.recommend-retry:hover {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
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
  .recommend-section {
    padding: 32px 24px 36px;
  }
  .recommend-grid {
    gap: 16px;
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
  .booking-form-wrap {
    margin-top: 24px;
    padding-top: 24px;
  }
  .recommend-section {
    padding: 24px 16px 28px;
  }
  .recommend-head {
    align-items: flex-start;
    margin-bottom: 20px;
  }
  .recommend-title {
    font-size: 20px;
  }
  .recommend-desc {
    font-size: 13px;
  }
  .recommend-grid {
    gap: 14px;
  }
  .rec-name {
    font-size: 15px;
  }
}

/* ============================================================
   DESKTOP HERO — 双栏布局（>=1024px）
   ============================================================ */
@media (min-width: 1024px) {
  .product-hero {
    display: grid;
    grid-template-columns: minmax(0, 1.06fr) minmax(0, 0.94fr);
    grid-template-areas:
      'image details'
      'thumbs details';
    align-items: start;
  }

  .hero-image {
    grid-area: image;
    width: auto;
    aspect-ratio: 4 / 3;
    margin: 20px 20px 0;
    border-radius: 20px;
  }

  .hero-thumbs {
    grid-area: thumbs;
    grid-template-columns: repeat(5, 1fr);
    gap: 10px;
    padding: 14px 20px 20px;
  }

  .hero-details {
    grid-area: details;
    padding: 34px 32px;
    gap: 14px;
    border-left: 1px solid var(--theme-hairline);
  }

  .product-name {
    font-size: 30px;
  }

  .stats-grid {
    gap: 12px;
  }

  .stat-item {
    padding: 14px;
  }
}

/* 1024–1199：双栏下压缩右侧信息密度，避免拥挤 */
@media (min-width: 1024px) and (max-width: 1199px) {
  .hero-details {
    padding: 28px 24px;
  }
  .product-name {
    font-size: 26px;
  }
  .stat-item {
    padding: 12px 10px;
    gap: 10px;
  }
  .stat-icon {
    width: 18px;
    height: 18px;
  }
  .stat-value {
    font-size: 16px;
  }
}
</style>
