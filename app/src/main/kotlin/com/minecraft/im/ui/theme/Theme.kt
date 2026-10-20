package com.minecraft.im.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.minecraft.im.data.ThemeMode

private val LightColors = lightColorScheme(
    primary = GrassGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF0D0),
    onPrimaryContainer = Color(0xFF1D3413),
    secondary = GrassGreenDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE7F2DF),
    onSecondaryContainer = Color(0xFF243A1A),
    tertiary = EmberOrange,
    background = Color(0xFFF6F8F4),
    onBackground = Color(0xFF182015),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF182015),
    surfaceVariant = Color(0xFFEDF1E9),
    onSurfaceVariant = Color(0xFF5B6457),
    outline = Color(0xFFC6CCC2),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8BD450),
    onPrimary = Color(0xFF13300A),
    primaryContainer = Color(0xFF2C4B22),
    onPrimaryContainer = Color(0xFFDDF3CF),
    secondary = Color(0xFFB7D9A4),
    onSecondary = Color(0xFF1E3314),
    tertiary = Color(0xFFFFB74D),
    background = Color(0xFF0E120D),
    onBackground = Color(0xFFE6ECE2),
    surface = Color(0xFF171C16),
    onSurface = Color(0xFFE6ECE2),
    surfaceVariant = Color(0xFF242A23),
    onSurfaceVariant = Color(0xFFBFC7BA),
    outline = Color(0xFF4A5347),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
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
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
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
            content = content
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
