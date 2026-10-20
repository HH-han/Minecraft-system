package com.minecraft.im.data

import com.minecraft.im.core.parseServerTime
import com.minecraft.im.data.remote.dto.ChatGroupDto
import com.minecraft.im.data.remote.dto.ChatMessageDto
import com.minecraft.im.data.remote.dto.FriendInfoDto
import com.minecraft.im.data.remote.dto.FriendRequestDto
import com.minecraft.im.data.remote.dto.GroupChatMessageDto
import com.minecraft.im.data.remote.dto.MomentCommentDto
import com.minecraft.im.data.remote.dto.MomentDto
import com.minecraft.im.domain.model.Contact
import com.minecraft.im.domain.model.FriendRequestUi
import com.minecraft.im.domain.model.MessageUi
import com.minecraft.im.domain.model.MomentCommentUi
import com.minecraft.im.domain.model.MomentUi
import com.minecraft.im.domain.model.messageKindOf

fun FriendInfoDto.toContact(): Contact = Contact(
    id = friendId,
    name = remark?.takeIf { it.isNotBlank() } ?: username?.takeIf { it.isNotBlank() } ?: "#$friendId",
    avatar = avatar,
    isGroup = false,
    online = online == true,
    phone = phone,
    email = email,
    remark = remark,
    lastMessage = lastMessage.orEmpty(),
    lastMessageTime = if (lastMessageTime.isNullOrBlank()) 0L else parseServerTime(lastMessageTime),
    unreadCount = unreadCount ?: 0
)

fun ChatGroupDto.toContact(): Contact = Contact(
    id = id,
    name = name?.takeIf { it.isNotBlank() } ?: "#$id",
    avatar = avatar,
    isGroup = true,
    memberCount = memberCount ?: 0,
    maxMembers = maxMembers ?: 0,
    creatorId = creatorId,
    description = description,
    lastMessageTime = 0L
)

fun FriendRequestDto.toUi(): FriendRequestUi = FriendRequestUi(
    id = id,
    userId = userId ?: friendId ?: 0L,
    name = username?.takeIf { it.isNotBlank() } ?: "#${userId ?: friendId}",
    avatar = avatar,
    message = message,
    time = parseServerTime(createTime)
)

fun ChatMessageDto.toUi(currentUserId: Long): MessageUi = MessageUi(
    id = id,
    conversationKey = Contact.keyOf(if (senderId == currentUserId) receiverId else senderId, false),
    senderId = senderId,
    content = content,
    kind = messageKindOf(messageType),
    time = parseServerTime(createTime),
    isMine = senderId == currentUserId,
    isRead = (isRead ?: 0) == 1
)

fun GroupChatMessageDto.toUi(currentUserId: Long): MessageUi = MessageUi(
    id = id,
    conversationKey = Contact.keyOf(groupId, true),
    senderId = senderId,
    senderName = senderName,
    senderAvatar = senderAvatar,
    content = content,
    kind = messageKindOf(messageType),
    time = parseServerTime(createTime),
    isMine = senderId == currentUserId
)

fun MomentDto.toUi(): MomentUi = MomentUi(
    id = id,
    userId = userId,
    name = nickname?.takeIf { it.isNotBlank() } ?: username.orEmpty(),
    avatar = avatar,
    content = content.orEmpty(),
    images = images,
    location = location,
    visibility = visibility ?: 0,
    likeCount = likeCount ?: 0,
    liked = liked == true,
    likeNames = likes.map { it.nickname?.takeIf { n -> n.isNotBlank() } ?: it.username.orEmpty() },
    comments = comments.map { it.toUi() },
    time = parseServerTime(createTime)
)

fun MomentCommentDto.toUi(): MomentCommentUi = MomentCommentUi(
    id = id,
    userId = userId,
    name = nickname?.takeIf { it.isNotBlank() } ?: username.orEmpty(),
    replyToName = replyToNickname,
    content = content,
    time = parseServerTime(createTime)
)
