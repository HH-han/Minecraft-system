<template>
    <div class="ticket-content">
        <!-- 页头 -->
        <header class="page-header">
            <h1 class="page-title">机票·火车票预订</h1>
            <p class="page-subtitle">精选线路，说走就走</p>
        </header>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading">
            <div class="loading-spinner"></div>
            <p>正在查询车次航班...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
            <div class="error-icon">⚠️</div>
            <p>{{ error }}</p>
            <button @click="fetchTickets()" class="btn retry">重新查询</button>
        </div>

        <!-- 票列表 -->
        <template v-else>
            <div class="ticket-list" v-if="tickets.length > 0">
                <div v-for="ticket in tickets" :key="ticket.id" class="ticket-card"
                    :class="'type-' + getTypeKey(ticket.type)" @click="openDetail(ticket)">

                    <!-- 卡片头部：类型徽章 + 状态 -->
                    <div class="ticket-header">
                        <span class="ticket-type">
                            <span class="type-icon" v-html="getTypeIcon(ticket.type)"></span>
                            {{ getTypeText(ticket.type) }}
                        </span>
                        <span class="ticket-status" :class="getStatusClass(ticket.status)">
                            <span class="status-dot"></span>
                            {{ getStatusText(ticket.status) }}
                        </span>
                    </div>

                    <!-- 路线：城市 + 时长 -->
                    <div class="ticket-route">
                        <div class="route-point">
                            <span class="city">{{ ticket.departureCity }}</span>
                            <span class="time">{{ ticket.departureTime }}</span>
                            <span class="point-label">出发</span>
                        </div>
                        <div class="route-mid">
                            <span class="duration">{{ getDuration(ticket) }}</span>
                            <svg viewBox="0 0 120 10" class="arrow-icon" preserveAspectRatio="none">
                                <path d="M2 5h104M106 5l-8-4M106 5l-8 4" stroke="currentColor" stroke-width="1.6"
                                    fill="none" stroke-linecap="round" stroke-linejoin="round" />
                            </svg>
                            <span class="duration-label">全程</span>
                        </div>
                        <div class="route-point route-point--end">
                            <span class="city">{{ ticket.arrivalCity }}</span>
                            <span class="time">{{ ticket.arrivalTime }}</span>
                            <span class="point-label">到达</span>
                        </div>
                    </div>

                    <!-- 信息条 -->
                    <div class="ticket-info">
                        <div class="info-item">
                            <span class="label">{{ getTypeCarrierLabel(ticket.type) }}</span>
                            <span class="value">{{ ticket.carrier }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">{{ getTypeSeatLabel(ticket.type) }}</span>
                            <span class="value">{{ ticket.seatClass }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">余票</span>
                            <span class="value" :class="{ 'stock-low': ticket.stock < 20 }">
                                {{ ticket.stock < 20 ? '仅剩' + ticket.stock + '张' : ticket.stock + '张' }}
                            </span>
                        </div>
                    </div>

                    <!-- 底部：价格 + 预订 -->
                    <div class="ticket-footer">
                        <div class="price-wrap">
                            <span class="price-symbol">¥</span>
                            <span class="price">{{ ticket.price }}</span>
                            <span class="price-unit">/张起</span>
                        </div>
                        <button class="btn-book" :disabled="ticket.status === 2" @click.stop="openDetail(ticket)">
                            {{ ticket.status === 2 ? '已售罄' : '立即预订' }}
                        </button>
                    </div>
                </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="empty-state">
                <div class="empty-icon">🚄</div>
                <h3>暂无可预订车票</h3>
                <p>稍后再来看看，更多线路正在上新中</p>
            </div>

            <!-- 分页 -->
            <Paging v-if="totalPages > 1" :total-pages="totalPages" :current-page="currentPage"
                @update:current-page="handlePageChange" />
        </template>

        <!-- 详情弹窗 -->
        <transition name="modal">
            <div v-if="showModal" class="modal-overlay" @click="closeModal">
                <div class="modal-content" @click.stop>
                    <button class="close-btn" @click="closeModal" aria-label="关闭">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                            stroke-linecap="round">
                            <path d="M18 6L6 18M6 6l12 12" />
                        </svg>
                    </button>
                    <div class="modal-body">
                        <div class="modal-header">
                            <span class="ticket-type">
                                <span class="type-icon" v-html="getTypeIcon(selectedTicket?.type)"></span>
                                {{ getTypeText(selectedTicket?.type) }}
                            </span>
                            <span class="status-badge" :class="getStatusClass(selectedTicket?.status)">
                                {{ getStatusText(selectedTicket?.status) }}
                            </span>
                        </div>

                        <!-- 路线主视觉 -->
                        <div class="modal-route">
                            <div class="route-main">
                                <span class="city-name">{{ selectedTicket?.departureCity }}</span>
                                <span class="time-info">{{ selectedTicket?.departureTime }} 出发</span>
                            </div>
                            <div class="route-line">
                                <span class="duration-pill">{{ getDuration(selectedTicket) }}</span>
                                <svg viewBox="0 0 200 20" class="flight-icon" preserveAspectRatio="none">
                                    <path d="M4 10h172M176 10l-14-8M176 10l-14 8" stroke="currentColor"
                                        stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" />
                                </svg>
                            </div>
                            <div class="route-main route-main--end">
                                <span class="city-name">{{ selectedTicket?.arrivalCity }}</span>
                                <span class="time-info">{{ selectedTicket?.arrivalTime }} 到达</span>
                            </div>
                        </div>

                        <div class="modal-details">
                            <div class="detail-row">
                                <span class="detail-label">{{ getTypeCarrierLabel(selectedTicket?.type) }}</span>
                                <span class="detail-value">{{ selectedTicket?.carrier }}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">{{ getTypeSeatLabel(selectedTicket?.type) }}</span>
                                <span class="detail-value">{{ selectedTicket?.seatClass }}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">剩余票数</span>
                                <span class="detail-value">{{ selectedTicket?.stock }} 张</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">票价</span>
                                <span class="detail-value price-highlight">¥{{ selectedTicket?.price }}</span>
                            </div>
                        </div>

                        <div class="modal-actions">
                            <button class="btn secondary" @click="closeModal">关闭</button>
                            <button class="btn primary" @click="handleBook">立即预订</button>
                        </div>
                    </div>
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTicketList } from '@/api/ticket.js'
import Paging from '@/components/paging/index.vue'

const tickets = ref([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const selectedTicket = ref(null)

// 分页数据
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = ref(10)

const fetchTickets = async (page = 1) => {
    loading.value = true
    error.value = ''
    try {
        const response = await getTicketList({ pageNum: page, pageSize: pageSize.value })
        tickets.value = response.data?.records || []
        totalPages.value = response.data?.pages || 0
    } catch (err) {
        error.value = err.message || '获取数据失败'
        console.error('获取机票数据失败:', err)
    } finally {
        loading.value = false
    }
}

// 分页切换
const handlePageChange = (page) => {
    currentPage.value = page
    fetchTickets(page)
}

/* ============ 类型映射（train/flight/bus/ship → 中文 + 图标） ============ */
const TYPE_MAP = {
    train: { text: '火车票', carrier: '承运方', seat: '坐席' },
    flight: { text: '机票', carrier: '航空公司', seat: '舱位' },
    air: { text: '机票', carrier: '航空公司', seat: '舱位' },
    bus: { text: '汽车票', carrier: '运营公司', seat: '座位类型' },
    ship: { text: '船票', carrier: '航运公司', seat: '舱位' },
    boat: { text: '船票', carrier: '航运公司', seat: '舱位' }
}
const DEFAULT_TYPE = { text: '车票', carrier: '承运方', seat: '座位类型' }

const getTypeKey = (type) => (type && TYPE_MAP[type.toLowerCase()] ? type.toLowerCase() : 'default')
const getTypeText = (type) => (TYPE_MAP[getTypeKey(type)] || DEFAULT_TYPE).text
const getTypeCarrierLabel = (type) => (TYPE_MAP[getTypeKey(type)] || DEFAULT_TYPE).carrier
const getTypeSeatLabel = (type) => (TYPE_MAP[getTypeKey(type)] || DEFAULT_TYPE).seat

const TYPE_ICONS = {
    train: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="5" y="3" width="14" height="14" rx="3"/><path d="M5 11h14M9 21l-1.5-4M15 21l1.5-4"/><circle cx="9" cy="14" r="0.5"/><circle cx="15" cy="14" r="0.5"/></svg>',
    flight: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M17.8 19.2 16 11l3.5-3.5C21 6 21.5 4 21 3c-1-.5-3 0-4.5 1.5L13 8 4.8 6.2c-.5-.1-.9.1-1.1.5l-.3.5c-.2.5-.1 1 .3 1.3L9 12l-2 3H4l-1 1 3 2 2 3 1-1v-3l3-2 3.5 5.3c.3.4.8.5 1.3.3l.5-.2c.4-.3.6-.7.5-1.2z"/></svg>',
    bus: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="3" width="16" height="14" rx="2"/><path d="M4 11h16M8 21v-4M16 21v-4"/><circle cx="8" cy="14" r="0.5"/><circle cx="16" cy="14" r="0.5"/></svg>',
    ship: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3 17c1.5 0 2.5 1.5 4.5 1.5S10.5 17 12 17s2.5 1.5 4.5 1.5S19.5 17 21 17M12 3v9M12 12l-5-3M12 12l5-3M5 12c0 2 3 4 7 4s7-2 7-4"/></svg>',
    default: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M2 8.5C2 7 4.5 5.5 12 5.5S22 7 22 8.5 19.5 11.5 12 11.5 2 10 2 8.5zM3 8.5V13c0 1.5 4 4 9 4s9-2.5 9-4V8.5"/></svg>'
}
const getTypeIcon = (type) => TYPE_ICONS[getTypeKey(type)] || TYPE_ICONS.default

/* ============ 行程时长计算（支持跨天） ============ */
const getDuration = (ticket) => {
    if (!ticket?.departureTime || !ticket?.arrivalTime) return '--'
    const [sh, sm] = ticket.departureTime.split(':').map(Number)
    const [eh, em] = ticket.arrivalTime.split(':').map(Number)
    let diff = (eh * 60 + em) - (sh * 60 + sm)
    if (diff < 0) diff += 24 * 60 // 跨天
    const h = Math.floor(diff / 60)
    const m = diff % 60
    if (h === 0) return `${m}分钟`
    return m === 0 ? `${h}小时` : `${h}小时${m}分`
}

/* ============ 状态 ============ */
const getStatusClass = (status) => {
    if (status === 1) return 'status-available'
    if (status === 0) return 'status-pending'
    return 'status-soldout'
}

const getStatusText = (status) => {
    if (status === 1) return '可预订'
    if (status === 0) return '待出发'
    return '已售罄'
}

const openDetail = (ticket) => {
    selectedTicket.value = ticket
    showModal.value = true
}

const closeModal = () => {
    showModal.value = false
    selectedTicket.value = null
}

const handleBook = () => {
    console.log('预订机票:', selectedTicket.value)
    closeModal()
}

onMounted(() => {
    fetchTickets()
})
</script>

<style scoped>
/* ============ 容器与页头 ============ */
.ticket-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 22px 56px;
}

.page-header {
    text-align: center;
    margin-bottom: 36px;
}

.page-title {
    margin: 0 0 8px;
    font-size: 32px;
    font-weight: 700;
    letter-spacing: -0.01em;
    background: linear-gradient(90deg, var(--theme-text-1), var(--theme-primary));
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
}

.page-subtitle {
    margin: 0;
    font-size: 15px;
    color: var(--theme-text-2);
}

/* ============ 加载 / 错误 ============ */
.loading,
.error-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 72px 20px;
    color: var(--theme-text-2);
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 3px solid var(--theme-border);
    border-top-color: var(--theme-primary);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    margin-bottom: 16px;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.error-icon {
    font-size: 44px;
    margin-bottom: 12px;
}

.error-state p {
    margin: 0 0 20px;
}

.btn.retry {
    padding: 12px 28px;
    background: var(--theme-primary);
    color: var(--theme-primary-text);
    border: none;
    border-radius: 40px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.2s, transform 0.2s;
}

.btn.retry:hover {
    background: var(--theme-primary-hover);
    transform: translateY(-2px);
}

/* ============ 票列表 ============ */
.ticket-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 24px;
}

.ticket-card {
    position: relative;
    display: flex;
    flex-direction: column;
    background: var(--theme-bg-surface);
    border-radius: 20px;
    padding: 24px;
    box-shadow: var(--theme-shadow-sm);
    transition: transform 0.25s ease, box-shadow 0.25s ease;
    cursor: pointer;
    overflow: hidden;
}

/* 顶部类型色条 */
.ticket-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: var(--theme-primary);
}

