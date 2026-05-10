<template>
  <div v-if="visible" class="modal-overlay" @click.self="onClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3>创建群组</h3>
        <button class="close-btn" @click="onClose">
          <Icon name="close" :size="'14px'" />
        </button>
      </div>
      <div class="modal-body">
        <div class="form-item">
          <label>群组名称</label>
          <input
            v-model="groupName"
            type="text"
            placeholder="请输入群组名称"
            class="form-input"
          />
        </div>
        <div class="form-item">
          <label>群组描述</label>
          <textarea
            v-model="groupDescription"
            placeholder="请输入群组描述（可选）"
            class="form-textarea"
            rows="3"
          />
        </div>
        <div class="form-item">
          <label>选择成员</label>
          <div class="friend-list">
            <div
              v-for="friend in friends"
              :key="friend.id"
              :class="['friend-item', { selected: selectedFriends.includes(friend.id) }]"
              @click="toggleFriend(friend.id)"
            >
              <img
                :src="friend.avatar || defaultAvatar"
                :alt="friend.name"
                class="friend-avatar"
              />
              <span class="friend-name">{{ friend.name }}</span>
              <span v-if="selectedFriends.includes(friend.id)" class="check-icon">
                <Icon name="check" :size="'16px'" />
              </span>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-cancel" @click="onClose">取消</button>
        <button
          class="btn btn-confirm"
          @click="onCreate"
          :disabled="!groupName.trim() || selectedFriends.length === 0"
        >
          创建
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Icon from './Icon.vue'
import { createGroupWithMembers } from '@/api/chat'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  friends: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:visible', 'group-created'])

const authStore = useAuthStore()
const groupName = ref('')
const groupDescription = ref('')
const selectedFriends = ref([])
const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const toggleFriend = (friendId) => {
  const index = selectedFriends.value.indexOf(friendId)
  if (index > -1) {
    selectedFriends.value.splice(index, 1)
  } else {
    selectedFriends.value.push(friendId)
  }
}

const onCreate = async () => {
  if (!groupName.value.trim()) {
    ElMessage.warning('请输入群组名称')
    return
  }
  if (selectedFriends.value.length === 0) {
    ElMessage.warning('请至少选择一个成员')
    return
  }

  try {
    const userId = authStore.userInfo?.id
    const groupData = {
      name: groupName.value.trim(),
      description: groupDescription.value.trim() || null,
      creatorId: userId,
      memberIds: [...selectedFriends.value, userId]
    }

    const response = await createGroupWithMembers(groupData)
    if (response.code === 200) {
      ElMessage.success('群组创建成功')
      emit('group-created', response.data)
      onClose()
    }
  } catch (error) {
    console.error('创建群组失败:', error)
    ElMessage.error('创建群组失败')
  }
}

const onClose = () => {
  groupName.value = ''
  groupDescription.value = ''
  selectedFriends.value = []
  emit('update:visible', false)
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
  width: 480px;
  max-height: 80vh;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-input {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  border-color: #409eff;
}

.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  resize: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-textarea:focus {
  border-color: #409eff;
}

.friend-list {
  max-height: 240px;
  overflow-y: auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 8px;
}

.friend-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
}

.friend-item:hover {
  background: #f5f5f5;
}

.friend-item.selected {
  background: #e8f4fd;
}

.friend-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.friend-name {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.check-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e0e0e0;
}

.btn {
  padding: 10px 24px;
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
  background: #e0e0e0;
}

.btn-confirm {
  background: #409eff;
  color: #fff;
}

.btn-confirm:hover:not(:disabled) {
  background: #66b1ff;
}

.btn-confirm:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
