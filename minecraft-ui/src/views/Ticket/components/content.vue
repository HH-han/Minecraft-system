<template>
    <div class="ticket-content">
        <div v-if="loading" class="loading">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
        </div>
        <div v-else-if="error" class="error-state">
            <p>加载失败: {{ error }}</p>
            <button @click="fetchTickets" class="btn retry">重试</button>
        </div>
        <div v-else class="ticket-list">
            <div v-for="ticket in tickets" :key="ticket.id" class="ticket-card" @click="openDetail(ticket)">
                <div class="ticket-header">
                    <span class="ticket-type">{{ ticket.type }}</span>
                    <span class="ticket-status" :class="getStatusClass(ticket.status)">
                        {{ getStatusText(ticket.status) }}
                    </span>
                </div>
                <div class="ticket-route">
                    <div class="route-point">
                        <span class="city">{{ ticket.departureCity }}</span>
                        <span class="time">{{ ticket.departureTime }}</span>
                    </div>
                    <div class="route-arrow">
                        <svg viewBox="0 0 100 24" class="arrow-icon">
                            <path d="M0 12h80M80 12l-10-6M80 12l-10 6" stroke="currentColor" stroke-width="2" fill="none"/>
                        </svg>
                    </div>
                    <div class="route-point">
                        <span class="city">{{ ticket.arrivalCity }}</span>
                        <span class="time">{{ ticket.arrivalTime }}</span>
                    </div>
                </div>
                <div class="ticket-info">
                    <div class="info-item">
                        <span class="label">承运人</span>
                        <span class="value">{{ ticket.carrier }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">舱位</span>
                        <span class="value">{{ ticket.seatClass }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">余票</span>
                        <span class="value">{{ ticket.stock }}</span>
                    </div>
                </div>
                <div class="ticket-footer">
                    <span class="price">¥{{ ticket.price }}</span>
                    <button class="btn-book">预订</button>
                </div>
            </div>
            <div v-if="tickets.length === 0 && !loading" class="empty-state">
                <p>暂无机票数据</p>
            </div>
        </div>

        <div v-if="showModal" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <button class="close-btn" @click="closeModal">&times;</button>
                <div class="modal-body">
                    <div class="modal-header">
                        <h2>{{ selectedTicket?.type }}</h2>
                        <span class="status-badge" :class="getStatusClass(selectedTicket?.status)">
                            {{ getStatusText(selectedTicket?.status) }}
                        </span>
                    </div>
                    <div class="modal-route">
                        <div class="route-main">
                            <span class="city-name">{{ selectedTicket?.departureCity }}</span>
                            <span class="time-info">{{ selectedTicket?.departureTime }}</span>
                        </div>
                        <div class="route-line">
                            <svg viewBox="0 0 200 24" class="flight-icon">
                                <path d="M0 12h160M160 12l-15-8M160 12l-15 8" stroke="currentColor" stroke-width="2" fill="none"/>
                            </svg>
                        </div>
                        <div class="route-main">
                            <span class="city-name">{{ selectedTicket?.arrivalCity }}</span>
                            <span class="time-info">{{ selectedTicket?.arrivalTime }}</span>
                        </div>
                    </div>
                    <div class="modal-details">
                        <div class="detail-row">
                            <span class="detail-label">承运人</span>
                            <span class="detail-value">{{ selectedTicket?.carrier }}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">舱位等级</span>
                            <span class="detail-value">{{ selectedTicket?.seatClass }}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">剩余票数</span>
                            <span class="detail-value">{{ selectedTicket?.stock }}</span>
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
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getTicketList } from '@/api/ticket.js'

const tickets = ref([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const selectedTicket = ref(null)

const fetchTickets = async () => {
    loading.value = true
    error.value = ''
    try {
        const response = await getTicketList()
        tickets.value = response.data?.records || []
    } catch (err) {
        error.value = err.message || '获取数据失败'
        console.error('获取机票数据失败:', err)
    } finally {
        loading.value = false
    }
}

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
.ticket-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 22px;
}

.loading, .error-state {
    text-align: center;
    padding: 60px 20px;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 3px solid #f5f5f7;
    border-top-color: #2997ff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.error-state .btn {
    margin-top: 16px;
    padding: 12px 24px;
    background: #2997ff;
    color: white;
    border: none;
    border-radius: 12px;
    cursor: pointer;
}

.ticket-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 24px;
}

.ticket-card {
    background: #f5f5f7;
    border-radius: 24px;
    padding: 28px 24px;
    transition: transform 0.2s, box-shadow 0.2s;
    cursor: pointer;
}

.ticket-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0,0,0,0.1);
}

