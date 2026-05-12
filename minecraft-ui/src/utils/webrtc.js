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