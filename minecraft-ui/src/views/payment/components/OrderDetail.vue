<template>
  <div class="order-detail" v-if="hasOrderData">
    <div class="order-header">
      <h2 class="order-title">订单详情</h2>
      <div class="order-status" :class="getStatusClass(order.status)">
        {{ getStatusText(order.status) }}
      </div>
    </div>

    <div class="order-info-card">
      <div class="info-item">
        <span class="label">订单编号</span>
        <span class="value">
          {{ order.orderNo || '-' }}
          <button v-if="order.orderNo" type="button" class="copy-btn" @click="copyText(order.orderNo)">
            复制
          </button>
        </span>
      </div>
      <div class="info-item">
        <span class="label">订单类型</span>
        <span class="value">{{ getItemTypeText(order.itemType) }}</span>
      </div>
      <div class="info-item">
        <span class="label">下单时间</span>
        <span class="value">{{ order.createTime || '-' }}</span>
      </div>
      <div class="info-item">
        <span class="label">支付时间</span>
        <span class="value">{{ order.payTime || '未支付' }}</span>
      </div>
      <div class="info-item">
        <span class="label">订单金额</span>
        <span class="value price">{{ priceText(order.amount) }}</span>
      </div>
    </div>

    <!-- 支付信息（聚合支付网关） -->
    <div class="payment-info-card" v-if="paymentInfo">
      <h3 class="section-title">{{ $t('payment.orderDetail.paymentInfo') }}</h3>
      <div class="info-item">
        <span class="label">{{ $t('payment.orderDetail.payNo') }}</span>
        <span class="value">
          {{ paymentInfo.payNo || '-' }}
          <button
            v-if="paymentInfo.payNo"
            type="button"
            class="copy-btn"
            @click="copyText(paymentInfo.payNo)"
          >
            {{ $t('payment.orderDetail.copy') }}
          </button>
        </span>
      </div>
      <div class="info-item">
        <span class="label">{{ $t('payment.orderDetail.purpose') }}</span>
        <span class="value">{{ paymentPurpose }}</span>
      </div>
      <div class="info-item">
        <span class="label">{{ $t('payment.orderDetail.channel') }}</span>
        <span class="value">{{ getChannelText(paymentInfo.channelCode) }}</span>
      </div>
      <div class="info-item">
        <span class="label">{{ $t('payment.orderDetail.payStatus') }}</span>
        <span class="value">{{ getPayStatusText(paymentInfo) }}</span>
      </div>
      <div class="info-item">
        <span class="label">{{ $t('payment.orderDetail.totalAmount') }}</span>
        <span class="value">{{ priceText(paymentInfo.totalAmount) }}</span>
      </div>
      <div class="info-item" v-if="Number(paymentInfo.pointsDeduct) > 0">
        <span class="label">{{ $t('payment.orderDetail.pointsDeduct') }}</span>
        <span class="value">-¥{{ formatMoney(paymentInfo.pointsDeduct) }}</span>
      </div>
      <div class="info-item">
        <span class="label">{{ $t('payment.orderDetail.payAmount') }}</span>
        <span class="value price">{{ priceText(paymentInfo.payAmount) }}</span>
      </div>
      <div class="info-item" v-if="Number(paymentInfo.refundedAmount) > 0">
        <span class="label">{{ $t('payment.orderDetail.refundedAmount') }}</span>
        <span class="value">¥{{ formatMoney(paymentInfo.refundedAmount) }}</span>
      </div>
      <div class="info-item" v-if="paymentInfo.payTime">
        <span class="label">{{ $t('payment.orderDetail.payTime') }}</span>
        <span class="value">{{ formatTime(paymentInfo.payTime) }}</span>
      </div>
      <div class="info-item" v-if="paymentInfo.channelTradeNo">
        <span class="label">{{ $t('payment.orderDetail.tradeNo') }}</span>
        <span class="value">{{ paymentInfo.channelTradeNo }}</span>
      </div>
    </div>
    
    <div class="shipping-address-card">
      <h3 class="section-title">收货地址</h3>
      <div class="address-content">
        <div class="name-phone">
          <span class="name">{{ order.address.name }}</span>
          <span class="phone">{{ order.address.phone }}</span>
        </div>
        <div class="address-detail">
          {{ order.address.detail }}
        </div>
      </div>
    </div>
    
    <div class="order-items-card">
      <h3 class="section-title">商品信息</h3>
      <div class="items-list">
        <div v-for="item in order.items" :key="item.id" class="item">
          <div class="item-image">
            <img :src="item.image" :alt="item.name">
          </div>
          <div class="item-info">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-price">
              <span class="price">{{ priceText(item.price) }}</span>
              <span class="quantity">x{{ item.quantity }}</span>
            </div>
          </div>
        </div>
      </div>
      <div v-if="order.remark" class="order-remark">
        <span class="order-remark__label">备注</span>
        {{ order.remark }}
      </div>
    </div>
    
    <div class="order-summary-card">
      <div class="summary-item">
        <span class="summary-label">商品数量</span>
        <span class="summary-value">{{ order.quantity }} 件</span>
      </div>
      <template v-if="!isPointsOrder">
        <div class="summary-item">
          <span class="summary-label">商品总价</span>
          <span class="summary-value">¥{{ formatMoney(order.amount) }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">运费</span>
          <span class="summary-value">¥{{ formatMoney(order.freight) }}</span>
        </div>
      </template>
      <div class="summary-item total">
        <span class="summary-label">实付金额</span>
        <span class="summary-value price">{{ priceText(order.amount) }}</span>
      </div>
    </div>

    <!-- 物流信息 -->
    <div class="shipping-info-card" v-if="hasShippingInfo">
      <h3 class="section-title">物流信息</h3>
      <div class="info-item">
        <span class="label">物流状态</span>
        <span class="value">{{ getShippingStatusText(order.shippingStatus) }}</span>
      </div>
      <div class="info-item" v-if="order.carrierName">
        <span class="label">承运商</span>
        <span class="value">{{ order.carrierName }}</span>
      </div>
      <div class="info-item" v-if="order.trackingNo">
        <span class="label">运单号</span>
        <span class="value">
          {{ order.trackingNo }}
          <button type="button" class="copy-btn" @click="copyText(order.trackingNo)">复制</button>
        </span>
      </div>
      <div class="info-item" v-if="order.shipTime">
        <span class="label">发货时间</span>
        <span class="value">{{ order.shipTime }}</span>
      </div>
      <div class="info-item" v-if="order.receiveTime">
        <span class="label">签收时间</span>
        <span class="value">{{ order.receiveTime }}</span>
      </div>
      <div class="info-item" v-if="order.afterSaleStatus > 0">
        <span class="label">售后状态</span>
        <span class="value">{{ getAfterSaleText(order.afterSaleStatus) }}</span>
      </div>
    </div>
    
    <div class="order-actions" v-if="order.status === '0'">
      <button class="btn btn-secondary" @click="handleCancelOrder">取消订单</button>
      <button class="btn btn-primary" @click="goToPay">立即支付</button>
    </div>
    <div class="order-actions" v-else-if="canViewLogistics">
      <button class="btn btn-primary" @click="viewLogistics">查看物流</button>
    </div>
  </div>
  
  <!-- 无订单数据页面 -->
  <div class="no-order" v-else>
    <div class="no-order-content">
      <div class="no-order-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="15" y1="9" x2="9" y2="15"></line>
          <line x1="9" y1="9" x2="15" y2="15"></line>
        </svg>
      </div>
      <h3 class="no-order-title">暂无订单数据</h3>
      <p class="no-order-desc">您还没有相关订单信息，请先去浏览商品</p>
      <button class="btn btn-primary" @click="goToHome">返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getFoodDetail } from '@/api/food.js'
