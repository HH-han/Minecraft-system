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
      @voice-call="handleVoiceCall"
      @video-call="handleVideoCall"
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
      @update-remark="handleUpdateRemark"
      @edit-group="handleEditGroup"
      @edit-friend-remark="handleEditFriendRemark"
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
    
    <GroupEditModal
      v-model:visible="showGroupEditModal"
      :group="editGroup"
      @confirm="handleGroupEditConfirm"
    />
    
    <FriendRemarkModal
      v-model:visible="showFriendRemarkModal"
      :friend="editFriend"
      @confirm="handleFriendRemarkConfirm"
    />
    
    <CallPanel
      ref="callPanelRef"
      :visible="showCallPanel"
      :contact="callContact"
      :call-type="currentCallType"
      :call-id="currentCallId"
      :is-incoming="isIncomingCall"
      @close="showCallPanel = false"
      @call-ended="handleCallEnded"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { sendMessage as apiSendMessage, sendSingleMessage, sendGroupMessage, getChatHistory, getSingleChatHistory, getGroupChatHistory, markAsRead, sendFriendRequest, getFriendList, getFriendInfoList, getPendingFriendRequests, acceptFriendRequest, rejectFriendRequest, getGroupsByUserId, inviteFriendsToGroup, updateGroup, updateFriendRemark } from '@/api/chat'
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
import GroupEditModal from './components/GroupEditModal.vue'
import FriendRemarkModal from './components/FriendRemarkModal.vue'
import CallPanel from './components/CallPanel.vue'
import { initiateVoiceCall, initiateVideoCall } from '@/api/call'

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
const showGroupEditModal = ref(false)
const editGroup = ref(null)
const showFriendRemarkModal = ref(false)
const editFriend = ref(null)

const chatAreaRef = ref(null)

const showCallPanel = ref(false)
const callPanelRef = ref(null)
const currentCallType = ref('voice')
const currentCallId = ref(null)
const isIncomingCall = ref(false)
const callContact = ref(null)

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

const handleVoiceCall = async () => {
  if (!selectedContact.value || selectedContact.value.isGroup) {
    ElMessage.warning('只能与好友进行语音通话')
    return
  }
  
  currentCallType.value = 'voice'
  callContact.value = selectedContact.value
  isIncomingCall.value = false
  
  try {
    const response = await initiateVoiceCall(currentUserId.value, selectedContact.value.id)
    if (response.code === 200) {
      currentCallId.value = response.data.callId
      showCallPanel.value = true
    } else {
      ElMessage.error(response.message || '发起通话失败')
    }
  } catch (error) {
    console.error('[ImChat] Failed to initiate voice call:', error)
    ElMessage.error('发起通话失败')
  }
}

const handleVideoCall = async () => {
  if (!selectedContact.value || selectedContact.value.isGroup) {
    ElMessage.warning('只能与好友进行视频通话')
    return
  }
  
  currentCallType.value = 'video'
  callContact.value = selectedContact.value
  isIncomingCall.value = false
  
  try {
    const response = await initiateVideoCall(currentUserId.value, selectedContact.value.id)
    if (response.code === 200) {
      currentCallId.value = response.data.callId
      showCallPanel.value = true
    } else {
      ElMessage.error(response.message || '发起通话失败')
    }
  } catch (error) {
    console.error('[ImChat] Failed to initiate video call:', error)
    ElMessage.error('发起通话失败')
  }
}

const handleIncomingCall = (data) => {
  console.log('[ImChat] Incoming call:', data)
  
  const callData = data.data
  if (!callData || !callData.callId || !callData.callerId) {
    console.warn('[ImChat] Invalid incoming call data:', data)
    return
  }
  
  const callerId = parseInt(callData.callerId)
  const caller = friends.value.find(f => f.id === callerId)
  
  if (!caller) {
    console.warn('[ImChat] Caller not found in friends list:', callerId)
    return
  }
  
  currentCallId.value = callData.callId
  currentCallType.value = callData.callType || 'voice'
  callContact.value = caller
  isIncomingCall.value = true
  showCallPanel.value = true
}

