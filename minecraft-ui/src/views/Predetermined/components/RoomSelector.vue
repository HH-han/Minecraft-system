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
  margin-bottom: 30px;
}

.room-selector h4 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.room-option {
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

.room-option:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.room-info {
  flex: 1;
}

.room-info h5 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.room-info p {
  color: #666;
  margin-bottom: 10px;
  font-size: 14px;
}

.room-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.room-facilities .facility {
  background-color: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  color: #666;
}

.room-price {
  text-align: right;
  min-width: 150px;
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

.book-btn {
  padding: 8px 20px;
  background-color: #ff6a00;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.book-btn:hover {
  background-color: #ff8c00;
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