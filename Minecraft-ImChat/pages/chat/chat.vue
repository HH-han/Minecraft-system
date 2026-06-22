<template>
  <view class="chat-page">
    <!-- 导航栏 -->
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

    <!-- 消息列表 -->
    <scroll-view
      class="message-list"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-with-animation="true"
      : refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="loadMoreMessages"
      @scrolltoupper="loadMoreMessages"
    >
      <!-- 加载更多提示 -->
      <view v-if="isLoading" class="loading-tip">
        <text>加载中...</text>
      </view>

      <!-- 消息列表 -->
      <view
        v-for="(msg, index) in messages"
        :key="msg.id || index"
        :class="['message-item', { mine: msg.senderId === currentUserId }]"
      >
        <!-- 时间分隔 -->
        <view v-if="showTimeDivider(index)" class="time-divider">
          <text class="time-text">{{ formatTime(msg.createTime) }}</text>
        </view>

        <!-- 消息内容 -->
        <view class="message-row">
          <!-- 头像 -->
          <image
            v-if="msg.senderId !== currentUserId"
            class="message-avatar"
            :src="msg.senderAvatar || getSenderAvatar(msg)"
            mode="aspectFill"
          />

          <!-- 消息气泡 -->
          <view
            :class="[
              'message-bubble',
              { mine: msg.senderId === currentUserId },
              { image: msg.messageType === 'image' || msg.messageType === 'IMAGE' }
            ]"
          >
            <!-- 文本消息 -->
            <text v-if="msg.messageType === 'text' || msg.messageType === 'TEXT'" class="message-text">
              {{ msg.content }}
            </text>

            <!-- 图片消息 -->
            <image
              v-else-if="msg.messageType === 'image' || msg.messageType === 'IMAGE'"
              class="message-image"
              :src="msg.content"
              mode="widthFix"
              @click="previewImage(msg.content)"
            />

            <!-- 其他类型 -->
            <text v-else class="message-text">
              {{ msg.content }}
            </text>
          </view>

          <!-- 我的头像 -->
          <image
            v-if="msg.senderId === currentUserId"
            class="message-avatar"
            :src="currentUserAvatar || defaultAvatar"
            mode="aspectFill"
          />
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="messages.length === 0 && !isLoading" class="empty-state">
        <text class="empty-text">还没有消息，开始聊天吧</text>
      </view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="input-area">
      <!-- 功能按钮 -->
      <view class="tools-bar">
        <view class="tool-btn" @click="toggleEmoji">
          <text class="tool-icon">😀</text>
        </view>
        <view class="tool-btn" @click="selectImage">
          <text class="tool-icon">🖼️</text>
        </view>
      </view>

      <!-- 表情面板 -->
      <view v-if="showEmojiPanel" class="emoji-panel">
        <scroll-view scroll-y class="emoji-list">
          <view
            v-for="(emoji, index) in emojiList"
            :key="index"
            class="emoji-item"
            @click="selectEmoji(emoji)"
          >
            <text>{{ emoji }}</text>
          </view>
        </scroll-view>
      </view>

      <!-- 输入框 -->
      <view class="input-row">
        <input
          class="message-input"
          v-model="inputMessage"
          :placeholder="inputPlaceholder"
          confirm-type="send"
          @confirm="sendMessage"
          @focus="onInputFocus"
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
      chatType: 'single', // single | group
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
      emojiList: ['😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣', '😊', '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰', '😘', '😗', '😙', '😚', '😋', '😛', '😜', '🤪', '😝', '🤗', '🤭', '🤫', '🤔', '🤐', '🤨', '😐', '😑', '😶', '😏', '😒', '🙄', '😬', '🤥', '😔', '😪'],

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
    // 清理 WebSocket 监听
    wsService.off('private-message', this.handlePrivateMessage)
    wsService.off('group-message', this.handleGroupMessage)

    // 标记已读
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
      // 连接 WebSocket
      wsService.connect(this.currentUserId)

      // 监听消息
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
        createTime: new Date().toISOString()
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
        createTime: new Date().toISOString()
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
          this.messages = res.data.reverse()
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

      // 清空输入框
      this.inputMessage = ''

      // 发送消息
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
          // 添加消息到列表
          this.messages.push({
            id: res.data?.id || Date.now(),
            senderId: this.currentUserId,
            content: content,
            messageType: messageType,
            createTime: new Date().toISOString()
          })

          this.scrollToBottom()
        }
      } catch (e) {
        console.error('发送消息失败:', e)
        uni.showToast({ title: '发送失败', icon: 'none' })
        // 恢复输入框内容
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

              // 发送图片消息
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
                  createTime: new Date().toISOString()
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
    },

    previewImage(url) {
      uni.previewImage({
        urls: [url],
        current: url
      })
    },

    getSenderAvatar(msg) {
      // 群聊中获取发送者头像
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
      return diff > 5 * 60 * 1000 // 超过5分钟显示时间
    },

    scrollToBottom() {
      this.$nextTick(() => {
        this.scrollTop = this.scrollTop === 0 ? 1 : 0
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
  background-color: #f5f5f5;
}

/* 导航栏 */
.nav-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #e5e5e5;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: #2997ff;
  font-weight: bold;
}

.nav-title {
  flex: 1;
  text-align: center;
}

.title-text {
  font-size: 34rpx;
  font-weight: 600;
  color: #1d1d1f;
}

.nav-more {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.more-icon {
  font-size: 36rpx;
  color: #6e6e73;
}

/* 消息列表 */
.message-list {
  flex: 1;
  padding: 20rpx 30rpx;
  overflow: hidden;
}

.loading-tip {
  text-align: center;
  padding: 20rpx;
  color: #a1a1a6;
  font-size: 24rpx;
}

/* 时间分隔 */
.time-divider {
  display: flex;
  justify-content: center;
  padding: 20rpx 0;
}

.time-text {
  font-size: 22rpx;
  color: #a1a1a6;
  background-color: #e5e5e5;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
}

/* 消息项 */
.message-item {
  margin-bottom: 30rpx;
}

.message-row {
  display: flex;
  align-items: flex-start;
}

.message-item.mine .message-row {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background-color: #e5e5e5;
  flex-shrink: 0;
}

/* 消息气泡 */
.message-bubble {
  max-width: 70%;
  margin: 0 20rpx;
  padding: 20rpx 28rpx;
  background-color: #fff;
  border-radius: 20rpx;
  position: relative;
}

.message-bubble::before {
  content: '';
  position: absolute;
  top: 24rpx;
  left: -16rpx;
  border: 10rpx solid transparent;
  border-right-color: #fff;
}

.message-item.mine .message-bubble {
  background-color: #2997ff;
  margin-left: 0;
  margin-right: 20rpx;
}

.message-item.mine .message-bubble::before {
  left: auto;
  right: -16rpx;
  border-right-color: transparent;
  border-left-color: #2997ff;
}

.message-text {
  font-size: 30rpx;
  color: #1d1d1f;
  line-height: 1.5;
  word-break: break-word;
}

.message-item.mine .message-text {
  color: #fff;
}

.message-bubble.image {
  background-color: transparent !important;
  padding: 0 !important;
}

.message-bubble.image::before {
  display: none;
}

.message-image {
  max-width: 400rpx;
  border-radius: 16rpx;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  padding: 100rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #a1a1a6;
}

/* 输入区域 */
.input-area {
  background-color: #fff;
  border-top: 1rpx solid #e5e5e5;
}

.tools-bar {
  display: flex;
  padding: 16rpx 30rpx;
  gap: 30rpx;
}

.tool-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 12rpx;
}

.tool-icon {
  font-size: 36rpx;
}

/* 表情面板 */
.emoji-panel {
  height: 300rpx;
  border-top: 1rpx solid #e5e5e5;
  background-color: #fafafa;
}

.emoji-list {
  display: flex;
  flex-wrap: wrap;
  padding: 20rpx;
  height: 100%;
}

.emoji-item {
  width: 12.5%;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.emoji-item text {
  font-size: 48rpx;
}

/* 输入行 */
.input-row {
  display: flex;
  align-items: center;
  padding: 16rpx 30rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
}

.message-input {
  flex: 1;
  height: 72rpx;
  padding: 0 24rpx;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  font-size: 30rpx;
}

.send-btn {
  margin-left: 20rpx;
  padding: 0 36rpx;
  height: 72rpx;
  background-color: #e5e5e5;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn.active {
  background-color: #2997ff;
}

.send-text {
  font-size: 28rpx;
  color: #6e6e73;
}

.send-btn.active .send-text {
  color: #fff;
}
</style>
