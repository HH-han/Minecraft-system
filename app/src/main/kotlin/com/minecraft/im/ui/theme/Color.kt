package com.minecraft.im.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// 品牌色：参考 Web 端与开发文档中的 Minecraft 绿
val GrassGreen = Color(0xFF4C9A2A)
val GrassGreenDark = Color(0xFF2E7D32)
val EmberOrange = Color(0xFFFF9800)
val SoilBrown = Color(0xFF7A5B3A)

/** 与 MaterialTheme 无关的 IM 专用色，便于气泡/聊天背景单独控制 */
@Immutable
data class ImColors(
    val chatBackground: Color,
    val sentBubble: Color,
    val sentText: Color,
    val receivedBubble: Color,
    val receivedText: Color,
    val online: Color,
    val muted: Color,
    val divider: Color
)

val LightImColors = ImColors(
    chatBackground = Color(0xFFF1F4EF),
    sentBubble = Color(0xFF95EC69),
    sentText = Color(0xFF1B1B1B),
    receivedBubble = Color(0xFFFFFFFF),
    receivedText = Color(0xFF1B1B1B),
    online = Color(0xFF4CAF50),
    muted = Color(0xFF8A8F88),
    divider = Color(0xFFE6E9E3)
)

val DarkImColors = ImColors(
    chatBackground = Color(0xFF10140F),
    sentBubble = Color(0xFF2E6B33),
    sentText = Color(0xFFF3F7F0),
    receivedBubble = Color(0xFF232823),
    receivedText = Color(0xFFEDF2EA),
    online = Color(0xFF6FCF6B),
    muted = Color(0xFF9AA396),
    divider = Color(0xFF2A302A)
)

val GameImColors = ImColors(
    chatBackground = Color(0xFF0B120D),
    sentBubble = Color(0xFF3C7A3A),
    sentText = Color(0xFFF2FBEA),
    receivedBubble = Color(0xFF1A241B),
    receivedText = Color(0xFFDCE8D8),
    online = Color(0xFF8BD450),
    muted = Color(0xFF8FA089),
    divider = Color(0xFF243026)
)

val LocalImColors = staticCompositionLocalOf { LightImColors }
