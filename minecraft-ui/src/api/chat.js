import request from '@/utils/request'

export const sendMessage = (message) => {
  return request.post('/chat/send', message)
}

export const getChatHistory = (friendId, limit = 50) => {
  return request.get('/chat/history', {
    params: {
      friendId,
      limit
    }
  })
}

export const markAsRead = (friendId) => {
  return request.post('/chat/read', null, {
    params: {
      friendId
    }
  })
}

export const sendSingleMessage = (message) => {
  return request.post('/public/im/single/send', message)
}

export const sendSingleMessageParam = (senderId, receiverId, content, messageType = 'text') => {
  return request.post('/public/im/single/send/param', null, {
    params: {
      senderId,
      receiverId,
      content,
      messageType
    }
  })
}

export const getSingleChatHistory = (userId1, userId2, limit = 50) => {
  return request.get('/public/im/single/history', {
    params: {
      userId1,
      userId2,
      limit
    }
  })
}

export const sendGroupMessage = (message) => {
  return request.post('/public/im/group/send', message)
}

export const sendGroupMessageParam = (groupId, senderId, content, messageType = 'text') => {
  return request.post('/public/im/group/send/param', null, {
    params: {
      groupId,
      senderId,
      content,
      messageType
    }
  })
}

export const getGroupChatHistory = (groupId, limit = 50) => {
  return request.get('/public/im/group/history', {
    params: {
      groupId,
      limit
    }
  })
}

export const sendFriendRequest = (data) => {
  return request.post('/public/im/friend/request', data)
}

export const sendFriendRequestParam = (senderId, receiverId, receiverPhone, message) => {
  return request.post('/public/im/friend/request/param', null, {
    params: {
      senderId,
      receiverId,
      receiverPhone,
      message
    }
  })
}

export const handleFriendRequest = (requestId, status) => {
  return request.put('/public/im/friend/request/handle', null, {
    params: {
      requestId,
      status
    }
  })
}

export const acceptFriendRequest = (requestId) => {
  return request.put('/public/im/friend/request/accept', null, {
    params: {
      requestId
    }
  })
}

export const rejectFriendRequest = (requestId) => {
  return request.put('/public/im/friend/request/reject', null, {
    params: {
      requestId
    }
  })
}

export const getFriendList = (userId) => {
  return request.get('/public/im/friend/list', {
    params: {
      userId
    }
  })
}

export const deleteFriend = (userId, friendId) => {
  return request.delete('/public/im/friend/delete', {
    params: {
      userId,
      friendId
    }
  })
}

export const getPendingFriendRequests = (userId) => {
  return request.get('/public/im/friend/request/pending', {
    params: {
      userId
    }
  })
}

export const markMessageAsRead = (messageId, userId, groupId = null) => {
  return request.post('/public/im/message/read', null, {
    params: {
      messageId,
      userId,
      groupId
    }
  })
}

export const getUnreadMessageCount = (userId, groupId = null) => {
  return request.get('/public/im/message/unread/count', {
    params: {
      userId,
      groupId
    }
  })
}

export const getGroupById = (groupId) => {
  return request.get(`/public/chat-group/${groupId}`)
}

export const getGroupsByCreatorId = (creatorId) => {
  return request.get(`/public/chat-group/creator/${creatorId}`)
}

export const searchGroups = (name) => {
  return request.get('/public/chat-group/search', {
    params: {
      name
    }
  })
}

export const createGroup = (group) => {
  return request.post('/public/chat-group', group)
}

export const updateGroup = (group) => {
  return request.put('/public/chat-group', group)
}

export const deleteGroup = (groupId) => {
  return request.delete(`/public/chat-group/${groupId}`)
}

export const addGroupMember = (groupId, userId, role = 'member') => {
  return request.post(`/public/chat-group/${groupId}/members`, null, {
    params: {
      userId,
      role
    }
  })
}

export const removeGroupMember = (groupId, userId) => {
  return request.delete(`/public/chat-group/${groupId}/members/${userId}`)
}

export const getGroupMembers = (groupId) => {
  return request.get(`/public/chat-group/${groupId}/members`)
}

export const initiateCall = (callerId, receiverId) => {
  return request.post('/public/voice/call/initiate', null, {
    params: {
      callerId,
      receiverId
    }
  })
}

export const acceptCall = (callId, userId) => {
  return request.post('/public/voice/call/accept', null, {
    params: {
      callId,
      userId
    }
  })
}

export const rejectCall = (callId, userId) => {
  return request.post('/public/voice/call/reject', null, {
    params: {
      callId,
      userId
    }
  })
}

export const endCall = (callId, userId) => {
  return request.post('/public/voice/call/end', null, {
    params: {
      callId,
      userId
    }
  })
}

export const sendSdpOffer = (callId, sdp) => {
  return request.post('/public/voice/call/sdp-offer', null, {
    params: {
      callId,
      sdp
    }
  })
}


export const sendSdpAnswer = (callId, sdp) => {
  return request.post('/public/voice/call/sdp-answer', null, {
    params: {
      callId,
      sdp
    }
  })
}

export const sendIceCandidate = (callId, candidate) => {
  return request.post('/public/voice/call/ice-candidate', null, {
    params: {
      callId,
      candidate
    }
  })
}

export const getCallStatus = (callId) => {
  return request.get('/public/voice/call/status', {
    params: {
      callId
    }
  })
}