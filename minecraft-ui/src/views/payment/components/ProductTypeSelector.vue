<template>
  <div class="product-type-selector">
    <div class="type-item" v-for="(type, index) in types" :key="index">
      <div class="type-label">{{ type.label }}</div>
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
  background: #ffffff;
  border-radius: 18px;
  padding: 28px;
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}

.type-item {
  margin-bottom: 24px;
}

.type-item:last-child {
  margin-bottom: 0;
}

.type-label {
  font-size: 16px;
  font-weight: 500;
  color: #1d1d1f;
  margin-bottom: 14px;
  letter-spacing: -0.01em;
}

.type-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.option-item {
  padding: 10px 20px;
  border: 1.5px solid transparent;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #f5f5f7;
  color: #1d1d1f;
  user-select: none;
}

.option-item:hover {
  transform: scale(1.01);
  background: #e8e8ed;
}

.option-item.active {
  border-color: #2997ff;
  color: #2997ff;
  background: rgba(41, 151, 255, 0.08);
  font-weight: 500;
}

.option-item.active:hover {
  background: rgba(41, 151, 255, 0.12);
}

@media (max-width: 767px) {
  .product-type-selector {
    padding: 20px;
    border-radius: 16px;
  }
  
  .type-item {
    margin-bottom: 20px;
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
    font-size: 15px;
    margin-bottom: 12px;
  }
}
</style>