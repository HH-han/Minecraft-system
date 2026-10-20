package com.minecraft.im.data.repository

import com.minecraft.im.core.ApiException
import com.minecraft.im.core.normalizeBaseUrl
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.remote.ApiProvider
import com.minecraft.im.data.remote.dto.LoginRequest
import com.minecraft.im.data.remote.dto.UserDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

/** 登录 / 登出 / 资料更新 */
@Singleton
class AuthRepository @Inject constructor(
    private val apiProvider: ApiProvider,
    private val session: SessionManager
) {
    val currentBaseUrl: String get() = session.currentBaseUrl

    suspend fun login(account: String, password: String, serverUrl: String): Result<Unit> = runCatching {
        if (account.isBlank()) throw ApiException("请输入账号")
        if (password.isBlank()) throw ApiException("请输入密码")
        val normalized = normalizeBaseUrl(serverUrl)
        if (normalized.isBlank()) throw ApiException("请输入服务器地址")
        session.updateBaseUrl(normalized)

        val res = apiProvider.api().login(LoginRequest(account.trim(), password))
        if (res.code != 200) throw ApiException(res.message ?: "登录失败")
        val data = res.data ?: throw ApiException("登录失败")
        if (data.token.isBlank()) throw ApiException("登录失败：未获取到 token")

        val user = UserDto(
            id = data.userId,
            account = data.account,
            username = data.username,
            nickname = data.nickname,
            avatar = data.avatar,
            email = data.email,
            phone = data.phone,
            signature = data.signature,
            experience = data.experience
        )
        session.onLogin(data.token, user)

        // 登录响应字段有限，再拉一次完整资料
        runCatching { apiProvider.api().getUserInfo() }
            .getOrNull()
            ?.data
            ?.let { session.updateUser(it) }
    }

    suspend fun logout() {
        runCatching { apiProvider.api().logout() }
        session.logout()
    }

    suspend fun updateProfile(user: UserDto): Result<UserDto> = runCatching {
        val res = apiProvider.api().updateUser(user)
        if (res.code != 200) throw ApiException(res.message ?: "保存失败")
        val updated = res.data ?: user
        session.updateUser(updated)
        updated
    }

    suspend fun refreshProfile(): Result<UserDto> = runCatching {
        val res = apiProvider.api().getUserInfo()
        if (res.code != 200) throw ApiException(res.message ?: "加载资料失败")
        val user = res.data ?: throw ApiException("加载资料失败")
        session.updateUser(user)
        user
    }

    suspend fun uploadAvatar(bytes: ByteArray, fileName: String, mime: String): Result<String> = runCatching {
        val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", fileName, body)
        val res = apiProvider.api().uploadAvatar(part)
        if (res.code != 200) throw ApiException(res.message ?: "头像上传失败")
        val url = res.data ?: throw ApiException("头像上传失败")
        session.currentUser?.let { session.updateUser(it.copy(avatar = url)) }
        url
    }
}
