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
  margin-top: 24px;
  padding-top: 0;
  border-top: none;
}

.form-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
  line-height: 1.3;
  display: flex;
  align-items: center;
  gap: 6px;
}

.ticket-count {
  font-size: 13px;
  font-weight: 400;
  color: var(--theme-text-2);
}

.tourist-form {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  background: var(--theme-bg-surface);
  padding: 18px 20px;
  border-radius: 18px;
  margin-bottom: 14px;
  border: 1px solid var(--theme-hairline);
  box-shadow: none;
  transition: border-color 0.2s ease;
}

.tourist-form:hover {
  border-color: var(--theme-border-strong);
}

.tourist-head {
  margin-bottom: 14px;
}

.tourist-index {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 999px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 12px;
  font-weight: 600;
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
  color: var(--theme-text-2);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.form-group input {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  height: 44px;
  padding: 0 16px;
  border: 1px solid var(--theme-border-strong);
  border-radius: 12px;
  font-size: 16px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.form-group input::placeholder {
  color: var(--theme-text-3);
}

.form-group input:hover {
  border-color: var(--theme-border-strong);
  background: var(--theme-bg-surface);
}

.form-group input:focus {
  outline: 2px solid var(--theme-primary);
  outline-offset: 2px;
  border-color: var(--theme-primary);
  background: var(--theme-bg-surface);
}

/* 响应式：手机 - 表单字段堆叠单列 */
@media (max-width: 767px) {
  .tourist-form {
    padding: 16px 18px;
    border-radius: 16px;
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
