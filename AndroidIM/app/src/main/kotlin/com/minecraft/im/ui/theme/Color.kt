package com.minecraft.im.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// 品牌色：参考 Web 端与开发文档中的 Minecraft 绿（QQ 风格的高饱和点缀）
val GrassGreen = Color(0xFF3FA34D)
val GrassGreenDark = Color(0xFF2A7D3A)
val EmberOrange = Color(0xFFFF9F0A)
val SoilBrown = Color(0xFF7A5B3A)

// 通用点缀色：用于功能入口图标
val AccentBlue = Color(0xFF3B82F6)
val AccentPurple = Color(0xFF8B5CF6)
val AccentPink = Color(0xFFEC4899)
val AccentTeal = Color(0xFF14B8A6)
val AccentRed = Color(0xFFEF4444)
val AccentAmber = Color(0xFFFFB300)

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
    chatBackground = Color(0xFFEDEEF1),
    sentBubble = Color(0xFF3FA34D),
    sentText = Color(0xFFFFFFFF),
    receivedBubble = Color(0xFFFFFFFF),
    receivedText = Color(0xFF17181A),
    online = Color(0xFF34C759),
    muted = Color(0xFF9AA0A6),
    divider = Color(0xFFECEDEF)
)

val DarkImColors = ImColors(
    chatBackground = Color(0xFF0F1113),
    sentBubble = Color(0xFF2E7D3C),
    sentText = Color(0xFFF3F7F0),
    receivedBubble = Color(0xFF1E2125),
    receivedText = Color(0xFFEDF2EA),
    online = Color(0xFF4CD964),
    muted = Color(0xFF8E949C),
    divider = Color(0xFF2A2D31)
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
