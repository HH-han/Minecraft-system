<template>
  <div class="tourist-info-form">
    <h5 class="form-title">
      {{ ticketName }}
      <span class="ticket-count">({{ quantity }}人)</span>
    </h5>

    <div class="tourist-form" v-for="i in quantity" :key="'tf-' + i">
      <div class="tourist-head">
        <span class="tourist-index">游客 {{ i }}</span>
      </div>
      <div class="form-row">
        <div class="form-group half">
          <label :for="'name-' + ticketName + '-' + i">姓名</label>
          <input
            :id="'name-' + ticketName + '-' + i"
            type="text"
            v-model="tourists[i-1].name"
            placeholder="请输入游客姓名"
            autocomplete="name"
            @input="updateTouristInfo"
          >
        </div>
        <div class="form-group half">
          <label :for="'idcard-' + ticketName + '-' + i">身份证号</label>
          <input
            :id="'idcard-' + ticketName + '-' + i"
            type="text"
            v-model="tourists[i-1].idCard"
            placeholder="请输入身份证号"
            autocomplete="off"
            @input="updateTouristInfo"
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, watch } from 'vue'

const props = defineProps({
  ticketName: {
    type: String,
    required: true
  },
  quantity: {
    type: Number,
    default: 0
  },
  initialTourists: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['touristInfoChange'])

const tourists = ref([])

const updateTouristsArray = (newQuantity) => {
  const newTourists = []
  for (let i = 0; i < newQuantity; i++) {
    newTourists.push({
      name: tourists.value[i]?.name || '',
      idCard: tourists.value[i]?.idCard || ''
    })
  }
  tourists.value = newTourists
  updateTouristInfo()
}

const updateTouristInfo = () => {
  emit('touristInfoChange', tourists.value)
}

watch(() => props.quantity, (newQuantity) => {
  updateTouristsArray(newQuantity)
}, { immediate: true })

watch(() => props.initialTourists, (newTourists) => {
  if (newTourists && newTourists.length > 0) {
    tourists.value = newTourists.map(t => ({ name: t.name || '', idCard: t.idCard || '' }))
  }
}, { immediate: true })
</script>

<style scoped>
.tourist-info-form {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  margin-top: 28px;
  padding-top: 0;
  border-top: none;
}

.form-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.3;
  display: flex;
  align-items: center;
  gap: 6px;
}

.ticket-count {
  font-size: 13px;
  font-weight: 400;
  color: #6e6e73;
}

.tourist-form {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  background: #ffffff;
  padding: 20px 24px;
  border-radius: 20px;
  margin-bottom: 12px;
  border: 1px solid #d2d2d6;
  box-shadow: none;
}

.tourist-head {
  margin-bottom: 14px;
}

.tourist-index {
  font-size: 12px;
  font-weight: 500;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.form-row {
  box-sizing: border-box;
  display: flex;
  gap: 24px;
  margin-bottom: 0;
}

.form-group {
  box-sizing: border-box;
  margin-bottom: 0;
  flex: 1;
  min-width: 0;
}

.form-group.half {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.form-group input {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  height: 44px;
  padding: 0 16px;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  font-size: 16px;
  background: #f5f5f7;
  color: #1d1d1f;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.form-group input::placeholder {
  color: #a1a1a6;
}

.form-group input:hover {
  border-color: #c7c7cc;
  background: #ffffff;
}

.form-group input:focus {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
  border-color: #2997ff;
  background: #ffffff;
}

/* 响应式：手机 - 表单字段堆叠单列 */
@media (max-width: 767px) {
  .tourist-form {
    padding: 16px 18px;
    border-radius: 18px;
  }

  .form-row {
    flex-direction: column;
    gap: 16px;
  }

  .form-group.half {
    width: 100%;
    max-width: 100%;
  }

  .form-title {
    font-size: 15px;
  }
}
</style>
