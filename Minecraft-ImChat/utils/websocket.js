/**
 * WebSocket 服务 - 用于实时消息推送
 *
 * 设计要点（修复连接反复断开/连接泄漏/跨端兼容）：
 * 1. 单连接保证：存在连接（连接中或已连接）时不再重复创建，避免孤儿连接堆积；
 * 2. 主动关闭标记 manualClose：登出等主动断开不再触发自动重连；
 * 3. 重连退避 + 单一定时器：避免多条重连链同时建立连接；
 * 4. 心跳与后端协议一致：type 使用 "HEARTBEAT"，并做超时看门狗；
 * 5. 事件名转换：后端 type 为大写下划线，转换为前端小写中划线事件名；
 * 6. 跨端自适应：优先使用 uni.connectSocket 返回的 SocketTask（App/小程序），
 *    H5 下其返回 Promise，则回退到 uni.onSocketXxx 全局事件 API。
 */

import { WS_CONFIG } from './config.js'
import { getToken } from './storage.js'

const WS_BASE_URL = WS_CONFIG.URL

// 后端 type -> 前端事件名
const EVENT_MAP = {
  CONNECT: 'ready',
  HEARTBEAT: 'heartbeat',
  PRIVATE_MESSAGE: 'private-message',
  GROUP_MESSAGE: 'group-message',
  READ_RECEIPT: 'read-receipt',
  TYPING: 'typing',
  VOICE_CALL: 'voice-call',
  VIDEO_CALL: 'video-call',
  SDP_OFFER: 'sdp-offer',
  SDP_ANSWER: 'sdp-answer',
  ICE_CANDIDATE: 'ice-candidate',
  CALL_TIMEOUT: 'call-timeout',
  FRIEND_REQUEST: 'friend-request',
  FRIEND_REQUEST_ACCEPTED: 'friend-request-accepted',
  GROUP_INVITE: 'group-invite'
}

function toEventName(type) {
  if (!type) return 'unknown'
  if (EVENT_MAP[type]) return EVENT_MAP[type]
  return String(type).toLowerCase().replace(/_/g, '-')
}

// 统一异常/失败兜底，避免 H5 下未捕获的 Promise 拒绝
function swallow(result) {
  if (result && typeof result.catch === 'function') {
    result.catch(() => {})
  }
  return result
}

class WebSocketService {
  constructor() {
    this.task = null
    this.active = false
    this.isConnected = false
    this.connecting = false
    this.manualClose = false
    this.useGlobalApi = false
    this.globalBound = false
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = WS_CONFIG.MAX_RECONNECT_ATTEMPTS || 5
    this.reconnectDelay = WS_CONFIG.RECONNECT_INTERVAL || 3000
    this.heartbeatTimer = null
    this.lastHeartbeatAt = 0
    this.reconnectTimer = null
    this.listeners = new Map()
    this.userId = null
    this.messageQueue = []
    this.reconnectFailNotified = false
  }

  /**
   * 建立连接（幂等）
   */
  connect(userId) {
    if (userId === undefined || userId === null || userId === '') return

    if (this.userId && String(this.userId) !== String(userId)) {
      this.closeSocket(true)
    }

    this.userId = userId
    this.manualClose = false

    if (this.active) return
    this.openSocket()
  }

  /**
   * 创建底层连接（兼容 SocketTask 与全局事件 API）
   */
  async openSocket() {
    this.connecting = true
    this.active = true
    const token = getToken() || ''
    const url = `${WS_BASE_URL}?token=${encodeURIComponent(token)}&userId=${this.userId}`
    const sessionId = Date.now() + Math.random()
    this.sessionId = sessionId

    let task = null
    let failed = false
    try {
      const ret = uni.connectSocket({ url })
      if (ret && typeof ret.onOpen === 'function') {
        task = ret
      } else if (ret && typeof ret.then === 'function') {
        task = await ret
      }
    } catch (e) {
      failed = true
    }

    if (!this.active || this.sessionId !== sessionId) return

    // 连接建立失败：直接进入重连流程
    if (failed && !task) {
      this.connecting = false
      this.active = false
      this.handleReconnect()
      return
    }

    if (task && typeof task.onOpen === 'function') {
      this.task = task
      this.useGlobalApi = false
      task.onOpen(() => this.handleOpen(sessionId))
      task.onMessage((res) => this.handleRawMessage(res, sessionId))
      task.onClose(() => this.handleClose(sessionId))
      task.onError((err) => this.emit('error', err))
    } else {
      // H5 等场景：使用全局 socket 事件 API
      this.task = null
      this.useGlobalApi = true
      this.bindGlobalSocketEvents()
    }
  }

  bindGlobalSocketEvents() {
    if (this.globalBound) return
    this.globalBound = true
    // 若已拿到 SocketTask，则忽略全局事件，避免重复处理
    if (typeof uni.onSocketOpen === 'function') {
      uni.onSocketOpen(() => {
        if (this.task) return
        this.handleOpen(this.sessionId)
      })
    }
    if (typeof uni.onSocketMessage === 'function') {
      uni.onSocketMessage((res) => {
        if (this.task) return
        this.handleRawMessage(res, this.sessionId)
      })
    }
    if (typeof uni.onSocketClose === 'function') {
      uni.onSocketClose(() => {
        if (this.task) return
        this.handleClose(this.sessionId)
      })
    }
    if (typeof uni.onSocketError === 'function') {
      uni.onSocketError((err) => {
        if (this.task) return
        this.emit('error', err)
      })
    }
  }

