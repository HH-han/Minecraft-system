# 视频通话和语音通话开发文档

## 1. 概述

本文档基于 Minecraft 旅游社交系统现有架构，设计原生视频通话和语音通话功能。系统采用 WebRTC 技术实现点对点音视频通信，结合 WebSocket 进行信令传输，Redis 存储通话状态。

## 2. 架构设计

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                        客户端层 (Web/APP)                           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐            │
│  │  WebRTC     │    │  WebRTC     │    │  WebRTC     │            │
│  │  PeerConnection│  │  PeerConnection│  │  PeerConnection│        │
│  │  (用户A)    │    │  (用户B)    │    │  (用户C)    │            │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘            │
│         │                  │                  │                     │
│         └────────┬─────────┴────────┬─────────┘                     │
│                  │                  │                              │
└──────────────────┼──────────────────┼──────────────────────────────┘
                   │                  │
┌──────────────────▼──────────────────▼──────────────────────────────┐
│                        信令服务层 (WebSocket)                       │
│  ┌─────────────────────────────────────────────────────┐           │
│  │           WebSocket Handler                          │           │
│  │  - 信令转发 (SDP/ICE)                               │           │
│  │  - 通话状态通知                                      │           │
│  │  - 用户在线状态管理                                  │           │
│  └─────────────────────────────────────────────────────┘           │
└──────────────────────────────┬─────────────────────────────────────┘
                               │
┌──────────────────────────────▼─────────────────────────────────────┐
│                        业务服务层                                 │
│  ┌─────────────────┐    ┌─────────────────┐                      │
│  │ VoiceCallController│  │ VideoCallController│                   │
│  │  - 通话发起/接受/拒绝/结束                          │           │
│  │  - SDP/ICE交换   │    │  - 视频流处理   │                      │
│  │  - 通话状态管理   │    │  - 屏幕共享     │                      │
│  └─────────────────┘    └─────────────────┘                      │
└──────────────────────────────┬─────────────────────────────────────┘
                               │
┌──────────────────────────────▼─────────────────────────────────────┐
│                        数据存储层                                  │
│  ┌───────────────┐    ┌───────────────┐    ┌───────────────┐     │
│  │   Redis       │    │   MySQL       │    │   Media Server│     │
│  │ - 通话状态     │    │ - 用户信息     │    │ - 媒体流转发   │     │
│  │ - 用户在线状态 │    │ - 通话记录     │    │ - NAT穿透     │     │
│  └───────────────┘    └───────────────┘    └───────────────┘     │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心组件说明

| 组件 | 职责 | 技术实现 |
|-----|------|---------|
| WebRTC PeerConnection | 音视频流采集、编码、传输 | browser WebRTC API |
| WebSocket Handler | 信令转发、状态同步 | Spring WebSocket |
| Call Controller | 通话业务逻辑 | Spring MVC |
| Redis | 通话状态缓存、在线状态 | Redis Cluster |
| Media Server | 可选：SFU/MCU部署 | Janus/GStreamer |

### 2.3 信令流程

```
通话发起方                        服务端                          通话接收方
    |                              |                               |
    |───[1] POST /call/initiate───>|                               |
    |                              |───[2] WS: VOICE_CALL_INIT───>|
    |                              |                               |
    |                              |<──[3] WS: CALL_ACCEPT───────|
    |<──[4] POST /call/accept──────|                               |
    |                              |                               |
    |───[5] POST /sdp-offer───────>|                               |
    |                              |───[6] WS: SDP_OFFER────────>|
    |                              |                               |
    |                              |<──[7] WS: SDP_ANSWER───────|
    |<──[8] POST /sdp-answer───────|                               |
    |                              |                               |
    |<──[9] ICE Candidates────────>|                               |
    |                              |<──[10] ICE Candidates──────|
    |                              |                               |
    |─────────────────────────────────────────[11] WebRTC P2P─────────────────────────>|
```

## 3. 后端实现

### 3.1 目录结构