import { getProductDetail } from '@/api/product.js'
import { getOrderDetail, cancelOrder } from '@/api/order.js'
import { queryPayment } from '@/api/payment.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const emit = defineEmits(['pay'])
const router = useRouter()
const route = useRoute()
const { t, te } = useI18n()

// 国际化键存在时取翻译，否则回退到后端返回值
const tOr = (key, fallback) => (te(key) ? t(key) : fallback)

// 承运商代码 → 名称
const CARRIER_MAP = {
  YTO: '圆通速递',
  ZTO: '中通快递',
  STO: '申通快递',
  YD: '韵达速递',
  SF: '顺丰速运',
  JD: '京东物流',
  EMS: '中国邮政',
  YZPY: '邮政快递包裹',
  DBL: '德邦快递'
}

// 订单默认结构
const createDefaultOrder = () => ({
  orderNo: '',
  status: '',
  itemType: '',
  createTime: '',
  payTime: '',
  amount: 0,
  quantity: 1,
  freight: 0,
  remark: '',
  address: { name: '未填写', phone: '', detail: '暂无收货地址' },
  items: [],
  carrierCode: '',
  carrierName: '',
  trackingNo: '',
  shippingStatus: null,
  shipTime: '',
  receiveTime: '',
  afterSaleStatus: 0
})

