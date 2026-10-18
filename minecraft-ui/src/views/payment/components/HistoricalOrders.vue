<template>
  <div class="historical-orders">
    <div class="orders-header">
      <h2>历史订单</h2>
      <div class="order-filters">
        <button 
          v-for="filter in filters" 
          :key="filter.value"
          :class="['filter-btn', { active: activeFilter === filter.value }]"
          @click="activeFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>
    
    <div class="orders-list">
      <div v-if="orders.length === 0" class="empty-orders">
        <div class="empty-icon">
          <svg t="1773668737283" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" width="128" height="128">
                <path d="M758.723 194.016c27.266 0 49.345-21.366 49.345-47.699V50.92c0-26.35-22.08-47.714-49.345-47.714-27.267 0-49.347 21.365-49.347 47.714v95.398c0.002 26.333 22.082 47.699 49.347 47.699z m-493.448 0c27.267 0 49.346-21.366 49.346-47.699V50.92c0-26.35-22.079-47.714-49.346-47.714-27.266 0-49.344 21.365-49.344 47.714v95.398c0.001 26.333 22.08 47.699 49.344 49.344z" fill="currentColor"></path>
                <path d="M939.642 98.617h-82.23v47.698c0 52.668-44.188 95.4-98.688 95.4s-98.687-42.732-98.687-95.4V98.617H607.4c0-1.117-40.9 0-95.4 0s-95.398 0.496-95.398 0h-52.639v47.698c0 52.668-44.156 95.4-98.688 95.4-54.499 0-98.688-42.732-98.688-95.4V98.617h-82.23c-27.266 0-49.346 21.367-49.346 47.698v826.78c0 26.333 22.08 47.699 49.346 47.699h855.287c27.266 0 49.345-21.366 49.345-47.7V146.316c-0.002-26.331-22.082-47.698-49.347-47.698zM725.817 861.795H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.195H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.684 15.961-35.646 35.644-35.646h427.635c19.685 0 35.646 15.962 35.646 35.646 0.001 19.672-15.96 35.634-35.645 35.634z m0-184.196H298.181c-19.683 0-35.644-15.962-35.644-35.634 0-19.683 15.961-35.644 35.644-35.644h427.635c19.685 0 35.646 15.96 35.646 35.644 0.001 19.673-15.96 35.634-35.645 35.634z" fill="currentColor"></path>
              </svg>
        </div>
        <div class="empty-text">暂无历史订单</div>
        <router-link to="/payment" class="btn btn-primary">去购物</router-link>
      </div>
      <div v-else class="order-item" v-for="order in pagedOrders" :key="order.key">
        <div class="order-header">
          <div class="order-id">订单编号：{{ order.orderId }}</div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </div>
        </div>
        <div class="order-content">
          <div class="order-info">
            <div class="info-item">
              <span class="label">下单时间</span>
              <span class="value">{{ order.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">支付时间</span>
              <span class="value">{{ getPayTimeText(order) || '未支付' }}</span>
            </div>
            <div class="info-item">
              <span class="label">订单金额</span>
              <span class="value price">¥{{ formatMoney(order.totalPrice) }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ isShippableOrder(order) ? '物流状态' : $t('payment.history.orderType') }}</span>
              <span class="value">{{ isShippableOrder(order) ? order.statusDesc : getItemTypeText(order.itemType) }}</span>
            </div>
          </div>
          <!-- 已执行的支付行为（商城取聚合支付单，会员/车票取业务订单） -->
          <div class="payment-record" v-if="order.payNo || order.source !== 'mall'">
            <div class="payment-record__head">
              <span class="payment-record__title">{{ $t('payment.history.paymentRecord') }}</span>
              <span
                v-if="getPaymentView(order)"
                class="payment-status"
                :class="getPayStatusClass(getPaymentView(order)?.status)"
              >
                {{ getPayStatusText(getPaymentView(order)) }}
              </span>
            </div>
            <div class="payment-record__grid">
              <div class="info-item">
                <span class="label">{{ $t('payment.orderDetail.channel') }}</span>
                <span class="value">{{ getChannelText(getPaymentView(order)?.channelCode) }}</span>
              </div>
              <div class="info-item">
                <span class="label">{{ $t('payment.orderDetail.payAmount') }}</span>
                <span class="value price">{{ payAmountText(order) }}</span>
              </div>
              <div class="info-item">
                <span class="label">{{ $t('payment.orderDetail.payTime') }}</span>
                <span class="value">{{ getPayTimeText(order) || '-' }}</span>
              </div>
              <div class="info-item" v-if="order.payNo">
                <span class="label">{{ $t('payment.orderDetail.payNo') }}</span>
                <span class="value mono">{{ order.payNo }}</span>
              </div>
              <div class="info-item" v-if="Number(getPaymentView(order)?.refundedAmount) > 0">
                <span class="label">{{ $t('payment.orderDetail.refundedAmount') }}</span>
                <span class="value">¥{{ formatMoney(getPaymentView(order)?.refundedAmount) }}</span>
              </div>
            </div>
          </div>
          <div class="order-products">
            <div class="product-item" v-for="(item, index) in order.items" :key="index">
              <div class="product-image">
                <img v-if="item.image" :src="item.image" :alt="item.name">
                <span v-else class="product-image__fallback">{{ getItemFallbackText(order) }}</span>
              </div>
              <div class="product-info">
                <div class="product-name">{{ item.name }}</div>
                <div class="product-price">
                  <span class="price">¥{{ formatMoney(item.price) }}</span>
                  <span class="quantity">x{{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="order-footer">
          <div class="order-total">
            共 {{ order.quantity }} 件商品，合计：
            <span class="total-price">¥{{ formatMoney(order.totalPrice) }}</span>
          </div>
          <div class="order-actions">
            <template v-if="order.source === 'mall'">
              <button v-if="order.status === '0'" class="btn btn-primary" @click="goToPay(order)">
                立即支付
              </button>
              <button v-if="order.status === '0'" class="btn btn-secondary" @click="handleCancelOrder(order.id)">
                取消订单
              </button>
              <button v-if="order.status === '3'" class="btn btn-secondary" @click="reviewOrder(order.id)">
                评价
              </button>
              <button class="btn btn-link" @click="viewOrderDetail(order.id)">
                查看详情
              </button>
              <button v-if="canViewLogistics(order)" class="btn btn-secondary" @click="viewLogistics(order)">
                查看物流
              </button>
              <button class="btn btn-delete" @click="handleDeleteOrder(order.id)">
                删除订单
              </button>
            </template>
            <button v-else class="btn btn-link" @click="openSourceOrder(order)">
              {{ order.source === 'member' ? $t('payment.history.memberCenter') : $t('payment.history.myTickets') }}
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="orders-pagination" v-if="totalPages > 1">
      <button 
        class="page-btn" 
        :disabled="currentPage === 1"
        @click="currentPage--"
      >上一页</button>
      <span class="page-info">
        {{ currentPage }} / {{ totalPages }}
      </span>
      <button 
        class="page-btn" 
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, cancelOrder, deleteOrder } from '@/api/order.js'
import { queryPayment } from '@/api/payment.js'
import { getMyMemberOrders } from '@/api/member.js'
import { getRailwayOrders } from '@/api/railway.js'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const router = useRouter()
const emit = defineEmits(['view-detail', 'pay'])
const { t, te } = useI18n()

// 国际化键存在时取翻译，否则回退
const tOr = (key, fallback) => (te(key) ? t(key) : fallback)

// 订单数据（商城订单 + 会员订单 + 火车票订单合并）
const orders = ref([])
const loading = ref(false)
// 商城订单主键 → 聚合支付单
const payments = ref({})

// 筛选条件
const filters = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: '0' },
  { label: '已支付', value: '1' },
  { label: '已发货', value: '2' },
  { label: '已完成', value: '3' },
  { label: '已取消', value: '4' }
]

const activeFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)

// 筛选订单
const filteredOrders = computed(() => {
  if (activeFilter.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === activeFilter.value)
})

// 计算总页数
const totalPages = computed(() => Math.ceil(filteredOrders.value.length / pageSize.value))

// 当前页订单（多来源合并后由前端统一分页）
const pagedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredOrders.value.slice(start, start + pageSize.value)
})

// 会员订单 / 火车票订单状态 → 商城订单统一状态码
const MEMBER_STATUS_MAP = { 0: '0', 1: '1', 2: '4', 3: '5' }
const RAILWAY_STATUS_MAP = { PENDING: '0', PAID: '1', CANCELLED: '4', REFUNDED: '5' }
const MEMBER_PAY_CHANNEL = { 1: 'ALIPAY', 2: 'WECHAT', 3: 'BALANCE' }

const toTimestamp = (value) => {
  if (!value) return 0
  const ts = new Date(String(value).replace(' ', 'T')).getTime()
  return Number.isNaN(ts) ? 0 : ts
}

// 商城 / 预订订单
const normalizeMallOrder = (order) => ({
  key: `mall-${order.id}`,
  id: order.id,
  source: 'mall',
  orderId: order.orderNo,
  status: order.status != null ? String(order.status) : '',
  rawStatus: order.status,
  itemType: order.itemType || '',
  payNo: order.payNo || '',
  createTime: formatTime(order.createTime),
  updateTime: formatTime(order.updateTime),
  totalPrice: Number(order.amount) || 0,
  quantity: order.quantity || 1,
  shippingStatus: order.shippingStatus,
  carrierCode: order.carrierCode,
  trackingNo: order.trackingNo,
  statusDesc: shippingStatusText(order.status),
  items: [{
    id: order.itemId,
    name: order.itemName,
    price: Number(order.amount) || 0,
    quantity: order.quantity || 1,
    image: order.image,
  }]
})

