package com.minecraft.im.data.repository

import com.minecraft.im.core.ApiException
import com.minecraft.im.core.parseServerTime
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.SessionState
import com.minecraft.im.data.remote.ApiProvider
import com.minecraft.im.data.remote.dto.CreateGroupRequest
import com.minecraft.im.data.remote.dto.SendFriendRequest
import com.minecraft.im.data.remote.dto.SingleMessageRequest
import com.minecraft.im.data.remote.dto.GroupMessageRequest
import com.minecraft.im.data.remote.ws.ImWebSocketClient
import com.minecraft.im.data.remote.ws.WsEvent
import com.minecraft.im.data.toContact
import com.minecraft.im.data.toUi
import com.minecraft.im.di.ApplicationScope
import com.minecraft.im.domain.model.Contact
import com.minecraft.im.domain.model.FriendRequestUi
import com.minecraft.im.domain.model.MessageKind
import com.minecraft.im.domain.model.MessageUi
import com.minecraft.im.domain.model.messageKindOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * IM 数据仓库：负责好友/群组/消息的拉取、发送，以及 WebSocket 事件的落地。
 * 会话列表与消息列表都以 StateFlow 暴露，UI 层只做渲染。
 */
@Singleton
class ImRepository @Inject constructor(
    private val apiProvider: ApiProvider,
    private val ws: ImWebSocketClient,
    private val session: SessionManager,
    @ApplicationScope private val scope: CoroutineScope
) {
    private val _friends = MutableStateFlow<List<Contact>>(emptyList())
    val friends: StateFlow<List<Contact>> = _friends.asStateFlow()

    private val _groups = MutableStateFlow<List<Contact>>(emptyList())
    val groups: StateFlow<List<Contact>> = _groups.asStateFlow()

    private val _requests = MutableStateFlow<List<FriendRequestUi>>(emptyList())
    val pendingRequests: StateFlow<List<FriendRequestUi>> = _requests.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<MessageUi>>>(emptyMap())
    val messages: StateFlow<Map<String, List<MessageUi>>> = _messages.asStateFlow()

    private val _activeKey = MutableStateFlow<String?>(null)
    val activeKey: StateFlow<String?> = _activeKey.asStateFlow()

    val conversations: StateFlow<List<Contact>> =
        combine(_friends, _groups) { friends, groups ->
            (friends + groups).sortedWith(
                compareByDescending<Contact> { it.lastMessageTime }.thenBy { it.name }
            )
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val totalUnread: StateFlow<Int> = conversations
        .map { list -> list.sumOf { it.unreadCount } }
        .stateIn(scope, SharingStarted.Eagerly, 0)

    private var started = false

    /** 在 Application 启动后调用一次：绑定登录态与实时事件 */
    fun start() {
        if (started) return
        started = true
        scope.launch {
            session.session.collect { state ->
                when (state) {
                    is SessionState.LoggedIn -> {
                        ws.connect(state.token)
                        refreshAll()
                    }

                    else -> ws.disconnect()
                }
            }
        }
        scope.launch {
            ws.events.collect { handleEvent(it) }
        }
    }

    // ---------------- 拉取 ----------------

    suspend fun refreshAll() {
        if (session.currentUserId == 0L) return
        refreshFriends()
        refreshGroups()
        refreshRequests()
    }

    suspend fun refreshFriends() {
        val userId = session.currentUserId
        if (userId == 0L) return
        runCatching { apiProvider.api().getFriendInfoList(userId) }
            .onSuccess { res ->
                if (res.code == 200) {
                    _friends.value = res.data.orEmpty().map { it.toContact() }
                }
            }
    }

    suspend fun refreshGroups() {
        val userId = session.currentUserId
        if (userId == 0L) return
        runCatching { apiProvider.api().getGroupList(userId) }
            .onSuccess { res ->
                if (res.code == 200) {
                    _groups.value = res.data.orEmpty().map { it.toContact() }
                }
            }
    }

    suspend fun refreshRequests() {
        val userId = session.currentUserId
        if (userId == 0L) return
        runCatching { apiProvider.api().getPendingFriendRequests(userId) }
            .onSuccess { res ->
                if (res.code == 200) {
                    _requests.value = res.data.orEmpty().map { it.toUi() }
                }
            }
    }

    suspend fun loadHistory(contact: Contact): Result<Unit> = runCatching {
        val api = apiProvider.api()
        val currentUserId = session.currentUserId
        val list: List<MessageUi> = if (contact.isGroup) {
            val res = api.getGroupHistory(contact.id)
            if (res.code != 200) throw ApiException(res.message ?: "加载聊天记录失败")
            res.data.orEmpty().map { it.toUi(currentUserId) }
        } else {
            val res = api.getSingleHistory(currentUserId, contact.id)
            if (res.code != 200) throw ApiException(res.message ?: "加载聊天记录失败")
            res.data.orEmpty().map { it.toUi(currentUserId) }
        }
        val local = _messages.value[contact.key].orEmpty()
        val merged = (list + local)
            .distinctBy { it.id }
            .sortedBy { it.time }
        _messages.update { it + (contact.key to merged) }
    }

    /** 打开会话：清空未读并上报服务器 */
    fun openConversation(contact: Contact) {
        _activeKey.value = contact.key
        clearUnread(contact.key)
        if (!contact.isGroup) {
            scope.launch {
                runCatching { apiProvider.api().markConversationRead(contact.id) }
            }
        }
    }

    fun closeConversation(key: String) {
        if (_activeKey.value == key) _activeKey.value = null
    }

    private fun clearUnread(key: String) {
        _friends.update { list -> list.map { if (it.key == key) it.copy(unreadCount = 0) else it } }
        _groups.update { list -> list.map { if (it.key == key) it.copy(unreadCount = 0) else it } }
    }

    // ---------------- 发送 ----------------

    suspend fun sendText(contact: Contact, text: String): Result<Unit> = send(contact, text, "text")

    suspend fun sendEmoji(contact: Contact, emoji: String): Result<Unit> = send(contact, emoji, "emoji")

    suspend fun sendImage(contact: Contact, bytes: ByteArray, fileName: String, mime: String): Result<Unit> {
        return runCatching {
            val url = uploadImage(bytes, fileName, mime).getOrThrow()
            send(contact, url, "image").getOrThrow()
        }
    }

    private suspend fun send(contact: Contact, content: String, messageType: String): Result<Unit> = runCatching {
        if (content.isBlank()) throw ApiException("消息内容不能为空")
        val currentUserId = session.currentUserId
        val api = apiProvider.api()
        val message: MessageUi
        if (contact.isGroup) {
            val res = api.sendGroupMessage(
                GroupMessageRequest(contact.id, currentUserId, content, messageType)
            )
            if (res.code != 200) throw ApiException(res.message ?: "发送失败")
            message = MessageUi(
                id = -System.nanoTime(),
                conversationKey = contact.key,
                senderId = currentUserId,
                senderName = session.currentUser?.nickname ?: session.currentUser?.username,
                senderAvatar = session.currentUser?.avatar,
                content = content,
                kind = messageKindOf(messageType),
                time = System.currentTimeMillis(),
                isMine = true
            )
        } else {
            val res = api.sendSingleMessage(
                SingleMessageRequest(currentUserId, contact.id, content, messageType)
            )
            if (res.code != 200) throw ApiException(res.message ?: "发送失败")
            val saved = res.data
            message = MessageUi(
                id = saved?.id?.takeIf { it > 0 } ?: -System.nanoTime(),
                conversationKey = contact.key,
                senderId = currentUserId,
                senderName = session.currentUser?.nickname ?: session.currentUser?.username,
                senderAvatar = session.currentUser?.avatar,
                content = saved?.content ?: content,
                kind = messageKindOf(saved?.messageType ?: messageType),
                time = parseServerTime(saved?.createTime),
                isMine = true
            )
        }
        appendMessage(contact.key, message)
        updatePreview(contact.key, previewOf(message), message.time, increaseUnread = false)
    }

    private fun previewOf(message: MessageUi): String = when (message.kind) {
        MessageKind.IMAGE -> "[图片]"
        else -> message.content
    }

    private fun appendMessage(key: String, message: MessageUi) {
        _messages.update { map ->
            val list = map[key].orEmpty()
            if (list.any { it.id == message.id && message.id > 0 }) {
                map
            } else {
                map + (key to (list + message).sortedBy { it.time })
            }
        }
    }

    private fun updatePreview(key: String, preview: String, time: Long, increaseUnread: Boolean) {
        val isActive = _activeKey.value == key
        fun Contact.bump(): Contact = copy(
            lastMessage = preview,
            lastMessageTime = time,
            unreadCount = when {
                isActive -> 0
                increaseUnread -> unreadCount + 1
                else -> unreadCount
            }
        )
        _friends.update { list -> list.map { if (it.key == key) it.bump() else it } }
        _groups.update { list -> list.map { if (it.key == key) it.bump() else it } }
    }

    // ---------------- 实时事件 ----------------

    private suspend fun handleEvent(event: WsEvent) {
        when (event) {
            is WsEvent.PrivateMessage -> {
                val currentUserId = session.currentUserId
                if (event.receiverId != 0L && event.receiverId != currentUserId) return
                if (event.senderId == currentUserId) return
                val contact = _friends.value.firstOrNull { it.id == event.senderId }
                val key = Contact.keyOf(event.senderId, false)
                val message = MessageUi(
                    id = event.timestamp,
                    conversationKey = key,
                    senderId = event.senderId,
                    senderName = event.senderName ?: contact?.name,
                    senderAvatar = event.senderAvatar ?: contact?.avatar,
                    content = event.content,
                    kind = messageKindOf(event.messageType),
                    time = event.timestamp,
                    isMine = false
                )
                appendMessage(key, message)
                updatePreview(key, previewOf(message), event.timestamp, increaseUnread = true)
                if (contact == null) refreshFriends()
            }

            is WsEvent.GroupMessage -> {
                val key = Contact.keyOf(event.groupId, true)
                val group = _groups.value.firstOrNull { it.id == event.groupId }
                if (group == null) {
                    refreshGroups()
                    return
                }
                val message = MessageUi(
                    id = event.timestamp,
                    conversationKey = key,
                    senderId = event.senderId,
                    senderName = event.senderName ?: "#${event.senderId}",
                    senderAvatar = event.senderAvatar,
                    content = event.content,
                    kind = messageKindOf(event.messageType),
                    time = event.timestamp,
                    isMine = event.senderId == session.currentUserId
                )
                appendMessage(key, message)
                updatePreview(
                    key,
                    previewOf(message),
                    event.timestamp,
                    increaseUnread = !message.isMine
                )
            }

            is WsEvent.ReadReceipt -> {
                _messages.update { map ->
                    map.mapValues { (_, list) ->
                        list.map { if (it.id == event.messageId) it.copy(isRead = true) else it }
                    }
                }
            }

            else -> Unit
        }
    }

    // ---------------- 好友 / 群组操作 ----------------

    suspend fun sendFriendRequest(phone: String, message: String): Result<Unit> = runCatching {
        val senderId = session.currentUserId
        if (senderId == 0L) throw ApiException("请先登录")
        val res = apiProvider.api().sendFriendRequest(
            SendFriendRequest(senderId = senderId, receiverPhone = phone, message = message)
        )
        if (res.code != 200) throw ApiException(res.message ?: "发送好友申请失败")
    }

    suspend fun handleFriendRequest(requestId: Long, accept: Boolean): Result<Unit> = runCatching {
        val res = apiProvider.api().handleFriendRequest(
            requestId = requestId,
            status = if (accept) 1 else 2
        )
        if (res.code != 200) throw ApiException(res.message ?: "处理好友申请失败")
        refreshRequests()
        if (accept) refreshFriends()
    }

    suspend fun updateRemark(friendId: Long, remark: String): Result<Unit> = runCatching {
        val res = apiProvider.api().updateFriendRemark(session.currentUserId, friendId, remark)
        if (res.code != 200) throw ApiException(res.message ?: "修改备注失败")
        refreshFriends()
    }

    suspend fun deleteFriend(friendId: Long): Result<Unit> = runCatching {
        val res = apiProvider.api().deleteFriend(session.currentUserId, friendId)
        if (res.code != 200) throw ApiException(res.message ?: "删除好友失败")
        refreshFriends()
    }

    suspend fun createGroup(name: String, description: String, memberIds: List<Long>): Result<Contact> =
        runCatching {
            val res = apiProvider.api().createGroup(
                CreateGroupRequest(
                    name = name,
                    description = description.ifBlank { null },
                    creatorId = session.currentUserId,
                    memberIds = memberIds
                )
            )
            if (res.code != 200) throw ApiException(res.message ?: "创建群聊失败")
            val group = res.data ?: throw ApiException("创建群聊失败")
            refreshGroups()
            group.toContact()
        }

    suspend fun inviteToGroup(groupId: Long, friendIds: List<Long>): Result<Unit> = runCatching {
        val res = apiProvider.api().inviteToGroup(groupId, friendIds)
        if (res.code != 200) throw ApiException(res.message ?: "邀请失败")
    }

    suspend fun getGroup(groupId: Long): Result<Contact> = runCatching {
        val res = apiProvider.api().getGroup(groupId)
        if (res.code != 200) throw ApiException(res.message ?: "加载群资料失败")
        val dto = res.data ?: throw ApiException("群聊不存在")
        dto.toContact()
    }

    suspend fun getGroupMembers(groupId: Long): Result<List<Pair<Long, String>>> = runCatching {
        val res = apiProvider.api().getGroupMembers(groupId)
        if (res.code != 200) throw ApiException(res.message ?: "加载群成员失败")
        res.data.orEmpty().map { member ->
            member.userId to (member.nickname?.takeIf { it.isNotBlank() } ?: "#${member.userId}")
        }
    }

    suspend fun updateGroup(
        groupId: Long,
        name: String,
        description: String,
        avatar: ByteArray? = null,
        avatarMime: String = "image/jpeg"
    ): Result<Unit> = runCatching {
        val res = apiProvider.api().updateGroup(
            id = groupId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            name = name.toRequestBody("text/plain".toMediaTypeOrNull()),
            description = description.toRequestBody("text/plain".toMediaTypeOrNull()),
            avatar = avatar?.let { imagePart(it, "group_avatar", avatarMime) }
        )
        if (res.code != 200) throw ApiException(res.message ?: "保存群资料失败")
        refreshGroups()
    }

    // ---------------- 上传 ----------------

    suspend fun uploadImage(bytes: ByteArray, fileName: String, mime: String): Result<String> = runCatching {
        val res = apiProvider.api().uploadFile(imagePart(bytes, fileName, mime))
        if (res.code != 200) throw ApiException(res.message ?: "图片上传失败")
        res.data ?: throw ApiException("图片上传失败")
    }

    suspend fun uploadAvatar(bytes: ByteArray, fileName: String, mime: String): Result<String> = runCatching {
        val res = apiProvider.api().uploadAvatar(imagePart(bytes, fileName, mime))
        if (res.code != 200) throw ApiException(res.message ?: "头像上传失败")
        res.data ?: throw ApiException("头像上传失败")
    }

    private fun imagePart(bytes: ByteArray, fileName: String, mime: String): MultipartBody.Part {
        val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", fileName, body)
    }

    fun findContact(id: Long, isGroup: Boolean): Contact? =
        if (isGroup) _groups.value.firstOrNull { it.id == id }
        else _friends.value.firstOrNull { it.id == id }
}
