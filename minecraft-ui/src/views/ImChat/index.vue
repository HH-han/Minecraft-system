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
import { ref, onMounted, onUnmounted, watch } from 'vue'
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
  
  const message = {
    receiverId: selectedContact.value.id,
    content: content,
    messageType: 'text'
  }
  
  if (wsService.isConnected) {
    wsService.sendPrivateMessage(
      currentUserId.value,
      selectedContact.value.id,
      content,
      'text'
    )
  }
  
  const response = await apiSendMessage(message)
  if (response.code === 200) {
    messages.value.push({
      id: response.data?.id || Date.now(),
      senderId: currentUserId.value,
      receiverId: selectedContact.value.id,
      content: content,
      messageType: 'text',
      createTime: new Date().toISOString()
    })
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
  const { senderId, content, messageType, timestamp } = data
  
  if (selectedContact.value && selectedContact.value.id === senderId) {
    messages.value.push({
      id: timestamp || Date.now(),
      senderId: senderId,
      receiverId: currentUserId.value,
      content: content,
      messageType: messageType || 'text',
      createTime: new Date().toISOString()
    })
  }
  
  const contactIndex = friends.value.findIndex(f => f.id === senderId)
  if (contactIndex !== -1) {
    friends.value[contactIndex].lastMessage = content
    friends.value[contactIndex].time = formatTime(new Date().toISOString())
    if (!selectedContact.value || selectedContact.value.id !== senderId) {
      friends.value[contactIndex].unreadCount = (friends.value[contactIndex].unreadCount || 0) + 1
    }
  }
  
  const groupIndex = groups.value.findIndex(g => g.id === senderId)
  if (groupIndex !== -1) {
    groups.value[groupIndex].lastMessage = content
    groups.value[groupIndex].time = formatTime(new Date().toISOString())
  }
}

const handleGroupMessage = (data) => {
  const { groupId, senderId, content, messageType, timestamp } = data
  
  if (selectedContact.value && selectedContact.value.id === groupId) {
    messages.value.push({
      id: timestamp || Date.now(),
      senderId: senderId,
      receiverId: groupId,
      content: content,
      messageType: messageType || 'text',
      createTime: new Date().toISOString()
    })
  }
  
  const groupIndex = groups.value.findIndex(g => g.id === groupId)
  if (groupIndex !== -1) {
    groups.value[groupIndex].lastMessage = content
    groups.value[groupIndex].time = formatTime(new Date().toISOString())
  }
}

const handleReadReceipt = (data) => {
  const { messageId } = data
  const msgIndex = messages.value.findIndex(m => m.id === messageId)
  if (msgIndex !== -1) {
    messages.value[msgIndex].isRead = true
  }
}

const handleTyping = (data) => {
  console.log('User typing:', data)
}

const initWebSocket = (userId) => {
  wsService.connect(userId)
  
  wsService.on('connect-success', (data) => {
    console.log('WebSocket connected successfully:', data)
  })
  
  wsService.on('private-message', handlePrivateMessage)
  wsService.on('group-message', handleGroupMessage)
  wsService.on('read-receipt', handleReadReceipt)
  wsService.on('typing', handleTyping)
  
  wsService.on('disconnect', () => {
    console.log('WebSocket disconnected')
  })
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

onMounted(async () => {
  const userInfo = authStore.userInfo || getUserInfo()
  if (userInfo) {
    currentUserId.value = userInfo.id || authStore.userInfo?.id || null
    currentUserName.value = userInfo.username || '用户'
    currentUserAvatar.value = userInfo.avatar || ''
    
    initWebSocket(currentUserId.value)
  }
  
  await loadFriends()
  pendingRequestCount.value = await loadFriendRequests()
  
  if (friends.value.length > 0) {
    selectContact(friends.value[0])
  }
})

onUnmounted(() => {
  wsService.disconnect()
})

const loadFriends = async () => {
  const userId = authStore.userInfo?.id || currentUserId.value
  if (!userId) {
    console.warn('未获取到用户ID')
    return
  }
  const response = await getFriendList(userId)
  if (response.code === 200) {
    friends.value = response.data.map(f => ({
      id: f.friendId,
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
    console.warn('未获取到用户ID')
    return
  }
  try {
    const response = await getPendingFriendRequests(userId)
    if (response.code === 200) {
      return response.data?.length || 0
    }
  } catch (error) {
    console.error('获取好友申请失败:', error)
  }
  return 0
}

const onRequestHandled = ({ requestId, status }) => {
  if (status === 'accept') {
    loadFriends()
  }
}

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