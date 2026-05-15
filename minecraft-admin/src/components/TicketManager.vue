<template>
  <div class="ticket-manager">
    <label>{{ label }}:</label>
    <div class="ticket-list">
      <div v-for="(ticket, index) in localTickets" :key="ticket.id || index" class="ticket-item">
        <div class="ticket-header">
          <span>{{ title }} {{ index + 1 }}</span>
          <button class="btn remove-ticket-btn" @click="removeTicket(index)">删除</button>
        </div>
        <div class="ticket-form">
          <input v-model="ticket.name" placeholder="名称" class="ticket-input" />
          <input v-model="ticket.description" placeholder="描述" class="ticket-input" />
          <input v-model="ticket.price" placeholder="价格" type="number" step="0.01" class="ticket-input" />
        </div>
      </div>
      <button type="button" class="btn add-ticket-btn" @click="addTicket">添加{{ title }}</button>
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
  label: {
    type: String,
    default: '门票列表'
  },
  title: {
    type: String,
    default: '门票'
  }
});

const emit = defineEmits(['update:modelValue']);

const localTickets = ref((props.modelValue && props.modelValue.length > 0) ? [...props.modelValue] : [{
  id: null,
  name: '',
  description: '',
  price: '',
  rules: []
}]);

watch(() => props.modelValue, (newVal) => {
  const newValue = (newVal && newVal.length > 0) ? [...newVal] : [{
    id: null,
    name: '',
    description: '',
    price: '',
    rules: []
  }];
  
  if (JSON.stringify(localTickets.value) !== JSON.stringify(newValue)) {
    localTickets.value = newValue;
  }
});

const emitUpdate = () => {
  emit('update:modelValue', [...localTickets.value]);
};

const addTicket = () => {
  localTickets.value.push({
    id: null,
    name: '',
    description: '',
    price: '',
    rules: []
  });
  emitUpdate();
};

const removeTicket = (index) => {
  if (localTickets.value.length > 1) {
    localTickets.value.splice(index, 1);
    emitUpdate();
  }
};
</script>

<style scoped>
.ticket-manager {
  width: 100%;
}

.ticket-manager label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.ticket-list {
  margin-top: 8px;
}

.ticket-item {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 8px;
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-weight: bold;
}

.ticket-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ticket-input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.add-ticket-btn, .remove-ticket-btn {
  padding: 6px 12px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-ticket-btn {
  background-color: #1976d2;
  color: white;
  margin-top: 8px;
}

.add-ticket-btn:hover {
  background-color: #1565c0;
}

.remove-ticket-btn {
  background-color: #ef5350;
  color: white;
}

.remove-ticket-btn:hover {
  background-color: #e53935;
}
</style>