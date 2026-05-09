<template>
  <div class="im-container">
    <Sidebar
      :friends="friends"
      :groups="groups"
      :active-tab="activeTab"
      :selected-contact="selectedContact"
      :friend-request-count="pendingRequestCount"
      @add-friend="showAddFriend = true"
      @tab-change="activeTab = $event"
      @select-contact="selectContact"
      @open-friend-requests="showFriendRequests = true"
    />
    
    <ChatArea
      ref="chatAreaRef"
      :selected-contact="selectedContact"
      :messages="messages"
      @send="sendMessage"
      @voice-call="showVoiceCall = true"
      @video-call="showVideoCall = true"
      @contact-info="showContactInfo = true"
    />
    
    <AddFriendModal
      v-model:visible="showAddFriend"
      @submit="addFriend"
    />
    
    <FriendRequestModal
      v-model:visible="showFriendRequests"
      @request-handled="onRequestHandled"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { sendMessage as apiSendMessage, getChatHistory, markAsRead, sendFriendRequest, getFriendList, getPendingFriendRequests, acceptFriendRequest, rejectFriendRequest } from '@/api/chat'
import { getUserByAccount } from '@/api/user'
import { getToken, getUserInfo } from '@/utils/storage'
import { useAuthStore } from '@/stores/auth'
import wsService from '@/utils/websocket'
import Sidebar from './components/Sidebar.vue'
import ChatArea from './components/ChatArea.vue'
import AddFriendModal from './components/AddFriendModal.vue'
import FriendRequestModal from './components/FriendRequestModal.vue'

const authStore = useAuthStore()

const currentUserId = ref(null)
const currentUserName = ref('用户')
const currentUserAvatar = ref('')

const activeTab = ref('friends')
const selectedContact = ref(null)
const messages = ref([])

const showAddFriend = ref(false)
const showFriendRequests = ref(false)
const showVoiceCall = ref(false)
const showVideoCall = ref(false)
const showContactInfo = ref(false)

const chatAreaRef = ref(null)

const friends = ref([])
const groups = ref([])
const pendingRequestCount = ref(0)

const selectContact = async (contact) => {
  selectedContact.value = contact
  if (contact.unreadCount > 0) {
    contact.unreadCount = 0
    await markAsRead(contact.id)
  }
  await loadMessages(contact.id)
}

const loadMessages = async (friendId) => {
  const response = await getChatHistory(friendId)
  if (response.code === 200) {
    messages.value = response.data.reverse()
  }
}

