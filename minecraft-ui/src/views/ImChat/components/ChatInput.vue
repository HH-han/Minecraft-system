<template>
  <div class="chat-input-area">
    <div class="input-tools">
      <button class="tool-btn" @click="toggleEmojiPanel">
        <Icon name="smile" :size="'18px'" />
      </button>
      <button class="tool-btn" @click="selectImage">
        <Icon name="picture" :size="'18px'" />
      </button>
      <button class="tool-btn" @click="selectFile">
        <Icon name="paperclip" :size="'18px'" />
      </button>
    </div>
    <input 
      v-model="inputMessage" 
      type="text" 
      placeholder="输入消息..."
      class="message-input"
      @keyup.enter="handleSend"
    />
    <button 
      :disabled="!canSend"
      class="send-btn"
      @click="handleSend"
    >
      <Icon name="send" :size="'16px'" />
    </button>
    
    <input 
      ref="imageInputRef"
      type="file" 
      accept="image/*" 
      class="hidden-input"
      @change="handleImageSelect"
    />
    <input 
      ref="fileInputRef"
      type="file" 
      class="hidden-input"
      @change="handleFileSelect"
    />
    
    <EmojiPanel 
      v-if="showEmojiPanel" 
      @select-emoji="handleEmojiSelect" 
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Icon from './Icon.vue'
import EmojiPanel from './EmojiPanel.vue'
import { uploadFile } from '@/api/upload'

const emit = defineEmits(['send', 'image', 'file'])

const inputMessage = ref('')
const showEmojiPanel = ref(false)
const selectedEmoji = ref(null)

const imageInputRef = ref(null)
const fileInputRef = ref(null)

const canSend = computed(() => {
  return inputMessage.value.trim() !== '' || selectedEmoji.value
})

const toggleEmojiPanel = () => {
  showEmojiPanel.value = !showEmojiPanel.value
}

const handleEmojiSelect = (emoji) => {
  selectedEmoji.value = emoji
  inputMessage.value = emoji.content
}

const selectImage = () => {
  imageInputRef.value?.click()
}

const handleImageSelect = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  
  try {
    const response = await uploadFile(file)
    if (response.code === 200) {
      emit('send', {
        content: response.data,
        messageType: 'IMAGE'
      })
    }
  } catch (error) {
    console.error('上传图片失败:', error)
  }
  
  event.target.value = ''
}

const selectFile = () => {
  fileInputRef.value?.click()
}

const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  
  emit('file', file)
  event.target.value = ''
}

const handleSend = () => {
  if (!canSend.value) return
  
  let messageType = 'TEXT'
  let content = inputMessage.value.trim()
  
  if (selectedEmoji.value) {
    if (selectedEmoji.value.type === 'system') {
      messageType = 'EMOJI'
      content = selectedEmoji.value.content
    } else if (selectedEmoji.value.type === 'custom') {
      messageType = 'EMOJI'
      content = selectedEmoji.value.emojiUrl
    }
  }
  
  emit('send', {
    content,
    messageType
  })
  
  inputMessage.value = ''
  selectedEmoji.value = null
  showEmojiPanel.value = false
}
</script>

<style scoped>
.chat-input-area {
  display: flex;
  align-items: flex-end;
  padding: 12px 20px;
  background: #fff;
  border-top: 1px solid #e0e0e0;
  gap: 12px;
  position: relative;
}

.input-tools {
  display: flex;
  gap: 8px;
}

.tool-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  color: #666;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.tool-btn:hover {
  background: #e8e8e8;
}

.message-input {
  flex: 1;
  height: 40px;
  padding: 0 16px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
}

.send-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #409eff;
  border-radius: 50%;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.send-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.hidden-input {
  display: none;
}
</style>