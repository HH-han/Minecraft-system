<template>
  <div class="predetermined-card">
    <!-- 头部：标题 + 类型筛选 + 刷新 -->
    <div class="card-header">
      <h2 class="card-title">{{ $t('payment.booking.title') }}</h2>
      <div class="header-actions">
        <div class="filter-tabs">
          <button
            v-for="filter in filters"
            :key="filter.value"
            class="filter-btn"
            :class="{ active: activeFilter === filter.value }"
            @click="activeFilter = filter.value"
          >{{ filter.label }}</button>
        </div>
        <button class="refresh-btn" :aria-label="$t('payment.booking.refresh')" @click="fetchBookings">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="23 4 23 10 17 10"></polyline>
            <polyline points="1 20 1 14 7 14"></polyline>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
          </svg>
        </button>
      </div>
    </div>

    <!-- 列表区 -->
    <div class="card-body">
      <!-- 加载中 -->
      <div v-if="loading" class="state-wrapper">
        <div class="loading-spinner"></div>
        <span class="state-text">{{ $t('payment.booking.loading') }}</span>
      </div>

      <!-- 未登录 / 无联系电话 -->
      <div v-else-if="!userPhone" class="state-wrapper">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" width="56" height="56" fill="none" stroke="#d2d2d6" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
        </div>
        <div class="state-text">{{ $t('payment.booking.noPhone') }}</div>
        <router-link to="/login" class="primary-button">{{ $t('payment.booking.goLogin') }}</router-link>
      </div>

      <!-- 空数据 -->
      <div v-else-if="bookingList.length === 0" class="state-wrapper">
        <div class="empty-icon">
          <svg t="1773668737283" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="96" height="96">
            <path d="M758.723 194.016c27.266 0 49.345-21.366 49.345-47.699V50.92c0-26.35-22.08-47.714-49.345-47.714-27.267 0-49.347 21.365-49.347 47.714v95.398c0.002 26.333 22.082 47.699 49.347 47.699z m-493.448 0c27.267 0 49.346-21.366 49.346-47.699V50.92c0-26.35-22.079-47.714-49.346-47.714-27.266 0-49.344 21.365-49.344 47.714v95.398c0.001 26.333 22.08 47.699 49.344 47.699z" fill="#d2d2d6"></path>
            <path d="M939.642 98.617h-82.23v47.698c0 52.668-44.188 95.4-98.688 95.4s-98.687-42.732-98.687-95.4V98.617H607.4c0-1.117-40.9 0-95.4 0s-95.398 0.496-95.398 0h-52.639v47.698c0 52.668-44.156 95.4-98.688 95.4-54.499 0-98.688-42.732-98.688-95.4V98.617h-82.23c-27.266 0-49.346 21.367-49.346 47.698v826.78c0 26.333 22.08 47.699 49.346 47.699h855.287c27.266 0 49.345-21.366 49.345-47.7V146.316c-0.002-26.331-22.082-47.698-49.347-47.698zM725.817 861.795H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.195H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.196H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.683 15.961-35.644 35.644-35.644h427.635c19.685 0 35.646 15.96 35.646 35.644 0.001 19.673-15.96 35.634-35.645 35.634z" fill="#d2d2d6"></path>
          </svg>
        </div>
        <div class="state-text">{{ $t('payment.booking.empty') }}</div>
        <router-link to="/predetermined" class="primary-button">{{ $t('payment.booking.goBooking') }}</router-link>
      </div>

      <!-- 预订卡片列表 -->
      <div v-else class="booking-list">
        <div
          v-for="booking in filteredBookings"
          :key="booking.key"
          class="booking-item"
          :class="{ selected: booking.selected }"
          @click="toggleSelect(booking)"
        >
          <div class="item-checkbox">
            <label class="apple-checkbox" @click.stop>
              <input
                type="checkbox"
                v-model="booking.selected"
                @click.stop
              >
              <span class="checkbox-custom"></span>
            </label>
          </div>

          <div class="item-main">
            <div class="item-top">
              <div class="item-title-group">
                <span class="type-badge" :class="booking.type === 'ticket' ? 'badge-ticket' : 'badge-hotel'">
                  {{ booking.type === 'ticket' ? $t('payment.booking.ticket') : $t('payment.booking.hotel') }}
                </span>
                <span class="item-name">{{ booking.title }}</span>
              </div>
              <span class="item-price">¥{{ booking.price.toFixed(2) }}</span>
            </div>

            <div class="item-meta">
              <div class="meta-row" v-if="booking.attractionText || booking.addressText">
                <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                  <circle cx="12" cy="10" r="3"></circle>
                </svg>
                <span>{{ booking.attractionText || booking.addressText }}</span>
              </div>
              <div class="meta-row">
                <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="4" width="18" height="18" rx="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                <span>{{ booking.dateText }}</span>
              </div>
              <div class="meta-row">
                <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <span>{{ booking.detailText }}</span>
              </div>
            </div>

            <!-- 门票明细 -->
            <div v-if="booking.type === 'ticket' && booking.items.length" class="ticket-items">
              <span class="ticket-tag" v-for="item in booking.items" :key="item.id">
                {{ item.ticketName }} × {{ item.quantity }}
              </span>
            </div>

            <!-- 房间明细 -->
            <div v-if="booking.type === 'hotel' && booking.rooms.length" class="ticket-items">
              <span class="ticket-tag" v-for="room in booking.rooms" :key="room.id">
                {{ $t('payment.booking.roomTag', { name: room.name, quantity: room.quantity, nights: room.nights }) }}
              </span>
            </div>

            <div class="item-footer">
              <div class="footer-meta">
                <span class="booking-no">{{ $t('payment.booking.bookingNo', { id: booking.id }) }}</span>
                <span class="booking-time">{{ booking.createdText }}</span>
              </div>
              <div class="booking-actions" @click.stop>
                <button type="button" class="mini-btn" @click="handleEdit(booking)">{{ $t('payment.booking.edit') }}</button>
                <button type="button" class="mini-btn" @click="handleCancel(booking)">{{ $t('payment.booking.cancel') }}</button>
                <button type="button" class="mini-btn mini-btn--danger" @click="handleDelete(booking)">{{ $t('payment.booking.delete') }}</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div class="card-footer" v-if="!loading && userPhone && filteredBookings.length > 0">
      <div class="footer-left">
        <label class="apple-checkbox">
          <input
            type="checkbox"
            v-model="selectAllFlag"
            @change="toggleSelectAll"
          >
          <span class="checkbox-custom"></span>
        </label>
        <span class="select-label">{{ $t('payment.booking.selectAll') }}</span>
        <span class="selected-count">{{ $t('payment.booking.selectedCount', { count: selectedCount }) }}</span>
      </div>
      <div class="footer-right">
        <div class="total-price">
          <span class="total-label">{{ $t('payment.booking.total') }}</span>
          <span class="price-value">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <button class="primary-button pay-btn" :disabled="submitting" @click="checkout">
          {{ submitting ? $t('payment.booking.creating') : $t('payment.booking.pay') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getTicketBookingList,
  getHotelBookingList,
  cancelTicketBooking,
  deleteTicketBooking,
  cancelHotelBooking,
  deleteHotelBooking
} from '@/api/booking.js'
import { createOrder } from '@/api/order.js'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const emit = defineEmits(['checkout'])
const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const submitting = ref(false)
const activeFilter = ref('all')
const selectAllFlag = ref(false)
const userPhone = ref('')

// 统一的预订卡片数据：type = 'ticket' | 'hotel'
const bookingList = ref([])

// 本次结算的预订（支付成功后据此更新状态并移除）
const pendingPayBookings = ref([])

const filters = computed(() => [
  { label: t('payment.booking.filterAll'), value: 'all' },
  { label: t('payment.booking.ticket'), value: 'ticket' },
  { label: t('payment.booking.hotel'), value: 'hotel' }
])

// 计算酒店入住晚数
const calcNights = (checkIn, checkOut) => {
  if (!checkIn || !checkOut) return 1
  const start = new Date(checkIn)
  const end = new Date(checkOut)
  const nights = Math.ceil((end - start) / (1000 * 60 * 60 * 24))
  return nights > 0 ? nights : 1
}

// 格式化日期显示（去掉时间部分）
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return String(dateStr).split('T')[0]
}

