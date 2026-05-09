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
    this.isClosing = false
    this.userId = null
  }

  connect(userId) {
    const token = getToken()
    if (!token) {
      console.error('[WebSocket] No token available for WebSocket connection')
      return
    }

    if (!userId || typeof userId !== 'number') {
      console.error('[WebSocket] Invalid userId:', userId)
      return
    }

    this.userId = userId

    const baseURL = import.meta.env.VITE_API_WS_URL || import.meta.env.VITE_API_BASE_URL?.replace('/api', '') || 'ws://localhost:8080'
    this.url = `${baseURL}/ws?token=${encodeURIComponent(token)}`

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      console.log('[WebSocket] Already connected')
      return
    }

    if (this.ws && this.ws.readyState !== WebSocket.CLOSED) {
      this.isClosing = true
      this.ws.close(1001, 'Reconnecting')
      this.ws = null
    }

    try {
      this.ws = new WebSocket(this.url)

      this.ws.onopen = () => {
        console.log('[WebSocket] Connected successfully')
        this.isConnected = true
        this.isClosing = false
        this.reconnectAttempts = 0
        this.emit('connect', { userId })
        this.startHeartbeat()
      }

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          console.log('[WebSocket] Received message:', data)
          this.handleMessage(data)
        } catch (e) {
          console.error('[WebSocket] Failed to parse message:', e, event.data)
        }
      }

      this.ws.onerror = (error) => {
        console.error('[WebSocket] Error:', error)
        this.emit('error', error)
      }

      this.ws.onclose = (event) => {
        console.log('[WebSocket] Closed:', event.code, event.reason)
        this.isConnected = false
        this.stopHeartbeat()
        this.emit('disconnect', { code: event.code, reason: event.reason })
        
        if (!this.isClosing && event.code !== 1000 && this.reconnectAttempts < this.maxReconnectAttempts) {
          this.reconnect()
        }
        this.isClosing = false
      }
    } catch (error) {
      console.error('[WebSocket] Failed to create connection:', error)
    }
  }

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
      default:
        this.emit('message', data)
    }
  }

  startHeartbeat() {
    this.stopHeartbeat()

    this.heartbeatInterval = setInterval(() => {
      this.send({
        type: 'HEARTBEAT'
      })
    }, 30000)

    this.heartbeatTimeout = setTimeout(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        console.warn('[WebSocket] Heartbeat timeout, reconnecting')
        this.isClosing = false
        this.ws.close(1002, 'Heartbeat timeout')
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

  reconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('[WebSocket] Max reconnection attempts reached')
      return
    }

    this.reconnectAttempts++
    console.log(`[WebSocket] Reconnection attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts}`)
    
    setTimeout(() => {
      this.connect(this.userId)
    }, this.reconnectDelay * this.reconnectAttempts)
  }

  send(data) {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.warn('[WebSocket] Not connected, cannot send:', data)
      return false
    }
    try {
      this.ws.send(JSON.stringify(data))
      console.log('[WebSocket] Sent:', data)
      return true
    } catch (error) {
      console.error('[WebSocket] Failed to send:', error)
      return false
    }
  }

  sendPrivateMessage(senderId, receiverId, content, messageType = 'text') {
    if (!senderId || !receiverId) {
      console.error('[WebSocket] Invalid senderId or receiverId:', senderId, receiverId)
      return false
    }
    return this.send({
      type: 'PRIVATE_MESSAGE',
      senderId,
      receiverId,
      content,
      messageType
    })
  }

  sendGroupMessage(groupId, senderId, content, messageType = 'text') {
    if (!groupId || !senderId) {
      console.error('[WebSocket] Invalid groupId or senderId:', groupId, senderId)
      return false
    }
    return this.send({
      type: 'GROUP_MESSAGE',
      groupId,
      senderId,
      content,
      messageType
    })
  }

  sendReadReceipt(senderId, readerId, messageId) {
    return this.send({
      type: 'READ_RECEIPT',
      senderId,
      readerId,
      messageId
    })
  }

  sendTyping(senderId, receiverId, chatType = 'private') {
    return this.send({
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
      this.listeners.get(event).forEach(callback => {
        try {
          callback(data)
        } catch (error) {
          console.error(`[WebSocket] Error in ${event} listener:`, error)
        }
      })
    }
  }

  disconnect() {
    this.isClosing = true
    this.stopHeartbeat()
    if (this.ws) {
      this.ws.close(1000, 'User logout')
      this.ws = null
    }
    this.isConnected = false
    this.listeners.clear()
    this.userId = null
  }
}

const wsService = new WebSocketService()

export default wsService