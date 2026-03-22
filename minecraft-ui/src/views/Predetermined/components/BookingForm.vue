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
  background: rgba(255, 255, 255, 0.3);
  padding: 25px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.booking-form-component h4 {
  margin-bottom: 25px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

.special-request {
  margin-bottom: 25px;
}

.special-request label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.special-request textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  min-height: 100px;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.special-request textarea:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

.price-summary {
  margin-bottom: 25px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: #333;
}

.price-item.total {
  font-weight: bold;
  font-size: 18px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  color: #ff6a00;
}

.submit-btn {
  width: 100%;
  padding: 16px;
  background-color: #ff6a00;
  color: white;
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