// 格式化创建时间
const formatCreated = (dateStr) => {
  if (!dateStr) return ''
  return String(dateStr).replace('T', ' ').slice(0, 19)
}

// 组装门票预订卡片数据
const mapTicketBooking = (booking) => {
  const items = booking.items || []
  const totalQuantity = items.reduce((sum, item) => sum + (item.quantity || 0), 0)
  const price = items.reduce((sum, item) => sum + (item.ticketPrice || 0) * (item.quantity || 0), 0)
  // 景点名称（多种票可能来自不同景点，去重后用「、」连接）
  const attractionNames = [...new Set(items.map(item => item.attractionName).filter(Boolean))]
  const attractionText = attractionNames.join('、')
  const title = attractionText
    ? (items.length === 1
        ? t('payment.booking.ticketTitleSame', { attraction: attractionText, ticket: items[0].ticketName })
        : t('payment.booking.ticketTitleMulti', { attraction: attractionText, count: items.length }))
    : (items.length === 1 ? items[0].ticketName : t('payment.booking.ticketTitleOnlyMulti', { count: items.length }))
  return {
    key: `ticket-${booking.id}`,
    type: 'ticket',
    id: booking.id,
    title,
    price,
    items,
    dateText: t('payment.booking.visitDate', { date: formatDate(booking.visitDate) }),
    detailText: t('payment.booking.ticketContact', { name: booking.contactName, count: totalQuantity }),
    createdText: formatCreated(booking.createdAt),
    attractionText,
    selected: false
  }
}

