<template>
  <div class="booking-form-component">
    <h4>预订详情</h4>
    <div class="form-group">
      <label>联系人姓名</label>
      <input type="text" v-model="contactName" placeholder="请输入联系人姓名">
    </div>
    <div class="form-group">
      <label>联系电话</label>
      <input type="tel" v-model="contactPhone" placeholder="请输入联系电话">
    </div>
    <div class="form-group">
      <label>电子邮箱</label>
      <input type="email" v-model="contactEmail" placeholder="请输入电子邮箱">
    </div>
    <div class="special-request" v-if="showSpecialRequest">
      <label>特殊要求</label>
      <textarea v-model="specialRequest" placeholder="如有特殊要求请在此说明"></textarea>
    </div>
    <slot name="additional-info"></slot>
    <div class="price-summary">
      <slot name="price-items"></slot>
      <div class="price-item total">
        <span>总计</span>
        <span>¥{{ totalPrice }}</span>
      </div>
    </div>
    <button class="submit-btn" @click="submitBooking">提交预订</button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref } from 'vue'

defineProps({
  totalPrice: {
    type: Number,
    default: 0
  },
  showSpecialRequest: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['submit'])

const contactName = ref('')
const contactPhone = ref('')
const contactEmail = ref('')
const specialRequest = ref('')

const submitBooking = () => {
  if (!contactName.value || !contactPhone.value || !contactEmail.value) {
    alert('请填写完整的联系人信息')
    return
  }
  emit('submit', {
    contactName: contactName.value,
    contactPhone: contactPhone.value,
    contactEmail: contactEmail.value,
    specialRequest: specialRequest.value
  })
}
</script>

<style scoped>
.booking-form-component {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  background: rgba(255, 255, 255, 0.9);
  padding: 25px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.booking-form-component h4 {
  margin-bottom: 25px;
  font-size: 18px;
  font-weight: bold;
  color: #1a1a1a;
}

.form-group {
  box-sizing: border-box;
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #3d3d3d;
}

.form-group input {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.8);
  color: #333333;
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

.special-request {
  box-sizing: border-box;
  margin-bottom: 25px;
}

.special-request label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #3d3d3d;
}

.special-request textarea {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  min-height: 100px;
  background: rgba(255, 255, 255, 0.8);
  color: #333333;
  transition: all 0.3s ease;
}

.special-request textarea:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

.price-summary {
  box-sizing: border-box;
  margin-bottom: 25px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: #3d3d3d;
}

.price-item.total {
  font-weight: bold;
  font-size: 18px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  color: #ff6a00;
}

.submit-btn {
  box-sizing: border-box;
  width: 100%;
  padding: 16px;
  background-color: #ff6a00;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 18px;
  font-weight: bold;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(255, 106, 0, 0.3);
}

.submit-btn:hover {
  background-color: #ff8c00;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 106, 0, 0.4);
}
</style>
