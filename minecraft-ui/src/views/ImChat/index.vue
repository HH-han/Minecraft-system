<template>
  <div class="im-container">
    <Sidebar
      :friends="friends"
      :groups="groups"
      :active-tab="activeTab"
      :selected-contact="selectedContact"
      @add-friend="showAddFriend = true"
      @tab-change="activeTab = $event"
      @select-contact="selectContact"
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { sendMessage as apiSendMessage, getChatHistory, markAsRead, sendFriendRequest, getFriendList } from '@/api/chat'
import { getToken, getUserInfo } from '@/utils/storage'
import Sidebar from './components/Sidebar.vue'
import ChatArea from './components/ChatArea.vue'
import AddFriendModal from './components/AddFriendModal.vue'

const currentUserId = ref(1)
const currentUserName = ref('用户')
const currentUserAvatar = ref('')

const activeTab = ref('friends')
const selectedContact = ref(null)
const messages = ref([])

const showAddFriend = ref(false)
const showVoiceCall = ref(false)
const showVideoCall = ref(false)
const showContactInfo = ref(false)

const chatAreaRef = ref(null)

const friends = ref([])

const groups = ref([])

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
  const response = await sendFriendRequest({
    senderId: currentUserId.value,
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
  const response = await getFriendList(currentUserId.value)
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

onMounted(() => {
  const userInfo = getUserInfo()
  if (userInfo) {
    currentUserId.value = userInfo.id || 1
    currentUserName.value = userInfo.username || '用户'
    currentUserAvatar.value = userInfo.avatar || ''
  }
  
  loadFriends()
  
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