.ticket-card.type-train::before {
    background: var(--theme-primary);
}

.ticket-card.type-flight::before,
.ticket-card.type-air::before {
    background: var(--theme-accent-2);
}

.ticket-card.type-bus::before {
    background: var(--theme-success);
}

.ticket-card.type-ship::before,
.ticket-card.type-boat::before {
    background: var(--theme-warning);
}

.ticket-card:hover {
    transform: translateY(-4px);
    box-shadow: var(--theme-shadow-md);
}

/* ---- 头部 ---- */
.ticket-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 18px;
}

.ticket-type {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 15px;
    font-weight: 600;
    color: var(--theme-text-1);
}

.type-icon {
    display: inline-flex;
    width: 20px;
    height: 20px;
    color: var(--theme-primary);
}

.type-icon :deep(svg) {
    width: 100%;
    height: 100%;
}

.ticket-card.type-flight .type-icon,
.ticket-card.type-air .type-icon {
    color: var(--theme-accent-2);
}

.ticket-card.type-bus .type-icon {
    color: var(--theme-success);
}

.ticket-card.type-ship .type-icon,
.ticket-card.type-boat .type-icon {
    color: var(--theme-warning);
}

.ticket-status {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    font-weight: 600;
    padding: 4px 12px;
    border-radius: 40px;
}

