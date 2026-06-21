<template>
  <div class="modal-overlay" v-if="visible" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">设置好友备注</h3>
        <button class="close-btn" @click="handleClose">
          <Icon name="close" :size="'18px'" />
        </button>
      </div>
      
      <div class="modal-body">
        <div class="form-group">
          <label class="form-label">好友名称</label>
          <div class="friend-info">
            <img 
              :src="friend.avatar || defaultAvatar" 
              :alt="friend.name"
              class="friend-avatar"
            />
            <span class="friend-name">{{ friend.name }}</span>
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label">备注名称</label>
          <input 
            v-model="remark" 
            type="text" 
            class="form-input"
            placeholder="请输入备注名称（仅自己可见）"
            @keyup.enter="handleConfirm"
          />
        </div>
      </div>
      
      <div class="modal-footer">
        <button class="btn btn-cancel" @click="handleClose">取消</button>
        <button 
          class="btn btn-confirm" 
          @click="handleConfirm"
          :disabled="!remark.trim()"
        >
          保存
        </button>
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
  },
  friend: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'confirm'])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'
const remark = ref('')

watch(() => props.visible, (val) => {
  if (val && props.friend) {
    remark.value = props.friend.remark || props.friend.name || ''
  }
})

const handleClose = () => {
  remark.value = ''
  emit('update:visible', false)
}

const handleConfirm = () => {
  if (!remark.value.trim()) return
  
  emit('confirm', {
    friendId: props.friend.id,
    remark: remark.value.trim()
  })
  handleClose()
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
  display: flex;
  flex-direction: column;
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

.modal-title {
  font-size: 17px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
  letter-spacing: -0.01em;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f7;
  color: #6e6e73;
  cursor: pointer;
  border-radius: 50%;
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

.form-label {
  display: block;
  font-size: 14px;
  color: #1d1d1f;
  margin-bottom: 8px;
  font-weight: 500;
}

.friend-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
}

.friend-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.friend-name {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  font-size: 14px;
  color: #1d1d1f;
  outline: none;
  box-sizing: border-box;
  transition: 0.2s ease;
  background: #ffffff;
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
  gap: 12px;
  padding: 16px 24px 24px;
  border-top: 1px solid #d2d2d6;
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

.btn-cancel {
  background: #f5f5f7;
  color: #1d1d1f;
  border-radius: 12px;
}

.btn-cancel:hover {
  background: #e8e8ed;
}

.btn-confirm {
  background: #2997ff;
  color: #ffffff;
  border-radius: 40px;
}

.btn-confirm:hover:not(:disabled) {
  background: #0066cc;
}

.btn-confirm:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>