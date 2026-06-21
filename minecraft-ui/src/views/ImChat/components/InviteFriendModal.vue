<template>
  <div class="modal-overlay" v-if="visible" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h3 class="modal-title">邀请好友加入群聊</h3>
        <button class="close-btn" @click="handleClose">
          <Icon name="close" :size="'18px'" />
        </button>
      </div>
      
      <div class="modal-body">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="搜索好友..."
            class="search-input"
          />
        </div>
        
        <div class="friend-list">
          <div 
            v-for="friend in filteredFriends" 
            :key="friend.id"
            :class="['friend-item', { selected: isSelected(friend.id) }]"
            @click="toggleFriend(friend)"
          >
            <img 
              :src="friend.avatar || defaultAvatar" 
              :alt="friend.name"
              class="friend-avatar"
            />
            <div class="friend-info">
              <h4 class="friend-name">{{ friend.name }}</h4>
              <p class="friend-status">{{ friend.online ? '在线' : '离线' }}</p>
            </div>
            <div :class="['checkbox', { checked: isSelected(friend.id) }]">
              <Icon v-if="isSelected(friend.id)" name="check" :size="'14px'" />
            </div>
          </div>
          
          <div v-if="filteredFriends.length === 0" class="empty-state">
            <Icon name="user" :size="'48px'" />
            <p>暂无好友</p>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button class="btn btn-cancel" @click="handleClose">取消</button>
        <button 
          class="btn btn-confirm" 
          :disabled="selectedFriends.length === 0"
          @click="handleConfirm"
        >
          邀请 ({{ selectedFriends.length }})
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  friends: {
    type: Array,
    default: () => []
  },
  groupId: {
    type: [Number, String],
    default: null
  },
  groupName: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:visible', 'confirm'])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'
const searchKeyword = ref('')
const selectedFriendIds = ref([])

const filteredFriends = computed(() => {
  let result = props.friends
  if (searchKeyword.value) {
    result = result.filter(f => f.name.includes(searchKeyword.value))
  }
  return result
})

const selectedFriends = computed(() => {
  return props.friends.filter(f => selectedFriendIds.value.includes(f.id))
})

const isSelected = (id) => {
  return selectedFriendIds.value.includes(id)
}

const toggleFriend = (friend) => {
  const index = selectedFriendIds.value.indexOf(friend.id)
  if (index === -1) {
    selectedFriendIds.value.push(friend.id)
  } else {
    selectedFriendIds.value.splice(index, 1)
  }
}

const handleClose = () => {
  selectedFriendIds.value = []
  searchKeyword.value = ''
  emit('update:visible', false)
}

const handleConfirm = () => {
  if (selectedFriendIds.value.length === 0) return
  
  emit('confirm', {
    groupId: props.groupId,
    groupName: props.groupName,
    friendIds: selectedFriendIds.value,
    friends: selectedFriends.value
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
  padding: 20px 24px;
}

.search-box {
  margin-bottom: 16px;
}

.search-input {
  width: 100%;
  height: 40px;
  padding: 0 14px;
  background: #f5f5f7;
  border: 1px solid transparent;
  border-radius: 12px;
  font-size: 14px;
  color: #1d1d1f;
  outline: none;
  box-sizing: border-box;
  transition: 0.2s ease;
  font-family: Inter, 'PingFang SC', -apple-system, sans-serif;
}

.search-input::placeholder {
  color: #6e6e73;
}

.search-input:focus {
  background: #ffffff;
  border-color: #2997ff;
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.15);
}

.friend-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.friend-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: 0.2s ease;
}

.friend-item:hover {
  background: #f5f5f7;
}

.friend-item.selected {
  background: rgba(41, 151, 255, 0.1);
}

.friend-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.friend-info {
  flex: 1;
  min-width: 0;
}

.friend-name {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  margin: 0 0 4px 0;
}

.friend-status {
  font-size: 12px;
  color: #6e6e73;
  margin: 0;
}

.checkbox {
  width: 24px;
  height: 24px;
  border: 2px solid #d2d2d6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s ease;
  color: transparent;
}

.checkbox.checked {
  background: #2997ff;
  border-color: #2997ff;
  color: #ffffff;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #6e6e73;
}

.empty-state .svg-icon {
  margin-bottom: 12px;
  opacity: 0.3;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
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