.status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: currentColor;
}

.status-available {
    background: var(--theme-success-soft);
    color: var(--theme-success);
}

.status-pending {
    background: var(--theme-warning-soft);
    color: var(--theme-warning);
}

.status-soldout {
    background: var(--theme-danger-soft);
    color: var(--theme-danger);
}

/* ---- 路线 ---- */
.ticket-route {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 12px;
    padding: 4px 0 18px;
}

.route-point {
    display: flex;
    flex-direction: column;
    gap: 3px;
}

.route-point--end {
    align-items: flex-end;
    text-align: right;
}

.route-point .city {
    font-size: 24px;
    font-weight: 700;
    color: var(--theme-text-1);
    letter-spacing: -0.01em;
    line-height: 1.2;
}

.route-point .time {
    font-size: 15px;
    font-weight: 600;
    color: var(--theme-primary);
    font-variant-numeric: tabular-nums;
}

.point-label {
    font-size: 11px;
    color: var(--theme-text-3);
}

.route-mid {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    padding-top: 6px;
    min-width: 72px;
}

.duration {
    font-size: 12px;
    font-weight: 600;
    color: var(--theme-text-2);
    background: var(--theme-bg-surface-2);
    padding: 2px 10px;
    border-radius: 40px;
    white-space: nowrap;
}

