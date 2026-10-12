<template>
  <div class="modal-overlay" v-if="visible" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">{{ isCreator ? '编辑群资料' : '设置群备注' }}</h3>
        <button class="close-btn" @click="handleClose">
          <Icon name="close" :size="'18px'" />
        </button>
      </div>
      
      <div class="modal-body">
        <div v-if="isCreator" class="form-group">
          <label class="form-label">群头像</label>
          <div class="avatar-upload">
            <img 
              :src="group.avatar || defaultAvatar" 
              :alt="group.name"
              class="preview-avatar"
            />
            <input 
              id="avatar-upload"
              type="file" 
              accept="image/*" 
              class="avatar-input"
              @change="handleAvatarChange"
            />
            <label class="upload-btn" for="avatar-upload">
              <Icon name="upload" :size="'16px'" />
              <span>更换头像</span>
            </label>
          </div>
        </div>

        <div v-if="isCreator" class="form-group">
          <label class="form-label">群名称</label>
          <input 
            type="text" 
            v-model="editForm.name" 
            class="form-input"
            placeholder="请输入群名称"
          />
        </div>

        <div v-if="isCreator" class="form-group">
          <label class="form-label">群描述</label>
          <textarea 
            v-model="editForm.description" 
            class="form-textarea"
            placeholder="请输入群描述"
            rows="3"
          ></textarea>
        </div>

        <div v-if="isCreator" class="form-group">
          <label class="form-label">最大群人数</label>
          <input 
            type="number" 
            v-model="editForm.maxMembers" 
            class="form-input"
            placeholder="请输入最大群人数"
            min="1"
            max="500"
          />
        </div>

        <div class="form-group">
          <label class="form-label">群备注</label>
          <input 
            type="text" 
            v-model="editForm.remark" 
            class="form-input"
            placeholder="请输入群备注（仅自己可见）"
          />
        </div>
      </div>
      
      <div class="modal-footer">
        <button class="btn btn-cancel" @click="handleClose">取消</button>
        <button 
          class="btn btn-confirm" 
          @click="handleConfirm"
        >
          保存
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  group: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'confirm'])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'
const avatarFile = ref(null)

const isCreator = computed(() => {
  return props.group?.isCreator || false
})

const editForm = reactive({
  name: '',
  description: '',
  maxMembers: 200,
  remark: ''
})

watch(() => props.visible, (val) => {
  if (val && props.group) {
    editForm.name = props.group.name || ''
    editForm.description = props.group.description || ''
    editForm.maxMembers = props.group.maxMembers || 200
    editForm.remark = props.group.remark || ''
    avatarFile.value = null
  }
})

const handleAvatarChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    avatarFile.value = file
    const reader = new FileReader()
    reader.onload = (event) => {
      props.group.avatar = event.target.result
    }
    reader.readAsDataURL(file)
  }
}

const handleClose = () => {
  avatarFile.value = null
  emit('update:visible', false)
}

const handleConfirm = async () => {
  const data = {
    id: props.group.id,
    name: editForm.name,
    description: editForm.description,
    maxMembers: editForm.maxMembers,
    remark: editForm.remark,
    avatar: avatarFile.value
  }
  emit('confirm', data)
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
  background: var(--theme-overlay);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 480px;
  max-height: 600px;
  background: var(--theme-bg-elevated);
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: var(--theme-shadow-lg);
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--theme-border);
}

.modal-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--theme-text-1);
  margin: 0;
  letter-spacing: -0.01em;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s ease;
}

.close-btn:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.modal-body::-webkit-scrollbar {
  display: none;
}

.form-group {
  margin-bottom: 24px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 14px;
  color: var(--theme-text-1);
  margin-bottom: 10px;
  font-weight: 500;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--theme-border);
  border-radius: 12px;
  font-size: 14px;
  color: var(--theme-text-1);
  outline: none;
  box-sizing: border-box;
  transition: 0.2s ease;
  background: var(--theme-bg-surface);
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: var(--theme-text-3);
}

.form-input:focus,
.form-textarea:focus {
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.form-textarea {
  resize: none;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.preview-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-input {
  display: none;
}

.upload-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: 0.2s ease;
}

.upload-btn:hover {
  background: var(--theme-bg-hover);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px 24px;
  border-top: 1px solid var(--theme-border);
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
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  border-radius: 12px;
}

.btn-cancel:hover {
  background: var(--theme-bg-hover);
}

.btn-confirm {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  border-radius: 40px;
}

.btn-confirm:hover {
  background: var(--theme-primary-hover);
}
</style>