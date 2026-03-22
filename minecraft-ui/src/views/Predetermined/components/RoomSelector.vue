<template>
  <div class="room-selector">
    <h4>选择房型</h4>
    <div class="room-option" v-for="room in rooms" :key="room.id">
      <div class="room-info">
        <h5>{{ room.name }}</h5>
        <p>{{ room.description }}</p>
        <div class="room-facilities">
          <span class="facility" v-for="facility in room.facilities" :key="facility">{{ facility }}</span>
        </div>
      </div>
      <div class="room-price">
        <div class="price">
          <span class="price-value">¥{{ room.price }}</span>
          <span class="price-unit">/晚</span>
        </div>
        <button class="book-btn" @click="selectRoom(room)">
          {{ selectedRoom?.id === room.id ? '已选择' : '选择' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    rooms: {
      type: Array,
      default: () => []
    },
    selectedRoom: {
      type: Object,
      default: null
    }
  },
  emits: ['roomSelect'],
  methods: {
    selectRoom(room) {
      this.$emit('roomSelect', room)
    }
  }
}
</script>

<style scoped>
.room-selector {
  margin-bottom: 35px;
}

.room-selector h4 {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.room-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  margin-bottom: 15px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.room-option:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 106, 0, 0.3);
}

.room-info {
  flex: 1;
}

.room-info h5 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.room-info p {
  color: #666;
  margin-bottom: 12px;
  font-size: 14px;
}

.room-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.room-facilities .facility {
  background: rgba(255, 255, 255, 0.3);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.room-price {
  text-align: right;
  min-width: 150px;
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
  color: #666;
}

.book-btn {
  padding: 10px 24px;
  background-color: #ff6a00;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 106, 0, 0.3);
}

.book-btn:hover {
  background-color: #ff8c00;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 106, 0, 0.4);
}

@media (max-width: 768px) {
  .room-option {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .room-price {
    margin-top: 15px;
    align-self: flex-end;
  }
}
</style>