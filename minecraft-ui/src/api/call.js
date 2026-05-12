import request from '@/utils/request'

export const initiateVoiceCall = (callerId, receiverId) => {
  return request.post('/public/voice/call/initiate', null, {
    params: { callerId, receiverId }
  })
}

export const acceptVoiceCall = (callId, userId) => {
  return request.post('/public/voice/call/accept', null, {
    params: { callId, userId }
  })
}

export const rejectVoiceCall = (callId, userId) => {
  return request.post('/public/voice/call/reject', null, {
    params: { callId, userId }
  })
}

export const endVoiceCall = (callId, userId) => {
  return request.post('/public/voice/call/end', null, {
    params: { callId, userId }
  })
}

export const sendVoiceSdpOffer = (callId, sdp) => {
  return request.post('/public/voice/call/sdp-offer', null, {
    params: { callId, sdp }
  })
}

export const sendVoiceSdpAnswer = (callId, sdp) => {
  return request.post('/public/voice/call/sdp-answer', null, {
    params: { callId, sdp }
  })
}

export const sendVoiceIceCandidate = (callId, candidate, sdpMid, sdpMLineIndex) => {
  return request.post('/public/voice/call/ice-candidate', null, {
    params: { callId, candidate: JSON.stringify(candidate), sdpMid, sdpMLineIndex }
  })
}

export const getVoiceCallStatus = (callId) => {
  return request.get('/public/voice/call/status', {
    params: { callId }
  })
}

export const initiateVideoCall = (callerId, receiverId) => {
  return request.post('/public/video/call/initiate', null, {
    params: { callerId, receiverId }
  })
}

export const acceptVideoCall = (callId, userId) => {
  return request.post('/public/video/call/accept', null, {
    params: { callId, userId }
  })
}

export const rejectVideoCall = (callId, userId) => {
  return request.post('/public/video/call/reject', null, {
    params: { callId, userId }
  })
}

export const endVideoCall = (callId, userId) => {
  return request.post('/public/video/call/end', null, {
    params: { callId, userId }
  })
}

export const sendVideoSdpOffer = (callId, sdp) => {
  return request.post('/public/video/call/sdp-offer', null, {
    params: { callId, sdp }
  })
}

export const sendVideoSdpAnswer = (callId, sdp) => {
  return request.post('/public/video/call/sdp-answer', null, {
    params: { callId, sdp }
  })
}

export const sendVideoIceCandidate = (callId, candidate, sdpMid, sdpMLineIndex) => {
  return request.post('/public/video/call/ice-candidate', null, {
    params: { callId, candidate: JSON.stringify(candidate), sdpMid, sdpMLineIndex }
  })
}

export const getVideoCallStatus = (callId) => {
  return request.get('/public/video/call/status', {
    params: { callId }
  })
}