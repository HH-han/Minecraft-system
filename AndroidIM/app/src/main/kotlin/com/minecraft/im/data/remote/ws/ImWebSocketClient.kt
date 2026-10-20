package com.minecraft.im.data.remote.ws

import com.minecraft.im.core.webSocketUrl
import com.minecraft.im.data.SessionManager
import com.minecraft.im.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 原生 WebSocket 客户端：JSON 文本帧 + 30s 心跳 + 断线自动重连。
 * 发送消息走 REST，WebSocket 只负责接收实时事件与回执。
 */
@Singleton
class ImWebSocketClient @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json,
    private val session: SessionManager,
    @ApplicationScope private val scope: CoroutineScope
) {
    private val _events = MutableSharedFlow<WsEvent>(
        replay = 0,
        extraBufferCapacity = 128,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<WsEvent> = _events.asSharedFlow()

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private var webSocket: WebSocket? = null
    private var heartbeatJob: Job? = null
    private var reconnectJob: Job? = null
    private val lastMessageAt = AtomicLong(0)
    private var manualClose = false
    private var reconnectAttempts = 0

    @Volatile
    private var currentToken: String? = null

    fun connect(token: String) {
        if (token.isBlank()) return
        if (_connected.value && currentToken == token && webSocket != null) return
        currentToken = token
        manualClose = false
        reconnectAttempts = 0
        reconnectJob?.cancel()
        webSocket?.close(1001, "Reconnecting")
        webSocket = null
        open(token)
    }

    private fun open(token: String) {
        val url = webSocketUrl(session.currentBaseUrl, token)
        val request = Request.Builder().url(url).build()
        lastMessageAt.set(System.currentTimeMillis())
        webSocket = client.newWebSocket(request, listener)
    }

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connected.value = true
            reconnectAttempts = 0
            _events.tryEmit(WsEvent.Connected)
            startHeartbeat()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            lastMessageAt.set(System.currentTimeMillis())
            parse(text)?.let { _events.tryEmit(it) }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            handleDisconnected(-1, t.message ?: "连接失败")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            handleDisconnected(code, reason)
        }
    }

    private fun handleDisconnected(code: Int, reason: String) {
        _connected.value = false
        stopHeartbeat()
        _events.tryEmit(WsEvent.Disconnected(code, reason))
        if (!manualClose && code != 1000 && reconnectAttempts < MAX_RECONNECT) {
            reconnectAttempts++
            reconnectJob?.cancel()
            reconnectJob = scope.launch {
                delay(3000L * reconnectAttempts)
                currentToken?.let { open(it) }
            }
        }
    }

    private fun startHeartbeat() {
        stopHeartbeat()
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(HEARTBEAT_INTERVAL)
                if (!_connected.value) break
                sendJson(buildJsonObject { put("type", "HEARTBEAT") })
                val idle = System.currentTimeMillis() - lastMessageAt.get()
                if (idle > HEARTBEAT_TIMEOUT) {
                    webSocket?.close(1002, "Heartbeat timeout")
                    break
                }
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    fun disconnect() {
        manualClose = true
        stopHeartbeat()
        reconnectJob?.cancel()
        reconnectJob = null
        webSocket?.close(1000, "User logout")
        webSocket = null
        _connected.value = false
    }

    /** 上报已读回执（后端会转发给原发送者） */
    fun sendReadReceipt(senderId: Long, readerId: Long, messageId: Long) {
        sendJson(
            buildJsonObject {
                put("type", "READ_RECEIPT")
                put("senderId", senderId)
                put("readerId", readerId)
                put("messageId", messageId)
            }
        )
    }

    /** 正在输入状态 */
    fun sendTyping(senderId: Long, receiverId: Long, chatType: String = "private") {
        sendJson(
            buildJsonObject {
                put("type", "TYPING")
                put("senderId", senderId)
                put("receiverId", receiverId)
                put("chatType", chatType)
            }
        )
    }

    private fun sendJson(payload: JsonObject): Boolean {
        val socket = webSocket ?: return false
        return runCatching { socket.send(payload.toString()) }.getOrDefault(false)
    }

    private fun parse(text: String): WsEvent? {
        val obj = runCatching { json.parseToJsonElement(text).jsonObject }.getOrNull() ?: return null
        return when (val type = obj.string("type")) {
            "PRIVATE_MESSAGE" -> WsEvent.PrivateMessage(
                senderId = obj.long("senderId"),
                receiverId = obj.long("receiverId"),
                content = obj.string("content") ?: "",
                messageType = obj.string("messageType") ?: "text",
                timestamp = obj.long("timestamp").takeIf { it > 0 } ?: System.currentTimeMillis(),
                senderName = obj.string("senderName"),
                senderAvatar = obj.string("senderAvatar")
            )

            "GROUP_MESSAGE" -> WsEvent.GroupMessage(
                groupId = obj.long("groupId"),
                senderId = obj.long("senderId"),
                content = obj.string("content") ?: "",
                messageType = obj.string("messageType") ?: "text",
                timestamp = obj.long("timestamp").takeIf { it > 0 } ?: System.currentTimeMillis(),
                senderName = obj.string("senderName"),
                senderAvatar = obj.string("senderAvatar")
            )

            "READ_RECEIPT" -> WsEvent.ReadReceipt(
                readerId = obj.long("readerId"),
                messageId = obj.long("messageId")
            )

            "TYPING" -> WsEvent.Typing(obj.long("senderId"), obj.string("chatType"))
            "CONNECT", "HEARTBEAT" -> null
            null -> null
            else -> WsEvent.Unknown(type)
        }
    }

    private fun JsonObject.string(key: String): String? =
        this[key]?.jsonPrimitive?.contentOrNull

    private fun JsonObject.long(key: String): Long =
        this[key]?.jsonPrimitive?.longOrNull
            ?: this[key]?.jsonPrimitive?.contentOrNull?.toLongOrNull()
            ?: 0L

    private companion object {
        const val MAX_RECONNECT = 5
        const val HEARTBEAT_INTERVAL = 30_000L
        const val HEARTBEAT_TIMEOUT = 90_000L
    }
}