.ticket-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.ticket-type {
    font-size: 18px;
    font-weight: 600;
    color: #1d1d1f;
}

.ticket-status {
    font-size: 13px;
    padding: 4px 12px;
    border-radius: 20px;
    font-weight: 500;
}

.status-available {
    background: #d1fae5;
    color: #065f46;
}

.status-pending {
    background: #fef3c7;
    color: #92400e;
}

.status-soldout {
    background: #fee2e2;
    color: #991b1b;
}

.ticket-route {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
}

.route-point {
    display: flex;
    flex-direction: column;
}

.route-point .city {
    font-size: 22px;
    font-weight: 700;
    color: #1d1d1f;
}

.route-point .time {
    font-size: 14px;
    color: #6e6e73;
    margin-top: 4px;
}

.route-arrow {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 20px;
}

.arrow-icon {
    width: 80px;
    height: 24px;
    color: #6e6e73;
}

.ticket-info {
    display: flex;
    gap: 24px;
    padding: 16px 0;
    border-top: 1px solid #d2d2d6;
    border-bottom: 1px solid #d2d2d6;
    margin-bottom: 20px;
}

.info-item {
    display: flex;
    flex-direction: column;
}

.info-item .label {
    font-size: 12px;
    color: #6e6e73;
}

.info-item .value {
    font-size: 14px;
    font-weight: 500;
    color: #1d1d1f;
    margin-top: 4px;
}

.ticket-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.price {
    font-size: 28px;
    font-weight: 700;
    color: #1d1d1f;
}

.btn-book {
    padding: 10px 24px;
    background: #2997ff;
    color: white;
    border: none;
    border-radius: 24px;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: background 0.2s;
}

.btn-book:hover {
    background: #0066cc;
}

.empty-state {
    text-align: center;
    padding: 60px 20px;
    color: #6e6e73;
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

.modal-content {
    background: white;
    border-radius: 24px;
    max-width: 560px;
    width: 90%;
    max-height: 80vh;
    overflow-y: auto;
    position: relative;
    animation: slideUp 0.3s ease;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.close-btn {
    position: absolute;
    top: 16px;
    right: 16px;
    background: none;
    border: none;
    font-size: 28px;
    cursor: pointer;
    color: #6e6e73;
    z-index: 10;
}

.close-btn:hover {
    color: #1d1d1f;
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

.modal-header h2 {
    font-size: 28px;
    font-weight: 700;
    color: #1d1d1f;
}

.status-badge {
    font-size: 13px;
    padding: 6px 14px;
    border-radius: 20px;
    font-weight: 500;
}

.modal-route {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px;
    background: #f5f5f7;
    border-radius: 16px;
    margin-bottom: 24px;
}

.route-main {
    display: flex;
    flex-direction: column;
}

.city-name {
    font-size: 24px;
    font-weight: 700;
    color: #1d1d1f;
}

.time-info {
    font-size: 14px;
    color: #6e6e73;
    margin-top: 4px;
}

.route-line {
    flex: 1;
    display: flex;
    justify-content: center;
}

.flight-icon {
    width: 120px;
    height: 24px;
    color: #2997ff;
}

.modal-details {
    margin-bottom: 24px;
}

.detail-row {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid #f5f5f7;
}

.detail-row:last-child {
    border-bottom: none;
}

.detail-label {
    color: #6e6e73;
    font-size: 15px;
}

.detail-value {
    color: #1d1d1f;
    font-size: 15px;
    font-weight: 500;
}

.price-highlight {
    font-size: 24px;
    font-weight: 700;
    color: #2997ff;
}

.modal-actions {
    display: flex;
    gap: 16px;
}

.modal-actions .btn {
    flex: 1;
    padding: 14px 24px;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.modal-actions .btn.secondary {
    background: #f5f5f7;
    color: #1d1d1f;
}

.modal-actions .btn.secondary:hover {
    background: #e5e5e7;
}

.modal-actions .btn.primary {
    background: #2997ff;
    color: white;
}

.modal-actions .btn.primary:hover {
    background: #0066cc;
}

@media (max-width: 768px) {
    .ticket-list {
        grid-template-columns: 1fr;
    }

    .ticket-route {
        flex-direction: column;
        gap: 16px;
    }

    .route-arrow {
        transform: rotate(90deg);
    }

    .modal-route {
        flex-direction: column;
        gap: 16px;
    }

    .route-line {
        transform: rotate(90deg);
    }
}
</style>