// 组装酒店预订卡片数据
const mapHotelBooking = (booking) => {
  const nights = calcNights(booking.checkInDate, booking.checkOutDate)
  const items = booking.items || []
  let title = booking.roomName || t('payment.booking.hotelTitle')
  let addressText = ''
  let rooms = []
  let price = 0

  if (items.length > 0) {
    // 有明细：以明细表快照为准（酒店名/地址/房间/小计）
    const first = items[0]
    title = first.hotelName || title
    const addrParts = [first.province, first.city, first.address].filter(Boolean)
    addressText = addrParts.join(' · ')
    price = items.reduce((sum, item) => sum + Number(item.subtotal || 0), 0)
    rooms = items.map(item => ({
      id: item.id,
      name: item.roomName,
      quantity: item.quantity || 1,
      nights: item.nights || nights,
      price: item.roomPrice
    }))
  } else {
    // 旧数据兼容：无明细时按主表字段计算
    price = (booking.roomPrice || 0) * nights
    rooms = [{ id: booking.roomId, name: booking.roomName, quantity: 1, nights, price: booking.roomPrice }]
  }

  return {
    key: `hotel-${booking.id}`,
    type: 'hotel',
    id: booking.id,
    title,
    price,
    rooms,
    items,
    dateText: t('payment.booking.hotelDate', {
      checkIn: formatDate(booking.checkInDate),
      checkOut: formatDate(booking.checkOutDate),
      nights
    }),
    detailText: t('payment.booking.hotelContact', { name: booking.contactName, guests: booking.guests }),
    createdText: formatCreated(booking.createdAt),
    addressText,
    selected: false
  }
}

const filteredBookings = computed(() => {
  if (activeFilter.value === 'all') return bookingList.value
  return bookingList.value.filter(item => item.type === activeFilter.value)
})

const selectedBookings = computed(() => filteredBookings.value.filter(item => item.selected))

const selectedCount = computed(() => selectedBookings.value.length)

