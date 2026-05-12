<template>
  <div class="call-panel" v-if="visible">
    <div class="call-window">
      <div class="call-header">
        <div class="caller-avatar-wrapper">
          <img :src="contact?.avatar || defaultAvatar" :alt="contact?.name" class="caller-avatar" />
        </div>
        <h2 class="caller-name">{{ contact?.name || '未知联系人' }}</h2>
        <p class="call-status">{{ statusText }}</p>
        
        <div v-if="isConnected" class="call-timer">
          {{ formatTime(callDuration) }}
        </div>
      </div>
      
      <div class="call-content">
        <div v-if="callType === 'video'" class="video-container">
          <video 
            ref="remoteVideo" 
            class="remote-video" 
            autoplay 
            playsinline 
            muted="false"
          ></video>
          <video 
            ref="localVideo" 
            class="local-video" 
            autoplay 
            playsinline 
            muted="true"
          ></video>
        </div>
        <div v-else class="voice-container">
          <div class="voice-wave">
            <span v-for="i in 5" :key="i" :class="['wave-bar', { active: isConnected }]"></span>
          </div>
        </div>
      </div>
      
      <div class="call-actions">
        <button 
          class="action-btn mic-btn" 
          :class="{ disabled: !micEnabled }"
          @click="toggleMic"
        >
          <Icon :name="micEnabled ? 'mic' : 'mic-off'" :size="'28px'" />
          <span class="btn-label">{{ micEnabled ? '麦克风已开' : '麦克风已关' }}</span>
        </button>
        
        <button 
          v-if="!isConnected && !isEnded" 
          class="action-btn accept-btn" 
          @click="handleAccept"
        >
          <Icon name="phone" :size="'28px'" />
          <span class="btn-label">接听</span>
        </button>
        
        <button 
          class="action-btn hangup-btn" 
          @click="isConnected ? handleEnd() : handleReject()"
        >
          <Icon :name="isConnected ? 'phone-off' : 'phone-off'" :size="'28px'" />
          <span class="btn-label">{{ isConnected ? '挂断' : '取消' }}</span>
        </button>
        
        <button 
          v-if="callType === 'video' && isConnected" 
          class="action-btn video-btn" 
          :class="{ disabled: !videoEnabled }"
          @click="toggleVideo"
        >
          <Icon :name="videoEnabled ? 'video-camera' : 'video-camera-off'" :size="'28px'" />
          <span class="btn-label">{{ videoEnabled ? '摄像头已开' : '摄像头已关' }}</span>
        </button>
        
        <button 
          class="action-btn speaker-btn" 
          :class="{ disabled: speakerEnabled }"
          @click="toggleSpeaker"
        >
          <Icon :name="speakerEnabled ? 'volume-2' : 'volume-x'" :size="'28px'" />
          <span class="btn-label">{{ speakerEnabled ? '扬声器已开' : '扬声器已关' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import Icon from './Icon.vue'
import WebRTCClient from '@/utils/webrtc'
import { 
  acceptVoiceCall, 
  rejectVoiceCall, 
  endVoiceCall,
  acceptVideoCall,
  rejectVideoCall,
  endVideoCall,
  sendVoiceSdpOffer,
  sendVoiceSdpAnswer,
  sendVideoSdpOffer,
  sendVideoSdpAnswer
} from '@/api/call'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  visible: Boolean,
  contact: Object,
  callType: {
    type: String,
    default: 'voice'
  },
  callId: String,
  isIncoming: Boolean
})

const emit = defineEmits(['close', 'call-ended'])

const authStore = useAuthStore()
const storedUserId = localStorage.getItem('userId')
const currentUserId = parseInt(authStore.userInfo?.id) || parseInt(storedUserId) || authStore.userInfo?.id || storedUserId || null
console.log('[CallPanel] Current user ID:', currentUserId)

