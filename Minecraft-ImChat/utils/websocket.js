/**
 * WebSocket 服务 - 用于实时消息推送
 */

const WS_BASE_URL = 'wss://api.mc-system.com/ws'

class WebSocketService {
  constructor() {
    this.socket = null
    this.isConnected = false
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
    this.heartbeatInterval = null
    this.listeners = new Map()
    this.userId = null
    this.messageQueue = []
  }

  /**
   * 连接到 WebSocket 服务器
   */
  connect(userId) {
    if (this.socket && this.isConnected) {
      console.log('[WS] Already connected')
      return
    }

    this.userId = userId
    const token = uni.getStorageSync('token') || ''

    console.log('[WS] Connecting to:', `${WS_BASE_URL}?userId=${userId}&token=${token}`)

    this.socket = uni.connectSocket({
      url: `${WS_BASE_URL}?userId=${userId}&token=${token}`,
      success: () => {
        console.log('[WS] Connection initiated')
      },
      fail: (err) => {
        console.error('[WS] Connection failed:', err)
        this.handleReconnect()
      }
    })

    // 监听连接打开
    this.socket.onOpen(() => {
      console.log('[WS] Connected successfully')
      this.isConnected = true
      this.reconnectAttempts = 0
      this.emit('connect', { userId: this.userId })
      this.startHeartbeat()

      // 发送队列中的消息
      this.flushMessageQueue()
    })

    // 监听消息
    this.socket.onMessage((res) => {
      try {
        const data = JSON.parse(res.data)
        console.log('[WS] Received message:', data)
        this.handleMessage(data)
      } catch (e) {
        console.error('[WS] Failed to parse message:', e)
      }
    })

    // 监听连接关闭
    this.socket.onClose(() => {
      console.log('[WS] Connection closed')
      this.isConnected = false
      this.stopHeartbeat()
      this.emit('disconnect', { userId: this.userId })
      this.handleReconnect()
    })

    // 监听错误
    this.socket.onError((err) => {
      console.error('[WS] Error:', err)
      this.emit('error', err)
    })
  }

  /**
   * 断开连接
   */
  disconnect() {
    this.stopHeartbeat()
    if (this.socket) {
      this.socket.close({
        success: () => {
          console.log('[WS] Disconnected manually')
        }
      })
      this.socket = null
    }
    this.isConnected = false
    this.reconnectAttempts = 0
    this.messageQueue = []
  }

  /**
   * 重新连接
   */
  handleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.log('[WS] Max reconnect attempts reached')
      uni.showToast({
        title: '网络连接失败，请检查网络',
        icon: 'none'
      })
      return
    }

    this.reconnectAttempts++
    console.log(`[WS] Reconnecting... Attempt ${this.reconnectAttempts}`)

    setTimeout(() => {
      if (this.userId) {
        this.connect(this.userId)
      }
    }, this.reconnectDelay)
  }

  /**
   * 发送消息
   */
  send(data) {
    if (!this.isConnected) {
      console.log('[WS] Not connected, queuing message')
      this.messageQueue.push(data)
      return false
    }

    const message = JSON.stringify(data)
    this.socket.send({
      data: message,
      fail: (err) => {
        console.error('[WS] Send failed:', err)
        this.messageQueue.push(data)
      }
    })
    return true
  }

  /**
   * 发送私聊消息
   */
  sendPrivateMessage(senderId, receiverId, content, messageType = 'text') {
    return this.send({
      type: 'private-message',
      senderId,
      receiverId,
      content,
      messageType,
      timestamp: Date.now()
    })
  }

  /**
   * 发送群聊消息
   */
  sendGroupMessage(groupId, senderId, content, messageType = 'text') {
    return this.send({
      type: 'group-message',
      groupId,
      senderId,
      content,
      messageType,
      timestamp: Date.now()
    })
  }

  /**
   * 发送已读回执
   */
  sendReadReceipt(messageId, userId) {
    return this.send({
      type: 'read-receipt',
      messageId,
      userId,
      timestamp: Date.now()
    })
  }

  /**
   * 发送正在输入状态
   */
  sendTyping(senderId, receiverId) {
    return this.send({
      type: 'typing',
      senderId,
      receiverId,
      timestamp: Date.now()
    })
  }

  /**
   * 处理接收到的消息
   */
  handleMessage(data) {
    const { type } = data

    switch (type) {
      case 'private-message':
        this.emit('private-message', data)
        break
      case 'group-message':
        this.emit('group-message', data)
        break
      case 'read-receipt':
        this.emit('read-receipt', data)
        break
      case 'typing':
        this.emit('typing', data)
        break
      case 'friend-request':
        this.emit('friend-request', data)
        break
      case 'friend-request-accepted':
        this.emit('friend-request-accepted', data)
        break
      case 'group-invite':
        this.emit('group-invite', data)
        break
      case 'pong':
        // 心跳响应，无需处理
        break
      default:
        console.log('[WS] Unknown message type:', type)
    }
  }

  /**
   * 添加事件监听器
   */
  on(event, callback) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, [])
    }
    this.listeners.get(event).push(callback)
  }

  /**
   * 移除事件监听器
   */
  off(event, callback) {
    if (!this.listeners.has(event)) return

    const callbacks = this.listeners.get(event)
    const index = callbacks.indexOf(callback)
    if (index > -1) {
      callbacks.splice(index, 1)
    }
  }

  /**
   * 触发事件
   */
  emit(event, data) {
    if (!this.listeners.has(event)) return

    this.listeners.get(event).forEach(callback => {
      try {
        callback(data)
      } catch (e) {
        console.error(`[WS] Event handler error (${event}):`, e)
      }
    })
  }

  /**
   * 开始心跳
   */
  startHeartbeat() {
    this.heartbeatInterval = setInterval(() => {
      if (this.isConnected) {
        this.send({ type: 'ping' })
      }
    }, 30000)
  }

  /**
   * 停止心跳
   */
  stopHeartbeat() {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval)
      this.heartbeatInterval = null
    }
  }

  /**
   * 发送队列中的消息
   */
  flushMessageQueue() {
    while (this.messageQueue.length > 0) {
      const message = this.messageQueue.shift()
      this.send(message)
    }
  }

  /**
   * 获取连接状态
   */
  getStatus() {
    return {
      isConnected: this.isConnected,
      reconnectAttempts: this.reconnectAttempts,
      queuedMessages: this.messageQueue.length
    }
  }
}

// 创建单例实例
const wsService = new WebSocketService()

export default wsService
