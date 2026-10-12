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
import { defineProps, defineEmits, ref, onMounted, watch } from 'vue'

const props = defineProps({
  totalPrice: {
    type: Number,
    default: 0
  },
  showSpecialRequest: {
    type: Boolean,
    default: true
  },
  // 修改预订时回填的联系人信息
  initialContact: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['submit'])

const contactName = ref('')
const contactPhone = ref('')
const contactEmail = ref('')
const specialRequest = ref('')

// 用外部传入的联系人信息回填表单
const applyContact = () => {
  const c = props.initialContact
  if (!c) return
  contactName.value = c.name || ''
  contactPhone.value = c.phone || ''
  contactEmail.value = c.email || ''
  specialRequest.value = c.specialRequest || ''
}

onMounted(applyContact)
watch(() => props.initialContact, applyContact, { deep: true })

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
  background: var(--theme-bg-surface-2);
  padding: 32px;
  border-radius: 24px;
  border: none;
  box-shadow: none;
}

.form-title {
  margin: 0 0 28px;
  font-size: 20px;
  font-weight: 700;
  color: var(--theme-text-1);
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
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-group input::placeholder {
  color: var(--theme-text-3);
}

.form-group input:hover {
  border-color: var(--theme-border-strong);
}

.form-group input:focus {
  outline: 2px solid var(--theme-primary);
  outline-offset: 2px;
  border-color: var(--theme-primary);
}

/* 特殊要求 textarea */
.special-request textarea {
  box-sizing: border-box;
  width: 100%;
  max-width: 100%;
  padding: 14px 16px;
  border: 1px solid var(--theme-border-strong);
  border-radius: 16px;
  font-size: 16px;
  line-height: 1.5;
  resize: vertical;
  min-height: 104px;
  background: var(--theme-bg-surface);
  color: var(--theme-text-1);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  font-family: inherit;
}

.special-request textarea::placeholder {
  color: var(--theme-text-3);
}

.special-request textarea:hover {
  border-color: var(--theme-border-strong);
}

.special-request textarea:focus {
  outline: 2px solid var(--theme-primary);
  outline-offset: 2px;
  border-color: var(--theme-primary);
}

/* 价格汇总 */
.price-summary {
  box-sizing: border-box;
  margin: 28px 0;
  padding: 24px 0 0;
  border-top: 1px solid var(--theme-border-strong);
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
  color: var(--theme-text-1);
  line-height: 1.4;
}

.price-item.total {
  font-weight: 600;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--theme-border-strong);
  border-top-style: dashed;
  color: var(--theme-text-1);
  margin-bottom: 0;
}

.total-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.1;
  color: var(--theme-text-1);
}

/* 提交按钮：胶囊蓝色 CTA */
.submit-btn {
  box-sizing: border-box;
  width: 100%;
  height: 48px;
  padding: 0 28px;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
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
  background: var(--theme-primary-hover);
}

.submit-btn:active {
  transform: scale(0.99);
}

.submit-btn:focus-visible {
  outline: 2px solid var(--theme-primary);
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
