<template>
  <el-autocomplete :model-value="modelValue" :fetch-suggestions="fetchStations" value-key="stationName"
    :trigger-on-focus="false" :debounce="280" clearable :placeholder="placeholder"
    @update:model-value="handleInput">
    <template #prefix>
      <el-icon><Location /></el-icon>
    </template>
    <template #default="{ item }">
      <div class="rw-svc-station-option">
        <span class="rw-svc-station-name">{{ item.stationName }}</span>
        <span class="rw-svc-station-city">{{ item.cityName }}</span>
        <span class="rw-svc-station-code">{{ item.stationCode }}</span>
      </div>
    </template>
  </el-autocomplete>
</template>

<script setup>
import { Location } from '@element-plus/icons-vue'
import { getRailwayStations } from '@/api/railway'

defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '' },
})

const emit = defineEmits(['update:modelValue'])

const handleInput = (value) => emit('update:modelValue', value || '')

const fetchStations = async (keyword, callback) => {
  if (!keyword) {
    callback([])
    return
  }
  try {
    const res = await getRailwayStations(keyword)
    callback((res?.data || []).map((item) => ({ ...item, value: item.stationName })))
  } catch {
    callback([])
  }
}
</script>
