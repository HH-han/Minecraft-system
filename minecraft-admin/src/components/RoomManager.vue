<template>
  <div class="room-manager">
    <label>{{ label }}:</label>
    <div class="room-list">
      <div v-for="(room, index) in localRooms" :key="room.id || index" class="room-item">
        <div class="room-header">
          <span>{{ title }} {{ index + 1 }}</span>
          <button class="btn remove-room-btn" @click="removeRoom(index)">删除</button>
        </div>
        <div class="room-form">
          <input v-model="room.name" placeholder="名称" class="room-input" />
          <input v-model="room.description" placeholder="描述" class="room-input" />
          <input v-model="room.price" placeholder="价格" type="number" step="0.01" class="room-input" />
          <select v-model="room.facilities" multiple class="room-facilities-select" size="3">
            <option v-for="facility in roomFacilities" :key="facility" :value="facility">
              {{ facility }}
            </option>
          </select>
        </div>
      </div>
      <button type="button" class="btn add-room-btn" @click="addRoom">添加{{ title }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  roomFacilities: {
    type: Array,
    default: () => ['免费WiFi', '空调', '电视', '独立卫浴', '迷你吧']
  },
  label: {
    type: String,
    default: '房型列表'
  },
  title: {
    type: String,
    default: '房型'
  }
});

const emit = defineEmits(['update:modelValue']);

const localRooms = ref((props.modelValue && props.modelValue.length > 0) ? [...props.modelValue] : [{
  id: null,
  name: '',
  description: '',
  price: '',
  facilities: []
}]);

watch(() => props.modelValue, (newVal) => {
  const newValue = (newVal && newVal.length > 0) ? [...newVal] : [{
    id: null,
    name: '',
    description: '',
    price: '',
    facilities: []
  }];
  
  if (JSON.stringify(localRooms.value) !== JSON.stringify(newValue)) {
    localRooms.value = newValue;
  }
});

const emitUpdate = () => {
  emit('update:modelValue', [...localRooms.value]);
};

const addRoom = () => {
  localRooms.value.push({
    id: null,
    name: '',
    description: '',
    price: '',
    facilities: []
  });
  emitUpdate();
};

const removeRoom = (index) => {
  if (localRooms.value.length > 1) {
    localRooms.value.splice(index, 1);
    emitUpdate();
  }
};
</script>

<style scoped>
.room-manager {
  width: 100%;
}

.room-manager label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.room-list {
  margin-top: 8px;
}

.room-item {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 8px;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-weight: bold;
}

.room-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.room-input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.room-facilities-select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.add-room-btn, .remove-room-btn {
  padding: 6px 12px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-room-btn {
  background-color: #1976d2;
  color: white;
  margin-top: 8px;
}

.add-room-btn:hover {
  background-color: #1565c0;
}

.remove-room-btn {
  background-color: #ef5350;
  color: white;
}

.remove-room-btn:hover {
  background-color: #e53935;
}
</style>