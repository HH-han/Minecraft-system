package com.minecraft.im.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "im_settings")

/** 一次性读取的本地配置快照 */
data class SettingsSnapshot(
    val token: String? = null,
    val userJson: String? = null,
    val baseUrl: String? = null,
    val theme: String? = null,
    val language: String? = null,
    val starredIds: Set<Long> = emptySet()
)

/** 本地偏好：登录态、服务器地址、主题、语言、星标好友 */
@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val USER = stringPreferencesKey("user_json")
        val BASE_URL = stringPreferencesKey("base_url")
        val THEME = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val STARRED = stringPreferencesKey("starred_friends")
    }

    val data: Flow<Preferences> = context.dataStore.data

    val baseUrl: Flow<String?> = data.map { it[Keys.BASE_URL] }
    val theme: Flow<String?> = data.map { it[Keys.THEME] }
    val language: Flow<String?> = data.map { it[Keys.LANGUAGE] }

    suspend fun snapshot(): SettingsSnapshot {
        val prefs = data.first()
        return SettingsSnapshot(
            token = prefs[Keys.TOKEN],
            userJson = prefs[Keys.USER],
            baseUrl = prefs[Keys.BASE_URL],
            theme = prefs[Keys.THEME],
            language = prefs[Keys.LANGUAGE],
            starredIds = prefs[Keys.STARRED]
                ?.split(",")
                ?.mapNotNull { it.toLongOrNull() }
                ?.toSet()
                ?: emptySet()
        )
    }

    suspend fun saveSession(token: String, userJson: String) {
        context.dataStore.edit {
            it[Keys.TOKEN] = token
            it[Keys.USER] = userJson
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(Keys.TOKEN)
            it.remove(Keys.USER)
        }
    }

    suspend fun saveBaseUrl(baseUrl: String) {
        context.dataStore.edit { it[Keys.BASE_URL] = baseUrl }
    }

    suspend fun saveTheme(mode: String) {
        context.dataStore.edit { it[Keys.THEME] = mode }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun saveStarred(ids: Set<Long>) {
        context.dataStore.edit { it[Keys.STARRED] = ids.joinToString(",") }
    }
}
