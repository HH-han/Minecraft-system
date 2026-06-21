<template>
  <div v-if="visible" class="modal-overlay" @click.self="onClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3>添加好友</h3>
        <button class="close-btn" @click="onClose">
          <Icon name="close" :size="'14px'" />
        </button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label>手机号</label>
          <input 
            v-model="phone" 
            type="text" 
            placeholder="输入好友手机号"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <label>备注</label>
          <input 
            v-model="message" 
            type="text" 
            placeholder="添加备注（可选）"
            class="form-input"
          />
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="onClose">取消</button>
        <button class="btn btn-primary" @click="onSubmit">发送请求</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'submit'])

const phone = ref('')
const message = ref('')

watch(() => props.visible, (newVal) => {
  if (!newVal) {
    phone.value = ''
    message.value = ''
  }
})

const onClose = () => {
  emit('update:visible', false)
}

const onSubmit = () => {
  if (!phone.value.trim()) {
    return
  }
  emit('submit', {
    phone: phone.value,
    message: message.value
  })
  onClose()
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 400px;
  background: #ffffff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08), 0 8px 24px rgba(0, 0, 0, 0.04);
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #d2d2d6;
}

.modal-header h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.01em;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f7;
  border-radius: 50%;
  color: #6e6e73;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s ease;
}

.close-btn:hover {
  background: #e8e8ed;
  color: #1d1d1f;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #1d1d1f;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  height: 44px;
  padding: 0 14px;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  font-size: 14px;
  color: #1d1d1f;
  background: #ffffff;
  outline: none;
  box-sizing: border-box;
  transition: 0.2s ease;
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.form-input::placeholder {
  color: #6e6e73;
}

.form-input:focus {
  border-color: #2997ff;
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.15);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px 24px;
  gap: 12px;
}

.btn {
  padding: 10px 24px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: 0.2s ease;
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.btn-secondary {
  background: #f5f5f7;
  color: #1d1d1f;
  border-radius: 12px;
}

.btn-secondary:hover {
  background: #e8e8ed;
}

.btn-primary {
  background: #2997ff;
  color: #ffffff;
  border-radius: 40px;
}

.btn-primary:hover {
  background: #0066cc;
}
</style>