const totalPrice = computed(() => {
  return selectedBookings.value.reduce((sum, item) => sum + item.price, 0)
})

const toggleSelect = (booking) => {
  booking.selected = !booking.selected
  syncSelectAllFlag()
}

const toggleSelectAll = () => {
  filteredBookings.value.forEach(item => {
    item.selected = selectAllFlag.value
  })
}

const syncSelectAllFlag = () => {
  selectAllFlag.value = filteredBookings.value.length > 0
    && filteredBookings.value.every(item => item.selected)
}

// 获取用户联系电话
const getUserPhone = () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('user'))
    return userInfo?.phone || ''
  } catch (error) {
    return ''
  }
}

const getUserId = () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('user'))
    return userInfo?.id || userInfo?.userId || ''
  } catch (error) {
    return ''
  }
}

// 并行拉取门票 + 酒店预订
const fetchBookings = async () => {
  userPhone.value = getUserPhone()
  if (!userPhone.value) return

  loading.value = true
  try {
    const [ticketRes, hotelRes] = await Promise.all([
      getTicketBookingList(userPhone.value),
      getHotelBookingList(userPhone.value)
    ])

    const ticketBookings = (ticketRes.code === 200 && Array.isArray(ticketRes.data))
      ? ticketRes.data.map(mapTicketBooking)
      : []
    const hotelBookings = (hotelRes.code === 200 && Array.isArray(hotelRes.data))
      ? hotelRes.data.map(mapHotelBooking)
      : []

    bookingList.value = [...ticketBookings, ...hotelBookings]
  } catch (error) {
    console.error('获取预订数据失败:', error)
    bookingList.value = []
  } finally {
    loading.value = false
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchBookings)

// 结算：为每项预订创建订单，触发支付流程
const checkout = async () => {
  if (selectedBookings.value.length === 0) {
    ElMessage.warning(t('payment.booking.selectRequired'))
    return
  }
  const userId = getUserId()
  if (!userId) {
    ElMessage.warning(t('payment.booking.loginRequired'))
    return
  }
  if (submitting.value) return
  submitting.value = true

  try {
    const orderPromises = selectedBookings.value.map(booking => {
      const isTicket = booking.type === 'ticket'
      const totalQuantity = isTicket
        ? booking.items.reduce((sum, item) => sum + (item.quantity || 0), 0)
        : 1
      return createOrder({
        itemType: booking.type,
        itemId: booking.id,
        itemName: booking.title,
        image: '',
        amount: booking.price,
        quantity: totalQuantity,
        remark: isTicket ? booking.dateText : booking.dateText
      })
    })

    const responses = await Promise.all(orderPromises)
    const failed = responses.filter(response => response.code !== 200)
    if (failed.length > 0) {
      ElMessage.error(t('payment.booking.orderCreatePartialFailed'))
      return
    }

    const orderIds = responses.map(response => response.data.orderId || response.data.id || response.data.orderNo)

    // 记录本次结算的预订，待支付成功后更新状态并移除
    pendingPayBookings.value = selectedBookings.value.map(booking => ({
      type: booking.type,
      id: booking.id
    }))

    emit('checkout', {
      orderId: orderIds[0],
      orderIds,
      userId,
      cartItems: selectedBookings.value.map(booking => ({
        id: booking.id,
        itemName: booking.title,
        itemType: booking.type,
        price: booking.price,
        quantity: 1
      }))
    })
  } catch (error) {
    console.error('创建预订订单失败:', error)
    ElMessage.error(t('payment.booking.orderCreateFailed'))
  } finally {
    submitting.value = false
  }
}

// 从列表移除指定预订（本地即时反馈）
const removeBookingFromList = (booking) => {
  bookingList.value = bookingList.value.filter(item => item.key !== booking.key)
  syncSelectAllFlag()
}

// 取消预订
const handleCancel = async (booking) => {
  try {
    await ElMessageBox.confirm(t('payment.booking.confirmCancel'), t('payment.common.tip'), {
      confirmButtonText: t('payment.common.confirm'),
      cancelButtonText: t('payment.common.cancel'),
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    const response = booking.type === 'ticket'
      ? await cancelTicketBooking(booking.id)
      : await cancelHotelBooking(booking.id)
    if (response && response.code === 200) {
      ElMessage.success(t('payment.booking.cancelSuccess'))
      removeBookingFromList(booking)
    } else {
      ElMessage.error(response?.message || t('payment.booking.cancelFailed'))
    }
  } catch (error) {
    console.error('取消预订失败:', error)
    ElMessage.error(t('payment.booking.cancelFailed'))
  }
}

// 删除预订
const handleDelete = async (booking) => {
  try {
    await ElMessageBox.confirm(t('payment.booking.confirmDelete'), t('payment.common.warning'), {
      confirmButtonText: t('payment.common.delete'),
      cancelButtonText: t('payment.common.cancel'),
      type: 'error'
    })
  } catch {
    return
  }
  try {
    const response = booking.type === 'ticket'
      ? await deleteTicketBooking(booking.id)
      : await deleteHotelBooking(booking.id)
    if (response && response.code === 200) {
      ElMessage.success(t('payment.booking.deleteSuccess'))
      removeBookingFromList(booking)
    } else {
      ElMessage.error(response?.message || t('payment.booking.deleteFailed'))
    }
  } catch (error) {
    console.error('删除预订失败:', error)
    ElMessage.error(t('payment.booking.deleteFailed'))
  }
}

// 修改预订：跳转回预订表单并回填该预订数据
const handleEdit = (booking) => {
  router.push({
    path: '/predetermined',
    query: {
      tab: booking.type === 'ticket' ? 'attraction' : 'hotel',
      editType: booking.type,
      editId: booking.id
    }
  })
}

// 支付成功后：后端已自动将预订置为已完成，这里只需刷新列表
const completePaidBookings = async () => {
  if (pendingPayBookings.value.length === 0) return
  pendingPayBookings.value = []
  try {
    ElMessage.success(t('payment.booking.paidSuccess'))
    await fetchBookings()
  } catch (error) {
    console.error('刷新预订列表失败:', error)
    ElMessage.error(t('payment.booking.refreshFailed'))
  }
}

// 监听 PayPage 支付成功后发出的 closeModal 消息（仅支付成功时会发出）
const handlePaymentMessage = (event) => {
  if (event.data?.action === 'closeModal' && pendingPayBookings.value.length > 0) {
    completePaidBookings()
  }
}

onMounted(() => {
  fetchBookings()
  window.addEventListener('message', handlePaymentMessage)
})

onBeforeUnmount(() => {
  window.removeEventListener('message', handlePaymentMessage)
})
</script>

<style scoped>
.predetermined-card {
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
  color: var(--theme-text-1);
}

/* ===== 头部 ===== */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--theme-hairline);
  flex-wrap: wrap;
  gap: 12px;
}

.card-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--theme-text-1);
  margin: 0;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-tabs {
  display: inline-flex;
  padding: 3px;
  background: var(--theme-bg-surface-2);
  border-radius: 20px;
}