// 会员开通订单
const normalizeMemberOrder = (order) => {
  const status = MEMBER_STATUS_MAP[order.status] ?? '0'
  return {
    key: `member-${order.id}`,
    id: order.id,
    source: 'member',
    orderId: order.orderNo,
    status,
    rawStatus: order.status,
    itemType: 'member',
    payNo: '',
    payType: order.payType,
    createTime: formatTime(order.createTime),
    updateTime: formatTime(order.payTime || order.updateTime),
    payTime: formatTime(order.payTime),
    totalPrice: Number(order.amount) || 0,
    quantity: 1,
    statusDesc: getStatusText(status),
    items: [{
      id: order.packageId,
      name: t('payment.history.memberItem', { name: order.packageName || '-' }),
      price: Number(order.amount) || 0,
      quantity: 1,
      image: ''
    }]
  }
}

// 火车票订单（金额单位为分）
const normalizeRailwayOrder = (order) => {
  const status = RAILWAY_STATUS_MAP[order.status] ?? '0'
  const amount = Number(order.totalAmount || 0) / 100
  const route = [order.fromStationName, order.toStationName].filter(Boolean).join(' → ')
  return {
    key: `railway-${order.id}`,
    id: order.id,
    source: 'railway',
    orderId: order.orderNo,
    status,
    rawStatus: order.status,
    itemType: 'railway',
    payNo: '',
    createTime: formatTime(order.createTime),
    updateTime: formatTime(order.payTime),
    payTime: formatTime(order.payTime),
    totalPrice: amount,
    quantity: order.passengerCount || 1,
    statusDesc: getStatusText(status),
    items: [{
      id: order.id,
      name: [order.trainNo, route].filter(Boolean).join(' ') || order.orderNo,
      price: amount,
      quantity: order.passengerCount || 1,
      image: ''
    }]
  }
}