const sendMessage = async (content) => {
  if (!selectedContact.value || !content.trim()) return
  if (!currentUserId.value) {
    console.error('[ImChat] currentUserId is null, cannot send message')
    return
  }
  
  console.log('[ImChat] Sending message:', {
    senderId: currentUserId.value,
    receiverId: selectedContact.value.id,
    content: content
  })
  
  if (wsService.isConnected) {
    const sent = wsService.sendPrivateMessage(
      currentUserId.value,
      selectedContact.value.id,
      content,
      'text'
    )
    if (!sent) {
      console.warn('[ImChat] WebSocket send failed, will try API')
    }
  }
  
  const response = await apiSendMessage({
    receiverId: selectedContact.value.id,
    content: content,
    messageType: 'text'
  })
  
  if (response.code === 200) {
    messages.value.push({
      id: response.data?.id || Date.now(),
      senderId: currentUserId.value,
      receiverId: selectedContact.value.id,
      content: content,
      messageType: 'text',
      createTime: new Date().toISOString(),
      isRead: false
    })
    
    await nextTick()
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  if (chatAreaRef.value && chatAreaRef.value.messageListRef) {
    chatAreaRef.value.messageListRef.scrollToBottom()
  }
}

const addFriend = async ({ phone, message }) => {
  if (!phone.trim()) {
    alert('请输入手机号')
    return
  }
  
  const senderId = authStore.userInfo?.id || currentUserId.value
  
  if (!senderId) {
    alert('请先登录')
    return
  }
  
  try {
    const receiverResponse = await getUserByAccount(phone)
    if (receiverResponse.code === 200 && receiverResponse.data) {
      const receiverId = receiverResponse.data.id
      
      if (receiverId === senderId) {
        alert('不能添加自己为好友')
        return
      }
    }
  } catch (error) {
    console.error('查询用户信息失败:', error)
  }
  
  const response = await sendFriendRequest({
    senderId: senderId,
    receiverPhone: phone,
    message: message
  })
  
  if (response.code === 200) {
    alert('好友请求发送成功')
  } else {
    alert(response.message || '发送失败')
  }
}

const handlePrivateMessage = (data) => {
  console.log('[ImChat] Received private message:', data)
  
  if (!data || !data.senderId) {
    console.warn('[ImChat] Invalid private message:', data)
    return
  }
  
  const { senderId, receiverId, content, messageType, timestamp } = data
  
  const senderIdNum = parseInt(senderId)
  const receiverIdNum = parseInt(receiverId)
  
  if (isNaN(senderIdNum)) {
    console.error('[ImChat] Invalid senderId:', senderId)
    return
  }
  
  const isForMe = receiverIdNum === currentUserId.value || !receiverId
  
  if (!isForMe) {
    console.log('[ImChat] Message not for current user, ignoring:', {
      receiverId: receiverIdNum,
      currentUserId: currentUserId.value
    })
    return
  }
  
  const isSelectedContact = selectedContact.value && selectedContact.value.id === senderIdNum
  
  if (isSelectedContact) {
    messages.value.push({
      id: timestamp || Date.now(),
      senderId: senderIdNum,
      receiverId: currentUserId.value,
      content: content,
      messageType: messageType || 'text',
      createTime: new Date().toISOString(),
      isRead: false
    })
    
    nextTick(() => {
      scrollToBottom()
    })
  }
  
  const contactIndex = friends.value.findIndex(f => f.id === senderIdNum)
  if (contactIndex !== -1) {
    friends.value[contactIndex].lastMessage = content
    friends.value[contactIndex].time = formatTime(new Date().toISOString())
    if (!isSelectedContact) {
      friends.value[contactIndex].unreadCount = (friends.value[contactIndex].unreadCount || 0) + 1
    }
  }
}

const handleGroupMessage = (data) => {
  console.log('[ImChat] Received group message:', data)
  
  if (!data || !data.groupId) {
    console.warn('[ImChat] Invalid group message:', data)
    return
  }
  
  const { groupId, senderId, content, messageType, timestamp } = data
  
  const groupIdNum = parseInt(groupId)
  const senderIdNum = parseInt(senderId)
  
  if (isNaN(groupIdNum)) {
    console.error('[ImChat] Invalid groupId:', groupId)
    return
  }
  
  const isSelectedGroup = selectedContact.value && selectedContact.value.id === groupIdNum
  
  if (isSelectedGroup) {
    messages.value.push({
      id: timestamp || Date.now(),
      senderId: senderIdNum,
      receiverId: groupIdNum,
      content: content,
      messageType: messageType || 'text',
      createTime: new Date().toISOString(),
      isRead: false
    })
    
    nextTick(() => {
      scrollToBottom()
    })
  }
  
  const groupIndex = groups.value.findIndex(g => g.id === groupIdNum)
  if (groupIndex !== -1) {
    groups.value[groupIndex].lastMessage = content
    groups.value[groupIndex].time = formatTime(new Date().toISOString())
  }
}

const handleReadReceipt = (data) => {
  console.log('[ImChat] Received read receipt:', data)
  
  if (!data || !data.messageId) {
    console.warn('[ImChat] Invalid read receipt:', data)
    return
  }
  
  const { messageId } = data
  const msgIndex = messages.value.findIndex(m => m.id === messageId)
  if (msgIndex !== -1) {
    messages.value[msgIndex].isRead = true
  }
}

const handleTyping = (data) => {
  console.log('[ImChat] User typing:', data)
}

const handleWsConnect = (data) => {
  console.log('[ImChat] WebSocket connected:', data)
}

const handleWsDisconnect = (data) => {
  console.log('[ImChat] WebSocket disconnected:', data)
}

const handleWsError = (error) => {
  console.error('[ImChat] WebSocket error:', error)
}

const initWebSocket = () => {
  if (!currentUserId.value) {
    console.warn('[ImChat] Cannot init WebSocket, currentUserId is null')
    return
  }
  
  console.log('[ImChat] Initializing WebSocket for userId:', currentUserId.value)
  
  wsService.on('connect', handleWsConnect)
  wsService.on('connect-success', handleWsConnect)
  wsService.on('private-message', handlePrivateMessage)
  wsService.on('group-message', handleGroupMessage)
  wsService.on('read-receipt', handleReadReceipt)
  wsService.on('typing', handleTyping)
  wsService.on('disconnect', handleWsDisconnect)
  wsService.on('error', handleWsError)
  
  wsService.connect(currentUserId.value)
}

const cleanupWebSocket = () => {
  console.log('[ImChat] Cleaning up WebSocket')
  
  wsService.off('connect', handleWsConnect)
  wsService.off('connect-success', handleWsConnect)
  wsService.off('private-message', handlePrivateMessage)
  wsService.off('group-message', handleGroupMessage)
  wsService.off('read-receipt', handleReadReceipt)
  wsService.off('typing', handleTyping)
  wsService.off('disconnect', handleWsDisconnect)
  wsService.off('error', handleWsError)
  
  wsService.disconnect()
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const loadFriends = async () => {
  const userId = authStore.userInfo?.id || currentUserId.value
  if (!userId) {
    console.warn('[ImChat] 未获取到用户ID')
    return
  }
  const response = await getFriendList(userId)
  if (response.code === 200) {
    friends.value = response.data.map(f => ({
      id: parseInt(f.friendId) || f.friendId,
      name: f.remark || '未知',
      avatar: '',
      lastMessage: '',
      time: '',
      unreadCount: 0,
      online: false
    }))
  }
}

const loadFriendRequests = async () => {
  const userId = authStore.userInfo?.id || currentUserId.value
  if (!userId) {
    console.warn('[ImChat] 未获取到用户ID')
    return
  }
  try {
    const response = await getPendingFriendRequests(userId)
    if (response.code === 200) {
      return response.data?.length || 0
    }
  } catch (error) {
    console.error('[ImChat] 获取好友申请失败:', error)
  }
  return 0
}

const onRequestHandled = ({ requestId, status }) => {
  if (status === 'accept') {
    loadFriends()
  }
}

onMounted(async () => {
  const userInfo = authStore.userInfo || getUserInfo()
  if (userInfo) {
    currentUserId.value = parseInt(userInfo.id) || userInfo.id || authStore.userInfo?.id || null
    currentUserName.value = userInfo.username || '用户'
    currentUserAvatar.value = userInfo.avatar || ''
  }
  
  await loadFriends()
  pendingRequestCount.value = await loadFriendRequests()
  
  if (currentUserId.value) {
    initWebSocket()
  }
  
  if (friends.value.length > 0) {
    selectContact(friends.value[0])
  }
})

onUnmounted(() => {
  cleanupWebSocket()
})

watch(selectedContact, () => {
  messages.value = []
})
</script>

<style scoped>
.im-container {
  display: flex;
  height: 100vh;
  background: #f5f5f5;
}
</style>