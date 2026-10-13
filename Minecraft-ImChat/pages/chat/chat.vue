<template>
  <view class="chat-page">
    <CustomNavBar
      :title="headerTitle"
      :show-back="true"
      :show-add="true"
      :show-search="false"
      background="#EDEDED"
      title-align="center"
      :menu-items="headerMenu"
      @back="goBack"
      @menuItemClick="onHeaderMenu"
    />

    <scroll-view
      class="msg-list"
      scroll-y
      :scroll-into-view="scrollIntoId"
      :scroll-with-animation="true"
      @scrolltoupper="onReachTop"
      :show-scrollbar="false"
    >
      <view class="msg-list__top">
        <text v-if="isLoading" class="msg-list__loading">加载中…</text>
        <text v-else-if="!messages.length" class="msg-list__empty">还没有消息，打个招呼吧</text>
      </view>

      <view v-for="(msg, index) in messages" :key="msg.localId || msg.id || index">
        <view v-if="showTimeDivider(index)" class="msg-time">
          <text>{{ formatChatTime(msg.createTime) }}</text>
        </view>

        <view class="msg-row" :class="{ 'is-self': isSelf(msg) }">
          <view class="msg-avatar" @click="onAvatarTap(msg)">
            <UiAvatar
              shape="square"
              :size="80"
              :src="isSelf(msg) ? currentUser.avatar : senderAvatarOf(msg)"
              :name="isSelf(msg) ? currentUser.name : (msg.senderName || chatName)"
            />
          </view>

          <view class="msg-main">
            <text
              v-if="chatType === 'group' && !isSelf(msg) && msg.senderName"
              class="msg-sender"
            >{{ msg.senderName }}</text>

            <view class="msg-bubble" :class="bubbleClass(msg)">
              <text v-if="isTextLike(msg)" class="msg-bubble__text">{{ msg.content }}</text>

              <image
                v-else-if="isImageLike(msg)"
                class="msg-bubble__image"
                :class="{ 'is-sticker': isSticker(msg) }"
                :src="msg.content"
                mode="widthFix"
                @click="previewImage(msg.content)"
              />

              <view v-else-if="isFileLike(msg)" class="msg-file">
                <UiIcon name="file" :size="64" color="#576B95" />
                <view class="msg-file__info">
                  <text class="msg-file__name">{{ msg.fileName || '文件' }}</text>
                  <text class="msg-file__size">{{ msg.content }}</text>
                </view>
              </view>

              <text v-else class="msg-bubble__text">{{ msg.content }}</text>

              <view v-if="isSelf(msg) && msg.status === 'failed'" class="msg-bubble__failed">
                <UiIcon name="close" :size="26" color="#FA5151" />
              </view>
              <view v-else-if="isSelf(msg) && msg.status === 'sending'" class="msg-bubble__sending">
                <view class="spinner"></view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view id="msg-bottom" class="msg-bottom"></view>
    </scroll-view>

    <view class="input-bar">
      <view class="input-bar__row">
        <view class="input-bar__btn" @click="toggleVoice">
          <UiIcon :name="voiceMode ? 'keyboard' : 'voice'" :size="52" color="#191919" />
        </view>

        <view v-if="voiceMode" class="input-bar__voice" @touchstart="onVoiceStart" @touchend="onVoiceEnd">
          <text>按住 说话</text>
        </view>
        <input
          v-else
          class="input-bar__input"
          v-model="inputText"
          :adjust-position="false"
          confirm-type="send"
          :cursor-spacing="10"
          @confirm="sendText"
          @focus="onInputFocus"
        />

        <view class="input-bar__btn" @click="toggleEmoji">
          <UiIcon name="smile" :size="52" color="#191919" />
        </view>
        <view class="input-bar__btn" @click="toggleMore">
          <UiIcon name="plus" :size="52" color="#191919" />
        </view>
        <view
          v-if="inputText.trim().length"
          class="input-bar__send"
          @click="sendText"
        >
          <text>发送</text>
        </view>
      </view>
    </view>

    <EmojiPanel v-if="panel === 'emoji'" @select="onEmojiSelect" />

    <view v-if="panel === 'more'" class="more-panel">
      <view class="more-panel__item" @click="pickImage('album')">
        <view class="more-panel__icon"><UiIcon name="image" :size="56" color="#576B95" /></view>
        <text>相册</text>
      </view>
      <view class="more-panel__item" @click="pickImage('camera')">
        <view class="more-panel__icon"><UiIcon name="camera" :size="56" color="#576B95" /></view>
        <text>拍摄</text>
      </view>
      <view class="more-panel__item" @click="openChatInfo">
        <view class="more-panel__icon"><UiIcon name="users" :size="56" color="#576B95" /></view>
        <text>{{ chatType === 'group' ? '群信息' : '好友信息' }}</text>
      </view>
      <view class="more-panel__item" @click="comingSoon">
        <view class="more-panel__icon"><UiIcon name="map-pin" :size="56" color="#576B95" /></view>
        <text>位置</text>
      </view>
    </view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import EmojiPanel from '../../components/EmojiPanel.vue'