const order = ref(createDefaultOrder())

// 订单数据是否存在
const hasOrderData = ref(false)

// 聚合支付单信息（来自后端 /api/pay/query）
const paymentInfo = ref(null)

// 支付用途：业务类型 + 支付标题（购买了什么、钱花在哪）
const paymentPurpose = computed(() => {
  if (!paymentInfo.value) return ''
  const bizType = tOr(
    `payment.orderDetail.bizTypes.${paymentInfo.value.bizType}`,
    paymentInfo.value.bizType || ''
  )
  const subject = paymentInfo.value.subject || order.value.items[0]?.name || ''
  return subject ? `${bizType} · ${subject}` : bizType || '-'
})

// 积分兑换订单（无实际支付金额）
const isPointsOrder = computed(() => order.value.itemType === 'points')

// 是否展示物流信息：非待支付且已进入物流环节
const hasShippingInfo = computed(
  () =>
    ['1', '2', '3'].includes(order.value.status) &&
    ((order.value.shippingStatus !== null && order.value.shippingStatus !== undefined) ||
      !!order.value.trackingNo)
)

// 是否可查看物流
const canViewLogistics = computed(() => ['1', '2', '3'].includes(order.value.status))

// 时间格式化：2026-10-11T16:16:02 → 2026-10-11 16:16:02
const formatTime = (time) => (time ? String(time).replace('T', ' ') : '')

// 金额格式化：整数不带小数，非整数保留两位
const formatMoney = (value) => {
  const num = Number(value || 0)
  return Number.isInteger(num) ? String(num) : num.toFixed(2)
}

// 金额展示：积分订单不显示金额
const priceText = (value) => (isPointsOrder.value ? '积分兑换' : `¥${formatMoney(value)}`)

const getItemTypeText = (type) => {
  const map = {
    food: '美食',
    product: '纪念品',
    points: '积分兑换',
    hotel: '酒店',
    attraction: '景点'
  }
  return map[type] || '商品'
}

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

// 物流状态 0-待发货 1-部分发货 2-已发货 3-已签收
const getShippingStatusText = (status) => {
  const map = { 0: '待发货', 1: '部分发货', 2: '已发货', 3: '已签收' }
  return map[status] ?? '待发货'
}

// 售后状态 0-无 1-售后中 2-售后完成
const getAfterSaleText = (status) => {
  const map = { 1: '售后中', 2: '售后完成' }
  return map[status] || '无'
}

// 复制文本
const copyText = async (text) => {
  try {
    await navigator.clipboard.writeText(String(text))
    ElMessage.success('已复制')
  } catch {
    ElMessage.warning('复制失败，请手动复制')
  }
}

// 将后端订单数据映射为页面数据
const normalizeOrder = (data) => {
  const status = data.status != null ? String(data.status) : ''
  const amount = Number(data.amount) || 0
  return {
    orderNo: data.orderNo || (data.id != null ? String(data.id) : ''),
    status,
    itemType: data.itemType || '',
    createTime: formatTime(data.createTime),
    // 后端无独立支付时间字段，非待支付订单以更新时间近似
    payTime: formatTime(data.payTime || (status !== '0' ? data.updateTime : '')),
    amount,
    quantity: data.quantity || 1,
    freight: Number(data.freight) || 0,
    remark: data.remark || '',
    address: {
      name: data.receiverName || '未填写',
      phone: data.receiverPhone || '',
      detail: data.receiverAddress || '暂无收货地址'
    },
    items: data.itemName
      ? [
          {
            id: data.itemId,
            name: data.itemName,
            price: amount,
            quantity: data.quantity || 1,
            image: data.image
          }
        ]
      : [],
    carrierCode: data.carrierCode || '',
    carrierName: CARRIER_MAP[data.carrierCode] || data.carrierName || data.carrierCode || '',
    trackingNo: data.trackingNo || '',
    shippingStatus: data.shippingStatus ?? null,
    shipTime: formatTime(data.shipTime),
    receiveTime: formatTime(data.receiveTime),
    afterSaleStatus: data.afterSaleStatus || 0
  }
}