.arrow-icon {
    width: 100%;
    max-width: 110px;
    height: 10px;
    color: var(--theme-text-3);
}

.duration-label {
    font-size: 11px;
    color: var(--theme-text-3);
}

/* ---- 信息条 ---- */
.ticket-info {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
    padding: 14px 16px;
    background: var(--theme-bg-surface-2);
    border-radius: 14px;
    margin-bottom: 18px;
}

.info-item {
    display: flex;
    flex-direction: column;
    gap: 3px;
    min-width: 0;
}

.info-item .label {
    font-size: 11px;
    color: var(--theme-text-2);
    white-space: nowrap;
}

.info-item .value {
    font-size: 14px;
    font-weight: 600;
    color: var(--theme-text-1);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.value.stock-low {
    color: var(--theme-warning);
}

/* ---- 底部 ---- */
.ticket-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;
}

.price-wrap {
    display: flex;
    align-items: baseline;
    gap: 2px;
}

.price-symbol {
    font-size: 15px;
    font-weight: 600;
    color: var(--theme-danger);
}

.price {
    font-size: 30px;
    font-weight: 700;
    color: var(--theme-danger);
    letter-spacing: -0.02em;
    line-height: 1;
    font-variant-numeric: tabular-nums;
}

.price-unit {
    font-size: 12px;
    color: var(--theme-text-2);
    margin-left: 2px;
}

.btn-book {
    padding: 10px 22px;
    background: var(--theme-gradient);
    color: var(--theme-primary-text);
    border: none;
    border-radius: 40px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s, opacity 0.2s;
    box-shadow: var(--theme-shadow-sm);
}

.btn-book:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: var(--theme-shadow-md);
}

.btn-book:disabled {
    background: var(--theme-bg-surface-2);
    color: var(--theme-text-3);
    box-shadow: none;
    cursor: not-allowed;
}

/* ---- 空状态 ---- */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 72px 20px;
    color: var(--theme-text-2);
}

.empty-icon {
    font-size: 56px;
    margin-bottom: 16px;
    opacity: 0.8;
}

.empty-state h3 {
    margin: 0 0 6px;
    font-size: 18px;
    color: var(--theme-text-1);
}

.empty-state p {
    margin: 0;
    font-size: 14px;
}

/* ============ 弹窗 ============ */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: var(--theme-overlay);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    padding: 20px;
    box-sizing: border-box;
}

.modal-enter-active,
.modal-leave-active {
    transition: opacity 0.25s ease;
}

.modal-enter-from,
.modal-leave-to {
    opacity: 0;
}