// 分页拉取全部商城订单（合并多来源后由前端统一分页）
const fetchAllMallOrders = async () => {
  const size = 100
  const list = []
  let pageNum = 1
  let total = 0
  do {
    const response = await getOrderList({ pageNum, pageSize: size })
    list.push(...(response?.data?.records || []))
    total = response?.data?.total || 0
    pageNum += 1
  } while (list.length < total && pageNum <= 20)
  return list
}

// 分页拉取全部火车票订单
const fetchAllRailwayOrders = async () => {
  const size = 50
  const list = []
  let pageNum = 1
  let total = 0
  do {
    const response = await getRailwayOrders({ pageNum, pageSize: size })
    list.push(...(response?.data?.records || []))
    total = response?.data?.total || 0
    pageNum += 1
  } while (list.length < total && pageNum <= 20)
  return list
}

// 获取订单列表（商城 + 会员 + 火车票）
const fetchOrders = async () => {
  loading.value = true
  try {
    const [mallList, memberList, railwayList] = await Promise.all([
      fetchAllMallOrders(),
      getMyMemberOrders()
        .then((response) => (response?.code === 200 ? response.data || [] : []))
        .catch(() => []),
      fetchAllRailwayOrders().catch(() => [])
    ])

    orders.value = [
      ...mallList.map(normalizeMallOrder),
      ...memberList.map(normalizeMemberOrder),
      ...railwayList.map(normalizeRailwayOrder)
    ].sort((a, b) => toTimestamp(b.createTime) - toTimestamp(a.createTime))

    // 查询商城订单关联的聚合支付单（用户归属由后端校验）
    await loadPayments(orders.value)
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error(t('payment.history.loadFailed'))
  } finally {
    loading.value = false
  }
}

// 批量查询商城订单关联的支付单
const loadPayments = async (list) => {
  const targets = list.filter((order) => order.source === 'mall' && order.payNo)
  if (targets.length === 0) {
    payments.value = {}
    return
  }
  const results = await Promise.all(
    targets.map(async (order) => {
      try {
        const response = await queryPayment(order.payNo, { silent: true })
        return { id: order.id, payment: response?.code === 200 ? response.data : null }
      } catch {
        return { id: order.id, payment: null }
      }
    })
  )
  const map = {}
  results.forEach(({ id, payment }) => {
    if (payment) map[id] = payment
  })
  payments.value = map
}

// 时间格式化：2026-10-11T16:16:02 → 2026-10-11 16:16:02
const formatTime = (time) => (time ? String(time).replace('T', ' ') : '')

// 金额格式化：整数不带小数，非整数保留两位
const formatMoney = (value) => {
  const num = Number(value || 0)
  return Number.isInteger(num) ? String(num) : num.toFixed(2)
}

// 商城订单对应的聚合支付单
const getPayment = (order) => payments.value[order.id] || null

// 统一支付信息视图：商城取聚合支付单，会员/车票用业务订单字段兜底
const getPaymentView = (order) => {
  if (order.source === 'mall') return getPayment(order)
  if (order.source === 'member') {
    const raw = String(order.rawStatus ?? '')
    const statusMap = { '0': 'INIT', '1': 'SUCCESS', '2': 'CLOSED', '3': 'REFUNDED' }
    return {
      status: statusMap[raw] || 'INIT',
      channelCode: MEMBER_PAY_CHANNEL[order.payType] || 'WECHAT',
      payAmount: order.totalPrice,
      payTime: order.payTime,
      refundedAmount: 0
    }
  }
  if (order.source === 'railway') {
    const statusMap = { PENDING: 'INIT', PAID: 'SUCCESS', CANCELLED: 'CLOSED', REFUNDED: 'REFUNDED' }
    return {
      status: statusMap[order.rawStatus] || 'INIT',
      // 车票订单表未留存渠道信息，展示为未知
      channelCode: '',
      payAmount: order.totalPrice,
      payTime: order.payTime,
      refundedAmount: 0
    }
  }
  return null
}

