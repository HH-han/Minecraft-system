import { getToken } from './storage'

class WebSocketService {
  constructor() {
    this.ws = null
    this.url = ''
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
    this.heartbeatInterval = null
    this.heartbeatTimeout = null
    this.listeners = new Map()
    this.isConnected = false
  }

  connect(userId) {
    const token = getToken()
    if (!token) {
      console.error('No token available for WebSocket connection')
      return
    }

    const baseURL = import.meta.env.VITE_API_WS_URL || import.meta.env.VITE_API_BASE_URL?.replace('/api', '') || 'ws://localhost:8080'
    this.url = `${baseURL}/ws?token=${encodeURIComponent(token)}`

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      console.log('WebSocket already connected')
      return
    }

    try {
      this.ws = new WebSocket(this.url)

      this.ws.onopen = () => {
        console.log('WebSocket connected')
        this.isConnected = true
        this.reconnectAttempts = 0
        this.emit('connect', { userId })
        this.startHeartbeat()
      }

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          this.handleMessage(data)
        } catch (e) {
          console.error('Failed to parse WebSocket message:', e)
        }
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error)
        this.emit('error', error)
      }

      this.ws.onclose = (event) => {
        console.log('WebSocket closed:', event.code, event.reason)
        this.isConnected = false
        this.stopHeartbeat()
        this.emit('disconnect', { code: event.code, reason: event.reason })
        
        if (event.code !== 1000 && this.reconnectAttempts < this.maxReconnectAttempts) {
          this.reconnect(userId)
        }
      }
    } catch (error) {
      console.error('Failed to create WebSocket:', error)
    }
  }

  handleMessage(data) {
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
      default:
        this.emit('message', data)
    }
  }

  startHeartbeat() {
    this.heartbeatInterval = setInterval(() => {
      this.send({
        type: 'HEARTBEAT'
      })
    }, 30000)

    this.heartbeatTimeout = setTimeout(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.close()
      }
    }, 90000)
  }

  stopHeartbeat() {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval)
      this.heartbeatInterval = null
    }
    if (this.heartbeatTimeout) {
      clearTimeout(this.heartbeatTimeout)
      this.heartbeatTimeout = null
    }
  }

  reconnect(userId) {
    this.reconnectAttempts++
    console.log(`WebSocket reconnection attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts}`)
    
    setTimeout(() => {
      this.connect(userId)
    }, this.reconnectDelay)
  }

  send(data) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data))
    } else {
      console.warn('WebSocket is not connected')
    }
  }

  sendPrivateMessage(senderId, receiverId, content, messageType = 'text') {
    this.send({
      type: 'PRIVATE_MESSAGE',
      senderId,
      receiverId,
      content,
      messageType
    })
  }

  sendGroupMessage(groupId, senderId, content, messageType = 'text') {
    this.send({
      type: 'GROUP_MESSAGE',
      groupId,
      senderId,
      content,
      messageType
    })
  }

  sendReadReceipt(senderId, readerId, messageId) {
    this.send({
      type: 'READ_RECEIPT',
      senderId,
      readerId,
      messageId
    })
  }

  sendTyping(senderId, receiverId, chatType = 'private') {
    this.send({
      type: 'TYPING',
      senderId,
      receiverId,
      chatType
    })
  }

  on(event, callback) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, [])
    }
    this.listeners.get(event).push(callback)
  }

  off(event, callback) {
    if (this.listeners.has(event)) {
      const callbacks = this.listeners.get(event)
      const index = callbacks.indexOf(callback)
      if (index > -1) {
        callbacks.splice(index, 1)
      }
    }
  }

  emit(event, data) {
    if (this.listeners.has(event)) {
      this.listeners.get(event).forEach(callback => callback(data))
    }
  }

  disconnect() {
    this.stopHeartbeat()
    if (this.ws) {
      this.ws.close(1000, 'User logout')
      this.ws = null
    }
    this.isConnected = false
    this.listeners.clear()
  }
}

const wsService = new WebSocketService()

export default wsService