const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const remoteVideo = ref(null)
const localVideo = ref(null)
const webrtcClient = ref(null)
const isConnected = ref(false)
const isEnded = ref(false)
const videoEnabled = ref(true)
const micEnabled = ref(true)
const speakerEnabled = ref(true)
const callStatus = ref('ringing')
const isWebRTCInitialized = ref(false)
const callDuration = ref(0)
const pendingSdpOffer = ref(null)
const pendingIceCandidates = ref([])
let durationInterval = null

const statusText = computed(() => {
  const statusMap = {
    ringing: props.isIncoming ? '正在等待对方接听...' : '等待对方接受邀请...',
    accepted: '正在连接...',
    connected: '通话中',
    ended: '通话已结束',
    rejected: '对方已拒绝',
    timeout: '通话超时'
  }
  return statusMap[callStatus.value] || '未知状态'
})

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const handleAccept = async () => {
  console.log(`[CallPanel] Accepting ${props.callType} call, callId: ${props.callId}`)
  try {
    const acceptFn = props.callType === 'video' ? acceptVideoCall : acceptVoiceCall
    const response = await acceptFn(props.callId, currentUserId)
    if (response.code === 200) {
      callStatus.value = 'accepted'
      await initWebRTC(false)
    } else {
      throw new Error(response.message || '接受通话失败')
    }
  } catch (error) {
    console.error('Failed to accept call:', error)
    ElMessage.error('接受通话失败')
  }
}

const handleReject = async () => {
  console.log(`[CallPanel] Rejecting ${props.callType} call, callId: ${props.callId}`)
  try {
    const rejectFn = props.callType === 'video' ? rejectVideoCall : rejectVoiceCall
    await rejectFn(props.callId, currentUserId)
    callStatus.value = 'rejected'
    isEnded.value = true
    cleanupWebRTC()
    setTimeout(() => {
      emit('close')
      emit('call-ended')
    }, 1500)
  } catch (error) {
    console.error('Failed to reject call:', error)
  }
}

const handleEnd = async () => {
  console.log(`[CallPanel] Ending ${props.callType} call, callId: ${props.callId}`)
  try {
    const endFn = props.callType === 'video' ? endVideoCall : endVoiceCall
    await endFn(props.callId, currentUserId)
    callStatus.value = 'ended'
    isEnded.value = true
    cleanupWebRTC()
    setTimeout(() => {
      emit('close')
      emit('call-ended')
    }, 1500)
  } catch (error) {
    console.error('Failed to end call:', error)
  }
}

const cleanupWebRTC = () => {
  if (durationInterval) {
    clearInterval(durationInterval)
    durationInterval = null
  }
  callDuration.value = 0
  
  if (webrtcClient.value) {
    webrtcClient.value.close()
    webrtcClient.value = null
  }
  isWebRTCInitialized.value = false
  // 清除通话缓存，防止影响下次通话
  pendingSdpOffer.value = null
  pendingIceCandidates.value = []
}

const toggleVideo = () => {
  videoEnabled.value = !videoEnabled.value
  if (webrtcClient.value?.localStream) {
    const videoTrack = webrtcClient.value.localStream.getVideoTracks()[0]
    if (videoTrack) {
      videoTrack.enabled = videoEnabled.value
    }
  }
}

const toggleMic = () => {
  micEnabled.value = !micEnabled.value
  if (webrtcClient.value?.localStream) {
    const audioTrack = webrtcClient.value.localStream.getAudioTracks()[0]
    if (audioTrack) {
      audioTrack.enabled = micEnabled.value
    }
  }
}

const toggleSpeaker = () => {
  speakerEnabled.value = !speakerEnabled.value
}

const startCallTimer = () => {
  durationInterval = setInterval(() => {
    callDuration.value++
  }, 1000)
}