// 支付时间：优先取支付单成功时间，未支付订单显示占位
const getPayTimeText = (order) => {
  const payment = getPaymentView(order)
  if (payment?.payTime) return formatTime(payment.payTime)
  if (order.status === '0') return t('payment.history.unpaid')
  return order.updateTime || ''
}

// 支付渠道文案
const getChannelText = (code) =>
  tOr(`payment.orderDetail.channels.${String(code || '').toLowerCase()}`, code || '-')

// 支付状态文案（后端 statusDesc 兜底）
const getPayStatusText = (payment) => {
  if (!payment) return '-'
  const status = String(payment.status || '')
  return tOr(`payment.orderDetail.statusLabels.${status}`, payment.statusDesc || status || '-')
}

// 支付状态样式沿用订单状态的配色
const getPayStatusClass = (status) => {
  const map = {
    SUCCESS: 'paid',
    INIT: 'unpaid',
    PAYING: 'unpaid',
    FAILED: 'cancelled',
    CLOSED: 'cancelled',
    REFUNDING: 'refunded',
    REFUNDED: 'refunded'
  }
  return map[status] || 'unpaid'
}

// 实付金额（积分订单显示积分兑换）
const payAmountText = (order) => {
  if (order.itemType === 'points') return t('payment.points')
  const payment = getPaymentView(order)
  return `¥${formatMoney(payment?.payAmount ?? order.totalPrice)}`
}

// 订单类型文案
const ITEM_TYPE_I18N_KEYS = {
  food: 'payment.food',
  product: 'payment.product',
  hotel: 'payment.hotel',
  ticket: 'payment.ticket',
  points: 'payment.points',
  member: 'payment.orderDetail.bizTypes.MEMBER',
  railway: 'payment.orderDetail.bizTypes.RAILWAY'
}
const getItemTypeText = (itemType) => {
  const type = String(itemType || '').toLowerCase()
  return tOr(ITEM_TYPE_I18N_KEYS[type] || '', type || '-')
}

// 是否使用物流系统：仅纪念品等实物商品（酒店/景点/美食等不发货）
const SHIPPABLE_ITEM_TYPES = ['product', 'points']
const isShippableOrder = (order) =>
  SHIPPABLE_ITEM_TYPES.includes(String(order.itemType || '').toLowerCase())

// 无商品图时的占位文案
const getItemFallbackText = (order) => {
  if (order.itemType === 'member') return t('payment.history.memberShort')
  if (order.itemType === 'railway') return t('payment.history.railwayShort')
  return t('payment.goods')
}

// 会员 / 车票订单跳转到对应业务页处理
const openSourceOrder = (order) => {
  router.push(order.source === 'member' ? '/member' : '/ticket/my-orders')
}

// 数据变更后自动刷新
useAutoRefresh(fetchOrders)

// 获取订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '待支付',
    '1': '已支付',
    '2': '已发货',
    '3': '已完成',
    '4': '已取消',
    '5': '已退款'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态样式类
const getStatusClass = (status) => {
  const classMap = {
    '0': 'unpaid',
    '1': 'paid',
    '2': 'shipping',
    '3': 'completed',
    '4': 'cancelled',
    '5': 'refunded'
  }
  return classMap[status] || 'unpaid'
}

// 物流状态文本（已支付即进入待发货）
const shippingStatusText = (status) => {
  const map = {
    '0': '待支付',
    '1': '待发货',
    '2': '已发货',
    '3': '已签收',
    '4': '已取消',
    '5': '已退款'
  }
  return map[status] || '未知状态'
}

// 是否可查看物流：仅纪念品等实物商品且已进入物流环节
const canViewLogistics = (order) =>
  isShippableOrder(order) && ['1', '2', '3'].includes(order.status)

// 查看物流
const viewLogistics = (order) => {
  router.push({
    path: '/logistics',
    query: { orderId: order.id }
  })
}

// 立即支付：拉起支付弹窗（使用数据库主键 id，而非订单编号 orderNo）
const goToPay = (order) => {
  emit('pay', order.id)
}