// 按商品类型获取商品详情，转换为订单商品项
const fetchCommodityItem = async (id, commodity) => {
  if (commodity === '0') {
    const { data } = await getFoodDetail(id)
    return data
      ? { id: data.id, name: data.name, price: Number(data.price) || 0, quantity: 1, image: data.coverImage }
      : null
  }
  if (commodity === '1') {
    const { data } = await getProductDetail(id)
    return data
      ? { id: data.id, name: data.name, price: Number(data.price) || 0, quantity: 1, image: data.coverImage }
      : null
  }
  return null
}

// 获取订单数据
const fetchProductData = async () => {
  const orderId = route.query.orderId
  const id = route.query.id
  const commodity = route.query.commodity

  // 初始设置为false
  hasOrderData.value = false

  if (orderId) {
    // 如果有订单编号，从后端获取订单数据
    try {
      const response = await getOrderDetail(orderId)
      const orderData = response.data

      if (orderData) {
        order.value = normalizeOrder(orderData)

        // 订单缺少商品信息时，按商品类型获取商品详情
        if (!orderData.itemName && id && commodity) {
          const item = await fetchCommodityItem(id, commodity)
          if (item) {
            order.value.items = [item]
            order.value.amount = item.price
          }
        }

        // 设置为有订单数据
        hasOrderData.value = true
      }
    } catch (err) {
      console.error('获取订单数据失败:', err)
      // 如果获取订单失败，尝试用商品数据兜底
      await setDefaultOrderData()
    }
  } else {
    // 如果没有订单编号，使用商品数据
    await setDefaultOrderData()
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchProductData)

// 无订单编号时，根据商品详情构建订单数据
const setDefaultOrderData = async () => {
  const id = route.query.id
  const commodity = route.query.commodity
  if (!id || !commodity) return

  try {
    const item = await fetchCommodityItem(id, commodity)
    if (item) {
      order.value = {
        ...createDefaultOrder(),
        itemType: commodity === '0' ? 'food' : commodity === '1' ? 'product' : '',
        amount: item.price,
        quantity: item.quantity,
        items: [item]
      }
      hasOrderData.value = true
    }
  } catch (err) {
    console.error('获取商品数据失败:', err)
  }
}

const goToPay = () => {
  // 触发支付事件
  emit('pay')
}

const goToHome = () => {
  // 跳转到首页
  router.push('/')
}

// 查看物流
const viewLogistics = () => {
  const orderId = route.query.orderId
  if (orderId) {
    router.push({ path: '/logistics', query: { orderId } })
  }
}

const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const orderId = route.query.orderId
    if (orderId) {
      await cancelOrder(orderId)
      order.value.status = '4' // 4-已取消
      ElMessage.success('订单已取消')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败，请重试')
    }
  }
}

// 组件挂载时获取商品数据
onMounted(() => {
  fetchProductData()
})
</script>

<style scoped>
.order-detail {
  font-family: 'PingFang SC', 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  color: var(--theme-text-1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--theme-hairline);
  gap: 16px;
  flex-wrap: wrap;
}

.order-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--theme-text-1);
  margin: 0;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.order-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.order-status::before {
  content: '';
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: currentColor;
}

.order-status.unpaid {
  background: var(--theme-warning-soft);
  color: var(--theme-warning);
}

.order-status.paid {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.order-status.shipping {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.order-status.completed {
  background: var(--theme-success-soft);
  color: var(--theme-success);
}

.order-status.cancelled {
  background: var(--theme-bg-muted);
  color: var(--theme-text-3);
}

.order-status.refunded {
  background: color-mix(in srgb, var(--theme-accent-2) 10%, transparent);
  color: var(--theme-accent-2);
}

/* 卡片通用样式 */
.order-info-card,
.shipping-address-card,
.order-items-card,
.order-summary-card,
.shipping-info-card {
  background: var(--theme-bg-surface-2);
  border-radius: 16px;
  padding: 20px 22px;
  margin-bottom: 16px;
  border: 1px solid var(--theme-hairline);
}

/* 复制按钮 */
.copy-btn {
  margin-left: 8px;
  padding: 2px 10px;
  border-radius: 999px;
  border: 1px solid color-mix(in srgb, var(--theme-primary) 35%, transparent);
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  vertical-align: middle;
  transition: all 0.2s ease;
}

.copy-btn:hover {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin: 0 0 14px 0;
  letter-spacing: -0.01em;
}

/* 订单信息 */
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  gap: 12px;
}

.info-item:not(:last-child) {
  border-bottom: 1px solid var(--theme-hairline);
}

.info-item .label {
  font-size: 14px;
  font-weight: 400;
  color: var(--theme-text-2);
  flex-shrink: 0;
}

.info-item .value {
  font-size: 14px;
  font-weight: 500;
  color: var(--theme-text-1);
  text-align: right;
  word-break: break-all;
}

.info-item .price {
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1);
}

