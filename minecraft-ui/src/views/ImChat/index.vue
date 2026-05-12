<template>
  <div class="im-container">
    <Sidebar
      :friends="friends"
      :groups="groups"
      :active-tab="activeTab"
      :selected-contact="selectedContact"
      :friend-request-count="pendingRequestCount"
      :current-user-avatar="currentUserAvatar"
      :current-user-name="currentUserName"
      @add-friend="showAddFriend = true"
      @create-group="showCreateGroup = true"
      @tab-change="activeTab = $event"
      @select-contact="selectContact"
      @open-friend-requests="showFriendRequests = true"
      @nav-change="handleNavChange"
      @user-profile="handleUserProfile"
      @new-friends="showFriendRequests = true"
      @view-groups="handleViewGroups"
      @tags="handleTags"
      @officials="handleOfficials"
      @moments="handleMoments"
      @scan="handleScan"
      @nearby="handleNearby"
      @shake="handleShake"
      @services="handleServices"
      @favorites="handleFavorites"
      @settings="handleSettings"
      @select-friend="handleSelectFriend"
      @open-detail="handleOpenDetail"
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
    
    <ContactDetail
      v-if="showDetail"
      :contact="detailContact"
      @close="handleCloseDetail"
      @send-message="handleDetailSendMessage"
      @delete-contact="handleDeleteContact"
      @delete-group="handleDeleteGroup"
      @leave-group="handleLeaveGroup"
      @invite-friend="handleInviteFriend"
    />
    
    <AddFriendModal
      v-model:visible="showAddFriend"
      @submit="addFriend"
    />
    
    <FriendRequestModal
      v-model:visible="showFriendRequests"
      @request-handled="onRequestHandled"
    />
    
    <CreateGroupModal
      v-model:visible="showCreateGroup"
      :friends="friends"
      @group-created="onGroupCreated"
    />
    
    <InviteFriendModal
      v-model:visible="showInviteModal"
      :friends="friends"
      :group-id="inviteGroup?.id"
      :group-name="inviteGroup?.name"
      @confirm="handleInviteConfirm"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { sendMessage as apiSendMessage, sendSingleMessage, sendGroupMessage, getChatHistory, getSingleChatHistory, getGroupChatHistory, markAsRead, sendFriendRequest, getFriendList, getFriendInfoList, getPendingFriendRequests, acceptFriendRequest, rejectFriendRequest, getGroupsByUserId } from '@/api/chat'
import { getUserByAccount } from '@/api/user'
import { getToken, getUserInfo } from '@/utils/storage'
import { useAuthStore } from '@/stores/auth'
import wsService from '@/utils/websocket'
import Sidebar from './components/Sidebar.vue'
import ChatArea from './components/ChatArea.vue'
import AddFriendModal from './components/AddFriendModal.vue'
import FriendRequestModal from './components/FriendRequestModal.vue'
import CreateGroupModal from './components/CreateGroupModal.vue'
import ContactDetail from './components/ContactDetail.vue'
import InviteFriendModal from './components/InviteFriendModal.vue'

const authStore = useAuthStore()

const currentUserId = ref(null)
const currentUserName = ref('用户')
const currentUserAvatar = ref('')

const activeTab = ref('friends')
const selectedContact = ref(null)
const messages = ref([])

const showAddFriend = ref(false)
const showFriendRequests = ref(false)
const showCreateGroup = ref(false)
const showVoiceCall = ref(false)
const showVideoCall = ref(false)
const showContactInfo = ref(false)
const showDetail = ref(false)
const detailContact = ref(null)
const showInviteModal = ref(false)
const inviteGroup = ref(null)

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
  await loadMessages(contact.id, contact.isGroup)
}

const loadMessages = async (contactId, isGroup) => {
  let response
  if (isGroup) {
    response = await getGroupChatHistory(contactId)
  } else {
    response = await getSingleChatHistory(currentUserId.value, contactId)
  }
  if (response.code === 200) {
    messages.value = response.data.reverse()
  }
}

