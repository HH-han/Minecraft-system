<template>
  <div class="call-panel" v-if="visible">
    <div class="call-header">
      <img :src="contact.avatar || defaultAvatar" :alt="contact.name" class="caller-avatar" />
      <div class="caller-info">
        <h3>{{ contact.name }}</h3>
        <span class="call-status">{{ statusText }}</span>
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
        v-if="!isConnected && !isEnded" 
        class="action-btn accept-btn" 
        @click="handleAccept"
      >
        <Icon name="phone" :size="'24px'" />
      </button>
      <button 
        class="action-btn reject-btn" 
        @click="handleReject"
      >
        <Icon name="phone-off" :size="'24px'" />
      </button>
      <button 
        v-if="callType === 'video' && isConnected" 
        class="action-btn video-btn" 
        @click="toggleVideo"
      >
        <Icon :name="videoEnabled ? 'video-camera' : 'video-camera-off'" :size="'24px'" />
      </button>
      <button 
        v-if="isConnected" 
        class="action-btn mic-btn" 
        @click="toggleMic"
      >
        <Icon :name="micEnabled ? 'mic' : 'mic-off'" :size="'24px'" />
      </button>
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
const currentUserId = authStore.userInfo?.id

const defaultAvatar = '/src/assets/defaultimage/moren.webp'

const remoteVideo = ref(null)
const localVideo = ref(null)
const webrtcClient = ref(null)
const isConnected = ref(false)
const isEnded = ref(false)
const videoEnabled = ref(true)
const micEnabled = ref(true)
const callStatus = ref('ringing')

const statusText = computed(() => {
  const statusMap = {
    ringing: '正在响铃...',
    accepted: '正在连接...',
    connected: '通话中',
    ended: '通话已结束',
    rejected: '通话已拒绝',
    timeout: '通话超时'
  }
  return statusMap[callStatus.value] || '未知状态'
})

const handleAccept = async () => {
  try {
    const acceptFn = props.callType === 'video' ? acceptVideoCall : acceptVoiceCall
    const response = await acceptFn(props.callId, currentUserId)
    
    if (response.code === 200) {
      callStatus.value = 'accepted'
      await initWebRTC(false)
    }
  } catch (error) {
    console.error('Failed to accept call:', error)
    ElMessage.error('接受通话失败')
  }
}

const handleReject = async () => {
  try {
    const rejectFn = props.callType === 'video' ? rejectVideoCall : rejectVoiceCall
    await rejectFn(props.callId, currentUserId)
    callStatus.value = 'rejected'
    isEnded.value = true
    setTimeout(() => {
      emit('close')
      emit('call-ended')
    }, 1500)
  } catch (error) {
    console.error('Failed to reject call:', error)
  }
}

const handleEnd = async () => {
  try {
    const endFn = props.callType === 'video' ? endVideoCall : endVoiceCall
    await endFn(props.callId, currentUserId)
    callStatus.value = 'ended'
    isEnded.value = true
    
    if (webrtcClient.value) {
      webrtcClient.value.close()
      webrtcClient.value = null
    }
    
    setTimeout(() => {
      emit('close')
      emit('call-ended')
    }, 1500)
  } catch (error) {
    console.error('Failed to end call:', error)
  }
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

const initWebRTC = async (isCaller) => {
  try {
    webrtcClient.value = new WebRTCClient()
    webrtcClient.value.onStreamChange = (type, stream) => {
      if (type === 'local' && localVideo.value) {
        localVideo.value.srcObject = stream
      } else if (type === 'remote' && remoteVideo.value) {
        remoteVideo.value.srcObject = stream
      }
    }
    
    webrtcClient.value.onCallStateChange = (state) => {
      if (state === 'connected') {
        callStatus.value = 'connected'
        isConnected.value = true
      } else if (state === 'disconnected' || state === 'failed') {
        handleEnd()
      }
    }
    
    await webrtcClient.value.init(
      props.callType, 
      props.callId, 
      props.isIncoming ? props.contact.id : currentUserId,
      props.isIncoming ? currentUserId : props.contact.id
    )
    
    if (isCaller) {
      const offer = await webrtcClient.value.createOffer()
      const sendOfferFn = props.callType === 'video' ? sendVideoSdpOffer : sendVoiceSdpOffer
      await sendOfferFn(props.callId, JSON.stringify(offer))
    }
  } catch (error) {
    console.error('Failed to init WebRTC:', error)
    ElMessage.error('初始化通话失败')
    handleReject()
  }
}

const handleSdpOffer = async (sdp) => {
  if (!webrtcClient.value) return
  
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
  if (!webrtcClient.value) return
  
  try {
    await webrtcClient.value.setRemoteDescription(JSON.parse(sdp))
  } catch (error) {
    console.error('Failed to handle SDP answer:', error)
  }
}

const handleIceCandidate = async (candidate) => {
  if (!webrtcClient.value) return
  
  try {
    await webrtcClient.value.addIceCandidate(JSON.parse(candidate))
  } catch (error) {
    console.error('Failed to handle ICE candidate:', error)
  }
}

const handleCallEnd = () => {
  callStatus.value = 'ended'
  isEnded.value = true
  
  if (webrtcClient.value) {
    webrtcClient.value.close()
    webrtcClient.value = null
  }
  
  setTimeout(() => {
    emit('close')
    emit('call-ended')
  }, 1500)
}

watch(() => props.visible, async (val) => {
  if (val && !props.isIncoming) {
    await initWebRTC(true)
  }
})

watch(() => props.callStatus, (status) => {
  if (status) {
    callStatus.value = status
    
    if (status === 'accepted' && props.isIncoming) {
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
  if (webrtcClient.value) {
    webrtcClient.value.close()
    webrtcClient.value = null
  }
})

defineExpose({
  handleSdpOffer,
  handleSdpAnswer,
  handleIceCandidate,
  handleCallEnd
})
</script>

<style scoped>
.call-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  color: white;
}

.call-header {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.caller-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 20px;
  border: 4px solid rgba(255, 255, 255, 0.5);
}

.caller-info h3 {
  font-size: 24px;
  margin: 0 0 8px 0;
}

.call-status {
  font-size: 16px;
  opacity: 0.8;
}

.call-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 600px;
}

.video-container {
  position: relative;
  width: 100%;
  height: 400px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 12px;
  overflow: hidden;
}

.remote-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.local-video {
  position: absolute;
  bottom: 16px;
  right: 16px;
  width: 120px;
  height: 90px;
  border-radius: 8px;
  border: 2px solid rgba(255, 255, 255, 0.5);
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
  gap: 8px;
  height: 60px;
}

.wave-bar {
  width: 6px;
  height: 20px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
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
  0%, 100% { height: 20px; }
  50% { height: 50px; }
}

.call-actions {
  display: flex;
  gap: 24px;
  margin-top: 40px;
}

.action-btn {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.action-btn:hover {
  transform: scale(1.1);
}

.action-btn:active {
  transform: scale(0.95);
}

.accept-btn {
  background: #67c23a;
  color: white;
}

.reject-btn {
  background: #f56c6c;
  color: white;
}

.video-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.mic-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}
</style>