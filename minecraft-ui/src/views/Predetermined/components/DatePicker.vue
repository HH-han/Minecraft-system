<template>
  <div class="date-picker-component">
    <div class="date-picker" v-for="(dateField, index) in dateFields" :key="index">
      <label>{{ dateField.label }}</label>
      <input 
        type="date" 
        v-model="dateField.value"
        :min="dateField.min"
        :max="dateField.max"
        @change="$emit('dateChange', { [dateField.name]: dateField.value })"
      >
    </div>
  </div>
</template>

<script>
export default {
  props: {
    dateFields: {
      type: Array,
      default: () => [
        {
          name: 'checkInDate',
          label: '入住日期',
          value: '',
          min: new Date().toISOString().split('T')[0]
        },
        {
          name: 'checkOutDate',
          label: '离店日期',
          value: '',
          min: new Date().toISOString().split('T')[0]
        }
      ]
    }
  },
  emits: ['dateChange']
}
</script>

<style scoped>
.date-picker-component {
  display: flex;
  gap: 20px;
  margin-bottom: 25px;
}

.date-picker {
  flex: 1;
}

.date-picker label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.date-picker input {
  width: 350px;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.date-picker input:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

@media (max-width: 768px) {
  .date-picker-component {
    flex-direction: column;
  }
}
</style>