<template>
  <div class="product-type-selector">
    <div class="type-item" v-for="(type, index) in types" :key="index">
      <div class="type-label">
        <span class="label-dot"></span>
        {{ type.label }}
      </div>
      <div class="type-options">
        <div
          v-for="(option, optionIndex) in type.options"
          :key="optionIndex"
          class="option-item"
          :class="{ active: selectedOptions[type.label] === option.value }"
          @click="selectOption(type.label, option.value)"
        >
          <span class="option-check" v-if="selectedOptions[type.label] === option.value">
            <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
          </span>
          {{ option.label }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  types: {
    type: Array,
    default: null
  }
})

const types = computed(() => props.types || [
  {
    label: t('payment.productType.version'),
    options: [
      { label: t('payment.productType.java'), value: 'java' },
      { label: t('payment.productType.bedrock'), value: 'bedrock' },
      { label: t('payment.productType.education'), value: 'education' }
    ]
  },
  {
    label: t('payment.productType.spec'),
    options: [
      { label: t('payment.productType.standard'), value: 'standard' },
      { label: t('payment.productType.deluxe'), value: 'deluxe' },
      { label: t('payment.productType.ultimate'), value: 'ultimate' }
    ]
  }
])

const emit = defineEmits(['optionChange'])

const selectedOptions = reactive({})

// 初始化默认选中第一个选项
types.value.forEach(type => {
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
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
  color: var(--theme-text-1);
}

.type-item {
  margin-bottom: 26px;
}

.type-item:last-child {
  margin-bottom: 0;
}

.type-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin-bottom: 14px;
  letter-spacing: -0.01em;
}

.label-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--theme-primary);
  flex-shrink: 0;
}

.type-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-item {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border: 1.5px solid var(--theme-border);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.22s cubic-bezier(0.4, 0, 0.2, 1);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  user-select: none;
}

.option-item:hover {
  border-color: var(--theme-primary);
  background: var(--theme-primary-soft);
  transform: translateY(-1px);
}

.option-item.active {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
  font-weight: 600;
  box-shadow: var(--theme-ring);
}

.option-check {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--theme-primary);
  animation: checkPop 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes checkPop {
  from { transform: scale(0.4); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

@media (max-width: 767px) {
  .type-item { margin-bottom: 22px; }
  .type-label { font-size: 14px; margin-bottom: 12px; }
  .type-options { gap: 8px; }
  .option-item { padding: 9px 15px; font-size: 13px; border-radius: 10px; }
}

@media (max-width: 480px) {
  .option-item { padding: 8px 13px; font-size: 12px; }
}
</style>