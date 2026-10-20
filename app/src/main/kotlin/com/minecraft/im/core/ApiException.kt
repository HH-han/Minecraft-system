package com.minecraft.im.core

import com.minecraft.im.data.remote.dto.ApiEnvelope

/** 业务异常：承载后端返回的 message，便于 UI 直接展示 */
class ApiException(message: String) : Exception(message)

/** 把任意异常转换成可展示的文案 */
fun Throwable.toMessage(): String = when (this) {
    is ApiException -> message ?: "请求失败"
    is java.net.UnknownHostException -> "网络不可用，请检查服务器地址"
    is java.net.ConnectException -> "无法连接服务器，请检查服务是否启动"
    is java.net.SocketTimeoutException -> "请求超时，请稍后重试"
    else -> message ?: "网络异常"
}

/** 统一判断接口业务状态 */
fun <T> ApiEnvelope<T>.requireData(): T {
    if (code != 200) throw ApiException(message ?: "请求失败")
    @Suppress("UNCHECKED_CAST")
    return (data ?: Unit) as T
}

fun <T> ApiEnvelope<T>.requireOrNull(): T? {
    if (code != 200) throw ApiException(message ?: "请求失败")
    return data
}
