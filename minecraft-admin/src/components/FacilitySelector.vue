<template>
  <div class="facility-selector">
    <label>{{ label }}:</label>
    <select :value="localSelected" @change="handleChange" multiple class="multiselect" :size="size">
      <option v-for="facility in facilities" :key="facility" :value="facility">
        {{ facility }}
      </option>
    </select>
    <p class="select-hint">按住 Ctrl 或 Cmd 键可多选</p>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  facilities: {
    type: Array,
    required: true
  },
  label: {
    type: String,
    default: '选择设施'
  },
  size: {
    type: Number,
    default: 4
  }
});

const emit = defineEmits(['update:modelValue']);

const localSelected = ref([...props.modelValue]);

watch(() => props.modelValue, (newVal) => {
  const newValue = [...newVal];
  if (JSON.stringify(localSelected.value) !== JSON.stringify(newValue)) {
    localSelected.value = newValue;
  }
});

const handleChange = (event) => {
  const selectedOptions = Array.from(event.target.selectedOptions);
  localSelected.value = selectedOptions.map(opt => opt.value);
  emit('update:modelValue', [...localSelected.value]);
};
</script>

<style scoped>
.facility-selector {
  width: 100%;
}

.facility-selector label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.multiselect {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.select-hint {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
</style>