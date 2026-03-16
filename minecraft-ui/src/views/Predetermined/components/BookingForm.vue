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

<script>
export default {
  props: {
    totalPrice: {
      type: Number,
      default: 0
    },
    showSpecialRequest: {
      type: Boolean,
      default: true
    }
  },
  emits: ['submit'],
  data() {
    return {
      contactName: '',
      contactPhone: '',
      contactEmail: '',
      specialRequest: ''
    }
  },
  methods: {
    submitBooking() {
      if (!this.contactName || !this.contactPhone || !this.contactEmail) {
        alert('请填写完整的联系人信息')
        return
      }
      this.$emit('submit', {
        contactName: this.contactName,
        contactPhone: this.contactPhone,
        contactEmail: this.contactEmail,
        specialRequest: this.specialRequest
      })
    }
  }
}
</script>

<style scoped>
.booking-form-component {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.booking-form-component h4 {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.special-request {
  margin-bottom: 20px;
}

.special-request label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.special-request textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  min-height: 80px;
}

.price-summary {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.price-item.total {
  font-weight: bold;
  font-size: 16px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #ddd;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background-color: #ff6a00;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: #ff8c00;
}
</style>