package com.minecraft.im.data.remote

import com.minecraft.im.data.remote.dto.*
import kotlinx.serialization.json.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 与 Minecraft 后端 IM 模块对接的 REST 接口。
 * 路径与 Web 端 src/api 目录下的接口保持一致（baseUrl 已包含 /api/）。
 */
interface ApiService {

    // ---------------- 认证 / 用户 ----------------
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): ApiEnvelope<LoginResponse>

    @GET("user/info")
    suspend fun getUserInfo(): ApiEnvelope<UserDto>

    @PUT("user/update")
    suspend fun updateUser(@Body user: UserDto): ApiEnvelope<UserDto>

    @GET("user/by-account")
    suspend fun getUserByAccount(@Query("account") account: String): ApiEnvelope<UserDto>

    @POST("user/logout")
    suspend fun logout(): ApiEnvelope<JsonElement>

    @Multipart
    @POST("user/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): ApiEnvelope<String>

    @Multipart
    @POST("upload/file")
    suspend fun uploadFile(@Part file: MultipartBody.Part): ApiEnvelope<String>

    // ---------------- 好友 ----------------
    @GET("public/im/friend/info/list")
    suspend fun getFriendInfoList(@Query("userId") userId: Long): ApiEnvelope<List<FriendInfoDto>>

    @GET("public/im/friend/request/info/pending")
    suspend fun getPendingFriendRequests(@Query("userId") userId: Long): ApiEnvelope<List<FriendRequestDto>>

    @POST("public/im/friend/request")
    suspend fun sendFriendRequest(@Body body: SendFriendRequest): ApiEnvelope<JsonElement>

    @PUT("public/im/friend/request/handle")
    suspend fun handleFriendRequest(
        @Query("requestId") requestId: Long,
        @Query("status") status: Int
    ): ApiEnvelope<JsonElement>

    @DELETE("public/im/friend/delete")
    suspend fun deleteFriend(
        @Query("userId") userId: Long,
        @Query("friendId") friendId: Long
    ): ApiEnvelope<JsonElement>

    @PUT("public/im/friend/remark")
    suspend fun updateFriendRemark(
        @Query("userId") userId: Long,
        @Query("friendId") friendId: Long,
        @Query("remark") remark: String
    ): ApiEnvelope<JsonElement>

    // ---------------- 群组 ----------------
    @GET("public/chat-group/user/{userId}")
    suspend fun getGroupList(@Path("userId") userId: Long): ApiEnvelope<List<ChatGroupDto>>

    @GET("public/chat-group/{groupId}")
    suspend fun getGroup(@Path("groupId") groupId: Long): ApiEnvelope<ChatGroupDto>

    @POST("public/chat-group/with-members")
    suspend fun createGroup(@Body body: CreateGroupRequest): ApiEnvelope<ChatGroupDto>

    @POST("public/chat-group/{groupId}/invite")
    suspend fun inviteToGroup(
        @Path("groupId") groupId: Long,
        @Body friendIds: List<Long>
    ): ApiEnvelope<JsonElement>

    @GET("public/chat-group/{groupId}/members")
    suspend fun getGroupMembers(@Path("groupId") groupId: Long): ApiEnvelope<List<GroupMemberDto>>

    @Multipart
    @POST("public/chat-group/update")
    suspend fun updateGroup(
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part avatar: MultipartBody.Part?
    ): ApiEnvelope<JsonElement>

    // ---------------- 消息 ----------------
    @POST("public/im/single/send")
    suspend fun sendSingleMessage(@Body body: SingleMessageRequest): ApiEnvelope<ChatMessageDto>

    @POST("public/im/group/send")
    suspend fun sendGroupMessage(@Body body: GroupMessageRequest): ApiEnvelope<JsonElement>

    @GET("public/im/single/history")
    suspend fun getSingleHistory(
        @Query("userId1") userId1: Long,
        @Query("userId2") userId2: Long,
        @Query("limit") limit: Int = 50
    ): ApiEnvelope<List<ChatMessageDto>>

    @GET("public/im/group/history")
    suspend fun getGroupHistory(
        @Query("groupId") groupId: Long,
        @Query("limit") limit: Int = 50
    ): ApiEnvelope<List<GroupChatMessageDto>>

    /** 清除与某好友的单聊未读数 */
    @POST("chat/read")
    suspend fun markConversationRead(@Query("friendId") friendId: Long): ApiEnvelope<JsonElement>

    // ---------------- 朋友圈 ----------------
    @POST("public/moment/publish")
    suspend fun publishMoment(@Body body: PublishMomentRequest): ApiEnvelope<MomentDto>

    @GET("public/moment/feed")
    suspend fun getMomentFeed(
        @Query("userId") userId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiEnvelope<List<MomentDto>>

    @DELETE("public/moment/{momentId}")
    suspend fun deleteMoment(
        @Path("momentId") momentId: Long,
        @Query("userId") userId: Long
    ): ApiEnvelope<JsonElement>

    @POST("public/moment/{momentId}/like")
    suspend fun toggleMomentLike(
        @Path("momentId") momentId: Long,
        @Query("userId") userId: Long
    ): ApiEnvelope<MomentLikeResult>

    @POST("public/moment/{momentId}/comment")
    suspend fun addMomentComment(
        @Path("momentId") momentId: Long,
        @Body body: MomentCommentRequest
    ): ApiEnvelope<MomentCommentDto>

    @GET("public/moment/{momentId}/comments")
    suspend fun getMomentComments(@Path("momentId") momentId: Long): ApiEnvelope<List<MomentCommentDto>>

    @DELETE("public/moment/comment/{commentId}")
    suspend fun deleteMomentComment(
        @Path("commentId") commentId: Long,
        @Query("userId") userId: Long
    ): ApiEnvelope<JsonElement>

    @GET("public/moment/cover")
    suspend fun getMomentCover(@Query("userId") userId: Long): ApiEnvelope<String>

    @Multipart
    @POST("public/moment/cover")
    suspend fun uploadMomentCover(
        @Query("userId") userId: Long,
        @Part file: MultipartBody.Part
    ): ApiEnvelope<String>

    // ---------------- 收藏 ----------------
    @GET("public/favorite/list")
    suspend fun getFavoriteList(
        @Query("userId") userId: Long,
        @Query("type") type: String? = null,
        @Query("tag") tag: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): ApiEnvelope<List<FavoriteDto>>

    @GET("public/favorite/recent")
    suspend fun getRecentFavorites(
        @Query("userId") userId: Long,
        @Query("limit") limit: Int = 30
    ): ApiEnvelope<List<FavoriteDto>>

    @GET("public/favorite/stats")
    suspend fun getFavoriteStats(@Query("userId") userId: Long): ApiEnvelope<FavoriteStatDto>

    @GET("public/favorite/tags")
    suspend fun getFavoriteTags(@Query("userId") userId: Long): ApiEnvelope<List<String>>

    @POST("public/favorite/add")
    suspend fun addFavorite(@Body body: FavoriteSaveRequest): ApiEnvelope<FavoriteDto>

    @PUT("public/favorite/{id}")
    suspend fun updateFavorite(
        @Path("id") id: Long,
        @Body body: FavoriteSaveRequest
    ): ApiEnvelope<FavoriteDto>

    @POST("public/favorite/{id}/touch")
    suspend fun touchFavorite(
        @Path("id") id: Long,
        @Query("userId") userId: Long
    ): ApiEnvelope<JsonElement>

    @DELETE("public/favorite/{id}")
    suspend fun deleteFavorite(
        @Path("id") id: Long,
        @Query("userId") userId: Long
    ): ApiEnvelope<JsonElement>

    @DELETE("public/favorite/clear")
    suspend fun clearFavorites(
        @Query("userId") userId: Long,
        @Query("type") type: String? = null
    ): ApiEnvelope<JsonElement>

    // ---------------- 自定义表情包 ----------------
    @GET("emoji/list")
    suspend fun getMyEmojis(): ApiEnvelope<List<UserEmojiDto>>

    @Multipart
    @POST("emoji/upload")
    suspend fun uploadEmoji(
        @Part file: MultipartBody.Part,
        @Part("emojiName") emojiName: RequestBody?
    ): ApiEnvelope<UserEmojiDto>

    @DELETE("emoji/{emojiId}")
    suspend fun deleteEmoji(@Path("emojiId") emojiId: Long): ApiEnvelope<JsonElement>
}
