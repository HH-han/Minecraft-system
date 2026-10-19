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

      <div v-if="selectedRoom" :key="selectedRoom.id" class="booking-form-wrap">
        <BookingForm
          :totalPrice="totalPrice + serviceFee"
          :initialContact="prefill?.contact"
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

    <!-- ====== 推荐酒店（推荐接口） ====== -->
    <section class="recommend-section card-shell" aria-labelledby="hotel-recommend-title">
      <header class="recommend-head">
        <div class="recommend-copy">
          <span class="recommend-eyebrow">{{ $t('predetermined.hotelRecommend.eyebrow') }}</span>
          <h3 id="hotel-recommend-title" class="recommend-title">
            {{ $t('predetermined.hotelRecommend.title') }}
          </h3>
          <p class="recommend-desc">{{ $t('predetermined.hotelRecommend.subtitle') }}</p>
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
              <span class="rec-price">
                ¥{{ item.price ?? 0 }}<small>/{{ $t('predetermined.perNight') }}</small>
              </span>
              <span class="rec-rating" v-if="item.rating">★ {{ formatRating(item.rating) }}</span>
              <span class="rec-rating is-empty" v-else>{{ $t('predetermined.noRating') }}</span>
            </div>
          </div>
        </article>
      </div>

      <div v-else class="recommend-empty">
        <p>{{ recommendError || $t('predetermined.hotelRecommend.empty') }}</p>
        <button v-if="recommendError" type="button" class="recommend-retry" @click="loadRecommendations">
          {{ $t('predetermined.retry') }}
        </button>
      </div>
    </section>

    <!-- 推荐酒店详情弹窗 -->
    <Select
      :visible="detailVisible"
      :productId="detailProductId"
      commodity="2"
      @close="detailVisible = false"
    />
  </div>
</template>

<script setup>
import { defineProps, ref, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import DatePicker from './DatePicker.vue'
import RoomSelector from './RoomSelector.vue'
import BookingForm from './BookingForm.vue'
import Select from '@/components/Payment/Select.vue'
import { createHotelBooking } from '@/api/booking.js'
import { getClientRecommendations } from '@/api/clientRecommendation'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

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
  },
  // 修改预订时的回填数据
  prefill: {
    type: Object,
    default: null
  }
})

const checkInDate = ref('')
const checkOutDate = ref('')
const guests = ref(2)
const selectedRoom = ref(null)

const { t } = useI18n()

/* ===================== 推荐酒店 ===================== */
const fallbackImage = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=luxury%20hotel%20exterior%20with%20pool&image_size=landscape_4_3'
const recommendations = ref([])
const recommendLoading = ref(false)
const recommendError = ref('')
const detailVisible = ref(false)
const detailProductId = ref('')

// 排除当前正在预订的酒店，最多展示 4 个
const visibleRecommendations = computed(() =>
  recommendations.value
    .filter(item => String(item.itemId) !== String(props.hotelData?.id))
    .slice(0, 4)
)

// 加载推荐酒店（与酒店推荐页同源接口）
const loadRecommendations = async () => {
  recommendLoading.value = true
  recommendError.value = ''
  try {
    const res = await getClientRecommendations('hotel', { limit: 8 })
    recommendations.value = res?.data || []
  } catch (error) {
    console.error('加载酒店推荐失败:', error)
    recommendError.value = t('predetermined.hotelRecommend.error')
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

// 评分兼容 5 分制与百分制两种历史数据
const formatRating = (rating) => {
  if (rating === null || rating === undefined) return ''
  return Number(rating) > 5 ? (Number(rating) / 20).toFixed(1) : rating
}

// 打开酒店详情弹窗（与酒店列表页一致）
const openRecommendation = (item) => {
  if (!item?.itemId) return
  detailProductId.value = String(item.itemId)
  detailVisible.value = true
}

// 用回填数据初始化日期 / 住客 / 房型
const applyPrefill = () => {
  if (!props.prefill) return
  if (props.prefill.checkInDate) checkInDate.value = props.prefill.checkInDate
  if (props.prefill.checkOutDate) checkOutDate.value = props.prefill.checkOutDate
  if (props.prefill.guests) guests.value = props.prefill.guests
  if (props.prefill.roomId && props.rooms.length) {
    const room = props.rooms.find(r => r.id === props.prefill.roomId)
    if (room) selectedRoom.value = room
  }
}

onMounted(() => {
  applyPrefill()
  loadRecommendations()
})
watch(() => props.prefill, applyPrefill, { deep: true })
watch(() => props.rooms, applyPrefill, { deep: true })

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

const submitting = ref(false)

const submitBooking = async (bookingData) => {
  if (!checkInDate.value || !checkOutDate.value) {
    ElMessage.warning('请选择入住和离店日期')
    return
  }
  if (!selectedRoom.value) {
    ElMessage.warning('请选择房间')
    return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    const response = await createHotelBooking({
      ...bookingData,
      checkInDate: checkInDate.value,
      checkOutDate: checkOutDate.value,
      guests: guests.value,
      roomId: selectedRoom.value.id
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
  background: var(--theme-bg-surface);
  border-radius: 28px;
  box-shadow: var(--theme-shadow-sm);
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
  background: var(--theme-bg-surface-2);
  cursor: default;
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
  color: var(--theme-text-1);
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
.meta-subtle {
  color: var(--theme-text-2);
  font-size: 14px;
}

/* --- Meta rows (Address + Description，18×18 stroke SVG，与景点尺寸/颜色一致) --- */
.meta-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  color: var(--theme-text-2);
  font-size: 14px;
  line-height: 1.6;
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

/* --- Facilities Tag Group（与景点.tag-group--facilities 完全同结构） --- */
.tag-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px 0;
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
.price-font {
  letter-spacing: -0.01em;
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

/* 住客 stepper（维持原样，景点没有 stepper） */
.guest-block {
  margin-bottom: 32px;
}
.field-label {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 500;
  color: var(--theme-text-2);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}
.stepper {
  display: inline-flex;
  align-items: center;
  width: 140px;
  height: 40px;
  border-radius: 40px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border-strong);
  overflow: hidden;
  transition: border-color 0.2s ease;
}
.stepper:hover {
  border-color: var(--theme-border-strong);
}
.stepper-btn {
  width: 44px;
  height: 40px;
  border: none;
  background: transparent;
  color: var(--theme-text-2);
  font-size: 20px;
  font-weight: 400;
  cursor: pointer;
  line-height: 1;
  transition: color 0.2s ease, background 0.2s ease;
  flex-shrink: 0;
}
.stepper-btn:hover:not(:disabled) {
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
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
  color: var(--theme-text-1);
  line-height: 1;
  user-select: none;
}
.stepper-btn:focus-visible {
  outline: 2px solid var(--theme-primary);
  outline-offset: -2px;
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

/* 表单区与上方选项之间的分隔 */
.booking-form-wrap {
  margin-top: 32px;
  padding-top: 32px;
  border-top: 1px solid var(--theme-hairline);
}

/* ============================================================
   RECOMMENDATIONS（推荐酒店）
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
  .recommend-section {
    padding: 32px 24px 36px;
  }
  .recommend-grid {
    gap: 16px;
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
