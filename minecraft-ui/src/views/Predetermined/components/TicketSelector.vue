<template>
  <div class="ticket-selector">
    <h4>选择票种</h4>
    <div class="ticket-option" v-for="ticket in tickets" :key="ticket.id">
      <div class="ticket-info">
        <h5>{{ ticket.name }}</h5>
        <p>{{ ticket.description }}</p>
        <div class="ticket-rules">
          <span class="rule" v-for="rule in ticket.rules" :key="rule">{{ rule }}</span>
        </div>
      </div>
      <div class="ticket-price">
        <div class="price">
          <span class="price-value">¥{{ ticket.price }}</span>
          <span class="price-unit">/人</span>
        </div>
        <div class="ticket-quantity">
          <button @click="decreaseQuantity(ticket.id)" :disabled="quantities[ticket.id] <= 0">-</button>
          <span>{{ quantities[ticket.id] }}</span>
          <button @click="increaseQuantity(ticket.id)" :disabled="quantities[ticket.id] >= 10">+</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    tickets: {
      type: Array,
      default: () => []
    },
    initialQuantities: {
      type: Object,
      default: () => {}
    }
  },
  emits: ['quantityChange'],
  data() {
    return {
      quantities: {}
    }
  },
  mounted() {
    // 初始化票种数量
    this.tickets.forEach(ticket => {
      this.quantities[ticket.id] = this.initialQuantities[ticket.id] || 0
    })
  },
  methods: {
    increaseQuantity(ticketId) {
      if (this.quantities[ticketId] < 10) {
        this.quantities[ticketId]++
        this.$emit('quantityChange', { [ticketId]: this.quantities[ticketId] })
      }
    },
    decreaseQuantity(ticketId) {
      if (this.quantities[ticketId] > 0) {
        this.quantities[ticketId]--
        this.$emit('quantityChange', { [ticketId]: this.quantities[ticketId] })
      }
    }
  }
}
</script>

<style scoped>
.ticket-selector {
  margin-bottom: 30px;
}

.ticket-selector h4 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.ticket-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: white;
  border-radius: 8px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  transition: all 0.3s ease;
}

.ticket-option:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ticket-info {
  flex: 1;
}

.ticket-info h5 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.ticket-info p {
  color: #666;
  margin-bottom: 10px;
  font-size: 14px;
}

.ticket-rules {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.ticket-rules .rule {
  background-color: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  color: #666;
}

.ticket-price {
  text-align: right;
  min-width: 200px;
}

.price {
  margin-bottom: 10px;
}

.price-value {
  font-size: 20px;
  font-weight: bold;
  color: #ff6a00;
}

.price-unit {
  font-size: 14px;
  color: #666;
}

.ticket-quantity {
  display: flex;
  align-items: center;
  width: 120px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  margin-left: auto;
}

.ticket-quantity button {
  width: 30px;
  height: 36px;
  border: none;
  background-color: #f5f5f5;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.ticket-quantity button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ticket-quantity span {
  flex: 1;
  text-align: center;
  line-height: 36px;
}

@media (max-width: 768px) {
  .ticket-option {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .ticket-price {
    margin-top: 15px;
    align-self: flex-end;
  }
}
</style>