.modal-enter-active .modal-content,
.modal-leave-active .modal-content {
    transition: transform 0.25s ease;
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
    transform: translateY(24px) scale(0.98);
}

.modal-content {
    background: var(--theme-bg-elevated);
    border-radius: 24px;
    max-width: 560px;
    width: 100%;
    max-height: 85vh;
    overflow-y: auto;
    position: relative;
    box-shadow: var(--theme-shadow-lg);
}

.close-btn {
    position: absolute;
    top: 16px;
    right: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    background: var(--theme-bg-surface-2);
    border: none;
    border-radius: 50%;
    cursor: pointer;
    color: var(--theme-text-2);
    z-index: 10;
    transition: background 0.2s, color 0.2s;
}

.close-btn:hover {
    background: var(--theme-bg-hover);
    color: var(--theme-text-1);
}

.close-btn svg {
    width: 18px;
    height: 18px;
}

.modal-body {
    padding: 32px;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.modal-header .ticket-type {
    font-size: 18px;
}

.status-badge {
    font-size: 13px;
    font-weight: 600;
    padding: 6px 14px;
    border-radius: 40px;
}

/* 路线主视觉 */
.modal-route {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 26px 24px;
    background: var(--theme-primary-soft);
    border-radius: 16px;
    margin-bottom: 24px;
}

.route-main {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.route-main--end {
    align-items: flex-end;
    text-align: right;
}

.city-name {
    font-size: 26px;
    font-weight: 700;
    color: var(--theme-text-1);
    letter-spacing: -0.01em;
}

.time-info {
    font-size: 13px;
    font-weight: 500;
    color: var(--theme-text-2);
    font-variant-numeric: tabular-nums;
}

.route-line {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    min-width: 90px;
}

.duration-pill {
    font-size: 12px;
    font-weight: 600;
    color: var(--theme-primary);
    background: var(--theme-primary-soft);
    padding: 3px 12px;
    border-radius: 40px;
    white-space: nowrap;
}

.flight-icon {
    width: 100%;
    max-width: 160px;
    height: 20px;
    color: var(--theme-primary);
}

/* 详情行 */
.modal-details {
    margin-bottom: 28px;
}

.detail-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 13px 0;
    border-bottom: 1px solid var(--theme-hairline);
}

.detail-row:last-child {
    border-bottom: none;
}

.detail-label {
    color: var(--theme-text-2);
    font-size: 14px;
}

.detail-value {
    color: var(--theme-text-1);
    font-size: 15px;
    font-weight: 600;
}

.price-highlight {
    font-size: 24px;
    font-weight: 700;
    color: var(--theme-danger);
    letter-spacing: -0.01em;
}

/* 操作按钮 */
.modal-actions {
    display: flex;
    gap: 12px;
}

.modal-actions .btn {
    flex: 1;
    padding: 14px 24px;
    border: none;
    border-radius: 14px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.modal-actions .btn.secondary {
    background: var(--theme-bg-surface-2);
    color: var(--theme-text-1);
}

.modal-actions .btn.secondary:hover {
    background: var(--theme-bg-hover);
}

.modal-actions .btn.primary {
    background: var(--theme-gradient);
    color: var(--theme-primary-text);
    box-shadow: var(--theme-shadow-sm);
}

.modal-actions .btn.primary:hover {
    transform: translateY(-2px);
    box-shadow: var(--theme-shadow-md);
}

/* ============ 响应式 ============ */
@media (max-width: 768px) {
    .ticket-content {
        padding: 24px 16px 40px;
    }

    .page-title {
        font-size: 26px;
    }

    .ticket-list {
        grid-template-columns: 1fr;
        gap: 16px;
    }

    .ticket-card {
        padding: 20px;
    }

    .ticket-route {
        flex-direction: column;
        gap: 10px;
        align-items: stretch;
    }

    .route-point,
    .route-point--end {
        flex-direction: row;
        align-items: baseline;
        gap: 10px;
    }

    .route-point--end {
        justify-content: flex-end;
    }

    .route-mid {
        flex-direction: row;
        justify-content: center;
        padding: 4px 0;
    }

    .arrow-icon {
        max-width: 90px;
    }

    .ticket-info {
        padding: 12px;
        gap: 8px;
    }

    .modal-body {
        padding: 24px;
    }

    .modal-route {
        flex-direction: column;
        gap: 14px;
        padding: 20px;
    }

    .route-main--end {
        align-items: center;
        text-align: center;
    }

    .route-line {
        flex-direction: row;
        min-width: 0;
    }
}
</style>