```
src/main/java/com/minecraft/
├── controller/
│   ├── VoiceCallController.java    # 语音通话API
│   └── VideoCallController.java    # 视频通话API
├── handler/
│   ├── WebSocketHandler.java       # WebSocket信令处理
│   └── WebSocketInterceptor.java   # WebSocket拦截器
├── service/
│   └── CallService.java            # 通话业务服务
├── entity/
│   └── CallRecord.java             # 通话记录实体
├── dto/
│   ├── request/
│   │   ├── CallInitiateRequest.java
│   │   ├── SdpExchangeRequest.java
│   │   └── IceCandidateRequest.java
│   └── response/
│       ├── CallStatusResponse.java
│       └── CallRecordResponse.java
├── enums/
│   └── CallStatus.java             # 通话状态枚举
└── config/
    └── WebSocketConfig.java        # WebSocket配置
```

### 3.2 通话状态枚举

```java
public enum CallStatus {
    INITIATED("已发起"),
    RINGING("响铃中"),
    ACCEPTED("已接受"),
    CONNECTED("通话中"),
    ENDED("已结束"),
    REJECTED("已拒绝"),
    TIMEOUT("超时");
    
    private final String description;
    
    CallStatus(String description) {
        this.description = description;
    }
}
```

### 3.3 API 接口设计

#### 3.3.1 语音通话接口

| API路径 | HTTP方法 | Controller文件 | 功能描述 |
|--------|---------|---------------|---------|
| `/api/public/voice/call/initiate` | POST | VoiceCallController.java | 发起语音通话 |
| `/api/public/voice/call/accept` | POST | VoiceCallController.java | 接受语音通话 |
| `/api/public/voice/call/reject` | POST | VoiceCallController.java | 拒绝语音通话 |
| `/api/public/voice/call/end` | POST | VoiceCallController.java | 结束语音通话 |
| `/api/public/voice/call/sdp-offer` | POST | VoiceCallController.java | 发送SDP Offer |
| `/api/public/voice/call/sdp-answer` | POST | VoiceCallController.java | 发送SDP Answer |
| `/api/public/voice/call/ice-candidate` | POST | VoiceCallController.java | 发送ICE Candidate |
| `/api/public/voice/call/status` | GET | VoiceCallController.java | 获取通话状态 |

#### 3.3.2 视频通话接口

| API路径 | HTTP方法 | Controller文件 | 功能描述 |
|--------|---------|---------------|---------|
| `/api/public/video/call/initiate` | POST | VideoCallController.java | 发起视频通话 |
| `/api/public/video/call/accept` | POST | VideoCallController.java | 接受视频通话 |
| `/api/public/video/call/reject` | POST | VideoCallController.java | 拒绝视频通话 |
| `/api/public/video/call/end` | POST | VideoCallController.java | 结束视频通话 |
| `/api/public/video/call/sdp-offer` | POST | VideoCallController.java | 发送SDP Offer |
| `/api/public/video/call/sdp-answer` | POST | VideoCallController.java | 发送SDP Answer |
| `/api/public/video/call/ice-candidate` | POST | VideoCallController.java | 发送ICE Candidate |
| `/api/public/video/call/status` | GET | VideoCallController.java | 获取通话状态 |

#### 3.3.3 接口详细定义

**1. 发起通话**

请求:
```json
POST /api/public/{callType}/call/initiate
Content-Type: application/x-www-form-urlencoded

callerId=1&receiverId=2
```

响应:
```json
{
  "code": 200,
  "message": "通话请求发送成功",
  "data": {
    "callId": "uuid-string",
    "status": "initiated"
  }
}
```

**2. 接受通话**

请求:
```json
POST /api/public/{callType}/call/accept
Content-Type: application/x-www-form-urlencoded

callId=uuid-string&userId=2
```

响应:
```json
{
  "code": 200,
  "message": "通话已接受",
  "data": {
    "callId": "uuid-string",
    "status": "accepted"
  }
}
```

**3. 发送SDP Offer**

请求:
```json
POST /api/public/{callType}/call/sdp-offer
Content-Type: application/x-www-form-urlencoded

callId=uuid-string&sdp=base64-encoded-sdp
```

响应:
```json
{
  "code": 200,
  "message": "SDP Offer发送成功",
  "data": null
}
```

**4. 发送ICE Candidate**

请求:
```json
POST /api/public/{callType}/call/ice-candidate
Content-Type: application/x-www-form-urlencoded

callId=uuid-string&candidate=json-string&sdpMid=0&sdpMLineIndex=0
```

响应:
```json
{
  "code": 200,
  "message": "ICE Candidate发送成功",
  "data": null
}
```

**5. 获取通话状态**