.filter-btn {
  border: none;
  background: transparent;
  color: var(--theme-text-2);
  font-size: 13px;
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 17px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
}

.filter-btn:hover:not(.active) {
  color: var(--theme-text-1);
}

.filter-btn.active {
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-weight: 600;
  box-shadow: var(--theme-shadow-sm);
}

.refresh-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--theme-border);
  border-radius: 50%;
  background: transparent;
  color: var(--theme-text-2);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.refresh-btn:hover {
  color: var(--theme-primary);
  border-color: var(--theme-primary);
  transform: rotate(30deg);
}

/* ===== 状态区（加载/空） ===== */
.state-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 48px 0;
}

.empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.state-text {
  font-size: 14px;
  color: var(--theme-text-2);
}

.loading-spinner {
  width: 28px;
  height: 28px;
  border: 2px solid var(--theme-border-strong);
  border-top-color: var(--theme-primary);
  border-right-color: var(--theme-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* ===== 预订卡片列表 ===== */
.booking-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.booking-item {
  display: flex;
  gap: 14px;
  padding: 18px 20px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.booking-item:hover {
  border-color: var(--theme-border-strong);
  box-shadow: var(--theme-shadow-sm);
}

.booking-item.selected {
  border-color: var(--theme-primary);
  background: var(--theme-primary-soft);
}

.item-main {
  flex: 1;
  min-width: 0;
}

.item-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.item-title-group {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.type-badge {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.4;
}

.badge-ticket {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.badge-hotel {
  background: color-mix(in srgb, var(--theme-accent-2) 12%, transparent);
  color: var(--theme-accent-2);
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price {
  flex-shrink: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--theme-danger);
}

.item-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 10px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--theme-text-2);
  line-height: 1.5;
}

.meta-icon {
  flex-shrink: 0;
  width: 14px;
  height: 14px;
  color: var(--theme-text-3);
}

.ticket-items {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;
}

.ticket-tag {
  display: inline-flex;
  padding: 4px 12px;
  background: var(--theme-bg-surface-2);
  border-radius: 14px;
  font-size: 12px;
  color: var(--theme-text-1);
  line-height: 1.4;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px dashed var(--theme-border);
  font-size: 12px;
  color: var(--theme-text-3);
  gap: 12px;
  flex-wrap: wrap;
}

.footer-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.booking-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  margin-left: auto;
}

.mini-btn {
  padding: 4px 12px;
  border: 1px solid var(--theme-border);
  border-radius: 8px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
}

.mini-btn:hover {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
}

.mini-btn--danger {
  color: var(--theme-danger);
  border-color: color-mix(in srgb, var(--theme-danger) 30%, transparent);
}

.mini-btn--danger:hover {
  background: var(--theme-danger);
  border-color: var(--theme-danger);
  color: var(--theme-primary-text);
}

.booking-no {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.booking-time {
  flex-shrink: 0;
}

/* ===== 复选框（与购物车一致） ===== */
.apple-checkbox {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
}

.apple-checkbox input {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.checkbox-custom {
  width: 20px;
  height: 20px;
  border: 1.5px solid var(--theme-border-strong);
  border-radius: 6px;
  background: var(--theme-bg-surface);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  flex-shrink: 0;
}

.apple-checkbox input:checked + .checkbox-custom {
  background: var(--theme-primary);
  border-color: var(--theme-primary);
}

.apple-checkbox input:checked + .checkbox-custom::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid var(--theme-primary-text);
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.item-checkbox {
  display: flex;
  align-items: flex-start;
  padding-top: 2px;
}

/* ===== 底部结算栏 ===== */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding: 18px 20px;
  background: var(--theme-bg-surface-2);
  border-radius: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.select-label {
  font-size: 14px;
  color: var(--theme-text-1);
}

.selected-count {
  font-size: 13px;
  color: var(--theme-text-2);
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 18px;
}

.total-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-size: 14px;
  color: var(--theme-text-2);
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--theme-danger);
}

.primary-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 42px;
  padding: 0 26px;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  border: none;
  border-radius: 21px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: background 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
}

.primary-button:hover {
  background: var(--theme-primary-hover);
}

.pay-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===== 响应式 ===== */
@media (max-width: 767px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .booking-item {
    padding: 14px 16px;
  }

  .item-top {
    flex-wrap: wrap;
  }

  .item-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .booking-actions {
    margin-left: 0;
    width: 100%;
    flex-wrap: wrap;
  }

  .mini-btn {
    flex: 1;
    text-align: center;
  }

  .card-footer {
    flex-direction: column;
    align-items: stretch;
  }

  .footer-right {
    justify-content: space-between;
  }

  .pay-btn {
    flex: 1;
  }
}
</style>
