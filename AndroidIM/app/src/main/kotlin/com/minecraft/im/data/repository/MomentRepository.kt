package com.minecraft.im.data.repository

import com.minecraft.im.core.ApiException
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.remote.ApiProvider
import com.minecraft.im.data.remote.dto.MomentCommentRequest
import com.minecraft.im.data.remote.dto.MomentLikeResult
import com.minecraft.im.data.remote.dto.PublishMomentRequest
import com.minecraft.im.data.toUi
import com.minecraft.im.domain.model.MomentUi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

/** 朋友圈数据仓库 */
@Singleton
class MomentRepository @Inject constructor(
    private val apiProvider: ApiProvider,
    private val session: SessionManager
) {
    suspend fun feed(page: Int, size: Int = 10): Result<List<MomentUi>> = runCatching {
        val res = apiProvider.api().getMomentFeed(session.currentUserId, page, size)
        if (res.code != 200) throw ApiException(res.message ?: "加载朋友圈失败")
        res.data.orEmpty().map { it.toUi() }
    }

    suspend fun publish(
        content: String,
        images: List<String>,
        location: String?,
        visibility: Int
    ): Result<Unit> = runCatching {
        val res = apiProvider.api().publishMoment(
            PublishMomentRequest(
                userId = session.currentUserId,
                content = content,
                images = images,
                location = location?.takeIf { it.isNotBlank() },
                visibility = visibility
            )
        )
        if (res.code != 200) throw ApiException(res.message ?: "发布失败")
    }

    suspend fun delete(momentId: Long): Result<Unit> = runCatching {
        val res = apiProvider.api().deleteMoment(momentId, session.currentUserId)
        if (res.code != 200) throw ApiException(res.message ?: "删除失败")
    }

    suspend fun toggleLike(momentId: Long): Result<MomentLikeResult> = runCatching {
        val res = apiProvider.api().toggleMomentLike(momentId, session.currentUserId)
        if (res.code != 200) throw ApiException(res.message ?: "操作失败")
        res.data ?: MomentLikeResult()
    }

    suspend fun addComment(momentId: Long, content: String, replyToUserId: Long?): Result<Unit> =
        runCatching {
            val res = apiProvider.api().addMomentComment(
                momentId,
                MomentCommentRequest(
                    userId = session.currentUserId,
                    content = content,
                    replyToUserId = replyToUserId
                )
            )
            if (res.code != 200) throw ApiException(res.message ?: "评论失败")
        }

    suspend fun deleteComment(commentId: Long): Result<Unit> = runCatching {
        val res = apiProvider.api().deleteMomentComment(commentId, session.currentUserId)
        if (res.code != 200) throw ApiException(res.message ?: "删除评论失败")
    }

    suspend fun uploadImage(bytes: ByteArray, fileName: String, mime: String): Result<String> = runCatching {
        val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", fileName, body)
        val res = apiProvider.api().uploadFile(part)
        if (res.code != 200) throw ApiException(res.message ?: "图片上传失败")
        res.data ?: throw ApiException("图片上传失败")
    }

    suspend fun cover(): Result<String?> = runCatching {
        val res = apiProvider.api().getMomentCover(session.currentUserId)
        if (res.code != 200) null else res.data
    }

    suspend fun uploadCover(
        bytes: ByteArray,
        fileName: String,
        mime: String
    ): Result<String> = runCatching {
        val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", fileName, body)
        val res = apiProvider.api().uploadMomentCover(session.currentUserId, part)
        if (res.code != 200) throw ApiException(res.message ?: "封面上传失败")
        res.data ?: throw ApiException("封面上传失败")
    }
}
