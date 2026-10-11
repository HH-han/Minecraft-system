<template>
  <div class="chat-input-area">
    <div class="input-box">
      <textarea 
        v-model="inputMessage" 
        placeholder="输入消息..."
        class="message-input"
        rows="3"
        @keydown.enter.exact.prevent="handleSend"
      ></textarea>
      <div class="input-toolbar">
        <div class="tool-left">
          <button class="tool-btn" title="表情" @click="toggleEmojiPanel">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8"
              stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10" />
              <path d="M8 14s1.5 2 4 2 4-2 4-2" />
              <line x1="9" y1="9" x2="9.01" y2="9" />
              <line x1="15" y1="9" x2="15.01" y2="9" />
            </svg>
          </button>
          <button class="tool-btn" title="图片" @click="selectImage">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8"
              stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
              <circle cx="8.5" cy="8.5" r="1.5" />
              <path d="M21 15l-5-5L5 21" />
            </svg>
          </button>
          <button class="tool-btn" title="文件" @click="selectFile">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" />
            </svg>
          </button>
          <button class="tool-btn" title="语音输入" @click="onVoiceInput">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8"
              stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z" />
              <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
              <line x1="12" y1="19" x2="12" y2="23" />
              <line x1="8" y1="23" x2="16" y2="23" />
            </svg>
          </button>
        </div>
        <button 
          :disabled="!canSend"
          class="send-btn"
          @click="handleSend"
        >
          发送
        </button>
      </div>
    </div>
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
import { ElMessage } from 'element-plus'
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

const onVoiceInput = () => {
  ElMessage.info('语音消息功能开发中')
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
  padding: 10px 16px 14px;
  background: #ffffff;
  border-top: 1px solid #ededed;
  position: relative;
  flex-shrink: 0;
  font-family: Inter, "PingFang SC", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.input-box {
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  background: #ffffff;
  overflow: hidden;
  transition: border-color 0.2s ease;
}

.input-box:focus-within {
  border-color: #c8c8cc;
}

.input-box .message-input {
  width: 100%;
  min-height: 76px;
  max-height: 160px;
  padding: 10px 12px 2px;
  border: none;
  background: transparent;
  outline: none;
  box-shadow: none;
  resize: none;
  overflow-y: auto;
  font-size: 14px;
  line-height: 1.5;
  color: #1d1d1f;
  box-sizing: border-box;
  font-family: inherit;
}

.input-box .message-input:focus {
  border: none;
  box-shadow: none;
}

.input-box .message-input::placeholder {
  color: #b0b0b5;
}

.input-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 8px 8px;
}

.tool-left {
  display: flex;
  align-items: center;
  gap: 2px;
}

.tool-btn {
  width: 30px;
  height: 30px;
  border: none;
  background: transparent;
  border-radius: 6px;
  color: #6a6a6f;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: background 0.15s ease, color 0.15s ease;
}

.tool-btn:hover {
  background: #f0f0f2;
  color: #1d1d1f;
}

.send-btn {
  height: 30px;
  padding: 0 16px;
  border: none;
  border-radius: 6px;
  font-size: 13.5px;
  color: #ffffff;
  background: #2997ff;
  cursor: pointer;
  flex-shrink: 0;
  transition: background 0.15s ease;
}

.send-btn:hover:not(:disabled) {
  background: #2997ff;
}

.send-btn:disabled {
  background: #f0f0f2;
  color: #b8b8bd;
  cursor: not-allowed;
}

.hidden-input {
  display: none;
}
</style>