// 取消订单
const handleCancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用 API 取消订单
    await cancelOrder(orderId)
    ElMessage.success('订单已取消')
    
    // 重新获取订单列表
    await fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败，请重试')
    }
  }
}

// 删除订单
const handleDeleteOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要删除订单吗？此操作不可恢复！', '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    // 调用 API 删除订单
    await deleteOrder(orderId)
    ElMessage.success('订单已删除')
    
    // 重新获取订单列表
    await fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      ElMessage.error('删除订单失败，请重试')
    }
  }
}

// 评价订单
const reviewOrder = (orderId) => {
  ElMessage.info('评价功能开发中')
}

// 查看订单详情
const viewOrderDetail = (orderId) => {
  emit('view-detail', orderId)
}

// 组件挂载时的逻辑
onMounted(() => {
  fetchOrders()
})

// 筛选条件变化时回到第一页（前端统一分页）
watch(activeFilter, () => {
  currentPage.value = 1
})

// 数据刷新后避免停留在超出范围的页码
watch(totalPages, (pages) => {
  if (pages > 0 && currentPage.value > pages) {
    currentPage.value = pages
  }
})
</script>

<style scoped>
.historical-orders {
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif;
  color: var(--theme-text-1);
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--theme-hairline);
  gap: 16px;
  flex-wrap: wrap;
}

.orders-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--theme-text-1);
  margin: 0;
  letter-spacing: -0.02em;
}

.order-filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 7px 16px;
  border: 1px solid var(--theme-border);
  border-radius: 20px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
}

.filter-btn:hover {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
}

.filter-btn.active {
  background: var(--theme-primary);
  border-color: var(--theme-primary);
  color: var(--theme-primary-text);
}

/* 空状态 */
.empty-orders {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 72px 0;
  text-align: center;
}

.empty-icon {
  width: 110px;
  height: 110px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--theme-bg-surface-2);
}

.empty-icon svg {
  width: 56px;
  height: 56px;
  color: var(--theme-text-3);
}

.empty-text {
  font-size: 16px;
  color: var(--theme-text-2);
  margin-bottom: 24px;
  font-weight: 500;
}

/* 订单列表 */
.orders-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
  gap: 16px;
}

.order-item {
  background: var(--theme-bg-surface);
  border-radius: 16px;
  padding: 20px;
  box-shadow: var(--theme-shadow-sm);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--theme-hairline);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.order-item:hover {
  transform: translateY(-3px);
  box-shadow: var(--theme-shadow-md);
  border-color: var(--theme-border);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--theme-hairline);
  gap: 10px;
}

.order-id {
  font-size: 13px;
  color: var(--theme-text-2);
  font-weight: 500;
  font-family: 'SF Mono', 'Menlo', monospace;
}

.order-status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.order-status::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.order-status.unpaid {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.order-status.paid {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.order-status.shipping {
  background: var(--theme-info-soft);
  color: var(--theme-info);
}

.order-status.completed {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.order-status.cancelled {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
}

.order-status.refunded {
  background: color-mix(in srgb, var(--theme-accent-2) 10%, transparent);
  color: var(--theme-accent-2);
}

.order-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 10px 16px;
}

/* 已执行的支付行为 */
.payment-record {
  margin: 12px 0;
  padding: 12px 14px;
  border-radius: 12px;
  background: var(--theme-primary-soft);
  border: 1px solid color-mix(in srgb, var(--theme-primary) 18%, transparent);
}

.payment-record__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.payment-record__title {
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-primary);
}

.payment-record__grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
}

/* 支付信息项自动撑满整行，避免出现半行空白 */
.payment-record__grid .info-item {
  flex: 1 1 140px;
  min-width: 0;
}

.payment-record__grid .value.mono {
  font-family: 'SF Mono', 'Menlo', monospace;
  font-size: 12px;
  word-break: break-all;
}

