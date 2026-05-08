<template>
  <div v-if="visible" class="modal-overlay" @click.self="onClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3>添加好友</h3>
        <button class="close-btn" @click="onClose">
          <i class="el-icon-close"></i>
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
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.close-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  color: #666;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid #e0e0e0;
  gap: 12px;
}

.btn {
  padding: 8px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.btn-secondary {
  background: #f5f5f5;
  color: #666;
}

.btn-primary {
  background: #409eff;
  color: #fff;
}
</style>