const initWebRTC = async (isCaller) => {
  if (isWebRTCInitialized.value) {
    console.log('[CallPanel] WebRTC already initialized')
    return
  }
  console.log(`[CallPanel] Initializing WebRTC, isCaller: ${isCaller}, callType: ${props.callType}, callId: ${props.callId}`)
  
  try {
    webrtcClient.value = new WebRTCClient()
    
    webrtcClient.value.onStreamChange = (type, stream) => {
      console.log(`[CallPanel] Stream changed: ${type}`)
      if (type === 'local' && localVideo.value) {
        localVideo.value.srcObject = stream
      } else if (type === 'remote' && remoteVideo.value) {
        remoteVideo.value.srcObject = stream
      }
    }
    
    webrtcClient.value.onCallStateChange = (state) => {
      console.log(`[CallPanel] Call state changed: ${state}`)
      if (state === 'connected') {
        callStatus.value = 'connected'
        isConnected.value = true
        startCallTimer()
      } else if (state === 'disconnected' || state === 'failed' || state === 'closed') {
        console.log('[CallPanel] Peer connection closed unexpectedly')
        if (!isEnded.value) {
          handleEnd()
        }
      }
    }
    
    const callerId = props.isIncoming ? props.contact?.id : currentUserId
    const receiverId = props.isIncoming ? currentUserId : props.contact?.id
    
    await webrtcClient.value.init(props.callType, props.callId, callerId, receiverId)
    isWebRTCInitialized.value = true
    
    // 如果存在缓存的SDP Offer，立即处理
    if (pendingSdpOffer.value) {
      console.log('[CallPanel] Applying cached SDP offer')
      const sdp = pendingSdpOffer.value
      pendingSdpOffer.value = null
      await handleSdpOffer(sdp)
    }
    
    // 处理缓存的ICE候选
    if (pendingIceCandidates.value.length > 0) {
      console.log('[CallPanel] Applying cached ICE candidates:', pendingIceCandidates.value.length)
      for (const candidate of pendingIceCandidates.value) {
        try {
          await webrtcClient.value.addIceCandidate(JSON.parse(candidate))
        } catch (error) {
          console.error('[CallPanel] Failed to handle cached ICE candidate:', error)
        }
      }
      pendingIceCandidates.value = []
    }
    
    if (isCaller) {
      const offer = await webrtcClient.value.createOffer()
      const sendOfferFn = props.callType === 'video' ? sendVideoSdpOffer : sendVoiceSdpOffer
      await sendOfferFn(props.callId, JSON.stringify(offer))
    }
  } catch (error) {
    console.error('Failed to init WebRTC:', error)
    ElMessage.error('初始化通话失败')
    isWebRTCInitialized.value = false
    handleReject()
  }
}

const handleSdpOffer = async (sdp) => {
  if (!webrtcClient.value) {
    console.log('[CallPanel] WebRTC not initialized yet, caching SDP offer')
    pendingSdpOffer.value = sdp
    return
  }
  
  try {
    await webrtcClient.value.setRemoteDescription(JSON.parse(sdp))
    const answer = await webrtcClient.value.createAnswer()
    const sendAnswerFn = props.callType === 'video' ? sendVideoSdpAnswer : sendVoiceSdpAnswer
    await sendAnswerFn(props.callId, JSON.stringify(answer))
  } catch (error) {
    console.error('Failed to handle SDP offer:', error)
  }
}

const handleSdpAnswer = async (sdp) => {
  if (!webrtcClient.value) {
    console.log('[CallPanel] WebRTC not initialized yet, caching SDP answer')
    pendingSdpOffer.value = sdp
    return
  }
  
  try {
    await webrtcClient.value.setRemoteDescription(JSON.parse(sdp))
  } catch (error) {
    console.error('Failed to handle SDP answer:', error)
  }
}

const handleIceCandidate = async (candidate) => {
  if (!webrtcClient.value) {
    console.log('[CallPanel] WebRTC not initialized yet, caching ICE candidate')
    pendingIceCandidates.value.push(candidate)
    return
  }
  
  try {
    await webrtcClient.value.addIceCandidate(JSON.parse(candidate))
  } catch (error) {
    console.error('Failed to handle ICE candidate:', error)
  }
}

const handleCallEnd = () => {
  callStatus.value = 'ended'
  isEnded.value = true
  cleanupWebRTC()
  setTimeout(() => {
    emit('close')
    emit('call-ended')
  }, 1500)
}

