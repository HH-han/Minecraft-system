package com.minecraft.im.data.remote.ws

/** WebSocket 上行/下行事件，与后端 WebSocketHandler 的 type 字段一一对应 */
sealed interface WsEvent {
    data object Connected : WsEvent

    data class PrivateMessage(
        val senderId: Long,
        val receiverId: Long,
        val content: String,
        val messageType: String,
        val timestamp: Long,
        val senderName: String?,
        val senderAvatar: String?
    ) : WsEvent

    data class GroupMessage(
        val groupId: Long,
        val senderId: Long,
        val content: String,
        val messageType: String,
        val timestamp: Long,
        val senderName: String?,
        val senderAvatar: String?
    ) : WsEvent

    data class ReadReceipt(val readerId: Long, val messageId: Long) : WsEvent

    data class Typing(val senderId: Long, val chatType: String?) : WsEvent

    data class Disconnected(val code: Int, val reason: String) : WsEvent

    data class Unknown(val type: String) : WsEvent
}
