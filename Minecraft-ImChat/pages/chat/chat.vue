<template>
  <view class="chat-page">
    <view class="nav-bar">
      <view class="nav-back" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <view class="nav-title">
        <text class="title-text">{{ chatName }}</text>
      </view>
      <view class="nav-more" @click="showMore">
        <text class="more-icon">···</text>
      </view>
    </view>

    <scroll-view
      class="message-list"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-with-animation="true"
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="loadMoreMessages"
      @scrolltoupper="loadMoreMessages"
    >
      <view v-if="isLoading" class="loading-tip">
        <text>加载中...</text>
      </view>

      <view
        v-for="(msg, index) in messages"
        :key="msg.id || index"
        :class="['message-item', { mine: msg.senderId === currentUserId }]"
      >
        <view v-if="showTimeDivider(index)" class="time-divider">
          <text class="time-text">{{ formatTime(msg.createTime) }}</text>
        </view>

        <view class="message-row">
          <image
            v-if="msg.senderId !== currentUserId"
            class="message-avatar"
            :src="msg.senderAvatar || getSenderAvatar(msg)"
            mode="aspectFill"
          />

          <view
            :class="[
              'message-bubble',
              { mine: msg.senderId === currentUserId },
              { image: msg.messageType === 'image' || msg.messageType === 'IMAGE' }
            ]"
          >
            <text v-if="msg.messageType === 'text' || msg.messageType === 'TEXT'" class="message-text">
              {{ msg.content }}
            </text>

            <image
              v-else-if="msg.messageType === 'image' || msg.messageType === 'IMAGE'"
              class="message-image"
              :src="msg.content"
              mode="widthFix"
              @click="previewImage(msg.content)"
            />

            <text v-else class="message-text">
              {{ msg.content }}
            </text>

            <view class="message-status">
              <text v-if="msg.senderId === currentUserId && msg.status === 'sending'" class="status-icon">⏳</text>
              <text v-else-if="msg.senderId === currentUserId && msg.status === 'sent'" class="status-icon">✓</text>
              <text v-else-if="msg.senderId === currentUserId && msg.status === 'read'" class="status-icon">✓✓</text>
              <text v-else-if="msg.senderId === currentUserId && msg.status === 'failed'" class="status-icon">✕</text>
            </view>
          </view>

          <image
            v-if="msg.senderId === currentUserId"
            class="message-avatar"
            :src="currentUserAvatar || defaultAvatar"
            mode="aspectFill"
          />
        </view>
      </view>

      <view v-if="messages.length === 0 && !isLoading" class="empty-state">
        <text class="empty-icon">💬</text>
        <text class="empty-text">还没有消息，开始聊天吧</text>
      </view>
    </scroll-view>

    <view class="input-area">
      <view class="tools-bar">
        <view class="tool-btn" @click="toggleEmoji">
          <text class="tool-icon">😀</text>
        </view>
        <view class="tool-btn" @click="selectImage">
          <text class="tool-icon">🖼️</text>
        </view>
        <view class="tool-btn" @click="showMoreTools">
          <text class="tool-icon">➕</text>
        </view>
      </view>

      <view v-if="showEmojiPanel" class="emoji-panel slide-up">
        <scroll-view scroll-y class="emoji-scroll">
          <view class="emoji-grid">
            <view
              v-for="(emoji, index) in emojiList"
              :key="index"
              class="emoji-item"
              @click="selectEmoji(emoji)"
            >
              <text>{{ emoji }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <view class="input-row">
        <input
          class="message-input"
          v-model="inputMessage"
          :placeholder="inputPlaceholder"
          confirm-type="send"
          @confirm="sendMessage"
          @focus="onInputFocus"
          @blur="onInputBlur"
        />
        <view
          :class="['send-btn', { active: canSend }]"
          @click="sendMessage"
        >
          <text class="send-text">发送</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {
  sendSingleMessage,
  sendGroupMessage,
  getSingleChatHistory,
  getGroupChatHistory,
  markAsRead,
  uploadFile
} from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'
import wsService from '../../utils/websocket.js'

export default {
  data() {
    return {
      chatType: 'single',
      chatId: '',
      chatName: '',
      chatAvatar: '',

      messages: [],
      inputMessage: '',
      isLoading: false,
      isRefreshing: false,
      scrollTop: 0,

      currentUserId: null,
      currentUserAvatar: '',
      defaultAvatar: '/static/default-avatar.png',

      showEmojiPanel: false,
      emojiList: [
        '😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣',
        '😊', '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰',
        '😘', '😗', '😙', '😚', '😋', '😛', '😜', '🤪',
        '😝', '🤗', '🤭', '🤫', '🤔', '🤐', '🤨', '😐',
        '😑', '😶', '😏', '😒', '🙄', '😬', '🤥', '😔',
        '😪', '🤤', '😴', '😷', '🤒', '🤕', '🤢', '🤮',
        '🤧', '🥵', '🥶', '🥴', '😵', '🤯', '🤠', '🥳',
        '🥸', '😎', '🤓', '🧐', '😕', '😟', '🙁', '☹️',
        '😮', '😯', '😲', '😳', '🥺', '😦', '😧', '😨',
        '😰', '😥', '😢', '😭', '😱', '😖', '😣', '😞',
        '😓', '😩', '😫', '🥱', '😤', '😡', '😠', '🤬',
        '😈', '👿', '💀', '☠️', '💩', '🤡', '👹', '👺',
        '👻', '👽', '👾', '🤖', '🎃', '😺', '😸', '😹',
        '😻', '😼', '😽', '🙀', '😿', '😾', '💋', '🤝',
        '👍', '👎', '👊', '✊', '🤛', '🤜', '🤞', '✌️',
        '🤟', '🤘', '🤙', '👋', '🤚', '🖐️', '🖖', '👌',
        '👍', '👎', '👊', '✊', '🤛', '🤜', '🤞', '✌️',
        '❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍',
        '🤎', '💔', '❣️', '💕', '💞', '💓', '💗', '💖',
        '💘', '💝', '💟', '💢', '💥', '💫', '💦', '💨',
        '🍎', '🍊', '🍋', '🍌', '🍉', '🍇', '🍓', '🍑',
        '🍒', '🥝', '🍅', '🥑', '🥕', '🌽', '🌶️', '🥒',
        '🍔', '🍕', '🌭', '🍿', '🍦', '🍩', '🍪', '🎂'
      ],

      inputPlaceholder: '输入消息...'
    }
  },

  computed: {
    canSend() {
      return this.inputMessage.trim().length > 0
    }
  },

  onLoad(options) {
    this.chatType = options.type || 'single'
    this.chatId = options.id
    this.chatName = decodeURIComponent(options.name || '聊天')
    this.chatAvatar = decodeURIComponent(options.avatar || '')

    uni.setNavigationBarTitle({ title: this.chatName })

    this.loadUserInfo()
    this.initWebSocket()
    this.loadMessages()
  },

  onUnload() {
    wsService.off('private-message', this.handlePrivateMessage)
    wsService.off('group-message', this.handleGroupMessage)

    if (this.chatType === 'single' && this.currentUserId) {
      markAsRead(this.chatId).catch(console.error)
    }
  },

  methods: {
    loadUserInfo() {
      const userInfo = getUserInfo()
      if (userInfo) {
        this.currentUserId = userInfo.id
        this.currentUserAvatar = userInfo.avatar || ''
      }
    },

    initWebSocket() {
      wsService.connect(this.currentUserId)

      wsService.on('private-message', this.handlePrivateMessage)
      wsService.on('group-message', this.handleGroupMessage)
    },

    handlePrivateMessage(data) {
      if (this.chatType !== 'single') return
      if (data.senderId !== parseInt(this.chatId)) return

      this.messages.push({
        id: data.timestamp || Date.now(),
        senderId: data.senderId,
        content: data.content,
        messageType: data.messageType || 'text',
        createTime: new Date().toISOString(),
        status: 'read'
      })

      this.scrollToBottom()
    },

    handleGroupMessage(data) {
      if (this.chatType !== 'group') return
      if (data.groupId !== parseInt(this.chatId)) return

      this.messages.push({
        id: data.timestamp || Date.now(),
        senderId: data.senderId,
        senderName: data.senderName,
        content: data.content,
        messageType: data.messageType || 'text',
        createTime: new Date().toISOString(),
        status: 'read'
      })

      this.scrollToBottom()
    },

    async loadMessages() {
      if (this.isLoading) return
      this.isLoading = true

      try {
        let res
        if (this.chatType === 'single') {
          res = await getSingleChatHistory(this.currentUserId, this.chatId)
        } else {
          res = await getGroupChatHistory(this.chatId)
        }

        if (res.code === 200 && res.data) {
          this.messages = res.data.reverse().map(m => ({
            ...m,
            status: 'read'
          }))
          this.scrollToBottom()
        }
      } catch (e) {
        console.error('加载消息失败:', e)
        uni.showToast({ title: '加载消息失败', icon: 'none' })
      } finally {
        this.isLoading = false
      }
    },

    async loadMoreMessages() {
      this.isRefreshing = true
      await this.loadMessages()
      this.isRefreshing = false
    },

    async sendMessage() {
      if (!this.canSend) return

      const content = this.inputMessage.trim()
      const messageType = 'text'

      this.inputMessage = ''

      const tempMsgId = Date.now()
      this.messages.push({
        id: tempMsgId,
        senderId: this.currentUserId,
        content: content,
        messageType: messageType,
        createTime: new Date().toISOString(),
        status: 'sending'
      })

      this.scrollToBottom()

      try {
        let res
        if (this.chatType === 'single') {
          res = await sendSingleMessage({
            senderId: this.currentUserId,
            receiverId: parseInt(this.chatId),
            content: content,
            messageType: messageType
          })
        } else {
          res = await sendGroupMessage({
            groupId: parseInt(this.chatId),
            senderId: this.currentUserId,
            content: content,
            messageType: messageType
          })
        }

        if (res.code === 200) {
          const msgIndex = this.messages.findIndex(m => m.id === tempMsgId)
          if (msgIndex !== -1) {
            this.messages[msgIndex].id = res.data?.id || tempMsgId
            this.messages[msgIndex].status = 'sent'
          }
        }
      } catch (e) {
        console.error('发送消息失败:', e)
        uni.showToast({ title: '发送失败', icon: 'none' })
        const msgIndex = this.messages.findIndex(m => m.id === tempMsgId)
        if (msgIndex !== -1) {
          this.messages[msgIndex].status = 'failed'
        }
        this.inputMessage = content
      }
    },

    async selectImage() {
      uni.chooseImage({
        count: 1,
        success: async (res) => {
          const tempFilePath = res.tempFilePaths[0]

          try {
            uni.showLoading({ title: '上传中...' })

            const uploadRes = await uploadFile(tempFilePath)

            if (uploadRes.code === 200) {
              const imageUrl = uploadRes.data

              let sendRes
              if (this.chatType === 'single') {
                sendRes = await sendSingleMessage({
                  senderId: this.currentUserId,
                  receiverId: parseInt(this.chatId),
                  content: imageUrl,
                  messageType: 'image'
                })
              } else {
                sendRes = await sendGroupMessage({
                  groupId: parseInt(this.chatId),
                  senderId: this.currentUserId,
                  content: imageUrl,
                  messageType: 'image'
                })
              }

              if (sendRes.code === 200) {
                this.messages.push({
                  id: sendRes.data?.id || Date.now(),
                  senderId: this.currentUserId,
                  content: imageUrl,
                  messageType: 'image',
                  createTime: new Date().toISOString(),
                  status: 'sent'
                })

                this.scrollToBottom()
              }
            }
          } catch (e) {
            console.error('上传图片失败:', e)
            uni.showToast({ title: '上传失败', icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },

    toggleEmoji() {
      this.showEmojiPanel = !this.showEmojiPanel
    },

    selectEmoji(emoji) {
      this.inputMessage += emoji
    },

    onInputFocus() {
      this.showEmojiPanel = false
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    onInputBlur() {
    },

    previewImage(url) {
      uni.previewImage({
        urls: [url],
        current: url
      })
    },

    getSenderAvatar(msg) {
      if (this.chatType === 'group' && msg.senderAvatar) {
        return msg.senderAvatar
      }
      return this.chatAvatar || this.defaultAvatar
    },

    formatTime(timeStr) {
      if (!timeStr) return ''

      const date = new Date(timeStr)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

      return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
    },

    showTimeDivider(index) {
      if (index === 0) return true

      const currentMsg = this.messages[index]
      const prevMsg = this.messages[index - 1]

      const currentTime = new Date(currentMsg.createTime)
      const prevTime = new Date(prevMsg.createTime)

      const diff = currentTime - prevTime
      return diff > 5 * 60 * 1000
    },

    scrollToBottom() {
      this.$nextTick(() => {
        this.scrollTop = 99999
      })
    },

    goBack() {
      uni.navigateBack()
    },

    showMore() {
      const actionList = [
        { text: '清空聊天记录', action: () => this.clearChatHistory() },
        { text: '查看聊天信息', action: () => this.showChatInfo() }
      ]

      uni.showActionSheet({
        itemList: actionList.map(a => a.text),
        success: (res) => {
          actionList[res.tapIndex].action()
        }
      })
    },

    showMoreTools() {
      const actionList = [
        { text: '图片', action: () => this.selectImage() },
        { text: '文件', action: () => uni.showToast({ title: '开发中', icon: 'none' }) },
        { text: '语音通话', action: () => uni.showToast({ title: '开发中', icon: 'none' }) }
      ]

      uni.showActionSheet({
        itemList: actionList.map(a => a.text),
        success: (res) => {
          actionList[res.tapIndex].action()
        }
      })
    },

    clearChatHistory() {
      uni.showModal({
        title: '提示',
        content: '确定要清空聊天记录吗？',
        success: (res) => {
          if (res.confirm) {
            this.messages = []
            uni.showToast({ title: '已清空', icon: 'success' })
          }
        }
      })
    },

    showChatInfo() {
      uni.navigateTo({
        url: `/pages/contact-detail/contact-detail?id=${this.chatId}&type=${this.chatType}&name=${encodeURIComponent(this.chatName)}`
      })
    }
  }
}
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f7;
}

.nav-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  background-color: rgba(255, 255, 255, 0.96);
  backdrop-filter: saturate(180%) blur(20rpx);
  -webkit-backdrop-filter: saturate(180%) blur(20rpx);
  border-bottom: 1rpx solid #e5e5e5;
  padding-top: calc(20rpx + env(safe-area-inset-top));
}

.nav-back {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.nav-back:active {
  background-color: rgba(7, 193, 96, 0.1);
}

.back-icon {
  font-size: 48rpx;
  color: #07C160;
  font-weight: 300;
}

.nav-title {
  flex: 1;
  text-align: center;
}

.title-text {
  font-size: 34rpx;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.4rpx;
}

.nav-more {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.nav-more:active {
  background-color: rgba(0, 0, 0, 0.05);
}

.more-icon {
  font-size: 36rpx;
  color: #6e6e73;
  font-weight: 600;
}

.message-list {
  flex: 1;
}

.loading-tip {
  text-align: center;
  padding: 24rpx;
  color: #6e6e73;
  font-size: 24rpx;
}

.time-divider {
  display: flex;
  justify-content: center;
  padding: 24rpx 0;
}

.time-text {
  font-size: 22rpx;
  color: #6e6e73;
  background-color: rgba(0, 0, 0, 0.05);
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  line-height: 1.4;
}

.message-item {
  margin-bottom: 32rpx;
  padding: 0 32rpx;
}

.message-row {
  display: flex;
  align-items: flex-start;
}

.message-item.mine .message-row {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.message-bubble {
  max-width: 70%;
  margin: 0 16rpx;
  padding: 20rpx 24rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  position: relative;
  font-size: 30rpx;
  line-height: 1.5;
  word-break: break-word;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
}

.message-bubble::before {
  content: '';
  position: absolute;
  top: 20rpx;
  left: -12rpx;
  border: 12rpx solid transparent;
  border-right-color: #ffffff;
}

.message-item.mine .message-bubble {
  background: linear-gradient(135deg, #07C160 0%, #06AD56 100%);
  margin-left: 0;
  margin-right: 16rpx;
}

.message-item.mine .message-bubble::before {
  left: auto;
  right: -12rpx;
  border-right-color: transparent;
  border-left-color: #07C160;
}

.message-text {
  font-size: 30rpx;
  color: #1d1d1f;
  line-height: 1.5;
  word-break: break-word;
}

.message-item.mine .message-text {
  color: #ffffff;
}

.message-bubble.image {
  background-color: transparent !important;
  padding: 0 !important;
  box-shadow: none !important;
}

.message-bubble.image::before {
  display: none;
}

.message-image {
  max-width: 400rpx;
  border-radius: 16rpx;
  display: block;
}

.message-status {
  position: absolute;
  right: 8rpx;
  bottom: 8rpx;
}

.status-icon {
  font-size: 20rpx;
  opacity: 0.6;
}

.message-item.mine .status-icon {
  color: rgba(255, 255, 255, 0.8);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 32rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 28rpx;
  color: #6e6e73;
}

.input-area {
  background-color: rgba(255, 255, 255, 0.96);
  backdrop-filter: saturate(180%) blur(20rpx);
  -webkit-backdrop-filter: saturate(180%) blur(20rpx);
  border-top: 1rpx solid #e5e5e5;
}

.tools-bar {
  display: flex;
  padding: 16rpx 32rpx;
  gap: 24rpx;
}

.tool-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f7;
  border-radius: 16rpx;
  transition: all 0.2s ease;
}

.tool-btn:active {
  background-color: #e5e5e5;
  transform: scale(0.95);
}

.tool-icon {
  font-size: 36rpx;
}

.emoji-panel {
  height: 400rpx;
  border-top: 1rpx solid #e5e5e5;
  background-color: #fafafa;
}

.emoji-scroll {
  height: 100%;
}

.emoji-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 24rpx;
  box-sizing: border-box;
}

.emoji-item {
  width: 12.5%;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.emoji-item:active {
  transform: scale(0.85);
}

.emoji-item text {
  font-size: 48rpx;
}

.input-row {
  display: flex;
  align-items: center;
  padding: 16rpx 32rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  gap: 16rpx;
}

.message-input {
  flex: 1;
  height: 72rpx;
  padding: 0 28rpx;
  background-color: #f5f5f7;
  border-radius: 36rpx;
  font-size: 30rpx;
  color: #1d1d1f;
  border: none;
  outline: none;
  transition: all 0.25s ease;
}

.message-input:focus {
  background-color: #ffffff;
  box-shadow: 0 0 0 3rpx rgba(7, 193, 96, 0.1);
}

.send-btn {
  padding: 0 36rpx;
  height: 72rpx;
  background-color: #e5e5e5;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
}

.send-btn.active {
  background: linear-gradient(135deg, #07C160 0%, #06AD56 100%);
}

.send-btn.active:active {
  transform: scale(0.97);
}

.send-text {
  font-size: 28rpx;
  color: #a1a1a6;
  font-weight: 500;
}

.send-btn.active .send-text {
  color: #ffffff;
}

.slide-up {
  animation: slideUp 0.3s ease forwards;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>