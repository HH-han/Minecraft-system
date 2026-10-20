package com.minecraft.im.domain.model

/** 会话/联系人：好友与群聊共用同一模型，用 isGroup 区分 */
data class Contact(
    val id: Long,
    val name: String,
    val avatar: String? = null,
    val isGroup: Boolean = false,
    val online: Boolean = false,
    val memberCount: Int = 0,
    val maxMembers: Int = 0,
    val creatorId: Long? = null,
    val description: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val remark: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val unreadCount: Int = 0
) {
    val key: String get() = if (isGroup) "g$id" else "u$id"

    companion object {
        fun keyOf(id: Long, isGroup: Boolean) = if (isGroup) "g$id" else "u$id"
    }
}

enum class MessageKind { TEXT, IMAGE, EMOJI, OTHER }

/** 聊天消息（好友与群聊统一结构） */
data class MessageUi(
    val id: Long,
    val conversationKey: String,
    val senderId: Long,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val content: String,
    val kind: MessageKind,
    val time: Long,
    val isMine: Boolean,
    val isRead: Boolean = false
)

/** 好友申请（待处理） */
data class FriendRequestUi(
    val id: Long,
    val userId: Long,
    val name: String,
    val avatar: String? = null,
    val message: String? = null,
    val time: Long = 0L
)

/** 朋友圈评论 */
data class MomentCommentUi(
    val id: Long,
    val userId: Long,
    val name: String,
    val replyToName: String? = null,
    val content: String,
    val time: Long = 0L
)

/** 朋友圈动态 */
data class MomentUi(
    val id: Long,
    val userId: Long,
    val name: String,
    val avatar: String? = null,
    val content: String,
    val images: List<String> = emptyList(),
    val location: String? = null,
    val visibility: Int = 0,
    val likeCount: Int = 0,
    val liked: Boolean = false,
    val likeNames: List<String> = emptyList(),
    val comments: List<MomentCommentUi> = emptyList(),
    val time: Long = 0L
)

/** 收藏类型，与后端 favorite.type 对应 */
object FavoriteType {
    const val NOTE = "note"
    const val LINK = "link"
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val FILE = "file"
    const val CHAT = "chat"
    const val VOICE = "voice"
    const val LOCATION = "location"
    const val MINIAPP = "miniapp"

    /** 分类筛选时传给后端的 type 参数 */
    fun queryOf(category: String): String? = when (category) {
        "all", "recent" -> null
        "media" -> "image,video"
        else -> category
    }
}

/** 收藏项 */
data class FavoriteUi(
    val id: Long,
    val type: String,
    val title: String? = null,
    val content: String? = null,
    val cover: String? = null,
    val url: String? = null,
    val fileName: String? = null,
    val fileSize: Long = 0L,
    val tags: List<String> = emptyList(),
    val source: String? = null,
    val createdTime: Long = 0L,
    val updatedTime: Long = 0L
)

fun messageKindOf(raw: String?): MessageKind = when (raw?.lowercase()) {
    "image" -> MessageKind.IMAGE
    "emoji" -> MessageKind.EMOJI
    "text" -> MessageKind.TEXT
    null, "" -> MessageKind.TEXT
    else -> MessageKind.OTHER
}
