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
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 480px;
  max-height: 600px;
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
  color: #1d1d1f;
  margin-bottom: 10px;
  font-weight: 500;
}

.form-input,
.form-textarea {
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

.form-input::placeholder,
.form-textarea::placeholder {
  color: #6e6e73;
}

.form-input:focus,
.form-textarea:focus {
  border-color: #2997ff;
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.15);
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
  background: #f5f5f7;
  color: #1d1d1f;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: 0.2s ease;
}

.upload-btn:hover {
  background: #e8e8ed;
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

.btn-confirm:hover {
  background: #0066cc;
}
</style>