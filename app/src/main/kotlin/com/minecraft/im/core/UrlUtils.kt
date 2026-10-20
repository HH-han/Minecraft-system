package com.minecraft.im.core

import java.net.URLEncoder

/**
 * 后端上传接口返回的是绝对地址（默认 http://localhost:8080/upload/...）。
 * 真机/模拟器无法访问 localhost，这里统一改写成当前配置的服务器域名。
 */
fun mediaUrl(baseUrl: String, raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    if (!raw.startsWith("http", ignoreCase = true)) {
        return baseUrl.trimEnd('/') + "/" + raw.trimStart('/')
    }
    val origin = baseUrl.substringBefore("/api").trimEnd('/')
    return raw
        .replace("http://localhost:8080", origin)
        .replace("http://127.0.0.1:8080", origin)
}

/** 由 http(s) 根地址推导 ws(s) 地址，追加 token 查询参数 */
fun webSocketUrl(rootUrl: String, token: String): String {
    val root = rootUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
    val wsRoot = when {
        root.startsWith("https://") -> "wss://" + root.removePrefix("https://")
        root.startsWith("http://") -> "ws://" + root.removePrefix("http://")
        else -> "ws://$root"
    }
    return "$wsRoot/ws?token=${URLEncoder.encode(token, "UTF-8")}"
}

fun normalizeBaseUrl(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return ""
    val withScheme = if (trimmed.startsWith("http")) trimmed else "http://$trimmed"
    return if (withScheme.endsWith("/")) withScheme else "$withScheme/"
}

