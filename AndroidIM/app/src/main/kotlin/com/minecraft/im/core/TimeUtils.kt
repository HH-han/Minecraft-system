package com.minecraft.im.core

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val SERVER_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val MONTH_DAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("M月d日")
private val MONTH_DAY_FORMATTER_EN: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")
private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d")

/**
 * 解析后端时间：兼容 "yyyy-MM-dd HH:mm:ss"、ISO 字符串与毫秒时间戳。
 * 解析失败时返回当前时间，避免消息因时间异常被隐藏。
 */
fun parseServerTime(raw: String?): Long {
    if (raw.isNullOrBlank()) return System.currentTimeMillis()
    raw.toLongOrNull()?.let { return it }
    val text = raw.replace("T", " ").substringBefore(".").trim()
    return try {
        LocalDateTime.parse(text, SERVER_FORMATTER)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (_: Exception) {
        try {
            Instant.parse(raw).toEpochMilli()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}

/** 会话列表时间：今天显示 HH:mm，昨天显示"昨天"，同年显示 M月d日，跨年显示 yyyy/M/d */
fun formatConversationTime(millis: Long, isEnglish: Boolean = false): String {
    if (millis <= 0L) return ""
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
    val today = LocalDate.now(zone)
    return when {
        date == today -> Instant.ofEpochMilli(millis).atZone(zone).format(TIME_FORMATTER)
        date == today.minusDays(1) -> if (isEnglish) "Yesterday" else "昨天"
        date.year == today.year -> Instant.ofEpochMilli(millis).atZone(zone)
            .format(if (isEnglish) MONTH_DAY_FORMATTER_EN else MONTH_DAY_FORMATTER)
        else -> Instant.ofEpochMilli(millis).atZone(zone).format(DATE_FORMATTER)
    }
}

/** 消息列表中的时间分隔文案 */
fun formatMessageDivider(millis: Long, isEnglish: Boolean = false): String {
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
    val today = LocalDate.now(zone)
    val time = Instant.ofEpochMilli(millis).atZone(zone).format(TIME_FORMATTER)
    return when {
        date == today -> time
        date == today.minusDays(1) -> if (isEnglish) "Yesterday $time" else "昨天 $time"
        date.year == today.year -> Instant.ofEpochMilli(millis).atZone(zone)
            .format(if (isEnglish) MONTH_DAY_FORMATTER_EN else MONTH_DAY_FORMATTER) + " " + time
        else -> Instant.ofEpochMilli(millis).atZone(zone).format(DATE_FORMATTER) + " " + time
    }
}

fun nowMillis(): Long = System.currentTimeMillis()

fun localeIsEnglish(locale: Locale = Locale.getDefault()): Boolean =
    locale.language.equals("en", ignoreCase = true)
