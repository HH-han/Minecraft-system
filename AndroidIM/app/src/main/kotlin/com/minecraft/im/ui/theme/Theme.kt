package com.minecraft.im.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.minecraft.im.data.ThemeMode

private val LightColors = lightColorScheme(
    primary = GrassGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDFF3E1),
    onPrimaryContainer = Color(0xFF113216),
    secondary = GrassGreenDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9F4EA),
    onSecondaryContainer = Color(0xFF1B3A21),
    tertiary = EmberOrange,
    background = Color(0xFFF3F4F6),
    onBackground = Color(0xFF17181A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF17181A),
    surfaceVariant = Color(0xFFF2F3F5),
    onSurfaceVariant = Color(0xFF8A9099),
    outline = Color(0xFFD9DCE1),
    outlineVariant = Color(0xFFECEDF0),
    error = Color(0xFFFF3B30),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6FD37B),
    onPrimary = Color(0xFF06280F),
    primaryContainer = Color(0xFF1E4426),
    onPrimaryContainer = Color(0xFFDDF3CF),
    secondary = Color(0xFFA8D9B0),
    onSecondary = Color(0xFF10301A),
    tertiary = Color(0xFFFFB74D),
    background = Color(0xFF0F1113),
    onBackground = Color(0xFFE7E9EC),
    surface = Color(0xFF17191C),
    onSurface = Color(0xFFE7E9EC),
    surfaceVariant = Color(0xFF222529),
    onSurfaceVariant = Color(0xFF9BA1AA),
    outline = Color(0xFF33373C),
    outlineVariant = Color(0xFF2A2D31),
    error = Color(0xFFFF6B63),
    onError = Color(0xFF3A0503)
)

// 游戏主题：更深的底色 + 草方块绿 + 泥土色调
private val GameColors = darkColorScheme(
    primary = Color(0xFF7ED957),
    onPrimary = Color(0xFF0A2003),
    primaryContainer = Color(0xFF27501F),
    onPrimaryContainer = Color(0xFFDDF7CE),
    secondary = Color(0xFFE3B34A),
    onSecondary = Color(0xFF2A1F00),
    tertiary = Color(0xFFB07B4F),
    background = Color(0xFF080D09),
    onBackground = Color(0xFFE4EEDF),
    surface = Color(0xFF121A14),
    onSurface = Color(0xFFE4EEDF),
    surfaceVariant = Color(0xFF1D2A1F),
    onSurfaceVariant = Color(0xFFAFC2AA),
    outline = Color(0xFF3D4C3E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val ImShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

/** 更紧凑、层级更清晰的字号，贴近 QQ 移动端的排版 */
private val ImTypography = Typography(
    headlineSmall = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 15.sp
    )
)

@Composable
fun MinecraftImTheme(mode: ThemeMode, content: @Composable () -> Unit) {
    val dark = when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.GAME -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when (mode) {
        ThemeMode.GAME -> GameColors
        else -> if (dark) DarkColors else LightColors
    }
    val imColors = when (mode) {
        ThemeMode.GAME -> GameImColors
        else -> if (dark) DarkImColors else LightImColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            view.context.findActivity()?.window?.let { window ->
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
        }
    }

    CompositionLocalProvider(LocalImColors provides imColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = ImShapes,
            typography = ImTypography,
            content = content
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
