<template>
  <div class="ticket-selector">
    <!-- 固定区：标题不随滚动移动 -->
    <h4 class="section-title">选择票种</h4>
    <!-- 滚动区：列表限定高度，超出部分内部滚动（隐藏滚动条但保留滚动能力） -->
    <div class="ticket-list-scroll scroll-container" role="list" aria-label="门票列表可滚动区域">
      <div
        class="ticket-option"
        v-for="ticket in tickets"
        :key="ticket.id"
        :class="{ active: quantities[ticket.id] > 0 }"
        role="listitem"
      >
        <div class="ticket-info">
          <h5 class="ticket-name">{{ ticket.name }}</h5>
          <p v-if="ticket.description" class="ticket-desc">{{ ticket.description }}</p>
          <div v-if="ticket.rules?.length" class="ticket-rules">
            <span class="rule" v-for="rule in ticket.rules" :key="rule">
              {{ rule }}
            </span>
          </div>
        </div>
        <div class="ticket-price">
          <div class="price-block">
            <span class="price-value">¥{{ ticket.price }}</span>
            <span class="price-unit">/人</span>
          </div>
          <div class="ticket-quantity" :class="{ filled: quantities[ticket.id] > 0 }">
            <button
              type="button"
              aria-label="减少数量"
              @click="decreaseQuantity(ticket.id)"
              :disabled="quantities[ticket.id] <= 0"
            >
              −
            </button>
            <span>{{ quantities[ticket.id] }}</span>
            <button
              type="button"
              aria-label="增加数量"
              @click="increaseQuantity(ticket.id)"
              :disabled="quantities[ticket.id] >= 10"
            >
              +
            </button>
          </div>
        </div>
      </div>
      <div v-if="!tickets || tickets.length === 0" class="empty-hint">
        暂无门票信息
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, reactive, watch, onMounted } from 'vue'

const props = defineProps({
  tickets: {
    type: Array,
    default: () => []
  },
  initialQuantities: {
    type: Object,
    default: () => {}
  }
})

const emit = defineEmits(['quantityChange'])

const quantities = reactive({})

const syncQuantities = () => {
  props.tickets.forEach(ticket => {
    if (quantities[ticket.id] === undefined) {
      quantities[ticket.id] = props.initialQuantities[ticket.id] || 0
    }
  })
}

onMounted(() => {
  syncQuantities()
})

watch(() => props.tickets, () => {
  syncQuantities()
}, { deep: true })

watch(() => props.initialQuantities, (val) => {
  if (!val) return
  Object.keys(val).forEach((k) => {
    if (quantities[k] !== undefined) quantities[k] = val[k] || 0
  })
}, { deep: true })

const increaseQuantity = (ticketId) => {
  if (quantities[ticketId] < 10) {
    quantities[ticketId]++
    emit('quantityChange', { [ticketId]: quantities[ticketId] })
  }
}

const decreaseQuantity = (ticketId) => {
  if (quantities[ticketId] > 0) {
    quantities[ticketId]--
    emit('quantityChange', { [ticketId]: quantities[ticketId] })
  }
}
</script>

<style scoped>
.ticket-selector {
  margin-bottom: 32px;
}

.section-title {
  margin: 0 0 20px;
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.3;
}

.ticket-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  padding: 24px;
  box-sizing: border-box;
  background: #f5f5f7;
  border-radius: 24px;
  margin-bottom: 12px;
  border: 1.5px solid transparent;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease, background 0.2s ease;
}

.ticket-option:hover {
  transform: scale(1.005);
  box-shadow: 0 8px 24px -12px rgba(0, 0, 0, 0.15);
}

.ticket-option.active {
  background: #ffffff;
  border-color: #2997ff;
  box-shadow: 0 8px 24px -12px rgba(41, 151, 255, 0.3);
}

.ticket-info {
  flex: 1;
  min-width: 0;
}

.ticket-name {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.3;
}

.ticket-desc {
  margin: 0 0 12px;
  color: #6e6e73;
  font-size: 14px;
  line-height: 1.4;
}

.ticket-rules {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.rule {
  background: #ffffff;
  color: #1d1d1f;
  border: 1px solid #d2d2d6;
  border-radius: 40px;
  padding: 5px 14px;
  font-size: 12px;
  line-height: 1.4;
}

.ticket-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  flex-shrink: 0;
}

.price-block {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.1;
}

.price-unit {
  font-size: 13px;
  color: #6e6e73;
}

/* 数量 stepper：Apple 风格胶囊 */
.ticket-quantity {
  display: inline-flex;
  align-items: center;
  width: 128px;
  height: 40px;
  border-radius: 40px;
  background: #ffffff;
  border: 1px solid #d2d2d6;
  overflow: hidden;
  transition: border-color 0.2s ease, background 0.2s ease;
}

.ticket-quantity.filled {
  border-color: #2997ff;
  background: #ffffff;
}

.ticket-quantity button {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  color: #6e6e73;
  font-size: 20px;
  font-weight: 400;
  cursor: pointer;
  transition: color 0.2s ease, background 0.2s ease;
  flex-shrink: 0;
  line-height: 1;
}

.ticket-quantity button:hover:not(:disabled) {
  color: #2997ff;
  background: rgba(41, 151, 255, 0.06);
}

.ticket-quantity button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.ticket-quantity span {
  flex: 1;
  text-align: center;
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1;
  user-select: none;
}

.ticket-quantity button:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: -2px;
}

.empty-hint {
  padding: 40px 16px;
  text-align: center;
  color: #6e6e73;
  font-size: 14px;
  border-radius: 24px;
  background: #f5f5f7;
}

/* ===== 滚动容器：固定高度 + 内部滚动 + 隐藏滚动条（参考 Recall 639323 两层结构） ===== */
.ticket-list-scroll {
  max-height: 520px;              /* 桌面：约放 4.5 张卡 */
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-right: 2px;
  box-sizing: border-box;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch; /* iOS 惯性滚动 */
}
/* Chrome / Safari / Edge 新版 */
.ticket-list-scroll::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
}
/* Firefox */
.ticket-list-scroll {
  scrollbar-width: none;
}
/* IE / 旧 Edge */
.ticket-list-scroll {
  -ms-overflow-style: none;
}
/* 原 .ticket-option 有 margin-bottom:16px；用 flex gap 后去重避免双倍 */
.ticket-list-scroll > .ticket-option {
  margin-bottom: 0 !important;
}

/* 平板 */
@media (max-width: 1199px) {
  .ticket-list-scroll {
    max-height: 460px;
  }
}
/* 手机 */
@media (max-width: 767px) {
  .ticket-list-scroll {
    max-height: 400px;
    gap: 12px;
  }
}

/* 响应式：手机竖排 */
@media (max-width: 767px) {
  .ticket-option {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
    padding: 20px;
  }

  .ticket-price {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
  }

  .section-title {
    font-size: 17px;
  }

  .price-value {
    font-size: 20px;
  }
}
</style>
