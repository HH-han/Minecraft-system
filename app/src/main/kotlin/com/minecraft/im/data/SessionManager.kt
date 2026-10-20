package com.minecraft.im.data

import com.minecraft.im.BuildConfig
import com.minecraft.im.core.normalizeBaseUrl
import com.minecraft.im.data.local.SettingsStore
import com.minecraft.im.data.remote.dto.UserDto
import com.minecraft.im.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/** 主题模式，与 Web 端保持一致：跟随系统 / 浅色 / 深色 / 游戏 */
enum class ThemeMode { SYSTEM, LIGHT, DARK, GAME }

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val token: String, val user: UserDto) : SessionState
}

/**
 * 会话状态中心：持有 token、当前用户、服务器地址、主题与语言偏好。
 * token 与 baseUrl 同时保留内存副本，供 OkHttp 拦截器与 Retrofit 同步读取。
 */
@Singleton
class SessionManager @Inject constructor(
    private val store: SettingsStore,
    private val json: Json,
    @ApplicationScope private val appScope: CoroutineScope
) {
    private val _session = MutableStateFlow<SessionState>(SessionState.Loading)
    val session: StateFlow<SessionState> = _session.asStateFlow()

    private val _theme = MutableStateFlow(ThemeMode.SYSTEM)
    val theme: StateFlow<ThemeMode> = _theme.asStateFlow()

    private val _language = MutableStateFlow("zh")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _starred = MutableStateFlow<Set<Long>>(emptySet())
    val starred: StateFlow<Set<Long>> = _starred.asStateFlow()

    @Volatile
    var currentToken: String? = null
        private set

    @Volatile
    var currentBaseUrl: String = BuildConfig.DEFAULT_BASE_URL
        private set

    val currentUserId: Long
        get() = currentUser?.id ?: 0L

    val currentUser: UserDto?
        get() = (_session.value as? SessionState.LoggedIn)?.user

    /** 启动时读取本地登录态 */
    fun bootstrap() {
        appScope.launch {
            val snapshot = runCatching { store.snapshot() }.getOrNull()
            if (snapshot == null) {
                _session.value = SessionState.LoggedOut
                return@launch
            }
            snapshot.baseUrl?.takeIf { it.isNotBlank() }?.let {
                currentBaseUrl = normalizeBaseUrl(it)
            }
            _theme.value = ThemeMode.entries.firstOrNull { it.name == snapshot.theme } ?: ThemeMode.SYSTEM
            _language.value = snapshot.language
                ?: if (Locale.getDefault().language == "en") "en" else "zh"
            _starred.value = snapshot.starredIds

            val token = snapshot.token
            val user = snapshot.userJson
                ?.takeIf { it.isNotBlank() }
                ?.let { runCatching { json.decodeFromString<UserDto>(it) }.getOrNull() }
            if (!token.isNullOrBlank() && user != null) {
                currentToken = token
                _session.value = SessionState.LoggedIn(token, user)
            } else {
                _session.value = SessionState.LoggedOut
            }
        }
    }

    suspend fun updateBaseUrl(baseUrl: String) {
        currentBaseUrl = normalizeBaseUrl(baseUrl)
        store.saveBaseUrl(currentBaseUrl)
    }

    suspend fun onLogin(token: String, user: UserDto) {
        currentToken = token
        store.saveSession(token, json.encodeToString(user))
        _session.value = SessionState.LoggedIn(token, user)
    }

    suspend fun updateUser(user: UserDto) {
        val state = _session.value as? SessionState.LoggedIn ?: return
        store.saveSession(state.token, json.encodeToString(user))
        _session.value = state.copy(user = user)
    }

    suspend fun logout() {
        currentToken = null
        store.clearSession()
        _session.value = SessionState.LoggedOut
    }

    suspend fun setTheme(mode: ThemeMode) {
        _theme.value = mode
        store.saveTheme(mode.name)
    }

    suspend fun setLanguage(language: String) {
        _language.value = language
        store.saveLanguage(language)
    }

    suspend fun toggleStar(friendId: Long) {
        val next = _starred.value.toMutableSet()
        if (!next.add(friendId)) next.remove(friendId)
        _starred.value = next
        store.saveStarred(next)
    }
}
