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
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e6e6e6;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: #666;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f5f5f5;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.friend-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.friend-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.friend-name {
  font-size: 14px;
  color: #333;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: #11b860;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e6e6e6;
}

.btn {
  padding: 8px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e6e6e6;
}

.btn-confirm {
  background: #11b860;
  color: #fff;
}

.btn-confirm:hover:not(:disabled) {
  background: #0da255;
}

.btn-confirm:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>