const updateCallStatus = (status) => {
  console.log('[CallPanel] Updating call status to:', status)
  callStatus.value = status
  if (status === 'accepted') {
    // Caller side: receiver accepted, start connecting
    if (!isWebRTCInitialized.value) {
      initWebRTC(true)
    }
  } else if (status === 'ended' || status === 'rejected') {
    handleCallEnd()
  }
}

watch(() => props.visible, async (val) => {
  if (val && !props.isIncoming && !isWebRTCInitialized.value) {
    await initWebRTC(true)
  }
})

watch(() => props.callStatus, (status) => {
  if (status) {
    callStatus.value = status
    if (status === 'accepted' && props.isIncoming && !isWebRTCInitialized.value) {
      initWebRTC(false)
    } else if (status === 'ended' || status === 'rejected') {
      handleCallEnd()
    }
  }
})

onMounted(() => {
  if (props.visible && props.isIncoming) {
    setTimeout(() => {
      if (!isConnected.value && !isEnded.value) {
        callStatus.value = 'timeout'
        handleReject()
      }
    }, 60000)
  }
})

onUnmounted(() => {
  cleanupWebRTC()
})

defineExpose({
  handleSdpOffer,
  handleSdpAnswer,
  handleIceCandidate,
  handleCallEnd,
  updateCallStatus
})
</script>

<style scoped>
.call-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.call-window {
  width: 420px;
  background: linear-gradient(180deg, #2a2a2a 0%, #1a1a1a 100%);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  overflow: hidden;
}

.call-header {
  text-align: center;
  padding: 40px 20px 20px;
}

.caller-avatar-wrapper {
  width: 100px;
  height: 100px;
  margin: 0 auto 16px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.05);
}

.caller-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.caller-name {
  font-size: 20px;
  color: #fff;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.call-status {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.call-timer {
  font-size: 16px;
  color: #00d68f;
  margin-top: 12px;
  font-weight: 500;
  font-family: monospace;
}

.call-content {
  padding: 20px;
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-container {
  position: relative;
  width: 100%;
  height: 240px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.remote-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.local-video {
  position: absolute;
  bottom: 8px;
  right: 8px;
  width: 80px;
  height: 60px;
  border-radius: 6px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  object-fit: cover;
}

.voice-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.voice-wave {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 40px;
}

.wave-bar {
  width: 4px;
  height: 12px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  transition: height 0.1s ease;
}

.wave-bar.active {
  animation: wave 1s ease-in-out infinite;
}

.wave-bar:nth-child(1) { animation-delay: 0s; }
.wave-bar:nth-child(2) { animation-delay: 0.1s; }
.wave-bar:nth-child(3) { animation-delay: 0.2s; }
.wave-bar:nth-child(4) { animation-delay: 0.3s; }
.wave-bar:nth-child(5) { animation-delay: 0.4s; }

@keyframes wave {
  0%, 100% { height: 12px; }
  50% { height: 35px; }
}

.call-actions {
  display: flex;
  justify-content: center;
  gap: 32px;
  padding: 24px 20px 32px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 50%;
  width: 64px;
  height: 64px;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: scale(1.05);
}

.action-btn:active {
  transform: scale(0.95);
}

.action-btn.disabled {
  opacity: 0.5;
}

.btn-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: -4px;
}

.mic-btn {
  color: #fff;
}

.mic-btn.disabled {
  color: #ff4757;
}

.accept-btn {
  background: #00d68f;
  color: #000;
}

.accept-btn:hover {
  background: #00e89c;
}

.hangup-btn {
  background: #ff4757;
  color: #fff;
}

.hangup-btn:hover {
  background: #ff5a6a;
}

.video-btn {
  color: #fff;
}

.video-btn.disabled {
  color: #ff4757;
}

.speaker-btn {
  color: #fff;
}

.speaker-btn.disabled {
  color: #00d68f;
}
</style>