import {
  sendSingleMessage,
  sendGroupMessage,
  getSingleChatHistory,
  getGroupChatHistory,
  markAsRead,
  uploadFile
} from '../../utils/chat-api.js'
import { getCurrentProfile } from '../../utils/session.js'
import { formatChatTime } from '../../utils/format.js'
import { clearUnread } from '../../store/index.js'
import wsService from '../../utils/websocket.js'

export default {
  components: { CustomNavBar, UiIcon, UiAvatar, EmojiPanel },
  data() {
    return {
      chatType: 'single',
      chatId: '',
      chatName: '',
      chatAvatar: '',
      memberCount: 0,

      messages: [],
      inputText: '',
      isLoading: false,
      scrollIntoId: '',
      panel: '',
      voiceMode: false,
      currentUser: { id: null, name: '', avatar: '' },
      headerMenu: [
        { text: '查看聊天信息', action: 'info' },
        { text: '清空聊天记录', action: 'clear' }
      ]
    }
  },
  computed: {
    headerTitle() {
      if (this.chatType === 'group' && this.memberCount) {
        return `${this.chatName}(${this.memberCount})`
      }
      return this.chatName
    }
  },
  onLoad(options) {
    this.chatType = options.type || 'single'
    this.chatId = options.id
    this.chatName = decodeURIComponent(options.name || '聊天')
    this.chatAvatar = decodeURIComponent(options.avatar || '')
    this.memberCount = Number(options.memberCount || 0)
    this.currentUser = getCurrentProfile()

    this.loadHistory()
    this.bindWs()
    this.clearUnreadState()
  },
  onUnload() {
    wsService.off('private-message', this.onPrivateMessage)
    wsService.off('group-message', this.onGroupMessage)
    if (this.chatType === 'single' && this.currentUser.id) {
      markAsRead(this.chatId).catch(() => {})
    }
  },
  methods: {
    formatChatTime,
    bindWs() {
      wsService.on('private-message', this.onPrivateMessage)
      wsService.on('group-message', this.onGroupMessage)
    },
    clearUnreadState() {
      clearUnread(this.chatType, this.chatId)
    },
    isSelf(msg) {
      return String(msg.senderId) === String(this.currentUser.id)
    },
    isTextLike(msg) {
      const type = String(msg.messageType || 'text').toLowerCase()
      return type === 'text' || !type
    },
    isImageLike(msg) {
      const type = String(msg.messageType || '').toLowerCase()
      return type === 'image'
    },
    isSticker(msg) {
      const type = String(msg.messageType || '').toLowerCase()
      return type === 'emoji'
    },
    isFileLike(msg) {
      return String(msg.messageType || '').toLowerCase() === 'file'
    },
    bubbleClass(msg) {
      return {
        'is-self': this.isSelf(msg),
        'is-plain': this.isImageLike(msg) || this.isSticker(msg)
      }
    },
    senderAvatarOf(msg) {
      return msg.senderAvatar || (this.chatType === 'single' ? this.chatAvatar : '')
    },
    onAvatarTap(msg) {
      if (this.isSelf(msg)) {
        return
      }
      const id = msg.senderId
      if (!id) {
        uni.showToast({ title: '无法获取用户信息', icon: 'none' })
        return
      }
      uni.navigateTo({
        url: `/pages/contact-detail/contact-detail?type=single&id=${id}&name=${encodeURIComponent(msg.senderName || '')}&avatar=${encodeURIComponent(msg.senderAvatar || '')}`
      })
    },
    showTimeDivider(index) {
      if (index === 0) return true
      const cur = new Date(this.messages[index].createTime).getTime()
      const prev = new Date(this.messages[index - 1].createTime).getTime()
      return cur - prev > 5 * 60 * 1000
    },
    scrollToBottom() {
      this.$nextTick(() => {
        this.scrollIntoId = ''
        this.$nextTick(() => {
          this.scrollIntoId = 'msg-bottom'
        })
      })
    },
    async loadHistory() {
      if (this.isLoading) return
      this.isLoading = true
      try {
        const res = this.chatType === 'single'
          ? await getSingleChatHistory(this.currentUser.id, this.chatId)
          : await getGroupChatHistory(this.chatId)
        if (res && res.code === 200 && Array.isArray(res.data)) {
          this.messages = res.data
            .slice()
            .reverse()
            .map((m) => ({ ...m, status: 'sent' }))
          this.scrollToBottom()
        }
      } catch (e) {
        console.error('加载聊天记录失败:', e)
      } finally {
        this.isLoading = false
      }
    },
    onReachTop() {
      // 预留：下拉加载更多历史
    },
    onPrivateMessage(data) {
      if (this.chatType !== 'single') return
      if (String(data.senderId) !== String(this.chatId)) return
      this.appendIncoming(data, '')
      this.clearUnreadState()
    },
    onGroupMessage(data) {
      if (this.chatType !== 'group') return
      if (String(data.groupId) !== String(this.chatId)) return
      this.appendIncoming(data, data.senderName || '')
      this.clearUnreadState()
    },
    appendIncoming(data, senderName) {
      this.messages.push({
        localId: `in-${data.timestamp || Date.now()}-${Math.random()}`,
        senderId: data.senderId,
        senderName,
        senderAvatar: data.senderAvatar || '',
        content: data.content,
        messageType: data.messageType || 'text',
        createTime: Date.now(),
        status: 'sent'
      })
      this.scrollToBottom()
    },
    async sendText() {
      const content = this.inputText.trim()
      if (!content) return
      this.inputText = ''
      await this.pushMessage(content, 'text')
    },
    onEmojiSelect(payload) {
      if (typeof payload === 'string') {
        this.inputText += payload
        return
      }
      if (payload && payload.type === 'image') {
        this.pushMessage(payload.url, 'emoji')
      }
    },
    async pushMessage(content, messageType) {
      const localId = `local-${Date.now()}`
      const message = {
        localId,
        senderId: this.currentUser.id,
        content,
        messageType,
        createTime: Date.now(),
        status: 'sending'
      }
      this.messages.push(message)
      this.scrollToBottom()

      try {
        const res = this.chatType === 'single'
          ? await sendSingleMessage({
              senderId: this.currentUser.id,
              receiverId: Number(this.chatId),
              content,
              messageType
            })
          : await sendGroupMessage({
              groupId: Number(this.chatId),
              senderId: this.currentUser.id,
              content,
              messageType
            })
        const target = this.messages.find((m) => m.localId === localId)
        if (target) {
          target.status = res && res.code === 200 ? 'sent' : 'failed'
          if (res && res.data && res.data.id) target.id = res.data.id
        }
      } catch (e) {
        console.error('发送消息失败:', e)
        const target = this.messages.find((m) => m.localId === localId)
        if (target) target.status = 'failed'
        uni.showToast({ title: '发送失败', icon: 'none' })
      }
    },
    pickImage(source) {
      uni.chooseImage({
        count: source === 'album' ? 9 : 1,
        sourceType: source === 'camera' ? ['camera'] : ['album'],
        success: async (res) => {
          for (const path of res.tempFilePaths) {
            await this.uploadAndSendImage(path)
          }
        }
      })
      this.panel = ''
    },
    async uploadAndSendImage(filePath) {
      uni.showLoading({ title: '发送中…', mask: true })
      try {
        const res = await uploadFile(filePath)
        if (res && res.code === 200 && res.data) {
          await this.pushMessage(res.data, 'image')
        } else {
          uni.showToast({ title: '上传失败', icon: 'none' })
        }
      } catch (e) {
        console.error('图片上传失败:', e)
        uni.showToast({ title: '上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    previewImage(url) {
      uni.previewImage({ urls: [url], current: url })
    },
    toggleEmoji() {
      this.panel = this.panel === 'emoji' ? '' : 'emoji'
      this.voiceMode = false
    },
    toggleMore() {
      this.panel = this.panel === 'more' ? '' : 'more'
      this.voiceMode = false
    },
    toggleVoice() {
      this.voiceMode = !this.voiceMode
      this.panel = ''
      if (this.voiceMode) uni.showToast({ title: '语音消息开发中', icon: 'none' })
    },
    onVoiceStart() {},
    onVoiceEnd() {},
    onInputFocus() {
      this.panel = ''
    },
    goBack() {
      uni.navigateBack()
    },
    onHeaderMenu(item) {
      if (item.action === 'info') this.openChatInfo()
      else if (item.action === 'clear') this.clearHistory()
    },
    openChatInfo() {
      this.panel = ''
      uni.navigateTo({
        url: `/pages/chat-info/chat-info?type=${this.chatType}&id=${this.chatId}&name=${encodeURIComponent(this.chatName)}&avatar=${encodeURIComponent(this.chatAvatar)}`
      })
    },
    clearHistory() {
      uni.showModal({
        title: '提示',
        content: '确定清空聊天记录吗？',
        success: (res) => {
          if (res.confirm) {
            this.messages = []
            uni.showToast({ title: '已清空', icon: 'success' })
          }
        }
      })
    },
    comingSoon() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.chat-page__subtitle {
  font-size: 26rpx;
  color: #888888;
  font-weight: 400;
  margin-left: 8rpx;
}

.msg-avatar:active {
  opacity: 0.7;
}

.msg-list {
  flex: 1;
  height: 0;
  padding: 20rpx 0;
}

.msg-list__top {
  display: flex;
  justify-content: center;
  padding: 20rpx 0 8rpx;
}

.msg-list__loading,
.msg-list__empty {
  font-size: 24rpx;
  color: #b2b2b2;
}

.msg-time {
  display: flex;
  justify-content: center;
  padding: 24rpx 0 8rpx;
}

.msg-time text {
  font-size: 22rpx;
  color: #b2b2b2;
}

.msg-row {
  display: flex;
  padding: 16rpx 32rpx;
  align-items: flex-start;
}

.msg-row.is-self {
  flex-direction: row-reverse;
}

.msg-main {
  display: flex;
  flex-direction: column;
  max-width: 72%;
  margin: 0 20rpx;
}

.msg-row.is-self .msg-main {
  align-items: flex-end;
}

.msg-sender {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 8rpx;
}

.msg-bubble {
  position: relative;
  padding: 20rpx 24rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  word-break: break-word;
}

.msg-bubble::after {
  content: '';
  position: absolute;
  top: 22rpx;
  left: -10rpx;
  width: 0;
  height: 0;
  border-top: 10rpx solid transparent;
  border-bottom: 10rpx solid transparent;
  border-right: 10rpx solid #ffffff;
}

.msg-bubble.is-self {
  background-color: #95ec69;
}

.msg-bubble.is-self::after {
  left: auto;
  right: -10rpx;
  border-right: none;
  border-left: 10rpx solid #95ec69;
}

.msg-bubble.is-plain {
  background-color: transparent;
  padding: 0;
}

.msg-bubble.is-plain::after {
  display: none;
}

.msg-bubble__text {
  font-size: 30rpx;
  color: #191919;
  line-height: 1.45;
}

.msg-bubble__image {
  max-width: 400rpx;
  border-radius: 12rpx;
  display: block;
}

.msg-bubble__image.is-sticker {
  max-width: 240rpx;
}

.msg-file {
  display: flex;
  align-items: center;
  min-width: 360rpx;
}

.msg-file__info {
  margin-left: 16rpx;
  display: flex;
  flex-direction: column;
}

.msg-file__name {
  font-size: 28rpx;
  color: #191919;
}

.msg-file__size {
  font-size: 22rpx;
  color: #999999;
  margin-top: 4rpx;
}

.msg-bubble__sending,
.msg-bubble__failed {
  position: absolute;
  left: -40rpx;
  top: 24rpx;
  display: flex;
  align-items: center;
}

.msg-bubble.is-self .msg-bubble__sending,
.msg-bubble.is-self .msg-bubble__failed {
  left: auto;
  right: -40rpx;
}

.spinner {
  width: 26rpx;
  height: 26rpx;
  border: 3rpx solid rgba(0, 0, 0, 0.15);
  border-top-color: #07c160;
  border-radius: 50%;
  animation: chatSpin 0.8s linear infinite;
}

@keyframes chatSpin {
  to { transform: rotate(360deg); }
}

.msg-bottom {
  height: 24rpx;
}

/* 输入栏 */
.input-bar {
  flex-shrink: 0;
  background-color: #f7f7f7;
  border-top: 1rpx solid #e2e2e2;
  padding-bottom: env(safe-area-inset-bottom);
}

.input-bar__row {
  display: flex;
  align-items: center;
  padding: 14rpx 20rpx;
  gap: 12rpx;
}

.input-bar__btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-bar__btn:active {
  opacity: 0.5;
}

.input-bar__input {
  flex: 1;
  height: 68rpx;
  padding: 0 22rpx;
  background-color: #ffffff;
  border-radius: 10rpx;
  font-size: 30rpx;
  color: #191919;
}

.input-bar__voice {
  flex: 1;
  height: 68rpx;
  background-color: #ffffff;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-bar__voice text {
  font-size: 28rpx;
  color: #576b95;
}

.input-bar__send {
  padding: 0 28rpx;
  height: 68rpx;
  background-color: #07c160;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
}

.input-bar__send:active {
  background-color: #06ad56;
}

.input-bar__send text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 500;
}

/* 更多面板 */
.more-panel {
  flex-shrink: 0;
  display: flex;
  padding: 32rpx 24rpx;
  background-color: #f7f7f7;
  border-top: 1rpx solid #e2e2e2;
}

.more-panel__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 25%;
}

.more-panel__icon {
  width: 112rpx;
  height: 112rpx;
  background-color: #ffffff;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}

.more-panel__item text {
  font-size: 24rpx;
  color: #666666;
}
</style>