请求:
```json
GET /api/public/{callType}/call/status?callId=uuid-string
```

响应:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "callId": "uuid-string",
    "callerId": 1,
    "receiverId": 2,
    "status": "connected",
    "sdpOffer": "...",
    "sdpAnswer": "...",
    "createTime": "2024-01-01T12:00:00"
  }
}
```

### 3.4 WebSocket 信令协议

#### 消息类型

| 消息类型 | 说明 | 触发场景 |
|---------|------|---------|
| `VOICE_CALL_INIT` | 语音通话请求 | 发起方向接收方发送通话请求 |
| `VOICE_CALL_ACCEPT` | 语音通话接受 | 接收方接受通话 |
| `VOICE_CALL_REJECT` | 语音通话拒绝 | 接收方拒绝通话 |
| `VOICE_CALL_END` | 语音通话结束 | 任一方结束通话 |
| `VIDEO_CALL_INIT` | 视频通话请求 | 发起方向接收方发送通话请求 |
| `VIDEO_CALL_ACCEPT` | 视频通话接受 | 接收方接受通话 |
| `VIDEO_CALL_REJECT` | 视频通话拒绝 | 接收方拒绝通话 |
| `VIDEO_CALL_END` | 视频通话结束 | 任一方结束通话 |
| `SDP_OFFER` | SDP Offer | 发起方发送SDP |
| `SDP_ANSWER` | SDP Answer | 接收方回复SDP |
| `ICE_CANDIDATE` | ICE候选 | 双方交换ICE |
| `CALL_TIMEOUT` | 通话超时 | 通话超时未响应 |

#### 消息格式

```json
{
  "type": "VOICE_CALL_INIT",
  "channel": "call",
  "data": {
    "callId": "uuid-string",
    "callerId": 1,
    "receiverId": 2,
    "callType": "voice",
    "timestamp": 1704067200000
  }
}
```

## 4. 前端实现

### 4.1 目录结构

```
src/
├── utils/
│   ├── webrtc.js           # WebRTC封装
│   └── websocket.js        # WebSocket封装
├── views/
│   └── ImChat/
│       ├── components/
│       │   ├── CallPanel.vue        # 通话面板
│       │   ├── VoiceCall.vue        # 语音通话组件
│       │   └── VideoCall.vue        # 视频通话组件
│       └── index.vue                # 聊天主页面
├── api/
│   └── call.js              # 通话API封装
└── stores/
    └── call.js              # 通话状态管理
```

### 4.2 WebRTC 工具类封装

```javascript
class WebRTCClient {
  constructor() {
    this.peerConnection = null
    this.localStream = null
    this.remoteStream = null
    this.callType = 'voice'
    this.callId = null
    this.callerId = null
    this.receiverId = null
    this.onStreamChange = null
    this.onCallStateChange = null
    this.iceCandidates = []
    
    this.configuration = {
      iceServers: [
        { urls: 'stun:stun.l.google.com:19302' },
        { urls: 'stun:stun1.l.google.com:19302' }
      ]
    }
  }