.payment-status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.payment-status::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.payment-status.unpaid {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.payment-status.paid {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.payment-status.cancelled {
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
}

.payment-status.refunded {
  background: color-mix(in srgb, var(--theme-accent-2) 10%, transparent);
  color: var(--theme-accent-2);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.info-item .label {
  font-size: 12px;
  color: var(--theme-text-3);
  font-weight: 400;
}

.info-item .value {
  font-size: 13px;
  color: var(--theme-text-1);
  font-weight: 500;
}

.info-item .price {
  color: var(--theme-text-1);
  font-weight: 700;
  font-size: 14px;
}

.order-products {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--theme-bg-surface-2);
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--theme-hairline);
  transition: background 0.2s ease;
}

.product-item:hover {
  background: var(--theme-bg-surface-2);
}

.product-image {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--theme-bg-surface);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

/* 会员 / 车票等无商品图时的占位 */
.product-image__fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-size: 12px;
  font-weight: 600;
  color: var(--theme-text-3);
  background: var(--theme-bg-surface-2);
}

.product-item:hover .product-image img {
  transform: scale(1.06);
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  color: var(--theme-text-1);
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 600;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.product-price .price {
  color: var(--theme-text-1);
  font-weight: 700;
}

.product-price .quantity {
  color: var(--theme-text-2);
  background: var(--theme-bg-surface);
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  border-top: 1px solid var(--theme-hairline);
  gap: 12px;
  flex-wrap: wrap;
}

.order-total {
  font-size: 13px;
  color: var(--theme-text-2);
  font-weight: 400;
}

.total-price {
  color: var(--theme-text-1);
  font-weight: 700;
  margin-left: 4px;
  font-size: 18px;
  letter-spacing: -0.01em;
}

.order-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.btn {
  padding: 7px 16px;
  border-radius: 10px;
  font-size: 13px;
  cursor: pointer;
  border: none;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

.btn-primary {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

.btn-primary:hover {
  background: var(--theme-primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--theme-shadow-md);
}

.btn-secondary {
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  border: 1px solid var(--theme-border);
}

.btn-secondary:hover {
  background: var(--theme-bg-hover);
}

.btn-delete {
  background: var(--theme-bg-surface);
  color: var(--theme-danger);
  border: 1px solid color-mix(in srgb, var(--theme-danger) 30%, transparent);
}

.btn-delete:hover {
  background: var(--theme-danger);
  color: var(--theme-primary-text);
  border-color: var(--theme-danger);
}

.btn-link {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  padding: 7px 14px;
}

.btn-link:hover {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

/* 分页 */
.orders-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid var(--theme-hairline);
}

.page-btn {
  padding: 9px 20px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
  font-family: inherit;
}

.page-btn:hover:not(:disabled) {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
}

.page-btn:disabled {
  color: var(--theme-text-3);
  cursor: not-allowed;
  border-color: var(--theme-hairline);
  background: var(--theme-bg-surface-2);
}

.page-info {
  font-size: 14px;
  color: var(--theme-text-2);
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 767px) {
  .orders-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }
  .orders-header h2 { font-size: 22px; }
  .order-filters { width: 100%; }
  .filter-btn { flex: 1; text-align: center; padding: 7px 12px; font-size: 12px; }

  .orders-list { grid-template-columns: 1fr; }

  .order-info { grid-template-columns: 1fr 1fr; gap: 8px 12px; }

  .order-footer { flex-direction: column; align-items: flex-start; gap: 12px; }
  .order-actions { width: 100%; flex-wrap: wrap; }

  .orders-pagination { gap: 14px; }
  .page-btn { padding: 8px 16px; }
}

@media (max-width: 480px) {
  .orders-header h2 { font-size: 20px; }
  .order-item { padding: 16px; border-radius: 14px; }
  .order-id { font-size: 12px; }
  .product-item { padding: 10px; }
  .product-image { width: 48px; height: 48px; }
  .product-name { font-size: 13px; }
  .total-price { font-size: 16px; }
  .btn { padding: 6px 12px; font-size: 12px; }
  .empty-icon { width: 90px; height: 90px; }
}
</style>