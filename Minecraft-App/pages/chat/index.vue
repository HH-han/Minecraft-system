<template>
  <div class="chat-container">
    <div class="chat-header">
      <h1>在线客服</h1>
    </div>
    
    <div class="chat-messages">
      <div class="message-item bot">
        <div class="message-avatar">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=customer%20service%20robot%20avatar&image_size=square" alt="客服头像">
        </div>
        <div class="message-content">
          <p>您好！欢迎使用在线客服，有什么可以帮助您的吗？</p>
          <span class="message-time">10:00</span>
        </div>
      </div>
      
      <div class="message-item user">
        <div class="message-content">
          <p>我想了解一下景点门票的预订流程</p>
          <span class="message-time">10:01</span>
        </div>
        <div class="message-avatar">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square" alt="用户头像">
        </div>
      </div>
      
      <div class="message-item bot">
        <div class="message-avatar">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=customer%20service%20robot%20avatar&image_size=square" alt="客服头像">
        </div>
        <div class="message-content">
          <p>您好，预订景点门票的流程如下：</p>
          <p>1. 在首页选择"景点"进入景点列表</p>
          <p>2. 选择您感兴趣的景点，点击进入详情页</p>
          <p>3. 在详情页点击"立即预订"按钮</p>
          <p>4. 选择日期和人数，然后点击"提交订单"</p>
          <p>5. 完成支付后，您将收到电子门票</p>
          <span class="message-time">10:02</span>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <input type="text" v-model="message" placeholder="请输入您的问题..." class="input-field">
      <button @click="sendMessage" class="send-btn">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const message = ref('')

// 发送消息
const sendMessage = () => {
  if (message.value.trim()) {
    // 发送消息逻辑
    console.log('发送消息:', message.value)
    message.value = ''
    
    // 模拟客服回复
    setTimeout(() => {
      // 这里可以添加客服回复的逻辑
    }, 1000)
  }
}
</script>

<style scoped>
.chat-container {
  min-height: 100vh;
  background-color: var(--background-color);
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  background-color: var(--card-background);
}

.chat-header h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  text-align: center;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  max-width: 80%;
}

.message-item.bot {
  align-self: flex-start;
  flex-direction: row;
}

.message-item.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 12px;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  background-color: var(--card-background);
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}

.message-item.bot .message-content {
  border-bottom-left-radius: 4px;
}

.message-item.user .message-content {
  background-color: var(--primary-color);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-content p {
  margin: 0 0 8px 0;
  font-size: 14px;
  line-height: 1.5;
}

.message-content p:last-child {
  margin-bottom: 0;
}

.message-time {
  font-size: 12px;
  color: var(--text-secondary-color);
  position: absolute;
  bottom: -20px;
  right: 0;
}

.message-item.user .message-time {
  color: rgba(255, 255, 255, 0.6);
}

.chat-input {
  padding: 20px;
  border-top: 1px solid var(--border-color);
  background-color: var(--card-background);
  display: flex;
  gap: 12px;
}

.input-field {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
}

.input-field:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(30, 136, 229, 0.1);
}

.send-btn {
  padding: 12px 24px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.send-btn:hover {
  background-color: var(--primary-dark);
}

.send-btn:disabled {
  background-color: var(--text-secondary-color);
  cursor: not-allowed;
}
</style>