  async init(callType, callId, callerId, receiverId) {
    this.callType = callType
    this.callId = callId
    this.callerId = callerId
    this.receiverId = receiverId
    
    this.peerConnection = new RTCPeerConnection(this.configuration)
    
    this.peerConnection.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendIceCandidate(event.candidate)
      }
    }
    
    this.peerConnection.ontrack = (event) => {
      if (event.streams && event.streams[0]) {
        this.remoteStream = event.streams[0]
        this.onStreamChange && this.onStreamChange('remote', this.remoteStream)
      }
    }
    
    this.peerConnection.onconnectionstatechange = () => {
      const state = this.peerConnection.connectionState
      this.onCallStateChange && this.onCallStateChange(state)
      
      if (state === 'failed' || state === 'disconnected') {
        this.close()
      }
    }
    
    await this.getLocalMedia()
  }

  async getLocalMedia() {
    const constraints = {
      audio: true,
      video: this.callType === 'video'
    }
    
    try {
      this.localStream = await navigator.mediaDevices.getUserMedia(constraints)
      this.onStreamChange && this.onStreamChange('local', this.localStream)
      
      this.localStream.getTracks().forEach(track => {
        this.peerConnection.addTrack(track, this.localStream)
      })
    } catch (error) {
      console.error('Failed to get local media:', error)
      throw error
    }
  }

  async createOffer() {
    try {
      const offer = await this.peerConnection.createOffer()
      await this.peerConnection.setLocalDescription(offer)
      return offer
    } catch (error) {
      console.error('Failed to create offer:', error)
      throw error
    }
  }

  async createAnswer() {
    try {
      const answer = await this.peerConnection.createAnswer()
      await this.peerConnection.setLocalDescription(answer)
      return answer
    } catch (error) {
      console.error('Failed to create answer:', error)
      throw error
    }
  }

  async setRemoteDescription(sdp) {
    try {
      await this.peerConnection.setRemoteDescription(new RTCSessionDescription(sdp))
    } catch (error) {
      console.error('Failed to set remote description:', error)
      throw error
    }
  }

  async addIceCandidate(candidate) {
    try {
      if (this.peerConnection.signalingState !== 'closed') {
        await this.peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
      }
    } catch (error) {
      console.error('Failed to add ICE candidate:', error)
    }
  }

  async sendIceCandidate(candidate) {
    const data = {
      callId: this.callId,
      candidate: JSON.stringify(candidate),
      sdpMid: candidate.sdpMid,
      sdpMLineIndex: candidate.sdpMLineIndex
    }
    
    await fetch(`/api/public/${this.callType}/call/ice-candidate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams(data)
    })
  }

  close() {
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop())
      this.localStream = null
    }
    
    if (this.peerConnection) {
      this.peerConnection.close()
      this.peerConnection = null
    }
    
    this.remoteStream = null
    this.callId = null
    this.callerId = null
    this.receiverId = null
  }
}

export default WebRTCClient
```

### 4.3 通话API封装

```javascript
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
```

### 4.4 通话面板组件

```vue
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
  sendVideoSdpAnswer,
  sendVoiceIceCandidate,
  sendVideoIceCandidate
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
```

### 4.5 WebSocket 消息处理扩展

在现有的 `websocket.js` 中添加通话相关消息处理：

```javascript
handleMessage(data) {
  if (!data || !data.type) {
    console.warn('[WebSocket] Invalid message format:', data)
    return
  }

  const { type } = data
  
  switch (type) {
    case 'CONNECT':
      this.emit('connect-success', data)
      break
    case 'HEARTBEAT':
      this.emit('heartbeat', data)
      break
    case 'PRIVATE_MESSAGE':
      this.emit('private-message', data)
      break
    case 'GROUP_MESSAGE':
      this.emit('group-message', data)
      break
    case 'READ_RECEIPT':
      this.emit('read-receipt', data)
      break
    case 'TYPING':
      this.emit('typing', data)
      break
    case 'VOICE_CALL_INIT':
      this.emit('voice-call-init', data)
      break
    case 'VOICE_CALL_ACCEPT':
      this.emit('voice-call-accept', data)
      break
    case 'VOICE_CALL_REJECT':
      this.emit('voice-call-reject', data)
      break
    case 'VOICE_CALL_END':
      this.emit('voice-call-end', data)
      break
    case 'VIDEO_CALL_INIT':
      this.emit('video-call-init', data)
      break
    case 'VIDEO_CALL_ACCEPT':
      this.emit('video-call-accept', data)
      break
    case 'VIDEO_CALL_REJECT':
      this.emit('video-call-reject', data)
      break
    case 'VIDEO_CALL_END':
      this.emit('video-call-end', data)
      break
    case 'SDP_OFFER':
      this.emit('sdp-offer', data)
      break
    case 'SDP_ANSWER':
      this.emit('sdp-answer', data)
      break
    case 'ICE_CANDIDATE':
      this.emit('ice-candidate', data)
      break
    case 'CALL_TIMEOUT':
      this.emit('call-timeout', data)
      break
    default:
      this.emit('message', data)
  }
}
```

## 5. 数据库设计

### 5.1 通话记录表

```sql
CREATE TABLE `call_records` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `call_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '通话唯一标识',
  `caller_id` BIGINT NOT NULL COMMENT '发起方用户ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收方用户ID',
  `call_type` VARCHAR(10) NOT NULL COMMENT '通话类型: voice/video',
  `status` VARCHAR(20) NOT NULL COMMENT '通话状态',
  `duration` INT DEFAULT 0 COMMENT '通话时长(秒)',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME COMMENT '结束时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_call_id` (`call_id`),
  INDEX `idx_caller_id` (`caller_id`),
  INDEX `idx_receiver_id` (`receiver_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通话记录表';
```

### 5.2 通话状态实体

```java
@Data
@Entity
@Table(name = "call_records")
public class CallRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "call_id", nullable = false, unique = true, length = 64)
    private String callId;
    
    @Column(name = "caller_id", nullable = false)
    private Long callerId;
    
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;
    
    @Column(name = "call_type", nullable = false, length = 10)
    private String callType;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CallStatus status;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

## 6. 部署与配置

### 6.1 后端配置

在 `application.yml` 中添加以下配置：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/minecraft?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: admin
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: localhost
    port: 6379
    password:
    timeout: 6000ms

# WebSocket配置
websocket:
  endpoint: /ws
  allowed-origins: "*"

# WebRTC配置
webrtc:
  ice-servers:
    - url: stun:stun.l.google.com:19302
    - url: stun:stun1.l.google.com:19302
  call-timeout-seconds: 60
  max-call-duration-minutes: 120
```

### 6.2 前端环境变量

在 `.env.development` 和 `.env.production` 中配置：

```
VITE_API_BASE_URL=http://localhost:8080/api
VITE_API_WS_URL=ws://localhost:8080
```

## 7. 注意事项与最佳实践

### 7.1 安全注意事项

| 风险点 | 解决方案 |
|-------|---------|
| 身份伪造 | 使用JWT token验证WebSocket连接，校验通话双方身份 |
| 通话劫持 | 通话ID使用UUID，防止猜测攻击 |
| 媒体流泄露 | 通话结束后立即停止并释放媒体流 |
| XSS攻击 | 对所有输入内容进行HTML转义处理 |
| CSRF攻击 | 使用Token验证所有API请求 |

### 7.2 WebRTC注意事项

1. **HTTPS要求**: WebRTC API在非HTTPS环境下可能受限，生产环境必须使用HTTPS
2. **STUN/TURN服务器**: 部署公共STUN服务器，考虑部署TURN服务器应对NAT穿透问题
3. **权限管理**: 明确请求用户媒体权限，提供友好的权限拒绝处理
4. **错误处理**: 完善的错误处理机制，包括媒体设备不可用、连接失败等场景
5. **资源释放**: 通话结束后必须释放PeerConnection和媒体流资源

### 7.3 性能优化

1. **视频分辨率适配**: 根据网络状况动态调整视频分辨率
2. **带宽检测**: 通话前检测网络带宽，选择合适的编码参数
3. **音频降噪**: 使用WebRTC内置降噪功能，提升通话质量
4. **断线重连**: 实现自动重连机制，提升用户体验
5. **缓存策略**: 合理使用Redis缓存通话状态，减少数据库查询

### 7.4 兼容性考虑

| 平台 | WebRTC支持情况 | 注意事项 |
|-----|---------------|---------|
| Chrome | 完全支持 | 推荐使用 |
| Firefox | 完全支持 | 推荐使用 |
| Safari | 支持 | 需要HTTPS |
| Edge | 支持 | 基于Chromium |
| iOS Safari | 支持 | 需要HTTPS，权限提示不同 |

## 8. 故障排查指南

### 8.1 常见问题

| 问题现象 | 可能原因 | 排查方法 |
|---------|---------|---------|
| 无法建立连接 | 网络不通、STUN服务器不可用 | 检查网络、验证STUN配置 |
| 无声音/无画面 | 权限未授予、设备不可用 | 检查浏览器权限、测试设备 |
| 通话卡顿 | 网络带宽不足 | 检测网络质量、降低分辨率 |
| 连接断开 | 心跳超时、网络切换 | 检查WebSocket心跳、实现重连 |
| ICE候选收集失败 | 防火墙限制、NAT类型限制 | 检查网络策略、使用TURN服务器 |

### 8.2 日志监控

建议在以下位置添加日志：

1. **WebSocket连接日志**: 记录连接建立、断开、重连事件
2. **通话状态日志**: 记录通话发起、接受、结束状态变化
3. **WebRTC日志**: 记录SDP交换、ICE候选收集过程
4. **错误日志**: 记录所有异常情况，便于问题定位

---

**文档版本**: v1.0  
**创建日期**: 2024年  
**适用项目**: Minecraft 旅游社交系统