/* 收货地址 */
.address-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.name-phone {
  display: flex;
  align-items: center;
  gap: 14px;
}

.name {
  font-size: 15px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.phone {
  font-size: 14px;
  font-weight: 400;
  color: var(--theme-text-2);
}

.address-detail {
  font-size: 14px;
  font-weight: 400;
  color: var(--theme-text-2);
  line-height: 1.6;
}

/* 商品信息 */
.items-list {
  background: var(--theme-bg-surface);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--theme-hairline);
}

.item {
  display: flex;
  padding: 16px;
  gap: 14px;
  transition: background 0.2s ease;
}

.item:hover {
  background: var(--theme-bg-surface-2);
}

.item:not(:last-child) {
  border-bottom: 1px solid var(--theme-hairline);
}

.item-image {
  width: 72px;
  height: 72px;
  flex-shrink: 0;
  border-radius: 10px;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 6px;
  min-width: 0;
}

.item-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--theme-text-1);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.order-remark {
  margin-top: 12px;
  padding: 10px 14px;
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  font-size: 13px;
  color: var(--theme-text-2);
  line-height: 1.5;
}

.order-remark__label {
  display: inline-block;
  margin-right: 8px;
  padding: 1px 8px;
  border-radius: 6px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-3);
  font-weight: 600;
}

.item-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price .price {
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.item-price .quantity {
  font-size: 13px;
  color: var(--theme-text-2);
  background: var(--theme-bg-surface-2);
  padding: 3px 10px;
  border-radius: 8px;
  font-weight: 500;
}

/* 订单摘要 */
.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.summary-label {
  font-size: 14px;
  font-weight: 400;
  color: var(--theme-text-2);
}

.summary-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--theme-text-1);
}

.summary-item.total {
  border-top: 1px solid var(--theme-hairline);
  padding-top: 14px;
  margin-top: 6px;
}

.summary-item.total .summary-label {
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.summary-item.total .summary-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--theme-text-1);
  letter-spacing: -0.02em;
}

/* 操作按钮 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

.btn {
  font-family: inherit;
  padding: 11px 26px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
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

/* 无订单数据 */
.no-order {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 460px;
  padding: 40px;
}

.no-order-content {
  max-width: 400px;
  width: 100%;
  text-align: center;
}

.no-order-icon {
  margin-bottom: 24px;
  color: var(--theme-text-3);
}

.no-order-icon svg {
  width: 64px;
  height: 64px;
}

.no-order-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin: 0 0 10px 0;
  letter-spacing: -0.01em;
}

.no-order-desc {
  font-size: 15px;
  font-weight: 400;
  color: var(--theme-text-2);
  margin: 0 0 28px 0;
  line-height: 1.5;
}

/* 响应式 */
@media (max-width: 767px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .order-title { font-size: 22px; }
  .section-title { font-size: 15px; }

  .order-info-card,
  .shipping-address-card,
  .order-items-card,
  .order-summary-card,
  .shipping-info-card {
    padding: 18px;
    border-radius: 14px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  .info-item .value { text-align: left; }

  .item { padding: 14px; }
  .item-image { width: 64px; height: 64px; }

  .name-phone {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
  }

  .order-actions {
    flex-direction: column;
    gap: 10px;
  }
  .btn { width: 100%; padding: 13px; }

  .no-order { padding: 32px; min-height: 380px; }
  .no-order-icon svg { width: 56px; height: 56px; }
  .no-order-title { font-size: 20px; }
}

@media (max-width: 480px) {
  .order-title { font-size: 20px; }
  .order-info-card,
  .shipping-address-card,
  .order-items-card,
  .order-summary-card,
  .shipping-info-card { padding: 16px; border-radius: 12px; }
  .info-item .label, .info-item .value { font-size: 13px; }
  .item-name { font-size: 14px; }
  .item-price .price { font-size: 15px; }
  .summary-item.total .summary-value { font-size: 20px; }
  .no-order { min-height: 320px; padding: 24px; }
}
</style>