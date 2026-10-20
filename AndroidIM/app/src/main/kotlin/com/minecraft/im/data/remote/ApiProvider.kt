package com.minecraft.im.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.minecraft.im.data.SessionManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 按当前服务器地址懒构建 Retrofit 实例。
 * 用户可在登录页/设置页修改服务器地址，因此不能把 baseUrl 写死在单例构建期。
 */
@Singleton
class ApiProvider @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    private val session: SessionManager
) {
    @Volatile
    private var cached: Pair<String, ApiService>? = null

    fun api(): ApiService {
        val baseUrl = session.currentBaseUrl
        cached?.let { (url, service) -> if (url == baseUrl) return service }
        return synchronized(this) {
            cached?.let { (url, service) -> if (url == baseUrl) return service }
            val service = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ApiService::class.java)
            cached = baseUrl to service
            service
        }
    }
}
