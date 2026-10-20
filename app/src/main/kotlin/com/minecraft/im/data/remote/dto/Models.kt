package com.minecraft.im.data.remote.dto

import kotlinx.serialization.Serializable

/** 后端统一响应结构 */
@Serializable
data class ApiEnvelope<T>(
    val code: Int = 0,
    val message: String? = null,
    val data: T? = null
)

@Serializable
data class LoginRequest(
    val account: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String = "",
    val userId: Long = 0,
    val username: String? = null,
    val email: String? = null,
    val avatar: String? = null,
    val account: String? = null,
    val phone: String? = null,
    val signature: String? = null,
    val nickname: String? = null,
    val experience: String? = null
)

/** 用户资料（同时用于更新请求与用户信息响应） */
@Serializable
data class UserDto(
    val id: Long? = null,
    val account: String? = null,
    val username: String? = null,
    val nickname: String? = null,
    val avatar: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val occupation: String? = null,
    val hobbies: String? = null,
    val bio: String? = null,
    val signature: String? = null,
    val experience: String? = null,
    val online: Boolean? = null,
    val status: Int? = null,
    val points: Int? = null,
    val permissions: String? = null
)

/** 好友信息（含未读数、在线状态） */
@Serializable
data class FriendInfoDto(
    val friendId: Long = 0,
    val remark: String? = null,
    val username: String? = null,
    val avatar: String? = null,
    val online: Boolean? = null,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    val unreadCount: Int? = null,
    val phone: String? = null,
    val email: String? = null
)

/** 好友申请 */
@Serializable
data class FriendRequestDto(
    val id: Long = 0,
    val userId: Long? = null,
    val friendId: Long? = null,
    val username: String? = null,
    val avatar: String? = null,
    val message: String? = null,
    val createTime: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val status: Int? = null
)

@Serializable
data class SendFriendRequest(
    val senderId: Long,
    val receiverId: Long? = null,
    val receiverPhone: String? = null,
    val message: String? = null
)

/** 单聊消息 */
@Serializable
data class ChatMessageDto(
    val id: Long = 0,
    val senderId: Long = 0,
    val receiverId: Long = 0,
    val content: String = "",
    val messageType: String = "text",
    val isRead: Int? = null,
    val createTime: String? = null
)

/** 群聊消息 */
@Serializable
data class GroupChatMessageDto(
    val id: Long = 0,
    val groupId: Long = 0,
    val senderId: Long = 0,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val content: String = "",
    val messageType: String = "text",
    val createTime: String? = null
)

@Serializable
data class SingleMessageRequest(
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val messageType: String = "text"
)

@Serializable
data class GroupMessageRequest(
    val groupId: Long,
    val senderId: Long,
    val content: String,
    val messageType: String = "text"
)

/** 群组 */
@Serializable
data class ChatGroupDto(
    val id: Long = 0,
    val name: String? = null,
    val description: String? = null,
    val creatorId: Long? = null,
    val maxMembers: Int? = null,
    val memberCount: Int? = null,
    val avatar: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val description: String? = null,
    val avatar: String? = null,
    val creatorId: Long,
    val memberIds: List<Long> = emptyList()
)

@Serializable
data class GroupMemberDto(
    val id: Long? = null,
    val groupId: Long? = null,
    val userId: Long = 0,
    val role: String? = null,
    val status: Int? = null,
    val joinTime: String? = null,
    val nickname: String? = null
)

/** 朋友圈 */
@Serializable
data class MomentDto(
    val id: Long = 0,
    val userId: Long = 0,
    val username: String? = null,
    val nickname: String? = null,
    val avatar: String? = null,
    val content: String? = null,
    val images: List<String> = emptyList(),
    val location: String? = null,
    val visibility: Int? = null,
    val likeCount: Int? = null,
    val commentCount: Int? = null,
    val createTime: String? = null,
    val liked: Boolean? = null,
    val likes: List<MomentUserDto> = emptyList(),
    val comments: List<MomentCommentDto> = emptyList()
)

@Serializable
data class MomentUserDto(
    val userId: Long = 0,
    val username: String? = null,
    val nickname: String? = null,
    val avatar: String? = null
)

@Serializable
data class MomentCommentDto(
    val id: Long = 0,
    val momentId: Long = 0,
    val userId: Long = 0,
    val username: String? = null,
    val nickname: String? = null,
    val avatar: String? = null,
    val replyToUserId: Long? = null,
    val replyToNickname: String? = null,
    val content: String = "",
    val createTime: String? = null
)

@Serializable
data class PublishMomentRequest(
    val userId: Long,
    val content: String,
    val images: List<String> = emptyList(),
    val location: String? = null,
    val visibility: Int = 0
)

@Serializable
data class MomentCommentRequest(
    val userId: Long,
    val content: String,
    val replyToUserId: Long? = null
)

@Serializable
data class MomentLikeResult(
    val liked: Boolean = false,
    val likeCount: Int = 0
)

/** 表情包接口返回结构（自定义表情） */
@Serializable
data class UserEmojiDto(
    val id: Long = 0,
    val userId: Long? = null,
    val emojiUrl: String? = null,
    val emojiName: String? = null,
    val sortOrder: Int? = null
)
