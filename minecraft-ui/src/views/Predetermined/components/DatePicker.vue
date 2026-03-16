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
  margin-bottom: 20px;
}

.date-picker {
  flex: 1;
}

.date-picker label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.date-picker input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

@media (max-width: 768px) {
  .date-picker-component {
    flex-direction: column;
  }
}
</style>