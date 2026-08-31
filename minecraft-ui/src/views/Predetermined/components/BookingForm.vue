<template>
  <div class="booking-form-component">
    <h4 class="form-title">预订详情</h4>

    <div class="form-group">
      <label for="contactName">联系人姓名</label>
      <input
        id="contactName"
        type="text"
        v-model="contactName"
        placeholder="请输入联系人姓名"
        autocomplete="name"
      >
    </div>

    <div class="form-group">
      <label for="contactPhone">联系电话</label>
      <input
        id="contactPhone"
        type="tel"
        v-model="contactPhone"
        placeholder="请输入联系电话"
        autocomplete="tel"
      >
    </div>

    <div class="form-group">
      <label for="contactEmail">电子邮箱</label>
      <input
        id="contactEmail"
        type="email"
        v-model="contactEmail"
        placeholder="请输入电子邮箱"
        autocomplete="email"
      >
    </div>

    <div class="form-group special-request" v-if="showSpecialRequest">
      <label for="specialRequest">特殊要求</label>
      <textarea
        id="specialRequest"
        v-model="specialRequest"
        placeholder="如有特殊要求请在此说明（选填）"
        rows="4"
      ></textarea>
    </div>

    <slot name="additional-info"></slot>

    <div class="price-summary">
      <slot name="price-items"></slot>
      <div class="price-item total">
        <span>总计</span>
        <span class="total-value">¥{{ totalPrice }}</span>
      </div>
    </div>

    <button type="button" class="submit-btn" @click="submitBooking">
      提交预订
    </button>
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
  background: #f5f5f7;
  padding: 32px;
  border-radius: 24px;
  border: none;
  box-shadow: none;
}

.form-title {
  margin: 0 0 28px;
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.2;
}

.form-group {
  box-sizing: border-box;
  margin-bottom: 20px;
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
  background: #ffffff;
  color: #1d1d1f;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-group input::placeholder {
  color: #a1a1a6;
}

.form-group input:hover {
  border-color: #c7c7cc;
}

.form-group input:focus {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
  border-color: #2997ff;
}

/* 特殊要求 textarea */
.special-request textarea {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  padding: 14px 16px;
  border: 1px solid #d2d2d6;
  border-radius: 16px;
  font-size: 16px;
  line-height: 1.5;
  resize: vertical;
  min-height: 104px;
  background: #ffffff;
  color: #1d1d1f;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  font-family: inherit;
}

.special-request textarea::placeholder {
  color: #a1a1a6;
}

.special-request textarea:hover {
  border-color: #c7c7cc;
}

.special-request textarea:focus {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
  border-color: #2997ff;
}

/* 价格汇总 */
.price-summary {
  box-sizing: border-box;
  margin: 28px 0;
  padding: 24px 0 0;
  border-top: 1px solid #d2d2d6;
  border-radius: 0;
  background: none;
  border-left: none;
  border-right: none;
  border-bottom: none;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 12px;
  font-size: 15px;
  color: #1d1d1f;
  line-height: 1.4;
}

.price-item.total {
  font-weight: 600;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #d2d2d6;
  border-top-style: dashed;
  color: #1d1d1f;
  margin-bottom: 0;
}

.total-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.1;
  color: #1d1d1f;
}

/* 提交按钮：胶囊蓝色 CTA */
.submit-btn {
  box-sizing: border-box;
  width: 100%;
  height: 48px;
  padding: 0 28px;
  background: #2997ff;
  color: #ffffff;
  border: none;
  border-radius: 40px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.01em;
  line-height: 48px;
  transition: background 0.2s ease, transform 0.1s ease;
  box-shadow: none;
}

.submit-btn:hover {
  background: #0066cc;
}

.submit-btn:active {
  transform: scale(0.99);
}

.submit-btn:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 3px;
}

/* 响应式：手机 */
@media (max-width: 767px) {
  .booking-form-component {
    padding: 24px 20px;
    border-radius: 20px;
  }

  .form-title {
    font-size: 18px;
    margin-bottom: 20px;
  }

  .form-group {
    margin-bottom: 16px;
  }

  .submit-btn {
    height: 46px;
    line-height: 46px;
    font-size: 15px;
  }

  .total-value {
    font-size: 20px;
  }

  .price-summary {
    margin: 20px 0;
    padding-top: 20px;
  }
}
</style>
