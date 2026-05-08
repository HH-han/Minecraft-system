<template>
  <div class="chat-input-area">
    <div class="input-tools">
      <button class="tool-btn" @click="onEmoji">
        <i class="el-icon-smile"></i>
      </button>
      <button class="tool-btn" @click="onImage">
        <i class="el-icon-picture"></i>
      </button>
      <button class="tool-btn" @click="onFile">
        <i class="el-icon-paperclip"></i>
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
      :disabled="!inputMessage.trim()"
      class="send-btn"
      @click="handleSend"
    >
      <i class="el-icon-send"></i>
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['send', 'emoji', 'image', 'file'])

const inputMessage = ref('')

const handleSend = () => {
  if (inputMessage.value.trim()) {
    emit('send', inputMessage.value)
    inputMessage.value = ''
  }
}

const onEmoji = () => {
  emit('emoji')
}

const onImage = () => {
  emit('image')
}

const onFile = () => {
  emit('file')
}
</script>

<style scoped>
.chat-input-area {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-top: 1px solid #e0e0e0;
  gap: 12px;
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
}

.send-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}
</style>