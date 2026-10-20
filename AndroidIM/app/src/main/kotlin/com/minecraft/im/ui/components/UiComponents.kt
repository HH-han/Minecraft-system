package com.minecraft.im.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.minecraft.im.core.mediaUrl

private val AVATAR_COLORS = listOf(
    Color(0xFF4C9A2A), Color(0xFF2E7D32), Color(0xFF00897B), Color(0xFF0288D1),
    Color(0xFF5E35B1), Color(0xFFC2185B), Color(0xFFEF6C00), Color(0xFF6D4C41)
)

/** 头像：加载失败或为空时退回"首字母 + 稳定色块" */
@Composable
fun ImAvatar(
    name: String,
    avatar: String?,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    isGroup: Boolean = false
) {
    val resolved = mediaUrl(LocalBaseUrl.current, avatar)
    val shape = if (isGroup) RoundedCornerShape(size * 0.28f) else CircleShape
    val color = AVATAR_COLORS[
        (name.hashCode().let { if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it) }) % AVATAR_COLORS.size
    ]
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(color)
            .border(0.5.dp, Color.Black.copy(alpha = 0.05f), shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.trim().take(1).ifBlank { "?" }.uppercase(),
            color = Color.White,
            fontSize = (size.value * 0.42f).sp,
            fontWeight = FontWeight.SemiBold
        )
        if (!resolved.isNullOrBlank()) {
            AsyncImage(
                model = resolved,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(shape)
            )
        }
    }
}

/** 在线状态小圆点 */
@Composable
fun OnlineDot(modifier: Modifier = Modifier, size: Dp = 10.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(1.5.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    )
}

/** 未读角标，超过 99 显示 99+ */
@Composable
fun UnreadBadge(count: Int, modifier: Modifier = Modifier, maxText: String = "99+") {
    if (count <= 0) return
    Box(
        modifier = modifier
            .heightIn(min = 18.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.error)
            .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .padding(horizontal = 5.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 99) maxText else count.toString(),
            color = MaterialTheme.colorScheme.onError,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EmptyState(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(Modifier.height(10.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(30.dp))
    }
}

/** 统一返回式顶栏 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarBack(
    title: String,
    onBack: () -> Unit,
    subtitle: String? = null,
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack)
                    .padding(9.dp)
                    .size(22.dp)
            )
        },
        actions = { actions() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/** 方形彩色图标块：用于功能入口；solid 为实心色块 + 白色图标（微信风格） */
@Composable
fun IconTile(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    solid: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * if (solid) 0.24f else 0.3f))
            .background(if (solid) tint else tint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (solid) Color.White else tint,
            modifier = Modifier.size(size * if (solid) 0.58f else 0.52f)
        )
    }
}

/** 微信风格通栏分组：白底、无圆角、无外边距 */
@Composable
fun GroupCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        content = content
    )
}

/** 微信风格分组标题：灰色小字 */
@Composable
fun GroupHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 10.dp, bottom = 6.dp)
    )
}

/** 列表功能行：左侧实心图标 + 标题 + 可选右侧内容 + 箭头 */
@Composable
fun NavRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconSize: Dp = 30.dp,
    badge: Int = 0,
    subtitle: String? = null,
    showChevron: Boolean = true,
    leftContent: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leftContent != null) {
            leftContent()
            Spacer(Modifier.width(12.dp))
        } else if (icon != null) {
            IconTile(icon = icon, tint = iconTint, size = iconSize, solid = true)
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (badge > 0) {
            Spacer(Modifier.width(6.dp))
            UnreadBadge(badge)
        }
        Spacer(Modifier.weight(1f))
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        if (trailing != null) {
            trailing()
            Spacer(Modifier.width(2.dp))
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** 设置项通用行 */
@Composable
fun SettingRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    titleColor: Color = Color.Unspecified
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (titleColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else titleColor
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            trailing()
            Spacer(Modifier.width(4.dp))
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** 分组卡片容器 */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
        content = content
    )
}

/** 卡片内部分隔线，默认左侧缩进 */
@Composable
fun HorizontalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(0.6.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    )
}

/** 顶部搜索框：QQ 风格的浅灰胶囊 */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (value.isNotEmpty()) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .clickable { onValueChange("") }
            )
        }
    }
}

@Composable
fun RowSpacer(width: Dp = 8.dp) = Spacer(Modifier.width(width))
