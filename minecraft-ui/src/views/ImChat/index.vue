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
import { ref, onMounted, watch } from 'vue'
import { sendMessage as apiSendMessage, getChatHistory, markAsRead, sendFriendRequest, getFriendList, getPendingFriendRequests, acceptFriendRequest, rejectFriendRequest } from '@/api/chat'
import { getUserByAccount } from '@/api/user'
import { getToken, getUserInfo } from '@/utils/storage'
import { useAuthStore } from '@/stores/auth'
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
  
  const response = await apiSendMessage(message)
  if (response.code === 200) {
    messages.value.push({
      id: Date.now(),
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

onMounted(async () => {
  const userInfo = authStore.userInfo || getUserInfo()
  if (userInfo) {
    currentUserId.value = userInfo.id || authStore.userInfo?.id || null
    currentUserName.value = userInfo.username || '用户'
    currentUserAvatar.value = userInfo.avatar || ''
  }
  
  await loadFriends()
  pendingRequestCount.value = await loadFriendRequests()
  
  if (friends.value.length > 0) {
    selectContact(friends.value[0])
  }
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