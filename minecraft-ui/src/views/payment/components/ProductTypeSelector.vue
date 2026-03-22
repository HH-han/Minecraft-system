<template>
  <div class="product-type-selector">
    <div class="type-item" v-for="(type, index) in types" :key="index">
      <div class="type-label">{{ type.label }}：</div>
      <div class="type-options">
        <div 
          v-for="(option, optionIndex) in type.options" 
          :key="optionIndex"
          class="option-item"
          :class="{ active: selectedOptions[type.label] === option.value }"
          @click="selectOption(type.label, option.value)"
        >
          {{ option.label }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const props = defineProps({
  types: {
    type: Array,
    default: () => [
      {
        label: '版本',
        options: [
          { label: 'Java版', value: 'java' },
          { label: '基岩版', value: 'bedrock' },
          { label: '教育版', value: 'education' }
        ]
      },
      {
        label: '规格',
        options: [
          { label: '标准版', value: 'standard' },
          { label: '豪华版', value: 'deluxe' },
          { label: '终极版', value: 'ultimate' }
        ]
      }
    ]
  }
})

const emit = defineEmits(['optionChange'])

const selectedOptions = reactive({})

// 初始化默认选中第一个选项
props.types.forEach(type => {
  if (type.options && type.options.length > 0) {
    selectedOptions[type.label] = type.options[0].value
  }
})

const selectOption = (typeLabel, optionValue) => {
  selectedOptions[typeLabel] = optionValue
  emit('optionChange', selectedOptions)
}
</script>

<style scoped>
.product-type-selector {
  margin-bottom: 30px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 25px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.product-type-selector:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.type-item {
  margin-bottom: 20px;
}

.type-item:last-child {
  margin-bottom: 0;
}

.type-label {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 12px;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.type-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.option-item {
  padding: 10px 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  font-weight: 500;
}

.option-item:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.2);
  background: rgba(255, 77, 79, 0.05);
}

.option-item.active {
  border-color: #ff4d4f;
  color: #ff4d4f;
  background: rgba(255, 77, 79, 0.1);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.2);
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .product-type-selector {
    padding: 20px;
  }
  
  .type-options {
    gap: 10px;
  }
  
  .option-item {
    padding: 8px 16px;
    font-size: 13px;
    border-radius: 10px;
  }
  
  .type-label {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .product-type-selector {
    padding: 15px;
  }
  
  .option-item {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>