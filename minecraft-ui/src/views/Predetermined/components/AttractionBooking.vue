<template>
  <div class="attraction-booking">
    <div class="attraction-info">
      <div class="attraction-images">
        <div class="main-image">
          <img :src="attractionData?.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20scenic%20spot%20landscape%20mountain%20lake&image_size=square_hd'" :alt="attractionData?.name || '景点景观'">
        </div>
        <div class="image-thumbs">
          <div class="thumb" v-for="(img, index) in displayImages" :key="index">
            <img :src="img" :alt="attractionData?.name || '景点图片'">
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
          <svg t="1778942347251" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10612" width="32" height="32"><path d="M950.784 442.88c0-243.2-197.12-440.32-440.32-440.32S70.144 199.68 70.144 442.88c0 153.6 78.336 288.256 197.632 367.104L510.464 1016.32l242.688-206.336c118.784-78.848 197.632-214.016 197.632-367.104z" fill="#22BCB9" p-id="10613"></path><path d="M292.608 392.96l364.544-120.832c8.192-3.072 17.408 2.048 20.48 10.24a17.44 17.44 0 0 1 0 10.24L556.8 657.152c-3.072 8.192-12.288 13.312-20.48 10.24-4.096-1.024-7.168-4.096-9.216-8.192l-77.824-158.72-158.72-76.8a15.648 15.648 0 0 1-7.168-21.504c1.024-4.096 5.12-7.168 9.216-9.216z" fill="#FFFFFF" p-id="10614"></path></svg>
          <span>{{ attractionData?.address || '风景秀丽的山区，距离市中心约1小时车程' }}</span>
        </div>
        <div class="attraction-description">
          <svg t="1778943372863" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="29988" width="32" height="32"><path d="M785.15199969 32L237.02400031 32C161.66400031 32 99.99999969 93.66399969 99.99999969 169.02399969l0 685.152c0 75.36 61.66399969 137.02399969 137.02399969 137.02399969l548.12800031-1e-8c75.36 0 137.02399969-61.66399969 137.02399968-137.02399968L922.17600031 169.02399969C922.17600031 93.66399969 860.51199969 32 785.15199969 32L785.15199969 32zM271.29600031 169.02399969l205.536 0c20.544 0 34.272 13.69600031 34.272 34.272 0 20.544-13.69600031 34.272-34.272 34.272L271.29600031 237.56799969c-20.544 0-34.272-13.69600031-34.272-34.272C237.02400031 182.72 250.71999969 169.02399969 271.29600031 169.02399969L271.29600031 169.02399969zM750.87999969 854.17599969L271.29600031 854.17599969c-20.544 0-34.272-13.69600031-34.272-34.272s13.69600031-34.272 34.272-34.272l479.616 0c20.544 0 34.272 13.69600031 34.272 34.272S771.45600031 854.17599969 750.87999969 854.17599969L750.87999969 854.17599969zM750.87999969 648.63999969L271.29600031 648.63999969c-20.544 0-34.272-13.69600031-34.272-34.272 0-20.544 13.69600031-34.272 34.272-34.272l479.616 0c20.544 0 34.272 13.69600031 34.272 34.272C785.15199969 634.91199969 771.45600031 648.63999969 750.87999969 648.63999969L750.87999969 648.63999969zM750.87999969 443.072L271.29600031 443.072c-20.544 0-34.272-13.69600031-34.272-34.272 0-20.544 13.69600031-34.272 34.272-34.272l479.616 0c20.544 0 34.272 13.69600031 34.272 34.272C785.15199969 429.37599969 771.45600031 443.072 750.87999969 443.072L750.87999969 443.072z" fill="#08c82f" p-id="29989"></path></svg>
          <span>{{ attractionData?.description || '这是一处风景秀丽的旅游胜地。' }}</span>
        </div>
        <div class="attraction-tags">
          <span class="tag" v-for="tag in tags" :key="tag">{{ tag }}</span>
        </div>
        <div class="attraction-facilities">
          <span class="facility-label">配套设施：</span>
          <span class="facility-tag" v-for="(facility, index) in displayFacilities" :key="index">{{ facility }}</span>
        </div>
        <div class="attraction-meta">
          <div class="meta-item">
            <svg t="1778942667903" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="26669" width="32" height="32"><path d="M819.2 285.44a371.84 371.84 0 0 1 96.64 208.853333c-10.453333 6.826667-19.84 0-29.013333-2.773333-132.266667-49.066667-252.586667-27.093333-362.666667 60.16-9.173333 7.253333-17.92 22.826667-33.92 9.813333a320 320 0 0 1-3.626667-80.853333v-91.52a90.24 90.24 0 0 0-3.626666-30.08 23.253333 23.253333 0 0 0-24.746667-17.706667 21.333333 21.333333 0 0 0-21.333333 21.333334 208.426667 208.426667 0 0 0-1.066667 30.506666v266.88A789.333333 789.333333 0 0 0 405.333333 761.386667a378.88 378.88 0 0 0 8.32 160.853333c2.346667 8.106667 6.186667 16.213333 0 24.32a365.013333 365.013333 0 0 1-183.466666-106.666667 178.986667 178.986667 0 0 1-21.333334-75.52c-12.8-142.506667 21.333333-271.786667 123.946667-373.973333 128-128 282.026667-172.586667 459.52-126.08a33.493333 33.493333 0 0 1 26.88 21.12z" fill="#F2AC3C" p-id="26670"></path><path d="M819.2 285.44A220.586667 220.586667 0 0 0 710.613333 256a445.013333 445.013333 0 0 0-296.32 75.093333 439.253333 439.253333 0 0 0-198.826666 326.186667 369.706667 369.706667 0 0 0 11.52 163.413333 87.466667 87.466667 0 0 1 2.346666 20.053334 386.346667 386.346667 0 0 1-120.106666-304.64 376.746667 376.746667 0 0 1 95.36-243.626667c13.653333-15.36 0-17.706667-8.533334-23.466667a112 112 0 0 1-38.613333-131.413333 103.893333 103.893333 0 0 1 111.146667-66.346667 106.666667 106.666667 0 0 1 92.8 82.133334c3.413333 16.426667 7.253333 21.333333 24.32 15.36a386.133333 386.133333 0 0 1 259.2 0c18.133333 6.4 18.773333-2.986667 21.333333-15.573334a109.653333 109.653333 0 0 1 114.56-81.92 101.973333 101.973333 0 0 1 97.28 88.32 111.786667 111.786667 0 0 1-42.666667 106.666667c-6.186667 4.906667-18.133333 6.613333-16.213333 19.2z" fill="#F6C15F" p-id="26671"></path><path d="M412.8 946.56A373.973333 373.973333 0 0 1 428.373333 661.333333c73.813333 17.493333 149.333333 5.973333 222.72 8.32 14.506667 0 27.306667-4.266667 27.946667-22.613333s-11.733333-25.6-27.52-26.666667c-45.866667-2.773333-91.733333 0-137.386667-1.493333-11.733333 0-25.386667 0-29.44-14.506667s-6.186667-28.8 9.386667-38.613333a522.24 522.24 0 0 1 134.613333-82.773333 356.053333 356.053333 0 0 1 256 0c10.24 3.84 21.333333 7.253333 30.933334 10.88a262.826667 262.826667 0 0 1 1.706666 112.853333 372.906667 372.906667 0 0 0-234.666666 80.853333 352 352 0 0 0-130.346667 235.733334 52.053333 52.053333 0 0 1-11.946667 34.986666 323.413333 323.413333 0 0 1-127.573333-11.733333z" fill="#F29F20" p-id="26672"></path><path d="M539.733333 958.293333a389.76 389.76 0 0 1 81.92-219.733333 348.8 348.8 0 0 1 234.666667-132.266667c20.266667-2.56 40.746667-13.013333 61.013333 0a364.586667 364.586667 0 0 1-88.106666 205.866667 396.373333 396.373333 0 0 1-244.266667 142.08 265.173333 265.173333 0 0 1-45.226667 4.053333z" fill="#F18A23" p-id="26673"></path><path d="M494.08 565.333333a21.333333 21.333333 0 0 0-1.706667 4.906667c-5.76 40.96-5.546667 40.96 34.986667 40.96h124.586667c26.24 0 37.12 11.306667 37.546666 33.92s-9.6 32-30.293333 32h-198.186667a45.653333 45.653333 0 0 1-32.64-15.786667V362.666667a27.733333 27.733333 0 0 1 30.933334-31.36A30.08 30.08 0 0 1 492.16 362.666667c1.28 37.333333 0 74.453333 0 111.573333 0.853333 30.506667 1.493333 60.8 1.92 91.093333z" fill="#FFFFFF" p-id="26674"></path><path d="M405.333333 245.333333a14.72 14.72 0 0 1-9.173333 11.52A1082.026667 1082.026667 0 0 0 277.333333 337.066667c-5.333333 4.053333-12.16 10.453333-18.133333 5.973333s-2.773333-14.08 1.28-21.333333a165.12 165.12 0 0 1 129.066667-83.413334c11.093333-0.426667 15.786667 2.346667 15.786666 7.04z" fill="#FFFFFE" p-id="26675"></path></svg>
            <span>开放时间：08:00-17:30</span>
          </div>
          <div class="meta-item">
            <svg t="1778942692285" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="27962" width="32" height="32"><path d="M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z" fill="#408FFF" opacity=".15" p-id="27963"></path><path d="M783.058824 537.750588C783.058824 623.344941 714.752 692.705882 630.573176 692.705882H376.470588C300.845176 688.700235 240.941176 631.687529 240.941176 562.206118c0-52.886588 34.755765-98.394353 84.690824-118.874353 10.420706-4.276706 17.889882-13.552941 20.239059-24.636236C359.905882 351.653647 421.315765 301.176471 495.043765 301.176471c54.332235 0 101.918118 27.407059 128.903529 68.608 5.300706 8.131765 13.854118 13.552941 23.43153 15.028705 76.890353 11.685647 135.68 75.595294 135.68 152.937412z" fill="#408FFF" p-id="27964"></path></svg>
            <span>最佳季节：{{ attractionData?.season || '四季皆宜' }}</span>
          </div>
        </div>
        <div class="attraction-stats">
          <div class="stat-item">
            <span class="stat-icon">
              <svg t="1778942479972" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="15264" width="32" height="32"><path d="M511.4368 512m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z" fill="#FF6161" p-id="15265"></path><path d="M286.72 417.8432a256.256 256.256 0 0 0-207.36 105.0624 432.3328 432.3328 0 0 0 285.952 396.032A256.768 256.768 0 0 0 286.72 417.8432z" fill="#FF7D7D" p-id="15266"></path><path d="M342.6304 427.9808h6.7584v293.632h-6.7584a65.3824 65.3824 0 0 1-65.3824-65.3824V493.312a65.3824 65.3824 0 0 1 65.3824-65.3312z" fill="#FFCAC7" p-id="15267"></path><path d="M710.656 390.9632h-136.7552c10.5984-34.9696 24.8832-106.8032-25.088-137.1648C525.312 239.616 469.9136 229.632 471.04 291.84s-2.2528 130.4064-81.1008 136.192v293.632h280.2688a66.8672 66.8672 0 0 0 65.4848-53.4528l40.448-197.1712a66.816 66.816 0 0 0-65.4848-80.0768z" fill="#FFFFFF" p-id="15268"></path></svg>
            </span>
            <span class="stat-value">{{ attractionData?.likeCount || 0 }}</span>
            <span class="stat-label">点赞</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">
              <svg t="1778942513013" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="17483" width="32" height="32"><path d="M517.44768 24.9856c267.4688 5.12 472.61696 207.81056 473.31328 470.1184 0.69632 269.43488-197.34528 476.672-470.56896 478.9248-268.14464 2.21184-474.64448-199.96672-475.87328-473.33376C43.13088 232.67328 249.77408 30.33088 517.44768 24.9856z" fill="#FCEBB8" p-id="17484"></path><path d="M314.44992 763.61728c8.00768-51.69152 12.16512-98.7136 23.4496-143.93344 6.656-26.66496-0.53248-42.35264-18.24768-59.35104-27.62752-26.48064-53.92384-54.41536-80.56832-81.94048-15.81056-16.32256-34.67264-33.54624-26.56256-58.44992 9.23648-28.38528 35.47136-35.328 62.58688-39.05536 34.34496-4.7104 68.42368-11.61216 102.8096-15.83104 27.2384-3.33824 41.984-16.71168 51.79392-42.88512 14.0288-37.31456 31.744-73.5232 50.9952-108.50304 23.57248-42.86464 59.33056-43.25376 83.31264-1.18784 17.2032 30.14656 35.4304 61.11232 44.50304 94.12608 13.14816 47.8208 45.7728 59.84256 88.22784 63.7952 29.30688 2.72384 58.7776 6.47168 87.53152 12.67712 51.79392 11.20256 69.632 55.02976 28.81536 85.27872-102.01088 75.65312-123.1872 167.07584-84.31616 283.72992 13.45536 40.36608-33.34144 73.17504-68.42368 47.26784-93.65504-69.20192-180.61312-72.54016-274.49344-0.47104-33.03424 25.41568-71.0656-0.55296-71.41376-35.26656z" fill="#FEC73C" p-id="17485"></path><path d="M485.0688 412.52864c8.2944 0.55296 19.06688-1.65888 22.44608 7.80288 4.75136 13.23008-6.656 19.2512-16.30208 21.0944-36.59776 7.02464-58.24512 27.42272-63.46752 64.75776-1.26976 9.09312-6.53312 18.37056-15.72864 17.42848-12.30848-1.26976-14.29504-12.63616-14.49984-23.73632-0.77824-40.3456 46.2848-87.47008 87.552-87.3472z" fill="#FEF9EE" p-id="17486"></path></svg>
            </span>
            <span class="stat-value">{{ attractionData?.collectCount || 0 }}</span>
            <span class="stat-label">收藏</span>
          </div>
          <div class="stat-item">
            <span class="stat-icon">
              <svg t="1778942547148" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="20992" width="32" height="32"><path d="M0 0h1024v1024H0V0z" fill="#202425" opacity=".01" p-id="20993"></path><path d="M214.152533 142.2336C181.725867 97.041067 213.981867 34.133333 269.585067 34.133333h484.829866c55.6032 0 87.8592 62.907733 55.432534 108.100267l-77.585067 108.066133A136.533333 136.533333 0 0 1 621.329067 307.2h-218.658134a136.533333 136.533333 0 0 1-110.933333-56.900267L214.152533 142.2336z" fill="#FFAA44" p-id="20994"></path><path d="M545.621333 238.933333h-67.242666a409.6 409.6 0 0 0-403.012267 336.315734l-31.470933 173.192533C21.026133 874.154667 117.623467 989.866667 245.418667 989.866667h533.230933c127.761067 0 224.3584-115.712 201.489067-241.425067l-31.470934-173.192533A409.6 409.6 0 0 0 545.5872 238.933333z" fill="#FF7744" p-id="20995"></path><path d="M351.3344 453.7344a34.133333 34.133333 0 0 1 48.264533 0L512 566.135467l112.401067-112.401067a34.133333 34.133333 0 0 1 48.264533 48.264533L594.397867 580.266667H648.533333a34.133333 34.133333 0 1 1 0 68.266666h-102.4v51.2h102.4a34.133333 34.133333 0 1 1 0 68.266667h-102.4v68.266667a34.133333 34.133333 0 1 1-68.266666 0v-68.266667h-102.4a34.133333 34.133333 0 1 1 0-68.266667h102.4V648.533333h-102.4a34.133333 34.133333 0 1 1 0-68.266666h54.135466l-78.267733-78.267734a34.133333 34.133333 0 0 1 0-48.264533z" fill="#FFFFFF" p-id="20996"></path></svg>
            </span>
            <span class="stat-value">¥{{ attractionData?.price || 0 }}</span>
            <span class="stat-label">起/人</span>
          </div>
        </div>
      </div>
    </div>

    <div class="booking-form">
      <h3>门票预订</h3>

      <DatePicker
        :dateFields="dateFields"
        @dateChange="handleDateChange"
      />

      <TicketSelector
        :tickets="tickets"
        :initialQuantities="ticketQuantities"
        @quantityChange="handleQuantityChange"
      />

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
.attraction-booking {
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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.attraction-name {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #1a1a1a;
}

.attraction-rating {
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

.attraction-location {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 8px;
  color: #4a4a4a;
}

.location-icon {
  margin-right: 8px;
}

.attraction-description {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 8px;
  color: #4a4a4a;
}

.attraction-description p {
  color: #4a4a4a;
  line-height: 1.7;
  font-size: 14px;
  margin: 0;
}

.attraction-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.tag {
  background: rgba(255, 106, 0, 0.08);
  padding: 6px 14px;
  border-radius: 15px;
  font-size: 12px;
  color: #ff6a00;
  border: 1px solid rgba(255, 106, 0, 0.2);
}

.attraction-facilities {
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
  background: rgba(52, 144, 222, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: #3490de;
  border: 1px solid rgba(52, 144, 222, 0.2);
}

.attraction-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4a4a4a;
}

.time-icon, .season-icon {
  margin-right: 8px;
}

.attraction-stats {
  display: flex;
  gap: 25px;
  padding-top: 15px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
}

.stat-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
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
  margin-bottom: 25px;
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

.ticket-selection {
  margin-bottom: 35px;
}

.ticket-selection h4 {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #1a1a1a;
}

.ticket-option {
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

.ticket-option:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: rgba(255, 106, 0, 0.2);
}

.ticket-info {
  flex: 1;
}

.ticket-info h5 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #1a1a1a;
}

.ticket-info p {
  color: #5a5a5a;
  margin-bottom: 12px;
  font-size: 14px;
}

.ticket-rules {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.ticket-rules .rule {
  background: rgba(255, 106, 0, 0.08);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #ff6a00;
  border: 1px solid rgba(255, 106, 0, 0.15);
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
  color: #5a5a5a;
}

.ticket-quantity {
  display: flex;
  align-items: center;
  width: 120px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  margin-left: auto;
  background: rgba(255, 255, 255, 0.8);
}

.ticket-quantity button {
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
  font-weight: 600;
  color: #1a1a1a;
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
  border-top: 1px solid rgba(0, 0, 0, 0.08);
}

.tourist-info h5 {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
  color: #1a1a1a;
}

.tourist-form {
  background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 15px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.price-summary {
  margin-top: 25px;
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
  margin-top: 25px;
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

  .attraction-stats {
    gap: 15px;
  }
}
</style>