const sendMessage = async (messageData) => {
  if (!selectedContact.value) return
  if (!currentUserId.value) {
    console.error('[ImChat] currentUserId is null, cannot send message')
    return
  }
  
  const content = typeof messageData === 'string' ? messageData : messageData.content
  const messageType = typeof messageData === 'string' ? 'text' : (messageData.messageType || 'text')
  
  if (!content.trim()) return
  
  console.log('[ImChat] Sending message via API:', {
    senderId: currentUserId.value,
    receiverId: selectedContact.value.id,
    content: content,
    messageType: messageType,
    isGroup: selectedContact.value.isGroup
  })
  
  let response
  if (selectedContact.value.isGroup) {
    response = await sendGroupMessage({
      groupId: selectedContact.value.id,
      senderId: currentUserId.value,
      content: content,
      messageType: messageType.toLowerCase()
    })
  } else {
    response = await sendSingleMessage({
      senderId: currentUserId.value,
      receiverId: selectedContact.value.id,
      content: content,
      messageType: messageType.toLowerCase()
    })
  }
  
  if (response.code === 200) {
    messages.value.push({
      id: response.data?.id || Date.now(),
      senderId: currentUserId.value,
      receiverId: selectedContact.value.id,
      content: content,
      messageType: messageType.toLowerCase(),
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
    ElMessage.warning('请输入手机号')
    return
  }
  
  const senderId = authStore.userInfo?.id || currentUserId.value
  
  if (!senderId) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    const receiverResponse = await getUserByAccount(phone)
    if (receiverResponse.code === 200 && receiverResponse.data) {
      const receiverId = receiverResponse.data.id
      
      if (receiverId === senderId) {
        ElMessage.warning('不能添加自己为好友')
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
    ElMessage.success('好友请求发送成功')
  } else {
    ElMessage.error(response.message || '发送失败')
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
  const response = await getFriendInfoList(userId)
  if (response.code === 200) {
    friends.value = response.data.map(f => ({
      id: parseInt(f.friendId) || f.friendId,
      name: f.remark || f.username || '未知',
      avatar: f.avatar || '',
      lastMessage: '',
      time: '',
      unreadCount: f.unreadCount || 0,
      online: f.online || false,
      phone: f.phone || '',
      email: f.email || ''
    }))
  }
}

const loadGroups = async () => {
  const userId = authStore.userInfo?.id || currentUserId.value
  if (!userId) {
    console.warn('[ImChat] 未获取到用户ID')
    return
  }
  try {
    const response = await getGroupsByUserId(userId)
    if (response.code === 200) {
      groups.value = response.data.map(g => ({
        id: g.id,
        name: g.name,
        avatar: g.avatar || '',
        lastMessage: '',
        time: '',
        unreadCount: 0,
        online: true,
        isGroup: true
      }))
    }
  } catch (error) {
    console.error('[ImChat] 加载群组失败:', error)
  }
}

const onGroupCreated = (group) => {
  if (group) {
    groups.value.push({
      id: group.id,
      name: group.name,
      avatar: group.avatar || '',
      lastMessage: '',
      time: '',
      unreadCount: 0,
      online: true,
      isGroup: true
    })
  }
}

const handleNavChange = (tab) => {
  console.log('[ImChat] 导航切换:', tab)
  // 可以根据不同的 tab 做相应的处理
}

const handleUserProfile = () => {
  console.log('[ImChat] 查看个人信息')
  // 打开个人信息弹窗
}

const handleViewGroups = () => {
  console.log('[ImChat] 查看群聊')
  // 显示群聊列表
}

const handleTags = () => {
  console.log('[ImChat] 标签')
  // 打开标签管理
}

const handleOfficials = () => {
  console.log('[ImChat] 公众号')
  // 打开公众号列表
}

const handleMoments = () => {
  console.log('[ImChat] 朋友圈')
  // 打开朋友圈
}

const handleScan = () => {
  console.log('[ImChat] 扫一扫')
  // 打开扫一扫
}

const handleNearby = () => {
  console.log('[ImChat] 附近的人')
  // 打开附近的人
}

const handleShake = () => {
  console.log('[ImChat] 摇一摇')
  // 打开摇一摇
}

const handleServices = () => {
  console.log('[ImChat] 服务')
  // 打开服务页面
}

const handleFavorites = () => {
  console.log('[ImChat] 收藏')
  // 打开收藏
}

const handleSettings = () => {
  console.log('[ImChat] 设置')
  // 打开设置
}

const handleSelectFriend = (friend) => {
  console.log('[ImChat] 选择好友:', friend)
  // 可以选择好友后跳转到聊天或查看详情
}

const handleOpenDetail = (contact) => {
  detailContact.value = contact
  showDetail.value = true
}

const handleCloseDetail = () => {
  showDetail.value = false
  detailContact.value = null
}

const handleDetailSendMessage = (contact) => {
  handleCloseDetail()
  selectContact(contact)
}

const handleDeleteContact = (contact) => {
  console.log('[ImChat] 删除好友:', contact)
  ElMessage.info('删除好友功能开发中')
}

const handleDeleteGroup = (group) => {
  console.log('[ImChat] 解散群组:', group)
  ElMessage.info('解散群组功能开发中')
}

const handleLeaveGroup = (group) => {
  console.log('[ImChat] 退出群组:', group)
  ElMessage.info('退出群组功能开发中')
}

const handleInviteFriend = (group) => {
  console.log('[ImChat] 邀请好友加入群组:', group)
  inviteGroup.value = group
  showInviteModal.value = true
}

const handleInviteConfirm = async ({ groupId, friendIds }) => {
  console.log('[ImChat] 确认邀请好友:', { groupId, friendIds })
  
  try {
    // 调用后端API邀请好友加入群组
    const response = await inviteFriendsToGroup(groupId, friendIds)
    if (response.code === 200) {
      ElMessage.success('邀请成功')
      // 更新群组成员数量
      const groupIndex = groups.value.findIndex(g => g.id === groupId)
      if (groupIndex !== -1) {
        groups.value[groupIndex].memberCount = (groups.value[groupIndex].memberCount || 0) + friendIds.length
      }
      // 更新详情面板中的群组成员数量
      if (detailContact.value && detailContact.value.id === groupId) {
        detailContact.value.memberCount = (detailContact.value.memberCount || 0) + friendIds.length
      }
    } else {
      ElMessage.error(response.message || '邀请失败')
    }
  } catch (error) {
    console.error('[ImChat] 邀请好友失败:', error)
    ElMessage.error('邀请失败')
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
  await loadGroups()
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