const handleCallEnded = () => {
  showCallPanel.value = false
  currentCallId.value = null
  callContact.value = null
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
  wsService.on('voice-call', handleIncomingCall)
  wsService.on('video-call', handleIncomingCall)
  
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
  wsService.off('voice-call', handleIncomingCall)
  wsService.off('video-call', handleIncomingCall)
  
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
      const currentUserId = authStore.userInfo?.id || currentUserId.value
      groups.value = response.data.map(g => ({
        id: g.id,
        name: g.name,
        avatar: g.avatar || '',
        lastMessage: '',
        time: '',
        unreadCount: 0,
        online: true,
        isGroup: true,
        memberCount: g.memberCount || 0,
        isCreator: g.creatorId === currentUserId
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
      isGroup: true,
      memberCount: group.memberCount || 1,
      isCreator: true
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

const handleEditGroup = (group) => {
  console.log('[ImChat] 编辑群组:', group)
  editGroup.value = { ...group }
  showGroupEditModal.value = true
}

const handleEditFriendRemark = (friend) => {
  console.log('[ImChat] 编辑好友备注:', friend)
  editFriend.value = { ...friend }
  showFriendRemarkModal.value = true
}

const handleFriendRemarkConfirm = async ({ friendId, remark }) => {
  console.log('[ImChat] 确认修改好友备注:', { friendId, remark })
  
  try {
    const userId = authStore.userInfo?.id || currentUserId.value
    const response = await updateFriendRemark(userId, friendId, remark)
    if (response.code === 200) {
      ElMessage.success('备注修改成功')
      
      const friendIndex = friends.value.findIndex(f => f.id === friendId)
      if (friendIndex !== -1) {
        friends.value[friendIndex].name = remark
      }
      
      if (detailContact.value && detailContact.value.id === friendId) {
        detailContact.value.name = remark
      }
      
      if (selectedContact.value && selectedContact.value.id === friendId) {
        selectedContact.value.name = remark
      }
    } else {
      ElMessage.error(response.message || '修改失败')
    }
  } catch (error) {
    console.error('[ImChat] 修改好友备注失败:', error)
    ElMessage.error('修改失败')
  }
}

const handleGroupEditConfirm = async (data) => {
  console.log('[ImChat] 确认编辑群组:', data)
  
  try {
    const formData = new FormData()
    formData.append('id', data.id)
    if (data.name) formData.append('name', data.name)
    if (data.description) formData.append('description', data.description)
    if (data.maxMembers) formData.append('maxMembers', data.maxMembers)
    if (data.remark) formData.append('remark', data.remark)
    if (data.avatar) formData.append('avatar', data.avatar)
    
    const response = await updateGroup(formData)
    if (response.code === 200) {
      ElMessage.success('修改成功')
      
      const groupIndex = groups.value.findIndex(g => g.id === data.id)
      if (groupIndex !== -1) {
        if (data.name) groups.value[groupIndex].name = data.name
        if (data.avatar) groups.value[groupIndex].avatar = URL.createObjectURL(data.avatar)
        if (data.maxMembers) groups.value[groupIndex].maxMembers = data.maxMembers
        if (data.remark) groups.value[groupIndex].remark = data.remark
      }
      
      if (detailContact.value && detailContact.value.id === data.id) {
        if (data.name) detailContact.value.name = data.name
        if (data.avatar) detailContact.value.avatar = URL.createObjectURL(data.avatar)
        if (data.maxMembers) detailContact.value.maxMembers = data.maxMembers
        if (data.remark) detailContact.value.remark = data.remark
      }
    } else {
      ElMessage.error(response.message || '修改失败')
    }
  } catch (error) {
    console.error('[ImChat] 编辑群组失败:', error)
    ElMessage.error('修改失败')
  }
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

const handleUpdateRemark = ({ friendId, remark }) => {
  const friendIndex = friends.value.findIndex(f => f.id === friendId)
  if (friendIndex !== -1) {
    friends.value[friendIndex].name = remark
  }

  if (detailContact.value?.id === friendId) {
    detailContact.value.name = remark
  }

  if (selectedContact.value?.id === friendId) {
    selectedContact.value.name = remark
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