  handleOpen(sessionId) {
    if (!this.active || this.sessionId !== sessionId || this.isConnected) return
    this.connecting = false
    this.isConnected = true
    this.reconnectAttempts = 0
    this.reconnectFailNotified = false
    this.lastHeartbeatAt = Date.now()
    this.emit('connect', { userId: this.userId })
    this.startHeartbeat()
    this.flushMessageQueue()
  }

  handleRawMessage(res, sessionId) {
    if (!this.active || this.sessionId !== sessionId) return
    try {
      const data = JSON.parse(res && res.data)
      this.handleMessage(data)
    } catch (e) {
      // 非 JSON 消息忽略
    }
  }

  handleClose(sessionId) {
    if (!this.active || this.sessionId !== sessionId) return
    this.active = false
    this.task = null
    this.connecting = false
    this.isConnected = false
    this.stopHeartbeat()
    this.emit('disconnect', { userId: this.userId })
    if (!this.manualClose) {
      this.handleReconnect()
    }
  }

  /**
   * 关闭当前连接
   * @param {Boolean} manual 是否主动关闭（主动关闭不触发重连）
   */
  closeSocket(manual = true) {
    if (manual) this.manualClose = true
    this.active = false
    this.stopHeartbeat()
    const task = this.task
    this.task = null
    this.connecting = false
    this.isConnected = false
    if (task) {
      try {
        swallow(task.close({}))
      } catch (e) {
        // 忽略
      }
    } else if (this.useGlobalApi && typeof uni.closeSocket === 'function') {
      try {
        swallow(uni.closeSocket({}))
      } catch (e) {
        // 忽略
      }
    }
  }

  /**
   * 断开连接（登出等场景）
   */
  disconnect() {
    this.manualClose = true
    this.reconnectAttempts = 0
    this.userId = null
    this.messageQueue = []
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    this.closeSocket(true)
  }

  /**
   * 自动重连（单定时器 + 递增退避）
   */
  handleReconnect() {
    if (this.manualClose || !this.userId) return

    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      if (!this.reconnectFailNotified) {
        this.reconnectFailNotified = true
        uni.showToast({ title: '网络连接失败，请检查网络', icon: 'none' })
      }
      return
    }

    this.reconnectAttempts += 1
    const delay = this.reconnectDelay * this.reconnectAttempts

    if (this.reconnectTimer) clearTimeout(this.reconnectTimer)
    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = null
      if (!this.manualClose && this.userId && !this.active) {
        this.openSocket()
      }
    }, delay)
  }

  /**
   * 发送数据（未连接时入队）
   */
  send(data) {
    if (!this.isConnected) {
      this.messageQueue.push(data)
      return false
    }
    try {
      const text = JSON.stringify(data)
      if (this.task) {
        swallow(
          this.task.send({
            data: text,
            fail: () => this.messageQueue.push(data)
          })
        )
      } else if (typeof uni.sendSocketMessage === 'function') {
        swallow(
          uni.sendSocketMessage({
            data: text,
            fail: () => this.messageQueue.push(data)
          })
        )
      } else {
        return false
      }
      return true
    } catch (e) {
      this.messageQueue.push(data)
      return false
    }
  }

  sendPrivateMessage(senderId, receiverId, content, messageType = 'TEXT') {
    return this.send({ type: 'PRIVATE_MESSAGE', senderId, receiverId, content, messageType })
  }

  sendGroupMessage(groupId, senderId, content, messageType = 'TEXT') {
    return this.send({ type: 'GROUP_MESSAGE', groupId, senderId, content, messageType })
  }

  sendReadReceipt(senderId, readerId, messageId) {
    return this.send({ type: 'READ_RECEIPT', senderId, readerId, messageId })
  }

  sendTyping(senderId, receiverId, chatType = 'single') {
    return this.send({ type: 'TYPING', senderId, receiverId, chatType })
  }

  /**
   * 处理服务端消息：按后端 type 分发为前端事件
   */
  handleMessage(data) {
    const type = data && data.type
    if (!type) return
    if (type === 'HEARTBEAT') {
      this.lastHeartbeatAt = Date.now()
    }
    this.emit(toEventName(type), data)
  }

  on(event, callback) {
    if (typeof callback !== 'function') return
    if (!this.listeners.has(event)) {
      this.listeners.set(event, [])
    }
    this.listeners.get(event).push(callback)
  }

  off(event, callback) {
    if (!this.listeners.has(event)) return
    const callbacks = this.listeners.get(event)
    const index = callbacks.indexOf(callback)
    if (index > -1) callbacks.splice(index, 1)
  }

  emit(event, data) {
    if (!this.listeners.has(event)) return
    this.listeners.get(event).forEach((callback) => {
      try {
        callback(data)
      } catch (e) {
        // 单个监听器异常不影响其它监听器
      }
    })
  }

  /**
   * 心跳：定周期发送 HEARTBEAT；超过 2 个周期无任何响应则判定掉线并重连
   */
  startHeartbeat() {
    this.stopHeartbeat()
    const interval = WS_CONFIG.HEARTBEAT_INTERVAL || 30000
    this.heartbeatTimer = setInterval(() => {
      if (!this.isConnected) return
      if (this.lastHeartbeatAt && Date.now() - this.lastHeartbeatAt > interval * 2) {
        this.closeSocket(false)
        this.handleReconnect()
        return
      }
      this.send({ type: 'HEARTBEAT' })
    }, interval)
  }

  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  flushMessageQueue() {
    if (!this.isConnected) return
    const queue = this.messageQueue.slice()
    this.messageQueue = []
    queue.forEach((message) => this.send(message))
  }

  getStatus() {
    return {
      isConnected: this.isConnected,
      connecting: this.connecting,
      reconnectAttempts: this.reconnectAttempts,
      queuedMessages: this.messageQueue.length
    }
  }
}

// 单例
const wsService = new WebSocketService()

export default wsService
